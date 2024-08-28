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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeMember;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_combined.CombinedEnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_combined.EnumDataType;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YEnum;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.DataTypeHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.Target;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.NamespaceAndIdentifier;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;

/**
 * enumeration-handling is slightly tricky, for the same reason as that discussed inside
 * the IdentityRefGenerator - basically, due to if-feature.
 * <p>
 * There is a crucial difference, however, to identitref: A leaf of type identityref points in effect to
 * an 'external data type' and the code logic (especially in relation to generation of the oss_edt) is all
 * based on the base-identity; a leaf of type 'enumeration' defines its values **inline** as part of the
 * type.
 * <p>
 * Consider this example:
 * <p>
 * leaf my-leaf {<br/>
 *     type enumeration {<br/>
 *         enum RED;<br/>
 *         enum YELLOW;<br/>
 *         enum TRANSPARENT { if-feature supports-transparency; }<br/>
 * }   }<br/>
 *<p>
 * In the BASE variant, these are the members then: {RED, YELLOW, TRANSPARENT}.
 * In the DEVIATED variant, these are the members then: {RED, YELLOW} (suppose the feature is not supported).
 * <p> 
 * To solve this, an OSS EDT EXT could be easily used to remove these members. However, an additional issue
 * is that YANG allows for data types to be deviated-replaced (which is rare, but conceivable). So we could
 * have this here:
 * <p>
 * leaf my-leaf {<br/>
 *     type enumeration {<br/>
 *         enum RED;<br/>
 *         enum YELLOW;<br/>
 *         enum TRANSPARENT { if-feature supports-transparency; }<br/>
 * }   }<br/>
 * <p>
 * deviation /path/to/my-leaf {<br/>
 *     deviate replace {<br/>
 *         type enumeration {<br/>
 *             enum MONDAY;<br/>
 *             enum TUESDAY;<br/>
 * }   }   }<br/>
 * <p>
 * ...in other words, the members are completely different from the original. Again, an OSS EDT EXT would do
 * the job here, but its already getting awkward. But it is also possible to get this here:
 * <p>
 * leaf my-leaf {<br/>
 *     type enumeration {<br/>
 *         enum RED { value 1; };<br/>
 *         enum YELLOW { value 2; }<br/>
 * }   }<br/>
 * <p>
 * deviation /path/to/my-leaf {<br/>
 *     deviate replace {<br/>
 *         type enumeration {<br/>
 *         enum RED { value 654; };<br/>
 *         enum YELLOW { value 941575; }<br/>
 * }   }   }<br/>
 * <p>
 * ...granted, it is very unlikely to be ever encountered, as changing the values is not allowed according to YANG.
 * In this case an OSS EDT EXT would not work at all - the members are the same, but their values are different. And
 * then we can this here:
 * <p>
 * leaf my-leaf {<br/>
 *     type string;<br/>
 * }<br/>
 * <p>
 * deviation /path/to/my-leaf {<br/>
 *     deviate replace {<br/>
 *         type enumeration {<br/>
 *             enum MONDAY;<br/>
 *             enum TUESDAY;<br/>
 * }   }   }<br/>
 * <p>
 * ... which causes yet more problems, because in the ORIGINAL there is no OSS EDT (because it is a string!), hence
 * in the DEVIATED variant (the DPS PT EXT) the data type that has to be generated is a OSS EDT and not a
 * OSS EDT EXT, because there is nothing to extend!
 * <p>
 * Bottom line: If the content of the enum in the DEVIATED-variant differs in <i>any way</i> from the content in the
 * ORIGINAL variant (which, by the way, should be very rare), we simply generate a brand-new OSS EDT, and won't tie
 * ourselves in knots messing around with OSS EDT EXT at all.
 */
public class EnumerationTypeGenerator {
	
