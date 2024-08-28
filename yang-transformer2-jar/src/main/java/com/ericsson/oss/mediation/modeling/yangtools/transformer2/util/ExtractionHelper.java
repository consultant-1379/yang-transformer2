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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.StatementModuleAndName;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.CERI;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.threegpp.C3GPP;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;

public class ExtractionHelper {

	/**
	 * Given a namespace, return the YangModel of the YAM that defines the namespace. Since submodules
	 * cannot declare a namespace, will always return a YANG 'module'.
	 * <p>
	 * Will always return a result, as any namespace used by the transformer code would be the result of a
	 * YANG module in the input, i.e. will never return null.
	 */
	public static YangModel findModuleDeclaringOriginalNamespace(final Schema schema, final String soughtOriginalNamespace) {
		return schema.getModuleRegistry().getAllYangModels().stream()
				.filter(yangModel -> yangModel.getYangModelRoot().isModule())
				.filter(yangModel -> yangModel.getYangModelRoot().getNamespace().equals(soughtOriginalNamespace))
				.findAny()
				.get();
	}

	/**
	 * Given a number of candidate statements, returns a schema node matching the type, namespace and name
	 * of a template statement, or null if not found.
	 */
	public static AbstractStatement findSchemaNode(final List<AbstractStatement> candidates, final AbstractStatement template) {

		final StatementModuleAndName templateStatementModuleAndName = template.getStatementModuleAndName();
		final String templateNamespace = PreProcessor.getOriginalNamespace(template);
		final String templateStatementIdentifier = PreProcessor.getRenamedStatementIdentifier(template);

		for(final AbstractStatement candidate : candidates) {
			if(!candidate.getStatementModuleAndName().equals(templateStatementModuleAndName)) {
				continue;
			}

			final String candidateNamespace = PreProcessor.getOriginalNamespace(candidate);
			final String candidateStatementIdentifier = PreProcessor.getRenamedStatementIdentifier(candidate);

			if(candidateNamespace.equals(templateNamespace) && candidateStatementIdentifier.equals(templateStatementIdentifier)) {
				return candidate;
			}
		}

		return null;
	}

	/**
	 * Given a data node (be that an attribute, an action input/outpt, or a struct member) being a descendant
	 * somewhere under an MOC, returns the ModelInfo representing the MOC that is the closest ancestor to it.
	 * <p>
	 * Will return the top-level containment parent if there is no MOC as ancestor, which is the case when:
	 * a) the data node sits at the top-level of the schema tree; b) is part of an 'rpc' statement.
	 */
	public static ModelInfo getModelInfoForOwningMoc(final TransformerContext context, final AbstractStatement dataNodeUnderMoc) {

		final AbstractStatement owningMoc = getOwningMoc(dataNodeUnderMoc);
		if(owningMoc == null) {
			/*
			 * There is no owning MOC, so the containment-parent it is.
			 */
			return context.getEffectiveContainmentParent();
		}

		return PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(owningMoc);
	}

	/**
	 * Given a data node (be that an attribute, an action input/outpt, or a struct member) being a descendant
	 * somewhere under an MOC, returns the container/list representing the MOC.
	 * <p>
	 * Will return null if there is no MOC as ancestor, which is the case when: a) the data node sits at
	 * the top-level of the schema tree; b) is part of an 'rpc' statement.
	 */
	public static AbstractStatement getOwningMoc(final AbstractStatement dataNodeUnderMoc) {

		AbstractStatement stmt = dataNodeUnderMoc.getParentStatement();
		while(true) {

			if(PreProcessor.isMoc(stmt)) {
				return stmt;
			}

			if(stmt.is(CY.STMT_MODULE)) {
				return null;
			}

			stmt = stmt.getParentStatement();
		}
	}

