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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.PathExpressionConditionType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.RequiresActiveChoiceCaseType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.RequiresType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceCaseAttributeType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceCasePrimaryTypeType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceCaseType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YCase;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YChoice;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YWhen;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.StringHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils.EqualsEvaluator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ThreeGPPSupport;

public class PrimaryTypeChoiceAndRequiresGenerator {

	public static RequiresType getRequires(final AbstractStatement dataNode) {

		final RequiresType requiresType = new RequiresType();

		/*
		 * Check whether this data node (MOC or attribute) is part of a choice, and if so get the
		 * relevant information (case name, choice name, moc name).
		 */
		final ChoiceCaseMemberInfo choiceCaseMemberInfo = getChoiceCaseMemberInfo(dataNode);
		if(choiceCaseMemberInfo != null) {
			final RequiresActiveChoiceCaseType requiresActiveChoiceCase = new RequiresActiveChoiceCaseType();
			requiresType.setActiveChoiceCase(requiresActiveChoiceCase);

			requiresActiveChoiceCase.setPrimaryTypeUrn(PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(choiceCaseMemberInfo.owningMoc).toImpliedUrn());
			requiresActiveChoiceCase.setChoice(choiceCaseMemberInfo.choiceName);
			requiresActiveChoiceCase.setCase(choiceCaseMemberInfo.caseName);
		}

		requiresType.setPathExpressionCondition(getPathExpressionCondition(dataNode));

		return (requiresType.getActiveChoiceCase() != null || requiresType.getPathExpressionCondition() != null) ? requiresType : null;
	}

	private static PathExpressionConditionType getPathExpressionCondition(final AbstractStatement dataNode) {

		/*
		 * The 'when' condition(s) will never be evaluated in ENM by any component. We generate it purely
		 * for documentation purposes so that it can be displayed to the user (who may or may not find
		 * this useful). Hence, we won't bother populating the prefix/namespace information, as this only
		 * blows up the model unnecessarily.
		 */

		final List<YWhen> whens = dataNode.getChildren(CY.STMT_WHEN);
		if(whens.isEmpty()) {
			return null;
		}

		final List<String> xPaths = whens.stream().map(YWhen::getXpathExpression).collect(Collectors.toList());
		Collections.sort(xPaths);		// ensure stable generation
		final String concatenatedXpaths = xPaths.size() == 1 ? xPaths.get(0) : StringHelper.toString(xPaths, null, null, " and ", "(", ")");

		final PathExpressionConditionType pathExpressionCondition = new PathExpressionConditionType();
		pathExpressionCondition.setCondition(concatenatedXpaths);

		return pathExpressionCondition;
	}

