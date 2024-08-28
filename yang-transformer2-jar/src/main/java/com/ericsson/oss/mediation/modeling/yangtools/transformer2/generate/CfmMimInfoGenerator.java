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

package com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.info.XyzModelVersionInfo;
import com.ericsson.oss.itpf.modeling.schema.util.ModelHandlingUtil;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.NetworkModelTransformers;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ConformanceType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.ModuleRegistry;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.CERI;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriCorrection;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriRelease;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriVersion;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.oran.CORAN;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.oran.YOranSmoTeivLabel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YRevision;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.YangFeature;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.eri.VersionReleaseCorrection;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.NetYangHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2Exception;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2Version;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.CfmMimInfoSupport;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExcludeHandler;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.IfFeatureHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation.SupportedMims;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.SupportedMimType;

public class CfmMimInfoGenerator {

	/**
	 * Generates the cfm_miminfo model based on the valid input of YANG models and/or a
	 * supplied "supported MIMs" file.
	 */
	public static ManagementInformationModelInformation generateCfmMimInfo(final TransformerContext context) {

		final List<SupportedMimType> supportedMims = new ArrayList<>();

		/*
		 * If we had YANG input then we have a deviated schema, and then we can add the
		 * entries for the YANG models.
		 */
		if(context.getDeviatedSchema() != null) {
			addEntriesForYangInputs(context, supportedMims);
		}

		/*
		 * If we have a supported MIMs file add the contents of that.
		 */
		if(context.getSupportedMimsFile() != null) {
			addEntriesFromSuppliedSupportedMimsFile(context, supportedMims);
		}

		/*
		 * Also generate an entry for the mock ManagedElement, if needs be.
		 */
		if(context.generateMockManagedElement()) {
			addEntryForMockManagedElement(context, supportedMims);
		}

		/*
		 * Time to create the actual cfm_miminfo model. 
		 */
		final ManagementInformationModelInformation mimInfo = createManagementInformationModelInformation(context, supportedMims);

		/*
		 * Attach the version of the transformer as meta-data to the model.
		 */
		EModelHelper.attachMeta(mimInfo.getMeta(), NetworkModelTransformers.YangTransformer2.toString(), YangTransformer2Version.YANG_TRANSFORMER2_VERSION.toString());

		/*
		 * We also attach some meta-information that might help mediation to figure out what the effective
		 * containment parent of all top-level data nodes is - which could be ComTop::ManagedElement (e.g.
		 * the routers), a mock ManagedElement (e.g. EPG), OSS_TOP::MeContext (Shared-CNF).
		 */
		EModelHelper.attachMeta(mimInfo.getMeta(), Constants.META_CONTAINMENT_PARENT_URN, context.getEffectiveContainmentParent().toImpliedUrn());

		ensureStableCfmMimInfoGeneration(mimInfo);

		return mimInfo;
	}

	private static void addEntriesForYangInputs(final TransformerContext context, final List<SupportedMimType> supportedMims) {

		final ModuleRegistry moduleRegistry = context.getDeviatedSchema().getModuleRegistry();
		final List<YangModel> allModules = moduleRegistry.getAllYangModels().stream()
				.filter(yangModel -> yangModel.getYangModelRoot().isModule())
				.collect(Collectors.toList());

		final Set<YangFeature> enabledYangFeatures = IfFeatureHelper.getEnabledFeaturesFromYangLibrary(context);

		/*
		 * Any module that is exclude-listed based on namespace we remove straight away. Note that we don't have to
		 * worry about the submodules - a submodule does not have a namespace by itself, but "inherits" the namespace
		 * of it's owning module. Therefore, filtering out modules is good enough.
		 */
		final List<YangModel> nonExcludedModules = ExcludeHandler.getNonExcludedModules(context.getExcludedNamespaces(), allModules);
		nonExcludedModules.forEach(moduleModelInput -> {
			/*
			 * Submodules are handled slightly different when it comes to namespace, so submodules are
			 * handled further down in the loop as part of the module that owns it.
			 *
			 * The namespace and name of the modules and submodules will be cleaned of the app
			 * instance name (if so stipulated).
			 */
			final String cleanedModuleNamespace = PreProcessor.getNamespaceCleanedOfNodeAppInstanceName(moduleModelInput.getYangModelRoot());
			final String xyzVersionForModule = PreProcessor.getXyzVersionForYangModelRoot(moduleModelInput.getYangModelRoot());
			final ModelInfo moduleYamModelInfo = NetYangHelper.getNetYangModelInfoForYangModel(moduleModelInput);

			final String conformance = moduleModelInput.getConformanceType() == ConformanceType.IMPLEMENT ? CfmMimInfoSupport.CONFORMANCE_IMPLEMENT : CfmMimInfoSupport.CONFORMANCE_IMPORT;
			final String moduleYamName = moduleModelInput.getModuleIdentity().getModuleName();

			String featureHandling = null;
			switch(context.getFeatureHandling()) {
			case RETAIN_ALL:
				featureHandling = CfmMimInfoSupport.FEATURE_HANDLING_ALL_RETAINED;
				break;
			case REMOVE_ALL:
				featureHandling = CfmMimInfoSupport.FEATURE_HANDLING_ALL_REMOVED;
				break;
			default:
				featureHandling = CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED;
			}

			final List<String> enabledFeatures = new ArrayList<>();
			if(context.getFeatureHandling() == FeatureHandling.CONFIGURED) {
				enabledYangFeatures.stream()
					.filter(yangFeature -> yangFeature.getModuleName().equals(moduleYamName))
					.forEach(yangFeature -> enabledFeatures.add(yangFeature.getFeatureName()));
			}
			Collections.sort(enabledFeatures);

			supportedMims.add(CfmMimInfoSupport.createSupportedMimTypeForModule(cleanedModuleNamespace, xyzVersionForModule, moduleYamModelInfo, featureHandling, enabledFeatures, conformance, moduleYamName));

			/*
			 * Now do the submodules for the module.
			 */
			moduleModelInput.getYangModelRoot().getOwnedSubmodules().forEach(submoduleYangModelRoot -> {

				final String submoduleName = PreProcessor.getModuleNameCleanedOfNodeAppInstanceName(submoduleYangModelRoot);
				final String xyzVersionForSubmodule = PreProcessor.getXyzVersionForYangModelRoot(submoduleYangModelRoot.getYangModelRoot());
				final ModelInfo submoduleYamModelInfo = NetYangHelper.getNetYangModelInfoForYangModel(submoduleYangModelRoot.getYangModel());
				final String submoduleYamName = submoduleYangModelRoot.getYangModel().getModuleIdentity().getModuleName();

				supportedMims.add(CfmMimInfoSupport.createSupportedMimTypeForSubmodule(cleanedModuleNamespace, submoduleName, xyzVersionForSubmodule, submoduleYamModelInfo, submoduleYamName));
			});
		});
	}

