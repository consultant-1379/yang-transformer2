/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.mediation.modeling.yangtools.transformer2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelDefinition;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.itpf.modeling.schema.util.XmlGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.parser.CustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.ParserExecutionContext;
import com.ericsson.oss.mediation.modeling.yangtools.parser.YangDeviceModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.Finding;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.FindingsManager;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ModifyableFindingSeverityCalculator;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ConformanceType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.StatementClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EriCustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EricssonExtensionsClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.ietf.IetfExtensionsClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.oran.OranExtensionsClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.threegpp.ThreeGppExtensionsClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor.Variant;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.CfmMimInfoGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EModelHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangLibraryHelper;

public class YangTransformer2 {

	public static final String REPORT_FILE_NAME = "enmYangTransformerReport.txt";

	/**
	 * Performs transformation using the supplied context as source of options.
	 */
	public static void transform(final TransformerContext context) {

		try {
			context.logSettingsForTransform();
			final long start = System.currentTimeMillis();

			/*
			 * Process the Yang Library, if any, and check whether we need it.
			 */
			YangLibraryHelper.parseYangLibrary(context);
			if(context.getTopLevelYangLibrary() == null && context.getFeatureHandling() == FeatureHandling.CONFIGURED) {
				throw new YangTransformer2Exception("feature-handling is set to 'configured', but Yang Library has not been supplied.");
			}

			/*
			 * Create the three variants
			 */
			final YangDeviceModel baseVariant = buildSchema(context, false, false);
			final YangDeviceModel augmentedVariant = buildSchema(context, true, false);
			final YangDeviceModel deviatedVariant = buildSchema(context, true, true);

			context.setSchemas(baseVariant.getTopLevelSchema(), augmentedVariant.getTopLevelSchema(), deviatedVariant.getTopLevelSchema());

			/*
			 * Build the Yang Library from the parsed modules, if so required - otherwise cross-reference
			 * with the modules we actually received.
			 */
			if(context.getTopLevelYangLibrary() == null) {
				YangLibraryHelper.buildYangLibraryFromSuppliedYams(context);
			} else {
				YangLibraryHelper.checkReceivedYangLibraryContents(context);
			}

			/*
			 * Before we generate, we clean up the schema trees and generate some meta-information that will make
			 * our life a lot easier later on.
			 */
			PreProcessor.processVariantsBeforeGeneration(context);

			/*
			 * The magic bit, where we generate the models.
			 */
			Generator.transform(context);

			/*
			 * Write out the lot
			 */
			writeOutGeneratedModels(context);
			NetYangHelper.copyYamsToOutput(context, deviatedVariant);

			/*
			 * And a bit of stats handling and log output
			 */
			handleStats(context, start);
			handleReportFile(context);

		} catch (final Exception ex) {
			context.logError("Exception during execution: " + ex.getMessage());
			throw new YangTransformer2Exception("Exception during execution: " + ex.getMessage(), ex);
		}
	}

	/**
	 * For purposes of Software Sync, only the CFM MIMINFO shall be generated. We may or may not have
	 * been given any modules as input - the generation of the MIMINFO differs then.
	 */
	public static void generateCfmMimOnly(final TransformerContext context) {

		context.logSettingsForGenerateCfmMimOnly();

		if(!context.getYangInputForImplementing().isEmpty()) {
			/*
			 * We have been given YANG models as input. At the very least we need to parse these in and
			 * calculate their x.y.z version, as this information will be written out in the CFM MIMINFO.
			 * We don't have to create the ORIGINAL variants; it is good enough just to create DEVIATED.
			 */
			final YangDeviceModel deviatedVariant = buildSchema(context, true, true);
			context.setSchemas(null, null, deviatedVariant.getTopLevelSchema());

			/*
			 * Clean up the app instance names to correctly generate the cfm_miminfo later on in case of
			 * an cIMS node.
			 */
			PreProcessor.applyNodeAppInstanceNameHandling(context);

			/*
			 * If so desired, we will also try to find data nodes representing network functions (for
			 * which we need the DEVIATED variant).
			 */
			discoverNetworkFunctions(context);
		}

		context.getCreatedEmodels().setGeneratedCfmMimInfoModel(CfmMimInfoGenerator.generateCfmMimInfo(context));
	}

