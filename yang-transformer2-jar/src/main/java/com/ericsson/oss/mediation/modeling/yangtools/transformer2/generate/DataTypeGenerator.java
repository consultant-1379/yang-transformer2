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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.util.UnsignedLongEncoderDecoder;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeAttribute;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractCollectionDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractPrimitiveNumericDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractSingleDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BitsMemberType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BitsType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ByteType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionSizeConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionUniquenessConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ComplexRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleMinMaxRange;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.DoubleValueRangeConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MinMaxRange;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MinMaxValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MoRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ReferenceEndpointConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringContentsConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringContentsConstraintType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringLengthConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.UnionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ValueRangeConstraint;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.CERI;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.YEriDnref;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YLength;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YLength.BoundaryPair;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YPattern;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YRange;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.DataTypeHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.DataTypeHelper.YangDataType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.NumberHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.PatternHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.util.NamespaceModuleIdentifier;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2Exception;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils.EqualsEvaluator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ThreeGPPSupport;

public class DataTypeGenerator {

	/**
	 * Given a data node (leaf, leaf-list, container, list) will generate the corresponding EModel data type.
	 */
	public static AbstractDataType generateDataType(final TransformerContext context, final AbstractStatement dataNode) {

		AbstractSingleDataType singleDataType = null;

		if(ExtractionHelper.isLeafOrLeafList(dataNode) || PreProcessor.is3gppNonUniqueSequence(dataNode)) {
			/*
			 * A regular (possibly wrapped) leaf/leaf-list = a regular attribute
			 */
			singleDataType = generateAbstractSingleDataType(context, dataNode);

		} else {
			/*
			 * Container or list, resulting in a CDT being generated (if the container / list represents
			 * an MOC we would not have gotten here).
			 */
			singleDataType = generateComplexRefType(context, dataNode);
		}

		if(ExtractionHelper.isLeafOrContainer(dataNode)) {
			return singleDataType;
		}

		/*
		 * It's a leaf-list or list, hence we need to handle the collection.
		 * 
		 * CollectionType is not supported in the toolchain (why?) so have to use a ListType.
		 */
		final ListType listType = new ListType();
		listType.setCollectionValuesType(singleDataType);

		/*
		 * For 'config true' leaf-lists, the values in a leaf-list must be unique according to the RFC.
		 */
		if(dataNode.isEffectiveConfigTrue()) {
			listType.setCollectionUniquenessConstraint(new CollectionUniquenessConstraint());
		}

		/*
		 * Handle collection size constraints
		 */
		final CollectionSizeConstraint collectionSizeConstraint = new CollectionSizeConstraint();

		final MinMaxValue minElementsValue = PropertyUtils.getMinElementsValue(dataNode);
		final MinMaxValue maxElementsValue = PropertyUtils.getMaxElementsValue(dataNode);

		if(minElementsValue != null && maxElementsValue != null) {
			final MinMaxRange minMaxRange = new MinMaxRange();
			minMaxRange.setMin(minElementsValue.getValue());
			minMaxRange.setMax(maxElementsValue.getValue());
			collectionSizeConstraint.getMinMaxSize().add(minMaxRange);
			listType.setCollectionSizeConstraint(collectionSizeConstraint);
		} else if (minElementsValue != null) {
			collectionSizeConstraint.setMinSize(minElementsValue);
			listType.setCollectionSizeConstraint(collectionSizeConstraint);
		} else if (maxElementsValue != null) {
			collectionSizeConstraint.setMaxSize(maxElementsValue);
			listType.setCollectionSizeConstraint(collectionSizeConstraint);
		}

		return listType;
	}

