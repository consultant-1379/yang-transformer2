package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.FeatureHandling;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class CheckYangLibraryContentTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/check-yang-library-content/modules";
	private static final String INSTANCE_DATA_DIR = TEST_MODULES_DIR + "various/check-yang-library-content/yanglib-instance-data/";

	@Test
	public void test___check_yang_library_content___yl_ok() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-ok.xml"));

		YangTransformer2.transform(context);
	}

	@Test
	public void test___check_yang_library_content___yl_tailf_module_excluded() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-tailf-module.xml"));
		context.setExcludedNamespaces(Collections.singletonList("https://tail-f.com/.*"));

		YangTransformer2.transform(context);
	}

	@Test
	public void test___check_yang_library_content___yl_module4_excluded() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-ok.xml"));
		context.setExcludedNamespaces(Collections.singletonList("urn:test:module4"));

		YangTransformer2.transform(context);
	}

	@Test
	public void test___check_yang_library_content___yl_module1_wrong_namespace() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-module1-wrong-namespace.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "module1");
		assertErrorTextContains(context, "wrong-namespace");
	}

	@Test
	public void test___check_yang_library_content___yl_module3_wrong_conformance() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-module3-wrong-conformance.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "module3");
		assertErrorTextContains(context, "conformance");
	}

	@Test
	public void test___check_yang_library_content___yl_submodule2_wrong_revision() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-submodule2-wrong-revision.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "submodule2");
		assertErrorTextContains(context, "not found");
	}

	@Test
	public void test___check_yang_library_content___yl_superfluous_submodule99() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-superfluous-submodule99.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "submodule99");
		assertErrorTextContains(context, "not found");
	}

	@Test
	public void test___check_yang_library_content___yl_submodule2_not_listed() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-submodule2-not-listed.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "submodule2");
		assertErrorTextContains(context, "not listed");
	}

	@Test
	public void test___check_yang_library_content___yl_module4_unknown_feature() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-module4-unknown-feature.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "module4");
		assertErrorTextContains(context, "unknown-feature");
	}

	@Test
	public void test___check_yang_library_content___yl_module99_listed_but_not_in_input() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-module99-listed.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "module99");
		assertErrorTextContains(context, "not found");
	}

	@Test
	public void test___check_yang_library_content___yl_module1_missing_from_yl() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-module1-missing.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "module1");
		assertErrorTextContains(context, "not listed");
	}

	@Test
	public void test___check_yang_library_content___yl_missing_vrc() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-missing-vrc.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "module4");
		assertErrorTextContains(context, "v/r/c mismatch");
	}

	@Test
	public void test___check_yang_library_content___yl_superfluous_vrc() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-superfluous-vrc.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "module1");
		assertErrorTextContains(context, "v/r/c mismatch");
	}

	@Test
	public void test___check_yang_library_content___yl_wrong_vrc() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		context.setFeatureHandling(FeatureHandling.CONFIGURED);
		context.setYangLibraryInstanceData(new File(INSTANCE_DATA_DIR + "yanglib-wrong-vrc.xml"));

		try {
			YangTransformer2.transform(context);
			fail("Should have thrown.");
		} catch (final Exception expected) {}

		assertErrorTextContains(context, "module4");
		assertErrorTextContains(context, "v/r/c mismatch");
	}

	private static void assertErrorTextContains(final TransformerContext context, final String soughtText) {
		for(final String error : context.getErrors()) {
			if(error.toLowerCase().contains(soughtText)) {
				return;
			}
		}
		fail("Error text did not contain expected text '" + soughtText + "'.");
	}
}
