package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate._3gpp;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class DuplicateMoc3gppTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-3gpp/";

	private static final String MODULE_NS = "urn:test:duplicate-moc-3gpp";
	private static final String MODULE_NAME = "duplicate-moc-3gpp";
	private static final String MODULE_REVISION = "2023-06-09";
	private static final String MODULE_XYZ_VERSION = "2023.6.9";

	@Test
	public void test___duplicate_moc_3gpp___apply_3gpp_handling() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "duplicate-moc-3gpp"));
		context.setApply3gppHandling(true);
		context.setFeatureHandling(FeatureHandling.RETAIN_ALL);

		YangTransformer2.transform(context);

		internal___duplicate_moc_3gpp___apply_3gpp_handling(context, null);
	}

	@Test
	public void test___duplicate_moc_3gpp___apply_3gpp_handling___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_3GPP_HANDLING, " true ");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "duplicate-moc-3gpp"), overwritingProperties);

		internal___duplicate_moc_3gpp___apply_3gpp_handling(null, actualNmtProperties);
	}

	private void internal___duplicate_moc_3gpp___apply_3gpp_handling(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(5, 0, 0, 0, 2, 0);

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------


		final ModelInfo moc1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "Moc1", MODULE_XYZ_VERSION);
		assertModelExists(moc1modelInfo);

		final ModelInfo moc2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "Moc2", MODULE_XYZ_VERSION);
		assertModelExists(moc2modelInfo);

		final ModelInfo moc3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "Moc3", MODULE_XYZ_VERSION);
		assertModelExists(moc3modelInfo);

		final ModelInfo moc4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "Moc4", MODULE_XYZ_VERSION);
		assertModelExists(moc4modelInfo);

		final ModelInfo moc7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "Moc7", MODULE_XYZ_VERSION);
		assertModelExists(moc7modelInfo);

		// -----------------------------------------------------

		final ModelInfo miRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(miRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 7);

		final PrimaryTypeContainment managedElementToMoc1 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "Moc1");
		assertContainment(managedElementToMoc1, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "Moc1", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment managedElementToMoc2 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "Moc2");
		assertContainment(managedElementToMoc2, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "Moc2", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment managedElementToMoc3 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "Moc3");
		assertContainment(managedElementToMoc3, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "Moc3", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment moc3toMoc4 = findAndAssertContainment(rels, "Moc3", "Moc4");
		assertContainment(moc3toMoc4, context, actualNmtProperties, MODULE_NS, "Moc3", MODULE_XYZ_VERSION, MODULE_NS, "Moc4", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment moc1toMoc7 = findAndAssertContainment(rels, "Moc1", "Moc7");
		assertContainment(moc1toMoc7, context, actualNmtProperties, MODULE_NS, "Moc1", MODULE_XYZ_VERSION, MODULE_NS, "Moc7", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment moc2toMoc7 = findAndAssertContainment(rels, "Moc2", "Moc7");
		assertContainment(moc2toMoc7, context, actualNmtProperties, MODULE_NS, "Moc2", MODULE_XYZ_VERSION, MODULE_NS, "Moc7", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment moc4toMoc7 = findAndAssertContainment(rels, "Moc4", "Moc7");
		assertContainment(moc4toMoc7, context, actualNmtProperties, MODULE_NS, "Moc4", MODULE_XYZ_VERSION, MODULE_NS, "Moc7", MODULE_XYZ_VERSION, null, null);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification moc1spec = modelService.getTypedAccess().getEModelSpecification(moc1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertNotNull(moc1spec);

		final HierarchicalPrimaryTypeSpecification moc2spec = modelService.getTypedAccess().getEModelSpecification(moc2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertNotNull(moc2spec);

		final HierarchicalPrimaryTypeSpecification moc3spec = modelService.getTypedAccess().getEModelSpecification(moc3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertNotNull(moc3spec);

		final HierarchicalPrimaryTypeSpecification moc4spec = modelService.getTypedAccess().getEModelSpecification(moc4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertNotNull(moc4spec);

		final HierarchicalPrimaryTypeSpecification moc7spec = modelService.getTypedAccess().getEModelSpecification(moc7modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertNotNull(moc7spec);

		// -----------------------------------------------------

		assertSingleContainmentParent(moc1spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(moc2spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(moc3spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(moc4spec, moc3modelInfo, null, null);

		assertSize(3, moc7spec.getParentTypes());
		assertHasContainmentParent(moc7spec, moc1modelInfo, null, null);
		assertHasContainmentParent(moc7spec, moc2modelInfo, null, null);
		assertHasContainmentParent(moc7spec, moc4modelInfo, null, null);
	}
}
