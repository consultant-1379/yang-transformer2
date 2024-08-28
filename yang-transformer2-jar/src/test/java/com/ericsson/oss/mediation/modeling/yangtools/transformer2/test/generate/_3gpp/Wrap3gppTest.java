package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate._3gpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAction;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeActionParameter;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeAttribute;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ComplexRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.ComplexTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.IdentityRefGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class Wrap3gppTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-3gpp/";

	private static final String MODULE_NS = "urn/test/wrap-3gpp";
	private static final String MODULE_NAME = "wrap-3gpp";
	private static final String MODULE_REVISION = "2015-01-08";
	private static final String MODULE_XYZ_VERSION = "2015.1.8";

	/**
	 * Tests the "wrap" handling for 3GPP non-unique sequence attributes
	 */
	@Test
	public void test___wrap_3gpp() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P019_MISSING_REQUIRED_CHILD_STATEMENT.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S394_LIST_MISSING_KEY.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S401_KEY_REFERS_TO_UNKNOWN_LEAF.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N662_COMPLEX_OUTPUT_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "wrap-3gpp"));
		context.setApply3gppHandling(true);
		context.setSuppressWrapGeneration(false);

		YangTransformer2.transform(context);

		internal___wrap_3gpp(context, null);
	}

	@Test
	public void test___wrap_3gpp_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_3GPP_HANDLING, "true");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "wrap-3gpp"), overwritingProperties);

