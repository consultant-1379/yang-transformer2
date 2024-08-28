package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.nmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.info.XyzModelVersionInfo;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.NetworkFunctionInfo;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.NetworkModelTransformerPlugin;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.NetworkModelTransformerPluginException;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.SupportedModelInformation;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2Plugin;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TestCaptureAppender;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.SupportedMimType;

public class YangTransformer2PluginTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "nmt/";

	@Test
	public void test___nmt_plugin_loads() {
		final ServiceLoader<NetworkModelTransformerPlugin> load = ServiceLoader.load(NetworkModelTransformerPlugin.class);
		final Iterator<NetworkModelTransformerPlugin> iter = load.iterator();
		final List<NetworkModelTransformerPlugin> plugins = new ArrayList<>();

		while(iter.hasNext()) {
			plugins.add(iter.next());
		}

		assertSize(1, plugins);
		assertEquals(YangTransformer2Plugin.class, plugins.get(0).getClass());
	}

	@Test
	public void test___skip_transformation() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.SKIP_TRANSFORM, "  true  ");

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final List<ModelInfo> outputFromTransform = yangTransformerPlugin.transform(properties);
			assertEmpty(outputFromTransform);

			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertEmpty(supportedModelInformation.getSupportedMimTypes());
			assertEmpty(supportedModelInformation.getSupportedModelInfos());
		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___transform_using_legacy_props() {
		final Properties defaults = new Properties();
		defaults.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
		defaults.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
		defaults.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "type");
		defaults.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "version");

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final Properties properties = new Properties(defaults);
			properties.setProperty(YangTransformer2PluginProperties.LEGACY_NS_PREFIX, "NS_PREFIX");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties(defaults);
			properties.setProperty(YangTransformer2PluginProperties.LEGACY_SKIP_MISSING_IMPORTS, "SKIP_MISSING_IMPORTS");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties(defaults);
			properties.setProperty(YangTransformer2PluginProperties.LEGACY_SKIP_MODULES_WITH_TOO_LONG_NAMES, "SKIP_MODULES_WITH_TOO_LONG_NAMES");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties(defaults);
			properties.setProperty(YangTransformer2PluginProperties.LEGACY_INPUT_DIR, "INPUT_DIR");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties(defaults);
			properties.setProperty(YangTransformer2PluginProperties.LEGACY_INPUT_COMMON_MODELS_DIR, "INPUT_COMMON_MODELS_DIR");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}
	}

	@Test
	public void test___transform___bad_properties() {
		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final Properties properties = new Properties();
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, "/some/dir");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
			properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
			properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "type");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "  ");
			properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "type");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "version");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
			properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "  ");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "type");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "version");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
			properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "    ");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "version");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
			properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "type");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "    ");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
			properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "type");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "version");
			properties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPORTING_MODULES, TEST_SUB_DIR + "one-only/nmt-one.yang");
			yangTransformerPlugin.transform(properties);
			fail("Should have thrown.");
		} catch (final NetworkModelTransformerPluginException expected) {
			/* expected */
		} catch (final Exception ex) {
			fail("Wrong exception received.");
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test___plugin_defaults_correctly_applied_to_transform_context() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
		properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "  type  ");
		properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "   version   ");

		try {
			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			/*
			 * The plugins default are different from the defaults of the context. So
			 * make sure they have been correctly applied.
			 *
			 *                          WARNING
			 *
			 * Any test failure here indicates that a plugin property default value has changed. This may require
			 * mandatory updates to those builds that do not explicitly supply a value for the property.
			 */
			assertEquals("NODE", context.getTarget().getCategory());
			assertEquals("type", context.getTarget().getType());
			assertEquals("version", context.getTarget().getVersion());

			assertNull(context.getMockManagedElement());

			assertNull(context.getExplicitContainmentParent());

			assertSize(1, context.getExcludedNamespaces());
			assertTrue(context.getExcludedNamespaces().contains("http://tail-f.*"));

			assertTrue(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());

			assertTrue(context.generateNPcontainersAsSystemCreated());

			assertFalse(context.generateRpcs());

			assertEquals(3, context.getExcludedNamespacesForRpcs().size());
			assertTrue(context.getExcludedNamespacesForRpcs().contains("urn:ietf:params:xml:ns:netconf:base:1\\.0"));
			assertTrue(context.getExcludedNamespacesForRpcs().contains("urn:ietf:params:xml:ns:yang:ietf-netconf-monitoring"));
			assertTrue(context.getExcludedNamespacesForRpcs().contains("urn:ietf:params:xml:ns:yang:ietf-system"));

			assertFalse(context.apply3gppHandling());

			assertTrue(context.getSchemaNodesToRename().isEmpty());

			assertTrue(context.getSchemaNodesToRemove().isEmpty());

			assertTrue(context.dontUse$$syntaxForUniqueMocs());

			assertFalse(context.generateCombinedEdt());		// TODO: [COMBINED_EDT] This will flip to true in the future

			assertFalse(context.applyNodeAppInstanceNameHandling());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___multi_value_properties_transform_context() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
		properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "type");
		properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "version");

		try {
			properties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-only");
			properties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPORTING_MODULES, TEST_SUB_DIR + "");

			properties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, " NS1   NS2   NS3  ");
			properties.setProperty(YangTransformer2PluginProperties.EXCLUDED_RPC_NAMESPACES, "  NS1   NS2   NS3");

			properties.setProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_RENAME, "/node1 one,/node2 two");
			properties.setProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_REMOVE, "/node1,/node2");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertSize(1, context.getOriginalImplementingYangInput());
			assertTrue(context.getOriginalImplementingYangInput().iterator().next().getName().equals("nmt-one.yang"));

			assertSize(3, context.getExcludedNamespaces());
			assertTrue(context.getExcludedNamespaces().contains("NS1"));
			assertTrue(context.getExcludedNamespaces().contains("NS2"));
			assertTrue(context.getExcludedNamespaces().contains("NS3"));

			assertSize(3, context.getExcludedNamespacesForRpcs());
			assertTrue(context.getExcludedNamespacesForRpcs().contains("NS1"));
			assertTrue(context.getExcludedNamespacesForRpcs().contains("NS2"));
			assertTrue(context.getExcludedNamespacesForRpcs().contains("NS3"));

			assertEquals(2, context.getSchemaNodesToRename().size());
			assertEquals("one", context.getSchemaNodesToRename().get("/node1"));
			assertEquals("two", context.getSchemaNodesToRename().get("/node2"));

			assertSize(2, context.getSchemaNodesToRemove());
			assertTrue(context.getSchemaNodesToRemove().contains("/node1"));
			assertTrue(context.getSchemaNodesToRemove().contains("/node2"));

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			properties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-and-two");
			properties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPORTING_MODULES, TEST_SUB_DIR + "one-only");

			properties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, "NS1");
			properties.setProperty(YangTransformer2PluginProperties.EXCLUDED_RPC_NAMESPACES, "NS1");

			properties.setProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_RENAME, "/node1 one");
			properties.setProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_REMOVE, "/node1");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertSize(1, context.getOriginalImportingYangInput());
			assertTrue(context.getOriginalImportingYangInput().iterator().next().getName().equals("nmt-one.yang"));

			assertSize(1, context.getExcludedNamespaces());
			assertTrue(context.getExcludedNamespaces().contains("NS1"));

			assertSize(1, context.getExcludedNamespacesForRpcs());
			assertTrue(context.getExcludedNamespacesForRpcs().contains("NS1"));

			assertEquals(1, context.getSchemaNodesToRename().size());
			assertEquals("one", context.getSchemaNodesToRename().get("/node1"));

			assertSize(1, context.getSchemaNodesToRemove());
			assertTrue(context.getSchemaNodesToRemove().contains("/node1"));

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			properties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-only" + "," + TEST_SUB_DIR + "one-only");
			properties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPORTING_MODULES, "");

			properties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, "");
			properties.setProperty(YangTransformer2PluginProperties.EXCLUDED_RPC_NAMESPACES, "");

			properties.setProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_RENAME, "");
			properties.setProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_REMOVE, "");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertSize(1, context.getOriginalImplementingYangInput());
			assertTrue(context.getOriginalImplementingYangInput().iterator().next().getName().equals("nmt-one.yang"));

			assertEmpty(context.getExcludedNamespaces());
			assertEmpty(context.getExcludedNamespacesForRpcs());
			assertEquals(0, context.getSchemaNodesToRename().size());
			assertEmpty(context.getSchemaNodesToRemove());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	private static final String MODULE_NS_ONE = "urn:test:nmt-one";
	private static final String MODULE_NAME_ONE = "nmt-one";
	private static final String MODULE_REVISION_ONE = "2021-11-10";
	private static final String MODULE_XYZ_VERSION_ONE = "2021.11.10";

	private static final String MODULE_NS_TWO = "urn:test:nmt-two";
	private static final String MODULE_NAME_TWO = "nmt-two";
	private static final String MODULE_REVISION_TWO = "2021-11-15";
	private static final String MODULE_XYZ_VERSION_TWO = "2021.11.15";

	private static final String MODULE_NS_THREE = "urn:test:nmt-three";
	private static final String MODULE_NAME_THREE = "nmt-three";
	private static final String MODULE_REVISION_THREE = "2021-11-21";
	private static final String MODULE_XYZ_VERSION_THREE = "2021.11.21";

	private static final String SUBMODULE_NAME_THREE = "nmt-three-submodule";
	private static final String SUBMODULE_REVISION_THREE = "2021-12-04";
	private static final String SUBMODULE_XYZ_VERSION_THREE = "2021.12.4";

	@Test
	public void test___transform_one_only() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, DEFAULT_TARGET_VERSION);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-only");
		properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, GENERATED_MODELS_BASE_DIR);

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final List<ModelInfo> output = yangTransformerPlugin.transform(properties);

			assertSize(1, output);
			assertEModelCreatedCount(1, 0, 0, 0, 1, 0);

			final ManagementInformationModelInformation cfmMimInfo = load(output.get(0));
			assertSize(1, cfmMimInfo.getSupportedMims().getMimMappedTo());

			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(0), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___transform_one_only___and_mock_managed_element() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, DEFAULT_TARGET_VERSION);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-only");
		properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, GENERATED_MODELS_BASE_DIR);

		final ModelInfo modelInfoMockMe = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "zzz", Constants.MANAGEDELEMENT, Constants.ONE_ZERO_ZERO);
		properties.setProperty(YangTransformer2PluginProperties.MOCK_MANAGED_ELEMENT_NAMESPACE, modelInfoMockMe.getNamespace());

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final List<ModelInfo> output = yangTransformerPlugin.transform(properties);

			assertSize(1, output);
			assertEModelCreatedCount(2, 0, 0, 0, 1, 0);

			final ManagementInformationModelInformation cfmMimInfo = load(output.get(0));
			assertSize(2, cfmMimInfo.getSupportedMims().getMimMappedTo());

			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(0), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);

			assertEquals(modelInfoMockMe.getNamespace(), cfmMimInfo.getSupportedMims().getMimMappedTo().get(1).getNamespace());
			assertEquals(modelInfoMockMe.toUrn(), cfmMimInfo.getSupportedMims().getMimMappedTo().get(1).getOriginalModelUrn());
			assertEquals(modelInfoMockMe.getVersion().toString(), cfmMimInfo.getSupportedMims().getMimMappedTo().get(1).getVersion());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___transform_one_and_two() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, DEFAULT_TARGET_VERSION);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-and-two");
		properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, GENERATED_MODELS_BASE_DIR);

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final List<ModelInfo> output = yangTransformerPlugin.transform(properties);

			assertSize(1, output);
			assertEModelCreatedCount(2, 0, 0, 0, 2, 0);

			final ManagementInformationModelInformation cfmMimInfo = load(output.get(0));
			assertSize(2, cfmMimInfo.getSupportedMims().getMimMappedTo());

			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(0), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);
			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(1), MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO, MODULE_XYZ_VERSION_TWO);

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___transform_one_and_two_and_three() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, DEFAULT_TARGET_VERSION);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-and-two-and-three");
		properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, GENERATED_MODELS_BASE_DIR);

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final List<ModelInfo> output = yangTransformerPlugin.transform(properties);

			assertSize(1, output);
			assertEModelCreatedCount(4, 0, 0, 0, 4, 0);

			final ManagementInformationModelInformation cfmMimInfo = load(output.get(0));
			assertSize(4, cfmMimInfo.getSupportedMims().getMimMappedTo());

			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(0), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);
			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(1), MODULE_NS_THREE, MODULE_NAME_THREE, MODULE_REVISION_THREE, MODULE_XYZ_VERSION_THREE);
			assertMimMappedToForSubmodule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(2), MODULE_NS_THREE, SUBMODULE_NAME_THREE, SUBMODULE_REVISION_THREE, SUBMODULE_XYZ_VERSION_THREE);
			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(3), MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO, MODULE_XYZ_VERSION_TWO);

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___transform_one_and_two_and_three___three_is_importing() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, DEFAULT_TARGET_VERSION);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-and-two-and-three/nmt-one.yang," + TEST_SUB_DIR + "one-and-two-and-three/nmt-two.yang");
		properties.setProperty(YangTransformer2PluginProperties.IMPORTING_MODULES, TEST_SUB_DIR + "one-and-two-and-three/nmt-three.yang," + TEST_SUB_DIR + "one-and-two-and-three/nmt-three-submodule.yang");
		properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, GENERATED_MODELS_BASE_DIR);

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final List<ModelInfo> output = yangTransformerPlugin.transform(properties);

			assertSize(1, output);
			assertEModelCreatedCount(2, 0, 0, 0, 4, 0);

			final ManagementInformationModelInformation cfmMimInfo = load(output.get(0));
			assertSize(4, cfmMimInfo.getSupportedMims().getMimMappedTo());

			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(0), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);
			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(1), MODULE_NS_THREE, MODULE_NAME_THREE, MODULE_REVISION_THREE, MODULE_XYZ_VERSION_THREE);
			assertMimMappedToForSubmodule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(2), MODULE_NS_THREE, SUBMODULE_NAME_THREE, SUBMODULE_REVISION_THREE, SUBMODULE_XYZ_VERSION_THREE);
			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(3), MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO, MODULE_XYZ_VERSION_TWO);

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___transform_one_and_two_and_three___and_importing_orig_modules() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, DEFAULT_TARGET_VERSION);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-and-two-and-three");
		properties.setProperty(YangTransformer2PluginProperties.IMPORTING_MODULES, THREEGPP_YANG_EXT);
		properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, GENERATED_MODELS_BASE_DIR);

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final List<ModelInfo> output = yangTransformerPlugin.transform(properties);

			assertSize(1, output);
			assertEModelCreatedCount(4, 0, 0, 0, 5, 0);

			final ManagementInformationModelInformation cfmMimInfo = load(output.get(0));
			assertSize(5, cfmMimInfo.getSupportedMims().getMimMappedTo());

			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(0), "urn:3gpp:sa5:_3gpp-common-yang-extensions", "_3gpp-common-yang-extensions", "2024-04-05", "2024.4.5");
			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(1), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);
			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(2), MODULE_NS_THREE, MODULE_NAME_THREE, MODULE_REVISION_THREE, MODULE_XYZ_VERSION_THREE);
			assertMimMappedToForSubmodule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(3), MODULE_NS_THREE, SUBMODULE_NAME_THREE, SUBMODULE_REVISION_THREE, SUBMODULE_XYZ_VERSION_THREE);
			assertMimMappedToForModule(cfmMimInfo.getSupportedMims().getMimMappedTo().get(4), MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO, MODULE_XYZ_VERSION_TWO);

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	// - - - - - - - - - - - - - GENERATE CFM MIM INFO only - - - - - - - - - - - - - - - - -

	@Test
	public void test___generate_cfm_miminfo_context_for_network_functions() {

		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "  " + DEFAULT_TARGET_TYPE + "   ");
		properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, SUPPORTED_MIMS_FILE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/location");

		try {
			final Set<NetworkFunctionInfo> networkFunctionInfos = new HashSet<>();
			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, networkFunctionInfos);

			assertEquals(0, context.getNetworkFunctionsToPath().size());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Set<NetworkFunctionInfo> networkFunctionInfos = new HashSet<>();
			networkFunctionInfos.add(new NetworkFunctionInfo("DU", "_3gpp:sa5:GNodeBDUFunction", "GNodeBDUFunction", null));

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, networkFunctionInfos);

			assertEquals(1, context.getNetworkFunctionsToPath().size());
			assertTrue(context.getNetworkFunctionsToPath().containsKey("DU"));
			assertEquals("/ManagedElement/GNodeBDUFunction", context.getNetworkFunctionsToPath().get("DU"));

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Set<NetworkFunctionInfo> networkFunctionInfos = new HashSet<>();
			networkFunctionInfos.add(new NetworkFunctionInfo("   DU   ", "   _3gpp:sa5:GNodeBDUFunction   ", "   GNodeBDUFunction   ", "    "));

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, networkFunctionInfos);

			assertEquals(1, context.getNetworkFunctionsToPath().size());
			assertTrue(context.getNetworkFunctionsToPath().containsKey("DU"));
			assertEquals("/ManagedElement/GNodeBDUFunction", context.getNetworkFunctionsToPath().get("DU"));

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Set<NetworkFunctionInfo> networkFunctionInfos = new HashSet<>();
			networkFunctionInfos.add(new NetworkFunctionInfo("DU", "_3gpp:sa5:GNodeBDUFunction", "GNodeBDUFunction", null));
			networkFunctionInfos.add(new NetworkFunctionInfo("CU-UP", "_3gpp:sa5:GNodeBCUUPFunction", "GNodeBCUUPFunction", null));

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, networkFunctionInfos);

			assertEquals(2, context.getNetworkFunctionsToPath().size());
			assertTrue(context.getNetworkFunctionsToPath().containsKey("DU"));
			assertEquals("/ManagedElement/GNodeBDUFunction", context.getNetworkFunctionsToPath().get("DU"));
			assertTrue(context.getNetworkFunctionsToPath().containsKey("CU-UP"));
			assertEquals("/ManagedElement/GNodeBCUUPFunction", context.getNetworkFunctionsToPath().get("CU-UP"));

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Set<NetworkFunctionInfo> networkFunctionInfos = new HashSet<>();
			networkFunctionInfos.add(new NetworkFunctionInfo("BGF", "ericsson:bgf-funtion", "BgfFunction", "/BgfFuntion-*"));
			networkFunctionInfos.add(new NetworkFunctionInfo("DU", "_3gpp:sa5:GNodeBDUFunction", "GNodeBDUFunction", null));

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, networkFunctionInfos);

			assertEquals(2, context.getNetworkFunctionsToPath().size());
			assertTrue(context.getNetworkFunctionsToPath().containsKey("DU"));
			assertEquals("/ManagedElement/GNodeBDUFunction", context.getNetworkFunctionsToPath().get("DU"));
			assertTrue(context.getNetworkFunctionsToPath().containsKey("BGF"));
			assertEquals("/BgfFuntion-*", context.getNetworkFunctionsToPath().get("BGF"));

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Set<NetworkFunctionInfo> networkFunctionInfos = new HashSet<>();
			networkFunctionInfos.add(new NetworkFunctionInfo("SMF", "urn:rdns:com:ericsson:oammodel:ericsson-epg", "feature-activation", "/epg/node/feature-activation/smf-5gc"));
			networkFunctionInfos.add(new NetworkFunctionInfo("   PGW-C   ", "  urn:rdns:com:ericsson:oammodel:ericsson-epg  ", "  pgw  ", "    /epg/pgw     "));

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, networkFunctionInfos);

			assertEquals(2, context.getNetworkFunctionsToPath().size());
			assertTrue(context.getNetworkFunctionsToPath().containsKey("SMF"));
			assertEquals("/epg/node/feature-activation/smf-5gc", context.getNetworkFunctionsToPath().get("SMF"));
			assertTrue(context.getNetworkFunctionsToPath().containsKey("PGW-C"));
			assertEquals("/epg/pgw", context.getNetworkFunctionsToPath().get("PGW-C"));

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___bad_mandatory_parameters() {
		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);

			new YangTransformer2Plugin().generateSupportedModelInformation(properties);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, "/some/location");

			new YangTransformer2Plugin().generateSupportedModelInformation(properties);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/location");

			new YangTransformer2Plugin().generateSupportedModelInformation(properties);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/location");
			properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "");

			new YangTransformer2Plugin().generateSupportedModelInformation(properties);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			final Set<NetworkFunctionInfo> networkFunctionInfos = new HashSet<>();
			networkFunctionInfos.add(new NetworkFunctionInfo("function", "namespace", "moc", null));

			final Properties properties = new Properties();
			properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "ABC");
			properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, "/some/location");

			new YangTransformer2Plugin().generateSupportedModelInformation(properties, networkFunctionInfos);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	@Test
	public void test___generate_cfm_miminfo___with_supported_mims_file_that_does_not_exist() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, SUPPORTED_MIMS_FILE + "_no_exist");

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			yangTransformerPlugin.generateSupportedModelInformation(properties);
			fail("Should have thrown.");

		} catch (final Exception expected) {}
	}

	private static final String SUPPORTED_MIMS_FILE = TEST_SUB_DIR + "supported-mims-file/NE-defined-123.456.789.xml";

	@Test
	public void test___generate_cfm_miminfo___using_supported_mims_file() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "  " + DEFAULT_TARGET_TYPE + "   ");
		properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, SUPPORTED_MIMS_FILE);

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertSize(1, supportedModelInformation.getSupportedModelInfos());

			assertNoEModelsCreated();

			assertSize(2, supportedModelInformation.getSupportedMimTypes());

			assertEquals("aaa:urn:xxx", supportedModelInformation.getSupportedMimTypes().get(0).getNamespace());
			assertEquals("2015.11.19", supportedModelInformation.getSupportedMimTypes().get(0).getVersion());

			assertEquals("bbb:urn:yyy", supportedModelInformation.getSupportedMimTypes().get(1).getNamespace());
			assertEquals("2015.2.5", supportedModelInformation.getSupportedMimTypes().get(1).getVersion());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___using_supported_mims_file___empty_excludelist() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "  " + DEFAULT_TARGET_TYPE + "   ");
		properties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, "");
		properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, SUPPORTED_MIMS_FILE);

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertSize(1, supportedModelInformation.getSupportedModelInfos());

			assertNoEModelsCreated();

			assertSize(2, supportedModelInformation.getSupportedMimTypes());

			assertEquals("aaa:urn:xxx", supportedModelInformation.getSupportedMimTypes().get(0).getNamespace());
			assertEquals("2015.11.19", supportedModelInformation.getSupportedMimTypes().get(0).getVersion());

			assertEquals("bbb:urn:yyy", supportedModelInformation.getSupportedMimTypes().get(1).getNamespace());
			assertEquals("2015.2.5", supportedModelInformation.getSupportedMimTypes().get(1).getVersion());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___using_supported_mims_file___with_mock_managed_element() {
		final ModelInfo modelInfoMockMe = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "zzz", Constants.MANAGEDELEMENT, Constants.ONE_ZERO_ZERO);

		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, "   " + SUPPORTED_MIMS_FILE + "   ");
		properties.setProperty(YangTransformer2PluginProperties.MOCK_MANAGED_ELEMENT_NAMESPACE, modelInfoMockMe.getNamespace());

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertSize(1, supportedModelInformation.getSupportedModelInfos());

			assertNoEModelsCreated();

			assertSize(3, supportedModelInformation.getSupportedMimTypes());

			assertEquals("aaa:urn:xxx", supportedModelInformation.getSupportedMimTypes().get(0).getNamespace());
			assertEquals("2015.11.19", supportedModelInformation.getSupportedMimTypes().get(0).getVersion());

			assertEquals("bbb:urn:yyy", supportedModelInformation.getSupportedMimTypes().get(1).getNamespace());
			assertEquals("2015.2.5", supportedModelInformation.getSupportedMimTypes().get(1).getVersion());

			assertEquals(modelInfoMockMe.getNamespace(), supportedModelInformation.getSupportedMimTypes().get(2).getNamespace());
			assertEquals(modelInfoMockMe.getVersion().toString(), supportedModelInformation.getSupportedMimTypes().get(2).getVersion());
			assertEquals(modelInfoMockMe.toUrn(), supportedModelInformation.getSupportedMimTypes().get(2).getOriginalModelUrn());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___using_supported_mims_file___with_excludelist___with_mock_managed_element() {
		final ModelInfo modelInfoMockMe = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "zzz", Constants.MANAGEDELEMENT, Constants.ONE_ZERO_ZERO);

		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, SUPPORTED_MIMS_FILE);
		properties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, "aaa.*");
		properties.setProperty(YangTransformer2PluginProperties.MOCK_MANAGED_ELEMENT_NAMESPACE, modelInfoMockMe.getNamespace());

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertSize(1, supportedModelInformation.getSupportedModelInfos());

			assertNoEModelsCreated();

			assertSize(2, supportedModelInformation.getSupportedMimTypes());

			assertEquals("bbb:urn:yyy", supportedModelInformation.getSupportedMimTypes().get(0).getNamespace());
			assertEquals("2015.2.5", supportedModelInformation.getSupportedMimTypes().get(0).getVersion());

			assertEquals(modelInfoMockMe.getNamespace(), supportedModelInformation.getSupportedMimTypes().get(1).getNamespace());
			assertEquals(modelInfoMockMe.getVersion().toString(), supportedModelInformation.getSupportedMimTypes().get(1).getVersion());
			assertEquals(modelInfoMockMe.toUrn(), supportedModelInformation.getSupportedMimTypes().get(1).getOriginalModelUrn());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___yang_input___one_only() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-only");

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertSize(1, supportedModelInformation.getSupportedModelInfos());

			assertNoEModelsCreated();

			assertSize(1, supportedModelInformation.getSupportedMimTypes());

			assertMimMappedToForModule(supportedModelInformation.getSupportedMimTypes().get(0), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___yang_input___one_only___with_network_function_info() {

		final Set<NetworkFunctionInfo> networkFunctionInfos = new HashSet<>();
		networkFunctionInfos.add(new NetworkFunctionInfo("Test-NF1", "urn:test:nmt-one", "cont1", "/cont1"));

		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-only");

		try {
			final SupportedModelInformation supportedModelInformation = new YangTransformer2Plugin().generateSupportedModelInformation(properties, networkFunctionInfos);
			assertSize(1, supportedModelInformation.getDeclaredNetworkFunctions());
			assertTrue(supportedModelInformation.getDeclaredNetworkFunctions().contains("Test-NF1"));

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___yang_input___one_only___and_mock_managed_element() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-only");

		final ModelInfo modelInfoMockMe = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "zzz", Constants.MANAGEDELEMENT, Constants.ONE_ZERO_ZERO);
		properties.setProperty(YangTransformer2PluginProperties.MOCK_MANAGED_ELEMENT_NAMESPACE, modelInfoMockMe.getNamespace());

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertSize(1, supportedModelInformation.getSupportedModelInfos());

			assertNoEModelsCreated();

			assertSize(2, supportedModelInformation.getSupportedMimTypes());

			assertMimMappedToForModule(supportedModelInformation.getSupportedMimTypes().get(0), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);

			assertEquals(modelInfoMockMe.getNamespace(), supportedModelInformation.getSupportedMimTypes().get(1).getNamespace());
			assertEquals(modelInfoMockMe.toUrn(), supportedModelInformation.getSupportedMimTypes().get(1).getOriginalModelUrn());
			assertEquals(modelInfoMockMe.getVersion().toString(), supportedModelInformation.getSupportedMimTypes().get(1).getVersion());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___yang_input_one_only___using_supported_mims_file___with_excludelist___and_mock_managed_element() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-only");
		properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, SUPPORTED_MIMS_FILE);
		properties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, "aaa.*");

		final ModelInfo modelInfoMockMe = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "zzz", Constants.MANAGEDELEMENT, Constants.ONE_ZERO_ZERO);
		properties.setProperty(YangTransformer2PluginProperties.MOCK_MANAGED_ELEMENT_NAMESPACE, modelInfoMockMe.getNamespace());

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertSize(1, supportedModelInformation.getSupportedModelInfos());

			assertNoEModelsCreated();

			assertSize(3, supportedModelInformation.getSupportedMimTypes());

			assertEquals("bbb:urn:yyy", supportedModelInformation.getSupportedMimTypes().get(0).getNamespace());
			assertEquals("2015.2.5", supportedModelInformation.getSupportedMimTypes().get(0).getVersion());

			assertMimMappedToForModule(supportedModelInformation.getSupportedMimTypes().get(1), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);

			assertEquals(modelInfoMockMe.getNamespace(), supportedModelInformation.getSupportedMimTypes().get(2).getNamespace());
			assertEquals(modelInfoMockMe.toUrn(), supportedModelInformation.getSupportedMimTypes().get(2).getOriginalModelUrn());
			assertEquals(modelInfoMockMe.getVersion().toString(), supportedModelInformation.getSupportedMimTypes().get(2).getVersion());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___yang_input_empty_dir___using_supported_mims_file() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "empty-dir");
		properties.setProperty(YangTransformer2PluginProperties.SUPPORTED_MIMS_FILE, SUPPORTED_MIMS_FILE);

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertSize(1, supportedModelInformation.getSupportedModelInfos());

			assertNoEModelsCreated();

			assertSize(2, supportedModelInformation.getSupportedMimTypes());

			assertEquals("aaa:urn:xxx", supportedModelInformation.getSupportedMimTypes().get(0).getNamespace());
			assertEquals("2015.11.19", supportedModelInformation.getSupportedMimTypes().get(0).getVersion());

			assertEquals("bbb:urn:yyy", supportedModelInformation.getSupportedMimTypes().get(1).getNamespace());
			assertEquals("2015.2.5", supportedModelInformation.getSupportedMimTypes().get(1).getVersion());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___yang_input___and_importing_orig_modules() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-and-two-and-three");
		properties.setProperty(YangTransformer2PluginProperties.IMPORTING_MODULES, THREEGPP_YANG_EXT);

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);
			assertSize(1, supportedModelInformation.getSupportedModelInfos());

			assertNoEModelsCreated();

			assertSize(5, supportedModelInformation.getSupportedMimTypes());

			assertMimMappedToForModule(supportedModelInformation.getSupportedMimTypes().get(0), "urn:3gpp:sa5:_3gpp-common-yang-extensions", "_3gpp-common-yang-extensions", "2024-04-05", "2024.4.5");
			assertMimMappedToForModule(supportedModelInformation.getSupportedMimTypes().get(1), MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE, MODULE_XYZ_VERSION_ONE);
			assertMimMappedToForModule(supportedModelInformation.getSupportedMimTypes().get(2), MODULE_NS_THREE, MODULE_NAME_THREE, MODULE_REVISION_THREE, MODULE_XYZ_VERSION_THREE);
			assertMimMappedToForSubmodule(supportedModelInformation.getSupportedMimTypes().get(3), MODULE_NS_THREE, SUBMODULE_NAME_THREE, SUBMODULE_REVISION_THREE, SUBMODULE_XYZ_VERSION_THREE);
			assertMimMappedToForModule(supportedModelInformation.getSupportedMimTypes().get(4), MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO, MODULE_XYZ_VERSION_TWO);

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___yang_input___and_importing_orig_modules___with_network_functions() {

		final Set<NetworkFunctionInfo> networkFunctionInfos = new HashSet<>();
		networkFunctionInfos.add(new NetworkFunctionInfo("Test-NF1", "urn:test:nmt-one", "cont1", "/cont1"));
		networkFunctionInfos.add(new NetworkFunctionInfo("Test-NF2", "urn:test:nmt-two", "cont2", "/cont2"));
		networkFunctionInfos.add(new NetworkFunctionInfo("Test-NF3", "urn:test:nmt-three", "cont3", "/cont3"));
		networkFunctionInfos.add(new NetworkFunctionInfo("Test-NF4", "urn:test:nmt-three", "cont4", "/cont4"));		// sits in submodule

		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "one-and-two-and-three");
		properties.setProperty(YangTransformer2PluginProperties.IMPORTING_MODULES, THREEGPP_YANG_EXT);

		try {
			final SupportedModelInformation supportedModelInformation = new YangTransformer2Plugin().generateSupportedModelInformation(properties, networkFunctionInfos);
			assertSize(4, supportedModelInformation.getDeclaredNetworkFunctions());
			assertTrue(supportedModelInformation.getDeclaredNetworkFunctions().contains("Test-NF1"));
			assertTrue(supportedModelInformation.getDeclaredNetworkFunctions().contains("Test-NF2"));
			assertTrue(supportedModelInformation.getDeclaredNetworkFunctions().contains("Test-NF3"));
			assertTrue(supportedModelInformation.getDeclaredNetworkFunctions().contains("Test-NF4"));

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___logging_output() {
		final Logger rootLogger = Logger.getRootLogger();
		final ConsoleAppender rootLoggerConsoleAppender = (ConsoleAppender) rootLogger.getAllAppenders().nextElement();		// assumes only one appender attached....
		rootLoggerConsoleAppender.setThreshold(Level.FATAL);

		final Logger nmtPluginLogger = Logger.getLogger(YangTransformer2Plugin.class);
		final TestCaptureAppender testCaptureAppender = new TestCaptureAppender(new PatternLayout());
		testCaptureAppender.setName("TestAppender");
		nmtPluginLogger.addAppender(testCaptureAppender);
		nmtPluginLogger.setLevel(Level.INFO);

		try {
			final Properties propertiesRun1 = new Properties();
			propertiesRun1.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
			propertiesRun1.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, DEFAULT_TARGET_VERSION);
			propertiesRun1.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "invalid-module");
			propertiesRun1.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, GENERATED_MODELS_BASE_DIR);

			final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

			yangTransformerPlugin.transform(propertiesRun1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		final String capturedOutputRun1 = testCaptureAppender.getCapturedOutput();
		assertTrue(capturedOutputRun1.contains(ParserFindingType.P013_INVALID_SYNTAX_AT_DOCUMENT_ROOT.toString()));
		assertTrue(capturedOutputRun1.contains(Constants.TEXT_SETTINGS));

		testCaptureAppender.reset();

		try {
			final Properties propertiesRun2 = new Properties();
			propertiesRun2.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
			propertiesRun2.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, DEFAULT_TARGET_VERSION);
			propertiesRun2.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "warning-module");
			propertiesRun2.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, GENERATED_MODELS_BASE_DIR);

			final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

			yangTransformerPlugin.transform(propertiesRun2);
		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		final String capturedOutputRun2 = testCaptureAppender.getCapturedOutput();
		assertTrue(capturedOutputRun2.contains(ParserFindingType.P034_UNRESOLVABLE_IMPORT.toString()));

		nmtPluginLogger.removeAllAppenders();
		nmtPluginLogger.setLevel(Level.OFF);
		rootLoggerConsoleAppender.setThreshold(Level.ERROR);
	}

	@Test
	public void test___getTransformerName() {
		assertEquals("YangTransformer2", new YangTransformer2Plugin().getTransformerName());
	}

	@Test
	public void test___getFunctionalTransformerVersion() {
		final XyzModelVersionInfo transformerFunctionalVersion = new YangTransformer2Plugin().getFunctionalTransformerVersion();
		assertNull(transformerFunctionalVersion.getExt());
	}

	@Test
	public void test___transformerIsEnabled_skipPropertyNotSet() {
		assertTrue(new YangTransformer2Plugin().isEnabled(new Properties()));
	}

	@Test
	public void test___transformerIsEnabled_skipPropertySetToFalse() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.SKIP_TRANSFORM, "false");

		assertTrue(new YangTransformer2Plugin().isEnabled(properties));
	}

	@Test
	public void test___transformerIsEnabled_skipPropertySetToTrue() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.SKIP_TRANSFORM, "true");

		assertFalse(new YangTransformer2Plugin().isEnabled(properties));
	}

	@Test
	public void test___transformerIsEnabled_skipPropertySetToInvalidValue() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.SKIP_TRANSFORM, "invalidIgnoredValue");

		assertTrue(new YangTransformer2Plugin().isEnabled(properties));
	}

	private void assertMimMappedToForModule(final SupportedMimType supportedMimType, final String moduleNamespace, final String moduleName, final String moduleRevision, final String moduleXyzVersion) {
		assertEquals(moduleNamespace, supportedMimType.getNamespace());
		assertEquals(moduleXyzVersion, supportedMimType.getVersion());
		final ModelInfo modelInfo = new ModelInfo(SchemaConstants.NET_YANG, moduleNamespace, moduleName + "@" + moduleRevision);
		assertEquals(modelInfo.toUrn(), supportedMimType.getOriginalModelUrn());
	}

	private void assertMimMappedToForSubmodule(final SupportedMimType supportedMimType, final String owningModuleNamespace, final String submoduleName, final String submoduleRevision, final String submoduleXyzVersion) {
		assertEquals(owningModuleNamespace + "::" + submoduleName, supportedMimType.getNamespace());
		assertEquals(submoduleXyzVersion, supportedMimType.getVersion());
		final ModelInfo modelInfo = new ModelInfo(SchemaConstants.NET_YANG, owningModuleNamespace, submoduleName + "@" + submoduleRevision);
		assertEquals(modelInfo.toUrn(), supportedMimType.getOriginalModelUrn());
	}
}
