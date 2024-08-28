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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.util.EnumMemberEditor;
import com.ericsson.oss.itpf.modeling.common.util.UnsignedLongEncoderDecoder;
import com.ericsson.oss.itpf.modeling.common.util.constraints.CommonConstraintsChecker;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractPrimitiveNumericDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractSingleDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BitsMemberType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BitsType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ByteType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ByteValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionValue.Values;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.UnionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeMember;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.PrefixResolver;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ModulePrefixResolver;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.CERI;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriInitialValue;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.threegpp.C3GPP;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.threegpp.Y3gppInitialValue;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YDefault;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.GrammarHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.NumberHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.StringHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.YangIdentity;
import com.ericsson.oss.mediation.modeling.yangtools.parser.util.QNameHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2Exception;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;

public class DefaultValueGenerator {

	/**
	 * Creates the default value in accordance with the data type. 
	 */
	public static AbstractValue generateDefault(final TransformerContext context, final AbstractDataType dataType, final AbstractStatement leafOrLeafList) {

		final List<String> stringefiedDefaultsFromModel = getStringefiedDefaultsFromModel(leafOrLeafList);
		if(stringefiedDefaultsFromModel.isEmpty()) {
			return null;
		}

		final ModulePrefixResolver prefixResolver = getPrefixResolverForDefaultValue(leafOrLeafList);

		/*
		 * We need to know the "real data type", which may have to be extracted from a CollectionType
		 */
		final AbstractSingleDataType actualType = dataType instanceof CollectionType ? ((CollectionType) dataType).getCollectionValuesType() : (AbstractSingleDataType) dataType;

		/*
		 * Translate all individual stringefied default values to their Java equivalent. null values
		 * encountered mean that the default value could not be translated, and we will skip those
		 * (error in the model).
		 */
		final List<AbstractValue> abstractValueDefaults = stringefiedDefaultsFromModel.stream()
				.filter(Objects::nonNull)
				.map(oneDefault -> getJavaEquivalentForStringefiedDefaultValue(context, actualType, oneDefault, prefixResolver))
				.filter(Objects::nonNull)
				.map(DefaultValueGenerator::javaToAbstractValue)
				.collect(Collectors.toList());

		/*
		 * There could have been an issue translating the defaults; if so, don't return a default.
		 * This is true also for collections, where individual members might not have been translatable.
		 */
		if(stringefiedDefaultsFromModel.size() != abstractValueDefaults.size()) {
			context.logWarn(Constants.TEXT_NOT_GENERATED_DEFAULT_VALUE + " Default value for '" + ExtractionHelper.getHumanReadablePathToSchemaNode(leafOrLeafList) + "' not generated as it would be invalid."); 
			return null;
		}

		/*
		 * If it is a collection, need to return a Collection Value, with the individual defaults part of that.
		 */
		if(leafOrLeafList.is(CY.STMT_LEAF_LIST)) {
			final CollectionValue collectionValue = new CollectionValue();
			collectionValue.setValues(new Values());
			collectionValue.getValues().getValue().addAll(abstractValueDefaults);
			return collectionValue;
		}

		return abstractValueDefaults.get(0);
	}

	private static Object getJavaEquivalentForStringefiedDefaultValue(final TransformerContext context, final AbstractSingleDataType singleType, final String stringefiedDefaultValue, final ModulePrefixResolver prefixResolver) {

		/*
		 * The supplied stringefied default value is translated to the appropriate Java value. For 'union', there is
		 * special handling - a default value may apply to any member of the union, so all of these need to be checked.
		 * The first conversion that succeeds will be returned. 
		 */
		final List<AbstractSingleDataType> typesToCheck = singleType instanceof UnionType ? ((UnionType) singleType).getMember() : Collections.singletonList(singleType);

		for(final AbstractSingleDataType oneTypeToCheck : typesToCheck) {
			final Object val = translateToJavaObject(context, oneTypeToCheck, stringefiedDefaultValue, prefixResolver);
			if(val != null) {
				return val;
			}
		}

		return null;
	}

