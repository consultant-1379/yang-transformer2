/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate;

import java.util.Collections;
import java.util.List;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.RelationshipEndpoint;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MinMaxValue;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.Target;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ThreeGPPSupport;

public class PrimaryTypeRelationshipGenerator {

	/**
	 * For a given schema tree generates the DPS containment relationships. The supplied top-level data nodes
	 * will be those of the DEVIATED tree.
	 */
	public static PrimaryTypeRelationshipDefinition generatePrimaryTypeRelationshipModel(final TransformerContext context, final List<AbstractStatement> mocsAtDeviatedSchemaRoot) {

		final ModelInfo ptrdModelInfo = getModelInfoForPrimaryTypeRelationshipDefinition(context.getTarget());
		final String desc = "Relationships for target type '" + context.getTarget().getType() + "' and target version '" + context.getTarget().getVersion() + "'.";

		final PrimaryTypeRelationshipDefinition ptrd = new PrimaryTypeRelationshipDefinition();

		EModelHelper.populateEModelHeaderForDesigned(ptrd, ptrdModelInfo, desc);

		generateContainments(context, ptrd, null, mocsAtDeviatedSchemaRoot);

		if(context.generateMockManagedElement() && !context.generateForDomainApplicationModel()) {
			generateContainmentForMockManagedElement(context, ptrd);
		}

		ensureStableDpsRelationshipGeneration(ptrd);

		return ptrd;
	}

	/**
	 * Creates the ModelInfo that contains the model identity of the generated DPS_RELATIONSHIP model.
	 * <p>
	 * Only a single DPS RELATIONSHIP model will be generated. We simply use the target version-specific
	 * name as namespace, and hard-code the name and version. Example for generated URN:
	 * <p>
	 * /dps_relationship/NODE__EPG-OI__3.12/all-relationships/1.0.0
	 */
	public static ModelInfo getModelInfoForPrimaryTypeRelationshipDefinition(final Target target) {
		final String namespace = EModelHelper.getTargetVersionSpecificName(target);
		final String version = EModelHelper.getModelInfoVersionForExtension();
		return new ModelInfo(SchemaConstants.DPS_RELATIONSHIP, namespace, "all-relationships", version);
	}

	/**
	 * Given a set of data nodes, generates the containment relationship between these and their parent MOCs.
	 */
	private static void generateContainments(final TransformerContext context, final PrimaryTypeRelationshipDefinition ptrd, final AbstractStatement parentMoc, final List<AbstractStatement> mocs) {

		/*
		 * If we generate for domain application model then we don't have a containment parent for
		 * the top-level MOCs.
		 */
		final String containmentParentOfTopLevelMocs = context.generateForDomainApplicationModel() ? null : context.getEffectiveContainmentParent().toImpliedUrn();

		/*
		 * The containment relationship for the 3GPP ManagedElement must only be generated if:
		 *
		 * - There is a Mock ManagedElement (e.g. core nodes)
		 * - No Mock ManagedElement and no explicit parent (e.g. routers - although they don't use 3GPP)
		 */
		final boolean generateContainmentRelationshipFor3gppME = context.getMockManagedElement() != null || context.getExplicitContainmentParent() == null;

		mocs.forEach(stmt -> {

			createContainment(context, ptrd, parentMoc, stmt, containmentParentOfTopLevelMocs, generateContainmentRelationshipFor3gppME);

			/*
			 * Go down through the tree recursively.
			 */
			final List<AbstractStatement> childMocs = ExtractionHelper.getChildDataNodes(stmt, PreProcessor::isMoc);
			if(!childMocs.isEmpty()) {
				generateContainments(context, ptrd, stmt, childMocs);
			}
		});
	}

