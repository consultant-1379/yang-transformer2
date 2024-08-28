package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ModuleIdentity;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.Module;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.Submodule;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.YangLibrary;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.eri.VersionReleaseCorrection;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.eri.YangLibraryEricssonExtensionsPopulator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class BuildYangLibraryTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/build-yang-library";

	@Test
	public void test___build_yang_library() {

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR), new File(ERI_YANG_EXT)));
		YangTransformer2.transform(context);

		final YangLibrary yangLib = context.getTopLevelYangLibrary();
		final Set<Module> allModules = yangLib.getRunningDatastore().getAllModules();
		assertSize(3, allModules);

		final Module module1 = allModules.stream().filter(m -> m.getName().equals("module1")).findAny().orElse(null);
		assertNotNull(module1);
		assertEquals("module1", module1.getName());
		assertEquals("2023-08-23", module1.getRevision());
		assertEquals("urn:test:module1", module1.getNamespace());
		assertEquals(new ModuleIdentity("module1", "2023-08-23"), module1.getModuleIdentity());

		final VersionReleaseCorrection module1vrc = YangLibraryEricssonExtensionsPopulator.getVRC(module1);
		assertNotNull(module1vrc);
		assertEquals(1, module1vrc.getVersion());
		assertEquals(2, module1vrc.getRelease());
		assertEquals(3, module1vrc.getCorrection());

		assertSize(1, module1.getSubmodules());
		final Submodule submodule3 = module1.getSubmodules().get(0);
		assertEquals("submodule3", submodule3.getName());
		assertEquals("2023-08-24", submodule3.getRevision());
		assertEquals(new ModuleIdentity("submodule3", "2023-08-24"), submodule3.getModuleIdentity());

		final VersionReleaseCorrection submodule3vrc = YangLibraryEricssonExtensionsPopulator.getVRC(submodule3);
		assertNotNull(submodule3vrc);
		assertEquals(7, submodule3vrc.getVersion());
		assertEquals(8, submodule3vrc.getRelease());
		assertEquals(9, submodule3vrc.getCorrection());

		final Module module2 = allModules.stream().filter(m -> m.getName().equals("module2")).findAny().orElse(null);
		assertNotNull(module2);
		assertEquals("module2", module2.getName());
		assertEquals("2023-08-25", module2.getRevision());
		assertEquals("urn:test:module2", module2.getNamespace());
		assertEquals(new ModuleIdentity("module2", "2023-08-25"), module2.getModuleIdentity());

		final VersionReleaseCorrection module2vrc = YangLibraryEricssonExtensionsPopulator.getVRC(module2);
		assertNotNull(module2vrc);
		assertEquals(4, module2vrc.getVersion());
		assertEquals(5, module2vrc.getRelease());
		assertEquals(6, module2vrc.getCorrection());
	}
}
