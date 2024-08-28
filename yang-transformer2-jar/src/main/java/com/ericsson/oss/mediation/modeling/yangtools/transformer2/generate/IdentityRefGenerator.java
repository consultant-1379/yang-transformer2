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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractSingleDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeMember;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeMemberForRemovalWithNamespace;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeRemovalType;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.PrefixResolver;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ModulePrefixResolver;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.IdentityRegistry;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YBase;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YIdentity;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.YangIdentity;
import com.ericsson.oss.mediation.modeling.yangtools.parser.util.QNameHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.Target;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;

/**
 * Identity-ref handling is so complex to warrant a separate generation class. For the following discussion,
 * suppose we have these two modules defining identities:
 * <p>
 * module A {<br/>
 * &nbsp;&nbsp;identity A1;<br/>
 * &nbsp;&nbsp;identity A2 { base A1; };<br/>
 * }<br/>
 * <p>
 * module B {<br/>
 * &nbsp;&nbsp;identity B3 { base A:A1; }<br/>
 * &nbsp;&nbsp;identity B4 { base A:A1; }<br/>
 * }<br/>
 * <p>
 * As can be seen, the identities in module B use A1 as base. The totality of all identites for base A1
 * are thus: {A1, A2, B3, B4}. However, there is a pitfall here: B3 and B4 are only part of this set of
 * identities if module B is processed at the same time as module A!
 * <p>
 * From module A's point of view, the totality of modules for base A1 are {A1, A2} - and everything else
 * is in effect an extension to it (hinting at the implementation below). Adding to the complexity is the
 * fact that the 'if-feature' statement is allowed under the 'identity' statement. Consider this example:
 * <p>
 * module A {<br/>
 * &nbsp;&nbsp;identity A1;<br/>
 * &nbsp;&nbsp;identity A2 { base A1; if-feature some-feature; }<br/>
 * }<br/>
 * <p>
 * module B {<br/>
 * &nbsp;&nbsp;identity B3 { base A:A1; }<br/>
 * &nbsp;&nbsp;identity B4 { base A:A1; if-feature some-other-feature; }<br/>
 * }<br/>
 * <p>
 * Suppose that statements with 'if-feature' shall be removed (option into transformer) - then this in
 * effect results in the totality of all identities for base A1 being: {A1, B3}.
 * <p>
 * To cater for all of these, the code logic adopted will do the following:
 * <ul>
 * <li>For the identity in question, generate a regular oss_edt. The members of the oss_edt are all derived
 * identities in the <b>same</b> YAM. Do this in the original (= base/augmenting) variant.</li>
 * <li>For the same identity, establish all derived identities, and do a diff against what was previously
 * produced, and use oss_edt_ext to add/remove members.</li>
 * </ul>
 * In the above example <i>with</i> the 'if-feature' handling, this would result in:
 * <p>
 * oss_edt with members {A1, A2}<br/>
 * oss_edt_ext with: REMOVE {A2}, ADD {B3} 
 * <p>
 * In the above example <i>without</i> the 'if-feature' handling, this would result in:
 * <p>
 * oss_edt with members {A1, A2}<br/>
 * oss_edt_ext with: ADD {B3, B4} 
 * <p>
 * The important bit is that the oss_edt will always be generated the same way and with the same content,
 * and any diff to the oss_edt will be in a target version-specific oss_edt_ext.
 */
public class IdentityRefGenerator {