	private static void createContainment(final TransformerContext context, final PrimaryTypeRelationshipDefinition ptrd, final AbstractStatement parentMoc, final AbstractStatement stmt, final String containmentParentOfTopLevelMocs, final boolean generateContainmentRelationshipFor3gppME) {

		/*
		 * The parent is supplied or comes from the context if this is a top-level data node...note this could be null.
		 */
		final String parentMocImpliedUrn = parentMoc == null ? containmentParentOfTopLevelMocs : EModelHelper.getImpliedVersionedUrnForMoc(parentMoc);
		if(parentMocImpliedUrn == null) {
			return;
		}

		/*
		 * The relationship is TMI-specific.
		 */
		final PrimaryTypeContainment cont = new PrimaryTypeContainment();
		cont.setRequiresTargetType(EModelHelper.getRequiresTargetType(context));

		final RelationshipEndpoint parent = new RelationshipEndpoint();
		parent.setPrimaryTypeUrn(parentMocImpliedUrn);
		cont.setParent(parent);

		/*
		 * The child is the statement itself.
		 */
		final RelationshipEndpoint child = new RelationshipEndpoint();
		child.setPrimaryTypeUrn(EModelHelper.getImpliedVersionedUrnForMoc(stmt));
		cont.setChild(child);

		/*
		 * Handle min/max constraint
		 */
		cont.setMinSizeConstraint(PropertyUtils.getMinElementsValue(stmt));
		cont.setMaxSizeConstraint(PropertyUtils.getMaxElementsValue(stmt));

		/*
		 * The name of the relationship is simply the concatenation of the URNs.
		 */
		final ModelInfo parentModelInfo = ModelInfo.fromImpliedUrn(parentMocImpliedUrn, SchemaConstants.DPS_PRIMARYTYPE);
		final ModelInfo childModelInfo = ModelInfo.fromImpliedUrn(child.getPrimaryTypeUrn(), SchemaConstants.DPS_PRIMARYTYPE);

		final String parentAsString = EModelHelper.getHumanReadableUrn(parentModelInfo);
		final String childAsString = EModelHelper.getHumanReadableUrn(childModelInfo);

		cont.setName(parentAsString + "__to__" + childAsString);
		cont.setDesc("Containment between " + parentAsString + " and " + childAsString);

		/*
		 * Add the relationship - the 3GPP ManagedElement will be handled in a special manner.
		 */
		if(!ThreeGPPSupport.is3gppManagedElement(stmt) || generateContainmentRelationshipFor3gppME) {
			ptrd.getContainment().add(cont);
		}
	}

	/**
	 * Creates the containment relationship between MeContext and the mock ManagedElement. Note that at
	 * runtime a MeContext instance is mandatory, as the MO ID of the mock ManagedElement is always "1".
	 */
	private static void generateContainmentForMockManagedElement(final TransformerContext context, final PrimaryTypeRelationshipDefinition ptrd) {

		final PrimaryTypeContainment cont = new PrimaryTypeContainment();
		cont.setRequiresTargetType(EModelHelper.getRequiresTargetType(context));

		/*
		 * The parent is always OSS_TOP::MeContext
		 */
		final RelationshipEndpoint parent = new RelationshipEndpoint();
		parent.setPrimaryTypeUrn(Constants.OSS_TOP_MECONTEXT_STAR.toImpliedUrn());
		cont.setParent(parent);

		/*
		 * The child is the mock ManagedElement.
		 */
		final RelationshipEndpoint child = new RelationshipEndpoint();
		child.setPrimaryTypeUrn(context.getMockManagedElement().toImpliedUrn());
		cont.setChild(child);

		/*
		 * At most 1 mock ManagedElement under MeContext.
		 */
		final MinMaxValue minMaxValue = new MinMaxValue();
		minMaxValue.setValue(1L);
		cont.setMaxSizeConstraint(minMaxValue);

		final String parentAsString = EModelHelper.getHumanReadableUrn(Constants.OSS_TOP_MECONTEXT_STAR);
		final String childAsString = EModelHelper.getHumanReadableUrn(context.getMockManagedElement());

		cont.setName(parentAsString + "__to__" + childAsString);
		cont.setDesc("Containment between " + parentAsString + " and (mock ManagedElement) " + childAsString);

		ptrd.getContainment().add(cont);
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 */
	private static void ensureStableDpsRelationshipGeneration(final PrimaryTypeRelationshipDefinition ptrd) {

		Collections.sort(ptrd.getContainment(), (o1, o2) -> {
			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		});
	}
}
