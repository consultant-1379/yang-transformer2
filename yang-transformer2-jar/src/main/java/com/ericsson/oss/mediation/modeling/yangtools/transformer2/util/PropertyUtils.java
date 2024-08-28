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

package com.ericsson.oss.mediation.modeling.yangtools.transformer2.util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MinMaxValue;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.resolvers.TypeResolver;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.CERI;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EriCustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriDependencies;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriDisturbances;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriPrecondition;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriSideEffects;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriStatusInformation;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriTakesEffect;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriUpdatedDescription;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.threegpp.C3GPP;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YDescription;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YMandatory;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YMaxElements;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YMinElements;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YStatus;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YUnits;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.DataTypeHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.util.NamespaceModuleIdentifier;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;

public class PropertyUtils {

	/**
	 * Returns whether the container or list will be system-created (i.e. cannot be created by the client). Defined as:
	 * (3gpp-yang-extension:only-system-created) OR (ericsson-yang-extension:is-system-created) OR (NP-container AND markNPContainersAsSystemCreated == true)
	 */
	public static boolean isSystemCreated(final TransformerContext context, final AbstractStatement stmt) {

		if(stmt.hasAtLeastOneChildOf(C3GPP.THREEGPP_COMMON_YANG_EXTENSIONS__ONLY_SYSTEM_CREATED)) {
			return true;
		}

		if(stmt.hasAtLeastOneChildOf(CERI.ERICSSON_YANG_EXTENSIONS__IS_SYSTEM_CREATED)) {
			return true;
		}

		return isNPContainer(stmt) && context.generateNPcontainersAsSystemCreated();
	}

	/**
	 * Returns whether the leaf or leaf-list is restricted, i.e. can only be written-to once. Defined as:
	 * (ericsson-yang-extension:restricted) OR (3gpp-yang-extensions:inVariant)
	 */
	public static boolean isRestricted(final AbstractStatement stmt) {

		return stmt.hasAtLeastOneChildOf(CERI.ERICSSON_YANG_EXTENSIONS__RESTRICTED) || stmt.hasAtLeastOneChildOf(C3GPP.THREEGPP_COMMON_YANG_EXTENSIONS__IN_VARIANT);
	}

	/**
	 * Returns whether the data node is configurable by a client (i.e. can be modified by ENM). Defined as:
	 * (config == true) AND (effective static-data == false)
	 */
	public static boolean isConfigurable(final AbstractStatement stmt) {

		return stmt.isEffectiveConfigTrue() && !EriCustomProcessor.isStatementEffectivelyStaticDataTrue(stmt);
	}

	/**
	 * Returns whether the statement is an NP container.
	 */
	public static boolean isNPContainer(final AbstractStatement stmt) {
		return stmt.is(CY.STMT_CONTAINER) && !stmt.hasAtLeastOneChildOf(CY.STMT_PRESENCE);
	}

	/**
	 * Returns the YANG description of the supplied statement, or the value of the Ericsson extension
	 * 'updated-description' that may sit under the supplied statement. Will return the default value 
	 * if neither are present.
	 */
	public static String getDescription(final AbstractStatement stmt, final String defaultValue) {
		final YEriUpdatedDescription yEriDescription = (YEriUpdatedDescription) stmt.getChild(CERI.ERICSSON_YANG_EXTENSIONS__UPDATED_DESCRIPTION);
		final String eriDescription = yEriDescription != null ? getTrimmedOrigOrEmpty(yEriDescription.getUpdatedDescription()) : "";
		if(!eriDescription.isEmpty()) {
			return eriDescription;
		}

		final YDescription yDescription = stmt.getDescription();
		final String yangDescription = yDescription != null ? getTrimmedOrigOrEmpty(yDescription.getValue()) : "";
		if(!yangDescription.isEmpty()) {
			return yangDescription;
		}

		return defaultValue;
	}

	/**
	 * Returns the value of the Ericsson extension 'dependencies' that may sit under the supplied
	 * statement. Will return null if the extension does not exist.
	 */
	public static String getDependencies(final AbstractStatement stmt) {
		final YEriDependencies dependencies = (YEriDependencies) stmt.getChild(CERI.ERICSSON_YANG_EXTENSIONS__DEPENDENCIES);
		return dependencies == null ? null : dependencies.getDependencies();
	}

