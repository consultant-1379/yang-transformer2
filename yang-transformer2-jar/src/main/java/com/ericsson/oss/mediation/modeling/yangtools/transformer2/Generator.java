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

package com.ericsson.oss.mediation.modeling.yangtools.transformer2;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.CfmMimInfoGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EModelHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeActionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeAttributeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeChoiceAndRequiresGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.TopologyInventoryRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.NamespaceNamePath;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class Generator {

	/**
	 * Transforms the cleaned schema tree(s) into EModels.
	 */
	static void transform(final TransformerContext context) {

		/*
		 * The mock ManagedElement. Somewhat legacy, but still required by some of the YANG-based nodes.
		 */
		if(context.generateMockManagedElement() && !context.generateForDomainApplicationModel()) {
			final PrimaryTypeDefinition mockManagedElement = PrimaryTypeGenerator.createMockManagedElement(context);
			context.getCreatedEmodels().addGeneratedEModel(mockManagedElement);
		}

		/*
		 * MOCs will only get generated if they exist in the DEVIATED variant - quite simply, if an MO has been
		 * completely deviated-out, there is no point whatsoever in generating it, as it can never be accessed in ENM.
		 */
		final List<AbstractStatement> mocsAtDeviatedSchemaRoot = ExtractionHelper.getStatementsAtSchemaRoot(context.getDeviatedSchema(), PreProcessor::isMoc);
		generateDpsPrimaryTypes(context, mocsAtDeviatedSchemaRoot, true);

		/*
		 * Generate leafs/leaf-lists that sit at the top-level of the schema. Have never seen them in the wild,
		 * but it is well possible. Likewise, generate the RPCs. Cannot be done for domain-application models,
		 * of course.
		 */
		if(!context.generateForDomainApplicationModel()) {
			generateTopLevelAttributes(context);
			generateRpcs(context);
		}

		/*
		 * Generate the relationships. Works of DEVIATED, of course, as DEVIATED represents the
		 * actual view of the node model.
		 */
		final PrimaryTypeRelationshipDefinition ptrd = PrimaryTypeRelationshipGenerator.generatePrimaryTypeRelationshipModel(context, mocsAtDeviatedSchemaRoot);
		context.getCreatedEmodels().addGeneratedEModel(ptrd);

		/*
		 * Generate the relationships for the Topology/Inventory service.
		 */
		TopologyInventoryRelationshipGenerator.generateUniDirectionalRelationships(context, ptrd);

		/*
		 * Any containers / lists that were not generated will be listed in meta-data.
		 */
		recordNonTransformedContainersAndLists(context);

		/*
		 * This the cfm_miminfo = the Supported Model. Only relates to NRMs, not domain application models.
		 */
		if(!context.generateForDomainApplicationModel()) {
			final ManagementInformationModelInformation cfmMimInfo = CfmMimInfoGenerator.generateCfmMimInfo(context);
			context.getCreatedEmodels().addGeneratedEModel(cfmMimInfo);
			context.getCreatedEmodels().setGeneratedCfmMimInfoModel(cfmMimInfo);
		}

		/*
		 * All models generated. Along the way, we may have created DPT PT EXT models unnecessarily. If so,
		 * remove those, as they are empty and serve no purpose.
		 */
		context.getCreatedEmodels().removeGeneratedEModels(emodel -> PrimaryTypeExtensionGenerator.isEmptyPrimaryTypeExtensionModel(emodel));

		/*
		 * Create combined EDTs if so desired.
		 */
		if(context.generateCombinedEdt()) {
			EnumerationTypeGenerator.createCombinedEdts(context);
		}
	}

	private static void generateDpsPrimaryTypes(final TransformerContext context, final List<AbstractStatement> mocsInDeviated, final boolean atTopLevelOfSchema) {

		mocsInDeviated.forEach(mocInDeviated -> {

			/*
			 * Find the exact same container or list in AUGMENTED. It *must* exist in AUGMENTED,
			 * as it is not possible to 'deviate add' a list / container.
			 */
			final AbstractStatement mocInAugmented = PreProcessor.getAugmentedVariantMoc(mocInDeviated);

			/*
			 * There are corner cases in the 3GPP models whereby a complete MO type is defined in a grouping,
			 * and the grouping is used multiple times by various MOCs in the same namespace. Normally this
			 * would result in the exact same DPS PrimaryType being generated multiple times, which is not
			 * allowed. We check for this situation here. Note that 3GPP explicitly allows for the same MOC
			 * to have different containment parents; however, YANG only supports this through the grouping
			 * concept. This gives rise to the possibility that different augmentations / deviations are done
			 * on different usages of the grouping. 3GPP explicitly disallows this - it is the SAME MOC type
			 * every time, so if an augmentation / deviation is applied to one usage of the grouping, it must
			 * also be applied to all other usages of the grouping. Otherwise this is a fault in the models.
			 */
			if(PreProcessor.is3gppMoc(mocInAugmented) && context.getCreatedEmodels().containsGeneratedEModel(PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(mocInAugmented))) {
				return;
			}

			/*
			 * We need to generate the container as it originally is, without deviations. We use the AUGMENTED
			 * variant as it may not exist in BASE, and augmentations cannot change any of the MOC properties.
			 */
			final PrimaryTypeDefinition ptd = PrimaryTypeGenerator.createDpsPrimaryType(context, mocInAugmented, atTopLevelOfSchema);
			context.getCreatedEmodels().addGeneratedEModel(ptd);

			/*
			 * We create the extension for the PT straight away, although it may turn out that we won't need it.
			 * The extension represents the "difference" from ORIGINAL, so we must use the DEVIATING of the
			 * list/container.
			 */
			final PrimaryTypeExtensionDefinition pted = PrimaryTypeExtensionGenerator.createDpsPrimaryTypeExtension(context, ptd, mocInDeviated);
			context.getCreatedEmodels().addGeneratedEModel(pted);

			/*
			 * If it is a container or key-less list we have to generate an artificial key attribute.
			 * 
			 * There is an exception to this: If the container has been renamed by the node to include
			 * the node app instance name, we will translate it to a "list" in ENM, and will use the
			 * existing leaf "id" as key, so no need to create an artificial key.
			 */
			if(PreProcessor.isKeyLessList(mocInDeviated) || (mocInDeviated.is(CY.STMT_CONTAINER) && !PreProcessor.isNainMoc(mocInDeviated))) {
				PrimaryTypeAttributeGenerator.generateArtificialKeyAttribute(mocInDeviated, ptd);
			}

			/*
			 * Make sure choices are generated before the attributes, as they will mark attributes / child-MOCs
			 * as choice/case members.
			 */
			PrimaryTypeChoiceAndRequiresGenerator.handleMocChoices(context, mocInDeviated, ptd, pted);
			PrimaryTypeAttributeGenerator.handleMocAttributes(context, mocInDeviated, ptd, pted);
			PrimaryTypeActionGenerator.handleMocActions(context, mocInDeviated, pted);

			PrimaryTypeGenerator.ensureStableModelGeneration(ptd);

			/*
			 * Now we navigate down a level and repeat.
			 */
			final List<AbstractStatement> childMocsInDeviated = ExtractionHelper.getChildMocs(mocInDeviated);
			generateDpsPrimaryTypes(context, childMocsInDeviated, false);
		});
	}

	/**
	 * Generates any top-level leaf / leaf-list into the extension model for the containment parent of all
	 * top-level data nodes. (The same extension model will also receive the actions that result from RPCs.)
	 */
	private static void generateTopLevelAttributes(final TransformerContext context) {

		final List<AbstractStatement> topLevelAttributesAtDeviatedSchemaRoot = ExtractionHelper.getStatementsAtSchemaRoot(context.getDeviatedSchema(), ExtractionHelper::isLeafOrLeafList);
		if(topLevelAttributesAtDeviatedSchemaRoot.isEmpty()) {
			return;
		}

		final PrimaryTypeExtensionDefinition extForContainmentParent = PrimaryTypeExtensionGenerator.getOrCreateDpsPrimaryTypeExtensionForContainmentParent(context);

		PrimaryTypeAttributeGenerator.handleTopLevelAttributes(context, topLevelAttributesAtDeviatedSchemaRoot, extForContainmentParent);
	}

	private static void generateRpcs(final TransformerContext context) {

		/*
		 * Just like actions, RPCs are taken from the deviated variant of the schema.
		 */
		final List<AbstractStatement> rpcs = ExtractionHelper.getStatementsAtSchemaRoot(context.getDeviatedSchema(), stmt -> stmt.is(CY.STMT_RPC));
		if(rpcs.isEmpty()) {
			return;
		}

		/*
		 * Every RPC sits at the top-level of the schema. This means it cannot be generated into one of the MOCs generated
		 * here. The only solution is to generate these on to the effective containment parent. Of course, this will have
		 * to be done in a target version-specific manner - so we need an extension model.
		 */
		final PrimaryTypeExtensionDefinition pted = PrimaryTypeExtensionGenerator.getOrCreateDpsPrimaryTypeExtensionForContainmentParent(context);
		PrimaryTypeActionGenerator.handleRpcs(context, rpcs, pted);
	}

	private static void recordNonTransformedContainersAndLists(final TransformerContext context) {

		final List<NamespaceNamePath> paths = context.getPathsOfNonGeneratedContainersAndLists();
		if(paths.isEmpty()) {
			return;
		}

		/*
		 * There is a possibility that some of the logic earlier on that removes schema nodes executes such that a
		 * node lower in the schema tree is removed, before a node higher in the same branch is removed. For example,
		 * the following entries may exist:
		 * 
		 * /ns1:cont1
		 * /ns1:cont1/ns1:cont2
		 * 
		 * The second entry is not required, of course. If anything, it would blow up the amount of meta-data
		 * generated, and blows up the filter in mediation later on for no good reason. So we remove these
		 * paths "lower-down".
		 * 
		 * Note: the algorithm will usually have n*n performance which is not ideal, but we don't expect the
		 * list to be large, so we should be ok.
		 */
		final List<NamespaceNamePath> pathsCopy = new ArrayList<>(paths);
		pathsCopy.forEach(onePathCopy -> {
			paths.removeIf(path -> path.sitsBelow(onePathCopy));
		});

		/*
		 * The paths for the non-generated containers and lists go into meta-data that is placed into the DPS PT
		 * extension model for MeContext, irrespective of whether there is a mock ME. This simplifies code logic
		 * in mediation as it then does not have to figure out the MO type on which the information can be found.
		 */
		final PrimaryTypeExtensionDefinition pted = PrimaryTypeExtensionGenerator.getOrCreateDpsPrimaryTypeExtensionForMeContext(context);

		for(int i = 0; i < paths.size(); ++i) {
			EModelHelper.attachMeta(pted.getMeta(), Constants.META_NON_GENERATED_DATA_NODE_PATH + i, paths.get(i).toEscapedString());
			context.logInfo(Constants.TEXT_TRANSFORMER + " non-generated-path: " + paths.get(i).toUnescapedString());
		}
	}
}
