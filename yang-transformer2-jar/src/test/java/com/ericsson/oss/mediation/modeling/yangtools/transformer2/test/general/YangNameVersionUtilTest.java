package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.info.XyzModelVersionInfo;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.SupportedMimType;

public class YangNameVersionUtilTest {

	@Test
	public void test___getXyzVersionForYam() {

		assertEquals("0.0.0", YangNameVersionUtil.getXyzVersionForYam("module-name", null, null));
		assertEquals("2021.10.30", YangNameVersionUtil.getXyzVersionForYam("module-name", null, "2021-10-30"));
		assertEquals("2021.1.3", YangNameVersionUtil.getXyzVersionForYam("module-name", null, "2021-01-03"));
		assertEquals("2021.1.3", YangNameVersionUtil.getXyzVersionForYam("module-name", new XyzModelVersionInfo(1, 2, 3), "2021-01-03"));

		assertEquals("1.2.3", YangNameVersionUtil.getXyzVersionForYam("ericsson-module", new XyzModelVersionInfo(1, 2, 3), null));
		assertEquals("1.2.3", YangNameVersionUtil.getXyzVersionForYam("ericsson-module", new XyzModelVersionInfo(1, 2, 3), "2021-10-30"));
		assertEquals("2021.10.30", YangNameVersionUtil.getXyzVersionForYam("ericsson-module", null, "2021-10-30"));

		assertEquals("1.2.3", YangNameVersionUtil.getXyzVersionForYam("eric-module", new XyzModelVersionInfo(1, 2, 3), null));
		assertEquals("1.2.3", YangNameVersionUtil.getXyzVersionForYam("eric-module", new XyzModelVersionInfo(1, 2, 3), "2021-10-30"));
		assertEquals("2021.10.30", YangNameVersionUtil.getXyzVersionForYam("eric-module", null, "2021-10-30"));

		assertEquals("0.0.0", YangNameVersionUtil.getXyzVersionForYam("Ericsson-module", new XyzModelVersionInfo(1, 2, 3), null));
		assertEquals("2021.10.30", YangNameVersionUtil.getXyzVersionForYam("Ericsson-module", new XyzModelVersionInfo(1, 2, 3), "2021-10-30"));
		assertEquals("2021.10.30", YangNameVersionUtil.getXyzVersionForYam("Ericsson-module", null, "2021-10-30"));

		try {
			YangNameVersionUtil.getXyzVersionForYam(null, null, null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	@Test
	public void test___isEricssonModule() {

		assertFalse(YangNameVersionUtil.isEricssonModule("module-name"));
		assertFalse(YangNameVersionUtil.isEricssonModule(""));
		assertFalse(YangNameVersionUtil.isEricssonModule("Ericsson-module"));
		assertFalse(YangNameVersionUtil.isEricssonModule("Eric-module"));
		assertFalse(YangNameVersionUtil.isEricssonModule("ericssonmodule"));
		assertFalse(YangNameVersionUtil.isEricssonModule("ericson-module"));

		assertTrue(YangNameVersionUtil.isEricssonModule("ericsson-module"));
		assertTrue(YangNameVersionUtil.isEricssonModule("eric-module"));

		try {
			YangNameVersionUtil.isEricssonModule(null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	@Test
	public void test___getEModelNamespaceForYam() {

		assertEquals("", YangNameVersionUtil.getEModelNamespaceForYam(""));
		assertEquals("abc", YangNameVersionUtil.getEModelNamespaceForYam("abc"));

		try {
			YangNameVersionUtil.getEModelNamespaceForYam(null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	private static final String TEST_YANG_MODULE_NAMESPACE = "yang-namespace";
	private static final String TEST_YANG_MODULE_NAME = "yang-module";
	private static final String TEST_YANG_MODULE_REVISION = "2020-01-30";
	private static final String TEST_MODULE_XYZ_VERSION = "2020.1.30";

	@Test
	public void test___getNetYangModelInfoForYam() {

		final ModelInfo generated1 = YangNameVersionUtil.getNetYangModelInfoForYam(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_MODULE_NAME, TEST_YANG_MODULE_REVISION);
		final ModelInfo expected1 = new ModelInfo(SchemaConstants.NET_YANG, TEST_YANG_MODULE_NAMESPACE, TEST_YANG_MODULE_NAME + "@" + TEST_YANG_MODULE_REVISION);

		assertEquals(expected1, generated1);

		final ModelInfo generated2 = YangNameVersionUtil.getNetYangModelInfoForYam(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_MODULE_NAME, null);
		final ModelInfo expected2 = new ModelInfo(SchemaConstants.NET_YANG, TEST_YANG_MODULE_NAMESPACE, TEST_YANG_MODULE_NAME);

		assertEquals(expected2, generated2);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test___createSupportedMimTypeForModule() {

		final ModelInfo modelInfo1 = YangNameVersionUtil.getNetYangModelInfoForYam(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_MODULE_NAME, TEST_YANG_MODULE_REVISION);
		final SupportedMimType mimType1 = YangNameVersionUtil.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, modelInfo1);

		assertEquals(TEST_YANG_MODULE_NAMESPACE, mimType1.getNamespace());
		assertEquals(TEST_MODULE_XYZ_VERSION, mimType1.getVersion());
		assertEquals(modelInfo1.toUrn(), mimType1.getOriginalModelUrn());

		try {
			YangNameVersionUtil.createSupportedMimTypeForModule(null, TEST_MODULE_XYZ_VERSION, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			YangNameVersionUtil.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, null, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			YangNameVersionUtil.createSupportedMimTypeForModule(TEST_YANG_MODULE_NAMESPACE, TEST_MODULE_XYZ_VERSION, null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	private static final String TEST_YANG_SUBMODULE_NAME = "yang-submodule";
	private static final String TEST_YANG_SUBMODULE_REVISION = "2020-12-01";
	private static final String TEST_SUBMODULE_XYZ_VERSION = "2020.12.1";

	@SuppressWarnings("deprecation")
	@Test
	public void test___createSupportedMimTypeForSubmodule() {

		final ModelInfo modelInfo1 = YangNameVersionUtil.getNetYangModelInfoForYam(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_YANG_SUBMODULE_REVISION);
		final SupportedMimType mimType1 = YangNameVersionUtil.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, modelInfo1);

		assertEquals(TEST_YANG_MODULE_NAMESPACE + "::" + TEST_YANG_SUBMODULE_NAME, mimType1.getNamespace());
		assertEquals(TEST_SUBMODULE_XYZ_VERSION, mimType1.getVersion());
		assertEquals(modelInfo1.toUrn(), mimType1.getOriginalModelUrn());

		try {
			YangNameVersionUtil.createSupportedMimTypeForSubmodule(null, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			YangNameVersionUtil.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, null, TEST_SUBMODULE_XYZ_VERSION, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			YangNameVersionUtil.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, null, modelInfo1);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}

		try {
			YangNameVersionUtil.createSupportedMimTypeForSubmodule(TEST_YANG_MODULE_NAMESPACE, TEST_YANG_SUBMODULE_NAME, TEST_SUBMODULE_XYZ_VERSION, null);
			fail("Should have thrown.");
		} catch (final Exception expected) {
		}
	}

	@Test
	public void test___cleanImsName() {

		assertEquals("eric-12.34.5", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3---12.34.5"));
		assertEquals("urn:ericsson:bgf-1-0-5", YangNameVersionUtil.removeNodeAppInstanceName("urn:ericsson:bgf---london3---1-0-5"));
		assertEquals("urn:ericsson:bgf-1-0.5", YangNameVersionUtil.removeNodeAppInstanceName("urn:ericsson:bgf---london3---1-0.5"));
		assertEquals("urn:ericsson:bgf-0.0-0", YangNameVersionUtil.removeNodeAppInstanceName("urn:ericsson:bgf---london3---0.0-0"));
		assertEquals("urn:ericsson:bgf-0.0.0", YangNameVersionUtil.removeNodeAppInstanceName("urn:ericsson:bgf---london3---0.0.0"));

		assertEquals("eric", YangNameVersionUtil.removeNodeAppInstanceName("eric"));
		assertEquals("eric---", YangNameVersionUtil.removeNodeAppInstanceName("eric---"));
		assertEquals("eric---london3", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3"));
		assertEquals("eric---london3-", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3-"));
		assertEquals("eric---london3--", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3--"));
		assertEquals("eric---london3---", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3---"));
		assertEquals("eric---london3-1", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3-1"));
		assertEquals("eric---london3--1.", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3--1."));
		assertEquals("eric---london3-1-2", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3-1-2"));
		assertEquals("eric---london3-1-2-", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3-1-2-"));

		assertEquals("eric---london3", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3"));
		assertEquals("eric---london3---", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3---"));
		assertEquals("eric--- ---1-2-3", YangNameVersionUtil.removeNodeAppInstanceName("eric--- ---1-2-3"));
		assertEquals(" ---london3---1-2-3", YangNameVersionUtil.removeNodeAppInstanceName(" ---london3---1-2-3"));
		assertEquals("eric---london3--- ", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3--- "));
		assertEquals("eric---london3---1-2-3---something-else", YangNameVersionUtil.removeNodeAppInstanceName("eric---london3---1-2-3---something-else"));

		assertEquals("", YangNameVersionUtil.removeNodeAppInstanceName(""));
		assertEquals("---", YangNameVersionUtil.removeNodeAppInstanceName("---"));
		assertEquals("------", YangNameVersionUtil.removeNodeAppInstanceName("------"));
	}
}