	/**
	 * Returns the value of the Ericsson extension 'precondition' that may sit under the supplied
	 * statement. Will return null if the extension does not exist.
	 */
	public static String getPrecondition(final AbstractStatement stmt) {
		final YEriPrecondition precondition = (YEriPrecondition) stmt.getChild(CERI.ERICSSON_YANG_EXTENSIONS__PRECONDITION);
		return precondition == null ? null : precondition.getPrecondition();
	}

	/**
	 * Returns the value of the Ericsson extension 'side-effects' that may sit under the supplied
	 * statement. Will return null if the extension does not exist.
	 */
	public static String getSideEffects(final AbstractStatement stmt) {
		final YEriSideEffects sideEffects = (YEriSideEffects) stmt.getChild(CERI.ERICSSON_YANG_EXTENSIONS__SIDE_EFFECTS);
		return sideEffects == null ? null : sideEffects.getSideEffects();
	}

	/**
	 * Returns the value of the Ericsson extension 'takes-effect' that may sit under the supplied
	 * statement. Will return null if the extension does not exist.
	 */
	public static String getTakesEffect(final AbstractStatement stmt) {
		final YEriTakesEffect takesEffect = (YEriTakesEffect) stmt.getChild(CERI.ERICSSON_YANG_EXTENSIONS__TAKES_EFFECT);
		return takesEffect == null ? null : takesEffect.getTakesEffect();
	}

	/**
	 * Returns the value of the Ericsson extension 'disturbances' that may sit under the supplied
	 * statement. Will return null if the extension does not exist.
	 */
	public static String getDisturbances(final AbstractStatement stmt) {
		final YEriDisturbances disturbances = (YEriDisturbances) stmt.getChild(CERI.ERICSSON_YANG_EXTENSIONS__DISTURBANCES);
		return disturbances == null ? null : disturbances.getDisturbances();
	}

	/**
	 * Returns the value of the Ericsson extension 'status-information' that may sit under the supplied
	 * statement. Will return null if the extension does not exist.
	 */
	public static String getStatusInformation(final AbstractStatement stmt) {
		final YStatus yStatus = stmt.getChild(CY.STMT_STATUS);
		final YEriStatusInformation statusInformation = yStatus == null ? null : (YEriStatusInformation) yStatus.getChild(CERI.ERICSSON_YANG_EXTENSIONS__STATUS_INFORMATION);
		return statusInformation == null ? null : statusInformation.getStatusInformation();
	}

	/**
	 * Returns the value of the YANG units statement that may sit under the supplied
	 * statement. Will return null if the units statement does not exist.
	 */
	public static String getUnits(final AbstractStatement stmt) {
		final YUnits units = (YUnits) stmt.getChild(CY.STMT_UNITS);
		return units == null ? null : units.getValue();
	}

	/**
	 * Returns whether it is mandatory to set this attribute.
	 * 
	 * @param dataNode typically a leaf or leaf-list denoting a scalar attribute, but could also be
	 * 		a container or list denoting a struct / struct-sequence.
	 */
	public static boolean isDataNodeMandatory(final AbstractStatement dataNode) {

		if(ExtractionHelper.hasDefaultValue(dataNode)) {
			/*
			 * A leaf/leaf-list with default value(s), hence cannot be mandatory.
			 */
			return false;
		}

		if(dataNode.is(CY.STMT_LEAF) && dataNode.hasAtLeastOneChildOf(CY.STMT_MANDATORY) && ((YMandatory) dataNode.getChild(CY.STMT_MANDATORY)).isMandatoryTrue()) {
			/*
			 * A mandatory leaf
			 */
			return true;
		}

		if(ExtractionHelper.isLeafListOrList(dataNode) && dataNode.hasAtLeastOneChildOf(CY.STMT_MIN_ELEMENTS)) {
			final YMinElements minElements = dataNode.getChild(CY.STMT_MIN_ELEMENTS);
			if(minElements.getMinValue() > 0) {
				/*
				 * A leaf-list or list ("wrap"!) that must have at least a single entry. 
				 */
				return true;
			}
		}

		return false;
	}

	private static final NamespaceModuleIdentifier ERICSSON_YANG_TYPES_SYSTEM_CREATED_BOOLEAN_TYPEDEF =
			new NamespaceModuleIdentifier("urn:rdns:com:ericsson:oammodel:ericsson-yang-types", "ericsson-yang-types", "system-created-boolean");

