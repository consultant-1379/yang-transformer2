package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.edt.EnumDataTypeMemberSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.edt.EnumDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.IdentityRefGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;

public class IdentityRefNamespacesTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-datatypes/";

	private static final String MODULE_NS_ONE = "urn#test#identityref-namespaces-one";
	private static final String MODULE_NAME_ONE = "identityref-namespaces-one";
	private static final String MODULE_PREFIX_ONE = "one";
	private static final String MODULE_REVISION_ONE = "2021-07-30";
	private static final String MODULE_XYZ_VERSION_ONE = "2021.7.30";

	private static final String MODULE_NS_TWO = "urn/test/identityref-namespaces-two";
	private static final String MODULE_NAME_TWO = "identityref-namespaces-two";
	private static final String MODULE_PREFIX_TWO = "two";
	private static final String MODULE_REVISION_TWO = "2021-03-09";
	private static final String MODULE_XYZ_VERSION_TWO = "2021.3.9";

	@Test
	public void test___identity_ref_namespaces() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "identityref-namespaces"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(2, 0, 7, 3, 2, 0);

		// -----------------------------------------------------

		final ModelInfo yangModuleOne = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE);
		assertModelExists(yangModuleOne);

		final ModelInfo yangModuleTwo = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO);
		assertModelExists(yangModuleTwo);

		// -----------------------------------------------------

		final ModelInfo id1ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "one-1", MODULE_XYZ_VERSION_ONE);
		final EnumDataTypeDefinition id1edt = load(id1ModelInfo);
		assertEquals(yangModuleOne.toUrn(), id1edt.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(4, id1edt.getMember());
		findAndAssertEnumMember(id1edt, "one-1");
		findAndAssertEnumMember(id1edt, "one-2");
		findAndAssertEnumMember(id1edt, "one-3");
		findAndAssertEnumMember(id1edt, "one-10");
		assertNull(findEnumMember(id1edt, "two-6"));		// should sit in the extension
		assertNull(findEnumMember(id1edt, "two-7"));		// should sit in the extension
		assertNull(findEnumMember(id1edt, "two-8"));		// should sit in the extension
		assertEquals(MODULE_NS_ONE, findEnumMember(id1edt, "one-1").getNamespace());
		assertEquals(MODULE_NS_ONE, findEnumMember(id1edt, "one-2").getNamespace());
		assertEquals(MODULE_NS_ONE, findEnumMember(id1edt, "one-3").getNamespace());
		assertEquals(MODULE_NS_ONE, findEnumMember(id1edt, "one-10").getNamespace());
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(id1edt, "one-1"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(id1edt, "one-2"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(id1edt, "one-3"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(id1edt, "one-10"));

		final ModelInfo id1extModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, id1ModelInfo.getNamespace(), id1ModelInfo.getName());
		final EnumDataTypeExtensionDefinition id1EdtExt = load(id1extModelInfo);
		assertEquals(id1ModelInfo.toImpliedUrn(), id1EdtExt.getExtendedModelElement().get(0).getUrn());
		assertRequiresTargetType(context, null, id1EdtExt.getRequiresTargetType());
		assertEquals(yangModuleOne.toUrn(), id1EdtExt.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(3, id1EdtExt.getEnumDataTypeExtension().getMember());
		assertEquals(MODULE_NS_TWO, findAndAssertAddedEnumMember(id1EdtExt, "two-6").getNamespace());
		assertEquals(MODULE_NS_TWO, findAndAssertAddedEnumMember(id1EdtExt, "two-7").getNamespace());
		assertEquals(MODULE_NS_TWO, findAndAssertAddedEnumMember(id1EdtExt, "two-8").getNamespace());
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findAddedEnumMember(id1EdtExt, "two-6"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findAddedEnumMember(id1EdtExt, "two-7"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findAddedEnumMember(id1EdtExt, "two-8"));

		final ModelInfo id2ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "one-2", MODULE_XYZ_VERSION_ONE);
		final EnumDataTypeDefinition id2edt = load(id2ModelInfo);

		assertSize(2, id2edt.getMember());
		findAndAssertEnumMember(id2edt, "one-2");
		findAndAssertEnumMember(id2edt, "one-3");
		assertNull(findEnumMember(id2edt, "two-7"));		// should sit in the extension
		assertNull(findEnumMember(id2edt, "two-8"));		// should sit in the extension
		assertEquals(MODULE_NS_ONE, findEnumMember(id2edt, "one-2").getNamespace());
		assertEquals(MODULE_NS_ONE, findEnumMember(id2edt, "one-3").getNamespace());
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(id2edt, "one-2"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(id2edt, "one-3"));

		final ModelInfo id2extModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, id2ModelInfo.getNamespace(), id2ModelInfo.getName());
		final EnumDataTypeExtensionDefinition id2EdtExt = load(id2extModelInfo);
		assertEquals(id2ModelInfo.toImpliedUrn(), id2EdtExt.getExtendedModelElement().get(0).getUrn());
		assertRequiresTargetType(context, null, id2EdtExt.getRequiresTargetType());

		assertSize(2, id2EdtExt.getEnumDataTypeExtension().getMember());
		assertEquals(MODULE_NS_TWO, findAndAssertAddedEnumMember(id2EdtExt, "two-7").getNamespace());
		assertEquals(MODULE_NS_TWO, findAndAssertAddedEnumMember(id2EdtExt, "two-8").getNamespace());
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findAddedEnumMember(id2EdtExt, "two-7"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findAddedEnumMember(id2EdtExt, "two-8"));

		final ModelInfo id3ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "one-3", MODULE_XYZ_VERSION_ONE);
		final EnumDataTypeDefinition id3edt = load(id3ModelInfo);
		assertSize(1, id3edt.getMember());
		findAndAssertEnumMember(id3edt, "one-3");
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(id3edt, "one-3"));

		final ModelInfo id9ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "one-9", MODULE_XYZ_VERSION_ONE);
		assertModelDoesNotExist(id9ModelInfo);		// used as part of *2 bases*

		final ModelInfo id6ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_TWO, "two-6", MODULE_XYZ_VERSION_TWO);
		final EnumDataTypeDefinition id6edt = load(id6ModelInfo);
		assertEquals(yangModuleTwo.toUrn(), id6edt.getModelCreationInfo().getDerivedModel().getDerivedFrom());

		assertSize(1, id6edt.getMember());
		findAndAssertEnumMember(id6edt, "two-6");
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findEnumMember(id6edt, "two-6"));

		final ModelInfo id7ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_TWO, "two-7", MODULE_XYZ_VERSION_TWO);
		final EnumDataTypeDefinition id7edt = load(id7ModelInfo);
		assertSize(2, id7edt.getMember());
		findAndAssertEnumMember(id7edt, "two-7");
		findAndAssertEnumMember(id7edt, "two-8");
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findEnumMember(id7edt, "two-7"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findEnumMember(id7edt, "two-8"));

		final ModelInfo id8ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_TWO, "two-8", MODULE_XYZ_VERSION_TWO);
		final EnumDataTypeDefinition id8edt = load(id8ModelInfo);
		assertSize(1, id8edt.getMember());
		findAndAssertEnumMember(id8edt, "two-8");
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findEnumMember(id8edt, "two-8"));

		final ModelInfo idForLeaf25ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithMultipleBases(MODULE_NS_TWO, "cont2__leaf25", MODULE_XYZ_VERSION_TWO, null);
		final EnumDataTypeDefinition idForLeaf25edt = load(idForLeaf25ModelInfo);
		assertEquals(yangModuleTwo.toUrn(), idForLeaf25edt.getModelCreationInfo().getDerivedModel().getDerivedFrom());
		assertEmpty(idForLeaf25edt.getMember());

		final ModelInfo extForLeaf25ModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, idForLeaf25ModelInfo.getNamespace(), idForLeaf25ModelInfo.getName());
		final EnumDataTypeExtensionDefinition leaf25EdtExt = load(extForLeaf25ModelInfo);
		assertEquals(idForLeaf25ModelInfo.toImpliedUrn(), leaf25EdtExt.getExtendedModelElement().get(0).getUrn());
		assertRequiresTargetType(context, null, leaf25EdtExt.getRequiresTargetType());

		assertSize(2, leaf25EdtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(leaf25EdtExt, MODULE_NS_ONE, "one-3");
		findAndAssertAddedEnumMember(leaf25EdtExt, MODULE_NS_ONE, "one-9");

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont1", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(EnumRefType.class, leaf11.getType());
		assertEquals(id1ModelInfo.toImpliedUrn(), ((EnumRefType) leaf11.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(EnumRefType.class, leaf12.getType());
		assertEquals(id2ModelInfo.toImpliedUrn(), ((EnumRefType) leaf12.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(EnumRefType.class, leaf13.getType());
		assertEquals(id3ModelInfo.toImpliedUrn(), ((EnumRefType) leaf13.getType()).getModelUrn());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont2", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertInstanceOf(EnumRefType.class, leaf21.getType());
		assertEquals(id6ModelInfo.toImpliedUrn(), ((EnumRefType) leaf21.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf22 = findAttribute(cont2, "leaf22");
		assertInstanceOf(EnumRefType.class, leaf22.getType());
		assertEquals(id7ModelInfo.toImpliedUrn(), ((EnumRefType) leaf22.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf23 = findAttribute(cont2, "leaf23");
		assertInstanceOf(EnumRefType.class, leaf23.getType());
		assertEquals(id8ModelInfo.toImpliedUrn(), ((EnumRefType) leaf23.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf24 = findAttribute(cont2, "leaf24");
		assertInstanceOf(EnumRefType.class, leaf24.getType());
		assertEquals(id3ModelInfo.toImpliedUrn(), ((EnumRefType) leaf24.getType()).getModelUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertEquals(DataType.ENUM_REF, attr11.getDataTypeSpecification().getDataType()); 
		assertEquals(id1ModelInfo, attr11.getDataTypeSpecification().getReferencedDataType()); 

		assertValidValue(attr11, "one-1");
		assertValidValue(attr11, "one-2");
		assertValidValue(attr11, "one-3");
		assertValidValue(attr11, "one-10");
		assertValidValue(attr11, "two-6");
		assertValidValue(attr11, "two-7");
		assertValidValue(attr11, "two-8");

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertEquals(DataType.ENUM_REF, attr12.getDataTypeSpecification().getDataType()); 
		assertEquals(id2ModelInfo, attr12.getDataTypeSpecification().getReferencedDataType()); 

		assertInvalidValue(attr12, "one-1");
		assertValidValue(attr12, "one-2");
		assertValidValue(attr12, "one-3");
		assertInvalidValue(attr12, "one-10");
		assertInvalidValue(attr12, "two-6");
		assertValidValue(attr12, "two-7");
		assertValidValue(attr12, "two-8");

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.ENUM_REF, attr13.getDataTypeSpecification().getDataType()); 
		assertEquals(id3ModelInfo, attr13.getDataTypeSpecification().getReferencedDataType()); 

		assertInvalidValue(attr13, "one-1");
		assertInvalidValue(attr13, "one-2");
		assertValidValue(attr13, "one-3");
		assertInvalidValue(attr13, "two-6");
		assertInvalidValue(attr13, "two-7");
		assertInvalidValue(attr13, "two-8");

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertEquals(DataType.ENUM_REF, attr21.getDataTypeSpecification().getDataType()); 
		assertEquals(id6ModelInfo, attr21.getDataTypeSpecification().getReferencedDataType()); 

		assertInvalidValue(attr21, "one-1");
		assertInvalidValue(attr21, "one-2");
		assertInvalidValue(attr21, "one-3");
		assertValidValue(attr21, "two-6");
		assertInvalidValue(attr21, "two-7");
		assertInvalidValue(attr21, "two-8");

		final PrimaryTypeAttributeSpecification attr22 = findAttribute(cont2spec, "leaf22");
		assertEquals(DataType.ENUM_REF, attr22.getDataTypeSpecification().getDataType()); 
		assertEquals(id7ModelInfo, attr22.getDataTypeSpecification().getReferencedDataType()); 

		assertInvalidValue(attr22, "one-1");
		assertInvalidValue(attr22, "one-2");
		assertInvalidValue(attr22, "one-3");
		assertInvalidValue(attr22, "two-6");
		assertValidValue(attr22, "two-7");
		assertValidValue(attr22, "two-8");

		final PrimaryTypeAttributeSpecification attr23 = findAttribute(cont2spec, "leaf23");
		assertEquals(DataType.ENUM_REF, attr23.getDataTypeSpecification().getDataType()); 
		assertEquals(id8ModelInfo, attr23.getDataTypeSpecification().getReferencedDataType()); 

		assertInvalidValue(attr23, "one-1");
		assertInvalidValue(attr23, "one-2");
		assertInvalidValue(attr23, "one-3");
		assertInvalidValue(attr23, "two-6");
		assertInvalidValue(attr23, "two-7");
		assertValidValue(attr23, "two-8");

		final PrimaryTypeAttributeSpecification attr24 = findAttribute(cont2spec, "leaf24");
		assertEquals(DataType.ENUM_REF, attr24.getDataTypeSpecification().getDataType()); 
		assertEquals(id3ModelInfo, attr24.getDataTypeSpecification().getReferencedDataType()); 

		assertInvalidValue(attr24, "one-1");
		assertInvalidValue(attr24, "one-2");
		assertValidValue(attr24, "one-3");
		assertInvalidValue(attr24, "two-6");
		assertInvalidValue(attr24, "two-7");
		assertInvalidValue(attr24, "two-8");

		// --------------------------------------

		{
			/*
			 * Using correct Target object
			 */
			final EnumDataTypeSpecification id1spec = modelService.getTypedAccess().getEModelSpecification(id1ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(7, id1spec.getAllMembers());

			final EnumDataTypeMemberSpecification id1specMember1 = findAndAssertEnumMember(id1spec, "one-1");
			assertEquals(MODULE_NS_ONE, id1specMember1.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember2 = findAndAssertEnumMember(id1spec, "one-2");
			assertEquals(MODULE_NS_ONE, id1specMember2.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember3 = findAndAssertEnumMember(id1spec, "one-3");
			assertEquals(MODULE_NS_ONE, id1specMember3.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember10 = findAndAssertEnumMember(id1spec, "one-10");
			assertEquals(MODULE_NS_ONE, id1specMember10.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember6 = findAndAssertEnumMember(id1spec, "two-6");
			assertEquals(MODULE_NS_TWO, id1specMember6.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember7 = findAndAssertEnumMember(id1spec, "two-7");
			assertEquals(MODULE_NS_TWO, id1specMember7.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember8 = findAndAssertEnumMember(id1spec, "two-8");
			assertEquals(MODULE_NS_TWO, id1specMember8.getMemberNamespace());

			final EnumDataTypeSpecification id2spec = modelService.getTypedAccess().getEModelSpecification(id2ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(4, id2spec.getAllMembers());

			final EnumDataTypeMemberSpecification id2specMember2 = findAndAssertEnumMember(id2spec, "one-2");
			assertEquals(MODULE_NS_ONE, id2specMember2.getMemberNamespace());

			final EnumDataTypeMemberSpecification id2specMember3 = findAndAssertEnumMember(id2spec, "one-3");
			assertEquals(MODULE_NS_ONE, id2specMember3.getMemberNamespace());

			final EnumDataTypeMemberSpecification id2specMember7 = findAndAssertEnumMember(id2spec, "two-7");
			assertEquals(MODULE_NS_TWO, id2specMember7.getMemberNamespace());

			final EnumDataTypeMemberSpecification id2specMember8 = findAndAssertEnumMember(id2spec, "two-8");
			assertEquals(MODULE_NS_TWO, id2specMember8.getMemberNamespace());

			final EnumDataTypeSpecification id3spec = modelService.getTypedAccess().getEModelSpecification(id3ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(1, id3spec.getAllMembers());

			final EnumDataTypeMemberSpecification id3specMember3 = findAndAssertEnumMember(id3spec, "one-3");
			assertEquals(MODULE_NS_ONE, id3specMember3.getMemberNamespace());

			final EnumDataTypeSpecification id6spec = modelService.getTypedAccess().getEModelSpecification(id6ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(1, id6spec.getAllMembers());

			final EnumDataTypeMemberSpecification id6specMember6 = findAndAssertEnumMember(id6spec, "two-6");
			assertEquals(MODULE_NS_TWO, id6specMember6.getMemberNamespace());

			final EnumDataTypeSpecification id7spec = modelService.getTypedAccess().getEModelSpecification(id7ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(2, id7spec.getAllMembers());

			final EnumDataTypeMemberSpecification id7specMember7 = findAndAssertEnumMember(id7spec, "two-7");
			assertEquals(MODULE_NS_TWO, id7specMember7.getMemberNamespace());

			final EnumDataTypeMemberSpecification id7specMember8 = findAndAssertEnumMember(id7spec, "two-8");
			assertEquals(MODULE_NS_TWO, id7specMember8.getMemberNamespace());

			final EnumDataTypeSpecification id8spec = modelService.getTypedAccess().getEModelSpecification(id8ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(1, id8spec.getAllMembers());

			final EnumDataTypeMemberSpecification id8specMember8 = findAndAssertEnumMember(id8spec, "two-8");
			assertEquals(MODULE_NS_TWO, id8specMember8.getMemberNamespace());
		}

		{
			/*
			 * Using null Target object - additions will be merged in, removals not.
			 */
			final EnumDataTypeSpecification id1spec = modelService.getTypedAccess().getEModelSpecification(id1ModelInfo, EnumDataTypeSpecification.class);
			assertSize(7, id1spec.getAllMembers());

			final EnumDataTypeMemberSpecification id1specMember1 = findAndAssertEnumMember(id1spec, "one-1");
			assertEquals(MODULE_NS_ONE, id1specMember1.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember2 = findAndAssertEnumMember(id1spec, "one-2");
			assertEquals(MODULE_NS_ONE, id1specMember2.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember3 = findAndAssertEnumMember(id1spec, "one-3");
			assertEquals(MODULE_NS_ONE, id1specMember3.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember10 = findAndAssertEnumMember(id1spec, "one-10");
			assertEquals(MODULE_NS_ONE, id1specMember10.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember6 = findAndAssertEnumMember(id1spec, "two-6");
			assertEquals(MODULE_NS_TWO, id1specMember6.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember7 = findAndAssertEnumMember(id1spec, "two-7");
			assertEquals(MODULE_NS_TWO, id1specMember7.getMemberNamespace());

			final EnumDataTypeMemberSpecification id1specMember8 = findAndAssertEnumMember(id1spec, "two-8");
			assertEquals(MODULE_NS_TWO, id1specMember8.getMemberNamespace());

			final EnumDataTypeSpecification id2spec = modelService.getTypedAccess().getEModelSpecification(id2ModelInfo, EnumDataTypeSpecification.class);
			assertSize(4, id2spec.getAllMembers());

			final EnumDataTypeMemberSpecification id2specMember2 = findAndAssertEnumMember(id2spec, "one-2");
			assertEquals(MODULE_NS_ONE, id2specMember2.getMemberNamespace());

			final EnumDataTypeMemberSpecification id2specMember3 = findAndAssertEnumMember(id2spec, "one-3");
			assertEquals(MODULE_NS_ONE, id2specMember3.getMemberNamespace());

			final EnumDataTypeMemberSpecification id2specMember7 = findAndAssertEnumMember(id2spec, "two-7");
			assertEquals(MODULE_NS_TWO, id2specMember7.getMemberNamespace());

			final EnumDataTypeMemberSpecification id2specMember8 = findAndAssertEnumMember(id2spec, "two-8");
			assertEquals(MODULE_NS_TWO, id2specMember8.getMemberNamespace());

			final EnumDataTypeSpecification id3spec = modelService.getTypedAccess().getEModelSpecification(id3ModelInfo, EnumDataTypeSpecification.class);
			assertSize(1, id3spec.getAllMembers());

			final EnumDataTypeMemberSpecification id3specMember3 = findAndAssertEnumMember(id3spec, "one-3");
			assertEquals(MODULE_NS_ONE, id3specMember3.getMemberNamespace());

			final EnumDataTypeSpecification id6spec = modelService.getTypedAccess().getEModelSpecification(id6ModelInfo, EnumDataTypeSpecification.class);
			assertSize(1, id6spec.getAllMembers());

			final EnumDataTypeMemberSpecification id6specMember6 = findAndAssertEnumMember(id6spec, "two-6");
			assertEquals(MODULE_NS_TWO, id6specMember6.getMemberNamespace());

			final EnumDataTypeSpecification id7spec = modelService.getTypedAccess().getEModelSpecification(id7ModelInfo, EnumDataTypeSpecification.class);
			assertSize(2, id7spec.getAllMembers());

			final EnumDataTypeMemberSpecification id7specMember7 = findAndAssertEnumMember(id7spec, "two-7");
			assertEquals(MODULE_NS_TWO, id7specMember7.getMemberNamespace());

			final EnumDataTypeMemberSpecification id7specMember8 = findAndAssertEnumMember(id7spec, "two-8");
			assertEquals(MODULE_NS_TWO, id7specMember8.getMemberNamespace());

			final EnumDataTypeSpecification id8spec = modelService.getTypedAccess().getEModelSpecification(id8ModelInfo, EnumDataTypeSpecification.class);
			assertSize(1, id8spec.getAllMembers());

			final EnumDataTypeMemberSpecification id8specMember8 = findAndAssertEnumMember(id8spec, "two-8");
			assertEquals(MODULE_NS_TWO, id8specMember8.getMemberNamespace());
		}
	}

	@Test
	public void test___identity_ref_namespaces___remove_if_feature_nodes() {

		/*
		 * Identity "one-3" and "one-10" and "two-8" will effectively be removed. Some leafs are therefore "dangling" (they are referring to a base that doesn't exist).
		 */

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "identityref-namespaces"));
		context.setFeatureHandling(FeatureHandling.REMOVE_ALL);
		YangTransformer2.transform(context);

		assertEModelCreatedCount(2, 0, 5, 3, 2, 0);

		// -----------------------------------------------------

		/*
		 * For one-1, we would expect:
		 * - in the edt model {1,2,3}
		 * - in the edt ext model {ADDED 6, 7} (but 8 is not added due to if-feature removal)
		 * - in the edt ext model {REMOVED 3, 10}  (due to if-feature removal)
		 */

		final ModelInfo id1ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "one-1", MODULE_XYZ_VERSION_ONE);
		final EnumDataTypeDefinition id1edt = load(id1ModelInfo);

		assertSize(4, id1edt.getMember());
		findAndAssertEnumMember(id1edt, "one-1");
		findAndAssertEnumMember(id1edt, "one-2");
		findAndAssertEnumMember(id1edt, "one-3");
		findAndAssertEnumMember(id1edt, "one-10");

		final ModelInfo id1extModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, id1ModelInfo.getNamespace(), id1ModelInfo.getName());
		final EnumDataTypeExtensionDefinition id1EdtExt = load(id1extModelInfo);

		assertSize(2, id1EdtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(id1EdtExt, MODULE_NS_TWO, "two-6");
		findAndAssertAddedEnumMember(id1EdtExt, MODULE_NS_TWO, "two-7");
		assertSize(2, id1EdtExt.getEnumDataTypeRemoval().getMemberWithNamespace());
		assertEnumMemberRemoved(id1EdtExt, MODULE_NS_ONE, "one-3");
		assertEnumMemberRemoved(id1EdtExt, MODULE_NS_ONE, "one-10");

		/*
		 * For one-2, we would expect:
		 * - in the edt model {2,3}
		 * - in the edt ext model {ADDED 7} (but 8 is not added due to if-feature removal)
		 * - in the edt ext model {REMOVED 3}  (due to if-feature removal)
		 */
		final ModelInfo id2ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "one-2", MODULE_XYZ_VERSION_ONE);
		final EnumDataTypeDefinition id2edt = load(id2ModelInfo);

		assertSize(2, id2edt.getMember());
		findAndAssertEnumMember(id2edt, "one-2");
		findAndAssertEnumMember(id2edt, "one-3");

		final ModelInfo id2extModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, id2ModelInfo.getNamespace(), id2ModelInfo.getName());
		final EnumDataTypeExtensionDefinition id2EdtExt = load(id2extModelInfo);

		assertSize(1, id2EdtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(id2EdtExt, MODULE_NS_TWO, "two-7");
		assertSize(1, id2EdtExt.getEnumDataTypeRemoval().getMemberWithNamespace());
		assertEnumMemberRemoved(id2EdtExt, MODULE_NS_ONE, "one-3");

		/*
		 * The EDT for one-3 does not exist, as the identity does not exist in the schema.
		 */
		final ModelInfo id3ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "one-3", MODULE_XYZ_VERSION_ONE);
		assertModelDoesNotExist(id3ModelInfo);

		/*
		 * No change to two-6
		 */
		final ModelInfo id6ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_TWO, "two-6", MODULE_XYZ_VERSION_TWO);
		final EnumDataTypeDefinition id6edt = load(id6ModelInfo);
		assertSize(1, id6edt.getMember());
		findAndAssertEnumMember(id6edt, "two-6");

		/*
		 * For two-7, we would expect:
		 * - in the edt model {7,8}
		 * - in the edt ext model {REMOVED 8}  (due to if-feature removal)
		 */
		final ModelInfo id7ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_TWO, "two-7", MODULE_XYZ_VERSION_TWO);
		final EnumDataTypeDefinition id7edt = load(id7ModelInfo);
		assertSize(2, id7edt.getMember());
		findAndAssertEnumMember(id7edt, "two-7");
		findAndAssertEnumMember(id7edt, "two-8");

		final ModelInfo id7extModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, id7ModelInfo.getNamespace(), id7ModelInfo.getName());
		final EnumDataTypeExtensionDefinition id7EdtExt = load(id7extModelInfo);

		assertNull(id7EdtExt.getEnumDataTypeExtension());
		assertSize(1, id7EdtExt.getEnumDataTypeRemoval().getMemberWithNamespace());
		assertEnumMemberRemoved(id7EdtExt, MODULE_NS_TWO, "two-8");

		/*
		 * The EDT for two-8 does not exist, as the identity does not exist in the schema.
		 */
		final ModelInfo id8ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "two-8", MODULE_XYZ_VERSION_ONE);
		assertModelDoesNotExist(id8ModelInfo);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont1", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(EnumRefType.class, leaf11.getType());
		assertEquals(id1ModelInfo.toImpliedUrn(), ((EnumRefType) leaf11.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(EnumRefType.class, leaf12.getType());
		assertEquals(id2ModelInfo.toImpliedUrn(), ((EnumRefType) leaf12.getType()).getModelUrn());

		/*
		 * The base for leaf13 does not exist due to if-feature. It should be generated as string.
		 */
		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(StringType.class, leaf13.getType());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "cont2", MODULE_XYZ_VERSION_TWO);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertInstanceOf(EnumRefType.class, leaf21.getType());
		assertEquals(id6ModelInfo.toImpliedUrn(), ((EnumRefType) leaf21.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf22 = findAttribute(cont2, "leaf22");
		assertInstanceOf(EnumRefType.class, leaf22.getType());
		assertEquals(id7ModelInfo.toImpliedUrn(), ((EnumRefType) leaf22.getType()).getModelUrn());

		/*
		 * The base for leaf23 does not exist due to if-feature. It should be generated as string.
		 */
		final PrimaryTypeAttribute leaf23 = findAttribute(cont2, "leaf23");
		assertInstanceOf(StringType.class, leaf23.getType());

		/*
		 * The base for leaf24 does not exist due to if-feature. It should be generated as string.
		 */
		final PrimaryTypeAttribute leaf24 = findAttribute(cont2, "leaf24");
		assertInstanceOf(StringType.class, leaf24.getType());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");

		assertValidValue(attr11, "one-1");
		assertValidValue(attr11, "one-2");
		assertInvalidValue(attr11, "one-3");	// removed due to if-feature
		assertInvalidValue(attr11, "one-10");	// removed due to if-feature
		assertValidValue(attr11, "two-6");
		assertValidValue(attr11, "two-7");
		assertInvalidValue(attr11, "two-8");	// removed due to if-feature

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");

		assertInvalidValue(attr12, "one-1");
		assertValidValue(attr12, "one-2");
		assertInvalidValue(attr12, "one-3");	// removed due to if-feature
		assertInvalidValue(attr12, "two-6");
		assertValidValue(attr12, "two-7");
		assertInvalidValue(attr12, "two-8");	// removed due to if-feature

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertEquals(DataType.STRING, attr13.getDataTypeSpecification().getDataType());		// dangling identityref, will be generated as String 

		assertValidValue(attr13, "one-1");	// valid, data type is string
		assertValidValue(attr13, "one-2");	// valid, data type is string
		assertValidValue(attr13, "one-3");	// valid, data type is string
		assertValidValue(attr13, "two-6");	// valid, data type is string
		assertValidValue(attr13, "two-7");	// valid, data type is string
		assertValidValue(attr13, "two-8");	// valid, data type is string

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");

		assertInvalidValue(attr21, "one-1");
		assertInvalidValue(attr21, "one-2");
		assertInvalidValue(attr21, "one-3");
		assertValidValue(attr21, "two-6");
		assertInvalidValue(attr21, "two-7");
		assertInvalidValue(attr21, "two-8");

		final PrimaryTypeAttributeSpecification attr22 = findAttribute(cont2spec, "leaf22");

		assertInvalidValue(attr22, "one-1");
		assertInvalidValue(attr22, "one-2");
		assertInvalidValue(attr22, "one-3");
		assertInvalidValue(attr22, "two-6");
		assertValidValue(attr22, "two-7");
		assertInvalidValue(attr22, "two-8");	// removed due to if-feature

		final PrimaryTypeAttributeSpecification attr23 = findAttribute(cont2spec, "leaf23");
		assertEquals(DataType.STRING, attr23.getDataTypeSpecification().getDataType());  		// dangling identityref, will be generated as String 

		assertValidValue(attr23, "one-1");	// valid, data type is string
		assertValidValue(attr23, "one-2");	// valid, data type is string
		assertValidValue(attr23, "one-3");	// valid, data type is string
		assertValidValue(attr23, "two-6");	// valid, data type is string
		assertValidValue(attr23, "two-7");	// valid, data type is string
		assertValidValue(attr23, "two-8");	// valid, data type is string

		final PrimaryTypeAttributeSpecification attr24 = findAttribute(cont2spec, "leaf24");
		assertEquals(DataType.STRING, attr24.getDataTypeSpecification().getDataType()); 		// dangling identityref, will be generated as String 

		assertValidValue(attr24, "one-1");	// valid, data type is string
		assertValidValue(attr24, "one-2");	// valid, data type is string
		assertValidValue(attr24, "one-3");	// valid, data type is string
		assertValidValue(attr24, "two-6");	// valid, data type is string
		assertValidValue(attr24, "two-7");	// valid, data type is string
		assertValidValue(attr24, "two-8");	// valid, data type is string

		// --------------------------------------

		{
			/*
			 * Using correct Target object
			 */
			final EnumDataTypeSpecification id1spec = modelService.getTypedAccess().getEModelSpecification(id1ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(4, id1spec.getAllMembers());

			findAndAssertEnumMember(id1spec, "one-1");
			findAndAssertEnumMember(id1spec, "one-2");
			assertNull(findEnumMember(id1spec, "one-3"));		// does not exist in model due to if-feature, so should not exist.
			findAndAssertEnumMember(id1spec, "two-6");
			findAndAssertEnumMember(id1spec, "two-7");
			assertNull(findEnumMember(id1spec, "two-8"));		// does not exist in model due to if-feature, so should not exist.

			final EnumDataTypeSpecification id2spec = modelService.getTypedAccess().getEModelSpecification(id2ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(2, id2spec.getAllMembers());

			findAndAssertEnumMember(id2spec, "one-2");
			assertNull(findEnumMember(id2spec, "one-3"));		// does not exist in model due to if-feature, so should not exist.
			findAndAssertEnumMember(id2spec, "two-7");
			assertNull(findEnumMember(id2spec, "two-8"));		// does not exist in model due to if-feature, so should not exist.

			assertModelDoesNotExistInModelService(modelService, id3ModelInfo);	// Has been removed due to if-feature, so should not have been generated.

			final EnumDataTypeSpecification id6spec = modelService.getTypedAccess().getEModelSpecification(id6ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(1, id6spec.getAllMembers());

			findAndAssertEnumMember(id6spec, "two-6");

			final EnumDataTypeSpecification id7spec = modelService.getTypedAccess().getEModelSpecification(id7ModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
			assertSize(1, id7spec.getAllMembers());

			findAndAssertEnumMember(id7spec, "two-7");
			assertNull(findEnumMember(id7spec, "two-8"));		// does not exist in model due to if-feature, so should not exist.

			assertModelDoesNotExistInModelService(modelService, id8ModelInfo);	// Has been removed due to if-feature, so should not have been generated.
		}

		{
			/*
			 * Using null Target object - additions will be merged in, removals not.
			 */
			final EnumDataTypeSpecification id1spec = modelService.getTypedAccess().getEModelSpecification(id1ModelInfo, EnumDataTypeSpecification.class);
			assertSize(6, id1spec.getAllMembers());

			findAndAssertEnumMember(id1spec, "one-1");
			findAndAssertEnumMember(id1spec, "one-2");
			findAndAssertEnumMember(id1spec, "one-3");
			findAndAssertEnumMember(id1spec, "one-10");
			findAndAssertEnumMember(id1spec, "two-6");
			findAndAssertEnumMember(id1spec, "two-7");
			assertNull(findEnumMember(id1spec, "two-8"));		// not added in extension, so does not exist here.

			final EnumDataTypeSpecification id2spec = modelService.getTypedAccess().getEModelSpecification(id2ModelInfo, EnumDataTypeSpecification.class);
			assertSize(3, id2spec.getAllMembers());

			findAndAssertEnumMember(id2spec, "one-2");
			findAndAssertEnumMember(id2spec, "one-3");
			findAndAssertEnumMember(id2spec, "two-7");
			assertNull(findEnumMember(id2spec, "two-8"));		// not added in extension, so does not exist here.

			assertModelDoesNotExistInModelService(modelService, id3ModelInfo);	// Has been removed due to if-feature, so should not have been generated.

			final EnumDataTypeSpecification id6spec = modelService.getTypedAccess().getEModelSpecification(id6ModelInfo, EnumDataTypeSpecification.class);
			assertSize(1, id6spec.getAllMembers());

			findAndAssertEnumMember(id6spec, "two-6");

			final EnumDataTypeSpecification id7spec = modelService.getTypedAccess().getEModelSpecification(id7ModelInfo, EnumDataTypeSpecification.class);
			assertSize(2, id7spec.getAllMembers());

			findAndAssertEnumMember(id7spec, "two-7");
			findAndAssertEnumMember(id7spec, "two-8");		// removed by extension, but extension not merged in, so exists here.

			assertModelDoesNotExistInModelService(modelService, id8ModelInfo);	// Has been removed due to if-feature, so should not have been generated.
		}
	}
}
