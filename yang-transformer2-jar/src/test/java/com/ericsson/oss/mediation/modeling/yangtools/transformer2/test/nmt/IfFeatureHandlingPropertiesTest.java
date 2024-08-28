package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.nmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2Plugin;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class IfFeatureHandlingPropertiesTest extends TransformerTestUtil {

	@SuppressWarnings("deprecation")
	@Test
	public void test___transform___valid_property_combinations() {

		final Properties defaultProperties = new Properties();
		defaultProperties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
		defaultProperties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
		defaultProperties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "  type  ");
		defaultProperties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "   version   ");

		try {
			final Properties properties = new Properties(defaultProperties);

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertTrue(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE, " true ");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertTrue(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE, " false ");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertFalse(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.RETAIN_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "remove-all");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertTrue(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "RETAIN-ALL");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertFalse(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.RETAIN_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "configured");
			properties.setProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "/some/instance-data-file");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertFalse(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.CONFIGURED, context.getFeatureHandling());
			assertNotNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "RETAIN_ALL");
			properties.setProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "/some/instance-data-file");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertFalse(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.RETAIN_ALL, context.getFeatureHandling());
			assertNotNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE, "false");
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "configured");
			properties.setProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "/some/instance-data-file");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertFalse(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.CONFIGURED, context.getFeatureHandling());
			assertNotNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "/some/instance-data-file");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			assertTrue(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());
			assertNotNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___transform___invalid_property_combinations() {

		final Properties defaultProperties = new Properties();
		defaultProperties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
		defaultProperties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
		defaultProperties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "  type  ");
		defaultProperties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "   version   ");

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "configured");

			YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "blurb");
			properties.setProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "/some/instance-data-file");

			YangTransformer2Plugin.createAndSetupContextForTransformation(properties);

			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test___generateSupportedModelInformation___valid_property_combinations() {

		final Properties defaultProperties = new Properties();
		defaultProperties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
		defaultProperties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
		defaultProperties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "  type  ");
		defaultProperties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "   version   ");

		try {
			final Properties properties = new Properties(defaultProperties);

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			assertTrue(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE, " true ");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			assertTrue(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE, " false ");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			assertFalse(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.RETAIN_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "remove_all");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			assertTrue(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "retainall");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			assertFalse(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.RETAIN_ALL, context.getFeatureHandling());
			assertNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "configured");
			properties.setProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "/some/instance-data-file");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			assertFalse(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.CONFIGURED, context.getFeatureHandling());
			assertNotNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "RETAIN_ALL");
			properties.setProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "/some/instance-data-file");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			assertFalse(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.RETAIN_ALL, context.getFeatureHandling());
			assertNotNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "/some/instance-data-file");

			final TransformerContext context = YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			assertTrue(context.removeSchemaNodesHavingIfFeature());
			assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());
			assertNotNull(context.getYangLibraryInstanceDataFile());

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generateSupportedModelInformation___invalid_property_combinations() {

		final Properties defaultProperties = new Properties();
		defaultProperties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, "/some/dir");
		defaultProperties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, "/some/other/dir");
		defaultProperties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, "  type  ");
		defaultProperties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, "   version   ");

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "configured");

			YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			final Properties properties = new Properties(defaultProperties);
			properties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "blurb");
			properties.setProperty(YangTransformer2PluginProperties.YANG_LIBRARY, "/some/instance-data-file");

			YangTransformer2Plugin.createAndSetupContextForGenerateSupportedModelInformation(properties, Collections.emptySet());

			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}
}