	/**
	 * Generates the choices within a MOC into the EModel. 
	 */
	public static void handleMocChoices(final TransformerContext context, final AbstractStatement mocInDeviated, final PrimaryTypeDefinition ptDef, final PrimaryTypeExtensionDefinition ptExtDef) {

		final AbstractStatement mocInAugmented = PreProcessor.getAugmentedVariantMoc(mocInDeviated);

		/*
		 * There is considerable complexity here, which arises from the fact that in YANG it is possible
		 * to augment into a 'choice', and into a 'case'. Consider this (pseudo-yang) example:
		 * 
		 * container ns1:my-container {
		 *   choice ns1:interface-choice {
		 *     case ns1:mac-interface {
		 *       leaf ns1:mac-address;
		 *     }
		 *     case ns1:ip-interface {
		 *       leaf ns1:ip-address;
		 *       leaf ns2:supportedIpv6;		<- in ns2; the 'leaf' was augmented into the 'case'
		 *     }
		 *     case ns3:atm-interface {			<- in ns3; the 'case' itself was augmented into the 'choice'
		 *       leaf ns3:atm-address;
		 *     }
		 *   }
		 * }
		 * 
		 * The easy bit first: In the DEVIATED variant, all three cases, with all leafs, would be generated
		 * into the choice.
		 * 
		 * But in AUGMENTED the choice can only have the first two cases ('mac-interface', 'ip-interface') as they
		 * were part of ORIGINAL as can be seen by the namespace. And for case 'ip-interface', only the first
		 * leaf ('ip-address') should be included, as the second leaf ('supportedIpv6') does not exist in
		 * ORIGINAL, as can again be seen from the namespace.
		 * 
		 * So during generation of AUGMENTED a check on the YAM must be done at all time. Of course, during processing
		 * of either of the variants it may happen that a choice has then effectively become empty, meaning it should
		 * not be generated into the EModel.
		 * 
		 * (BTW, it is of course also possible that a complete 'choice' gets augmented-in. The same trick with
		 * the YAM applies, of course).
		 */

		/*
		 * For both AUGMENTED and DEVIATED we generate the choices. Note that for AUGMENTED we must check for the
		 * originating YAM and whether the choice is part of a submodule when generating the choice and contents;
		 * when generating for DEVIATED we can safely ignore the YAM origin and submodule (as all of this would go
		 * into the extension, which is target-version-specific).
		 */

		final List<AbstractStatement> choicesForMocInAugmented = getChoicesForMoc(mocInAugmented);
		final Map<String, ChoiceType> choicesGeneratedForAugmented = new HashMap<>();

		choicesForMocInAugmented.forEach(choiceInAugmented -> {
			final boolean choiceDefinedInSubmodule = PropertyUtils.definedInSubmodule(choiceInAugmented);
			final boolean mocAndChoiceDefinedInSameYam = PropertyUtils.definedInSameYam(choiceInAugmented, mocInAugmented);
			if(mocAndChoiceDefinedInSameYam && !choiceDefinedInSubmodule) {
				final ChoiceType choice = generateChoice(mocInAugmented, (YChoice) choiceInAugmented, true);
				if(choice != null) {
					choicesGeneratedForAugmented.put(choice.getName(), choice);
				}
			}
		});

		final List<AbstractStatement> choicesForMocInDeviated = getChoicesForMoc(mocInDeviated);
		final Map<String, ChoiceType> choicesGeneratedForDeviated = new HashMap<>();

		choicesForMocInDeviated.forEach(choiceInDeviated -> {
			final ChoiceType choice = generateChoice(mocInDeviated, (YChoice) choiceInDeviated, false);
			if(choice != null) {
				choicesGeneratedForDeviated.put(choice.getName(), choice);
			}
		});

		/*
		 * We now do a diff between the choices that have been generated for ORIGINAL and DEVIATED.
		 */
		final Set<String> allChoiceNames = new HashSet<>(choicesGeneratedForAugmented.keySet());
		allChoiceNames.addAll(choicesGeneratedForDeviated.keySet());

		allChoiceNames.forEach(choiceName -> {

			final ChoiceType inAugmented = choicesGeneratedForAugmented.get(choiceName);
			final ChoiceType inDeviating = choicesGeneratedForDeviated.get(choiceName);

			if(inDeviating != null) {
				if(inAugmented != null) {
					/*
					 * Exists in both variants. They could be the same -> Generate into the MOC --- OR ---
					 * they could differ -> Generate into the MOC and REPLACE via extension.
					 */
					ptDef.getChoice().add(inAugmented);
					replaceChoiceIfNecessary(ptExtDef, inAugmented, inDeviating);
				}
				else {
					/*
					 * Exists only in DEVIATING. -> ADD via extension.
					 */
					addOrReplaceChoiceThroughExtension(ptExtDef, inDeviating);
				}
			} else {
				/*
				 * Exists only in AUGMENTED, i.e. choice has been completely deviated-out -> Generate into
				 * the MOC and REMOVE via extension.
				 */
				ptDef.getChoice().add(inAugmented);
				ptExtDef.getChoiceHandling().getRemoveChoice().add(inAugmented.getName());

				context.logInfo(Constants.TEXT_REMOVED_DEVIATED_OUT + " Removed choice '" + choiceName + "' from MOC '" + ExtractionHelper.getHumanReadablePathToSchemaNode(mocInAugmented) + "' via extension as it does not exist in the deviated-variant.");
			}
		});
	}

	/**
	 * Returns all 'choice' statements under a given MOC, taking into account that the MOC may be a 3GPP MOC.
	 */
	private static List<AbstractStatement> getChoicesForMoc(final AbstractStatement moc) {

		/*
		 * Have never seen it, but it is possible that a 'choice' will be used in a 3GPP attributes container.
		 */
		if(PreProcessor.is3gppMoc(moc)) {
			final AbstractStatement attributesContainer = ThreeGPPSupport.get3gppAttributesContainer(moc);
			return attributesContainer != null ? attributesContainer.getChildren(CY.STMT_CHOICE) : Collections.emptyList();
		} else {
			return moc.getChildren(CY.STMT_CHOICE);
		}
	}