	/**
	 * Returns whether the attribute is used to denote at runtime that the instance of the
	 * MOC has been system-created.
	 * <p/>
	 * Note this is different from the Ericsson Yang extension "is-system-created"; that extension
	 * denotes that *all* instances of a given MOC are always system-created.
	 */
	public static boolean isAttributeDenotingInstanceIsSystemCreated(final AbstractStatement dataNode) {

		final List<YType> types = collectTypes(dataNode);
		for(final YType yType : types) {
			if(isDerivedFromTypedef(yType, ERICSSON_YANG_TYPES_SYSTEM_CREATED_BOOLEAN_TYPEDEF)) {
				return true;
			}
		}

		return false;
	}

	private static final NamespaceModuleIdentifier IANA_CRYPT_HASH_TYPEDEF =
			new NamespaceModuleIdentifier("urn:ietf:params:xml:ns:yang:iana-crypt-hash", "iana-crypt-hash", "crypt-hash");

	/**
	 * Returns whether the attribute is sensitive.
	 */
	public static boolean isSensitive(final AbstractStatement dataNode) {

		if(dataNode.hasAtLeastOneChildOf(CERI.ERICSSON_YANG_EXTENSIONS__PERSONAL_DATA) || dataNode.hasAtLeastOneChildOf(CERI.ERICSSON_YANG_EXTENSIONS__IS_PASSPHRASE)) {
			/*
			 * These Ericsson extensions are used to denote personal data or passwords, and
			 * as such they should be considered sensitive alright.
			 */
			return true;
		}

		/*
		 * There is a data type defined by IANA crypt-hash module that is used to hold passwords.
		 * The password may be in cleartext or in hashed form. We check the data type of the data
		 * node to see if it derives from that derived type, and if so we also consider it sensitive.
		 */
		final List<YType> types = collectTypes(dataNode);
		boolean derivedFromIanaCryptHash = false;

		for(final YType yType : types) {
			if(isDerivedFromTypedef(yType, IANA_CRYPT_HASH_TYPEDEF)){
				derivedFromIanaCryptHash = true;
				break;
			}
		}

		if(derivedFromIanaCryptHash) {
			return true;
		}

		/*
		 * Possibly check for other data types or extensions here in the future should these
		 * ever be defined in IETF / 3GPP extensions to YANG...
		 */
		return false;
	}

	private static List<YType> collectTypes(final AbstractStatement dataNode) {

		final YType typeUnderDataNode = dataNode.getChild(CY.STMT_TYPE);
		if(typeUnderDataNode == null) {
			/*
			 * This method can conceivably be invoked on a container / list that represents
			 * a CDT, in which case there will be no type statement.
			 */
			return Collections.emptyList();
		}

		if(DataTypeHelper.isUnionType(typeUnderDataNode.getDataType())) {
			/*
			 * Return the types within the union.
			 */
			return typeUnderDataNode.getTypes();
		}

		/*
		 * Not a union type, so just return the type.
		 */
		return Collections.singletonList(typeUnderDataNode);
	}

	public static boolean isDerivedFromTypedef(final YType yType, final NamespaceModuleIdentifier typedefNsmi) {
		/*
		 * If the type has been derived then we have the typedef stack, and we can look at the original type.
		 */
		final List<NamespaceModuleIdentifier> typedefStack = TypeResolver.getTypedefStack(yType);
		return (!typedefStack.isEmpty() && typedefStack.get(0).equals(typedefNsmi));
	}

	/**
	 * Returns whether both statements have been defined in the very same YAM.
	 */
	public static boolean definedInSameYam(final AbstractStatement stmt1, final AbstractStatement stmt2) {
		/*
		 * Must work on the DOM, of course, as the schema tree is merged.
		 */
		return stmt1.getDomElement().getDocumentRoot() == stmt2.getDomElement().getDocumentRoot();
	}

	/**
	 * Returns whether the statement has been defined in a submodule.
	 */
	public static boolean definedInSubmodule(final AbstractStatement stmt) {
		/*
		 * Must navigate via the DOM as the schema tree is merged.
		 */
		return stmt.getDomElement().getDocumentRoot().getYangModel().getYangModelRoot().isSubmodule();
	}

	private static final EqualsEvaluator<Object> OBJECT_EQUALS_EVALUATOR = (o1, o2) -> o1.equals(o2);

	public static boolean valuesAreEqual(final Object val1, final Object val2) {
		return valuesAreEqual(val1, val2, OBJECT_EQUALS_EVALUATOR);
	}

	/**
	 * Returns whether two values are equal. They are equal if either both values are null, or the supplied
	 * evaluator returns true when comparing these.
	 */
	public static <T extends Object> boolean valuesAreEqual(final T val1, final T val2, final EqualsEvaluator<T> equalsEvaluator) {

		if(val1 == null) {
			return val2 == null;
		}

		if(val2 == null) {
			return false;
		}

		return equalsEvaluator.areEqual(val1, val2);
	}