	private static void addEntriesFromSuppliedSupportedMimsFile (final TransformerContext context, final List<SupportedMimType> supportedMims) {

		/*
		 * Load the supported MIMs that Software Sync has already calculated for us.
		 */
		final List<SupportedMimType> supportedMimsReceived = loadAdditionalSupportedMims(context);

		/*
		 * Any MIM of a excluded namespace we remove.
		 */
		final List<SupportedMimType> nonExcludedSupportedMims = ExcludeHandler.getNonExcludedSupportedMimTypes(context.getExcludedNamespaces(), supportedMimsReceived);

		supportedMims.addAll(nonExcludedSupportedMims);
	}

	private static List<SupportedMimType> loadAdditionalSupportedMims(final TransformerContext context) {

		final File supportedMimsFile = context.getSupportedMimsFile();
		final Unmarshaller unmarshaller = ModelHandlingUtil.getUnmarshaller(SchemaConstants.CFM_MIMINFO, false);

		try {
			final ManagementInformationModelInformation cfmMimInfo = (ManagementInformationModelInformation) unmarshaller.unmarshal(supportedMimsFile);
			return cfmMimInfo.getSupportedMims().getMimMappedTo();

		} catch (final JAXBException unmarshallingException) {
			context.logError("Unmarshalling file '" + supportedMimsFile.getAbsolutePath() + "' failed.");
			throw new YangTransformer2Exception("Could not unmarshall cfm_miminfo file containing additional supported MIMs: " + supportedMimsFile.getAbsolutePath(), unmarshallingException);
		}
	}

	/**
	 * Adds an entry for the mock ManagedElement (if any) to the list of supported MIMs.
	 */
	private static void addEntryForMockManagedElement(final TransformerContext context, final List<SupportedMimType> supportedMims) {
		final SupportedMimType mocManagedElementMimType = CfmMimInfoSupport.createSupportedMimTypeForMockManagedElement(context.getMockManagedElement().getNamespace());
		supportedMims.add(mocManagedElementMimType);
	}

	private static ManagementInformationModelInformation createManagementInformationModelInformation(final TransformerContext context, final List<SupportedMimType> supportedMims) {

		final ModelInfo cfmMimInfoModelInfo = getModelInfoForCfmMimInfo(context.getTarget().getType(), calculateCfmMimInfoXyzVersion(supportedMims));
		final ManagementInformationModelInformation mimInfo = new ManagementInformationModelInformation();

		EModelHelper.populateEModelHeaderForDesigned(mimInfo, cfmMimInfoModelInfo, "MIM Information for Software Sync");

		mimInfo.setSupportedMims(new SupportedMims());
		mimInfo.getSupportedMims().getMimMappedTo().addAll(supportedMims);

		context.logInfo(Constants.TEXT_TRANSFORMER + " generated cfm_miminfo model URN: " + cfmMimInfoModelInfo.toUrn());

		return mimInfo;
	}