	private static AbstractSingleDataType generateAbstractSingleDataType(final TransformerContext context, final AbstractStatement dataNode) {

		final YType yType = PropertyUtils.getType(dataNode);
		if(yType == null) {
			context.logWarn(Constants.TEXT_GENERATED_AS_STRING + " Missing data type on '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNode) + "'; will generate as 'StringType'.");
			return new StringType();
		}

		final List<YType> typesToHandle = DataTypeHelper.isUnionType(yType.getDataType()) ? yType.getTypes() : Collections.singletonList(yType);
		if(typesToHandle.size() == 1) {
			/*
			 * Normal case
			 */
			return generateAbstractSingleDataType(context, dataNode, typesToHandle.get(0), null);

		} else {
			/*
			 * Union type
			 */
			final UnionType unionType = new UnionType();

			for(int i = 0; i < typesToHandle.size(); ++i) {
				final AbstractSingleDataType unionMemberType = generateAbstractSingleDataType(context, dataNode, typesToHandle.get(i), i);
				unionType.getMember().add(unionMemberType);
			}

			return unionType;
		}
	}

	public static AbstractSingleDataType generateAbstractSingleDataType(final TransformerContext context, final AbstractStatement dataNode, final YType yType, final Integer positionInUnion) {

		final YangDataType origDataType = DataTypeHelper.getYangDataType(yType.getDataType());

		switch(origDataType) {
		case STRING:
			return generateTypeBasedOnString(context, dataNode, yType);
		case BOOLEAN:
			return new BooleanType();
		case EMPTY:
			final BooleanType emptyType = new BooleanType();
			emptyType.setEmpty(true);
			return emptyType;
		case INT8:
		case INT16:
		case INT32:
		case INT64:
			return generateSignedNumericType(yType, origDataType);
		case UINT8:
		case UINT16:
		case UINT32:
		case UINT64:
			return generateUnsignedNumericType(yType, origDataType);
		case BITS:
			return generateBitsType(yType);
		case ENUMERATION:
			return generateEnumRefType(context, dataNode, yType, positionInUnion);
		case IDENTITYREF:
			return generateIdentityRefType(context, dataNode, yType, positionInUnion);
		case LEAFREF:
			return generateLeafRefType(context, dataNode, yType, positionInUnion);
		case INSTANCE_IDENTIFIER:
			return new StringType();
		case DECIMAL64:
			return generateFloatNumericType(yType);
		case BINARY:
			return generateBinaryType(context, dataNode);
		case DERIVED____TYPE:
			/*
			 * Unresolved derived type, usually due to missing module. Seen a few times, especially with tail-f
			 * modules. Nothing much we can do here except generate as 'StringType', giving at least some level
			 * of configuration support to the user.
			 */
			context.logWarn(Constants.TEXT_GENERATED_AS_STRING + " Unresolvable derived type on '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNode) + "'; will generate as 'StringType'.");
			return new StringType();
		default:
		}

		/*
		 * We should never get here. Building this in as a fail-safe.
		 */
		throw new YangTransformer2Exception("Unhandled orig data type " + origDataType);
	}

	private static AbstractSingleDataType generateLeafRefType(final TransformerContext context, final AbstractStatement dataNode, final YType yType, final Integer positionInUnion) {

		/*
		 * A leafref "points" to another leaf/leaf-list by means of its path. To figure out what
		 * the target is, the path needs to be resolved. As first step in this, we clean up the
		 * path and remove all predicates, as they are not needed and complicate navigation.
		 * 
		 * The clean-up of the path is such to remove anything in [] - this is a bit dodgy as it
		 * is theoretically possible that these characters are used within a string used in a
		 * comparison within a predicate - but this would be a very rare scenario.
		 */
		final String origPath = yType.getPath() == null ? "" : yType.getPath().getValue();
		final String pathPredicatesRemoved = origPath == null ? "" : origPath.replaceAll("\\[(.)+\\]", "").trim();
		if(pathPredicatesRemoved.isEmpty()) {
			/*
			 * Hm, there is no path? Maybe fault in the model? Generate as StringType, because
			 * what else can we do? 
			 */
			context.logWarn(Constants.TEXT_GENERATED_AS_STRING + " Empty path for leafref on '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNode) + "'; will generate as 'StringType'.");
			return new StringType();
		}

		/*
		 * Now navigate - the path can be relative or absolute. If it starts with a '/' then it
		 * is absolute and we start navigating at schema root, otherwise we will start with the
		 * leaf/leaf-list.
		 */
		final boolean isAbsolutePath = pathPredicatesRemoved.startsWith("/");
		final String[] identifierParts = getIdentifierParts(isAbsolutePath, pathPredicatesRemoved);
		final AbstractStatement navigationStart = isAbsolutePath ? dataNode.getYangModelRoot() : dataNode;
		final AbstractStatement target = LeafRefHelper.navigateToTarget(navigationStart, identifierParts, dataNode.getYangModelRoot().getOwningSchema());

		/*
		 * Could be that we could not pull out the target node. Could be a fault in the model, or a
		 * very complex XPath condition that our simple parser here cannot properly process, or maybe
		 * the target leaf-list was deviated out. If so, then we generate into a StringType.
		 */
		if(target == null) {
			final String message = Constants.TEXT_GENERATED_AS_STRING + " Leafref target for path '" + origPath + "' on '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNode) + "' could not be found; will generate as 'StringType'.";

			/*
			 * It makes a difference where we are. If we are in DEVIATED we don't really care too much;
			 * but if we are in ORIGINAL we have a problem - another build, for another node, may build
			 * the exact same MOC with the attribute being correct. Then we have two copies of the same
			 * DPS PT in the system, but with different data type for the same attribute. That would not
			 * be good, as it is not deterministic which of the copies gets deployed in the end.
			 */
			if(PreProcessor.isInOriginalVariant(dataNode)) {
				context.logWarn(message);
			} else {
				context.logInfo(message);
			}

			return new StringType();
		}

		/*
		 * We (re-)generate the data type of the target. The target will most likely be a leaf-list,
		 * so will be converted to collection type. What we want is the collection member. 
		 */
		final AbstractDataType targetDataType = generateDataType(context, target);
		if(targetDataType instanceof CollectionType) {
			return ((CollectionType) targetDataType).getCollectionValuesType();
		}

		return (AbstractSingleDataType) targetDataType;
	}

	/**
	 * Splits the schema node identifier into individual parts.
	 */
	private static String[] getIdentifierParts(final boolean isAbsoluteSchemaNodeIdentifier, final String schemaNodeIdentifier) {
		final String relativeSchemaNodeIdentifier = isAbsoluteSchemaNodeIdentifier ? schemaNodeIdentifier.substring(1) : schemaNodeIdentifier;
		return relativeSchemaNodeIdentifier.contains("/") ? relativeSchemaNodeIdentifier.split("/") : new String[] {relativeSchemaNodeIdentifier};
	}

	private static AbstractSingleDataType generateIdentityRefType(final TransformerContext context, final AbstractStatement dataNode, final YType yType, final Integer positionInUnion) {
		return IdentityRefGenerator.generateIdentityRefType(context, dataNode, yType, positionInUnion);
	}

	private static EnumRefType generateEnumRefType(final TransformerContext context, final AbstractStatement dataNode, final YType yType, final Integer positionInUnion) {
		return EnumerationTypeGenerator.generateEnumRefType(context, yType, positionInUnion, dataNode);
	}

	private static ComplexRefType generateComplexRefType(final TransformerContext context, final AbstractStatement containerOrList) {
		return ComplexTypeGenerator.generateComplexType(context, containerOrList);
	}

	private static AbstractSingleDataType generateBitsType(final YType yType) {

		final BitsType bitsType = new BitsType();

		yType.getBits().forEach(yBit -> {
			final BitsMemberType bitsMemberType = new BitsMemberType();
			bitsType.getMember().add(bitsMemberType);

			bitsMemberType.setName(PreProcessor.getOriginalStatementIdentifier(yBit));
			bitsMemberType.setDesc(PropertyUtils.getDescription(yBit, PreProcessor.getOriginalStatementIdentifier(yBit)));
			bitsMemberType.setLifeCycle(EModelHelper.getLifeCycleType(yBit));
			bitsMemberType.setLifeCycleDesc(PropertyUtils.getStatusInformation(yBit));
		});

		Collections.sort(bitsType.getMember(), (o1, o2) -> o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH)));

		return bitsType;
	}

	private static AbstractSingleDataType generateSignedNumericType(final YType yType, final YangDataType yangDataType) {

		AbstractPrimitiveNumericDataType dataType = null;

		switch(yangDataType) {
		case INT8:
			dataType = new ByteType();
			break;
		case INT16:
			dataType = new ShortType();
			break;
		case INT32:
			dataType = new IntegerType();
			break;
		default:
			dataType = new LongType();
			break;
		}

		dataType.setValueRangeConstraint(getValueRangeConstraint(yType, yangDataType));

		return dataType;
	}

	private static AbstractSingleDataType generateUnsignedNumericType(final YType yType, final YangDataType yangDataType) {

		AbstractPrimitiveNumericDataType dataType = null;

		switch(yangDataType) {
		case UINT8:
			dataType = new ShortType();
			break;
		case UINT16:
			dataType = new IntegerType();
			break;
		case UINT32:
			dataType = new LongType();
			break;
		default:
			dataType = new LongType();
			((LongType) dataType).setUnsigned(true);
			break;
		}

		dataType.setValueRangeConstraint(getValueRangeConstraint(yType, yangDataType));

		/*
		 * If there is no explicit range constraint, we create an artificial constraint in accordance with the unsigned data type.
		 */
		if(dataType.getValueRangeConstraint() == null) {
			final ValueRangeConstraint valueRangeConstraint = new ValueRangeConstraint();
			dataType.setValueRangeConstraint(valueRangeConstraint);

			final MinMaxRange minMaxRange = new MinMaxRange();
			minMaxRange.setMin(0L);
			minMaxRange.setMax(UnsignedLongEncoderDecoder.stringToUnsignedLong(NumberHelper.getMaxValueForYangIntegerDataType(yangDataType).toPlainString()));
			valueRangeConstraint.getMinMaxRange().add(minMaxRange);
		}

		return dataType;
	}

	private static ValueRangeConstraint getValueRangeConstraint(final YType yType, final YangDataType yangDataType) {

		final YRange yRange = yType.getRange();
		if(yRange == null) {
			return null;
		}

		final List<YRange.BoundaryPair> boundaries = yRange.getBoundaries();
		if(boundaries.isEmpty()) {
			return null;
		}

		try {
			final ValueRangeConstraint valueRangeConstraint = new ValueRangeConstraint();

			final boolean signed = DataTypeHelper.isYangSignedIntegerType(yangDataType);
			final boolean isUnsignedInt64 = yangDataType == YangDataType.UINT64;

			if(boundaries.size() == 1) {

				final YRange.BoundaryPair boundaryPair = boundaries.get(0);

				final boolean lowerIsMin = boundaryPair.lower.compareTo(NumberHelper.getMinValueForYangIntegerDataType(yangDataType)) == 0;
				final boolean upperIsMax = boundaryPair.upper.compareTo(NumberHelper.getMaxValueForYangIntegerDataType(yangDataType)) == 0;

				if(lowerIsMin && upperIsMax) {
					return null;		// min/max explicitly encoded in a 'range' statement, pointless. Muppet.
				}

				if(signed && lowerIsMin) {
					final MinMaxValue minMaxValue = new MinMaxValue();
					minMaxValue.setValue(convertToLong(boundaryPair.upper, isUnsignedInt64));
					valueRangeConstraint.setMaxValue(minMaxValue);
				} else if (signed && upperIsMax) {
					final MinMaxValue minMaxValue = new MinMaxValue();
					minMaxValue.setValue(convertToLong(boundaryPair.lower, isUnsignedInt64));
					valueRangeConstraint.setMinValue(minMaxValue);
				} else {
					final MinMaxRange minMaxRange = new MinMaxRange();
					minMaxRange.setMin(convertToLong(boundaryPair.lower, isUnsignedInt64));
					minMaxRange.setMax(convertToLong(boundaryPair.upper, isUnsignedInt64));
					valueRangeConstraint.getMinMaxRange().add(minMaxRange);
				}
			} else {
				boundaries.forEach(boundaryPair -> {
					final MinMaxRange minMaxRange = new MinMaxRange();
					minMaxRange.setMin(convertToLong(boundaryPair.lower, isUnsignedInt64));
					minMaxRange.setMax(convertToLong(boundaryPair.upper, isUnsignedInt64));
					valueRangeConstraint.getMinMaxRange().add(minMaxRange);
				});
			}

			return valueRangeConstraint;

		} catch (final Exception ignored) {
			/*
			 * If the model has an error (such as a number that is too large for a uint64) then
			 * somewhere along the line an exception will be thrown. Very unlikely to happen,
			 * but no harm guarding against it.
			 */
		}

		return null;
	}

	/**
	 * For the UINT64 data type we must use the encoder utilities to correctly handle values
	 * that are larger than Long.MAX_VALUE.
	 */
	private static Long convertToLong(final BigDecimal bigDecimal, final boolean isUnsignedInt64) {
		return isUnsignedInt64 ? UnsignedLongEncoderDecoder.stringToUnsignedLong(bigDecimal.toPlainString()) : bigDecimal.longValueExact();
	}

	private static AbstractSingleDataType generateFloatNumericType(final YType yType) {

		final DoubleType doubleType = new DoubleType();

		doubleType.setValueRangeConstraint(getDoubleValueRangeConstraint(yType));

		return doubleType;
	}

	private static DoubleValueRangeConstraint getDoubleValueRangeConstraint(final YType yType) {

		final YRange yRange = yType.getRange();
		if(yRange == null) {
			return null;
		}

		final List<YRange.BoundaryPair> boundaries = yRange.getBoundaries();
		if(boundaries.isEmpty()) {
			return null;
		}

		final DoubleValueRangeConstraint valueRangeConstraint = new DoubleValueRangeConstraint();

		/*
		 * The constraints for double values are handled slightly differently - basically, we
		 * don't try to be smart about the min/max values, as this is tricky to handle. We simply
		 * generate according to boundaries given to us by the parser, which will correctly
		 * handle the min/max for us.
		 */
		boundaries.forEach(boundaryPair -> {
			final DoubleMinMaxRange minMaxRange = new DoubleMinMaxRange();
			minMaxRange.setMin(boundaryPair.lower.doubleValue());
			minMaxRange.setMax(boundaryPair.upper.doubleValue());
			valueRangeConstraint.getMinMaxRange().add(minMaxRange);
		});

		return valueRangeConstraint;
	}

	private static final NamespaceModuleIdentifier THREEGPP_YANG_TYPES_DISTINGUISHED_NAME_TYPEDEF =
			new NamespaceModuleIdentifier("urn:3gpp:sa5:_3gpp-common-yang-types", "_3gpp-common-yang-types", "DistinguishedName");

	private static final NamespaceModuleIdentifier ERICSSON_YANG_TYPES_DISTINGUISHED_NAME_TYPEDEF =
			new NamespaceModuleIdentifier("urn:rdns:com:ericsson:oammodel:ericsson-yang-types", "ericsson-yang-types", "distinguished-name");

	private static AbstractSingleDataType generateTypeBasedOnString(final TransformerContext context, final AbstractStatement dataNode, final YType yType) {

		/*
		 * The existence of the 'dnref' extension on the leaf/leaf-list will cause us to generate
		 * a MoRef instead of a string.
		 */
		final List<AbstractStatement> dnRefs = dataNode.getChildren(CERI.ERICSSON_YANG_EXTENSIONS__DNREF);
		if(!dnRefs.isEmpty()) {

			final List<String> impliedUrns = getMoTypes(dataNode, dnRefs);
			Collections.sort(impliedUrns);									// stable generation

			/*
			 * The type of the MoRef may or may not be constrained. If it is not constrained it can point
			 * to "any" MO. If it can only point at a single type we directly encode the type in the ref.
			 * Otherwise, we have to use a special constraint.
			 */
			final MoRefType moRefType = new MoRefType();
			moRefType.setModelUrn(Constants.ANY_MANAGED_OBJECT.toImpliedUrn());

			if (impliedUrns.size() == 1) {
				moRefType.setModelUrn(impliedUrns.get(0));
			} else if (impliedUrns.size() > 1) {
				for(final String impliedUrn : impliedUrns) {
					final ReferenceEndpointConstraint referenceEndpointConstraint = new ReferenceEndpointConstraint();
					referenceEndpointConstraint.setPrimaryTypeUrn(impliedUrn);
					moRefType.getMoTypeConstraint().add(referenceEndpointConstraint);
				}
			}

			return moRefType;
		}

		/*
		 * 3GPP defines a typedef "DistinguishedName". If that is being used as type by the attribute (possibly
		 * in a chain of types / typedefs) then we can likewise generate the attribute to be of type MoRefType.
		 * The MoRef will point to "any" MOC, as there is nothing in 3GPP that allows us to figure out what the
		 * target MOC is.
		 * 
		 * There is a pattern defined as part of the "DistinguishedName" type which we are completely ignoring
		 * here. That pattern is a) incorrect and b) super-complex and is not something that we would like to
		 * evaluate a lot. We will use the in-build support by the modeling framework for DN syntax checks, which
		 * is simpler and much faster.
		 * 
		 * For legacy reasons, there is also a "distinguished name" type defined in the Ericsson Yang Types. It
		 * is unlikely that this would be used, but we cater for it nonetheless.
		 */
		if(PropertyUtils.isDerivedFromTypedef(yType, THREEGPP_YANG_TYPES_DISTINGUISHED_NAME_TYPEDEF) || PropertyUtils.isDerivedFromTypedef(yType, ERICSSON_YANG_TYPES_DISTINGUISHED_NAME_TYPEDEF)) {
			final MoRefType moRefType = new MoRefType();
			moRefType.setModelUrn(Constants.ANY_MANAGED_OBJECT.toImpliedUrn());
			return moRefType;
		}

		/*
		 * Regular string type
		 */
		final StringType stringType = new StringType();

		stringType.setStringLengthConstraint(getStringLengthConstraint(yType));
		stringType.setStringContentsConstraint(getStringContentsConstraint(context, dataNode, yType));

		return stringType;
	}

	private static List<String> getMoTypes(final AbstractStatement dataNode, final List<AbstractStatement> dnRefs) {

		final Set<String> result = new HashSet<>();

		/*
		 * A DnRef may or may not have an argument. If it does not have an argument, then it can
		 * point to "any MO" and we ignore that. Otherwise we try to follow the path argument of
		 * the extension to the target of the reference.
		 */
		for(final AbstractStatement dnRef : dnRefs) {

			final String schemaPath = ((YEriDnref) dnRef).getSchemaPath();

			/*
			 * If there is a dnref without path that means "any" MOC. It is pointless to look
			 * for type-specific MOs then, as "any" covers, well, any MO. So shortcut it.
			 */
			if(schemaPath == null) {
				return Collections.emptyList();
			}

			final AbstractStatement foundMoc = ThreeGPPSupport.followPath(dataNode, schemaPath);
			if(foundMoc != null) {
				result.add(EModelHelper.getImpliedVersionedUrnForMoc(foundMoc));
			}
		}

		return new ArrayList<String>(result);
	}

	private static AbstractSingleDataType generateBinaryType(final TransformerContext context, final AbstractStatement dataNode) {
		/*
		 * TODO: [BINARY_DATA_TYPE] For now all we can do is to return a StringType.
		 */
		if(PreProcessor.isInDeviatedVariant(dataNode)) {
			context.logInfo(Constants.TEXT_GENERATED_AS_STRING + " 'binary' data type encountered for data node '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNode) + "'; generated as 'StringType' for now.");
		}

		return new StringType();
	}

	private static StringLengthConstraint getStringLengthConstraint(final YType yType) {

		final YLength yLength = yType.getLength();
		if(yLength == null) {
			return null;
		}

		final List<BoundaryPair> boundaries = yLength.getBoundaries();
		if(boundaries.isEmpty()) {
			return null;
		}

		final StringLengthConstraint stringLengthConstraint = new StringLengthConstraint();

		if(boundaries.size() == 1) {

			final BoundaryPair boundaryPair = boundaries.get(0);

			if(boundaryPair.lower == 0) {
				if(boundaryPair.upper == Long.MAX_VALUE) {
					return null;													// pointless combination.
				} else {
					final MinMaxValue minMaxValue = new MinMaxValue();
					minMaxValue.setValue(boundaryPair.upper);
					stringLengthConstraint.setMaxLength(minMaxValue);				// set MAX only.
				}
			} else {
				if (boundaryPair.upper == Long.MAX_VALUE) {
					final MinMaxValue minMaxValue = new MinMaxValue();
					minMaxValue.setValue(boundaryPair.lower);
					stringLengthConstraint.setMinLength(minMaxValue);				// set MIN only.
				} else {
					final MinMaxRange minMaxRange = new MinMaxRange();
					minMaxRange.setMin(boundaryPair.lower);
					minMaxRange.setMax(boundaryPair.upper);
					stringLengthConstraint.getMinMaxLength().add(minMaxRange);		// set MIN/MAX pair.
				}
			}

		} else {
			boundaries.forEach(boundaryPair -> {
				final MinMaxRange minMaxRange = new MinMaxRange();
				minMaxRange.setMin(boundaryPair.lower);
				minMaxRange.setMax(boundaryPair.upper);
				stringLengthConstraint.getMinMaxLength().add(minMaxRange);
			});
		}

		return stringLengthConstraint;
	}

	private static StringContentsConstraint getStringContentsConstraint(final TransformerContext context, final AbstractStatement dataNode, final YType yType) {

		final List<YPattern> patterns = yType.getPatterns();
		if(patterns.isEmpty()) {
			return null;
		}

		/*
		 * It can happen that a pattern REGEX (which is XSD flavour) cannot be translated to Java - or, perhaps, is wrong.
		 * Validator would have found this long time ago. We cater for this here by compiling the pattern, and if there
		 * are issues we will ignore the pattern - otherwise we would be generating a pattern into the EModel that later
		 * on cannot be compiled and there will be runtime failures.
		 */
		final List<YPattern> correctPatterns = patterns.stream()
			.filter(yPattern -> checkPattern(context, yPattern.getPattern(), dataNode))
			.collect(Collectors.toList());

		if(correctPatterns.isEmpty()) {
			return null;
		}

		/*
		 * The RFC allows for multiple patterns to be specified, which must all be true. EModel only supports a
		 * single pattern value. It is also not possible to somehow concatenate multiple patterns into one
		 * (strangely, perhaps). So if there are multiple patterns we don't generate a constraint, as this would
		 * be wrong. The worst that can happen is that the user enters a value for the attribute that the node
		 * will not accept.
		 */
		if(correctPatterns.size() > 1) {
			if(PreProcessor.isInDeviatedVariant(dataNode)) {
				context.logWarn(Constants.TEXT_IGNORED_SCHEMA_SUPPORT + " Multiple patterns encountered for data node '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNode) + "'; string-contents-constraint not generated.");
			}
			return null;
		}

		final YPattern yPattern = correctPatterns.get(0);

		/*
		 * The YANG 'modifier' statement qualifies the regex. There is no support for this in EModel either - if
		 * we encounter it, we don't generate the constraint either (see above for reason).
		 */
		if(yPattern.hasAtLeastOneChildOf(CY.STMT_MODIFIER)) {
			if(PreProcessor.isInDeviatedVariant(dataNode)) {
				context.logWarn(Constants.TEXT_IGNORED_SCHEMA_SUPPORT + " 'modifier' statement encountered for data node '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNode) + "'; string-contents-constraint not generated.");
			}
			return null;
		}

		final StringContentsConstraint stringContentsConstraint = new StringContentsConstraint();
		stringContentsConstraint.setType(StringContentsConstraintType.REGEX);
		stringContentsConstraint.setValue(PatternHelper.toJavaPatternString(correctPatterns.get(0).getPattern()));

		return stringContentsConstraint;
	}

	private static boolean checkPattern(final TransformerContext context, final String xsdPattern, final AbstractStatement dataNode) {

		try {
			Pattern.compile(PatternHelper.toJavaPatternString(xsdPattern));
			return true;
		} catch (final PatternSyntaxException psex) {
			if(PreProcessor.isInDeviatedVariant(dataNode)) {
				context.logWarn(Constants.TEXT_IGNORED_UNRESOLVABLE + " Incorrect pattern constraint on data node '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNode) + "'; pattern will be ignored.");
			}
		}

		return false;
	}

	// ====================================================================================================================================================

	public static boolean typesAreEqual(final AbstractDataType origType, final AbstractDataType deviatedType) {

		if(!origType.getClass().equals(deviatedType.getClass())) {
			return false;
		}

		/*
		 * No need to check for not-null constraint - that constraint is only set if if
		 * the attribute is mandatory, so checking on a diff on mandatory is good enough.
		 */

		if(origType instanceof EnumRefType) {
			return PropertyUtils.valuesAreEqual(((EnumRefType) origType).getModelUrn(), ((EnumRefType) deviatedType).getModelUrn());
		} else if(origType instanceof ComplexRefType) {
			return PropertyUtils.valuesAreEqual(((ComplexRefType) origType).getModelUrn(), ((ComplexRefType) deviatedType).getModelUrn());
		} else if(origType instanceof BooleanType) {
			return PropertyUtils.valuesAreEqual(((BooleanType) origType).isEmpty(), ((BooleanType) deviatedType).isEmpty());
		} else if(origType instanceof StringType) {
			return stringTypesAreEqual((StringType) origType, (StringType) deviatedType);
		} else if(origType instanceof MoRefType) {
			return moRefTypesAreEqual((MoRefType) origType, (MoRefType) deviatedType);
		} else if(origType instanceof DoubleType) {
			return doubleTypesAreEqual((DoubleType) origType, (DoubleType) deviatedType);
		} else if(origType instanceof AbstractPrimitiveNumericDataType) {
			return numericTypesAreEqual((AbstractPrimitiveNumericDataType) origType, (AbstractPrimitiveNumericDataType) deviatedType);
		} else if(origType instanceof BitsType) {
			return bitsTypesAreEqual((BitsType) origType, (BitsType) deviatedType);
		} else if(origType instanceof AbstractCollectionDataType) {
			return collectionTypesAreEqual((AbstractCollectionDataType) origType, (AbstractCollectionDataType) deviatedType);
		} else if(origType instanceof UnionType) {
			return unionTypesAreEqual((UnionType) origType, (UnionType) deviatedType);
		}

		/*
		 * We should never get here. Building this in as a fail-safe.
		 */
		throw new YangTransformer2Exception("Unhandled data type " + origType.getClass().getName());
	}

	private static boolean unionTypesAreEqual(final UnionType origType, final UnionType deviatedType) {
		return PropertyUtils.listsAreEqual(origType.getMember(), deviatedType.getMember(), DataTypeGenerator::typesAreEqual);
	}

	private static final CollectionSizeConstraint EMPTY_COLLECTION_SIZE_CONSTRAINT = new CollectionSizeConstraint();

	private static boolean collectionTypesAreEqual(final AbstractCollectionDataType origType, final AbstractCollectionDataType deviatedType) {

		final boolean origIsUnique = origType.getCollectionUniquenessConstraint() != null;
		final boolean deviatedIsUnique = deviatedType.getCollectionUniquenessConstraint() != null;
		if(origIsUnique != deviatedIsUnique) {
			return false;
		}

		final AbstractSingleDataType origMembersType = origType.getCollectionValuesType();
		final AbstractSingleDataType deviatedMembersType = deviatedType.getCollectionValuesType();
		if(!typesAreEqual(origMembersType, deviatedMembersType)) {
			return false;
		}

		final CollectionSizeConstraint origSizeConstraint = origType.getCollectionSizeConstraint() != null ? origType.getCollectionSizeConstraint() : EMPTY_COLLECTION_SIZE_CONSTRAINT;
		final CollectionSizeConstraint deviatedSizeConstraint = deviatedType.getCollectionSizeConstraint() != null ? deviatedType.getCollectionSizeConstraint() : EMPTY_COLLECTION_SIZE_CONSTRAINT;

		return minMaxAreEqual(origSizeConstraint.getMinSize(), origSizeConstraint.getMaxSize(), origSizeConstraint.getMinMaxSize(),
				deviatedSizeConstraint.getMinSize(), deviatedSizeConstraint.getMaxSize(), deviatedSizeConstraint.getMinMaxSize());
	}

	private static boolean bitsTypesAreEqual(final BitsType origType, final BitsType deviatedType) {
		/*
		 * We just check the names are all the same. We don't care about
		 * differences in lifecycle or description
		 */
		return PropertyUtils.listsAreEqual(origType.getMember(), deviatedType.getMember(), (o1,o2) -> o1.getName().equals(o2.getName()));
	}

	private static final ValueRangeConstraint EMPTY_VALUE_RANGE_CONSTRAINT = new ValueRangeConstraint();

	private static boolean numericTypesAreEqual(final AbstractPrimitiveNumericDataType origType, final AbstractPrimitiveNumericDataType deviatedType) {

		if(origType instanceof LongType) {
			final boolean origIsUnsigned = ((LongType) origType).isUnsigned();
			final boolean deviatedIsUnsigned = ((LongType) deviatedType).isUnsigned();
			if(origIsUnsigned != deviatedIsUnsigned) {
				return false;
			}
		}

		final ValueRangeConstraint origLengthConstraint = origType.getValueRangeConstraint() != null ? origType.getValueRangeConstraint() : EMPTY_VALUE_RANGE_CONSTRAINT;
		final ValueRangeConstraint deviatedLengthConstraint = deviatedType.getValueRangeConstraint() != null ? deviatedType.getValueRangeConstraint() : EMPTY_VALUE_RANGE_CONSTRAINT;

		return minMaxAreEqual(origLengthConstraint.getMinValue(), origLengthConstraint.getMaxValue(), origLengthConstraint.getMinMaxRange(),
				deviatedLengthConstraint.getMinValue(), deviatedLengthConstraint.getMaxValue(), deviatedLengthConstraint.getMinMaxRange());
	}

	private static final DoubleValueRangeConstraint EMPTY_DOUBLE_VALUE_RANGE_CONSTRAINT = new DoubleValueRangeConstraint();

	private static boolean doubleTypesAreEqual(final DoubleType origType, final DoubleType deviatedType) {

		final DoubleValueRangeConstraint origLengthConstraint = origType.getValueRangeConstraint() != null ? origType.getValueRangeConstraint() : EMPTY_DOUBLE_VALUE_RANGE_CONSTRAINT;
		final DoubleValueRangeConstraint deviatedLengthConstraint = deviatedType.getValueRangeConstraint() != null ? deviatedType.getValueRangeConstraint() : EMPTY_DOUBLE_VALUE_RANGE_CONSTRAINT;

		return minMaxAreEqual(origLengthConstraint.getMinMaxRange(), deviatedLengthConstraint.getMinMaxRange());
	}

	private static final EqualsEvaluator<String> URN_EQUALS_EVALUATOR = (o1, o2) -> o1.equals(o2);

	private static boolean moRefTypesAreEqual(final MoRefType origType, final MoRefType deviatedType) {

		if(!PropertyUtils.valuesAreEqual(origType.getModelUrn(), deviatedType.getModelUrn())) {
			return false;
		}

		final List<String> origUrns = origType.getMoTypeConstraint().stream().map(ReferenceEndpointConstraint::getPrimaryTypeUrn).collect(Collectors.toList());
		final List<String> deviatedUrns = deviatedType.getMoTypeConstraint().stream().map(ReferenceEndpointConstraint::getPrimaryTypeUrn).collect(Collectors.toList());

		return PropertyUtils.listsAreEqual(origUrns, deviatedUrns, URN_EQUALS_EVALUATOR);
	}

	private static final StringContentsConstraint EMPTY_STRING_CONTENTS_CONSTRAINT = new StringContentsConstraint();
	private static final StringLengthConstraint EMPTY_STRING_LENGTH_CONSTRAINT = new StringLengthConstraint();

	private static boolean stringTypesAreEqual(final StringType origType, final StringType deviatedType) {

		final StringContentsConstraint origContentsConstraint = origType.getStringContentsConstraint() != null ? origType.getStringContentsConstraint() : EMPTY_STRING_CONTENTS_CONSTRAINT;
		final StringContentsConstraint deviatedContentsConstraint = deviatedType.getStringContentsConstraint() != null ? deviatedType.getStringContentsConstraint() : EMPTY_STRING_CONTENTS_CONSTRAINT;

		if(!PropertyUtils.valuesAreEqual(origContentsConstraint.getValue(), deviatedContentsConstraint.getValue())) {
			return false;
		}

		final StringLengthConstraint origLengthConstraint = origType.getStringLengthConstraint() != null ? origType.getStringLengthConstraint() : EMPTY_STRING_LENGTH_CONSTRAINT;
		final StringLengthConstraint deviatedLengthConstraint = deviatedType.getStringLengthConstraint() != null ? deviatedType.getStringLengthConstraint() : EMPTY_STRING_LENGTH_CONSTRAINT;

		return minMaxAreEqual(origLengthConstraint.getMinLength(), origLengthConstraint.getMaxLength(), origLengthConstraint.getMinMaxLength(),
				deviatedLengthConstraint.getMinLength(), deviatedLengthConstraint.getMaxLength(), deviatedLengthConstraint.getMinMaxLength());
	}

	private static boolean minMaxAreEqual(final MinMaxValue origMin, final MinMaxValue origMax, final List<MinMaxRange> origMinMax, final MinMaxValue deviatedMin, final MinMaxValue deviatedMax, final List<MinMaxRange> deviatedMinMax) {

		if(!PropertyUtils.valuesAreEqual(minMaxValueToLong(origMin), minMaxValueToLong(deviatedMin))) {
			return false;
		}
		if(!PropertyUtils.valuesAreEqual(minMaxValueToLong(origMax), minMaxValueToLong(deviatedMax))) {
			return false;
		}

		return PropertyUtils.listsAreEqual(origMinMax, deviatedMinMax, (o1,o2) -> o1.getMin() == o2.getMin() && o1.getMax() == o2.getMax());
	}

	private static boolean minMaxAreEqual(final List<DoubleMinMaxRange> origMinMax, final List<DoubleMinMaxRange> deviatedMinMax) {

		return PropertyUtils.listsAreEqual(origMinMax, deviatedMinMax, (o1,o2) -> o1.getMin() == o2.getMin() && o1.getMax() == o2.getMax());
	}

	private static Long minMaxValueToLong(final MinMaxValue minMaxValue) {
		return minMaxValue == null ? null : minMaxValue.getValue();
	}

	// =======================================================================================================================================

	/**
	 * Returns whether the data type is such to force a value for it to be supplied during runtime (i.e. should 
	 * be considered "mandatory").
	 */
	public static boolean isDataTypeConsideredMandatory(final TransformerContext context, final AbstractDataType type) {

		if(type instanceof CollectionType) {
			/*
			 * A collection (of anything) is considered mandatory if there must be at least a single member.
			 */
			final CollectionType collectionType = (CollectionType) type;
			final CollectionSizeConstraint sizeConstraint = collectionType.getCollectionSizeConstraint();

			if(sizeConstraint == null) {
				return false;
			}

			final MinMaxValue minSize = sizeConstraint.getMinSize();
			if(minSize != null) {
				return true;
			}

			if(!sizeConstraint.getMinMaxSize().isEmpty()) {
				return true;
			}

		} else if (type instanceof ComplexRefType) {
			/*
			 * It's a singleton struct. The struct itself is mandatory if it has a member that is mandatory.
			 */
			final ComplexRefType complexRefType = (ComplexRefType) type;
			final ModelInfo cdtModelInfo = ModelInfo.fromImpliedUrn(complexRefType.getModelUrn(), SchemaConstants.OSS_CDT);

			/*
			 * Fetch the CDT (it must exist - we have just generated it!), iterate over it and see if any
			 * of it's attributes are mandatory
			 */
			final ComplexDataTypeDefinition cdtDef = context.getCreatedEmodels().getGeneratedEModel(cdtModelInfo);
			return cdtDef.getAttribute().stream().anyMatch(ComplexDataTypeAttribute::isMandatory);
		}

		return false;
	}
}
