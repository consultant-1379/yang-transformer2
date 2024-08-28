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

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAction;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeActionParameter;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.NetworkManagementDomainType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YAction;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;

public class PrimaryTypeActionGenerator {

	public static void handleMocActions(final TransformerContext context, final AbstractStatement mocInDeviated, final PrimaryTypeExtensionDefinition ptExtDef) {

		/*
		 * Actions are handled differently to attributes. It is not possible via an extension to replace, or
		 * extend, an action. This means for the action to be guaranteed to be correct, always the DEVIATED
		 * variant of the action needs to be processed. The action will therefore always be generated into
		 * the DPS PT EXT model.
		 */
		final List<YAction> actions = mocInDeviated.getChildren(CY.STMT_ACTION);

		for(final YAction action : actions) {
			final PrimaryTypeAction generatedAction = generateAction(context, action);
			addActionThroughExtension(context, ptExtDef, generatedAction);
		}

		ensureStableDpsPrimaryTypeExtensionActionGeneration(ptExtDef);
	}

	/**
	 * For all the RPCs in the schema, generates actions and places these onto the containment parent of
	 * all top-level data nodes.
	 */
	public static void handleRpcs(final TransformerContext context, final List<AbstractStatement> rpcs, final PrimaryTypeExtensionDefinition extensionForTopLevelContainmentParent) {

		rpcs.forEach(rpc -> {
			final PrimaryTypeAction generatedAction = generateAction(context, rpc);
			EModelHelper.attachMeta(generatedAction.getMeta(), Constants.META_RPC);

			addActionThroughExtension(context, extensionForTopLevelContainmentParent, generatedAction);
		});

		ensureStableDpsPrimaryTypeExtensionActionGeneration(extensionForTopLevelContainmentParent);
	}

	private static PrimaryTypeAction generateAction(final TransformerContext context, final AbstractStatement actionOrRpc) {

		final PrimaryTypeAction pta = new PrimaryTypeAction();

		/*
		 * The basics
		 */
		pta.setNs(PreProcessor.getNamespaceCleanedOfNodeAppInstanceName(actionOrRpc));
		pta.setName(PreProcessor.getRenamedStatementIdentifier(actionOrRpc));

		pta.setDesc(PropertyUtils.getDescription(actionOrRpc, PreProcessor.getOriginalStatementIdentifier(actionOrRpc)));
		pta.setLifeCycle(EModelHelper.getLifeCycleType(actionOrRpc));
		pta.setLifeCycleDesc(PropertyUtils.getStatusInformation(actionOrRpc));
		pta.setDefinedBy(NetworkManagementDomainType.NE);

		/*
		 * Some other properties
		 */
		pta.setDependencies(PropertyUtils.getDependencies(actionOrRpc));
		pta.setPreCondition(PropertyUtils.getPrecondition(actionOrRpc));
		pta.setSideEffects(PropertyUtils.getSideEffects(actionOrRpc));
		pta.setTakesEffect(PropertyUtils.getTakesEffect(actionOrRpc));
		pta.setDisturbances(PropertyUtils.getDisturbances(actionOrRpc));

		/*
		 * The input parameters will be translated to Action Parameters. That's straightforward.
		 */
		final List<AbstractStatement> childrenOfInput = ExtractionHelper.getChildAttributesOfInputOutput(actionOrRpc.getChild(CY.STMT_INPUT));
		for(final AbstractStatement childOfInput : childrenOfInput) {
			generateParameterIntoAction(context, pta, childOfInput);
		}

		final List<AbstractStatement> childrenOfOutput = ExtractionHelper.getChildAttributesOfInputOutput(actionOrRpc.getChild(CY.STMT_OUTPUT));
		if(childrenOfOutput.size() == 1) {
			/*
			 * Easy - single data node is returned (of course, it could be a container / list in which case
			 * it is a CDT, but still, just a single object).
			 */
			generateReturnTypeIntoAction(context, pta, childrenOfOutput.get(0));

		} else if (childrenOfOutput.size() > 1) {
			/*
			 * More than one output parameter. We generate an artificial struct to hold on to these.
			 */
			pta.setReturnType(ComplexTypeGenerator.generatePlaceholderComplexTypeForActionOutput(context, actionOrRpc, childrenOfOutput));
			EModelHelper.attachMeta(pta.getMeta(), Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED);
		}

		EModelHelper.attachMetaForOriginalName(pta.getMeta(), pta.getName(), actionOrRpc);

		return pta;
	}

