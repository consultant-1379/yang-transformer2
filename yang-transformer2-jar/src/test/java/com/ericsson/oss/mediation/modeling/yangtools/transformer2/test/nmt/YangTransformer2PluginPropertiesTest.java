package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.nmt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.NetworkModelTransformerProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

@SuppressWarnings("squid:S3415")		// SQ doesn't like the constants below to the on the RHS of the assertEquals, although that is correct!
public class YangTransformer2PluginPropertiesTest extends TransformerTestUtil {

	@SuppressWarnings("deprecation")
	@Test
	public void test___properties_names_general() {

		/*
		 * ==============================================================================================
		 *
		 *                                        WARNING
		 *
		 * If this test fails then some constants have been modified. This is very likely to break 
		 * existing builds and Release Independence. Any test failure must be very carefully evaluated.
		 *
		 * ==============================================================================================
		 */

		assertEquals(NetworkModelTransformerProperties.NE_TYPE, YangTransformer2PluginProperties.TARGET_TYPE);

		assertEquals(NetworkModelTransformerProperties.TARGET_MODEL_IDENTITY, YangTransformer2PluginProperties.TARGET_VERSION);

		assertEquals("yangTransformer.implementingModuleDir", YangTransformer2PluginProperties.IMPLEMENTING_MODULES);

		assertEquals(NetworkModelTransformerProperties.OUTPUT_PATH, YangTransformer2PluginProperties.OUTPUT_DIR);

		// ----------------------------- Optional ------------------------------

		assertEquals("yangTransformer.importModuleDir", YangTransformer2PluginProperties.IMPORTING_MODULES);

		assertEquals("yangTransformer.originalImplementingModuleDir", YangTransformer2PluginProperties.ORIGINAL_IMPLEMENTING_MODULES);

		assertEquals("yangTransformer.originalImportModuleDir", YangTransformer2PluginProperties.ORIGINAL_IMPORTING_MODULES);

		assertEquals("yangTransformer.yangLibrary", YangTransformer2PluginProperties.YANG_LIBRARY);

		assertEquals("yangTransformer.rootMoNameSpace", YangTransformer2PluginProperties.MOCK_MANAGED_ELEMENT_NAMESPACE);

		assertEquals("yangTransformer.containmentParentImpliedUrn", YangTransformer2PluginProperties.EXPLICIT_CONTAINMENT_PARENT_IMPLIED_URN);

		assertEquals("yangTransformer.blacklist", YangTransformer2PluginProperties.EXCLUDED_NAMESPACES);

		assertEquals("yangTransformer.skipIfFeature", YangTransformer2PluginProperties.REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE);

		assertEquals("yangTransformer.ifFeatureHandling", YangTransformer2PluginProperties.IF_FEATURE_HANDLING);

		assertEquals("yangTransformer.markNPContainerAsSystemCreated", YangTransformer2PluginProperties.GENERATE_NP_CONTAINER_AS_SYSTEM_CREATED);

		assertEquals("yangTransformer.generateRpcs", YangTransformer2PluginProperties.GENERATE_RPCS);

		assertEquals("yangTransformer.blacklistedRpcNamespaces", YangTransformer2PluginProperties.EXCLUDED_RPC_NAMESPACES);

		assertEquals("yangTransformer.apply3gppHandling", YangTransformer2PluginProperties.APPLY_3GPP_HANDLING);

		assertEquals("yangTransformer.schemaNodesToRename", YangTransformer2PluginProperties.SCHEMA_NODES_TO_RENAME);

		assertEquals("yangTransformer.schemaNodesToRemove", YangTransformer2PluginProperties.SCHEMA_NODES_TO_REMOVE);

		assertEquals("yangTransformer.dontUseDollarDollarForUniqueMocs", YangTransformer2PluginProperties.DONT_USE_DOLLARDOLLAR_FOR_UNIQUE_MOCS);

		assertEquals("yangTransformer.generateCombinedEdt", YangTransformer2PluginProperties.GENERATE_COMBINED_EDT);

		assertEquals("yangTransformer.applyNodeAppInstanceNameHandling", YangTransformer2PluginProperties.APPLY_NODE_APP_INSTANCE_NAME_HANDLING);

		// ----------------------------- Unsupported (anymore) ------------------------------

		assertEquals("yangTransformer.namespacePrefix", YangTransformer2PluginProperties.LEGACY_NS_PREFIX);

		assertEquals("yangTransformer.skipMissingImports", YangTransformer2PluginProperties.LEGACY_SKIP_MISSING_IMPORTS);

		assertEquals("yangTransformer.skipModulesWithTooLongNames", YangTransformer2PluginProperties.LEGACY_SKIP_MODULES_WITH_TOO_LONG_NAMES);

		assertEquals("yangTransformer.inputDir", YangTransformer2PluginProperties.LEGACY_INPUT_DIR);

		assertEquals("yangTransformer.inputCommonModelsDir", YangTransformer2PluginProperties.LEGACY_INPUT_COMMON_MODELS_DIR);

		// ----------------------------- Other ------------------------------

		assertEquals("yangTransformer.skip", YangTransformer2PluginProperties.SKIP_TRANSFORM);

		assertEquals("yangTransformer.supportedMimsFile", YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE);
	}
}
