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

import java.util.List;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.YangModelRoot;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YInput;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YModule;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YOutput;
import com.ericsson.oss.mediation.modeling.yangtools.parser.util.QNameHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;

public class LeafRefHelper {

	/**
	 * Given a data node to start navigation from, follows a path through the data model tree (not schema tree).
	 * <p>
	 * Returns null if the path could not be followed.
	 */
	public static AbstractStatement navigateToTarget(final AbstractStatement navigationStart, final String[] path, final Schema schema) {

		AbstractStatement navigationNode = navigationStart;
		for(final String pathPart : path) {

			if(navigationNode == null) {
				return null;
			}

			if(pathPart.equals("..")) {
				navigationNode = findNextDataNodeUpwards(navigationNode);
			} else if (pathPart.equals(".")) {
				// do nothing.
			} else {
				navigationNode = findNextDataNodeDownwards(navigationNode, pathPart, schema);
			}
		}

		return navigationNode;
	}

	private static AbstractStatement findNextDataNodeDownwards(final AbstractStatement navigationNode, final String possiblyPrefixedDataNodeName, final Schema schema) {

		final List<AbstractStatement> candidateChildren = getCandidateChildren(navigationNode, schema);

		/*
		 * Look for the data node by name. This should in 99.9% of cases yield exactly one hit. Note that we
		 * have to use the original statement names of course, as this is the one that would be used inside
		 * the 'path' statement.
		 */
		final String dataNodeName = QNameHelper.extractName(possiblyPrefixedDataNodeName);
		return candidateChildren.stream()
				.filter(dataNode -> PreProcessor.getOriginalStatementIdentifier(dataNode).equals(dataNodeName))
				.findAny()
				.orElse(null);
	}

	/**
	 * If we are at schema root (represented by the YangModelRoot) we return all top-level data nodes - otherwise
	 * the direct children only.
	 */
	private static List<AbstractStatement> getCandidateChildren(final AbstractStatement parent, final Schema schema) {

		if(parent instanceof YangModelRoot) {
			return ExtractionHelper.getStatementsAtSchemaRoot(schema, ExtractionHelper::isContainerOrListOrLeafOrLeafList);
		}

		return ExtractionHelper.getChildDataNodes(parent, ExtractionHelper::isContainerOrListOrLeafOrLeafList);
	}

	/**
	 * Go up in the tree until a statement is found that defines a data node.
	 */
	private static AbstractStatement findNextDataNodeUpwards(final AbstractStatement statement) {

		/*
		 * Already as module level. Can't go up any further.
		 */
		if(statement instanceof YModule || statement instanceof YangModelRoot) {
			return null;
		}

		AbstractStatement runStatement = statement.getParentStatement();

		while(true) {

			if(runStatement.definesDataNode() || runStatement instanceof YInput || runStatement instanceof YOutput) {
				/*
				 * Obviously a data node is a boundary, but also some other statements are considered a boundary.
				 */
				return runStatement;

			} else if(runStatement instanceof YModule) {
				/*
				 * We have reached top of a module, can't go up any more.
				 */
				return statement.getYangModelRoot();
			}

			runStatement = runStatement.getParentStatement();
		}
	}
}
