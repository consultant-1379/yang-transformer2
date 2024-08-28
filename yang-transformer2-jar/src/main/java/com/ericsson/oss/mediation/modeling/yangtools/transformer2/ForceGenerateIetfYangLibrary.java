/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.mediation.modeling.yangtools.transformer2;

import java.util.List;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EriCustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YContainer;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YList;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YStatus;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.IetfYangLibraryParser;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;

/**
 * This class serves as short-term workaround (that likely will never get removed again...) to make sure that
 * the IETF Yang Library is generated into EModel such that it is synced up into ENM. While ENM itself doesn't
 * care about the YL being mirrored, it is EIAP (that sits NB of ENM) which requires this information.
 */
public class ForceGenerateIetfYangLibrary {

	public static void tweakVariants(final TransformerContext context) {
		tweakVariant(context, context.getBaseSchema());
		tweakVariant(context, context.getAugmentedSchema());
		tweakVariant(context, context.getDeviatedSchema());
	}

	private static void tweakVariant(final TransformerContext context, final Schema schema) {

		/*
		 * This isn't quite so straightforward. There are two revisions of the YL:
		 *
		 * 2016-04-09 - this is the original revision that introduced the /modules-state branch. In this revision,
		 * the whole tree is simply read-only (config false).
		 * 
		 * 2019-01-04 - this introduced (for NMDA) a new branch /yang-library, and the existing branch /modules-state
		 * has been deprecated.
		 *
		 * EIAP only needs the /modules-state branch, so we can ignore the /yang-library branch.
		 *
		 * We need to make sure that the /modules-state branch is made effectively "persistent". That means:
		 * - we need to inject "notifiable true" for the branch.
		 * - override the "status deprecated" for the /modules-state branch. (Because if we don't do that, the
		 *   attributes will be non-persistent and it will take forever to read it from the node, slowing down EIAP
		 *   big time).
		 *
		 * We also want to make sure that we don't pull up too much data - there are a few data nodes that EIAP doesn't
		 * care about, so we would not want to persist these.
		 */
		final YContainer modulesState = findModulesStateBranch(schema);
		if(modulesState == null) {
			return;
		}

		makeTreeCurrent(modulesState);
		makeModulesStateBranchNotifiable(modulesState);
	}

	private static YContainer findModulesStateBranch(final Schema schema) {

		final YangModel ietfYangLibraryInput = schema.getModuleRegistry().getAllYangModels().stream()
				.filter(yangModel -> yangModel.getModuleIdentity().getModuleName().equals(IetfYangLibraryParser.IETF_YANG_LIBRARY_MODULE_NAME))
				.findFirst().orElse(null);

		if(ietfYangLibraryInput == null) {
			return null;
		}

		final List<AbstractStatement> containers = ExtractionHelper.getChildDataNodes(ietfYangLibraryInput.getYangModelRoot().getModule(), ExtractionHelper::isContainer);

		return (YContainer) containers.stream()
				.filter(stmt -> stmt.getStatementIdentifier().equals(IetfYangLibraryParser.YANG_LIBRARY_MODULES_STATE))
				.findFirst().orElse(null);
	}

	private static void makeTreeCurrent(final AbstractStatement stmt) {
		/*
		 * The transformer code later on will look at the "effective Ericsson status" (and not the 'status' statement).
		 * We flip it to CURRENT so that the attributes don't become non-persistent just because they are DEPRECATED.
		 */
		EriCustomProcessor.setEffectiveEricssonStatus(stmt, YStatus.CURRENT);

		final List<AbstractStatement> childDataNodes = ExtractionHelper.getChildDataNodes(stmt);
		childDataNodes.forEach(ForceGenerateIetfYangLibrary::makeTreeCurrent);
	}

	private static void makeModulesStateBranchNotifiable(final YContainer modulesState) {
		/*
		 * We don't make everything persistent - only what EIAP actually needs. We skip the 'deviation' list - no
		 * point creating a MOC for that, nobody will ever need that.
		 * 
		 * Start off with the /modules-state branch itself and the /modules-state/module-set-id
		 */
		makeNotifiable(modulesState);
		makeLeafOrLeafListNotifiable(modulesState, "module-set-id");

		/*
		 * Go into the 'module' sub-branch. Don't need 'schema'.
		 */
		final YList module = (YList) findChild(ExtractionHelper.getChildDataNodes(modulesState, ExtractionHelper::isList), "module");
		makeNotifiable(module);

		makeLeafOrLeafListNotifiable(module, "conformance-type");
		makeLeafOrLeafListNotifiable(module, "feature");

		makeLeafOrLeafListNotifiable(module, "name");
		makeLeafOrLeafListNotifiable(module, "namespace");
		makeLeafOrLeafListNotifiable(module, "revision");

		makeLeafOrLeafListNotifiable(module, "version");		// Ericsson extensions, will not exist on 3P node
		makeLeafOrLeafListNotifiable(module, "release");		// Ericsson extensions, will not exist on 3P node
		makeLeafOrLeafListNotifiable(module, "correction");		// Ericsson extensions, will not exist on 3P node

		/*
		 * Go into the 'submodule' sub-branch. Don't need 'schema'.
		 */
		final YList submodule = (YList) findChild(ExtractionHelper.getChildDataNodes(module, ExtractionHelper::isList), "submodule");
		makeNotifiable(submodule);

		makeLeafOrLeafListNotifiable(submodule, "name");
		makeLeafOrLeafListNotifiable(submodule, "revision");

		makeLeafOrLeafListNotifiable(submodule, "version");		// Ericsson extensions
		makeLeafOrLeafListNotifiable(submodule, "release");		// Ericsson extensions
		makeLeafOrLeafListNotifiable(submodule, "correction");	// Ericsson extensions
	}

	/**
	 * Make notifiable a leaf or leaf-list under a container/list.
	 */
	private static void makeLeafOrLeafListNotifiable(final AbstractStatement parent, final String leafOrLeafListName) {
		makeNotifiable(findChild(ExtractionHelper.getChildDataNodes(parent, ExtractionHelper::isLeafOrLeafList), leafOrLeafListName));
	}

	private static void makeNotifiable(final AbstractStatement stmt) {
		if(stmt != null) {
			/*
			 * We don't use 'notifiable-state-data true' here. We may encounter a 3P node that does not use NSD, and
			 * the pre-processor behaviour may be such to fully ignore the EOI 'notifiable-state-data' when it comes
			 * to figuring out whether a date node is notified or not.
			 */
			PreProcessor.setNotified(stmt);
		}
	}

	private static AbstractStatement findChild(final List<AbstractStatement> candidates, final String name) {
		return candidates.stream()
				.filter(child -> child.getStatementIdentifier().equals(name))
				.findFirst()
				.orElse(null);
	}
}