	/**
	 * Returns the path from the MOC that eventually owns the data node, onto the data node, taking
	 * into consideration that the data node might be directly under a MOC, or within an action somewhere,
	 * or within a struct member somewhere, within the MOC.
	 * <p>
	 * The path returned will be such to make sure that data nodes named the same, but sitting in different
	 * parts of the MOC, are generated as different paths. <b>Namespaces are not handled</b>, ie. two attributes
	 * having the same name, but different namespace, would result in the same path. The expectation is that
	 * duplicate attributes are not supplied in the input (eg. because they have been removed by validation).
	 * <p>
	 * Special handling is applied for the 3GPP attributes container, which does not contain structural
	 * information of relevance, and is thus skipped. Some examples:
	 * <p>
	 * GNBDUFunction__administrativeState ('GNBDUFunction' is a MOC; 'administrativeState' is an attribute)<br/>
	 * NRCellDU__lock__in__userLabel  ('NRCellDU' is a MOC; 'lock' is an action; 'userLabel' is an action parameter)<br/>
	 * GNBDUFunction__plmnId__mcc  ('GNBDUFunction' is a MOC; 'plmnId' is a struct; 'mcc' is a struct member)<br/>
	 * 
	 * @param dataNode usually a leaf or leaf-list denoting a scalar attribute / struct member / action
	 * 		parameter, but can also be a container / list denoting a struct / struct-sequence.
	 */
	public static String getPathFromMocToDataNode(final TransformerContext context, final AbstractStatement dataNode) {

		String path = PreProcessor.getRenamedStatementIdentifier(dataNode);

		AbstractStatement stmt = dataNode.getParentStatement();
		while(true) {

			if(PreProcessor.isMoc(stmt)) {
				/*
				 * We have arrived at the owning MOC. The name of the owning MOC will always be
				 * the first part of the path. We are done here.
				 */
				return PreProcessor.getMocName(stmt) + "__" + path;

			} else if(PreProcessor.isStructOrStructSequence(stmt)) {
				/*
				 * We add the name of the struct to the path.
				 */
				path = PreProcessor.getRenamedStatementIdentifier(stmt) + "__" + path;

			} else if(PreProcessor.is3gppAttributesContainer(stmt)) {
				/*
				 * We don't add anything to the path. The 3GPP 'attributes' container does not add anything
				 * structural that we must capture as part of the path. It is simply a way how 3GPP structures
				 * MOC attribute in the data model.
				 */
			} else if(stmt.is(CY.STMT_INPUT)) {
				path = "in__" + path;
			} else if(stmt.is(CY.STMT_OUTPUT)) {
				path = "out__" + path;
			} else if(stmt.is(CY.STMT_ACTION)) {
				path = PreProcessor.getRenamedStatementIdentifier(stmt) + "__" + path;
			} else if(stmt.is(CY.STMT_RPC)) {
				path = PreProcessor.getRenamedStatementIdentifier(stmt) + "__" + path;
			} else if(stmt.is(CY.STMT_MODULE)) {
				/*
				 * We have arrived at the module, meaning that the data node sits not under an MOC but is a top-level
				 * data node (for example, part of an RPC). In this case the "MOC" is the containment-parent of all
				 * top-level data nodes.
				 */
				final ModelInfo effectiveContainmentParent = context.getEffectiveContainmentParent();
				return effectiveContainmentParent.getName() + "__" + path;
			}

			stmt = stmt.getParentStatement();
		}
	}

	/**
	 * Returns a path that is nicely formatted so that a human can make sense of it. Typically used for logging.
	 */
	public static String getHumanReadablePathToSchemaNode(final AbstractStatement schemaNode) {

		String path = "/" + PreProcessor.getOriginalStatementIdentifier(schemaNode);
		AbstractStatement stmt = schemaNode.getParentStatement();

		while(true) {
			if(stmt.is(CY.STMT_MODULE)) {
				return path;
			}

			path = "/" + PreProcessor.getOriginalStatementIdentifier(stmt) + path;
			stmt = stmt.getParentStatement();
		}
	}

