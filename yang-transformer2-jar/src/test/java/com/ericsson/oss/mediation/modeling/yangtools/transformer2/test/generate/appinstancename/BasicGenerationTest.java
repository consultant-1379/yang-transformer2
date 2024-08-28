package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.appinstancename;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.SupportedModelInformation;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2Plugin;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.SupportedMimType;

public class BasicGenerationTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-app-instance-name/";

	private static final String MODULE_NS_ORIG = "urn:ericsson:eric-ims-bgf---london3---2.2.5";
	private static final String MODULE_NAME_ORIG = "eric-ims-bgf---london3---2.2.5";
	private static final String MODULE_NS_CLEANED = "urn:ericsson:eric-ims-bgf-2.2.5";
	private static final String MODULE_NAME_CLEANED = "eric-ims-bgf-2.2.5";
	private static final String MODULE_REVISION = "2021-11-18";
	private static final String MODULE_XYZ_VERSION = "1.2.3";

	@Test
	public void test___handle_app_instance_name___basic_generation() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "basic")));
		context.setApplyNodeAppInstanceNameHandling(true);

		YangTransformer2.transform(context);

		internal___handle_app_instance_name___basic_generation(context, null);
	}

	@Test
	public void test___handle_app_instance_name___basic_generation___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_NODE_APP_INSTANCE_NAME_HANDLING, "true");

		final Properties actualNmtProperties = transformThroughNmtPluginWith3ppModules(new File(TEST_SUB_DIR + "basic"), overwritingProperties);

		internal___handle_app_instance_name___basic_generation(null, actualNmtProperties);
	}

	private void internal___handle_app_instance_name___basic_generation(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(4, 0, 0, 0, 5, 0);

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_CLEANED, MODULE_NAME_CLEANED, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ModelInfo londonModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_CLEANED, "eric-ims-bgf-2.2.5", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition london = load(londonModelInfo);

		assertUsualValuesForPrimaryType(london);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, london);
		assertNotNull(london.getSystemCreated() != null);
		assertEquals(modelInfoForYangModule.toUrn(), london.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, london);
		assertHasMeta(Constants.META_NON_PRESENCE_CONTAINER, london);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, london);
		assertHasMeta(Constants.META_REAL_NAME_ATTACHED_TO_DATA, london);
		assertHasMeta(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, london);

		final PrimaryTypeAttribute idLeaf = findAttribute(london.getPrimaryTypeAttribute(), "id");
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, idLeaf);
		assertTrue(idLeaf.isKey());
		assertTrue(idLeaf.isMandatory());

		// -----------------------------------------------------

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_CLEANED, "cont2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		assertUsualValuesForPrimaryType(cont2);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont2);
		assertNull(cont2.getSystemCreated());

		assertHasMeta(Constants.META_PRESENCE_CONTAINER, cont2);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, cont2);
		assertHasNotMeta(Constants.META_REAL_NAME_ATTACHED_TO_DATA, cont2);
		assertHasMeta(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, cont2);
		assertHasAutogeneratedKey(cont2, "cont2", WriteBehaviorType.PERSIST_AND_DELEGATE);

		// -----------------------------------------------------

		final ModelInfo newyorkModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_CLEANED, "eric-ims-bgf---newyork12---1.7.8", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition newyork = load(newyorkModelInfo);

		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, newyork);
		assertNull(newyork.getSystemCreated());

		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, newyork);
		assertHasMeta(Constants.META_NON_PRESENCE_CONTAINER, newyork);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, newyork);
		assertHasNotMeta(Constants.META_REAL_NAME_ATTACHED_TO_DATA, newyork);
		assertHasMeta( Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, newyork);

		// -----------------------------------------------------

		final ModelInfo parisModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_CLEANED, "eric-ims-bgf---paris7---3.2.6", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition paris = load(parisModelInfo);

		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, paris);
		assertNull(paris.getSystemCreated());

		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, paris);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, paris);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, paris);
		assertHasNotMeta(Constants.META_REAL_NAME_ATTACHED_TO_DATA, paris);
		assertHasMeta(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, paris);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 4);

		final PrimaryTypeContainment managedElementToLondon3 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "eric-ims-bgf-2.2.5");
		assertContainment(managedElementToLondon3, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_CLEANED, "eric-ims-bgf-2.2.5", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment managedElementToNewYork12 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "eric-ims-bgf---newyork12---1.7.8");
		assertContainment(managedElementToNewYork12, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_CLEANED, "eric-ims-bgf---newyork12---1.7.8", MODULE_XYZ_VERSION, null, 1L);

		final PrimaryTypeContainment managedElementToParis7 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "eric-ims-bgf---paris7---3.2.6");
		assertContainment(managedElementToParis7, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_CLEANED, "eric-ims-bgf---paris7---3.2.6", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment londonToCont2 = findAndAssertContainment(rels, "eric-ims-bgf-2.2.5", "cont2");
		assertContainment(londonToCont2, context, actualNmtProperties, MODULE_NS_CLEANED, "eric-ims-bgf-2.2.5", MODULE_XYZ_VERSION, MODULE_NS_CLEANED, "cont2", MODULE_XYZ_VERSION, null, 1L);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertHasSupportedMim(mimInfo, MODULE_NS_CLEANED, MODULE_XYZ_VERSION, modelInfoForYangModule.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification londonSpec = modelService.getTypedAccess().getEModelSpecification(londonModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertTrue(londonSpec.isSystemCreated());
		assertHasMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, londonSpec);
		assertHasMetaInModelService(Constants.META_REAL_NAME_ATTACHED_TO_DATA, londonSpec);
		assertHasMetaInModelService(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, londonSpec);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertFalse(cont2spec.isSystemCreated());
		assertHasMetaInModelService(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, cont2spec);
		assertHasNotMetaInModelService(Constants.META_REAL_NAME_ATTACHED_TO_DATA, cont2spec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont2spec);

		final HierarchicalPrimaryTypeSpecification newYorkSpec = modelService.getTypedAccess().getEModelSpecification(newyorkModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertFalse(newYorkSpec.isSystemCreated());
		assertHasMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, newYorkSpec);
		assertHasNotMetaInModelService(Constants.META_REAL_NAME_ATTACHED_TO_DATA, newYorkSpec);
		assertHasMetaInModelService(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, newYorkSpec);

		final HierarchicalPrimaryTypeSpecification parisSpec = modelService.getTypedAccess().getEModelSpecification(parisModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertFalse(parisSpec.isSystemCreated());
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, parisSpec);
		assertHasNotMetaInModelService(Constants.META_REAL_NAME_ATTACHED_TO_DATA, parisSpec);
		assertHasMetaInModelService(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, parisSpec);

		assertSingleContainmentParent(londonSpec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont2spec, londonModelInfo, null, null);
		assertSingleContainmentParent(newYorkSpec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(parisSpec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
	}

	@Test
	public void test___do_not_handle_app_instance_name___basic_generation() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "basic")));
		context.setApplyNodeAppInstanceNameHandling(false);

		YangTransformer2.transform(context);

		internal___do_not_handle_app_instance_name___basic_generation(context, null);
	}

	@Test
	public void test___do_not_handle_app_instance_name___basic_generation___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_NODE_APP_INSTANCE_NAME_HANDLING, "false");

		final Properties actualNmtProperties = transformThroughNmtPluginWith3ppModules(new File(TEST_SUB_DIR + "basic"), overwritingProperties);

		internal___do_not_handle_app_instance_name___basic_generation(null, actualNmtProperties);
	}

	private void internal___do_not_handle_app_instance_name___basic_generation(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(4, 0, 0, 0, 5, 0);

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ORIG, MODULE_NAME_ORIG, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ModelInfo londonModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ORIG, "eric-ims-bgf---london3---2.2.5", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition london = load(londonModelInfo);

		assertUsualValuesForPrimaryType(london);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, london);
		assertNull(london.getSystemCreated());
		assertEquals(modelInfoForYangModule.toUrn(), london.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, london);
		assertHasMeta(Constants.META_NON_PRESENCE_CONTAINER, london);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, london);
		assertHasNotMeta(Constants.META_REAL_NAME_ATTACHED_TO_DATA, london);
		assertHasNotMeta(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, london);
		assertHasAutogeneratedKey(london, "eric-ims-bgf---london3---2.2.5", WriteBehaviorType.PERSIST_AND_DELEGATE);

		// -----------------------------------------------------

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ORIG, "cont2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		assertUsualValuesForPrimaryType(cont2);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont2);
		assertNull(cont2.getSystemCreated());

		assertHasMeta(Constants.META_PRESENCE_CONTAINER, cont2);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, cont2);
		assertHasNotMeta(Constants.META_REAL_NAME_ATTACHED_TO_DATA, cont2);
		assertHasNotMeta(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, cont2);
		assertHasAutogeneratedKey(cont2, "cont2", WriteBehaviorType.PERSIST_AND_DELEGATE);

		// -----------------------------------------------------

		final ModelInfo newyorkModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ORIG, "eric-ims-bgf---newyork12---1.7.8", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition newyork = load(newyorkModelInfo);

		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, newyork);
		assertNull(newyork.getSystemCreated());

		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, newyork);
		assertHasMeta(Constants.META_NON_PRESENCE_CONTAINER, newyork);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, newyork);
		assertHasNotMeta(Constants.META_REAL_NAME_ATTACHED_TO_DATA, newyork);
		assertHasNotMeta(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, newyork);

		// -----------------------------------------------------

		final ModelInfo parisModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ORIG, "eric-ims-bgf---paris7---3.2.6", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition paris = load(parisModelInfo);

		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, paris);
		assertNull(paris.getSystemCreated());

		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, paris);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, paris);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, paris);
		assertHasNotMeta(Constants.META_REAL_NAME_ATTACHED_TO_DATA, paris);
		assertHasNotMeta(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, paris);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 4);

		final PrimaryTypeContainment managedElementToLondon3 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "eric-ims-bgf---london3---2.2.5");
		assertContainment(managedElementToLondon3, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_ORIG, "eric-ims-bgf---london3---2.2.5", MODULE_XYZ_VERSION, 1L, 1L);

		final PrimaryTypeContainment managedElementToNewYork12 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "eric-ims-bgf---newyork12---1.7.8");
		assertContainment(managedElementToNewYork12, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_ORIG, "eric-ims-bgf---newyork12---1.7.8", MODULE_XYZ_VERSION, null, 1L);

		final PrimaryTypeContainment managedElementToParis7 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "eric-ims-bgf---paris7---3.2.6");
		assertContainment(managedElementToParis7, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_ORIG, "eric-ims-bgf---paris7---3.2.6", MODULE_XYZ_VERSION, null, null);

		final PrimaryTypeContainment londonToCont2 = findAndAssertContainment(rels, "eric-ims-bgf---london3---2.2.5", "cont2");
		assertContainment(londonToCont2, context, actualNmtProperties, MODULE_NS_ORIG, "eric-ims-bgf---london3---2.2.5", MODULE_XYZ_VERSION, MODULE_NS_ORIG, "cont2", MODULE_XYZ_VERSION, null, 1L);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertHasSupportedMim(mimInfo, MODULE_NS_ORIG, MODULE_XYZ_VERSION, modelInfoForYangModule.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification londonSpec = modelService.getTypedAccess().getEModelSpecification(londonModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertFalse(londonSpec.isSystemCreated());
		assertHasMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, londonSpec);
		assertHasNotMetaInModelService(Constants.META_REAL_NAME_ATTACHED_TO_DATA, londonSpec);
		assertHasNotMetaInModelService(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, londonSpec);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertFalse(cont2spec.isSystemCreated());
		assertHasNotMetaInModelService(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, cont2spec);
		assertHasNotMetaInModelService(Constants.META_REAL_NAME_ATTACHED_TO_DATA, cont2spec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont2spec);

		final HierarchicalPrimaryTypeSpecification newYorkSpec = modelService.getTypedAccess().getEModelSpecification(newyorkModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertFalse(newYorkSpec.isSystemCreated());
		assertHasNotMetaInModelService(Constants.META_REAL_NAME_ATTACHED_TO_DATA, newYorkSpec);
		assertHasNotMetaInModelService(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, newYorkSpec);

		final HierarchicalPrimaryTypeSpecification parisSpec = modelService.getTypedAccess().getEModelSpecification(parisModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertFalse(parisSpec.isSystemCreated());
		assertHasNotMetaInModelService(Constants.META_REAL_NAME_ATTACHED_TO_DATA, parisSpec);
		assertHasNotMetaInModelService(Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA, parisSpec);

		assertSingleContainmentParent(londonSpec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(cont2spec, londonModelInfo, null, null);
		assertSingleContainmentParent(newYorkSpec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
		assertSingleContainmentParent(parisSpec, Constants.COM_TOP_MANAGEDELEMENT_STAR, null, null);
	}

	@Test
	public void test___generate_cfm_miminfo___clean_app_instance_name() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "basic");
		properties.setProperty(YangTransformer2PluginProperties.APPLY_NODE_APP_INSTANCE_NAME_HANDLING, "true");

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);

			assertSize(1, supportedModelInformation.getSupportedMimTypes());
			assertMimMappedToForModule(supportedModelInformation.getSupportedMimTypes().get(0), MODULE_NS_CLEANED, MODULE_NAME_CLEANED, MODULE_REVISION, MODULE_XYZ_VERSION);

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	@Test
	public void test___generate_cfm_miminfo___do_not_clean_app_instance_name() {
		final Properties properties = new Properties();
		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, TEST_SUB_DIR + "basic");
		properties.setProperty(YangTransformer2PluginProperties.APPLY_NODE_APP_INSTANCE_NAME_HANDLING, "false");

		final YangTransformer2Plugin yangTransformerPlugin = new YangTransformer2Plugin();

		try {
			final SupportedModelInformation supportedModelInformation = yangTransformerPlugin.generateSupportedModelInformation(properties);

			assertSize(1, supportedModelInformation.getSupportedMimTypes());
			assertMimMappedToForModule(supportedModelInformation.getSupportedMimTypes().get(0), MODULE_NS_ORIG, MODULE_NAME_ORIG, MODULE_REVISION, MODULE_XYZ_VERSION);

		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown.");
		}
	}

	private void assertMimMappedToForModule(final SupportedMimType supportedMimType, final String moduleNamespace, final String moduleName, final String moduleRevision, final String moduleXyzVersion) {
		assertEquals(moduleNamespace, supportedMimType.getNamespace());
		assertEquals(moduleXyzVersion, supportedMimType.getVersion());
		final ModelInfo modelInfo = new ModelInfo(SchemaConstants.NET_YANG, moduleNamespace, moduleName + "@" + moduleRevision);
		assertEquals(modelInfo.toUrn(), supportedMimType.getOriginalModelUrn());
	}
}
