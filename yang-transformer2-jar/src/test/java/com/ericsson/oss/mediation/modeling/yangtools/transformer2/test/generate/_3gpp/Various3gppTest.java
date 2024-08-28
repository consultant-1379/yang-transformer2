package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate._3gpp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import org.junit.Test;

import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MoRefType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class Various3gppTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "generate-3gpp/various-3gpp/";

	private static final String MODULE_NS_VAR = "urn:test:various-3gpp";
	private static final String MODULE_XYZ_VERSION_VAR = "2019.1.8";

	private static final String MODULE_NS_3GPP = "urn:3gpp:threegpp-module";
	private static final String MODULE_XYZ_VERSION_3GPP = "2020.12.8";

	private static final String MODULE_NS_IETF = "urn:ietf:ietf-module";
	private static final String MODULE_XYZ_VERSION_IETF = "2019.3.8";

	private static final String MODULE_NS_DN_USAGE = "urn:test:dn-usage";
	private static final String MODULE_XYZ_VERSION_DN_USAGE = "2023.6.15";

	@Test
	public void test___various_3gpp() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P052_INVALID_YANG_IDENTIFIER.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S394_LIST_MISSING_KEY.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S401_KEY_REFERS_TO_UNKNOWN_LEAF.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N664_3GPP_LIST_IN_WRONG_SUBTREE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N665_INCORRECT_3GPP_MOC_CONTENT.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N674_MORE_THAN_ONE_KEY.toString());

		final TransformerContext context = createContext(Arrays.asList(new File(TEST_SUB_DIR + "3gpp-module.yang"), new File(TEST_SUB_DIR + "ietf-module.yang"), new File(TEST_SUB_DIR + "various-3gpp.yang")));
		context.setApply3gppHandling(true);

		YangTransformer2.transform(context);

		internal___various_3gpp(context, null);
	}

	@Test
	public void test___various_3gpp_through_nmt_plugin() {

		final Properties overwritingProperties = new Properties();
		overwritingProperties.setProperty(YangTransformer2PluginProperties.APPLY_3GPP_HANDLING, "true");

		final Properties actualNmtProperties = transformThroughNmtPlugin(Arrays.asList(new File(TEST_SUB_DIR + "3gpp-module.yang"), new File(TEST_SUB_DIR + "ietf-module.yang"), new File(TEST_SUB_DIR + "various-3gpp.yang")), overwritingProperties);

		internal___various_3gpp(null, actualNmtProperties);
	}

	private void internal___various_3gpp(final TransformerContext context, final Properties actualNmtProperties) {

		final ModelInfo cont1modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "Cont1", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition cont1 = load(cont1modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, cont1);

		assertSize(2, cont1.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(cont1.getPrimaryTypeAttribute(), "Cont1-key"));
		assertHasMeta(Constants.META_ARTIFIAL_KEY, findAttribute(cont1.getPrimaryTypeAttribute(), "Cont1-key"));
		assertNotNull(findAttribute(cont1.getPrimaryTypeAttribute(), "leaf11"));

		final ModelInfo cont2modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "Cont.2", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition cont2 = load(cont2modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, cont2);

		assertSize(1, cont2.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(cont2.getPrimaryTypeAttribute(), "Cont.2-key"));
		assertHasMeta(Constants.META_ARTIFIAL_KEY, findAttribute(cont2.getPrimaryTypeAttribute(), "Cont.2-key"));

		final ModelInfo cont2attributesModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "Cont.2$$attributes", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition cont2attributes = load(cont2attributesModelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, cont2attributes);

		assertSize(2, cont2attributes.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(cont2attributes.getPrimaryTypeAttribute(), "attributes-key"));
		assertHasMeta(Constants.META_ARTIFIAL_KEY, findAttribute(cont2attributes.getPrimaryTypeAttribute(), "attributes-key"));
		assertNotNull(findAttribute(cont2attributes.getPrimaryTypeAttribute(), "leaf21"));

		final ModelInfo list3modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List3", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list3 = load(list3modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list3);

		assertSize(2, list3.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(list3.getPrimaryTypeAttribute(), "id"));
		assertNotNull(findAttribute(list3.getPrimaryTypeAttribute(), "leaf31"));

		final ModelInfo list3attributesModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List3$$attributes", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list3attributes = load(list3attributesModelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list3attributes);

		assertSize(2, list3attributes.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(list3attributes.getPrimaryTypeAttribute(), "attributes-key"));
		assertHasMeta(Constants.META_ARTIFIAL_KEY, findAttribute(list3attributes.getPrimaryTypeAttribute(), "attributes-key"));
		assertNotNull(findAttribute(list3attributes.getPrimaryTypeAttribute(), "leaf32"));

		final ModelInfo list4modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List4", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list4 = load(list4modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list4);

		assertSize(2, list4.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(list4.getPrimaryTypeAttribute(), "id"));
		assertNotNull(findAttribute(list4.getPrimaryTypeAttribute(), "leaf41"));

		final ModelInfo list4attributesModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List4$$attributes", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list4attributes = load(list4attributesModelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list4attributes);

		assertSize(2, list4attributes.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(list4attributes.getPrimaryTypeAttribute(), "attributes-key"));
		assertHasMeta(Constants.META_ARTIFIAL_KEY, findAttribute(list4attributes.getPrimaryTypeAttribute(), "attributes-key"));
		assertNotNull(findAttribute(list4attributes.getPrimaryTypeAttribute(), "leaf42"));

		final ModelInfo cont5modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "Cont5", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition cont5 = load(cont5modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, cont5);

		assertSize(1, cont5.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(cont5.getPrimaryTypeAttribute(), "id"));

		final ModelInfo list6modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List6", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list6 = load(list6modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, list6);

		assertSize(2, list6.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(list6.getPrimaryTypeAttribute(), "id"));
		assertNotNull(findAttribute(list6.getPrimaryTypeAttribute(), "leaf61"));

		final ModelInfo list7modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List7", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list7 = load(list7modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list7);

		final ModelInfo list8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List8", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list8 = load(list8modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list8);

		final ModelInfo list9modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List9", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list9 = load(list9modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list9);

		final ModelInfo list10modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "list10", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list10 = load(list10modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list10);

		final ModelInfo list11modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "list11", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list11 = load(list11modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list11);

		final ModelInfo list12modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List12", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list12 = load(list12modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, list12);
		assertNull(findAttribute(list12, "leafXxx"));

		final ModelInfo list13modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "List13", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list13 = load(list13modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, list13);
		assertNull(findAttribute(list13, "leafXxx"));

		final ModelInfo list14modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_VAR, "3list14", MODULE_XYZ_VERSION_VAR);
		final PrimaryTypeDefinition list14 = load(list14modelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, list14);

		// -----------------------------------------------------

		final ModelInfo managedElementModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_3GPP, "ManagedElement", MODULE_XYZ_VERSION_3GPP);
		final PrimaryTypeDefinition managedElement = load(managedElementModelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, managedElement);

		assertSize(2, managedElement.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(managedElement.getPrimaryTypeAttribute(), "id"));
		assertNotNull(findAttribute(managedElement.getPrimaryTypeAttribute(), "leafXx"));

		final ModelInfo cont8modelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_3GPP, "cont.ainer.8", MODULE_XYZ_VERSION_3GPP);
		final PrimaryTypeDefinition cont8 = load(cont8modelInfo);
		assertHasMeta(Constants.META_3GPP_REAGGREGATED_IOC, cont8);

		assertSize(2, cont8.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(cont8.getPrimaryTypeAttribute(), "id"));
		assertNotNull(findAttribute(cont8.getPrimaryTypeAttribute(), "leaf82"));

		// -----------------------------------------------------

		final ModelInfo ietfContModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_IETF, "IetfCont", MODULE_XYZ_VERSION_IETF);
		final PrimaryTypeDefinition ietfCont = load(ietfContModelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, ietfCont);

		assertSize(1, ietfCont.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(ietfCont.getPrimaryTypeAttribute(), "IetfCont-key"));

		final ModelInfo ietfAttributesModelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_IETF, "attributes", MODULE_XYZ_VERSION_IETF);
		final PrimaryTypeDefinition attributes = load(ietfAttributesModelInfo);
		assertHasNotMeta(Constants.META_3GPP_REAGGREGATED_IOC, attributes);

		assertSize(2, attributes.getPrimaryTypeAttribute());
		assertNotNull(findAttribute(attributes.getPrimaryTypeAttribute(), "attributes-key"));
		assertNotNull(findAttribute(attributes.getPrimaryTypeAttribute(), "leafXx"));
	}

	@Test
	public void test___dn_usage() {

		additionallyIgnoreFindingDuringValidation(ParserFindingType.P052_INVALID_YANG_IDENTIFIER.toString());
		additionallyIgnoreFindingDuringValidation(ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S394_LIST_MISSING_KEY.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S401_KEY_REFERS_TO_UNKNOWN_LEAF.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N664_3GPP_LIST_IN_WRONG_SUBTREE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N665_INCORRECT_3GPP_MOC_CONTENT.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N674_MORE_THAN_ONE_KEY.toString());

		final TransformerContext context = createContextWith3gppYangTypes(Collections.singletonList(new File(TEST_SUB_DIR + "dn-usage.yang")));
		context.setApply3gppHandling(true);

		YangTransformer2.transform(context);

		final ModelInfo MocAmodelInfo = PrimaryTypeGenerator.getModelInfoForPrimaryTypeDefinition(MODULE_NS_DN_USAGE, "MocA", MODULE_XYZ_VERSION_DN_USAGE);
		final PrimaryTypeDefinition MocA = load(MocAmodelInfo);

		assertSize(7, MocA.getPrimaryTypeAttribute());		// 1 key + 3 leafs + 3 leafs from grouping

		final PrimaryTypeAttribute leaf01 = findAttribute(MocA.getPrimaryTypeAttribute(), "leaf01");
		assertInstanceOf(StringType.class, leaf01.getType());

		final PrimaryTypeAttribute leaf02 = findAttribute(MocA.getPrimaryTypeAttribute(), "leaf02");
		assertInstanceOf(MoRefType.class, leaf02.getType());

		final PrimaryTypeAttribute leaf03 = findAttribute(MocA.getPrimaryTypeAttribute(), "leaf03");
		assertInstanceOf(MoRefType.class, leaf03.getType());

		final PrimaryTypeAttribute leaf11 = findAttribute(MocA.getPrimaryTypeAttribute(), "leaf11");
		assertInstanceOf(StringType.class, leaf11.getType());

		final PrimaryTypeAttribute leaf12 = findAttribute(MocA.getPrimaryTypeAttribute(), "leaf12");
		assertInstanceOf(MoRefType.class, leaf12.getType());

		final PrimaryTypeAttribute leaf13 = findAttribute(MocA.getPrimaryTypeAttribute(), "leaf13");
		assertInstanceOf(MoRefType.class, leaf13.getType());
	}
}
