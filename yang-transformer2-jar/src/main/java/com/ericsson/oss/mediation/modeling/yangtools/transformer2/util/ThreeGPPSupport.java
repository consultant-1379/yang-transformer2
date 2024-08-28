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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YContainer;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YKey;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YLeaf;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YList;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.DataTypeHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.DataTypeHelper.YangDataType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;

public class ThreeGPPSupport {

	public static final String NAMESPACE_3GPP_MANAGED_ELEMENT_MODULE = "urn:3gpp:sa5:_3gpp-common-managed-element";

	/**
	 * Returns whether the statement represents the 3GPP-defined 'ManagedElement' MOC.
	 */
	public static boolean is3gppManagedElement(final AbstractStatement stmt) {
		return NAMESPACE_3GPP_MANAGED_ELEMENT_MODULE.equals(PreProcessor.getOriginalNamespace(stmt)) && Constants.MANAGEDELEMENT.equals(PreProcessor.getOriginalStatementIdentifier(stmt));
	}

	/**
	 * Returns whether the statement represents the 'attributes' container that holds the attributes of a 3GPP MOC.
	 */
	public static boolean is3gppAttributesContainer(final AbstractStatement stmt) {
		return stmt.is(CY.STMT_CONTAINER) && PreProcessor.getOriginalStatementIdentifier(stmt).equals("attributes");
	}

	/**
	 * Returns the 3GPP 'attributes' container, if any, under the given MOC (some MOCs do not define any
	 * attributes, in which case the 'attributes' container does not exist).
	 */
	public static YContainer get3gppAttributesContainer(final AbstractStatement moc) {
		final List<AbstractStatement> found = ExtractionHelper.getChildDataNodes(moc, ThreeGPPSupport::is3gppAttributesContainer);
		return found.isEmpty() ? null : (YContainer) found.get(0);
	}

	/**
	 * Denotes whether the supplied statement, which must be a container or a list, represents a 3GPP MOC.
	 * <p>
	 * The rules for this are as follows:
	 * <p>
	 * A YANG 'container' is considered to represent a 3GPP MOC if: The general 3GPP naming conventions for
	 * MOCs are adhered to — AND — the container does not have any leafs / leaf-lists.
	 * <p>
	 * A YANG 'list' is considered to represent a 3GPP IOC if: The general 3GPP naming conventions for MOCs
	 * are adhered to — AND — the list has exactly a single leaf, which is the key of the list.
	 */
	public static boolean is3gppIoc(final TransformerContext context, final AbstractStatement stmt) {

		/*
		 * Quick check to see whether it is a 3GPP-defined MOC. According to 3GPP TS 32.160 chapter 6.2.1.3
		 * all 3GPP-defined IOCs shall have namespace format 'urn:3gpp:saX:<module-name>'
		 */
		if(PreProcessor.getOriginalNamespace(stmt).startsWith("urn:3gpp:")) {
			return true;
		}

		/*
		 * IETF stuff is never 3GPP...
		 */
		if(PreProcessor.getOriginalNamespace(stmt).startsWith("urn:ietf:")) {
			return false;
		}

		/*
		 * Not defined by 3GPP or IETF, OK, have a quick look at the naming convention.
		 */
		if(!iocNamingConventionAdheredTo(PreProcessor.getRenamedStatementIdentifier(stmt))) {
			return false;
		}

		if(stmt.is(CY.STMT_CONTAINER)) {
			/*
			 * 3GPP stipulates that all MOCs shall be translated to YANG 'list', but realistically, somebody
			 * will end up using a container for singleton MOCs. It is well possible that a non-presence
			 * container is used, without using the 'attributes' container underneath, purely for structural
			 * reasons. But there cannot be any leafs/ leaf-lists within the container.
			 */
			final List<AbstractStatement> childLeafsAndLeafLists = ExtractionHelper.getChildDataNodes(stmt, ExtractionHelper::isLeafOrLeafList);
			return childLeafsAndLeafLists.isEmpty();
		}

		/*
		 * It is a list, it must have a single leaf, that being the key, and nothing else. It is quite
		 * unlikely, but possible, that the MOC does not define an 'attributes' container; hence, we are
		 * not checking for that.
		 */
		final List<AbstractStatement> children = ExtractionHelper.getChildDataNodes(stmt, ExtractionHelper::isLeaf);
		if(children.size() != 1) {
			return false;
		}

		final YKey yKey = stmt.getChild(CY.STMT_KEY);
		final List<String> keys = yKey != null ? yKey.getKeys() : Collections.<String>emptyList();
		if(keys.size() != 1) {
			return false;
		}
		if(!PreProcessor.getOriginalStatementIdentifier(children.get(0)).equals(keys.get(0))) {
			return false;
		}

		return true;
	}

