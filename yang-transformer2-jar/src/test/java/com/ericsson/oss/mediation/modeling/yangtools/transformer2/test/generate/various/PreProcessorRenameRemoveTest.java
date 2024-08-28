package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAction;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeActionParameter;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;

public class PreProcessorRenameRemoveTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/pre-processor/";

	private static final String MODULE_NS_RENAME_ONE = "urn*test*rename-schema-nodes-one";
	private static final String MODULE_XYZ_VERSION_RENAME_ONE = "2021.3.7";

	private static final String MODULE_NS_RENAME_TWO = "urn*test*rename-schema-nodes-two";
	private static final String MODULE_XYZ_VERSION_RENAME_TWO = "2021.4.4";

	@Test
	public void test___rename_schema_nodes() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N663_DUPLICATE_DATA_NODE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "rename-schema-nodes"));

		final Map<String, String> toRename = new HashMap<>();

		toRename.put("/cont1/leaf12", "leaf12renamed");
		toRename.put("/urn*test*rename-schema-nodes-one:cont1/action15/input/leaf16", "leaf16renamed");
		toRename.put("/urn*test*rename-schema-nodes-one:list2/choice24/case2/leaf27", "leaf27renamed");
		toRename.put("/rpc3", "rpc3renamed");

		toRename.put("/cont1/cont6/leaf61", "leaf61renamed");
		toRename.put("/cont1/urn*test*rename-schema-nodes-two:cont6/leaf62", "leaf62renamed");
		toRename.put("/urn*test*rename-schema-nodes-two:list2/leaf72", "leaf72renamed");

		context.setSchemaNodesToRename(toRename);
		context.setGenerateRpcs(true);

		YangTransformer2.transform(context);

		internal___rename_schema_nodes(context, null);
	}

	@Test
	public void test___rename_schema_nodes_through_nmt_plugin() {

		final StringBuilder sb = new StringBuilder();
		sb.append("/cont1/leaf12").append(" ").append("leaf12renamed").append(",");
		sb.append("/urn*test*rename-schema-nodes-one:cont1/action15/input/leaf16").append(" ").append("leaf16renamed").append(",");
		sb.append("/urn*test*rename-schema-nodes-one:list2/choice24/case2/leaf27").append(" ").append("leaf27renamed").append(",");
		sb.append("/rpc3").append(" ").append("rpc3renamed").append(",");

		sb.append("/cont1/cont6/leaf61").append(" ").append("leaf61renamed").append(",");
		sb.append("/cont1/urn*test*rename-schema-nodes-two:cont6/leaf62").append(" ").append("leaf62renamed").append(",");
		sb.append("/urn*test*rename-schema-nodes-two:list2/leaf72").append(" ").append("leaf72renamed");

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_RENAME, sb.toString());
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_RPCS, "true");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "rename-schema-nodes"), overwritingProperties);

		internal___rename_schema_nodes(null, actualNmtProperties);
	}

	private void internal___rename_schema_nodes(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_RENAME_ONE, "cont1", MODULE_XYZ_VERSION_RENAME_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_RENAME_ONE, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1.getPrimaryTypeAttribute(), "leaf11");
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, leaf11);

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1.getPrimaryTypeAttribute(), "leaf12renamed");
		assertHasMeta(Constants.META_ORIGINAL_NAME, "leaf12", leaf12);

		final PrimaryTypeAction action15 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action15");
		final PrimaryTypeActionParameter leaf16 = findActionParameter(action15.getParameter(), "leaf16renamed");
		assertHasMeta(Constants.META_ORIGINAL_NAME, "leaf16", leaf16);

		final ModelInfo list2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_RENAME_ONE, "list2", MODULE_XYZ_VERSION_RENAME_ONE);
		final PrimaryTypeDefinition list2 = load(list2modelInfo);

		final PrimaryTypeAttribute leaf27 = findAttribute(list2.getPrimaryTypeAttribute(), "leaf27renamed");
		assertHasMeta(Constants.META_ORIGINAL_NAME, "leaf27", leaf27);

		final ModelInfo comTopExtModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, COM_TOP_MANAGEDELEMENT_10221.getNamespace(), COM_TOP_MANAGEDELEMENT_10221.getName());
		final PrimaryTypeExtensionDefinition comTopExt = load(comTopExtModelInfo);

		final PrimaryTypeAction rpc3 = findAction(comTopExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc3renamed");
		assertHasMeta(Constants.META_ORIGINAL_NAME, "rpc3", rpc3);

		final ModelInfo cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_RENAME_TWO, "cont6", MODULE_XYZ_VERSION_RENAME_TWO);
		final PrimaryTypeDefinition cont6 = load(cont6modelInfo);

		final PrimaryTypeAttribute leaf61 = findAttribute(cont6.getPrimaryTypeAttribute(), "leaf61renamed");
		assertHasMeta(Constants.META_ORIGINAL_NAME, "leaf61", leaf61);

		final PrimaryTypeAttribute leaf62 = findAttribute(cont6.getPrimaryTypeAttribute(), "leaf62renamed");
		assertHasMeta(Constants.META_ORIGINAL_NAME, "leaf62", leaf62);

		final ModelInfo listTwo2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_RENAME_TWO, "list2", MODULE_XYZ_VERSION_RENAME_TWO);
		final PrimaryTypeDefinition listTwo2 = load(listTwo2modelInfo);

		final PrimaryTypeAttribute leaf71 = findAttribute(listTwo2.getPrimaryTypeAttribute(), "leaf71");
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, leaf71);

		final PrimaryTypeAttribute leaf72 = findAttribute(listTwo2.getPrimaryTypeAttribute(), "leaf72renamed");
		assertHasMeta(Constants.META_ORIGINAL_NAME, "leaf72", leaf72);
	}

	private static final String MODULE_NS_REMOVE_ONE = "urn*test*remove-schema-nodes-one";
	private static final String MODULE_XYZ_VERSION_REMOVE_ONE = "2021.3.7";

	private static final String MODULE_NS_REMOVE_TWO = "urn*test*remove-schema-nodes-two";
	private static final String MODULE_XYZ_VERSION_REMOVE_TWO = "2021.4.4";

	@Test
	public void test___remove_schema_nodes() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N663_DUPLICATE_DATA_NODE.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "remove-schema-nodes"));

		final List<String> toRemove = new ArrayList<>();

		toRemove.add("/cont1/leaf11");
		toRemove.add("/urn*test*remove-schema-nodes-one:cont1/action15/input/leaf16");
		toRemove.add("/urn*test*remove-schema-nodes-one:list2/choice24/case1/leaf26");
		toRemove.add("/cont1/action17");

		toRemove.add("/cont1/cont6/leaf61");
		toRemove.add("/urn*test*remove-schema-nodes-two:list2/leaf71");
		toRemove.add("/rpc4");

		context.setSchemaNodesToRemove(toRemove);
		context.setGenerateRpcs(true);

		YangTransformer2.transform(context);

		internal___remove_schema_nodes(context, null);
	}

	@Test
	public void test___remove_schema_nodes_through_nmt_plugin() {

		final StringBuilder sb = new StringBuilder();
		sb.append("/cont1/leaf11").append(",");
		sb.append("/urn*test*remove-schema-nodes-one:cont1/action15/input/leaf16").append(",");
		sb.append("/urn*test*remove-schema-nodes-one:list2/choice24/case1/leaf26").append(",");
		sb.append("/cont1/action17").append(",");

		sb.append("/cont1/cont6/leaf61").append(",");
		sb.append("/urn*test*remove-schema-nodes-two:list2/leaf71").append(",");
		sb.append("/rpc4");

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.SCHEMA_NODES_TO_REMOVE, sb.toString());
		overwritingProperties.setProperty(YangTransformer2PluginProperties.GENERATE_RPCS, "true");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "remove-schema-nodes"), overwritingProperties);

		internal___remove_schema_nodes(null, actualNmtProperties);
	}

	private void internal___remove_schema_nodes(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_REMOVE_ONE, "cont1", MODULE_XYZ_VERSION_REMOVE_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final ModelInfo cont1ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_REMOVE_ONE, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1ExtensionModelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1.getPrimaryTypeAttribute(), "leaf11");
		assertNull(leaf11);

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1.getPrimaryTypeAttribute(), "leaf12");
		assertNotNull(leaf12);

		final PrimaryTypeAction action15 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action15");
		assertEmpty(action15.getParameter());

		final PrimaryTypeAction action17 = findAction(cont1ext.getPrimaryTypeExtension().getPrimaryTypeAction(), "action17");
		assertNull(action17);

		final ModelInfo list2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_REMOVE_ONE, "list2", MODULE_XYZ_VERSION_REMOVE_ONE);
		final PrimaryTypeDefinition list2 = load(list2modelInfo);

		final PrimaryTypeAttribute leaf26 = findAttribute(list2.getPrimaryTypeAttribute(), "leaf26");
		assertNull(leaf26);

		final PrimaryTypeAttribute leaf27 = findAttribute(list2.getPrimaryTypeAttribute(), "leaf27");
		assertNotNull(leaf27);

		final ModelInfo cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_REMOVE_TWO, "cont6", MODULE_XYZ_VERSION_REMOVE_TWO);
		final PrimaryTypeDefinition cont6 = load(cont6modelInfo);

		final PrimaryTypeAttribute leaf61 = findAttribute(cont6.getPrimaryTypeAttribute(), "leaf61");
		assertNull(leaf61);

		final PrimaryTypeAttribute leaf62 = findAttribute(cont6.getPrimaryTypeAttribute(), "leaf62");
		assertNotNull(leaf62);

		final ModelInfo listTwo2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_REMOVE_TWO, "list2", MODULE_XYZ_VERSION_REMOVE_TWO);
		final PrimaryTypeDefinition listTwo2 = load(listTwo2modelInfo);

		final PrimaryTypeAttribute leaf71 = findAttribute(listTwo2.getPrimaryTypeAttribute(), "leaf71");
		assertNull(leaf71);

		final PrimaryTypeAttribute leaf72 = findAttribute(listTwo2.getPrimaryTypeAttribute(), "leaf72");
		assertNotNull(leaf72);

		final ModelInfo comTopExtModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, COM_TOP_MANAGEDELEMENT_10221.getNamespace(), COM_TOP_MANAGEDELEMENT_10221.getName());
		final PrimaryTypeExtensionDefinition comTopExt = load(comTopExtModelInfo);

		final PrimaryTypeAction rpc3 = findAction(comTopExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc3");
		assertNotNull(rpc3);

		final PrimaryTypeAction rpc4 = findAction(comTopExt.getPrimaryTypeExtension().getPrimaryTypeAction(), "rpc4");
		assertNull(rpc4);
	}
}
