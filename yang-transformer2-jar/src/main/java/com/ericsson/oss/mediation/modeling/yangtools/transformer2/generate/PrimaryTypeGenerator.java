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

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.ReadBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.HierarchyType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition.MultiKey;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.SystemCreated;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ImpliedUrnType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.NetworkManagementDomainType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.NotNullConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ThreeGPPSupport;

public class PrimaryTypeGenerator {

	/**
	 * Does what it says on the tin...
	 */
	public static PrimaryTypeDefinition createMockManagedElement(final TransformerContext context) {

		final ModelInfo ptdModelInfo = context.getMockManagedElement();

		final PrimaryTypeDefinition ptd = new PrimaryTypeDefinition();

		EModelHelper.populateEModelHeaderForDesigned(ptd, ptdModelInfo, "Root MO for node type " + context.getTarget().getType());

		ptd.setInheritsFrom(new ImpliedUrnType());
		ptd.getInheritsFrom().setUrn(Constants.OSS_TOP_MANAGEDELEMENT_300.toImpliedUrn());

		ptd.setHierarchyType(HierarchyType.HIERARCHICAL);
		ptd.setReadBehavior(ReadBehaviorType.FROM_PERSISTENCE);
		ptd.setWriteBehavior(WriteBehaviorType.NOT_ALLOWED);
		ptd.setSystemCreated(new SystemCreated());
		ptd.setDefinedBy(NetworkManagementDomainType.NE);

		EModelHelper.attachMeta(ptd.getMeta(), Constants.META_MOCK_MANAGED_ELEMENT);

		/*
		 * The key attribute
		 */
		final PrimaryTypeAttribute keyAttr = new PrimaryTypeAttribute();

		keyAttr.setName(Constants.MANAGEDELEMENT + "Id");
		keyAttr.setDesc("Key attribute");
		keyAttr.setKey(true);
		keyAttr.setMandatory(true);
		keyAttr.setImmutable(true);
		keyAttr.setSensitive(false);

		keyAttr.setReadBehavior(ReadBehaviorType.FROM_PERSISTENCE);
		keyAttr.setWriteBehavior(WriteBehaviorType.NOT_ALLOWED);

		final StringType stringType = new StringType();
		stringType.setNotNullConstraint(new NotNullConstraint());
		keyAttr.setType(stringType);

		ptd.getPrimaryTypeAttribute().add(keyAttr);

		return ptd;
	}

