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

import java.util.Objects;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.ChoiceHandlingType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionType;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelDefinition;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.Target;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;

public class PrimaryTypeExtensionGenerator {

	/**
	 * Generates an extension model for a given DPS PrimaryType. This extension model will eventually contain in
	 * effect the delta, <b>if any</b>, of a DPS PrimaryType ORIGINAL variant to its DEVIATED variant.
	 * <p>
	 * Where the DPS PrimaryType is not augmented/deviated, then this extension model will remain in effect empty
	 * (and will not be written out as it will be removed beforehand).
	 */
	public static PrimaryTypeExtensionDefinition createDpsPrimaryTypeExtension(final TransformerContext context, final PrimaryTypeDefinition extendedPrimaryType, final AbstractStatement listOrContainerInDeviated) {

		final ModelInfo ptedModelInfo = getModelInfoForPrimaryTypeExtensionDefinition(context.getTarget(), extendedPrimaryType.getNs(), extendedPrimaryType.getName());
		final String primaryTypeImpliedUrn = EModelHelper.getImpliedVersionedUrn(extendedPrimaryType.getNs(), extendedPrimaryType.getName(), extendedPrimaryType.getVersion());
		final String desc = "Extension for " + EModelHelper.getHumanReadableUrn(ModelInfo.fromImpliedUrn(primaryTypeImpliedUrn, SchemaConstants.DPS_PRIMARYTYPE)) + " to handle augmentations/deviations";

		final PrimaryTypeExtensionDefinition ptExtDef = new PrimaryTypeExtensionDefinition();

		EModelHelper.populateEModelExtensionHeader(context, ptExtDef, ptedModelInfo, primaryTypeImpliedUrn, desc);

		/*
		 * The extension is target-version specific.
		 */
		ptExtDef.setRequiresTargetType(EModelHelper.getRequiresTargetType(context));

		/*
		 * Pre-populate to make subsequent code logic a bit easier.
		 */
		ptExtDef.setPrimaryTypeExtension(new PrimaryTypeExtensionType());
		ptExtDef.setChoiceHandling(new ChoiceHandlingType());

		/*
		 * Any diff to the original MOC must be populated into the extension.
		 */
		populateExtensionWithMocDiff(context, extendedPrimaryType, ptExtDef, listOrContainerInDeviated);

		return ptExtDef;
	}

	/**
	 * Generates, or returns an already existing, extension model for the MOC that is the containment parent of all top-level data nodes.
	 */
	public static PrimaryTypeExtensionDefinition getOrCreateDpsPrimaryTypeExtensionForContainmentParent(final TransformerContext context) {

		final PrimaryTypeExtensionDefinition alreadyGenerated = getDpsPrimaryTypeExtensionForContainmentParent(context);
		if(alreadyGenerated != null) {
			return alreadyGenerated;
		}

		final PrimaryTypeExtensionDefinition ptExtDef = new PrimaryTypeExtensionDefinition();
		context.getCreatedEmodels().addGeneratedEModel(ptExtDef);

		final ModelInfo containmentParent = context.getEffectiveContainmentParent();
		final ModelInfo ptedModelInfo = getModelInfoForPrimaryTypeExtensionForContainmentParent(context);

		final String desc = "Extension for " + EModelHelper.getHumanReadableUrn(containmentParent) + " to handle RPCs and top-level attributes.";
		EModelHelper.populateEModelExtensionHeader(context, ptExtDef, ptedModelInfo, containmentParent.toImpliedUrn(), desc);

		ptExtDef.setRequiresTargetType(EModelHelper.getRequiresTargetType(context));

		/*
		 * Pre-populate to make subsequent code logic a bit easier.
		 */
		ptExtDef.setPrimaryTypeExtension(new PrimaryTypeExtensionType());
		ptExtDef.setChoiceHandling(new ChoiceHandlingType());

		return ptExtDef;
	}

