package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate._3gpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
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
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.ComplexTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.eri.ValidatorFindingTypeEri;

public class StructsComplex3gppTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-3gpp/";

	private static final String MODULE_NS = "urn:test:structs-complex-3gpp";
	private static final String MODULE_NAME = "structs-complex-3gpp";
	private static final String MODULE_REVISION = "2021-12-07";
	private static final String MODULE_XYZ_VERSION = "2021.12.7";

	private static final String MODULE_NS_DEV = "urn:test:structs-complex-3gpp-dev";

	/**
	 * Tests deviations made into a 3GPP struct
	 */
	@Test
	public void test___complex_structs_3gpp() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E647_NON_ERICSSON_MODULE_IMPORTS_ERICSSON_MODULE.toString());

		final TransformerContext context = createContextWith3ppModules(new File(TEST_SUB_DIR + "structs-complex-3gpp"));
		context.setApply3gppHandling(true);

		YangTransformer2.transform(context);

		internal___complex_structs_3gpp(context, null);
	}

	@Test
	public void test___complex_structs_3gpp___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_3GPP_HANDLING, "true");

		final Properties actualNmtProperties = transformThroughNmtPluginWith3ppModules(new File(TEST_SUB_DIR + "structs-complex-3gpp"), overwritingProperties);

		internal___complex_structs_3gpp(null, actualNmtProperties);
	}

	private void internal___complex_structs_3gpp(final TransformerContext context, final Properties actualNmtProperties) {
		assertEModelCreatedCount(1, 1, 0, 0, 6, 6);

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ModelInfo miStruct11cdt = ComplexTypeGenerator.getModelInfoForCdtInOriginal(MODULE_NS, "Cont1__struct11", MODULE_XYZ_VERSION);
		final ComplexDataTypeDefinition struct11 = load(miStruct11cdt);
		assertSize(2, struct11.getAttribute());
		final ComplexDataTypeAttribute member12 = findStructMember(struct11.getAttribute(), "member12");
		assertEquals(MODULE_NS, member12.getNamespace());
		assertInstanceOf(StringType.class, member12.getType());
		final ComplexDataTypeAttribute member13 = findStructMember(struct11.getAttribute(), "member13");
		assertEquals(MODULE_NS, member13.getNamespace());
		assertInstanceOf(BooleanType.class, member13.getType());

		final ModelInfo miStruct21cdt = ComplexTypeGenerator.getModelInfoForCdtInOriginal(MODULE_NS, "Cont1__struct21", MODULE_XYZ_VERSION);
		final ComplexDataTypeDefinition struct21 = load(miStruct21cdt);
		assertSize(2, struct21.getAttribute());
		final ComplexDataTypeAttribute member22 = findStructMember(struct21.getAttribute(), "member22");
		assertEquals("description leaf22", member22.getDesc());
		final ComplexDataTypeAttribute member23 = findStructMember(struct21.getAttribute(), "member23");
		assertEquals("description leaf23", member23.getDesc());

		final ModelInfo miStruct31cdt = ComplexTypeGenerator.getModelInfoForCdtInOriginal(MODULE_NS, "Cont1__struct31", MODULE_XYZ_VERSION);
		final ComplexDataTypeDefinition struct31 = load(miStruct31cdt);
		assertSize(2, struct31.getAttribute());
		final ComplexDataTypeAttribute member32 = findStructMember(struct31.getAttribute(), "member32");
		assertTrue(member32.isMandatory());
		final ComplexDataTypeAttribute member33 = findStructMember(struct31.getAttribute(), "member33");
		assertTrue(member33.isMandatory());

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "Cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		assertInstanceOf(ComplexRefType.class, findAttribute(cont1.getPrimaryTypeAttribute(), "struct11").getType());
		assertEquals(miStruct11cdt.toImpliedUrn(), ((ComplexRefType) findAttribute(cont1.getPrimaryTypeAttribute(), "struct11").getType()).getModelUrn());
		assertInstanceOf(ComplexRefType.class, findAttribute(cont1.getPrimaryTypeAttribute(), "struct21").getType());
		assertEquals(miStruct21cdt.toImpliedUrn(), ((ComplexRefType) findAttribute(cont1.getPrimaryTypeAttribute(), "struct21").getType()).getModelUrn());
		assertInstanceOf(ComplexRefType.class, findAttribute(cont1.getPrimaryTypeAttribute(), "struct31").getType());
		assertEquals(miStruct31cdt.toImpliedUrn(), ((ComplexRefType) findAttribute(cont1.getPrimaryTypeAttribute(), "struct31").getType()).getModelUrn());

		// -----------------------------------------------------

		final ModelInfo miStruct11cdtExt = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "Cont1__struct11");
		final ComplexDataTypeDefinition struct11cdtExt = load(miStruct11cdtExt);
		assertSize(2, struct11cdtExt.getAttribute());
		final ComplexDataTypeAttribute member12ext = findStructMember(struct11cdtExt.getAttribute(), "member12");
		assertEquals(MODULE_NS, member12ext.getNamespace());
		assertInstanceOf(StringType.class, member12ext.getType());
		final ComplexDataTypeAttribute member13ext = findStructMember(struct11cdtExt.getAttribute(), "member13");
		assertEquals(MODULE_NS_DEV, member13ext.getNamespace());
		assertInstanceOf(IntegerType.class, member13ext.getType());

		final ModelInfo miStruct21cdtExt = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "Cont1__struct21");
		final ComplexDataTypeDefinition struct21cdtExt = load(miStruct21cdtExt);
		assertSize(2, struct21cdtExt.getAttribute());
		final ComplexDataTypeAttribute member22ext = findStructMember(struct21cdtExt.getAttribute(), "member22");
		assertEquals("updated description leaf22", member22ext.getDesc());
		final ComplexDataTypeAttribute member23ext = findStructMember(struct21cdtExt.getAttribute(), "member23");
		assertEquals("description leaf23", member23ext.getDesc());

		final ModelInfo miStruct31cdtExt = ComplexTypeGenerator.getModelInfoForCdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "Cont1__struct31");
		final ComplexDataTypeDefinition struct31cdtExt = load(miStruct31cdtExt);
		assertSize(2, struct31cdtExt.getAttribute());
		final ComplexDataTypeAttribute member32ext = findStructMember(struct31cdtExt.getAttribute(), "member32");
		assertTrue(member32ext.isMandatory());
		final ComplexDataTypeAttribute member33ext = findStructMember(struct31cdtExt.getAttribute(), "member33");
		assertFalse(member33ext.isMandatory());

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "Cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);

		final PrimaryTypeAttribute struct11ext = findReplacedAttribute(cont1ext, "struct11");
		assertEquals(miStruct11cdtExt.toImpliedUrn(), ((ComplexRefType) struct11ext.getType()).getModelUrn());
		final PrimaryTypeAttribute struct21ext = findReplacedAttribute(cont1ext, "struct21");
		assertEquals(miStruct21cdtExt.toImpliedUrn(), ((ComplexRefType) struct21ext.getType()).getModelUrn());
		final PrimaryTypeAttribute struct31ext = findReplacedAttribute(cont1ext, "struct31");
		assertEquals(miStruct31cdtExt.toImpliedUrn(), ((ComplexRefType) struct31ext.getType()).getModelUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final ComplexDataTypeSpecification struct11spec = modelService.getTypedAccess().getEModelSpecification(miStruct11cdtExt, ComplexDataTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertSize(2, struct11spec.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification member12Spec = struct11spec.getAttributeSpecification("member12");
		assertEquals(MODULE_NS, member12Spec.getAttributeNamespace());
		assertEquals(DataType.STRING, member12Spec.getDataTypeSpecification().getDataType());
		final ComplexDataTypeAttributeSpecification member13Spec = struct11spec.getAttributeSpecification("member13");
		assertEquals(MODULE_NS_DEV, member13Spec.getAttributeNamespace());		// because replaced by attr with same name but different namespace
		assertEquals(DataType.INTEGER, member13Spec.getDataTypeSpecification().getDataType());

		final ComplexDataTypeSpecification struct21spec = modelService.getTypedAccess().getEModelSpecification(miStruct21cdtExt, ComplexDataTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertSize(2, struct21spec.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification member22Spec = struct21spec.getAttributeSpecification("member22");
		assertEquals(MODULE_NS, member22Spec.getAttributeNamespace());
		assertEquals("updated description leaf22", member22Spec.getDescription());
		final ComplexDataTypeAttributeSpecification member23Spec = struct21spec.getAttributeSpecification("member23");
		assertEquals(MODULE_NS, member23Spec.getAttributeNamespace());
		assertEquals("description leaf23", member23Spec.getDescription());

		final ComplexDataTypeSpecification struct31spec = modelService.getTypedAccess().getEModelSpecification(miStruct31cdtExt, ComplexDataTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertSize(2, struct31spec.getAllAttributeSpecifications());
		final ComplexDataTypeAttributeSpecification member32Spec = struct31spec.getAttributeSpecification("member32");
		assertEquals(MODULE_NS, member32Spec.getAttributeNamespace());
		assertTrue(member32Spec.isMandatory());
		final ComplexDataTypeAttributeSpecification member33Spec = struct31spec.getAttributeSpecification("member33");
		assertEquals(MODULE_NS, member33Spec.getAttributeNamespace());
		assertFalse(member33Spec.isMandatory());

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertSize(4, cont1spec.getAttributeSpecifications());		// +1 for the auto-generated key

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "struct11");
		assertEquals(DataType.COMPLEX_REF, attr11.getDataTypeSpecification().getDataType());
		assertEquals(miStruct11cdtExt, attr11.getDataTypeSpecification().getReferencedDataType());
		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont1spec, "struct21");
		assertEquals(DataType.COMPLEX_REF, attr21.getDataTypeSpecification().getDataType());
		assertEquals(miStruct21cdtExt, attr21.getDataTypeSpecification().getReferencedDataType());
		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont1spec, "struct31");
		assertEquals(DataType.COMPLEX_REF, attr31.getDataTypeSpecification().getDataType());
		assertEquals(miStruct31cdtExt, attr31.getDataTypeSpecification().getReferencedDataType());
	}
}