	/**
	 * Returns the attributes of a MOC.
	 */
	public static List<AbstractStatement> getChildAttributesForMoc(final AbstractStatement moc) {
		return PreProcessor.is3gppMoc(moc) ? ThreeGPPSupport.getChildAttributesFor3gppMoc(moc) : getChildAttributesForNon3gppMoc(moc);
	}

	/**
	 * Returns all YANG data nodes for a non-3GPP MOC. That's simply all leafs and leaf-lists below the MOC.
	 */
	private static List<AbstractStatement> getChildAttributesForNon3gppMoc(final AbstractStatement non3gppMoc) {
		return getChildDataNodes(non3gppMoc, ExtractionHelper::isLeafOrLeafList);
	}

	/**
	 * The members of the struct are usually leafs and leaf-lists, but a "wrap" could also be
	 * part of it, so we need to include 'lists' as well.
	 */
	public static List<AbstractStatement> getMembersOfStruct(final AbstractStatement struct) {
		return getChildDataNodes(struct, ExtractionHelper::isLeafOrLeafListOrList);
	}

	/**
	 * Returns whether the data node represents an empty struct.
	 */
	public static boolean isEmptyStruct(final AbstractStatement dataNode) {
		return PreProcessor.isStructOrStructSequence(dataNode) && getMembersOfStruct(dataNode).isEmpty();
	}

	/**
	 * For both input / output we can conceivably have structs as well, so we need to include not just
	 * the usual 'leafs' and 'leaf-lists', but also 'containers' and 'lists'.
	 */
	public static List<AbstractStatement> getChildAttributesOfInputOutput(final AbstractStatement inputOrOutput) {
		return getChildDataNodes(inputOrOutput, ExtractionHelper::isContainerOrListOrLeafOrLeafList);
	}

	/**
	 * Returns all YANG data nodes that should be considered containment-children of the parent MOC.
	 */
	public static List<AbstractStatement> getChildMocs(final AbstractStatement parentMoc) {
		return getChildDataNodes(parentMoc, PreProcessor::isMoc);
	}

	/**
	 * Returns the containment parent of the given MOC. Returns null if there is no parent (i.e. the
	 * supplied child MOC sits at the top-level of the schema).
	 */
	public static AbstractStatement getParentMoc(final AbstractStatement childMoc) {

		AbstractStatement stmt = childMoc.getParentStatement();
		while(true) {
			if(stmt == null || PreProcessor.isMoc(stmt)) {
				return stmt;
			}
			stmt = stmt.getParentStatement();
		}
	}

	/**
	 * Returns whether the statement has default value(s). Takes into consideration Ericsson and
	 * 3GPP 'initial-value' extensions
	 */
	public static boolean hasDefaultValue(final AbstractStatement stmt) {
		return stmt.hasAtLeastOneChildOf(CY.STMT_DEFAULT) || stmt.hasAtLeastOneChildOf(CERI.ERICSSON_YANG_EXTENSIONS__INITIAL_VALUE) || stmt.hasAtLeastOneChildOf(C3GPP.THREEGPP_COMMON_YANG_EXTENSIONS__INITIAL_VALUE);
	}