	static EnumRefType generateEnumRefType(final TransformerContext context, final YType yType, final Integer positionInUnion, final AbstractStatement leafOrLeafList) {

		/*
		 * The first bit is easy: If we are currently processing the ORIGINAL, then we generate a
		 * OSS EDT and give it an identity based on the leaf/leaf-list that defines it. We can be sure
		 * that the EDT does not exist already, so no need to check. 
		 */
		if(PreProcessor.isInOriginalVariant(leafOrLeafList)) {
			return generateOssEdtForOriginal(context, yType, positionInUnion, leafOrLeafList);
		} else {
			return handleEnumInDeviatingVariant(context, yType, positionInUnion, leafOrLeafList);
		}
	}

	private static EnumRefType handleEnumInDeviatingVariant(final TransformerContext context, final YType yType, final Integer positionInUnion, final AbstractStatement leafOrLeafList) {

		/*
		 * This is more tricky. We are in DEVIATING, so the question is whether the content of the
		 * enumeration in the DEVIATED variant is the *exact* same as the content of the enumeration in
		 * the ORIGINAL variant (assuming it even is an enumeration).
		 * 
		 * First thing to figure out is whether an EDT has been generated for the exact same enum in
		 * original. If so, it should exists in the EModels generated...
		 */
		final ModelInfo owningMoc = ExtractionHelper.getModelInfoForOwningMoc(context, leafOrLeafList);
		final String pathToLeafOrLeafList = ExtractionHelper.getPathFromMocToDataNode(context, leafOrLeafList);
		final ModelInfo originalEdtModelInfo = getModelInfoForEdtInOriginal(owningMoc.getNamespace(), pathToLeafOrLeafList, owningMoc.getVersion().toString(), positionInUnion);

		final EnumDataTypeDefinition originalEdtDef = context.getCreatedEmodels().getGeneratedEModel(originalEdtModelInfo);
		if(originalEdtDef != null) {
			/*
			 * EDT also in the ORIGINAL (that will be the 99% case). Now we need to figure out whether the
			 * DEVIATED variant of the enumeration is the exact same as the ORIGINAL variant. For this, we
			 * generate a fake OSS EDT that we can then use to compare against the original OSS EDT. In the
			 * vast majority of cases, the EDTs will be the same.
			 */
			final ModelInfo fakeModelInfo = new ModelInfo(SchemaConstants.OSS_EDT, "fake-ns", "fake-name", "1.1.1");
			final EnumDataTypeDefinition fakeEdtDef = createOssEdt(context, yType, leafOrLeafList, fakeModelInfo);

			if(edtsAreTheSame(originalEdtDef, fakeEdtDef)) {
				/*
				 * Content is the exact same - hence we can re-use the original OSS EDT. No need to create
				 * another OSS EDT.
				 */
				return getEnumRefForEdt(originalEdtModelInfo);

			} else {
				/*
				 * They are not the same. We generate a brand-new OSS EDT, which must be target version-specific. 
				 */
				return generateOssEdtForDeviatedVariant(context, yType, positionInUnion, leafOrLeafList);
			}
		} else {
			/*
			 * The data type in ORIGINAL is not enumeration, or we are in an 'action' - hence we have to generate the
			 * OSS EDT here now. Note, however, there is a chance that an OSS EDT actually *was* generated as part
			 * of the processing of a 'union'. Further, there is a chance that the EDT was also deviated - meaning
			 * the OSS EDT will be target version-specific.
			 */
			return generateOssEdtForDeviatedVariant(context, yType, positionInUnion, leafOrLeafList);
		}
	}

