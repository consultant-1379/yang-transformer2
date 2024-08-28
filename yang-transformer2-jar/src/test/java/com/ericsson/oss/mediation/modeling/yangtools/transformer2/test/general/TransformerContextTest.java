package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collections;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.NotificationBehaviour;

public class TransformerContextTest {

	@SuppressWarnings("deprecation")
	@Test
	public void test___defaults() {

		final TransformerContext context = new TransformerContext(Collections.emptyList(), Collections.emptyList(), new File(""), "type", "version");

		/*
		 * =============================================================================================
		 * 
		 *                                        WARNING
		 *
		 * Test failures here indicate that the default value for a property of the context has changed.
		 * This may cause different build results of existing projects if the properties whose default
		 * value has changed is not explicitly supplied.
		 */
		assertNull(context.getMockManagedElement());
		assertNull(context.getExplicitContainmentParent());
		assertTrue(context.getExcludedNamespaces().isEmpty());
		assertFalse(context.removeSchemaNodesHavingIfFeature());
		assertEquals(FeatureHandling.RETAIN_ALL, context.getFeatureHandling());
		assertFalse(context.generateNPcontainersAsSystemCreated());
		assertFalse(context.generateRpcs());
		assertTrue(context.getExcludedNamespacesForRpcs().isEmpty());
		assertFalse(context.apply3gppHandling());
		assertTrue(context.getSchemaNodesToRename().isEmpty());
		assertTrue(context.getSchemaNodesToRemove().isEmpty());
		assertTrue(context.dontUse$$syntaxForUniqueMocs());
		assertFalse(context.generateCombinedEdt());
		assertFalse(context.applyNodeAppInstanceNameHandling());
		assertEquals(NotificationBehaviour.EOI, context.getNotificationBehaviour());
	}

	@Test
	public void test___various() {

		final TransformerContext context = new TransformerContext(Collections.emptyList(), Collections.emptyList(), new File(""), "type", "version");

		// - - - - - - - - - - - - - - - - -

		final ModelInfo mi1 = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "ns1", "name1", "1.0.0");
		final ModelInfo mi2 = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "ns2", "name2", "2.0.0");

		final PrimaryTypeDefinition ptd = new PrimaryTypeDefinition();
		ptd.setNs(mi1.getNamespace());
		ptd.setName(mi1.getName());
		ptd.setVersion(mi1.getVersion().toString());

		context.getCreatedEmodels().addGeneratedEModel(ptd);

		try {
			context.getCreatedEmodels().addGeneratedEModel(ptd);
			fail("Expected an exception.");
		} catch (final Exception expected) {}

		assertTrue(context.getCreatedEmodels().containsGeneratedEModel(mi1));
		assertFalse(context.getCreatedEmodels().containsGeneratedEModel(mi2));

		// - - - - - - - - - - - - - - - - -

		context.getErrors();
		context.getWarnings();
		context.getInfos();

		// - - - - - - - - - - - - - - - - -

		context.setMockManagedElementNamespace("ns1");
		context.setExplicitContainmentParent(mi2);

		try {
			context.getEffectiveContainmentParent();
			fail("Expected an exception.");
		} catch (final Exception expected) {}

		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		assertEquals(FeatureHandling.CONFIGURED, context.getFeatureHandling());

		context.setNotificationBehaviour(NotificationBehaviour.EOI);
		assertEquals(NotificationBehaviour.EOI, context.getNotificationBehaviour());
	}

	@Test
	public void test___hard_error() {

		final TransformerContext context = new TransformerContext(Collections.emptyList(), Collections.emptyList(), new File(""), "type", "version");

		try {
			YangTransformer2.transform(context);
			fail("Expected an exception.");
		} catch (final Exception expected) {}

		assertFalse(context.getErrors().isEmpty());
	}

	@Test
	public void test___feature_handling(){

		assertEquals(FeatureHandling.REMOVE_ALL, FeatureHandling.fromString("REMOVE_ALL"));
		assertEquals(FeatureHandling.REMOVE_ALL, FeatureHandling.fromString("remove_all"));
		assertEquals(FeatureHandling.REMOVE_ALL, FeatureHandling.fromString("remove-all"));
		assertEquals(FeatureHandling.REMOVE_ALL, FeatureHandling.fromString("removeAll"));
		assertEquals(FeatureHandling.REMOVE_ALL, FeatureHandling.fromString("remove all"));

		assertEquals(FeatureHandling.RETAIN_ALL, FeatureHandling.fromString("retain-all"));

		assertEquals(FeatureHandling.CONFIGURED, FeatureHandling.fromString("configured"));

		try {
			FeatureHandling.fromString(null);
			fail("Expected an exception.");
		} catch (final Exception expected) {}

		try {
			FeatureHandling.fromString("");
			fail("Expected an exception.");
		} catch (final Exception expected) {}

		try {
			FeatureHandling.fromString("blurb");
			fail("Expected an exception.");
		} catch (final Exception expected) {}

		try {
			final TransformerContext context = new TransformerContext(Collections.emptyList(), Collections.emptyList(), new File(""), "type", "version");
			context.setFeatureHandling(null);
			fail("Expected an exception.");
		} catch (final Exception expected) {}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test___feature_handling___old_property(){

		final TransformerContext context = new TransformerContext(Collections.emptyList(), Collections.emptyList(), new File(""), "type", "version");

		context.setRemoveSchemaNodesHavingIfFeature(true);
		assertTrue(context.removeSchemaNodesHavingIfFeature());
		assertEquals(FeatureHandling.REMOVE_ALL, context.getFeatureHandling());

		context.setRemoveSchemaNodesHavingIfFeature(false);
		assertFalse(context.removeSchemaNodesHavingIfFeature());
		assertEquals(FeatureHandling.RETAIN_ALL, context.getFeatureHandling());
	}

	@Test
	public void test___notification_behaviour(){

		assertEquals(NotificationBehaviour.EOI, NotificationBehaviour.fromString("EOI"));
		assertEquals(NotificationBehaviour.EOI, NotificationBehaviour.fromString("eoi"));

		assertEquals(NotificationBehaviour.EOI, NotificationBehaviour.valueOf("EOI"));

		try {
			NotificationBehaviour.fromString(null);
			fail("Expected an exception.");
		} catch (final Exception expected) {}

		try {
			NotificationBehaviour.fromString("");
			fail("Expected an exception.");
		} catch (final Exception expected) {}

		try {
			NotificationBehaviour.fromString("blurb");
			fail("Expected an exception.");
		} catch (final Exception expected) {}

		try {
			final TransformerContext context = new TransformerContext(Collections.emptyList(), Collections.emptyList(), new File(""), "type", "version");
			context.setNotificationBehaviour(null);
			fail("Expected an exception.");
		} catch (final Exception expected) {}
	}
}
