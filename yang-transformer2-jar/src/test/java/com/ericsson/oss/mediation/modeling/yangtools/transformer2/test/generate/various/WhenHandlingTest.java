package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.PathExpressionCondition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class WhenHandlingTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/";

	private static final String MODULE_NS = "urn_test_when-handling";
	private static final String MODULE_XYZ_VERSION = "2021.12.5";

	@Test
	public void test___when_handling() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "when-handling")));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertTrue(leaf11.isMandatory());
		assertNull(leaf11.getRequires());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertFalse(leaf12.isMandatory());
		assertEquals("../leaf11 = 'Hello'", leaf12.getRequires().getPathExpressionCondition().getCondition());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertFalse(leaf13.isMandatory());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		final ModelInfo cont2extModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont2");
		final PrimaryTypeExtensionDefinition cont2ext = load(cont2extModelInfo);

		final PrimaryTypeAttribute leaf21ext = findAddedAttribute(cont2ext, "leaf21");
		assertFalse(leaf21ext.isMandatory());
		assertEquals("/wh:cont1/wh:leaf11 = 'World'", leaf21ext.getRequires().getPathExpressionCondition().getCondition());

		final PrimaryTypeAttribute leaf22ext = findAddedAttribute(cont2ext, "leaf22");
		assertFalse(leaf22ext.isMandatory());
		assertEquals("(../leaf21 = 'Hello') and (/wh:cont1/wh:leaf11 = 'World')", leaf22ext.getRequires().getPathExpressionCondition().getCondition());

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont3", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);

		final PrimaryTypeAttribute leaf31 = findAttribute(cont3, "leaf31");
		assertFalse(leaf31.isMandatory());
		assertEquals("../cont1/leaf11 = 'Hi'", leaf31.getRequires().getPathExpressionCondition().getCondition());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertTrue(attr11.isMandatory());
		final PathExpressionCondition pathCondition11 = findRequires(attr11.getRequires(), PathExpressionCondition.class);
		assertNull(pathCondition11);

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(cont1spec, "leaf12");
		assertFalse(attr12.isMandatory());
		final PathExpressionCondition pathCondition12 = findRequires(attr12.getRequires(), PathExpressionCondition.class);
		assertEquals("../leaf11 = 'Hello'", pathCondition12.getCondition());

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertFalse(attr13.isMandatory());

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertNotNull(cont2spec);

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertFalse(attr21.isMandatory());
		final PathExpressionCondition pathCondition21 = findRequires(attr21.getRequires(), PathExpressionCondition.class);
		assertEquals("/wh:cont1/wh:leaf11 = 'World'", pathCondition21.getCondition());

		final PrimaryTypeAttributeSpecification attr22 = findAttribute(cont2spec, "leaf22");
		assertFalse(attr22.isMandatory());
		final PathExpressionCondition pathCondition22 = findRequires(attr22.getRequires(), PathExpressionCondition.class);
		assertEquals("(../leaf21 = 'Hello') and (/wh:cont1/wh:leaf11 = 'World')", pathCondition22.getCondition());

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertNotNull(cont3spec);

		final PrimaryTypeAttributeSpecification attr31 = findAttribute(cont3spec, "leaf31");
		assertFalse(attr31.isMandatory());
		final PathExpressionCondition pathCondition31 = findRequires(attr31.getRequires(), PathExpressionCondition.class);
		assertEquals("../cont1/leaf11 = 'Hi'", pathCondition31.getCondition());
	}
}