	/**
	 * Generates a choice. This method will return null if during processing it transpires that the choice
	 * is in effect "empty" (perhaps due to YAM-origin / submodule filtering).
	 */
	private static ChoiceType generateChoice(final AbstractStatement owningMoc, final YChoice yChoice, final boolean inOriginalVariant) {

		final ChoiceType choiceType = new ChoiceType();
		choiceType.setName(yChoice.getStatementIdentifier());

		final boolean mandatoryChoice = yChoice.getMandatory() != null && yChoice.getMandatory().isMandatoryTrue();
		choiceType.setMandatory(mandatoryChoice);

		for(final YCase yCase : yChoice.getCases()) {
			if(inOriginalVariant && !PropertyUtils.definedInSameYam(yChoice, yCase)) {
				/*
				 * If we are in ORIGINAL the 'case' must be located in the same YAM as the 'choice'. There is no
				 * need to check whether the 'case' sits in a submodule - we have already made this check of the
				 * 'choice' statement. If the 'case' sits in a submodule, and has been augmented-in, then the YAMs
				 * will be different, so checking on YAM sameness will do the job.
				 */
				continue;
			}

			final ChoiceCaseType choiceCaseType = new ChoiceCaseType();
			choiceCaseType.setName(yCase.getStatementIdentifier());

			final List<AbstractStatement> caseDataNodes = ExtractionHelper.getChildDataNodes(yCase, ExtractionHelper::isContainerOrListOrLeafOrLeafList);

			for(final AbstractStatement dataNodeUnderCase : caseDataNodes) {
				if(inOriginalVariant && !PropertyUtils.definedInSameYam(yChoice, dataNodeUnderCase)) {
					/*
					 * If we are in ORIGINAL the data node must be located in the same YAM as the 'choice'.
					 */
					continue;
				}

				final Serializable choiceCasePrimaryTypeOrAttribute = getChoiceCasePrimaryTypeOrAttribute(dataNodeUnderCase);
				choiceCaseType.getAttributeOrPrimaryType().add(choiceCasePrimaryTypeOrAttribute);

				markDataNodeAsChoiceCaseMember(dataNodeUnderCase, new ChoiceCaseMemberInfo(owningMoc, choiceType.getName(), choiceCaseType.getName()));
			}

			/*
			 * We only add the 'case' if there is actually something inside of it (which might not be
			 * the case due to YAM filtering, or perhaps there was never any content in the 'case' in the
			 * first place, or perhaps some data node got deviated-out).
			 */
			if(!choiceCaseType.getAttributeOrPrimaryType().isEmpty()) {
				choiceType.getCase().add(choiceCaseType);
			}
		}

		if(choiceType.getCase().isEmpty()) {
			/*
			 * No cases left after processing. Quite unlikely, but possible. We must return null here, as
			 * the XSD enforces at least a single case per choice, hence generation of the empty choice
			 * would fail.
			 */
			return null;
		}

		/*
		 * In a moment we may be comparing the contents of choices generated into original and deviated.
		 * We need to make sure that they are stable, otherwise comparison may fail.
		 */
		Collections.sort(choiceType.getCase(), (o1, o2) -> o1.getName().compareTo(o2.getName()));
		choiceType.getCase().forEach(choiceCaseType -> 

		Collections.sort(choiceCaseType.getAttributeOrPrimaryType(), (o1, o2) -> {
			final String o1Name = o1 instanceof ChoiceCasePrimaryTypeType ? ((ChoiceCasePrimaryTypeType) o1).getPrimaryTypeUrn() : ((ChoiceCaseAttributeType) o1).getName();
			final String o2Name = o2 instanceof ChoiceCasePrimaryTypeType ? ((ChoiceCasePrimaryTypeType) o2).getPrimaryTypeUrn() : ((ChoiceCaseAttributeType) o2).getName();
			return o1Name.compareTo(o2Name);
		}));

		return choiceType;
	}

