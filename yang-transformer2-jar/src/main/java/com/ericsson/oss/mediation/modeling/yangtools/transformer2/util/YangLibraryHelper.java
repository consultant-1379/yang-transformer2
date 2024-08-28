package com.ericsson.oss.mediation.modeling.yangtools.transformer2.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.Finding;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.FindingsManager;
import com.ericsson.oss.mediation.modeling.yangtools.parser.input.FileBasedYangInputResolver;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ConformanceType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ModuleIdentity;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.YangModelRoot;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YFeature;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.Datastore;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.IetfYangLibraryParser;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.Module;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.Module.IetfYangLibraryConformanceType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.ModuleSet;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.RFC8525Populator;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.Submodule;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.YangLibrary;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.eri.VersionReleaseCorrection;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.eri.YangLibraryEricssonExtensionsPopulator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2Exception;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.CfmMimInfoGenerator;

public class YangLibraryHelper {

	/**
	 * Reads the Yang Library, if supplied, and extracts the the top-level YL instance.
	 */
	public static void parseYangLibrary(final TransformerContext context) {

		final File yangLibraryInstanceDataFile = context.getYangLibraryInstanceDataFile();
		if(yangLibraryInstanceDataFile == null) {
			return;
		}

		if(!yangLibraryInstanceDataFile.exists()) {
			throw new YangTransformer2Exception("Yang Library instance data file '" + yangLibraryInstanceDataFile.getAbsolutePath() + "' does not exist.");
		}

		final FileBasedYangInputResolver resolver = new FileBasedYangInputResolver(Collections.singletonList(yangLibraryInstanceDataFile));

		/*
		 * We need to parse the Ericsson extensions as well, so make sure we use a special populator.
		 */
		final IetfYangLibraryParser ietfYangLibraryParser = new IetfYangLibraryParser(Arrays.asList(new RFC8525Populator(), new YangLibraryEricssonExtensionsPopulator()));
		final List<YangLibrary> extractedYangLibraries = ietfYangLibraryParser.parseIntoYangLibraries(resolver);

		/*
		 * Assuming all went well with parsing of the library, there should not be any findings.
		 */
		final FindingsManager yangLibParserFindingsManager = ietfYangLibraryParser.getFindingsManager();
		final Set<Finding> yangLibParserFindings = yangLibParserFindingsManager.getAllFindings();
		if(!yangLibParserFindings.isEmpty()) {
			yangLibParserFindings.forEach(finding -> {
				context.logError("Yang Library parser finding: " + finding.toString());
			});
			throw new YangTransformer2Exception("There were problems parsing the YangLibrary. See error log.");
		}

		/*
		 * Rubbish in the file?
		 */
		if(extractedYangLibraries.isEmpty()) {
			throw new YangTransformer2Exception("File '" + yangLibraryInstanceDataFile.getAbsolutePath() + "' would not appear to contain Yang Library instance data.");
		}

		/*
		 * Note: This code is not NMDA safe. If we ever have to support NMDA, then we need to look for
		 * the top-level YL in the running datastore.
		 */
		final YangLibrary topLevelYangLibrary = YangLibrary.getTopLevelSchema(extractedYangLibraries);
		context.setTopLevelYangLibrary(topLevelYangLibrary);
	}