	/**
	 * This is pretty simple - in the ORIGINAL (BASE or AUGMENTED - makes no difference as an augment
	 * cannot modify a data type) get all the enumeration members and generate a OSS EDT.
	 */
	private static EnumRefType generateOssEdtForOriginal(final TransformerContext context, final YType yType, final Integer positionInUnion, final AbstractStatement leafOrLeafList) {

		final ModelInfo owningMoc = ExtractionHelper.getModelInfoForOwningMoc(context, leafOrLeafList);
		final String pathToDataNode = ExtractionHelper.getPathFromMocToDataNode(context, leafOrLeafList);
		final ModelInfo edtModelInfo = getModelInfoForEdtInOriginal(owningMoc.getNamespace(), pathToDataNode, owningMoc.getVersion().toString(), positionInUnion);

		/*
		 * There is a corner case where a leaf of data type 'leafref' points to a leaf-list of type
		 * 'enumeration'. The navigation done as part of the resolution of the target of the leafref
		 * may result in an attempt to generate the exact same EDT twice. So we do a quick check here
		 * first to see whether the EDT already exists before we actually create it.
		 */
		if(!context.getCreatedEmodels().containsGeneratedEModel(edtModelInfo)) {
			generateOssEdt(context, yType, leafOrLeafList, edtModelInfo);
		}

		return getEnumRefForEdt(edtModelInfo);
	}

	/**
	 * Very similar for doing the same in ORIGINAL - only the model identity is different.
	 */
	private static EnumRefType generateOssEdtForDeviatedVariant(final TransformerContext context, final YType yType, final Integer positionInUnion, final AbstractStatement leafOrLeafList) {

		final ModelInfo owningMoc = ExtractionHelper.getModelInfoForOwningMoc(context, leafOrLeafList);
		final String pathToLeafOrLeafList = ExtractionHelper.getPathFromMocToDataNode(context, leafOrLeafList);
		final ModelInfo edtModelInfo = getModelInfoForEdtInDeviated(context.getTarget(), owningMoc.getNamespace(), pathToLeafOrLeafList, positionInUnion);

		if(!context.getCreatedEmodels().containsGeneratedEModel(edtModelInfo)) {
			generateOssEdt(context, yType, leafOrLeafList, edtModelInfo);
		}

		return getEnumRefForEdt(edtModelInfo);
	}

	private static void generateOssEdt(final TransformerContext context, final YType yType, final AbstractStatement leafOrLeafList, final ModelInfo edtModelInfo) {
		final EnumDataTypeDefinition edt = createOssEdt(context, yType, leafOrLeafList, edtModelInfo);
		context.getCreatedEmodels().addGeneratedEModel(edt);
	}

	private static EnumDataTypeDefinition createOssEdt(final TransformerContext context, final YType yType, final AbstractStatement leafOrLeafList, final ModelInfo edtModelInfo) {

		final String desc = PropertyUtils.getDescription(leafOrLeafList, "Enumeration for attribute '" + PreProcessor.getOriginalStatementIdentifier(leafOrLeafList) + "'.");
		final ModelInfo derivedFrom = CfmMimInfoGenerator.getDerivedFrom(context, leafOrLeafList);

		final EnumDataTypeDefinition edtDef = new EnumDataTypeDefinition();
		EModelHelper.populateEModelHeaderForDerived(edtDef, edtModelInfo, derivedFrom, leafOrLeafList, desc);

		final Map<String, Long> calculatedValuesOfEnums = DataTypeHelper.calculateValuesOfEnums(null, yType, null);

		yType.getEnums().forEach(yEnum -> {
			final EnumDataTypeMember member = createEnumDataTypeMember(yEnum, calculatedValuesOfEnums.get(yEnum.getEnumName()));
			edtDef.getMember().add(member);
		});

		ensureStableOssEdtGeneration(edtDef);

		return edtDef;
	}

	private static EnumDataTypeMember createEnumDataTypeMember(final YEnum yEnum, final Long enumValue) {

		final EnumDataTypeMember member = new EnumDataTypeMember();

		member.setName(PreProcessor.getOriginalStatementIdentifier(yEnum));
		member.setValue(enumValue.intValue());
		member.setDesc(PropertyUtils.getDescription(yEnum, PreProcessor.getOriginalStatementIdentifier(yEnum)));
		member.setLifeCycle(EModelHelper.getLifeCycleType(yEnum));
		member.setLifeCycleDesc(PropertyUtils.getStatusInformation(yEnum));

		return member;
	}