	/**
	 * Returns all YANG data nodes for a 3GPP MOC. That's at most one leaf sitting under the list (the key),
	 * and the contents of the 'attributes' container, if any.
	 */
	public static List<AbstractStatement> getChildAttributesFor3gppMoc(final AbstractStatement three3gppMoc) {

		final List<AbstractStatement> result = new ArrayList<>();

		final AbstractStatement listKey = get3gppMocKey(three3gppMoc);
		if(listKey != null) {
			result.add(listKey);
		}

		/*
		 * Within the 'attributes' container we can have structs as well, so need to return containers & lists as well.
		 */
		final AbstractStatement threegppAttributesContainer = get3gppAttributesContainer(three3gppMoc);
		if(threegppAttributesContainer != null) {
			result.addAll(ExtractionHelper.getChildDataNodes(threegppAttributesContainer, ExtractionHelper::isContainerOrListOrLeafOrLeafList));
		}

		return result;
	}

	/**
	 * Returns the key attribute of a YANG 'list' representing a 3GPP MOC. Will return null if the
	 * 3GPP MOC is a 'container'.
	 */
	private static AbstractStatement get3gppMocKey(final AbstractStatement three3gppMoc) {

		if(!three3gppMoc.is(CY.STMT_LIST)) {
			return null;
		}

		/*
		 * It's safe to call get(0) here since the MOC would only have been identified to be a 3GPP MOC if it
		 * has a key (that being a single leaf), so it must be found.
		 */
		return ExtractionHelper.getChildDataNodes(three3gppMoc, ExtractionHelper::isLeaf).get(0);
	}

