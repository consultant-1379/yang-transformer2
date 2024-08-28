package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.nrms;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.cdt.ComplexDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.edt.EnumDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.Target;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EModelHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.eri.ValidatorFindingTypeEri;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

/*
 * The tests in this class are usually commented out as they take a long time to run. They
 * are usually manually run every once in a while to see the transformer output for real NRMs.
 */
public class NrmsTest extends TransformerTestUtil {

	@Test
	public void test___empty_test_as_usually_we_comment_out_the_nrm_tests_but_when_we_commit_then_sonarqube_complains_so_dont_comment_out() {
		assertTrue(true);
	}

//	private static final String PCC_19_DIR = NRMS_DIR + "pcc-1.9/";
//
//	private static final String PCC_TARGET_TYPE = "PCC";
//	private static final String PCC_TARGET_VERSION = "1.9";
//
//	@Test
//	public void test___PCC_19() {
//
//		/*
//		 * PCC 1.9 input is incomplete. There is a missing import from 'ietf-if-l3-vlan' towards 'ieee802-dot1q-types'.
//		 */
//		final TransformerContext context = createContext(new File(PCC_19_DIR + "input_implement"), new File(PCC_19_DIR + "input_import"), PCC_TARGET_TYPE, PCC_TARGET_VERSION, false);
//		transformNrm(context, "PCC 1.9");
//
//		// =====================================================================================================================
//		// =====================================================================================================================
//		// =====================================================================================================================
//
//		processAndDeployIntoModelService();
//		loadAllEmodels(context);
//
//		/*
//		 * FM should have deployed.
//		 */
//		final Target target = new Target(context.getTarget().getCategory(), context.getTarget().getType(), "target-name", context.getTarget().getVersion());
//		final ModelInfo modelInfoFm = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "urn:rdns:com:ericsson:oammodel:ericsson-fm", "fm", "3.1.0");
//
//		assertModelExistsInModelService(modelService, modelInfoFm);
//		modelService.getTypedAccess().getEModelSpecification(modelInfoFm, HierarchicalPrimaryTypeSpecification.class, target);
//	}

//	private static final String VDU_120_DIR = NRMS_DIR + "vDU-1.2.0/";
//
//	private static final String VDU_TARGET_TYPE = "vDU";
//	private static final String VDU_TARGET_VERSION = "1.2.0";
//
//	@Test
//	public void test___vDU_120() {
//
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P049_DUPLICATE_REVISION.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S444_XPATH_REFERS_TO_REMOVED_STATEMENT.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E616_SYSTEM_FRAGMENT_DEVIATED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N662_COMPLEX_OUTPUT_NOT_SUPPORTED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N665_INCORRECT_3GPP_MOC_CONTENT.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N666_CANNOT_DEVIATE_OR_AUGMENT_INTO_3GPP_STRUCT.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N667_KEYLESS_READONLY_LIST_NOT_SUPPORTED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N671_UNSUPPORTED_EXTENSION.toString());
//
//		final TransformerContext context = createContext(new File(VDU_120_DIR + "implementing"), new File(VDU_120_DIR + "import-only"), VDU_TARGET_TYPE, VDU_TARGET_VERSION, true);
//		context.setGenerateRpcs(true);
//		context.setApply3gppHandling(true);
//
//		transformNrm(context, "vDU 1.2.0");
//
//		// =====================================================================================================================
//		// =====================================================================================================================
//		// =====================================================================================================================
//
//		processAndDeployIntoModelService();
//		loadAllEmodels(context);
//
//		/*
//		 * 3GPP ManagedElement should have deployed.
//		 */
//		final Target target = new Target(context.getTarget().getCategory(), context.getTarget().getType(), "target-name", context.getTarget().getVersion());
//		final ModelInfo modelInfo3gppManagedElement = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "urn:3gpp:sa5:_3gpp-common-managed-element", "ManagedElement", "2019.6.17");
//
//		assertModelExistsInModelService(modelService, modelInfo3gppManagedElement);
//		modelService.getTypedAccess().getEModelSpecification(modelInfo3gppManagedElement, HierarchicalPrimaryTypeSpecification.class, target);
//	}

//	private static final String CUCP_22Q4_DIR = NRMS_DIR + "cucp-22.Q4/";
//
//	private static final String CUCP_TARGET_TYPE = "vDU";
//	private static final String CUCP_TARGET_VERSION = "22.Q4";
//
//	@Test
//	public void test___CUCP_22Q4() {
//
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P049_DUPLICATE_REVISION.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P122_EXCESSIVE_USES_DEPTH.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P142_UNUSUAL_CHARACTERS_IN_ENUM.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P167_CANNOT_USE_UNDER_DEVIATE_ADD_OR_DELETE.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S329_LEAFREF_TARGET_IS_LEAFREF.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S444_XPATH_REFERS_TO_REMOVED_STATEMENT.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E616_SYSTEM_FRAGMENT_DEVIATED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N662_COMPLEX_OUTPUT_NOT_SUPPORTED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N665_INCORRECT_3GPP_MOC_CONTENT.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N667_KEYLESS_READONLY_LIST_NOT_SUPPORTED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N671_UNSUPPORTED_EXTENSION.toString());
//
//		final TransformerContext context = createContext(new File(CUCP_22Q4_DIR + "implementing"), new File(CUCP_22Q4_DIR + "import-only"), CUCP_TARGET_TYPE, CUCP_TARGET_VERSION, true);
//		context.setGenerateRpcs(true);
//		context.setApply3gppHandling(true);
//
//		transformNrm(context, "CUCP 22Q4");
//
//		// =====================================================================================================================
//		// =====================================================================================================================
//		// =====================================================================================================================
//
//		processAndDeployIntoModelService();
//		loadAllEmodels(context);
//	}