	/**
	 * Creates the ModelInfo that contains the model identity of the generated OSS EDT when generated in original.
	 * The name of the EDT is always the path from the owning MOC to the data node that uses the enumeration as
	 * data type. The namespace and version then must be the same as those of the owning MOC. Example for generated URN:
	 * <p>
	 * /oss_edt/urn:3gpp:sa5:_3gpp-nr-nrm-nrcelldu/NRCellDU__administrativeState/2019.9.3 ('NRCellDU' is the MOC
	 * name and 'administrativeState' is the name of the leaf using enumeration as data type). 
	 */
	public static ModelInfo getModelInfoForEdtInOriginal(final String cleanedMocNamespace, final String pathToDataNode, final String mocXyzVersion, final Integer positionInUnion) {

		final String modelName = positionInUnion == null ? pathToDataNode : (pathToDataNode + "__" + positionInUnion);

		return new ModelInfo(SchemaConstants.OSS_EDT, cleanedMocNamespace, modelName, mocXyzVersion);
	}

	/**
	 * Creates the ModelInfo that contains the model identity of the generated OSS EDT when generated in deviated.
	 * Since it is generated in deviated it implies that it is target version-specific, and that will be used as
	 * EDT namespace. The EDT name has to be made unique, which we achieve by concatenating the namespace and path
	 * to the data node using as data type the enumeration. The EDT version can be hardcoded since it it
	 * target version-specific. Example for generated URN:
	 * <p>
	 * /oss_edt/NODE__vDU__1.2.0/urn:3gpp:sa5:_3gpp-nr-nrm-nrcelldu__NRCellDU__administrativeState/1.0.0 ('NRCellDU' is the MOC
	 * name and 'administrativeState' is the name of the leaf using enumeration as data type). 
	 */
	public static ModelInfo getModelInfoForEdtInDeviated(final Target target, final String cleanedMocNamespace, final String pathToDataNode, final Integer positionInUnion) {

		final String modelNamespace = EModelHelper.getTargetVersionSpecificName(target);
		final String modelName = positionInUnion == null ? (cleanedMocNamespace + "__" + pathToDataNode) : (cleanedMocNamespace + "__" + pathToDataNode + "__" + positionInUnion);
		final String modelVersion = Constants.ONE_ZERO_ZERO;

		return new ModelInfo(SchemaConstants.OSS_EDT, modelNamespace, modelName, modelVersion);
	}

	private static boolean edtsAreTheSame(final EnumDataTypeDefinition orig, final EnumDataTypeDefinition other) {

		return PropertyUtils.listsAreEqual(orig.getMember(), other.getMember(),
				/*
				 * What counts in terms of comparison is only the members (which will be ordered);
				 * the general model blurb we don't care about.
				 */
				(o1, o2) -> o1.getValue() == o2.getValue() && o1.getName().equals(o2.getName())
		);
	}

	private static EnumRefType getEnumRefForEdt(final ModelInfo edtModelInfo) {
		final EnumRefType enumRefType = new EnumRefType();
		enumRefType.setModelUrn(edtModelInfo.toImpliedUrn());
		return enumRefType;
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 */
	private static void ensureStableOssEdtGeneration(final EnumDataTypeDefinition edtDef) {

		/*
		 * Sort the members, and the meta-data within.
		 */
		Collections.sort(edtDef.getMember(), (o1, o2) -> o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH)));
		edtDef.getMember().forEach(edtMember -> EModelHelper.ensureStableMetaGeneration(edtMember.getMeta()));

