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
import java.util.Objects;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DerivedModel;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DesignedModel;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelAttributeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelExtensionDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelNamedEntityDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ExtendedModelElementType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LifeCycleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MetaInformation;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ModelCreationInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.NotNullConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.RequiresTargetType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_combined.CombinedEnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EriCustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriPreliminary;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YOrderedBy;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YStatus;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.Target;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2Exception;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class EModelHelper {

	/**
	 * All extension models, which will always be target type/version-specific, will get
	 * the same namespace. This makes it much easier to correlate them, and will also allow
	 * combining of OSS_EDTs.
	 * <p>
	 * (The name of the extension model is specific and will always reflect the element that
	 * has been extended. Often, this requires the name of the extension model to include
	 * the namespace and name, and possibly the path, of the element that has been extended.) 
	 * <p>
	 * Note that the namespace of extension models is typically not visible to the end user,
	 * so it can look a bit more quirky.
	 */
	public static String getExtensionModelNamespace(final Target target) {
		return getTargetVersionSpecificName(target);
	}

	/**
	 * Generates a string that is specific to the target version; includes the target category, target type
	 * and target version, all of which are required to make the target version unique amongst all other
	 * target types and target versions in the system.
	 * <p>
	 * Example for generated value: "NODE__EPG-OI__3.12"
	 */
	public static String getTargetVersionSpecificName(final Target target) {
		return target.getCategory() + "__" + target.getType() + "__" + target.getVersion();
	}

	/**
	 * The version of an extension model will always be 1.0.0
	 */
	public static String getModelInfoVersionForExtension() {
		return Constants.ONE_ZERO_ZERO;
	}

	/**
	 * Given a statement (usually a data node) returns the EModel lifecycle value for the data node.
	 */
	public static LifeCycleType getLifeCycleType(final AbstractStatement statement) {

		LifeCycleType result;

		final String effectiveEricssonStatus = EriCustomProcessor.getEffectiveEricssonStatus(statement);
		switch(effectiveEricssonStatus) {
		case YEriPreliminary.PRELIMINARY:
			result = LifeCycleType.PRELIMINARY;
			break;
		case YStatus.DEPRECATED:
			result = LifeCycleType.DEPRECATED;
			break;
		case YStatus.OBSOLETE:
			result = LifeCycleType.OBSOLETE;
			break;
		default:
			result = LifeCycleType.CURRENT;
		}

		return result;
	}

	/**
	 * If the name of an element generated into an EModel differs from the original name of the element (as defined
	 * in the original YANG module), attaches meta information to the element in the EModel denoting the original
	 * name. Mediation will use this to map data coming/going over NETCONF.
	 */
	public static void attachMetaForOriginalName(final List<MetaInformation> metaList, final String nameInEmodel, final AbstractStatement origStatement) {

		final String originalStatementIdentifier = PreProcessor.getOriginalStatementIdentifier(origStatement);
		if(!nameInEmodel.equals(originalStatementIdentifier)) {
			attachMeta(metaList, Constants.META_ORIGINAL_NAME, originalStatementIdentifier);
		}
	}

	/**
	 * If the namespace is in data for the given statement denote this in metadata.
	 */
	public static void attachMetaForNamespaceCleanedOfNodeAppInstanceName(final List<MetaInformation> metaList, final AbstractStatement origStatement) {

		if(PreProcessor.namespaceHasBeenCleanedOfNodeAppInstanceName(origStatement)) {
			attachMeta(metaList, Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA);
		}
	}

	public static void attachMeta(final List<MetaInformation> metaList, final String metaName) {
		attachMeta(metaList, metaName, null);
	}

	public static void attachMeta(final List<MetaInformation> metaList, final String metaName, final String metaValue) {
		final MetaInformation meta = new MetaInformation();
		meta.setMetaName(Objects.requireNonNull(metaName));
		meta.setMetaValue(metaValue);
		metaList.add(meta);
	}

	/**
	 * Returns the <requiresTarget> element that will be used to make models target-version specific.
	 */
	public static RequiresTargetType getRequiresTargetType(final TransformerContext context) {

		/*
		 * If we are generating a domain application model then we won't have a NRM, and
		 * therefore no target type / version that we require.
		 */
		if(context.generateForDomainApplicationModel()) {
			return null;
		}

		final RequiresTargetType requiresTargetType = new RequiresTargetType();
		requiresTargetType.setCategory(context.getTarget().getCategory());
		requiresTargetType.setType(context.getTarget().getType());
		requiresTargetType.setVersion(context.getTarget().getVersion());

		return requiresTargetType;
	}

	/**
	 * Given a MOC, returns it's implied versioned URN. The namespace will be cleaned if necessary.
	 */
	public static String getImpliedVersionedUrnForMoc(final AbstractStatement moc) {

		final String mocNamespace = PreProcessor.getNamespaceCleanedOfNodeAppInstanceName(moc);
		final String mocName = PreProcessor.getMocName(moc);
		final String mocVersion = PreProcessor.getXyzVersionForNamespace(moc);

		return EModelHelper.getImpliedVersionedUrn(mocNamespace, mocName, mocVersion);
	}

	/**
	 * Returns the implied versioned URN for the supplied (possibly cleaned) namespace, name and version.
	 */
	public static String getImpliedVersionedUrn(final String cleanedNamespace, final String mocName, final String xyzVersion) {
		/*
		 * Note: The DPS_PRIMARYTYPE below is irrelevant, it will not be returned in the
		 * implied URN, of course, just using it for convenience.
		 */
		return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, cleanedNamespace, mocName, xyzVersion).toImpliedUrn();
	}

	/**
	 * Populates the header of an EModel Extension. 
	 */
	public static void populateEModelExtensionHeader(final TransformerContext context, final EModelExtensionDefinition emodelExtDef, final ModelInfo extensionModelNsnv, final String impliedUrnOfExtendedModel, final String desc) {
		/*
		 * Find out what model it is we are extending.
		 */
		final Class<EModelDefinition> eModelClassOfExtendedModel = getExtendedEModelClass(emodelExtDef);
		final ModelInfo extendedModel = ModelInfo.fromImpliedUrn(impliedUrnOfExtendedModel, getSchemaNameForEModelClass(eModelClassOfExtendedModel));

		if(extendedModel.equals(context.getEffectiveContainmentParent()) || extendedModel.equals(Constants.OSS_TOP_MECONTEXT_STAR)) {
			/*
			 * Special case - this extension model here extends the containment parent model, which will possibly
			 * not have been generated (could be ComTop::ManagedElement, or MeContext). We have to use designed-model
			 * then - nothing much else we can do. Same thing applies if it MeContext (to record the non-generated
			 * data nodes).
			 */
			populateEModelHeaderForDesigned(emodelExtDef, extensionModelNsnv, desc);
		} else {
			/*
			 * Normal case - the model that is being extended has also been created by us, so we can find it. The
			 * derived-from for an extension will be set to the exact same value as the model it extends. The model
			 * that is extended has been generated already, so is available to us.
			 */
			final EModelDefinition extendedEmodel = context.getCreatedEmodels().getGeneratedEModel(extendedModel);
			final String derivedFromUrn = extendedEmodel.getModelCreationInfo().getDerivedModel().getDerivedFrom();
			populateEModelHeaderForDerived(emodelExtDef, extensionModelNsnv, ModelInfo.fromUrn(derivedFromUrn), null, desc);
		}

		emodelExtDef.getExtendedModelElement().add(new ExtendedModelElementType());
		emodelExtDef.getExtendedModelElement().get(0).setUrn(impliedUrnOfExtendedModel);
	}

	public static void populateEModelHeaderForDesigned(final EModelDefinition emodelDef, final ModelInfo nsnv, final String desc) {

		populateEModelHeaderWithNsnvAndDesc(emodelDef, nsnv, desc);

		final ModelCreationInfo modelCreationInfo = new ModelCreationInfo();
		modelCreationInfo.setAuthor("YANG Transformer2");
		modelCreationInfo.setDesignedModel(new DesignedModel());
		modelCreationInfo.getDesignedModel().setComponentId("YANG Transformer2");
		emodelDef.setModelCreationInfo(modelCreationInfo);
	}

	public static void populateEModelHeaderForDerived(final EModelDefinition emodelDef, final ModelInfo nsnv, final ModelInfo derivedFrom, final AbstractStatement sourceStatement, final String desc) {

		populateEModelHeaderWithNsnvAndDesc(emodelDef, nsnv, desc);

		final ModelCreationInfo modelCreationInfo = new ModelCreationInfo();
		modelCreationInfo.setAuthor("YANG Transformer2");
		modelCreationInfo.setDerivedModel(new DerivedModel());
		modelCreationInfo.getDerivedModel().setDerivedFrom(Objects.requireNonNull(derivedFrom).toUrn());
		emodelDef.setModelCreationInfo(modelCreationInfo);

		if(sourceStatement != null) {
			emodelDef.setLifeCycle(EModelHelper.getLifeCycleType(sourceStatement));
			emodelDef.setLifeCycleDesc(PropertyUtils.getStatusInformation(sourceStatement));
		}
	}

	private static void populateEModelHeaderWithNsnvAndDesc(final EModelDefinition emodelDef, final ModelInfo nsnv, final String desc) {

		emodelDef.setNs(nsnv.getNamespace());
		emodelDef.setName(nsnv.getName());
		emodelDef.setVersion(nsnv.getVersion().toString());

		emodelDef.setDesc(Objects.requireNonNull(desc));
	}

	/**
	 * Sets the properties to handle the EmodelNamedEntityDefinition
	 */
	public static void setEModelNamedEntityDefinitionProperties(final EModelNamedEntityDefinition namedEntityDefinition, final AbstractStatement schemaNode) {
		namedEntityDefinition.setName(PreProcessor.getRenamedStatementIdentifier(schemaNode));
		namedEntityDefinition.setLifeCycle(EModelHelper.getLifeCycleType(schemaNode));
		namedEntityDefinition.setLifeCycleDesc(PropertyUtils.getStatusInformation(schemaNode));
		namedEntityDefinition.setDesc(PropertyUtils.getDescription(schemaNode, PreProcessor.getRenamedStatementIdentifier(schemaNode)));
	}

	/**
	 * Sets the properties to handle the EModelAttributeDefinition
	 */
	public static void setEModelAttributeDefinitionProperties(final TransformerContext context, final EModelAttributeDefinition attrDef, final AbstractStatement dataNode) {

		attrDef.setKey(PreProcessor.isListKey(dataNode));
		attrDef.setMandatory(attrDef.isKey() || PropertyUtils.isDataNodeMandatory(dataNode));
		attrDef.setImmutable(attrDef.isKey() || PropertyUtils.isRestricted(dataNode));
		attrDef.setSensitive(PropertyUtils.isSensitive(dataNode));

		attrDef.setType(DataTypeGenerator.generateDataType(context, dataNode));
		attrDef.setDefault(DefaultValueGenerator.generateDefault(context, attrDef.getType(), dataNode));
		attrDef.setUnit(PropertyUtils.getUnits(dataNode));

		/*
		 * Some data types by nature of their constraints, or contents, should be considered mandatory.
		 */
		if(DataTypeGenerator.isDataTypeConsideredMandatory(context, attrDef.getType())) {
			attrDef.setMandatory(true);
		}

		if(attrDef.isMandatory()) {
			attrDef.getType().setNotNullConstraint(new NotNullConstraint());
		}

		/*
		 * Where the data node (a leaf-list in this case) has a 'ordered-by user' it is important that
		 * the order of entries is maintained when mediating down to the node. We use meta-data for this.
		 */
		if(ExtractionHelper.isLeafListOrList(dataNode)) {
			final YOrderedBy orderedBy = dataNode.getChild(CY.STMT_ORDERED_BY);
			final boolean isUserOrdered = orderedBy != null && orderedBy.isOrderedByUser();
			if(isUserOrdered) {
				EModelHelper.attachMeta(attrDef.getMeta(), Constants.META_USER_ORDERED);
			}
		}
		if(PreProcessor.is3gppNonUniqueSequence(dataNode)) {
			EModelHelper.attachMeta(attrDef.getMeta(), Constants.META_3GPP_NON_UNIQUE_SEQUENCE);
		}
	}

	static boolean eModelNamedEntityDefinitionPropertiesAreEqual(final TransformerContext context, final EModelNamedEntityDefinition origNamedEntity, final EModelNamedEntityDefinition deviatedNamedEntity) {
		/*
		 * Inside eModelNamedEntityDefinition the following are defined:
		 * 
		 * - description
		 * - meta                    (will be ignored, cannot differ)
		 * - name                    (will be ignored, cannot differ at this point)
		 * - lifecycle               (will be ignored, cannot differ as Ericsson lifecycle extensions sit under
		 *                            'status' and that cannot be augmented/deviated)
		 * - lifecycle-description   (will be ignored, not important enough)
		 */

		if(!PropertyUtils.valuesAreEqual(origNamedEntity.getDesc(), deviatedNamedEntity.getDesc())) {
			context.setLastRelevantInfo("description");
			return false;
		}

		return true;
	}

	static boolean eModelAttributeDefinitionPropertiesAreEqual(final TransformerContext context, final EModelAttributeDefinition origAttribute, final EModelAttributeDefinition deviatedAttribute) {
		/*
		 * Inside eModelAttributeDefinition the following are defined:
		 * 
		 * - type
		 * - default
		 * - unit        (will be ignored, not important enough)
		 * - mandatory
		 * - immutable
		 * - key         (will be ignored, cannot differ)
		 * - sensitive
		 */		

		if(!PropertyUtils.valuesAreEqual(origAttribute.isMandatory(), deviatedAttribute.isMandatory())) {
			context.setLastRelevantInfo("mandatory-flag");
			return false;
		}

		if(!PropertyUtils.valuesAreEqual(origAttribute.isImmutable(), deviatedAttribute.isImmutable())) {
			context.setLastRelevantInfo("immutable-flag");
			return false;
		}

		if(!PropertyUtils.valuesAreEqual(origAttribute.isSensitive(), deviatedAttribute.isSensitive())) {
			context.setLastRelevantInfo("sensitive-flag");
			return false;
		}

		if(!DefaultValueGenerator.defaultsAreEqual(origAttribute.getDefault(), deviatedAttribute.getDefault())) {
			context.setLastRelevantInfo("default-value");
			return false;
		}

		if(!DataTypeGenerator.typesAreEqual(origAttribute.getType(), deviatedAttribute.getType())) {
			context.setLastRelevantInfo("data-type / constraints");
			return false;
		}

		return true;
	}

	/**
	 * Sorts the meta information to help with stable model generation
	 */
	static void ensureStableMetaGeneration(final List<MetaInformation> metaInformation) {
		Collections.sort(metaInformation, (o1, o2) -> o1.getMetaName().compareTo(o2.getMetaName()));
	}

	static String getHumanReadableUrn(final ModelInfo mi) {
		return "/" + mi.getNamespace() + "/" + mi.getName() + "/" + mi.getVersion().toString();
	}

	/**
	 * Given an EModel, returns its ModelInfo.
	 */
	public static ModelInfo getModelInfoForEModel(final EModelDefinition emodel) {
		final String schema = getSchemaNameForEModelClass(emodel.getClass());
		return new ModelInfo(schema, emodel.getNs(), emodel.getName(), emodel.getVersion());
	}

	/**
	 * Given an EModel class, returns the corresponding schema name (one of the constants from {@link SchemaConstants}).
	 */
	public static <T extends EModelDefinition> String getSchemaNameForEModelClass(final Class<T> emodelClass) {
		if(emodelClass.equals(PrimaryTypeDefinition.class)) {
			return SchemaConstants.DPS_PRIMARYTYPE;
		}
		if(emodelClass.equals(PrimaryTypeExtensionDefinition.class)) {
			return SchemaConstants.DPS_PRIMARYTYPE_EXT;
		}
		if(emodelClass.equals(PrimaryTypeRelationshipDefinition.class)) {
			return SchemaConstants.DPS_RELATIONSHIP;
		}

		if(emodelClass.equals(EnumDataTypeDefinition.class)) {
			return SchemaConstants.OSS_EDT;
		}
		if(emodelClass.equals(EnumDataTypeExtensionDefinition.class)) {
			return SchemaConstants.OSS_EDT_EXT;
		}
		if(emodelClass.equals(CombinedEnumDataTypeDefinition.class)) {
			return SchemaConstants.OSS_EDT_COMBINED;
		}

		if(emodelClass.equals(ManagementInformationModelInformation.class)) {
			return SchemaConstants.CFM_MIMINFO;
		}

		if(emodelClass.equals(ComplexDataTypeDefinition.class)) {
			return SchemaConstants.OSS_CDT;
		}

		throw new YangTransformer2Exception("Unhandled EModel class " + emodelClass.getName());
	}

	/**
	 * Given an instance of an EModel Extension, returns the class representing the EModel type that is being extended.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends EModelDefinition> Class<T> getExtendedEModelClass(final EModelExtensionDefinition emodelExt) {

		if(emodelExt.getClass().equals(PrimaryTypeExtensionDefinition.class)) {
			return (Class<T>) PrimaryTypeDefinition.class;
		}
		if(emodelExt.getClass().equals(EnumDataTypeExtensionDefinition.class)) {
			return (Class<T>) EnumDataTypeDefinition.class;
		}

		throw new YangTransformer2Exception("Unhandled extension definition " + emodelExt.getClass().getName());
	}
}