	/**
	 * Generates, or returns an already existing, extension model for OSS_TOP::MeContext. The extension may
	 * have previously been created as it may serve as containment parent for all top-level data nodes.
	 */
	public static PrimaryTypeExtensionDefinition getOrCreateDpsPrimaryTypeExtensionForMeContext(final TransformerContext context) {

		final PrimaryTypeExtensionDefinition alreadyGenerated = getDpsPrimaryTypeExtensionForMeContext(context);
		if(alreadyGenerated != null) {
			return alreadyGenerated;
		}

		final PrimaryTypeExtensionDefinition meContextExtDef = new PrimaryTypeExtensionDefinition();
		context.getCreatedEmodels().addGeneratedEModel(meContextExtDef);

		final ModelInfo meContextModelInfo = Constants.OSS_TOP_MECONTEXT_STAR;
		final ModelInfo meContextExtModelInfo = getModelInfoForPrimaryTypeExtensionForMeContext(context);

		final String desc = "Extension for " + EModelHelper.getHumanReadableUrn(meContextModelInfo);
		EModelHelper.populateEModelExtensionHeader(context, meContextExtDef, meContextExtModelInfo, meContextModelInfo.toImpliedUrn(), desc);

		meContextExtDef.setRequiresTargetType(EModelHelper.getRequiresTargetType(context));
		meContextExtDef.setPrimaryTypeExtension(new PrimaryTypeExtensionType());
		meContextExtDef.setChoiceHandling(new ChoiceHandlingType());

		return meContextExtDef;
	}

	/**
	 * Returns the extension model for the MOC that is the containment parent of all top-level data nodes.
	 * Will return null if it does not already exist.
	 */
	public static PrimaryTypeExtensionDefinition getDpsPrimaryTypeExtensionForContainmentParent(final TransformerContext context) {

		final ModelInfo ptedModelInfo = getModelInfoForPrimaryTypeExtensionForContainmentParent(context);
		return context.getCreatedEmodels().getGeneratedEModel(ptedModelInfo);
	}

	private static ModelInfo getModelInfoForPrimaryTypeExtensionForContainmentParent(final TransformerContext context) {

		final ModelInfo containmentParent = context.getEffectiveContainmentParent();
		return getModelInfoForPrimaryTypeExtensionDefinition(context.getTarget(), containmentParent.getNamespace(), containmentParent.getName());
	}

	private static PrimaryTypeExtensionDefinition getDpsPrimaryTypeExtensionForMeContext(final TransformerContext context) {

		final ModelInfo meContextModelInfo = getModelInfoForPrimaryTypeExtensionForMeContext(context);
		return context.getCreatedEmodels().getGeneratedEModel(meContextModelInfo);
	}

	private static ModelInfo getModelInfoForPrimaryTypeExtensionForMeContext(final TransformerContext context) {

		final ModelInfo meContext = Constants.OSS_TOP_MECONTEXT_STAR;
		return getModelInfoForPrimaryTypeExtensionDefinition(context.getTarget(), meContext.getNamespace(), meContext.getName());
	}

