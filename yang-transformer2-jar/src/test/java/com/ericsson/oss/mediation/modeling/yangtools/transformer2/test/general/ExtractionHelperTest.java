package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YContainer;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YLeaf;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YLeafList;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YModule;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YRpc;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;

public class ExtractionHelperTest  extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/";

	@Test
	public void test___extraction_helper() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N668_NOT_SUPPORTED_AT_TOP_LEVEL.toString());

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "extraction-helper"));
		context.setExplicitContainmentParent(OSS_TOP_ME_CONTEXT_300);
		context.setGenerateRpcs(true);
		YangTransformer2.transform(context);

		// ------------------------------------------------------

		final YModule module = context.getBaseSchema().getModuleRegistry().getAllYangModels().get(0).getYangModelRoot().getModule();
		assertEquals("extraction-helper", module.getModuleName());

		final YLeaf leaf01 = module.getLeafs().get(0);
		assertNull(ExtractionHelper.getOwningMoc(leaf01));
		assertEquals("MeContext__leaf01", ExtractionHelper.getPathFromMocToDataNode(context, leaf01));

		final YLeafList leaflist02 = module.getLeafLists().get(0);
		assertNull(ExtractionHelper.getOwningMoc(leaflist02));
		assertEquals(OSS_TOP_ME_CONTEXT_300, ExtractionHelper.getModelInfoForOwningMoc(context, leaflist02));

		final YRpc rpc3 = module.getRpcs().get(0);
		assertNull(ExtractionHelper.getOwningMoc(rpc3));
		assertEquals(OSS_TOP_ME_CONTEXT_300, ExtractionHelper.getModelInfoForOwningMoc(context, rpc3));
		assertEquals("MeContext__rpc3", ExtractionHelper.getPathFromMocToDataNode(context, rpc3));

		final YLeaf leaf03 = rpc3.getInput().getLeafs().get(0);
		assertNull(ExtractionHelper.getOwningMoc(leaf03));
		assertEquals(OSS_TOP_ME_CONTEXT_300, ExtractionHelper.getModelInfoForOwningMoc(context, leaf03));
		assertEquals("MeContext__rpc3__in__leaf03", ExtractionHelper.getPathFromMocToDataNode(context, leaf03));

		final YContainer cont1 = module.getContainers().get(0);
		assertNull(ExtractionHelper.getOwningMoc(cont1));

		final YLeaf leaf11 = cont1.getLeafs().get(0);
		assertEquals(cont1, ExtractionHelper.getOwningMoc(leaf11));
		assertEquals("cont1__leaf11", ExtractionHelper.getPathFromMocToDataNode(context, leaf11));

		final YLeaf leaf12 = cont1.getChoices().get(0).getCases().get(0).getLeafs().get(0);
		final YLeaf leaf13 = cont1.getChoices().get(0).getCases().get(1).getLeafs().get(0);

		assertEquals(cont1, ExtractionHelper.getOwningMoc(leaf12));
		assertEquals(cont1, ExtractionHelper.getOwningMoc(leaf13));
		assertEquals("cont1__leaf12", ExtractionHelper.getPathFromMocToDataNode(context, leaf12));
		assertEquals("cont1__leaf13", ExtractionHelper.getPathFromMocToDataNode(context, leaf13));

		final YLeaf leaf31 = cont1.getActions().get(0).getInput().getLeafs().get(0);
		final YLeaf leaf32 = cont1.getActions().get(0).getOutput().getLeafs().get(0);

		assertEquals(cont1, ExtractionHelper.getOwningMoc(leaf31));
		assertEquals(cont1, ExtractionHelper.getOwningMoc(leaf32));
		assertEquals("cont1__action3__in__leaf31", ExtractionHelper.getPathFromMocToDataNode(context, leaf31));
		assertEquals("cont1__action3__out__leaf32", ExtractionHelper.getPathFromMocToDataNode(context, leaf32));

		final YContainer cont5 = cont1.getContainers().get(0);
		assertEquals(cont1, ExtractionHelper.getOwningMoc(cont5));

		final YLeaf leaf51 = cont5.getLeafs().get(0);
		assertEquals(cont5, ExtractionHelper.getOwningMoc(leaf51));
		assertEquals("cont5__leaf51", ExtractionHelper.getPathFromMocToDataNode(context, leaf51));

		final YContainer struct7 = cont5.getActions().get(0).getInput().getContainers().get(0);
		assertEquals(cont5, ExtractionHelper.getOwningMoc(struct7));
		assertEquals("cont5__action6__in__struct7", ExtractionHelper.getPathFromMocToDataNode(context, struct7));

		final YLeaf leaf71 = struct7.getLeafs().get(0);
		assertEquals(cont5, ExtractionHelper.getOwningMoc(leaf71));
		assertEquals("cont5__action6__in__struct7__leaf71", ExtractionHelper.getPathFromMocToDataNode(context, leaf71));
	}
}