	/**
	 * For a leaf or leaf-list of type 'identityref' will return a EnumRefType pointing to a (generated)
	 * OSS_EDT that contains the derived identities from the referenced base(s). 
	 */
	static AbstractSingleDataType generateIdentityRefType(final TransformerContext context, final AbstractStatement leafOrLeafList, final YType yType, final Integer positionInUnion) {

		/*
		 * Get all valid bases first.
		 */
		final List<YBase> bases = resolveBases(context, yType, leafOrLeafList);

		/*
		 * If there are no bases left, then there is no point whatsoever generating an empty EDT. The
		 * attribute would be completely useless to the user. In this exceptional scenario we generate
		 * a StringType so that at least the attribute can be synced up (and possibly even written-to,
		 * if the user knows the syntax of identity values.)
		 */
		if(bases.isEmpty()) {
			context.logWarn(Constants.TEXT_GENERATED_AS_STRING + " No identity bases left for '" + ExtractionHelper.getHumanReadablePathToSchemaNode(leafOrLeafList) + "', will generate identityref attribute as 'StringType' instead.");
			return new StringType();
		}

		/*
		 * Generate the ModelInfo for the EDT first. It is well possible that the EDT was already generated
		 * previously (as there was another identityref attribute with the exact same set of bases), in which
		 * case we should not have to generate it again, of course. 
		 */
		final ModelInfo edtModelInfo = calculateEdtModelInfo(context, bases, leafOrLeafList, positionInUnion);

		/*
		 * This here will be the EnumRefType that we will return at some stage. It points to the EDT we
		 * are about to generate (or which has previously been generated).
		 */
		final EnumRefType enumRefType = new EnumRefType();
		enumRefType.setModelUrn(edtModelInfo.toImpliedUrn());

		if(context.getCreatedEmodels().containsGeneratedEModel(edtModelInfo)) {
			/*
			 * Already previously created, don't create again.
			 */
			return enumRefType;
		}

		/*
		 * Ok - we now want to fetch all identities declared in the same YAM as the base identity (if there is only
		 * one base) and ignore the rest for now. If the base sits in a submodule we will force empty generation, as
		 * the contents of the submodule can change between revisions.
		 *
		 * If we have multiple bases, the oss_edt will always stay empty.
		 */
		final Set<YangIdentity> identitiesDefinedInSameYamAsBaseIdentity = new HashSet<>();
		final boolean oneBaseOnly = bases.size() == 1;

		final YangIdentity singleBase = oneBaseOnly ? getYangIdentityForYBase(bases.get(0)) : null;
		final AbstractStatement baseToUseForDerivedFrom = oneBaseOnly ? findIdentityStatementInSchema(singleBase, context.getAugmentedSchema()) : leafOrLeafList;

		if(oneBaseOnly && !PropertyUtils.definedInSubmodule(baseToUseForDerivedFrom)) {
			final Set<YangIdentity> allDerivedIdentitiesInBase = collectDerivedIdentities(bases, context.getBaseSchema().getIdentityRegistry());
			allDerivedIdentitiesInBase.stream()
					.filter(yangIdentity -> {
						final AbstractStatement yIdentity = findIdentityStatementInSchema(yangIdentity, context.getAugmentedSchema());
						return PropertyUtils.definedInSameYam(baseToUseForDerivedFrom, yIdentity);
					})
					.forEach(yangIdentity -> identitiesDefinedInSameYamAsBaseIdentity.add(yangIdentity));
		}

		/*
		 * We can already generate the oss_edt now, even if it is empty, so let's do it!
		 */
		generateOssEdtForIdentities(context, edtModelInfo, baseToUseForDerivedFrom, identitiesDefinedInSameYamAsBaseIdentity);

		/*
		 * Now comes the extension model. This will be target version-specific, and will contain the
		 * "diff" to the identities in the oss_edt.
		 * 
		 * We need to get the identities now based on the deviated-version, which will then handle the
		 * issue with the 'if-feature' statement nicely. 
		 */
		final Set<YangIdentity> allIdentitiesInDeviated = collectDerivedIdentities(bases, context.getDeviatedSchema().getIdentityRegistry());

		/*
		 * The diff between base and deviated can be easily ascertained by some simple Set-operations:
		 */
		final Set<YangIdentity> toBeAddedInExtension = new HashSet<>(allIdentitiesInDeviated);
		toBeAddedInExtension.removeAll(identitiesDefinedInSameYamAsBaseIdentity);

		final Set<YangIdentity> toBeRemovedInExtension = new HashSet<>(identitiesDefinedInSameYamAsBaseIdentity);
		toBeRemovedInExtension.removeAll(allIdentitiesInDeviated);

		/*
		 * We might not actually have to create an extension - often, all derived identities sit in the same
		 * YAM and do not have an if-feature. In this case, they are all listed in the oss_edt already and no
		 * need for the extension.
		 */
		if(!toBeAddedInExtension.isEmpty() || !toBeRemovedInExtension.isEmpty()) {
			final ModelInfo edtExtModelInfo = getModelInfoForExtension(context.getTarget(), edtModelInfo.getNamespace(), edtModelInfo.getName());
			generateOssEdtExtensionForIdentities(context, edtExtModelInfo, edtModelInfo, toBeAddedInExtension, toBeRemovedInExtension);
		}

		return enumRefType;
	}

