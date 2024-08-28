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

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeAttribute;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ComplexRefType;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.StringHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.Target;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;

public class ComplexTypeGenerator {

	static ComplexRefType generatePlaceholderComplexTypeForActionOutput(final TransformerContext context, final AbstractStatement actionOrRpc, final List<AbstractStatement> filteredChildrenOfOutput) {

		final ModelInfo owningMocModelInfo = ExtractionHelper.getModelInfoForOwningMoc(context, actionOrRpc);
		final String actionOrRpcName = PreProcessor.getRenamedStatementIdentifier(actionOrRpc);
		final String pathToActionOutput = getPathToActionOrRpcOutput(owningMocModelInfo.getName(), actionOrRpcName);

		/*
		 * The CDT for complex action output is always derived from the deviated-variant; hence,
		 * it will become target version-specific.
		 */
		final ModelInfo cdtModelInfo = getModelInfoForCdtInDeviated(context.getTarget(), owningMocModelInfo.getNamespace(), pathToActionOutput);
		final ModelInfo derivedFrom = CfmMimInfoGenerator.getDerivedFrom(context, actionOrRpc);

		final ComplexDataTypeDefinition cdtDef = new ComplexDataTypeDefinition();
		context.getCreatedEmodels().addGeneratedEModel(cdtDef);

		EModelHelper.populateEModelHeaderForDerived(cdtDef, cdtModelInfo, derivedFrom, null, "Auto-generated complex type for complex action output.");

		/*
		 * Now do the filtered members of the output.
		 */
		for(final AbstractStatement filteredChild : filteredChildrenOfOutput) {
			final ComplexDataTypeAttribute member = generateStructMember(context, filteredChild);
			cdtDef.getAttribute().add(member);
		}

		ensureStableOssCdtGeneration(cdtDef);

		return getComplexRefForCdt(cdtModelInfo);
	}

	/**
	 * Returns the "path" from the MOC to the CDT representing complex output of an action or RPC. Actions
	 * always sit directly under a MOC; RPCs always sit directly under the containment parent. So no need
	 * for fancy navigation.
	 */
	public static String getPathToActionOrRpcOutput(final String mocName, final String actionOrRpcName) {
		return mocName + "__" + actionOrRpcName + "__out";
	}

	static ComplexRefType generateComplexType(final TransformerContext context, final AbstractStatement containerOrList) {
		/*
		 * The generation logic is slightly different between original and deviated - we will come to
		 * that in a moment...
		 */
		if(PreProcessor.isInOriginalVariant(containerOrList)) {
			return generateOssCdtForOriginal(context, containerOrList);
		} else {
			return handleComplexTypeInDeviatingVariant(context, containerOrList);
		}
	}

	private static ComplexRefType handleComplexTypeInDeviatingVariant(final TransformerContext context, final AbstractStatement containerOrList) {

		/*
		 * This is more tricky. We are in deviating, so the question is whether the content of the
		 * struct in the deviated-variant is the *exact* same as the content of the struct in
		 * the original-variant (assuming it even is a struct there).
		 * 
		 * First thing to figure out is whether a CDT has been generated for the exact same struct in
		 * original. If so, it should exists in the EModels generated...
		 */
		final ModelInfo owningMoc = ExtractionHelper.getModelInfoForOwningMoc(context, containerOrList);
		final String pathToContainerOrList = ExtractionHelper.getPathFromMocToDataNode(context, containerOrList);
		final ModelInfo originalCdtModelInfo = getModelInfoForCdtInOriginal(owningMoc.getNamespace(), pathToContainerOrList, owningMoc.getVersion().toString());

		final ComplexDataTypeDefinition originalCdt = context.getCreatedEmodels().getGeneratedEModel(originalCdtModelInfo);
		if(originalCdt != null) {
			/*
			 * CDT also in the original (that will be the 99% case). Now we need to figure out whether the
			 * deviated-variant of the struct is the exact same as the original-variant. For this, we
			 * generate a fake OSS CDT that we can then use to compare against the original OSS CDT.
			 * 
			 * In the vast majority of cases, the CDTs will be the same.
			 */
			final ModelInfo fakeModelInfo = new ModelInfo(SchemaConstants.OSS_CDT, "fake-ns", "fake-name", "1.1.1");
			final ComplexDataTypeDefinition fakeCdt = createOssCdt(context, containerOrList, fakeModelInfo);

			if(cdtsAreTheSame(context, originalCdt, fakeCdt)) {
				/*
				 * Content is the exact same - hence we can re-use the original OSS CDT. No need to create
				 * another OSS CDT.
				 */
				return getComplexRefForCdt(originalCdtModelInfo);

			} else {
				/*
				 * They are not the same. We generate a brand-new OSS CDT, which must be target version-specific. 
				 */
				return generateOssCdtForDeviatedVariant(context, containerOrList);
			}

		} else {
			/*
			 * The data type in original is not struct, or we are in an action - hence we have to generate the
			 * OSS CDT here now.
			 */
			return generateOssCdtForDeviatedVariant(context, containerOrList);
		}
	}