	/**
	 * If no YL was explicitly supplied, we are generating it here on-the-fly. We are doing this so that we always
	 * have YL information available to us, that we can then use to generate the relevant content for the cfm_miminfo.
	 */
	public static void buildYangLibraryFromSuppliedYams(final TransformerContext context) {

		/*
		 * We use the information from the DEVIATED variant, as this is the one that will be created when the
		 * cfm_miminfo is generated. We process the modules, and within each module navigate to any submodules
		 * that may exist.
		 * 
		 * We don't care about certain things in the YL here - the schema location, features, or
		 * deviations - and so we don't populate these.
		 */
		final ModuleSet moduleSet = new ModuleSet();
		moduleSet.setName("auto-generated module-set");

		final Schema deviatedSchema = context.getDeviatedSchema();
		deviatedSchema.getModuleRegistry().getAllYangModels().stream()
			.filter(yangModel -> yangModel.getYangModelRoot().isModule())
			.forEach(yangModel -> {

				final Module module = new Module();
				module.setName(yangModel.getModuleIdentity().getModuleName());
				module.setRevision(yangModel.getModuleIdentity().getRevision());
				module.setNamespace(yangModel.getPrefixResolver().getDefaultNamespaceUri());

				if(yangModel.getConformanceType() == ConformanceType.IMPLEMENT) {
					module.setConformanceType(Module.MODULE_IMPLEMENT);
					moduleSet.addImplementingModule(module);
				} else {
					module.setConformanceType(Module.MODULE_IMPORT);
					moduleSet.addImportOnlyModule(module);
				}

				/*
				 * Don't forget the Ericsson x.y.z version!
				 */
				final VersionReleaseCorrection moduleVrc = CfmMimInfoGenerator.getVersionReleaseCorrectionFromEricsonExtensions(yangModel);
				if(moduleVrc != null) {
					YangLibraryEricssonExtensionsPopulator.setVRC(module, moduleVrc);
				}

				/*
				 * Now we do the sub-modules.
				 */
				yangModel.getYangModelRoot().getOwnedSubmodules().forEach(submoduleRoot -> {
					final Submodule submodule = new Submodule();
					module.addSubmodule(submodule);

					submodule.setName(submoduleRoot.getYangModel().getModuleIdentity().getModuleName());
					submodule.setRevision(submoduleRoot.getYangModel().getModuleIdentity().getRevision());

					final VersionReleaseCorrection submoduleVrc = CfmMimInfoGenerator.getVersionReleaseCorrectionFromEricsonExtensions(submoduleRoot.getYangModel());
					if(submoduleVrc != null) {
						YangLibraryEricssonExtensionsPopulator.setVRC(submodule, submoduleVrc);
					}
				});
			});

		final Datastore datastore = new Datastore(Datastore.RUNNING_DATASTORE_IDENTITY, "running", Collections.singletonList(moduleSet));

		final YangLibrary yangLibrary = new YangLibrary(null);
		yangLibrary.setContentId("auto-generated content");
		yangLibrary.addDatastore(datastore);

		context.setTopLevelYangLibrary(yangLibrary);
	}

	/**
	 * Checks the contents of the (supplied) YL against the modules supplied. This is more of a sanity check
	 * to make sure that the YAMs we have received actually match up with the YL in terms of their name, release,
	 * namespace and so on. This is important as different parts of the system use the modules, and other parts
	 * use the YL, and we want to make sure they all align properly.
	 */
	public static void checkReceivedYangLibraryContents(final TransformerContext context) {

		/*
		 * First thing is that we exclude from consideration everything that is exclude-listed - typically,
		 * tail-f modules. There is an excellent chance that for these the YL and the supplied YAMs differ,
		 * so they are likely to throw up issues.
		 */
		final List<Pattern> excludePatterns = ExcludeHandler.getExcludePatterns(context.getExcludedNamespaces());

		final List<YangModel> nonExcludedYams = context.getDeviatedSchema().getModuleRegistry().getAllYangModels().stream()
				.filter(yangModel -> yangModel.getYangModelRoot().isModule())
				.filter(yangModel -> !ExcludeHandler.isNamespaceExcluded(excludePatterns, yangModel.getPrefixResolver().getDefaultNamespaceUri()))
				.collect(Collectors.toList());

		final List<Module> nonExcludedModules = context.getTopLevelYangLibrary().getRunningDatastore().getAllModules().stream()
				.filter(module -> !ExcludeHandler.isNamespaceExcluded(excludePatterns, module.getNamespace()))
				.collect(Collectors.toList());

		/*
		 * Pair up the entries, and check these
		 */
		boolean foundError = false;
		for(final Module oneModule : nonExcludedModules) {

			final ModuleIdentity soughtModuleIdentity = new ModuleIdentity(oneModule.getName(), oneModule.getRevision());

			YangModel foundInput = null;
			for(final YangModel oneInput : nonExcludedYams) {
				if(soughtModuleIdentity.equals(oneInput.getModuleIdentity())) {
					foundInput = oneInput;
					break;
				}
			}
			if(foundInput == null) {
				foundError = true;
				context.logError("Yang Library lists '" + soughtModuleIdentity.toString() + "' but this YAM was not found in the input.");
			} else {
				nonExcludedYams.remove(foundInput);
				foundError |= compareModuleAgainstInput(context, oneModule, foundInput);
			}
		}

		/*
		 * If the match up was perfect there should be nothing left inside the non-excluded list of YAMs...
		 */
		for(final YangModel oneInput : nonExcludedYams) {
			foundError = true;
			context.logError("YAM '" + oneInput.getModuleIdentity().toString() + "' is in the input, but it is not listed in the Yang Library.");
		}

		if(foundError) {
			throw new YangTransformer2Exception("There were issues comparing the Yang Library to the input. See log for more details.");
		}
	}