	public static Object translateToJavaObject(final TransformerContext context, final AbstractSingleDataType type, final String stringefiedDefaultValue, final ModulePrefixResolver prefixResolver) {

		/*
		 * For all of the types, we attempt to translate accordingly. This will take into consideration
		 * constraints as well, to make sure we generate the proper java type.
		 */

		if(type instanceof BitsType) {
			return translateForBitsType((BitsType) type, stringefiedDefaultValue);
		}
		if(type instanceof BooleanType) {
			return translateForBooleanType((BooleanType) type, stringefiedDefaultValue);
		}
		if(type instanceof DoubleType) {
			return translateForDoubleType((DoubleType) type, stringefiedDefaultValue);
		}
		if(type instanceof StringType) {
			return translateForStringType((StringType) type, stringefiedDefaultValue);
		}
		if(type instanceof AbstractPrimitiveNumericDataType) {
			return translateForNumericType((AbstractPrimitiveNumericDataType) type, stringefiedDefaultValue);
		}
		if(type instanceof EnumRefType) {
			return translateForEnumRefType(context, (EnumRefType) type, stringefiedDefaultValue, prefixResolver);
		}

		/*
		 * We should never get here. Building this in as a fail-safe.
		 */
		throw new YangTransformer2Exception("Unhandled java type " + type.getClass().getName());
	}

	private static Object translateForBitsType(final BitsType bitsType, final String stringefiedDefaultValue) {

		/*
		 * We are being fairly lenient here. There can be duplicates in the default value,
		 * which we simply ignore. Additional whitespace we also ignore. BUT - the bit names
		 * must be valid.
		 */
		final Set<String> uniqueDefaultBitNames = new HashSet<>(GrammarHelper.parseToStringList(stringefiedDefaultValue));
		if(uniqueDefaultBitNames.isEmpty()) {
			return null;
		}

		final Set<String> validMemberNames = bitsType.getMember().stream().map(BitsMemberType::getName).collect(Collectors.toSet());

		if(!validMemberNames.containsAll(uniqueDefaultBitNames)) {
			return null;
		}

		final List<String> defaultBitNamesAsList = new ArrayList<>(uniqueDefaultBitNames);
		Collections.sort(defaultBitNamesAsList);		// stable generation

		return StringHelper.toString(defaultBitNamesAsList, null, null, " ", null, null);
	}

	private static Object translateForBooleanType(final BooleanType booleanType, final String stringefiedDefaultValue) {

		/*
		 * The RFC stipulates that the 'empty' data type is not allowed to have a
		 * default value. Any default value in the model would be incorrect.
		 */
		if(booleanType.isEmpty() != null) {
			return null;
		}

		/*
		 * Being lenient here - RFC states that the correct values are 'true' and 'false', but easy
		 * for a designer to get this wrong.
		 */
		if(stringefiedDefaultValue.equalsIgnoreCase("true")) {
			return Boolean.TRUE;
		}

		if(stringefiedDefaultValue.equalsIgnoreCase("false")) {
			return Boolean.FALSE;
		}

		return null;
	}

	private static Double translateForDoubleType(final DoubleType doubleType, final String stringefiedDefaultValue) {
		try {
			final Double parsedDouble = Double.parseDouble(stringefiedDefaultValue.trim());
			final StringBuilder sb = new StringBuilder();
			CommonConstraintsChecker.checkConstraintsOneValue(doubleType, parsedDouble, sb);
			return sb.length() == 0 ? parsedDouble : null;
		} catch (final Exception ignored) {
			/*
			 * Parsing of the string might throw. We can safely ignore.
			 */
		}
		return null;
	}

	private static String translateForStringType(final StringType stringType, final String stringefiedDefaultValue) {
		final StringBuilder sb = new StringBuilder();
		CommonConstraintsChecker.checkConstraintsOneValue(stringType, stringefiedDefaultValue, sb);
		return sb.length() == 0 ? stringefiedDefaultValue : null;
	}

