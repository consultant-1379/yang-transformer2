package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.Target;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.CfmMimInfoGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.ComplexTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EModelHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.IdentityRefGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;

/*
 * These tests here are quite important. If any of these tests here fails this means that
 * the code logic employed to generate the model identities for the various models has
 * changed. This could result in issues at runtime, so would have to be very carefully
 * evaluated.
 */
public class ModelIdentityGenerationTest {

	private static final String TEST_TARGET_CATEGORY = "target-category";
	private static final String TEST_TARGET_TYPE = "target-type";
	private static final String TEST_TARGET_VERSION = "target-version";

	private static final String TEST_MOC_NAMESPACE = "moc-namespace";
	private static final String TEST_MOC_NAME = "moc-name";
	private static final String TEST_MOC_VERSION = "4.5.6";

	private static final String TEST_LEAF_PATH = "moc-name__leaf-name";

	private static final String TEST_IDENTITY_NAMESPACE = "identity-namespace";
	private static final String TEST_IDENTITY_NAME = "identity-name";

	private static final String TEST_ORIG_EDT_NAMESPACE = "orig-edt-namespace";
	private static final String TEST_ORIG_EDT_NAME = "orig-edt-name";

	private static final String TEST_YANG_NAMESPACE = "yang-namespace";

	@Test
	public void test___modelinfo_namespace_for_extensions() {

		final String generated = EModelHelper.getExtensionModelNamespace(new Target(TEST_TARGET_CATEGORY, TEST_TARGET_TYPE, TEST_TARGET_VERSION));
		final String expected = TEST_TARGET_CATEGORY + "__" + TEST_TARGET_TYPE + "__" + TEST_TARGET_VERSION;

		assertEquals(expected, generated);
	}

	@Test
	public void test___modelinfo_version_for_extensions() {

		final String generated = EModelHelper.getModelInfoVersionForExtension();
		final String expected = Constants.ONE_ZERO_ZERO;

		assertEquals(expected, generated);
	}

	// ---------------------------------------------------------------------------------------------------

	@Test
	public void test___generate_dps_relationship_model_info() {

		final Target target = new Target(TEST_TARGET_CATEGORY, TEST_TARGET_TYPE, TEST_TARGET_VERSION);

		final String expectedNamespace = EModelHelper.getTargetVersionSpecificName(target);
		final String expectedName = "all-relationships";
		final String expectedVersion = EModelHelper.getModelInfoVersionForExtension();

		final ModelInfo generated = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(target);
		final ModelInfo expected = new ModelInfo(SchemaConstants.DPS_RELATIONSHIP, expectedNamespace, expectedName, expectedVersion);

		assertEquals(expected, generated);
	}

	// ---------------------------------------------------------------------------------------------------

	@Test
	public void test___generate_dps_primarytype_model_info() {

		final ModelInfo generated = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(TEST_MOC_NAMESPACE, TEST_MOC_NAME, TEST_MOC_VERSION);
		final ModelInfo expected = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, TEST_MOC_NAMESPACE, TEST_MOC_NAME, TEST_MOC_VERSION);

