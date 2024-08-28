package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LifeCycleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MetaInformation;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_combined.CombinedEnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_eventtype.EventTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext.Target;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EModelHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.eri.ValidatorFindingTypeEri;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class EModelHelperTest  extends TransformerTestUtil {
	
	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/";

	@Test
	public void test___general() {

		/*
		 * Test extension namespace generation
		 */
		assertEquals("category__type__version", EModelHelper.getExtensionModelNamespace(new Target("category", "type", "version")));

		/*
		 * Test extension version always 1.0.0
		 */
		assertEquals("1.0.0", EModelHelper.getModelInfoVersionForExtension());

		/*
		 * Test meta information
		 */
		final List<MetaInformation> metaList = new ArrayList<>();
		EModelHelper.attachMeta(metaList, "name1", "value");
		EModelHelper.attachMeta(metaList, "name2");
		assertTrue(metaList.stream().filter(mi -> mi.getMetaName().equals("name1") && mi.getMetaValue().equals("value")).findAny().isPresent());
		assertTrue(metaList.stream().filter(mi -> mi.getMetaName().equals("name2") && mi.getMetaValue() == null).findAny().isPresent());

		/*
		 * Test URN generation
		 */
		final PrimaryTypeDefinition ptd = new PrimaryTypeDefinition();
		ptd.setNs("namespace");
		ptd.setName("name");
		ptd.setVersion("1.2.3");
		assertEquals("//namespace/name/1.2.3", EModelHelper.getImpliedVersionedUrn("namespace", "name", "1.2.3"));

		/*
		 * Mapping schema class to constant
		 */
		assertEquals(SchemaConstants.DPS_PRIMARYTYPE, EModelHelper.getSchemaNameForEModelClass(new PrimaryTypeDefinition().getClass()));
		assertEquals(SchemaConstants.DPS_PRIMARYTYPE_EXT, EModelHelper.getSchemaNameForEModelClass(new PrimaryTypeExtensionDefinition().getClass()));
		assertEquals(SchemaConstants.DPS_RELATIONSHIP, EModelHelper.getSchemaNameForEModelClass(new PrimaryTypeRelationshipDefinition().getClass()));
		assertEquals(SchemaConstants.OSS_EDT, EModelHelper.getSchemaNameForEModelClass(new EnumDataTypeDefinition().getClass()));
		assertEquals(SchemaConstants.OSS_EDT_EXT, EModelHelper.getSchemaNameForEModelClass(new EnumDataTypeExtensionDefinition().getClass()));
		assertEquals(SchemaConstants.OSS_EDT_COMBINED, EModelHelper.getSchemaNameForEModelClass(new CombinedEnumDataTypeDefinition().getClass()));
		assertEquals(SchemaConstants.CFM_MIMINFO, EModelHelper.getSchemaNameForEModelClass(new ManagementInformationModelInformation().getClass()));
		assertEquals(SchemaConstants.OSS_CDT, EModelHelper.getSchemaNameForEModelClass(new ComplexDataTypeDefinition().getClass()));
		try {
			EModelHelper.getSchemaNameForEModelClass(new EventTypeDefinition().getClass());
			fail("Should have thrown.");
		} catch (final Exception ignore) {}
	}

	private static final String MODULE_NS_ONE = "urn:test:emodel-helper-one";
	private static final String MODULE_NAME_ONE = "emodel-helper-one";
	private static final String MODULE_REVISION_ONE = null;
	private static final String MODULE_XYZ_VERSION_ONE = "0.0.0";

	private static final String MODULE_NS_TWO = "urn:test:emodel-helper-two";
	private static final String MODULE_NAME_TWO = "emodel-helper-two";
	private static final String MODULE_REVISION_TWO = "2021-10-30";
	private static final String MODULE_XYZ_VERSION_TWO = "2021.10.30";

	private static final String MODULE_NS_ERI_XYZ = "urn:test:ericsson-using-xyz";
	private static final String MODULE_NAME_ERI_XYZ = "ericsson-using-xyz";
	private static final String MODULE_REVISION_ERI_XYZ = "2021-11-31";
	private static final String MODULE_XYZ_VERSION_ERI_XYZ = "5.6.7";

	private static final String MODULE_NS_ERI_NOT_XYZ = "urn:test:ericsson-not-using-xyz";
	private static final String MODULE_NAME_ERI_NOT_XYZ = "ericsson-not-using-xyz";
	private static final String MODULE_REVISION_ERI_NOT_XYZ = "2021-12-03";
	private static final String MODULE_XYZ_VERSION_ERI_NOT_XYZ = "2021.12.3";

	private static final String MODULE_NS_ERI_NO_REV = "urn:test:ericsson-no-revision";
	private static final String MODULE_NAME_ERI_NO_REV = "ericsson-no-revision";
	private static final String MODULE_REVISION_ERI_NO_REV = null;
	private static final String MODULE_XYZ_VERSION_ERI_NO_REV = "0.0.0";

	private static final String MODULE_NS_ERI_LABEL = "urn:test:ericsson-using-label";
	private static final String MODULE_NAME_ERI_LABEL = "ericsson-using-label";
	private static final String MODULE_REVISION_ERI_LABEL = "2024-01-30";
	private static final String MODULE_XYZ_VERSION_ERI_LABEL = "5.7.3";

	@Test
	public void test___model_related() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P032_MISSING_REVISION.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E601_MISSING_VERSION_RELEASE_CORRECTION_FOR_CURRENT_REVISION.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "emodel-helper")));
		YangTransformer2.transform(context);

		// ------------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont1", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertEquals(LifeCycleType.CURRENT, leaf11.getLifeCycle());

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertEquals(LifeCycleType.DEPRECATED, leaf12.getLifeCycle());

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertEquals(LifeCycleType.OBSOLETE, leaf13.getLifeCycle());

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertEquals(LifeCycleType.PRELIMINARY, leaf14.getLifeCycle());

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont2", MODULE_XYZ_VERSION_ONE);
		assertModelExists(cont2modelInfo);

		final ModelInfo list4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "list4", MODULE_XYZ_VERSION_TWO);
		assertModelExists(list4modelInfo);

		final ModelInfo list5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "list5", MODULE_XYZ_VERSION_TWO);
		assertModelExists(list5modelInfo);

		final ModelInfo list6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "list6", MODULE_XYZ_VERSION_TWO);
		assertModelExists(list6modelInfo);

		final ModelInfo list7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_TWO, "list7", MODULE_XYZ_VERSION_TWO);
		assertModelExists(list7modelInfo);

		final ModelInfo cont8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ERI_XYZ, "cont8", MODULE_XYZ_VERSION_ERI_XYZ);
		assertModelExists(cont8modelInfo);

		final ModelInfo cont9modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ERI_NOT_XYZ, "cont9", MODULE_XYZ_VERSION_ERI_NOT_XYZ);
		assertModelExists(cont9modelInfo);

		final ModelInfo cont10modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ERI_NO_REV, "cont10", MODULE_XYZ_VERSION_ERI_NO_REV);
		assertModelExists(cont10modelInfo);

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ERI_LABEL, "cont11", MODULE_XYZ_VERSION_ERI_LABEL);
		assertModelExists(cont11modelInfo);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 10);

		final PrimaryTypeContainment comTopToCont1 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		assertContainment(comTopToCont1, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_ONE, "cont1", MODULE_XYZ_VERSION_ONE, null, 1L);

		final PrimaryTypeContainment comTopToCont2 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont2");
		assertContainment(comTopToCont2, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_ONE, "cont2", MODULE_XYZ_VERSION_ONE, null, 1L);

		final PrimaryTypeContainment comTopToList4 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "list4");
		assertContainment(comTopToList4, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_TWO, "list4", MODULE_XYZ_VERSION_TWO, 3L, null);

		final PrimaryTypeContainment comTopToList5 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "list5");
		assertContainment(comTopToList5, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_TWO, "list5", MODULE_XYZ_VERSION_TWO, null, 8L);

		final PrimaryTypeContainment comTopToList6 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "list6");
		assertContainment(comTopToList6, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_TWO, "list6", MODULE_XYZ_VERSION_TWO, null, null);

		final PrimaryTypeContainment comTopToList7 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "list7");
		assertContainment(comTopToList7, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_TWO, "list7", MODULE_XYZ_VERSION_TWO, null, null);

		final PrimaryTypeContainment comTopToCont8 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont8");
		assertContainment(comTopToCont8, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_ERI_XYZ, "cont8", MODULE_XYZ_VERSION_ERI_XYZ, null, 1L);

		final PrimaryTypeContainment comTopToCont9 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont9");
		assertContainment(comTopToCont9, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_ERI_NOT_XYZ, "cont9", MODULE_XYZ_VERSION_ERI_NOT_XYZ, null, 1L);

		final PrimaryTypeContainment comTopToCont10 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont10");
		assertContainment(comTopToCont10, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_ERI_NO_REV, "cont10", MODULE_XYZ_VERSION_ERI_NO_REV, null, 1L);

		final PrimaryTypeContainment comTopToCont11 = findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont11");
		assertContainment(comTopToCont11, context, null, Constants.COM_TOP_MANAGEDELEMENT_STAR.getNamespace(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getName(), Constants.COM_TOP_MANAGEDELEMENT_STAR.getVersion().toString(), MODULE_NS_ERI_LABEL, "cont11", MODULE_XYZ_VERSION_ERI_LABEL, null, 1L);

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModuleOne = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE);
		assertModelExists(modelInfoForYangModuleOne);

		final ModelInfo modelInfoForYangModuleTwo = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO);
		assertModelExists(modelInfoForYangModuleTwo);

		final ModelInfo modelInfoForYangModuleEriUsingXyz = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ERI_XYZ, MODULE_NAME_ERI_XYZ, MODULE_REVISION_ERI_XYZ);
		assertModelExists(modelInfoForYangModuleEriUsingXyz);

		final ModelInfo modelInfoForYangModuleEriNotUsingXyz = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ERI_NOT_XYZ, MODULE_NAME_ERI_NOT_XYZ, MODULE_REVISION_ERI_NOT_XYZ);
		assertModelExists(modelInfoForYangModuleEriNotUsingXyz);

		final ModelInfo modelInfoForYangModuleEriNoRevision = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ERI_NO_REV, MODULE_NAME_ERI_NO_REV, MODULE_REVISION_ERI_NO_REV);
		assertModelExists(modelInfoForYangModuleEriNoRevision);

		final ModelInfo modelInfoForYangModuleEriLabel = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ERI_LABEL, MODULE_NAME_ERI_LABEL, MODULE_REVISION_ERI_LABEL);
		assertModelExists(modelInfoForYangModuleEriLabel);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);

		assertHasSupportedMim(mimInfo, MODULE_NS_ONE, MODULE_XYZ_VERSION_ONE, modelInfoForYangModuleOne.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_TWO, MODULE_XYZ_VERSION_TWO, modelInfoForYangModuleTwo.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_ERI_XYZ, MODULE_XYZ_VERSION_ERI_XYZ, modelInfoForYangModuleEriUsingXyz.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_ERI_NOT_XYZ, MODULE_XYZ_VERSION_ERI_NOT_XYZ, modelInfoForYangModuleEriNotUsingXyz.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_ERI_NO_REV, MODULE_XYZ_VERSION_ERI_NO_REV, modelInfoForYangModuleEriNoRevision.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_ERI_LABEL, MODULE_XYZ_VERSION_ERI_LABEL, modelInfoForYangModuleEriLabel.toUrn());
	}
}
