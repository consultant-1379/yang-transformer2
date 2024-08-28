package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.actionsandrpcs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeActionParameterSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeActionSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAction;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeActionParameter;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.LifeCycleState;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.cdt.ComplexDataTypeAttributeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.cdt.ComplexDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.CollectionSizeConstraint;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.MinMaxValue;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.MinValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeAttribute;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ComplexRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LifeCycleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.ComplexTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;

public class ActionsTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-actions-and-rpcs/";

	private static final String MODULE_NS_SIM = "urn:::test:::simple-actions";
	private static final String MODULE_NAME_SIM = "simple-actions";
	private static final String MODULE_REVISION_SIM = "2021-11-11";
	private static final String MODULE_XYZ_VERSION_SIM = "2021.11.11";

	@Test
	public void test___simple_actions() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N662_COMPLEX_OUTPUT_NOT_SUPPORTED.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N671_UNSUPPORTED_EXTENSION.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "actions/simple-actions.yang")));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(3, 3, 2, 0, 5, 5);

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_SIM, MODULE_NAME_SIM, MODULE_REVISION_SIM);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_SIM, "cont1", MODULE_XYZ_VERSION_SIM);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertEmpty(cont1.getPrimaryTypeAction());		// should all sit in the extension!

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_SIM, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);
		assertRequiresTargetType(context, null, cont1ext.getRequiresTargetType());
		assertSize(5, cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction());

		final PrimaryTypeAction action11 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action11");
		assertHasNotMeta(Constants.META_RPC, action11);
		assertEmpty(action11.getParameter());
		assertNull(action11.getReturnType());

		final PrimaryTypeAction action12 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action12");
		assertSize(1, action12.getParameter());
		final PrimaryTypeActionParameter leaf121 = findActionParameter(action12.getParameter(), "leaf121");
		assertInstanceOf(StringType.class, leaf121.getType());
		assertNull(action12.getReturnType());

		final PrimaryTypeAction action13 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action13");
		assertSize(2, action13.getParameter());
		final PrimaryTypeActionParameter leaf131 = findActionParameter(action13.getParameter(), "leaf131");
		assertInstanceOf(StringType.class, leaf131.getType());
		final PrimaryTypeActionParameter leaf132 = findActionParameter(action13.getParameter(), "leaf132");
		assertInstanceOf(IntegerType.class, leaf132.getType());
		assertNull(action13.getReturnType());

		final ModelInfo miLeaf141enum = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_SIM, "cont1__action14__out__leaf141", null);
		final EnumDataTypeDefinition leaf141enum = load(miLeaf141enum);
		assertEquals("GREEN", leaf141enum.getMember().get(0).getName());
		assertEquals("RED", leaf141enum.getMember().get(1).getName());
		assertEquals("YELLOW", leaf141enum.getMember().get(2).getName());

		final PrimaryTypeAction action14 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action14");
		assertEmpty(action14.getParameter());
		assertHasNotMeta(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, action14);
		assertInstanceOf(EnumRefType.class, action14.getReturnType());
		assertEquals(miLeaf141enum.toImpliedUrn(), ((EnumRefType) action14.getReturnType()).getModelUrn());

		final ModelInfo miStruct152cdt = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_SIM, "cont1__action15__in__struct152");
		final ComplexDataTypeDefinition struct152cdt = load(miStruct152cdt);
		assertSize(2, struct152cdt.getAttribute());
		final ComplexDataTypeAttribute leaf1521 = findStructMember(struct152cdt.getAttribute(), "leaf1521");
		assertInstanceOf(StringType.class, leaf1521.getType());
		final ComplexDataTypeAttribute leaf1522 = findStructMember(struct152cdt.getAttribute(), "leaf1522");
		assertInstanceOf(BooleanType.class, leaf1522.getType());

		final PrimaryTypeAction action15 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action15");
		assertSize(2, action15.getParameter());
		final PrimaryTypeActionParameter leaf151 = findActionParameter(action15.getParameter(), "leaf151");
		assertInstanceOf(StringType.class, leaf151.getType());
		final PrimaryTypeActionParameter struct152 = findActionParameter(action15.getParameter(), "struct152");
		assertInstanceOf(ComplexRefType.class, struct152.getType());
		assertEquals(miStruct152cdt.toImpliedUrn(), ((ComplexRefType) struct152.getType()).getModelUrn());
		assertNull(action15.getReturnType());

		// -----------------------------------------------------

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_SIM, "cont2", MODULE_XYZ_VERSION_SIM);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);
		assertEmpty(cont2.getPrimaryTypeAction());

		final ModelInfo cont2ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_SIM, "cont2");
		final PrimaryTypeExtensionDefinition cont2ext = load(cont2ExtensionModelInfo);
		assertRequiresTargetType(context, null, cont2ext.getRequiresTargetType());
		assertSize(4, cont2ext.getPrimaryTypeExtension().getPrimaryTypeAction());

		final ModelInfo miOutStructAction21 = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_SIM, ComplexTypeGenerator.getPathToActionOrRpcOutput("cont2", "action21"));
		final ComplexDataTypeDefinition outStructAction21 = load(miOutStructAction21);
		assertSize(2, outStructAction21.getAttribute());
		final ComplexDataTypeAttribute leaf211 = findStructMember(outStructAction21.getAttribute(), "leaf211");
		assertInstanceOf(StringType.class, leaf211.getType());
		final ComplexDataTypeAttribute leaf212 = findStructMember(outStructAction21.getAttribute(), "leaf212");
		assertInstanceOf(IntegerType.class, leaf212.getType());

		final PrimaryTypeAction action21 = findAction(cont2ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action21");
		assertEmpty(action21.getParameter());
		assertHasMeta(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, action21);
		assertInstanceOf(ComplexRefType.class, action21.getReturnType());
		assertEquals(miOutStructAction21.toImpliedUrn(), ((ComplexRefType) action21.getReturnType()).getModelUrn());

		final ModelInfo miOutStructAction22 = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_SIM, ComplexTypeGenerator.getPathToActionOrRpcOutput("cont2", "action22"));
		final ComplexDataTypeDefinition outStructAction22 = load(miOutStructAction22);
		assertSize(2, outStructAction22.getAttribute());
		final ComplexDataTypeAttribute leaf221 = findStructMember(outStructAction22.getAttribute(), "leaf221");
		assertInstanceOf(BooleanType.class, leaf221.getType());
		final ComplexDataTypeAttribute leaf222 = findStructMember(outStructAction22.getAttribute(), "leaf222");
		assertInstanceOf(LongType.class, leaf222.getType());

		final PrimaryTypeAction action22 = findAction(cont2ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action22");
		assertEmpty(action22.getParameter());
		assertHasMeta(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, action22);
		assertInstanceOf(ComplexRefType.class, action22.getReturnType());
		assertEquals(miOutStructAction22.toImpliedUrn(), ((ComplexRefType) action22.getReturnType()).getModelUrn());

		final ModelInfo miStruct231 = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_SIM, "cont2__action23__out__struct231");
		final ComplexDataTypeDefinition struct231 = load(miStruct231);
		assertSize(1, struct231.getAttribute());
		final ComplexDataTypeAttribute leaf2311 = findStructMember(struct231.getAttribute(), "leaf2311");
		assertInstanceOf(StringType.class, leaf2311.getType());

		final PrimaryTypeAction action23 = findAction(cont2ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action23");
		assertEmpty(action23.getParameter());
		assertHasNotMeta(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, action23);
		assertInstanceOf(ComplexRefType.class, action23.getReturnType());
		assertEquals(miStruct231.toImpliedUrn(), ((ComplexRefType) action23.getReturnType()).getModelUrn());

		final PrimaryTypeAction action24 = findAction(cont2ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action24");
		assertEmpty(action24.getParameter());
		assertHasNotMeta(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, action24);
		assertNull(action24.getReturnType());

		// -----------------------------------------------------

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_SIM, "cont3", MODULE_XYZ_VERSION_SIM);
		assertModelExists(cont3modelInfo);

		final ModelInfo cont3ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_SIM, "cont3");
		final PrimaryTypeExtensionDefinition cont3ext = load(cont3ExtensionModelInfo);

		final PrimaryTypeAction action31 = findAction(cont3ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action31");
		assertEmpty(action31.getParameter());
		assertNull(action31.getReturnType());
		assertEquals("action31 desc", action31.getDesc());
		assertLifecycle(LifeCycleType.DEPRECATED, action31);
		assertEquals("action31 status information", action31.getLifeCycleDesc());
		assertEquals("action31 dependencies", action31.getDependencies());
		assertEquals("action31 precondition", action31.getPreCondition());
		assertEquals("action31 side-effects", action31.getSideEffects());
		assertEquals("action31 takes-effect", action31.getTakesEffect());
		assertEquals("action31 disturbances", action31.getDisturbances());

		final PrimaryTypeAction action32 = findAction(cont3ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action32");
		assertSize(3, action32.getParameter());

		final PrimaryTypeActionParameter leaf321 = findActionParameter(action32.getParameter(), "leaf321");
		assertInstanceOf(StringType.class, leaf321.getType());
		assertTrue(leaf321.isMandatory());
		assertNull(leaf321.getDefault());

		final PrimaryTypeActionParameter leaf322 = findActionParameter(action32.getParameter(), "leaf322");
		assertInstanceOf(StringType.class, leaf322.getType());
		assertFalse(leaf322.isMandatory());
		assertInstanceOf(StringValue.class, leaf322.getDefault());
		assertEquals("abc", ((StringValue) leaf322.getDefault()).getValue());

		final ModelInfo miLeaf3232enum = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_SIM, "cont3__action32__in__structsequence323__leaf3232", null);
		final EnumDataTypeDefinition leaf3232enum = load(miLeaf3232enum);
		assertEquals("RED", leaf3232enum.getMember().get(0).getName());
		assertEquals("YELLOW", leaf3232enum.getMember().get(1).getName());

		final ModelInfo miStructsequence323Cdt = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_SIM, "cont3__action32__in__structsequence323");
		final ComplexDataTypeDefinition structsequence323Cdt = load(miStructsequence323Cdt);
		assertSize(2, structsequence323Cdt.getAttribute());
		final ComplexDataTypeAttribute leaf3231 = findStructMember(structsequence323Cdt.getAttribute(), "leaf3231");
		assertInstanceOf(ShortType.class, leaf3231.getType());
		assertTrue(leaf3231.isKey());
		final ComplexDataTypeAttribute leaf3232 = findStructMember(structsequence323Cdt.getAttribute(), "leaf3232");
		assertInstanceOf(EnumRefType.class, leaf3232.getType());
		assertEquals(miLeaf3232enum.toImpliedUrn(), ((EnumRefType) leaf3232.getType()).getModelUrn());

		final PrimaryTypeActionParameter structsequence323 = findActionParameter(action32.getParameter(), "structsequence323");
		assertInstanceOf(ListType.class, structsequence323.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) structsequence323.getType()).getCollectionValuesType());
		assertEquals(2L, ((ListType) structsequence323.getType()).getCollectionSizeConstraint().getMinSize().getValue());
		assertEquals(miStructsequence323Cdt.toImpliedUrn(), ((ComplexRefType) ((ListType) structsequence323.getType()).getCollectionValuesType()).getModelUrn());
		assertTrue(structsequence323.isMandatory());

		final PrimaryTypeAction action33 = findAction(cont3ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action33");
		assertSize(7, action33.getParameter());

		final PrimaryTypeActionParameter leaf331 = findActionParameter(action33.getParameter(), "leaf331");
		assertTrue(leaf331.isMandatory());
		final PrimaryTypeActionParameter leaf332 = findActionParameter(action33.getParameter(), "leaf332");
		assertFalse(leaf332.isMandatory());
		final PrimaryTypeActionParameter leaf333 = findActionParameter(action33.getParameter(), "leaf333");
		assertFalse(leaf333.isMandatory());

		final PrimaryTypeActionParameter leaf3311 = findActionParameter(action33.getParameter(), "leaf3311");
		assertFalse(leaf3311.isMandatory());
		final PrimaryTypeActionParameter leaf3312 = findActionParameter(action33.getParameter(), "leaf3312");
		assertFalse(leaf3312.isMandatory());
		final PrimaryTypeActionParameter leaf3321 = findActionParameter(action33.getParameter(), "leaf3321");
		assertFalse(leaf3321.isMandatory());
		final PrimaryTypeActionParameter leaf3322 = findActionParameter(action33.getParameter(), "leaf3322");
		assertFalse(leaf3322.isMandatory());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeActionSpecification action11Spec = cont1spec.getActionSpecification("action11");
		assertEmpty(action11Spec.getParameters());
		assertNull(action11Spec.getReturnTypeSpecification());

		final PrimaryTypeActionSpecification action12Spec = cont1spec.getActionSpecification("action12");
		final PrimaryTypeActionParameterSpecification attr121 = findActionParameterSpec(action12Spec.getParameters(), "leaf121");
		assertEquals(DataType.STRING, attr121.getDataTypeSpecification().getDataType());
		assertNull(action12Spec.getReturnTypeSpecification());

		final PrimaryTypeActionSpecification action13Spec = cont1spec.getActionSpecification("action13");
		final PrimaryTypeActionParameterSpecification attr131 = findActionParameterSpec(action13Spec.getParameters(), "leaf131");
		assertEquals(DataType.STRING, attr131.getDataTypeSpecification().getDataType());
		final PrimaryTypeActionParameterSpecification attr132 = findActionParameterSpec(action13Spec.getParameters(), "leaf132");
		assertEquals(DataType.INTEGER, attr132.getDataTypeSpecification().getDataType());
		assertNull(action13Spec.getReturnTypeSpecification());

		final PrimaryTypeActionSpecification action14Spec = cont1spec.getActionSpecification("action14");
		assertHasNotMetaInModelService(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, action14Spec);
		assertEmpty(action14Spec.getParameters());
		assertEquals(DataType.ENUM_REF, action14Spec.getReturnTypeSpecification().getDataType());
		assertEquals(miLeaf141enum, action14Spec.getReturnTypeSpecification().getReferencedDataType());

		final PrimaryTypeActionSpecification action15Spec = cont1spec.getActionSpecification("action15");
		final PrimaryTypeActionParameterSpecification attr151 = findActionParameterSpec(action15Spec.getParameters(), "leaf151");
		assertEquals(DataType.STRING, attr151.getDataTypeSpecification().getDataType());
		final PrimaryTypeActionParameterSpecification attr152 = findActionParameterSpec(action15Spec.getParameters(), "struct152");
		assertEquals(DataType.COMPLEX_REF, attr152.getDataTypeSpecification().getDataType());
		assertEquals(miStruct152cdt, attr152.getDataTypeSpecification().getReferencedDataType());

		final ComplexDataTypeSpecification struct152spec = modelService.getTypedAccess().getEModelSpecification(miStruct152cdt, ComplexDataTypeSpecification.class, getModelServiceTarget(context, null));
		final ComplexDataTypeAttributeSpecification attr1521 = struct152spec.getAttributeSpecification("leaf1521");
		assertEquals(DataType.STRING, attr1521.getDataTypeSpecification().getDataType());
		final ComplexDataTypeAttributeSpecification attr1522 = struct152spec.getAttributeSpecification("leaf1522");
		assertEquals(DataType.BOOLEAN, attr1522.getDataTypeSpecification().getDataType());

		// -----------------------------------------------------

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeActionSpecification action21Spec = cont2spec.getActionSpecification("action21");
		assertHasMetaInModelService(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, action21Spec);
		assertEmpty(action21Spec.getParameters());
		assertEquals(DataType.COMPLEX_REF, action21Spec.getReturnTypeSpecification().getDataType());
		assertEquals(miOutStructAction21, action21Spec.getReturnTypeSpecification().getReferencedDataType());

		final ComplexDataTypeSpecification structAction21 = modelService.getTypedAccess().getEModelSpecification(miOutStructAction21, ComplexDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(2, structAction21.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification attr211 = structAction21.getAttributeSpecification("leaf211");
		assertEquals(DataType.STRING, attr211.getDataTypeSpecification().getDataType());
		final ComplexDataTypeAttributeSpecification attr212 = structAction21.getAttributeSpecification("leaf212");
		assertEquals(DataType.INTEGER, attr212.getDataTypeSpecification().getDataType());

		final PrimaryTypeActionSpecification action22Spec = cont2spec.getActionSpecification("action22");
		assertHasMetaInModelService(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, action22Spec);
		assertEmpty(action22Spec.getParameters());
		assertEquals(DataType.COMPLEX_REF, action22Spec.getReturnTypeSpecification().getDataType());
		assertEquals(miOutStructAction22, action22Spec.getReturnTypeSpecification().getReferencedDataType());

		final ComplexDataTypeSpecification structAction22 = modelService.getTypedAccess().getEModelSpecification(miOutStructAction22, ComplexDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(2, structAction22.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification attr221 = structAction22.getAttributeSpecification("leaf221");
		assertEquals(DataType.BOOLEAN, attr221.getDataTypeSpecification().getDataType());
		final ComplexDataTypeAttributeSpecification attr222 = structAction22.getAttributeSpecification("leaf222");
		assertEquals(DataType.LONG, attr222.getDataTypeSpecification().getDataType());

		final PrimaryTypeActionSpecification action23Spec = cont2spec.getActionSpecification("action23");
		assertHasNotMetaInModelService(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, action23Spec);
		assertEmpty(action23Spec.getParameters());
		assertEquals(DataType.COMPLEX_REF, action23Spec.getReturnTypeSpecification().getDataType());
		assertEquals(miStruct231, action23Spec.getReturnTypeSpecification().getReferencedDataType());

		final ComplexDataTypeSpecification structAction23 = modelService.getTypedAccess().getEModelSpecification(miStruct231, ComplexDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(1, structAction23.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification attr2311 = structAction23.getAttributeSpecification("leaf2311");
		assertEquals(DataType.STRING, attr2311.getDataTypeSpecification().getDataType());

		final PrimaryTypeActionSpecification action24Spec = cont2spec.getActionSpecification("action24");
		assertEmpty(action24Spec.getParameters());
		assertNull(action24Spec.getReturnTypeSpecification());

		// -----------------------------------------------------

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeActionSpecification action31Spec = cont3spec.getActionSpecification("action31");
		assertEmpty(action31Spec.getParameters());
		assertNull(action31Spec.getReturnTypeSpecification());
		assertEquals("action31 desc", action31Spec.getDescription());
		assertEquals(LifeCycleState.DEPRECATED, action31Spec.getLifeCycle().getState());
		assertEquals("action31 status information", action31Spec.getLifeCycle().getDescription());
		assertEquals("action31 dependencies", action31Spec.getDependencies());
		assertEquals("action31 precondition", action31Spec.getPrecondition());
		assertEquals("action31 side-effects", action31Spec.getSideEffects());
		assertEquals("action31 takes-effect", action31Spec.getTakesEffect());
		assertEquals("action31 disturbances", action31Spec.getDisturbances());

		final PrimaryTypeActionSpecification action32Spec = cont3spec.getActionSpecification("action32");

		final PrimaryTypeActionParameterSpecification attr321 = findActionParameterSpec(action32Spec.getParameters(), "leaf321");
		assertEquals(DataType.STRING, attr321.getDataTypeSpecification().getDataType());
		assertTrue(attr321.isMandatory());
		assertNull(attr321.getDefaultValue());

		final PrimaryTypeActionParameterSpecification attr322 = findActionParameterSpec(action32Spec.getParameters(), "leaf322");
		assertEquals(DataType.STRING, attr322.getDataTypeSpecification().getDataType());
		assertFalse(attr322.isMandatory());
		assertEquals("abc", ((String) attr322.getDefaultValue()));

		final PrimaryTypeActionParameterSpecification attr323 = findActionParameterSpec(action32Spec.getParameters(), "structsequence323");
		assertEquals(DataType.LIST, attr323.getDataTypeSpecification().getDataType());
		assertEquals(DataType.COMPLEX_REF, attr323.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		assertEquals(miStructsequence323Cdt, attr323.getDataTypeSpecification().getValuesDataTypeSpecification().getReferencedDataType());

		final CollectionSizeConstraint sizeConstraint = findConstraint(attr323.getDataTypeSpecification().getConstraints(), CollectionSizeConstraint.class);
		final MinMaxValue minMaxValue = sizeConstraint.getAllowedSize().iterator().next();
		assertEquals(2L, ((MinValue) minMaxValue).getMinValueConstraint());
		assertTrue(attr323.isMandatory());		// because of min-elements
	}

	private static final String MODULE_NS_CPX = "urn+test+complex-actions";
	private static final String MODULE_NAME_CPX = "complex-actions";
	private static final String MODULE_REVISION_CPX = "2021-11-11";
	private static final String MODULE_XYZ_VERSION_CPX = "2021.11.11";

	private static final String MODULE_NS_CPX_DEV = "urn+test+complex-actions-dev";
	private static final String MODULE_NAME_CPX_DEV = "complex-actions-dev";
	private static final String MODULE_REVISION_CPX_DEV = "2021-11-31";

	@Test
	public void test___complex_actions() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N662_COMPLEX_OUTPUT_NOT_SUPPORTED.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N671_UNSUPPORTED_EXTENSION.toString());

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR + "actions/complex-actions.yang"), new File(TEST_SUB_DIR + "actions/complex-actions-dev.yang")));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_CPX, MODULE_NAME_CPX, MODULE_REVISION_CPX);
		assertModelExists(modelInfoForYangModule);

		final ModelInfo modelInfoForYangModuleDev = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_CPX_DEV, MODULE_NAME_CPX_DEV, MODULE_REVISION_CPX_DEV);
		assertModelExists(modelInfoForYangModuleDev);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_CPX, "cont1", MODULE_XYZ_VERSION_CPX);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertEmpty(cont1.getPrimaryTypeAction());		// should all sit in the extension!

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_CPX, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);
		assertRequiresTargetType(context, null, cont1ext.getRequiresTargetType());
		assertSize(3, cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction());

		final PrimaryTypeAction action11 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action11");
		assertEquals(MODULE_NS_CPX, action11.getNs());
		assertSize(1, action11.getParameter());
		final PrimaryTypeActionParameter param111 = findActionParameter(action11.getParameter(), "param111");
		//		assertTrue(leaf111.getNs().equals(MODULE_NS_CPX_DEV));		// TODO: [ACTION_PARAM_NAMESPACE]
		assertInstanceOf(BooleanType.class, param111.getType());

		final PrimaryTypeAction action12 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action12");
		assertEquals(MODULE_NS_CPX, action12.getNs());
		assertEmpty(action12.getParameter());								// deviated out
		assertInstanceOf(StringType.class, action12.getReturnType());		// augmented-in

		final ModelInfo miStruct181cdt = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_CPX, "cont1__action18__in__param181");
		final ComplexDataTypeDefinition struct181cdt = load(miStruct181cdt);
		assertSize(1, struct181cdt.getAttribute());				// container 'struct1812' not generated, so only 1 member
		final ComplexDataTypeAttribute leaf1811 = findStructMember(struct181cdt.getAttribute(), "leaf1811");
		assertEquals(MODULE_NS_CPX_DEV, leaf1811.getNamespace());
		assertInstanceOf(StringType.class, leaf1811.getType());

		final ModelInfo miAction18autogeneratedOutput = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_CPX, "cont1__action18__out");
		final ComplexDataTypeDefinition action18output = load(miAction18autogeneratedOutput);
		assertSize(2, action18output.getAttribute());
		final ComplexDataTypeAttribute leaf185 = findStructMember(action18output.getAttribute(), "leaf185");
		assertEquals(MODULE_NS_CPX_DEV, leaf185.getNamespace());
		assertInstanceOf(StringType.class, leaf185.getType());
		final ComplexDataTypeAttribute leaf186 = findStructMember(action18output.getAttribute(), "leaf186");
		assertEquals(MODULE_NS_CPX_DEV, leaf186.getNamespace());
		assertInstanceOf(StringType.class, leaf186.getType());

		final PrimaryTypeAction action18 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action18");
		assertEquals(MODULE_NS_CPX_DEV, action18.getNs());		// augmented-in
		assertSize(1, action18.getParameter());

		final PrimaryTypeActionParameter param181 = findActionParameter(action18.getParameter(), "param181");
		//		assertTrue(struct181.getNs().equals(MODULE_NS_CPX_DEV));		// TODO: [ACTION_PARAM_NAMESPACE]
		assertInstanceOf(ComplexRefType.class, param181.getType());
		assertEquals(miStruct181cdt.toImpliedUrn(), ((ComplexRefType) param181.getType()).getModelUrn());
		assertInstanceOf(ComplexRefType.class, action18.getReturnType());
		assertEquals(miAction18autogeneratedOutput.toImpliedUrn(), ((ComplexRefType) action18.getReturnType()).getModelUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeActionSpecification action11Spec = cont1spec.getActionSpecification("action11");
		assertNull(action11Spec.getReturnTypeSpecification());

		final PrimaryTypeActionParameterSpecification param111spec = findActionParameterSpec(action11Spec.getParameters(), "param111");
		//		assertTrue(param111.getNs().equals(MODULE_NS_CPX_DEV));		// TODO: [ACTION_PARAM_NAMESPACE]
		assertEquals(DataType.BOOLEAN, param111spec.getDataTypeSpecification().getDataType());

		final PrimaryTypeActionSpecification action12Spec = cont1spec.getActionSpecification("action12");
		assertEmpty(action12Spec.getParameters());
		assertEquals(DataType.STRING, action12Spec.getReturnTypeSpecification().getDataType());

		final PrimaryTypeActionSpecification action18Spec = cont1spec.getActionSpecification("action18");
		assertEquals(MODULE_NS_CPX_DEV, action18Spec.getNamespace());
		assertEquals(DataType.COMPLEX_REF, action18Spec.getReturnTypeSpecification().getDataType());
		assertEquals(miAction18autogeneratedOutput, action18Spec.getReturnTypeSpecification().getReferencedDataType());

		final PrimaryTypeActionParameterSpecification param181spec = findActionParameterSpec(action18Spec.getParameters(), "param181");
		//		assertTrue(param181.getNs().equals(MODULE_NS_CPX_DEV));		// TODO: [ACTION_PARAM_NAMESPACE]
		assertEquals(DataType.COMPLEX_REF, param181spec.getDataTypeSpecification().getDataType());
		assertEquals(miStruct181cdt, param181spec.getDataTypeSpecification().getReferencedDataType());

		final ComplexDataTypeSpecification struct181spec = modelService.getTypedAccess().getEModelSpecification(miStruct181cdt, ComplexDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(1, struct181spec.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification attr1811 = struct181spec.getAttributeSpecification("leaf1811");
		assertEquals(MODULE_NS_CPX_DEV, attr1811.getAttributeNamespace());
		assertEquals(DataType.STRING, attr1811.getDataTypeSpecification().getDataType());

		final ComplexDataTypeSpecification action18outputSpec = modelService.getTypedAccess().getEModelSpecification(miAction18autogeneratedOutput, ComplexDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(2, action18outputSpec.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification attr185 = action18outputSpec.getAttributeSpecification("leaf185");
		assertEquals(MODULE_NS_CPX_DEV, attr185.getAttributeNamespace());
		assertEquals(DataType.STRING, attr185.getDataTypeSpecification().getDataType());
		final ComplexDataTypeAttributeSpecification attr186 = action18outputSpec.getAttributeSpecification("leaf186");
		assertEquals(MODULE_NS_CPX_DEV, attr186.getAttributeNamespace());
		assertEquals(DataType.STRING, attr186.getDataTypeSpecification().getDataType());
	}
}
