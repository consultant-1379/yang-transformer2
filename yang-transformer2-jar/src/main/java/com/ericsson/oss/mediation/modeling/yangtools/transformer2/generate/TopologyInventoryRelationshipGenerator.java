/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.ReadBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.AssociationAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.AssociationEndpoint;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.AssociationKind;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeAssociation;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MinMaxValue;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.oran.CORAN;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.oran.YOranSmoTeivASide;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.oran.YOranSmoTeivBSide;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.oran.YOranSmoTeivBiDirectionalTopologyRelationship;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YContainer;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YKey;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YList;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YMandatory;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YMaxElements;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YMinElements;
import com.ericsson.oss.mediation.modeling.yangtools.parser.util.QNameHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ThreeGPPSupport;

/**
 * Generates the relationships for the Topology/Inventory model, if any.
 */
public class TopologyInventoryRelationshipGenerator {

	public static void generateUniDirectionalRelationships(final TransformerContext context, final PrimaryTypeRelationshipDefinition ptrd) {

		final List<AbstractStatement> allUdas = ExtractionHelper.getStatementsAtSchemaRoot(context.getDeviatedSchema(), TopologyInventoryRelationshipGenerator::isBdaTopRel);
		allUdas.forEach(stmt -> generateRelationship(context, ptrd, (YOranSmoTeivBiDirectionalTopologyRelationship) stmt));

		ensureStableGeneration(ptrd);
	}

	public static void generateRelationship(final TransformerContext context, final PrimaryTypeRelationshipDefinition ptrd, final YOranSmoTeivBiDirectionalTopologyRelationship bdaTopRel) {
		/*
		 * A relationship must have two endpoints (a-side/b-side), may have an id attribute as key, and may have some attributes.
		 */
		final AbstractStatement aSideDataNode = bdaTopRel.getChildStatements().stream()
				.filter(stmt -> stmt.is(CY.STMT_LEAF) || stmt.is(CY.STMT_LEAF_LIST))
				.filter(stmt -> stmt.hasAtLeastOneChildOf(CORAN.ORAN_SMO_TEIV_COMMON_YANG_EXTENSIONS__A_SIDE))
				.findAny().orElse(null);

		final AbstractStatement bSideDataNode = bdaTopRel.getChildStatements().stream()
				.filter(stmt -> stmt.is(CY.STMT_LEAF) || stmt.is(CY.STMT_LEAF_LIST))
				.filter(stmt -> stmt.hasAtLeastOneChildOf(CORAN.ORAN_SMO_TEIV_COMMON_YANG_EXTENSIONS__B_SIDE))
				.findAny().orElse(null);

		if(aSideDataNode == null || bSideDataNode == null) {
			context.logWarn(Constants.TEXT_IGNORED_UNRESOLVABLE + " Did not generate TI BDA '" + bdaTopRel.getRelationshipName() + "' as A-side or B-side extensions could not be found.");
			return;
		}

		/*
		 * We need to find the two types that are referenced by the a-side/b-side.
		 */
		final YOranSmoTeivASide aSide = aSideDataNode.getChild(CORAN.ORAN_SMO_TEIV_COMMON_YANG_EXTENSIONS__A_SIDE);
		final YOranSmoTeivBSide bSide = bSideDataNode.getChild(CORAN.ORAN_SMO_TEIV_COMMON_YANG_EXTENSIONS__B_SIDE);
		final YList aSideMoc = findMoc(context, bdaTopRel, aSide.getTeivTypeName());
		final YList bSideMoc = findMoc(context, bdaTopRel, bSide.getTeivTypeName());

		if(aSideMoc == null || bSideMoc == null) {
			context.logWarn(Constants.TEXT_IGNORED_UNRESOLVABLE + " Did not generate TI BDA '" + bdaTopRel.getRelationshipName() + "' as A-side or B-side MOC could not be found.");
			return;
		}

		/*
		 * We have enough stuff now to create the association.
		 */
		final PrimaryTypeAssociation ptAssociation = new PrimaryTypeAssociation();
		ptrd.getAssociation().add(ptAssociation);

		ptAssociation.setName(bdaTopRel.getRelationshipName());
		ptAssociation.setKind(AssociationKind.BI_DIRECTIONAL);
		ptAssociation.setReadBehavior(ReadBehaviorType.FROM_PERSISTENCE);
		ptAssociation.setWriteBehavior(WriteBehaviorType.PERSIST);
		ptAssociation.setDesc(PropertyUtils.getDescription(bdaTopRel, "BDA " + bdaTopRel.getRelationshipName()));

		final AssociationEndpoint endpointA = new AssociationEndpoint();
		final AssociationEndpoint endpointB = new AssociationEndpoint();
		ptAssociation.setASide(endpointA);
		ptAssociation.setBSide(endpointB);

		endpointA.setEndpointName(aSideDataNode.getStatementIdentifier());
		endpointB.setEndpointName(bSideDataNode.getStatementIdentifier());
		endpointA.setDesc(PropertyUtils.getDescription(aSideDataNode, "A-side"));
		endpointB.setDesc(PropertyUtils.getDescription(bSideDataNode, "B-side"));
		endpointA.setPrimaryTypeUrn(EModelHelper.getImpliedVersionedUrnForMoc(aSideMoc));
		endpointB.setPrimaryTypeUrn(EModelHelper.getImpliedVersionedUrnForMoc(bSideMoc));

		endpointA.setMinSizeConstraint(getMinSizeConstraint(aSideDataNode));
		endpointB.setMinSizeConstraint(getMinSizeConstraint(bSideDataNode));
		endpointA.setMaxSizeConstraint(getMaxSizeConstraint(aSideDataNode));
		endpointB.setMaxSizeConstraint(getMaxSizeConstraint(bSideDataNode));

		handleAttributes(context, ptAssociation, bdaTopRel);
	}