//		internal___wrap_3gpp(null, actualNmtProperties);
	}

	private void internal___wrap_3gpp(final TransformerContext context, final Properties actualNmtProperties) {

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ModelInfo identity1EnumModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "identity1", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition  identity1Enum = load(identity1EnumModelInfo);
		assertNotNull(identity1Enum.getModelCreationInfo().getDerivedModel());
		assertEquals(modelInfoForYangModule.toUrn(), identity1Enum.getModelCreationInfo().getDerivedModel().getDerivedFrom());
		assertSize(2, identity1Enum.getMember());
		assertNotNull(findEnumMember(identity1Enum, "identity1"));
		assertNotNull(findEnumMember(identity1Enum, "identity2"));

		final ModelInfo miStructsequence15 = ComplexTypeGenerator.getModelInfoForCdtInOriginal(MODULE_NS, "List1__structsequence15", MODULE_XYZ_VERSION);
		final ComplexDataTypeDefinition structsequence15cdt = load(miStructsequence15);
		assertSize(3, structsequence15cdt.getAttribute());

		final ComplexDataTypeAttribute leaf16 = findStructMember(structsequence15cdt.getAttribute(), "leaf16");
		assertHasNotMeta(Constants.META_3GPP_NON_UNIQUE_SEQUENCE, leaf16);
		assertEquals(MODULE_NS, leaf16.getNamespace());
		assertInstanceOf(StringType.class, leaf16.getType());

		final ComplexDataTypeAttribute leaflist17 = findStructMember(structsequence15cdt.getAttribute(), "leaflist17");
		assertHasNotMeta(Constants.META_3GPP_NON_UNIQUE_SEQUENCE, leaflist17);
		assertEquals(MODULE_NS, leaflist17.getNamespace());
		assertInstanceOf(ListType.class, leaflist17.getType());

		final ComplexDataTypeAttribute leaf18 = findStructMember(structsequence15cdt.getAttribute(), "leaf18");
		assertInstanceOf(ListType.class, leaf18.getType());
		assertHasMeta(Constants.META_3GPP_NON_UNIQUE_SEQUENCE, leaf18);
		assertEquals(MODULE_NS, leaf18.getNamespace());
		assertInstanceOf(EnumRefType.class, ((ListType) leaf18.getType()).getCollectionValuesType());
		assertEquals(identity1EnumModelInfo.toImpliedUrn(), ((EnumRefType) ((ListType) leaf18.getType()).getCollectionValuesType()).getModelUrn());

		final ModelInfo list1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "List1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition list1 = load(list1modelInfo);
		assertNotNull(list1.getModelCreationInfo().getDerivedModel());
		assertEquals(modelInfoForYangModule.toUrn(), list1.getModelCreationInfo().getDerivedModel().getDerivedFrom());
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, list1);
		assertSize(6, list1.getPrimaryTypeAttribute());

		final PrimaryTypeAttribute id = findAttribute(list1.getPrimaryTypeAttribute(), "id");
		assertTrue(id.isKey());
		assertHasNotMeta(Constants.META_ARTIFIAL_KEY, id);

		final PrimaryTypeAttribute leaf11 = findAttribute(list1.getPrimaryTypeAttribute(), "leaf11");
		assertInstanceOf(StringType.class, leaf11.getType());

		final PrimaryTypeAttribute leaflist12 = findAttribute(list1.getPrimaryTypeAttribute(), "leaflist12");
		assertInstanceOf(ListType.class, leaflist12.getType());
		assertInstanceOf(IntegerType.class, ((ListType) leaflist12.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaf13 = findAttribute(list1.getPrimaryTypeAttribute(), "leaf13");
		assertHasMeta(Constants.META_3GPP_NON_UNIQUE_SEQUENCE, leaf13);
		assertInstanceOf(ListType.class, leaf13.getType());
		assertInstanceOf(StringType.class, ((ListType) leaf13.getType()).getCollectionValuesType());
		assertEquals(10L, ((ListType) leaf13.getType()).getCollectionSizeConstraint().getMaxSize().getValue());

		final ModelInfo edtLeaf14ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "List1__leaf14", MODULE_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf11edt = load(edtLeaf14ModelInfo);
		assertSize(2, leaf11edt.getMember());
		assertNotNull(findEnumMember(leaf11edt, "RED"));
		assertNotNull(findEnumMember(leaf11edt, "YELLOW"));

		final PrimaryTypeAttribute leaf14 = findAttribute(list1.getPrimaryTypeAttribute(), "leaf14");
		assertHasMeta(Constants.META_3GPP_NON_UNIQUE_SEQUENCE, leaf14);
		assertInstanceOf(ListType.class, leaf14.getType());
		assertInstanceOf(EnumRefType.class, ((ListType) leaf14.getType()).getCollectionValuesType());
		assertEquals(edtLeaf14ModelInfo.toImpliedUrn(), ((EnumRefType) ((ListType) leaf14.getType()).getCollectionValuesType()).getModelUrn());

		final PrimaryTypeAttribute leaf15 = findAttribute(list1, "structsequence15");
		assertInstanceOf(ListType.class, leaf15.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) leaf15.getType()).getCollectionValuesType());
		assertEquals(miStructsequence15.toImpliedUrn(), ((ComplexRefType) ((ListType) leaf15.getType()).getCollectionValuesType()).getModelUrn());

		// -----------------------------------------------------

		final ModelInfo list1ExtModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "List1");
		final PrimaryTypeExtensionDefinition list1Ext = load(list1ExtModelInfo);

		final PrimaryTypeAction action2 = findAction(list1Ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action2");
		assertHasNotMeta(Constants.META_3GPP_ACTION_RETURN_TYPE_IS_NON_UNIQUE_SEQUENCE, action2);

		final PrimaryTypeActionParameter leaf21 = findActionParameter(action2.getParameter(), "leaf21");
		assertInstanceOf(StringType.class, leaf21.getType());

		final PrimaryTypeActionParameter leaf22 = findActionParameter(action2.getParameter(), "leaf22");
		assertHasMeta(Constants.META_3GPP_NON_UNIQUE_SEQUENCE, leaf22);
		assertInstanceOf(ListType.class, leaf22.getType());
		assertInstanceOf(StringType.class, ((ListType) leaf22.getType()).getCollectionValuesType());
		assertEquals(5L, ((ListType) leaf22.getType()).getCollectionSizeConstraint().getMinSize().getValue());

		final ModelInfo edtLeaf26DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "List1__action2__out__leaf26", null);
		final EnumDataTypeDefinition leaf26deviatedEdt = load(edtLeaf26DeviatedModelInfo);
		assertEquals(modelInfoForYangModule.toUrn(), leaf26deviatedEdt.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		final ModelInfo modelInfoOutStructAction2 = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, ComplexTypeGenerator.getPathToActionOrRpcOutput("List1", "action2"));
		final ComplexDataTypeDefinition outStructAction2 = load(modelInfoOutStructAction2);
		assertSize(2, outStructAction2.getAttribute());

		final ComplexDataTypeAttribute leaf25 = findStructMember(outStructAction2.getAttribute(), "leaf25");
		assertHasNotMeta(Constants.META_3GPP_NON_UNIQUE_SEQUENCE, leaf25);
		assertEquals(MODULE_NS, leaf25.getNamespace());
		assertInstanceOf(BooleanType.class, leaf25.getType());

		final ComplexDataTypeAttribute leaf26 = findStructMember(outStructAction2.getAttribute(), "leaf26");
		assertHasMeta(Constants.META_3GPP_NON_UNIQUE_SEQUENCE, leaf26);
		assertEquals(MODULE_NS, leaf26.getNamespace());
		assertInstanceOf(ListType.class, leaf26.getType());
		assertInstanceOf(EnumRefType.class, ((ListType) leaf26.getType()).getCollectionValuesType());
		assertEquals(edtLeaf26DeviatedModelInfo.toImpliedUrn(), ((EnumRefType) ((ListType) leaf26.getType()).getCollectionValuesType()).getModelUrn());

		assertInstanceOf(ComplexRefType.class, action2.getReturnType());
		assertEquals(modelInfoOutStructAction2.toImpliedUrn(), ((ComplexRefType) action2.getReturnType()).getModelUrn());

		// -----------------------------------------------------

		final PrimaryTypeAction action3 = findAction(list1Ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action3");
		assertHasMeta(Constants.META_3GPP_ACTION_RETURN_TYPE_IS_NON_UNIQUE_SEQUENCE, action3);

		assertInstanceOf(ListType.class, action3.getReturnType());
		assertInstanceOf(StringType.class, ((ListType) action3.getReturnType()).getCollectionValuesType());

		// -----------------------------------------------------

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "Cont4", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, cont4);

		final PrimaryTypeAttribute leaf41 = findAttribute(cont4.getPrimaryTypeAttribute(), "leaf41");
		assertHasMeta(Constants.META_3GPP_NON_UNIQUE_SEQUENCE, leaf41);
		assertInstanceOf(ListType.class, leaf41.getType());
		assertInstanceOf(StringType.class, ((ListType) leaf41.getType()).getCollectionValuesType());

		/*
		 * The rest of the "wraps" are not recognized as wraps, but as struct-sequences
		 */
		assertNull(findAttribute(cont4.getPrimaryTypeAttribute(), "leaf42"));
		assertNull(findAttribute(cont4.getPrimaryTypeAttribute(), "leaf43"));
		assertNull(findAttribute(cont4.getPrimaryTypeAttribute(), "leaf44"));
		assertNull(findAttribute(cont4.getPrimaryTypeAttribute(), "leaf45"));
		assertNull(findAttribute(cont4.getPrimaryTypeAttribute(), "leaf46"));
		assertNull(findAttribute(cont4.getPrimaryTypeAttribute(), "leaf47"));
		assertNull(findAttribute(cont4.getPrimaryTypeAttribute(), "leaf48"));
		assertNull(findAttribute(cont4.getPrimaryTypeAttribute(), "leaf49"));

		final PrimaryTypeAttribute leaf42wrap = findAttribute(cont4, "leaf42wrap");
		assertInstanceOf(ListType.class, leaf42wrap.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) leaf42wrap.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaf43Wrap = findAttribute(cont4, "leaf43Wrap");
		assertInstanceOf(ListType.class, leaf43Wrap.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) leaf43Wrap.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaf44Wrap = findAttribute(cont4, "leaf44Wrap");
		assertInstanceOf(ListType.class, leaf44Wrap.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) leaf44Wrap.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaf45Wrap = findAttribute(cont4, "leaf45Wrap");
		assertInstanceOf(ListType.class, leaf45Wrap.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) leaf45Wrap.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaf46Wrap = findAttribute(cont4, "leaf46Wrap");
		assertInstanceOf(ListType.class, leaf46Wrap.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) leaf46Wrap.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaf47Wrap = findAttribute(cont4, "leaf47Wrap");
		assertInstanceOf(ListType.class, leaf47Wrap.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) leaf47Wrap.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaf48Wrap = findAttribute(cont4, "leaf48Wrap");
		assertInstanceOf(ListType.class, leaf48Wrap.getType());
		assertInstanceOf(ComplexRefType.class, ((ListType) leaf48Wrap.getType()).getCollectionValuesType());

		// -----------------------------------------------------

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont5", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont5 = load(cont5modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, cont5);

		assertNull(findAttribute(cont5.getPrimaryTypeAttribute(), "leaf51"));
		assertNull(findAttribute(cont5.getPrimaryTypeAttribute(), "leaf51Wrap"));

		final ModelInfo leaf51WrapModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "leaf51Wrap", MODULE_XYZ_VERSION);
		assertModelExists(leaf51WrapModelInfo);
	}
}
