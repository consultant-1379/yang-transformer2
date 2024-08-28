package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.CollectionSizeConstraint;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.DoubleMinMaxRange;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.DoubleValueRangeConstraint;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.MaxValue;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.MinMaxRange;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.MinValue;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.StringContentsConstraint;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.StringLengthConstraint;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.ValueRangeConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringContentsConstraintType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class BoundariesTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-datatypes/";

	private static final String MODULE_NS_BOU = "urn::test::boundaries";
	private static final String MODULE_XYZ_VERSION_BOU = "2021.4.21";

	@Test
	public void test___data_type_boundaries(){

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P056_CONSTRAINT_NARROWED.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P057_DATA_TYPE_CHANGED.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S393_CANNOT_ORDER_ON_STATE_DATA.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "boundaries"));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_BOU, "cont1", MODULE_XYZ_VERSION_BOU);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(StringType.class, leaf11.getType());
		assertNotNull(((StringType) leaf11.getType()).getStringLengthConstraint());
		assertEmpty(((StringType) leaf11.getType()).getStringLengthConstraint().getMinMaxLength());
		assertNull(((StringType) leaf11.getType()).getStringLengthConstraint().getMinLength());
		assertEquals(20L, ((StringType) leaf11.getType()).getStringLengthConstraint().getMaxLength().getValue());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(StringType.class, leaf12.getType());
		assertNotNull(((StringType) leaf12.getType()).getStringLengthConstraint());
		assertEmpty(((StringType) leaf12.getType()).getStringLengthConstraint().getMinMaxLength());
		assertEquals(10L, ((StringType) leaf12.getType()).getStringLengthConstraint().getMinLength().getValue());
		assertNull(((StringType) leaf12.getType()).getStringLengthConstraint().getMaxLength());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(StringType.class, leaf13.getType());
		assertNotNull(((StringType) leaf13.getType()).getStringLengthConstraint());
		assertNull(((StringType) leaf13.getType()).getStringLengthConstraint().getMinLength());
		assertNull(((StringType) leaf13.getType()).getStringLengthConstraint().getMaxLength());
		assertSize(1, ((StringType) leaf13.getType()).getStringLengthConstraint().getMinMaxLength());
		assertEquals(10L, ((StringType) leaf13.getType()).getStringLengthConstraint().getMinMaxLength().get(0).getMin());
		assertEquals(20L, ((StringType) leaf13.getType()).getStringLengthConstraint().getMinMaxLength().get(0).getMax());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(StringType.class, leaf14.getType());
		assertNotNull(((StringType) leaf14.getType()).getStringContentsConstraint());
		assertEquals(StringContentsConstraintType.REGEX, ((StringType) leaf14.getType()).getStringContentsConstraint().getType());
		assertEquals("ab*c", ((StringType) leaf14.getType()).getStringContentsConstraint().getValue());

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_BOU, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);

		final PrimaryTypeAttribute leaf11ext = findReplacedAttribute(cont1ext, "leaf11");
		assertInstanceOf(StringType.class, leaf11ext.getType());
		assertNotNull(((StringType) leaf11ext.getType()).getStringLengthConstraint());
		assertEmpty(((StringType) leaf11ext.getType()).getStringLengthConstraint().getMinMaxLength());
		assertNull(((StringType) leaf11ext.getType()).getStringLengthConstraint().getMinLength());
		assertEquals(25L, ((StringType) leaf11ext.getType()).getStringLengthConstraint().getMaxLength().getValue());

		final PrimaryTypeAttribute leaf12ext = findReplacedAttribute(cont1ext, "leaf12");
		assertInstanceOf(StringType.class, leaf12ext.getType());
		assertNotNull(((StringType) leaf12ext.getType()).getStringLengthConstraint());
		assertEmpty(((StringType) leaf12ext.getType()).getStringLengthConstraint().getMinMaxLength());
		assertEquals(5L, ((StringType) leaf12ext.getType()).getStringLengthConstraint().getMinLength().getValue());
		assertNull(((StringType) leaf12ext.getType()).getStringLengthConstraint().getMaxLength());

		final PrimaryTypeAttribute leaf13ext = findReplacedAttribute(cont1ext, "leaf13");
		assertInstanceOf(StringType.class, leaf13ext.getType());
		assertNotNull(((StringType) leaf13ext.getType()).getStringLengthConstraint());
		assertNull(((StringType) leaf13ext.getType()).getStringLengthConstraint().getMinLength());
		assertNull(((StringType) leaf13ext.getType()).getStringLengthConstraint().getMaxLength());
		assertSize(1, ((StringType) leaf13ext.getType()).getStringLengthConstraint().getMinMaxLength());
		assertEquals(8L, ((StringType) leaf13ext.getType()).getStringLengthConstraint().getMinMaxLength().get(0).getMin());
		assertEquals(22L, ((StringType) leaf13ext.getType()).getStringLengthConstraint().getMinMaxLength().get(0).getMax());

		final PrimaryTypeAttribute leaf14ext = findReplacedAttribute(cont1ext, "leaf14");
		assertInstanceOf(StringType.class, leaf14ext.getType());
		assertNotNull(((StringType) leaf14ext.getType()).getStringContentsConstraint());
		assertEquals(StringContentsConstraintType.REGEX, ((StringType) leaf14ext.getType()).getStringContentsConstraint().getType());
		assertEquals("def*", ((StringType) leaf14ext.getType()).getStringContentsConstraint().getValue());

		// ----------------------------------------------------------

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_BOU, "cont2", MODULE_XYZ_VERSION_BOU);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertInstanceOf(IntegerType.class, leaf21.getType());
		assertNotNull(((IntegerType) leaf21.getType()).getValueRangeConstraint());
		assertEmpty(((IntegerType) leaf21.getType()).getValueRangeConstraint().getMinMaxRange());
		assertNull(((IntegerType) leaf21.getType()).getValueRangeConstraint().getMinValue());
		assertEquals(20L, ((IntegerType) leaf21.getType()).getValueRangeConstraint().getMaxValue().getValue());

		final PrimaryTypeAttribute leaf22 = findAttribute(cont2, "leaf22");
		assertInstanceOf(IntegerType.class, leaf22.getType());
		assertNotNull(((IntegerType) leaf22.getType()).getValueRangeConstraint());
		assertEmpty(((IntegerType) leaf22.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(10L, ((IntegerType) leaf22.getType()).getValueRangeConstraint().getMinValue().getValue());
		assertNull(((IntegerType) leaf22.getType()).getValueRangeConstraint().getMaxValue());

		final PrimaryTypeAttribute leaf23 = findAttribute(cont2, "leaf23");
		assertInstanceOf(IntegerType.class, leaf23.getType());
		assertNotNull(((IntegerType) leaf23.getType()).getValueRangeConstraint());
		assertSize(1, ((IntegerType) leaf23.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(10L, ((IntegerType) leaf23.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(20L, ((IntegerType) leaf23.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf24 = findAttribute(cont2, "leaf24");
		assertInstanceOf(LongType.class, leaf24.getType());
		assertTrue(((LongType) leaf24.getType()).isUnsigned());

		final ModelInfo cont2ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_BOU, "cont2");
		final PrimaryTypeExtensionDefinition cont2ext = load(cont2ExtensionModelInfo);

		final PrimaryTypeAttribute leaf21ext = findReplacedAttribute(cont2ext, "leaf21");
		assertInstanceOf(IntegerType.class, leaf21ext.getType());
		assertNotNull(((IntegerType) leaf21ext.getType()).getValueRangeConstraint());
		assertEmpty(((IntegerType) leaf21ext.getType()).getValueRangeConstraint().getMinMaxRange());
		assertNull(((IntegerType) leaf21ext.getType()).getValueRangeConstraint().getMinValue());
		assertEquals(25L, ((IntegerType) leaf21ext.getType()).getValueRangeConstraint().getMaxValue().getValue());

		final PrimaryTypeAttribute leaf22ext = findReplacedAttribute(cont2ext, "leaf22");
		assertInstanceOf(IntegerType.class, leaf22ext.getType());
		assertNotNull(((IntegerType) leaf22ext.getType()).getValueRangeConstraint());
		assertEmpty(((IntegerType) leaf22ext.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(12L, ((IntegerType) leaf22ext.getType()).getValueRangeConstraint().getMinValue().getValue());
		assertNull(((IntegerType) leaf22ext.getType()).getValueRangeConstraint().getMaxValue());

		final PrimaryTypeAttribute leaf23ext = findReplacedAttribute(cont2ext, "leaf23");
		assertInstanceOf(IntegerType.class, leaf23ext.getType());
		assertNotNull(((IntegerType) leaf23ext.getType()).getValueRangeConstraint());
		assertSize(1, ((IntegerType) leaf23ext.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(8L, ((IntegerType) leaf23ext.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(22L, ((IntegerType) leaf23ext.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf24ext = findReplacedAttribute(cont2ext, "leaf24");
		assertInstanceOf(LongType.class, leaf24ext.getType());
		assertFalse(((LongType) leaf24ext.getType()).isUnsigned());

		// ----------------------------------------------------------

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_BOU, "cont3", MODULE_XYZ_VERSION_BOU);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);

		final PrimaryTypeAttribute leaflist31 = findAttribute(cont3, "leaflist31");
		assertHasNotMeta(Constants.META_USER_ORDERED, leaflist31);
		assertInstanceOf(ListType.class, leaflist31.getType());
		assertNotNull(((ListType) leaflist31.getType()).getCollectionUniquenessConstraint());

		final PrimaryTypeAttribute leaflist32 = findAttribute(cont3, "leaflist32");
		assertHasMeta(Constants.META_USER_ORDERED, leaflist32);
		assertInstanceOf(ListType.class, leaflist32.getType());
		assertNotNull(((ListType) leaflist32.getType()).getCollectionUniquenessConstraint());

		final PrimaryTypeAttribute leaflist33 = findAttribute(cont3, "leaflist33");
		assertInstanceOf(ListType.class, leaflist33.getType());
		assertNotNull(((ListType) leaflist33.getType()).getCollectionUniquenessConstraint());
		assertInstanceOf(EnumRefType.class, ((ListType) leaflist33.getType()).getCollectionValuesType());

		final ModelInfo cont3ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_BOU, "cont3");
		final PrimaryTypeExtensionDefinition cont3ext = load(cont3ExtensionModelInfo);

		assertSize(3, cont3ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaflist31ext = findReplacedAttribute(cont3ext, "leaflist31");
		assertInstanceOf(ListType.class, leaflist31ext.getType());
		assertNull(((ListType) leaflist31ext.getType()).getCollectionUniquenessConstraint());

		final PrimaryTypeAttribute leaflist32ext = findReplacedAttribute(cont3ext, "leaflist32");
		assertInstanceOf(ListType.class, leaflist32ext.getType());
		assertInstanceOf(BooleanType.class, ((ListType) leaflist32ext.getType()).getCollectionValuesType());

		final PrimaryTypeAttribute leaflist33ext = findReplacedAttribute(cont3ext, "leaflist33");
		assertInstanceOf(ListType.class, leaflist33ext.getType());
		assertNotNull(((ListType) leaflist33ext.getType()).getCollectionUniquenessConstraint());
		assertInstanceOf(StringType.class, ((ListType) leaflist33ext.getType()).getCollectionValuesType());

		// ----------------------------------------------------------

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_BOU, "cont4", MODULE_XYZ_VERSION_BOU);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);

		final PrimaryTypeAttribute leaf41 = findAttribute(cont4, "leaf41");
		assertInstanceOf(DoubleType.class, leaf41.getType());
		assertNotNull(((DoubleType) leaf41.getType()).getValueRangeConstraint());
		assertSize(1, ((DoubleType) leaf41.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(20.2d, ((DoubleType) leaf41.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax(), 0.01d);

		final PrimaryTypeAttribute leaf42 = findAttribute(cont4, "leaf42");
		assertInstanceOf(DoubleType.class, leaf42.getType());
		assertNotNull(((DoubleType) leaf42.getType()).getValueRangeConstraint());
		assertSize(1, ((DoubleType) leaf42.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(10.1d, ((DoubleType) leaf42.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin(), 0.01d);

		final PrimaryTypeAttribute leaf43 = findAttribute(cont4, "leaf43");
		assertInstanceOf(DoubleType.class, leaf43.getType());
		assertNotNull(((DoubleType) leaf43.getType()).getValueRangeConstraint());
		assertSize(1, ((DoubleType) leaf43.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(10.1d, ((DoubleType) leaf43.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin(), 0.01d);
		assertEquals(20.2d, ((DoubleType) leaf43.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax(), 0.01d);

		final ModelInfo cont4ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_BOU, "cont4");
		final PrimaryTypeExtensionDefinition cont4ext = load(cont4ExtensionModelInfo);
		assertSize(3, cont4ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf41ext = findReplacedAttribute(cont4ext, "leaf41");
		assertInstanceOf(DoubleType.class, leaf41ext.getType());
		assertNotNull(((DoubleType) leaf41ext.getType()).getValueRangeConstraint());
		assertSize(1, ((DoubleType) leaf41ext.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(22.4d, ((DoubleType) leaf41ext.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax(), 0.01d);

		final PrimaryTypeAttribute leaf42ext = findReplacedAttribute(cont4ext, "leaf42");
		assertInstanceOf(DoubleType.class, leaf42ext.getType());
		assertNotNull(((DoubleType) leaf42ext.getType()).getValueRangeConstraint());
		assertSize(1, ((DoubleType) leaf42ext.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(13.3d, ((DoubleType) leaf42ext.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin(), 0.01d);

		final PrimaryTypeAttribute leaf43ext = findReplacedAttribute(cont4ext, "leaf43");
		assertInstanceOf(DoubleType.class, leaf43ext.getType());
		assertNotNull(((DoubleType) leaf43ext.getType()).getValueRangeConstraint());
		assertSize(1, ((DoubleType) leaf43ext.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(13.3d, ((DoubleType) leaf43ext.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin(), 0.01d);
		assertEquals(22.4d, ((DoubleType) leaf43ext.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax(), 0.01d);

		// ----------------------------------------------------------

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_BOU, "cont5", MODULE_XYZ_VERSION_BOU);
		final PrimaryTypeDefinition cont5 = load(cont5modelInfo);

		final PrimaryTypeAttribute leaflist51 = findAttribute(cont5, "leaflist51");
		assertFalse(leaflist51.isMandatory());
		assertInstanceOf(ListType.class, leaflist51.getType());
		assertEquals(5L, ((ListType) leaflist51.getType()).getCollectionSizeConstraint().getMaxSize().getValue());

		final PrimaryTypeAttribute leaflist52 = findAttribute(cont5, "leaflist52");
		assertTrue(leaflist52.isMandatory());
		assertInstanceOf(ListType.class, leaflist52.getType());
		assertEquals(2L, ((ListType) leaflist52.getType()).getCollectionSizeConstraint().getMinSize().getValue());

		final PrimaryTypeAttribute leaflist53 = findAttribute(cont5, "leaflist53");
		assertTrue(leaflist53.isMandatory());
		assertInstanceOf(ListType.class, leaflist53.getType());
		assertEquals(2L, ((ListType) leaflist53.getType()).getCollectionSizeConstraint().getMinMaxSize().get(0).getMin());
		assertEquals(5L, ((ListType) leaflist53.getType()).getCollectionSizeConstraint().getMinMaxSize().get(0).getMax());

		final ModelInfo cont5ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_BOU, "cont5");
		final PrimaryTypeExtensionDefinition cont5ext = load(cont5ExtensionModelInfo);

		final PrimaryTypeAttribute leaflist51ext = findReplacedAttribute(cont5ext, "leaflist51");
		assertFalse(leaflist51ext.isMandatory());
		assertInstanceOf(ListType.class, leaflist51ext.getType());
		assertEquals(7L, ((ListType) leaflist51ext.getType()).getCollectionSizeConstraint().getMaxSize().getValue());

		final PrimaryTypeAttribute leaflist52ext = findReplacedAttribute(cont5ext, "leaflist52");
		assertTrue(leaflist52ext.isMandatory());
		assertInstanceOf(ListType.class, leaflist52ext.getType());
		assertEquals(3L, ((ListType) leaflist52ext.getType()).getCollectionSizeConstraint().getMinSize().getValue());

		final PrimaryTypeAttribute leaflist53ext = findReplacedAttribute(cont5ext, "leaflist53");
		assertTrue(leaflist53ext.isMandatory());
		assertInstanceOf(ListType.class, leaflist53ext.getType());
		assertEquals(3L, ((ListType) leaflist53ext.getType()).getCollectionSizeConstraint().getMinMaxSize().get(0).getMin());
		assertEquals(7L, ((ListType) leaflist53ext.getType()).getCollectionSizeConstraint().getMinMaxSize().get(0).getMax());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");

		assertEquals(DataType.STRING, attr11.getDataTypeSpecification().getDataType());
		assertSize(1, attr11.getDataTypeSpecification().getConstraints());
		final StringLengthConstraint slc11 = (StringLengthConstraint) attr11.getDataTypeSpecification().getConstraints().iterator().next();
		final MaxValue maxValue11 = (MaxValue) slc11.getAllowedLength().iterator().next();
		assertEquals(25L, maxValue11.getMaxValueConstraint());
		assertValidValue(attr11, "This has fewer than 25");
		assertInvalidValue(attr11, "This has definitely more than 25 chars");

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");

		assertEquals(DataType.STRING, attr12.getDataTypeSpecification().getDataType());
		assertSize(1, attr12.getDataTypeSpecification().getConstraints());
		final StringLengthConstraint slc12 = (StringLengthConstraint) attr12.getDataTypeSpecification().getConstraints().iterator().next();
		final MinValue minValue12 = (MinValue) slc12.getAllowedLength().iterator().next();
		assertEquals(5L, minValue12.getMinValueConstraint());
		assertValidValue(attr12, "five!");
		assertInvalidValue(attr12, "four");

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");

		assertEquals(DataType.STRING, attr13.getDataTypeSpecification().getDataType());
		assertSize(1, attr13.getDataTypeSpecification().getConstraints());
		final StringLengthConstraint slc13 = (StringLengthConstraint) attr13.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range13 = (MinMaxRange) slc13.getAllowedLength().iterator().next();
		assertEquals(8L, range13.getMinValue());
		assertEquals(22L, range13.getMaxValue());
		assertValidValue(attr13, "8 or more");
		assertInvalidValue(attr13, "A lot more than 22 characters here!");

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");

		assertEquals(DataType.STRING, attr14.getDataTypeSpecification().getDataType());
		assertSize(1, attr14.getDataTypeSpecification().getConstraints());
		final StringContentsConstraint scc14 = (StringContentsConstraint) attr14.getDataTypeSpecification().getConstraints().iterator().next();
		assertEquals("def*", scc14.getValue());

		// ----------------------------------------------------------

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");

		assertEquals(DataType.INTEGER, attr21.getDataTypeSpecification().getDataType());
		assertSize(1, attr21.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc21 = (ValueRangeConstraint) attr21.getDataTypeSpecification().getConstraints().iterator().next();
		final MaxValue maxValue21 = (MaxValue) vrc21.getAllowedValues().iterator().next();
		assertEquals(25L, maxValue21.getMaxValueConstraint());
		assertValidValue(attr21, 25L);
		assertInvalidValue(attr21, 26L);

		final PrimaryTypeAttributeSpecification attr22 = findAttribute(cont2spec, "leaf22");

		assertEquals(DataType.INTEGER, attr22.getDataTypeSpecification().getDataType());
		assertSize(1, attr22.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc22 = (ValueRangeConstraint) attr22.getDataTypeSpecification().getConstraints().iterator().next();
		final MinValue minValue22 = (MinValue) vrc22.getAllowedValues().iterator().next();
		assertEquals(12L, minValue22.getMinValueConstraint());
		assertValidValue(attr22, 12L);
		assertInvalidValue(attr22, 11L);

		final PrimaryTypeAttributeSpecification attr23 = findAttribute(cont2spec, "leaf23");

		assertEquals(DataType.INTEGER, attr23.getDataTypeSpecification().getDataType());
		assertSize(1, attr23.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc23 = (ValueRangeConstraint) attr23.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range23 = (MinMaxRange) vrc23.getAllowedValues().iterator().next();
		assertEquals(8L, range23.getMinValue());
		assertEquals(22L, range23.getMaxValue());

		assertInvalidValue(attr23, 7L);
		assertValidValue(attr23, 8L);
		assertValidValue(attr23, 22L);
		assertInvalidValue(attr23, 23L);

		final PrimaryTypeAttributeSpecification attr24 = findAttribute(cont2spec, "leaf24");

		assertEquals(DataType.LONG, attr24.getDataTypeSpecification().getDataType());
		assertFalse(attr24.getDataTypeSpecification().isUnsigned());

		// ----------------------------------------------------------

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont3spec, "leaflist31");
		assertEquals(DataType.LIST, attr31.getDataTypeSpecification().getDataType());
		assertEmpty(attr31.getDataTypeSpecification().getConstraints());				// no uniqueness constraint, switched to config false
		assertEquals(DataType.INTEGER, attr31.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr32 = findAttribute(cont3spec, "leaflist32");
		assertEquals(DataType.LIST, attr32.getDataTypeSpecification().getDataType());
		assertEquals(DataType.BOOLEAN, attr32.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr33 = findAttribute(cont3spec, "leaflist33");
		assertEquals(DataType.LIST, attr33.getDataTypeSpecification().getDataType());
		assertSize(1, attr33.getDataTypeSpecification().getConstraints());
		assertEquals(DataType.STRING, attr33.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());

		// ----------------------------------------------------------

		final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr41 = findAttribute(cont4spec, "leaf41");

		assertEquals(DataType.DOUBLE, attr41.getDataTypeSpecification().getDataType());
		assertSize(1, attr41.getDataTypeSpecification().getConstraints());
		final DoubleValueRangeConstraint vrc41 = (DoubleValueRangeConstraint) attr41.getDataTypeSpecification().getConstraints().iterator().next();
		final DoubleMinMaxRange maxValue41 = (DoubleMinMaxRange) vrc41.getAllowedValues().iterator().next();
		assertEquals(22.4d, maxValue41.getMaxValue(), 0.01d);
		assertValidValue(attr41, 22.4d);
		assertInvalidValue(attr41, 22.5d);

		final PrimaryTypeAttributeSpecification attr42 = findAttribute(cont4spec, "leaf42");

		assertEquals(DataType.DOUBLE, attr42.getDataTypeSpecification().getDataType());
		assertSize(1, attr42.getDataTypeSpecification().getConstraints());
		final DoubleValueRangeConstraint vrc42 = (DoubleValueRangeConstraint) attr42.getDataTypeSpecification().getConstraints().iterator().next();
		final DoubleMinMaxRange maxValue42 = (DoubleMinMaxRange) vrc42.getAllowedValues().iterator().next();
		assertEquals(13.3d, maxValue42.getMinValue(), 0.01d);
		assertInvalidValue(attr42, 13.2999d);
		assertValidValue(attr42, 13.3d);

		final PrimaryTypeAttributeSpecification attr43 = findAttribute(cont4spec, "leaf43");

		assertEquals(DataType.DOUBLE, attr43.getDataTypeSpecification().getDataType());
		assertSize(1, attr43.getDataTypeSpecification().getConstraints());
		final DoubleValueRangeConstraint vrc43 = (DoubleValueRangeConstraint) attr43.getDataTypeSpecification().getConstraints().iterator().next();
		final DoubleMinMaxRange range43 = (DoubleMinMaxRange) vrc43.getAllowedValues().iterator().next();
		assertEquals(13.3d, range43.getMinValue(), 0.01d);
		assertEquals(22.4d, range43.getMaxValue(), 0.01d);

		assertInvalidValue(attr43, 13.2d);
		assertValidValue(attr43, 13.3d);
		assertValidValue(attr43, 22.4d);
		assertInvalidValue(attr43, 22.5d);

		// ----------------------------------------------------------

		final HierarchicalPrimaryTypeSpecification cont5spec = modelService.getTypedAccess().getEModelSpecification(cont5modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr51 = findAttribute(cont5spec, "leaflist51");
		assertFalse(attr51.isMandatory());
		assertEquals(DataType.LIST, attr51.getDataTypeSpecification().getDataType());
		final CollectionSizeConstraint csc51 = findConstraint(attr51.getDataTypeSpecification().getConstraints(), CollectionSizeConstraint.class);
		assertEquals(7L, ((MaxValue) csc51.getAllowedSize().iterator().next()).getMaxValueConstraint());

		final PrimaryTypeAttributeSpecification attr52 = findAttribute(cont5spec, "leaflist52");
		assertTrue(attr52.isMandatory());
		assertEquals(DataType.LIST, attr52.getDataTypeSpecification().getDataType());
		final CollectionSizeConstraint csc52 = findConstraint(attr52.getDataTypeSpecification().getConstraints(), CollectionSizeConstraint.class);
		assertEquals(3L, ((MinValue) csc52.getAllowedSize().iterator().next()).getMinValueConstraint());

		final PrimaryTypeAttributeSpecification attr53 = findAttribute(cont5spec, "leaflist53");
		assertTrue(attr53.isMandatory());
		assertEquals(DataType.LIST, attr53.getDataTypeSpecification().getDataType());
		final CollectionSizeConstraint csc53 = findConstraint(attr53.getDataTypeSpecification().getConstraints(), CollectionSizeConstraint.class);
		assertEquals(3L, ((MinMaxRange) csc53.getAllowedSize().iterator().next()).getMinValue());
		assertEquals(7L, ((MinMaxRange) csc53.getAllowedSize().iterator().next()).getMaxValue());

		final PrimaryTypeAttributeSpecification attr56 = findAttribute(cont5spec, "leaflist56");
		assertTrue(attr56.isMandatory());
		assertEquals(DataType.LIST, attr56.getDataTypeSpecification().getDataType());
		final CollectionSizeConstraint csc56 = findConstraint(attr56.getDataTypeSpecification().getConstraints(), CollectionSizeConstraint.class);
		assertEquals(3L, ((MinValue) csc56.getAllowedSize().iterator().next()).getMinValueConstraint());

		final PrimaryTypeAttributeSpecification attr57 = findAttribute(cont5spec, "leaflist57");
		assertFalse(attr57.isMandatory());
		assertEquals(DataType.LIST, attr57.getDataTypeSpecification().getDataType());
		final CollectionSizeConstraint csc57 = findConstraint(attr57.getDataTypeSpecification().getConstraints(), CollectionSizeConstraint.class);
		assertNull(csc57);
	}
}
