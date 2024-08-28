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

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;

public class AppInstanceNameSupport {

	/**
	 * Returns whether the supplied statement represents a container whose name has been modified at runtime
	 * of the node with the name of a node application instance. For example, it looks like so:
	 * <p>
	 * eric-ims-bgf---london3---2-2-5-260
	 */
	public static boolean isContainerWithAppInstanceName(final AbstractStatement stmt) {

		if(!stmt.is(CY.STMT_CONTAINER)) {
			return false;
		}

		final String containerName = stmt.getStatementIdentifier();

		/*
		 * Attempt to remove the instance name. If that doesn't work, then the usual "---" syntax was not adhered to.
		 */
		if(containerName.equals(YangNameVersionUtil.removeNodeAppInstanceName(containerName))) {
			return false;
		}

		/*
		 * There should be a leaf under it called "id", which we will use as list key later on. So check
		 * that it exists.
		 */
		final AbstractStatement idLeaf = getIdLeafOfContainerWithAppInstanceName(stmt);
		return idLeaf != null;
	}

	/**
	 * Each container whose name has been renamed to include the app instance name will have an 'id' leaf.
	 * This serves no purpose for the node application itself, but it can be used in ENM as key for the
	 * list that we will generate.
	 */
	public static AbstractStatement getIdLeafOfContainerWithAppInstanceName(final AbstractStatement containerOrList) {

		return ExtractionHelper.getChildDataNodes(containerOrList, ExtractionHelper::isLeaf).stream()
				.filter(leaf -> leaf.getStatementIdentifier().equals("id"))
				.findAny()
				.orElse(null);
	}
}
