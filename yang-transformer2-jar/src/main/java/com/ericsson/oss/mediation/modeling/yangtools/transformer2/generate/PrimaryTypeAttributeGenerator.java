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
import java.util.Locale;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.ReadBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeAttributeRemovalType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeAttributeReplacementType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ComplexRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.NetworkManagementDomainType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.NotNullConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringValue;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EriCustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YStatus;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;

public class PrimaryTypeAttributeGenerator {

	public static void generateArtificialKeyAttribute(final AbstractStatement dataNodeInDeviated, final PrimaryTypeDefinition ptd) {

		final PrimaryTypeAttribute attr = new PrimaryTypeAttribute();

		/*
		 * Basics - note that in 3GPP it is customary to use "id" as key leaf name. Otherwise we use the
		 * name of the MOC and append -key to it.
		 */
		attr.setName(PreProcessor.is3gppMoc(dataNodeInDeviated) ? "id" : PreProcessor.getRenamedStatementIdentifier(dataNodeInDeviated) + "-key");
		attr.setDesc("Key attribute");

		attr.setKey(true);
		attr.setMandatory(true);
		attr.setImmutable(true);
		attr.setSensitive(false);

		attr.setWriteBehavior(ptd.getWriteBehavior() == WriteBehaviorType.NOT_ALLOWED ? WriteBehaviorType.NOT_ALLOWED : WriteBehaviorType.PERSIST_AND_DELEGATE);
		attr.setReadBehavior(ReadBehaviorType.FROM_PERSISTENCE);
		attr.setDefinedBy(NetworkManagementDomainType.OSS);

		/*
		 * Data type and default
		 */
		final StringType stringType = new StringType();
		stringType.setNotNullConstraint(new NotNullConstraint());
		attr.setType(stringType);

		final StringValue defaultValue = new StringValue();
		defaultValue.setValue("1");
		attr.setDefault(defaultValue);

		/*
		 * Some meta to help mediation
		 */
		EModelHelper.attachMeta(attr.getMeta(), Constants.META_ARTIFIAL_KEY);

		ptd.getPrimaryTypeAttribute().add(attr);
	}