	/**
	 * Returns the ModelInfo for a DPS PRIMARYTYPE EXTENSION. Extensions are target version-specific, and
	 * that will be used for the namespace of the model. The name of the extension model is made unique by
	 * concatenating the namespace + name of the DPS PT model which is extended. As it is an extension model,
	 * it's version will always be 1.0.0. Example for generated URN:
	 * <p>
	 * /dps_primarytype_ext/NODE__EPG-OI__3.12/urn:3gpp:sa5:_3gpp-nr-nrm-gnbdufunction__GNBDUFunction__ext/1.0.0
	 */
	public static ModelInfo getModelInfoForPrimaryTypeExtensionDefinition(final Target target, final String namespaceOfExtendedDpsPt, final String nameOfExtendedDpsPt) {

		final String namespace = EModelHelper.getExtensionModelNamespace(target);
		final String name = namespaceOfExtendedDpsPt + "__" + nameOfExtendedDpsPt + "__ext";
		final String version = EModelHelper.getModelInfoVersionForExtension();

		return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE_EXT, namespace, name, version);
	}

	/**
	 * For the DEVIATED variant, extracts the properties of the MOC and compares these against the properties
	 * of the MOC in the ORIGINAL (which will be from the BASE variant or the AUGMENTED variant). Whatever differs
	 * will be generated into the extension (as far as this is possible given the DPS PT EXT schema).
	 * <p>
	 * Only handles the properties of the MOC itself. Does not look at the attributes or actions.
	 */
	private static void populateExtensionWithMocDiff(final TransformerContext context, final PrimaryTypeDefinition ptDef, final PrimaryTypeExtensionDefinition ptExtDef, final AbstractStatement mocInDeviated) {

		final String descriptionInDeviated = PropertyUtils.getDescription(mocInDeviated, PreProcessor.getOriginalStatementIdentifier(mocInDeviated));
		final String descriptionInOrig = ptDef.getDesc();
		if(!Objects.equals(descriptionInDeviated, descriptionInOrig)) {
			ptExtDef.getPrimaryTypeExtension().setDesc(descriptionInDeviated);
		}

		final String dependenciesInDeviated = PropertyUtils.getDependencies(mocInDeviated);
		final String dependenciesInOrig = ptDef.getDependencies();
		if(!Objects.equals(dependenciesInDeviated, dependenciesInOrig)) {
			ptExtDef.getPrimaryTypeExtension().setDependencies(dependenciesInDeviated);
		}

		final String preconditionInDeviated = PropertyUtils.getPrecondition(mocInDeviated);
		final String preconditionInOrig = ptDef.getPreCondition();
		if(!Objects.equals(preconditionInDeviated, preconditionInOrig)) {
			ptExtDef.getPrimaryTypeExtension().setPreCondition(preconditionInDeviated);
		}

		/*
		 * The system-created property can be modified by means of a 'deviate add / delete'. Has been seen in
		 * an EPG NRM for ericsson-swim and ericsson-lm.
		 */
		final boolean systemCreatedInDeviated = PrimaryTypeGenerator.isMocSystemCreated(context, mocInDeviated);
		final boolean systemCreatedInOrig = ptDef.getSystemCreated() != null;
		if(!Objects.equals(systemCreatedInDeviated, systemCreatedInOrig)) {
			ptExtDef.getPrimaryTypeExtension().setSystemCreated(systemCreatedInDeviated);
			context.logInfo(Constants.TEXT_REPLACED + " Replaced property 'system-created' for MOC " + ExtractionHelper.getHumanReadablePathToSchemaNode(mocInDeviated) + " with new value '" + systemCreatedInDeviated + "' through extension.");
		}

		/*
		 * Handle write behaviour. Eminently possible that a 'deviate replace' modifies the config of a data node
		 * from true to false.
		 */
		final WriteBehaviorType writeBehaviorTypeInDeviated = PrimaryTypeGenerator.getWriteBehaviourForMoc(mocInDeviated);
		final WriteBehaviorType WriteBehaviorTypeInOrig = ptDef.getWriteBehavior();
		if(!Objects.equals(writeBehaviorTypeInDeviated, WriteBehaviorTypeInOrig)) {
			ptExtDef.getPrimaryTypeExtension().setWriteBehavior(writeBehaviorTypeInDeviated);
			context.logInfo(Constants.TEXT_REPLACED + " Replaced property 'write-behavior' for MOC " + ExtractionHelper.getHumanReadablePathToSchemaNode(mocInDeviated) + " with new value '" + writeBehaviorTypeInDeviated + "' through extension.");
		}

		/*
		 * No check is done on the "requires". If we have come this far it means that the MOC exists both
		 * in ORIGINAL and DEVIATED. The "requires" will always be the same for both of these:
		 * 
		 * 1.) It is not possible in YANG to move a data node in-to or out-of a 'case'. That means the same MOC
		 *     will always require the exact same case/choice.
		 * 2.) It is not possible to augment or deviate a 'when' statement, hence the 'when' is always the same
		 *     on a given MOC, irrespective of variant.
		 */
	}

	/**
	 * Returns whether the supplied model is an empty DPS PT EXT model - that is, it has in effect no content.
	 */
	public static boolean isEmptyPrimaryTypeExtensionModel(final EModelDefinition emodel) {

		if(!(emodel instanceof PrimaryTypeExtensionDefinition)) {
			return false;
		}

		final PrimaryTypeExtensionDefinition ptExtDef = (PrimaryTypeExtensionDefinition) emodel;

		if(!ptExtDef.getMeta().isEmpty()) {
			return false;
		}

		final ChoiceHandlingType choiceHandling = ptExtDef.getChoiceHandling();

		if(!choiceHandling.getRemoveChoice().isEmpty()) {
			return false;
		}
		if(!choiceHandling.getReplaceChoice().isEmpty()) {
			return false;
		}

		if(!ptExtDef.getPrimaryTypeAttributeRemoval().isEmpty()) {
			return false;
		}

		if(ptExtDef.getPrimaryTypeAttributeReplacement() != null) {
			return false;
		}

		final PrimaryTypeExtensionType primaryTypeExtension = ptExtDef.getPrimaryTypeExtension();

		if(primaryTypeExtension.getDesc() != null) {
			return false;
		}
		if(primaryTypeExtension.getDependencies() != null) {
			return false;
		}
		if(primaryTypeExtension.getPreCondition() != null) {
			return false;
		}
		if(primaryTypeExtension.isSystemCreated() != null) {
			return false;
		}
		if(primaryTypeExtension.getWriteBehavior() != null) {
			return false;
		}

		if(!primaryTypeExtension.getPrimaryTypeAttribute().isEmpty()) {
			return false;
		}
		if(!primaryTypeExtension.getPrimaryTypeAction().isEmpty()) {
			return false;
		}

		return true;
	}
}
