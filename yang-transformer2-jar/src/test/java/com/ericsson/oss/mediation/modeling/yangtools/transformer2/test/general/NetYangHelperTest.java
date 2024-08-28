package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.input.YangInput;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.NetYangHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;

public class NetYangHelperTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/copy-yams/";

	@Test
	public void test___normal_generation() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P032_MISSING_REVISION.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "cleaned"));
		context.setApplyNodeAppInstanceNameHandling(true);

		YangTransformer2.transform(context);
		internal___normal_generation(context, null);
	}

	@Test
	public void test___normal_generation___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_NODE_APP_INSTANCE_NAME_HANDLING, "true");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "cleaned"), overwritingProperties);

		internal___normal_generation(null, actualNmtProperties);
	}

	private void internal___normal_generation(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo modelInfoForModule1 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module1", "module1", "2023-03-24");
		assertModelExists(modelInfoForModule1);

		final ModelInfo modelInfoForModule2 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module2", "module2", null);
		assertModelExists(modelInfoForModule2);

		final ModelInfo modelInfoForModule3 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module3", "module3", "2023-03-24");
		assertModelExists(modelInfoForModule3);

		final ModelInfo modelInfoForSubmodule4 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module3", "submodule4", null);
		assertModelExists(modelInfoForSubmodule4);

		final ModelInfo modelInfoForImsModule = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:eric-ims-bgf-2-2-5-260", "eric-ims-bgf-2-2-5-260", "2023-03-25");
		assertModelExists(modelInfoForImsModule);

		final ModelInfo modelInfoForAdditionalModule = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:additional-module", "additional-module", "2023-03-22");
		assertModelDoesNotExist(modelInfoForAdditionalModule);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final String module1raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForModule1));
		assertTrue(module1raw.contains("module module1"));
		assertTrue(module1raw.contains("namespace \"urn:test:module1\";"));
		assertFalse(module1raw.contains("default \"Hello!\";"));

		final String module2raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForModule2));
		assertTrue(module2raw.contains("module module2"));
		assertTrue(module2raw.contains("namespace \"urn:test:module2\";"));

		final String module3raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForModule3));
		assertTrue(module3raw.contains("module module3"));
		assertTrue(module3raw.contains("namespace \"urn:test:module3\";"));

		final String module4raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForSubmodule4));
		assertTrue(module4raw.contains("submodule submodule4"));

		final String moduleImsRaw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForImsModule));
		assertTrue(moduleImsRaw.contains("module eric-ims-bgf---london3---2-2-5-260"));
		assertTrue(moduleImsRaw.contains("namespace \"urn:test:eric-ims-bgf---london3---2-2-5-260\";"));
	}

	@Test
	public void test___normal_generation___and_originals() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P032_MISSING_REVISION.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "cleaned"));
		context.setApplyNodeAppInstanceNameHandling(true);
		context.setOriginalImplementingFiles(Arrays.asList(new File(TEST_SUB_DIR + "orig")));

		YangTransformer2.transform(context);
		internal___normal_generation___and_originals(context, null);
	}

	@Test
	public void test___normal_generation___and_originals___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_NODE_APP_INSTANCE_NAME_HANDLING, "true");
		overwritingProperties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPLEMENTING_MODULES, new File(TEST_SUB_DIR + "orig").getAbsolutePath());

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "cleaned"), overwritingProperties);

		internal___normal_generation___and_originals(null, actualNmtProperties);
	}

	private void internal___normal_generation___and_originals(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo modelInfoForModule1 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module1", "module1", "2023-03-24");
		assertModelExists(modelInfoForModule1);

		final ModelInfo modelInfoForModule2 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module2", "module2", null);
		assertModelExists(modelInfoForModule2);

		final ModelInfo modelInfoForModule3 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module3", "module3", "2023-03-24");
		assertModelExists(modelInfoForModule3);

		final ModelInfo modelInfoForSubmodule4 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module3", "submodule4", null);
		assertModelExists(modelInfoForSubmodule4);

		final ModelInfo modelInfoForImsModule = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:eric-ims-bgf-2-2-5-260", "eric-ims-bgf-2-2-5-260", "2023-03-25");
		assertModelExists(modelInfoForImsModule);

		final ModelInfo modelInfoForAdditionalModule = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:additional-module", "additional-module", "2023-03-22");
		assertModelExists(modelInfoForAdditionalModule);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final String module1raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForModule1));
		assertTrue(module1raw.contains("module module1"));
		assertTrue(module1raw.contains("namespace \"urn:test:module1\";"));
		assertTrue(module1raw.contains("default \"Hello!\";"));

		final String module2raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForModule2));
		assertTrue(module2raw.contains("module module2"));
		assertTrue(module2raw.contains("namespace \"urn:test:module2\";"));

		final String module3raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForModule3));
		assertTrue(module3raw.contains("module module3"));
		assertTrue(module3raw.contains("namespace \"urn:test:module3\";"));

		final String module4raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForSubmodule4));
		assertTrue(module4raw.contains("submodule submodule4"));

		final String moduleImsRaw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForImsModule));
		assertTrue(moduleImsRaw.contains("module eric-ims-bgf---london3---2-2-5-260"));
		assertTrue(moduleImsRaw.contains("namespace \"urn:test:eric-ims-bgf---london3---2-2-5-260\";"));

		final String additionalModuleRaw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForAdditionalModule));
		assertTrue(additionalModuleRaw.contains("additional-module"));
		assertTrue(additionalModuleRaw.contains("namespace \"urn:test:additional-module\";"));
	}

	@Test
	public void test___normal_generation___and_originals___implement_and_import() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P032_MISSING_REVISION.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "cleaned"));
		context.setApplyNodeAppInstanceNameHandling(true);
		context.setOriginalImplementingFiles(Arrays.asList(new File(TEST_SUB_DIR + "orig/module1.yang")));
		context.setOriginalImportingFiles(Arrays.asList(new File(TEST_SUB_DIR + "orig/module2.yang")));

		YangTransformer2.transform(context);
		internal___normal_generation___and_originals___implement_and_import(context, null);
	}

	@Test
	public void test___normal_generation___and_originals___implement_and_import___through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_NODE_APP_INSTANCE_NAME_HANDLING, "true");
		overwritingProperties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPLEMENTING_MODULES, new File(TEST_SUB_DIR + "orig/module1.yang").getAbsolutePath());
		overwritingProperties.setProperty(YangTransformer2PluginProperties.ORIGINAL_IMPORTING_MODULES, new File(TEST_SUB_DIR + "orig/module2.yang").getAbsolutePath());

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "cleaned"), overwritingProperties);

		internal___normal_generation___and_originals___implement_and_import(null, actualNmtProperties);
	}

	private void internal___normal_generation___and_originals___implement_and_import(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo modelInfoForModule1 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module1", "module1", "2023-03-24");
		assertModelExists(modelInfoForModule1);

		final ModelInfo modelInfoForModule2 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module2", "module2", null);
		assertModelExists(modelInfoForModule2);

		final ModelInfo modelInfoForModule3 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module3", "module3", "2023-03-24");
		assertModelDoesNotExist(modelInfoForModule3);

		final ModelInfo modelInfoForSubmodule4 = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:module3", "submodule4", null);
		assertModelDoesNotExist(modelInfoForSubmodule4);

		final ModelInfo modelInfoForImsModule = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:eric-ims-bgf-2-2-5-260", "eric-ims-bgf-2-2-5-260", "2023-03-25");
		assertModelDoesNotExist(modelInfoForImsModule);

		final ModelInfo modelInfoForAdditionalModule = YangNameVersionUtil.getNetYangModelInfoForYam("urn:test:additional-module", "additional-module", "2023-03-22");
		assertModelDoesNotExist(modelInfoForAdditionalModule);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final String module1raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForModule1));
		assertTrue(module1raw.contains("module module1"));
		assertTrue(module1raw.contains("namespace \"urn:test:module1\";"));
		assertTrue(module1raw.contains("default \"Hello!\";"));

		final String module2raw = loadRaw(modelService.getDirectAccess().getAsInputStream(modelInfoForModule2));
		assertTrue(module2raw.contains("module module2"));
		assertTrue(module2raw.contains("namespace \"urn:test:module2\";"));
	}

	@Test
	public void test___exception_on_copy___bad_input_stream() {
		try {
			NetYangHelper.copyYamContents(new FaultyYangInput(), new File(BASE_OUT_DIR + "/out-file"));
			fail("Should have thrown");
		} catch (final Exception expected) {
		}
	}

	private static class FaultyYangInput implements YangInput {

		@Override
		public File getFile() {
			return null;
		}

		@Override
		public InputStream getInputStream() {
			throw new RuntimeException("Faulty Yang Input");
		}

		@Override
		public String getMediaType() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}
	}
}
