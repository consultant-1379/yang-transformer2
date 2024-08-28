package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate._3gpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

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
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.cdt.ComplexDataTypeAttributeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.cdt.ComplexDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeAttribute;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ComplexRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.ComplexTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.eri.ValidatorFindingTypeEri;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class Simple3gppTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-3gpp/";

	private static final String MODULE_NS = "urn:test:simple-3gpp";
	private static final String MODULE_NAME = "simple-3gpp";
	private static final String MODULE_REVISION = "2021-11-08";
	private static final String MODULE_XYZ_VERSION = "2021.11.8";

	private static final String MODULE_NS_DEV = "urn:test:simple-3gpp-dev";

	@Test
	public void test___simple_3gpp() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N665_INCORRECT_3GPP_MOC_CONTENT.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N674_MORE_THAN_ONE_KEY.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E641_NOT_NOTIFIABLE_NSD_MISMATCH.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E642_READ_ONLY_MOC_EFFECTIVELY_NSD_FALSE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E647_NON_ERICSSON_MODULE_IMPORTS_ERICSSON_MODULE.toString());

		final TransformerContext context = createContextWith3ppModules(new File(TEST_SUB_DIR + "simple-3gpp"));
		context.setApply3gppHandling(true);
		context.setFeatureHandling(FeatureHandling.RETAIN_ALL);

		YangTransformer2.transform(context);

		internal___simple_3gpp(context, null);
	}

	@Test
	public void test___simple_3gpp_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_3GPP_HANDLING, " true ");

		final Properties actualNmtProperties = transformThroughNmtPluginWith3ppModules(new File(TEST_SUB_DIR + "simple-3gpp"), overwritingProperties);

		internal___simple_3gpp(null, actualNmtProperties);
	}

	private void internal___simple_3gpp(final TransformerContext context, final Properties actualNmtProperties) {
		assertEModelCreatedCount(5, 5, 0, 0, 6, 11);

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ModelInfo modelInfoStructSequence13cdt = ComplexTypeGenerator.getModelInfoForCdtInOriginal(MODULE_NS, "Cont1__structsequence13", MODULE_XYZ_VERSION);
		final ComplexDataTypeDefinition structSequence13cdt = load(modelInfoStructSequence13cdt);
		assertSize(3, structSequence13cdt.getAttribute());
		assertHasMeta(Constants.META_MULTIPLE_KEY_NAMES, "member16 member15", structSequence13cdt);

		final ComplexDataTypeAttribute member15 = findStructMember(structSequence13cdt.getAttribute(), "member15");
		assertEquals(MODULE_NS, member15.getNamespace());
		assertTrue(member15.isKey());
		assertInstanceOf(StringType.class, member15.getType());

		final ComplexDataTypeAttribute member16 = findStructMember(structSequence13cdt.getAttribute(), "member16");
		assertEquals(MODULE_NS, member16.getNamespace());
		assertTrue(member16.isKey());
		assertInstanceOf(BooleanType.class, member16.getType());

		final ComplexDataTypeAttribute member17 = findStructMember(structSequence13cdt.getAttribute(), "member17");
		assertEquals(MODULE_NS, member17.getNamespace());
		assertFalse(member17.isKey());
		assertInstanceOf(StringType.class, member17.getType());

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "Cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertHasMeta(Constants.META_PRESENCE_CONTAINER, cont1);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, cont1);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, cont1);

		assertSize(4, cont1.getPrimaryTypeAttribute());	// 2 leafs + 1 struct + auto-generated key = 4
		assertEmpty(cont1.getPrimaryTypeAction());

		assertNotNull(findAttribute(cont1.getPrimaryTypeAttribute(), "id"));
		assertHasMeta(Constants.META_ARTIFIAL_KEY, findAttribute(cont1.getPrimaryTypeAttribute(), "id"));
		assertNotNull(findAttribute(cont1.getPrimaryTypeAttribute(), "leaf11"));
		assertNotNull(findAttribute(cont1.getPrimaryTypeAttribute(), "leaf12"));
		assertNotNull(findAttribute(cont1.getPrimaryTypeAttribute(), "structsequence13"));
		assertInstanceOf(ListType.class, findAttribute(cont1.getPrimaryTypeAttribute(), "structsequence13").getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) findAttribute(cont1.getPrimaryTypeAttribute(), "structsequence13").getType()).getCollectionValuesType());
		assertEquals(modelInfoStructSequence13cdt.toImpliedUrn(), ((ComplexRefType) ((ListType) findAttribute(cont1.getPrimaryTypeAttribute(), "structsequence13").getType()).getCollectionValuesType()).getModelUrn());

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo miStructSequence13cdtExt = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "Cont1__structsequence13");
		final ComplexDataTypeDefinition structSequence13cdtExt = load(miStructSequence13cdtExt);
		assertHasMeta(Constants.META_MULTIPLE_KEY_NAMES, "member16 member15", structSequence13cdtExt);
		assertSize(3, structSequence13cdtExt.getAttribute());

		final ComplexDataTypeAttribute member15ext = findStructMember(structSequence13cdtExt.getAttribute(), "member15");
		assertEquals(MODULE_NS, member15ext.getNamespace());
		assertInstanceOf(StringType.class, member15ext.getType());

		final ComplexDataTypeAttribute member16ext = findStructMember(structSequence13cdtExt.getAttribute(), "member16");
		assertEquals(MODULE_NS, member16ext.getNamespace());
		assertInstanceOf(BooleanType.class, member16ext.getType());

		final ComplexDataTypeAttribute member17ext = findStructMember(structSequence13cdtExt.getAttribute(), "member17");
		assertNull(member17ext);

		final ComplexDataTypeAttribute member18ext = findStructMember(structSequence13cdtExt.getAttribute(), "member18");
		assertEquals(MODULE_NS_DEV, member18ext.getNamespace());
		assertInstanceOf(IntegerType.class, member18ext.getType());

		// -----------------------------------------------------

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "Cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);
		assertRequiresTargetType(context, actualNmtProperties, cont1ext.getRequiresTargetType());

		assertEmpty(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());
		assertEmpty(cont1ext.getPrimaryTypeAttributeRemoval());
		assertSize(1, cont1ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());
		assertEmpty(cont1ext.getPrimaryTypeAttributeExtension());

		final PrimaryTypeAttribute structSequence13 = findReplacedAttribute(cont1ext, "structsequence13");
		assertEquals(MODULE_NS, structSequence13.getNs());
		assertFalse(structSequence13.isMandatory());
		assertInstanceOf(ListType.class, structSequence13.getType());
		assertInstanceOf(ComplexRefType.class, ((CollectionType) structSequence13.getType()).getCollectionValuesType());
		assertEquals(miStructSequence13cdtExt.toImpliedUrn(), ((ComplexRefType) ((CollectionType) structSequence13.getType()).getCollectionValuesType()).getModelUrn());

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo modelInfoStruct23cdt = ComplexTypeGenerator.getModelInfoForCdtInOriginal(MODULE_NS, "List2__struct23", MODULE_XYZ_VERSION);
		final ComplexDataTypeDefinition struct23cdt = load(modelInfoStruct23cdt);
		assertSize(2, struct23cdt.getAttribute());

		final ComplexDataTypeAttribute member25 = findStructMember(struct23cdt.getAttribute(), "member25");
		assertEquals(MODULE_NS, member25.getNamespace());
		assertFalse(member25.isMandatory());
		assertInstanceOf(IntegerType.class, member25.getType());

		final ComplexDataTypeAttribute member26 = findStructMember(struct23cdt.getAttribute(), "member26");
		assertEquals(MODULE_NS, member26.getNamespace());
		assertTrue(member26.isMandatory());
		assertInstanceOf(ShortType.class, member26.getType());

		final ModelInfo list2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "List2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition list2 = load(list2modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, list2);

		assertSize(9, list2.getPrimaryTypeAttribute());	// 2 leafs + 6 structs + auto-generated key = 8
		assertEmpty(list2.getPrimaryTypeAction());

		assertNotNull(findAttribute(list2.getPrimaryTypeAttribute(), "leaf21"));
		assertNotNull(findAttribute(list2.getPrimaryTypeAttribute(), "leaf22"));

		final PrimaryTypeAttribute struct23 = findAttribute(list2.getPrimaryTypeAttribute(), "struct23");
		assertTrue(struct23.isMandatory());
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, struct23);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, struct23);
		assertInstanceOf(ComplexRefType.class, struct23.getType());
		assertEquals(modelInfoStruct23cdt.toImpliedUrn(), ((ComplexRefType) struct23.getType()).getModelUrn());

		final PrimaryTypeAttribute struct27 = findAttribute(list2.getPrimaryTypeAttribute(), "structsequence27");
		assertFalse(struct27.isMandatory());
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, struct27);
		assertWriteBehaviour(WriteBehaviorType.DELEGATE, struct27);

		final PrimaryTypeAttribute struct28 = findAttribute(list2.getPrimaryTypeAttribute(), "struct28");
		assertFalse(struct28.isMandatory());
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, struct28);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, struct28);

		final PrimaryTypeAttribute struct29 = findAttribute(list2.getPrimaryTypeAttribute(), "struct29");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, struct29);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, struct29);

		final PrimaryTypeAttribute struct30 = findAttribute(list2.getPrimaryTypeAttribute(), "struct30");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, struct30);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, struct30);

		final PrimaryTypeAttribute struct32 = findAttribute(list2.getPrimaryTypeAttribute(), "struct32");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, struct32);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, struct32);

		// -----------------------------------------------------

		final ModelInfo modelInfoStructSequence27cdt = ComplexTypeGenerator.getModelInfoForCdtInOriginal(MODULE_NS, "List2__structsequence27", MODULE_XYZ_VERSION);
		final ComplexDataTypeDefinition structSequence27cdt = load(modelInfoStructSequence27cdt);
		assertHasNotMeta(Constants.META_MULTIPLE_KEY_NAMES, structSequence27cdt);

		final ComplexDataTypeAttribute memberXxx = findStructMember(structSequence27cdt.getAttribute(), "memberXxx");
		assertTrue(memberXxx.isKey());
		final ComplexDataTypeAttribute memberYyy = findStructMember(structSequence27cdt.getAttribute(), "memberYyy");
		assertFalse(memberYyy.isKey());

		// -----------------------------------------------------

		final ModelInfo miStruct31cdt = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "List2__struct31");
		final ComplexDataTypeDefinition struct31cdt = load(miStruct31cdt);
		assertSize(2, struct31cdt.getAttribute());
		final ComplexDataTypeAttribute member32 = findStructMember(struct31cdt.getAttribute(), "member32");
		assertEquals(MODULE_NS_DEV, member32.getNamespace());
		final ComplexDataTypeAttribute member33 = findStructMember(struct31cdt.getAttribute(), "member33");
		assertEquals(MODULE_NS_DEV, member33.getNamespace());

		final ModelInfo list2ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "List2");
		final PrimaryTypeExtensionDefinition list2ext = load(list2ExtensionModelInfo);
		assertRequiresTargetType(context, actualNmtProperties, list2ext.getRequiresTargetType());
		assertEquals(list2modelInfo.toImpliedUrn(), list2ext.getExtendedModelElement().get(0).getUrn());

		assertSize(1, list2ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());
		final PrimaryTypeAttribute struct31 = findAddedAttribute(list2ext, "struct31");
		assertEquals(MODULE_NS_DEV, struct31.getNs());
		assertInstanceOf(ComplexRefType.class, struct31.getType());
		assertEquals(miStruct31cdt.toImpliedUrn(), ((ComplexRefType) struct31.getType()).getModelUrn());

		// -----------------------------------------------------

		final ModelInfo list4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "List4", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition list4 = load(list4modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, list4);

		assertSize(1, list4.getPrimaryTypeAttribute());	// 1 key only
		assertEmpty(list4.getPrimaryTypeAction());

		assertNotNull(findAttribute(list4.getPrimaryTypeAttribute(), "id"));

		final ModelInfo list4ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "List4");
		final PrimaryTypeExtensionDefinition list4ext = load(list4ExtensionModelInfo);

		assertSize(2, list4ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());
		final PrimaryTypeAttribute leaf41 = findAddedAttribute(list4ext, "leaf41");
		assertEquals(MODULE_NS, leaf41.getNs());
		final PrimaryTypeAttribute leaf42 = findAddedAttribute(list4ext, "leaf42");
		assertEquals(MODULE_NS, leaf42.getNs());

		// -----------------------------------------------------

		final ModelInfo modelInfoStruct51cdt = ComplexTypeGenerator.getModelInfoForCdtInOriginal(MODULE_NS, "List5__structsequence51", MODULE_XYZ_VERSION);
		assertModelDoesNotExist(modelInfoStruct51cdt);

		final ModelInfo miStruct51cdtExt = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "List5__structsequence51");
		final ComplexDataTypeDefinition struct51cdtExt = load(miStruct51cdtExt);
		assertSize(2, struct51cdtExt.getAttribute());
		assertHasMeta(Constants.META_MULTIPLE_KEY_NAMES, "member51 member52", struct51cdtExt);

		final ComplexDataTypeAttribute member51ext = findStructMember(struct51cdtExt.getAttribute(), "member51");
		assertEquals(MODULE_NS, member51ext.getNamespace());
		assertTrue(member51ext.isKey());
		final ComplexDataTypeAttribute member52ext = findStructMember(struct51cdtExt.getAttribute(), "member52");
		assertEquals(MODULE_NS, member52ext.getNamespace());
		assertTrue(member52ext.isKey());

		final ModelInfo list5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "List5", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition list5 = load(list5modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, list5);

		assertSize(1, list5.getPrimaryTypeAttribute());	// 1 key only
		assertEmpty(list5.getPrimaryTypeAction());

		assertNotNull(findAttribute(list5.getPrimaryTypeAttribute(), "id"));
		assertNull(findAttribute(list5.getPrimaryTypeAttribute(), "structsequence51"));

		final ModelInfo list5ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "List5");
		final PrimaryTypeExtensionDefinition list5ext = load(list5ExtensionModelInfo);

		assertSize(1, list5ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());
		final PrimaryTypeAttribute structsequence51 = findAddedAttribute(list5ext, "structsequence51");
		assertEquals(MODULE_NS, structsequence51.getNs());
		assertInstanceOf(ListType.class, structsequence51.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) structsequence51.getType()).getCollectionValuesType());
		assertEquals(miStruct51cdtExt.toImpliedUrn(), ((ComplexRefType) ((ListType) structsequence51.getType()).getCollectionValuesType()).getModelUrn());

		// -----------------------------------------------------

		final ModelInfo modelInfoStruct61cdt = ComplexTypeGenerator.getModelInfoForCdtInOriginal(MODULE_NS, "List6__struct61", MODULE_XYZ_VERSION);
		final ComplexDataTypeDefinition struct61cdt = load(modelInfoStruct61cdt);

		final ComplexDataTypeAttribute leaf61 = findStructMember(struct61cdt.getAttribute(), "leaf61");
		assertFalse(leaf61.isMandatory());

		final ComplexDataTypeAttribute leaf62 = findStructMember(struct61cdt.getAttribute(), "leaf62");
		assertFalse(leaf62.isMandatory());

		final ComplexDataTypeAttribute leaf63 = findStructMember(struct61cdt.getAttribute(), "leaf63");
		assertFalse(leaf63.isMandatory());

		final ComplexDataTypeAttribute leaf65 = findStructMember(struct61cdt.getAttribute(), "leaf65");
		assertFalse(leaf65.isMandatory());

		final ComplexDataTypeAttribute leaf66 = findStructMember(struct61cdt.getAttribute(), "leaf66");
		assertFalse(leaf66.isMandatory());

		final ModelInfo list6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "List6", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition list6 = load(list6modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, list6);

		final PrimaryTypeAttribute struct61 = findAttribute(list6.getPrimaryTypeAttribute(), "struct61");
		assertFalse(struct61.isMandatory());

		// -----------------------------------------------------

		final ModelInfo list7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "List7", MODULE_XYZ_VERSION);
		assertModelDoesNotExist(list7modelInfo);

		// -----------------------------------------------------

		final ModelInfo miRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(miRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 5);

		final PrimaryTypeContainment managedElementToCont1 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "Cont1");
		assertContainment(managedElementToCont1, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "Cont1", MODULE_XYZ_VERSION, null, 1L);

		final PrimaryTypeContainment managedElementToList2 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "List2");
		assertContainment(managedElementToList2, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "List2", MODULE_XYZ_VERSION, 2L, 4L);

		final PrimaryTypeContainment managedElementToList4 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "List4");
		assertContainment(managedElementToList4, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "List4", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment managedElementToList5 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "List5");
		assertContainment(managedElementToList5, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "List5", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment managedElementToList6 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "List6");
		assertContainment(managedElementToList6, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "List6", MODULE_XYZ_VERSION, null, null);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final ComplexDataTypeSpecification structSequence13spec = modelService.getTypedAccess().getEModelSpecification(miStructSequence13cdtExt, ComplexDataTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertSize(3, structSequence13spec.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification member15Spec = structSequence13spec.getAttributeSpecification("member15");
		assertEquals(MODULE_NS, member15Spec.getAttributeNamespace());
		assertEquals(DataType.STRING, member15Spec.getDataTypeSpecification().getDataType());
		final ComplexDataTypeAttributeSpecification member16Spec = structSequence13spec.getAttributeSpecification("member16");
		assertEquals(MODULE_NS, member16Spec.getAttributeNamespace());
		assertEquals(DataType.BOOLEAN, member16Spec.getDataTypeSpecification().getDataType());
		final ComplexDataTypeAttributeSpecification member18Spec = structSequence13spec.getAttributeSpecification("member18");
		assertEquals(MODULE_NS_DEV, member18Spec.getAttributeNamespace());
		assertEquals(DataType.INTEGER, member18Spec.getDataTypeSpecification().getDataType());

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasMetaInModelService(Constants.META_PRESENCE_CONTAINER, cont1spec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont1spec);
		assertHasMetaInModelService(Constants.META_3GPP_REAGGREGATED_IOC, cont1spec);

		assertSize(4, cont1spec.getAttributeSpecifications());		// +1 for the auto-generated key

		final PrimaryTypeAttributeSpecification idSpec = findAttribute(cont1spec, "id");
		assertEquals(DataType.STRING, idSpec.getDataTypeSpecification().getDataType());
		assertTrue(idSpec.isMandatory());
		assertTrue(idSpec.isKey());
		assertHasMetaInModelService(Constants.META_ARTIFIAL_KEY, idSpec);
		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.STRING, attr11.getDataTypeSpecification().getDataType());
		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.STRING, attr12.getDataTypeSpecification().getDataType());
		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "structsequence13");
		assertFalse(attr13.isMandatory());
		assertEquals(DataType.LIST, attr13.getDataTypeSpecification().getDataType());
		assertEquals(DataType.COMPLEX_REF, attr13.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		assertEquals(miStructSequence13cdtExt, attr13.getDataTypeSpecification().getValuesDataTypeSpecification().getReferencedDataType());

		// -----------------------------------------------------

		final ComplexDataTypeSpecification struct23spec = modelService.getTypedAccess().getEModelSpecification(modelInfoStruct23cdt, ComplexDataTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertSize(2, struct23spec.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification member25Spec = struct23spec.getAttributeSpecification("member25");
		assertEquals(MODULE_NS, member25Spec.getAttributeNamespace());
		assertFalse(member25Spec.isMandatory());
		assertEquals(DataType.INTEGER, member25Spec.getDataTypeSpecification().getDataType());
		final ComplexDataTypeAttributeSpecification member26Spec = struct23spec.getAttributeSpecification("member26");
		assertEquals(MODULE_NS, member26Spec.getAttributeNamespace());
		assertTrue(member26Spec.isMandatory());
		assertEquals(DataType.SHORT, member26Spec.getDataTypeSpecification().getDataType());

		final HierarchicalPrimaryTypeSpecification list2spec = modelService.getTypedAccess().getEModelSpecification(list2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasNotMetaInModelService(Constants.META_PRESENCE_CONTAINER, list2spec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, list2spec);
		assertHasMetaInModelService(Constants.META_3GPP_REAGGREGATED_IOC, list2spec);

		assertSize(10, list2spec.getAttributeSpecifications());		// +1 for the auto-generated key

		final PrimaryTypeAttributeSpecification list2IdSpec = findAttribute(list2spec, "id");
		assertEquals(DataType.STRING, list2IdSpec.getDataTypeSpecification().getDataType());
		assertTrue(list2IdSpec.isMandatory());
		assertTrue(list2IdSpec.isKey());
		assertHasNotMetaInModelService(Constants.META_ARTIFIAL_KEY, list2IdSpec);
		final PrimaryTypeAttributeSpecification attr21 = findAttribute(list2spec, "leaf21");
		assertEquals(DataType.STRING, attr21.getDataTypeSpecification().getDataType());
		final PrimaryTypeAttributeSpecification attr22 = findAttribute(list2spec, "leaf22");
		assertEquals(DataType.STRING, attr22.getDataTypeSpecification().getDataType());
		final PrimaryTypeAttributeSpecification attr23 = findAttribute(list2spec, "struct23");
		assertTrue(attr23.isMandatory());
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr23.getReadBehavior());
		assertEquals(WriteBehavior.PERSIST_AND_DELEGATE, attr23.getWriteBehavior());
		assertEquals(DataType.COMPLEX_REF, attr23.getDataTypeSpecification().getDataType());
		assertEquals(modelInfoStruct23cdt, attr23.getDataTypeSpecification().getReferencedDataType());
		final PrimaryTypeAttributeSpecification attr27 = findAttribute(list2spec, "structsequence27");
		assertFalse(attr27.isMandatory());
		assertEquals(ReadBehavior.FROM_DELEGATE, attr27.getReadBehavior());
		assertEquals(WriteBehavior.DELEGATE, attr27.getWriteBehavior());
		final PrimaryTypeAttributeSpecification attr28 = findAttribute(list2spec, "struct28");
		assertFalse(attr28.isMandatory());
		assertEquals(ReadBehavior.FROM_DELEGATE, attr28.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr28.getWriteBehavior());
		final PrimaryTypeAttributeSpecification attr29 = findAttribute(list2spec, "struct29");
		assertEquals(ReadBehavior.FROM_DELEGATE, attr29.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr29.getWriteBehavior());
		final PrimaryTypeAttributeSpecification attr30 = findAttribute(list2spec, "struct30");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr30.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr30.getWriteBehavior());
		final PrimaryTypeAttributeSpecification attr31 = findAttribute(list2spec, "struct31");
		assertEquals(DataType.COMPLEX_REF, attr31.getDataTypeSpecification().getDataType());
		assertEquals(miStruct31cdt, attr31.getDataTypeSpecification().getReferencedDataType());
		final PrimaryTypeAttributeSpecification attr32 = findAttribute(list2spec, "struct32");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attr32.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attr32.getWriteBehavior());

		// -----------------------------------------------------

		final HierarchicalPrimaryTypeSpecification list4spec = modelService.getTypedAccess().getEModelSpecification(list4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasNotMetaInModelService(Constants.META_PRESENCE_CONTAINER, list4spec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, list4spec);
		assertHasMetaInModelService(Constants.META_3GPP_REAGGREGATED_IOC, list4spec);

		assertSize(3, list4spec.getAttributeSpecifications());

		assertNotNull(findAttribute(list4spec, "id"));
		assertNotNull(findAttribute(list4spec, "leaf41"));
		assertNotNull(findAttribute(list4spec, "leaf42"));

		// -----------------------------------------------------

		final ComplexDataTypeSpecification struct51spec = modelService.getTypedAccess().getEModelSpecification(miStruct51cdtExt, ComplexDataTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertSize(2, struct51spec.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification member51Spec = struct51spec.getAttributeSpecification("member51");
		assertEquals(MODULE_NS, member51Spec.getAttributeNamespace());
		final ComplexDataTypeAttributeSpecification member52Spec = struct51spec.getAttributeSpecification("member52");
		assertEquals(MODULE_NS, member52Spec.getAttributeNamespace());

		final HierarchicalPrimaryTypeSpecification list5spec = modelService.getTypedAccess().getEModelSpecification(list5modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasNotMetaInModelService(Constants.META_PRESENCE_CONTAINER, list5spec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, list5spec);
		assertHasMetaInModelService(Constants.META_3GPP_REAGGREGATED_IOC, list5spec);

		assertSize(2, list5spec.getAttributeSpecifications());

		final PrimaryTypeAttributeSpecification list5IdSpec = findAttribute(list5spec, "id");
		assertEquals(DataType.STRING, list5IdSpec.getDataTypeSpecification().getDataType());
		assertTrue(list5IdSpec.isMandatory());
		assertTrue(list5IdSpec.isKey());
		assertHasNotMetaInModelService(Constants.META_ARTIFIAL_KEY, list5IdSpec);
		final PrimaryTypeAttributeSpecification attr51 = findAttribute(list5spec, "structsequence51");
		assertEquals(DataType.LIST, attr51.getDataTypeSpecification().getDataType());
		assertEquals(DataType.COMPLEX_REF, attr51.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		assertEquals(miStruct51cdtExt, attr51.getDataTypeSpecification().getValuesDataTypeSpecification().getReferencedDataType());

		// -----------------------------------------------------

		assertSingleContainmentParent(cont1spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, 1L);
		assertSingleContainmentParent(list2spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, 2L, 4L);
	}
}
