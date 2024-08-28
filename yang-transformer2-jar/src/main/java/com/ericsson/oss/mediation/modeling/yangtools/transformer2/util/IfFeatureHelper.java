package com.ericsson.oss.mediation.modeling.yangtools.transformer2.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ModuleIdentity;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YIfFeature;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YIfFeature.Token;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.YangFeature;
import com.ericsson.oss.mediation.modeling.yangtools.parser.util.QNameHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.YangLibrary;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;

/**
 * A utility class that helps with if-feature handling.
 */
public class IfFeatureHelper {

	public static Set<YangFeature> getEnabledFeaturesFromYangLibrary(final TransformerContext context) {

		final Set<YangFeature> result = new HashSet<>();

		final YangLibrary yangLibrary = context.getTopLevelYangLibrary();
		if(yangLibrary != null) {
			yangLibrary.getRunningDatastore().getAllModules().forEach(module -> {
				module.getFeatures().forEach(featureName -> result.add(new YangFeature(module.getNamespace(), module.getName(), featureName)));
			});
		}

		return result;
	}

	/**
	 * Determines whether all if-feature statements under a schema node are satisfied, i.e. evaluate to TRUE.
	 */
	public static boolean ifFeaturesUnderStatementAreSatisfied(final AbstractStatement statement, final Schema schema, final Set<YangFeature> enabledFeatures) {

		final List<YIfFeature> ifFeatures = statement.getChildren(CY.STMT_IF_FEATURE);

		/*
		 * There can be multiple if-feature statements under a statement. According to the RFC, they all must be
		 * true for the overall result to be true.
		 */
		for (final YIfFeature ifFeature : ifFeatures) {
			if (!ifFeatureIsSatisfied(ifFeature, schema, enabledFeatures)) {
				return false;
			}
		}

		return true;
	}

	private static boolean ifFeatureIsSatisfied(final YIfFeature ifFeature, final Schema schema, final Set<YangFeature> enabledFeatures) {

		final List<Token> tokens = ifFeature.getTokens();

		if (tokens.size() == 1) {
			/*
			 * Simple if-feature statement, just referring to a single feature. For example:
			 * 
			 * leaf leafXyz {
			 *   if-feature my-feature;
			 * }
			 * 
			 * The vast majority of if-feature statements will be simple.
			 */
			return isFeatureSupported(ifFeature, tokens.get(0).name, schema, enabledFeatures);
		}

		/*
		 * More than one token in the if-feature, meaning the statement is more complex. For example:
		 * 
		 * leaf leafXyz {
		 *   if-feature "my-feature AND other-feature";
		 * }
		 * 
		 * We need to apply boolean logic to the expression. We build a logical expression first, and then
		 * simplify it bit-by-bit.
		 */
		final List<Operand> expression = buildExpressionFromTokens(ifFeature, tokens, schema, enabledFeatures);

		final Operand result = simplify(expression);
		return result == Operand.TRUE;
	}

	private static boolean isFeatureSupported(final YIfFeature ifFeature, final String possiblyPrefixedFeatureName, final Schema schema, final Set<YangFeature> enabledFeatures) {

		if(enabledFeatures.isEmpty()) {
			return false;
		}

		final String featureName = QNameHelper.extractName(possiblyPrefixedFeatureName);
		String namespace = null;

		if (QNameHelper.hasPrefix(possiblyPrefixedFeatureName)) {
			final String prefix = QNameHelper.extractPrefix(possiblyPrefixedFeatureName);
			final ModuleIdentity moduleIdentityForPrefix = ifFeature.getPrefixResolver().getModuleForPrefix(prefix);
			if (moduleIdentityForPrefix == null) {
				/*
				 * Error in the model, should have been found and fixed by validation before transformer was ever invoked.
				 */
				return false;
			}

			final YangModel yangModel = schema.getModuleRegistry().find(moduleIdentityForPrefix);
			namespace = yangModel.getYangModelRoot().getNamespace();
		} else {
			/*
			 * No prefix, so refers to same module in which it was defined.
			 */
			namespace = ifFeature.getDomElement().getYangModel().getYangModelRoot().getNamespace();
		}

		final String moduleName = schema.getModuleNamespaceResolver().getModuleForNamespace(namespace);

		final YangFeature yangFeature = new YangFeature(namespace, moduleName, featureName);
		return enabledFeatures.contains(yangFeature);
	}

	// -------------------------- All the expression handling stuff here -------------------------------

	public enum Operand {
		NOT, OR, AND, TRUE, FALSE, LEFT_PARENTHESIS, RIGHT_PARENTHESIS;
	}