	/**
	 * We are pre-processing the bases to make sure they actually exist. It is possible that an identity used as a
	 * base has been removed due to an if-feature without the data node using it as base having been removed (this
	 * is really a model fault that we are covering for - it should be very rare that this happens).
	 * <p>
	 * Since the schema nodes are only removed due to if-feature inside the DEVIATED-variant, we must use the
	 * Identity Registry from the DEVIATED-variant.
	 */
	private static List<YBase> resolveBases(final TransformerContext context, final YType yType, final AbstractStatement leafOrLeafList) {

		final Set<YangIdentity> allIdentitiesInDeviatedSchema = context.getDeviatedSchema().getIdentityRegistry().getIdentities();

		final List<YBase> bases = yType.getBases();
		return bases.stream()
				.filter(yBase -> {
					final YangIdentity baseIdentity = getYangIdentityForYBase(yBase);
					final boolean baseExistsInDeviatedSchema = allIdentitiesInDeviatedSchema.contains(baseIdentity);
					if(!baseExistsInDeviatedSchema) {
						context.logWarn(Constants.TEXT_IGNORED_UNRESOLVABLE + " Base '" + yBase.getValue() + "', part of '" + leafOrLeafList.getDomElement().getSimplifiedPath() + "' not found in deviated-variant of the schema.");
					}
					return baseExistsInDeviatedSchema;
				})
				.collect(Collectors.toList());
	}

	/**
	 * Returns the ModelInfo of the EDT that will be generated for the identity base(s).
	 * <p>
	 * Where there is only a single base (which is the 99% case), the EDT ModelInfo is quite simply the namespace
	 * and name of the identity, and the xyz version of the module.
	 * <p>
	 * Where there are multiple bases the EDT has to be made unique by using the data node path, in the exact same
	 * way how this is done when an EDT for an enumeration is created.
	 */
	private static ModelInfo calculateEdtModelInfo(final TransformerContext context, final List<YBase> bases, final AbstractStatement leafOrLeafList, final Integer positionInUnion) {

		if(bases.size() == 1) {

			final YangIdentity baseIdentity = getYangIdentityForYBase(bases.get(0));

			final YangModel moduleDefiningIdentity = ExtractionHelper.findModuleDeclaringOriginalNamespace(context.getAugmentedSchema(), baseIdentity.getIdentityNamespace());
			final String xyzVersion = PreProcessor.getXyzVersionForYangModelRoot(moduleDefiningIdentity.getYangModelRoot());
			final String baseIdentityNamespace = PreProcessor.getNamespaceCleanedOfNodeAppInstanceName(moduleDefiningIdentity.getYangModelRoot());

			return getModelInfoForIdentityWithSingleBase(baseIdentityNamespace, baseIdentity.getIdentityName(), xyzVersion);

		} else {

			final ModelInfo owningMoc = ExtractionHelper.getModelInfoForOwningMoc(context, leafOrLeafList);
			final String pathToLeafOrLeafList = ExtractionHelper.getPathFromMocToDataNode(context, leafOrLeafList);

			return getModelInfoForIdentityWithMultipleBases(owningMoc.getNamespace(), pathToLeafOrLeafList, owningMoc.getVersion().toString(), positionInUnion);
		}
	}

