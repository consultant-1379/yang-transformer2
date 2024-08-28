package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.datatypes;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collections;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.UnionType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.IdentityRefGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class LeafrefTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-datatypes/";

	private static final String MODULE_NS_ONE = "urn#test#leafref-one";
	private static final String MODULE_XYZ_VERSION_ONE = "2021.7.30";

	private static final String MODULE_NS_TWO = "urn/test/leafref-two";
	private static final String MODULE_XYZ_VERSION_TWO = "2021.3.9";

	@Test
	public void test___leafref_type() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P015_INVALID_SYNTAX_IN_DOCUMENT.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P019_MISSING_REQUIRED_CHILD_STATEMENT.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S329_LEAFREF_TARGET_IS_LEAFREF.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S441_INVALID_XPATH.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "leafref"));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo identity1ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "one-1", MODULE_XYZ_VERSION_ONE);
		assertModelExists(identity1ModelInfo);

		final ModelInfo leaflist13EnumModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS_ONE, "list1__leaflist13", MODULE_XYZ_VERSION_ONE, null);
		assertModelExists(leaflist13EnumModelInfo);

		final ModelInfo leaflist17EnumModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE_NS_ONE, "list1__leaflist17", MODULE_XYZ_VERSION_ONE, null);
		assertModelExists(leaflist17EnumModelInfo);

		final ModelInfo leaflist17ExtEnumModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE_NS_ONE, "list1__leaflist17", null);
		assertModelExists(leaflist17ExtEnumModelInfo);

		final ModelInfo list1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "list1", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition list1 = load(list1modelInfo);

		final PrimaryTypeAttribute leaflist11 = findAttribute(list1, "leaflist11");
		assertInstanceOf(ListType.class, leaflist11.getType());
		assertInstanceOf(EnumRefType.class, ((CollectionType) leaflist11.getType()).getCollectionValuesType());
		assertEquals(identity1ModelInfo.toImpliedUrn(), ((EnumRefType) ((CollectionType) leaflist11.getType()).getCollectionValuesType()).getModelUrn());

		final PrimaryTypeAttribute leaflist12 = findAttribute(list1, "leaflist12");
		assertInstanceOf(ListType.class, leaflist12.getType());
		assertInstanceOf(IntegerType.class, ((CollectionType) leaflist12.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaflist13 = findAttribute(list1, "leaflist13");
		assertInstanceOf(ListType.class, leaflist13.getType());
		assertInstanceOf(EnumRefType.class, ((CollectionType) leaflist13.getType()).getCollectionValuesType());
		assertEquals(leaflist13EnumModelInfo.toImpliedUrn(), ((EnumRefType) ((CollectionType) leaflist13.getType()).getCollectionValuesType()).getModelUrn());

		final PrimaryTypeAttribute leaflist14 = findAttribute(list1, "leaflist14");
		assertInstanceOf(ListType.class, leaflist14.getType());
		assertInstanceOf(StringType.class, ((CollectionType) leaflist14.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaflist15 = findAttribute(list1, "leaflist15");
		assertInstanceOf(ListType.class, leaflist15.getType());
		assertInstanceOf(BooleanType.class, ((CollectionType) leaflist15.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaflist16 = findAttribute(list1, "leaflist16");
		assertInstanceOf(ListType.class, leaflist16.getType());
		assertInstanceOf(UnionType.class, ((CollectionType) leaflist16.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaflist17 = findAttribute(list1, "leaflist17");
		assertInstanceOf(ListType.class, leaflist17.getType());
		assertInstanceOf(EnumRefType.class, ((CollectionType) leaflist17.getType()).getCollectionValuesType());
		assertEquals(leaflist17EnumModelInfo.toImpliedUrn(), ((EnumRefType) ((CollectionType) leaflist17.getType()).getCollectionValuesType()).getModelUrn());

		final ModelInfo list1extModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_ONE, "list1");
		final PrimaryTypeExtensionDefinition list1ext = load(list1extModelInfo);

		final PrimaryTypeAttribute leaflist17ext = findAttribute(list1ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute(), "leaflist17");
		assertInstanceOf(ListType.class, leaflist17ext.getType());
		assertInstanceOf(EnumRefType.class, ((CollectionType) leaflist17ext.getType()).getCollectionValuesType());
		assertEquals(leaflist17ExtEnumModelInfo.toImpliedUrn(), ((EnumRefType) ((CollectionType) leaflist17ext.getType()).getCollectionValuesType()).getModelUrn());

		final PrimaryTypeAttribute leaflist19 = findAttribute(list1ext.getPrimaryTypeExtension().getPrimaryTypeAttribute(), "leaflist19");
		assertInstanceOf(ListType.class, leaflist19.getType());
		assertInstanceOf(DoubleType.class, ((CollectionType) leaflist19.getType()).getCollectionValuesType());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont2", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertInstanceOf(EnumRefType.class, leaf21.getType());
		assertEquals(identity1ModelInfo.toImpliedUrn(), ((EnumRefType) leaf21.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf22 = findAttribute(cont2, "leaf22");
		assertInstanceOf(IntegerType.class, leaf22.getType());

		final PrimaryTypeAttribute leaf23 = findAttribute(cont2, "leaf23");
		assertInstanceOf(EnumRefType.class, leaf23.getType());
		assertEquals(leaflist13EnumModelInfo.toImpliedUrn(), ((EnumRefType) leaf23.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf24 = findAttribute(cont2, "leaf24");
		assertInstanceOf(StringType.class, leaf24.getType());

		final PrimaryTypeAttribute leaf25 = findAttribute(cont2, "leaf25");
		assertInstanceOf(BooleanType.class, leaf25.getType());

		final PrimaryTypeAttribute leaf26 = findAttribute(cont2, "leaf26");
		assertInstanceOf(UnionType.class, leaf26.getType());
		assertInstanceOf(ShortType.class, ((UnionType) leaf26.getType()).getMember().get(0));
		assertInstanceOf(StringType.class, ((UnionType) leaf26.getType()).getMember().get(1));

		final PrimaryTypeAttribute leaf27 = findAttribute(cont2, "leaf27");
		assertInstanceOf(EnumRefType.class, leaf27.getType());
		assertEquals(leaflist17EnumModelInfo.toImpliedUrn(), ((EnumRefType) leaf27.getType()).getModelUrn());

		final ModelInfo cont2extModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_ONE, "cont2");
		final PrimaryTypeExtensionDefinition cont2ext = load(cont2extModelInfo);

		final PrimaryTypeAttribute leaf27ext = findAttribute(cont2ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute(), "leaf27");
		assertInstanceOf(EnumRefType.class, leaf27ext.getType());
		assertEquals(leaflist17ExtEnumModelInfo.toImpliedUrn(), ((EnumRefType) leaf27ext.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf29 = findAttribute(cont2ext.getPrimaryTypeExtension().getPrimaryTypeAttribute(), "leaf29");
		assertInstanceOf(DoubleType.class, leaf29.getType());

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont3", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);

		final PrimaryTypeAttribute leaf31 = findAttribute(cont3, "leaf31");
		assertInstanceOf(StringType.class, leaf31.getType());

		final PrimaryTypeAttribute leaf32 = findAttribute(cont3, "leaf32");
		assertInstanceOf(StringType.class, leaf32.getType());

		final PrimaryTypeAttribute leaf33 = findAttribute(cont3, "leaf33");
		assertInstanceOf(StringType.class, leaf33.getType());

		final PrimaryTypeAttribute leaf34 = findAttribute(cont3, "leaf34");
		assertInstanceOf(StringType.class, leaf34.getType());

		final PrimaryTypeAttribute leaf35 = findAttribute(cont3, "leaf35");
		assertInstanceOf(StringType.class, leaf35.getType());

		final PrimaryTypeAttribute leaf36 = findAttribute(cont3, "leaf36");
		assertInstanceOf(StringType.class, leaf36.getType());

		final PrimaryTypeAttribute leaf37 = findAttribute(cont3, "leaf37");
		assertInstanceOf(StringType.class, leaf37.getType());

		final PrimaryTypeAttribute leaf38 = findAttribute(cont3, "leaf38");
		assertInstanceOf(StringType.class, leaf38.getType());

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont5", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont5 = load(cont5modelInfo);

		final PrimaryTypeAttribute leaf51 = findAttribute(cont5, "leaf51");
		assertInstanceOf(EnumRefType.class, leaf51.getType());
		assertEquals(identity1ModelInfo.toImpliedUrn(), ((EnumRefType) leaf51.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf52 = findAttribute(cont5, "leaf52");
		assertInstanceOf(EnumRefType.class, leaf52.getType());
		assertEquals(leaflist13EnumModelInfo.toImpliedUrn(), ((EnumRefType) leaf52.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf53 = findAttribute(cont5, "leaf53");	// leafref -> leafref -> enum
		assertInstanceOf(EnumRefType.class, leaf53.getType());
		assertEquals(leaflist13EnumModelInfo.toImpliedUrn(), ((EnumRefType) leaf53.getType()).getModelUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification list1spec = modelService.getTypedAccess().getEModelSpecification(list1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(list1spec, "leaflist11");
		assertEquals(DataType.LIST, attr11.getDataTypeSpecification().getDataType());
		assertEquals(DataType.ENUM_REF, attr11.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		assertEquals(identity1ModelInfo, attr11.getDataTypeSpecification().getValuesDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(list1spec, "leaflist12");
		assertEquals(DataType.LIST, attr12.getDataTypeSpecification().getDataType());
		assertEquals(DataType.INTEGER, attr12.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(list1spec, "leaflist13");
		assertEquals(DataType.LIST, attr13.getDataTypeSpecification().getDataType());
		assertEquals(DataType.ENUM_REF, attr13.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		assertEquals(leaflist13EnumModelInfo, attr13.getDataTypeSpecification().getValuesDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(list1spec, "leaflist14");
		assertEquals(DataType.LIST, attr14.getDataTypeSpecification().getDataType());
		assertEquals(DataType.STRING, attr14.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr15 = findAttribute(list1spec, "leaflist15");
		assertEquals(DataType.LIST, attr15.getDataTypeSpecification().getDataType());
		assertEquals(DataType.BOOLEAN, attr15.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr16 = findAttribute(list1spec, "leaflist16");
		assertEquals(DataType.LIST, attr16.getDataTypeSpecification().getDataType());
		assertEquals(DataType.UNION, attr16.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr17 = findAttribute(list1spec, "leaflist17");
		assertEquals(DataType.LIST, attr17.getDataTypeSpecification().getDataType());
		assertEquals(DataType.ENUM_REF, attr17.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		assertEquals(leaflist17ExtEnumModelInfo, attr17.getDataTypeSpecification().getValuesDataTypeSpecification().getReferencedDataType());
		assertValidValue(attr17, Collections.singletonList("MARS"));
		assertInvalidValue(attr17, Collections.singletonList("TUESDAY"));

		final PrimaryTypeAttributeSpecification attr19 = findAttribute(list1spec, "leaflist19");
		assertEquals(DataType.LIST, attr19.getDataTypeSpecification().getDataType());
		assertEquals(DataType.DOUBLE, attr19.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		assertEquals(MODULE_NS_TWO, attr19.getNamespace());

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertEquals(DataType.ENUM_REF, attr21.getDataTypeSpecification().getDataType());
		assertEquals(identity1ModelInfo, attr21.getDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr22 = findAttribute(cont2spec, "leaf22");
		assertEquals(DataType.INTEGER, attr22.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr23 = findAttribute(cont2spec, "leaf23");
		assertEquals(DataType.ENUM_REF, attr23.getDataTypeSpecification().getDataType());
		assertEquals(leaflist13EnumModelInfo, attr23.getDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr24 = findAttribute(cont2spec, "leaf24");
		assertEquals(DataType.STRING, attr24.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr25 = findAttribute(cont2spec, "leaf25");
		assertEquals(DataType.BOOLEAN, attr25.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr26 = findAttribute(cont2spec, "leaf26");
		assertEquals(DataType.UNION, attr26.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr27 = findAttribute(cont2spec, "leaf27");
		assertEquals(DataType.ENUM_REF, attr27.getDataTypeSpecification().getDataType());
		assertEquals(leaflist17ExtEnumModelInfo, attr27.getDataTypeSpecification().getReferencedDataType());
		assertValidValue(attr27, "MARS");
		assertInvalidValue(attr27, "TUESDAY");

		final PrimaryTypeAttributeSpecification attr29 = findAttribute(cont2spec, "leaf29");
		assertEquals(DataType.DOUBLE, attr29.getDataTypeSpecification().getDataType());
		assertEquals(MODULE_NS_TWO, attr29.getNamespace());

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont3spec, "leaf31");
		assertEquals(DataType.STRING, attr31.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr32 = findAttribute(cont3spec, "leaf32");
		assertEquals(DataType.STRING, attr32.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr33 = findAttribute(cont3spec, "leaf33");
		assertEquals(DataType.STRING, attr33.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr34 = findAttribute(cont3spec, "leaf34");
		assertEquals(DataType.STRING, attr34.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr35 = findAttribute(cont3spec, "leaf35");
		assertEquals(DataType.STRING, attr35.getDataTypeSpecification().getDataType());

		final HierarchicalPrimaryTypeSpecification cont5spec = modelService.getTypedAccess().getEModelSpecification(cont5modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr51 = findAttribute(cont5spec, "leaf51");
		assertEquals(DataType.ENUM_REF, attr51.getDataTypeSpecification().getDataType());
		assertEquals(identity1ModelInfo, attr51.getDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr52 = findAttribute(cont5spec, "leaf52");
		assertEquals(DataType.ENUM_REF, attr52.getDataTypeSpecification().getDataType());
		assertEquals(leaflist13EnumModelInfo, attr52.getDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr53 = findAttribute(cont5spec, "leaf53");
		assertEquals(DataType.ENUM_REF, attr53.getDataTypeSpecification().getDataType());
		assertEquals(leaflist13EnumModelInfo, attr53.getDataTypeSpecification().getReferencedDataType());
	}

}