	/**
	 * Returns whether two lists are equal. They are equal if either both lists are null, or the supplied
	 * evaluator returns true when comparing the members of each.
	 */
	public static <T extends Object> boolean listsAreEqual(final List<T> list1, final List<T> list2, final EqualsEvaluator<T> memberEqualsEvaluator) {

		if(list1 == null) {
			return list2 == null;
		}
		if(list2 == null) {
			return false;
		}

		if(list1.size() != list2.size()) {
			return false;
		}
		for(int i=0; i<list1.size(); ++i) {
			if(!memberEqualsEvaluator.areEqual(list1.get(i), list2.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * A helper interface since the generated JAXB classes do not define a useful equals()
	 * method, and frequently the content of these must be compared.
	 */
	public interface EqualsEvaluator<T> {
		boolean areEqual(T o1, T o2);
	}

	public static String requireNotNullNotEmpty(final String string) {
		if(Objects.requireNonNull(string).isEmpty()) {
			throw new IllegalArgumentException();
		}

		return string;
	}

	public static String getTrimmedOrigOrEmpty(final String orig) {
		return orig == null ? "" : orig.trim();
	}

	/**
	 * Given a statement, returns the YANG 'type' for that statement. This method exists to handle the
	 * complexity that arises from the 3GPP "wrapping" on non-unique sequences.
	 */
	public static YType getType(final AbstractStatement dataNode) {

		if(PreProcessor.is3gppNonUniqueSequence(dataNode)) {
			return ThreeGPPSupport.getWrappedDataNode(dataNode).getChild(CY.STMT_TYPE);
		} else {
			return dataNode.getChild(CY.STMT_TYPE);
		}
	}

	public static MinMaxValue getMinElementsValue(final AbstractStatement dataNode) {

		long min = 0;

		if(PreProcessor.isNainMoc(dataNode)) {
			/*
			 * Do nothing. On the node it will be a system-created container. Hence min will always be zero.
			 */
		} else if(PropertyUtils.isNPContainer(dataNode)) {
			/*
			 * Whether the NP container is mandatory or not depends very much on its subtree. If there are
			 * descendants in the subtree that are themselves mandatory, then the NP container is also mandatory.
			 */
			min = hasMandatoryDescendants(dataNode) ? 1 : 0;

		} else {

			final YMinElements minElements = (YMinElements) dataNode.getChild(CY.STMT_MIN_ELEMENTS);
			if(minElements != null) {
				min = minElements.getMinValue();
			}
		}

		MinMaxValue minMaxValue = null;

		if(min > 0) {
			minMaxValue = new MinMaxValue();
			minMaxValue.setValue(min);
		}

		return minMaxValue;
	}

	public static MinMaxValue getMaxElementsValue(final AbstractStatement dataNode) {

		long max = 0;

		if(PreProcessor.isNainMoc(dataNode)) {
			/*
			 * Do nothing. On the node it will be a system-created container, but in ENM it will
			 * be translated to a "list" without constraints.
			 */
		} else if(dataNode.is(CY.STMT_CONTAINER)) {

			max = 1;

		} else {

			final YMaxElements maxElements = (YMaxElements) dataNode.getChild(CY.STMT_MAX_ELEMENTS);
			if(maxElements != null && !maxElements.isUnbounded()) {
				max = maxElements.getMaxValue();
			}
		}

		MinMaxValue minMaxValue = null;

		if(max > 0) {
			minMaxValue = new MinMaxValue();
			minMaxValue.setValue(max);
		}

		return minMaxValue;
	}

	/**
	 * Returns whether the data node has any mandatory descendant data nodes.
	 */
	private static boolean hasMandatoryDescendants(final AbstractStatement dataNode) {

		final List<AbstractStatement> children = ExtractionHelper.getChildDataNodes(dataNode, ExtractionHelper::isContainerOrListOrLeafOrLeafList);
		for(final AbstractStatement child : children) {

			if(isNPContainer(child)) {
				if(hasMandatoryDescendants(child)) {
					return true;
				}
			} else if (child.is(CY.STMT_LEAF) || child.is(CY.STMT_LEAF_LIST)) {
				if(isDataNodeMandatory(child)) {
					return true;
				}
			} else if (child.is(CY.STMT_LIST)) {
				final MinMaxValue minValue = getMinElementsValue(child);
				if(minValue != null) {
					return true;
				}
			}
		}

		return false;
	}
}