	private static ComplexRefType generateOssCdtForOriginal(final TransformerContext context, final AbstractStatement containerOrList) {

		final ModelInfo owningMoc = ExtractionHelper.getModelInfoForOwningMoc(context, containerOrList);
		final String pathToDataNode = ExtractionHelper.getPathFromMocToDataNode(context, containerOrList);
		final ModelInfo cdtModelInfo = getModelInfoForCdtInOriginal(owningMoc.getNamespace(), pathToDataNode, owningMoc.getVersion().toString());

		generateOssCdt(context, containerOrList, cdtModelInfo);

		return getComplexRefForCdt(cdtModelInfo);
	}

	private static ComplexRefType generateOssCdtForDeviatedVariant(final TransformerContext context, final AbstractStatement containerOrList) {

		final ModelInfo owningMoc = ExtractionHelper.getModelInfoForOwningMoc(context, containerOrList);
		final String pathToContainerOrList = ExtractionHelper.getPathFromMocToDataNode(context, containerOrList);
		final ModelInfo cdtModelInfo = getModelInfoForCdtInDeviated(context.getTarget(), owningMoc.getNamespace(), pathToContainerOrList);

		generateOssCdt(context, containerOrList, cdtModelInfo);

		return getComplexRefForCdt(cdtModelInfo);
	}

	private static void generateOssCdt(final TransformerContext context, final AbstractStatement containerOrList, final ModelInfo cdtModelInfo) {
		final ComplexDataTypeDefinition cdt = createOssCdt(context, containerOrList, cdtModelInfo);
		context.getCreatedEmodels().addGeneratedEModel(cdt);
	}

	/**
	 * Creates the actual CDT, for both ORIGINAL and DEVIATED variants.
	 * 
	 * @param containerOrList The YANG container or list representing the struct/struct-sequence.
	 */
	private static ComplexDataTypeDefinition createOssCdt(final TransformerContext context, final AbstractStatement containerOrList, final ModelInfo cdtModelInfo) {

		/*
		 * The CDT sits somewhere under an MOC, which is its eventual owner.
		 */
		final String desc = PropertyUtils.getDescription(containerOrList, "Struct for attribute '" + PreProcessor.getOriginalStatementIdentifier(containerOrList) + "'.");
		final ModelInfo derivedFrom = CfmMimInfoGenerator.getDerivedFrom(context, containerOrList);

		final ComplexDataTypeDefinition cdtDef = new ComplexDataTypeDefinition();
		EModelHelper.populateEModelHeaderForDerived(cdtDef, cdtModelInfo, derivedFrom, containerOrList, desc);
		final boolean weAreInDeviatedVariant = PreProcessor.isInDeviatedVariant(containerOrList);

		final boolean structDefinedInSubmodule = PropertyUtils.definedInSubmodule(containerOrList);
		if (structDefinedInSubmodule && !weAreInDeviatedVariant) {
			/*
			 * If the struct was defined in a submodule and we are in ORIGINAL, we don't generate any members at
			 * all. Otherwise the CDT generated for ORIGINAL might be different depending on submodule content.
			 */
		} else {
			/*
			 * The members of the struct will be only leafs/leaf-lists, as we don't support struct-in-struct.
			 */
			final List<AbstractStatement> childDataNodes = ExtractionHelper.getMembersOfStruct(containerOrList);
			for(final AbstractStatement childDataNode : childDataNodes) {
				/*
				 * In ORIGINAL we will only create a member into the CDT if they have been declared in the same
				 * YAM - ie. the member was declared in the same YAM as the CDT itself. In DEVIATED, everything gets
				 * generated. This logic is the exact same that is adopted for MOC attributes, with the difference
				 * being that there is no extension mechanism for CDTs, so if the CDT has been augmented or deviated
				 * there will be a difference between the CDTs generated between ORIGINAL and DEVIATED. (Hence the need
				 * for cdtsAreTheSame() later on).
				 */
				final boolean createMember = weAreInDeviatedVariant || PropertyUtils.definedInSameYam(containerOrList, childDataNode);
				if(createMember) {
					final ComplexDataTypeAttribute member = generateStructMember(context, childDataNode);
					cdtDef.getAttribute().add(member);
				}
			}
		}

		/*
		 * The struct-sequence may have multiple keys. The order of these must be maintained. There is no support
		 * in the oss_cdt schema for this, so we must generate meta-data for this.
		 */
		if(containerOrList.is(CY.STMT_LIST) && !PreProcessor.isKeyLessList(containerOrList)) {
			final List<String> keyNamesAsList = PreProcessor.getListKeyNames(containerOrList);
			if(keyNamesAsList.size() > 1) {
				final String keyNames = StringHelper.toString(keyNamesAsList, null, null, " ", null, null);
				EModelHelper.attachMeta(cdtDef.getMeta(), Constants.META_MULTIPLE_KEY_NAMES, keyNames);
			}
		}

		ensureStableOssCdtGeneration(cdtDef);

		return cdtDef;
	}

