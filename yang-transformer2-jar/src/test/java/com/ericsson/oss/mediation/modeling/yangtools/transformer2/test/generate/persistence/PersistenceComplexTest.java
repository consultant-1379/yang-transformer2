package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.ReadBehavior;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.WriteBehavior;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.ReadBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class PersistenceComplexTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "persistence/";

	private static final String MODULE_NS_ONE = "urn~test~persistence-complex-one";
	private static final String MODULE_NAME_ONE = "persistence-complex-one";
	private static final String MODULE_REVISION_ONE = "2021-10-18";
	private static final String MODULE_XYZ_VERSION_ONE = "2021.10.18";

	private static final String MODULE_NS_TWO = "urn:test:persistence-complex-two";
	private static final String MODULE_NAME_TWO = "persistence-complex-two";
	private static final String MODULE_REVISION_TWO = "2021-11-28";
	private static final String MODULE_XYZ_VERSION_TWO = "2021.11.28";

	private static final String MODULE_NS_THREE = "urn:test:persistence-complex-three";
	private static final String MODULE_NAME_THREE = "persistence-complex-three";
	private static final String MODULE_REVISION_THREE = "2021-12-24";
	private static final String MODULE_XYZ_VERSION_THREE = "2021.12.24";

	@Test
	public void test___persistence_complex___base_and_augment_only_for_now() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "persistence-complex/persistence-complex-one.yang"), new File(TEST_SUB_DIR + "persistence-complex/persistence-complex-two.yang")));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(8, 1, 0, 0, 6, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont1", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont1);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont1);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertTrue(leaf11.isMandatory());
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf11);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf11);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont2", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont2);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont2);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertFalse(leaf21.isMandatory());
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf21);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf21);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont3", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont3);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont3);

		final PrimaryTypeAttribute leaf31 = findAttribute(cont3, "leaf31");
		assertFalse(leaf31.isMandatory());
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf31);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf31);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont4", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont4);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont4);

		final PrimaryTypeAttribute leaf41 = findAttribute(cont4, "leaf41");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf41);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf41);

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont5", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont5 = load(cont5modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont5);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont5);

		final PrimaryTypeAttribute leaf51 = findAttribute(cont5, "leaf51");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf51);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf51);

		final ModelInfo cont7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont7", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont7 = load(cont7modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont7);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont7);

		final PrimaryTypeAttribute leaf73 = findAttribute(cont7, "leaf73");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf73);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf73);

		final PrimaryTypeAttribute leaf74 = findAttribute(cont7, "leaf74");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf74);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf74);

		final ModelInfo cont8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont8", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont8 = load(cont8modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont8);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont8);			// because it's under cont3, which is 'config false'

		final PrimaryTypeAttribute leaf85 = findAttribute(cont8, "leaf85");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf85);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf85);

		final PrimaryTypeAttribute leaf86 = findAttribute(cont8, "leaf86");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf86);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf86);

		final ModelInfo cont9modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont9", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont9 = load(cont9modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont9);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont9);

		final PrimaryTypeAttribute leaf91 = findAttribute(cont9, "leaf91");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf91);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf91);

		// -----------------------------------------------------

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_ONE, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);

		assertSize(4, cont1ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf13 = findAddedAttribute(cont1ext, "leaf13");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf13);
		assertWriteBehaviour(WriteBehaviorType.DELEGATE, leaf13);

		final PrimaryTypeAttribute leaf14 = findAddedAttribute(cont1ext, "leaf14");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf14);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf14);

		final PrimaryTypeAttribute leaf15 = findAddedAttribute(cont1ext, "leaf15");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf15);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf15);

		final PrimaryTypeAttribute leaf16 = findAddedAttribute(cont1ext, "leaf16");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf16);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf16);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 8);

		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont2");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont3");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont4");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont5");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont9");
		findAndAssertContainment(rels, "cont2", "cont7");
		findAndAssertContainment(rels, "cont3", "cont8");

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont1spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont1spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertTrue(attr11.isMandatory());
		assertEquals(MODULE_NS_ONE, attr11.getNamespace());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr11.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, attr11.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(MODULE_NS_TWO, attr13.getNamespace());
		assertEquals(ReadBehavior.FROM_DELEGATE, attr13.getReadBehavior());
		assertEquals(WriteBehavior.DELEGATE, attr13.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(MODULE_NS_TWO, attr14.getNamespace());
		assertEquals(ReadBehavior.FROM_DELEGATE, attr14.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr14.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
		assertEquals(MODULE_NS_TWO, attr15.getNamespace());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr15.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr15.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr16 = findAttribute(cont1spec, "leaf16");
		assertEquals(MODULE_NS_TWO, attr16.getNamespace());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr16.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr16.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont2spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont2spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertEquals(MODULE_NS_ONE, attr21.getNamespace());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr21.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, attr21.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont3spec.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, cont3spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont3spec, "leaf31");
		assertFalse(attr31.isMandatory());
		assertEquals(MODULE_NS_ONE, attr31.getNamespace());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr31.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr31.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont4spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont4spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr41 = findAttribute(cont4spec, "leaf41");
		assertEquals(MODULE_NS_ONE, attr41.getNamespace());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr41.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, attr41.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont5spec = modelService.getTypedAccess().getEModelSpecification(cont5modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont5spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont5spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr51 = findAttribute(cont5spec, "leaf51");
		assertEquals(MODULE_NS_TWO, attr51.getNamespace());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr51.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr51.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont7spec = modelService.getTypedAccess().getEModelSpecification(cont7modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont7spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont7spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr73 = findAttribute(cont7spec, "leaf73");
		assertEquals(MODULE_NS_TWO, attr73.getNamespace());
		assertEquals(ReadBehavior.FROM_DELEGATE, attr73.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr73.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr74 = findAttribute(cont7spec, "leaf74");
		assertEquals(MODULE_NS_TWO, attr74.getNamespace());
		assertEquals(ReadBehavior.FROM_DELEGATE, attr74.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr74.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont8spec = modelService.getTypedAccess().getEModelSpecification(cont8modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont8spec.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, cont8spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr85 = findAttribute(cont8spec, "leaf85");
		assertEquals(MODULE_NS_TWO, attr85.getNamespace());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr85.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr85.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr86 = findAttribute(cont8spec, "leaf86");
		assertEquals(MODULE_NS_TWO, attr86.getNamespace());
		assertEquals(ReadBehavior.FROM_DELEGATE, attr86.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr86.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont9spec = modelService.getTypedAccess().getEModelSpecification(cont9modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont9spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont9spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr91 = findAttribute(cont9spec, "leaf91");
		assertEquals(MODULE_NS_TWO, attr91.getNamespace());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr91.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, attr91.getWriteBehavior());

		// -----------------------------------------------------------------------------------------

		assertSingleContainmentParent(cont1spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont2spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont3spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont4spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont5spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont9spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont7spec, cont2modelInfo, null, null);
		assertSingleContainmentParent(cont8spec, cont3modelInfo, null, null);
	}

	@Test
	public void test___persistence_complex___base_and_augment_and_deviations() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "persistence-complex")));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(7, 4, 0, 0, 7, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont1", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont1);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont1);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertTrue(leaf11.isMandatory());
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf11);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf11);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont2", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont2);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont2);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertFalse(leaf21.isMandatory());
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf21);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf21);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont3", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont3);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont3);

		final PrimaryTypeAttribute leaf31 = findAttribute(cont3, "leaf31");
		assertFalse(leaf31.isMandatory());
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf31);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf31);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont4", MODULE_XYZ_VERSION_ONE);
		assertModelDoesNotExist(cont4modelInfo);		// deviated to 'config false', not notified, therefore should not have been generated.

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont5", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont5 = load(cont5modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont5);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont5);

		final PrimaryTypeAttribute leaf51 = findAttribute(cont5, "leaf51");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf51);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf51);

		final ModelInfo cont7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont7", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont7 = load(cont7modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont7);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont7);

		final PrimaryTypeAttribute leaf73 = findAttribute(cont7, "leaf73");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf73);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf73);

		final PrimaryTypeAttribute leaf74 = findAttribute(cont7, "leaf74");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf74);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf74);

		final ModelInfo cont8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont8", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont8 = load(cont8modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont8);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont8);			// because it's under cont3, which is 'config false'

		final PrimaryTypeAttribute leaf85 = findAttribute(cont8, "leaf85");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf85);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf85);

		final PrimaryTypeAttribute leaf86 = findAttribute(cont8, "leaf86");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf86);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf86);

		final ModelInfo cont9modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont9", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont9 = load(cont9modelInfo);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont9);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont9);

		final PrimaryTypeAttribute leaf91 = findAttribute(cont9, "leaf91");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf91);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf91);

		// -----------------------------------------------------

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_ONE, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);

		assertSize(4, cont1ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf13 = findAddedAttribute(cont1ext, "leaf13");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf13);
		assertWriteBehaviour(WriteBehaviorType.DELEGATE, leaf13);

		final PrimaryTypeAttribute leaf14 = findAddedAttribute(cont1ext, "leaf14");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf14);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf14);

		final PrimaryTypeAttribute leaf15 = findAddedAttribute(cont1ext, "leaf15");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf15);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf15);

		final PrimaryTypeAttribute leaf16 = findAddedAttribute(cont1ext, "leaf16");	// correct, deviates an augmented leaf, so is 'added', not 'replaced'
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf16);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf16);

		// -----------------------

		final ModelInfo cont7ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_TWO, "cont7");
		final PrimaryTypeExtensionDefinition cont7ext = load(cont7ExtensionModelInfo);

		assertTrue(cont7ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute().size() == 1);
		final PrimaryTypeAttribute cont7extLeaf73 = findReplacedAttribute(cont7ext, "leaf73");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, cont7extLeaf73);
		assertWriteBehaviour(WriteBehaviorType.DELEGATE, cont7extLeaf73);		// not static-data anymore, so writable

		// -----------------------------------------------------

		final ModelInfo cont9ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_TWO, "cont9");
		final PrimaryTypeExtensionDefinition cont9ext = load(cont9ExtensionModelInfo);

		assertTrue(cont9ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute().size() == 1);
		final PrimaryTypeAttribute cont9extLeaf91 = findReplacedAttribute(cont9ext, "leaf91");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, cont9extLeaf91);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont9extLeaf91);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 7);

		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont2");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont3");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont5");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont9");
		findAndAssertContainment(rels, "cont2", "cont7");
		findAndAssertContainment(rels, "cont3", "cont8");

		// -----------------------------------------------------

		final ModelInfo modelInfoForOne = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE);
		assertModelExists(modelInfoForOne);

		final ModelInfo modelInfoForTwo = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO);
		assertModelExists(modelInfoForTwo);

		final ModelInfo modelInfoForThree = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_THREE, MODULE_NAME_THREE, MODULE_REVISION_THREE);
		assertModelExists(modelInfoForThree);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertEquals(7, mimInfo.getSupportedMims().getMimMappedTo().size());

		assertHasSupportedMim(mimInfo, MODULE_NS_ONE, MODULE_XYZ_VERSION_ONE, modelInfoForOne.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_TWO, MODULE_XYZ_VERSION_TWO, modelInfoForTwo.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_THREE, MODULE_XYZ_VERSION_THREE, modelInfoForThree.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont1spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont1spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr11.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, attr11.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(ReadBehavior.FROM_DELEGATE, attr13.getReadBehavior());
		assertEquals(WriteBehavior.DELEGATE, attr13.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(ReadBehavior.FROM_DELEGATE, attr14.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr14.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr15.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr15.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr16 = findAttribute(cont1spec, "leaf16");
		assertEquals(ReadBehavior.FROM_DELEGATE, attr16.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr16.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont2spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont2spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr21.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, attr21.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont3spec.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, cont3spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont3spec, "leaf31");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr31.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr31.getWriteBehavior());

		assertModelDoesNotExistInModelService(modelService, cont4modelInfo);

		final HierarchicalPrimaryTypeSpecification cont5spec = modelService.getTypedAccess().getEModelSpecification(cont5modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont5spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont5spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr51 = findAttribute(cont5spec, "leaf51");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr51.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr51.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont7spec = modelService.getTypedAccess().getEModelSpecification(cont7modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont7spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont7spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr73 = findAttribute(cont7spec, "leaf73");
		assertEquals(ReadBehavior.FROM_DELEGATE, attr73.getReadBehavior());
		assertEquals(WriteBehavior.DELEGATE, attr73.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr74 = findAttribute(cont7spec, "leaf74");
		assertEquals(ReadBehavior.FROM_DELEGATE, attr74.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr74.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont8spec = modelService.getTypedAccess().getEModelSpecification(cont8modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont8spec.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, cont8spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr85 = findAttribute(cont8spec, "leaf85");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr85.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr85.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr86 = findAttribute(cont8spec, "leaf86");
		assertEquals(ReadBehavior.FROM_DELEGATE, attr86.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr86.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification cont9spec = modelService.getTypedAccess().getEModelSpecification(cont9modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, cont9spec.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, cont9spec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attr91 = findAttribute(cont9spec, "leaf91");
		assertEquals(ReadBehavior.FROM_DELEGATE, attr91.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr91.getWriteBehavior());

		// -----------------------------------------------------------------------------------------

		assertSingleContainmentParent(cont1spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont2spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont3spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont5spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont9spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont7spec, cont2modelInfo, null, null);
		assertSingleContainmentParent(cont8spec, cont3modelInfo, null, null);
	}

}