		EModelHelper.ensureStableMetaGeneration(edtDef.getMeta());
	}

	/**
	 * Aggregates all created OSS EDT models into OSS EDT COMBINED models. This will be done even if
	 * there is only a single EDT in a given namespace/version.
	 */
	public static void createCombinedEdts(final TransformerContext context) {

		final List<EnumDataTypeDefinition> allGeneratedEdts = context.getCreatedEmodels().getGeneratedEModels().stream()
				.filter(emodel -> emodel instanceof EnumDataTypeDefinition)
				.map(emodel -> (EnumDataTypeDefinition) emodel)
				.collect(Collectors.toList());

		/*
		 * Combined EDTs work by aggregating all EDTs that have the same namespace and version. So in a first
		 * step we need to figure out what these ns/v combinations are.
		 */
		final Set<NamespaceAndIdentifier> uniqueNamespacesAndVersions = allGeneratedEdts.stream()
				.map(edtDef -> new NamespaceAndIdentifier(edtDef.getNs(), edtDef.getVersion()))
				.collect(Collectors.toSet());

		uniqueNamespacesAndVersions.forEach(nsai -> createCombinedEdt(context, nsai, allGeneratedEdts));

		/*
		 * Need to remove all OSS EDTs now, don't need them anymore.
		 */
		context.getCreatedEmodels().removeGeneratedEModels(emodel -> emodel.getClass().equals(EnumDataTypeDefinition.class));
	}

	private static void createCombinedEdt(final TransformerContext context, final NamespaceAndIdentifier forNsai, final List<EnumDataTypeDefinition> allGeneratedEdts) {

		final List<EnumDataTypeDefinition> edtsForNsai = allGeneratedEdts.stream()
				.filter(edtDef -> edtDef.getNs().equals(forNsai.getNamespace()))
				.filter(edtDef -> edtDef.getVersion().equals(forNsai.getIdentifier()))
				.collect(Collectors.toList());

		final CombinedEnumDataTypeDefinition combinedEnumDataTypeDefinition = new CombinedEnumDataTypeDefinition();
		context.getCreatedEmodels().addGeneratedEModel(combinedEnumDataTypeDefinition);

		final ModelInfo modelInfo = getModelInfoForCombinedOssEdt(forNsai.getNamespace(), forNsai.getIdentifier());
		EModelHelper.populateEModelHeaderForDesigned(combinedEnumDataTypeDefinition, modelInfo, "Combined OSS EDT generated by YANG Transformer2");

		edtsForNsai.forEach(edtDef -> {
			final EnumDataType enumDataType = new EnumDataType();
			combinedEnumDataTypeDefinition.getEnumDataType().add(enumDataType);

			enumDataType.setName(edtDef.getName());
			enumDataType.setLifeCycle(edtDef.getLifeCycle());
			enumDataType.setLifeCycleDesc(edtDef.getLifeCycleDesc());
			enumDataType.setDesc(edtDef.getDesc());
			enumDataType.getMember().addAll(edtDef.getMember());	// retains stability
			enumDataType.getMeta().addAll(edtDef.getMeta());		// retains stability
		});

		ensureStableOssEdtCombinedGeneration(combinedEnumDataTypeDefinition);
	}

	/**
	 * Creates the ModelInfo that contains the model identity of a OSS EDT COMBINED. The namespace is always 'global',
	 * and the name is equal to the namespace of the EDTs it contains. The version of the combined likewise is the
	 * same as the version of the EDTs it contains. Example for generated URN:
	 * <p>
	 * /oss_edt_combined/global/urn:3gpp:sa5:_3gpp-nr-nrm-nrcelldu/2019.9.3
	 */
	public static ModelInfo getModelInfoForCombinedOssEdt(final String edtsNamespace, final String edtsVersion) {
		return new ModelInfo(SchemaConstants.OSS_EDT_COMBINED, SchemaConstants.GLOBAL_MODEL_NAMESPACE, edtsNamespace, edtsVersion);
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 */
	private static void ensureStableOssEdtCombinedGeneration(final CombinedEnumDataTypeDefinition combinedEdtDef) {
		/*
		 * Only sort the list of types. No need to sort within the enum types (was previously done).
		 */
		Collections.sort(combinedEdtDef.getEnumDataType(), (o1, o2) -> o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH)));
	}
}
