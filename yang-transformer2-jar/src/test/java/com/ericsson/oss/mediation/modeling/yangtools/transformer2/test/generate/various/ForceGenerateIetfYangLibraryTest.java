package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.ReadBehavior;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.WriteBehavior;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.ReadBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LifeCycleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ListType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LongType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.UnionType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.IetfYangLibraryParser;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class ForceGenerateIetfYangLibraryTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/";

	private static final String YANG_LIBRARY_OLD_XYZ_VERSION = "2016.06.21";
	private static final String YANG_LIBRARY_NEW_XYZ_VERSION = "2019.1.4";

	@Test
	public void test___old_yang_library___no_eri_extensions() {

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(
				new File(TEST_SUB_DIR + "force-generate-ietf-yang-library"),
				new File(IETF_YANG_LIBRARY_OLD),
				new File(IETF_INET_TYPES),
				new File(IETF_YANG_TYPES),
				new File(IETF_DATASTORES)
				));

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo modulesStateModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "modules-state", YANG_LIBRARY_OLD_XYZ_VERSION);
		final PrimaryTypeDefinition modulesState = load(modulesStateModelInfo);
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, modulesState.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, modulesState.getWriteBehavior());

		final PrimaryTypeAttribute moduleSetId = findAttribute(modulesState, "module-set-id");
		assertInstanceOf(StringType.class, moduleSetId.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleSetId.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleSetId.getWriteBehavior());

		final ModelInfo moduleModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "module", YANG_LIBRARY_OLD_XYZ_VERSION);
		final PrimaryTypeDefinition module = load(moduleModelInfo);
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, module.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, module.getWriteBehavior());

		assertNotNull(module.getMultiKey());
		assertSize(2, module.getMultiKey().getKeyParts());
		assertEquals("name", module.getMultiKey().getKeyParts().get(0));
		assertEquals("revision", module.getMultiKey().getKeyParts().get(1));

		final PrimaryTypeAttribute moduleConformanceType = findAttribute(module, "conformance-type");
		assertInstanceOf(EnumRefType.class, moduleConformanceType.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleConformanceType.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleConformanceType.getWriteBehavior());

		final PrimaryTypeAttribute feature = findAttribute(module, "feature");
		assertInstanceOf(ListType.class, feature.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, feature.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, feature.getWriteBehavior());

		final PrimaryTypeAttribute moduleSchema = findAttribute(module, "schema");
		assertInstanceOf(StringType.class, moduleSchema.getType());
		assertEquals(ReadBehaviorType.FROM_DELEGATE, moduleSchema.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleSchema.getWriteBehavior());

		final PrimaryTypeAttribute moduleFeature = findAttribute(module, "feature");
		assertInstanceOf(ListType.class, moduleFeature.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleFeature.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleFeature.getWriteBehavior());

		final PrimaryTypeAttribute moduleName = findAttribute(module, "name");
		assertInstanceOf(StringType.class, moduleName.getType());
		assertTrue(moduleName.isKey());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleName.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleName.getWriteBehavior());

		final PrimaryTypeAttribute moduleNamespace = findAttribute(module, "namespace");
		assertInstanceOf(StringType.class, moduleNamespace.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleNamespace.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleNamespace.getWriteBehavior());

		final PrimaryTypeAttribute moduleRevision = findAttribute(module, "revision");
		assertInstanceOf(UnionType.class, moduleRevision.getType());
		assertTrue(moduleRevision.isKey());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleRevision.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleRevision.getWriteBehavior());

		final PrimaryTypeAttribute moduleVersion = findAttribute(module, "version");
		assertNull(moduleVersion);
		final PrimaryTypeAttribute moduleRelease = findAttribute(module, "release");
		assertNull(moduleRelease);
		final PrimaryTypeAttribute moduleCorrection = findAttribute(module, "correction");
		assertNull(moduleCorrection);

		final ModelInfo submoduleModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "submodule", YANG_LIBRARY_OLD_XYZ_VERSION);
		final PrimaryTypeDefinition submodule = load(submoduleModelInfo);
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, submodule.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, submodule.getWriteBehavior());

		assertNotNull(submodule.getMultiKey());
		assertSize(2, submodule.getMultiKey().getKeyParts());
		assertEquals("name", submodule.getMultiKey().getKeyParts().get(0));
		assertEquals("revision", submodule.getMultiKey().getKeyParts().get(1));

		final PrimaryTypeAttribute submoduleSchema = findAttribute(submodule, "schema");
		assertInstanceOf(StringType.class, submoduleSchema.getType());
		assertEquals(ReadBehaviorType.FROM_DELEGATE, submoduleSchema.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, submoduleSchema.getWriteBehavior());

		final PrimaryTypeAttribute submoduleName = findAttribute(submodule, "name");
		assertInstanceOf(StringType.class, submoduleName.getType());
		assertTrue(submoduleName.isKey());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, submoduleName.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, submoduleName.getWriteBehavior());

		final PrimaryTypeAttribute submoduleRevision = findAttribute(submodule, "revision");
		assertInstanceOf(UnionType.class, submoduleRevision.getType());
		assertTrue(submoduleRevision.isKey());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, submoduleRevision.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, submoduleRevision.getWriteBehavior());

		final PrimaryTypeAttribute submoduleVersion = findAttribute(submodule, "version");
		assertNull(submoduleVersion);
		final PrimaryTypeAttribute submoduleRelease = findAttribute(submodule, "release");
		assertNull(submoduleRelease);
		final PrimaryTypeAttribute submoduleCorrection = findAttribute(submodule, "correction");
		assertNull(submoduleCorrection);

		final ModelInfo deviationModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "deviation", YANG_LIBRARY_OLD_XYZ_VERSION);
		assertModelDoesNotExist(deviationModelInfo);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification modulesStateSpec = modelService.getTypedAccess().getEModelSpecification(modulesStateModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertNotNull(modulesStateSpec);
		assertEquals(ReadBehavior.FROM_PERSISTENCE, modulesStateSpec.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, modulesStateSpec.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrModuleSetId = findAttribute(modulesStateSpec, "module-set-id");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleSetId.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleSetId.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification moduleSpec = modelService.getTypedAccess().getEModelSpecification(moduleModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertNotNull(moduleSpec);
		assertEquals(ReadBehavior.FROM_PERSISTENCE, moduleSpec.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, moduleSpec.getWriteBehavior());
		assertSize(2, moduleSpec.getKeyAttributeNames());
		assertEquals("name", moduleSpec.getKeyAttributeNames().get(0));
		assertEquals("revision", moduleSpec.getKeyAttributeNames().get(1));

		final PrimaryTypeAttributeSpecification attrModuleConformanceType = findAttribute(moduleSpec, "conformance-type");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleConformanceType.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleConformanceType.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrModuleSchema = findAttribute(moduleSpec, "schema");
		assertEquals(ReadBehavior.FROM_DELEGATE, attrModuleSchema.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleSchema.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrModuleFeature = findAttribute(moduleSpec, "feature");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleFeature.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleFeature.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrModuleName = findAttribute(moduleSpec, "name");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleName.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleName.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrModuleNamespace = findAttribute(moduleSpec, "namespace");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleNamespace.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleNamespace.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrModuleRevision = findAttribute(moduleSpec, "revision");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleRevision.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleRevision.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrModuleVersion = findAttribute(moduleSpec, "version");
		assertNull(attrModuleVersion);
		final PrimaryTypeAttributeSpecification attrModuleRelease = findAttribute(moduleSpec, "release");
		assertNull(attrModuleRelease);
		final PrimaryTypeAttributeSpecification attrModuleCorrection = findAttribute(moduleSpec, "correction");
		assertNull(attrModuleCorrection);

		final HierarchicalPrimaryTypeSpecification submoduleSpec = modelService.getTypedAccess().getEModelSpecification(submoduleModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertNotNull(submoduleSpec);
		assertEquals(ReadBehavior.FROM_PERSISTENCE, submoduleSpec.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, submoduleSpec.getWriteBehavior());
		assertSize(2, submoduleSpec.getKeyAttributeNames());
		assertEquals("name", submoduleSpec.getKeyAttributeNames().get(0));
		assertEquals("revision", submoduleSpec.getKeyAttributeNames().get(1));

		final PrimaryTypeAttributeSpecification attrSubmoduleSchema = findAttribute(submoduleSpec, "schema");
		assertEquals(ReadBehavior.FROM_DELEGATE, attrSubmoduleSchema.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrSubmoduleSchema.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrSubmoduleName = findAttribute(submoduleSpec, "name");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrSubmoduleName.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrSubmoduleName.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrSubmoduleRevision = findAttribute(submoduleSpec, "revision");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrSubmoduleRevision.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrSubmoduleRevision.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrSubmoduleVersion = findAttribute(submoduleSpec, "version");
		assertNull(attrSubmoduleVersion);
		final PrimaryTypeAttributeSpecification attrSubmoduleRelease = findAttribute(submoduleSpec, "release");
		assertNull(attrSubmoduleRelease);
		final PrimaryTypeAttributeSpecification attrSubmoduleCorrection = findAttribute(submoduleSpec, "correction");
		assertNull(attrSubmoduleCorrection);
	}

	@Test
	public void test___new_yang_library___no_eri_extensions() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(
				new File(TEST_SUB_DIR + "force-generate-ietf-yang-library"),
				new File(IETF_YANG_LIBRARY_NEW),
				new File(IETF_INET_TYPES),
				new File(IETF_YANG_TYPES),
				new File(IETF_DATASTORES)
				));

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo modulesStateModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "modules-state", YANG_LIBRARY_NEW_XYZ_VERSION);
		final PrimaryTypeDefinition modulesState = load(modulesStateModelInfo);
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, modulesState.getReadBehavior());
		assertEquals(LifeCycleType.CURRENT, modulesState.getLifeCycle());

		final PrimaryTypeAttribute moduleSetId = findAttribute(modulesState, "module-set-id");
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleSetId.getReadBehavior());

		final ModelInfo moduleModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "modules-state$$module", YANG_LIBRARY_NEW_XYZ_VERSION);
		final PrimaryTypeDefinition module = load(moduleModelInfo);
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, module.getReadBehavior());
		assertEquals(LifeCycleType.CURRENT, module.getLifeCycle());

		final PrimaryTypeAttribute moduleConformanceType = findAttribute(module, "conformance-type");
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleConformanceType.getReadBehavior());

		final PrimaryTypeAttribute moduleSchema = findAttribute(module, "schema");
		assertEquals(ReadBehaviorType.FROM_DELEGATE, moduleSchema.getReadBehavior());

		final PrimaryTypeAttribute moduleFeature = findAttribute(module, "feature");
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleFeature.getReadBehavior());

		final PrimaryTypeAttribute moduleName = findAttribute(module, "name");
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleName.getReadBehavior());

		final PrimaryTypeAttribute moduleNamespace = findAttribute(module, "namespace");
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleNamespace.getReadBehavior());

		final PrimaryTypeAttribute moduleRevision = findAttribute(module, "revision");
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleRevision.getReadBehavior());

		final PrimaryTypeAttribute moduleVersion = findAttribute(module, "version");
		assertNull(moduleVersion);
		final PrimaryTypeAttribute moduleRelease = findAttribute(module, "release");
		assertNull(moduleRelease);
		final PrimaryTypeAttribute moduleCorrection = findAttribute(module, "correction");
		assertNull(moduleCorrection);

		final ModelInfo submoduleModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "modules-state$$module$$submodule", YANG_LIBRARY_NEW_XYZ_VERSION);
		final PrimaryTypeDefinition submodule = load(submoduleModelInfo);
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, submodule.getReadBehavior());

		final PrimaryTypeAttribute submoduleSchema = findAttribute(submodule, "schema");
		assertEquals(ReadBehaviorType.FROM_DELEGATE, submoduleSchema.getReadBehavior());

		final PrimaryTypeAttribute submoduleName = findAttribute(submodule, "name");
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, submoduleName.getReadBehavior());

		final PrimaryTypeAttribute submoduleRevision = findAttribute(submodule, "revision");
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, submoduleRevision.getReadBehavior());

		final PrimaryTypeAttribute submoduleVersion = findAttribute(submodule, "version");
		assertNull(submoduleVersion);
		final PrimaryTypeAttribute submoduleRelease = findAttribute(submodule, "release");
		assertNull(submoduleRelease);
		final PrimaryTypeAttribute submoduleCorrection = findAttribute(submodule, "correction");
		assertNull(submoduleCorrection);

		final ModelInfo deviationModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "deviation", YANG_LIBRARY_NEW_XYZ_VERSION);
		assertModelDoesNotExist(deviationModelInfo);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification modulesStateSpec = modelService.getTypedAccess().getEModelSpecification(modulesStateModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, modulesStateSpec.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrModuleSetId = findAttribute(modulesStateSpec, "module-set-id");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleSetId.getReadBehavior());

		final HierarchicalPrimaryTypeSpecification moduleSpec = modelService.getTypedAccess().getEModelSpecification(moduleModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, moduleSpec.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrModuleConformanceType = findAttribute(moduleSpec, "conformance-type");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleConformanceType.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrModuleSchema = findAttribute(moduleSpec, "schema");
		assertEquals(ReadBehavior.FROM_DELEGATE, attrModuleSchema.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrModuleFeature = findAttribute(moduleSpec, "feature");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleFeature.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrModuleName = findAttribute(moduleSpec, "name");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleName.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrModuleNamespace = findAttribute(moduleSpec, "namespace");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleNamespace.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrModuleRevision = findAttribute(moduleSpec, "revision");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleRevision.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrModuleVersion = findAttribute(moduleSpec, "version");
		assertNull(attrModuleVersion);
		final PrimaryTypeAttributeSpecification attrModuleRelease = findAttribute(moduleSpec, "release");
		assertNull(attrModuleRelease);
		final PrimaryTypeAttributeSpecification attrModuleCorrection = findAttribute(moduleSpec, "correction");
		assertNull(attrModuleCorrection);

		final HierarchicalPrimaryTypeSpecification submoduleSpec = modelService.getTypedAccess().getEModelSpecification(submoduleModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertEquals(ReadBehavior.FROM_PERSISTENCE, submoduleSpec.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrSubmoduleSchema = findAttribute(submoduleSpec, "schema");
		assertEquals(ReadBehavior.FROM_DELEGATE, attrSubmoduleSchema.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrSubmoduleName = findAttribute(submoduleSpec, "name");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrSubmoduleName.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrSubmoduleRevision = findAttribute(submoduleSpec, "revision");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrSubmoduleRevision.getReadBehavior());

		final PrimaryTypeAttributeSpecification attrSubmoduleVersion = findAttribute(submoduleSpec, "version");
		assertNull(attrSubmoduleVersion);
		final PrimaryTypeAttributeSpecification attrSubmoduleRelease = findAttribute(submoduleSpec, "release");
		assertNull(attrSubmoduleRelease);
		final PrimaryTypeAttributeSpecification attrSubmoduleCorrection = findAttribute(submoduleSpec, "correction");
		assertNull(attrSubmoduleCorrection);
	}

	@Test
	public void test___new_yang_library___with_eri_extensions() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());

		final TransformerContext context = createContextWith3ppModules(Arrays.asList(
				new File(TEST_SUB_DIR + "force-generate-ietf-yang-library"),
				new File(IETF_YANG_LIBRARY_NEW),
				new File(IETF_INET_TYPES),
				new File(IETF_YANG_TYPES),
				new File(IETF_DATASTORES),
				new File(ERI_YANG_LIBRARY_EXT)
				));

		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo moduleExtModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "modules-state$$module");
		final PrimaryTypeExtensionDefinition module = load(moduleExtModelInfo);

		final PrimaryTypeAttribute moduleVersion = findAddedAttribute(module, "version");
		assertInstanceOf(LongType.class, moduleVersion.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleVersion.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleVersion.getWriteBehavior());

		final PrimaryTypeAttribute moduleRelease = findAddedAttribute(module, "release");
		assertInstanceOf(LongType.class, moduleRelease.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleRelease.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleRelease.getWriteBehavior());

		final PrimaryTypeAttribute moduleCorrection = findAddedAttribute(module, "correction");
		assertInstanceOf(LongType.class, moduleCorrection.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, moduleCorrection.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, moduleCorrection.getWriteBehavior());

		final ModelInfo submoduleExtModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "modules-state$$module$$submodule");
		final PrimaryTypeExtensionDefinition submodule = load(submoduleExtModelInfo);

		final PrimaryTypeAttribute submoduleVersion = findAddedAttribute(submodule, "version");
		assertInstanceOf(LongType.class, submoduleVersion.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, submoduleVersion.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, submoduleVersion.getWriteBehavior());

		final PrimaryTypeAttribute submoduleRelease = findAddedAttribute(submodule, "release");
		assertInstanceOf(LongType.class, submoduleRelease.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, submoduleRelease.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, submoduleRelease.getWriteBehavior());

		final PrimaryTypeAttribute submoduleCorrection = findAddedAttribute(submodule, "correction");
		assertInstanceOf(LongType.class, submoduleCorrection.getType());
		assertEquals(ReadBehaviorType.FROM_PERSISTENCE, submoduleCorrection.getReadBehavior());
		assertEquals(WriteBehaviorType.NOT_ALLOWED, submoduleCorrection.getWriteBehavior());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		final ModelInfo moduleModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "modules-state$$module", YANG_LIBRARY_NEW_XYZ_VERSION);
		final ModelInfo submoduleModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(IetfYangLibraryParser.IETF_YANG_LIBRARY_NAMESPACE, "modules-state$$module$$submodule", YANG_LIBRARY_NEW_XYZ_VERSION);

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification moduleSpec = modelService.getTypedAccess().getEModelSpecification(moduleModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attrModuleVersion = findAttribute(moduleSpec, "version");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleVersion.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleVersion.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrModuleRelease = findAttribute(moduleSpec, "release");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleRelease.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleRelease.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrModuleCorrection = findAttribute(moduleSpec, "correction");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrModuleCorrection.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrModuleCorrection.getWriteBehavior());

		final HierarchicalPrimaryTypeSpecification submoduleSpec = modelService.getTypedAccess().getEModelSpecification(submoduleModelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attrSubmoduleVersion = findAttribute(submoduleSpec, "version");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrSubmoduleVersion.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrSubmoduleVersion.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrSubmoduleRelease = findAttribute(submoduleSpec, "release");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrSubmoduleRelease.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrSubmoduleRelease.getWriteBehavior());

		final PrimaryTypeAttributeSpecification attrSubmoduleCorrection = findAttribute(submoduleSpec, "correction");
		assertEquals(ReadBehavior.FROM_PERSISTENCE, attrSubmoduleCorrection.getReadBehavior());
		assertEquals(WriteBehavior.NOT_ALLOWED, attrSubmoduleCorrection.getWriteBehavior());
	}
}