	private static final Set<StatementModuleAndName> SMAN_CONTAINER_AND_LIST = new HashSet<>(Arrays.asList(CY.STMT_CONTAINER, CY.STMT_LIST));
	private static final Set<StatementModuleAndName> SMAN_LEAF_AND_LEAFLIST = new HashSet<>(Arrays.asList(CY.STMT_LEAF, CY.STMT_LEAF_LIST));
	private static final Set<StatementModuleAndName> SMAN_LEAF_AND_CONTAINER = new HashSet<>(Arrays.asList(CY.STMT_LEAF, CY.STMT_CONTAINER));
	private static final Set<StatementModuleAndName> SMAN_LEAFLIST_AND_LIST = new HashSet<>(Arrays.asList(CY.STMT_LEAF_LIST, CY.STMT_LIST));
	private static final Set<StatementModuleAndName> SMAN_LEAF_AND_LEAFLIST_AND_LIST = new HashSet<>(Arrays.asList(CY.STMT_LEAF, CY.STMT_LEAF_LIST, CY.STMT_LIST));
	private static final Set<StatementModuleAndName> SMAN_CONTAINER_AND_LIST_AND_LEAF_AND_LEAFLIST = new HashSet<>(Arrays.asList(CY.STMT_CONTAINER, CY.STMT_LIST, CY.STMT_LEAF, CY.STMT_LEAF_LIST));

	public static boolean isContainer(final AbstractStatement stmt) {
		return stmt.is(CY.STMT_CONTAINER);
	}

	public static boolean isContainerOrList(final AbstractStatement stmt) {
		return SMAN_CONTAINER_AND_LIST.contains(stmt.getStatementModuleAndName());
	}

	public static boolean isLeafOrLeafList(final AbstractStatement stmt) {
		return SMAN_LEAF_AND_LEAFLIST.contains(stmt.getStatementModuleAndName());
	}

	public static boolean isLeafOrContainer(final AbstractStatement stmt) {
		return SMAN_LEAF_AND_CONTAINER.contains(stmt.getStatementModuleAndName());
	}

	public static boolean isLeafListOrList(final AbstractStatement stmt) {
		return SMAN_LEAFLIST_AND_LIST.contains(stmt.getStatementModuleAndName());
	}

	public static boolean isLeafOrLeafListOrList(final AbstractStatement stmt) {
		return SMAN_LEAF_AND_LEAFLIST_AND_LIST.contains(stmt.getStatementModuleAndName());
	}

	public static boolean isContainerOrListOrLeafOrLeafList(final AbstractStatement stmt) {
		return SMAN_CONTAINER_AND_LIST_AND_LEAF_AND_LEAFLIST.contains(stmt.getStatementModuleAndName());
	}

	public static boolean isLeaf(final AbstractStatement stmt) {
		return stmt.is(CY.STMT_LEAF);
	}

	public static boolean isList(final AbstractStatement stmt) {
		return stmt.is(CY.STMT_LIST);
	}

	/**
	 * Given a statement, returns all child data nodes. Data nodes underneath choice / case are likewise returned.
	 * Nested choice/case are also handled.
	 */
	public static List<AbstractStatement> getChildDataNodes(final AbstractStatement stmt) {
		return getChildDataNodes(stmt, v -> true);
	}

	/**
	 * Given a statement, returns all child data nodes satisfying the predicate. Data nodes underneath
	 * choice / case are likewise returned. Nested choice/case are also handled.
	 */
	public static List<AbstractStatement> getChildDataNodes(final AbstractStatement stmt, final Predicate<AbstractStatement> predicate) {

		List<AbstractStatement> result = null;

		for(final AbstractStatement directChild : stmt.getChildStatements()) {

			if(directChild.definesDataNode() && predicate.test(directChild)) {

				if(result == null) {
					result = new ArrayList<>();
				}
				result.add(directChild);

			} else if(directChild.is(CY.STMT_CHOICE)) {

				for(final AbstractStatement yCase : directChild.getChildren(CY.STMT_CASE)) {

					final List<AbstractStatement> childDataNodesOfCase = getChildDataNodes(yCase, predicate);
					if(!childDataNodesOfCase.isEmpty()) {
						if(result == null) {
							result = new ArrayList<>();
						}
						result.addAll(childDataNodesOfCase);
					}
				}
			}
		}

		return result == null ? Collections.<AbstractStatement>emptyList() : result;
	}

	/**
	 * Returns all statements (not just data nodes) that sit at the top-level of all modules.
	 */
	public static List<AbstractStatement> getStatementsAtSchemaRoot(final Schema schema) {
		return getStatementsAtSchemaRoot(schema, stmt -> true);
	}