	public static void handleMocAttributes(final TransformerContext context, final AbstractStatement mocInDeviated, final PrimaryTypeDefinition ptd, final PrimaryTypeExtensionDefinition pted) {

		/*
		 * This is a bit more complicated. In terms of attributes, the following scenarios can occur:
		 * 
		 * - The attribute has been originally defined within the exact same YAM as the MOC.
		 * - The attribute has been defined within a YAM different to the YAM where the MOC was defined.
		 *   In this case, the attribute was pulled in through a grouping, or was augmented-in.
		 * - The same two scenarios again as above, but the attribute has been deviated out.
		 * - The same two scenarios again as above, but the properties of the attribute have been modified
		 *   by virtue of an augmentation or deviation.
		 * 
		 * So there are 6 possible combinations we need to cater for:
		 * 
		 * 1) Attribute declared in same YAM as the MOC; it exists in DEVIATED; and properties are the same.
		 * 2) Attribute declared in same YAM as the MOC; it exists in DEVIATED; properties are different.
		 * 3) Attribute declared in different YAM from the MOC; it exists in DEVIATED; and properties are the same.
		 * 4) Attribute declared in different YAM from the MOC; it exists in DEVIATED; properties are different.
		 * 5) Attribute declared in same YAM as the MOC; it does not exist in DEVIATED.
		 * 6) Attribute declared in different YAM from the MOC; it does not exist in DEVIATED.
		 *
		 * In terms of handling, this is what happens:
		 *
		 * 1) Since the attribute comes from the same YAM as the MOC, it must be generated into the MOC.
		 *    There is no need to modify it in any way by means of an extension.
		 * 2) Since the attribute comes from the same YAM as the MOC, it must be generated into the MOC.
		 *    However, it's properties have changed, so it must also be REPLACED via an extension.
		 * 3) Since the attribute comes from a different YAM, it must be ADDED via an extension.
		 * 4) Since the attribute comes from a different YAM, it must be ADDED via an extension.
		 *    (So same handling as 3)
		 * 5) Since the attribute comes from the same YAM as the MOC, it must be generated into the MOC.
		 *    However, it doesn't exist in this target version, so it must also be REMOVED via an extension.
		 * 6) Since the attribute comes from a different YAM, it will be IGNORED (as it makes no sense ADDING
		 *    it and at the same time REMOVING it via the same extension).
		 *
		 * Note that in a corner case, the key(s) of a list may be part of a grouping as well - very unlikely
		 * to happen, but when it does, then the key *must* be generated into the MOC and not the extension,
		 * otherwise deployment will fail (modeling toolchain will kick up fuss).
		 *
		 * The reason to check for the origin of the YAM is to prevent a situation where a module A 'uses' a
		 * 'grouping' in module B, and in two different NRMs, the 'grouping' in module B has changed and the
		 * grouping contents is different between the versions, but module A's revision has not changed. Unless
		 * the contents of the grouping is generated into the extension, the generated MOC for module A would be
		 * generated with different content! (Similar problem using submodules then).
		 */
		final AbstractStatement mocInAugmented = PreProcessor.getAugmentedVariantMoc(mocInDeviated);

		final List<AbstractStatement> childAttributesInAugmented = ExtractionHelper.getChildAttributesForMoc(mocInAugmented);
		final List<AbstractStatement> childAttributesInDeviated = ExtractionHelper.getChildAttributesForMoc(mocInDeviated);

		final boolean mocDefinedInSubmodule = PropertyUtils.definedInSubmodule(mocInDeviated);

		/*
		 * We start off by handling scenarios 5 & 6 first. In all of these, the attribute does not exist in
		 * DEVIATED since the attribute has been deviated-out. We consider the contents of AUGMENTED, as this
		 * can never have fewer attributes than the base-variant.
		 */
		for(final AbstractStatement dataNodeInAugmented : childAttributesInAugmented) {

			final AbstractStatement dataNodeInDeviated = ExtractionHelper.findSchemaNode(childAttributesInDeviated, dataNodeInAugmented);
			if(dataNodeInDeviated != null) {
				/*
				 * Exists in DEVIATED, i.e. not scenario 5 & 6, so skip over this attribute, will be handled in a moment...
				 */
				continue;
			}

			final boolean mocDefinedInSameYamAsAttributeButNotInSubmodule = !mocDefinedInSubmodule && PropertyUtils.definedInSameYam(mocInAugmented, dataNodeInAugmented);
			final boolean isKeyLeaf = PreProcessor.isListKey(dataNodeInAugmented);
			final boolean isEmptyStructInAugmented = ExtractionHelper.isEmptyStruct(dataNodeInAugmented);

			if (PreProcessor.is3gppNonUniqueSequence(dataNodeInAugmented) && context.suppressWrapGeneration()) {

				context.logWarn(Constants.TEXT_REMOVED_EXPLICIT + " Did not generate attribute '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNodeInAugmented) + "' as it is a '3GPP Wrap' attribute.");

			} else if (!isEmptyStructInAugmented && (isKeyLeaf || mocDefinedInSameYamAsAttributeButNotInSubmodule)) {
				/*
				 * Scenario 5 - declared in same YAM (and not in a submodule), doesn't exist in DEVIATED.
				 * Generate into the MOC, and REMOVE via extension.
				 */
				final PrimaryTypeAttribute origAttribute = generateAttribute(context, dataNodeInAugmented);
				ptd.getPrimaryTypeAttribute().add(origAttribute);

				removeAttributeThroughExtension(pted, origAttribute);
				context.logInfo(Constants.TEXT_REMOVED_DEVIATED_OUT + " Removed attribute '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNodeInAugmented) + "' from MOC via extension as it does not exist in the deviated-variant.");

			} else {
				/*
				 * Scenario 6 - declared in different YAMs (or a submodule), so it usually would get generated
				 * into the extension, but since it doesn't exist in DEVIATED there is nothing we have to do here.
				 */
				context.logInfo(Constants.TEXT_REMOVED_DEVIATED_OUT + " Did not generate augmented-in attribute '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNodeInAugmented) + "' into extension as it does not exist in the deviated-variant.");
			}
		}

		/*
		 * Now we do scenarios 1-3. In those scenarios, the attribute has not been deviated-out. So we iterate
		 * using the attributes from DEVIATED.
		 */
		for(final AbstractStatement dataNodeInDeviated : childAttributesInDeviated) {
			/*
			 * Must be found in AUGMENTED
			 */
			final AbstractStatement dataNodeInAugmented = ExtractionHelper.findSchemaNode(childAttributesInAugmented, dataNodeInDeviated);

			final boolean mocDefinedInSameYamAsAttributeButNotSubmodule = !mocDefinedInSubmodule && PropertyUtils.definedInSameYam(mocInDeviated, dataNodeInDeviated);
			final boolean isKeyLeaf = PreProcessor.isListKey(dataNodeInDeviated);
			final boolean isEmptyStructInAugmented = ExtractionHelper.isEmptyStruct(dataNodeInAugmented);

			if (PreProcessor.is3gppNonUniqueSequence(dataNodeInAugmented) && context.suppressWrapGeneration()) {

				context.logWarn(Constants.TEXT_REMOVED_EXPLICIT + " Did not generate attribute '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNodeInAugmented) + "' as it is a '3GPP Wrap' attribute.");

			} else if (!isEmptyStructInAugmented && (isKeyLeaf || mocDefinedInSameYamAsAttributeButNotSubmodule)) {
				/*
				 * Scenario 1 & 2 - declared in same YAM (and not in a submodule either), exists in DEVIATED,
				 * properties may or may not be the same.
				 * 
				 * Either way, generate first of all into the MOC, and then check if we have to REPLACE it.
				 */
				final PrimaryTypeAttribute origAttribute = generateAttribute(context, dataNodeInAugmented);
				ptd.getPrimaryTypeAttribute().add(origAttribute);

				final PrimaryTypeAttribute deviatedAttribute = generateAttribute(context, dataNodeInDeviated);
				replaceAttrIfNecessary(context, pted, origAttribute, deviatedAttribute, dataNodeInDeviated);

			} else {
				/*
				 * Scenario 3 + 4 - declared in different YAMs (so e.g. was augmented into a foreign MOC) or
				 * submodule, exists in DEVIATED, so ADD via extension. Makes no difference if the properties
				 * between AUGMENTED and DEVIATED are the same.
				 */
				final PrimaryTypeAttribute deviatedAttribute = generateAttribute(context, dataNodeInDeviated);
				addAttributeThroughExtension(pted, deviatedAttribute);
			}
		}

		ensureStableDpsPrimaryTypeAttributeGeneration(ptd);
		ensureStableDpsPrimaryTypeExtensionAttributeGeneration(pted);
	}

	public static void handleTopLevelAttributes(final TransformerContext context, final List<AbstractStatement> topLevelAttributesOfDeviatedSchema, final PrimaryTypeExtensionDefinition extensionForTopLevelContainmentParent) {

		topLevelAttributesOfDeviatedSchema.forEach(dataNodeInDeviated -> {

			final PrimaryTypeAttribute generatedTopLevelAttribute = PrimaryTypeAttributeGenerator.generateAttribute(context, dataNodeInDeviated);
			EModelHelper.attachMeta(generatedTopLevelAttribute.getMeta(), Constants.META_TOP_LEVEL_DATA_NODE);
			addAttributeThroughExtension(extensionForTopLevelContainmentParent, generatedTopLevelAttribute);
		});

		ensureStableDpsPrimaryTypeExtensionAttributeGeneration(extensionForTopLevelContainmentParent);
	}

	/**
	 * Generates the PT attribute.
	 */
	private static PrimaryTypeAttribute generateAttribute(final TransformerContext context, final AbstractStatement dataNode) {

		final PrimaryTypeAttribute ptAttr = new PrimaryTypeAttribute();

		EModelHelper.setEModelNamedEntityDefinitionProperties(ptAttr, dataNode);
		EModelHelper.setEModelAttributeDefinitionProperties(context, ptAttr, dataNode);

		/*
		 * Handle properties for PrimaryTypeAttribute
		 */
		ptAttr.setNs(PreProcessor.getNamespaceCleanedOfNodeAppInstanceName(dataNode));
		EModelHelper.attachMetaForNamespaceCleanedOfNodeAppInstanceName(ptAttr.getMeta(), dataNode);

		ptAttr.setWriteBehavior(getAttributeWriteBehaviour(ptAttr.isKey(), dataNode));
		ptAttr.setReadBehavior(getAttributeReadBehaviour(ptAttr.isKey(), dataNode));

		if(PropertyUtils.isAttributeDenotingInstanceIsSystemCreated(dataNode)) {
			EModelHelper.attachMeta(ptAttr.getMeta(), Constants.META_INSTANCE_SYSTEM_CREATED_INDICATOR);
		}

		ptAttr.setRequires(PrimaryTypeChoiceAndRequiresGenerator.getRequires(dataNode));

		ptAttr.setDependencies(PropertyUtils.getDependencies(dataNode));
		ptAttr.setPreCondition(PropertyUtils.getPrecondition(dataNode));
		ptAttr.setSideEffects(PropertyUtils.getSideEffects(dataNode));
		ptAttr.setTakesEffect(PropertyUtils.getTakesEffect(dataNode));
		ptAttr.setDisturbances(PropertyUtils.getDisturbances(dataNode));

		adjustReadWriteBehaviourForAttribute(ptAttr, dataNode);

		/*
		 * There is special handling for the 'mandatory' flag. If the attribute is read-only, then
		 * we force mandatory to 'false', as otherwise a whole bunch of model-driven applications in
		 * ENM will fall over.
		 */
		if(!ptAttr.isKey() && ptAttr.getWriteBehavior() == WriteBehaviorType.NOT_ALLOWED) {
			ptAttr.setMandatory(false);
			ptAttr.getType().setNotNullConstraint(null);
		}

		/*
		 * There are certain language constructs in YANG that may result in a data node not actually existing
		 * at runtime. If such a data node does not exist, we cannot mark it as mandatory, as otherwise we would
		 * force the user to supply a value for an attribute that does not exist, and hence the operation (typically
		 * a create-MO operation) would always fail.
		 */
		if(attributeMayNotExistInSchemaAtRuntime(context, dataNode)) {
			ptAttr.setMandatory(false);
			ptAttr.getType().setNotNullConstraint(null);
		}

		EModelHelper.attachMetaForOriginalName(ptAttr.getMeta(), ptAttr.getName(), dataNode);

		return ptAttr;
	}

	private static void adjustReadWriteBehaviourForAttribute(final PrimaryTypeAttribute ptAttr, final AbstractStatement dataNode) {

		final AbstractDataType attrType = ptAttr.getType();
		if(attrType instanceof ComplexRefType || (attrType instanceof ListType && ((ListType) attrType).getCollectionValuesType() instanceof ComplexRefType)) {
			adjustReadWriteBehaviourForStructAttribute(ptAttr, dataNode);
		}
	}

	/**
	 * Special handling - The granularity of read/write behaviour in ENM is always at MOC attribute level. However,
	 * if the attribute is a struct (or struct-sequence) we could have *members* of the struct that are not
	 * notified - so some members are notified, others not. This would result in a situation where we might
	 * store the struct instance in the mirror, but we would never get updates for the non-persistent members,
	 * and hence the data in the mirror is wrong. Should we encounter such a struct, we will make the complete
	 * attribute non-persistent.
	 * <p>
	 * Somewhat similar logic applies for a struct where all members are read-only - in this case, make the
	 * attribute itself also read-only.
	 */
	private static void adjustReadWriteBehaviourForStructAttribute(final PrimaryTypeAttribute ptAttr, final AbstractStatement containerOrList) {

		final boolean structHasAtLeastOneWritableMember = ExtractionHelper.getMembersOfStruct(containerOrList).stream()
				.anyMatch(stmt -> PropertyUtils.isConfigurable(stmt));

		/*
		 * TODO: [STRUCT_IN_STRUCT] - since struct-in-struct is not supported, YT2 will remove all data nodes
		 * underneath struct members. That means the code below is sufficient for checking whether members of the
		 * struct are notifyable. If we ever support struct-in-struct, then we will have to look into the complete
		 * sub-tree to find any possible non-notified members (at 2nd, 3rd, ... level).
		 */
		final boolean structHasAtLeastOneNonNotifiedMember = ExtractionHelper.getMembersOfStruct(containerOrList).stream()
				.anyMatch(stmt -> !PreProcessor.isNotified(stmt));

		if(!structHasAtLeastOneWritableMember) {
			/*
			 * All members of the struct are non-writable (ie. they are all read-only). Hence make the
			 * attribute's write-behaviour NOT_ALLOWED.
			 */
			ptAttr.setWriteBehavior(WriteBehaviorType.NOT_ALLOWED);
		}

		if(structHasAtLeastOneNonNotifiedMember) {
			/*
			 * We make the attribute non-persistent. Careful that we don't override a write-behaviour
			 * of NOT_ALLOWED...
			 */
			ptAttr.setReadBehavior(ReadBehaviorType.FROM_DELEGATE);

			if(ptAttr.getWriteBehavior() == WriteBehaviorType.PERSIST_AND_DELEGATE) {
				ptAttr.setWriteBehavior(WriteBehaviorType.DELEGATE);
			}
		}
	}

	/**
	 * Returns the write-behaviour of an attribute.
	 */
	private static WriteBehaviorType getAttributeWriteBehaviour(final boolean isKey, final AbstractStatement leafOrLeafList) {

		if(!leafOrLeafList.isEffectiveConfigTrue()) {
			/*
			 * The leaf/leaf-list is 'config false', so we can never write to it.
			 */
			return WriteBehaviorType.NOT_ALLOWED;
		}

		if(EriCustomProcessor.isStatementEffectivelyStaticDataTrue(leafOrLeafList)) {
			/*
			 * The leaf/leaf-list is static data, meaning it cannot be modified by a
			 * client outside of the node - hence can't write.
			 */
			return WriteBehaviorType.NOT_ALLOWED;
		}

		if (isKey) {
			/*
			 * Keys are always persisted and always writable down to the node.
			 */
			return WriteBehaviorType.PERSIST_AND_DELEGATE;
		}

		/*
		 * At this point we have established that the leaf/leaf-list is 'config true' and 'static-data false'
		 * and not a key - in other words, a normal, configurable, attribute.
		 *
		 * Note that in YANG, we will *always* get a notification for 'config true' attributes, hence we don't
		 * have to check the notification property of the attribute. This is markedly different from ECIM, where
		 * a 'config true' attribute may not issue notifications.
		 */

		if (!EriCustomProcessor.getEffectiveEricssonStatus(leafOrLeafList).equals(YStatus.CURRENT)) {
			/*
			 * A non-key attribute, that is not 'status current' - means it will not be stored persistently.
			 */
			return WriteBehaviorType.DELEGATE;
		}

		/*
		 * It is a bog-standard, configurable, current, attribute.
		 */
		return WriteBehaviorType.PERSIST_AND_DELEGATE;
	}

	/**
	 * Returns the read-behaviour of an attribute - one of FROM_DELEGATE, FROM_PERSISTENCE.
	 * 
	 * The value of this depends on whether the attribute is "persistent" or not. Rules that
	 * apply, in order:
	 * 
	 * - Key attributes are always PERSISTED in the DB.
	 * - Non-current attributes are NOT-PERSISTED in the DB
	 * - Attributes that are static are PERSISTED in the DB (they don't change once they have been synched-up once).
	 * - Attributes that send notifications are PERSISTED in the DB (as they can be kept up-to-date with notifications).
	 * - Otherwise it must be read from the network.
	 */
	private static ReadBehaviorType getAttributeReadBehaviour(final boolean isKey, final AbstractStatement leafOrLeafList) {

		if(isKey) {
			/*
			 * Keys are always stored persistently.
			 */
			return ReadBehaviorType.FROM_PERSISTENCE;
		}

		if (!EriCustomProcessor.getEffectiveEricssonStatus(leafOrLeafList).equals(YStatus.CURRENT)) {
			/*
			 * The attribute is not 'status current', so it won't be stored
			 * persistently, i.e. need to read from network always.
			 */
			return ReadBehaviorType.FROM_DELEGATE;
		}

		if (PreProcessor.isNotified(leafOrLeafList) || EriCustomProcessor.isStatementEffectivelyStaticDataTrue(leafOrLeafList)) {
			/*
			 * We receive notifications for this attribute, or the value is
			 * static - meaning we can persist the value in ENM.
			 */
			return ReadBehaviorType.FROM_PERSISTENCE;
		}

		/*
		 * So if we come to here, we know the following:
		 * 
		 * - The attribute is not a key
		 * - The attribute is 'current'
		 * - It is not notified
		 * - It is not effectively static
		 */
		return ReadBehaviorType.FROM_DELEGATE;
	}

	private static boolean attributeMayNotExistInSchemaAtRuntime(final TransformerContext context, final AbstractStatement dataNode) {

		/*
		 * The data node sits inside a choice/case. Hence, the data node will not exist if the case-branch
		 * is not active.
		 */
		if(PrimaryTypeChoiceAndRequiresGenerator.hasChoiceCaseMemberInfo(dataNode)) {
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

	/**
	 * This is the method that decides whether an attribute must be modified by means of an extension. For this, the
	 * original attr generated is compared against its version in deviated. Technically, there are three possible scenarios:
	 * <p>
	 * - They are the same -> no need for extension handling
	 * - They are not the same and an extension can handle the diff -> create an attribute extension.
	 * - They are not the same and an extension cannot handle the diff -> create an attribute replacement.
	 * <p>
	 * HOWEVER, and that is really important, the behaviour by Model Service on model extension merge is to merge-in
	 * all "attribute extensions" irrespective of the <requiresTargetType> in the extension model WHEN THE TARGET OBJECT
	 * is null. In other words, they would overwrite each other, and the result is not deterministic.
	 * <p>
	 * For that reason we will always use a REPLACE and not an EXTEND, as REPLACE will only kick in when a target is
	 * supplied that matches the <requiresTargetType>.
	 * <p>
	 * Also, the extension mechanism is quite restrictive in terms of the properties it supports - with a replace, anything
	 * goes.
	 */
	private static void replaceAttrIfNecessary(final TransformerContext context, final PrimaryTypeExtensionDefinition pted, final PrimaryTypeAttribute origAttribute, final PrimaryTypeAttribute deviatedAttribute, final AbstractStatement dataNode) {
		context.setLastRelevantInfo(null);

		if(!attrPropertiesAreEqual(context, origAttribute, deviatedAttribute)) {
			/*
			 * It is not possible to EXTEND the attribute, the whole attribute must be REPLACED.
			 */
			context.logInfo(Constants.TEXT_REPLACED + " Replaced attribute '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNode) + "' through extension due to property change ['" + context.getLastRelevantInfo() + "'].");
			replaceAttributeThroughExtension(pted, deviatedAttribute);
		}
	}

	/**
	 * Checks whether any property of the attribute differs between the ORIGINAL and the DEVIATED variant.
	 */
	private static boolean attrPropertiesAreEqual(final TransformerContext context, final PrimaryTypeAttribute origAttribute, final PrimaryTypeAttribute deviatedAttribute) {

		if(!EModelHelper.eModelNamedEntityDefinitionPropertiesAreEqual(context, origAttribute, deviatedAttribute)) {
			return false;
		}

		if(!EModelHelper.eModelAttributeDefinitionPropertiesAreEqual(context, origAttribute, deviatedAttribute)) {
			return false;
		}

		return primaryTypeAttributePropertiesAreEqual(context, origAttribute, deviatedAttribute);
	}

	private static boolean primaryTypeAttributePropertiesAreEqual(final TransformerContext context, final PrimaryTypeAttribute origAttribute, final PrimaryTypeAttribute deviatedAttribute) {
		/*
		 * Inside primaryTypeAttribute the following are defined:
		 * 
		 * - requiresCapability         (will be ignored, cannot differ, even if/when we bring in support for 'feature' statement)
		 * - lockBeforeModify           (will be ignored, no Ericsson Yang Extension for it)
		 * - disturbances
		 * - changeEventHandling        (will be ignored, not transformed)
		 * - requires                   (will be ignored, cannot differ)
		 * - roleBasedAccessControlled  (will be ignored, does not apply to NRMs)
		 * - auditTrailLogging          (will be ignored, does not apply to NRMs)
		 * - dependencies
		 * - preCondition
		 * - sideEffects
		 * - takesEffect
		 * - readBehavior
		 * - writeBehavior
		 * - changeLogHandling    (will be ignored, not transformed)
		 * - cascadeDelete        (will be ignored, does not apply to NRMs)
		 * - definedBy            (will be ignored, not transformed)
		 * - namespace            (will be ignored, cannot differ)
		 * - userExposure         (will be ignored, does not apply to NRMs)
		 */

		/*
		 * No check is done on the "requires". If we have come this far it means that the attribute exists both
		 * in ORIGINAL and DEVIATED. The "requires" will always be the same for both of these:
		 * 
		 * 1.) It is not possible in YANG to move a data node in-to or out-of a 'case'. That means the same data
		 *     node will always require the exact same case/choice.
		 * 2.) It is not possible to augment or deviate a 'when' statement, hence the 'when' is always the same
		 *     on a given data node, irrespective of variant.
		 */

		if(!PropertyUtils.valuesAreEqual(origAttribute.getDisturbances(), deviatedAttribute.getDisturbances())) {
			context.setLastRelevantInfo("disturbances");
			return false;
		}

		if(!PropertyUtils.valuesAreEqual(origAttribute.getDependencies(), deviatedAttribute.getDependencies())) {
			context.setLastRelevantInfo("dependencies");
			return false;
		}

		if(!PropertyUtils.valuesAreEqual(origAttribute.getPreCondition(), deviatedAttribute.getPreCondition())) {
			context.setLastRelevantInfo("pre-condition");
			return false;
		}

		if(!PropertyUtils.valuesAreEqual(origAttribute.getSideEffects(), deviatedAttribute.getSideEffects())) {
			context.setLastRelevantInfo("side-effects");
			return false;
		}

		if(!PropertyUtils.valuesAreEqual(origAttribute.getTakesEffect(), deviatedAttribute.getTakesEffect())) {
			context.setLastRelevantInfo("takes-effect");
			return false;
		}

		if(!PropertyUtils.valuesAreEqual(origAttribute.getReadBehavior(), deviatedAttribute.getReadBehavior())) {
			context.setLastRelevantInfo("read-behaviour");
			return false;
		}

		if(!PropertyUtils.valuesAreEqual(origAttribute.getWriteBehavior(), deviatedAttribute.getWriteBehavior())) {
			context.setLastRelevantInfo("write-behaviour");
			return false;
		}

		return true;
	}

	private static void removeAttributeThroughExtension(final PrimaryTypeExtensionDefinition pted, final PrimaryTypeAttribute attributeToRemove) {

		/*
		 * This is probably slightly dodgy - really, the namespace of the attribute should also be specified,
		 * as there could be multiple attributes having the same name but different namespace. In reality,
		 * it is very rare that this ever happens, but at some stage we should really cater for this.
		 */

		final PrimaryTypeAttributeRemovalType attributeRemoval = new PrimaryTypeAttributeRemovalType(); 
		attributeRemoval.setAttributeName(attributeToRemove.getName());
		pted.getPrimaryTypeAttributeRemoval().add(attributeRemoval);
	}

	private static void addAttributeThroughExtension(final PrimaryTypeExtensionDefinition pted, final PrimaryTypeAttribute attribute) {
		pted.getPrimaryTypeExtension().getPrimaryTypeAttribute().add(attribute);
	}

	private static void replaceAttributeThroughExtension(final PrimaryTypeExtensionDefinition ptExtDef, final PrimaryTypeAttribute deviatedAttribute) {

		if(ptExtDef.getPrimaryTypeAttributeReplacement() == null) {
			ptExtDef.setPrimaryTypeAttributeReplacement(new PrimaryTypeAttributeReplacementType());
		}

		ptExtDef.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute().add(deviatedAttribute);
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 */
	private static void ensureStableDpsPrimaryTypeAttributeGeneration(final PrimaryTypeDefinition ptDef) {

		/*
		 * Sort the attributes. We sort using toLowerCase() which makes the model a bit more readable for
		 * human users. Then, within each attribute, sort the meta-data.
		 */
		Collections.sort(ptDef.getPrimaryTypeAttribute(), (o1, o2) -> {
			final int o1IsKey = o1.isKey() ? 0 : 1;
			final int o2IsKey = o2.isKey() ? 0 : 1;
			return (o1IsKey != o2IsKey) ? (o1IsKey - o2IsKey) : o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH));
		});

		ptDef.getPrimaryTypeAttribute().forEach(ptAttr -> EModelHelper.ensureStableMetaGeneration(ptAttr.getMeta()));
	}

	private static void ensureStableDpsPrimaryTypeExtensionAttributeGeneration(final PrimaryTypeExtensionDefinition ptExtDef) {
		/*
		 * Whatever was generated into the extension in terms of attribute removal / replacement etc.
		 * also needs to be stable.
		 *
		 * Sort added attributes and meta-information within.
		 */
		Collections.sort(ptExtDef.getPrimaryTypeExtension().getPrimaryTypeAttribute(), (o1, o2) ->
			o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH)));

		ptExtDef.getPrimaryTypeExtension().getPrimaryTypeAttribute().forEach(ptAttr ->
			EModelHelper.ensureStableMetaGeneration(ptAttr.getMeta()));

		/*
		 * Sort removed attributes
		 */
		Collections.sort(ptExtDef.getPrimaryTypeAttributeRemoval(), (o1, o2) ->
			o1.getAttributeName().toLowerCase(Locale.ENGLISH).compareTo(o2.getAttributeName().toLowerCase(Locale.ENGLISH)));

		/*
		 * Sort replaced attributes and meta-information within. 
		 */
		if(ptExtDef.getPrimaryTypeAttributeReplacement() != null) {

			Collections.sort(ptExtDef.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute(), (o1, o2) ->
				o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH)));

			ptExtDef.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute().forEach(ptAttr ->
				EModelHelper.ensureStableMetaGeneration(ptAttr.getMeta()));
		}
	}
}
