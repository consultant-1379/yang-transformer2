package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class PreProcessorMocNamingTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/pre-processor/";

	private static final String MODULE_NS_NAMING_ONE = "urn*test*moc-naming-one";
	private static final String MODULE_XYZ_VERSION_NAMING_ONE = "2021.3.7";

	private static final String MODULE_NS_NAMING_TWO = "urn.test.moc-naming-two";
	private static final String MODULE_XYZ_VERSION_NAMING_TWO = "2021.5.27";


	@Test
	public void test___moc_naming_simple___dont_us_$$_syntax_for_unique_mocs() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N663_DUPLICATE_DATA_NODE.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "moc-naming-simple")));
		context.setDontUse$$syntaxForUniqueMocs(true);
		YangTransformer2.transform(context);

		internal___moc_naming_simple___dont_us_$$_syntax_for_unique_mocs(context, null);
	}

	@Test
	public void test___moc_naming_simple___dont_us_$$_syntax_for_unique_mocs_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();

		overwritingProperties.setProperty(YangTransformer2PluginProperties.DONT_USE_DOLLARDOLLAR_FOR_UNIQUE_MOCS, "true");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "moc-naming-simple"), overwritingProperties);

		internal___moc_naming_simple___dont_us_$$_syntax_for_unique_mocs(null, actualNmtProperties);
	}

	private void internal___moc_naming_simple___dont_us_$$_syntax_for_unique_mocs(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont1);

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont11", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont11 = load(cont11modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont11);

		final ModelInfo cont11$$cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont11$$cont14", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont11$$cont14 = load(cont11$$cont14modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont14", cont11$$cont14);

		final ModelInfo cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont16", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont16 = load(cont16modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont16);

		final ModelInfo cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont12", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont12 = load(cont12modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont12);

		final ModelInfo cont12$$cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont12$$cont14", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont12$$cont14 = load(cont12$$cont14modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont14", cont12$$cont14);

		final ModelInfo cont1$$cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1$$cont1", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1$$cont1 = load(cont1$$cont1modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont1", cont1$$cont1);

		final ModelInfo cont1$$cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1$$cont18", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1$$cont18 = load(cont1$$cont18modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont18", cont1$$cont18);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont2", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont2);

		final ModelInfo cont2$$cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont2$$cont18", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont2$$cont18 = load(cont2$$cont18modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont18", cont2$$cont18);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont3", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont3);

		final ModelInfo cont31modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont31", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont31 = load(cont31modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont31);

		final ModelInfo cont32modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont32", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont32 = load(cont32modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont32);

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo contTwo1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont1", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo1 = load(contTwo1modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo1);

		final ModelInfo contTwo18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont18", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo18 = load(contTwo18modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo18);

		final ModelInfo contTwo41modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont41", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo41 = load(contTwo41modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo41);

		final ModelInfo contTwo41$$42modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont41$$cont42", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo41$$42 = load(contTwo41$$42modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont42", contTwo41$$42);

		final ModelInfo contTwo42modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont42", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo42 = load(contTwo42modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo42);

		final ModelInfo contTwo43modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont43", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo43 = load(contTwo43modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo43);

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 19);

		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_ONE, "cont1");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1", MODULE_NS_NAMING_ONE, "cont11");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont11", MODULE_NS_NAMING_ONE, "cont11$$cont14");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont11$$cont14", MODULE_NS_NAMING_ONE, "cont16");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1", MODULE_NS_NAMING_ONE, "cont12");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont12", MODULE_NS_NAMING_ONE, "cont12$$cont14");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1", MODULE_NS_NAMING_ONE, "cont1$$cont1");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1", MODULE_NS_NAMING_ONE, "cont1$$cont18");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_ONE, "cont2");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont2", MODULE_NS_NAMING_ONE, "cont2$$cont18");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_ONE, "cont3");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont3", MODULE_NS_NAMING_ONE, "cont31");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont3", MODULE_NS_NAMING_ONE, "cont32");

		// - - - 

		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_TWO, "cont1");
		findAndAssertContainment(rels, MODULE_NS_NAMING_TWO, "cont1", MODULE_NS_NAMING_TWO, "cont18");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont31", MODULE_NS_NAMING_TWO, "cont41");
		findAndAssertContainment(rels, MODULE_NS_NAMING_TWO, "cont41", MODULE_NS_NAMING_TWO, "cont41$$cont42");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont32", MODULE_NS_NAMING_TWO, "cont42");
		findAndAssertContainment(rels, MODULE_NS_NAMING_TWO, "cont42", MODULE_NS_NAMING_TWO, "cont43");
	}

	@Test
	public void test___moc_naming_simple___always_use_$$_syntax_even_for_unique_mocs() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N663_DUPLICATE_DATA_NODE.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "moc-naming-simple")));
		context.setDontUse$$syntaxForUniqueMocs(false);
		YangTransformer2.transform(context);

		internal___moc_naming_simple___always_use_$$_syntax_even_for_unique_mocs(context, null);
	}

	@Test
	public void test___moc_naming_simple___always_use_$$_syntax_even_for_unique_mocs_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.DONT_USE_DOLLARDOLLAR_FOR_UNIQUE_MOCS, "false");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "moc-naming-simple"), overwritingProperties);

		internal___moc_naming_simple___always_use_$$_syntax_even_for_unique_mocs(null, actualNmtProperties);
	}

	private void internal___moc_naming_simple___always_use_$$_syntax_even_for_unique_mocs(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont1);

		final ModelInfo cont1$$cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1$$cont11", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1$$cont11 = load(cont1$$cont11modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont11", cont1$$cont11);

		final ModelInfo cont1$$cont11$$cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1$$cont11$$cont14", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1$$cont11$$cont14 = load(cont1$$cont11$$cont14modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont14", cont1$$cont11$$cont14);

		final ModelInfo cont1$$cont11$$cont14$$cont16modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1$$cont11$$cont14$$cont16", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1$$cont11$$cont14$$cont16 = load(cont1$$cont11$$cont14$$cont16modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont16", cont1$$cont11$$cont14$$cont16);

		final ModelInfo cont1$$cont12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1$$cont12", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1$$cont12 = load(cont1$$cont12modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont12", cont1$$cont12);

		final ModelInfo cont1$$cont12$$cont14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1$$cont12$$cont14", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1$$cont12$$cont14 = load(cont1$$cont12$$cont14modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont14", cont1$$cont12$$cont14);

		final ModelInfo cont1$$cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1$$cont1", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1$$cont1 = load(cont1$$cont1modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont1", cont1$$cont1);

		final ModelInfo cont1$$cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont1$$cont18", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont1$$cont18 = load(cont1$$cont18modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont18", cont1$$cont18);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont2", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont2);

		final ModelInfo cont2$$cont18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont2$$cont18", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont2$$cont18 = load(cont2$$cont18modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont18", cont2$$cont18);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont3", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, cont3);

		final ModelInfo cont3$$cont31modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont3$$cont31", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont3$$cont31 = load(cont3$$cont31modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont31", cont3$$cont31);

		final ModelInfo cont3$$cont32modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_ONE, "cont3$$cont32", MODULE_XYZ_VERSION_NAMING_ONE);
		final PrimaryTypeDefinition cont3$$cont32 = load(cont3$$cont32modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont32", cont3$$cont32);

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo contTwo1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont1", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo1 = load(contTwo1modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo1);

		final ModelInfo contTwo1$$18modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont1$$cont18", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo1$$18 = load(contTwo1$$18modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont18", contTwo1$$18);

		final ModelInfo contTwo41modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont41", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo41 = load(contTwo41modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo41);

		final ModelInfo contTwo41$$42modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont41$$cont42", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo41$$42 = load(contTwo41$$42modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont42", contTwo41$$42);

		final ModelInfo contTwo42modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont42", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition contTwo42 = load(contTwo42modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo42);

		final ModelInfo cont42$$cont43modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_TWO, "cont42$$cont43", MODULE_XYZ_VERSION_NAMING_TWO);
		final PrimaryTypeDefinition cont42$$cont43 = load(cont42$$cont43modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont43", cont42$$cont43);

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 19);

		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_ONE, "cont1");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1", MODULE_NS_NAMING_ONE, "cont1$$cont11");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1$$cont11", MODULE_NS_NAMING_ONE, "cont1$$cont11$$cont14");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1$$cont11$$cont14", MODULE_NS_NAMING_ONE, "cont1$$cont11$$cont14$$cont16");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1", MODULE_NS_NAMING_ONE, "cont1$$cont12");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1$$cont12", MODULE_NS_NAMING_ONE, "cont1$$cont12$$cont14");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1", MODULE_NS_NAMING_ONE, "cont1$$cont1");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont1", MODULE_NS_NAMING_ONE, "cont1$$cont18");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_ONE, "cont2");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont2", MODULE_NS_NAMING_ONE, "cont2$$cont18");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_ONE, "cont3");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont3", MODULE_NS_NAMING_ONE, "cont3$$cont31");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont3", MODULE_NS_NAMING_ONE, "cont3$$cont32");

		// - - - 

		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_TWO, "cont1");
		findAndAssertContainment(rels, MODULE_NS_NAMING_TWO, "cont1", MODULE_NS_NAMING_TWO, "cont1$$cont18");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont3$$cont31", MODULE_NS_NAMING_TWO, "cont41");
		findAndAssertContainment(rels, MODULE_NS_NAMING_TWO, "cont41", MODULE_NS_NAMING_TWO, "cont41$$cont42");
		findAndAssertContainment(rels, MODULE_NS_NAMING_ONE, "cont3$$cont32", MODULE_NS_NAMING_TWO, "cont42");
		findAndAssertContainment(rels, MODULE_NS_NAMING_TWO, "cont42", MODULE_NS_NAMING_TWO, "cont42$$cont43");
	}

	private static final String MODULE_NS_NBH_ONE = "urn.test.namespace-boundary-handling-one";
	private static final String MODULE_XYZ_VERSION_NBH_ONE = "2021.3.7";

	private static final String MODULE_NS_NBH_TWO = "urn.test.namespace-boundary-handling-two";
	private static final String MODULE_XYZ_VERSION_NBH_TWO = "2021.4.23";

	private static final String MODULE_NS_NBH_THREE = "urn.test.namespace-boundary-handling-three";
	private static final String MODULE_XYZ_VERSION_NBH_THREE = "2021.5.13";

	@Test
	public void test___moc_naming_namespace_boundary_handling___dont_use_$$_for_unique_mocs() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N663_DUPLICATE_DATA_NODE.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "moc-naming-namespace-boundary-handling")));
		context.setDontUse$$syntaxForUniqueMocs(true);

		YangTransformer2.transform(context);

		internal___moc_naming_namespace_boundary_handling___dont_use_$$_for_unique_mocs(context, null);
	}

	@Test
	public void test___moc_naming_namespace_boundary_handling___dont_use_$$_for_unique_mocs_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.DONT_USE_DOLLARDOLLAR_FOR_UNIQUE_MOCS, "true");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "moc-naming-namespace-boundary-handling"), overwritingProperties);

		internal___moc_naming_namespace_boundary_handling___dont_use_$$_for_unique_mocs(null, actualNmtProperties);
	}

	private void internal___moc_naming_namespace_boundary_handling___dont_use_$$_for_unique_mocs(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_ONE, "cont1", MODULE_XYZ_VERSION_NBH_ONE);
		assertModelExists(cont1modelInfo);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_ONE, "cont2", MODULE_XYZ_VERSION_NBH_ONE);
		assertModelExists(cont2modelInfo);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_ONE, "cont3", MODULE_XYZ_VERSION_NBH_ONE);
		assertModelExists(cont3modelInfo);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_ONE, "cont4", MODULE_XYZ_VERSION_NBH_ONE);
		assertModelExists(cont4modelInfo);

		final ModelInfo cont4$$cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_ONE, "cont4$$cont3", MODULE_XYZ_VERSION_NBH_ONE);
		assertModelExists(cont4$$cont3modelInfo);

		final ModelInfo cont1$$cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_TWO, "cont1$$cont6", MODULE_XYZ_VERSION_NBH_TWO);
		assertModelExists(cont1$$cont6modelInfo);

		final ModelInfo cont2$$cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_TWO, "cont2$$cont6", MODULE_XYZ_VERSION_NBH_TWO);
		assertModelExists(cont2$$cont6modelInfo);

		final ModelInfo cont7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_TWO, "cont7", MODULE_XYZ_VERSION_NBH_TWO);
		assertModelExists(cont7modelInfo);

		final ModelInfo cont1$$cont6$$cont8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_THREE, "cont1$$cont6$$cont8", MODULE_XYZ_VERSION_NBH_THREE);
		assertModelExists(cont1$$cont6$$cont8modelInfo);

		final ModelInfo cont2$$cont6$$cont8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_THREE, "cont2$$cont6$$cont8", MODULE_XYZ_VERSION_NBH_THREE);
		assertModelExists(cont2$$cont6$$cont8modelInfo);

		final ModelInfo cont7$$cont8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_THREE, "cont7$$cont8", MODULE_XYZ_VERSION_NBH_THREE);
		assertModelExists(cont7$$cont8modelInfo);

		final ModelInfo cont9modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NBH_THREE, "cont9", MODULE_XYZ_VERSION_NBH_THREE);
		assertModelExists(cont9modelInfo);

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 12);

		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NBH_ONE, "cont1");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NBH_ONE, "cont2");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NBH_ONE, "cont3");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NBH_ONE, "cont4");
		findAndAssertContainment(rels, MODULE_NS_NBH_ONE, "cont4", MODULE_NS_NBH_ONE, "cont4$$cont3");
		findAndAssertContainment(rels, MODULE_NS_NBH_ONE, "cont1", MODULE_NS_NBH_TWO, "cont1$$cont6");
		findAndAssertContainment(rels, MODULE_NS_NBH_ONE, "cont2", MODULE_NS_NBH_TWO, "cont2$$cont6");
		findAndAssertContainment(rels, MODULE_NS_NBH_ONE, "cont3", MODULE_NS_NBH_TWO, "cont7");
		findAndAssertContainment(rels, MODULE_NS_NBH_TWO, "cont1$$cont6", MODULE_NS_NBH_THREE, "cont1$$cont6$$cont8");
		findAndAssertContainment(rels, MODULE_NS_NBH_TWO, "cont2$$cont6", MODULE_NS_NBH_THREE, "cont2$$cont6$$cont8");
		findAndAssertContainment(rels, MODULE_NS_NBH_TWO, "cont7", MODULE_NS_NBH_THREE, "cont7$$cont8");
		findAndAssertContainment(rels, MODULE_NS_NBH_THREE, "cont7$$cont8", MODULE_NS_NBH_THREE, "cont9");
	}

	private static final String MODULE_NS_NAMING_WEIRD_ONE = "urn*test*moc-naming-weird-one";
	private static final String MODULE_XYZ_VERSION_NAMING_WEIRD_ONE = "2021.3.7";

	private static final String MODULE_NS_NAMING_WEIRD_TWO = "urn*test*moc-naming-weird-two";
	private static final String MODULE_XYZ_VERSION_NAMING_WEIRD_TWO = "2021.5.12";

	private static final String MODULE_NS_NAMING_WEIRD_THREE = "urn*test*moc-naming-weird-three";
	private static final String MODULE_XYZ_VERSION_NAMING_WEIRD_THREE = "2021.11.1";

	@Test
	public void test___moc_naming_weird_cases___dont_us_$$_syntax_for_unique_mocs() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N663_DUPLICATE_DATA_NODE.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "moc-naming-weird-cases")));
		context.setDontUse$$syntaxForUniqueMocs(true);
		YangTransformer2.transform(context);

		internal___moc_naming_weird_cases___dont_us_$$_syntax_for_unique_mocs(context, null);
	}

	@Test
	public void test___moc_naming_weird_cases___dont_us_$$_syntax_for_unique_mocs_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();

		overwritingProperties.setProperty(YangTransformer2PluginProperties.DONT_USE_DOLLARDOLLAR_FOR_UNIQUE_MOCS, "true");

		final Properties actualNmtProperties = transformThroughNmtPlugin(new File(TEST_SUB_DIR + "moc-naming-weird-cases"), overwritingProperties);

		internal___moc_naming_weird_cases___dont_us_$$_syntax_for_unique_mocs(null, actualNmtProperties);
	}	

	private void internal___moc_naming_weird_cases___dont_us_$$_syntax_for_unique_mocs(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo contOne1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_ONE, "cont1", MODULE_XYZ_VERSION_NAMING_WEIRD_ONE);
		final PrimaryTypeDefinition contOne1 = load(contOne1modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contOne1);

		final ModelInfo contOne6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_ONE, "cont6", MODULE_XYZ_VERSION_NAMING_WEIRD_ONE);
		final PrimaryTypeDefinition contOne6 = load(contOne6modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contOne6);

		final ModelInfo contOne6to7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_ONE, "cont6$$cont7", MODULE_XYZ_VERSION_NAMING_WEIRD_ONE);
		final PrimaryTypeDefinition contOne6to7 = load(contOne6to7modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont7", contOne6to7);

		final ModelInfo contOne7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_ONE, "cont7", MODULE_XYZ_VERSION_NAMING_WEIRD_ONE);
		final PrimaryTypeDefinition contOne7 = load(contOne7modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contOne7);

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo contTwo2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_TWO, "cont2", MODULE_XYZ_VERSION_NAMING_WEIRD_TWO);
		final PrimaryTypeDefinition contTwo2 = load(contTwo2modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo2);

		final ModelInfo contTwo1to2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_TWO, "cont1$$cont2", MODULE_XYZ_VERSION_NAMING_WEIRD_TWO);
		final PrimaryTypeDefinition contTwo1to2 = load(contTwo1to2modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont2", contTwo1to2);

		final ModelInfo contTwo7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_TWO, "cont7", MODULE_XYZ_VERSION_NAMING_WEIRD_TWO);
		final PrimaryTypeDefinition contTwo7 = load(contTwo7modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contTwo7);

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo contThree8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_THREE, "cont8", MODULE_XYZ_VERSION_NAMING_WEIRD_THREE);
		final PrimaryTypeDefinition contThree8 = load(contThree8modelInfo);
		assertHasNotMeta(Constants.META_ORIGINAL_NAME, contThree8);

		final ModelInfo contThree8under7underModuleOneModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_THREE, "moc-naming-weird-one$$cont7$$cont8", MODULE_XYZ_VERSION_NAMING_WEIRD_THREE);
		final PrimaryTypeDefinition contThree8under7underModuleOne = load(contThree8under7underModuleOneModelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont8", contThree8under7underModuleOne);

		final ModelInfo contThree8under7under6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_THREE, "cont6$$cont7$$cont8", MODULE_XYZ_VERSION_NAMING_WEIRD_THREE);
		final PrimaryTypeDefinition contThree8under7under6 = load(contThree8under7under6modelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont8", contThree8under7under6);

		final ModelInfo contThree8under7underModuleTwoModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NAMING_WEIRD_THREE, "moc-naming-weird-two$$cont7$$cont8", MODULE_XYZ_VERSION_NAMING_WEIRD_THREE);
		final PrimaryTypeDefinition contThree8under7underModuleTwo = load(contThree8under7underModuleTwoModelInfo);
		assertHasMeta(Constants.META_ORIGINAL_NAME, "cont8", contThree8under7underModuleTwo);

		// - - - - - - - - - - - - - - - - - -

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 11);

		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_WEIRD_ONE, "cont1");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_WEIRD_ONE, "cont6");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_WEIRD_ONE, "cont7");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_WEIRD_TWO, "cont2");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_WEIRD_TWO, "cont7");
		findAndAssertContainment(rels, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.MANAGEDELEMENT, MODULE_NS_NAMING_WEIRD_THREE, "cont8");

		// - - - 

		findAndAssertContainment(rels, MODULE_NS_NAMING_WEIRD_ONE, "cont6", MODULE_NS_NAMING_WEIRD_ONE, "cont6$$cont7");
		findAndAssertContainment(rels, MODULE_NS_NAMING_WEIRD_ONE, "cont1", MODULE_NS_NAMING_WEIRD_TWO, "cont1$$cont2");

		// - - - 

		findAndAssertContainment(rels, MODULE_NS_NAMING_WEIRD_ONE, "cont7", MODULE_NS_NAMING_WEIRD_THREE, "moc-naming-weird-one$$cont7$$cont8");
		findAndAssertContainment(rels, MODULE_NS_NAMING_WEIRD_ONE, "cont6$$cont7", MODULE_NS_NAMING_WEIRD_THREE, "cont6$$cont7$$cont8");
		findAndAssertContainment(rels, MODULE_NS_NAMING_WEIRD_TWO, "cont7", MODULE_NS_NAMING_WEIRD_THREE, "moc-naming-weird-two$$cont7$$cont8");
	}
}