	/**
	 * The naming convention for IOCs ("classes") is laid down in 3GPP TS 32.300. Chapter 7.3 shows
	 * the EBNF for DN, and as part of this type 'ClassName' is defined to be the following:
	 * <p>
	 * ClassName                = CapitalLetterChar , { LocalDNAttributeTypeChar }
	 * CapitalLetterChar        = (* ISO/IEC 646 IRV U+0041-005A Latin capital letters A to Z *)
	 * LocalDNAttributeTypeChar = DNChar – FullStopChar ;
	 * DNChar                   = DNCharUnrestricted - ReservedChar ;
	 * DNCharUnrestricted       = ? Character of ISO/IEC 646 IRV ? | ? Character of standard coded character set supporting and extending ISO/IEC 646 IRV  ? ;
	 * ReservedChar             = Rfc2253ReservedChar | CarriageReturnChar | LineFeedChar | AsteriskChar ;
	 * Rfc2253ReservedChar      = CommaChar | EqualsSignChar | PlusSignChar | LessThanSignChar | GreaterThanSignChar | NumberSignChar | SemiColonChar | ReverseSolidusChar | QuotationMarkChar ;
	 * <p>
	 * So, in a nutshell, must start with an Uppercase letter (A-Z), and then pretty much anything goes except .*,=+<>#;\"
	 * <p>
	 * Note that especially usage of the '-' character ('hyphen-minus') is allowed.
	 * <p>
	 * A valid identifier in YANG, according to the RFC, must:
	 * <p>
	 * Start with '_' or 'A-Z' or 'a-z'
	 * Followed by '_' or '-' or '.' or 'A-Z' or 'a-z' or '0-9'
	 * <p>
	 * Note especially that other unicode characters are not allowed, and that an identifier MUST NOT start with a digit.
	 */
	private static boolean iocNamingConventionAdheredTo(final String name) {

		/*
		 * Check the first character. According to 3GPP (see above), it must be a
		 * CapitalLetterChar = (* ISO/IEC 646 IRV U+0041-005A Latin capital letters A to Z *)
		 * 
		 * Note that in YANG there is no valid character part of an identifier with the character
		 * being lower than ASCII 65 ('A'). RFC7950 stipulates:
		 * 
		 * identifier = (ALPHA / "_")
		 *                *(ALPHA / DIGIT / "_" / "-" / ".")
		 *
		 * In other words, the first character MUST be an underscore '_' (ASCII 95) or alpha
		 * (the 'A' is the lowest in the ASCII table). If we get anything else that would be
		 * an incorrect identifier, and the model should not have it made this far (validation
		 * should have flagged this up).
		 */
		if(name.charAt(0) < 'A') {
			return false;
		}

		if(name.charAt(0) > 'Z') {
			return false;
		}

		/*
		 * Check remainder of the characters.
		 */
		for(int i = 1; i < name.length(); ++i) {
			final char charAt = name.charAt(i);
			switch(charAt) {
			case '.':
/*
 * These here need not be checked for. Yang does not allow any of these to be part of data node identifiers.
 
			case '*':
			case ',':
			case '=':
			case '+':
			case '<':
			case '>':
			case '#':
			case ';':
			case '\\':
			case '?':
*/
				return false;
			}
		}

		return true;
	}

	/**
	 * Bit weird. From chapter 6.2.11.2 of the 3GPP document:
	 * <p>
	 * If the attribute is isUnique=false and isWritable=true map it to a list with an additional dummy index.
	 * The name of the list shall be <attributeName>Wrap. The name of the dummyIndex shall be idx and shall
	 * have a type uint32 or uint64.
	 * <p>
	 * // attribute is unique or read-only:
	 * <p>
	 * leaf-list mySimpleMultivalueAttribute1 { type xxx; }
	 * <p>
	 * // attribute is non-unique and read-write:
	 * <p>
	 * list mySimpleMultivalueAttribute2Wrap {<br/>
	 *   key idx;<br/>
	 *   leaf idx { type uint32 ; }<br/>
	 *   leaf mySimpleMultivalueAttribute2 { type xxx; }<br/>
	 */
	public static boolean isNonUniqueWritableMultiValuedAttribute(final AbstractStatement stmt) {

		if(!stmt.is(CY.STMT_LIST)) {
			return false;
		}

		final YList yList = (YList) stmt;

		/*
		 * list name must end on "Wrap".
		 */
		if(!yList.getListName().endsWith("Wrap")) {
			return false;
		}

		/*
		 * There must be two leafs in here, one key, and the actual leaf
		 */
		final List<YLeaf> leafsInsideWrap = yList.getLeafs();

		if(leafsInsideWrap.size() != 2) {
			return false;
		}

		/*
		 * Check on key name
		 */
		final YKey yKey = yList.getKey();
		final String keys = yKey == null ? "" : yKey.getValue();
		if(!"idx".equals(keys)) {
			return false;
		}

		final Optional<YLeaf> indexLeaf = leafsInsideWrap.stream().filter(yLeaf -> yLeaf.getLeafName().equals("idx")).findAny();
		if(indexLeaf.isPresent()) {
			final YType yType = (YType) indexLeaf.get().getChild(CY.STMT_TYPE);	
			final YangDataType yangDataType = yType != null ? DataTypeHelper.getYangDataType(yType.getDataType()) : YangDataType.EMPTY;
			if(yangDataType != YangDataType.UINT32 && yangDataType != YangDataType.UINT64) {
				return false;
			}
		} else {
			return false;
		}

		/*
		 * There must be a leaf inside the list with the same name as the outer attribute, minus the "Wrap"
		 */
		final String actualAttrName = getRealNonUniqueSequenceName(yList.getListName());

		final Optional<YLeaf> actualAttr = leafsInsideWrap.stream().filter(yLeaf -> yLeaf.getLeafName().equals(actualAttrName)).findAny();
		if(!actualAttr.isPresent()) {
			return false;
		}

		return true;
	}

