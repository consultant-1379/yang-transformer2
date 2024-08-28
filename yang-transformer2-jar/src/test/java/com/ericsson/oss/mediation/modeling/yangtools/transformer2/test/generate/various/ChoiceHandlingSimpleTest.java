package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class ChoiceHandlingSimpleTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/";

	private static final String MODULE_NS = "urn~test~choice-handling";
	private static final String MODULE_XYZ_VERSION = "2021.11.7";

	@Test
	public void test___choice_handling_simple() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "choice-handling-simple")));
		context.setApply3gppHandling(true);
		context.setSuppressWrapGeneration(false);

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		assertSize(4, cont1.getPrimaryTypeAttribute());	// 3 leafs in the cases, +1 auto-gen key
		assertSize(1, cont1.getChoice());
		final ChoiceType choiceTypeCont1 = cont1.getChoice().get(0);
		assertFalse(choiceTypeCont1.isMandatory());
		assertSize(3, choiceTypeCont1.getCase());
		assertEquals("case12", choiceTypeCont1.getCase().get(0).getName());
		assertEquals("case15", choiceTypeCont1.getCase().get(1).getName());
		assertEquals("case17", choiceTypeCont1.getCase().get(2).getName());
		assertSize(2, choiceTypeCont1.getCase().get(0).getAttributeOrPrimaryType());
		assertSize(1, choiceTypeCont1.getCase().get(1).getAttributeOrPrimaryType());
		assertSize(1, choiceTypeCont1.getCase().get(2).getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, choiceTypeCont1.getCase().get(0).getAttributeOrPrimaryType().get(0));
		assertInstanceOf(ChoiceCaseAttributeType.class, choiceTypeCont1.getCase().get(0).getAttributeOrPrimaryType().get(1));
		assertInstanceOf(ChoiceCaseAttributeType.class, choiceTypeCont1.getCase().get(1).getAttributeOrPrimaryType().get(0));
		assertInstanceOf(ChoiceCasePrimaryTypeType.class, choiceTypeCont1.getCase().get(2).getAttributeOrPrimaryType().get(0));
		assertEquals("leaf13", ((ChoiceCaseAttributeType) choiceTypeCont1.getCase().get(0).getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf14", ((ChoiceCaseAttributeType) choiceTypeCont1.getCase().get(0).getAttributeOrPrimaryType().get(1)).getName());
		assertEquals("leaf16", ((ChoiceCaseAttributeType) choiceTypeCont1.getCase().get(1).getAttributeOrPrimaryType().get(0)).getName());
		assertEquals(cont2modelInfo.toImpliedUrn(), ((ChoiceCasePrimaryTypeType) choiceTypeCont1.getCase().get(2).getAttributeOrPrimaryType().get(0)).getPrimaryTypeUrn());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf13 = leaf13.getRequires().getActiveChoiceCase();
		assertEquals("case12", activeChoiceCaseLeaf13.getCase());
		assertEquals("choice11", activeChoiceCaseLeaf13.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf13.getPrimaryTypeUrn());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertFalse(leaf14.isMandatory());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf14 = leaf14.getRequires().getActiveChoiceCase();
		assertEquals("case12", activeChoiceCaseLeaf14.getCase());
		assertEquals("choice11", activeChoiceCaseLeaf14.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf14.getPrimaryTypeUrn());

		final PrimaryTypeAttribute leaf16 = findAttribute(cont1, "leaf16");
		assertFalse(leaf16.isMandatory());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf16 = leaf16.getRequires().getActiveChoiceCase();
		assertEquals("case15", activeChoiceCaseLeaf16.getCase());
		assertEquals("choice11", activeChoiceCaseLeaf16.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf16.getPrimaryTypeUrn());

		final RequiresActiveChoiceCaseType activeChoiceCaseCont2 = cont2.getRequires().getActiveChoiceCase();
		assertEquals("case17", activeChoiceCaseCont2.getCase());
		assertEquals("choice11", activeChoiceCaseCont2.getChoice());
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseCont2.getPrimaryTypeUrn());
		assertEmpty(cont2.getChoice());

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertTrue(leaf21.isMandatory());

		// -----------------------------------------------------

		final ModelInfo list3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "List3", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition list3 = load(list3modelInfo);

		assertSize(4, list3.getPrimaryTypeAttribute());
		assertSize(1, list3.getChoice());
		final ChoiceType choiceTypelist3 = list3.getChoice().get(0);
		assertTrue(choiceTypelist3.isMandatory());
		assertSize(2, choiceTypelist3.getCase());
		assertEquals("case32", choiceTypelist3.getCase().get(0).getName());
		assertEquals("case35", choiceTypelist3.getCase().get(1).getName());
		assertSize(2, choiceTypelist3.getCase().get(0).getAttributeOrPrimaryType());
		assertSize(1, choiceTypelist3.getCase().get(1).getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, choiceTypelist3.getCase().get(0).getAttributeOrPrimaryType().get(0));
		assertInstanceOf(ChoiceCaseAttributeType.class, choiceTypelist3.getCase().get(0).getAttributeOrPrimaryType().get(1));
		assertInstanceOf(ChoiceCaseAttributeType.class, choiceTypelist3.getCase().get(1).getAttributeOrPrimaryType().get(0));
		assertEquals("leaf33", ((ChoiceCaseAttributeType) choiceTypelist3.getCase().get(0).getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf34", ((ChoiceCaseAttributeType) choiceTypelist3.getCase().get(0).getAttributeOrPrimaryType().get(1)).getName());
		assertEquals("leaf36", ((ChoiceCaseAttributeType) choiceTypelist3.getCase().get(1).getAttributeOrPrimaryType().get(0)).getName());

		final PrimaryTypeAttribute leaf33 = findAttribute(list3, "leaf33");
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf33 = leaf33.getRequires().getActiveChoiceCase();
		assertEquals("case32", activeChoiceCaseLeaf33.getCase());
		assertEquals("choice31", activeChoiceCaseLeaf33.getChoice());
		assertEquals(list3modelInfo.toImpliedUrn(), activeChoiceCaseLeaf33.getPrimaryTypeUrn());

		final PrimaryTypeAttribute leaf34 = findAttribute(list3, "leaf34");
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf34 = leaf34.getRequires().getActiveChoiceCase();
		assertEquals("case32", activeChoiceCaseLeaf34.getCase());
		assertEquals("choice31", activeChoiceCaseLeaf34.getChoice());
		assertEquals(list3modelInfo.toImpliedUrn(), activeChoiceCaseLeaf34.getPrimaryTypeUrn());

		final PrimaryTypeAttribute leaf36 = findAttribute(list3, "leaf36");
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf36 = leaf36.getRequires().getActiveChoiceCase();
		assertEquals("case35", activeChoiceCaseLeaf36.getCase());
		assertEquals("choice31", activeChoiceCaseLeaf36.getChoice());
		assertEquals(list3modelInfo.toImpliedUrn(), activeChoiceCaseLeaf36.getPrimaryTypeUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertTrue(cont1spec != null);

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertTrue(cont2spec != null);

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(cont1spec, "leaf13");
		assertFalse(attr13.isMandatory());
		final PrimaryTypeAttributeSpecification attr14 = findAttribute(cont1spec, "leaf14");
		assertFalse(attr14.isMandatory());
		final PrimaryTypeAttributeSpecification attr16 = findAttribute(cont1spec, "leaf16");
		assertFalse(attr16.isMandatory());

		final Choice choice11 = findChoice(cont1spec.getChoices(), "choice11");
		assertFalse(choice11.isMandatory());
		assertSize(3, choice11.getCases());
		final Case case12 = findCase(choice11.getCases(), "case12");
		assertSize(2, case12.getAttributes());
		assertEmpty(case12.getPrimaryTypeSpecifications());
		assertTrue(case12.getAttributes().contains(attr13));
		assertTrue(case12.getAttributes().contains(attr14));
		final Case case15 = findCase(choice11.getCases(), "case15");
		assertSize(1, case15.getAttributes());
		assertEmpty(case15.getPrimaryTypeSpecifications());
		assertTrue(case15.getAttributes().contains(attr16));
		final Case case17 = findCase(choice11.getCases(), "case17");
		assertEmpty(case17.getAttributes());
		assertSize(1, case17.getPrimaryTypeSpecifications());
		assertEquals(cont2modelInfo, case17.getPrimaryTypeSpecifications().iterator().next().getModelInfo());	// hm, the existing HPTS is not returned, but another one ... bug in MS?

		final ActiveChoiceCase requiresAttr13 = findRequires(attr13.getRequires(), ActiveChoiceCase.class);
		assertEquals("case12", requiresAttr13.getCaseName());
		assertEquals("choice11", requiresAttr13.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr13.getOwningPrimaryTypeUrn());

		final ActiveChoiceCase requiresAttr14 = findRequires(attr14.getRequires(), ActiveChoiceCase.class);
		assertEquals("case12", requiresAttr14.getCaseName());
		assertEquals("choice11", requiresAttr14.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr14.getOwningPrimaryTypeUrn());

		final ActiveChoiceCase requiresAttr16 = findRequires(attr16.getRequires(), ActiveChoiceCase.class);
		assertEquals("case15", requiresAttr16.getCaseName());
		assertEquals("choice11", requiresAttr16.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr16.getOwningPrimaryTypeUrn());

		final ActiveChoiceCase requiresCont2Spec = findRequires(cont2spec.getRequires(), ActiveChoiceCase.class);
		assertEquals("case17", requiresCont2Spec.getCaseName());
		assertEquals("choice11", requiresCont2Spec.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresCont2Spec.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr21 = findAttribute(cont2spec, "leaf21");
		assertTrue(attr21.isMandatory());
	}
}