	private static YangDeviceModel buildSchema(final TransformerContext context, final boolean resolveAugments, final boolean resolveDeviations) {

		PreProcessor.Variant variant;
		if(resolveDeviations) {
			variant = Variant.DEVIATED;
		} else if(resolveAugments) {
			variant = Variant.AUGMENTED;
		} else {
			variant = Variant.BASE;
		}

		final List<YangModel> yangModelFiles = new ArrayList<>();
		context.getYangInputForImplementing().forEach(yangInput -> yangModelFiles.add(new YangModel(yangInput, ConformanceType.IMPLEMENT)));
		context.getYangInputForImporting().forEach(yangInput -> yangModelFiles.add(new YangModel(yangInput, ConformanceType.IMPORT)));

		final ModifyableFindingSeverityCalculator severityCalculator = new ModifyableFindingSeverityCalculator();
		final FindingsManager findingsManager = new FindingsManager(severityCalculator);
		suppressFindingsToIgnore(severityCalculator);

		final List<StatementClassSupplier> extensionCreators = Arrays.asList(new EricssonExtensionsClassSupplier(), new IetfExtensionsClassSupplier(), new ThreeGppExtensionsClassSupplier(), new OranExtensionsClassSupplier());
		final List<CustomProcessor> customProcessors = Arrays.asList(new EriCustomProcessor());
		final ParserExecutionContext parserContext = new ParserExecutionContext(findingsManager, extensionCreators, customProcessors);

		parserContext.setFailFast(false);
		parserContext.setCheckModulesAgainstYangLibrary(false);
		parserContext.setMergeSubmodulesIntoModules(true);
		parserContext.setResolveDerivedTypesAndGroupings(true);
		parserContext.setStopAfterInitialParse(false);
		parserContext.setIgnoreImportedProtocolAccessibleObjects(true);

		/*
		 * For DEVIATED, we are not using the parser facility to remove data nodes having non-satisfied if-feature condition.
		 * We do this later on during pre-processing, as we must capture the data nodes removed, as their paths must be generated
		 * into meta-data.
		 */
		parserContext.setRemoveSchemaNodesNotSatisfyingIfFeature(false);

		parserContext.setResolveAugments(resolveAugments);
		parserContext.setResolveDeviations(resolveDeviations);

		final YangDeviceModel yangDeviceModel = new YangDeviceModel("Parsed modules - variant: " + variant);
		yangDeviceModel.parseIntoYangModels(parserContext, yangModelFiles);

		/*
		 * We only check for errors in DEVIATING. It is pointless checking for errors in BASE or AUGMENTING, as
		 * deviations may modify/remove schema nodes and the model loses referential integrity. And of course,
		 * the same errors are present in DEVIATING as in BASE/AUGMENTING.
		 */
		if(resolveDeviations) {
			final boolean hasErrors = outputSignificantFindings(context, findingsManager);
			if(hasErrors) {
				throw new YangTransformer2Exception("There have been significant errors during parsing; transformation was not performed. Check log for details.");
			}
		}

		/*
		 * We attach the Variant to all modules, and calculate the X.Y.Z versions straight away.
		 */
		PreProcessor.assignVariantToAllYangModelRootsInSchema(yangDeviceModel.getTopLevelSchema(), variant);
		PreProcessor.attachXyzVersionToAllYangModelRootsInSchema(yangDeviceModel.getTopLevelSchema());

		return yangDeviceModel;
	}

