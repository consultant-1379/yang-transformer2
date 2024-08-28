package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.util.UnsignedLongEncoderDecoder;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.DoubleMinMaxRange;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.DoubleValueRangeConstraint;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.MaxValue;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.MinMaxRange;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.MinValue;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.StringContentsConstraint;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.StringLengthConstraint;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.ValueRangeConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BitsType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ByteType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringContentsConstraintType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class BasicDataTypesTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-datatypes/";

	private static final String MODULE_NS_UIT = "urn::test::unsigned-integer-types";
	private static final String MODULE_XYZ_VERSION_UIT = "2021.10.15";

	@Test
	public void test___unsigned_integer_types() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P053_INVALID_VALUE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "basic-data-types/unsigned-integer-types.yang"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(2, 0, 0, 0, 1, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_UIT, "cont1", MODULE_XYZ_VERSION_UIT);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(ShortType.class, leaf11.getType());
		assertNotNull(((ShortType) leaf11.getType()).getValueRangeConstraint());
		assertSize(1, ((ShortType) leaf11.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(0L, ((ShortType) leaf11.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(255L, ((ShortType) leaf11.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(IntegerType.class, leaf12.getType());
		assertNotNull(((IntegerType) leaf12.getType()).getValueRangeConstraint());
		assertSize(1, ((IntegerType) leaf12.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(0L, ((IntegerType) leaf12.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(65535L, ((IntegerType) leaf12.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(LongType.class, leaf13.getType());
		assertFalse(((LongType) leaf13.getType()).isUnsigned());
		assertNotNull(((LongType) leaf13.getType()).getValueRangeConstraint());
		assertSize(1, ((LongType) leaf13.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(0L, ((LongType) leaf13.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(4294967295L, ((LongType) leaf13.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(LongType.class, leaf14.getType());
		assertTrue(((LongType) leaf14.getType()).isUnsigned());
		assertNotNull(((LongType) leaf14.getType()).getValueRangeConstraint());
		assertSize(1, ((LongType) leaf14.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(0L, ((LongType) leaf14.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(UnsignedLongEncoderDecoder.stringToUnsignedLong("18446744073709551615"), (Long) ((LongType) leaf14.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_UIT, "cont2", MODULE_XYZ_VERSION_UIT);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertInstanceOf(IntegerType.class, leaf21.getType());
		assertNotNull(((IntegerType) leaf21.getType()).getValueRangeConstraint());
		assertSize(1, ((IntegerType) leaf21.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(0L, ((IntegerType) leaf21.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(65535L, ((IntegerType) leaf21.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf22 = findAttribute(cont2, "leaf22");
		assertInstanceOf(IntegerType.class, leaf22.getType());
		assertNotNull(((IntegerType) leaf22.getType()).getValueRangeConstraint());
		assertSize(1, ((IntegerType) leaf22.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(0L, ((IntegerType) leaf22.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(10L, ((IntegerType) leaf22.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf23 = findAttribute(cont2, "leaf23");
		assertInstanceOf(IntegerType.class, leaf23.getType());
		assertNotNull(((IntegerType) leaf23.getType()).getValueRangeConstraint());
		assertSize(1, ((IntegerType) leaf23.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(2000L, ((IntegerType) leaf23.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(65535L, ((IntegerType) leaf23.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf24 = findAttribute(cont2, "leaf24");
		assertInstanceOf(IntegerType.class, leaf24.getType());
		assertNotNull(((IntegerType) leaf24.getType()).getValueRangeConstraint());
		assertSize(1, ((IntegerType) leaf24.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(50L, ((IntegerType) leaf24.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(300L, ((IntegerType) leaf24.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf25 = findAttribute(cont2, "leaf25");
		assertInstanceOf(IntegerType.class, leaf25.getType());
		assertNotNull(((IntegerType) leaf25.getType()).getValueRangeConstraint());
		assertSize(2, ((IntegerType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(50L, ((IntegerType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(300L, ((IntegerType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());
		assertEquals(400L, ((IntegerType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange().get(1).getMin());
		assertEquals(750L, ((IntegerType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange().get(1).getMax());

		final PrimaryTypeAttribute leaf26 = findAttribute(cont2, "leaf26");
		assertInstanceOf(IntegerType.class, leaf26.getType());
		/*
		 * The error in the model would have resulted in no explicit constraint
		 * being generated - hence the default range constraint for unsigned type
		 * has been generated.
		 */
		assertSize(1, ((IntegerType) leaf26.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(0L, ((IntegerType) leaf26.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(65535L, ((IntegerType) leaf26.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.SHORT, attr11.getDataTypeSpecification().getDataType());
		assertSize(1, attr11.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc11 = (ValueRangeConstraint) attr11.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range11 = (MinMaxRange) vrc11.getAllowedValues().iterator().next();
		assertEquals(0L, range11.getMinValue());
		assertEquals(255L, range11.getMaxValue());

		assertInvalidValue(attr11, -1L);
		assertValidValue(attr11, 0L);
		assertValidValue(attr11, 255L);
		assertInvalidValue(attr11, 256L);

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.INTEGER, attr12.getDataTypeSpecification().getDataType());
		assertSize(1, attr12.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc12 = (ValueRangeConstraint) attr12.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range12 = (MinMaxRange) vrc12.getAllowedValues().iterator().next();
		assertEquals(0L, range12.getMinValue());
		assertEquals(65535L, range12.getMaxValue());

		assertInvalidValue(attr12, -1L);
		assertValidValue(attr12, 0L);
		assertValidValue(attr12, 65535L);
		assertInvalidValue(attr12, 65536L);

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.LONG, attr13.getDataTypeSpecification().getDataType());
		assertFalse(attr13.getDataTypeSpecification().isUnsigned());
		assertSize(1, attr13.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc13 = (ValueRangeConstraint) attr13.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range13 = (MinMaxRange) vrc13.getAllowedValues().iterator().next();
		assertEquals(0L, range13.getMinValue());
		assertEquals(4294967295L, range13.getMaxValue());

		assertInvalidValue(attr13, -1L);
		assertValidValue(attr13, 0L);
		assertValidValue(attr13, 4294967295L);
		assertInvalidValue(attr13, 4294967296L);

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.LONG, attr14.getDataTypeSpecification().getDataType());
		assertTrue(attr14.getDataTypeSpecification().isUnsigned());
		assertSize(1, attr14.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc14 = (ValueRangeConstraint) attr14.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range14 = (MinMaxRange) vrc14.getAllowedValues().iterator().next();
		assertEquals(0L, range14.getMinValue());
		assertEquals(UnsignedLongEncoderDecoder.stringToUnsignedLong("18446744073709551615"), (Long) range14.getMaxValue());

		assertValidValue(attr14, 0L);
		assertValidValue(attr14, UnsignedLongEncoderDecoder.stringToUnsignedLong("18446744073709551615"));

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertEquals(DataType.INTEGER, attr21.getDataTypeSpecification().getDataType());
		assertSize(1, attr21.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc21 = (ValueRangeConstraint) attr21.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range21 = (MinMaxRange) vrc21.getAllowedValues().iterator().next();
		assertEquals(0L, range21.getMinValue());
		assertEquals(65535L, range21.getMaxValue());

		assertInvalidValue(attr21, -1L);
		assertValidValue(attr21, 0L);
		assertValidValue(attr21, 65535L);
		assertInvalidValue(attr21, 65536L);

		final PrimaryTypeAttributeSpecification attr22 = findAttribute(cont2spec, "leaf22");
		assertEquals(DataType.INTEGER, attr22.getDataTypeSpecification().getDataType());
		assertSize(1, attr22.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc22 = (ValueRangeConstraint) attr22.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range22 = (MinMaxRange) vrc22.getAllowedValues().iterator().next();
		assertEquals(0L, range22.getMinValue());
		assertEquals(10L, range22.getMaxValue());

		assertInvalidValue(attr22, -1L);
		assertValidValue(attr22, 0L);
		assertValidValue(attr22, 10L);
		assertInvalidValue(attr22, 11L);

		final PrimaryTypeAttributeSpecification attr23 = findAttribute(cont2spec, "leaf23");
		assertEquals(DataType.INTEGER, attr23.getDataTypeSpecification().getDataType());
		assertSize(1, attr23.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc23 = (ValueRangeConstraint) attr23.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range23 = (MinMaxRange) vrc23.getAllowedValues().iterator().next();
		assertEquals(2000L, range23.getMinValue());
		assertEquals(65535L, range23.getMaxValue());

		assertInvalidValue(attr23, 1999L);
		assertValidValue(attr23, 2000L);
		assertValidValue(attr23, 65535L);
		assertInvalidValue(attr23, 65536L);

		final PrimaryTypeAttributeSpecification attr24 = findAttribute(cont2spec, "leaf24");
		assertEquals(DataType.INTEGER, attr24.getDataTypeSpecification().getDataType());
		assertSize(1, attr24.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc24 = (ValueRangeConstraint) attr24.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range24 = (MinMaxRange) vrc24.getAllowedValues().iterator().next();
		assertEquals(50L, range24.getMinValue());
		assertEquals(300L, range24.getMaxValue());

		assertInvalidValue(attr24, 49L);
		assertValidValue(attr24, 50L);
		assertValidValue(attr24, 300L);
		assertInvalidValue(attr24, 301L);

		final PrimaryTypeAttributeSpecification attr25 = findAttribute(cont2spec, "leaf25");
		assertEquals(DataType.INTEGER, attr25.getDataTypeSpecification().getDataType());
		assertSize(1, attr25.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc25 = (ValueRangeConstraint) attr25.getDataTypeSpecification().getConstraints().iterator().next();
		assertSize(2, vrc25.getAllowedValues());

		assertInvalidValue(attr25, 49L);
		assertValidValue(attr25, 50L);
		assertValidValue(attr25, 300L);
		assertInvalidValue(attr25, 301L);

		assertInvalidValue(attr25, 399L);
		assertValidValue(attr25, 400L);
		assertValidValue(attr25, 750L);
		assertInvalidValue(attr25, 751L);

		final PrimaryTypeAttributeSpecification attr26 = findAttribute(cont2spec, "leaf26");
		assertEquals(DataType.INTEGER, attr26.getDataTypeSpecification().getDataType());
		assertSize(1, attr26.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc26 = (ValueRangeConstraint) attr26.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range26 = (MinMaxRange) vrc26.getAllowedValues().iterator().next();
		assertEquals(0L, range26.getMinValue());
		assertEquals(65535L, range26.getMaxValue());

		assertInvalidValue(attr26, -1L);
		assertValidValue(attr26, 0L);
		assertValidValue(attr26, 65535L);
		assertInvalidValue(attr26, 65536L);
	}

	private static final String MODULE_NS_SIT = "urn+test+signed-integer-types";
	private static final String MODULE_XYZ_VERSION_SIT = "2021.1.31";

	@Test
	public void test___signed_integer_types() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P053_INVALID_VALUE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "basic-data-types/signed-integer-types.yang"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(2, 0, 0, 0, 1, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_SIT, "cont1", MODULE_XYZ_VERSION_SIT);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(ByteType.class, leaf11.getType());
		assertNull(((ByteType) leaf11.getType()).getValueRangeConstraint());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(ShortType.class, leaf12.getType());
		assertNull(((ShortType) leaf12.getType()).getValueRangeConstraint());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(IntegerType.class, leaf13.getType());
		assertNull(((IntegerType) leaf13.getType()).getValueRangeConstraint());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(LongType.class, leaf14.getType());
		assertFalse(((LongType) leaf14.getType()).isUnsigned());
		assertNull(((LongType) leaf14.getType()).getValueRangeConstraint());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_SIT, "cont2", MODULE_XYZ_VERSION_SIT);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertInstanceOf(ShortType.class, leaf21.getType());
		assertNull(((ShortType) leaf21.getType()).getValueRangeConstraint());

		final PrimaryTypeAttribute leaf22 = findAttribute(cont2, "leaf22");
		assertInstanceOf(ShortType.class, leaf22.getType());
		assertNotNull(((ShortType) leaf22.getType()).getValueRangeConstraint());
		assertEmpty(((ShortType) leaf22.getType()).getValueRangeConstraint().getMinMaxRange());
		assertNull(((ShortType) leaf22.getType()).getValueRangeConstraint().getMinValue());
		assertEquals(10L, ((ShortType) leaf22.getType()).getValueRangeConstraint().getMaxValue().getValue());

		final PrimaryTypeAttribute leaf23 = findAttribute(cont2, "leaf23");
		assertInstanceOf(ShortType.class, leaf23.getType());
		assertNotNull(((ShortType) leaf23.getType()).getValueRangeConstraint());
		assertEmpty(((ShortType) leaf23.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(2000L, ((ShortType) leaf23.getType()).getValueRangeConstraint().getMinValue().getValue());
		assertNull(((ShortType) leaf23.getType()).getValueRangeConstraint().getMaxValue());

		final PrimaryTypeAttribute leaf24 = findAttribute(cont2, "leaf24");
		assertInstanceOf(ShortType.class, leaf24.getType());
		assertNotNull(((ShortType) leaf24.getType()).getValueRangeConstraint());
		assertSize(1, ((ShortType) leaf24.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(50L, ((ShortType) leaf24.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(300L, ((ShortType) leaf24.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());

		final PrimaryTypeAttribute leaf25 = findAttribute(cont2, "leaf25");
		assertInstanceOf(ShortType.class, leaf25.getType());
		assertNotNull(((ShortType) leaf25.getType()).getValueRangeConstraint());
		assertSize(2, ((ShortType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(50L, ((ShortType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin());
		assertEquals(300L, ((ShortType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax());
		assertEquals(400L, ((ShortType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange().get(1).getMin());
		assertEquals(750L, ((ShortType) leaf25.getType()).getValueRangeConstraint().getMinMaxRange().get(1).getMax());

		final PrimaryTypeAttribute leaf26 = findAttribute(cont2, "leaf26");
		assertInstanceOf(ShortType.class, leaf26.getType());
		/*
		 * The error in the model would have resulted in no explicit constraint
		 * being generated.
		 */
		assertNull(((ShortType) leaf26.getType()).getValueRangeConstraint());

		final PrimaryTypeAttribute leaf27 = findAttribute(cont2, "leaf27");
		assertInstanceOf(ShortType.class, leaf27.getType());
		/*
		 * The error in the model would have resulted in no explicit constraint
		 * being generated.
		 */
		assertNull(((ShortType) leaf27.getType()).getValueRangeConstraint());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.BYTE, attr11.getDataTypeSpecification().getDataType());
		assertEmpty(attr11.getDataTypeSpecification().getConstraints());

		assertInvalidValue(attr11, -129L);
		assertValidValue(attr11, -128L);
		assertValidValue(attr11, 127L);
		assertInvalidValue(attr11, 128L);

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.SHORT, attr12.getDataTypeSpecification().getDataType());
		assertEmpty(attr12.getDataTypeSpecification().getConstraints());

		assertInvalidValue(attr12, -32769L);
		assertValidValue(attr12, -32768L);
		assertValidValue(attr12, 32767L);
		assertInvalidValue(attr12, 32768L);

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.INTEGER, attr13.getDataTypeSpecification().getDataType());
		assertEmpty(attr13.getDataTypeSpecification().getConstraints());

		assertInvalidValue(attr13, -2147483649L);
		assertValidValue(attr13, -2147483648L);
		assertValidValue(attr13, 2147483647L);
		assertInvalidValue(attr13, 2147483648L);

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.LONG, attr14.getDataTypeSpecification().getDataType());
		assertFalse(attr14.getDataTypeSpecification().isUnsigned());
		assertEmpty(attr14.getDataTypeSpecification().getConstraints());

		assertValidValue(attr14, -9223372036854775808L);
		assertValidValue(attr14, 9223372036854775807L);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertEquals(DataType.SHORT, attr21.getDataTypeSpecification().getDataType());
		assertEmpty(attr21.getDataTypeSpecification().getConstraints());

		assertInvalidValue(attr12, -32769L);
		assertValidValue(attr12, -32768L);
		assertValidValue(attr12, 32767L);
		assertInvalidValue(attr12, 32768L);

		final PrimaryTypeAttributeSpecification attr22 = findAttribute(cont2spec, "leaf22");
		assertEquals(DataType.SHORT, attr22.getDataTypeSpecification().getDataType());
		assertSize(1, attr22.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc22 = (ValueRangeConstraint) attr22.getDataTypeSpecification().getConstraints().iterator().next();
		final MaxValue maxValue22 = (MaxValue) vrc22.getAllowedValues().iterator().next();
		assertEquals(10L, maxValue22.getMaxValueConstraint());

		assertInvalidValue(attr22, -32769L);
		assertValidValue(attr22, -32768L);
		assertValidValue(attr22, 10L);
		assertInvalidValue(attr22, 11L);

		final PrimaryTypeAttributeSpecification attr23 = findAttribute(cont2spec, "leaf23");
		assertEquals(DataType.SHORT, attr23.getDataTypeSpecification().getDataType());
		assertSize(1, attr23.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc23 = (ValueRangeConstraint) attr23.getDataTypeSpecification().getConstraints().iterator().next();
		final MinValue minValue23 = (MinValue) vrc23.getAllowedValues().iterator().next();
		assertEquals(2000L, minValue23.getMinValueConstraint());

		assertInvalidValue(attr23, 1999L);
		assertValidValue(attr23, 2000L);
		assertValidValue(attr23, 32767L);
		assertInvalidValue(attr23, 32768L);

		final PrimaryTypeAttributeSpecification attr24 = findAttribute(cont2spec, "leaf24");
		assertEquals(DataType.SHORT, attr24.getDataTypeSpecification().getDataType());
		assertSize(1, attr24.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc24 = (ValueRangeConstraint) attr24.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange range24 = (MinMaxRange) vrc24.getAllowedValues().iterator().next();
		assertEquals(50L, range24.getMinValue());
		assertEquals(300L, range24.getMaxValue());

		assertInvalidValue(attr24, 49L);
		assertValidValue(attr24, 50L);
		assertValidValue(attr24, 300L);
		assertInvalidValue(attr24, 301L);

		final PrimaryTypeAttributeSpecification attr25 = findAttribute(cont2spec, "leaf25");
		assertEquals(DataType.SHORT, attr25.getDataTypeSpecification().getDataType());
		assertSize(1, attr25.getDataTypeSpecification().getConstraints());
		final ValueRangeConstraint vrc25 = (ValueRangeConstraint) attr25.getDataTypeSpecification().getConstraints().iterator().next();
		assertSize(2, vrc25.getAllowedValues());

		assertInvalidValue(attr25, 49L);
		assertValidValue(attr25, 50L);
		assertValidValue(attr25, 300L);
		assertInvalidValue(attr25, 301L);

		assertInvalidValue(attr25, 399L);
		assertValidValue(attr25, 400L);
		assertValidValue(attr25, 750L);
		assertInvalidValue(attr25, 751L);

		final PrimaryTypeAttributeSpecification attr26 = findAttribute(cont2spec, "leaf26");
		assertEquals(DataType.SHORT, attr26.getDataTypeSpecification().getDataType());
		assertEmpty(attr26.getDataTypeSpecification().getConstraints());

		final PrimaryTypeAttributeSpecification attr27 = findAttribute(cont2spec, "leaf27");
		assertEquals(DataType.SHORT, attr27.getDataTypeSpecification().getDataType());
		assertEmpty(attr27.getDataTypeSpecification().getConstraints());
	}

	private static final String MODULE_NS_D64 = "urn<test>decimal64-type";
	private static final String MODULE_XYZ_VERSION_D64 = "2021.1.31";

	@Test
	public void test___decimal64_type() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P053_INVALID_VALUE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "basic-data-types/decimal64-type.yang"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(1, 0, 0, 0, 1, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_D64, "cont1", MODULE_XYZ_VERSION_D64);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(DoubleType.class, leaf11.getType());
		assertNull(((DoubleType) leaf11.getType()).getValueRangeConstraint());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(DoubleType.class, leaf12.getType());
		assertNotNull(((DoubleType) leaf12.getType()).getValueRangeConstraint());
		assertSize(1, ((DoubleType) leaf12.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(50.0d, ((DoubleType) leaf12.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin(), 0.01d);
		assertEquals(300.0d, ((DoubleType) leaf12.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax(), 0.01d);

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(DoubleType.class, leaf13.getType());
		assertNotNull(((DoubleType) leaf13.getType()).getValueRangeConstraint());
		assertSize(2, ((DoubleType) leaf13.getType()).getValueRangeConstraint().getMinMaxRange());
		assertEquals(50.1d, ((DoubleType) leaf13.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMin(), 0.01d);
		assertEquals(300.2d, ((DoubleType) leaf13.getType()).getValueRangeConstraint().getMinMaxRange().get(0).getMax(), 0.01d);
		assertEquals(400.4d, ((DoubleType) leaf13.getType()).getValueRangeConstraint().getMinMaxRange().get(1).getMin(), 0.01d);
		assertEquals(750.7d, ((DoubleType) leaf13.getType()).getValueRangeConstraint().getMinMaxRange().get(1).getMax(), 0.01d);

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(DoubleType.class, leaf14.getType());
		/*
		 * The error in the model would have resulted in no explicit constraint
		 * being generated.
		 */
		assertNull(((DoubleType) leaf14.getType()).getValueRangeConstraint());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.DOUBLE, attr11.getDataTypeSpecification().getDataType());
		assertEmpty(attr11.getDataTypeSpecification().getConstraints());

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.DOUBLE, attr12.getDataTypeSpecification().getDataType());
		assertSize(1, attr12.getDataTypeSpecification().getConstraints());
		final DoubleValueRangeConstraint vrc12 = (DoubleValueRangeConstraint) attr12.getDataTypeSpecification().getConstraints().iterator().next();
		final DoubleMinMaxRange range12 = (DoubleMinMaxRange) vrc12.getAllowedValues().iterator().next();
		assertEquals(50d, range12.getMinValue(), 0.01d);
		assertEquals(300d, range12.getMaxValue(), 0.01d);

		assertInvalidValue(attr12, 49.99999d);
		assertValidValue(attr12, 50d);
		assertValidValue(attr12, 300d);
		assertInvalidValue(attr12, 300.000001d);

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.DOUBLE, attr13.getDataTypeSpecification().getDataType());
		assertSize(1, attr13.getDataTypeSpecification().getConstraints());
		final DoubleValueRangeConstraint vrc13 = (DoubleValueRangeConstraint) attr13.getDataTypeSpecification().getConstraints().iterator().next();
		assertSize(2, vrc13.getAllowedValues());

		assertInvalidValue(attr13, 50.0999d);
		assertValidValue(attr13, 50.1d);
		assertValidValue(attr13, 300.2d);
		assertInvalidValue(attr13, 300.200001d);

		assertInvalidValue(attr13, 400.39999d);
		assertValidValue(attr13, 400.4d);
		assertValidValue(attr13, 750.7d);
		assertInvalidValue(attr13, 750.700001d);

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.DOUBLE, attr14.getDataTypeSpecification().getDataType());
		assertEmpty(attr14.getDataTypeSpecification().getConstraints());
	}

	private static final String MODULE_NS_OTHER = "urn&test&other-types";
	private static final String MODULE_XYZ_VERSION_OTHER = "2021.2.30";

	@Test
	public void test___other_types() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P019_MISSING_REQUIRED_CHILD_STATEMENT.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P113_UNRESOLVABLE_DERIVED_TYPE.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P144_BIT_WITHOUT_POSITION.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "basic-data-types/other-types.yang"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(1, 0, 0, 0, 1, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_OTHER, "cont1", MODULE_XYZ_VERSION_OTHER);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(BooleanType.class, leaf11.getType());
		assertNull(((BooleanType) leaf11.getType()).isEmpty());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(BooleanType.class, leaf12.getType());
		assertTrue(((BooleanType) leaf12.getType()).isEmpty());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(StringType.class, leaf13.getType());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(StringType.class, leaf14.getType());

		final PrimaryTypeAttribute leaf15 = findAttribute(cont1, "leaf15");
		assertInstanceOf(BitsType.class, leaf15.getType());

		assertSize(3, ((BitsType) leaf15.getType()).getMember());
		assertEquals("green", ((BitsType) leaf15.getType()).getMember().get(0).getName());
		assertEquals("red", ((BitsType) leaf15.getType()).getMember().get(1).getName());
		assertEquals("yellow", ((BitsType) leaf15.getType()).getMember().get(2).getName());

		final PrimaryTypeAttribute leaf16 = findAttribute(cont1, "leaf16");
		assertInstanceOf(StringType.class, leaf16.getType());

		final PrimaryTypeAttribute leaf17 = findAttribute(cont1, "leaf17");
		assertInstanceOf(StringType.class, leaf17.getType());

		final PrimaryTypeAttribute leaf18 = findAttribute(cont1, "leaf18");
		assertInstanceOf(StringType.class, leaf18.getType());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.BOOLEAN, attr11.getDataTypeSpecification().getDataType());
		assertFalse(attr11.getDataTypeSpecification().isEmpty());

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.BOOLEAN, attr12.getDataTypeSpecification().getDataType());
		assertTrue(attr12.getDataTypeSpecification().isEmpty());

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.STRING, attr13.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.STRING, attr14.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
		assertEquals(DataType.BITS, attr15.getDataTypeSpecification().getDataType());
		final Set<String> bitMembers = attr15.getDataTypeSpecification().getBitsDataTypeMembers().stream().map(m -> m.getMemberName()).collect(Collectors.toSet());
		assertTrue(bitMembers.contains("red"));
		assertTrue(bitMembers.contains("yellow"));
		assertTrue(bitMembers.contains("green"));
	}

	private static final String MODULE_NS_STR = "urn#test#string-type";
	private static final String MODULE_XYZ_VERSION_STR = "2021.1.31";

	@Test
	public void test___string_type() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P015_INVALID_SYNTAX_IN_DOCUMENT.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P053_INVALID_VALUE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S322_INVALID_PATTERN.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N670_MODIFIER_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "basic-data-types/string-type.yang"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(1, 0, 0, 0, 1, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_STR, "cont1", MODULE_XYZ_VERSION_STR);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(StringType.class, leaf11.getType());
		assertNull(((StringType) leaf11.getType()).getStringLengthConstraint());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(StringType.class, leaf12.getType());
		assertNotNull(((StringType) leaf12.getType()).getStringLengthConstraint());
		assertEmpty(((StringType) leaf12.getType()).getStringLengthConstraint().getMinMaxLength());
		assertNull(((StringType) leaf12.getType()).getStringLengthConstraint().getMinLength());
		assertEquals(10L, ((StringType) leaf12.getType()).getStringLengthConstraint().getMaxLength().getValue());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(StringType.class, leaf13.getType());
		assertNotNull(((StringType) leaf13.getType()).getStringLengthConstraint());
		assertEmpty(((StringType) leaf13.getType()).getStringLengthConstraint().getMinMaxLength());
		assertEquals(20L, ((StringType) leaf13.getType()).getStringLengthConstraint().getMinLength().getValue());
		assertNull(((StringType) leaf13.getType()).getStringLengthConstraint().getMaxLength());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(StringType.class, leaf14.getType());
		assertNotNull(((StringType) leaf14.getType()).getStringLengthConstraint());
		assertSize(1, ((StringType) leaf14.getType()).getStringLengthConstraint().getMinMaxLength());
		assertEquals(5L, ((StringType) leaf14.getType()).getStringLengthConstraint().getMinMaxLength().get(0).getMin());
		assertEquals(30L, ((StringType) leaf14.getType()).getStringLengthConstraint().getMinMaxLength().get(0).getMax());

		final PrimaryTypeAttribute leaf15 = findAttribute(cont1, "leaf15");
		assertInstanceOf(StringType.class, leaf15.getType());
		assertNotNull(((StringType) leaf15.getType()).getStringLengthConstraint());
		assertSize(2, ((StringType) leaf15.getType()).getStringLengthConstraint().getMinMaxLength());
		assertEquals(5L, ((StringType) leaf15.getType()).getStringLengthConstraint().getMinMaxLength().get(0).getMin());
		assertEquals(30L, ((StringType) leaf15.getType()).getStringLengthConstraint().getMinMaxLength().get(0).getMax());
		assertEquals(40L, ((StringType) leaf15.getType()).getStringLengthConstraint().getMinMaxLength().get(1).getMin());
		assertEquals(50L, ((StringType) leaf15.getType()).getStringLengthConstraint().getMinMaxLength().get(1).getMax());

		final PrimaryTypeAttribute leaf16 = findAttribute(cont1, "leaf16");
		assertInstanceOf(StringType.class, leaf16.getType());
		/*
		 * The error in the model would have resulted in no explicit constraint
		 * being generated.
		 */
		assertNull(((StringType) leaf16.getType()).getStringLengthConstraint());

		final PrimaryTypeAttribute leaf17 = findAttribute(cont1, "leaf17");
		assertInstanceOf(StringType.class, leaf17.getType());
		/*
		 * The error in the model would have resulted in no explicit constraint
		 * being generated.
		 */
		assertNull(((StringType) leaf17.getType()).getStringLengthConstraint());

		final PrimaryTypeAttribute leaf18 = findAttribute(cont1, "leaf18");
		assertInstanceOf(StringType.class, leaf18.getType());
		assertNotNull(((StringType) leaf18.getType()).getStringContentsConstraint());
		assertEquals(StringContentsConstraintType.REGEX, ((StringType) leaf18.getType()).getStringContentsConstraint().getType());
		assertEquals("ab*c", ((StringType) leaf18.getType()).getStringContentsConstraint().getValue());

		final PrimaryTypeAttribute leaf19 = findAttribute(cont1, "leaf19");
		assertNull(((StringType) leaf19.getType()).getStringContentsConstraint());

		final PrimaryTypeAttribute leaf20 = findAttribute(cont1, "leaf20");
		assertNull(((StringType) leaf20.getType()).getStringContentsConstraint());

		final PrimaryTypeAttribute leaf21 = findAttribute(cont1, "leaf21");
		assertNull(((StringType) leaf21.getType()).getStringContentsConstraint());

		final PrimaryTypeAttribute leaf22 = findAttribute(cont1, "leaf22");
		assertNotNull(((StringType) leaf22.getType()).getStringContentsConstraint());
		assertEquals(StringContentsConstraintType.REGEX, ((StringType) leaf22.getType()).getStringContentsConstraint().getType());
		assertEquals("de*f", ((StringType) leaf22.getType()).getStringContentsConstraint().getValue());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.STRING, attr11.getDataTypeSpecification().getDataType());
		assertEmpty(attr11.getDataTypeSpecification().getConstraints());

		assertValidValue(attr11, "");
		assertValidValue(attr11, "Hello World!");

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.STRING, attr12.getDataTypeSpecification().getDataType());
		assertSize(1, attr12.getDataTypeSpecification().getConstraints());
		final StringLengthConstraint slc12 = (StringLengthConstraint) attr12.getDataTypeSpecification().getConstraints().iterator().next();
		final MaxValue maxValue12 = (MaxValue) slc12.getAllowedLength().iterator().next();
		assertEquals(10L, maxValue12.getMaxValueConstraint());

		assertValidValue(attr12, "");
		assertValidValue(attr12, "Hello!");
		assertInvalidValue(attr12, "More than 10 characters!");

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.STRING, attr13.getDataTypeSpecification().getDataType());
		assertSize(1, attr13.getDataTypeSpecification().getConstraints());
		final StringLengthConstraint slc13 = (StringLengthConstraint) attr13.getDataTypeSpecification().getConstraints().iterator().next();
		final MinValue minValue13 = (MinValue) slc13.getAllowedLength().iterator().next();
		assertEquals(20L, minValue13.getMinValueConstraint());

		assertInvalidValue(attr13, "");
		assertInvalidValue(attr13, "Not enough chars");
		assertValidValue(attr13, "Yep, this has more than 20 chars");

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.STRING, attr14.getDataTypeSpecification().getDataType());
		assertSize(1, attr14.getDataTypeSpecification().getConstraints());
		final StringLengthConstraint slc14 = (StringLengthConstraint) attr14.getDataTypeSpecification().getConstraints().iterator().next();
		final MinMaxRange length14 = (MinMaxRange) slc14.getAllowedLength().iterator().next();
		assertEquals(5L, length14.getMinValue());
		assertEquals(30L, length14.getMaxValue());

		assertInvalidValue(attr14, "four");
		assertValidValue(attr14, "five!");
		assertValidValue(attr14, "Less than 30 chars here");
		assertInvalidValue(attr14, "Definitely more than 30 characters here");

		final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
		assertEquals(DataType.STRING, attr15.getDataTypeSpecification().getDataType());
		assertSize(1, attr15.getDataTypeSpecification().getConstraints());
		final StringLengthConstraint slc15 = (StringLengthConstraint) attr15.getDataTypeSpecification().getConstraints().iterator().next();
		assertSize(2, slc15.getAllowedLength());

		assertInvalidValue(attr14, "four");
		assertValidValue(attr14, "five!");
		assertValidValue(attr14, "Less than 30 chars here");
		assertInvalidValue(attr14, "About 35 characters here in this sentence!");
		assertValidValue(attr15, "Pretty long sentence with between 40 and 50");

		final PrimaryTypeAttributeSpecification attr16 = findAttribute(cont1spec, "leaf16");
		assertEquals(DataType.STRING, attr16.getDataTypeSpecification().getDataType());
		assertEmpty(attr16.getDataTypeSpecification().getConstraints());

		final PrimaryTypeAttributeSpecification attr17 = findAttribute(cont1spec, "leaf17");
		assertEquals(DataType.STRING, attr17.getDataTypeSpecification().getDataType());
		assertEmpty(attr17.getDataTypeSpecification().getConstraints());

		final PrimaryTypeAttributeSpecification attr18 = findAttribute(cont1spec, "leaf18");
		assertEquals(DataType.STRING, attr18.getDataTypeSpecification().getDataType());
		assertSize(1, attr18.getDataTypeSpecification().getConstraints());
		final StringContentsConstraint scc18 = (StringContentsConstraint) attr18.getDataTypeSpecification().getConstraints().iterator().next();
		assertEquals(com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.StringContentsConstraintType.REGEX, scc18.getContentsConstraintType());
		assertEquals("ab*c", scc18.getValue());
	}
}