	private static Number translateForNumericType(final AbstractPrimitiveNumericDataType numericType, final String stringefiedDefaultValue) {

		/*
		 * Really simple - translate the default value to a BigInteger first (taking into
		 * account special default value syntax) and then narrow down to what it should be.
		 */
		final BigInteger bigIntegerDefaultValue = NumberHelper.getIntegerDefaultValue(stringefiedDefaultValue);
		if(bigIntegerDefaultValue == null) {
			/*
			 * Didn't translate to a number...
			 */
			return null;
		}

		try {
			Number number = null;

			if(numericType instanceof ByteType) {
				number = bigIntegerDefaultValue.byteValueExact();
			} else if(numericType instanceof ShortType) {
				number = bigIntegerDefaultValue.shortValueExact();
			} else if(numericType instanceof IntegerType) {
				number = bigIntegerDefaultValue.intValueExact();
			} else {
				if(((LongType) numericType).isUnsigned()) {
					/*
					 * Not efficient but simple: Translate to string and let the encoder worry about it.
					 */
					number = UnsignedLongEncoderDecoder.stringToUnsignedLong(bigIntegerDefaultValue.toString());
				} else {
					number = bigIntegerDefaultValue.longValueExact();
				}
			} 

			final StringBuilder sb = new StringBuilder();
			CommonConstraintsChecker.checkConstraintsOneValue(numericType, number, sb);
			return sb.length() == 0 ? number : null;

		} catch (final Exception ignored) {
			/*
			 * Probably BigInteger class threw an exception during narrowing; decimal value
			 * could have been too large for the data type. We can safely ignore.
			 */
		}

		return null;
	}

	private static String translateForEnumRefType(final TransformerContext context, final EnumRefType enumRefType, final String stringefiedDefaultValue, final ModulePrefixResolver prefixResolver) {

		/*
		 * The enum can be either a 'true enumeration' or comes from an identityref.
		 * The default values for these must be handled differently.
		 */
		final ModelInfo edtModelInfo = ModelInfo.fromImpliedUrn(enumRefType.getModelUrn(), SchemaConstants.OSS_EDT);
		final EnumDataTypeDefinition edt = context.getCreatedEmodels().getGeneratedEModel(edtModelInfo);

		final boolean isIdentity = edt.getMeta().stream().anyMatch(metaInfo -> metaInfo.getMetaName().equals(Constants.META_DERIVED_FROM_IDENTITIES));

		if(isIdentity) {
			return translateForIdentityRef(context, enumRefType, edt, stringefiedDefaultValue, prefixResolver);
		} else {
			return translateForEnumeration(edt, stringefiedDefaultValue);
		}
	}

	private static String translateForEnumeration(final EnumDataTypeDefinition edt, final String stringefiedDefaultValue) {

		/*
		 * The default value must be a member of the EDT.
		 */
		final Set<String> memberNames = edt.getMember().stream()
				.map(EnumDataTypeMember::getName)
				.collect(Collectors.toSet());

		return memberNames.contains(stringefiedDefaultValue) ? stringefiedDefaultValue : null;
	}

