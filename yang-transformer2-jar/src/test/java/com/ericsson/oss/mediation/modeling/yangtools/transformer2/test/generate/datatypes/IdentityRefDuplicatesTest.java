package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.util.EnumMemberEditor;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataType;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.edt.EnumDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EnumRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.IdentityRefGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public class IdentityRefDuplicatesTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-datatypes/";

	private static final String MODULE_NS_ONE = "urn,test,identityref-duplicates-one";
	private static final String MODULE_NAME_ONE = "identityref-duplicates-one";
	private static final String MODULE_PREFIX_ONE = "one";
	private static final String MODULE_REVISION_ONE = "2021-07-30";
	private static final String MODULE_XYZ_VERSION_ONE = "2021.7.30";

	private static final String MODULE_NS_TWO = "urn!test!identityref-duplicates-two";
	private static final String MODULE_NAME_TWO = "identityref-duplicates-two";
	private static final String MODULE_PREFIX_TWO = "two";
	private static final String MODULE_REVISION_TWO = "2021-03-09";
	private static final String MODULE_XYZ_VERSION_TWO = "2021.3.9";

	private static final String MODULE_NS_THREE = "urn*test*identityref-duplicates-three";
	private static final String MODULE_NAME_THREE = "identityref-duplicates-three";
	private static final String MODULE_PREFIX_THREE = "three";
	private static final String MODULE_REVISION_THREE = "2021-02-31";
	private static final String MODULE_XYZ_VERSION_THREE = "2021.2.31";

	@Test
	public void test___identity_ref_duplicates() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "identityref-duplicates"));
		YangTransformer2.transform(context);

		assertEModelCreatedCount(1, 0, 1, 1, 3, 0);

		// -----------------------------------------------------

		final ModelInfo idBaseModelInfo = IdentityRefGenerator.getModelInfoForIdentityWithSingleBase(MODULE_NS_ONE, "base", MODULE_XYZ_VERSION_ONE);
		final EnumDataTypeDefinition idBaseEdt = load(idBaseModelInfo);

		assertSize(4, idBaseEdt.getMember());
		assertEquals(MODULE_NS_ONE, findAndAssertEnumMember(idBaseEdt, "base").getNamespace());
		assertEquals(MODULE_NS_ONE, findAndAssertEnumMember(idBaseEdt, "red").getNamespace());
		assertEquals(MODULE_NS_ONE, findAndAssertEnumMember(idBaseEdt, "yellow").getNamespace());
		assertEquals(MODULE_NS_ONE, findAndAssertEnumMember(idBaseEdt, "green").getNamespace());

		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(idBaseEdt, "base"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(idBaseEdt, "red"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(idBaseEdt, "yellow"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_ONE, findEnumMember(idBaseEdt, "green"));

		final ModelInfo idBaseExtModelInfo = IdentityRefGenerator.getModelInfoForExtension(DEFAULT_TEST_TARGET, idBaseModelInfo.getNamespace(), idBaseModelInfo.getName());
		final EnumDataTypeExtensionDefinition idBaseEdtExt = load(idBaseExtModelInfo);
		assertEquals(idBaseModelInfo.toImpliedUrn(), idBaseEdtExt.getExtendedModelElement().get(0).getUrn());
		assertRequiresTargetType(context, null, idBaseEdtExt.getRequiresTargetType());

		assertSize(6, idBaseEdtExt.getEnumDataTypeExtension().getMember());
		findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_TWO, "red");
		findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_TWO, "yellow");
		findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_TWO, "blue");
		findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_THREE, "red");
		findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_THREE, "blue");
		findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_THREE, "pink");
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_TWO, "red"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_TWO, "yellow"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_TWO, findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_TWO, "blue"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_THREE, findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_THREE, "red"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_THREE, findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_THREE, "blue"));
		assertHasMeta(Constants.META_ORIG_MODULE_PREFIX, MODULE_PREFIX_THREE, findAndAssertAddedEnumMember(idBaseEdtExt, MODULE_NS_THREE, "pink"));

		// -----------------------------------------------------

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_ONE, "cont1", MODULE_XYZ_VERSION_ONE);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);

		final PrimaryTypeAttribute leaf11 = findAttribute(cont1, "leaf11");
		assertInstanceOf(EnumRefType.class, leaf11.getType());
		assertEquals(idBaseModelInfo.toImpliedUrn(), ((EnumRefType) leaf11.getType()).getModelUrn());

		// -----------------------------------------------------

		final ModelInfo yangModuleOne = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_ONE, MODULE_NAME_ONE, MODULE_REVISION_ONE);
		assertModelExists(yangModuleOne);

		final ModelInfo yangModuleTwo = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_TWO, MODULE_NAME_TWO, MODULE_REVISION_TWO);
		assertModelExists(yangModuleTwo);

		final ModelInfo yangModuleThree = YangNameVersionUtil.getNetYangModelInfoForYam(MODULE_NS_THREE, MODULE_NAME_THREE, MODULE_REVISION_THREE);
		assertModelExists(yangModuleThree);

		// -----------------------------------------------------

		final ManagementInformationModelInformation mimInfo = loadCfmMimInfo(context, null);
		assertSize(3, mimInfo.getSupportedMims().getMimMappedTo());

		assertHasSupportedMim(mimInfo, MODULE_NS_ONE, MODULE_XYZ_VERSION_ONE, yangModuleOne.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_TWO, MODULE_XYZ_VERSION_TWO, yangModuleTwo.toUrn());
		assertHasSupportedMim(mimInfo, MODULE_NS_THREE, MODULE_XYZ_VERSION_THREE, yangModuleThree.toUrn());

		// =====================================================================================================================
		// =====================================================================================================================
		// =====================================================================================================================

		processAndDeployIntoModelService();

		final EnumDataTypeSpecification idBaseSpec = modelService.getTypedAccess().getEModelSpecification(idBaseModelInfo, EnumDataTypeSpecification.class, getModelServiceTarget(context, null));
		assertSize(10, idBaseSpec.getAllMembers());
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_ONE, "base") != null);
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_ONE, EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_ONE, "red")));
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_ONE, EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_ONE, "yellow")));
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_ONE, "green") != null);
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_TWO, EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_TWO, "red")));
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_TWO, EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_TWO, "yellow")));
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_TWO, EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_TWO, "blue")));
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_THREE, EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_THREE, "red")));
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_THREE, EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_THREE, "blue")));
		assertNotNull(findEnumMember(idBaseSpec, MODULE_NS_THREE, "pink") != null);

		// --------------------------------------

		final HierarchicalPrimaryTypeSpecification cont1spec = modelService.getTypedAccess().getEModelSpecification(cont1modelInfo, HierarchicalPrimaryTypeSpecification.class, getModelServiceTarget(context, null));
		assertNotNull(cont1spec);

		final PrimaryTypeAttributeSpecification attr11 = findAttribute(cont1spec, "leaf11");
		assertNotNull(attr11);
		assertEquals(DataType.ENUM_REF, attr11.getDataTypeSpecification().getDataType()); 
		assertEquals(idBaseModelInfo, attr11.getDataTypeSpecification().getReferencedDataType()); 

		/*
		 * 'red' is a duplicate
		 */
		assertInvalidValue(attr11, "red");
		assertValidValue(attr11, EnumMemberEditor.joinMemberNamespaceAndName(MODULE_NS_ONE, "red"));

		assertValidValue(attr11, EnumMemberEditor.joinMemberNamespaceAndName(idBaseModelInfo, MODULE_NS_TWO, "red"));
		assertValidValue(attr11, EnumMemberEditor.joinMemberNamespaceAndName(idBaseModelInfo, MODULE_NS_THREE, "red"));
	}
}