	/**
	 * Creates the ModelInfo for the CFM MIMINFO model. Example for generated URN:
	 * <p>
	 * /cfm_miminfo/EPG-OI/NE-defined/846584034.3977847237.3056856383
	 */
	public static ModelInfo getModelInfoForCfmMimInfo(final String targetType, final String calculatedVersion) {
		return new ModelInfo(SchemaConstants.CFM_MIMINFO, targetType, SchemaConstants.NE_DEFINED, calculatedVersion);
	}

	/**
	 * Generates the version of the cfm_miminfo. The version will be a hash-code in x.y.z format, build
	 * from the contents of the supported MIMs.
	 */
	private static String calculateCfmMimInfoXyzVersion(final List<SupportedMimType> supportedMims) {
		return CfmMimInfoSupport.calculateCfmMimInfoEModelVersion(supportedMims, YangTransformer2Version.YANG_TRANSFORMER2_VERSION.toString());
	}

	/**
	 * Given a statement that sits in a namespace, returns the ModelInfo object for the NET_YANG model
	 * from which the statement was derived from.
	 */
	public static ModelInfo getDerivedFrom(final TransformerContext context, final AbstractStatement statement) {

		/*
		 * This works based on the namespace of the statement. We find the module that declares the namespace
		 * (and it will be a module, as submodules don't declare a namespace), and we simply get the NET_YANG
		 * for that module.
		 */
		final String statementNamespace = PreProcessor.getOriginalNamespace(statement);
		final Schema owningSchema = statement.getYangModelRoot().getOwningSchema();
		final YangModel yangModel = ExtractionHelper.findModuleDeclaringOriginalNamespace(owningSchema, statementNamespace);

		return NetYangHelper.getNetYangModelInfoForYangModel(yangModel);
	}

	/**
	 * Returns the EModel x.y.z version for a given module. Will make use of the Ericsson
	 * version/release/correction extensions, if they exist; otherwise will translate the
	 * revision date.
	 */
	public static String getEricssonXyzVersionForYam(final YangModel yangModel) {

		final VersionReleaseCorrection vrc = getVersionReleaseCorrectionFromEricsonExtensions(yangModel);

		final String yamName = yangModel.getModuleIdentity().getModuleName();
		final String yamRevision = yangModel.getModuleIdentity().getRevision();
		final XyzModelVersionInfo asXyz = vrc == null ? null : new XyzModelVersionInfo(vrc.getVersion(), vrc.getRelease(), vrc.getCorrection());

		return YangNameVersionUtil.getXyzVersionForYam(yamName, asXyz, yamRevision);
	}

	/**
	 * Returns the Ericsson version / release / correction information as encoded in Ericsson YANG extensions
	 * sitting underneath the newest YANG 'revision' statement. Returns null if the extensions were not found
	 * on the revision, or the module does not have a revision.
	 */
	public static VersionReleaseCorrection getVersionReleaseCorrectionFromEricsonExtensions(final YangModel yangModel) {

		final String moduleRevision = yangModel.getModuleIdentity().getRevision();
		if(moduleRevision == null) {
			return null;
		}

		/*
		 * Need to find that exact revision amongst the revision statements. Note - this will always find at least (usually exactly) one.
		 */
		final YRevision foundRevision = yangModel.getYangModelRoot().getModuleOrSubmodule().getChildren(CY.STMT_REVISION).stream()
				.map(stmt -> (YRevision) stmt)
				.filter(yRevision -> yRevision.getRevision().equals(moduleRevision))
				.findAny().get();

		/*
		 * We look for the TEIV extension first. Very few models will support that.
		 */
		final YOranSmoTeivLabel teivLabel = (YOranSmoTeivLabel) foundRevision.getChild(CORAN.ORAN_SMO_TEIV_COMMON_YANG_EXTENSIONS__LABEL);
		if(teivLabel != null) {
			return new VersionReleaseCorrection(teivLabel.getVersion(), teivLabel.getRelease(), teivLabel.getCorrection());
		}

		/*
		 * The usual v/r/c extensions.
		 */
		final YEriVersion version = (YEriVersion) foundRevision.getChild(CERI.ERICSSON_YANG_EXTENSIONS__VERSION);
		final YEriRelease release = (YEriRelease) foundRevision.getChild(CERI.ERICSSON_YANG_EXTENSIONS__RELEASE);
		final YEriCorrection correction = (YEriCorrection) foundRevision.getChild(CERI.ERICSSON_YANG_EXTENSIONS__CORRECTION);

		if(version == null || release == null || correction == null) {
			return null;
		}

		return new VersionReleaseCorrection(version.getVersion(), release.getRelease(), correction.getCorrection());
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 */
	private static void ensureStableCfmMimInfoGeneration(final ManagementInformationModelInformation cfmMimInfo) {

		Collections.sort(cfmMimInfo.getSupportedMims().getMimMappedTo(), (o1, o2) -> o1.getNamespace().toLowerCase().compareTo(o2.getNamespace().toLowerCase()));
		EModelHelper.ensureStableMetaGeneration(cfmMimInfo.getMeta());
	}
}