	private static String translateForIdentityRef(final TransformerContext context, final EnumRefType enumRefType, final EnumDataTypeDefinition edt, final String stringefiedDefaultValue, final ModulePrefixResolver prefixResolver) {

		/*
		 * Start off nice and simple by resolving the identity namespace and name.
		 */
		final String prefix = QNameHelper.extractPrefix(stringefiedDefaultValue);
		final String identityName = QNameHelper.extractName(stringefiedDefaultValue);

		final String identityNamespace = PrefixResolver.NO_PREFIX.equals(prefix) ? prefixResolver.getDefaultNamespaceUri() : prefixResolver.resolveNamespaceUri(prefix);
		if(identityNamespace == null) {
			/*
			 * If we can't resolve the namespace (unknown prefix used (which is a fault in the model))
			 * then we won't generate a default value.
			 */
			return null;
		}

		final YangIdentity defaultValueIdentity = new YangIdentity(identityNamespace, "", identityName);

		/*
		 * Identities are translated to EDTs. So we need to check the EDT if it has a member of the
		 * given namespace and name. However, things are a bit more complicated - we could conceivably
		 * be in a YAM different from the YAM that declares the identity, in which case the enum
		 * member representing the default value would sit in an extension to the EDT. So we need to
		 * load both (of course, there may not be an extension...)
		 */

		/*
		 * Start off easy by loading the EDT and collecting the members.
		 */
		final Set<YangIdentity> edtMembers = edt.getMember().stream()
				.map(edtMember -> new YangIdentity(edtMember.getNamespace(), "", edtMember.getName()))
				.collect(Collectors.toSet());

		/*
		 * Now see if there is an extension - if so, we add/remove members as appropriate.
		 */
		final EnumDataTypeExtensionDefinition edtExt = context.getCreatedEmodels().getGeneratedEModels().stream()
				.filter(emodel -> emodel instanceof EnumDataTypeExtensionDefinition)
				.map(emodel -> (EnumDataTypeExtensionDefinition) emodel)
				.filter(edtExtDef -> edtExtDef.getExtendedModelElement().get(0).getUrn().equals(enumRefType.getModelUrn()))
				.findAny()
				.orElse(null);

		if(edtExt != null) {
			Optional.ofNullable(edtExt.getEnumDataTypeExtension()).ifPresent(added -> {
				added.getMember().forEach(edtMember -> edtMembers.add(new YangIdentity(edtMember.getNamespace(), "", edtMember.getName())));
			});
			Optional.ofNullable(edtExt.getEnumDataTypeRemoval()).ifPresent(removed -> {
				removed.getMemberWithNamespace().forEach(edtMember -> edtMembers.remove(new YangIdentity(edtMember.getNamespace(), "", edtMember.getName())));
			});
		}

		/*
		 * We now have the totality of all identities for the EDT. Now check whether the default value
		 * exists in those identities (if not that's really a fault in the model).
		 */
		if(!edtMembers.contains(defaultValueIdentity)) {
			return null;
		}

		/*
		 * If it's a duplicate we need to concatenate the namespace and the identity name, as
		 * otherwise the default value will be ambiguous.
		 */
		final boolean duplicate = edtMembers.stream().filter(yangIdentity -> yangIdentity.getIdentifier().equals(identityName)).limit(2).count() > 1;
		if(duplicate) {
			return EnumMemberEditor.joinMemberNamespaceAndName(identityNamespace, identityName);
		} else {
			return identityName;
		}
	}

	/**
	 * Given a Java object, returns an abstract value for it.
	 */
	public static AbstractValue javaToAbstractValue(final Object obj) {

		if(obj instanceof Boolean) {
			final BooleanValue booleanValue = new BooleanValue();
			booleanValue.setValue((Boolean) obj);
			return booleanValue;
		}

		if(obj instanceof Byte) {
			final ByteValue byteValue = new ByteValue();
			byteValue.setValue((Byte) obj);
			return byteValue;
		}

		if(obj instanceof Double) {
			final DoubleValue doubleValue = new DoubleValue();
			doubleValue.setValue((Double) obj);
			return doubleValue;
		}

		if(obj instanceof Integer) {
			final IntegerValue integerValue = new IntegerValue();
			integerValue.setValue((Integer) obj);
			return integerValue;
		}

		if(obj instanceof Long) {
			final LongValue longValue = new LongValue();
			longValue.setValue((Long) obj);
			return longValue;
		}

		if(obj instanceof Short) {
			final ShortValue shortValue = new ShortValue();
			shortValue.setValue((Short) obj);
			return shortValue;
		}

		if(obj instanceof String) {
			final StringValue stringValue = new StringValue();
			stringValue.setValue((String) obj);
			return stringValue;
		}

		/*
		 * We should never get here. Building this in as a fail-safe.
		 */
		throw new YangTransformer2Exception("Unhandled java type " + obj.getClass().getName());
	}