	/**
	 * Creates the ModelInfo that contains the model identity of the generated EDT for an identity-ref having a
	 * single base. Having a single base (which is the 99% case) allows us to use the namespace + name of the
	 * base (the combination of which will be unique) as identity. Example for generated URN:
	 * <p>
	 * /oss_edt/urn:ietf:params:xml:ns:yang:ietf-system/radius-authentication-type/2014.8.6
	 */
	public static ModelInfo getModelInfoForIdentityWithSingleBase(final String cleanedNamespaceOfModuleDeclaringBaseIdentity, final String baseIdentityName, final String xyzVersionOfModuleDeclaringIdentity) {
		return new ModelInfo(SchemaConstants.OSS_EDT, cleanedNamespaceOfModuleDeclaringBaseIdentity, baseIdentityName, xyzVersionOfModuleDeclaringIdentity);
	}

	/**
	 * Creates the ModelInfo that contains the model identity of the generated EDT for an identity-ref having multiple
	 * bases. Since there are multiple bases, conceivably in different namespaces, the combination of namespace(s) + name(s)
	 * cannot be used. The only alternative is to use the path information from the MOC to the leaf using the bases,
	 * implying we then need the namespace and version of the MOC as well. Example for generated URN:
	 * <p>
	 * /oss_edt/urn:ietf:params:xml:ns:yang:ietf-system/authentication__authentication-type/2014.8.6 ('authentication' is
	 * the MOC name and 'authentication-type' is the name of the leaf using multiple bases)
	 */
	public static ModelInfo getModelInfoForIdentityWithMultipleBases(final String cleanedMocNamespace, final String pathToDataNode, final String mocXyzVersion, final Integer positionInUnion) {
		final String modelName = positionInUnion == null ? pathToDataNode : (pathToDataNode + "__" + positionInUnion);
		return new ModelInfo(SchemaConstants.OSS_EDT, cleanedMocNamespace, modelName, mocXyzVersion);
	}

	/**
	 * Creates the ModelInfo for the generated EDT EXT model for an identity. An extension model is always generated in
	 * the EDT itself cannot hold all identities derived from the base. Since it is an extension model, the namespace
	 * will be target version-specific as usual. The name of the extension model must include the namespace and name of
	 * the model that is being extended. Examples for generated URN:
	 * <p>
	 * /oss_edt_ext/NODE__EPG-OI__3.12/urn:ietf:params:xml:ns:yang:ietf-system__radius-authentication-type__ext/1.0.0
	 * /oss_edt_ext/NODE__EPG-OI__3.12/urn:ietf:params:xml:ns:yang:ietf-system__authentication__authentication-type__ext/1.0.0
	 */
	public static ModelInfo getModelInfoForExtension(final Target target, final String namespaceOfExtendedEdtModel, final String nameOfExtendedEdtModel) {
		final String namespace = EModelHelper.getExtensionModelNamespace(target);
		final String modelName = namespaceOfExtendedEdtModel + "__" + nameOfExtendedEdtModel + "__ext";
		final String version = EModelHelper.getModelInfoVersionForExtension();

		return new ModelInfo(SchemaConstants.OSS_EDT_EXT, namespace, modelName, version);
	}

	private static void generateOssEdtForIdentities(final TransformerContext context, final ModelInfo edtModelInfo, final AbstractStatement base, final Set<YangIdentity> identities) {

		final ModelInfo derivedFrom = CfmMimInfoGenerator.getDerivedFrom(context, base);

		final EnumDataTypeDefinition edtDef = new EnumDataTypeDefinition();
		context.getCreatedEmodels().addGeneratedEModel(edtDef);

		EModelHelper.populateEModelHeaderForDerived(edtDef, edtModelInfo, derivedFrom, null, "Generated enumeration for YANG identity");
		EModelHelper.attachMeta(edtDef.getMeta(), Constants.META_DERIVED_FROM_IDENTITIES);

		identities.forEach(yangIdentity -> {
			final YIdentity yIdentity = findIdentityStatementInSchema(yangIdentity, context.getAugmentedSchema());

			final EnumDataTypeMember member = createEnumDataTypeMember(yangIdentity, yIdentity);
			edtDef.getMember().add(member);
		});

		ensureStableOssEdtGeneration(edtDef);
	}

