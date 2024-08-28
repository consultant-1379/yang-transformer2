package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeMember;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.IdentityRefGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;

public class TopLevelAttributesTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/";

	private static final String MODULE_NS_ONE = "urn~test~top-level-attributes-one";
	private static final String MODULE_XYZ_VERSION_ONE = "2021.11.30";
	private static final String MODULE_NS_TWO = "urn#test#top-level-attributes-two";

	@Test
	public void test___top_level_attributes() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N663_DUPLICATE_DATA_NODE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N668_NOT_SUPPORTED_AT_TOP_LEVEL.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "top-level-attributes"));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo containmentParent = context.getEffectiveContainmentParent();

		final ModelInfo ptedModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, containmentParent.getNamespace(), containmentParent.getName());
		final PrimaryTypeExtensionDefinition pted = load(ptedModelInfo);

		assertSize(6, pted.getPrimaryTypeExtension().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf11one = findAddedAttribute(pted, MODULE_NS_ONE, "leaf11");
		assertHasMeta(Constants.META_TOP_LEVEL_DATA_NODE, leaf11one);
		assertInstanceOf(BooleanType.class, leaf11one.getType());

		final PrimaryTypeAttribute leaf11two = findAddedAttribute(pted, MODULE_NS_TWO, "leaf11");
		assertHasMeta(Constants.META_TOP_LEVEL_DATA_NODE, leaf11two);
		assertInstanceOf(LongType.class, leaf11two.getType());

		final ModelInfo edt12ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, containmentParent.getNamespace(), "ManagedElement__leaf12", null);
		final EnumDataTypeDefinition edt12 = load(edt12ModelInfo);
		assertSize(2, edt12.getMember());

		final PrimaryTypeAttribute leaf12 = findAddedAttribute(pted, MODULE_NS_ONE, "leaf12");
		assertHasMeta(Constants.META_TOP_LEVEL_DATA_NODE, leaf12);
		assertInstanceOf(EnumRefType.class, leaf12.getType());
		assertEquals(edt12ModelInfo.toImpliedUrn(), ((EnumRefType) leaf12.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf13 = findAddedAttribute(pted, MODULE_NS_ONE, "leaf13");
		assertHasMeta(Constants.META_TOP_LEVEL_DATA_NODE, leaf13);
		assertInstanceOf(IntegerType.class, leaf13.getType());

		final PrimaryTypeAttribute leaf14 = findAddedAttribute(pted, MODULE_NS_ONE, "leaf14");
		assertHasMeta(Constants.META_TOP_LEVEL_DATA_NODE, leaf14);
		assertInstanceOf(IntegerType.class, leaf14.getType());

		final ModelInfo edt15ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "identity1", MODULE_XYZ_VERSION_ONE);
		final EnumDataTypeDefinition edt15 = load(edt15ModelInfo);
		assertSize(2, edt15.getMember());		// 1 & 2

		final EnumDataTypeMember identity1 = findEnumMember(edt15, "identity1");
		assertEquals(MODULE_NS_ONE, identity1.getNamespace());
		final EnumDataTypeMember identity2 = findEnumMember(edt15, "identity2");
		assertEquals(MODULE_NS_ONE, identity2.getNamespace());

		final ModelInfo edt15ModelInfoExt = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, edt15ModelInfo.getNamespace(), edt15ModelInfo.getName());
		final EnumDataTypeExtensionDefinition edt15ext = load(edt15ModelInfoExt);
		assertSize(1, edt15ext.getEnumDataTypeExtension().getMember());		// identity3

		final EnumDataTypeMember identity3 = findAndAssertAddedEnumMember(edt15ext, "identity3");
		assertEquals(MODULE_NS_TWO, identity3.getNamespace());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification comTopMeSpec = modelService.getTypedAccess().getEModelSpecification(containmentParent, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		assertNotNull(findAttribute(comTopMeSpec, "leaf12"));
		assertNotNull(findAttribute(comTopMeSpec, "leaf13"));
		assertNotNull(findAttribute(comTopMeSpec, "leaf14"));
		assertNotNull(findAttribute(comTopMeSpec, "leaf15"));

		/*
		 * TODO: [MS_BUG_DUPLICATE_ATTRIBUTES_IN_SAME_EXTENSION]
		 */
		//		assertTrue(findAttribute(comTopMeSpec, MODULE_NS_ONE, "leaf11") != null);	
		//		assertTrue(findAttribute(comTopMeSpec, MODULE_NS_TWO, "leaf11") != null);
	}

}