	private static boolean compareModuleAgainstInput(final TransformerContext context, final Module module, final YangModel input) {

		boolean foundErrors = false;
		/*
		 * Check on the namespace
		 */
		if(!Objects.equals(module.getNamespace(), input.getPrefixResolver().getDefaultNamespaceUri())) {
			foundErrors = true;
			context.logError("Namespace mismatch for '" + module.getName() + "' - Yang Library: '" + module.getNamespace() + "' - YAM: '" + input.getPrefixResolver().getDefaultNamespaceUri() + "'.");
		}

		/*
		 * Check on the conformance type
		 */
		final ConformanceType moduleConformanceTypeInYL = module.getConformanceType() == IetfYangLibraryConformanceType.IMPLEMENT ? ConformanceType.IMPLEMENT : ConformanceType.IMPORT;
		if(!input.getConformanceType().equals(moduleConformanceTypeInYL)) {
			foundErrors = true;
			context.logError("Conformance mismatch for '" + module.getName() + "' - Yang Library: '" + module.getConformanceType() + "' - YAM: '" + input.getConformanceType() + "'.");
		}

		/*
		 * Check Ericsson VRC
		 */
		final VersionReleaseCorrection inputVrc = CfmMimInfoGenerator.getVersionReleaseCorrectionFromEricsonExtensions(input);
		final VersionReleaseCorrection moduleVrcInYL = YangLibraryEricssonExtensionsPopulator.getVRC(module);
		if(!Objects.equals(inputVrc, moduleVrcInYL)) {
			foundErrors = true;
			context.logError("Ericsson v/r/c mismatch for '" + module.getName() + "' - Yang Library: '" + Objects.toString(moduleVrcInYL) + "' - YAM: '" + Objects.toString(inputVrc) + "'.");
		}

		/*
		 * Check the enabled features actually exist in the YAM. We need not worry about submodules,
		 * as the parser would have merged feature declarations from the submodule into the owning module.
		 */
		final Set<String> featuresDefinedInYam = input.getYangModelRoot().getModule().getFeatures().stream()
				.map(YFeature::getFeatureName)
				.collect(Collectors.toSet());

		for(final String enabledFeatureInYL : module.getFeatures()) {
			if(!featuresDefinedInYam.contains(enabledFeatureInYL)) {
				foundErrors = true;
				context.logError("Feature '" + enabledFeatureInYL + "' listed in Yang Library for is not defined in YAM '" + input.getModuleIdentity() + "'.");
			}
		}

		return foundErrors | checkSubmodules(context, module, input);
	}

	private static boolean checkSubmodules(final TransformerContext context, final Module module, final YangModel input) {

		boolean foundError = false;

		final List<Submodule> submodules = module.getSubmodules();
		final List<YangModelRoot> inputSubmodules = new ArrayList<>(input.getYangModelRoot().getOwnedSubmodules());		// deep copy required

		for(final Submodule oneSubmodule : submodules) {

			final ModuleIdentity soughtSubmoduleIdentity = new ModuleIdentity(oneSubmodule.getName(), oneSubmodule.getRevision());

			YangModelRoot foundInputSubmodule = null;
			for(final YangModelRoot oneInputSubmodule : inputSubmodules) {
				if(soughtSubmoduleIdentity.equals(oneInputSubmodule.getYangModel().getModuleIdentity())) {
					foundInputSubmodule = oneInputSubmodule;
					break;
				}
			}
			if(foundInputSubmodule == null) {
				foundError = true;
				context.logError("Yang Library lists '" + soughtSubmoduleIdentity.toString() + "' as submodule of '" + module.getName() + "', but this YAM was not found in the input.");
			} else {
				inputSubmodules.remove(foundInputSubmodule);
			}
		}

		/*
		 * If the match up was perfect there should be nothing left...
		 */
		for(final YangModelRoot oneInputSubmodule : inputSubmodules) {
			foundError = true;
			context.logError("YAM '" + oneInputSubmodule.getYangModel().getModuleIdentity().toString() + "' is in the input, but it is not listed in the Yang Library as submodule of '" + module.getName() + "'.");
		}

		return foundError;
	}
}
