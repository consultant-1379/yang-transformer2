package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.defaults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BitsType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ByteType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ByteValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.UnionType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.eri.ValidatorFindingTypeEri;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class SimpleDefaultsTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-defaults/";

	private static final String MODULE_NS_ONE = "urn{test}simple-defaults-one";
	private static final String MODULE_XYZ_VERSION_ONE = "2021.10.30";

	@Test
	public void test___simple_defaults_one() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S392_INVALID_DEFAULT_VALUE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple/simple-defaults-one.yang"));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont1", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(BooleanType.class, leaf11.getType());
		assertNull(leaf11.getDefault());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(BooleanType.class, leaf12.getType());
		assertInstanceOf(BooleanValue.class, leaf12.getDefault());
		assertTrue(((BooleanValue) leaf12.getDefault()).isValue());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(BooleanType.class, leaf13.getType());
		assertInstanceOf(BooleanValue.class, leaf13.getDefault());
		assertFalse(((BooleanValue) leaf13.getDefault()).isValue());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(BooleanType.class, leaf14.getType());
		assertNull(leaf14.getDefault());

		final PrimaryTypeAttribute leaf17 = findAttribute(cont1, "leaf17");
		assertInstanceOf(BooleanType.class, leaf17.getType());
		assertTrue(((BooleanType) leaf17.getType()).isEmpty());
		assertNull(leaf17.getDefault());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont2", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertInstanceOf(BitsType.class, leaf21.getType());
		assertInstanceOf(StringValue.class, leaf21.getDefault());
		assertEquals("ONE", ((StringValue) leaf21.getDefault()).getValue());

		final PrimaryTypeAttribute leaf22 = findAttribute(cont2, "leaf22");
		assertInstanceOf(BitsType.class, leaf22.getType());
		assertInstanceOf(StringValue.class, leaf22.getDefault());
		assertEquals("ONE TWO", ((StringValue) leaf22.getDefault()).getValue());

		final PrimaryTypeAttribute leaf23 = findAttribute(cont2, "leaf23");
		assertInstanceOf(BitsType.class, leaf23.getType());
		assertInstanceOf(StringValue.class, leaf23.getDefault());
		assertEquals("ONE THREE", ((StringValue) leaf23.getDefault()).getValue());

		final PrimaryTypeAttribute leaf24 = findAttribute(cont2, "leaf24");
		assertInstanceOf(BitsType.class, leaf24.getType());
		assertNull(leaf24.getDefault());

		final PrimaryTypeAttribute leaf25 = findAttribute(cont2, "leaf25");
		assertInstanceOf(BitsType.class, leaf25.getType());
		assertInstanceOf(StringValue.class, leaf25.getDefault());
		assertEquals("ONE", ((StringValue) leaf25.getDefault()).getValue());

		final PrimaryTypeAttribute leaf26 = findAttribute(cont2, "leaf26");
		assertInstanceOf(BitsType.class, leaf26.getType());
		assertNull(leaf26.getDefault());

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont3", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);

		final PrimaryTypeAttribute leaf31 = findAttribute(cont3, "leaf31");
		assertInstanceOf(StringType.class, leaf31.getType());
		assertInstanceOf(StringValue.class, leaf31.getDefault());
		assertEquals("", ((StringValue) leaf31.getDefault()).getValue());

		final PrimaryTypeAttribute leaf32 = findAttribute(cont3, "leaf32");
		assertInstanceOf(StringType.class, leaf32.getType());
		assertInstanceOf(StringValue.class, leaf32.getDefault());
		assertEquals("\t\t", ((StringValue) leaf32.getDefault()).getValue());

		final PrimaryTypeAttribute leaf33 = findAttribute(cont3, "leaf33");
		assertInstanceOf(StringType.class, leaf33.getType());
		assertInstanceOf(StringValue.class, leaf33.getDefault());
		assertEquals(" abc ", ((StringValue) leaf33.getDefault()).getValue());

		final PrimaryTypeAttribute leaf34 = findAttribute(cont3, "leaf34");
		assertInstanceOf(StringType.class, leaf34.getType());
		assertNull(leaf34.getDefault());

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont4", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);

		final PrimaryTypeAttribute leaf41 = findAttribute(cont4, "leaf41");
		assertInstanceOf(ByteType.class, leaf41.getType());
		assertInstanceOf(ByteValue.class, leaf41.getDefault());
		assertEquals((Byte)(byte) 10, ((ByteValue) leaf41.getDefault()).getValue());

		final PrimaryTypeAttribute leaf42 = findAttribute(cont4, "leaf42");
		assertInstanceOf(ShortType.class, leaf42.getType());
		assertInstanceOf(ShortValue.class, leaf42.getDefault());
		assertEquals((Short)(short) 1000, ((ShortValue) leaf42.getDefault()).getValue());

		final PrimaryTypeAttribute leaf43 = findAttribute(cont4, "leaf43");
		assertInstanceOf(IntegerType.class, leaf43.getType());
		assertInstanceOf(IntegerValue.class, leaf43.getDefault());
		assertEquals((Integer) 10000000, ((IntegerValue) leaf43.getDefault()).getValue());

		final PrimaryTypeAttribute leaf44 = findAttribute(cont4, "leaf44");
		assertInstanceOf(LongType.class, leaf44.getType());
		assertInstanceOf(LongValue.class, leaf44.getDefault());
		assertEquals((Long) 10000000000000000L, ((LongValue) leaf44.getDefault()).getValue());

		final PrimaryTypeAttribute leaf45 = findAttribute(cont4, "leaf45");
		assertInstanceOf(ShortType.class, leaf45.getType());
		assertInstanceOf(ShortValue.class, leaf45.getDefault());
		assertEquals((Short)(short) 10, ((ShortValue) leaf45.getDefault()).getValue());

		final PrimaryTypeAttribute leaf46 = findAttribute(cont4, "leaf46");
		assertInstanceOf(IntegerType.class, leaf46.getType());
		assertInstanceOf(IntegerValue.class, leaf46.getDefault());
		assertEquals((Integer) 1000, ((IntegerValue) leaf46.getDefault()).getValue());

		final PrimaryTypeAttribute leaf47 = findAttribute(cont4, "leaf47");
		assertInstanceOf(LongType.class, leaf47.getType());
		assertFalse(((LongType) leaf47.getType()).isUnsigned());
		assertInstanceOf(LongValue.class, leaf47.getDefault());
		assertEquals((Long) 10000000L, ((LongValue) leaf47.getDefault()).getValue());

		final PrimaryTypeAttribute leaf48 = findAttribute(cont4, "leaf48");
		assertInstanceOf(LongType.class, leaf48.getType());
		assertTrue(((LongType) leaf48.getType()).isUnsigned());
		assertInstanceOf(LongValue.class, leaf48.getDefault());
		assertEquals((Long) 10000000000000000L, ((LongValue) leaf48.getDefault()).getValue());

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont5", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont5 = load(cont5modelInfo);

		final PrimaryTypeAttribute leaf51 = findAttribute(cont5, "leaf51");
		assertInstanceOf(ByteType.class, leaf51.getType());
		assertNull(leaf51.getDefault());

		final PrimaryTypeAttribute leaf52 = findAttribute(cont5, "leaf52");
		assertInstanceOf(ByteType.class, leaf52.getType());
		assertNull(leaf52.getDefault());

		final PrimaryTypeAttribute leaf53 = findAttribute(cont5, "leaf53");
		assertInstanceOf(ByteType.class, leaf53.getType());
		assertInstanceOf(ByteValue.class, leaf53.getDefault());
		assertEquals((Byte)(byte) -16, ((ByteValue) leaf53.getDefault()).getValue());

		final PrimaryTypeAttribute leaf54 = findAttribute(cont5, "leaf54");
		assertInstanceOf(ByteType.class, leaf54.getType());
		assertNull(leaf54.getDefault());

		final PrimaryTypeAttribute leaf55 = findAttribute(cont5, "leaf55");
		assertInstanceOf(ShortType.class, leaf55.getType());
		assertNull(leaf55.getDefault());

		final PrimaryTypeAttribute leaf56 = findAttribute(cont5, "leaf56");
		assertInstanceOf(LongType.class, leaf56.getType());
		assertNull(leaf56.getDefault());

		final ModelInfo cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont6", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont6 = load(cont6modelInfo);

		final PrimaryTypeAttribute leaf61 = findAttribute(cont6, "leaf61");
		assertInstanceOf(DoubleType.class, leaf61.getType());
		assertInstanceOf(DoubleValue.class, leaf61.getDefault());
		assertEquals(10.12345d, ((DoubleValue) leaf61.getDefault()).getValue(), 0.01d);

		final PrimaryTypeAttribute leaf62 = findAttribute(cont6, "leaf62");
		assertInstanceOf(DoubleType.class, leaf62.getType());
		assertNull(leaf62.getDefault());

		final PrimaryTypeAttribute leaf63 = findAttribute(cont6, "leaf63");
		assertInstanceOf(DoubleType.class, leaf63.getType());
		assertNull(leaf63.getDefault());

		final ModelInfo cont7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont7", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont7 = load(cont7modelInfo);

		final PrimaryTypeAttribute leaf71 = findAttribute(cont7, "leaf71");
		assertInstanceOf(EnumRefType.class, leaf71.getType());
		assertInstanceOf(StringValue.class, leaf71.getDefault());
		assertEquals("RED", ((StringValue) leaf71.getDefault()).getValue());

		final PrimaryTypeAttribute leaf72 = findAttribute(cont7, "leaf72");
		assertInstanceOf(EnumRefType.class, leaf72.getType());
		assertNull(leaf72.getDefault());

		final PrimaryTypeAttribute leaf73 = findAttribute(cont7, "leaf73");
		assertInstanceOf(EnumRefType.class, leaf73.getType());
		assertNull(leaf73.getDefault());

		final PrimaryTypeAttribute leaf74 = findAttribute(cont7, "leaf74");
		assertInstanceOf(EnumRefType.class, leaf74.getType());
		assertNull(leaf74.getDefault());

		final ModelInfo cont8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont8", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont8 = load(cont8modelInfo);

		final PrimaryTypeAttribute leaf81 = findAttribute(cont8, "leaf81");
		assertInstanceOf(EnumRefType.class, leaf81.getType());
		assertInstanceOf(StringValue.class, leaf81.getDefault());
		assertEquals("identity1", ((StringValue) leaf81.getDefault()).getValue());

		final PrimaryTypeAttribute leaf82 = findAttribute(cont8, "leaf82");
		assertInstanceOf(EnumRefType.class, leaf82.getType());
		assertInstanceOf(StringValue.class, leaf82.getDefault());
		assertEquals("identity3", ((StringValue) leaf82.getDefault()).getValue());

		final PrimaryTypeAttribute leaf83 = findAttribute(cont8, "leaf83");
		assertInstanceOf(EnumRefType.class, leaf83.getType());
		assertNull(leaf83.getDefault());

		final PrimaryTypeAttribute leaf84 = findAttribute(cont8, "leaf84");
		assertInstanceOf(EnumRefType.class, leaf84.getType());
		assertNull(leaf84.getDefault());

		final PrimaryTypeAttribute leaf85 = findAttribute(cont8, "leaf85");
		assertInstanceOf(EnumRefType.class, leaf85.getType());
		assertNull(leaf85.getDefault());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.BOOLEAN, attr11.getDataTypeSpecification().getDataType());
		final Boolean defaultLeaf11 = attr11.getDefaultValue();
		assertNull(defaultLeaf11);

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.BOOLEAN, attr12.getDataTypeSpecification().getDataType());
		final Boolean defaultLeaf12 = attr12.getDefaultValue();
		assertTrue(defaultLeaf12);

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.BOOLEAN, attr13.getDataTypeSpecification().getDataType());
		final Boolean defaultLeaf13 = attr13.getDefaultValue();
		assertFalse(defaultLeaf13);

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.BOOLEAN, attr14.getDataTypeSpecification().getDataType());
		final Boolean defaultLeaf14 = attr14.getDefaultValue();
		assertNull(defaultLeaf14);

		final PrimaryTypeAttributeSpecification attr17 = findAttribute(cont1spec, "leaf17");
		assertEquals(DataType.BOOLEAN, attr17.getDataTypeSpecification().getDataType());
		final Boolean defaultLeaf17 = attr17.getDefaultValue();
		assertNull(defaultLeaf17);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertEquals(DataType.BITS, attr21.getDataTypeSpecification().getDataType());
		final String defaultLeaf21 = attr21.getDefaultValue();
		assertEquals("ONE", defaultLeaf21);

		final PrimaryTypeAttributeSpecification attr22 = findAttribute(cont2spec, "leaf22");
		assertEquals(DataType.BITS, attr22.getDataTypeSpecification().getDataType());
		final String defaultLeaf22 = attr22.getDefaultValue();
		assertEquals("ONE TWO", defaultLeaf22);

		final PrimaryTypeAttributeSpecification attr23 = findAttribute(cont2spec, "leaf23");
		assertEquals(DataType.BITS, attr23.getDataTypeSpecification().getDataType());
		final String defaultLeaf23 = attr23.getDefaultValue();
		assertEquals("ONE THREE", defaultLeaf23);

		final PrimaryTypeAttributeSpecification attr24 = findAttribute(cont2spec, "leaf24");
		assertEquals(DataType.BITS, attr24.getDataTypeSpecification().getDataType());
		final String defaultLeaf24 = attr24.getDefaultValue();
		assertNull(defaultLeaf24);

		final PrimaryTypeAttributeSpecification attr25 = findAttribute(cont2spec, "leaf25");
		assertEquals(DataType.BITS, attr25.getDataTypeSpecification().getDataType());
		final String defaultLeaf25 = attr25.getDefaultValue();
		assertEquals("ONE", defaultLeaf25);

		final PrimaryTypeAttributeSpecification attr26 = findAttribute(cont2spec, "leaf26");
		assertEquals(DataType.BITS, attr26.getDataTypeSpecification().getDataType());
		final String defaultLeaf26 = attr26.getDefaultValue();
		assertNull(defaultLeaf26);

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont3spec, "leaf31");
		assertEquals(DataType.STRING, attr31.getDataTypeSpecification().getDataType());
		final String defaultLeaf31 = attr31.getDefaultValue();
		assertEquals("", defaultLeaf31);

		final PrimaryTypeAttributeSpecification attr32 = findAttribute(cont3spec, "leaf32");
		assertEquals(DataType.STRING, attr32.getDataTypeSpecification().getDataType());
		final String defaultLeaf32 = attr32.getDefaultValue();
		assertEquals("\t\t", defaultLeaf32);

		final PrimaryTypeAttributeSpecification attr33 = findAttribute(cont3spec, "leaf33");
		assertEquals(DataType.STRING, attr33.getDataTypeSpecification().getDataType());
		final String defaultLeaf33 = attr33.getDefaultValue();
		assertEquals(" abc ", defaultLeaf33);

		final PrimaryTypeAttributeSpecification attr34 = findAttribute(cont3spec, "leaf34");
		assertEquals(DataType.STRING, attr34.getDataTypeSpecification().getDataType());
		final String defaultLeaf34 = attr34.getDefaultValue();
		assertNull(defaultLeaf34);

		final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr41 = findAttribute(cont4spec, "leaf41");
		assertEquals(DataType.BYTE, attr41.getDataTypeSpecification().getDataType());
		final Byte defaultLeaf41 = attr41.getDefaultValue();
		assertEquals((Byte)(byte) 10, defaultLeaf41);

		final PrimaryTypeAttributeSpecification attr42 = findAttribute(cont4spec, "leaf42");
		assertEquals(DataType.SHORT, attr42.getDataTypeSpecification().getDataType());
		final Short defaultLeaf42 = attr42.getDefaultValue();
		assertEquals((Short)(short) 1000, defaultLeaf42);

		final PrimaryTypeAttributeSpecification attr43 = findAttribute(cont4spec, "leaf43");
		assertEquals(DataType.INTEGER, attr43.getDataTypeSpecification().getDataType());
		final Integer defaultLeaf43 = attr43.getDefaultValue();
		assertEquals((Integer) 10000000, defaultLeaf43);

		final PrimaryTypeAttributeSpecification attr44 = findAttribute(cont4spec, "leaf44");
		assertEquals(DataType.LONG, attr44.getDataTypeSpecification().getDataType());
		final Long defaultLeaf44 = attr44.getDefaultValue();
		assertEquals((Long) 10000000000000000L, defaultLeaf44);

		final PrimaryTypeAttributeSpecification attr45 = findAttribute(cont4spec, "leaf45");
		assertEquals(DataType.SHORT, attr45.getDataTypeSpecification().getDataType());
		final Short defaultLeaf45 = attr45.getDefaultValue();
		assertEquals((Short)(short) 10, defaultLeaf45);

		final PrimaryTypeAttributeSpecification attr46 = findAttribute(cont4spec, "leaf46");
		assertEquals(DataType.INTEGER, attr46.getDataTypeSpecification().getDataType());
		final Integer defaultLeaf46 = attr46.getDefaultValue();
		assertEquals((Integer) 1000, defaultLeaf46);

		final PrimaryTypeAttributeSpecification attr47 = findAttribute(cont4spec, "leaf47");
		assertEquals(DataType.LONG, attr47.getDataTypeSpecification().getDataType());
		assertFalse(attr47.getDataTypeSpecification().isUnsigned());
		final Long defaultLeaf47 = attr47.getDefaultValue();
		assertEquals((Long) 10000000L, defaultLeaf47);

		final PrimaryTypeAttributeSpecification attr48 = findAttribute(cont4spec, "leaf48");
		assertEquals(DataType.LONG, attr48.getDataTypeSpecification().getDataType());
		assertTrue(attr48.getDataTypeSpecification().isUnsigned());
		final Long defaultLeaf48 = attr48.getDefaultValue();
		assertEquals((Long) 10000000000000000L, defaultLeaf48);

		final HierarchicalPrimaryTypeSpecification cont5spec = modelService.getTypedAccess().getEModelSpecification(cont5modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr51 = findAttribute(cont5spec, "leaf51");
		assertEquals(DataType.BYTE, attr51.getDataTypeSpecification().getDataType());
		final Byte defaultLeaf51 = attr51.getDefaultValue();
		assertNull(defaultLeaf51);

		final PrimaryTypeAttributeSpecification attr52 = findAttribute(cont5spec, "leaf52");
		assertEquals(DataType.BYTE, attr52.getDataTypeSpecification().getDataType());
		final Byte defaultLeaf52 = attr52.getDefaultValue();
		assertNull(defaultLeaf52);

		final PrimaryTypeAttributeSpecification attr53 = findAttribute(cont5spec, "leaf53");
		assertEquals(DataType.BYTE, attr53.getDataTypeSpecification().getDataType());
		final Byte defaultLeaf53 = attr53.getDefaultValue();
		assertEquals((Byte)(byte) -16, defaultLeaf53);

		final PrimaryTypeAttributeSpecification attr54 = findAttribute(cont5spec, "leaf54");
		assertEquals(DataType.BYTE, attr54.getDataTypeSpecification().getDataType());
		final Byte defaultLeaf54 = attr54.getDefaultValue();
		assertNull(defaultLeaf54);

		final PrimaryTypeAttributeSpecification attr55 = findAttribute(cont5spec, "leaf55");
		assertEquals(DataType.SHORT, attr55.getDataTypeSpecification().getDataType());
		final Short defaultLeaf55 = attr55.getDefaultValue();
		assertNull(defaultLeaf55);

		final PrimaryTypeAttributeSpecification attr56 = findAttribute(cont5spec, "leaf56");
		assertEquals(DataType.LONG, attr56.getDataTypeSpecification().getDataType());
		final Long defaultLeaf56 = attr56.getDefaultValue();
		assertNull(defaultLeaf56);

		final HierarchicalPrimaryTypeSpecification cont6spec = modelService.getTypedAccess().getEModelSpecification(cont6modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr61 = findAttribute(cont6spec, "leaf61");
		assertEquals(DataType.DOUBLE, attr61.getDataTypeSpecification().getDataType());
		final Double defaultLeaf61 = attr61.getDefaultValue();
		assertEquals(10.12345d, defaultLeaf61, 0.01d);

		final PrimaryTypeAttributeSpecification attr62 = findAttribute(cont6spec, "leaf62");
		assertEquals(DataType.DOUBLE, attr62.getDataTypeSpecification().getDataType());
		final Double defaultLeaf62 = attr62.getDefaultValue();
		assertNull(defaultLeaf62);

		final PrimaryTypeAttributeSpecification attr63 = findAttribute(cont6spec, "leaf63");
		assertEquals(DataType.DOUBLE, attr63.getDataTypeSpecification().getDataType());
		final Double defaultLeaf63 = attr63.getDefaultValue();
		assertNull(defaultLeaf63);

		final HierarchicalPrimaryTypeSpecification cont7spec = modelService.getTypedAccess().getEModelSpecification(cont7modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr71 = findAttribute(cont7spec, "leaf71");
		assertEquals(DataType.ENUM_REF, attr71.getDataTypeSpecification().getDataType());
		final String defaultLeaf71 = attr71.getDefaultValue();
		assertEquals("RED", defaultLeaf71);

		final PrimaryTypeAttributeSpecification attr72 = findAttribute(cont7spec, "leaf72");
		assertEquals(DataType.ENUM_REF, attr72.getDataTypeSpecification().getDataType());
		final String defaultLeaf72 = attr72.getDefaultValue();
		assertNull(defaultLeaf72);

		final PrimaryTypeAttributeSpecification attr73 = findAttribute(cont7spec, "leaf73");
		assertEquals(DataType.ENUM_REF, attr73.getDataTypeSpecification().getDataType());
		final String defaultLeaf73 = attr73.getDefaultValue();
		assertNull(defaultLeaf73);

		final PrimaryTypeAttributeSpecification attr74 = findAttribute(cont7spec, "leaf74");
		assertEquals(DataType.ENUM_REF, attr74.getDataTypeSpecification().getDataType());
		final String defaultLeaf74 = attr74.getDefaultValue();
		assertNull(defaultLeaf74);

		final HierarchicalPrimaryTypeSpecification cont8spec = modelService.getTypedAccess().getEModelSpecification(cont8modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr81 = findAttribute(cont8spec, "leaf81");
		assertEquals(DataType.ENUM_REF, attr81.getDataTypeSpecification().getDataType());
		final String defaultLeaf81 = attr81.getDefaultValue();
		assertEquals("identity1", defaultLeaf81);

		final PrimaryTypeAttributeSpecification attr82 = findAttribute(cont8spec, "leaf82");
		assertEquals(DataType.ENUM_REF, attr82.getDataTypeSpecification().getDataType());
		final String defaultLeaf82 = attr82.getDefaultValue();
		assertEquals("identity3", defaultLeaf82);

		final PrimaryTypeAttributeSpecification attr83 = findAttribute(cont8spec, "leaf83");
		assertEquals(DataType.ENUM_REF, attr83.getDataTypeSpecification().getDataType());
		final String defaultLeaf83 = attr83.getDefaultValue();
		assertNull(defaultLeaf83);

		final PrimaryTypeAttributeSpecification attr84 = findAttribute(cont8spec, "leaf84");
		assertEquals(DataType.ENUM_REF, attr84.getDataTypeSpecification().getDataType());
		final String defaultLeaf84 = attr84.getDefaultValue();
		assertNull(defaultLeaf84);

		final PrimaryTypeAttributeSpecification attr85 = findAttribute(cont8spec, "leaf85");
		assertEquals(DataType.ENUM_REF, attr85.getDataTypeSpecification().getDataType());
		final String defaultLeaf85 = attr85.getDefaultValue();
		assertNull(defaultLeaf85);
	}

	private static final String MODULE_NS_TWO = "urn%test%simple-defaults-two";
	private static final String MODULE_XYZ_VERSION_TWO = "2021.11.1";

	@Test
	public void test___simple_defaults_two() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S392_INVALID_DEFAULT_VALUE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E623_CANT_USE_BOTH_DEFAULT_AND_INITIAL_VALUE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N671_UNSUPPORTED_EXTENSION.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "simple/simple-defaults-two.yang")));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont1", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(StringType.class, leaf11.getType());
		assertInstanceOf(StringValue.class, leaf11.getDefault());
		assertEquals("abc", ((StringValue) leaf11.getDefault()).getValue());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(StringType.class, leaf12.getType());
		assertInstanceOf(StringValue.class, leaf12.getDefault());
		assertEquals("def", ((StringValue) leaf12.getDefault()).getValue());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(StringType.class, leaf13.getType());
		assertInstanceOf(StringValue.class, leaf13.getDefault());
		assertEquals("ghi", ((StringValue) leaf13.getDefault()).getValue());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(StringType.class, leaf14.getType());
		assertInstanceOf(StringValue.class, leaf14.getDefault());
		assertEquals("def", ((StringValue) leaf14.getDefault()).getValue());

		final PrimaryTypeAttribute leaf15 = findAttribute(cont1, "leaf15");
		assertInstanceOf(StringType.class, leaf15.getType());
		assertInstanceOf(StringValue.class, leaf15.getDefault());
		assertEquals("ghi", ((StringValue) leaf15.getDefault()).getValue());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont2", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertInstanceOf(UnionType.class, leaf21.getType());
		assertInstanceOf(IntegerValue.class, leaf21.getDefault());
		assertEquals((Integer) 1000, ((IntegerValue) leaf21.getDefault()).getValue());

		final PrimaryTypeAttribute leaf22 = findAttribute(cont2, "leaf22");
		assertInstanceOf(UnionType.class, leaf22.getType());
		assertInstanceOf(StringValue.class, leaf22.getDefault());
		assertEquals("abc", ((StringValue) leaf22.getDefault()).getValue());

		final PrimaryTypeAttribute leaf23 = findAttribute(cont2, "leaf23");
		assertInstanceOf(UnionType.class, leaf23.getType());
		assertInstanceOf(StringValue.class, leaf23.getDefault());
		assertEquals("1000", ((StringValue) leaf23.getDefault()).getValue());

		final PrimaryTypeAttribute leaf24 = findAttribute(cont2, "leaf24");
		assertInstanceOf(UnionType.class, leaf24.getType());
		assertInstanceOf(StringValue.class, leaf24.getDefault());
		assertEquals(" YELLOW ", ((StringValue) leaf24.getDefault()).getValue());

		final PrimaryTypeAttribute leaf25 = findAttribute(cont2, "leaf25");
		assertInstanceOf(UnionType.class, leaf25.getType());
		assertNull(leaf25.getDefault());

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont3", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);

		final PrimaryTypeAttribute leaflist31 = findAttribute(cont3, "leaflist31");
		assertInstanceOf(ListType.class, leaflist31.getType());
		assertInstanceOf(StringType.class, ((ListType) leaflist31.getType()).getCollectionValuesType());
		assertInstanceOf(CollectionValue.class, leaflist31.getDefault());
		assertSize(3, ((CollectionValue) leaflist31.getDefault()).getValues().getValue());
		assertInstanceOf(StringValue.class, ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(0));
		assertInstanceOf(StringValue.class, ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(1));
		assertInstanceOf(StringValue.class, ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(2));
		assertEquals("abc", ((StringValue) ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(0)).getValue());
		assertEquals("def", ((StringValue) ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(1)).getValue());
		assertEquals("ghi", ((StringValue) ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(2)).getValue());

		final PrimaryTypeAttribute leaflist32 = findAttribute(cont3, "leaflist32");
		assertInstanceOf(ListType.class, leaflist32.getType());
		assertInstanceOf(IntegerType.class, ((ListType) leaflist32.getType()).getCollectionValuesType());
		assertNull(leaflist32.getDefault());

		final PrimaryTypeAttribute leaflist33 = findAttribute(cont3, "leaflist33");
		assertInstanceOf(ListType.class, leaflist33.getType());
		assertInstanceOf(UnionType.class, ((ListType) leaflist33.getType()).getCollectionValuesType());
		assertInstanceOf(CollectionValue.class, leaflist33.getDefault());
		assertSize(4, ((CollectionValue) leaflist33.getDefault()).getValues().getValue());
		assertInstanceOf(IntegerValue.class, ((CollectionValue) leaflist33.getDefault()).getValues().getValue().get(0));
		assertInstanceOf(StringValue.class, ((CollectionValue) leaflist33.getDefault()).getValues().getValue().get(1));
		assertInstanceOf(IntegerValue.class, ((CollectionValue) leaflist33.getDefault()).getValues().getValue().get(2));
		assertInstanceOf(StringValue.class, ((CollectionValue) leaflist33.getDefault()).getValues().getValue().get(3));
		assertEquals((Integer) 1000, ((IntegerValue) ((CollectionValue) leaflist33.getDefault()).getValues().getValue().get(0)).getValue());
		assertEquals("abc", ((StringValue) ((CollectionValue) leaflist33.getDefault()).getValues().getValue().get(1)).getValue());
		assertEquals((Integer) 3500, ((IntegerValue) ((CollectionValue) leaflist33.getDefault()).getValues().getValue().get(2)).getValue());
		assertEquals("def", ((StringValue) ((CollectionValue) leaflist33.getDefault()).getValues().getValue().get(3)).getValue());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.STRING, attr11.getDataTypeSpecification().getDataType());
		final String defaultLeaf11 = attr11.getDefaultValue();
		assertEquals("abc", defaultLeaf11);

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.STRING, attr12.getDataTypeSpecification().getDataType());
		final String defaultLeaf12 = attr12.getDefaultValue();
		assertEquals("def", defaultLeaf12);

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.STRING, attr13.getDataTypeSpecification().getDataType());
		final String defaultLeaf13 = attr13.getDefaultValue();
		assertEquals("ghi", defaultLeaf13);

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.STRING, attr14.getDataTypeSpecification().getDataType());
		final String defaultLeaf14 = attr14.getDefaultValue();
		assertEquals("def", defaultLeaf14);

		final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
		assertEquals(DataType.STRING, attr15.getDataTypeSpecification().getDataType());
		final String defaultLeaf15 = attr15.getDefaultValue();
		assertEquals("ghi", defaultLeaf15);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertEquals(DataType.UNION, attr21.getDataTypeSpecification().getDataType());
		final Integer defaultLeaf21 = attr21.getDefaultValue();
		assertEquals((Integer) 1000, defaultLeaf21);

		final PrimaryTypeAttributeSpecification attr22 = findAttribute(cont2spec, "leaf22");
		assertEquals(DataType.UNION, attr22.getDataTypeSpecification().getDataType());
		final String defaultLeaf22 = attr22.getDefaultValue();
		assertEquals("abc", defaultLeaf22);

		final PrimaryTypeAttributeSpecification attr23 = findAttribute(cont2spec, "leaf23");
		assertEquals(DataType.UNION, attr23.getDataTypeSpecification().getDataType());
		final String defaultLeaf23 = attr23.getDefaultValue();
		assertEquals("1000", defaultLeaf23);

		final PrimaryTypeAttributeSpecification attr24 = findAttribute(cont2spec, "leaf24");
		assertEquals(DataType.UNION, attr24.getDataTypeSpecification().getDataType());
		final String defaultLeaf24 = attr24.getDefaultValue();
		assertEquals(" YELLOW ", defaultLeaf24);

		final PrimaryTypeAttributeSpecification attr25 = findAttribute(cont2spec, "leaf25");
		assertEquals(DataType.UNION, attr25.getDataTypeSpecification().getDataType());
		final String defaultLeaf25 = attr25.getDefaultValue();
		assertNull(defaultLeaf25);

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont3spec, "leaflist31");
		assertEquals(DataType.LIST, attr31.getDataTypeSpecification().getDataType());
		assertEquals(DataType.STRING, attr31.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		final List<String> defaultLeaf31 = attr31.getDefaultValue();
		assertSize(3, defaultLeaf31);
		assertEquals("abc", defaultLeaf31.get(0));
		assertEquals("def", defaultLeaf31.get(1));
		assertEquals("ghi", defaultLeaf31.get(2));

		final PrimaryTypeAttributeSpecification attr32 = findAttribute(cont3spec, "leaflist32");
		assertEquals(DataType.LIST, attr32.getDataTypeSpecification().getDataType());
		assertEquals(DataType.INTEGER, attr32.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		final List<Integer> defaultLeaf32 = attr32.getDefaultValue();
		assertNull(defaultLeaf32);

		final PrimaryTypeAttributeSpecification attr33 = findAttribute(cont3spec, "leaflist33");
		assertEquals(DataType.LIST, attr33.getDataTypeSpecification().getDataType());
		assertEquals(DataType.UNION, attr33.getDataTypeSpecification().getValuesDataTypeSpecification().getDataType());
		final List<Object> defaultLeaf33 = attr33.getDefaultValue();
		assertSize(4, defaultLeaf33);
		assertInstanceOf(Integer.class, defaultLeaf33.get(0));
		assertEquals((Integer) 1000, ((Integer) defaultLeaf33.get(0)));
		assertInstanceOf(String.class, defaultLeaf33.get(1));
		assertEquals("abc", ((String) defaultLeaf33.get(1)));
		assertInstanceOf(Integer.class, defaultLeaf33.get(2));
		assertEquals((Integer) 3500, ((Integer) defaultLeaf33.get(2)));
		assertInstanceOf(String.class, defaultLeaf33.get(3));
		assertEquals("def", ((String) defaultLeaf33.get(3)));
	}
}