	/**
	 * From the tokens that the parser has generated for the if-feature statement, we build a logical expression.
	 * For example, the statement:
	 * 
	 * if-feature "my-feature AND other-feature";
	 * 
	 * ... will be translated to: (assuming "my-feature" is enabled, and "other-feature" is not enabled)
	 * 
	 * TRUE AND FALSE
	 */
	private static List<Operand> buildExpressionFromTokens(final YIfFeature ifFeature, final List<Token> tokens, final Schema schema, final Set<YangFeature> enabledFeatures) {

		final List<Operand> result = new ArrayList<>(tokens.size());

		for (final Token token : tokens) {

			switch (token.type) {
			case NOT:
				result.add(Operand.NOT);
				break;
			case AND:
				result.add(Operand.AND);
				break;
			case OR:
				result.add(Operand.OR);
				break;
			case LEFT_PARENTHESIS:
				result.add(Operand.LEFT_PARENTHESIS);
				break;
			case RIGHT_PARENTHESIS:
				result.add(Operand.RIGHT_PARENTHESIS);
				break;
			default:
				if (isFeatureSupported(ifFeature, token.name, schema, enabledFeatures)) {
					result.add(Operand.TRUE);
				} else {
					result.add(Operand.FALSE);
				}
			}
		}

		return result;
	}

	/**
	 * We simplify the logical expression until only a single value is left over, which will be either TRUE or FALSE.
	 */
	private static Operand simplify(final List<Operand> input) {

		final List<Operand> result = new ArrayList<>(input);

		/*
		 * The RFC gives as precedence: parenthesis, not, and, or. So that is the order in which we resolve things.
		 * 
		 * To simplify the list, we simplify all sub-expressions (those in parenthesis) first.
		 */
		while (result.contains(Operand.LEFT_PARENTHESIS)) {
			/*
			 * Find first occurrence of LEFT, and last occurrence or RIGHT. The contents of
			 * that will be simplified and replaces the sub-expression.
			 */
			final int indexOfLeft = result.indexOf(Operand.LEFT_PARENTHESIS);
			int parenthesisCount = 1;
			int indexOfRight = indexOfLeft;

			while(parenthesisCount > 0) {
				indexOfRight++;
				if(result.get(indexOfRight) == Operand.LEFT_PARENTHESIS) {
					parenthesisCount++;
				} else if (result.get(indexOfRight) == Operand.RIGHT_PARENTHESIS) {
					parenthesisCount--;
				}
			}

			/*
			 * Get the content that is between the parenthesis. That content will be
			 * simplified in a moment and replaces the sub-expression.
			 */
			final List<Operand> sublist = new ArrayList<>(result.subList(indexOfLeft + 1, indexOfRight));	// deep copy required!
			final Operand replaceSubExpressionWith = simplify(sublist);

			/*
			 * Remove the sub-expression and replace with the result of the simplification.
			 */
			final int nrOfElementsToRemove = indexOfRight - indexOfLeft + 1;
			for (int i = 0; i < nrOfElementsToRemove; ++i) {
				result.remove(indexOfLeft);
			}
			result.add(indexOfLeft, replaceSubExpressionWith);
		}

		/*
		 * Next precedence is "NOT"
		 */
		while (result.contains(Operand.NOT)) {

			final int indexOfNot = result.indexOf(Operand.NOT);

			/*
			 * We simply remove the 'NOT', and flip the next element.
			 */
			result.remove(indexOfNot);
			result.set(indexOfNot, result.get(indexOfNot) == Operand.TRUE ? Operand.FALSE : Operand.TRUE);
		}

		/*
		 * Next precedence is "AND"
		 */
		while (result.contains(Operand.AND)) {

			final int indexOfAnd = result.indexOf(Operand.AND);

			/*
			 * We logically AND together the elements before and after. Then we remove all
			 * three elements and replace with the ANDed result.
			 */
			final boolean resultOfAnd = (result.get(indexOfAnd - 1) == Operand.TRUE) && (result.get(indexOfAnd + 1) == Operand.TRUE);

			result.remove(indexOfAnd - 1);
			result.remove(indexOfAnd - 1);
			result.remove(indexOfAnd - 1);
			result.add(indexOfAnd - 1, resultOfAnd ? Operand.TRUE : Operand.FALSE);
		}

		/*
		 * And finally "OR"
		 */
		while (result.contains(Operand.OR)) {

			final int indexOfOr = result.indexOf(Operand.OR);

			/*
			 * We logically OR together the elements before and after. Then we remove all
			 * three elements and replace with the ORed result.
			 */
			final boolean resultOfOr = (result.get(indexOfOr - 1) == Operand.TRUE)
					|| (result.get(indexOfOr + 1) == Operand.TRUE);

			result.remove(indexOfOr - 1);
			result.remove(indexOfOr - 1);
			result.remove(indexOfOr - 1);
			result.add(indexOfOr - 1, resultOfOr ? Operand.TRUE : Operand.FALSE);
		}

		/*
		 * At this point, there can only be a single entry left in the list.
		 */

		return result.get(0);
	}
}