	/**
	 * We don't care about those. They are noise. Suppress.
	 */
	private static final List<String> FINDINGS_TO_IGNORE = Arrays.asList(
			ParserFindingType.P032_MISSING_REVISION.toString(),
			ParserFindingType.P049_DUPLICATE_REVISION.toString(),
			ParserFindingType.P055_SUPERFLUOUS_STATEMENT.toString(),
			ParserFindingType.P056_CONSTRAINT_NARROWED.toString(),
			ParserFindingType.P057_DATA_TYPE_CHANGED.toString(),
			ParserFindingType.P101_EMPTY_DOCUMENTATION_VALUE.toString(),
			ParserFindingType.P104_USAGE_OF_DEPRECATED_ELEMENT.toString(),
			ParserFindingType.P112_EXCESSIVE_TYPEDEF_DEPTH.toString(),
			ParserFindingType.P114_TYPEDEF_NOT_USED.toString(),
			ParserFindingType.P115_TYPEDEF_USED_ONCE_ONLY.toString(),
			ParserFindingType.P122_EXCESSIVE_USES_DEPTH.toString(),
			ParserFindingType.P132_GROUPING_NOT_USED.toString(),
			ParserFindingType.P133_GROUPING_USED_ONCE_ONLY.toString(),
			ParserFindingType.P142_UNUSUAL_CHARACTERS_IN_ENUM.toString(),
			ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString(),
			ParserFindingType.P162_DEVIATION_TARGET_NODE_IN_SAME_MODULE.toString(),
			ParserFindingType.P167_CANNOT_USE_UNDER_DEVIATE_ADD_OR_DELETE.toString()
		);

	private static void suppressFindingsToIgnore(final ModifyableFindingSeverityCalculator severityCalculator) {
		FINDINGS_TO_IGNORE.forEach(severityCalculator::suppressFinding);
	}

	/**
	 * These are all findings that will make the build unstable. It should be very rare that any of these
	 * occur, but if they do it is important that we don't try to generate, as there is an excellent chance
	 * that what we would generate would be wrong or meaningless. Better to fail the transformation than to
	 * generate junk and let it loose in a deployment.
	 */
	private static final List<String> FINDINGS_THAT_ARE_ERRORS = Arrays.asList(
			ParserFindingType.P000_UNSPECIFIED_ERROR.toString(),
			ParserFindingType.P001_BASIC_FILE_READ_ERROR.toString(),
			ParserFindingType.P003_DUPLICATE_INPUT.toString(),
			ParserFindingType.P004_SAME_MODULE_DUPLICATE_IMPLEMENTS.toString(),
			ParserFindingType.P005_NO_IMPLEMENTS.toString(),
			ParserFindingType.P006_IMPLEMENT_IMPORT_MISMATCH.toString(),
			ParserFindingType.P013_INVALID_SYNTAX_AT_DOCUMENT_ROOT.toString(),
			ParserFindingType.P014_INVALID_SYNTAX_AT_DOCUMENT_END.toString(),
			ParserFindingType.P031_PREFIX_NOT_UNIQUE.toString(),
			ParserFindingType.P035_AMBIGUOUS_IMPORT.toString(),
			ParserFindingType.P038_AMBIGUOUS_INCLUDE.toString(),
			ParserFindingType.P040_CIRCULAR_INCLUDE_REFERENCES.toString(),
			ParserFindingType.P043_SAME_MODULE_IMPLEMENTS_MORE_THAN_ONCE.toString(),
			ParserFindingType.P044_SAME_MODULE_IMPLEMENTS_AND_IMPORTS.toString(),
			ParserFindingType.P045_NOT_A_SUBMODULE.toString(),
			ParserFindingType.P046_NOT_A_MODULE.toString(),
			ParserFindingType.P047_SUBMODULE_OWNERSHIP_MISMATCH.toString(),
			ParserFindingType.P048_ORPHAN_SUBMODULE.toString(),
			ParserFindingType.P049_DUPLICATE_REVISION.toString()
		);