	private static void generateOssEdtExtensionForIdentities(final TransformerContext context, final ModelInfo edtExtModelInfo, final ModelInfo edtModelInfo, final Set<YangIdentity> toBeAdded, final Set<YangIdentity> toBeRemoved) {

		final String desc = "Extension for '" + edtModelInfo.toImpliedUrn() + "' to handle augmentations/deviations";

		final EnumDataTypeExtensionDefinition edtExt = new EnumDataTypeExtensionDefinition();
		context.getCreatedEmodels().addGeneratedEModel(edtExt);

		EModelHelper.populateEModelExtensionHeader(context, edtExt, edtExtModelInfo, edtModelInfo.toImpliedUrn(), desc);
		EModelHelper.attachMeta(edtExt.getMeta(), Constants.META_DERIVED_FROM_IDENTITIES);

		/*
		 * The extension is target-version specific.
		 */
		edtExt.setRequiresTargetType(EModelHelper.getRequiresTargetType(context));

		if(!toBeAdded.isEmpty()) {
			edtExt.setEnumDataTypeExtension(new EnumDataTypeExtensionType());
			toBeAdded.forEach(yangIdentity -> {
				final YIdentity yIdentity = findIdentityStatementInSchema(yangIdentity, context.getDeviatedSchema());

				final EnumDataTypeMember member = createEnumDataTypeMember(yangIdentity, yIdentity);
				edtExt.getEnumDataTypeExtension().getMember().add(member);
			});
		}

		if(!toBeRemoved.isEmpty()) {
			edtExt.setEnumDataTypeRemoval(new EnumDataTypeRemovalType());
			toBeRemoved.forEach(yangIdentity -> {
				final EnumDataTypeMemberForRemovalWithNamespace member = new EnumDataTypeMemberForRemovalWithNamespace();
				edtExt.getEnumDataTypeRemoval().getMemberWithNamespace().add(member);

				member.setNamespace(yangIdentity.getIdentityNamespace());
				member.setName(yangIdentity.getIdentityName());
			});
		}

		ensureStableOssEdtExtGeneration(edtExt);
	}

	private static EnumDataTypeMember createEnumDataTypeMember(final YangIdentity yangIdentity, final YIdentity yIdentity) {

		final EnumDataTypeMember member = new EnumDataTypeMember();

		member.setNamespace(yangIdentity.getIdentityNamespace());
		member.setName(yangIdentity.getIdentityName());
		member.setValue(getMemberValueForIdentity(yangIdentity));

		member.setDesc(PropertyUtils.getDescription(yIdentity, PreProcessor.getOriginalStatementIdentifier(yIdentity)));

		member.setLifeCycle(EModelHelper.getLifeCycleType(yIdentity));
		member.setLifeCycleDesc(PropertyUtils.getStatusInformation(yIdentity));

		/*
		 * Attach meta information about the prefix used by the module that defines the identity.
		 */
		final String prefix = yIdentity.getYangModelRoot().getOwningYangModelRoot().getModule().getPrefix().getPrefix();
		EModelHelper.attachMeta(member.getMeta(), Constants.META_ORIG_MODULE_PREFIX, prefix);

		return member;
	}

	/**
	 * Computes the member value for an identity. Uses hashing on the namespace and name, as really there is no
	 * other way how this can be handled such to avoid collisions.
	 */
	public static int getMemberValueForIdentity(final YangIdentity yangIdentity) {
		/*
		 * We give it a bit of a salt at the end - this way it is more likely that
		 * we get a nice spread of values.
		 */
		return Math.abs((yangIdentity.getIdentityNamespace() + "-" + yangIdentity.getIdentityName() + "#@#@").hashCode());
	}

