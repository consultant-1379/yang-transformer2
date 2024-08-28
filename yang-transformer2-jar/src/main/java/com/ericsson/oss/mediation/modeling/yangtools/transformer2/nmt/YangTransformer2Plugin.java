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

package com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt;

import static com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2Version.YANG_TRANSFORMER2_VERSION;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.info.XyzModelVersionInfo;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.NetworkFunctionInfo;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.NetworkModelTransformerPlugin;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.NetworkModelTransformerPluginException;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.NetworkModelTransformers;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.SupportedModelInformation;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EModelHelper;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class YangTransformer2Plugin implements NetworkModelTransformerPlugin {

	private static final String COMMA = ",";
	private static final String SPACE = " ";
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String DEFAULT_EXCLUDED_NAMESPACES = "http://tail-f.*";
	private static final String DEFAULT_EXCLUDED_NAMESPACES_FOR_RPC = "urn:ietf:params:xml:ns:netconf:base:1\\.0 urn:ietf:params:xml:ns:yang:ietf-netconf-monitoring urn:ietf:params:xml:ns:yang:ietf-system";
	private static final Logger LOGGER = LoggerFactory.getLogger(YangTransformer2Plugin.class);

	@Override
	public List<ModelInfo> transform(final Properties properties) throws NetworkModelTransformerPluginException {
		if(skipTransformation(properties)) {
			return Collections.emptyList();
		}

		if(properties.getProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE) != null) {
			throw new NetworkModelTransformerPluginException("You cannot supply property '" + YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE + "' as part of transformation.");
		}

		final TransformerContext context = createAndSetupContextForTransformation(properties);

		try {
			LOGGER.info("Starting transformation for " + context.getTarget().getType() + "/" + context.getTarget().getVersion() + "...");

			YangTransformer2.transform(context);

			final ManagementInformationModelInformation generatedCfmMimInfoModel = context.getCreatedEmodels().getGeneratedCfmMimInfoModel();
			final ModelInfo cfmMimInfoModelInfo = EModelHelper.getModelInfoForEModel(generatedCfmMimInfoModel);

			final List<ModelInfo> supportedModels = new ArrayList<>();
			supportedModels.add(cfmMimInfoModelInfo);

			return supportedModels;

		} catch (final Exception ex) {

			throw new NetworkModelTransformerPluginException("Exception encountered in YANG Transformer: " + ex.getMessage(), ex);

		} finally {

			printLogs(context);
		}
	}

	public static TransformerContext createAndSetupContextForTransformation(final Properties properties) throws NetworkModelTransformerPluginException {
		final List<File> implementingPaths = getImplementingPaths(properties, true);
		final List<File> importingPaths = getImportingPaths(properties);
		final String outputDir = getOutputDir(properties);
		final String targetType = getTargetType(properties);
		final String targetVersion = getTargetVersion(properties);

		if(implementingPaths.isEmpty()) {
			throw new NetworkModelTransformerPluginException("Must supply a value for '" + YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE + "'.");
		} else if (outputDir.isEmpty()) {
			throw new NetworkModelTransformerPluginException("Must supply a value for '" + YangTransformer2PluginProperties.OUTPUT_DIR + "'.");
		} else if (targetType.isEmpty()) {
			throw new NetworkModelTransformerPluginException("Must supply a value for '" + YangTransformer2PluginProperties.TARGET_TYPE + "'.");
		} else if (targetVersion.isEmpty()) {
			throw new NetworkModelTransformerPluginException("Must supply a value for '" + YangTransformer2PluginProperties.TARGET_VERSION + "'.");
		}

		/*
		 * The output dir that we are getting is usually "model-build". We want to generate
		 * into /etc/model, though, like all other transformers.
		 */
		final File etcModelDir = new File(new File(outputDir), "etc" + File.separator + "model");

		final TransformerContext context = new TransformerContext(implementingPaths, importingPaths, etcModelDir, targetType, targetVersion);

		handleOriginalImplementAndImportPaths(context, properties);
		handleMockManagedElementNamespace(context, properties);
		handleExplicitContainmentParent(context, properties);
		handleExcludedNamespaces(context, properties);

		handleFeatureHandling(context, properties);
		handleYangLibrary(context, properties);

		if(context.getFeatureHandling() == FeatureHandling.CONFIGURED && context.getYangLibraryInstanceDataFile() == null) {
			throw new NetworkModelTransformerPluginException("Must supply a Yang Library when feature handling is set to 'configured'.");
		}

		handleGenerateNPcontainersAsSystemCreated(context, properties);
		handleGenerateRpcs(context, properties);
		handleExcludeListedNamespacesForRpc(context, properties);
		handleApply3gppHandling(context, properties);
		handleSchemaNodesToRename(context, properties);
		handleSchemaNodesToRemove(context, properties);
		handleDontUse$$syntaxForUniqueMocs(context, properties);
		handleGenerateCombinedEdt(context, properties);
		handleApplyNodeAppInstanceNameHandling(context, properties);

		checkForLegacyNsPrefix(properties);
		checkForLegacySkipMissingImports(properties);
		checkForLegacySkipModulesWithTooLongNames(properties);
		checkForLegacyInputDir(properties);
		checkForLegacyInputCommonModelsDir(properties);

		return context;
	}

	@Override
	public SupportedModelInformation generateSupportedModelInformation(final Properties properties) throws NetworkModelTransformerPluginException {
		return generateSupportedModelInformation(properties, Collections.emptySet());
	}

	@Override
	public SupportedModelInformation generateSupportedModelInformation(final Properties properties, final Set<NetworkFunctionInfo> networkFunctionInfos) throws NetworkModelTransformerPluginException {
		if(skipTransformation(properties)) {
			return new SupportedModelInformation();
		}

		final TransformerContext context = createAndSetupContextForGenerateSupportedModelInformation(properties, networkFunctionInfos);

		try {
			YangTransformer2.generateCfmMimOnly(context);

			final ManagementInformationModelInformation generatedCfmMimInfoModel = context.getCreatedEmodels().getGeneratedCfmMimInfoModel();
			final ModelInfo cfmMimInfoModelInfo = EModelHelper.getModelInfoForEModel(generatedCfmMimInfoModel);

			final List<ModelInfo> supportedModels = new ArrayList<>();
			supportedModels.add(cfmMimInfoModelInfo);

			return new SupportedModelInformation(supportedModels, generatedCfmMimInfoModel.getSupportedMims().getMimMappedTo(), context.getDiscoveredNetworkFunctions());

		} catch (final Exception ex) {

			throw new NetworkModelTransformerPluginException("Exception encountered in YANG Transformer: " + ex.getMessage(), ex);

		} finally {

			printLogs(context);
		}
	}

	@Override
	public String getTransformerName() {
		return NetworkModelTransformers.YangTransformer2.name();
	}

	@Override
	public XyzModelVersionInfo getFunctionalTransformerVersion() {
		return YANG_TRANSFORMER2_VERSION;
	}

	@Override
	public boolean isEnabled(final Properties properties) {
		return !skipTransformation(properties);
	}

	public static TransformerContext createAndSetupContextForGenerateSupportedModelInformation(final Properties properties, final Set<NetworkFunctionInfo> networkFunctionInfos) throws NetworkModelTransformerPluginException {
		final List<File> implementingPaths = getImplementingPaths(properties, false);
		final List<File> importingPaths = getImportingPaths(properties);
		final String targetType = getTargetType(properties);
		final File supportedMimFile = getSupportedMimFile(properties);

		/*
		 * One, or possibly both, of these must be supplied:
		 * - The supported MIM file
		 * - Implementing Paths
		 */
		if (implementingPaths.isEmpty() && supportedMimFile == null) {
			throw new NetworkModelTransformerPluginException("Must supply either or both of '" + YangTransformer2PluginProperties.IMPLEMENTING_MODULES + "' and/or '" + YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE + "'.");
		} else if (targetType.isEmpty()) {
			throw new NetworkModelTransformerPluginException("Must supply a value for '" + YangTransformer2PluginProperties.TARGET_TYPE + "'.");
		}

		/*
		 * If we have been given network function infos then we must have at least a single implementing
		 * path as well, otherwise we could never check the data tree.
		 */
		if(!networkFunctionInfos.isEmpty() && implementingPaths.isEmpty()) {
			throw new NetworkModelTransformerPluginException("Must supply '" + YangTransformer2PluginProperties.IMPLEMENTING_MODULES + "' when also supplying network function infos.");
		}

		final TransformerContext context = new TransformerContext(implementingPaths, importingPaths, targetType, supportedMimFile);

		handleMockManagedElementNamespace(context, properties);
		handleExplicitContainmentParent(context, properties);
		handleExcludedNamespaces(context, properties);
		handleNetworkFunctionInfos(context, networkFunctionInfos);

		handleFeatureHandling(context, properties);
		handleYangLibrary(context, properties);

		if(context.getFeatureHandling() == FeatureHandling.CONFIGURED && context.getYangLibraryInstanceDataFile() == null) {
			throw new NetworkModelTransformerPluginException("Must supply a Yang Library when feature handling is set to 'configured'.");
		}

		handleApplyNodeAppInstanceNameHandling(context, properties);

		return context;
	}

	private static void printLogs(final TransformerContext context) {
		if(LOGGER.isErrorEnabled()) {
			orderedUnique(context.getErrors()).forEach(LOGGER::error);
		}
		if(LOGGER.isWarnEnabled()) {
			orderedUnique(context.getWarnings()).forEach(LOGGER::warn);
		}
		if(LOGGER.isInfoEnabled()) {
			orderedUnique(context.getInfos()).forEach(LOGGER::info);
		}
	}

	private static List<String> orderedUnique(final List<String> entries) {
		final List<String> uniqueEntries = new ArrayList<>(new HashSet<>(entries));
		Collections.sort(uniqueEntries);
		return uniqueEntries;
	}

	private static boolean skipTransformation(final Properties properties) {
		return "true".equals(properties.getProperty(YangTransformer2PluginProperties.SKIP_TRANSFORM, "").trim());
	}

	/**
	 * Returns the target-type, which MUST always be supplied.
	 */
	private static String getTargetType(final Properties properties) throws NetworkModelTransformerPluginException {
		checkPropertySupplied(properties, YangTransformer2PluginProperties.TARGET_TYPE);
		return properties.getProperty(YangTransformer2PluginProperties.TARGET_TYPE).trim();
	}

	/**
	 * Returns the target-version, which MUST exist for transformation.
	 */
	private static String getTargetVersion(final Properties properties) throws NetworkModelTransformerPluginException {
		checkPropertySupplied(properties, YangTransformer2PluginProperties.TARGET_VERSION);
		return properties.getProperty(YangTransformer2PluginProperties.TARGET_VERSION).trim();
	}

	/**
	 * Returns the supported MIMs file, which MUST NOT exist for transformation, and MAY exist
	 * for cfm_miminfo generation.
	 */
	private static File getSupportedMimFile(final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, "").trim();
		return value.isEmpty() ? null : new File(value);
	}

	/**
	 * Returns the paths to the implementing YAMs. For transformation at least a single entry
	 * MUST be supplied; for cfm_miminfo generation, entries MAY be supplied.
	 */
	private static List<File> getImplementingPaths(final Properties properties, final boolean mustExist) throws NetworkModelTransformerPluginException {
		if(mustExist) {
			checkPropertySupplied(properties, YangTransformer2PluginProperties.IMPLEMENTING_MODULES);
		}

		final String value = properties.getProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "").trim();
		if(value.isEmpty()) {
			return Collections.emptyList();
		}

		final List<String> entries = value.contains(COMMA) ? Arrays.asList(value.split(COMMA)) : Collections.singletonList(value);
		return entries.stream().map(File::new).collect(Collectors.toList());
	}

	/**
	 * Returns the paths to the importing YAMs. Optional in both cases.
	 */
	private static List<File> getImportingPaths(final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.IMPORTING_MODULES, "").trim();
		if(value.isEmpty()) {
			return Collections.emptyList();
		}

		final List<String> entries = value.contains(COMMA) ? Arrays.asList(value.split(COMMA)) : Collections.singletonList(value);
		return entries.stream().map(File::new).collect(Collectors.toList());
	}

	/**
	 * Returns the output directory. MUST be supplied for transformation; IGNORED if
	 * supplied for cfm_miminfo generation.
	 */
	private static String getOutputDir(final Properties properties) throws NetworkModelTransformerPluginException {
		checkPropertySupplied(properties, YangTransformer2PluginProperties.OUTPUT_DIR);
		return properties.getProperty(YangTransformer2PluginProperties.OUTPUT_DIR).trim();
	}

	private static void handleOriginalImplementAndImportPaths(final TransformerContext context, final Properties properties) throws NetworkModelTransformerPluginException {
		final String implValue = properties.getProperty(YangTransformer2PluginProperties.ORIGINAL_IMPLEMENTING_MODULES, "").trim();
		if(!implValue.isEmpty()) {
			final List<String> entries = implValue.contains(COMMA) ? Arrays.asList(implValue.split(COMMA)) : Collections.singletonList(implValue);
			final List<File> origImplementingFiles = entries.stream().map(File::new).collect(Collectors.toList());
			context.setOriginalImplementingFiles(origImplementingFiles);
		}

		final String value = properties.getProperty(YangTransformer2PluginProperties.ORIGINAL_IMPORTING_MODULES, "").trim();
		if(!value.isEmpty()) {
			final List<String> entries = value.contains(COMMA) ? Arrays.asList(value.split(COMMA)) : Collections.singletonList(value);
			final List<File> origImportingFiles = entries.stream().map(File::new).collect(Collectors.toList());
			context.setOriginalImportingFiles(origImportingFiles);
		}

		if(!context.getOriginalImportingYangInput().isEmpty() && context.getOriginalImplementingYangInput().isEmpty()) {
			throw new NetworkModelTransformerPluginException("Must also supply path(s) for '" + YangTransformer2PluginProperties.ORIGINAL_IMPLEMENTING_MODULES + "'.");
		}
	}

	private static void handleMockManagedElementNamespace(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.MOCK_MANAGED_ELEMENT_NAMESPACE);
		if(value != null) {
			context.setMockManagedElementNamespace(value.trim());
		}
	}

	private static void handleExplicitContainmentParent(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.EXPLICIT_CONTAINMENT_PARENT_IMPLIED_URN);
		if(value != null) {
			context.setExplicitContainmentParent(ModelInfo.fromImpliedUrn(value.trim(), SchemaConstants.DPS_PRIMARYTYPE));
		}
	}

	private static void handleExcludedNamespaces(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, DEFAULT_EXCLUDED_NAMESPACES).trim();
		if(value.isEmpty()) {
			context.setExcludedNamespaces(Collections.emptyList());
			return;
		}

		final List<String> excludeListedNamespaces = value.contains(SPACE) ? Arrays.asList(value.split(SPACE)) : Collections.singletonList(value);
		final List<String> filteredExcludeListedNamespaces = excludeListedNamespaces.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());
		context.setExcludedNamespaces(filteredExcludeListedNamespaces);
	}

	@SuppressWarnings("deprecation")
	private static void handleFeatureHandling(final TransformerContext context, final Properties properties) throws NetworkModelTransformerPluginException {
		/*
		 * Property 'REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE' is deprecated in favour of 'IF_FEATURE_HANDLING',
		 * but we must still be able to handle the old property as some nodes will never switch over to use
		 * the new property.
		 */
		final String removeSchemaNodesValue = properties.getProperty(YangTransformer2PluginProperties.REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE, "").trim();
		final String ifFeatureHandlingValue = properties.getProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "").trim();

		/*
		 * The default for 'IF_FEATURE_HANDLING' is 'REMOVE_ALL'. This aligns with the default value of TRUE
		 * for the deprecated property 'REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE'.
		 */
		FeatureHandling featureHandling = null;

		if(!ifFeatureHandlingValue.isEmpty()) {
			try {
				featureHandling = FeatureHandling.fromString(ifFeatureHandlingValue);
			} catch (final Exception ex) {
				throw new NetworkModelTransformerPluginException("Illegal value '" + ifFeatureHandlingValue + "' for property '" + YangTransformer2PluginProperties.IF_FEATURE_HANDLING + "'. Use one of: 'REMOVE_ALL', 'RETAIN_ALL', 'CONFIGURED'.");
			}
		} else if(!removeSchemaNodesValue.isEmpty()) {
			featureHandling = Boolean.parseBoolean(removeSchemaNodesValue) ? FeatureHandling.REMOVE_ALL : FeatureHandling.RETAIN_ALL;
		} else {
			featureHandling = FeatureHandling.REMOVE_ALL;
		}

		context.setFeatureHandling(featureHandling);
	}

	private static void handleYangLibrary(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "").trim();						// does not have a default
		if(!value.isEmpty()) {
			context.setYangLibraryInstanceData(new File(value));
		}
	}

	private static void handleGenerateNPcontainersAsSystemCreated(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.GENERATE_NP_CONTAINER_AS_SYSTEM_CREATED, TRUE);	// default for this option is 'true'
		context.setGenerateNPcontainersAsSystemCreated(Boolean.parseBoolean(value.trim()));
	}

	private static void handleGenerateRpcs(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.GENERATE_RPCS, FALSE);							// default for this option is 'false'
		context.setGenerateRpcs(Boolean.parseBoolean(value.trim()));
	}

	private static void handleExcludeListedNamespacesForRpc(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.EXCLUDED_RPC_NAMESPACES, DEFAULT_EXCLUDED_NAMESPACES_FOR_RPC).trim();
		if(value.isEmpty()) {
			context.setExcludedNamespacesForRpcs(Collections.emptyList());
			return;
		}

		final List<String> namespaces = value.contains(SPACE) ? Arrays.asList(value.split(SPACE)) : Collections.singletonList(value);
		final List<String> filteredNamespaces = namespaces.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());
		context.setExcludedNamespacesForRpcs(filteredNamespaces);
	}

	private static void handleApply3gppHandling(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.APPLY_3GPP_HANDLING, FALSE);		// default for this option is 'false'
		context.setApply3gppHandling(Boolean.parseBoolean(value.trim()));
	}

	private static void handleSchemaNodesToRename(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_RENAME, "").trim();
		if(value.isEmpty()) {
			context.setSchemaNodesToRename(Collections.emptyMap());
			return;
		}

		final Map<String, String> toRename = new HashMap<>();

		final List<String> mapEntries = value.contains(COMMA) ? Arrays.asList(value.split(COMMA)) : Collections.singletonList(value);
		mapEntries.forEach(entry -> {
			final String[] split = entry.split(SPACE);
			toRename.put(split[0], split[1]);
		});

		context.setSchemaNodesToRename(toRename);
	}

	private static void handleSchemaNodesToRemove(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_REMOVE, "").trim();
		if(value.isEmpty()) {
			context.setSchemaNodesToRemove(Collections.emptyList());
			return;
		}

		final List<String> schemaNodes = value.contains(COMMA) ? Arrays.asList(value.split(COMMA)) : Collections.singletonList(value);
		context.setSchemaNodesToRemove(schemaNodes);
	}

	private static void handleDontUse$$syntaxForUniqueMocs(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.DONT_USE_DOLLARDOLLAR_FOR_UNIQUE_MOCS, TRUE);		// default for this option is 'true'
		context.setDontUse$$syntaxForUniqueMocs(Boolean.parseBoolean(value.trim()));
	}

	private static void handleGenerateCombinedEdt(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.GENERATE_COMBINED_EDT, FALSE);					// default for this option is 'false'
		context.setGenerateCombinedEdt(Boolean.parseBoolean(value.trim()));
		// TODO: [COMBINED_EDT] Update default in the future to "true" once supported in Model Service
	}

	private static void handleApplyNodeAppInstanceNameHandling(final TransformerContext context, final Properties properties) {
		final String value = properties.getProperty(YangTransformer2PluginProperties.APPLY_NODE_APP_INSTANCE_NAME_HANDLING, FALSE);					// default for this option is 'false'
		context.setApplyNodeAppInstanceNameHandling(Boolean.parseBoolean(value.trim()));
	}

	private static void handleNetworkFunctionInfos(final TransformerContext context, final Set<NetworkFunctionInfo> networkFunctionInfos) {

		final Map<String, String> networkFunctionsToPath = new HashMap<>();

		networkFunctionInfos.forEach(nfi -> {
			final String networkFunction = nfi.getFunctionName().trim();
			/*
			 * Traditionally, each managed function has been sitting under the ManagedElement MOC. If the path
			 * attribute of the info is null we can safely assume that this is the case. It may happen, e.g.,
			 * for the cRAN functions that sit under the 3GPP ManagedElement.
			 * 
			 * For core/IMS especially, some network functions may sit in other places. For those, the path
			 * attribute will be specified.
			 * 
			 * Note: Although the contract is such that the path must be NULL if the function is below
			 * ManageElement, it is conceivable that a designer (wrongly) places the construct 'path=""'
			 * into a oss_targettype model. We guard against this here.
			 */
			if(nfi.getPath() == null || nfi.getPath().trim().isEmpty()) {
				networkFunctionsToPath.put(networkFunction, "/ManagedElement/" + nfi.getMocName().trim());
			} else {
				networkFunctionsToPath.put(networkFunction, nfi.getPath().trim());
			}
		});

		context.setNetworkFunctionsToPaths(networkFunctionsToPath);
	}

	private static void checkForLegacyNsPrefix(final Properties properties) throws NetworkModelTransformerPluginException {
		if(properties.getProperty(YangTransformer2PluginProperties.LEGACY_NS_PREFIX) != null) {
			throw new NetworkModelTransformerPluginException("Property '" + YangTransformer2PluginProperties.LEGACY_NS_PREFIX + "' is legacy and not supported.");
		}
	}

	private static void checkForLegacySkipMissingImports(final Properties properties) throws NetworkModelTransformerPluginException {
		if(properties.getProperty(YangTransformer2PluginProperties.LEGACY_SKIP_MISSING_IMPORTS) != null) {
			throw new NetworkModelTransformerPluginException("Property '" + YangTransformer2PluginProperties.LEGACY_SKIP_MISSING_IMPORTS + "' is legacy and not supported.");
		}
	}

	private static void checkForLegacySkipModulesWithTooLongNames(final Properties properties) throws NetworkModelTransformerPluginException {
		if(properties.getProperty(YangTransformer2PluginProperties.LEGACY_SKIP_MODULES_WITH_TOO_LONG_NAMES) != null) {
			throw new NetworkModelTransformerPluginException("Property '" + YangTransformer2PluginProperties.LEGACY_SKIP_MODULES_WITH_TOO_LONG_NAMES + "' is legacy and not supported.");
		}
	}

	private static void checkForLegacyInputDir(final Properties properties) throws NetworkModelTransformerPluginException {
		if(properties.getProperty(YangTransformer2PluginProperties.LEGACY_INPUT_DIR) != null) {
			throw new NetworkModelTransformerPluginException("Property '" + YangTransformer2PluginProperties.LEGACY_INPUT_DIR + "' is legacy and not supported.");
		}
	}

	private static void checkForLegacyInputCommonModelsDir(final Properties properties) throws NetworkModelTransformerPluginException {
		if(properties.getProperty(YangTransformer2PluginProperties.LEGACY_INPUT_COMMON_MODELS_DIR) != null) {
			throw new NetworkModelTransformerPluginException("Property '" + YangTransformer2PluginProperties.LEGACY_INPUT_COMMON_MODELS_DIR + "' is legacy and not supported.");
		}
	}

	private static void checkPropertySupplied(final Properties properties, final String propName) throws NetworkModelTransformerPluginException {
		if(properties.getProperty(propName) == null) {
			throw new NetworkModelTransformerPluginException("Mandatory property '" + propName + "' has not been supplied.");
		}
	}
}