	/**
	 * Given a non-unique sequence name, returns the real name of the sequence - basically, trim of the "Wrap". 
	 */
	public static String getRealNonUniqueSequenceName(final String originalStatementIdentifier) {
		return originalStatementIdentifier.substring(0, originalStatementIdentifier.length() - 4);
	}

	public static AbstractStatement getWrappedDataNode(final AbstractStatement wrappedDataNode) {
		final String soughtName = PreProcessor.getRenamedStatementIdentifier(wrappedDataNode);
		return ExtractionHelper.getChildDataNodes(wrappedDataNode, child -> soughtName.equals(child.getStatementIdentifier())).get(0);
	}

	/**
	 * Given a simplified schema path, navigates from the starting point (which could be an attribute)
	 * along the path to the referenced MOC and returns it, if found.
	 */
	public static AbstractStatement followPath(final AbstractStatement leafOrLeafList, final String schemaPath) {

		/*
		 * The path can look as follows
		 * 
		 * - absolute path (starting with the '/' symbol) = starting at schema root
		 * - relative path (starting with the '..' symbol) = moving up one MO
		 * - relative path (starting with a YANG list's identifier) = moving down one MO
		 */
		final boolean pathIsAbsolute = schemaPath.startsWith("/");
		final String pathWithoutLeadingSlash = pathIsAbsolute ? schemaPath.substring(1) : schemaPath;
		if(pathWithoutLeadingSlash.isEmpty()) {
			return null;
		}

		final String[] pathSegments = pathWithoutLeadingSlash.contains("/") ? pathWithoutLeadingSlash.split("/") : new String[] {pathWithoutLeadingSlash};
		AbstractStatement navigationMoc = null;
		int segStartIndex;

		/*
		 * If the path is absolute we need to move to the top of the schema and find the first path
		 * element. We can happily ignore the namespace as 3GPP clearly says that MOC names must be unique.
		 */
		if(pathIsAbsolute) {

			final String firstPathSegment = pathSegments[0];
			segStartIndex = 1;

			final Schema schema = leafOrLeafList.getYangModelRoot().getOwningSchema();
			navigationMoc = ExtractionHelper.getStatementsAtSchemaRoot(schema, PreProcessor::is3gppMoc).stream()
				.filter(moc -> moc.getStatementIdentifier().equals(firstPathSegment))
				.findAny().orElse(null);

		} else {			// relative

			segStartIndex = 0;
			navigationMoc = ExtractionHelper.getOwningMoc(leafOrLeafList);
		}

		if(navigationMoc == null) {
			return null;
		}

		/*
		 * And now we simply navigate through the tree
		 */
		for(int i = segStartIndex ; i < pathSegments.length; ++i) {

			final String pathSegment = pathSegments[i];

			/*
			 * Either move upwards (..) or downwards (MOC name) in the tree
			 */
			if(pathSegment.equals("..")) {
				navigationMoc = ExtractionHelper.getParentMoc(navigationMoc);
			} else {
				navigationMoc = ExtractionHelper.getChildMocs(navigationMoc).stream()
						.filter(moc -> moc.getStatementIdentifier().equals(pathSegment))
						.findAny().orElse(null);
			}

			if(navigationMoc == null) {
				return null;
			}
		}

		return navigationMoc;
	}
}
