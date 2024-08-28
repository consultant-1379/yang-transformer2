package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.CfmMimInfoSupport;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.SupportedMimType;

public class CfmMimInfoSupportTest {

	private static final String TEST_YANG_MODULE_NAMESPACE = "yang-namespace";
	private static final String TEST_YANG_MODULE_NAME = "yang-module";
	private static final String TEST_YANG_MODULE_REVISION = "2020-01-30";
	private static final String TEST_MODULE_XYZ_VERSION = "2020.1.30";

	@SuppressWarnings("deprecation")
	@Test
	public void test___createSupportedMimTypeForModule___deprecated_method() {

		final ModelInfo modelInfo1 = YangNameVersionUtil.getNetYangModelInfoForYam(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_MODULE_NAME, TEST_YANG_MODULE_REVISION);
		final SupportedMimType mimType1 = CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, modelInfo1);

		assertEquals(TEST_YANG_MODULE_NAMESPACE, mimType1.getNamespace());
		assertEquals(TEST_MODULE_XYZ_VERSION, mimType1.getVersion());
		assertEquals(modelInfo1.toUrn(), mimType1.getOriginalModelUrn());

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(null, TEST_MODULE_XYZ_VERSION, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, null, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	private static final String TEST_FEATURE_HANDLING = "feature-handling";
	private static final List<String> TEST_FEATURES = Arrays.asList("f1", "f2", "f3");
	private static final String TEST_CONFORMANCE = "conformance";
	private static final String TEST_YAM_NAME = "yam-name";

	@Test
	public void test___createSupportedMimTypeForModule() {

		final ModelInfo modelInfo = YangNameVersionUtil.getNetYangModelInfoForYam(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_MODULE_NAME, TEST_YANG_MODULE_REVISION);
		final SupportedMimType mimType = CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, modelInfo, TEST_FEATURE_HANDLING, TEST_FEATURES, TEST_CONFORMANCE, TEST_YAM_NAME);

		assertEquals(TEST_YANG_MODULE_NAMESPACE, mimType.getNamespace());
		assertEquals(TEST_MODULE_XYZ_VERSION, mimType.getVersion());
		assertEquals(modelInfo.toUrn(), mimType.getOriginalModelUrn());
		assertEquals(TEST_FEATURE_HANDLING, mimType.getFeatureHandling());
		assertEquals(TEST_FEATURES, mimType.getFeatures());
		assertEquals(TEST_CONFORMANCE, mimType.getConformance());
		assertEquals(TEST_YAM_NAME, mimType.getYamName());
		assertEquals("MODULE", mimType.getType());

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(null, TEST_MODULE_XYZ_VERSION, modelInfo, TEST_FEATURE_HANDLING, TEST_FEATURES, TEST_CONFORMANCE, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, null, modelInfo, TEST_FEATURE_HANDLING, TEST_FEATURES, TEST_CONFORMANCE, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, null, TEST_FEATURE_HANDLING, TEST_FEATURES, TEST_CONFORMANCE, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, modelInfo, null, TEST_FEATURES, TEST_CONFORMANCE, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, modelInfo, TEST_FEATURE_HANDLING, null, TEST_CONFORMANCE, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, modelInfo, TEST_FEATURE_HANDLING, TEST_FEATURES, null, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, modelInfo, TEST_FEATURE_HANDLING, TEST_FEATURES, TEST_CONFORMANCE, null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	private static final String TEST_YANG_SUBMODULE_NAME = "yang-submodule";
	private static final String TEST_YANG_SUBMODULE_REVISION = "2020-12-01";
	private static final String TEST_SUBMODULE_XYZ_VERSION = "2020.12.1";

	@SuppressWarnings("deprecation")
	@Test
	public void test___createSupportedMimTypeForSubmodule___deprecated_method() {

		final ModelInfo modelInfo1 = YangNameVersionUtil.getNetYangModelInfoForYam(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_YANG_SUBMODULE_REVISION);
		final SupportedMimType mimType1 = CfmMimInfoSupport.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, modelInfo1);

		assertEquals(TEST_YANG_MODULE_NAMESPACE + "::" + TEST_YANG_SUBMODULE_NAME, mimType1.getNamespace());
		assertEquals(TEST_SUBMODULE_XYZ_VERSION, mimType1.getVersion());
		assertEquals(modelInfo1.toUrn(), mimType1.getOriginalModelUrn());

		try {
			CfmMimInfoSupport.createSupportedMimTypeForSubmodule(null, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, null, TEST_SUBMODULE_XYZ_VERSION, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, null, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	@Test
	public void test___createSupportedMimTypeForSubmodule() {

		final ModelInfo modelInfo = YangNameVersionUtil.getNetYangModelInfoForYam(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_YANG_SUBMODULE_REVISION);
		final SupportedMimType mimType = CfmMimInfoSupport.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, modelInfo, TEST_YAM_NAME);

		assertEquals(TEST_YANG_MODULE_NAMESPACE + "::" + TEST_YANG_SUBMODULE_NAME, mimType.getNamespace());
		assertEquals(TEST_SUBMODULE_XYZ_VERSION, mimType.getVersion());
		assertEquals(modelInfo.toUrn(), mimType.getOriginalModelUrn());
		assertEquals(TEST_YAM_NAME, mimType.getYamName());
		assertEquals("SUBMODULE", mimType.getType());

		try {
			CfmMimInfoSupport.createSupportedMimTypeForSubmodule(null, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, modelInfo, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, null, TEST_SUBMODULE_XYZ_VERSION, modelInfo, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, null, modelInfo, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, null, TEST_YAM_NAME);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			CfmMimInfoSupport.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, modelInfo, null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	@Test
	public void test___createSupportedMimTypeForMockManagedElement() {

		final SupportedMimType mimType = CfmMimInfoSupport.createSupportedMimTypeForMockManagedElement(TEST_YANG_MODULE_NAMESPACE);

		assertEquals(TEST_YANG_MODULE_NAMESPACE, mimType.getNamespace());
		assertEquals(Constants.ONE_ZERO_ZERO, mimType.getVersion());

		try {
			CfmMimInfoSupport.createSupportedMimTypeForMockManagedElement(null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}
}
