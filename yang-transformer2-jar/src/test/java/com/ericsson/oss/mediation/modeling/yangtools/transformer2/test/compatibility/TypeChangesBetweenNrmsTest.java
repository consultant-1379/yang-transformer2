package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.compatibility;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.Target;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class TypeChangesBetweenNrmsTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "compatibility/";

	private static final String MODULE_NS = "urn:test:type-changes-module";
	private static final String MODULE_NRM1_XYZ_VERSION = "2021.11.30";
	private static final String MODULE_NRM2_XYZ_VERSION = "2021.12.1";

	/**
	 * Tests that two different versions of the same MOC, with the same attributes but different data
	 * types, are successfully generated (which is the easy bit) but also, and more importantly, that
	 * they get processed and deployed into Model Service correctly. Really, tests more Model Service
	 * and the modeling infrastructure than transformation, but these scenarios are likely to happen
	 * in reality so no harm testing for these here in Transformer.
	 */
	@Test
	public void test___deploy_two_nrms_with_data_type_changes_between_versions() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());

		final TransformerContext context1 = createContext(Arrays.asList(new File(TEST_SUB_DIR + "type-changes-between-nrms/type-changes-module@2021-11-30.yang")), Collections.emptyList(), DEFAULT_TARGET_TYPE, "21.Q4", true);
		final TransformerContext context2 = createContext(Arrays.asList(new File(TEST_SUB_DIR + "type-changes-between-nrms/type-changes-module@2021-12-01.yang")), Collections.emptyList(), DEFAULT_TARGET_TYPE, "22.Q1", true);
		YangTransformer2.transform(context1);
		YangTransformer2.transform(context2);

		// -----------------------------------------------------

		final ModelInfo nrm1cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_NRM1_XYZ_VERSION);
		final PrimaryTypeDefinition nrmCont1 = load(nrm1cont1modelInfo);

		final PrimaryTypeAttribute nrm1leaf11 = findAttribute(nrmCont1, "leaf11");
		assertInstanceOf(StringType.class, nrm1leaf11.getType());
		final PrimaryTypeAttribute nrm1leaf12 = findAttribute(nrmCont1, "leaf12");
		assertInstanceOf(BooleanType.class, nrm1leaf12.getType());

		// -----------------------------------------------------

		final ModelInfo nrm2cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_NRM2_XYZ_VERSION);
		final PrimaryTypeDefinition nrmCont2 = load(nrm2cont1modelInfo);

		final PrimaryTypeAttribute nrm2leaf11 = findAttribute(nrmCont2, "leaf11");
		assertInstanceOf(IntegerType.class, nrm2leaf11.getType());
		final PrimaryTypeAttribute nrm2leaf12 = findAttribute(nrmCont2, "leaf12");
		assertInstanceOf(EnumRefType.class, nrm2leaf12.getType());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		/*
		 * Note: Both NRMs are deployed here, with two different versions for "cont1".
		 */
		processAndDeployIntoModelService();

		final Target targetNrm1 = new Target(DEFAULT_TARGET_CATEGORY, DEFAULT_TARGET_TYPE, "name", "21.Q4");
		final Target targetNrm2 = new Target(DEFAULT_TARGET_CATEGORY, DEFAULT_TARGET_TYPE, "name", "22.Q1");

		final HierarchicalPrimaryTypeSpecification nrm1cont1spec = modelService.getTypedAccess().getEModelSpecification(nrm1cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, targetNrm1);

		final PrimaryTypeAttributeSpecification nrm1attr11 = findAttribute(nrm1cont1spec, "leaf11");
		assertEquals(DataType.STRING, nrm1attr11.getDataTypeSpecification().getDataType());
		final PrimaryTypeAttributeSpecification nrm1attr12 = findAttribute(nrm1cont1spec, "leaf12");
		assertEquals(DataType.BOOLEAN, nrm1attr12.getDataTypeSpecification().getDataType());

		final HierarchicalPrimaryTypeSpecification nrm2cont1spec = modelService.getTypedAccess().getEModelSpecification(nrm2cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, targetNrm2);

		final PrimaryTypeAttributeSpecification nrm2attr11 = findAttribute(nrm2cont1spec, "leaf11");
		assertEquals(DataType.INTEGER, nrm2attr11.getDataTypeSpecification().getDataType());
		final PrimaryTypeAttributeSpecification nrm2attr12 = findAttribute(nrm2cont1spec, "leaf12");
		assertEquals(DataType.ENUM_REF, nrm2attr12.getDataTypeSpecification().getDataType());
	}
}