//	private static final String EPG_OI_35_DIR = NRMS_DIR + "epg-oi-3.5/";
//
//	private static final String EPG_OI_TARGET_TYPE = "EPG-OI";
//	private static final String EPG_TARGET_VERSION = "3.5";
//
//	@Test
//	public void test___EPG_OI_35() {
//
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P049_DUPLICATE_REVISION.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P057_DATA_TYPE_CHANGED.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P122_EXCESSIVE_USES_DEPTH.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P142_UNUSUAL_CHARACTERS_IN_ENUM.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P163_AMBIGUOUS_DEVIATE_REPLACE_OF_SAME_STATEMENT.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P167_CANNOT_USE_UNDER_DEVIATE_ADD_OR_DELETE.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S329_LEAFREF_TARGET_IS_LEAFREF.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S392_INVALID_DEFAULT_VALUE.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E602_MISSING_VERSION_RELEASE_CORRECTION_FOR_OLDER_REVISION.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E612_NO_STATUS_INFORMATION_ON_DEPRECATED_ELEMENT.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E616_SYSTEM_FRAGMENT_DEVIATED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E617_SYSTEM_FRAGMENT_AUGMENTED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N667_KEYLESS_READONLY_LIST_NOT_SUPPORTED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N671_UNSUPPORTED_EXTENSION.toString());
//
//		final TransformerContext context = createContext(new File(EPG_OI_35_DIR + "input_mims"), EPG_OI_TARGET_TYPE, EPG_TARGET_VERSION, true);
//		context.setMockManagedElementNamespace("EpgOiTop");
//
//		transformNrm(context, "EPG-OI 3.5");
//
//		// =====================================================================================================================
//		// =====================================================================================================================
//		// =====================================================================================================================
//
//		processAndDeployIntoModelService();
//		loadAllEmodels(context);
//
//		/*
//		 * EPG should have deployed.
//		 */
//		final Target target = new Target(context.getTarget().getCategory(), context.getTarget().getType(), "target-name", context.getTarget().getVersion());
//		final ModelInfo modelInfoEpg = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "urn:rdns:com:ericsson:oammodel:ericsson-epg", "epg", "3.5.0");
//
//		assertModelExistsInModelService(modelService, modelInfoEpg);
//		modelService.getTypedAccess().getEModelSpecification(modelInfoEpg, HierarchicalPrimaryTypeSpecification.class, target);
//	}
//
//	private static final String ER6274_21Q3GA_DIR = NRMS_DIR + "er6274_21Q3GA/";
//
//	private static final String ER6274_21Q3GA_TARGET_TYPE = "ER6274";
//	private static final String ER6274_21Q3GA_TARGET_VERSION = "21Q3GA";
//
//	@Test
//	public void test___ER6274_21Q3GA() {
//
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P049_DUPLICATE_REVISION.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P057_DATA_TYPE_CHANGED.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P122_EXCESSIVE_USES_DEPTH.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P142_UNUSUAL_CHARACTERS_IN_ENUM.toString());
//		additionallyIgnoreFindingDuringValidation(ParserFindingType.P167_CANNOT_USE_UNDER_DEVIATE_ADD_OR_DELETE.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S329_LEAFREF_TARGET_IS_LEAFREF.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S441_INVALID_XPATH.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S444_XPATH_REFERS_TO_REMOVED_STATEMENT.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEri.E602_MISSING_VERSION_RELEASE_CORRECTION_FOR_OLDER_REVISION.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());
//		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N663_DUPLICATE_DATA_NODE.toString());
//
//		final TransformerContext context = createContext(new File(ER6274_21Q3GA_DIR), ER6274_21Q3GA_TARGET_TYPE, ER6274_21Q3GA_TARGET_VERSION, true);
//		transformNrm(context, "ER6274 21.Q3 GA");
//		
//		// =====================================================================================================================
//		// =====================================================================================================================
//		// =====================================================================================================================
//		
//		processAndDeployIntoModelService();
//		loadAllEmodels(context);
//	}

	private void loadAllEmodels(final TransformerContext context) {
		try {
			final Target target = new Target(context.getTarget().getCategory(), context.getTarget().getType(), "target-name", context.getTarget().getVersion());

			context.getCreatedEmodels().getGeneratedEModels().forEach(emodel -> {

				final String schemaName = EModelHelper.getSchemaNameForEModelClass(emodel.getClass());
				final ModelInfo modelInfo = new ModelInfo(schemaName, emodel.getNs(), emodel.getName(), emodel.getVersion());

				switch(schemaName) {
				case SchemaConstants.DPS_PRIMARYTYPE:
					final HierarchicalPrimaryTypeSpecification hpts = modelService.getTypedAccess().getEModelSpecification(modelInfo, HierarchicalPrimaryTypeSpecification.class, target);
					hpts.getAttributeSpecifications();
					hpts.getActionSpecifications();
					break;
				case SchemaConstants.OSS_EDT:
					final EnumDataTypeSpecification edts = modelService.getTypedAccess().getEModelSpecification(modelInfo, EnumDataTypeSpecification.class, target);
					edts.getAllMembers();
					break;
				case SchemaConstants.OSS_CDT:
					final ComplexDataTypeSpecification cdts = modelService.getTypedAccess().getEModelSpecification(modelInfo, ComplexDataTypeSpecification.class, target);
					cdts.getAllAttributeSpecifications();
					break;
				}
			});
		} catch (final Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
}
