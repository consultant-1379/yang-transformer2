package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class DomainApplicationTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/domain-application";

	private static final String MODULE_NS = "urn:test:simple-module";
	private static final String MODULE_XYZ_VERSION = "2023.7.12";

	@Test
	public void test___domain_app() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR)));
		context.setGenerateForDomainApplicationModel(true);
		YangTransformer2.transform(context);

		assertEModelCreatedCount(SchemaConstants.DPS_PRIMARYTYPE, 3);
		assertEModelCreatedCount(SchemaConstants.DPS_PRIMARYTYPE_EXT, 2);
		assertEModelCreatedCount(SchemaConstants.DPS_RELATIONSHIP, 1);
		assertEModelCreatedCount(SchemaConstants.OSS_EDT, 2);
		assertEModelCreatedCount(SchemaConstants.NET_YANG, 2);
		assertEModelCreatedCount(SchemaConstants.CFM_MIMINFO, 0);

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		assertModelExists(cont1modelInfo);

		final ModelInfo cont1ExtModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtModelInfo);
		assertNotNull(cont1ext);
		assertNull(cont1ext.getRequiresTargetType());
		assertNotNull(findAddedAttribute(cont1ext, "leaf12"));

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		assertModelExists(cont2modelInfo);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont3", MODULE_XYZ_VERSION);
		assertModelExists(cont3modelInfo);

		final ModelInfo cont3ExtModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont3");
		final PrimaryTypeExtensionDefinition cont3ext = load(cont3ExtModelInfo);
		assertNotNull(cont3ext);
		assertNull(cont3ext.getRequiresTargetType());
		assertNotNull(findRemovedAttribute(cont3ext, "leaf32"));

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 1);
		final PrimaryTypeContainment containment1to2 = findAndAssertContainment(rels, MODULE_NS, "cont1", MODULE_NS, "cont2");
		assertNull(containment1to2.getRequiresTargetType());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService(false);

		assertSize(3, modelService.getModelMetaInformation().getModelsFromUrn(SchemaConstants.DPS_PRIMARYTYPE, ModelMetaInformation.ANY, ModelMetaInformation.ANY, ModelMetaInformation.ANY));
		assertSize(2, modelService.getModelMetaInformation().getModelsFromUrn(SchemaConstants.DPS_PRIMARYTYPE_EXT, ModelMetaInformation.ANY, ModelMetaInformation.ANY, ModelMetaInformation.ANY));
		assertSize(1, modelService.getModelMetaInformation().getModelsFromUrn(SchemaConstants.DPS_RELATIONSHIP, ModelMetaInformation.ANY, ModelMetaInformation.ANY, ModelMetaInformation.ANY));
		assertSize(2, modelService.getModelMetaInformation().getModelsFromUrn(SchemaConstants.OSS_EDT, ModelMetaInformation.ANY, ModelMetaInformation.ANY, ModelMetaInformation.ANY));
		assertSize(0, modelService.getModelMetaInformation().getModelsFromUrn(SchemaConstants.CFM_MIMINFO, ModelMetaInformation.ANY, ModelMetaInformation.ANY, ModelMetaInformation.ANY));
		assertSize(0, modelService.getModelMetaInformation().getModelsFromUrn(SchemaConstants.OSS_TARGETVERSION, ModelMetaInformation.ANY, ModelMetaInformation.ANY, ModelMetaInformation.ANY));
		assertSize(0, modelService.getModelMetaInformation().getModelsFromUrn(SchemaConstants.OSS_TARGETTYPE, ModelMetaInformation.ANY, ModelMetaInformation.ANY, ModelMetaInformation.ANY));

		/*
		 * Note that null is supplied as Target, thereby checking that the extensions are merged-in.
		 */

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, null);
		assertSize(0, cont1spec.getParentTypes());

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertNotNull(attr11);
		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertNotNull(attr12);
		assertEquals(DataType.ENUM_REF, attr12.getDataTypeSpecification().getDataType());

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, null);
		assertSize(1, cont2spec.getParentTypes());

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, null);
		assertSize(0, cont3spec.getParentTypes());

		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont3spec, "leaf31");
		assertNotNull(attr31);
		final PrimaryTypeAttributeSpecification attr32 = findAttribute(cont3spec, "leaf32");
		assertNull(attr32);
	}
}