	private static boolean outputSignificantFindings(final TransformerContext context, final FindingsManager findingsManager) {

		boolean hasErrors = false;

		for(final Finding finding : findingsManager.getAllFindings()) {
			if(FINDINGS_THAT_ARE_ERRORS.contains(finding.getFindingType())) {
				hasErrors = true;
				context.logError(Constants.TEXT_PARSER + " Parser finding: " + finding.getFindingType() + " - " + finding.toString());
			} else {
				context.logWarn(Constants.TEXT_PARSER + " Parser finding: " + finding.getFindingType() + " - " + finding.toString());
			}
		}

		return hasErrors;
	}

	private static void writeOutGeneratedModels(final TransformerContext context) throws IOException {
		final Map<String, XmlGenerator> generators = new HashMap<>(); 

		generators.put(SchemaConstants.DPS_PRIMARYTYPE, new XmlGenerator(SchemaConstants.DPS_PRIMARYTYPE));
		generators.put(SchemaConstants.DPS_PRIMARYTYPE_EXT, new XmlGenerator(SchemaConstants.DPS_PRIMARYTYPE_EXT));
		generators.put(SchemaConstants.DPS_RELATIONSHIP, new XmlGenerator(SchemaConstants.DPS_RELATIONSHIP));
		generators.put(SchemaConstants.OSS_EDT, new XmlGenerator(SchemaConstants.OSS_EDT));
		generators.put(SchemaConstants.OSS_EDT_EXT, new XmlGenerator(SchemaConstants.OSS_EDT_EXT));
		generators.put(SchemaConstants.OSS_EDT_COMBINED, new XmlGenerator(SchemaConstants.OSS_EDT_COMBINED));
		generators.put(SchemaConstants.CFM_MIMINFO, new XmlGenerator(SchemaConstants.CFM_MIMINFO));
		generators.put(SchemaConstants.OSS_CDT, new XmlGenerator(SchemaConstants.OSS_CDT));

		for(final EModelDefinition emodel : context.getCreatedEmodels().getGeneratedEModels()) {
			final String emodelSchemaName = EModelHelper.getSchemaNameForEModelClass(emodel.getClass());
			final File fileForModel = XmlGenerator.getFileForModel(context.getOutDir(), emodelSchemaName, emodel.getNs(), emodel.getName(), emodel.getVersion());

			fileForModel.getParentFile().mkdirs();
			Files.deleteIfExists(fileForModel.toPath());
			Files.createFile(fileForModel.toPath());

			generators.get(emodelSchemaName).generate(emodel, fileForModel);
		}
	}

	private static void handleStats(final TransformerContext context, final long start) {

		final long duration = System.currentTimeMillis() - start;
		context.logInfo(Constants.TEXT_STATS + " Generated " + context.getCreatedEmodels().getGeneratedEModels().size() + " models in " + duration + " ms.");

		final Map<String, Integer> modelCount = new HashMap<>(); 

		context.getCreatedEmodels().getGeneratedEModels().forEach(emodel -> {
			final String schemaName = EModelHelper.getSchemaNameForEModelClass(emodel.getClass());

			if(modelCount.containsKey(schemaName)) {
				modelCount.put(schemaName, modelCount.get(schemaName) + 1);
			} else {
				modelCount.put(schemaName, 1);
			}
		});

		modelCount.forEach((schemaName, count) -> context.logInfo(Constants.TEXT_STATS + " Generated " + count + " model(s) of schema " + schemaName));
	}

	private static void handleReportFile(final TransformerContext context) {

		final List<String> outputForReportFile = new ArrayList<>();

		outputForReportFile.addAll(orderedUnique(context.getErrors()));
		outputForReportFile.addAll(orderedUnique(context.getWarnings()));
		outputForReportFile.addAll(orderedUnique(context.getInfos()));

		/*
		 * The out dir will be /etc/model. We cannot generate the report file into that directory, as only
		 * models are supposed to go into that (and MDT will enforce that). We will use the parent directory,
		 * which is also what the validator does when writing the marker file and validation report file.
		 */

		final Path reportDir = context.getOutDir().toPath().getParent();
		final Path reportFile = reportDir.resolve(REPORT_FILE_NAME);

		try {
			Files.write(reportFile, outputForReportFile);
		} catch (final Exception ignored) { /* we don't care if this worked or not */ }
	}