		assertEquals(expected, generated);
	}

	@Test
	public void test___generate_dps_primarytype_model_info_for_mock_managed_element() {

		final ModelInfo generated = PrimaryTypeGenerator.getModelInfoForMockManagedElementPrimaryTypeDefinition(TEST_MOC_NAMESPACE);
		final ModelInfo expected = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, TEST_MOC_NAMESPACE, Constants.MANAGEDELEMENT, Constants.ONE_ZERO_ZERO);

		assertEquals(expected, generated);
	}

	// ---------------------------------------------------------------------------------------------------

	@Test
	public void test___generate_dps_primarytype_extension_model_info() {

		final Target target = new Target(TEST_TARGET_CATEGORY, TEST_TARGET_TYPE, TEST_TARGET_VERSION);

		final String expectedNamespace = EModelHelper.getExtensionModelNamespace(target);
		final String expectedName = TEST_MOC_NAMESPACE + "__" + TEST_MOC_NAME + "__ext";
		final String expectedVersion = EModelHelper.getModelInfoVersionForExtension();

		final ModelInfo generated = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(target, TEST_MOC_NAMESPACE, TEST_MOC_NAME);
		final ModelInfo expected = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE_EXT, expectedNamespace, expectedName, expectedVersion);

		assertEquals(expected, generated);
	}

	// ---------------------------------------------------------------------------------------------------

	@Test
	public void test___generate_oss_edt_for_identity_single_base_model_info() {

		final ModelInfo generated = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(TEST_IDENTITY_NAMESPACE, TEST_IDENTITY_NAME, TEST_MOC_VERSION);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_EDT, TEST_IDENTITY_NAMESPACE, TEST_IDENTITY_NAME, TEST_MOC_VERSION);

		assertEquals(expected, generated);
	}

	@Test
	public void test___generate_oss_edt_for_identity_multiple_bases_no_union_model_info() {

		final String expectedName = TEST_LEAF_PATH;

		final ModelInfo generated = IdentityRefGenerator.getModelInfoForIdentityWithMultipleBases(TEST_MOC_NAMESPACE, TEST_LEAF_PATH, TEST_MOC_VERSION, null);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_EDT, TEST_MOC_NAMESPACE, expectedName, TEST_MOC_VERSION);

		assertEquals(expected, generated);
	}

	@Test
	public void test___generate_oss_edt_for_identity_multiple_bases_union_pos1_model_info() {

		final String expectedName = TEST_LEAF_PATH + "__1";

		final ModelInfo generated = IdentityRefGenerator.getModelInfoForIdentityWithMultipleBases(TEST_MOC_NAMESPACE, TEST_LEAF_PATH, TEST_MOC_VERSION, 1);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_EDT, TEST_MOC_NAMESPACE, expectedName, TEST_MOC_VERSION);

		assertEquals(expected, generated);
	}

	@Test
	public void test___generate_oss_edt_ext_for_identity_model_info() {

		final Target target = new Target(TEST_TARGET_CATEGORY, TEST_TARGET_TYPE, TEST_TARGET_VERSION);

		final String expectedNamespace = EModelHelper.getExtensionModelNamespace(target);
		final String expectedName = TEST_ORIG_EDT_NAMESPACE + "__" + TEST_ORIG_EDT_NAME + "__ext";
		final String expectedVersion = EModelHelper.getModelInfoVersionForExtension();

		final ModelInfo generated = IdentityRefGenerator.getModelInfoForExtension(target, TEST_ORIG_EDT_NAMESPACE, TEST_ORIG_EDT_NAME);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_EDT_EXT, expectedNamespace, expectedName, expectedVersion);

		assertEquals(expected, generated);
	}

	// ---------------------------------------------------------------------------------------------------

	@Test
	public void test___generate_oss_edt_for_enumeration_in_original_no_union_model_info() {

		final ModelInfo generated = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(TEST_MOC_NAMESPACE, TEST_LEAF_PATH, TEST_MOC_VERSION, null);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_EDT, TEST_MOC_NAMESPACE, TEST_LEAF_PATH, TEST_MOC_VERSION);

		assertEquals(expected, generated);
	}

	@Test
	public void test___generate_oss_edt_for_enumeration_in_original_union_pos2_model_info() {

		final ModelInfo generated = EnumerationTypeGenerator.getModelInfoForEdtInOriginal(TEST_MOC_NAMESPACE, TEST_LEAF_PATH, TEST_MOC_VERSION, 2);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_EDT, TEST_MOC_NAMESPACE, TEST_LEAF_PATH + "__2", TEST_MOC_VERSION);

		assertEquals(expected, generated);
	}

	@Test
	public void test___generate_oss_edt_for_enumeration_in_deviated_no_union_model_info() {

		final Target target = new Target(TEST_TARGET_CATEGORY, TEST_TARGET_TYPE, TEST_TARGET_VERSION);

		final String expectedNamespace = EModelHelper.getTargetVersionSpecificName(target);
		final String expectedName = TEST_MOC_NAMESPACE + "__" + TEST_LEAF_PATH;
		final String expectedVersion = Constants.ONE_ZERO_ZERO;

		final ModelInfo generated = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(target, TEST_MOC_NAMESPACE, TEST_LEAF_PATH, null);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_EDT, expectedNamespace, expectedName, expectedVersion);

		assertEquals(expected, generated);
	}

	@Test
	public void test___generate_oss_edt_for_enumeration_in_deviated_union_pos1_model_info() {

		final Target target = new Target(TEST_TARGET_CATEGORY, TEST_TARGET_TYPE, TEST_TARGET_VERSION);

		final String expectedNamespace = EModelHelper.getTargetVersionSpecificName(target);
		final String expectedName = TEST_MOC_NAMESPACE + "__" + TEST_LEAF_PATH + "__1";
		final String expectedVersion = Constants.ONE_ZERO_ZERO;

		final ModelInfo generated = EnumerationTypeGenerator.getModelInfoForEdtInDeviated(target, TEST_MOC_NAMESPACE, TEST_LEAF_PATH, 1);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_EDT, expectedNamespace, expectedName, expectedVersion);

		assertEquals(expected, generated);
	}

	@Test
	public void test___generate_combined_oss_edt() {

		final ModelInfo generated = EnumerationTypeGenerator.getModelInfoForCombinedOssEdt(TEST_MOC_NAMESPACE, TEST_MOC_VERSION);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_EDT_COMBINED, SchemaConstants.GLOBAL_MODEL_NAMESPACE, TEST_MOC_NAMESPACE, TEST_MOC_VERSION);

		assertEquals(expected, generated);
	}

	// ---------------------------------------------------------------------------------------------------

	@Test
	public void test___generate_oss_cdt_for_struct_in_original() {

		final ModelInfo generated = ComplexTypeGenerator.getModelInfoForCdtInOriginal(TEST_MOC_NAMESPACE, TEST_LEAF_PATH, TEST_MOC_VERSION);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_CDT, TEST_MOC_NAMESPACE, TEST_LEAF_PATH, TEST_MOC_VERSION);

		assertEquals(expected, generated);
	}

	@Test
	public void test___generate_oss_cdt_for_struct_in_deviated() {

		final Target target = new Target(TEST_TARGET_CATEGORY, TEST_TARGET_TYPE, TEST_TARGET_VERSION);

		final String expectedNamespace = EModelHelper.getTargetVersionSpecificName(target);
		final String expectedName = TEST_MOC_NAMESPACE + "__" + TEST_LEAF_PATH;
		final String expectedVersion = Constants.ONE_ZERO_ZERO;

		final ModelInfo generated = ComplexTypeGenerator.getModelInfoForCdtInDeviated(target, TEST_MOC_NAMESPACE, TEST_LEAF_PATH);
		final ModelInfo expected = new ModelInfo(SchemaConstants.OSS_CDT, expectedNamespace, expectedName, expectedVersion);

		assertEquals(expected, generated);
	}

	// ---------------------------------------------------------------------------------------------------

	@Test
	public void test___generate_cfm_miminfo() {

		final ModelInfo generated = CfmMimInfoGenerator.getModelInfoForCfmMimInfo(TEST_YANG_NAMESPACE, TEST_MOC_VERSION);
		final ModelInfo expected = new ModelInfo(SchemaConstants.CFM_MIMINFO, TEST_YANG_NAMESPACE, SchemaConstants.NE_DEFINED, TEST_MOC_VERSION);

		assertEquals(expected, generated);
	}
}
