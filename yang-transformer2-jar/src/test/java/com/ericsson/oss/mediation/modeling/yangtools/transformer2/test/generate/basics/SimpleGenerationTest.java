package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class SimpleGenerationTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-basics/";

	private static final String MODULE_NS = "urn:test:simple-generation";
	private static final String MODULE_NAME = "simple-generation";
	private static final String MODULE_REVISION = "2021-10-11";
	private static final String MODULE_XYZ_VERSION = "2021.10.11";

	@Test
	public void test___yang_transformer_name__and_version() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-generation"));
		YangTransformer2.transform(context);

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);

		assertHasMeta("YangTransformer2", "1.1.4", mimInfo);
	}

	@Test
	public void test___simple_generation___np_containers_not_system_created() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-generation"));
		context.setGenerateNPcontainersAsSystemCreated(false);
		context.setFeatureHandling(FeatureHandling.REMOVE_ALL);

		YangTransformer2.transform(context);

		final File reportFile = new File(context.getOutDir().getParentFile(), YangTransformer2.REPORT_FILE_NAME);
		assertTrue(reportFile.exists());
		assertTrue(reportFile.length() > 0);

		internal___simple_generation___np_containers_not_system_created(context, null);
	}

	@Test
	public void test___simple_generation___np_containers_not_system_created_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_NP_CONTAINER_AS_SYSTEM_CREATED, "false");
		overwritingProperties.setProperty(YangTransformer2PluginProperties.IF_FEATURE_HANDLING, "remove-all");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "simple-generation"), overwritingProperties);

		final File reportFile = new File(new File(GENERATED_MODELS_BASE_DIR, "etc"), YangTransformer2.REPORT_FILE_NAME);
		assertTrue(reportFile.exists());
		assertTrue(reportFile.length() > 0);

		internal___simple_generation___np_containers_not_system_created(null, actualNmtProperties);
	}

	private void internal___simple_generation___np_containers_not_system_created(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(2, 0, 0, 0, 1, 0);

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		assertUsualValuesForPrimaryType(cont1);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont1);
		assertNull(cont1.getSystemCreated());
		assertNotNull(cont1.getModelCreationInfo().getDerivedModel());
		assertEquals(modelInfoForYangModule.toUrn(), cont1.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, cont1);
		assertHasMeta(Constants.META_NON_PRESENCE_CONTAINER, cont1);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, cont1);
		assertHasAutogeneratedKey(cont1, "cont1", WriteBehaviorType.PERSIST_AND_DELEGATE);
		assertSize(1, cont1.getPrimaryTypeAttribute());
		assertEmpty(cont1.getPrimaryTypeAction());

		// -----------------------------------------------------

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		assertUsualValuesForPrimaryType(cont2);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont2);
		assertNull(cont2.getSystemCreated());
		assertNotNull(cont2.getModelCreationInfo().getDerivedModel());
		assertTrue(cont2.getModelCreationInfo().getDerivedModel().getDerivedFrom().equals(modelInfoForYangModule.toUrn()));

		assertHasMeta(Constants.META_PRESENCE_CONTAINER, cont2);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, cont2);
		assertHasAutogeneratedKey(cont2, "cont2", WriteBehaviorType.PERSIST_AND_DELEGATE);
		assertSize(2, cont2.getPrimaryTypeAttribute());
		assertEmpty(cont2.getPrimaryTypeAction());

		final PrimaryTypeAttribute leaf3 = findAttribute(cont2, "leaf3");
		assertNotNull(leaf3);
		assertInstanceOf(StringType.class, leaf3.getType());

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);
		assertNotNull(rels.getModelCreationInfo().getDesignedModel());

		assertContainmentsCreatedCount(rels, 2);

		final PrimaryTypeContainment managedElementToCont1 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		assertContainment(managedElementToCont1, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS, "cont1", MODULE_XYZ_VERSION, null, 1L);

		final PrimaryTypeContainment cont1ToCont2 = findAndAssertContainment(rels, "cont1", "cont2");
		assertContainment(cont1ToCont2, context, actualNmtProperties, MODULE_NS, "cont1", MODULE_XYZ_VERSION, MODULE_NS, "cont2", MODULE_XYZ_VERSION, null, 1L);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertNotNull(mimInfo.getModelCreationInfo().getDesignedModel());
		assertSize(1, mimInfo.getSupportedMims().getMimMappedTo());

		assertHasSupportedMimModule(mimInfo, MODULE_NS, MODULE_XYZ_VERSION, modelInfoForYangModule.toUrn(), MODULE_NAME);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertNotNull(cont1spec);
		assertFalse(cont1spec.isSystemCreated());
		assertHasMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont1spec);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertNotNull(cont2spec);
		assertFalse(cont2spec.isSystemCreated());
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont2spec);

		assertSingleContainmentParent(cont1spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont2spec, cont1modelInfo, null, null);
	}

	@Test
	public void test___simple_generation___np_containers_system_created() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-generation"));
		context.setGenerateNPcontainersAsSystemCreated(true);

		YangTransformer2.transform(context);

		internal___simple_generation___np_containers_system_created(context, null);
	}

	@Test
	public void test___simple_generation___np_containers_system_created_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_NP_CONTAINER_AS_SYSTEM_CREATED, " true ");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "simple-generation"), overwritingProperties);

		internal___simple_generation___np_containers_system_created(null, actualNmtProperties);
	}

	private void internal___simple_generation___np_containers_system_created(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(2, 0, 0, 0, 1, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		assertNotNull(cont1.getSystemCreated());

		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, cont1);
		assertHasMeta(Constants.META_NON_PRESENCE_CONTAINER, cont1);

		// -----------------------------------------------------

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		assertNull(cont2.getSystemCreated());		// correct - cont2 is not NP, so can never be system-created.

		assertHasMeta(Constants.META_PRESENCE_CONTAINER, cont2);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, cont2);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertTrue(cont1spec.isSystemCreated());
		assertHasNotMetaInModelService(Constants.META_PRESENCE_CONTAINER, cont1spec);
		assertHasMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont1spec);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertFalse(cont2spec.isSystemCreated());
		assertHasMetaInModelService(Constants.META_PRESENCE_CONTAINER, cont2spec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont2spec);

		assertSingleContainmentParent(cont1spec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont2spec, cont1modelInfo, null, null);
	}
}