	/**
	 * Given a YangIdentity object, finds and returns the corresponding 'identity' statement in the schema.
	 */
	private static YIdentity findIdentityStatementInSchema(final YangIdentity yangIdentity, final Schema owningSchema) {

		return owningSchema.getModuleRegistry().getAllYangModels().stream()
			.filter(ymi -> ymi.getYangModelRoot().isModule())
			.filter(ymi -> ymi.getModuleIdentity().getModuleName().equals(yangIdentity.getModuleName()))
			.findAny().get()
			.getYangModelRoot().getModule().getIdentities().stream()
			.filter(yIdentity -> PreProcessor.getOriginalStatementIdentifier(yIdentity).equals(yangIdentity.getIdentityName()))
			.findAny().get();
	}

	/**
	 * Given an identity, recursively fetches all derived identities.
	 */
	private static Set<YangIdentity> collectDerivedIdentities(final List<YBase> bases, final IdentityRegistry identityRegistry) {

		final Set<YangIdentity> identityAndDerivedIdentities = new HashSet<>();

		for(final YBase yBase : bases) {
			final YangIdentity baseIdentity = getYangIdentityForYBase(yBase);
			identityAndDerivedIdentities.addAll(identityRegistry.getIdentityAndDerivedIdentitiesRecursively(baseIdentity));
		}

		return identityAndDerivedIdentities;
	}

	/**
	 * Given a 'base' statement, resolves this to a YangIdentity object.
	 */
	private static YangIdentity getYangIdentityForYBase(final YBase yBase) {

		final ModulePrefixResolver prefixResolver = yBase.getPrefixResolver();

		/*
		 * Form the YANG Identity object. Must resolve owning namespace and module name for that from the
		 * prefix (if any) used by the base.
		 */
		final String basePrefix = QNameHelper.extractPrefix(yBase.getValue());
		final String baseName = QNameHelper.extractName(yBase.getValue());

		final String baseNamespace = PrefixResolver.NO_PREFIX.equals(basePrefix) ? prefixResolver.getDefaultNamespaceUri() : prefixResolver.resolveNamespaceUri(basePrefix);
		if(baseNamespace == null) {
			/*
			 * Unresolvable prefix, error in the model. Really, should have been caught during validation
			 * and the leaf/leaf-list cleaned up. We cater for it just in case...
			 */
			return null;
		}

		final YangModel moduleDefiningBase = ExtractionHelper.findModuleDeclaringOriginalNamespace(yBase.getYangModelRoot().getOwningSchema(), baseNamespace);
		final String baseModule = moduleDefiningBase.getModuleIdentity().getModuleName();

		return new YangIdentity(baseNamespace, baseModule, baseName);
	}

	/**
	 * Sort various parts of the generated content to make sure the order of elements is stable to
	 * facilitate model-hashing by the modeling toolchain.
	 */
	private static void ensureStableOssEdtGeneration(final EnumDataTypeDefinition edtDef) {
		/*
		 * Sort the members, and the meta-data within.
		 */
		Collections.sort(edtDef.getMember(), (o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
		edtDef.getMember().forEach(edtMember -> {
			EModelHelper.ensureStableMetaGeneration(edtMember.getMeta());
		});

		EModelHelper.ensureStableMetaGeneration(edtDef.getMeta());
	}

	private static void ensureStableOssEdtExtGeneration(final EnumDataTypeExtensionDefinition edtExtDef) {
		/*
		 * Sort added and removed members (if any).
		 */
		if(edtExtDef.getEnumDataTypeExtension() != null) {
			Collections.sort(edtExtDef.getEnumDataTypeExtension().getMember(), (o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
			edtExtDef.getEnumDataTypeExtension().getMember().forEach(edtMember -> {
				EModelHelper.ensureStableMetaGeneration(edtMember.getMeta());
			});
		}

		if(edtExtDef.getEnumDataTypeRemoval() != null) {
			Collections.sort(edtExtDef.getEnumDataTypeRemoval().getMemberWithNamespace(), (o1, o2) -> {
				final String o1Name = o1.getNamespace() + "/" + o1.getName();
				final String o2Name = o2.getNamespace() + "/" + o2.getName();
				return o1Name.compareTo(o2Name);
			});
		}
	}
}
