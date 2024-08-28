package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class TopLevelDataNodesHandlingTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-basics/";

	private static final String MODULE_NS = "urn:test:top-level-data-nodes-handling";
	private static final String MODULE_NAME = "top-level-data-nodes-handling";
	private static final String MODULE_REVISION = "2021-10-11";
	private static final String MODULE_XYZ_VERSION = "2021.10.11";

	private static final String MODULE_NS_3GPP = "urn:3gpp:sa5:_3gpp-common-managed-element";
	private static final String MODULE_NAME_3GPP = "_3gpp-common-managed-element";
	private static final String MODULE_REVISION_3GPP = "2018-07-06";
	private static final String MODULE_XYZ_VERSION_3GPP = "2018.7.6";


	@Test
	public void test___top_level_handling___no_mock___no_containment_parent() {

		/*
		 * Mock ManagedElement = NO
		 * Explicit Containment Parent = NO
		 * 
		 * E.g. Routers
		 */
		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "top-level-data-nodes-handling"));

		assertFalse(context.generateMockManagedElement());
		assertNull(context.getMockManagedElement());
		assertNull(context.getExplicitContainmentParent());
		assertEquals(Constants.COM_TOP_MANAGEDELEMENT_STAR, context.getEffectiveContainmentParent());

		YangTransformer2.transform(context);

		internal___top_level_handling___no_mock___no_containment_parent(context, null);
	}

	@Test
	public void test___top_level_handling___no_mock___no_containment_parent___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "top-level-data-nodes-handling"), overwritingProperties);

		internal___top_level_handling___no_mock___no_containment_parent(null, actualNmtProperties);
	}

	private void internal___top_level_handling___no_mock___no_containment_parent(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(6, 0, 0, 0, 2, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertNull(cont1.getInheritsFrom());
		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, cont1);
		assertHasMeta(Constants.META_NON_PRESENCE_CONTAINER, cont1);
		assertHasMeta(Constants.META_TOP_LEVEL_DATA_NODE, cont1);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);
		assertNull(cont2.getInheritsFrom());
		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, cont2);
		assertHasMeta(Constants.META_NON_PRESENCE_CONTAINER, cont2);
		assertHasNotMeta(Constants.META_TOP_LEVEL_DATA_NODE, cont2);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont3", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);
		assertHasMeta(Constants.META_PRESENCE_CONTAINER, cont3);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, cont3);
		assertHasMeta(Constants.META_TOP_LEVEL_DATA_NODE, cont3);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont4", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);
		assertHasMeta(Constants.META_PRESENCE_CONTAINER, cont4);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, cont4);
		assertHasNotMeta(Constants.META_TOP_LEVEL_DATA_NODE, cont4);

		final ModelInfo managedElementModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_3GPP, "ManagedElement", MODULE_XYZ_VERSION_3GPP);
		final PrimaryTypeDefinition managedElement = load(managedElementModelInfo);
		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, managedElement);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, managedElement);
		assertHasMeta(Constants.META_TOP_LEVEL_DATA_NODE, managedElement);
		assertEquals(Constants.OSS_TOP_MANAGEDELEMENT_300.toImpliedUrn(), managedElement.getInheritsFrom().getUrn());

		final ModelInfo subnetworkModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_3GPP, "SubNetwork", MODULE_XYZ_VERSION_3GPP);
		final PrimaryTypeDefinition subnetwork = load(subnetworkModelInfo);
		assertHasNotMeta(Constants.META_PRESENCE_CONTAINER, subnetwork);
		assertHasNotMeta(Constants.META_NON_PRESENCE_CONTAINER, subnetwork);
		assertHasMeta(Constants.META_TOP_LEVEL_DATA_NODE, subnetwork);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 6);

		/*
		 * Slightly non-sensical - there is no Mock and no explicit containment parent, i.e. "router scenario".
		 * But routers don't use 3GPP ManagedElement.
		 */
		findAndAssertContainment(rels, Constants.COM_TOP, Constants.MANAGEDELEMENT, MODULE_NS, "cont1");
		findAndAssertContainment(rels, Constants.COM_TOP, Constants.MANAGEDELEMENT, MODULE_NS, "cont3");
		findAndAssertContainment(rels, Constants.COM_TOP, Constants.MANAGEDELEMENT, MODULE_NS_3GPP, Constants.MANAGEDELEMENT);
		findAndAssertContainment(rels, Constants.COM_TOP, Constants.MANAGEDELEMENT, MODULE_NS_3GPP, "SubNetwork");
		findAndAssertContainment(rels, MODULE_NS, "cont1", MODULE_NS, "cont2");
		findAndAssertContainment(rels, MODULE_NS, "cont3", MODULE_NS, "cont4");

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		final ModelInfo modelInfoFor3GPPYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_3GPP, MODULE_NAME_3GPP, MODULE_REVISION_3GPP);
		assertModelExists(modelInfoFor3GPPYangModule);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertHasMeta(Constants.META_CONTAINMENT_PARENT_URN, Constants.COM_TOP_MANAGEDELEMENT_STAR.toImpliedUrn(), mimInfo);

		assertSize(2, mimInfo.getSupportedMims().getMimMappedTo());
		assertHasSupportedMim(mimInfo, MODULE_NS, MODULE_XYZ_VERSION, modelInfoForYangModule.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_3GPP, MODULE_XYZ_VERSION_3GPP, modelInfoFor3GPPYangModule.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasNotMetaInModelService(Constants.META_PRESENCE_CONTAINER, cont1spec);
		assertHasMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont1spec);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasNotMetaInModelService(Constants.META_PRESENCE_CONTAINER, cont2spec);
		assertHasMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont2spec);

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasMetaInModelService(Constants.META_PRESENCE_CONTAINER, cont3spec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont3spec);

		final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasMetaInModelService(Constants.META_PRESENCE_CONTAINER, cont4spec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, cont4spec);

		final HierarchicalPrimaryTypeSpecification managedElementSpec = modelService.getTypedAccess().getEModelSpecification(managedElementModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasNotMetaInModelService(Constants.META_PRESENCE_CONTAINER, managedElementSpec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, managedElementSpec);

		final HierarchicalPrimaryTypeSpecification subnetworkSpec = modelService.getTypedAccess().getEModelSpecification(subnetworkModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasNotMetaInModelService(Constants.META_PRESENCE_CONTAINER, subnetworkSpec);
		assertHasNotMetaInModelService(Constants.META_NON_PRESENCE_CONTAINER, subnetworkSpec);

		final Collection<HierarchicalPrimaryTypeSpecification> cont1parents = cont1spec.getParentTypes();
		assertSize(1, cont1parents);
		assertEquals(COM_TOP_MANAGEDELEMENT_10221, cont1parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> cont2parents = cont2spec.getParentTypes();
		assertSize(1, cont2parents);
		assertEquals(cont1modelInfo, cont2parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> cont3parents = cont3spec.getParentTypes();
		assertSize(1, cont3parents);
		assertEquals(COM_TOP_MANAGEDELEMENT_10221, cont3parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> cont4parents = cont4spec.getParentTypes();
		assertSize(1, cont4parents);
		assertEquals(cont3modelInfo, cont4parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> managedElementParents = managedElementSpec.getParentTypes();
		assertSize(2, managedElementParents);
		final Set<ModelInfo> parents = managedElementParents.stream().map(HierarchicalPrimaryTypeSpecification::getModelInfo).collect(Collectors.toSet());
		assertTrue(parents.contains(COM_TOP_MANAGEDELEMENT_10221));
		assertTrue(parents.contains(OSS_TOP_ME_CONTEXT_300));

		final Collection<HierarchicalPrimaryTypeSpecification> subnetworkParents = subnetworkSpec.getParentTypes();
		assertSize(1, subnetworkParents);
		assertEquals(COM_TOP_MANAGEDELEMENT_10221, subnetworkParents.iterator().next().getModelInfo());
	}

	@Test
	public void test___top_level_handling___generate_mock___no_containment_parent() {

		/*
		 * Mock ManagedElement = YES
		 * Explicit Containment Parent = NO
		 * 
		 * E.g. EPG-OI, PCC
		 */
		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "top-level-data-nodes-handling"));
		context.setMockManagedElementNamespace(DEFAULT_MOCK_ME.getNamespace());

		assertTrue(context.generateMockManagedElement());
		assertEquals(DEFAULT_MOCK_ME, context.getMockManagedElement());
		assertNull(context.getExplicitContainmentParent());
		assertEquals(DEFAULT_MOCK_ME, context.getEffectiveContainmentParent());

		YangTransformer2.transform(context);

		internal___top_level_handling___generate_mock___no_containment_parent(context, null);
	}

	@Test
	public void test___top_level_handling___generate_mock___no_containment_parent___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.MOCK_MANAGED_ELEMENT_NAMESPACE, "   " + DEFAULT_MOCK_ME.getNamespace() + "   ");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "top-level-data-nodes-handling"), overwritingProperties);

		internal___top_level_handling___generate_mock___no_containment_parent(null, actualNmtProperties);
	}

	private void internal___top_level_handling___generate_mock___no_containment_parent(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(7, 0, 0, 0, 2, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		assertModelExists(cont1modelInfo);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		assertModelExists(cont2modelInfo);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont3", MODULE_XYZ_VERSION);
		assertModelExists(cont3modelInfo);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont4", MODULE_XYZ_VERSION);
		assertModelExists(cont4modelInfo);

		final ModelInfo managedElementModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_3GPP, "ManagedElement", MODULE_XYZ_VERSION_3GPP);
		assertModelExists(managedElementModelInfo);
		final PrimaryTypeDefinition managedElement = load(managedElementModelInfo);
		assertEquals(Constants.OSS_TOP_MANAGEDELEMENT_300.toImpliedUrn(), managedElement.getInheritsFrom().getUrn());

		final ModelInfo subnetworkModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_3GPP, "SubNetwork", MODULE_XYZ_VERSION_3GPP);
		assertModelExists(subnetworkModelInfo);

		final PrimaryTypeDefinition mockManagedElement = load(DEFAULT_MOCK_ME);
		assertEquals(Constants.OSS_TOP_MANAGEDELEMENT_300.toImpliedUrn(), mockManagedElement.getInheritsFrom().getUrn());
		assertHasMeta(Constants.META_MOCK_MANAGED_ELEMENT, mockManagedElement);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 7);

		findAndAssertContainment(rels, Constants.OSS_TOP, Constants.MECONTEXT, DEFAULT_MOCK_ME.getNamespace(), Constants.MANAGEDELEMENT);

		findAndAssertContainment(rels, DEFAULT_MOCK_ME.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS, "cont1");
		findAndAssertContainment(rels, DEFAULT_MOCK_ME.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS, "cont3");
		findAndAssertContainment(rels, DEFAULT_MOCK_ME.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_3GPP, Constants.MANAGEDELEMENT);
		findAndAssertContainment(rels, DEFAULT_MOCK_ME.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_3GPP, "SubNetwork");
		findAndAssertContainment(rels, MODULE_NS, "cont1", MODULE_NS, "cont2");
		findAndAssertContainment(rels, MODULE_NS, "cont3", MODULE_NS, "cont4");

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		final ModelInfo modelInfoFor3GPPYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_3GPP, MODULE_NAME_3GPP, MODULE_REVISION_3GPP);
		assertModelExists(modelInfoFor3GPPYangModule);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertHasMeta(Constants.META_CONTAINMENT_PARENT_URN, DEFAULT_MOCK_ME.toImpliedUrn(), mimInfo);

		assertSize(3, mimInfo.getSupportedMims().getMimMappedTo());
		assertHasSupportedMim(mimInfo, MODULE_NS, MODULE_XYZ_VERSION, modelInfoForYangModule.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_3GPP, MODULE_XYZ_VERSION_3GPP, modelInfoFor3GPPYangModule.toUrn());
		assertHasSupportedMim(mimInfo, DEFAULT_MOCK_ME.getNamespace(), Constants.ONE_ZERO_ZERO, DEFAULT_MOCK_ME.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification managedElementSpec = modelService.getTypedAccess().getEModelSpecification(managedElementModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification subnetworkSpec = modelService.getTypedAccess().getEModelSpecification(subnetworkModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));

		final HierarchicalPrimaryTypeSpecification mockManagedElementSpec = modelService.getTypedAccess().getEModelSpecification(DEFAULT_MOCK_ME, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		assertHasMetaInModelService(Constants.META_MOCK_MANAGED_ELEMENT, mockManagedElementSpec);

		final Collection<HierarchicalPrimaryTypeSpecification> cont1parents = cont1spec.getParentTypes();
		assertSize(1, cont1parents);
		assertEquals(DEFAULT_MOCK_ME, cont1parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> cont2parents = cont2spec.getParentTypes();
		assertSize(1, cont2parents);
		assertEquals(cont1modelInfo, cont2parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> cont3parents = cont3spec.getParentTypes();
		assertSize(1, cont3parents);
		assertEquals(DEFAULT_MOCK_ME, cont3parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> cont4parents = cont4spec.getParentTypes();
		assertSize(1, cont4parents);
		assertEquals(cont3modelInfo, cont4parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> managedElementParents = managedElementSpec.getParentTypes();
		assertSize(2, managedElementParents);
		final Set<ModelInfo> parents = managedElementParents.stream().map(HierarchicalPrimaryTypeSpecification::getModelInfo).collect(Collectors.toSet());
		assertTrue(parents.contains(DEFAULT_MOCK_ME));
		assertTrue(parents.contains(OSS_TOP_ME_CONTEXT_300));

		final Collection<HierarchicalPrimaryTypeSpecification> subnetworkParents = subnetworkSpec.getParentTypes();
		assertSize(1, subnetworkParents);
		assertEquals(DEFAULT_MOCK_ME, subnetworkParents.iterator().next().getModelInfo());

		/*
		 * Via inheritance the mock ManagedElement inherits from MeContext
		 */
		final Collection<HierarchicalPrimaryTypeSpecification> mockManagedElementParents = mockManagedElementSpec.getParentTypes();
		assertSize(1, mockManagedElementParents);
		assertEquals(OSS_TOP_ME_CONTEXT_300, mockManagedElementParents.iterator().next().getModelInfo());
	}

	@Test
	public void test___top_level_handling___no_mock___explicit_containment_parent() {
		/*
		 * Mock ManagedElement = NO
		 * Explicit Containment Parent = YES
		 * 
		 * E.g. Shared-CNF
		 */
		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "top-level-data-nodes-handling"));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		assertFalse(context.generateMockManagedElement());
		assertNull(context.getMockManagedElement());
		assertEquals(OSS_TOP_ME_CONTEXT_300, context.getExplicitContainmentParent());
		assertEquals(OSS_TOP_ME_CONTEXT_300, context.getEffectiveContainmentParent());

		YangTransformer2.transform(context);

		internal___top_level_handling___no_mock___explicit_containment_parent(context, null);
	}

	@Test
	public void test___top_level_handling___no_mock___explicit_containment_parent___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();

		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXPLICIT_CONTAINMENT_PARENT_IMPLIED_URN, "  " + OSS_TOP_ME_CONTEXT_300.toImpliedUrn() + "  ");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "top-level-data-nodes-handling"), overwritingProperties);

		internal___top_level_handling___no_mock___explicit_containment_parent(null, actualNmtProperties);
	}

	private void internal___top_level_handling___no_mock___explicit_containment_parent(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(6, 0, 0, 0, 2, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		assertModelExists(cont1modelInfo);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		assertModelExists(cont2modelInfo);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont3", MODULE_XYZ_VERSION);
		assertModelExists(cont3modelInfo);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont4", MODULE_XYZ_VERSION);
		assertModelExists(cont4modelInfo);

		final ModelInfo managedElementModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_3GPP, "ManagedElement", MODULE_XYZ_VERSION_3GPP);
		final PrimaryTypeDefinition managedElement = load(managedElementModelInfo);
		assertEquals(Constants.OSS_TOP_MANAGEDELEMENT_300.toImpliedUrn(), managedElement.getInheritsFrom().getUrn());

		final ModelInfo subnetworkModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_3GPP, "SubNetwork", MODULE_XYZ_VERSION_3GPP);
		assertModelExists(subnetworkModelInfo);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 5);

		/*
		 * The containment relationship for the 3GPP ME is not created since we don't have a mock ME.
		 */
		findAndAssertContainment(rels, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName(), MODULE_NS, "cont1");
		findAndAssertContainment(rels, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName(), MODULE_NS, "cont3");
		findAndAssertContainment(rels, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName(), MODULE_NS_3GPP, "SubNetwork");
		findAndAssertContainment(rels, MODULE_NS, "cont1", MODULE_NS, "cont2");
		findAndAssertContainment(rels, MODULE_NS, "cont3", MODULE_NS, "cont4");

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(modelInfoForYangModule);

		final ModelInfo modelInfoFor3GPPYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_3GPP, MODULE_NAME_3GPP, MODULE_REVISION_3GPP);
		assertModelExists(modelInfoFor3GPPYangModule);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertHasMeta(Constants.META_CONTAINMENT_PARENT_URN, OSS_TOP_ME_CONTEXT_300.toImpliedUrn(), mimInfo);

		assertTrue(mimInfo.getSupportedMims().getMimMappedTo().size() == 2);
		assertHasSupportedMim(mimInfo, MODULE_NS, MODULE_XYZ_VERSION, modelInfoForYangModule.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_3GPP, MODULE_XYZ_VERSION_3GPP, modelInfoFor3GPPYangModule.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification managedElementSpec = modelService.getTypedAccess().getEModelSpecification(managedElementModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final HierarchicalPrimaryTypeSpecification subnetworkSpec = modelService.getTypedAccess().getEModelSpecification(subnetworkModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));

		final Collection<HierarchicalPrimaryTypeSpecification> cont1parents = cont1spec.getParentTypes();
		assertSize(1, cont1parents);
		assertEquals(OSS_TOP_ME_CONTEXT_300, cont1parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> cont2parents = cont2spec.getParentTypes();
		assertSize(1, cont2parents);
		assertEquals(cont1modelInfo, cont2parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> cont3parents = cont3spec.getParentTypes();
		assertSize(1, cont3parents);
		assertEquals(OSS_TOP_ME_CONTEXT_300, cont3parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> cont4parents = cont4spec.getParentTypes();
		assertSize(1, cont4parents);
		assertEquals(cont3modelInfo, cont4parents.iterator().next().getModelInfo());

		final Collection<HierarchicalPrimaryTypeSpecification> managedElementParents = managedElementSpec.getParentTypes();
		assertSize(1, managedElementParents);
		final Set<ModelInfo> parents = managedElementParents.stream().map(HierarchicalPrimaryTypeSpecification::getModelInfo).collect(Collectors.toSet());
		assertTrue(parents.contains(OSS_TOP_ME_CONTEXT_300));

		final Collection<HierarchicalPrimaryTypeSpecification> subnetworkParents = subnetworkSpec.getParentTypes();
		assertSize(1, subnetworkParents);
		assertEquals(OSS_TOP_ME_CONTEXT_300, subnetworkParents.iterator().next().getModelInfo());
	}

	@Test
	public void test___top_level_handling___mock___containment_parent() {
		/*
		 * Mock ManagedElement = YES
		 * Explicit Containment Parent = YES
		 * 
		 * Illegal combination!
		 */
		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "top-level-data-nodes-handling"));
		context.setMockManagedElementNamespace(DEFAULT_MOCK_ME.getNamespace());
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		try {
			context.getEffectiveContainmentParent();
			fail("This should have thrown an exception.");
		} catch (final Exception ignored) {
		}
	}

	@Test
	public void test___top_level_handling___mock___containment_parent___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.MOCK_MANAGED_ELEMENT_NAMESPACE, DEFAULT_MOCK_ME.getNamespace());
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXPLICIT_CONTAINMENT_PARENT_IMPLIED_URN, OSS_TOP_ME_CONTEXT_300.toImpliedUrn());

		try {
			transformThroughNmtPlugin(new File(TEST_SUB_DIR + "top-level-data-nodes-handling"), overwritingProperties);
			fail("This should have thrown an exception.");
		} catch (final Exception ignored) {
		}
	}
}
