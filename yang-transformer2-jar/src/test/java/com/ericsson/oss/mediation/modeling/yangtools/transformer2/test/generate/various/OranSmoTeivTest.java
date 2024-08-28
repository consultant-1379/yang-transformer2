package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.relationship.AssociationEndpoint;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.ReadBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.AssociationAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.AssociationKind;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeAssociation;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.oran.ValidatorFindingTypeOran;

public class OranSmoTeivTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/oran-smo-teiv/";

	private static final String MODULE_NS = "urn~test~oran-smo-teiv-simple";
	private static final String MODULE_XYZ_VERSION = "2023.8.22";

	@Test
	public void test___topinv___simple() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P025_INVALID_EXTENSION.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeOran.T583_TEIV_DUPLICATE_ROLE_NAME.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeOran.T584_TEIV_UNKNOWN_TYPE.toString());

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR + "oran-smo-teiv-simple.yang"), new File(ORAN_TEIV_YANG_EXT)));
		context.setApply3gppHandling(true);
		context.setGenerateForDomainApplicationModel(true);
		YangTransformer2.transform(context);

		final ModelInfo mocAmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocA", MODULE_XYZ_VERSION);
		assertModelExists(mocAmodelInfo);
		final ModelInfo mocBmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocB", MODULE_XYZ_VERSION);
		assertModelExists(mocBmodelInfo);
		final ModelInfo mocCmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocC", MODULE_XYZ_VERSION);
		assertModelExists(mocCmodelInfo);
		final ModelInfo mocDmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocD", MODULE_XYZ_VERSION);
		assertModelExists(mocDmodelInfo);

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 0);
		assertAssociationsCreatedCount(rels, 4);

		// - - - - - - - - - - - - - - - - - -

		final PrimaryTypeAssociation rel1 = findAssociation(rels, "rel-1");
		assertNotNull(rel1);
		assertEquals("rel-1", rel1.getName());
		assertEquals("desc1", rel1.getDesc());
		assertEquals(AssociationKind.BI_DIRECTIONAL, rel1.getKind());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, rel1.getReadBehavior());
		assertEquals(WriteBehaviorType.PERSIST, rel1.getWriteBehavior());

		assertEquals("role_AB_A", rel1.getASide().getEndpointName());
		assertEquals("role_AB_B", rel1.getBSide().getEndpointName());
		assertEquals("role_AB_A desc", rel1.getASide().getDesc());
		assertEquals("role_AB_B desc", rel1.getBSide().getDesc());
		assertEquals(mocAmodelInfo.toImpliedUrn(), rel1.getASide().getPrimaryTypeUrn());
		assertEquals(mocBmodelInfo.toImpliedUrn(), rel1.getBSide().getPrimaryTypeUrn());

		assertNull(rel1.getASide().getMinSizeConstraint());
		assertEquals(1L, rel1.getASide().getMaxSizeConstraint().getValue());
		assertNull(rel1.getBSide().getMinSizeConstraint());
		assertNull(rel1.getBSide().getMaxSizeConstraint());
		assertSize(0, rel1.getAttribute());

		// - - - - - - - - - - - - - - - - - -

		final PrimaryTypeAssociation rel2 = findAssociation(rels, "rel-2");
		assertNotNull(rel2);
		assertEquals("BDA rel-2", rel2.getDesc());

		assertEquals("role_BC_B", rel2.getASide().getEndpointName());
		assertEquals("role_BC_C", rel2.getBSide().getEndpointName());
		assertEquals("A-side", rel2.getASide().getDesc());
		assertEquals("B-side", rel2.getBSide().getDesc());
		assertEquals(mocBmodelInfo.toImpliedUrn(), rel2.getASide().getPrimaryTypeUrn());
		assertEquals(mocCmodelInfo.toImpliedUrn(), rel2.getBSide().getPrimaryTypeUrn());

		assertEquals(1L, rel2.getASide().getMinSizeConstraint().getValue());
		assertEquals(1L, rel2.getASide().getMaxSizeConstraint().getValue());
		assertEquals(2L, rel2.getBSide().getMinSizeConstraint().getValue());
		assertEquals(10L, rel2.getBSide().getMaxSizeConstraint().getValue());
		assertSize(0, rel2.getAttribute());

		// - - - - - - - - - - - - - - - - - -

		final PrimaryTypeAssociation rel3 = findAssociation(rels, "rel-3");
		assertNotNull(rel3);

		assertNull(rel3.getASide().getMinSizeConstraint());
		assertEquals(1L, rel3.getASide().getMaxSizeConstraint().getValue());
		assertNull(rel3.getBSide().getMinSizeConstraint());
		assertNull(rel3.getBSide().getMaxSizeConstraint());

		// - - - - - - - - - - - - - - - - - -

		final PrimaryTypeAssociation rel4 = findAssociation(rels, "rel-4");
		assertNotNull(rel4);
		assertSize(3, rel4.getAttribute());

		final AssociationAttribute idAttr = findAttribute(rel4, "id");
		assertNotNull(idAttr);
		assertTrue(idAttr.isKey());
		assertTrue(idAttr.isMandatory());
		assertInstanceOf(StringType.class, idAttr.getType());

		final AssociationAttribute attr02 = findAttribute(rel4, "attr02");
		assertNotNull(attr02);
		assertFalse(attr02.isKey());
		assertFalse(attr02.isMandatory());
		assertInstanceOf(IntegerType.class, attr02.getType());

		final AssociationAttribute attr03 = findAttribute(rel4, "attr03");
		assertNotNull(attr03);
		assertInstanceOf(BooleanType.class, attr03.getType());

		assertNull(findAttribute(rel4, "should-be-ignored"));

		// - - - - - - - - - - - Not generated due to wrong modeling - - - - - - -

		assertNull(findAssociation(rels, "rel-X1"));
		assertNull(findAssociation(rels, "rel-X2"));
		assertNull(findAssociation(rels, "rel-X3"));
		assertNull(findAssociation(rels, "rel-X4"));
		assertNull(findAssociation(rels, "rel-X5"));
		assertNull(findAssociation(rels, "rel-X6"));

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService(false);

		final HierarchicalPrimaryTypeSpecification mocAspec = modelService.getTypedAccess().getEModelSpecification(mocAmodelInfo, HierarchicalPrimaryTypeSpecification.class, null);
		final HierarchicalPrimaryTypeSpecification mocBspec = modelService.getTypedAccess().getEModelSpecification(mocBmodelInfo, HierarchicalPrimaryTypeSpecification.class, null);
		final HierarchicalPrimaryTypeSpecification mocCspec = modelService.getTypedAccess().getEModelSpecification(mocCmodelInfo, HierarchicalPrimaryTypeSpecification.class, null);
		final HierarchicalPrimaryTypeSpecification mocDspec = modelService.getTypedAccess().getEModelSpecification(mocDmodelInfo, HierarchicalPrimaryTypeSpecification.class, null);

		final AssociationEndpoint role_AB_A_Endpoint = mocAspec.getAssociationEndpoint("role_AB_A");
		assertNotNull(role_AB_A_Endpoint);
		assertEquals("role_AB_A", role_AB_A_Endpoint.getAssociationEndpointName());
		assertEquals(com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.relationship.AssociationKind.BI_DIRECTIONAL, role_AB_A_Endpoint.getAssociationKind());
		assertEquals(0, role_AB_A_Endpoint.getMinimumCardinality());
		assertEquals(1, role_AB_A_Endpoint.getMaximumCardinality());
		assertTrue("role_AB_A", role_AB_A_Endpoint.canHaveAssociationWithType(mocBspec));

		// TODO - the Model Service impl is not ready yet to do more tests here
	}
}