	private static void generateParameterIntoAction(final TransformerContext context, final PrimaryTypeAction pta, final AbstractStatement dataNode) {

		final PrimaryTypeActionParameter param = new PrimaryTypeActionParameter();
		pta.getParameter().add(param);

		EModelHelper.setEModelNamedEntityDefinitionProperties(param, dataNode);
		EModelHelper.setEModelAttributeDefinitionProperties(context, param, dataNode);

		// TODO: [ACTION_PARAM_NAMESPACE] Set parameter namespace here.

		/*
		 * There are certain language constructs in YANG that may result in a data node not actually existing
		 * at runtime. If such a data node does not exist, we cannot mark it as mandatory, as otherwise we would
		 * force the user to supply a value for a parameter that does not exist, and hence the operation (action
		 * or RPC) would always fail.
		 */
		if(parameterMayNotExistInSchemaAtRuntime(context, dataNode)) {
			param.setMandatory(false);
			param.getType().setNotNullConstraint(null);
		}

		EModelHelper.attachMetaForOriginalName(param.getMeta(), param.getName(), dataNode);
	}

	private static boolean parameterMayNotExistInSchemaAtRuntime(final TransformerContext context, final AbstractStatement dataNode) {

		/*
		 * The data node sits inside a case. Hence, the data node will not exist if the case-branch
		 * is not active. Note that for actions, we don't capture choice/case information.
		 */
		if(dataNode.getParentStatement().is(CY.STMT_CASE)) {
			return true;
		}

		/*
		 * If the 'when' clause is not fulfilled, the data node will not exist. Note that ENM does not support
		 * 'when' statement, so at runtime we don't know whether the data node exists.
		 */
		if(dataNode.hasAtLeastOneChildOf(CY.STMT_WHEN)) {
			return true;
		}

		/*
		 * Statement has an if-feature, and context says that all statements with if-feature shall be retained.
		 * This basically means that we don't really know whether at runtime the feature is enabled or not - so
		 * the data node may not actually exist on the node at runtime. (If the feature handling was CONFIGURED
		 * or REMOVE_ALL then the data node should be considered to always exists.)
		 */
		if(dataNode.hasAtLeastOneChildOf(CY.STMT_IF_FEATURE) && context.getFeatureHandling() == FeatureHandling.RETAIN_ALL) {
			return true;
		}

		return false;
	}

	private static void generateReturnTypeIntoAction(final TransformerContext context, final PrimaryTypeAction pta, final AbstractStatement dataNode) {
		final AbstractDataType returnType = DataTypeGenerator.generateDataType(context, dataNode);
		pta.setReturnType(returnType);

		/*
		 * In the emodel, the return type is not modeled as an attribute, but only the type is set.
		 * If the returned value is a non-unique sequence, we need to capture this information
		 * somewhere - the only place to capture it is as part of the action itself.
		 */
		if(PreProcessor.is3gppNonUniqueSequence(dataNode)) {
			EModelHelper.attachMeta(pta.getMeta(), Constants.META_3GPP_ACTION_RETURN_TYPE_IS_NON_UNIQUE_SEQUENCE);
		}
	}

	public static void addActionThroughExtension(final TransformerContext context, final PrimaryTypeExtensionDefinition pted, final PrimaryTypeAction action) {
		pted.getPrimaryTypeExtension().getPrimaryTypeAction().add(action);
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 */
	private static void ensureStableDpsPrimaryTypeExtensionActionGeneration(final PrimaryTypeExtensionDefinition ptExtDef) {
		/*
		 * Sort added actions and various things inside of these. 
		 */
		Collections.sort(ptExtDef.getPrimaryTypeExtension().getPrimaryTypeAction(), (o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));

		ptExtDef.getPrimaryTypeExtension().getPrimaryTypeAction().forEach(ptAction -> {

			EModelHelper.ensureStableMetaGeneration(ptAction.getMeta());

			Collections.sort(ptAction.getParameter(), (o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
			ptAction.getParameter().forEach(param -> EModelHelper.ensureStableMetaGeneration(param.getMeta()));
		});
	}
}
