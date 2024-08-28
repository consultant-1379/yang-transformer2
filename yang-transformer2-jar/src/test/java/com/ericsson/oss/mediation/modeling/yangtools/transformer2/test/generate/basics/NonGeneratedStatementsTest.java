package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.basics;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;

public class NonGeneratedStatementsTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-basics/non-generated-statements/";

	private static final String MODULE_SIMPLE_NS = "urn:test:non-generated-simple";
	private static final String MODULE_COMPLEX_NS = "urn:test:non-generated-complex";
	private static final String MODULE_COMPLEX_EXT_NS = "urn:test:non-generated-complex-ext";

	@Test
	public void test___non_generated_simple_notified_static() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "non-generated-simple")));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		YangTransformer2.transform(context);

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);
		assertRequiresTargetType(context, null, containmentParentExt.getRequiresTargetType());

		assertNonGeneratedDataNodeCount(5, containmentParentExt.getMeta());

		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "cont2"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "leaf11"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "cont2", MODULE_SIMPLE_NS, "leaf21"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "cont2", MODULE_SIMPLE_NS, "cont3"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "list4"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "list5"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "list5", MODULE_SIMPLE_NS, "leaf51"}, containmentParentExt.getMeta());

		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont6"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont6", MODULE_SIMPLE_NS, "leaf61"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont6", MODULE_SIMPLE_NS, "leaf62"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont6", MODULE_SIMPLE_NS, "cont7"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont6", MODULE_SIMPLE_NS, "cont7", MODULE_SIMPLE_NS, "leaf71"}, containmentParentExt.getMeta());

		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "list8"}, containmentParentExt.getMeta());

		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont9"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont9", MODULE_SIMPLE_NS, "leaf91"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont9", MODULE_SIMPLE_NS, "cont10"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont9", MODULE_SIMPLE_NS, "cont11"}, containmentParentExt.getMeta());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification meContextSpec = modelService.getTypedAccess().getEModelSpecification(OSS_TOP_ME_CONTEXT_300, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertNotNull(meContextSpec);

		assertNonGeneratedDataNodeCountInModelService(5, meContextSpec.getMetaInformation());

		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "cont2"}, meContextSpec.getMetaInformation());
		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "list5"}, meContextSpec.getMetaInformation());
		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "cont6", MODULE_SIMPLE_NS, "cont7"}, meContextSpec.getMetaInformation());
		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "list8"}, meContextSpec.getMetaInformation());
		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "cont9", MODULE_SIMPLE_NS, "cont11"}, meContextSpec.getMetaInformation());
	}

	@Test
	public void test___non_generated_simple_notified_static___remove_if_feature_nodes() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "non-generated-simple")));
		context.setMockManagedElementNamespace(DEFAULT_MOCK_ME.getNamespace());
		context.setFeatureHandling(FeatureHandling.REMOVE_ALL);
		YangTransformer2.transform(context);

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);
		assertRequiresTargetType(context, null, containmentParentExt.getRequiresTargetType());

		assertNonGeneratedDataNodeCount(5, containmentParentExt.getMeta());

		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont6", MODULE_SIMPLE_NS, "cont7"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "list8"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont9", MODULE_SIMPLE_NS, "cont10"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont9", MODULE_SIMPLE_NS, "cont11"}, containmentParentExt.getMeta());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification meContextSpec = modelService.getTypedAccess().getEModelSpecification(OSS_TOP_ME_CONTEXT_300, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertNotNull(meContextSpec);

		assertNonGeneratedDataNodeCountInModelService(5, meContextSpec.getMetaInformation());

		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "cont1"}, meContextSpec.getMetaInformation());
		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "cont6", MODULE_SIMPLE_NS, "cont7"}, meContextSpec.getMetaInformation());
		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "list8"}, meContextSpec.getMetaInformation());
		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "cont9", MODULE_SIMPLE_NS, "cont10"}, meContextSpec.getMetaInformation());
		assertHasNonGeneratedDataNodeInModelService(new String[] {MODULE_SIMPLE_NS, "cont9", MODULE_SIMPLE_NS, "cont11"}, meContextSpec.getMetaInformation());
	}

	@Test
	public void test___non_generated_simple_notified_static___explicit_remove_of_some_data_nodes() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "non-generated-simple")));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		final List<String> removed = Arrays.asList("/cont1/list4", "/cont6", "/rpc1/input/cont13");
		context.setSchemaNodesToRemove(removed);
		context.setGenerateRpcs(true);
		YangTransformer2.transform(context);

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);
		assertRequiresTargetType(context, null, containmentParentExt.getRequiresTargetType());

		assertNonGeneratedDataNodeCount(6, containmentParentExt.getMeta());

		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "cont2"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "list4"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont1", MODULE_SIMPLE_NS, "list5"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont6"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "list8"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_SIMPLE_NS, "cont9", MODULE_SIMPLE_NS, "cont11"}, containmentParentExt.getMeta());
	}

	@Test
	public void test___non_generated_complex() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "non-generated-complex")));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		final List<String> removed = Arrays.asList("/cont10/choice1/case1/cont11", "/cont10/action1/input/cont13");
		context.setSchemaNodesToRemove(removed);
		YangTransformer2.transform(context);

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);
		assertRequiresTargetType(context, null, containmentParentExt.getRequiresTargetType());

		assertNonGeneratedDataNodeCount(7, containmentParentExt.getMeta());

		assertHasNonGeneratedDataNode(new String[] {MODULE_COMPLEX_NS, "cont1", MODULE_COMPLEX_NS, "cont2"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_COMPLEX_NS, "cont1", MODULE_COMPLEX_NS, "list4"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_COMPLEX_NS, "cont5", MODULE_COMPLEX_NS, "cont6"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_COMPLEX_NS, "cont10", MODULE_COMPLEX_NS, "cont11"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_COMPLEX_NS, "cont1", MODULE_COMPLEX_EXT_NS, "cont22", MODULE_COMPLEX_EXT_NS, "cont23"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_COMPLEX_NS, "cont1", MODULE_COMPLEX_EXT_NS, "cont24", MODULE_COMPLEX_EXT_NS, "cont25"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE_COMPLEX_EXT_NS, "cont21"}, containmentParentExt.getMeta());
	}
}