	/**
	 * Returns from the model the default value(s), if any. Preference will be given first to initial
	 * values defined by a 3GPP-extension, then initial values defined by the Ericsson extension, then
	 * the regular YANG 'default' statement.
	 * <p>
	 * Returns null if there is / are no defaults.
	 */
	private static List<String> getStringefiedDefaultsFromModel(final AbstractStatement leafOrLeafList) {

		if(leafOrLeafList.hasAtLeastOneChildOf(C3GPP.THREEGPP_COMMON_YANG_EXTENSIONS__INITIAL_VALUE)) {
			return leafOrLeafList.getChildren(C3GPP.THREEGPP_COMMON_YANG_EXTENSIONS__INITIAL_VALUE).stream()
					.map(stmt -> (Y3gppInitialValue) stmt)
					.map(Y3gppInitialValue::getInitialValue)
					.collect(Collectors.toList());
		}

		if(leafOrLeafList.hasAtLeastOneChildOf(CERI.ERICSSON_YANG_EXTENSIONS__INITIAL_VALUE)) {
			return leafOrLeafList.getChildren(CERI.ERICSSON_YANG_EXTENSIONS__INITIAL_VALUE).stream()
					.map(stmt -> (YEriInitialValue) stmt)
					.map(YEriInitialValue::getInitialValue)
					.collect(Collectors.toList());
		}

		if(leafOrLeafList.hasAtLeastOneChildOf(CY.STMT_DEFAULT)) {
			return leafOrLeafList.getChildren(CY.STMT_DEFAULT).stream()
					.map(stmt -> (YDefault) stmt)
					.map(YDefault::getValue)
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	/**
	 * Returns the prefix resolver to use when resolving any prefixes of default values. Note
	 * that simply returning the prefix resolver of the leaf/leaf-list is naive - it is the prefix
	 * resolver of the 'default' statement that must be returned, as YANG allows for a default
	 * value to be deviated. This means that a deviated default value gets the prefix resolver
	 * of the deviating module, not the module that defines the leaf/leaf-list.
	 */
	private static ModulePrefixResolver getPrefixResolverForDefaultValue(final AbstractStatement leafOrLeafList) {

		if(leafOrLeafList.hasAtLeastOneChildOf(C3GPP.THREEGPP_COMMON_YANG_EXTENSIONS__INITIAL_VALUE)) {
			return leafOrLeafList.getChildren(C3GPP.THREEGPP_COMMON_YANG_EXTENSIONS__INITIAL_VALUE).get(0).getPrefixResolver();
		}

		if(leafOrLeafList.hasAtLeastOneChildOf(CERI.ERICSSON_YANG_EXTENSIONS__INITIAL_VALUE)) {
			return leafOrLeafList.getChildren(CERI.ERICSSON_YANG_EXTENSIONS__INITIAL_VALUE).get(0).getPrefixResolver();
		}

		return leafOrLeafList.getChildren(CY.STMT_DEFAULT).get(0).getPrefixResolver();
	}

	// ====================================================================================================================================================

	public static boolean defaultsAreEqual(final AbstractValue origDefault, final AbstractValue deviatedDefault) {

		if(origDefault == null && deviatedDefault == null) {
			return true;
		}

		if(origDefault == null || deviatedDefault == null) {
			return false;
		}

		if(!origDefault.getClass().equals(deviatedDefault.getClass())) {
			return false;
		}

		if(origDefault instanceof BooleanValue) {
			return PropertyUtils.valuesAreEqual(((BooleanValue) origDefault).isValue(), ((BooleanValue) deviatedDefault).isValue());
		} else if(origDefault instanceof ByteValue) {
			return PropertyUtils.valuesAreEqual(((ByteValue) origDefault).getValue(), ((ByteValue) deviatedDefault).getValue());
		} else if(origDefault instanceof DoubleValue) {
			return PropertyUtils.valuesAreEqual(((DoubleValue) origDefault).getValue(), ((DoubleValue) deviatedDefault).getValue());
		} else if(origDefault instanceof IntegerValue) {
			return PropertyUtils.valuesAreEqual(((IntegerValue) origDefault).getValue(), ((IntegerValue) deviatedDefault).getValue());
		} else if(origDefault instanceof LongValue) {
			return PropertyUtils.valuesAreEqual(((LongValue) origDefault).getValue(), ((LongValue) deviatedDefault).getValue());
		} else if(origDefault instanceof ShortValue) {
			return PropertyUtils.valuesAreEqual(((ShortValue) origDefault).getValue(), ((ShortValue) deviatedDefault).getValue());
		} else if(origDefault instanceof StringValue) {
			return PropertyUtils.valuesAreEqual(((StringValue) origDefault).getValue(), ((StringValue) deviatedDefault).getValue());
		} else if (origDefault instanceof CollectionValue) {

			final CollectionValue origCollection = (CollectionValue) origDefault;
			final CollectionValue deviatedCollection = (CollectionValue) deviatedDefault;

			return PropertyUtils.listsAreEqual(origCollection.getValues().getValue(), deviatedCollection.getValues().getValue(), (o1,o2) -> defaultsAreEqual(o1, o2)); 
		}

		/*
		 * We should never get here. Building this in as a fail-safe.
		 */
		throw new YangTransformer2Exception("Unhandled value type " + origDefault.getClass().getName());
	}
}
