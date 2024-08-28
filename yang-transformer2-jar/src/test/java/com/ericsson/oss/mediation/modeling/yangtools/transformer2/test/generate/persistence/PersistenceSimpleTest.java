package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.ReadBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LifeCycleType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.eri.ValidatorFindingTypeEri;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class PersistenceSimpleTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "persistence/";

	private static final String MODULE_NS_NSD = "urn:test:persistence-simple-nsd";
	private static final String MODULE_NAME_NSD = "persistence-simple-nsd";
	private static final String MODULE_REVISION_NSD = "2021-10-18";
	private static final String MODULE_XYZ_VERSION_NSD = "2021.10.18";

	@Test
	public void test___persistence_simple_nsd() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E639_NOTIFIABLE_STATE_DATA_TRUE_TOO_LOW.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "persistence-simple/persistence-simple-nsd.yang")));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(7, 1, 0, 0, 5, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "cont1", MODULE_XYZ_VERSION_NSD);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont1);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont1);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf11);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf11);

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf12);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf12);

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "cont2", MODULE_XYZ_VERSION_NSD);
		assertModelDoesNotExist(cont2modelInfo);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "cont3", MODULE_XYZ_VERSION_NSD);
		final PrimaryTypeDefinition cont3 = load(cont3modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont3);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont3);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "cont4", MODULE_XYZ_VERSION_NSD);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont4);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont4);

		final PrimaryTypeAttribute leaf41 = findAttribute(cont4, "leaf41");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf41);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf41);

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "cont5", MODULE_XYZ_VERSION_NSD);
		final PrimaryTypeDefinition cont5 = load(cont5modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont5);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont5);

		final PrimaryTypeAttribute leaf51 = findAttribute(cont5, "leaf51");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf51);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf51);

		final ModelInfo cont6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "cont6", MODULE_XYZ_VERSION_NSD);
		assertModelDoesNotExist(cont6modelInfo);

		final ModelInfo cont7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "cont7", MODULE_XYZ_VERSION_NSD);
		final PrimaryTypeDefinition cont7 = load(cont7modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont7);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont7);

		final PrimaryTypeAttribute leaf71 = findAttribute(cont7, "leaf71");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf71);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf71);

		final ModelInfo list8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "list8", MODULE_XYZ_VERSION_NSD);
		assertModelDoesNotExist(list8modelInfo);

		final ModelInfo list9modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "list9", MODULE_XYZ_VERSION_NSD);
		final PrimaryTypeDefinition list9 = load(list9modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, list9);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, list9);

		final PrimaryTypeAttribute leaf91 = findAttribute(list9, "leaf91");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf91);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf91);
		assertTrue(leaf91.isKey());

		final ModelInfo cont10modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "cont10", MODULE_XYZ_VERSION_NSD);
		final PrimaryTypeDefinition cont10 = load(cont10modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont10);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont10);

		final PrimaryTypeAttribute leaf101 = findAttribute(cont10, "leaf101");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf101);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf101);

		final PrimaryTypeAttribute leaf102 = findAttribute(cont10, "leaf102");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf102);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf102);

		final ModelInfo cont11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_NSD, "cont11", MODULE_XYZ_VERSION_NSD);
		assertModelDoesNotExist(cont11modelInfo);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 7);

		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont3");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont7");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "list9");
		findAndAssertContainment(rels, "cont3", "cont4");
		findAndAssertContainment(rels, "cont4", "cont5");
		findAndAssertContainment(rels, "list9", "cont10");

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_NSD, MODULE_NAME_NSD, MODULE_REVISION_NSD);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertEquals(5, mimInfo.getSupportedMims().getMimMappedTo().size());

		assertHasSupportedMim(mimInfo, MODULE_NS_NSD, MODULE_XYZ_VERSION_NSD, modelInfoForYangModule.toUrn());
	}

	private static final String MODULE_NS_STATIC_DATA = "urn:test:persistence-simple-static-data";
	private static final String MODULE_NAME_STATIC_DATA = "persistence-simple-static-data";
	private static final String MODULE_REVISION_STATIC_DATA = "2021-07-20";
	private static final String MODULE_XYZ_VERSION_STATIC_DATA = "2021.7.20";

	@Test
	public void test___persistence_simple_static_data() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "persistence-simple/persistence-simple-static-data.yang")));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(4, 1, 0, 0, 5, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_STATIC_DATA, "cont1", MODULE_XYZ_VERSION_STATIC_DATA);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont1);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont1);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf11);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf11);

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf12);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf12);

		final ModelInfo cont2ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_STATIC_DATA, "cont2", MODULE_XYZ_VERSION_STATIC_DATA);
		final PrimaryTypeDefinition cont2 = load(cont2ModelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont2);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont2);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf21);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf21);

		final ModelInfo cont3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_STATIC_DATA, "cont3", MODULE_XYZ_VERSION_STATIC_DATA);
		assertModelDoesNotExist(cont3modelInfo);

		final ModelInfo cont4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_STATIC_DATA, "cont4", MODULE_XYZ_VERSION_STATIC_DATA);
		final PrimaryTypeDefinition cont4 = load(cont4modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont4);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont4);

		final PrimaryTypeAttribute leaf41 = findAttribute(cont4, "leaf41");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf41);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf41);

		final PrimaryTypeAttribute leaf42 = findAttribute(cont4, "leaf42");
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf42);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf42);

		final ModelInfo list5ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_STATIC_DATA, "list5", MODULE_XYZ_VERSION_STATIC_DATA);
		final PrimaryTypeDefinition list5 = load(list5ModelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, list5);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, list5);

		final PrimaryTypeAttribute leaf51 = findAttribute(list5, "leaf51");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf51);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf51);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 4);

		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont4");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "list5");
		findAndAssertContainment(rels, "cont1", "cont2");

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_STATIC_DATA, MODULE_NAME_STATIC_DATA, MODULE_REVISION_STATIC_DATA);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertEquals(5, mimInfo.getSupportedMims().getMimMappedTo().size());

		assertHasSupportedMim(mimInfo, MODULE_NS_STATIC_DATA, MODULE_XYZ_VERSION_STATIC_DATA, modelInfoForYangModule.toUrn());
	}

	private static final String MODULE_NS_LIFECYCLE = "urn:test:persistence-simple-lifecycle";
	private static final String MODULE_NAME_LIFECYCLE = "persistence-simple-lifecycle";
	private static final String MODULE_REVISION_LIFECYCLE = "2021-03-11";
	private static final String MODULE_XYZ_VERSION_LIFECYCLE = "2021.3.11";

	@Test
	public void test___persistence_simple_lifecycle() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "persistence-simple/persistence-simple-lifecycle.yang")));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(3, 0, 0, 0, 5, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_LIFECYCLE, "cont1", MODULE_XYZ_VERSION_LIFECYCLE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont1);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont1);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertLifecycle(LifeCycleType.DEPRECATED, leaf11);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf11);
		assertWriteBehaviour(WriteBehaviorType.DELEGATE, leaf11);

		final PrimaryTypeAttribute leaf12 = findAttribute(cont1, "leaf12");
		assertLifecycle(LifeCycleType.CURRENT, leaf12);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf12);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf12);

		final PrimaryTypeAttribute leaf13 = findAttribute(cont1, "leaf13");
		assertLifecycle(LifeCycleType.DEPRECATED, leaf13);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf13);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf13);

		final PrimaryTypeAttribute leaf14 = findAttribute(cont1, "leaf14");
		assertLifecycle(LifeCycleType.PRELIMINARY, leaf14);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf14);
		assertWriteBehaviour(WriteBehaviorType.DELEGATE, leaf14);

		final ModelInfo cont2ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_LIFECYCLE, "cont2", MODULE_XYZ_VERSION_LIFECYCLE);
		final PrimaryTypeDefinition cont2 = load(cont2ModelInfo);
		assertLifecycle(LifeCycleType.PRELIMINARY, cont2);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont2);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont2);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertLifecycle(LifeCycleType.PRELIMINARY, leaf21);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf21);
		assertWriteBehaviour(WriteBehaviorType.DELEGATE, leaf21);

		final PrimaryTypeAttribute leaf22 = findAttribute(cont2, "leaf22");
		assertLifecycle(LifeCycleType.PRELIMINARY, leaf22);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf22);
		assertWriteBehaviour(WriteBehaviorType.DELEGATE, leaf22);

		final ModelInfo list3ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_LIFECYCLE, "list3", MODULE_XYZ_VERSION_LIFECYCLE);
		final PrimaryTypeDefinition list3 = load(list3ModelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, list3);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, list3);

		final PrimaryTypeAttribute leaf31 = findAttribute(list3, "leaf31");
		assertLifecycle(LifeCycleType.DEPRECATED, leaf31);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf31);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, leaf31);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 3);

		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont2");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "list3");

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_LIFECYCLE, MODULE_NAME_LIFECYCLE, MODULE_REVISION_LIFECYCLE);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertEquals(5, mimInfo.getSupportedMims().getMimMappedTo().size());

		assertHasSupportedMim(mimInfo, MODULE_NS_LIFECYCLE, MODULE_XYZ_VERSION_LIFECYCLE, modelInfoForYangModule.toUrn());
	}

	private static final String MODULE_NS_COMBINED = "urn:test:persistence-simple-combined";
	private static final String MODULE_NAME_COMBINED = "persistence-simple-combined";
	private static final String MODULE_REVISION_COMBINED = "2021-10-18";
	private static final String MODULE_XYZ_VERSION_COMBINED = "2021.10.18";

	@Test
	public void test___persistence_simple_combined() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "persistence-simple/persistence-simple-combined.yang")));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(4, 0, 0, 0, 5, 0);

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_COMBINED, "cont1", MODULE_XYZ_VERSION_COMBINED);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont1);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont1);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertLifecycle(LifeCycleType.DEPRECATED, leaf11);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf11);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf11);

		final ModelInfo cont2ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_COMBINED, "cont2", MODULE_XYZ_VERSION_COMBINED);
		final PrimaryTypeDefinition cont2 = load(cont2ModelInfo);
		assertTrue(cont2.getLifeCycle() == LifeCycleType.PRELIMINARY);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont2);
		assertWriteBehaviour(WriteBehaviorType.PERSIST_AND_DELEGATE, cont2);

		final PrimaryTypeAttribute leaf21 = findAttribute(cont2, "leaf21");
		assertLifecycle(LifeCycleType.DEPRECATED, leaf21);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf21);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf21);

		final ModelInfo cont3ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_COMBINED, "cont3", MODULE_XYZ_VERSION_COMBINED);
		final PrimaryTypeDefinition cont3 = load(cont3ModelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont3);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont3);

		final PrimaryTypeAttribute leaf31 = findAttribute(cont3, "leaf31");
		assertLifecycle(LifeCycleType.DEPRECATED, leaf31);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf31);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf31);

		final ModelInfo cont4ModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_COMBINED, "cont4", MODULE_XYZ_VERSION_COMBINED);
		final PrimaryTypeDefinition cont4 = load(cont4ModelInfo);

		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, cont4);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, cont4);

		final PrimaryTypeAttribute leaf41 = findAttribute(cont4, "leaf41");
		assertLifecycle(LifeCycleType.DEPRECATED, leaf41);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf41);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf41);

		final PrimaryTypeAttribute leaf42 = findAttribute(cont4, "leaf42");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf42);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf42);

		final PrimaryTypeAttribute leaf43 = findAttribute(cont4, "leaf43");
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, leaf43);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf43);

		final PrimaryTypeAttribute leaf44 = findAttribute(cont4, "leaf44");
		assertLifecycle(LifeCycleType.DEPRECATED, leaf44);
		assertReadBehaviour(ReadBehaviorType.FROM_DELEGATE, leaf44);
		assertWriteBehaviour(WriteBehaviorType.NOT_ALLOWED, leaf44);

		// -----------------------------------------------------

		final ModelInfo modelInfoForPrimaryTypeRelationshipDefinition = PrimaryTypeRelationshipGenerator.getModelInfoForPrimaryTypeRelationshipDefinition(DEFAULT_TEST_TARGET);
		final PrimaryTypeRelationshipDefinition rels = load(modelInfoForPrimaryTypeRelationshipDefinition);

		assertContainmentsCreatedCount(rels, 4);

		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont1");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont2");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont3");
		findAndAssertContainment(rels, Constants.MANAGEDELEMENT, "cont4");

		// -----------------------------------------------------

		final ModelInfo modelInfoForYangModule = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_COMBINED, MODULE_NAME_COMBINED, MODULE_REVISION_COMBINED);
		assertModelExists(modelInfoForYangModule);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertEquals(5, mimInfo.getSupportedMims().getMimMappedTo().size());

		assertHasSupportedMim(mimInfo, MODULE_NS_COMBINED, MODULE_XYZ_VERSION_COMBINED, modelInfoForYangModule.toUrn());
	}
}