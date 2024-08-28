package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.basics;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class ContainerMinCardinalityTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-basics/";

	private static final String MODULE_NS = "urn:test:container-min-cardinality";
	private static final String MODULE_XYZ_VERSION = "2021.10.11";

	@Test
	public void test___simple_generation___np_containers_not_system_created() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "container-min-cardinality"));
		YangTransformer2.transform(context);

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);
		assertNotNull(rels.getModelCreationInfo().getDesignedModel());

		final PrimaryTypeContainment managedElementToCont1 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		assertContainment(managedElementToCont1, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "cont1", MODULE_XYZ_VERSION, 1L, 1L);

		final PrimaryTypeContainment managedElementToCont2 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont2");
		assertContainment(managedElementToCont2, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "cont2", MODULE_XYZ_VERSION, 1L, 1L);

		final PrimaryTypeContainment managedElementToCont3 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont3");
		assertContainment(managedElementToCont3, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "cont3", MODULE_XYZ_VERSION, null, 1L);

		final PrimaryTypeContainment managedElementToCont4 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont4");
		assertContainment(managedElementToCont4, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "cont4", MODULE_XYZ_VERSION, null, 1L);

		final PrimaryTypeContainment managedElementToCont5 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont5");
		assertContainment(managedElementToCont5, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "cont5", MODULE_XYZ_VERSION, null, 1L);

		final PrimaryTypeContainment managedElementToCont6 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont6");
		assertContainment(managedElementToCont6, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "cont6", MODULE_XYZ_VERSION, 1L, 1L);

		final PrimaryTypeContainment managedElementToCont7 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont7");
		assertContainment(managedElementToCont7, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "cont7", MODULE_XYZ_VERSION, null, 1L);
	}
}
