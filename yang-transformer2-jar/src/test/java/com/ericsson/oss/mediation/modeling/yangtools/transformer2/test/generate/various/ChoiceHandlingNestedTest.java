package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.ActiveChoiceCase;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.Case;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.Choice;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.RequiresActiveChoiceCaseType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceCaseAttributeType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceCasePrimaryTypeType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceCaseType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class ChoiceHandlingNestedTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/";

	private static final String MODULE_NS = "urn~test~choice-handling-nested";
	private static final String MODULE_XYZ_VERSION = "2022.3.23";

	@Test
	public void test___choice_handling_nested() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "choice-handling-nested")));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		// -----------------------------------------------------

		assertSize(1, cont1.getChoice());
		final ChoiceType choiceOuter = findChoice(cont1, "choice-outer");
		assertNotNull(choiceOuter);
		assertNull(findChoice(cont1, "choice-inner2"));
		assertNull(findChoice(cont1, "choice-inner3"));
		assertNull(findChoice(cont1, "choice-inner7"));

		assertSize(4, cont1.getPrimaryTypeAttribute());	// 3 leafs in the various cases, +1 auto-gen key
		final PrimaryTypeAttribute leaf41 = findAttribute(cont1, "leaf41");
		final PrimaryTypeAttribute leaf91 = findAttribute(cont1, "leaf91");
		final PrimaryTypeAttribute leaf101 = findAttribute(cont1, "leaf101");
		assertNotNull(leaf41);
		assertNotNull(leaf91);
		assertNotNull(leaf101);

		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf41 = leaf41.getRequires().getActiveChoiceCase();
		assertEquals("case-outer1", activeChoiceCaseLeaf41.getCase());
		assertEquals("choice-outer", activeChoiceCaseLeaf41.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf41.getPrimaryTypeUrn());

		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf91 = leaf91.getRequires().getActiveChoiceCase();
		assertEquals("case-outer6", activeChoiceCaseLeaf91.getCase());
		assertEquals("choice-outer", activeChoiceCaseLeaf91.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf91.getPrimaryTypeUrn());

		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf101 = leaf101.getRequires().getActiveChoiceCase();
		assertEquals("case-outer6", activeChoiceCaseLeaf101.getCase());
		assertEquals("choice-outer", activeChoiceCaseLeaf101.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf101.getPrimaryTypeUrn());

		assertSize(2, choiceOuter.getCase());
		final ChoiceCaseType caseOuter1 = findCase(choiceOuter, "case-outer1");
		final ChoiceCaseType caseOuter6 = findCase(choiceOuter, "case-outer6");
		assertNotNull(caseOuter1);
		assertNotNull(caseOuter6);
		assertNull(findCase(choiceOuter, "case-inner3"));
		assertNull(findCase(choiceOuter, "case-inner4"));

		assertSize(3, caseOuter1.getAttributeOrPrimaryType());
		final ChoiceCasePrimaryTypeType caseOuter1ContInner3entry = findChoiceCasePrimaryTypeType(caseOuter1, "cont-inner3");
		assertNotNull(caseOuter1ContInner3entry);
		final ChoiceCaseAttributeType caseOuter1Leaf41entry = findChoiceCaseAttributeType(caseOuter1, "leaf41");
		assertNotNull(caseOuter1Leaf41entry);
		final ChoiceCasePrimaryTypeType caseOuter1ContInner5entry = findChoiceCasePrimaryTypeType(caseOuter1, "cont-inner5");
		assertNotNull(caseOuter1ContInner5entry);

		assertSize(3, caseOuter6.getAttributeOrPrimaryType());
		final ChoiceCasePrimaryTypeType caseOuter6ContInner8entry = findChoiceCasePrimaryTypeType(caseOuter6, "cont-inner8");
		assertNotNull(caseOuter6ContInner8entry);
		final ChoiceCaseAttributeType caseOuter6Leaf91entry = findChoiceCaseAttributeType(caseOuter6, "leaf91");
		assertNotNull(caseOuter6Leaf91entry);
		final ChoiceCaseAttributeType caseOuter6Leaf101entry = findChoiceCaseAttributeType(caseOuter6, "leaf101");
		assertNotNull(caseOuter6Leaf101entry);

		// -----------------------------------------------------

		final ModelInfo contInner3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont-inner3", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition contInner3 = load(contInner3modelInfo);

		final RequiresActiveChoiceCaseType activeChoiceCaseContInner3 = contInner3.getRequires().getActiveChoiceCase();
		assertEquals("case-outer1", activeChoiceCaseContInner3.getCase());
		assertEquals("choice-outer", activeChoiceCaseContInner3.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseContInner3.getPrimaryTypeUrn());

		assertSize(3, contInner3.getPrimaryTypeAttribute());	// 2 leafs in the choice, +1 auto-gen key
		final PrimaryTypeAttribute leaf31 = findAttribute(contInner3, "leaf31");
		final PrimaryTypeAttribute leaf32 = findAttribute(contInner3, "leaf32");
		assertNotNull(leaf31);
		assertNotNull(leaf32);

		assertSize(1, contInner3.getChoice());
		final ChoiceType choiceInner3 = findChoice(contInner3, "choice-inner3");
		assertNotNull(choiceInner3);

		assertSize(2, choiceInner3.getCase());
		assertNotNull(findCase(choiceInner3, "leaf31"));
		assertNotNull(findCase(choiceInner3, "leaf32"));

		// -----------------------------------------------------

		final ModelInfo contInner5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont-inner5", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition contInner5 = load(contInner5modelInfo);

		final RequiresActiveChoiceCaseType activeChoiceCaseContInner5 = contInner5.getRequires().getActiveChoiceCase();
		assertEquals("case-outer1", activeChoiceCaseContInner5.getCase());
		assertEquals("choice-outer", activeChoiceCaseContInner5.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseContInner5.getPrimaryTypeUrn());

		// -----------------------------------------------------

		final ModelInfo contInner8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont-inner8", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition contInner8 = load(contInner8modelInfo);

		final RequiresActiveChoiceCaseType activeChoiceCaseContInner8 = contInner8.getRequires().getActiveChoiceCase();
		assertEquals("case-outer6", activeChoiceCaseContInner8.getCase());
		assertEquals("choice-outer", activeChoiceCaseContInner8.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseContInner8.getPrimaryTypeUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final Choice choiceOuterSpec = findChoice(cont1spec.getChoices(), "choice-outer");
		assertNotNull(choiceOuterSpec);
		assertSize(2, choiceOuterSpec.getCases());

		final Case caseOuter1spec = findCase(choiceOuterSpec.getCases(), "case-outer1");
		assertNotNull(findCasePrimaryTypeSpec(caseOuter1spec.getPrimaryTypeSpecifications(), "cont-inner3"));
		assertNotNull(findCaseAttributeSpec(caseOuter1spec.getAttributes(), "leaf41"));
		assertNotNull(findCasePrimaryTypeSpec(caseOuter1spec.getPrimaryTypeSpecifications(), "cont-inner5"));

		final Case caseOuter6spec = findCase(choiceOuterSpec.getCases(), "case-outer6");
		assertNotNull(findCasePrimaryTypeSpec(caseOuter6spec.getPrimaryTypeSpecifications(), "cont-inner8"));
		assertNotNull(findCaseAttributeSpec(caseOuter6spec.getAttributes(), "leaf91"));
		assertNotNull(findCaseAttributeSpec(caseOuter6spec.getAttributes(), "leaf101"));

		final PrimaryTypeAttributeSpecification attr41 = findAttribute(cont1spec, "leaf41");
		final ActiveChoiceCase requiresAttr41 = findRequires(attr41.getRequires(), ActiveChoiceCase.class);
		assertEquals("case-outer1", requiresAttr41.getCaseName());
		assertEquals("choice-outer", requiresAttr41.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr41.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr91 = findAttribute(cont1spec, "leaf91");
		final ActiveChoiceCase requiresAttr91 = findRequires(attr91.getRequires(), ActiveChoiceCase.class);
		assertEquals("case-outer6", requiresAttr91.getCaseName());
		assertEquals("choice-outer", requiresAttr91.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr91.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr101 = findAttribute(cont1spec, "leaf101");
		final ActiveChoiceCase requiresAttr101 = findRequires(attr101.getRequires(), ActiveChoiceCase.class);
		assertEquals("case-outer6", requiresAttr101.getCaseName());
		assertEquals("choice-outer", requiresAttr101.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr101.getOwningPrimaryTypeUrn());

		// -----------------------------------------------------

		final HierarchicalPrimaryTypeSpecification contInner3spec = modelService.getTypedAccess().getEModelSpecification(contInner3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final ActiveChoiceCase requiresContInner3spec = findRequires(contInner3spec.getRequires(), ActiveChoiceCase.class);
		assertEquals("case-outer1", requiresContInner3spec.getCaseName());
		assertEquals("choice-outer", requiresContInner3spec.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresContInner3spec.getOwningPrimaryTypeUrn());

		final HierarchicalPrimaryTypeSpecification contInner5spec = modelService.getTypedAccess().getEModelSpecification(contInner5modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final ActiveChoiceCase requiresContInner5spec = findRequires(contInner5spec.getRequires(), ActiveChoiceCase.class);
		assertEquals("case-outer1", requiresContInner5spec.getCaseName());
		assertEquals("choice-outer", requiresContInner5spec.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresContInner5spec.getOwningPrimaryTypeUrn());

		final HierarchicalPrimaryTypeSpecification contInner8spec = modelService.getTypedAccess().getEModelSpecification(contInner8modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final ActiveChoiceCase requiresContInner8spec = findRequires(contInner8spec.getRequires(), ActiveChoiceCase.class);
		assertEquals("case-outer6", requiresContInner8spec.getCaseName());
		assertEquals("choice-outer", requiresContInner8spec.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresContInner8spec.getOwningPrimaryTypeUrn());
	}
}