	private static final String YT_APP_DATA_IS_CHOICE_CASE_MEMBER = "YT_APP_DATA_IS_CHOICE_CASE_MEMBER";

	private static class ChoiceCaseMemberInfo {
		public final AbstractStatement owningMoc;
		public final String choiceName;
		public final String caseName;

		ChoiceCaseMemberInfo(final AbstractStatement owningMoc, final String choiceName, final String caseName) {
			this.owningMoc = Objects.requireNonNull(owningMoc);
			this.choiceName = Objects.requireNonNull(choiceName);
			this.caseName = Objects.requireNonNull(caseName);
		}
	}

	private static void markDataNodeAsChoiceCaseMember(final AbstractStatement dataNodeUnderChoiceCase, final ChoiceCaseMemberInfo info) {
		dataNodeUnderChoiceCase.setCustomAppData(YT_APP_DATA_IS_CHOICE_CASE_MEMBER, info);
	}

	private static ChoiceCaseMemberInfo getChoiceCaseMemberInfo(final AbstractStatement dataNode) {
		return dataNode.getCustomAppData(YT_APP_DATA_IS_CHOICE_CASE_MEMBER);
	}

	public static boolean hasChoiceCaseMemberInfo(final AbstractStatement dataNode) {
		return dataNode.hasCustomAppData(YT_APP_DATA_IS_CHOICE_CASE_MEMBER);
	}

	private static Serializable getChoiceCasePrimaryTypeOrAttribute(final AbstractStatement dataNodeUnderCase) {

		/*
		 * Under the 'case' can be containers/lists (= MOCs) and leafs/leaf-lists (= attributes). The EModel
		 * expects different types for those, so we need to check.
		 */
		if(PreProcessor.isMoc(dataNodeUnderCase)) {

			final ChoiceCasePrimaryTypeType choiceCasePrimaryTypeType = new ChoiceCasePrimaryTypeType();
			choiceCasePrimaryTypeType.setPrimaryTypeUrn(PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(dataNodeUnderCase).toImpliedUrn());

			return choiceCasePrimaryTypeType;

		} else {

			final String statementIdentifier = PreProcessor.getRenamedStatementIdentifier(dataNodeUnderCase);

			final ChoiceCaseAttributeType choiceCaseAttributeType = new ChoiceCaseAttributeType();
			choiceCaseAttributeType.setName(statementIdentifier);

			return choiceCaseAttributeType;
		}
	}

	private static final EqualsEvaluator<Serializable> CHOICE_CASE_MEMBER_EQUALS_EVALUATOR = (orig, deviated) -> {
		final String origName = orig instanceof ChoiceCasePrimaryTypeType ? ((ChoiceCasePrimaryTypeType) orig).getPrimaryTypeUrn() : ((ChoiceCaseAttributeType) orig).getName();
		final String deviatedName = deviated instanceof ChoiceCasePrimaryTypeType ? ((ChoiceCasePrimaryTypeType) deviated).getPrimaryTypeUrn() : ((ChoiceCaseAttributeType) deviated).getName();
		return origName.equals(deviatedName);
	};

	private static final EqualsEvaluator<ChoiceCaseType> CHOICE_CASE_TYPE_EQUALS_EVALUATOR = (origChoiceCase, deviatedChoiceCase) -> {
		if(!origChoiceCase.getName().equals(deviatedChoiceCase.getName())) {
			return false;
		}
		return PropertyUtils.listsAreEqual(origChoiceCase.getAttributeOrPrimaryType(), deviatedChoiceCase.getAttributeOrPrimaryType(), CHOICE_CASE_MEMBER_EQUALS_EVALUATOR);
	};

	private static void replaceChoiceIfNecessary(final PrimaryTypeExtensionDefinition pted, final ChoiceType origChoice, final ChoiceType deviatedChoice) {

		if(!PropertyUtils.listsAreEqual(origChoice.getCase(), deviatedChoice.getCase(), CHOICE_CASE_TYPE_EQUALS_EVALUATOR)) {
			addOrReplaceChoiceThroughExtension(pted, deviatedChoice);
		}
	}

	private static void addOrReplaceChoiceThroughExtension(final PrimaryTypeExtensionDefinition pted, final ChoiceType choice) {
		pted.getChoiceHandling().getReplaceChoice().add(choice);
	}
}
