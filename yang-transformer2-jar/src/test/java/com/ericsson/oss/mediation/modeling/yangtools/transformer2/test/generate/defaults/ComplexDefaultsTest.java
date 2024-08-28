package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.defaults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.util.EnumMemberEditor;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.BooleanType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.CollectionValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.IntegerValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.UnionType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class ComplexDefaultsTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-defaults/";

	private static final String MODULE_NS = "urn_test_complex-defaults";
	private static final String MODULE_XYZ_VERSION = "2021.11.1";

	private static final String MODULE_NS_DEV = "urn-test-complex-defaults-dev";

	@Test
	public void test___complex_defaults___if_feature_removal() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P015_INVALID_SYNTAX_IN_DOCUMENT.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P018_ILLEGAL_CHILD_STATEMENT.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P057_DATA_TYPE_CHANGED.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "complex")));
		context.setFeatureHandling(FeatureHandling.REMOVE_ALL);
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(StringType.class, leaf11.getType());
		assertNull(leaf11.getDefault());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(StringType.class, leaf12.getType());
		assertInstanceOf(StringValue.class, leaf12.getDefault());
		assertEquals("abc", ((StringValue) leaf12.getDefault()).getValue());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(StringType.class, leaf13.getType());
		assertInstanceOf(StringValue.class, leaf13.getDefault());
		assertEquals("abc", ((StringValue) leaf13.getDefault()).getValue());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(StringType.class, leaf14.getType());
		assertNull(leaf14.getDefault());

		final PrimaryTypeAttribute leaf15 = findAttribute(cont1, "leaf15");
		assertInstanceOf(BooleanType.class, leaf15.getType());
		assertNull(leaf15.getDefault());

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);

		assertEmpty(cont1ext.getPrimaryTypeAttributeRemoval());
		assertSize(3, cont1ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf12ext = findReplacedAttribute(cont1ext, "leaf12");
		assertInstanceOf(StringType.class, leaf12ext.getType());
		assertNull(leaf12ext.getDefault());

		final PrimaryTypeAttribute leaf13ext = findReplacedAttribute(cont1ext, "leaf13");
		assertInstanceOf(StringType.class, leaf13ext.getType());
		assertInstanceOf(StringValue.class, leaf13ext.getDefault());
		assertEquals("def", ((StringValue) leaf13ext.getDefault()).getValue());

		final PrimaryTypeAttribute leaf14ext = findReplacedAttribute(cont1ext, "leaf14");
		assertInstanceOf(StringType.class, leaf14ext.getType());
		assertInstanceOf(StringValue.class, leaf14ext.getDefault());
		assertEquals("ghi", ((StringValue) leaf14ext.getDefault()).getValue());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertInstanceOf(UnionType.class, leaf21.getType());
		assertInstanceOf(IntegerValue.class, leaf21.getDefault());
		assertEquals((Integer) 10, ((IntegerValue) leaf21.getDefault()).getValue());

		final ModelInfo cont2ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont2");
		final PrimaryTypeExtensionDefinition cont2ext = load(cont2ExtensionModelInfo);
		assertSize(1, cont2ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf21ext = findReplacedAttribute(cont2ext, "leaf21");
		assertInstanceOf(UnionType.class, leaf21ext.getType());
		assertInstanceOf(StringValue.class, leaf21ext.getDefault());
		assertEquals("xyz", ((StringValue) leaf21ext.getDefault()).getValue());

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont3", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);

		final PrimaryTypeAttribute leaflist31 = findAttribute(cont3, "leaflist31");
		assertInstanceOf(ListType.class, leaflist31.getType());
		assertInstanceOf(StringType.class, ((CollectionType) leaflist31.getType()).getCollectionValuesType());
		assertInstanceOf(CollectionValue.class, leaflist31.getDefault());
		assertSize(2, ((CollectionValue) leaflist31.getDefault()).getValues().getValue());
		assertInstanceOf(StringValue.class, ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(0));
		assertInstanceOf(StringValue.class, ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(1));
		assertEquals("abc", ((StringValue) ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(0)).getValue());
		assertEquals("def", ((StringValue) ((CollectionValue) leaflist31.getDefault()).getValues().getValue().get(1)).getValue());

		final ModelInfo cont3ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont3");
		final PrimaryTypeExtensionDefinition cont3ext = load(cont3ExtensionModelInfo);

		assertSize(1, cont3ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaflist31ext = findReplacedAttribute(cont3ext, "leaflist31");
		assertInstanceOf(ListType.class, leaflist31ext.getType());
		assertInstanceOf(IntegerType.class, ((CollectionType) leaflist31ext.getType()).getCollectionValuesType());
		assertInstanceOf(CollectionValue.class, leaflist31ext.getDefault());
		assertSize(2, ((CollectionValue) leaflist31ext.getDefault()).getValues().getValue());
		assertInstanceOf(IntegerValue.class, ((CollectionValue) leaflist31ext.getDefault()).getValues().getValue().get(0));
		assertInstanceOf(IntegerValue.class, ((CollectionValue) leaflist31ext.getDefault()).getValues().getValue().get(1));
		assertEquals((Integer) 10, ((IntegerValue) ((CollectionValue) leaflist31ext.getDefault()).getValues().getValue().get(0)).getValue());
		assertEquals((Integer) 20, ((IntegerValue) ((CollectionValue) leaflist31ext.getDefault()).getValues().getValue().get(1)).getValue());

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont4", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);

		final PrimaryTypeAttribute leaf41 = findAttribute(cont4, "leaf41");
		assertInstanceOf(EnumRefType.class, leaf41.getType());
		assertInstanceOf(StringValue.class, leaf41.getDefault());
		assertEquals("identity-1", ((StringValue) leaf41.getDefault()).getValue());

		final ModelInfo cont4ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont4");
		final PrimaryTypeExtensionDefinition cont4ext = load(cont4ExtensionModelInfo);
		assertSize(1, cont4ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf41ext = findReplacedAttribute(cont4ext, "leaf41");
		assertInstanceOf(EnumRefType.class, leaf41ext.getType());
		assertInstanceOf(StringValue.class, leaf41ext.getDefault());
		assertEquals("identity-2", ((StringValue) leaf41ext.getDefault()).getValue());		// if-feature has removed the duplicate!

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.STRING, attr11.getDataTypeSpecification().getDataType());
		final String defaultLeaf11 = attr11.getDefaultValue();
		assertNull(defaultLeaf11);

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.STRING, attr12.getDataTypeSpecification().getDataType());
		final String defaultLeaf12 = attr12.getDefaultValue();
		assertNull(defaultLeaf12);

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.STRING, attr13.getDataTypeSpecification().getDataType());
		final String defaultLeaf13 = attr13.getDefaultValue();
		assertEquals("def", defaultLeaf13);

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertEquals(DataType.STRING, attr14.getDataTypeSpecification().getDataType());
		final String defaultLeaf14 = attr14.getDefaultValue();
		assertEquals("ghi", defaultLeaf14);

		final PrimaryTypeAttributeSpecification attr15 = findAttribute(cont1spec, "leaf15");
		assertEquals(DataType.BOOLEAN, attr15.getDataTypeSpecification().getDataType());
		final Boolean defaultLeaf15 = attr15.getDefaultValue();
		assertNull(defaultLeaf15);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertEquals(DataType.UNION, attr21.getDataTypeSpecification().getDataType());
		final String defaultLeaf21 = attr21.getDefaultValue();
		assertEquals("xyz", defaultLeaf21);

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont3spec, "leaflist31");
		assertEquals(DataType.LIST, attr31.getDataTypeSpecification().getDataType());
		final List<Integer> defaultAttr31 = attr31.getDefaultValue();
		assertEquals((Integer) 10, defaultAttr31.get(0));
		assertEquals((Integer) 20, defaultAttr31.get(1));

		final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr41 = findAttribute(cont4spec, "leaf41");
		assertEquals(DataType.ENUM_REF, attr41.getDataTypeSpecification().getDataType());
		final String defaultLeaf41 = attr41.getDefaultValue();
		assertEquals("identity-2", defaultLeaf41);
	}

	@Test
	public void test___complex_defaults___if_feature_retained() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P015_INVALID_SYNTAX_IN_DOCUMENT.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P018_ILLEGAL_CHILD_STATEMENT.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P057_DATA_TYPE_CHANGED.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "complex")));
		context.setFeatureHandling(FeatureHandling.RETAIN_ALL);
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont4", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);

		final PrimaryTypeAttribute leaf41 = findAttribute(cont4, "leaf41");
		assertInstanceOf(EnumRefType.class, leaf41.getType());
		assertInstanceOf(StringValue.class, leaf41.getDefault());
		assertEquals("identity-1", ((StringValue) leaf41.getDefault()).getValue());

		final ModelInfo cont4ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont4");
		final PrimaryTypeExtensionDefinition cont4ext = load(cont4ExtensionModelInfo);
		assertSize(1, cont4ext.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf41ext = findReplacedAttribute(cont4ext, "leaf41");
		assertInstanceOf(EnumRefType.class, leaf41ext.getType());
		assertInstanceOf(StringValue.class, leaf41ext.getDefault());
		assertEquals(EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_DEV, "identity-2"), ((StringValue) leaf41ext.getDefault()).getValue());		// duplicate, hence must have namespace!

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont4spec = modelService.getTypedAccess().getEModelSpecification(cont4modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr41 = findAttribute(cont4spec, "leaf41");
		assertEquals(DataType.ENUM_REF, attr41.getDataTypeSpecification().getDataType());
		final String defaultLeaf41 = attr41.getDefaultValue();
		assertEquals(EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_DEV, "identity-2"), defaultLeaf41);
	}
}