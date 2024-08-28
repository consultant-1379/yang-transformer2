package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_combined.CombinedEnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EModelHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;

public class EnumerationComplexTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-datatypes/";

	private static final String MODULE_NS = "urn#test#enumeration-complex";
	private static final String MODULE_NAME = "enumeration-complex";
	private static final String MODULE_REVISION = "2021-10-30";
	private static final String MODULE_XYZ_VERSION = "2021.10.30";

	@Test
	public void test___enumeration_complex() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P057_DATA_TYPE_CHANGED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "enumeration-complex"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(3, 3, 11, 0, 2, 0);

		// -----------------------------------------------------

		final ModelInfo yangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(yangModule);

		// -----------------------------------------------------

		final ModelInfo edtLeaf11ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf11", MODULE_XYZ_VERSION, null);
		assertModelExists(edtLeaf11ModelInfo);

		final ModelInfo edtLeaf12ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf12", MODULE_XYZ_VERSION, null);
		assertModelExists(edtLeaf12ModelInfo);

		final ModelInfo edtLeaf13ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf13", MODULE_XYZ_VERSION, null);
		assertModelExists(edtLeaf13ModelInfo);

		final ModelInfo edtLeaf14ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf14", MODULE_XYZ_VERSION, null);
		assertModelExists(edtLeaf14ModelInfo);

		final ModelInfo edtLeaf15ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf15", MODULE_XYZ_VERSION, null);
		assertModelExists(edtLeaf15ModelInfo);

		final ModelInfo edtLeaf71ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont7__leaf71", MODULE_XYZ_VERSION, null);
		assertModelExists(edtLeaf71ModelInfo);

		final ModelInfo edtLeaf11DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "cont1__leaf11", null);
		assertModelDoesNotExist(edtLeaf11DeviatedModelInfo);		// because content is the same in deviated-variant, only in a different order

		final ModelInfo edtLeaf12DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "cont1__leaf12", null);
		final EnumDataTypeDefinition leaf12deviatedEdt = load(edtLeaf12DeviatedModelInfo);
		assertEquals(yangModule.toUrn(), leaf12deviatedEdt.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(2, leaf12deviatedEdt.getMember());
		findAndAssertEnumMember(leaf12deviatedEdt, "RED");
		findAndAssertEnumMember(leaf12deviatedEdt, "YELLOW");

		final ModelInfo edtLeaf13DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "cont1__leaf13", null);
		final EnumDataTypeDefinition leaf13deviatedEdt = load(edtLeaf13DeviatedModelInfo);

		assertSize(4, leaf13deviatedEdt.getMember());
		findAndAssertEnumMember(leaf13deviatedEdt, "RED");
		findAndAssertEnumMember(leaf13deviatedEdt, "YELLOW");
		findAndAssertEnumMember(leaf13deviatedEdt, "GREEN");
		findAndAssertEnumMember(leaf13deviatedEdt, "BLUE");

		final ModelInfo edtLeaf14DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "cont1__leaf14", null);
		final EnumDataTypeDefinition leaf14deviatedEdt = load(edtLeaf14DeviatedModelInfo);

		assertSize(3, leaf14deviatedEdt.getMember());
		findAndAssertEnumMember(leaf14deviatedEdt, "RED");
		findAndAssertEnumMember(leaf14deviatedEdt, "YELLOW");
		findAndAssertEnumMember(leaf14deviatedEdt, "GREEN");
		assertEquals(0, findEnumMember(leaf14deviatedEdt, "GREEN").getValue());

		final ModelInfo edtLeaf15DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "cont1__leaf15", null);
		final EnumDataTypeDefinition leaf15deviatedEdt = load(edtLeaf15DeviatedModelInfo);

		assertSize(3, leaf15deviatedEdt.getMember());
		findAndAssertEnumMember(leaf15deviatedEdt, "RED");
		findAndAssertEnumMember(leaf15deviatedEdt, "YELOW");
		findAndAssertEnumMember(leaf15deviatedEdt, "GREEN");

		final ModelInfo edtLeaf61DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "cont6__leaf61", null);
		final EnumDataTypeDefinition leaf61deviatedEdt = load(edtLeaf61DeviatedModelInfo);

		assertSize(3, leaf61deviatedEdt.getMember());
		findAndAssertEnumMember(leaf61deviatedEdt, "RED");
		findAndAssertEnumMember(leaf61deviatedEdt, "YELLOW");
		findAndAssertEnumMember(leaf61deviatedEdt, "GREEN");

		final ModelInfo edtLeaf71DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "cont7__leaf71", null);
		assertModelDoesNotExist(edtLeaf71DeviatedModelInfo);

		// -----------------------------------------------------

		final ModelInfo cont1ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		assertModelExists(cont1ModelInfo);

		final ModelInfo cont6ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont6", MODULE_XYZ_VERSION);
		assertModelExists(cont6ModelInfo);

		final ModelInfo cont7ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont7", MODULE_XYZ_VERSION);
		assertModelExists(cont7ModelInfo);

		final ModelInfo cont1extModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1extModelInfo);
		assertSize(4, cont1ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf12ext = findReplacedAttribute(cont1ext, "leaf12");
		assertInstanceOf(EnumRefType.class, leaf12ext.getType());
		assertEquals(edtLeaf12DeviatedModelInfo.toImpliedUrn(), ((EnumRefType) leaf12ext.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf13ext = findReplacedAttribute(cont1ext, "leaf13");
		assertInstanceOf(EnumRefType.class, leaf13ext.getType());
		assertEquals(edtLeaf12DeviatedModelInfo.toImpliedUrn(), ((EnumRefType) leaf12ext.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf14ext = findReplacedAttribute(cont1ext, "leaf14");
		assertInstanceOf(EnumRefType.class, leaf14ext.getType());
		assertEquals(edtLeaf12DeviatedModelInfo.toImpliedUrn(), ((EnumRefType) leaf12ext.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf15ext = findReplacedAttribute(cont1ext, "leaf15");
		assertInstanceOf(EnumRefType.class, leaf15ext.getType());
		assertEquals(edtLeaf12DeviatedModelInfo.toImpliedUrn(), ((EnumRefType) leaf12ext.getType()).getModelUrn());

		final ModelInfo cont7extModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont7");
		final PrimaryTypeExtensionDefinition cont7ext = load(cont7extModelInfo);
		assertSize(1, cont7ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf71ext = findReplacedAttribute(cont7ext, "leaf71");
		assertInstanceOf(StringType.class, leaf71ext.getType());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1ModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.ENUM_REF, attr11.getDataTypeSpecification().getDataType()); 
		assertEquals(edtLeaf11ModelInfo, attr11.getDataTypeSpecification().getReferencedDataType()); 	// correct, the enum for leaf11 is not deviated.
		assertValidValue(attr11, "RED");
		assertValidValue(attr11, "YELLOW");
		assertValidValue(attr11, "GREEN");

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.ENUM_REF, attr12.getDataTypeSpecification().getDataType()); 
		assertEquals(edtLeaf12DeviatedModelInfo, attr12.getDataTypeSpecification().getReferencedDataType());
		assertValidValue(attr12, "RED");
		assertValidValue(attr12, "YELLOW");
		assertInvalidValue(attr12, "GREEN");

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.ENUM_REF, attr13.getDataTypeSpecification().getDataType()); 
		assertEquals(edtLeaf13DeviatedModelInfo, attr13.getDataTypeSpecification().getReferencedDataType()); 
		assertValidValue(attr13, "RED");
		assertValidValue(attr13, "YELLOW");
		assertValidValue(attr13, "GREEN");
		assertValidValue(attr13, "BLUE");

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.ENUM_REF, attr14.getDataTypeSpecification().getDataType()); 
		assertEquals(edtLeaf14DeviatedModelInfo, attr14.getDataTypeSpecification().getReferencedDataType()); 
		assertValidValue(attr14, "RED");
		assertValidValue(attr14, "YELLOW");
		assertValidValue(attr14, "GREEN");

		final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
		assertEquals(DataType.ENUM_REF, attr15.getDataTypeSpecification().getDataType()); 
		assertEquals(edtLeaf15DeviatedModelInfo, attr15.getDataTypeSpecification().getReferencedDataType()); 
		assertValidValue(attr15, "RED");
		assertValidValue(attr15, "YELOW");
		assertValidValue(attr15, "GREEN");

		final HierarchicalPrimaryTypeSpecification cont6spec = modelService.getTypedAccess().getEModelSpecification(cont6ModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr61 = findAttribute(cont6spec, "leaf61");
		assertEquals(DataType.ENUM_REF, attr61.getDataTypeSpecification().getDataType()); 
		assertEquals(edtLeaf61DeviatedModelInfo, attr61.getDataTypeSpecification().getReferencedDataType());
		assertValidValue(attr61, "RED");
		assertValidValue(attr61, "YELLOW");
		assertValidValue(attr61, "GREEN");

		final HierarchicalPrimaryTypeSpecification cont7spec = modelService.getTypedAccess().getEModelSpecification(cont7ModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr71 = findAttribute(cont7spec, "leaf71");
		assertEquals(DataType.STRING, attr71.getDataTypeSpecification().getDataType()); 
	}

	@Test
	public void test___enumeration_complex___combined_edt() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P057_DATA_TYPE_CHANGED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "enumeration-complex"));
		context.setGenerateCombinedEdt(true);

		YangTransformer2.transform(context);

		assertEModelCreatedCount(SchemaConstants.OSS_EDT, 0);
		assertEModelCreatedCount(SchemaConstants.OSS_EDT_COMBINED, 2);

		// -----------------------------------------------------

		final ModelInfo yangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(yangModule);

		// -----------------------------------------------------

		final ModelInfo combinedEdtForModuleModelInfo = EnumerationTypeGenerator.getModelInfoForCombinedOssEdt(MODULE_NS, MODULE_XYZ_VERSION);
		final CombinedEnumDataTypeDefinition combinedEdtDefForModule = load(combinedEdtForModuleModelInfo);

		assertSize(6, combinedEdtDefForModule.getEnumDataType());
		assertNotNull(findEdtInCombined(combinedEdtDefForModule, "cont1__leaf11"));
		assertNotNull(findEdtInCombined(combinedEdtDefForModule, "cont1__leaf12"));
		assertNotNull(findEdtInCombined(combinedEdtDefForModule, "cont1__leaf13"));
		assertNotNull(findEdtInCombined(combinedEdtDefForModule, "cont1__leaf14"));
		assertNotNull(findEdtInCombined(combinedEdtDefForModule, "cont1__leaf15"));
		assertNotNull(findEdtInCombined(combinedEdtDefForModule, "cont7__leaf71"));

		final ModelInfo combinedEdtForTargetSpecificModelInfo = EnumerationTypeGenerator.getModelInfoForCombinedOssEdt(EModelHelper.getTargetVersionSpecificName(DEFAULT_TEST_TARGET), Constants.ONE_ZERO_ZERO);
		final CombinedEnumDataTypeDefinition combinedEdtDefForTargetSpecific = load(combinedEdtForTargetSpecificModelInfo);

		assertSize(5, combinedEdtDefForTargetSpecific.getEnumDataType());
		assertNotNull(findEdtInCombined(combinedEdtDefForTargetSpecific, "urn#test#enumeration-complex__cont1__leaf12"));
		assertNotNull(findEdtInCombined(combinedEdtDefForTargetSpecific, "urn#test#enumeration-complex__cont1__leaf13"));
		assertNotNull(findEdtInCombined(combinedEdtDefForTargetSpecific, "urn#test#enumeration-complex__cont1__leaf14"));
		assertNotNull(findEdtInCombined(combinedEdtDefForTargetSpecific, "urn#test#enumeration-complex__cont1__leaf15"));
		assertNotNull(findEdtInCombined(combinedEdtDefForTargetSpecific, "urn#test#enumeration-complex__cont6__leaf61"));

		/*
		 * TODO: [COMBINED_EDT] Model Service support not ready yet; update the test code below once ready.
		 */

		//		// =====================================================================================================================
		//		// =====================================================================================================================
		//		// =====================================================================================================================
		//		
		//		processAndDeployIntoModelService();
		//		
		//		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1ModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		//		assertTrue(cont1spec != null);
		//		
		//		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		//		assertTrue(attr11 != null);
		//		assertTrue(attr11.getDataTypeSpecification().getDataType() == DataType.ENUM_REF); 
		//		assertTrue(attr11.getDataTypeSpecification().getReferencedDataType().equals(edtLeaf11ModelInfo)); 	// correct, the enum for leaf11 is not deviated.
		//		assertValidValue(attr11, "RED");
		//		assertValidValue(attr11, "YELLOW");
		//		assertValidValue(attr11, "GREEN");
		//		
		//		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		//		assertTrue(attr12 != null);
		//		assertTrue(attr12.getDataTypeSpecification().getDataType() == DataType.ENUM_REF); 
		//		assertTrue(attr12.getDataTypeSpecification().getReferencedDataType().equals(edtLeaf12DeviatedModelInfo));
		//		assertValidValue(attr12, "RED");
		//		assertValidValue(attr12, "YELLOW");
		//		assertInvalidValue(attr12, "GREEN");
		//		
		//		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		//		assertTrue(attr13 != null);
		//		assertTrue(attr13.getDataTypeSpecification().getDataType() == DataType.ENUM_REF); 
		//		assertTrue(attr13.getDataTypeSpecification().getReferencedDataType().equals(edtLeaf13DeviatedModelInfo)); 
		//		assertValidValue(attr13, "RED");
		//		assertValidValue(attr13, "YELLOW");
		//		assertValidValue(attr13, "GREEN");
		//		assertValidValue(attr13, "BLUE");
		//		
		//		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		//		assertTrue(attr14 != null);
		//		assertTrue(attr14.getDataTypeSpecification().getDataType() == DataType.ENUM_REF); 
		//		assertTrue(attr14.getDataTypeSpecification().getReferencedDataType().equals(edtLeaf14DeviatedModelInfo)); 
		//		assertValidValue(attr14, "RED");
		//		assertValidValue(attr14, "YELLOW");
		//		assertValidValue(attr14, "GREEN");
		//		
		//		final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
		//		assertTrue(attr15 != null);
		//		assertTrue(attr15.getDataTypeSpecification().getDataType() == DataType.ENUM_REF); 
		//		assertTrue(attr15.getDataTypeSpecification().getReferencedDataType().equals(edtLeaf15DeviatedModelInfo)); 
		//		assertValidValue(attr15, "RED");
		//		assertValidValue(attr15, "YELOW");
		//		assertValidValue(attr15, "GREEN");
		//		
		////		final HierarchicalPrimaryTypeSpecification cont6spec = modelService.getTypedAccess().getEModelSpecification(cont6ModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context));
		////		assertTrue(cont6spec != null);
		//
		////		final PrimaryTypeAttributeSpecification attr61 = findAttribute(cont1spec, "leaf61");
		//
		//		final HierarchicalPrimaryTypeSpecification cont7spec = modelService.getTypedAccess().getEModelSpecification(cont7ModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		//		assertTrue(cont7spec != null);
		//
		//		final PrimaryTypeAttributeSpecification attr71 = findAttribute(cont7spec, "leaf71");
		//		assertTrue(attr71 != null);
		//		assertTrue(attr71.getDataTypeSpecification().getDataType() == DataType.STRING); 
	}
}
