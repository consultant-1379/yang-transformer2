package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAction;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.YangFeature;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.IdentityRefGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.CfmMimInfoSupport;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.IfFeatureHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class IfFeatureHandlingTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/if-feature/modules";
	private static final String INSTANCE_DATA_DIR = TEST_MODULES_DIR + "various/if-feature/yanglib-instance-data/";

	private static final String MODULE1_NS = "urn;test;if-feature-handling-module1";
	private static final String MODULE1_NAME = "if-feature-handling-module1";
	private static final String MODULE1_REVISION = "2022-09-23";
	private static final String MODULE1_XYZ_VERSION = "2022.9.23";

	private static final String MODULE5_NS = "urn;test;if-feature-handling-module5";
	private static final String MODULE5_NAME = "if-feature-handling-module5";
	private static final String MODULE5_REVISION = "2022-09-25";
	private static final String MODULE5_XYZ_VERSION = "2022.9.25";

	private static final String MODULE6_NS = "urn;test;if-feature-handling-module6";
	private static final String MODULE6_NAME = "if-feature-handling-module6";
	private static final String MODULE6_REVISION = "2022-09-26";
	private static final String MODULE6_XYZ_VERSION = "2022.9.26";

	private static final String SUBMODULE2_NAME = "if-feature-handling-submodule2";
	private static final String SUBMODULE2_REVISION = "2022-09-25";
	private static final String SUBMODULE2_XYZ_VERSION = "2022.9.25";

	@Test
	public void test___if_feature_handling___retain_all() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.RETAIN_ALL);
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont11", MODULE1_XYZ_VERSION);
		assertModelExists(cont11modelInfo);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont12", MODULE1_XYZ_VERSION);
		assertModelExists(cont12modelInfo);

		final ModelInfo cont13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont13", MODULE1_XYZ_VERSION);
		assertModelExists(cont13modelInfo);

		final ModelInfo cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont14", MODULE1_XYZ_VERSION);
		assertModelExists(cont14modelInfo);

		final ModelInfo cont15modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont15", MODULE1_XYZ_VERSION);
		assertModelExists(cont15modelInfo);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont16", MODULE1_XYZ_VERSION);
		assertModelExists(cont16modelInfo);

		final ModelInfo cont17modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont17", MODULE1_XYZ_VERSION);
		assertModelExists(cont17modelInfo);

		final ModelInfo cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont18", MODULE1_XYZ_VERSION);
		assertModelExists(cont18modelInfo);

		final ModelInfo cont19modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont19", MODULE1_XYZ_VERSION);
		assertModelExists(cont19modelInfo);

		final ModelInfo cont21modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont21", MODULE1_XYZ_VERSION);
		final PrimaryTypeDefinition cont21 = load(cont21modelInfo);

		final PrimaryTypeAttribute leaf211 = findAttribute(cont21, "leaf211");
		assertTrue(leaf211.isMandatory());
		final PrimaryTypeAttribute leaf212 = findAttribute(cont21, "leaf212");
		assertFalse(leaf212.isMandatory());

		// -----------------------------------------------------

		final ModelInfo cont51modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont51", MODULE5_XYZ_VERSION);
		assertModelExists(cont51modelInfo);

		final ModelInfo cont52modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont52", MODULE5_XYZ_VERSION);
		assertModelExists(cont52modelInfo);

		final ModelInfo cont53modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont53", MODULE5_XYZ_VERSION);
		assertModelExists(cont53modelInfo);

		final ModelInfo cont54modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont54", MODULE5_XYZ_VERSION);
		assertModelExists(cont54modelInfo);

		// -----------------------------------------------------

		final ModelInfo cont61modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont61", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont61 = load(cont61modelInfo);

		final ModelInfo edtLeaf61ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE6_NS, "identity61", MODULE6_XYZ_VERSION);
		final EnumDataTypeDefinition leaf61edt = load(edtLeaf61ModelInfo);

		assertNotNull(findEnumMember(leaf61edt, "identity61"));
		assertNotNull(findEnumMember(leaf61edt, "identity62"));
		assertNotNull(findEnumMember(leaf61edt, "identity63"));

		final ModelInfo edtLeaf61DeviatedModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE6_NS, "identity61");
		assertModelDoesNotExist(edtLeaf61DeviatedModelInfo);

		final PrimaryTypeAttribute leaf61 = findAttribute(cont61, "leaf61");
		assertInstanceOf(EnumRefType.class, leaf61.getType());
		assertEquals(edtLeaf61ModelInfo.toImpliedUrn(), ((EnumRefType) leaf61.getType()).getModelUrn());

		final ModelInfo edtLeaf62ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE6_NS, "cont61__leaf62", MODULE6_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf62edt = load(edtLeaf62ModelInfo);

		assertNotNull(findEnumMember(leaf62edt, "RED"));
		assertNotNull(findEnumMember(leaf62edt, "YELLOW"));
		assertNotNull(findEnumMember(leaf62edt, "GREEN"));

		final ModelInfo edtLeaf62DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE6_NS, "cont61__leaf62", null);
		assertModelDoesNotExist(edtLeaf62DeviatedModelInfo);

		final PrimaryTypeAttribute leaf62 = findAttribute(cont61, "leaf62");
		assertInstanceOf(EnumRefType.class, leaf62.getType());
		assertEquals(edtLeaf62ModelInfo.toImpliedUrn(), ((EnumRefType) leaf62.getType()).getModelUrn());

		// -----------------------------------------------------

		final ModelInfo cont65modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont65", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont65 = load(cont65modelInfo);

		final PrimaryTypeAttribute leaf65 = findAttribute(cont65, "leaf65");
		assertNotNull(leaf65);

		// -----------------------------------------------------

		final ModelInfo cont66modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont66", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont66 = load(cont66modelInfo);

		final PrimaryTypeAttribute leaf66 = findAttribute(cont66, "leaf66");
		assertNotNull(leaf66);
		final PrimaryTypeAttribute leaf67 = findAttribute(cont66, "leaf67");
		assertNotNull(leaf67);

		assertSize(1, cont66.getChoice());
		final ChoiceType choice66 = cont66.getChoice().get(0);
		assertSize(2, choice66.getCase());

		// -----------------------------------------------------

		final ModelInfo cont68modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont68", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont68 = load(cont68modelInfo);

		final PrimaryTypeAttribute leaf68 = findAttribute(cont68, "leaf68");
		assertNotNull(leaf68);

		// -----------------------------------------------------

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);

		final PrimaryTypeAction rpc69 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc69");
		assertNotNull(rpc69);

		// -----------------------------------------------------

		final ModelInfo modelInfoYam1 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, MODULE1_NAME, MODULE1_REVISION);
		assertModelExists(modelInfoYam1);
		final ModelInfo modelInfoYam5 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE5_NS, MODULE5_NAME, MODULE5_REVISION);
		assertModelExists(modelInfoYam5);
		final ModelInfo modelInfoYam6 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE6_NS, MODULE6_NAME, MODULE6_REVISION);
		assertModelExists(modelInfoYam6);
		final ModelInfo modelInfoYam2 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, SUBMODULE2_NAME, SUBMODULE2_REVISION);
		assertModelExists(modelInfoYam2);

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertHasSupportedMimModule(mimInfo, MODULE1_NS, MODULE1_XYZ_VERSION, modelInfoYam1.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_ALL_RETAINED, Collections.emptyList(), MODULE1_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE5_NS, MODULE5_XYZ_VERSION, modelInfoYam5.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_ALL_RETAINED, Collections.emptyList(), MODULE5_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE6_NS, MODULE6_XYZ_VERSION, modelInfoYam6.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_ALL_RETAINED, Collections.emptyList(), MODULE6_NAME);
		assertHasSupportedMimSubmodule(mimInfo, MODULE1_NS + "::" + SUBMODULE2_NAME, SUBMODULE2_XYZ_VERSION, modelInfoYam2.toUrn(), SUBMODULE2_NAME);
	}

	@Test
	public void test___if_feature_handling___remove_all() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.REMOVE_ALL);
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont11", MODULE1_XYZ_VERSION);
		assertModelExists(cont11modelInfo);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont12", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont12modelInfo);

		final ModelInfo cont13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont13", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont13modelInfo);

		final ModelInfo cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont14", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont14modelInfo);

		final ModelInfo cont15modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont15", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont15modelInfo);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont16", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont16modelInfo);

		final ModelInfo cont17modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont17", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont17modelInfo);

		final ModelInfo cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont18", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont18modelInfo);

		final ModelInfo cont19modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont19", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont19modelInfo);

		final ModelInfo cont21modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont21", MODULE1_XYZ_VERSION);
		final PrimaryTypeDefinition cont21 = load(cont21modelInfo);

		assertNotNull(findAttribute(cont21, "leaf211"));
		assertNotNull(findAttribute(cont21, "leaf212"));

		final ModelInfo cont21ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE1_NS, "cont21");
		final PrimaryTypeExtensionDefinition cont21ext = load(cont21ExtensionModelInfo);

		assertSize(1, cont21ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf212", cont21ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo cont51modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont51", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont51modelInfo);

		final ModelInfo cont52modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont52", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont52modelInfo);

		final ModelInfo cont53modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont53", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont53modelInfo);

		final ModelInfo cont54modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont54", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont54modelInfo);

		// -----------------------------------------------------

		final ModelInfo cont61modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont61", MODULE6_XYZ_VERSION);
		assertModelExists(cont61modelInfo);

		final ModelInfo edtLeaf61ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE6_NS, "identity61", MODULE6_XYZ_VERSION);
		final EnumDataTypeDefinition leaf61edt = load(edtLeaf61ModelInfo);

		assertNotNull(findEnumMember(leaf61edt, "identity61"));
		assertNotNull(findEnumMember(leaf61edt, "identity62"));
		assertNotNull(findEnumMember(leaf61edt, "identity63"));

		final ModelInfo edtLeaf61DeviatedModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE6_NS, "identity61");
		final EnumDataTypeExtensionDefinition edtLeaf61Deviated = load(edtLeaf61DeviatedModelInfo);
		assertSize(1, edtLeaf61Deviated.getEnumDataTypeRemoval().getMemberWithNamespace());		// identity62 removed

		final ModelInfo edtLeaf62ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE6_NS, "cont61__leaf62", MODULE6_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf62edt = load(edtLeaf62ModelInfo);

		assertNotNull(findEnumMember(leaf62edt, "RED"));
		assertNotNull(findEnumMember(leaf62edt, "YELLOW"));
		assertNotNull(findEnumMember(leaf62edt, "GREEN"));

		final ModelInfo edtLeaf62DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE6_NS, "cont61__leaf62", null);
		final EnumDataTypeDefinition edtLeaf62Deviated = load(edtLeaf62DeviatedModelInfo);

		assertNotNull(findEnumMember(edtLeaf62Deviated, "RED"));
		assertNull(findEnumMember(edtLeaf62Deviated, "YELLOW"));
		assertNull(findEnumMember(edtLeaf62Deviated, "GREEN"));

		// -----------------------------------------------------

		final ModelInfo cont65modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont65", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont65 = load(cont65modelInfo);

		final PrimaryTypeAttribute leaf65 = findAttribute(cont65, "leaf65");
		assertNotNull(leaf65);

		final ModelInfo cont65ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont65");
		final PrimaryTypeExtensionDefinition cont65ext = load(cont65ExtensionModelInfo);

		assertSize(1, cont65ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf65", cont65ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo cont66modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont66", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont66 = load(cont66modelInfo);

		final PrimaryTypeAttribute leaf66 = findAttribute(cont66, "leaf66");
		assertNotNull(leaf66);
		final PrimaryTypeAttribute leaf67 = findAttribute(cont66, "leaf67");
		assertNotNull(leaf67);

		assertSize(1, cont66.getChoice());
		final ChoiceType choice66 = cont66.getChoice().get(0);
		assertSize(2, choice66.getCase());

		final ModelInfo cont66ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont66");
		final PrimaryTypeExtensionDefinition cont66ext = load(cont66ExtensionModelInfo);

		assertSize(1, cont66ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf66", cont66ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo cont68modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont68", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont68 = load(cont68modelInfo);

		final PrimaryTypeAttribute leaf68 = findAttribute(cont68, "leaf68");
		assertNotNull(leaf68);

		final ModelInfo cont68ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont68");
		final PrimaryTypeExtensionDefinition cont68ext = load(cont68ExtensionModelInfo);

		assertSize(1, cont68ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf68", cont68ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);

		final PrimaryTypeAction rpc69 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc69");
		assertNull(rpc69);

		final PrimaryTypeAction rpc70 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc70");
		assertNull(rpc70);

		assertHasNonGeneratedDataNode(new String[] {MODULE1_NS, "cont12"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE1_NS, "cont13"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE1_NS, "cont14"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE1_NS, "cont15"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE1_NS, "cont16"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE1_NS, "cont17"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE1_NS, "cont18"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE1_NS, "cont19"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE1_NS, "cont21"}, containmentParentExt.getMeta());

		assertHasNonGeneratedDataNode(new String[] {MODULE5_NS, "cont51"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE5_NS, "cont52"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE5_NS, "cont53"}, containmentParentExt.getMeta());
		assertHasNonGeneratedDataNode(new String[] {MODULE5_NS, "cont54"}, containmentParentExt.getMeta());

		assertHasNotNonGeneratedDataNode(new String[] {MODULE6_NS, "cont61"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE6_NS, "cont65"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE6_NS, "cont66"}, containmentParentExt.getMeta());
		assertHasNotNonGeneratedDataNode(new String[] {MODULE6_NS, "cont68"}, containmentParentExt.getMeta());

		// -----------------------------------------------------

		final ModelInfo modelInfoYam1 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, MODULE1_NAME, MODULE1_REVISION);
		assertModelExists(modelInfoYam1);
		final ModelInfo modelInfoYam5 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE5_NS, MODULE5_NAME, MODULE5_REVISION);
		assertModelExists(modelInfoYam5);
		final ModelInfo modelInfoYam6 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE6_NS, MODULE6_NAME, MODULE6_REVISION);
		assertModelExists(modelInfoYam6);
		final ModelInfo modelInfoYam2 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, SUBMODULE2_NAME, SUBMODULE2_REVISION);
		assertModelExists(modelInfoYam2);

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertHasSupportedMimModule(mimInfo, MODULE1_NS, MODULE1_XYZ_VERSION, modelInfoYam1.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_ALL_REMOVED, Collections.emptyList(), MODULE1_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE5_NS, MODULE5_XYZ_VERSION, modelInfoYam5.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_ALL_REMOVED, Collections.emptyList(), MODULE5_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE6_NS, MODULE6_XYZ_VERSION, modelInfoYam6.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_ALL_REMOVED, Collections.emptyList(), MODULE6_NAME);
		assertHasSupportedMimSubmodule(mimInfo, MODULE1_NS + "::" + SUBMODULE2_NAME, SUBMODULE2_XYZ_VERSION, modelInfoYam2.toUrn(), SUBMODULE2_NAME);
	}

	@Test
	public void test___if_feature_handling___configured___no_yang_lib_file() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown an exception.");
		} catch (final Exception expected) {}
	}

	@Test
	public void test___if_feature_handling___configured___file_does_not_exist() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "file-does-not-exist.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown an exception.");
		} catch (final Exception expected) {}
	}

	@Test
	public void test___if_feature_handling___configured___no_content() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "no-content.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown an exception.");
		} catch (final Exception expected) {}
	}

	@Test
	public void test___if_feature_handling___configured___not_an_xml_file() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "not-an-xml-file.txt"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown an exception.");
		} catch (final Exception expected) {}
	}

	@Test
	public void test___if_feature_handling___configured___corrupt_yanglibrary_data() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-corrupted.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown an exception.");
		} catch (final Exception expected) {}
	}

	@Test
	public void test___if_feature_handling___if_feature_handler() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mixture-of-features-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		final Set<YangFeature> enabledFeatures = IfFeatureHelper.getEnabledFeaturesFromYangLibrary(context);
		assertSize(6, enabledFeatures);
		assertTrue(enabledFeatures.contains(new YangFeature(MODULE1_NS, "if-feature-handling-module1", "feature11")));
		assertTrue(enabledFeatures.contains(new YangFeature(MODULE1_NS, "if-feature-handling-module1", "feature12")));
		assertTrue(enabledFeatures.contains(new YangFeature(MODULE1_NS, "if-feature-handling-module1", "feature14")));
		assertTrue(enabledFeatures.contains(new YangFeature(MODULE5_NS, "if-feature-handling-module5", "feature12")));
		assertTrue(enabledFeatures.contains(new YangFeature(MODULE6_NS, "if-feature-handling-module6", "feature61")));
		assertTrue(enabledFeatures.contains(new YangFeature(MODULE6_NS, "if-feature-handling-module6", "feature70")));
	}

	@Test
	public void test___if_feature_handling___configured___nothing_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-nothing-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont11", MODULE1_XYZ_VERSION);
		assertModelExists(cont11modelInfo);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont12", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont12modelInfo);

		final ModelInfo cont13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont13", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont13modelInfo);

		final ModelInfo cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont14", MODULE1_XYZ_VERSION);
		assertModelExists(cont14modelInfo);

		final ModelInfo cont15modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont15", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont15modelInfo);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont16", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont16modelInfo);

		final ModelInfo cont17modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont17", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont17modelInfo);

		final ModelInfo cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont18", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont18modelInfo);

		final ModelInfo cont19modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont19", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont19modelInfo);

		final ModelInfo cont21modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont21", MODULE1_XYZ_VERSION);
		final PrimaryTypeDefinition cont21 = load(cont21modelInfo);

		assertNotNull(findAttribute(cont21, "leaf211"));
		assertNotNull(findAttribute(cont21, "leaf212"));

		final ModelInfo cont21ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE1_NS, "cont21");
		final PrimaryTypeExtensionDefinition cont21ext = load(cont21ExtensionModelInfo);

		assertSize(1, cont21ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf212", cont21ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		final ModelInfo cont51modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont51", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont51modelInfo);

		final ModelInfo cont52modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont52", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont52modelInfo);

		final ModelInfo cont53modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont53", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont53modelInfo);

		final ModelInfo cont54modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont54", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont54modelInfo);

		// -----------------------------------------------------

		final ModelInfo modelInfoYam1 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, MODULE1_NAME, MODULE1_REVISION);
		assertModelExists(modelInfoYam1);
		final ModelInfo modelInfoYam5 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE5_NS, MODULE5_NAME, MODULE5_REVISION);
		assertModelExists(modelInfoYam5);
		final ModelInfo modelInfoYam6 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE6_NS, MODULE6_NAME, MODULE6_REVISION);
		assertModelExists(modelInfoYam6);
		final ModelInfo modelInfoYam2 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, SUBMODULE2_NAME, SUBMODULE2_REVISION);
		assertModelExists(modelInfoYam2);

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertHasSupportedMimModule(mimInfo, MODULE1_NS, MODULE1_XYZ_VERSION, modelInfoYam1.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Collections.emptyList(), MODULE1_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE5_NS, MODULE5_XYZ_VERSION, modelInfoYam5.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Collections.emptyList(), MODULE5_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE6_NS, MODULE6_XYZ_VERSION, modelInfoYam6.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Collections.emptyList(), MODULE6_NAME);
		assertHasSupportedMimSubmodule(mimInfo, MODULE1_NS + "::" + SUBMODULE2_NAME, SUBMODULE2_XYZ_VERSION, modelInfoYam2.toUrn(), SUBMODULE2_NAME);
	}

	@Test
	public void test___if_feature_handling___configured___mod1_feature11_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod1-feature11-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont11", MODULE1_XYZ_VERSION);
		assertModelExists(cont11modelInfo);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont12", MODULE1_XYZ_VERSION);
		assertModelExists(cont12modelInfo);

		final ModelInfo cont13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont13", MODULE1_XYZ_VERSION);
		assertModelExists(cont13modelInfo);

		final ModelInfo cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont14", MODULE1_XYZ_VERSION);
		assertModelExists(cont14modelInfo);

		final ModelInfo cont15modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont15", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont15modelInfo);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont16", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont16modelInfo);

		final ModelInfo cont17modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont17", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont17modelInfo);

		final ModelInfo cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont18", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont18modelInfo);

		final ModelInfo cont19modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont19", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont19modelInfo);

		final ModelInfo cont21modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont21", MODULE1_XYZ_VERSION);
		final PrimaryTypeDefinition cont21 = load(cont21modelInfo);

		final PrimaryTypeAttribute leaf211 = findAttribute(cont21, "leaf211");
		assertTrue(leaf211.isMandatory());
		final PrimaryTypeAttribute leaf212 = findAttribute(cont21, "leaf212");
		assertTrue(leaf212.isMandatory());

		// -----------------------------------------------------

		final ModelInfo cont51modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont51", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont51modelInfo);

		final ModelInfo cont52modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont52", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont52modelInfo);

		final ModelInfo cont53modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont53", MODULE5_XYZ_VERSION);
		assertModelExists(cont53modelInfo);

		final ModelInfo cont54modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont54", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont54modelInfo);

		// -----------------------------------------------------

		final ModelInfo cont61modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont61", MODULE6_XYZ_VERSION);
		assertModelExists(cont61modelInfo);

		final ModelInfo edtLeaf61ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE6_NS, "identity61", MODULE6_XYZ_VERSION);
		final EnumDataTypeDefinition leaf61edt = load(edtLeaf61ModelInfo);

		assertNotNull(findEnumMember(leaf61edt, "identity61"));
		assertNotNull(findEnumMember(leaf61edt, "identity62"));
		assertNotNull(findEnumMember(leaf61edt, "identity63"));

		final ModelInfo edtLeaf61DeviatedModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE6_NS, "identity61");
		final EnumDataTypeExtensionDefinition edtLeaf61Deviated = load(edtLeaf61DeviatedModelInfo);
		assertSize(1, edtLeaf61Deviated.getEnumDataTypeRemoval().getMemberWithNamespace());		// identity62 removed

		final ModelInfo edtLeaf62ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE6_NS, "cont61__leaf62", MODULE6_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf62edt = load(edtLeaf62ModelInfo);

		assertNotNull(findEnumMember(leaf62edt, "RED"));
		assertNotNull(findEnumMember(leaf62edt, "YELLOW"));
		assertNotNull(findEnumMember(leaf62edt, "GREEN"));

		final ModelInfo edtLeaf62DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE6_NS, "cont61__leaf62", null);
		final EnumDataTypeDefinition edtLeaf62Deviated = load(edtLeaf62DeviatedModelInfo);

		assertNotNull(findEnumMember(edtLeaf62Deviated, "RED"));
		assertNull(findEnumMember(edtLeaf62Deviated, "YELLOW"));
		assertNull(findEnumMember(edtLeaf62Deviated, "GREEN"));

		// -----------------------------------------------------

		final ModelInfo cont65modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont65", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont65 = load(cont65modelInfo);

		final PrimaryTypeAttribute leaf65 = findAttribute(cont65, "leaf65");
		assertNotNull(leaf65);

		final ModelInfo cont65ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont65");
		final PrimaryTypeExtensionDefinition cont65ext = load(cont65ExtensionModelInfo);

		assertSize(1, cont65ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf65", cont65ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo cont66modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont66", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont66 = load(cont66modelInfo);

		final PrimaryTypeAttribute leaf66 = findAttribute(cont66, "leaf66");
		assertNotNull(leaf66);
		final PrimaryTypeAttribute leaf67 = findAttribute(cont66, "leaf67");
		assertNotNull(leaf67);

		assertSize(1, cont66.getChoice());
		final ChoiceType choice66 = cont66.getChoice().get(0);
		assertSize(2, choice66.getCase());

		final ModelInfo cont66ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont66");
		final PrimaryTypeExtensionDefinition cont66ext = load(cont66ExtensionModelInfo);

		assertSize(1, cont66ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf66", cont66ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo cont68modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont68", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont68 = load(cont68modelInfo);

		final PrimaryTypeAttribute leaf68 = findAttribute(cont68, "leaf68");
		assertNotNull(leaf68);

		final ModelInfo cont68ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont68");
		final PrimaryTypeExtensionDefinition cont68ext = load(cont68ExtensionModelInfo);

		assertSize(1, cont68ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf68", cont68ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		assertModelDoesNotExist(containmentParentModelInfo);

		// -----------------------------------------------------

		final ModelInfo modelInfoYam1 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, MODULE1_NAME, MODULE1_REVISION);
		final ModelInfo modelInfoYam5 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE5_NS, MODULE5_NAME, MODULE5_REVISION);
		final ModelInfo modelInfoYam6 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE6_NS, MODULE6_NAME, MODULE6_REVISION);
		final ModelInfo modelInfoYam2 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, SUBMODULE2_NAME, SUBMODULE2_REVISION);

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertHasSupportedMimModule(mimInfo, MODULE1_NS, MODULE1_XYZ_VERSION, modelInfoYam1.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Arrays.asList("feature11"), MODULE1_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE5_NS, MODULE5_XYZ_VERSION, modelInfoYam5.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Collections.emptyList(), MODULE5_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE6_NS, MODULE6_XYZ_VERSION, modelInfoYam6.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Collections.emptyList(), MODULE6_NAME);
		assertHasSupportedMimSubmodule(mimInfo, MODULE1_NS + "::" + SUBMODULE2_NAME, SUBMODULE2_XYZ_VERSION, modelInfoYam2.toUrn(), SUBMODULE2_NAME);
	}

	@Test
	public void test___if_feature_handling___configured___mod1_feature12_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod1-feature12-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont11", MODULE1_XYZ_VERSION);
		assertModelExists(cont11modelInfo);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont12", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont12modelInfo);

		final ModelInfo cont13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont13", MODULE1_XYZ_VERSION);
		assertModelExists(cont13modelInfo);

		final ModelInfo cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont14", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont14modelInfo);

		final ModelInfo cont15modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont15", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont15modelInfo);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont16", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont16modelInfo);

		final ModelInfo cont17modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont17", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont17modelInfo);

		final ModelInfo cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont18", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont18modelInfo);

		final ModelInfo cont19modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont19", MODULE1_XYZ_VERSION);
		assertModelExists(cont19modelInfo);
	}

	@Test
	public void test___if_feature_handling___configured___mod1_feature11_12_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod1-feature11-12-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont11", MODULE1_XYZ_VERSION);
		assertModelExists(cont11modelInfo);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont12", MODULE1_XYZ_VERSION);
		assertModelExists(cont12modelInfo);

		final ModelInfo cont13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont13", MODULE1_XYZ_VERSION);
		assertModelExists(cont13modelInfo);

		final ModelInfo cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont14", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont14modelInfo);

		final ModelInfo cont15modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont15", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont15modelInfo);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont16", MODULE1_XYZ_VERSION);
		assertModelExists(cont16modelInfo);

		final ModelInfo cont17modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont17", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont17modelInfo);

		final ModelInfo cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont18", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont18modelInfo);

		final ModelInfo cont19modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont19", MODULE1_XYZ_VERSION);
		assertModelExists(cont19modelInfo);
	}

	@Test
	public void test___if_feature_handling___configured___mod1_feature11_12_13_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod1-feature11-12-13-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont11", MODULE1_XYZ_VERSION);
		assertModelExists(cont11modelInfo);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont12", MODULE1_XYZ_VERSION);
		assertModelExists(cont12modelInfo);

		final ModelInfo cont13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont13", MODULE1_XYZ_VERSION);
		assertModelExists(cont13modelInfo);

		final ModelInfo cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont14", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont14modelInfo);

		final ModelInfo cont15modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont15", MODULE1_XYZ_VERSION);
		assertModelExists(cont15modelInfo);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont16", MODULE1_XYZ_VERSION);
		assertModelExists(cont16modelInfo);

		final ModelInfo cont17modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont17", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont17modelInfo);

		final ModelInfo cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont18", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont18modelInfo);

		final ModelInfo cont19modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont19", MODULE1_XYZ_VERSION);
		assertModelExists(cont19modelInfo);

		// -----------------------------------------------------

		final ModelInfo modelInfoYam1 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, MODULE1_NAME, MODULE1_REVISION);
		final ModelInfo modelInfoYam5 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE5_NS, MODULE5_NAME, MODULE5_REVISION);

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertHasSupportedMimModule(mimInfo, MODULE1_NS, MODULE1_XYZ_VERSION, modelInfoYam1.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Arrays.asList("feature11", "feature12", "feature13"), MODULE1_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE5_NS, MODULE5_XYZ_VERSION, modelInfoYam5.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Collections.emptyList(), MODULE5_NAME);
	}

	@Test
	public void test___if_feature_handling___configured___mod1_feature11_18_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod1-feature11-18-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont11", MODULE1_XYZ_VERSION);
		assertModelExists(cont11modelInfo);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont12", MODULE1_XYZ_VERSION);
		assertModelExists(cont12modelInfo);

		final ModelInfo cont13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont13", MODULE1_XYZ_VERSION);
		assertModelExists(cont13modelInfo);

		final ModelInfo cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont14", MODULE1_XYZ_VERSION);
		assertModelExists(cont14modelInfo);

		final ModelInfo cont15modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont15", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont15modelInfo);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont16", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont16modelInfo);

		final ModelInfo cont17modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont17", MODULE1_XYZ_VERSION);
		assertModelExists(cont17modelInfo);

		final ModelInfo cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont18", MODULE1_XYZ_VERSION);
		assertModelExists(cont18modelInfo);

		final ModelInfo cont19modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont19", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont19modelInfo);
	}

	@Test
	public void test___if_feature_handling___configured___mod1_feature19___mod5_feature12_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod1-feature19-mod5-feature12-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont11", MODULE1_XYZ_VERSION);
		assertModelExists(cont11modelInfo);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont12", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont12modelInfo);

		final ModelInfo cont13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont13", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont13modelInfo);

		final ModelInfo cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont14", MODULE1_XYZ_VERSION);
		assertModelExists(cont14modelInfo);

		final ModelInfo cont15modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont15", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont15modelInfo);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont16", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont16modelInfo);

		final ModelInfo cont17modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont17", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont17modelInfo);

		final ModelInfo cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont18", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont18modelInfo);

		final ModelInfo cont19modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE1_NS, "cont19", MODULE1_XYZ_VERSION);
		assertModelDoesNotExist(cont19modelInfo);

		// -----------------------------------------------------

		final ModelInfo cont51modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont51", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont51modelInfo);

		final ModelInfo cont52modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont52", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont52modelInfo);

		final ModelInfo cont53modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont53", MODULE5_XYZ_VERSION);
		assertModelDoesNotExist(cont53modelInfo);

		final ModelInfo cont54modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE5_NS, "cont54", MODULE5_XYZ_VERSION);
		assertModelExists(cont54modelInfo);

		// -----------------------------------------------------

		final ModelInfo modelInfoYam1 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, MODULE1_NAME, MODULE1_REVISION);
		final ModelInfo modelInfoYam5 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE5_NS, MODULE5_NAME, MODULE5_REVISION);
		final ModelInfo modelInfoYam6 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE6_NS, MODULE6_NAME, MODULE6_REVISION);
		final ModelInfo modelInfoYam2 = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE1_NS, SUBMODULE2_NAME, SUBMODULE2_REVISION);

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertHasSupportedMimModule(mimInfo, MODULE1_NS, MODULE1_XYZ_VERSION, modelInfoYam1.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Arrays.asList("feature19"), MODULE1_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE5_NS, MODULE5_XYZ_VERSION, modelInfoYam5.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Arrays.asList("feature12"), MODULE5_NAME);
		assertHasSupportedMimModule(mimInfo, MODULE6_NS, MODULE6_XYZ_VERSION, modelInfoYam6.toUrn(), CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_CONFIGURED, Collections.emptyList(), MODULE6_NAME);
		assertHasSupportedMimSubmodule(mimInfo, MODULE1_NS + "::" + SUBMODULE2_NAME, SUBMODULE2_XYZ_VERSION, modelInfoYam2.toUrn(), SUBMODULE2_NAME);
	}

	@Test
	public void test___if_feature_handling___configured___mod6_feature61_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod6-feature61-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		final ModelInfo cont61modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont61", MODULE6_XYZ_VERSION);
		assertModelExists(cont61modelInfo);

		final ModelInfo edtLeaf61ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE6_NS, "identity61", MODULE6_XYZ_VERSION);
		final EnumDataTypeDefinition leaf61edt = load(edtLeaf61ModelInfo);

		assertNotNull(findEnumMember(leaf61edt, "identity61"));
		assertNotNull(findEnumMember(leaf61edt, "identity62"));
		assertNotNull(findEnumMember(leaf61edt, "identity63"));

		final ModelInfo edtLeaf61DeviatedModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE6_NS, "identity61");
		assertModelDoesNotExist(edtLeaf61DeviatedModelInfo);

		final ModelInfo edtLeaf62ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE6_NS, "cont61__leaf62", MODULE6_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf62edt = load(edtLeaf62ModelInfo);

		assertNotNull(findEnumMember(leaf62edt, "RED"));
		assertNotNull(findEnumMember(leaf62edt, "YELLOW"));
		assertNotNull(findEnumMember(leaf62edt, "GREEN"));

		final ModelInfo edtLeaf62DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE6_NS, "cont61__leaf62", null);
		final EnumDataTypeDefinition edtLeaf62Deviated = load(edtLeaf62DeviatedModelInfo);

		assertNotNull(findEnumMember(edtLeaf62Deviated, "RED"));
		assertNotNull(findEnumMember(edtLeaf62Deviated, "YELLOW"));
		assertNull(findEnumMember(edtLeaf62Deviated, "GREEN"));

		// -----------------------------------------------------

		final ModelInfo cont65modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont65", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont65 = load(cont65modelInfo);

		final PrimaryTypeAttribute leaf65 = findAttribute(cont65, "leaf65");
		assertNotNull(leaf65);

		final ModelInfo cont65ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont65");
		final PrimaryTypeExtensionDefinition cont65ext = load(cont65ExtensionModelInfo);

		assertSize(1, cont65ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf65", cont65ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo cont66modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont66", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont66 = load(cont66modelInfo);

		final PrimaryTypeAttribute leaf66 = findAttribute(cont66, "leaf66");
		assertNotNull(leaf66);
		final PrimaryTypeAttribute leaf67 = findAttribute(cont66, "leaf67");
		assertNotNull(leaf67);

		assertSize(1, cont66.getChoice());
		final ChoiceType choice66 = cont66.getChoice().get(0);
		assertSize(2, choice66.getCase());

		final ModelInfo cont66ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont66");
		final PrimaryTypeExtensionDefinition cont66ext = load(cont66ExtensionModelInfo);

		assertSize(1, cont66ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf66", cont66ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo cont68modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont68", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont68 = load(cont68modelInfo);

		final PrimaryTypeAttribute leaf68 = findAttribute(cont68, "leaf68");
		assertNotNull(leaf68);

		final ModelInfo cont68ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont68");
		final PrimaryTypeExtensionDefinition cont68ext = load(cont68ExtensionModelInfo);

		assertSize(1, cont68ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf68", cont68ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());

		// -----------------------------------------------------

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		assertModelDoesNotExist(containmentParentModelInfo);
	}

	@Test
	public void test___if_feature_handling___configured___mod6_feature61_62_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod6-feature61-62-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		final ModelInfo cont61modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont61", MODULE6_XYZ_VERSION);
		assertModelExists(cont61modelInfo);

		final ModelInfo edtLeaf61ModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE6_NS, "identity61", MODULE6_XYZ_VERSION);
		final EnumDataTypeDefinition leaf61edt = load(edtLeaf61ModelInfo);

		assertNotNull(findEnumMember(leaf61edt, "identity61"));
		assertNotNull(findEnumMember(leaf61edt, "identity62"));
		assertNotNull(findEnumMember(leaf61edt, "identity63"));

		final ModelInfo edtLeaf61DeviatedModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, MODULE6_NS, "identity61");
		assertModelDoesNotExist(edtLeaf61DeviatedModelInfo);

		final ModelInfo edtLeaf62ModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(MODULE6_NS, "cont61__leaf62", MODULE6_XYZ_VERSION, null);
		final EnumDataTypeDefinition leaf62edt = load(edtLeaf62ModelInfo);

		assertNotNull(findEnumMember(leaf62edt, "RED"));
		assertNotNull(findEnumMember(leaf62edt, "YELLOW"));
		assertNotNull(findEnumMember(leaf62edt, "GREEN"));

		final ModelInfo edtLeaf62DeviatedModelInfo = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(DEFAULT_TEST_TARGET, MODULE6_NS, "cont61__leaf62", null);
		assertModelDoesNotExist(edtLeaf62DeviatedModelInfo);
	}

	@Test
	public void test___if_feature_handling___configured___mod6_feature63_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod6-feature63-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		final ModelInfo cont65modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont65", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont65 = load(cont65modelInfo);

		final PrimaryTypeAttribute leaf65 = findAttribute(cont65, "leaf65");
		assertNotNull(leaf65);

		final ModelInfo cont65ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont65");
		assertModelDoesNotExist(cont65ExtensionModelInfo);
	}

	@Test
	public void test___if_feature_handling___configured___mod6_feature68_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod6-feature68-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		final ModelInfo cont68modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont68", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont68 = load(cont68modelInfo);

		final PrimaryTypeAttribute leaf68 = findAttribute(cont68, "leaf68");
		assertNotNull(leaf68);

		final ModelInfo cont68ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont68");
		final PrimaryTypeExtensionDefinition cont68ext = load(cont68ExtensionModelInfo);

		assertSize(1, cont68ext.getPrimaryTypeAttributeRemoval());
		assertEquals("leaf68", cont68ext.getPrimaryTypeAttributeRemoval().get(0).getAttributeName());
	}

	@Test
	public void test___if_feature_handling___configured___mod6_feature68_69_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod6-feature68-69-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		final ModelInfo cont68modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE6_NS, "cont68", MODULE6_XYZ_VERSION);
		final PrimaryTypeDefinition cont68 = load(cont68modelInfo);

		final PrimaryTypeAttribute leaf68 = findAttribute(cont68, "leaf68");
		assertNotNull(leaf68);

		final ModelInfo cont68ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE6_NS, "cont68");
		assertModelDoesNotExist(cont68ExtensionModelInfo);
	}

	@Test
	public void test___if_feature_handling___configured___mod6_feature69_70_enabled() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P033_UNRESOLVEABLE_PREFIX.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S351_FEATURE_NOT_FOUND.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-mod6-feature69-70-enabled.xml"));
		context.setGenerateRpcs(true);
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);

		YangTransformer2.transform(context);

		final ModelInfo containmentParentModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, OSS_TOP_ME_CONTEXT_300.getNamespace(), OSS_TOP_ME_CONTEXT_300.getName());
		final PrimaryTypeExtensionDefinition containmentParentExt = load(containmentParentModelInfo);

		final PrimaryTypeAction rpc69 = findAction(containmentParentExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc69");
		assertNotNull(rpc69);
	}
}