	private static ComplexDataTypeAttribute generateStructMember(final TransformerContext context, final AbstractStatement dataNode) {

		final ComplexDataTypeAttribute member = new ComplexDataTypeAttribute();

		EModelHelper.setEModelNamedEntityDefinitionProperties(member, dataNode);
		EModelHelper.setEModelAttributeDefinitionProperties(context, member, dataNode);

		member.setNamespace(PreProcessor.getNamespaceCleanedOfNodeAppInstanceName(dataNode));

		/*
		 * There are certain language constructs in YANG that may result in a data node not actually existing
		 * at runtime. If such a data node does not exist, we cannot mark the member as mandatory, as otherwise
		 * we would force the user to supply a value for a member that does not exist, and hence the operation
		 * (typically a create-MO operation) would always fail.
		 */
		if(memberMayNotExistInSchemaAtRuntime(context, dataNode)) {
			member.setMandatory(false);
			member.getType().setNotNullConstraint(null);
		}

		EModelHelper.attachMetaForOriginalName(member.getMeta(), member.getName(), dataNode);

		return member;
	}

	private static boolean memberMayNotExistInSchemaAtRuntime(final TransformerContext context, final AbstractStatement dataNode) {

		/*
		 * The data node sits inside a choice/case. Hence, the data node will not exist if the case-branch
		 * is not active.
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

	/**
	 * Creates the ModelInfo that contains the model identity of the generated OSS CDT when generated in ORIGINAL.
	 * The name of the CDT is always the path from the owning MOC to the data node that uses the struct as
	 * data type. The namespace and version then must be the same as those of the owning MOC. Example for generated URN:
	 * <p>
	 * /oss_cdt/urn:3gpp:sa5:_3gpp-nr-nrm-nrcelldu/NRCellDU__plmnId/2019.9.3 ('NRCellDU' is the MOC name and 'plmnId'
	 * is the name of the container representing the struct). 
	 */
	public static ModelInfo getModelInfoForCdtInOriginal(final String cleanedMocNamespace, final String pathToDataNode, final String mocXyzVersion) {

		return new ModelInfo(SchemaConstants.OSS_CDT, cleanedMocNamespace, pathToDataNode, mocXyzVersion);
	}

	/**
	 * Creates the ModelInfo that contains the model identity of the generated OSS CDT when generated in DEVIATED.
	 * Since it is generated in DEVIATED it implies that it is target version-specific; and that will be used as
	 * CDT namespace. The CDT name has to be made unique, which we achieve by concatenating the namespace and path
	 * to the data node defining the struct. The CDT version can be hardcoded since it it target version-specific.
	 * Example for generated URN:
	 * <p>
	 * /oss_cdt/NODE__vDU__1.2.0/urn:3gpp:sa5:_3gpp-nr-nrm-nrcelldu__NRCellDU__pLMNIdList/1.0.0
	 */
	public static ModelInfo getModelInfoForCdtInDeviated(final Target target, final String mocNamespace, final String pathToDataNode) {

		final String modelNamespace = EModelHelper.getTargetVersionSpecificName(target);
		final String modelName = mocNamespace + "__" + pathToDataNode;
		final String modelVersion = Constants.ONE_ZERO_ZERO;

		return new ModelInfo(SchemaConstants.OSS_CDT, modelNamespace, modelName, modelVersion);
	}

	private static boolean cdtsAreTheSame(final TransformerContext context, final ComplexDataTypeDefinition orig, final ComplexDataTypeDefinition other) {

		return PropertyUtils.listsAreEqual(orig.getAttribute(), other.getAttribute(), (o1, o2) -> structMembersAreSame(context, o1, o2));
	}

	private static boolean structMembersAreSame(final TransformerContext context, final ComplexDataTypeAttribute origStructMember, final ComplexDataTypeAttribute deviatedStructMember) {

		if(!origStructMember.getName().equals(deviatedStructMember.getName())) {
			return false;
		}

		if(!origStructMember.getNamespace().equals(deviatedStructMember.getNamespace())) {
			return false;
		}

		if(!EModelHelper.eModelNamedEntityDefinitionPropertiesAreEqual(context, origStructMember, deviatedStructMember)) {
			return false;
		}

		return EModelHelper.eModelAttributeDefinitionPropertiesAreEqual(context, origStructMember, deviatedStructMember);
	}

	private static ComplexRefType getComplexRefForCdt(final ModelInfo cdtModelInfo) {
		final ComplexRefType complexRefType = new ComplexRefType();
		complexRefType.setModelUrn(cdtModelInfo.toImpliedUrn());
		return complexRefType;
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 */
	private static void ensureStableOssCdtGeneration(final ComplexDataTypeDefinition cdtDef) {

		/*
		 * Sort the members, and the meta-data within.
		 */
		Collections.sort(cdtDef.getAttribute(), (o1, o2) -> o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH)));
		cdtDef.getAttribute().forEach(cdtMember -> EModelHelper.ensureStableMetaGeneration(cdtMember.getMeta()));

		EModelHelper.ensureStableMetaGeneration(cdtDef.getMeta());
	}
}
