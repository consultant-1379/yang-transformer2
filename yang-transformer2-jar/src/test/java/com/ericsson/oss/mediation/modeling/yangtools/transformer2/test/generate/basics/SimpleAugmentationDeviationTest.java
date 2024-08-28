package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class SimpleAugmentationDeviationTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-basics/";

	private static final String MODULE_NS1 = "urn:test:simple-augmentation-and-deviation-one";
	private static final String MODULE_NAME1 = "simple-augmentation-and-deviation-one";
	private static final String MODULE_REVISION1 = "2021-10-14";
	private static final String MODULE_XYZ_VERSION1 = "2021.10.14";

	private static final String MODULE_NS2 = "urn:test:simple-augmentation-and-deviation-two";
	private static final String MODULE_NAME2 = "simple-augmentation-and-deviation-two";
	private static final String MODULE_REVISION2 = "2021-06-01";
	private static final String MODULE_XYZ_VERSION2 = "2021.6.1";

	private static final String MODULE_NS3 = "urn:test:simple-augmentation-and-deviation-three";
	private static final String MODULE_NAME3 = "simple-augmentation-and-deviation-three";
	private static final String MODULE_REVISION3 = "2021-05-09";
	private static final String MODULE_XYZ_VERSION3 = "2021.5.9";

	@Test
	public void test___simple_augmentation_and_deviation() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"));
		context.setApplyNodeAppInstanceNameHandling(true);		// random setting that should not affect the test
		YangTransformer2.transform(context);

		internal___simple_augmentation_and_deviation(context, null);
	}

	@Test
	public void test___simple_augmentation_and_deviation_no_excludelist() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"));
		context.setExcludedNamespaces(Collections.emptyList());

		YangTransformer2.transform(context);

		internal___simple_augmentation_and_deviation(context, null);
	}

	@Test
	public void test___simple_augmentation_and_deviation_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"), overwritingProperties);

		internal___simple_augmentation_and_deviation(null, actualNmtProperties);
	}

	@Test
	public void test___simple_augmentation_and_deviation_no_excludelist_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, "");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"), overwritingProperties);

		internal___simple_augmentation_and_deviation(null, actualNmtProperties);
	}

	private void internal___simple_augmentation_and_deviation(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(5, 4, 0, 0, 3, 0);

		// -----------------------------------------------------

		final ModelInfo yangModuleOne = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS1, MODULE_NAME1, MODULE_REVISION1);
		assertModelExists(yangModuleOne);

		final ModelInfo yangModuleTwo = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS2, MODULE_NAME2, MODULE_REVISION2);
		assertModelExists(yangModuleTwo);

		final ModelInfo yangModuleThree = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS3, MODULE_NAME3, MODULE_REVISION3);
		assertModelExists(yangModuleThree);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont1", MODULE_XYZ_VERSION1);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertNotNull(cont1.getModelCreationInfo().getDerivedModel());
		assertEquals(yangModuleOne.toUrn(), cont1.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(3, cont1.getPrimaryTypeAttribute());				// +1 for the auto-generated key
		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertEquals(MODULE_NS1, leaf11.getNs());
		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertEquals(MODULE_NS1, leaf12.getNs());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont2", MODULE_XYZ_VERSION1);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		assertSize(2, cont2.getPrimaryTypeAttribute());				// +1 for the auto-generated key
		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertEquals(MODULE_NS1, leaf21.getNs());

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont3", MODULE_XYZ_VERSION1);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);

		assertSize(2, cont3.getPrimaryTypeAttribute());				// +1 for the auto-generated key
		final PrimaryTypeAttribute leaf35 = findAttribute(cont3, "leaf35");
		assertEquals(MODULE_NS1, leaf35.getNs());

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont4", MODULE_XYZ_VERSION2);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);
		assertEquals(yangModuleTwo.toUrn(), cont4.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(3, cont4.getPrimaryTypeAttribute());				// +1 for the auto-generated key
		final PrimaryTypeAttribute leaf43 = findAttribute(cont4, "leaf43");
		assertEquals(MODULE_NS2, leaf43.getNs());
		final PrimaryTypeAttribute leaf44 = findAttribute(cont4, "leaf44");
		assertEquals(MODULE_NS2, leaf44.getNs());

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont5", MODULE_XYZ_VERSION2);
		assertModelDoesNotExist(cont5modelInfo);		// deviated-out

		final ModelInfo cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont6", MODULE_XYZ_VERSION2);
		final PrimaryTypeDefinition cont6 = load(cont6modelInfo);

		assertSize(3, cont6.getPrimaryTypeAttribute());				// +1 for the auto-generated key
		final PrimaryTypeAttribute leaf66 = findAttribute(cont6, "leaf66");
		assertEquals(MODULE_NS2, leaf66.getNs());
		final PrimaryTypeAttribute leaf67 = findAttribute(cont6, "leaf67");
		assertEquals(MODULE_NS2, leaf67.getNs());

		// -----------------------------------------------------

		/*
		 * Containers 1, 4 and 6 are deviated. Container 2 has been augmented. Hence there should
		 * be 4 extension models.
		 */

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS1, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);
		assertRequiresTargetType(context, actualNmtProperties, cont1ext.getRequiresTargetType());
		assertEquals(cont1modelInfo.toImpliedUrn(), cont1ext.getExtendedModelElement().get(0).getUrn());
		assertEquals(yangModuleOne.toUrn(), cont1ext.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(1, cont1ext.getPrimaryTypeAttributeRemoval());
		assertNotNull(findRemovedAttribute(cont1ext, "leaf12"));
		assertNull(findRemovedAttribute(cont1ext, "leaf19"));		// should not have been generated

		final ModelInfo cont4ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS2, "cont4");
		final PrimaryTypeExtensionDefinition cont4ext = load(cont4ExtensionModelInfo);
		assertRequiresTargetType(context, actualNmtProperties, cont4ext.getRequiresTargetType());
		assertEquals(cont4modelInfo.toImpliedUrn(), cont4ext.getExtendedModelElement().get(0).getUrn());
		assertEquals(yangModuleTwo.toUrn(), cont4ext.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(1, cont4ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf43", cont4ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		final ModelInfo cont6ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS2, "cont6");
		final PrimaryTypeExtensionDefinition cont6ext = load(cont6ExtensionModelInfo);
		assertRequiresTargetType(context, actualNmtProperties, cont6ext.getRequiresTargetType());
		assertEquals(cont6modelInfo.toImpliedUrn(), cont6ext.getExtendedModelElement().get(0).getUrn());
		assertEquals(yangModuleTwo.toUrn(), cont6ext.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(1, cont6ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf66", cont6ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		assertSize(1, cont6ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());
		assertEquals("leaf69", cont6ext.getPrimaryTypeExtension().getPrimaryTypeAttribute().get(0).getName());
		assertEquals(MODULE_NS3, cont6ext.getPrimaryTypeExtension().getPrimaryTypeAttribute().get(0).getNs());

		final ModelInfo cont2ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS1, "cont2");
		final PrimaryTypeExtensionDefinition cont2ext = load(cont2ExtensionModelInfo);
		assertRequiresTargetType(context, actualNmtProperties, cont2ext.getRequiresTargetType());
		assertEquals(cont2modelInfo.toImpliedUrn(), cont2ext.getExtendedModelElement().get(0).getUrn());
		assertEquals(yangModuleOne.toUrn(), cont2ext.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(1, cont2ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());
		final PrimaryTypeAttribute leaf28 = cont2ext.getPrimaryTypeExtension().getPrimaryTypeAttribute().get(0);
		assertEquals(MODULE_NS2, leaf28.getNs());

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 5);

		final PrimaryTypeContainment managedElementToCont1 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		assertContainment(managedElementToCont1, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS1, "cont1", MODULE_XYZ_VERSION1, null, 1L);

		final PrimaryTypeContainment managedElementToCont2 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont2");
		assertContainment(managedElementToCont2, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS1, "cont2", MODULE_XYZ_VERSION1, null, 1L);

		final PrimaryTypeContainment cont2ToCont3 = findAndAssertContainment(rels, "cont2", "cont3");
		assertContainment(cont2ToCont3, context, actualNmtProperties, MODULE_NS1, "cont2", MODULE_XYZ_VERSION1, MODULE_NS1, "cont3", MODULE_XYZ_VERSION1, null, 1L);

		final PrimaryTypeContainment managedElementToCont4 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont4");
		assertContainment(managedElementToCont4, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS2, "cont4", MODULE_XYZ_VERSION2, null, 1L);

		final PrimaryTypeContainment cont2ToCont6 = findAndAssertContainment(rels, "cont2", "cont6");
		assertContainment(cont2ToCont6, context, actualNmtProperties, MODULE_NS1, "cont2", MODULE_XYZ_VERSION1, MODULE_NS2, "cont6", MODULE_XYZ_VERSION2, null, 1L);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertSize(3, mimInfo.getSupportedMims().getMimMappedTo());

		assertHasSupportedMim(mimInfo, MODULE_NS1, MODULE_XYZ_VERSION1, yangModuleOne.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS2, MODULE_XYZ_VERSION2, yangModuleTwo.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS3, MODULE_XYZ_VERSION3, yangModuleThree.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		{
			/*
			 * *** Without context *** so any removal that is target version-specific *** should not ***
			 * be merged in! (But note that MS will merge-in all additions)!
			 */
			final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class);

			final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
			assertEquals(MODULE_NS1, attr11.getNamespace());

			final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
			assertNotNull(attr12);			// Correct - removals will not be merged-in when target is not supplied to MS.
			assertEquals(MODULE_NS1, attr12.getNamespace());

			final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class);

			final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
			assertEquals(MODULE_NS1, attr21.getNamespace());

			final PrimaryTypeAttributeSpecification attr28 = findAttribute(cont2spec, "leaf28");
			assertEquals(MODULE_NS2, attr28.getNamespace());

			final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class);

			final PrimaryTypeAttributeSpecification attr35 = findAttribute(cont3spec, "leaf35");
			assertEquals(MODULE_NS1, attr35.getNamespace());

			final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class);

			final PrimaryTypeAttributeSpecification attr43 = findAttribute(cont4spec, "leaf43");
			assertNotNull(attr43);			// Correct - removals will not be merged-in when target is not supplied to MS.
			assertEquals(MODULE_NS2, attr43.getNamespace());

			final PrimaryTypeAttributeSpecification attr44 = findAttribute(cont4spec, "leaf44");
			assertEquals(MODULE_NS2, attr44.getNamespace());

			assertModelDoesNotExistInModelService(modelService, cont5modelInfo);	// has been deviated-out, hence the DPS_PT was never created.

			final HierarchicalPrimaryTypeSpecification cont6spec = modelService.getTypedAccess().getEModelSpecification(cont6modelInfo, HierarchicalPrimaryTypeSpecification.class);

			final PrimaryTypeAttributeSpecification attr66 = findAttribute(cont6spec, "leaf66");
			assertNotNull(attr66);			// Correct - removals will not be merged-in when target is not supplied to MS.
			assertEquals(MODULE_NS2, attr66.getNamespace());

			final PrimaryTypeAttributeSpecification attr67 = findAttribute(cont6spec, "leaf67");
			assertEquals(MODULE_NS2, attr67.getNamespace());

			final PrimaryTypeAttributeSpecification attr69 = findAttribute(cont6spec, "leaf69");
			assertEquals(MODULE_NS3, attr69.getNamespace());
		}

		{
			/*
			 * *** With context *** so any removal that is target version-specific *** should be ***
			 * merged in (i.e. attribute removed).
			 */
			final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));

			final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
			assertEquals(MODULE_NS1, attr11.getNamespace());

			final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
			assertNull(attr12);		// deviated out

			final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));

			final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
			assertEquals(MODULE_NS1, attr21.getNamespace());

			final PrimaryTypeAttributeSpecification attr28 = findAttribute(cont2spec, "leaf28");
			assertEquals(MODULE_NS2, attr28.getNamespace());

			final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));

			final PrimaryTypeAttributeSpecification attr35 = findAttribute(cont3spec, "leaf35");
			assertEquals(MODULE_NS1, attr35.getNamespace());

			final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));

			final PrimaryTypeAttributeSpecification attr43 = findAttribute(cont4spec, "leaf43");
			assertNull(attr43);		// deviated out

			final PrimaryTypeAttributeSpecification attr44 = findAttribute(cont4spec, "leaf44");
			assertEquals(MODULE_NS2, attr44.getNamespace());

			assertModelDoesNotExistInModelService(modelService, cont5modelInfo);	// has been deviated-out, hence the DPS_PT was never created.

			final HierarchicalPrimaryTypeSpecification cont6spec = modelService.getTypedAccess().getEModelSpecification(cont6modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));

			final PrimaryTypeAttributeSpecification attr66 = findAttribute(cont6spec, "leaf66");
			assertNull(attr66);		// deviated out

			final PrimaryTypeAttributeSpecification attr67 = findAttribute(cont6spec, "leaf67");
			assertEquals(MODULE_NS2, attr67.getNamespace());

			final PrimaryTypeAttributeSpecification attr69 = findAttribute(cont6spec, "leaf69");
			assertEquals(MODULE_NS3, attr69.getNamespace());
		}
	}

	@Test
	public void test___simple_augmentation_and_deviation_excludelist_one() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"));
		context.setExcludedNamespaces(Collections.singletonList(MODULE_NS1));

		YangTransformer2.transform(context);

		internal___simple_augmentation_and_deviation_excludelist_one(context, null);
	}

	@Test
	public void test___simple_augmentation_and_deviation_excludelist_one_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, "   " + MODULE_NS1 + "    ");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"), overwritingProperties);

		internal___simple_augmentation_and_deviation_excludelist_one(null, actualNmtProperties);
	}

	private void internal___simple_augmentation_and_deviation_excludelist_one(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(1, 2, 0, 0, 3, 0);

		// -----------------------------------------------------

		final ModelInfo yangModuleOne = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS1, MODULE_NAME1, MODULE_REVISION1);
		assertModelExists(yangModuleOne);

		final ModelInfo yangModuleTwo = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS2, MODULE_NAME2, MODULE_REVISION2);
		assertModelExists(yangModuleTwo);

		final ModelInfo yangModuleThree = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS3, MODULE_NAME3, MODULE_REVISION3);
		assertModelExists(yangModuleThree);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont1", MODULE_XYZ_VERSION1);
		assertModelDoesNotExist(cont1modelInfo);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont2", MODULE_XYZ_VERSION1);
		assertModelDoesNotExist(cont2modelInfo);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont3", MODULE_XYZ_VERSION1);
		assertModelDoesNotExist(cont3modelInfo);		// sits below cont2 so removed as well.

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont4", MODULE_XYZ_VERSION2);
		assertModelExists(cont4modelInfo);

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont5", MODULE_XYZ_VERSION2);
		assertModelDoesNotExist(cont5modelInfo);		// sits below cont2 so removed as well.

		final ModelInfo cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont6", MODULE_XYZ_VERSION2);
		assertModelDoesNotExist(cont6modelInfo);		// sits below cont2 so removed as well.

		// -----------------------------------------------------

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS1, "cont1");
		assertModelDoesNotExist(cont1ExtensionModelInfo);

		final ModelInfo cont2ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS1, "cont2");
		assertModelDoesNotExist(cont2ExtensionModelInfo);

		final ModelInfo cont4ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS2, "cont4");
		assertModelExists(cont4ExtensionModelInfo);

		final ModelInfo cont6ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS2, "cont6");
		assertModelDoesNotExist(cont6ExtensionModelInfo);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 1);

		final PrimaryTypeContainment managedElementToCont4 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont4");
		assertContainment(managedElementToCont4, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS2, "cont4", MODULE_XYZ_VERSION2, null, 1L);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertSize(2, mimInfo.getSupportedMims().getMimMappedTo());

		assertHasSupportedMim(mimInfo, MODULE_NS2, MODULE_XYZ_VERSION2, yangModuleTwo.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS3, MODULE_XYZ_VERSION3, yangModuleThree.toUrn());


		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		assertModelDoesNotExistInModelService(modelService, cont1modelInfo);

		assertModelDoesNotExistInModelService(modelService, cont2modelInfo);

		assertModelDoesNotExistInModelService(modelService, cont3modelInfo);

		assertModelExistsInModelService(modelService, cont4modelInfo);

		assertModelDoesNotExistInModelService(modelService, cont5modelInfo);

		assertModelDoesNotExistInModelService(modelService, cont6modelInfo);
	}

	@Test
	public void test___simple_augmentation_and_deviation_excludelist_two() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"));
		context.setExcludedNamespaces(Collections.singletonList(MODULE_NS2));

		YangTransformer2.transform(context);

		internal___simple_augmentation_and_deviation_excludelist_two(context, null);
	}

	@Test
	public void test___simple_augmentation_and_deviation_excludelist_two_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, MODULE_NS2);

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"), overwritingProperties);

		internal___simple_augmentation_and_deviation_excludelist_two(null, actualNmtProperties);
	}

	private void internal___simple_augmentation_and_deviation_excludelist_two(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(3, 2, 0, 0, 3, 0);

		// -----------------------------------------------------

		final ModelInfo yangModuleOne = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS1, MODULE_NAME1, MODULE_REVISION1);
		assertModelExists(yangModuleOne);

		final ModelInfo yangModuleTwo = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS2, MODULE_NAME2, MODULE_REVISION2);
		assertModelExists(yangModuleTwo);

		final ModelInfo yangModuleThree = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS3, MODULE_NAME3, MODULE_REVISION3);
		assertModelExists(yangModuleThree);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont1", MODULE_XYZ_VERSION1);
		assertModelExists(cont1modelInfo);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont2", MODULE_XYZ_VERSION1);
		assertModelExists(cont2modelInfo);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont3", MODULE_XYZ_VERSION1);
		assertModelExists(cont3modelInfo);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont4", MODULE_XYZ_VERSION2);
		assertModelDoesNotExist(cont4modelInfo);

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont5", MODULE_XYZ_VERSION2);
		assertModelDoesNotExist(cont5modelInfo);

		final ModelInfo cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont6", MODULE_XYZ_VERSION2);
		assertModelDoesNotExist(cont6modelInfo);

		// -----------------------------------------------------

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS1, "cont1");
		assertModelExists(cont1ExtensionModelInfo);

		final ModelInfo cont2ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS1, "cont2");
		assertModelDoesNotExist(cont2ExtensionModelInfo);

		final ModelInfo cont4ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS2, "cont4");
		assertModelDoesNotExist(cont4ExtensionModelInfo);

		final ModelInfo cont6ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS2, "cont6");
		assertModelDoesNotExist(cont6ExtensionModelInfo);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 3);

		final PrimaryTypeContainment managedElementToCont1 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		assertContainment(managedElementToCont1, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS1, "cont1", MODULE_XYZ_VERSION1, null, 1L);

		final PrimaryTypeContainment managedElementToCont2 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont2");
		assertContainment(managedElementToCont2, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS1, "cont2", MODULE_XYZ_VERSION1, null, 1L);

		final PrimaryTypeContainment cont2ToCont3 = findAndAssertContainment(rels, "cont2", "cont3");
		assertContainment(cont2ToCont3, context, actualNmtProperties, MODULE_NS1, "cont2", MODULE_XYZ_VERSION1, MODULE_NS1, "cont3", MODULE_XYZ_VERSION1, null, 1L);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertSize(2, mimInfo.getSupportedMims().getMimMappedTo());

		assertHasSupportedMim(mimInfo, MODULE_NS1, MODULE_XYZ_VERSION1, yangModuleOne.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS3, MODULE_XYZ_VERSION3, yangModuleThree.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		assertModelExistsInModelService(modelService, cont1modelInfo);
		assertModelExistsInModelService(modelService, cont2modelInfo);
		assertModelExistsInModelService(modelService, cont3modelInfo);

		assertModelDoesNotExistInModelService(modelService, cont4modelInfo);
		assertModelDoesNotExistInModelService(modelService, cont5modelInfo);
		assertModelDoesNotExistInModelService(modelService, cont6modelInfo);
	}

	@Test
	public void test___simple_augmentation_and_deviation_excludelist_three() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"));
		context.setExcludedNamespaces(Collections.singletonList(MODULE_NS3));

		YangTransformer2.transform(context);

		internal___simple_augmentation_and_deviation_excludelist_three(context, null);
	}

	@Test
	public void test___simple_augmentation_and_deviation_excludelist_three_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.EXCLUDED_NAMESPACES, MODULE_NS3);

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "simple-augmentation-and-deviation"), overwritingProperties);

		internal___simple_augmentation_and_deviation_excludelist_three(null, actualNmtProperties);
	}

	private void internal___simple_augmentation_and_deviation_excludelist_three(final TransformerContext context, final Properties actualNmtProperties) {

		assertEModelCreatedCount(5, 4, 0, 0, 3, 0);

		// -----------------------------------------------------

		final ModelInfo yangModuleOne = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS1, MODULE_NAME1, MODULE_REVISION1);
		assertModelExists(yangModuleOne);

		final ModelInfo yangModuleTwo = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS2, MODULE_NAME2, MODULE_REVISION2);
		assertModelExists(yangModuleTwo);

		final ModelInfo yangModuleThree = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS3, MODULE_NAME3, MODULE_REVISION3);
		assertModelExists(yangModuleThree);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont1", MODULE_XYZ_VERSION1);
		assertModelExists(cont1modelInfo);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont2", MODULE_XYZ_VERSION1);
		assertModelExists(cont2modelInfo);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS1, "cont3", MODULE_XYZ_VERSION1);
		assertModelExists(cont3modelInfo);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont4", MODULE_XYZ_VERSION2);
		assertModelExists(cont4modelInfo);

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont5", MODULE_XYZ_VERSION2);
		assertModelDoesNotExist(cont5modelInfo);

		final ModelInfo cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS2, "cont6", MODULE_XYZ_VERSION2);
		assertModelExists(cont6modelInfo);

		// -----------------------------------------------------

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS1, "cont1");
		assertModelExists(cont1ExtensionModelInfo);

		final ModelInfo cont2ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS1, "cont2");
		assertModelExists(cont2ExtensionModelInfo);

		final ModelInfo cont4ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS2, "cont4");
		assertModelExists(cont4ExtensionModelInfo);

		final ModelInfo cont6ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS2, "cont6");
		final PrimaryTypeExtensionDefinition cont6ext = load(cont6ExtensionModelInfo);
		assertRequiresTargetType(context, actualNmtProperties, cont6ext.getRequiresTargetType());
		assertEquals(cont6modelInfo.toImpliedUrn(), cont6ext.getExtendedModelElement().get(0).getUrn());
		assertNotNull(cont6ext.getModelCreationInfo().getDerivedModel());
		assertEquals(yangModuleTwo.toUrn(), cont6ext.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(1, cont6ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf66", cont6ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 5);

		final PrimaryTypeContainment managedElementToCont1 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		assertContainment(managedElementToCont1, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS1, "cont1", MODULE_XYZ_VERSION1, null, 1L);

		final PrimaryTypeContainment managedElementToCont2 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont2");
		assertContainment(managedElementToCont2, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS1, "cont2", MODULE_XYZ_VERSION1, null, 1L);

		final PrimaryTypeContainment cont2ToCont3 = findAndAssertContainment(rels, "cont2", "cont3");
		assertContainment(cont2ToCont3, context, actualNmtProperties, MODULE_NS1, "cont2", MODULE_XYZ_VERSION1, MODULE_NS1, "cont3", MODULE_XYZ_VERSION1, null, 1L);

		final PrimaryTypeContainment managedElementToCont4 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont4");
		assertContainment(managedElementToCont4, context, actualNmtProperties, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS2, "cont4", MODULE_XYZ_VERSION2, null, 1L);

		final PrimaryTypeContainment cont2ToCont6 = findAndAssertContainment(rels, "cont2", "cont6");
		assertContainment(cont2ToCont6, context, actualNmtProperties, MODULE_NS1, "cont2", MODULE_XYZ_VERSION1, MODULE_NS2, "cont6", MODULE_XYZ_VERSION2, null, 1L);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, actualNmtProperties);
		assertSize(2, mimInfo.getSupportedMims().getMimMappedTo());

		assertHasSupportedMim(mimInfo, MODULE_NS1, MODULE_XYZ_VERSION1, yangModuleOne.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS2, MODULE_XYZ_VERSION2, yangModuleTwo.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		assertModelExistsInModelService(modelService, cont1modelInfo);
		assertModelExistsInModelService(modelService, cont2modelInfo);
		assertModelExistsInModelService(modelService, cont3modelInfo);
		assertModelExistsInModelService(modelService, cont4modelInfo);

		assertModelDoesNotExistInModelService(modelService, cont5modelInfo);

		final HierarchicalPrimaryTypeSpecification cont6spec = modelService.getTypedAccess().getEModelSpecification(cont6modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, actualNmtProperties));
		final PrimaryTypeAttributeSpecification attr66 = findAttribute(cont6spec, "leaf66");
		assertNull(attr66);
		final PrimaryTypeAttributeSpecification attr67 = findAttribute(cont6spec, "leaf67");
		assertNotNull(attr67);
		final PrimaryTypeAttributeSpecification attr69 = findAttribute(cont6spec, "leaf69");
		assertNull(attr69);
	}
}
