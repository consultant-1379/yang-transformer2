package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.edt.EnumDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LifeCycleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.UnionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeMember;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_combined.CombinedEnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_combined.EnumDataType;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class EnumerationSimpleTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-datatypes/";

	private static final String MODULE_NS = "urn@test@enumeration-simple";
	private static final String MODULE_NAME = "enumeration-simple";
	private static final String MODULE_REVISION = "2021-10-30";
	private static final String MODULE_XYZ_VERSION = "2021.10.30";

	@Test
	public void test___enumeration_simple() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "enumeration-simple"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(1, 0, 4, 0, 1, 0);

		// -----------------------------------------------------

		final ModelInfo yangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(yangModule);

		// -----------------------------------------------------

		final ModelInfo edtLeaf11ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf11", MODULE_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf11edt = load(edtLeaf11ModelInfo);
		assertEquals(yangModule.toUrn(), leaf11edt.getModelCreationInfo().getDerivedModel().getDerivedFrom());
		assertEquals(LifeCycleType.DEPRECATED, leaf11edt.getLifeCycle());

		assertSize(3, leaf11edt.getMember());

		final EnumDataTypeMember leaf11edtRed = findEnumMember(leaf11edt, "RED");
		assertNull(leaf11edtRed.getNamespace());
		assertEquals(0, leaf11edtRed.getValue());

		final EnumDataTypeMember leaf11edtYellow = findEnumMember(leaf11edt, "YELLOW");
		assertNull(leaf11edtYellow.getNamespace());
		assertEquals(1, leaf11edtYellow.getValue());

		final EnumDataTypeMember leaf11edtGreen = findEnumMember(leaf11edt, "GREEN");
		assertNull(leaf11edtGreen.getNamespace());
		assertEquals(2, leaf11edtGreen.getValue());

		final ModelInfo edtLeaf12ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf12", MODULE_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf12edt = load(edtLeaf12ModelInfo);

		assertSize(3, leaf12edt.getMember());
		final EnumDataTypeMember leaf12edtRed = findEnumMember(leaf12edt, "BLUE");
		final EnumDataTypeMember leaf12edtYellow = findEnumMember(leaf12edt, "PINK");
		final EnumDataTypeMember leaf12edtGreen = findEnumMember(leaf12edt, "PURPLE");
		assertEquals(995, leaf12edtRed.getValue());
		assertEquals(996, leaf12edtYellow.getValue());
		assertEquals(997, leaf12edtGreen.getValue());

		final ModelInfo edtLeaf13ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf13", MODULE_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf13edt = load(edtLeaf13ModelInfo);

		assertSize(3, leaf13edt.getMember());
		final EnumDataTypeMember leaf13edtRed = findEnumMember(leaf13edt, "TEAL");
		final EnumDataTypeMember leaf13edtYellow = findEnumMember(leaf13edt, "ORANGE");
		final EnumDataTypeMember leaf13edtGreen = findEnumMember(leaf13edt, "RUBY");
		assertEquals(0, leaf13edtRed.getValue());
		assertEquals(1, leaf13edtYellow.getValue());
		assertEquals(2, leaf13edtGreen.getValue());

		final ModelInfo edtLeaf14ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf14", MODULE_XYZ_VERSION, 0);
		final EnumDataTypeDefinition leaf14edt = load(edtLeaf14ModelInfo);

		assertSize(3, leaf14edt.getMember());
		final EnumDataTypeMember leaf14edtRed = findEnumMember(leaf14edt, "BLACK");
		final EnumDataTypeMember leaf14edtYellow = findEnumMember(leaf14edt, "WHITE");
		final EnumDataTypeMember leaf14edtGreen = findEnumMember(leaf14edt, "GREY");
		assertEquals(0, leaf14edtRed.getValue());
		assertEquals(1, leaf14edtYellow.getValue());
		assertEquals(2, leaf14edtGreen.getValue());

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(EnumRefType.class, leaf11.getType());
		assertEquals(edtLeaf11ModelInfo.toImpliedUrn(), ((EnumRefType) leaf11.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(EnumRefType.class, leaf12.getType());
		assertEquals(edtLeaf12ModelInfo.toImpliedUrn(), ((EnumRefType) leaf12.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(EnumRefType.class, leaf13.getType());
		assertEquals(edtLeaf13ModelInfo.toImpliedUrn(), ((EnumRefType) leaf13.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(UnionType.class, leaf14.getType());
		assertInstanceOf(EnumRefType.class, ((UnionType) leaf14.getType()).getMember().get(0));
		assertInstanceOf(StringType.class, ((UnionType) leaf14.getType()).getMember().get(1));
		assertEquals(edtLeaf14ModelInfo.toImpliedUrn(), ((EnumRefType) ((UnionType) leaf14.getType()).getMember().get(0)).getModelUrn());

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertSize(1, mimInfo.getSupportedMims().getMimMappedTo());
		assertHasSupportedMim(mimInfo, MODULE_NS, MODULE_XYZ_VERSION, yangModule.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.ENUM_REF, attr11.getDataTypeSpecification().getDataType()); 
		assertEquals(edtLeaf11ModelInfo, attr11.getDataTypeSpecification().getReferencedDataType()); 

		assertValidValue(attr11, "RED");
		assertValidValue(attr11, "YELLOW");
		assertValidValue(attr11, "GREEN");

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.ENUM_REF, attr12.getDataTypeSpecification().getDataType()); 
		assertEquals(edtLeaf12ModelInfo, attr12.getDataTypeSpecification().getReferencedDataType());

		assertValidValue(attr12, "BLUE");
		assertValidValue(attr12, "PINK");
		assertValidValue(attr12, "PURPLE");

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.ENUM_REF, attr13.getDataTypeSpecification().getDataType()); 
		assertEquals(edtLeaf13ModelInfo, attr13.getDataTypeSpecification().getReferencedDataType()); 

		assertValidValue(attr13, "TEAL");
		assertValidValue(attr13, "ORANGE");
		assertValidValue(attr13, "RUBY");

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.UNION, attr14.getDataTypeSpecification().getDataType());
		assertSize(2, attr14.getDataTypeSpecification().getMembersDataTypeSpecification());
		assertEquals(DataType.ENUM_REF, attr14.getDataTypeSpecification().getMembersDataTypeSpecification().get(0).getDataType());
		assertEquals(edtLeaf14ModelInfo, attr14.getDataTypeSpecification().getMembersDataTypeSpecification().get(0).getReferencedDataType());

		assertValidValue(attr14, "BLACK");
		assertValidValue(attr14, "WHITE");
		assertValidValue(attr14, "GREY");

		// --------------------------------------

		final EnumDataTypeSpecification leaf11EnumSpec = modelService.getTypedAccess().getEModelSpecification(edtLeaf11ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(3, leaf11EnumSpec.getAllMembers());
		assertEquals(0, findAndAssertEnumMember(leaf11EnumSpec, "RED").getMemberValue());
		assertEquals(1, findAndAssertEnumMember(leaf11EnumSpec, "YELLOW").getMemberValue());
		assertEquals(2, findAndAssertEnumMember(leaf11EnumSpec, "GREEN").getMemberValue());

		final EnumDataTypeSpecification leaf12EnumSpec = modelService.getTypedAccess().getEModelSpecification(edtLeaf12ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(3, leaf12EnumSpec.getAllMembers());
		assertEquals(995, findAndAssertEnumMember(leaf12EnumSpec, "BLUE").getMemberValue());
		assertEquals(996, findAndAssertEnumMember(leaf12EnumSpec, "PINK").getMemberValue());
		assertEquals(997, findAndAssertEnumMember(leaf12EnumSpec, "PURPLE").getMemberValue());

		final EnumDataTypeSpecification leaf13EnumSpec = modelService.getTypedAccess().getEModelSpecification(edtLeaf13ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(3, leaf13EnumSpec.getAllMembers());
		assertEquals(0, findAndAssertEnumMember(leaf13EnumSpec, "TEAL").getMemberValue());
		assertEquals(1, findAndAssertEnumMember(leaf13EnumSpec, "ORANGE").getMemberValue());
		assertEquals(2, findAndAssertEnumMember(leaf13EnumSpec, "RUBY").getMemberValue());

		final EnumDataTypeSpecification leaf14EnumSpec = modelService.getTypedAccess().getEModelSpecification(edtLeaf14ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(3, leaf14EnumSpec.getAllMembers());
		assertEquals(0, findAndAssertEnumMember(leaf14EnumSpec, "BLACK").getMemberValue());
		assertEquals(1, findAndAssertEnumMember(leaf14EnumSpec, "WHITE").getMemberValue());
		assertEquals(2, findAndAssertEnumMember(leaf14EnumSpec, "GREY").getMemberValue());
	}

	@Test
	public void test___enumeration_simple_remove_if_feature_nodes() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "enumeration-simple"));
		context.setFeatureHandling(FeatureHandling.REMOVE_ALL);
		YangTransformer2.transform(context);

		assertEModelCreatedCount(1, 1, 5, 0, 1, 0);

		// -----------------------------------------------------

		final ModelInfo edtLeaf11ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf11", MODULE_XYZ_VERSION, null);
		assertModelExists(edtLeaf11ModelInfo);

		final ModelInfo edtLeaf12ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf12", MODULE_XYZ_VERSION, null);
		assertModelExists(edtLeaf12ModelInfo);

		final ModelInfo edtLeaf13ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf13", MODULE_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf13edt = load(edtLeaf13ModelInfo);

		assertSize(3, leaf13edt.getMember());
		findAndAssertEnumMember(leaf13edt, "TEAL");
		findAndAssertEnumMember(leaf13edt, "ORANGE");
		findAndAssertEnumMember(leaf13edt, "RUBY");				// correct, should be in base model

		final ModelInfo edtLeaf13DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS, "cont1__leaf13", null);
		final EnumDataTypeDefinition leaf13deviatedEdt = load(edtLeaf13DeviatedModelInfo);

		assertSize(2, leaf13deviatedEdt.getMember());
		findAndAssertEnumMember(leaf13deviatedEdt, "TEAL");
		findAndAssertEnumMember(leaf13deviatedEdt, "ORANGE");
		assertNull(findEnumMember(leaf13deviatedEdt, "RUBY"));	// not in deviated variant

		final ModelInfo edtLeaf14ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf14", MODULE_XYZ_VERSION, 0);
		assertModelExists(edtLeaf14ModelInfo);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(UnionType.class, leaf14.getType());
		assertInstanceOf(EnumRefType.class, ((UnionType) leaf14.getType()).getMember().get(0));
		assertInstanceOf(StringType.class, ((UnionType) leaf14.getType()).getMember().get(1));
		assertEquals(edtLeaf14ModelInfo.toImpliedUrn(), ((EnumRefType) ((UnionType) leaf14.getType()).getMember().get(0)).getModelUrn());

		// -----------------------------------------------------

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);
		assertRequiresTargetType(context, null, cont1ext.getRequiresTargetType());

		assertSize(1, cont1ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());
		final PrimaryTypeAttribute leaf13ext = findReplacedAttribute(cont1ext, "leaf13");
		assertInstanceOf(EnumRefType.class, leaf13ext.getType());
		assertEquals(edtLeaf13DeviatedModelInfo.toImpliedUrn(), ((EnumRefType) leaf13ext.getType()).getModelUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertValidValue(attr11, "RED");
		assertValidValue(attr11, "YELLOW");
		assertValidValue(attr11, "GREEN");

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertValidValue(attr12, "BLUE");
		assertValidValue(attr12, "PINK");
		assertValidValue(attr12, "PURPLE");

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertValidValue(attr13, "TEAL");
		assertValidValue(attr13, "ORANGE");
		assertInvalidValue(attr13, "RUBY");

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.UNION, attr14.getDataTypeSpecification().getDataType());
		assertSize(2, attr14.getDataTypeSpecification().getMembersDataTypeSpecification());
		assertEquals(DataType.ENUM_REF, attr14.getDataTypeSpecification().getMembersDataTypeSpecification().get(0).getDataType());
		assertEquals(edtLeaf14ModelInfo, attr14.getDataTypeSpecification().getMembersDataTypeSpecification().get(0).getReferencedDataType());

		assertValidValue(attr14, "BLACK");
		assertValidValue(attr14, "WHITE");
		assertValidValue(attr14, "GREY");
	}

	@Test
	public void test___enumeration_simple___combined_edt() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "enumeration-simple"));
		context.setGenerateCombinedEdt(true);
		YangTransformer2.transform(context);

		internal___enumeration_simple___combined_edt(context, null);
	}

	@Test
	public void test___enumeration_simple___combined_edt___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_COMBINED_EDT, "true");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "enumeration-simple"), overwritingProperties);

		internal___enumeration_simple___combined_edt(null, actualNmtProperties);
	}

	private void internal___enumeration_simple___combined_edt(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(SchemaConstants.OSS_EDT, 0);
		assertEModelCreatedCount(SchemaConstants.OSS_EDT_COMBINED, 1);

		// -----------------------------------------------------

		final ModelInfo yangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(yangModule);

		// -----------------------------------------------------

		final ModelInfo combinedEdtForModuleModelInfo = EnumerationTypeGenerator.getModelInfoForCombinedOssEdt(MODULE_NS, MODULE_XYZ_VERSION);
		final CombinedEnumDataTypeDefinition combinedEdtDefForModule = load(combinedEdtForModuleModelInfo);

		assertNotNull(combinedEdtDefForModule.getModelCreationInfo().getDesignedModel());
		assertEquals(LifeCycleType.CURRENT, combinedEdtDefForModule.getLifeCycle());
		assertSize(4, combinedEdtDefForModule.getEnumDataType());

		final EnumDataType edtLeaf11 = findEdtInCombined(combinedEdtDefForModule, "cont1__leaf11");
		assertEquals(LifeCycleType.DEPRECATED, edtLeaf11.getLifeCycle());
		assertSize(3, edtLeaf11.getMember());

		final EnumDataTypeMember leaf11edtRed = findEnumMember(edtLeaf11, "RED");
		assertNull(leaf11edtRed.getNamespace());
		assertEquals(0, leaf11edtRed.getValue());
		final EnumDataTypeMember leaf11edtYellow = findEnumMember(edtLeaf11, "YELLOW");
		assertNull(leaf11edtYellow.getNamespace());
		assertEquals(1, leaf11edtYellow.getValue());
		final EnumDataTypeMember leaf11edtGreen = findEnumMember(edtLeaf11, "GREEN");
		assertNull(leaf11edtGreen.getNamespace());
		assertEquals(2, leaf11edtGreen.getValue());

		final EnumDataType edtLeaf12 = findEdtInCombined(combinedEdtDefForModule, "cont1__leaf12");
		assertEquals(LifeCycleType.CURRENT, edtLeaf12.getLifeCycle());
		assertSize(3, edtLeaf12.getMember());

		final EnumDataTypeMember leaf12edtRed = findEnumMember(edtLeaf12, "BLUE");
		final EnumDataTypeMember leaf12edtYellow = findEnumMember(edtLeaf12, "PINK");
		final EnumDataTypeMember leaf12edtGreen = findEnumMember(edtLeaf12, "PURPLE");
		assertEquals(995, leaf12edtRed.getValue());
		assertEquals(996, leaf12edtYellow.getValue());
		assertEquals(997, leaf12edtGreen.getValue());

		final EnumDataType edtLeaf13 = findEdtInCombined(combinedEdtDefForModule, "cont1__leaf13");
		assertSize(3, edtLeaf13.getMember());

		final EnumDataTypeMember leaf13edtRed = findEnumMember(edtLeaf13, "TEAL");
		final EnumDataTypeMember leaf13edtYellow = findEnumMember(edtLeaf13, "ORANGE");
		final EnumDataTypeMember leaf13edtGreen = findEnumMember(edtLeaf13, "RUBY");
		assertEquals(0, leaf13edtRed.getValue());
		assertEquals(1, leaf13edtYellow.getValue());
		assertEquals(2, leaf13edtGreen.getValue());

		final EnumDataType edtLeaf14 = findEdtInCombined(combinedEdtDefForModule, "cont1__leaf14__0");
		assertSize(3, edtLeaf14.getMember());

		final EnumDataTypeMember leaf14edtRed = findEnumMember(edtLeaf14, "BLACK");
		final EnumDataTypeMember leaf14edtYellow = findEnumMember(edtLeaf14, "WHITE");
		final EnumDataTypeMember leaf14edtGreen = findEnumMember(edtLeaf14, "GREY");
		assertEquals(0, leaf14edtRed.getValue());
		assertEquals(1, leaf14edtYellow.getValue());
		assertEquals(2, leaf14edtGreen.getValue());

		// -----------------------------------------------------

		final ModelInfo edtLeaf11ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf11", MODULE_XYZ_VERSION, null);
		final ModelInfo edtLeaf12ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf12", MODULE_XYZ_VERSION, null);
		final ModelInfo edtLeaf13ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf13", MODULE_XYZ_VERSION, null);
		final ModelInfo edtLeaf14ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS, "cont1__leaf14", MODULE_XYZ_VERSION, 0);

		assertModelDoesNotExist(edtLeaf11ModelInfo);
		assertModelDoesNotExist(edtLeaf12ModelInfo);
		assertModelDoesNotExist(edtLeaf13ModelInfo);
		assertModelDoesNotExist(edtLeaf14ModelInfo);

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(EnumRefType.class, leaf11.getType());
		assertEquals(edtLeaf11ModelInfo.toImpliedUrn(), ((EnumRefType) leaf11.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(EnumRefType.class, leaf12.getType());
		assertEquals(edtLeaf12ModelInfo.toImpliedUrn(), ((EnumRefType) leaf12.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(EnumRefType.class, leaf13.getType());
		assertEquals(edtLeaf13ModelInfo.toImpliedUrn(), ((EnumRefType) leaf13.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(UnionType.class, leaf14.getType());
		assertInstanceOf(EnumRefType.class, ((UnionType) leaf14.getType()).getMember().get(0));
		assertInstanceOf(StringType.class, ((UnionType) leaf14.getType()).getMember().get(1));
		assertEquals(edtLeaf14ModelInfo.toImpliedUrn(), ((EnumRefType) ((UnionType) leaf14.getType()).getMember().get(0)).getModelUrn());

		/*
		 * TODO: [COMBINED_EDT] Model Service support not ready yet; update the test code below once ready.
		 */

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		//		processAndDeployIntoModelService();
		//		
		//		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		//		assertTrue(cont1spec != null);
		//		
		//		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		//		assertTrue(attr11 != null);
		//		assertTrue(attr11.getDataTypeSpecification().getDataType() == DataType.ENUM_REF); 
		//		assertTrue(attr11.getDataTypeSpecification().getReferencedDataType().equals(edtLeaf11ModelInfo)); 
		//		
		//		assertValidValue(attr11, "RED");
		//		assertValidValue(attr11, "YELLOW");
		//		assertValidValue(attr11, "GREEN");
		//		
		//		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		//		assertTrue(attr12 != null);
		//		assertTrue(attr12.getDataTypeSpecification().getDataType() == DataType.ENUM_REF); 
		//		assertTrue(attr12.getDataTypeSpecification().getReferencedDataType().equals(edtLeaf12ModelInfo));
		//
		//		assertValidValue(attr12, "BLUE");
		//		assertValidValue(attr12, "PINK");
		//		assertValidValue(attr12, "PURPLE");
		//		
		//		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		//		assertTrue(attr13 != null);
		//		assertTrue(attr13.getDataTypeSpecification().getDataType() == DataType.ENUM_REF); 
		//		assertTrue(attr13.getDataTypeSpecification().getReferencedDataType().equals(edtLeaf13ModelInfo)); 
		//
		//		assertValidValue(attr13, "TEAL");
		//		assertValidValue(attr13, "ORANGE");
		//		assertValidValue(attr13, "RUBY");
		//		
		//		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		//		assertTrue(attr14 != null);
		//		assertTrue(attr14.getDataTypeSpecification().getDataType() == DataType.UNION);
		//		assertTrue(attr14.getDataTypeSpecification().getMembersDataTypeSpecification().size() == 2);
		//		assertTrue(attr14.getDataTypeSpecification().getMembersDataTypeSpecification().get(0).getDataType() == DataType.ENUM_REF);
		//		assertTrue(attr14.getDataTypeSpecification().getMembersDataTypeSpecification().get(0).getReferencedDataType().equals(edtLeaf14ModelInfo));
		//		
		//		assertValidValue(attr14, "BLACK");
		//		assertValidValue(attr14, "WHITE");
		//		assertValidValue(attr14, "GREY");
		//
		//		// --------------------------------------
		//		
		//		final EnumDataTypeSpecification leaf11EnumSpec = modelService.getTypedAccess().getEModelSpecification(edtLeaf11ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
		//		assertTrue(leaf11EnumSpec.getAllMembers().size() == 3);
		//		assertTrue(findEnumMember(leaf11EnumSpec, "RED") != null);
		//		assertTrue(findEnumMember(leaf11EnumSpec, "YELLOW") != null);
		//		assertTrue(findEnumMember(leaf11EnumSpec, "GREEN") != null);
		//		assertTrue(findEnumMember(leaf11EnumSpec, "RED").getMemberValue() == 0);
		//		assertTrue(findEnumMember(leaf11EnumSpec, "YELLOW").getMemberValue() == 1);
		//		assertTrue(findEnumMember(leaf11EnumSpec, "GREEN").getMemberValue() == 2);
		//
		//		final EnumDataTypeSpecification leaf12EnumSpec = modelService.getTypedAccess().getEModelSpecification(edtLeaf12ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
		//		assertTrue(leaf12EnumSpec.getAllMembers().size() == 3);
		//		assertTrue(findEnumMember(leaf12EnumSpec, "BLUE") != null);
		//		assertTrue(findEnumMember(leaf12EnumSpec, "PINK") != null);
		//		assertTrue(findEnumMember(leaf12EnumSpec, "PURPLE") != null);
		//		assertTrue(findEnumMember(leaf12EnumSpec, "BLUE").getMemberValue() == 995);
		//		assertTrue(findEnumMember(leaf12EnumSpec, "PINK").getMemberValue() == 996);
		//		assertTrue(findEnumMember(leaf12EnumSpec, "PURPLE").getMemberValue() == 997);
		//
		//		final EnumDataTypeSpecification leaf13EnumSpec = modelService.getTypedAccess().getEModelSpecification(edtLeaf13ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
		//		assertTrue(leaf13EnumSpec.getAllMembers().size() == 3);
		//		assertTrue(findEnumMember(leaf13EnumSpec, "TEAL") != null);
		//		assertTrue(findEnumMember(leaf13EnumSpec, "ORANGE") != null);
		//		assertTrue(findEnumMember(leaf13EnumSpec, "RUBY") != null);
		//		assertTrue(findEnumMember(leaf13EnumSpec, "TEAL").getMemberValue() == 0);
		//		assertTrue(findEnumMember(leaf13EnumSpec, "ORANGE").getMemberValue() == 1);
		//		assertTrue(findEnumMember(leaf13EnumSpec, "RUBY").getMemberValue() == 2);
		//		
		//		final EnumDataTypeSpecification leaf14EnumSpec = modelService.getTypedAccess().getEModelSpecification(edtLeaf14ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
		//		assertTrue(leaf14EnumSpec.getAllMembers().size() == 3);
		//		assertTrue(findEnumMember(leaf14EnumSpec, "BLACK") != null);
		//		assertTrue(findEnumMember(leaf14EnumSpec, "WHITE") != null);
		//		assertTrue(findEnumMember(leaf14EnumSpec, "GREY") != null);
		//		assertTrue(findEnumMember(leaf14EnumSpec, "BLACK").getMemberValue() == 0);
		//		assertTrue(findEnumMember(leaf14EnumSpec, "WHITE").getMemberValue() == 1);
		//		assertTrue(findEnumMember(leaf14EnumSpec, "GREY").getMemberValue() == 2);
	}
}