	/**
	 * Returns all statements (not just data nodes) that sit at the top-level of all modules, and
	 * that satisfy the predicate.
	 */
	public static List<AbstractStatement> getStatementsAtSchemaRoot(final Schema schema, final Predicate<AbstractStatement> predicate) {
		final List<AbstractStatement> result = new ArrayList<>();

		for(final YangModel yangFile : schema.getModuleRegistry().getAllYangModels()) {

			final AbstractStatement moduleOrSubmodule = yangFile.getYangModelRoot().getModuleOrSubmodule();
			for(final AbstractStatement topLevelNode : moduleOrSubmodule.getChildStatements()) {
				if(predicate.test(topLevelNode)) {
					result.add(topLevelNode);
				}
			}
		}

		return result;
	}

	/**
	 * Extracts from the given schema all statements, sitting anywhere in the schema tree, satisfying the predicate.
	 */
	public static List<AbstractStatement> getStatementsInSchema(final Schema schema, final Predicate<AbstractStatement> predicate) {

		final List<AbstractStatement> result = new ArrayList<>();

		for(final YangModel yangModel : schema.getModuleRegistry().getAllYangModels()) {
			final AbstractStatement moduleOrSubmodule = yangModel.getYangModelRoot().getModuleOrSubmodule();
			findStatementsInSubtree(moduleOrSubmodule.getChildStatements(), predicate, result);
		}

		return result;
	}

	private static void findStatementsInSubtree(final List<AbstractStatement> statements, final Predicate<AbstractStatement> predicate, final List<AbstractStatement> result) {

		for(final AbstractStatement stmt : statements) {

			if(predicate.test(stmt)) {
				result.add(stmt);
			}

			final List<AbstractStatement> children = stmt.getChildStatements();
			if(!children.isEmpty()) {
				findStatementsInSubtree(children, predicate, result);
			}
		}
	}

	/**
	 * Finds a schema node given a number of path elements. Each path element is the name of a schema node,
	 * possibly prefixed with a namespace. Returns null if the schema node was not found.
	 */
	public static AbstractStatement findTargetSchemaNode(final List<String> schemaPathElements, final List<AbstractStatement> candidates) {

		/*
		 * We take (and remove) the first element from the list and try to find it amongst the candidates.
		 */
		final String pathElement = schemaPathElements.remove(0);
		final int lastIndexOf = pathElement.lastIndexOf(":");

		final String soughtName = lastIndexOf < 0 ? pathElement : pathElement.substring(lastIndexOf + 1);
		final String soughtNamespace = lastIndexOf < 0 ? null : pathElement.substring(0, lastIndexOf);

		final AbstractStatement match = candidates.stream()
				.filter(AbstractStatement::definesSchemaNode)
				.filter(stmt -> stmt.getStatementIdentifier().equals(soughtName))
				.filter(stmt -> soughtNamespace == null || PreProcessor.getOriginalNamespace(stmt).equals(soughtNamespace))
				.findAny()
				.orElse(null);

		if(match == null || schemaPathElements.isEmpty()) {
			return match;
		}

		/*
		 * Work recursively down the path.
		 */
		return findTargetSchemaNode(schemaPathElements, match.getChildStatements());
	}

	/**
	 * Returns whether the supplied statement is part of the data tree, i.e. does not sit in an action or similar.
	 */
	public static boolean withinDataTree(final AbstractStatement toCheck) {

		AbstractStatement stmt = toCheck;
		while(true) {

			if(stmt.is(CY.STMT_MODULE)) {
				return true;
			}

			if(!stmt.definesDataNode() && !stmt.is(CY.STMT_CASE) && !stmt.is(CY.STMT_CHOICE)) {
				return false;
			}

			stmt = stmt.getParentStatement();
		}
	}
}
