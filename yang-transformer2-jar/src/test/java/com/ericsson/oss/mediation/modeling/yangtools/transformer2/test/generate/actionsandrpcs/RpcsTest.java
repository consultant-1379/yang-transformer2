package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.actionsandrpcs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeActionParameterSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeActionSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAction;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeActionParameter;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.cdt.ComplexDataTypeAttributeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.cdt.ComplexDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeAttribute;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ComplexRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.ComplexTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;

public class RpcsTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-actions-and-rpcs/";

	private static final String MODULE_NS_ONE = "urn=test=rpcs-one";
	private static final String MODULE_NAME_ONE = "rpcs-one";
	private static final String MODULE_REVISION_ONE = "2021-11-04";

	private static final String MODULE_NS_TWO = "urn!test!rpcs-two";
	private static final String MODULE_NAME_TWO = "rpcs-two";
	private static final String MODULE_REVISION_TWO = "2019-03-12";


	@Test
	public void test___rpc_generation_turned_off() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "rpcs"));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		context.setGenerateRpcs(false);
		YangTransformer2.transform(context);

		internal___rpc_generation_turned_off(context, null);
	}

	@Test
	public void test___rpc_generation_turned_off_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_RPCS, "false");
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXPLICIT_CONTAINMENT_PARENT_IMPLIED_URN, OSS_TOP_ME_CONTEXT_300.toImpliedUrn());

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "rpcs"), overwritingProperties);

		internal___rpc_generation_turned_off(null, actualNmtProperties);
	}	

	private void internal___rpc_generation_turned_off(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo modelInfoForYangModuleOne = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE);
		assertModelExists(modelInfoForYangModuleOne);

		final ModelInfo modelInfoForYangModuleTwo = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO);
		assertModelExists(modelInfoForYangModuleTwo);

		// -----------------------------------------------------

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		assertModelDoesNotExist(containmentParentModelInfo);
	}

	@Test
	public void test___rpc_both_namespaces_suppressed() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "rpcs"));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		context.setGenerateRpcs(true);
		context.setExcludedNamespacesForRpcs(Arrays.asList(MODULE_NS_ONE, MODULE_NS_TWO));
		YangTransformer2.transform(context);

		internal___rpc_both_namespaces_suppressed(context, null);
	}

	@Test
	public void test___rpc_both_namespaces_suppressed___wildcarded_on_urn() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "rpcs"));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		context.setGenerateRpcs(true);
		context.setExcludedNamespacesForRpcs(Arrays.asList("urn.*"));
		YangTransformer2.transform(context);

		internal___rpc_both_namespaces_suppressed(context, null);
	}

	@Test
	public void test___rpc_both_namespaces_suppressed___wildcarded_on_test() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "rpcs"));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		context.setGenerateRpcs(true);
		context.setExcludedNamespacesForRpcs(Arrays.asList(".*test.*"));
		YangTransformer2.transform(context);

		internal___rpc_both_namespaces_suppressed(context, null);
	}

	@Test
	public void test___rpc_both_namespaces_suppressed_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_RPCS, "true");
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXPLICIT_CONTAINMENT_PARENT_IMPLIED_URN, OSS_TOP_ME_CONTEXT_300.toImpliedUrn());
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXCLUDED_RPC_NAMESPACES, MODULE_NS_ONE + " " + MODULE_NS_TWO);

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "rpcs"), overwritingProperties);

		internal___rpc_both_namespaces_suppressed(null, actualNmtProperties);
	}

	private void internal___rpc_both_namespaces_suppressed(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		assertModelDoesNotExist(containmentParentModelInfo);
	}

	@Test
	public void test___rpc_both_generated() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "rpcs"));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		context.setGenerateRpcs(true);
		YangTransformer2.transform(context);

		internal___rpc_both_generated(context, null);
	}

	@Test
	public void test___rpc_both_generated_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_RPCS, "true");
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXPLICIT_CONTAINMENT_PARENT_IMPLIED_URN, OSS_TOP_ME_CONTEXT_300.toImpliedUrn());

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "rpcs"), overwritingProperties);

		internal___rpc_both_generated(null, actualNmtProperties);
	}	

	private void internal___rpc_both_generated(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);
		assertRequiresTargetType(context, actualNmtProperties, containmentParentExt.getRequiresTargetType());
		assertNotNull(containmentParentExt.getPrimaryTypeExtension());
		assertSize(6, containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction());

		final PrimaryTypeAction rpc1 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc1");
		assertHasMeta(Constants.META_RPC, rpc1);
		assertEquals(MODULE_NS_ONE, rpc1.getNs());
		assertEmpty(rpc1.getParameter());
		assertNull(rpc1.getReturnType());

		final PrimaryTypeAction rpc2 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc2");
		assertHasMeta(Constants.META_RPC, rpc2);
		assertEquals(MODULE_NS_ONE, rpc2.getNs());
		assertSize(1, rpc2.getParameter());
		final PrimaryTypeActionParameter leaf21 = findActionParameter(rpc2.getParameter(), "leaf21");
		//		assertTrue(leaf21.getNs().equals(MODULE_NS_ONE));		// TODO: [ACTION_PARAM_NAMESPACE]
		assertInstanceOf(StringType.class, leaf21.getType());
		assertNull(rpc2.getReturnType());

		final ModelInfo miOutStructRpc3 = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), ComplexTypeGenerator.getPathToActionOrRpcOutput(OSS_TOP_ME_CONTEXT_300.getName(), "rpc3"));
		final ComplexDataTypeDefinition outStructRpc3 = load(miOutStructRpc3);
		assertSize(2, outStructRpc3.getAttribute());
		final ComplexDataTypeAttribute leaf31 = findStructMember(outStructRpc3.getAttribute(), "leaf31");
		assertEquals(MODULE_NS_ONE, leaf31.getNamespace());
		assertInstanceOf(StringType.class, leaf31.getType());
		final ComplexDataTypeAttribute leaf32 = findStructMember(outStructRpc3.getAttribute(), "leaf32");
		assertEquals(MODULE_NS_ONE, leaf32.getNamespace());
		assertInstanceOf(IntegerType.class, leaf32.getType());

		final PrimaryTypeAction rpc3 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc3");
		assertHasMeta(Constants.META_RPC, rpc3);
		assertEquals(MODULE_NS_ONE, rpc3.getNs());
		assertEmpty(rpc3.getParameter());
		assertHasMeta(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, rpc3);
		assertInstanceOf(ComplexRefType.class, rpc3.getReturnType());
		assertEquals(miOutStructRpc3.toImpliedUrn(), ((ComplexRefType) rpc3.getReturnType()).getModelUrn());

		final PrimaryTypeAction rpc6 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc6");
		assertHasMeta(Constants.META_RPC, rpc6);
		assertEquals(MODULE_NS_TWO, rpc6.getNs());
		assertSize(1, rpc6.getParameter());
		final PrimaryTypeActionParameter leaf61 = findActionParameter(rpc6.getParameter(), "leaf61");
		assertInstanceOf(BooleanType.class, leaf61.getType());
		assertNull(rpc6.getReturnType());

		final PrimaryTypeAction rpc7 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc7");
		assertHasMeta(Constants.META_RPC, rpc7);
		assertEquals(MODULE_NS_TWO, rpc7.getNs());
		assertEmpty(rpc7.getParameter());
		assertNull(rpc7.getReturnType());

		final ModelInfo miStruct81 = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), "MeContext__rpc8__out__struct81");
		final ComplexDataTypeDefinition struct81 = load(miStruct81);

		assertSize(2, struct81.getAttribute());
		final ComplexDataTypeAttribute leaf82 = findStructMember(struct81.getAttribute(), "leaf82");
		assertEquals(MODULE_NS_TWO, leaf82.getNamespace());
		assertInstanceOf(StringType.class, leaf82.getType());
		final ComplexDataTypeAttribute leaf83 = findStructMember(struct81.getAttribute(), "leaf83");
		assertEquals(MODULE_NS_TWO, leaf83.getNamespace());
		assertInstanceOf(IntegerType.class, leaf83.getType());

		final PrimaryTypeAction rpc8 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc8");
		assertHasMeta(Constants.META_RPC, rpc8);
		assertEquals(MODULE_NS_TWO, rpc8.getNs());
		assertEmpty(rpc8.getParameter());
		assertHasNotMeta(Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED, rpc8);
		assertInstanceOf(ComplexRefType.class, rpc8.getReturnType());
		assertEquals(miStruct81.toImpliedUrn(), ((ComplexRefType) rpc8.getReturnType()).getModelUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification meContextSpec = modelService.getTypedAccess().getEModelSpecification(OSS_TOP_ME_CONTEXT_300, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertSize(6, meContextSpec.getActionSpecifications());

		final PrimaryTypeActionSpecification rpc1Spec = meContextSpec.getActionSpecification("rpc1");
		assertHasMetaInModelService(Constants.META_RPC, rpc1Spec);
		assertEquals(MODULE_NS_ONE, rpc1Spec.getNamespace());
		assertEmpty(rpc1Spec.getParameters());
		assertNull(rpc1Spec.getReturnTypeSpecification());

		final PrimaryTypeActionSpecification rpc2Spec = meContextSpec.getActionSpecification("rpc2");
		assertHasMetaInModelService(Constants.META_RPC, rpc2Spec);
		assertEquals(MODULE_NS_ONE, rpc2Spec.getNamespace());
		final PrimaryTypeActionParameterSpecification attr21 = findActionParameterSpec(rpc2Spec.getParameters(), "leaf21");
		//		assertTrue(attr21.getNs().equals(MODULE_NS_ONE));		// TODO: [ACTION_PARAM_NAMESPACE]
		assertEquals(DataType.STRING, attr21.getDataTypeSpecification().getDataType());
		assertNull(rpc2Spec.getReturnTypeSpecification());

		final ComplexDataTypeSpecification rpc3outputSpec = modelService.getTypedAccess().getEModelSpecification(miOutStructRpc3, ComplexDataTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final ComplexDataTypeAttributeSpecification attr31 = rpc3outputSpec.getAttributeSpecification("leaf31");
		assertEquals(DataType.STRING, attr31.getDataTypeSpecification().getDataType());
		assertEquals(MODULE_NS_ONE, attr31.getAttributeNamespace());
		final ComplexDataTypeAttributeSpecification attr32 = rpc3outputSpec.getAttributeSpecification("leaf32");
		assertEquals(DataType.INTEGER, attr32.getDataTypeSpecification().getDataType());
		assertEquals(MODULE_NS_ONE, attr32.getAttributeNamespace());

		final PrimaryTypeActionSpecification rpc3Spec = meContextSpec.getActionSpecification("rpc3");
		assertHasMetaInModelService(Constants.META_RPC, rpc3Spec);
		assertEquals(MODULE_NS_ONE, rpc3Spec.getNamespace());
		assertEmpty(rpc3Spec.getParameters());
		assertEquals(DataType.COMPLEX_REF, rpc3Spec.getReturnTypeSpecification().getDataType());
		assertEquals(miOutStructRpc3, rpc3Spec.getReturnTypeSpecification().getReferencedDataType());

		final PrimaryTypeActionSpecification rpc6Spec = meContextSpec.getActionSpecification("rpc6");
		assertHasMetaInModelService(Constants.META_RPC, rpc6Spec);
		assertEquals(MODULE_NS_TWO, rpc6Spec.getNamespace());
		final PrimaryTypeActionParameterSpecification attr61 = findActionParameterSpec(rpc6Spec.getParameters(), "leaf61");
		//		assertTrue(attr61.getNs().equals(MODULE_NS_ONE));		// TODO: [ACTION_PARAM_NAMESPACE]
		assertEquals(DataType.BOOLEAN, attr61.getDataTypeSpecification().getDataType());
		assertNull(rpc6Spec.getReturnTypeSpecification());

		final PrimaryTypeActionSpecification rpc7Spec = meContextSpec.getActionSpecification("rpc7");
		assertHasMetaInModelService(Constants.META_RPC, rpc7Spec);
		assertEquals(MODULE_NS_TWO, rpc7Spec.getNamespace());
		assertEmpty(rpc7Spec.getParameters());
		assertNull(rpc7Spec.getReturnTypeSpecification());

		final ComplexDataTypeSpecification struct81spec = modelService.getTypedAccess().getEModelSpecification(miStruct81, ComplexDataTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final ComplexDataTypeAttributeSpecification attr82 = struct81spec.getAttributeSpecification("leaf82");
		assertEquals(DataType.STRING, attr82.getDataTypeSpecification().getDataType());
		assertEquals(MODULE_NS_TWO, attr82.getAttributeNamespace());
		final ComplexDataTypeAttributeSpecification attr83 = struct81spec.getAttributeSpecification("leaf83");
		assertEquals(DataType.INTEGER, attr83.getDataTypeSpecification().getDataType());
		assertEquals(MODULE_NS_TWO, attr83.getAttributeNamespace());

		final PrimaryTypeActionSpecification rpc8Spec = meContextSpec.getActionSpecification("rpc8");
		assertHasMetaInModelService(Constants.META_RPC, rpc8Spec);
		assertEquals(MODULE_NS_TWO, rpc8Spec.getNamespace());
		assertEmpty(rpc8Spec.getParameters());
		assertEquals(DataType.COMPLEX_REF, rpc8Spec.getReturnTypeSpecification().getDataType());
		assertEquals(miStruct81, rpc8Spec.getReturnTypeSpecification().getReferencedDataType());
	}

	@Test
	public void test___rpc_module_one_suppressed() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "rpcs"));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		context.setGenerateRpcs(true);
		context.setExcludedNamespacesForRpcs(Arrays.asList(MODULE_NS_ONE));
		YangTransformer2.transform(context);

		internal___rpc_module_one_suppressed(context, null);
	}

	@Test
	public void test___rpc_module_one_suppressed___wildcarded_on_one() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "rpcs"));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		context.setGenerateRpcs(true);
		context.setExcludedNamespacesForRpcs(Arrays.asList(".*one"));
		YangTransformer2.transform(context);

		internal___rpc_module_one_suppressed(context, null);
	}

	@Test
	public void test___rpc_module_one_suppressed_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_RPCS, " true ");
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXPLICIT_CONTAINMENT_PARENT_IMPLIED_URN, OSS_TOP_ME_CONTEXT_300.toImpliedUrn());
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXCLUDED_RPC_NAMESPACES, MODULE_NS_ONE);

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "rpcs"), overwritingProperties);

		internal___rpc_module_one_suppressed(null, actualNmtProperties);
	}

	private void internal___rpc_module_one_suppressed(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);
		assertSize(3, containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction());

		assertNotNull(findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc6"));
		assertNotNull(findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc7"));
		assertNotNull(findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc8"));
	}

	@Test
	public void test___rpc_module_two_suppressed() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "rpcs"));
		context.setGenerateRpcs(true);
		context.setExcludedNamespacesForRpcs(Arrays.asList(MODULE_NS_TWO));
		YangTransformer2.transform(context);

		internal___rpc_module_two_suppressed(context, null);
	}

	@Test
	public void test___rpc_module_two_suppressed_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_RPCS, "true");
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXCLUDED_RPC_NAMESPACES, MODULE_NS_TWO);

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "rpcs"), overwritingProperties);

		internal___rpc_module_two_suppressed(null, actualNmtProperties);
	}

	private void internal___rpc_module_two_suppressed(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);
		assertSize(3, containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction());

		assertNotNull(findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc1"));
		assertNotNull(findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc2"));
		assertNotNull(findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc3"));
	}
}
