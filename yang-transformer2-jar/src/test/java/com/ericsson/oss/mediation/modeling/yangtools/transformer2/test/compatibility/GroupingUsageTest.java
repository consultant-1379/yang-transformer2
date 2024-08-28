package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.compatibility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class GroupingUsageTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "compatibility/";

	private static final String MODULE_NS_ONE = "urn!test!grouping-usage-one";
	private static final String MODULE_XYZ_VERSION_ONE = "2021.12.12";

	private static final String MODULE_NS_TWO = "urn+test+grouping-usage-two";

	/**
	 * Tests that groupings get the correct namespace depending on where they are used, and that grouping
	 * content defined in a submodule gets generated into an extension.
	 */
	@Test
	public void test___grouping_usage() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "grouping-usage"));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont7", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont7 = load(cont7modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont7, "leaf11");
		assertEquals(MODULE_NS_ONE, leaf11.getNs());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont7, "leaf12");
		assertEquals(MODULE_NS_ONE, leaf12.getNs());

		final PrimaryTypeAttribute leaf21 = findAttribute(cont7, "leaf21");
		assertEquals(MODULE_NS_ONE, leaf21.getNs());

		final PrimaryTypeAttribute leaf31 = findAttribute(cont7, "leaf31");
		assertEquals(MODULE_NS_ONE, leaf31.getNs());

		final PrimaryTypeAttribute leaf41 = findAttribute(cont7, "leaf41");		// grouping sits in submodule, so gets generated into extension
		assertNull(leaf41);

		final PrimaryTypeAttribute leaf42 = findAttribute(cont7, "leaf42");		// grouping sits in submodule, so gets generated into extension
		assertNull(leaf42);

		final PrimaryTypeAttribute leaf51 = findAttribute(cont7, "leaf51");		// grouping sits in other module, so gets generated into extension
		assertNull(leaf51);

		final PrimaryTypeAttribute leaf52 = findAttribute(cont7, "leaf52");		// grouping sits in other module, so gets generated into extension
		assertNull(leaf52);

		final PrimaryTypeAttribute leaf61 = findAttribute(cont7, "leaf61");		// augmented-in, so gets generated into extension
		assertNull(leaf61);

		final PrimaryTypeAttribute leaf62 = findAttribute(cont7, "leaf62");		// augmented-in, so gets generated into extension
		assertNull(leaf62);

		final PrimaryTypeAttribute leaf71 = findAttribute(cont7, "leaf71");
		assertEquals(MODULE_NS_ONE, leaf71.getNs());

		// - - - - - - - - - - - - - -

		final ModelInfo cont7ExtensionModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS_ONE, "cont7");
		final PrimaryTypeExtensionDefinition cont7ext = load(cont7ExtensionModelInfo);

		assertSize(6, cont7ext.getPrimaryTypeExtension().getPrimaryTypeAttribute());

		final PrimaryTypeAttribute leaf41ext = findAddedAttribute(cont7ext, "leaf41");
		assertEquals(MODULE_NS_ONE, leaf41ext.getNs());					// in submodule, so NS ONE

		final PrimaryTypeAttribute leaf42ext = findAddedAttribute(cont7ext, "leaf42");
		assertEquals(MODULE_NS_ONE, leaf42ext.getNs());					// in submodule, so NS ONE

		final PrimaryTypeAttribute leaf51ext = findAddedAttribute(cont7ext, "leaf51");
		assertEquals(MODULE_NS_ONE, leaf51ext.getNs());					// in other module grouping, so NS ONE

		final PrimaryTypeAttribute leaf52ext = findAddedAttribute(cont7ext, "leaf52");
		assertEquals(MODULE_NS_ONE, leaf52ext.getNs());					// in other module grouping, so NS ONE

		final PrimaryTypeAttribute leaf61ext = findAddedAttribute(cont7ext, "leaf61");
		assertEquals(MODULE_NS_TWO, leaf61ext.getNs());					// augmented-in by other module, so NS TWO

		final PrimaryTypeAttribute leaf62ext = findAddedAttribute(cont7ext, "leaf62");
		assertEquals(MODULE_NS_TWO, leaf62ext.getNs());					// augmented-in by other module, so NS TWO

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont7spec = modelService.getTypedAccess().getEModelSpecification(cont7modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		assertEquals(MODULE_NS_ONE, findAttribute(cont7spec, "leaf11").getNamespace());
		assertEquals(MODULE_NS_ONE, findAttribute(cont7spec, "leaf12").getNamespace());
		assertEquals(MODULE_NS_ONE, findAttribute(cont7spec, "leaf21").getNamespace());
		assertEquals(MODULE_NS_ONE, findAttribute(cont7spec, "leaf31").getNamespace());
		assertEquals(MODULE_NS_ONE, findAttribute(cont7spec, "leaf41").getNamespace());
		assertEquals(MODULE_NS_ONE, findAttribute(cont7spec, "leaf42").getNamespace());
		assertEquals(MODULE_NS_ONE, findAttribute(cont7spec, "leaf51").getNamespace());
		assertEquals(MODULE_NS_ONE, findAttribute(cont7spec, "leaf52").getNamespace());
		assertEquals(MODULE_NS_TWO, findAttribute(cont7spec, "leaf61").getNamespace());
		assertEquals(MODULE_NS_TWO, findAttribute(cont7spec, "leaf62").getNamespace());
		assertEquals(MODULE_NS_ONE, findAttribute(cont7spec, "leaf71").getNamespace());
	}
}
