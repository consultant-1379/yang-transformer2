package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ShortType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.UnionType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeMember;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.YangIdentity;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.IdentityRefGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class IdentityRefSimpleTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-datatypes/";

	private static final String MODULE_NS = "urn:test:identityref-simple";
	private static final String MODULE_NAME = "identityref-simple";
	private static final String MODULE_REVISION = "2021-10-15";
	private static final String MODULE_XYZ_VERSION = "2021.10.15";

	private static final String SUB_MODULE_NAME = "identityref-simple-submodule";
	private static final String SUB_MODULE_REVISION = "2021-01-31";

	@Test
	public void test___identity_ref_simple() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S325_REFERENCED_IDENTITY_NOT_FOUND.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "identityref-simple"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(2, 1, 7, 5, 2, 0);

		// -----------------------------------------------------

		final ModelInfo yangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, MODULE_NAME, MODULE_REVISION);
		assertModelExists(yangModule);

		final ModelInfo yangSubmodule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS, SUB_MODULE_NAME, SUB_MODULE_REVISION);
		assertModelExists(yangSubmodule);

		// -----------------------------------------------------

		final ModelInfo id1ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-1", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id1edt = load(id1ModelInfo);
		assertNotNull(id1edt.getModelCreationInfo().getDerivedModel());
		assertEquals(yangModule.toUrn(), id1edt.getModelCreationInfo().getDerivedModel().getDerivedFrom());
		assertSize(4, id1edt.getMember());

		final EnumDataTypeMember edt1member1 = findAndAssertEnumMember(id1edt, "id-1");
		assertEquals(MODULE_NS, edt1member1.getNamespace());
		assertEquals(IdentityRefGenerator.getMemberValueForIdentity(new YangIdentity(MODULE_NS, MODULE_NAME, "id-1")), edt1member1.getValue());

		final EnumDataTypeMember edt1member3 = findAndAssertEnumMember(id1edt, "id-3");
		assertEquals(MODULE_NS, edt1member3.getNamespace());
		assertEquals(IdentityRefGenerator.getMemberValueForIdentity(new YangIdentity(MODULE_NS, MODULE_NAME, "id-3")), edt1member3.getValue());

		final EnumDataTypeMember edt1member4 = findAndAssertEnumMember(id1edt, "id-4");
		assertEquals(MODULE_NS, edt1member4.getNamespace());
		assertEquals(IdentityRefGenerator.getMemberValueForIdentity(new YangIdentity(MODULE_NS, MODULE_NAME, "id-4")), edt1member4.getValue());

		final EnumDataTypeMember edt1member5 = findAndAssertEnumMember(id1edt, "id-5");
		assertEquals(MODULE_NS, edt1member5.getNamespace());
		assertEquals(IdentityRefGenerator.getMemberValueForIdentity(new YangIdentity(MODULE_NS, MODULE_NAME, "id-5")), edt1member5.getValue());

		final EnumDataTypeMember edt1member8 = findEnumMember(id1edt, "id-8");
		assertNull(edt1member8);			// sits in the extension as in different YAM

		final EnumDataTypeMember edt1member9 = findEnumMember(id1edt, "id-9");
		assertNull(edt1member9);			// sits in the extension as in different YAM

		final ModelInfo id1ExtModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE_NS, "id-1");
		final EnumDataTypeExtensionDefinition id1edtExt = load(id1ExtModelInfo);

		final EnumDataTypeMember edt1member8ext = findAndAssertAddedEnumMember(id1edtExt, "id-8");
		assertEquals(MODULE_NS, edt1member8ext.getNamespace());
		assertEquals(IdentityRefGenerator.getMemberValueForIdentity(new YangIdentity(MODULE_NS, MODULE_NAME, "id-8")), edt1member8ext.getValue());

		final EnumDataTypeMember edt1member9ext = findAndAssertAddedEnumMember(id1edtExt, "id-9");
		assertEquals(MODULE_NS, edt1member9ext.getNamespace());
		assertEquals(IdentityRefGenerator.getMemberValueForIdentity(new YangIdentity(MODULE_NS, MODULE_NAME, "id-9")), edt1member9ext.getValue());

		final ModelInfo id2ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-2", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id2edt = load(id2ModelInfo);
		assertSize(2, id2edt.getMember());
		findAndAssertEnumMember(id2edt, "id-2");
		findAndAssertEnumMember(id2edt, "id-4");

		final ModelInfo id4ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-4", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id4edt = load(id4ModelInfo);
		assertSize(1, id4edt.getMember());
		findAndAssertEnumMember(id4edt, "id-4");

		final ModelInfo id6ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-6", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id6edt = load(id6ModelInfo);
		assertEmpty(id6edt.getMember());					// Base defined in submodule, hence all members in extension.

		final ModelInfo id6ExtModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE_NS, "id-6");
		final EnumDataTypeExtensionDefinition id6edtExt = load(id6ExtModelInfo);
		assertSize(3, id6edtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(id6edtExt, "id-6");
		findAndAssertAddedEnumMember(id6edtExt, "id-7");
		findAndAssertAddedEnumMember(id6edtExt, "id-8");

		final ModelInfo id7ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-7", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id7edt = load(id7ModelInfo);
		assertEmpty(id7edt.getMember());					// Base defined in submodule, hence all members in extension.

		final ModelInfo id7ExtModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE_NS, "id-7");
		final EnumDataTypeExtensionDefinition id7edtExt = load(id7ExtModelInfo);
		assertSize(2, id7edtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(id7edtExt, "id-7");
		findAndAssertAddedEnumMember(id7edtExt, "id-8");

		/*
		 * Multiple bases - more complex...
		 */
		final ModelInfo idForLeaf13ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithMultipleBases(MODULE_NS, "cont1__leaf13", MODULE_XYZ_VERSION, null);
		final EnumDataTypeDefinition idForLeaf13edt = load(idForLeaf13ModelInfo);
		assertNotNull(idForLeaf13edt.getModelCreationInfo().getDerivedModel());
		assertEquals(yangModule.toUrn(), idForLeaf13edt.getModelCreationInfo().getDerivedModel().getDerivedFrom());
		assertEmpty(idForLeaf13edt.getMember());

		final ModelInfo extForLeaf13ModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, idForLeaf13ModelInfo.getNamespace(), idForLeaf13ModelInfo.getName());
		final EnumDataTypeExtensionDefinition leaf13EdtExt = load(extForLeaf13ModelInfo);
		assertEquals(idForLeaf13ModelInfo.toImpliedUrn(), leaf13EdtExt.getExtendedModelElement().get(0).getUrn());
		assertRequiresTargetType(context, null, leaf13EdtExt.getRequiresTargetType());

		assertSize(5, leaf13EdtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-3");
		findAndAssertAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-4");
		findAndAssertAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-6");
		findAndAssertAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-7");
		findAndAssertAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-8");

		final ModelInfo idForLeaf15ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithMultipleBases(MODULE_NS, "cont1__leaf15", MODULE_XYZ_VERSION, 1);
		final EnumDataTypeDefinition idForLeaf15edt = load(idForLeaf15ModelInfo);
		assertEmpty(idForLeaf15edt.getMember());

		final ModelInfo extForLeaf15ModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, idForLeaf15ModelInfo.getNamespace(), idForLeaf15ModelInfo.getName());
		final EnumDataTypeExtensionDefinition leaf15EdtExt = load(extForLeaf15ModelInfo);
		assertEquals(idForLeaf15ModelInfo.toImpliedUrn(), leaf15EdtExt.getExtendedModelElement().get(0).getUrn());
		assertRequiresTargetType(context, null, leaf15EdtExt.getRequiresTargetType());

		assertSize(2, leaf15EdtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(leaf15EdtExt, MODULE_NS, "id-4");
		findAndAssertAddedEnumMember(leaf15EdtExt, MODULE_NS, "id-5");

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(EnumRefType.class, leaf11.getType());
		assertEquals(id1ModelInfo.toImpliedUrn(), ((EnumRefType) leaf11.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertInstanceOf(EnumRefType.class, leaf12.getType());
		assertEquals(id2ModelInfo.toImpliedUrn(), ((EnumRefType) leaf12.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertInstanceOf(EnumRefType.class, leaf13.getType());
		assertEquals(idForLeaf13ModelInfo.toImpliedUrn(), ((EnumRefType) leaf13.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertInstanceOf(UnionType.class, leaf14.getType());
		assertInstanceOf(EnumRefType.class, ((UnionType) leaf14.getType()).getMember().get(0));
		assertInstanceOf(StringType.class, ((UnionType) leaf14.getType()).getMember().get(1));
		assertEquals(id4ModelInfo.toImpliedUrn(), ((EnumRefType) ((UnionType) leaf14.getType()).getMember().get(0)).getModelUrn());

		final PrimaryTypeAttribute leaf15 = findAttribute(cont1, "leaf15");
		assertInstanceOf(UnionType.class, leaf15.getType());
		assertInstanceOf(EnumRefType.class, ((UnionType) leaf15.getType()).getMember().get(1));
		assertEquals(idForLeaf15ModelInfo.toImpliedUrn(), ((EnumRefType) ((UnionType) leaf15.getType()).getMember().get(1)).getModelUrn());
		assertInstanceOf(ShortType.class, ((UnionType) leaf15.getType()).getMember().get(0));

		final PrimaryTypeAttribute leaf16 = findAttribute(cont1, "leaf16");
		assertInstanceOf(EnumRefType.class, leaf16.getType());
		assertEquals(id4ModelInfo.toImpliedUrn(), ((EnumRefType) leaf16.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf17 = findAttribute(cont1, "leaf17");
		assertInstanceOf(StringType.class, leaf17.getType());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);
		assertSize(1, cont2.getPrimaryTypeAttribute());				// Only contains the auto-key, rest in extension

		final ModelInfo cont2ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont2");
		final PrimaryTypeExtensionDefinition cont2ext = load(cont2ExtensionModelInfo);
		assertSize(2, cont2ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf21 = findAddedAttribute(cont2ext, "leaf21");
		assertInstanceOf(EnumRefType.class, leaf21.getType());
		assertEquals(id6ModelInfo.toImpliedUrn(), ((EnumRefType) leaf21.getType()).getModelUrn());

		final PrimaryTypeAttribute leaf22 = findAddedAttribute(cont2ext, "leaf22");
		assertInstanceOf(EnumRefType.class, leaf22.getType());
		assertEquals(id7ModelInfo.toImpliedUrn(), ((EnumRefType) leaf22.getType()).getModelUrn());
	}

	@Test
	public void test___identity_ref_simple___remove_if_feature_nodes() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S325_REFERENCED_IDENTITY_NOT_FOUND.toString());

		/*
		 * Identity "id-8" and "id-9" will be removed. This impacts on id-1, id-6, id-7, from where the identity should be removed inside an extension.
		 */

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "identityref-simple"));
		context.setFeatureHandling(FeatureHandling.REMOVE_ALL);
		YangTransformer2.transform(context);

		assertEModelCreatedCount(2, 1, 7, 4, 2, 0);

		// -----------------------------------------------------

		final ModelInfo id1ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-1", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id1edt = load(id1ModelInfo);
		assertSize(4, id1edt.getMember());
		findAndAssertEnumMember(id1edt, "id-1");
		findAndAssertEnumMember(id1edt, "id-3");
		findAndAssertEnumMember(id1edt, "id-4");
		findAndAssertEnumMember(id1edt, "id-5");
		assertNull(findEnumMember(id1edt, "id-8"));			// in different YAM
		assertNull(findEnumMember(id1edt, "id-9"));			// in different YAM

		final ModelInfo extForId1ModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, id1ModelInfo.getNamespace(), id1ModelInfo.getName());
		assertModelDoesNotExist(extForId1ModelInfo);

		final ModelInfo id2ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-2", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id2edt = load(id2ModelInfo);
		assertSize(2, id2edt.getMember());
		findAndAssertEnumMember(id2edt, "id-2");
		findAndAssertEnumMember(id2edt, "id-4");

		final ModelInfo id4ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-4", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id4edt = load(id4ModelInfo);
		assertSize(1, id4edt.getMember());
		findAndAssertEnumMember(id4edt, "id-4");

		final ModelInfo id6ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-6", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id6edt = load(id6ModelInfo);
		assertEmpty(id6edt.getMember());						// base defined in submodule

		final ModelInfo id6ExtModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE_NS, "id-6");
		final EnumDataTypeExtensionDefinition id6edtExt = load(id6ExtModelInfo);
		assertSize(2, id6edtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(id6edtExt, "id-6");
		findAndAssertAddedEnumMember(id6edtExt, "id-7");
		assertNull(findAddedEnumMember(id6edtExt, "id-8"));			// removed due to if-feature

		final ModelInfo id7ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS, "id-7", MODULE_XYZ_VERSION);
		final EnumDataTypeDefinition id7edt = load(id7ModelInfo);
		assertEmpty(id7edt.getMember());						// base defined in submodule

		final ModelInfo id7ExtModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE_NS, "id-7");
		final EnumDataTypeExtensionDefinition id7edtExt = load(id7ExtModelInfo);
		assertSize(1, id7edtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(id7edtExt, "id-7");
		assertNull(findAddedEnumMember(id7edtExt, "id-8"));			// removed due to if-feature

		/*
		 * Multiple bases - more complex...
		 */
		final ModelInfo idForLeaf13ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithMultipleBases(MODULE_NS, "cont1__leaf13", MODULE_XYZ_VERSION, null);
		final EnumDataTypeDefinition idForLeaf13edt = load(idForLeaf13ModelInfo);
		assertEmpty(idForLeaf13edt.getMember());

		final ModelInfo extForLeaf13ModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, idForLeaf13ModelInfo.getNamespace(), idForLeaf13ModelInfo.getName());
		final EnumDataTypeExtensionDefinition leaf13EdtExt = load(extForLeaf13ModelInfo);

		assertSize(4, leaf13EdtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-3");
		findAndAssertAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-4");
		findAndAssertAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-6");
		findAndAssertAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-7");
		assertNull(findAddedEnumMember(leaf13EdtExt, MODULE_NS, "id-8"));	// not added, but also not removed.
		assertNull(leaf13EdtExt.getEnumDataTypeRemoval());

		final ModelInfo idForLeaf15ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithMultipleBases(MODULE_NS, "cont1__leaf15", MODULE_XYZ_VERSION, 1);
		final EnumDataTypeDefinition idForLeaf15edt = load(idForLeaf15ModelInfo);
		assertEmpty(idForLeaf15edt.getMember());

		final ModelInfo extForLeaf15ModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, idForLeaf15ModelInfo.getNamespace(), idForLeaf15ModelInfo.getName());
		final EnumDataTypeExtensionDefinition leaf15EdtExt = load(extForLeaf15ModelInfo);

		assertSize(2, leaf15EdtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(leaf15EdtExt, MODULE_NS, "id-4");
		findAndAssertAddedEnumMember(leaf15EdtExt, MODULE_NS, "id-5");
	}
}