	private static List<String> orderedUnique(final List<String> entries) {
		final List<String> uniqueEntries = new ArrayList<>(new HashSet<>(entries));
		Collections.sort(uniqueEntries);
		return uniqueEntries;
	}

	/**
	 * Performs discovery of network functions based on supplied paths.
	 */
	private static void discoverNetworkFunctions(final TransformerContext context) {
		context.getNetworkFunctionsToPath().forEach((nf, path) -> {
			if(pathToNetworkFunctionFound(context.getDeviatedSchema(), path)) {
				context.addDiscoveredNetworkFunction(nf);
				context.logInfo(Constants.TEXT_TRANSFORMER + " discovered network function: " + nf);
			}
		});
	}

	private static boolean pathToNetworkFunctionFound(final Schema deviatedSchema, final String path) {
		/*
		 * Conceivable that a designer forgot to begin the path with a slash in the oss_targettype
		 * model. We are being lenient here and handle that.
		 */
		final String pathWithoutLeadingSlash = path.charAt(0) == '/' ? path.substring(1) : path;
		final List<String> dataPathElements = new ArrayList<>(pathWithoutLeadingSlash.contains("/") ? Arrays.asList(pathWithoutLeadingSlash.split("/")) : Collections.singletonList(pathWithoutLeadingSlash));

		final List<AbstractStatement> candidates = ExtractionHelper.getStatementsAtSchemaRoot(deviatedSchema, ExtractionHelper::isContainerOrListOrLeafOrLeafList);
		return !findTargetDataNode(dataPathElements, candidates).isEmpty();
	}

	/**
	 * Attempts to find data nodes in the schema. May return multiple data nodes, as namespaces
	 * are ignored during navigation. Will handle '*' wildcard, and nested choice/case as well.
	 */
	private static List<AbstractStatement> findTargetDataNode(final List<String> dataPathElements, final List<AbstractStatement> candidateDataNodes) {
		/*
		 * We take (and remove) the first element from the list and try to find it amongst the candidates. There
		 * can be multiple hits (data nodes named the same, in different namespaces) - should be very rare but
		 * better cater for it.
		 *
		 * We will ignore capitalization, because what are the chances that a designer gets the capitalization of
		 * the MO name wrong in the oss_targettype model...
		 *
		 * Also, we need to support wildcards of sorts (basically, just a * at the end to denote 'any').
		 */
		final String soughtDataNodeName = dataPathElements.remove(0).trim().toLowerCase();
		final boolean startsWithMatch = soughtDataNodeName.endsWith("*");
		final String mustStartWith = startsWithMatch ? soughtDataNodeName.substring(0, soughtDataNodeName.length() - 1) : null;

		final List<AbstractStatement> foundDataNodes = candidateDataNodes.stream()
				.filter(stmt -> {
					final String stmtIdentifier = stmt.getStatementIdentifier().toLowerCase();
					return startsWithMatch ? stmtIdentifier.startsWith(mustStartWith) : stmtIdentifier.equals(soughtDataNodeName);
				})
				.collect(Collectors.toList());

		/*
		 * Nothing further found, or we are done navigating.
		 */
		if(foundDataNodes.isEmpty() || dataPathElements.isEmpty()) {
			return foundDataNodes;
		}

		/*
		 * We are getting the data node children of the found data nodes, forming a new set of candidates.
		 */
		final List<AbstractStatement> newCandidateDataNodes = new ArrayList<>();
		foundDataNodes.forEach(foundDataNode -> newCandidateDataNodes.addAll(ExtractionHelper.getChildDataNodes(foundDataNode)));

		/*
		 * Work recursively down the path.
		 */
		return findTargetDataNode(dataPathElements, newCandidateDataNodes);
	}
}