	/**
	 * Creates the ModelInfo that contains the model identity of the generated mock ManagedElement
	 * DPS_PRIMARYTYPE model. The namespace will be supplied through properties; the name and version
	 * are hard-coded. Example for generated URN:
	 * <p>
	 * /dps_primarytype/EpgOiTop/ManagedElement/1.0.0
	 */
	public static ModelInfo getModelInfoForMockManagedElementPrimaryTypeDefinition(final String mockManagedElementNamespace) {
		return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, mockManagedElementNamespace, Constants.MANAGEDELEMENT, Constants.ONE_ZERO_ZERO);
	}

	/**
	 * Generates a single DPS PT without attributes, i.e. the MOC and it's properties only, based on a supplied
	 * container or list. The data node supplied will be from the AUGMENTED variant, as this is certain to exist,
	 * and it is not possible via augmentation to modify any of the properties below.
	 */
	public static PrimaryTypeDefinition createDpsPrimaryType(final TransformerContext context, final AbstractStatement mocInAugmented, final boolean atTopLevelOfSchema) {

		final ModelInfo ptdModelInfo = getModelInfoForPrimaryTypeDefinition(mocInAugmented);
		final ModelInfo derivedFrom = CfmMimInfoGenerator.getDerivedFrom(context, mocInAugmented);

		final PrimaryTypeDefinition ptDef = new PrimaryTypeDefinition();

		EModelHelper.populateEModelHeaderForDerived(ptDef, ptdModelInfo, derivedFrom, mocInAugmented, PropertyUtils.getDescription(mocInAugmented, PreProcessor.getOriginalStatementIdentifier(mocInAugmented)));

		handleInheritanceOf3gppManagedElement(context, ptDef, mocInAugmented);

		/*
		 * Some properties that are always hard-coded.
		 */
		ptDef.setDefinedBy(NetworkManagementDomainType.NE);
		ptDef.setHierarchyType(HierarchyType.HIERARCHICAL);
		ptDef.setReadBehavior(ReadBehaviorType.FROM_PERSISTENCE);
		ptDef.setRequires(PrimaryTypeChoiceAndRequiresGenerator.getRequires(mocInAugmented));

		/*
		 * Properties whose values will come from the YANG container or list.
		 */
		if(isMocSystemCreated(context, mocInAugmented)) {
			ptDef.setSystemCreated(new SystemCreated());
		}

		ptDef.setDependencies(PropertyUtils.getDependencies(mocInAugmented));
		ptDef.setPreCondition(PropertyUtils.getPrecondition(mocInAugmented));

		/*
		 * Check for multiple keys on the list
		 */
		if(mocInAugmented.is(CY.STMT_LIST) && !PreProcessor.isKeyLessList(mocInAugmented)) {
			final List<String> keyNames = PreProcessor.getListKeyNames(mocInAugmented);

			if(keyNames.size() > 1) {
				final MultiKey multiKey = new MultiKey();
				multiKey.getKeyParts().addAll(keyNames);
				multiKey.setDelimiter("..");
				ptDef.setMultiKey(multiKey);
			}
		}

		/*
		 * At MO level, the write behaviour can only be ever either NOT_ALLOWED (if the MO is not modifiable) or
		 * PERSIST_AND_DELEGATE. There is no such thing as a DELEGATE MOC! The only way how the write-behaviour
		 * could be different between ORIGINAL and DEVIATED is if either the 'config' statement is changed, or
		 * the static-data statement is changed, both of which can only ever done through extension magic - hence
		 * we can safely use AUGMENTED and need not worry about BASE.
		 * 
		 * (Note that at attribute level, the write behaviour is much more complex.)
		 */
		ptDef.setWriteBehavior(getWriteBehaviourForMoc(mocInAugmented));

		/*
		 * Handle all of the meta-data (there can be quite a bit!)
		 */
		if(!PreProcessor.isNainMoc(mocInAugmented)) {
			EModelHelper.attachMetaForOriginalName(ptDef.getMeta(), ptDef.getName(), mocInAugmented);
		}

		EModelHelper.attachMetaForNamespaceCleanedOfNodeAppInstanceName(ptDef.getMeta(), mocInAugmented);

		if(atTopLevelOfSchema) {
			EModelHelper.attachMeta(ptDef.getMeta(), Constants.META_TOP_LEVEL_DATA_NODE);
		}

		if(mocInAugmented.is(CY.STMT_CONTAINER)) {
			EModelHelper.attachMeta(ptDef.getMeta(), PropertyUtils.isNPContainer(mocInAugmented) ? Constants.META_NON_PRESENCE_CONTAINER : Constants.META_PRESENCE_CONTAINER);
		}

		if(PreProcessor.isKeyLessList(mocInAugmented)) {
			EModelHelper.attachMeta(ptDef.getMeta(), Constants.META_KEYLESS_LIST);
		}

		if(PreProcessor.is3gppMoc(mocInAugmented)) {
			EModelHelper.attachMeta(ptDef.getMeta(), Constants.META_3GPP_REAGGREGATED_IOC);
		}

		if(PreProcessor.isNainMoc(mocInAugmented)) {
			EModelHelper.attachMeta(ptDef.getMeta(), Constants.META_REAL_NAME_ATTACHED_TO_DATA);
		}

		return ptDef;
	}

	public static boolean isMocSystemCreated(final TransformerContext context, final AbstractStatement moc) {

		/*
		 * The MOC has been marked as explicitly system-created, or is NP-container and context says they
		 * shall be generated as system-created.
		 */
		if(PropertyUtils.isSystemCreated(context, moc)) {
			return true;
		}

		/*
		 * The MOC on the node has been given an application instance name. The MOC instance will always be
		 * generated by the node (when the application instance is deployed), hence it is system-created.
		 */
		if(PreProcessor.isNainMoc(moc)) {
			return true;
		}

		return false;
	}

	/**
	 * At MO level, the write behaviour can only be ever either NOT_ALLOWED (if the MO is not modifyable) or
	 * PERSIST_AND_DELEGATE. There is no such thing as a DELEGATE MOC!
	 */
	public static WriteBehaviorType getWriteBehaviourForMoc(final AbstractStatement moc) {

		return PropertyUtils.isConfigurable(moc) ? WriteBehaviorType.PERSIST_AND_DELEGATE : WriteBehaviorType.NOT_ALLOWED;
	}

	/**
	 * Creates the ModelInfo that contains the model identity for a MOC. The namespace of the MOC will
	 * be cleaned if necessary.
	 */
	public static ModelInfo getModelInfoForPrimaryTypeDefinition(final AbstractStatement moc) {

		final String mocNamespace = PreProcessor.getNamespaceCleanedOfNodeAppInstanceName(moc);
		final String mocName = PreProcessor.getMocName(moc);
		final String xyzVersion = PreProcessor.getXyzVersionForNamespace(moc);

		return getModelInfoForPrimaryTypeDefinition(mocNamespace, mocName, xyzVersion);
	}

	/**
	 * Creates the ModelInfo that contains the model identity of a DPS_PRIMARYTYPE model. Example
	 * for generated URN:
	 * <p>
	 * /dps_primarytype/urn:3gpp:sa5:_3gpp-nr-nrm-gnbdufunction/GNBDUFunction/2019.8.21
	 */
	public static ModelInfo getModelInfoForPrimaryTypeDefinition(final String cleanedNamespace, final String mocName, final String xyzVersion) {
		return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, cleanedNamespace, mocName, xyzVersion);
	}

	/**
	 * Special handling for 3GPP ManagedElement - it will always inherit from OSS_TOP::ManagedElement
	 * (even if the 'apply3gppHandling' property is not set).
	 */
	private static void handleInheritanceOf3gppManagedElement(final TransformerContext context, final PrimaryTypeDefinition ptd, final AbstractStatement containerOrList) {
		if(ThreeGPPSupport.is3gppManagedElement(containerOrList)) {
			/*
			 * The inheritance relationship must always be generated - otherwise we could have
			 * different builds producing different models, some using inheritance, others not.
			 */
			ptd.setInheritsFrom(new ImpliedUrnType());
			ptd.getInheritsFrom().setUrn(Constants.OSS_TOP_MANAGEDELEMENT_300.toImpliedUrn());
		}
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 * <p>
	 * Attributes and actions are sorted separately, so not done here.
	 */
	public static void ensureStableModelGeneration(final PrimaryTypeDefinition ptDef) {
		/*
		 * Sort the choices. No need to sort within, previously done.
		 */
		Collections.sort(ptDef.getChoice(), (o1, o2) -> o1.getName().compareTo(o2.getName()));

		/*
		 * Sort the meta-data of the PrimaryType itself.
		 */
		EModelHelper.ensureStableMetaGeneration(ptDef.getMeta());
	}
}
