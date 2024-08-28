package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.compatibility;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class DataTypeCompatibilityTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "compatibility/";

	private static final String MODULE_NS_COMP = "urn::test::data-type-compatibility";
	private static final String MODULE_XYZ_VERSION_COMP = "2021.4.21";

	/**
	 * Tests data type changes by means of deviations, and that these are correctly generated and reflected in MS then.
	 */
	@Test
	public void test___data_type_compatibility(){

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P057_DATA_TYPE_CHANGED.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "data-type-compatibility"));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_COMP, "cont1", MODULE_XYZ_VERSION_COMP);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(StringType.class, leaf11.getType());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(StringType.class, leaf12.getType());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(LongType.class, leaf13.getType());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(LongType.class, leaf14.getType());

		final PrimaryTypeAttribute leaf15 = findAttribute(cont1, "leaf15");
		assertInstanceOf(EnumRefType.class, leaf15.getType());

		final PrimaryTypeAttribute leaf16 = findAttribute(cont1, "leaf16");
		assertInstanceOf(EnumRefType.class, leaf16.getType());

		// - - - - - - - - - - - - - -

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_COMP, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);

		assertEmpty(cont1ext.getPrimaryTypeAttributeRemoval());
		assertSize(6, cont1ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf11ext = findReplacedAttribute(cont1ext, "leaf11");
		assertInstanceOf(BooleanType.class, leaf11ext.getType());

		final PrimaryTypeAttribute leaf12ext = findReplacedAttribute(cont1ext, "leaf12");
		assertInstanceOf(IntegerType.class, leaf12ext.getType());

		final PrimaryTypeAttribute leaf13ext = findReplacedAttribute(cont1ext, "leaf13");
		assertInstanceOf(IntegerType.class, leaf13ext.getType());

		final PrimaryTypeAttribute leaf14ext = findReplacedAttribute(cont1ext, "leaf14");
		assertInstanceOf(BooleanType.class, leaf14ext.getType());

		final PrimaryTypeAttribute leaf15ext = findReplacedAttribute(cont1ext, "leaf15");
		assertInstanceOf(StringType.class, leaf15ext.getType());

		final PrimaryTypeAttribute leaf16ext = findReplacedAttribute(cont1ext, "leaf16");
		assertInstanceOf(IntegerType.class, leaf16ext.getType());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		assertEquals(DataType.BOOLEAN, findAttribute(cont1spec, "leaf11").getDataTypeSpecification().getDataType());
		assertEquals(DataType.INTEGER, findAttribute(cont1spec, "leaf12").getDataTypeSpecification().getDataType());
		assertEquals(DataType.INTEGER, findAttribute(cont1spec, "leaf13").getDataTypeSpecification().getDataType());
		assertEquals(DataType.BOOLEAN, findAttribute(cont1spec, "leaf14").getDataTypeSpecification().getDataType());
		assertEquals(DataType.STRING, findAttribute(cont1spec, "leaf15").getDataTypeSpecification().getDataType());
		assertEquals(DataType.INTEGER, findAttribute(cont1spec, "leaf16").getDataTypeSpecification().getDataType());
	}
}