	private static void handleAttributes(final TransformerContext context, final PrimaryTypeAssociation ptAssociation, final YOranSmoTeivBiDirectionalTopologyRelationship udaTopRel) {

		final List<AbstractStatement> dataNodes = udaTopRel.getChildStatements().stream()
				.filter(AbstractStatement::definesDataNode)
				.collect(Collectors.toList());

		/*
		 * Get the key leaf and the attributes container, if any.
		 */
		final YKey yKey = udaTopRel.getChild(CY.STMT_KEY);
		final String keyName = yKey == null ? null : yKey.getKeys().get(0);

		final AbstractStatement keyDataNode = dataNodes.stream()
				.filter(stmt -> stmt.getStatementIdentifier().equals(keyName))
				.findAny().orElse(null);

		final YContainer attributesContainer = ThreeGPPSupport.get3gppAttributesContainer(udaTopRel);

		/*
		 * Anything else defined directly under the relationship will be ignored. Dump out warnings.
		 */
		dataNodes.removeIf(stmt -> {
			if(stmt == keyDataNode || stmt == attributesContainer) {
				return true;
			}
			if(stmt.hasAtLeastOneChildOf(CORAN.ORAN_SMO_TEIV_COMMON_YANG_EXTENSIONS__A_SIDE) || stmt.hasAtLeastOneChildOf(CORAN.ORAN_SMO_TEIV_COMMON_YANG_EXTENSIONS__B_SIDE)) {
				return true;
			}
			return false;
		});
		dataNodes.forEach(stmt -> {
			context.logWarn(Constants.TEXT_NOT_GENERATED_WRONG_LOCATION + " Did not generate data node '" + stmt.getStatementIdentifier() + "' as it is not defined inside the 'attributes' container.");
		});

		/*
		 * Now we can create the attributes.
		 */
		final List<AbstractStatement> attributes = new ArrayList<>();
		if(keyDataNode != null) {
			attributes.add(keyDataNode);
		}
		if(attributesContainer != null) {
			attributes.addAll(ExtractionHelper.getChildDataNodes(attributesContainer, ExtractionHelper::isContainerOrListOrLeafOrLeafList));
		}

		attributes.forEach(stmt -> {
			final AssociationAttribute assocAttr = new AssociationAttribute();
			ptAssociation.getAttribute().add(assocAttr);

			EModelHelper.setEModelNamedEntityDefinitionProperties(assocAttr, stmt);
			EModelHelper.setEModelAttributeDefinitionProperties(context, assocAttr, stmt);
		});
	}

