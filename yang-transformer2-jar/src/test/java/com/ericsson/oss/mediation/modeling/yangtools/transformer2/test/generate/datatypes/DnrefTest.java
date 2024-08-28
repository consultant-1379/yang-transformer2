package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.ReferenceEndpointConstraint;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MoRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.eri.ValidatorFindingTypeEri;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class DnrefTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-datatypes/dnref/";

	private static final String MODULE_NS = "urn#test#dnref-simple";
	private static final String MODULE_XYZ_VERSION = "2023.4.18";

	@Test
	public void test___dnref_type___simple() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E644_INCORRECT_DNREF.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E645_SHOULD_USE_3GPP_DISTINGUISHED_NAME.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E647_NON_ERICSSON_MODULE_IMPORTS_ERICSSON_MODULE.toString());

		final TransformerContext context = createContextWithEriExtensions(Arrays.asList(new File(TEST_SUB_DIR + "dnref-simple.yang"), new File(ERI_YANG_TYPES), new File(IETF_YANG_TYPES), new File(IETF_INET_TYPES)));
		context.setApply3gppHandling(true);
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo mocAmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocA", MODULE_XYZ_VERSION);
		final ModelInfo mocBmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocB", MODULE_XYZ_VERSION);
		final ModelInfo mocCmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocC", MODULE_XYZ_VERSION);
		final ModelInfo mocFmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocF", MODULE_XYZ_VERSION);
		final ModelInfo mocGmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocG", MODULE_XYZ_VERSION);
		final ModelInfo mocHmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocH", MODULE_XYZ_VERSION);

		final ModelInfo notAmocModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "not-a-moc", MODULE_XYZ_VERSION);

		final PrimaryTypeDefinition mocA = load(mocAmodelInfo);

		final PrimaryTypeAttribute moRef11 = findAttribute(mocA, "moRef11");
		assertInstanceOf(StringType.class, moRef11.getType());

		final PrimaryTypeAttribute moRef12 = findAttribute(mocA, "moRef12");
		assertMoRefToAnyManagedObject(moRef12);

		final PrimaryTypeAttribute moRef13 = findAttribute(mocA, "moRef13");
		assertInstanceOf(MoRefType.class, moRef13.getType());
		assertEquals(mocBmodelInfo.toImpliedUrn(), ((MoRefType) moRef13.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef13.getType()).getMoTypeConstraint());

		final PrimaryTypeAttribute moRef14 = findAttribute(mocA, "moRef14");
		assertInstanceOf(MoRefType.class, moRef14.getType());
		assertEquals(Constants.ANY_MANAGED_OBJECT.toImpliedUrn(), ((MoRefType) moRef14.getType()).getModelUrn());
		assertSize(2, ((MoRefType) moRef14.getType()).getMoTypeConstraint());
		assertEquals(mocBmodelInfo.toImpliedUrn(), ((MoRefType) moRef14.getType()).getMoTypeConstraint().get(0).getPrimaryTypeUrn());
		assertEquals(mocCmodelInfo.toImpliedUrn(), ((MoRefType) moRef14.getType()).getMoTypeConstraint().get(1).getPrimaryTypeUrn());

		final PrimaryTypeAttribute moRef15 = findAttribute(mocA, "moRef15");
		assertMoRefToAnyManagedObject(moRef15);

		final PrimaryTypeAttribute moRef16 = findAttribute(mocA, "moRef16");
		assertInstanceOf(MoRefType.class, moRef16.getType());
		assertEquals(mocBmodelInfo.toImpliedUrn(), ((MoRefType) moRef16.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef16.getType()).getMoTypeConstraint());

		final PrimaryTypeAttribute moRef17 = findAttribute(mocA, "moRef17");
		assertInstanceOf(MoRefType.class, moRef17.getType());
		assertEquals(mocFmodelInfo.toImpliedUrn(), ((MoRefType) moRef17.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef17.getType()).getMoTypeConstraint());

		final PrimaryTypeAttribute moRef18 = findAttribute(mocA, "moRef18");
		assertInstanceOf(MoRefType.class, moRef18.getType());
		assertEquals(mocFmodelInfo.toImpliedUrn(), ((MoRefType) moRef18.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef18.getType()).getMoTypeConstraint());

		final PrimaryTypeAttribute moRef19 = findAttribute(mocA, "moRef19");
		assertMoRefToAnyManagedObject(moRef19);

		final PrimaryTypeDefinition mocG = load(mocGmodelInfo);

		final PrimaryTypeAttribute moRef21 = findAttribute(mocG, "moRef21");
		assertInstanceOf(MoRefType.class, moRef21.getType());
		assertEquals(mocFmodelInfo.toImpliedUrn(), ((MoRefType) moRef21.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef21.getType()).getMoTypeConstraint());

		final PrimaryTypeAttribute moRef22 = findAttribute(mocG, "moRef22");
		assertInstanceOf(MoRefType.class, moRef22.getType());
		assertEquals(mocGmodelInfo.toImpliedUrn(), ((MoRefType) moRef22.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef22.getType()).getMoTypeConstraint());

		final PrimaryTypeDefinition mocH = load(mocHmodelInfo);

		final PrimaryTypeAttribute moRef31 = findAttribute(mocH, "moRef31");
		assertMoRefToAnyManagedObject(moRef31);

		final PrimaryTypeAttribute moRef32 = findAttribute(mocH, "moRef32");
		assertMoRefToAnyManagedObject(moRef32);

		final PrimaryTypeAttribute moRef33 = findAttribute(mocH, "moRef33");
		assertMoRefToAnyManagedObject(moRef33);

		final PrimaryTypeAttribute moRef34 = findAttribute(mocH, "moRef34");
		assertMoRefToAnyManagedObject(moRef34);

		final PrimaryTypeAttribute moRef35 = findAttribute(mocH, "moRef35");
		assertMoRefToAnyManagedObject(moRef35);

		final PrimaryTypeAttribute moRef36 = findAttribute(mocH, "moRef36");
		assertMoRefToAnyManagedObject(moRef36);

		final PrimaryTypeDefinition notAmoc = load(notAmocModelInfo);

		final PrimaryTypeAttribute moRef51 = findAttribute(notAmoc, "moRef51");
		assertMoRefToAnyManagedObject(moRef51);

		final PrimaryTypeAttribute moRef52 = findAttribute(notAmoc, "moRef52");
		assertMoRefToAnyManagedObject(moRef52);

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification mocAspec = modelService.getTypedAccess().getEModelSpecification(mocAmodelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(mocAspec, "moRef11");
		assertEquals(DataType.STRING, attr11.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(mocAspec, "moRef12");
		assertEquals(DataType.MO_REF, attr12.getDataTypeSpecification().getDataType());
		assertTrue(attr12.getDataTypeSpecification().isReferenceDataType());
		assertEquals(Constants.ANY_MANAGED_OBJECT, attr12.getDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(mocAspec, "moRef13");
		assertEquals(DataType.MO_REF, attr13.getDataTypeSpecification().getDataType());
		assertTrue(attr13.getDataTypeSpecification().isReferenceDataType());
		assertEquals(mocBmodelInfo, attr13.getDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(mocAspec, "moRef14");
		assertEquals(DataType.MO_REF, attr14.getDataTypeSpecification().getDataType());
		assertTrue(attr14.getDataTypeSpecification().isReferenceDataType());
		assertEquals(Constants.ANY_MANAGED_OBJECT, attr14.getDataTypeSpecification().getReferencedDataType());
		assertSize(1, attr14.getDataTypeSpecification().getConstraints());
		assertTrue(((ReferenceEndpointConstraint) attr14.getDataTypeSpecification().getConstraints().iterator().next()).getPrimaryTypeUrns().contains(mocBmodelInfo.toImpliedUrn()));
		assertTrue(((ReferenceEndpointConstraint) attr14.getDataTypeSpecification().getConstraints().iterator().next()).getPrimaryTypeUrns().contains(mocCmodelInfo.toImpliedUrn()));

		final PrimaryTypeAttributeSpecification attr19 = findAttribute(mocAspec, "moRef19");
		assertEquals(DataType.MO_REF, attr19.getDataTypeSpecification().getDataType());
		assertTrue(attr19.getDataTypeSpecification().isReferenceDataType());
		assertEquals(Constants.ANY_MANAGED_OBJECT, attr19.getDataTypeSpecification().getReferencedDataType());
	}

	@Test
	public void test___dnref_type___simple___with_ext() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E644_INCORRECT_DNREF.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E645_SHOULD_USE_3GPP_DISTINGUISHED_NAME.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E647_NON_ERICSSON_MODULE_IMPORTS_ERICSSON_MODULE.toString());

		final TransformerContext context = createContextWithEriExtensions(Arrays.asList(new File(TEST_SUB_DIR + "dnref-simple.yang"), new File(TEST_SUB_DIR + "dnref-simple-ext.yang"), new File(ERI_YANG_TYPES), new File(IETF_YANG_TYPES), new File(IETF_INET_TYPES)));
		context.setApply3gppHandling(true);
		YangTransformer2.transform(context);

		// -----------------------------------------------------

		final ModelInfo mocAmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocA", MODULE_XYZ_VERSION);
		final ModelInfo mocBmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocB", MODULE_XYZ_VERSION);
		final ModelInfo mocCmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS, "MocC", MODULE_XYZ_VERSION);

		final PrimaryTypeDefinition mocA = load(mocAmodelInfo);

		final PrimaryTypeAttribute moRef11 = findAttribute(mocA, "moRef11");
		assertInstanceOf(StringType.class, moRef11.getType());

		final PrimaryTypeAttribute moRef12 = findAttribute(mocA, "moRef12");
		assertMoRefToAnyManagedObject(moRef12);

		final PrimaryTypeAttribute moRef13 = findAttribute(mocA, "moRef13");
		assertInstanceOf(MoRefType.class, moRef13.getType());
		assertEquals(mocBmodelInfo.toImpliedUrn(), ((MoRefType) moRef13.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef13.getType()).getMoTypeConstraint());

		// -----------------------------------------------------

		final ModelInfo mocAextModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "MocA");
		final PrimaryTypeExtensionDefinition mocAext = load(mocAextModelInfo);

		final PrimaryTypeAttribute moRef11ext = findReplacedAttribute(mocAext, "moRef11");
		assertMoRefToAnyManagedObject(moRef11ext);

		final PrimaryTypeAttribute moRef12ext = findReplacedAttribute(mocAext, "moRef12");
		assertInstanceOf(StringType.class, moRef12ext.getType());

		final PrimaryTypeAttribute moRef13ext = findReplacedAttribute(mocAext, "moRef13");
		assertInstanceOf(MoRefType.class, moRef13ext.getType());
		assertEquals(mocCmodelInfo.toImpliedUrn(), ((MoRefType) moRef13ext.getType()).getModelUrn());

		final PrimaryTypeAttribute moRef14ext = findReplacedAttribute(mocAext, "moRef14");
		assertInstanceOf(MoRefType.class, moRef14ext.getType());
		assertEquals(mocBmodelInfo.toImpliedUrn(), ((MoRefType) moRef14ext.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef14ext.getType()).getMoTypeConstraint());

		final ModelInfo mocBextModelInfo = PrimaryTypeExtensionGenerator.getModelInfoForPrimaryTypeExtensionDefinition(DEFAULT_TEST_TARGET, MODULE_NS, "MocB");
		final PrimaryTypeExtensionDefinition mocBext = load(mocBextModelInfo);

		final PrimaryTypeAttribute moRef41ext = findAddedAttribute(mocBext, "moRef41");
		assertInstanceOf(MoRefType.class, moRef41ext.getType());
		assertEquals(mocCmodelInfo.toImpliedUrn(), ((MoRefType) moRef41ext.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef41ext.getType()).getMoTypeConstraint());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final HierarchicalPrimaryTypeSpecification mocAspec = modelService.getTypedAccess().getEModelSpecification(mocAmodelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(mocAspec, "moRef11");
		assertEquals(DataType.MO_REF, attr11.getDataTypeSpecification().getDataType());
		assertTrue(attr11.getDataTypeSpecification().isReferenceDataType());
		assertEquals(Constants.ANY_MANAGED_OBJECT, attr11.getDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr12 = findAttribute(mocAspec, "moRef12");
		assertEquals(DataType.STRING, attr12.getDataTypeSpecification().getDataType());

		final PrimaryTypeAttributeSpecification attr13 = findAttribute(mocAspec, "moRef13");
		assertEquals(DataType.MO_REF, attr13.getDataTypeSpecification().getDataType());
		assertTrue(attr13.getDataTypeSpecification().isReferenceDataType());
		assertEquals(mocCmodelInfo, attr13.getDataTypeSpecification().getReferencedDataType());

		final PrimaryTypeAttributeSpecification attr14 = findAttribute(mocAspec, "moRef14");
		assertEquals(DataType.MO_REF, attr14.getDataTypeSpecification().getDataType());
		assertTrue(attr14.getDataTypeSpecification().isReferenceDataType());
		assertEquals(mocBmodelInfo, attr14.getDataTypeSpecification().getReferencedDataType());
	}

	private void assertMoRefToAnyManagedObject(final PrimaryTypeAttribute moRef) {
		assertInstanceOf(MoRefType.class, moRef.getType());
		assertEquals(Constants.ANY_MANAGED_OBJECT.toImpliedUrn(), ((MoRefType) moRef.getType()).getModelUrn());
		assertSize(0, ((MoRefType) moRef.getType()).getMoTypeConstraint());
	}
}
