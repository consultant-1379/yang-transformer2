package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
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
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;

public class ChoiceHandlingComplexTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/";

	private static final String MODULE_NS = "urn~test~choice-handling";
	private static final String MODULE_XYZ_VERSION = "2021.11.7";
	private static final String MODULE_NS_DEV = "urn::test::choice-handling-dev";

	@Test
	public void test___choice_handling_complex() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P152_AUGMENT_TARGET_NODE_IN_SAME_MODULE.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "choice-handling-complex")));
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont1", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final ModelInfo cont1extModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont1");
		final PrimaryTypeExtensionDefinition cont1ext = load(cont1extModelInfo);

		// -----------------------------------------------------

		assertNull(findChoice(cont1, "choice10"));

		// -----------------------------------------------------

		assertNull(findChoice(cont1, "choice11"));

		final ChoiceType choice11ext = findAddedOrReplacedChoice(cont1ext, "choice11");
		assertSize(1, choice11ext.getCase());
		assertEquals("case119", choice11ext.getCase().get(0).getName());
		assertSize(1, choice11ext.getCase().get(0).getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, choice11ext.getCase().get(0).getAttributeOrPrimaryType().get(0));
		assertEquals("leaf119", ((ChoiceCaseAttributeType) choice11ext.getCase().get(0).getAttributeOrPrimaryType().get(0)).getName());

		assertNull(findAttribute(cont1, "leaf119"));

		final PrimaryTypeAttribute leaf119ext = findAddedAttribute(cont1ext, "leaf119");
		assertEquals(MODULE_NS_DEV, leaf119ext.getNs());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf119ext = leaf119ext.getRequires().getActiveChoiceCase();
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf119ext.getPrimaryTypeUrn());
		assertEquals("choice11", activeChoiceCaseLeaf119ext.getChoice());
		assertEquals("case119", activeChoiceCaseLeaf119ext.getCase());

		// -----------------------------------------------------

		final ChoiceType choice12 = findChoice(cont1, "choice12");
		assertSize(1, choice12.getCase());
		assertEquals("case121", choice12.getCase().get(0).getName());
		assertSize(2, choice12.getCase().get(0).getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, choice12.getCase().get(0).getAttributeOrPrimaryType().get(0));
		assertInstanceOf(ChoiceCaseAttributeType.class, choice12.getCase().get(0).getAttributeOrPrimaryType().get(1));
		assertEquals("leaf121", ((ChoiceCaseAttributeType) choice12.getCase().get(0).getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf122", ((ChoiceCaseAttributeType) choice12.getCase().get(0).getAttributeOrPrimaryType().get(1)).getName());

		final ChoiceType choice12ext = findAddedOrReplacedChoice(cont1ext, "choice12");
		assertSize(2, choice12ext.getCase());
		assertSize(2, findCase(choice12ext, "case121").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice12ext, "case121").getAttributeOrPrimaryType().get(0));
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice12ext, "case121").getAttributeOrPrimaryType().get(1));
		assertEquals("leaf121", ((ChoiceCaseAttributeType) findCase(choice12ext, "case121").getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf122", ((ChoiceCaseAttributeType) findCase(choice12ext, "case121").getAttributeOrPrimaryType().get(1)).getName());
		assertSize(1, findCase(choice12ext, "case129").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice12ext, "case129").getAttributeOrPrimaryType().get(0));
		assertEquals("leaf129", ((ChoiceCaseAttributeType) findCase(choice12ext, "case129").getAttributeOrPrimaryType().get(0)).getName());

		final PrimaryTypeAttribute leaf121 = findAttribute(cont1, "leaf121");
		assertEquals(MODULE_NS, leaf121.getNs());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf121 = leaf121.getRequires().getActiveChoiceCase();
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf121.getPrimaryTypeUrn());
		assertEquals("choice12", activeChoiceCaseLeaf121.getChoice());
		assertEquals("case121", activeChoiceCaseLeaf121.getCase());

		final PrimaryTypeAttribute leaf122 = findAttribute(cont1, "leaf122");
		assertEquals(MODULE_NS, leaf122.getNs());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf122 = leaf122.getRequires().getActiveChoiceCase();
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf122.getPrimaryTypeUrn());
		assertEquals("choice12", activeChoiceCaseLeaf122.getChoice());
		assertEquals("case121", activeChoiceCaseLeaf122.getCase());

		assertNull(findAttribute(cont1, "leaf129"));

		final PrimaryTypeAttribute leaf129ext = findAddedAttribute(cont1ext, "leaf129");
		assertEquals(MODULE_NS_DEV, leaf129ext.getNs());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf129ext = leaf129ext.getRequires().getActiveChoiceCase();
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf129ext.getPrimaryTypeUrn());
		assertEquals("choice12", activeChoiceCaseLeaf129ext.getChoice());
		assertEquals("case129", activeChoiceCaseLeaf129ext.getCase());

		// -----------------------------------------------------

		final ChoiceType choice13 = findChoice(cont1, "choice13");
		assertSize(2, choice13.getCase());
		assertEquals("case131", choice13.getCase().get(0).getName());
		assertSize(1, choice13.getCase().get(0).getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, choice13.getCase().get(0).getAttributeOrPrimaryType().get(0));
		assertEquals("leaf131", ((ChoiceCaseAttributeType) choice13.getCase().get(0).getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("case132", choice13.getCase().get(1).getName());
		assertSize(1, choice13.getCase().get(1).getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, choice13.getCase().get(1).getAttributeOrPrimaryType().get(0));
		assertEquals("leaf132", ((ChoiceCaseAttributeType) choice13.getCase().get(1).getAttributeOrPrimaryType().get(0)).getName());

		final ChoiceType choice13ext = findAddedOrReplacedChoice(cont1ext, "choice13");
		assertSize(2, choice13ext.getCase());
		assertSize(1, findCase(choice13ext, "case131").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice13ext, "case131").getAttributeOrPrimaryType().get(0));
		assertEquals("leaf131", ((ChoiceCaseAttributeType) findCase(choice13ext, "case131").getAttributeOrPrimaryType().get(0)).getName());
		assertSize(2, findCase(choice13ext, "case132").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice13ext, "case132").getAttributeOrPrimaryType().get(0));
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice13ext, "case132").getAttributeOrPrimaryType().get(1));
		assertEquals("leaf132", ((ChoiceCaseAttributeType) findCase(choice13ext, "case132").getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf139", ((ChoiceCaseAttributeType) findCase(choice13ext, "case132").getAttributeOrPrimaryType().get(1)).getName());

		final PrimaryTypeAttribute leaf131 = findAttribute(cont1, "leaf131");
		assertEquals(MODULE_NS, leaf131.getNs());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf131 = leaf131.getRequires().getActiveChoiceCase();
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf131.getPrimaryTypeUrn());
		assertEquals("choice13", activeChoiceCaseLeaf131.getChoice());
		assertEquals("case131", activeChoiceCaseLeaf131.getCase());

		final PrimaryTypeAttribute leaf132 = findAttribute(cont1, "leaf132");
		assertEquals(MODULE_NS, leaf132.getNs());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf132 = leaf132.getRequires().getActiveChoiceCase();
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf132.getPrimaryTypeUrn());
		assertEquals("choice13", activeChoiceCaseLeaf132.getChoice());
		assertEquals("case132", activeChoiceCaseLeaf132.getCase());

		final PrimaryTypeAttribute leaf139ext = findAddedAttribute(cont1ext, "leaf139");
		assertEquals(MODULE_NS_DEV, leaf139ext.getNs());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf139ext = leaf139ext.getRequires().getActiveChoiceCase();
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf139ext.getPrimaryTypeUrn());
		assertEquals("choice13", activeChoiceCaseLeaf139ext.getChoice());
		assertEquals("case132", activeChoiceCaseLeaf139ext.getCase());

		// -----------------------------------------------------

		assertNull(findChoice(cont1, "choice19"));

		final ChoiceType choice19ext = findAddedOrReplacedChoice(cont1ext, "choice19");
		assertSize(1, choice19ext.getCase());
		assertSize(2, findCase(choice19ext, "case198").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice19ext, "case198").getAttributeOrPrimaryType().get(0));
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice19ext, "case198").getAttributeOrPrimaryType().get(1));
		assertEquals("leaf198", ((ChoiceCaseAttributeType) findCase(choice19ext, "case198").getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf199", ((ChoiceCaseAttributeType) findCase(choice19ext, "case198").getAttributeOrPrimaryType().get(1)).getName());

		assertNull(findAttribute(cont1, "leaf198"));
		assertNull(findAttribute(cont1, "leaf199"));

		final PrimaryTypeAttribute leaf198ext = findAddedAttribute(cont1ext, "leaf198");
		assertEquals(MODULE_NS_DEV, leaf198ext.getNs());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf198ext = leaf198ext.getRequires().getActiveChoiceCase();
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf198ext.getPrimaryTypeUrn());
		assertEquals("choice19", activeChoiceCaseLeaf198ext.getChoice());
		assertEquals("case198", activeChoiceCaseLeaf198ext.getCase());

		final PrimaryTypeAttribute leaf199ext = findAddedAttribute(cont1ext, "leaf199");
		assertEquals(MODULE_NS_DEV, leaf199ext.getNs());
		final RequiresActiveChoiceCaseType activeChoiceCaseLeaf199ext = leaf199ext.getRequires().getActiveChoiceCase();
		assertEquals(cont1modelInfo.toImpliedUrn(), activeChoiceCaseLeaf199ext.getPrimaryTypeUrn());
		assertEquals("choice19", activeChoiceCaseLeaf199ext.getChoice());
		assertEquals("case198", activeChoiceCaseLeaf199ext.getCase());

		// -----------------------------------------------------
		// -----------------------------------------------------

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont2", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);

		final ModelInfo cont2extModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont2");
		final PrimaryTypeExtensionDefinition cont2ext = load(cont2extModelInfo);

		// -----------------------------------------------------

		final ChoiceType choice21 = findChoice(cont2, "choice21");
		assertSize(1, choice21.getCase());
		assertSize(1, findCase(choice21, "case211").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice21, "case211").getAttributeOrPrimaryType().get(0));
		assertEquals("leaf211", ((ChoiceCaseAttributeType) findCase(choice21, "case211").getAttributeOrPrimaryType().get(0)).getName());

		assertNull(findAddedOrReplacedChoice(cont2ext, "choice21"));

		// -----------------------------------------------------

		final ChoiceType choice22 = findChoice(cont2, "choice22");
		assertSize(1, choice22.getCase());
		assertSize(1, findCase(choice22, "case221").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice22, "case221").getAttributeOrPrimaryType().get(0));
		assertEquals("leaf221", ((ChoiceCaseAttributeType) findCase(choice22, "case221").getAttributeOrPrimaryType().get(0)).getName());

		assertChoiceRemoved(cont2ext, "choice22");
		assertNull(findAddedOrReplacedChoice(cont2ext, "choice22"));

		// -----------------------------------------------------

		final ChoiceType choice23 = findChoice(cont2, "choice23");
		assertSize(1, choice23.getCase());
		assertSize(1, findCase(choice23, "case231").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice23, "case231").getAttributeOrPrimaryType().get(0));
		assertEquals("leaf231", ((ChoiceCaseAttributeType) findCase(choice23, "case231").getAttributeOrPrimaryType().get(0)).getName());

		assertChoiceRemoved(cont2ext, "choice23");
		assertNull(findAddedOrReplacedChoice(cont2ext, "choice23"));

		// -----------------------------------------------------

		final ChoiceType choice24 = findChoice(cont2, "choice24");
		assertSize(1, choice24.getCase());
		assertSize(1, findCase(choice24, "case241").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice24, "case241").getAttributeOrPrimaryType().get(0));
		assertEquals("leaf241", ((ChoiceCaseAttributeType) findCase(choice24, "case241").getAttributeOrPrimaryType().get(0)).getName());

		assertChoiceRemoved(cont2ext, "choice24");
		assertNull(findAddedOrReplacedChoice(cont2ext, "choice24"));

		// -----------------------------------------------------

		final ChoiceType choice25 = findChoice(cont2, "choice25");
		assertSize(1, choice25.getCase());
		assertSize(1, findCase(choice25, "case251").getAttributeOrPrimaryType());
		assertInstanceOf(ChoiceCaseAttributeType.class, findCase(choice25, "case251").getAttributeOrPrimaryType().get(0));
		assertEquals("leaf251", ((ChoiceCaseAttributeType) findCase(choice25, "case251").getAttributeOrPrimaryType().get(0)).getName());

		final ChoiceType choice25ext = findAddedOrReplacedChoice(cont2ext, "choice25");
		assertSize(1, choice25ext.getCase());
		assertSize(1, findCase(choice25ext, "case252").getAttributeOrPrimaryType());
		assertEquals("leaf252", ((ChoiceCaseAttributeType) findCase(choice25ext, "case252").getAttributeOrPrimaryType().get(0)).getName());

		// -----------------------------------------------------
		// -----------------------------------------------------

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont3", MODULE_XYZ_VERSION);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);

		final ModelInfo cont3extModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "cont3");
		final PrimaryTypeExtensionDefinition cont3ext = load(cont3extModelInfo);

		final ModelInfo cont312modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont312", MODULE_XYZ_VERSION);
		final ModelInfo cont313modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont313", MODULE_XYZ_VERSION);
		final ModelInfo cont322modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont322", MODULE_XYZ_VERSION);
		final ModelInfo cont323modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont323", MODULE_XYZ_VERSION);
		final ModelInfo cont332modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont332", MODULE_XYZ_VERSION);
		final ModelInfo cont333modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "cont333", MODULE_XYZ_VERSION);

		// -----------------------------------------------------

		final ChoiceType choice31 = findChoice(cont3, "choice31");
		assertSize(2, choice31.getCase());
		assertSize(2, findCase(choice31, "case311").getAttributeOrPrimaryType());
		assertEquals(cont312modelInfo.toImpliedUrn(), ((ChoiceCasePrimaryTypeType) findCase(choice31, "case311").getAttributeOrPrimaryType().get(0)).getPrimaryTypeUrn());
		assertEquals(cont313modelInfo.toImpliedUrn(), ((ChoiceCasePrimaryTypeType) findCase(choice31, "case311").getAttributeOrPrimaryType().get(1)).getPrimaryTypeUrn());
		assertSize(2, findCase(choice31, "case314").getAttributeOrPrimaryType());
		assertEquals("leaf315", ((ChoiceCaseAttributeType) findCase(choice31, "case314").getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf316", ((ChoiceCaseAttributeType) findCase(choice31, "case314").getAttributeOrPrimaryType().get(1)).getName());

		final ChoiceType choice31ext = findAddedOrReplacedChoice(cont3ext, "choice31");
		assertSize(1, choice31ext.getCase());
		assertSize(2, findCase(choice31ext, "case314").getAttributeOrPrimaryType());
		assertEquals("leaf315", ((ChoiceCaseAttributeType) findCase(choice31ext, "case314").getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf316", ((ChoiceCaseAttributeType) findCase(choice31ext, "case314").getAttributeOrPrimaryType().get(1)).getName());

		// -----------------------------------------------------

		final ChoiceType choice32 = findChoice(cont3, "choice32");
		assertSize(2, choice32.getCase());
		assertSize(2, findCase(choice32, "case321").getAttributeOrPrimaryType());
		assertEquals(cont322modelInfo.toImpliedUrn(), ((ChoiceCasePrimaryTypeType) findCase(choice32, "case321").getAttributeOrPrimaryType().get(0)).getPrimaryTypeUrn());
		assertEquals(cont323modelInfo.toImpliedUrn(), ((ChoiceCasePrimaryTypeType) findCase(choice32, "case321").getAttributeOrPrimaryType().get(1)).getPrimaryTypeUrn());
		assertSize(2, findCase(choice32, "case324").getAttributeOrPrimaryType());
		assertEquals("leaf325", ((ChoiceCaseAttributeType) findCase(choice32, "case324").getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf326", ((ChoiceCaseAttributeType) findCase(choice32, "case324").getAttributeOrPrimaryType().get(1)).getName());

		assertChoiceRemoved(cont3ext, "choice32");
		assertNull(findAddedOrReplacedChoice(cont3ext, "choice32"));

		// -----------------------------------------------------

		final ChoiceType choice33 = findChoice(cont3, "choice33");
		assertSize(2, choice33.getCase());
		assertSize(2, findCase(choice33, "case331").getAttributeOrPrimaryType());
		assertEquals(cont332modelInfo.toImpliedUrn(), ((ChoiceCasePrimaryTypeType) findCase(choice33, "case331").getAttributeOrPrimaryType().get(0)).getPrimaryTypeUrn());
		assertEquals(cont333modelInfo.toImpliedUrn(), ((ChoiceCasePrimaryTypeType) findCase(choice33, "case331").getAttributeOrPrimaryType().get(1)).getPrimaryTypeUrn());
		assertSize(2, findCase(choice33, "case334").getAttributeOrPrimaryType());
		assertEquals("leaf335", ((ChoiceCaseAttributeType) findCase(choice33, "case334").getAttributeOrPrimaryType().get(0)).getName());
		assertEquals("leaf336", ((ChoiceCaseAttributeType) findCase(choice33, "case334").getAttributeOrPrimaryType().get(1)).getName());

		final ChoiceType choice33ext = findAddedOrReplacedChoice(cont3ext, "choice33");
		assertSize(2, choice33ext.getCase());
		assertSize(1, findCase(choice33ext, "case331").getAttributeOrPrimaryType());
		assertEquals(cont333modelInfo.toImpliedUrn(), ((ChoiceCasePrimaryTypeType) findCase(choice33ext, "case331").getAttributeOrPrimaryType().get(0)).getPrimaryTypeUrn());
		assertSize(1, findCase(choice33ext, "case334").getAttributeOrPrimaryType());
		assertEquals("leaf336", ((ChoiceCaseAttributeType) findCase(choice33ext, "case334").getAttributeOrPrimaryType().get(0)).getName());

		final PrimaryTypeDefinition cont333 = load(cont333modelInfo);

		assertEquals(cont3modelInfo.toImpliedUrn(), cont333.getRequires().getActiveChoiceCase().getPrimaryTypeUrn());
		assertEquals("choice33", cont333.getRequires().getActiveChoiceCase().getChoice());
		assertEquals("case331", cont333.getRequires().getActiveChoiceCase().getCase());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final Choice choice10spec = findChoice(cont1spec.getChoices(), "choice10");
		assertNull(choice10spec);

		final Choice choice11spec = findChoice(cont1spec.getChoices(), "choice11");
		assertSize(1, choice11spec.getCases());
		final Case case119 = findCase(choice11spec.getCases(), "case119");
		assertSize(1, case119.getAttributes());
		assertEquals("leaf119", case119.getAttributes().iterator().next().getName());

		final Choice choice12spec = findChoice(cont1spec.getChoices(), "choice12");
		assertSize(2, choice12spec.getCases());
		final Case case121 = findCase(choice12spec.getCases(), "case121");
		assertSize(2, case121.getAttributes());
		final Case case129 = findCase(choice12spec.getCases(), "case129");
		assertSize(1, case129.getAttributes());

		final Choice choice13spec = findChoice(cont1spec.getChoices(), "choice13");
		assertSize(2, choice13spec.getCases());
		final Case case131 = findCase(choice13spec.getCases(), "case131");
		assertSize(1, case131.getAttributes());
		final Case case132 = findCase(choice13spec.getCases(), "case132");
		assertSize(2, case132.getAttributes());

		final Choice choice19spec = findChoice(cont1spec.getChoices(), "choice19");
		assertSize(1, choice19spec.getCases());
		final Case case198 = findCase(choice19spec.getCases(), "case198");
		assertSize(2, case198.getAttributes());

		final HierarchicalPrimaryTypeSpecification cont2spec = modelService.getTypedAccess().getEModelSpecification(cont2modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final Choice choice21spec = findChoice(cont2spec.getChoices(), "choice21");
		assertSize(1, choice21spec.getCases());
		final Case case211 = findCase(choice21spec.getCases(), "case211");
		assertSize(1, case211.getAttributes());

		final Choice choice22spec = findChoice(cont2spec.getChoices(), "choice22");
		assertNull(choice22spec);

		final Choice choice23spec = findChoice(cont2spec.getChoices(), "choice23");
		assertNull(choice23spec);

		final Choice choice24spec = findChoice(cont2spec.getChoices(), "choice24");
		assertNull(choice24spec);

		final Choice choice25spec = findChoice(cont2spec.getChoices(), "choice25");
		assertSize(1, choice25spec.getCases());
		final Case case252 = findCase(choice25spec.getCases(), "case252");
		assertSize(1, case252.getAttributes());

		final HierarchicalPrimaryTypeSpecification cont3spec = modelService.getTypedAccess().getEModelSpecification(cont3modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final Choice choice31spec = findChoice(cont3spec.getChoices(), "choice31");
		assertSize(1, choice31spec.getCases());
		final Case case314 = findCase(choice31spec.getCases(), "case314");
		assertSize(2, case314.getAttributes());

		final Choice choice32spec = findChoice(cont3spec.getChoices(), "choice32");
		assertNull(choice32spec);

		final Choice choice33spec = findChoice(cont3spec.getChoices(), "choice33");
		assertSize(2, choice33spec.getCases());
		final Case case331 = findCase(choice33spec.getCases(), "case331");
		assertSize(1, case331.getPrimaryTypeSpecifications());
		final Case case334 = findCase(choice33spec.getCases(), "case334");
		assertSize(1, case334.getAttributes());

		// - - - - - - - - - - - - - - - - - - - - -

		final PrimaryTypeAttributeSpecification attr119 = findAttribute(cont1spec, "leaf119");
		final ActiveChoiceCase requiresAttr119 = findRequires(attr119.getRequires(), ActiveChoiceCase.class);
		assertEquals("case119", requiresAttr119.getCaseName());
		assertEquals("choice11", requiresAttr119.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr119.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr121 = findAttribute(cont1spec, "leaf121");
		final ActiveChoiceCase requiresAttr121 = findRequires(attr121.getRequires(), ActiveChoiceCase.class);
		assertEquals("case121", requiresAttr121.getCaseName());
		assertEquals("choice12", requiresAttr121.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr121.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr122 = findAttribute(cont1spec, "leaf122");
		final ActiveChoiceCase requiresAttr122 = findRequires(attr122.getRequires(), ActiveChoiceCase.class);
		assertEquals("case121", requiresAttr122.getCaseName());
		assertEquals("choice12", requiresAttr122.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr122.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr129 = findAttribute(cont1spec, "leaf129");
		final ActiveChoiceCase requiresAttr129 = findRequires(attr129.getRequires(), ActiveChoiceCase.class);
		assertEquals("case129", requiresAttr129.getCaseName());
		assertEquals("choice12", requiresAttr129.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr129.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr131 = findAttribute(cont1spec, "leaf131");
		final ActiveChoiceCase requiresAttr131 = findRequires(attr131.getRequires(), ActiveChoiceCase.class);
		assertEquals("case131", requiresAttr131.getCaseName());
		assertEquals("choice13", requiresAttr131.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr131.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr132 = findAttribute(cont1spec, "leaf132");
		final ActiveChoiceCase requiresAttr132 = findRequires(attr132.getRequires(), ActiveChoiceCase.class);
		assertEquals("case132", requiresAttr132.getCaseName());
		assertEquals("choice13", requiresAttr132.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr132.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr139 = findAttribute(cont1spec, "leaf139");
		final ActiveChoiceCase requiresAttr139 = findRequires(attr139.getRequires(), ActiveChoiceCase.class);
		assertEquals("case132", requiresAttr139.getCaseName());
		assertEquals("choice13", requiresAttr139.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr139.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr198 = findAttribute(cont1spec, "leaf198");
		final ActiveChoiceCase requiresAttr198 = findRequires(attr198.getRequires(), ActiveChoiceCase.class);
		assertEquals("case198", requiresAttr198.getCaseName());
		assertEquals("choice19", requiresAttr198.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr198.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr199 = findAttribute(cont1spec, "leaf199");
		final ActiveChoiceCase requiresAttr199 = findRequires(attr199.getRequires(), ActiveChoiceCase.class);
		assertEquals("case198", requiresAttr199.getCaseName());
		assertEquals("choice19", requiresAttr199.getChoiceName());
		assertEquals(cont1modelInfo.toImpliedUrn(), requiresAttr199.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr211 = findAttribute(cont2spec, "leaf211");
		final ActiveChoiceCase requiresAttr211 = findRequires(attr211.getRequires(), ActiveChoiceCase.class);
		assertEquals("case211", requiresAttr211.getCaseName());
		assertEquals("choice21", requiresAttr211.getChoiceName());
		assertEquals(cont2modelInfo.toImpliedUrn(), requiresAttr211.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr221 = findAttribute(cont2spec, "leaf221");
		assertNull(attr221);

		final PrimaryTypeAttributeSpecification attr231 = findAttribute(cont2spec, "leaf231");
		assertNull(attr231);

		final PrimaryTypeAttributeSpecification attr241 = findAttribute(cont2spec, "leaf241");
		assertNull(attr241);

		final PrimaryTypeAttributeSpecification attr251 = findAttribute(cont2spec, "leaf251");
		assertNull(attr251);

		final PrimaryTypeAttributeSpecification attr252 = findAttribute(cont2spec, "leaf252");
		final ActiveChoiceCase requiresAttr252 = findRequires(attr252.getRequires(), ActiveChoiceCase.class);
		assertEquals("case252", requiresAttr252.getCaseName());
		assertEquals("choice25", requiresAttr252.getChoiceName());
		assertEquals(cont2modelInfo.toImpliedUrn(), requiresAttr252.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr315 = findAttribute(cont3spec, "leaf315");
		final ActiveChoiceCase requiresAttr315 = findRequires(attr315.getRequires(), ActiveChoiceCase.class);
		assertEquals("case314", requiresAttr315.getCaseName());
		assertEquals("choice31", requiresAttr315.getChoiceName());
		assertEquals(cont3modelInfo.toImpliedUrn(), requiresAttr315.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr316 = findAttribute(cont3spec, "leaf316");
		final ActiveChoiceCase requiresAttr316 = findRequires(attr316.getRequires(), ActiveChoiceCase.class);
		assertEquals("case314", requiresAttr316.getCaseName());
		assertEquals("choice31", requiresAttr316.getChoiceName());
		assertEquals(cont3modelInfo.toImpliedUrn(), requiresAttr316.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr325 = findAttribute(cont3spec, "leaf325");
		assertNull(attr325);

		final PrimaryTypeAttributeSpecification attr326 = findAttribute(cont3spec, "leaf326");
		assertNull(attr326);

		final HierarchicalPrimaryTypeSpecification cont333spec = modelService.getTypedAccess().getEModelSpecification(cont333modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final ActiveChoiceCase requiresCont333spec = findRequires(cont333spec.getRequires(), ActiveChoiceCase.class);
		assertEquals("case331", requiresCont333spec.getCaseName());
		assertEquals("choice33", requiresCont333spec.getChoiceName());
		assertEquals(cont3modelInfo.toImpliedUrn(), requiresCont333spec.getOwningPrimaryTypeUrn());

		final PrimaryTypeAttributeSpecification attr336 = findAttribute(cont3spec, "leaf336");
		final ActiveChoiceCase requiresAttr336 = findRequires(attr336.getRequires(), ActiveChoiceCase.class);
		assertEquals("case334", requiresAttr336.getCaseName());
		assertEquals("choice33", requiresAttr336.getChoiceName());
		assertEquals(cont3modelInfo.toImpliedUrn(), requiresAttr336.getOwningPrimaryTypeUrn());
	}
}
