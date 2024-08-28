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

package com.ericsson.oss.mediation.modeling.yangtools.transformer2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EriCustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.YangFeature;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;

/**
 * Trims various bits and bobs from a schema.
 */
public class SchemaTrimmer {

	public static void removeIfFeatureDependentSchemaNodes(final TransformerContext context) {

		final List<AbstractStatement> topLevelStatements = ExtractionHelper.getStatementsAtSchemaRoot(context.getDeviatedSchema());

		switch(context.getFeatureHandling()) {
		case REMOVE_ALL:
			removeIfFeatureDependentSchemaNodes(context, topLevelStatements, context.getDeviatedSchema(), true, null);
			break;
		case CONFIGURED:
			removeIfFeatureDependentSchemaNodes(context, topLevelStatements, context.getDeviatedSchema(), false, IfFeatureHelper.getEnabledFeaturesFromYangLibrary(context));
			break;
		default:
		}
	}

	/**
	 * Removes all statements from the schema tree that are dependent on a feature, and the feature is not enabled.
	 */
	private static void removeIfFeatureDependentSchemaNodes(final TransformerContext context, final List<AbstractStatement> statements, final Schema schema, final boolean removeAll, final Set<YangFeature> enabledFeatures) {

		for(final AbstractStatement stmt : statements) {
			if(stmt.hasAtLeastOneChildOf(CY.STMT_IF_FEATURE) && (removeAll || !IfFeatureHelper.ifFeaturesUnderStatementAreSatisfied(stmt, schema, enabledFeatures))) {
				context.logInfo(Constants.TEXT_REMOVED_IF_FEATURE + " Removed '" + ExtractionHelper.getHumanReadablePathToSchemaNode(stmt) + "' from DEVIATED schema during pre-processing as 'if-feature' statement not satisfied / feature-handling is REMOVE_ALL.");

				/*
				 * We only capture the non-generated path for REMOVE_ALL. There is no need to do this for
				 * CONFIGURED, as the data node does not exist on the device, hence it cannot be read up anyway!
				 */
				if(removeAll) {
					capturePathOfNonGeneratedStatement(context, stmt);
				}

				stmt.getParentStatement().removeChild(stmt);

			} else {

				final List<AbstractStatement> children = stmt.getChildStatements();
				if(!children.isEmpty()) {
					removeIfFeatureDependentSchemaNodes(context, new ArrayList<>(children), schema, removeAll, enabledFeatures);		// deep copy needed
				}
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	public static void removeSchemaNodesOfExcludedNamespaces(final TransformerContext context) {
		removeSchemaNodesOfExcludedNamespaces(context, context.getBaseSchema());
		removeSchemaNodesOfExcludedNamespaces(context, context.getAugmentedSchema());
		removeSchemaNodesOfExcludedNamespaces(context, context.getDeviatedSchema());
	}

	/**
	 * We remove from the schema tree any schema node that sits in a exclude-listed namespace. This way we don't
	 * have to worry about them anymore later on during comparison. It is safe to do so, as either a complete
	 * MOC will be removed and not generated, or an attribute that has been augmented-in. Either way, original
	 * MOCs as defined in BASE or AUGMENTING will always be generated the same way (if they are generated),
	 * no matter the exclude-list.
	 */
	private static void removeSchemaNodesOfExcludedNamespaces(final TransformerContext context, final Schema schema) {

		final List<Pattern> patterns = ExcludeHandler.getExcludePatterns(context.getExcludedNamespaces());

		if(!patterns.isEmpty()) {
			final List<AbstractStatement> topLevelStatements = ExtractionHelper.getStatementsAtSchemaRoot(schema, AbstractStatement::definesSchemaNode);
			removeSchemaNodesOfExcludedNamespaces(context, patterns, topLevelStatements);
		}
	}

	private static void removeSchemaNodesOfExcludedNamespaces(final TransformerContext context, final List<Pattern> patterns, final List<AbstractStatement> statements) {

		statements.forEach(stmt -> {
			if(isSchemaNodeInExcludedNamespace(patterns, stmt)) {
				context.logInfo(Constants.TEXT_REMOVED_NS_EXCLUDE_LISTED + " Removed '" + ExtractionHelper.getHumanReadablePathToSchemaNode(stmt) + "' from " + PreProcessor.getVariant(stmt) + " schema during pre-processing as it's namespace is exclude-listed.");
				capturePathOfNonGeneratedStatement(context, stmt);
				stmt.getParentStatement().removeChild(stmt);
			} else {
				final List<AbstractStatement> children = new ArrayList<>(stmt.getChildStatements());		// deep copy needed
				removeSchemaNodesOfExcludedNamespaces(context, patterns, children);
			}
		});
	}

	private static boolean isSchemaNodeInExcludedNamespace(final List<Pattern> patterns, final AbstractStatement stmt) {

		if(!stmt.definesSchemaNode()) {
			return false;
		}

		return ExcludeHandler.isNamespaceExcluded(patterns, stmt.getEffectiveNamespace());
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * This is not quite the same as the normal exclude mechanism. It only applies to RPCs.
	 */
	public static void removeRpcsBasedOnNamespace(final TransformerContext context) {

		if(!context.generateRpcs()) {
			context.logInfo(Constants.TEXT_REMOVED_RPC_NAMESPACE + " Removing all RPCs as they shall not be generated according to settings.");
		}

		final List<Pattern> excludePatterns = ExcludeHandler.getExcludePatterns(context.getExcludedNamespacesForRpcs());

		removeRpcsBasedOnNamespace(context, context.getBaseSchema(), excludePatterns);
		removeRpcsBasedOnNamespace(context, context.getAugmentedSchema(), excludePatterns);
		removeRpcsBasedOnNamespace(context, context.getDeviatedSchema(), excludePatterns);
	}

	/**
	 * RPCs of certain namespaces will be removed. Likewise, if RPCs are not supported they will (all) be removed. Note
	 * that this happens only in the deviated variant, as later on only DEVIATED will be considered.
	 */
	public static void removeRpcsBasedOnNamespace(final TransformerContext context, final Schema schema, final List<Pattern> excludePatterns) {

		final List<AbstractStatement> allRpcs = ExtractionHelper.getStatementsAtSchemaRoot(schema, stmt -> stmt.is(CY.STMT_RPC));

		allRpcs.forEach(rpc -> {
			if(!context.generateRpcs()) {
				rpc.getParentStatement().removeChild(rpc);
			} else if(ExcludeHandler.isNamespaceExcluded(excludePatterns, rpc.getEffectiveNamespace())) {
				context.logInfo(Constants.TEXT_REMOVED_RPC_NAMESPACE + " Removed RPC '" + PreProcessor.getOriginalStatementIdentifier(rpc) + "' during pre-processing as it sits in a namespace for which RPCs are exclude-listed.");
				rpc.getParentStatement().removeChild(rpc);
			}
		});
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	public static void removeExplicitlyRemovedSchemaNodes(final TransformerContext context) {
		removeSchemaNodes(context, context.getBaseSchema());
		removeSchemaNodes(context, context.getAugmentedSchema());
		removeSchemaNodes(context, context.getDeviatedSchema());
	}

	/**
	 * Explicitly remove certain schema nodes.
	 */
	private static void removeSchemaNodes(final TransformerContext context, final Schema schema) {

		context.getSchemaNodesToRemove().forEach(path -> {

			final List<String> schemaPathElements = new ArrayList<>(Arrays.asList(path.substring(1).split("/")));
			final List<AbstractStatement> candidates = ExtractionHelper.getStatementsAtSchemaRoot(schema, AbstractStatement::definesSchemaNode);

			final AbstractStatement targetSchemaNode = ExtractionHelper.findTargetSchemaNode(schemaPathElements, candidates);
			if(targetSchemaNode != null) {
				context.logInfo(Constants.TEXT_REMOVED_EXPLICIT + " Removed schema node '" + ExtractionHelper.getHumanReadablePathToSchemaNode(targetSchemaNode) + "' during pre-processing due to explicit removal.");
				capturePathOfNonGeneratedStatement(context, targetSchemaNode);
				targetSchemaNode.getParentStatement().removeChild(targetSchemaNode);
			} else {
				context.logInfo(Constants.TEXT_IGNORED_UNRESOLVABLE + " Cannot explicitly remove schema node for path '" + path + "' as it was not found in the schema during pre-processing.");
			}
		});
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Removes from the deviated-tree all MOCs (containers, lists) that will not be notified. Since they
	 * are not notified, they will not be stored in the ENM mirror. If they are not stored in the mirror,
	 * we can simply pretend that these do not exist (have been "deviated out").
	 * <p>
	 * A similar consideration applies to static-data then.
	 * <p>
	 * Note that in 3GPP, containers/lists may be used for structs/struct-sequences and the wrap construct.
	 * These will effectively translate to attributes, not MOCs. Hence those should not be removed.
	 */
	public static void removeNonNotifiedNonStaticContainersAndLists(final TransformerContext context) {
		final List<AbstractStatement> topLevelContainersAndLists = ExtractionHelper.getStatementsAtSchemaRoot(context.getDeviatedSchema(), ExtractionHelper::isContainerOrList);
		removeNonNotifiedNonStaticMocs(context, topLevelContainersAndLists);
	}

	private static void removeNonNotifiedNonStaticMocs(final TransformerContext context, final List<AbstractStatement> containersAndLists) {

		containersAndLists.forEach(containerOrList -> {

			final boolean isNotified = PreProcessor.isNotified(containerOrList);
			final boolean isStatic = EriCustomProcessor.isStatementEffectivelyStaticDataTrue(containerOrList);
			final boolean isMoc = PreProcessor.isMoc(containerOrList);

			if(isMoc && !isNotified && !isStatic) {
				context.logInfo(Constants.TEXT_REMOVED_NON_PERSISTENT + " Removed '" + ExtractionHelper.getHumanReadablePathToSchemaNode(containerOrList) + "' during pre-processing as the " + containerOrList.getStatementName() + " is deemed non-notified & non-static.");
				capturePathOfNonGeneratedStatement(context, containerOrList);
				containerOrList.getParentStatement().removeChild(containerOrList);
			} else {
				/*
				 * Go down in the tree.
				 */
				final List<AbstractStatement> childDataNodes = ExtractionHelper.getChildDataNodes(containerOrList, ExtractionHelper::isContainerOrList);
				if(!childDataNodes.isEmpty()) {
					removeNonNotifiedNonStaticMocs(context, childDataNodes);
				}
			}
		});
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * There is an issue with structs, where all members may be defined in a YAM different to the struct itself,
	 * which would result in an empty struct being generated - however, it is not possible to generate a OSS_CDT
	 * without members. So we remove these members here, allowing us to check later on for empty structs during
	 * processing. This processing only ever applies to ORIGINAL, never to DEVIATED.
	 * <p>
	 * The removal of members here will typically result in two CDTs being generated (one for ORIGINAL, one for
	 * DEVIATED), and an DPS attribute-replacement being generated.
	 */
	public static void removeUnreliableStructMembers(final TransformerContext context) {
		removeUnreliableStructMembers(context, context.getBaseSchema());
		removeUnreliableStructMembers(context, context.getAugmentedSchema());
	}

	private static void removeUnreliableStructMembers(final TransformerContext context, final Schema schema) {
		final List<AbstractStatement> topLevelMocs = ExtractionHelper.getStatementsAtSchemaRoot(schema, PreProcessor::isMoc);
		removeUnreliableStructMembers(context, topLevelMocs);
	}

	private static void removeUnreliableStructMembers(final TransformerContext context, final List<AbstractStatement> mocs) {
		mocs.forEach(moc -> {

			final List<AbstractStatement> attributes = ExtractionHelper.getChildAttributesForMoc(moc);
			for(final AbstractStatement attr : attributes) {

				if(PreProcessor.isStructOrStructSequence(attr)) {
					removeUnreliableSchemaNodesWithinStruct(context, attr);
				}
			}

			final List<AbstractStatement> childMocs = ExtractionHelper.getChildMocs(moc);
			removeUnreliableStructMembers(context, childMocs);
		});
	}

	private static void removeUnreliableSchemaNodesWithinStruct(final TransformerContext context, final AbstractStatement struct) {

		final boolean structDefinedInSubmodule = PropertyUtils.definedInSubmodule(struct);

		final List<AbstractStatement> membersOfStruct = ExtractionHelper.getMembersOfStruct(struct);
		for(final AbstractStatement member : membersOfStruct) {
			/*
			 * A struct member is removed from the struct if it comes from a YAM different to the struct itself,
			 * or when the whole struct comes from a submodule.
			 */
			final boolean memberDefinedInSameYamAsStruct = PropertyUtils.definedInSameYam(struct, member);

			final boolean remove = structDefinedInSubmodule || !memberDefinedInSameYamAsStruct;
			if(remove) {
				context.logInfo(Constants.TEXT_REMOVED_UNRELIABLE + " Removed member '" + member.getStatementIdentifier() + "' from struct '" + ExtractionHelper.getHumanReadablePathToSchemaNode(struct) + "' in " + PreProcessor.getVariant(struct).toUpperCase() + " during pre-processing as its generation would not be reliable.");
				member.getParentStatement().removeChild(member);
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * A statement has been removed from the schema tree. If it is a container or list, we capture the path
	 * to the data node, as this information will be written out at a later stage.
	 */
	private static void capturePathOfNonGeneratedStatement(final TransformerContext context, final AbstractStatement removedStmt) {

		/*
		 * We don't care about leafs/leaf-lists here. Pulling these up from the node will not cause significant
		 * overheads, so we won't capture these - also, if we were to do this, it would likely result in a rather
		 * large filter that ENM will send down on a NETCONF get, which is not desirable.
		 */
		if(!removedStmt.is(CY.STMT_CONTAINER) && !removedStmt.is(CY.STMT_LIST)) {
			return;
		}

		/*
		 * We really only care about the DEVIATED variant. This is quite important, as conceivably a "deviate add"
		 * injects an Ericsson extension that suddenly makes a data node notifiable. In ORIGINAL this would lead
		 * to an entry, but in DEVIATED it would not, and of course at runtime the DEVIATED variant is of relevance.
		 */
		if(!PreProcessor.isInDeviatedVariant(removedStmt)) {
			return;
		}

		/*
		 * We want to make sure the data node is actually part of the data tree, and doesn't sit in an
		 * action or similar.
		 */
		if(!ExtractionHelper.withinDataTree(removedStmt)) {
			return;
		}

		/*
		 * Ok, record the path
		 */
		final NamespaceNamePath namespaceNamePath = new NamespaceNamePath();
		AbstractStatement stmt = removedStmt;

		while(true) {
			if(stmt.is(CY.STMT_MODULE)) {
				break;
			}
			/*
			 * The check for defines-data-node will skip the likes of 'choice' / 'case'.
			 */
			if(stmt.definesDataNode()) {
				namespaceNamePath.addPathSegmentAtStart(PreProcessor.getOriginalNamespace(stmt), PreProcessor.getOriginalStatementIdentifier(stmt));
			}

			stmt = stmt.getParentStatement();
		}

		context.addPathOfNonGeneratedContainerOrList(namespaceNamePath);
	}
}