	private static MinMaxValue getMinSizeConstraint(final AbstractStatement leafOrLeafList) {

		long minValue = 0;

		if(leafOrLeafList.is(CY.STMT_LEAF)) {
			/*
			 * Minimum can be either 0 or 1. It is 1 if it is mandatory.
			 */
			final YMandatory yMandatory = leafOrLeafList.getChild(CY.STMT_MANDATORY);
			if(yMandatory != null && yMandatory.isMandatoryTrue()) {
				minValue = 1;
			}
		} else {
			/*
			 * Minimum can be absent or any number. Check for min-elements.
			 */
			final YMinElements yMinElements = leafOrLeafList.getChild(CY.STMT_MIN_ELEMENTS);
			if(yMinElements != null && yMinElements.getMinValue() > 0) {
				minValue = yMinElements.getMinValue();
			}
		}

		if(minValue > 0) {
			final MinMaxValue minMaxValue = new MinMaxValue();
			minMaxValue.setValue(minValue);
			return minMaxValue;
		}

		return null;
	}

	private static MinMaxValue getMaxSizeConstraint(final AbstractStatement leafOrLeafList) {

		long maxValue = 0;

		if(leafOrLeafList.is(CY.STMT_LEAF)) {
			maxValue = 1;
		} else {
			/*
			 * Maximum can be absent or any number. Check for max-elements.
			 */
			final YMaxElements yMaxElements = leafOrLeafList.getChild(CY.STMT_MAX_ELEMENTS);
			if(yMaxElements != null && yMaxElements.getMaxValue() > 0) {
				maxValue = yMaxElements.getMaxValue();
			}
		}

		if(maxValue > 0) {
			final MinMaxValue minMaxValue = new MinMaxValue();
			minMaxValue.setValue(maxValue);
			return minMaxValue;
		}

		return null;
	}

	private static YList findMoc(final TransformerContext context, final YOranSmoTeivBiDirectionalTopologyRelationship bdaTopRel, final String possiblyPrefixedMocName) {

		final String prefix = QNameHelper.hasPrefix(possiblyPrefixedMocName) ? QNameHelper.extractPrefix(possiblyPrefixedMocName) : null;
		final String namespace = prefix == null ? bdaTopRel.getEffectiveNamespace() : bdaTopRel.getPrefixResolver().resolveNamespaceUri(prefix);
		final String mocName = QNameHelper.hasPrefix(possiblyPrefixedMocName) ? QNameHelper.extractName(possiblyPrefixedMocName) : possiblyPrefixedMocName;

		return (YList) ExtractionHelper.getStatementsAtSchemaRoot(context.getDeviatedSchema()).stream()
				.filter(stmt -> stmt.is(CY.STMT_LIST))
				.filter(stmt -> stmt.getStatementIdentifier().equals(mocName))
				.filter(stmt -> stmt.getEffectiveNamespace().equals(namespace))
				.findAny().orElse(null);
	}

	private static boolean isBdaTopRel(final AbstractStatement stmt) {
		return stmt.is(CORAN.ORAN_SMO_TEIV_COMMON_YANG_EXTENSIONS__BI_DIRECTIONAL_TOPOLOGY_RELATIONSHIP);
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 */
	private static void ensureStableGeneration(final PrimaryTypeRelationshipDefinition ptrd) {

		Collections.sort(ptrd.getAssociation(), (o1, o2) -> {
			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		});
	}
}
