package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class TwoNrmVersionsTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-basics/two-nrm-versions/";

	private static final String MODULE_NS_BASE = "urn:test:two-nrm-versions-base";
	private static final String MODULE_XYZ_VERSION_BASE = "2021.10.11";

	@Test
	public void test___two_nrm_versions() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P057_DATA_TYPE_CHANGED.toString());

		/*
		 * We are generating two different NRMs (with different target types and target version),
		 * deploy these and see what happens. Both use an augmentation to inject an attribute,
		 * but both nodes use a different data type. 
		 */
		final TransformerContext context1 = createContext(Arrays.asList(new File(TEST_SUB_DIR + "two-nrm-versions-base.yang"), new File(TEST_SUB_DIR + "two-nrm-versions-augmentations.yang"), new File(TEST_SUB_DIR + "two-nrm-versions-deviations-one.yang")), Collections.emptyList(), GENERATED_MODELS_ETC_MODEL_DIR, "vDU", "Q4.22", true);
		YangTransformer2.transform(context1);

		final TransformerContext context2 = createContext(Arrays.asList(new File(TEST_SUB_DIR + "two-nrm-versions-base.yang"), new File(TEST_SUB_DIR + "two-nrm-versions-augmentations.yang"), new File(TEST_SUB_DIR + "two-nrm-versions-deviations-two.yang")), Collections.emptyList(), GENERATED_MODELS_ETC_MODEL_DIR, "vCU-UP", "Q2.20", true);
		YangTransformer2.transform(context2);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_BASE, "cont1", MODULE_XYZ_VERSION_BASE);

		{
			/*
			 * Asserts for Target for deviation ONE (vDU)
			 */

			final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context1, null));

			final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
			assertEquals(DataType.LONG, attr15.getDataTypeSpecification().getDataType());
			assertTrue(attr15.getDataTypeSpecification().isUnsigned());

			final PrimaryTypeAttributeSpecification attr16 = findAttribute(cont1spec, "leaf16");
			assertEquals(DataType.LONG, attr16.getDataTypeSpecification().getDataType());
			assertFalse(attr16.getDataTypeSpecification().isUnsigned());
		}

		{
			/*
			 * Asserts for Target for deviation TWO (vCU-UP)
			 */

			final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context2, null));

			final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
			assertEquals(DataType.BOOLEAN, attr15.getDataTypeSpecification().getDataType());

			final PrimaryTypeAttributeSpecification attr16 = findAttribute(cont1spec, "leaf16");
			assertEquals(DataType.BITS, attr16.getDataTypeSpecification().getDataType());
		}
	}
}
