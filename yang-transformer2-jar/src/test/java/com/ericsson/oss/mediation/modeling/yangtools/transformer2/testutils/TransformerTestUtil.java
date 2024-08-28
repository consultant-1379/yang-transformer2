package com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.Unmarshaller;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;

import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.Case;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.Choice;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeActionParameterSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeActionSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.ChangeLogHandlingType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.ReadBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_common.WriteBehaviorType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceCaseAttributeType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceCasePrimaryTypeType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceCaseType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.ChoiceType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.HierarchyType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAction;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeActionParameter;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype.PrimaryTypeDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeAttributeRemovalType;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_primarytype_ext.PrimaryTypeExtensionDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.AssociationAttribute;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeAssociation;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeContainment;
import com.ericsson.oss.itpf.datalayer.dps.modeling.schema.gen.dps_relationship.PrimaryTypeRelationshipDefinition;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.mdt.impl.app.MdtApplication;
import com.ericsson.oss.itpf.modeling.mdt.impl.app.SystemProperties;
import com.ericsson.oss.itpf.modeling.mdt.impl.inputs.DeploymentInputs;
import com.ericsson.oss.itpf.modeling.mdt.impl.inputs.ModelDeploymentInputs;
import com.ericsson.oss.itpf.modeling.mdt.util.InvocationContext;
import com.ericsson.oss.itpf.modeling.modelrepo.ModelRepo;
import com.ericsson.oss.itpf.modeling.modelrepo.ModelRepoFactory;
import com.ericsson.oss.itpf.modeling.modelrepo.XmlBasedModelRepo;
import com.ericsson.oss.itpf.modeling.modelservice.ModelRepoRetriever;
import com.ericsson.oss.itpf.modeling.modelservice.ModelService;
import com.ericsson.oss.itpf.modeling.modelservice.ModelServiceImpl;
import com.ericsson.oss.itpf.modeling.modelservice.cache.CacheManager;
import com.ericsson.oss.itpf.modeling.modelservice.exception.ConstraintViolationException;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelRepoBasedModelMetaInformation;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.EModelAttributeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.EModelSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.Requires;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.constraints.Constraint;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.edt.EnumDataTypeMemberSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.edt.EnumDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.Target;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_cdt.ComplexDataTypeAttribute;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelNamedEntityDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.ImpliedUrnType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.LifeCycleType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MetaInformation;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.MinMaxValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.NetworkManagementDomainType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.RequiresTargetType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.StringValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt.EnumDataTypeMember;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_combined.CombinedEnumDataTypeDefinition;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_combined.EnumDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_edt_ext.EnumDataTypeExtensionDefinition;
import com.ericsson.oss.itpf.modeling.schema.util.ModelHandlingUtil;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.itpf.modeling.schema.util.XmlGenerator;
import com.ericsson.oss.itpf.modeling.tools.modelproc.app.ProcessingRunner;
import com.ericsson.oss.itpf.modeling.tools.modelproc.modelmetainfo.ModelsMetaInformationGenerator;
import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.plugin.NetworkModelTransformerPluginException;
import com.ericsson.oss.mediation.modeling.yangtools.parser.CustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.ParserExecutionContext;
import com.ericsson.oss.mediation.modeling.yangtools.parser.YangDeviceModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.Finding;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.FindingsManager;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ModifyableFindingSeverityCalculator;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ParserFindingType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.input.FileBasedYangInputResolver;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ConformanceType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.StatementClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EriCustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EricssonExtensionsClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.ietf.IetfExtensionsClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.threegpp.ThreeGppExtensionsClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.StringHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2Plugin;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.CfmMimInfoSupport;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.NamespaceNamePath;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.StandaloneValidatorContext;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.StandaloneValidationManagerEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.eri.StandaloneValidationManagerEri;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.eri.ValidatorFindingTypeEri;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.StandaloneValidationManagerYang;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

public abstract class TransformerTestUtil {

	static {
		/*
		 * This suppresses a significant amount of output from the various modeling toolchain
		 * components, such as model processing, MDT and Model Service, during test deployment
		 * of the generated models. If you want to see output from those comment-out the
		 * configure() invocation below.
		 */
		final Properties properties = new Properties();
		properties.setProperty("log4j.reset", "true");
		properties.setProperty("log4j.rootLogger", "ERROR, STDOUT");
		properties.setProperty("log4j.appender.STDOUT", "org.apache.log4j.ConsoleAppender");
		properties.setProperty("log4j.appender.STDOUT.Threshold", "INFO");
		properties.setProperty("log4j.appender.STDOUT.layout", "org.apache.log4j.PatternLayout");
		properties.setProperty("log4j.appender.STDOUT.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
		properties.setProperty("log4j.logger.com.ericsson.oss.itpf.modeling", "ERROR");
		properties.setProperty("log4j.logger.com.ericsson.oss.itpf.modeling.mdt.impl.postplugins.HtmlGeneratorPostProcessingPlugin", "ERROR");
		properties.setProperty("log4j.logger.com.ericsson.oss.itpf.modeling.tools.modelproc.modelmetainfo", "ERROR");
		properties.setProperty("log4j.logger.com.ericsson.oss.itpf.modeling.modelservice.logging.ModelServiceLogger", "ERROR");

		/*
		 * Change the below property value to INFO if you want to see relevant output from an NMT plugin test run.
		 * As there are some test cases that provoke errors that use the NMT plugin, by default the logging is
		 * disabled so not to put errors into the build log that might be confusing.
		 * 
		 * Note that test class "YangTransformer2PluginTest" modifies this property to test the logging output; at
		 * the end of that test, the logger will be set to OFF again. You may have to comment-out the relevant test
		 * in that test class to get general logging for the plugin to work.
		 */
		properties.setProperty("log4j.logger.com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2Plugin", "OFF");

		PropertyConfigurator.configure(properties);
	}

	protected static final String DEFAULT_TARGET_CATEGORY = "NODE";
	protected static final String DEFAULT_TARGET_TYPE = "vDU";
	protected static final String DEFAULT_TARGET_VERSION = "Q4.22";

	protected static final TransformerContext.Target DEFAULT_TEST_TARGET = new TransformerContext.Target(DEFAULT_TARGET_CATEGORY, DEFAULT_TARGET_TYPE, DEFAULT_TARGET_VERSION);

	protected static final ModelInfo DEFAULT_MOCK_ME = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "VduTop", "ManagedElement", "1.0.0");
	protected static final ModelInfo OSS_TOP_ME_CONTEXT_300 = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "OSS_TOP", "MeContext", "3.0.0");
	protected static final ModelInfo COM_TOP_MANAGEDELEMENT_10221 = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, "ComTop", "ManagedElement", "10.22.1");

	protected static final String BASE_OUT_DIR = "target/test-output-generated";
	protected static final String GENERATED_MODELS_BASE_DIR = BASE_OUT_DIR + "/generated-models";
	protected static final String GENERATED_MODELS_ETC_MODEL_DIR = GENERATED_MODELS_BASE_DIR + "/etc/model";
	protected static final String GENERATED_JAR_FILE = BASE_OUT_DIR + "/generated-jar/generated.jar";
	protected static final String MDT_BASE_DIR = BASE_OUT_DIR + "/mdt";
	protected static final String MDT_REPO = MDT_BASE_DIR + "/modelRepo.xml";
	protected static final String MDT_REPORTS_BASE_DIR = BASE_OUT_DIR + "/mdt-reports";

	protected static final String TEST_MODULES_DIR = "src/test/resources/test-modules/";
	protected static final String NRMS_DIR = "src/test/resources/nrms/";

	private static final String ORIG_MODULES_DIR = "src/test/resources/orig-modules";
	protected static final String ERI_NOTIF = ORIG_MODULES_DIR + "/ericsson-notifications@2021-07-22.yang";
	protected static final String ERI_YANG_EXT = ORIG_MODULES_DIR + "/ericsson-yang-extensions@2023-03-29.yang";
	protected static final String ERI_YANG_LIBRARY_EXT = ORIG_MODULES_DIR + "/ericsson-yang-library-ext@2021-03-29.yang";
	protected static final String ERI_YANG_TYPES = ORIG_MODULES_DIR + "/ericsson-yang-types@2024-03-07.yang";
	protected static final String ORAN_TEIV_YANG_EXT = ORIG_MODULES_DIR + "/o-ran-smo-teiv-common-yang-extensions@2023-12-12.yang";
	protected static final String ORAN_TEIV_YANG_TYPES = ORIG_MODULES_DIR + "/o-ran-smo-teiv-common-yang-types@2023-12-12.yang";
	protected static final String THREEGPP_YANG_EXT = ORIG_MODULES_DIR + "/_3gpp-common-yang-extensions@2024-04-05.yang";
	protected static final String THREEGPP_YANG_TYPES = ORIG_MODULES_DIR + "/_3gpp-common-yang-types@2023-02-14.yang";
	protected static final String IETF_DATASTORES = ORIG_MODULES_DIR + "/ietf-datastores@2018-02-14.yang";
	protected static final String IETF_INET_TYPES = ORIG_MODULES_DIR + "/ietf-inet-types@2019-11-04.yang";
	protected static final String IETF_YANG_LIBRARY_OLD = ORIG_MODULES_DIR + "/ietf-yang-library@2016-06-21.yang";
	protected static final String IETF_YANG_LIBRARY_NEW = ORIG_MODULES_DIR + "/ietf-yang-library@2019-01-04.yang";
	protected static final String IETF_YANG_TYPES = ORIG_MODULES_DIR + "/ietf-yang-types@2019-11-04.yang";
	protected static final String IANA_CRYPT_HASH = ORIG_MODULES_DIR + "/iana-crypt-hash@2014-08-06.yang";

	protected ModelServiceImpl modelService;
	protected List<String> additionalFindingsToIgnoreDuringValidation = new ArrayList<>();

	@Before
	public void setUp() {
		createCleanTargetDir();
		modelService = null;
		additionalFindingsToIgnoreDuringValidation.clear();
	}

	@After
	public void cleanup() {
	}

	protected void additionallyIgnoreFindingDuringValidation(final String findingType) {
		additionalFindingsToIgnoreDuringValidation.add(findingType);
	}

	protected TransformerContext createContext(final File pathToImplementing) {
		return createContext(Collections.singletonList(pathToImplementing), Collections.<File>emptyList());
	}

	protected TransformerContext createContext(final File pathToImplementing, final File pathToImporting) {
		return createContext(Collections.singletonList(pathToImplementing), Collections.singletonList(pathToImporting));
	}

	protected TransformerContext createContext(final List<File> pathToImplementing) {
		return createContext(pathToImplementing, Collections.<File>emptyList());
	}

	protected TransformerContext createContextWith3ppModules(final File pathToImplementing) {
		return createContext(Collections.singletonList(pathToImplementing), Arrays.asList(new File(ERI_YANG_EXT), new File(ERI_NOTIF), new File(ORAN_TEIV_YANG_EXT), new File(THREEGPP_YANG_EXT)));
	}

	protected TransformerContext createContextWithEriExtensions(final File pathToImplementing) {
		return createContext(Collections.singletonList(pathToImplementing), Arrays.asList(new File(ERI_YANG_EXT)));
	}

	protected TransformerContext createContextWithEriExtensions(final List<File> pathToImplementing) {
		return createContext(pathToImplementing, Arrays.asList(new File(ERI_YANG_EXT)));
	}

	protected TransformerContext createContextWith3ppModules(final List<File> pathToImplementing) {
		return createContext(pathToImplementing, Arrays.asList(new File(ERI_YANG_EXT), new File(ERI_NOTIF), new File(ORAN_TEIV_YANG_EXT), new File(THREEGPP_YANG_EXT)));
	}

	protected TransformerContext createContextWith3gppYangTypes(final List<File> pathToImplementing) {
		return createContext(pathToImplementing, Arrays.asList(new File(THREEGPP_YANG_TYPES)));
	}

	protected TransformerContext createContext(final List<File> pathToImplementing, final List<File> pathToImporting) {
		return createContext(pathToImplementing, pathToImporting, DEFAULT_TARGET_TYPE, DEFAULT_TARGET_VERSION, true);
	}

	protected TransformerContext createContext(final File pathToImplementing, final String targetType, final String targetVersion, final boolean runValidation) {
		return createContext(Collections.singletonList(pathToImplementing), Collections.emptyList(), targetType, targetVersion, runValidation);
	}

	protected TransformerContext createContext(final List<File> pathToImplementing, final List<File> pathToImporting, final String targetType, final String targetVersion, final boolean runValidation) {
		return createContext(pathToImplementing, pathToImporting, GENERATED_MODELS_ETC_MODEL_DIR, targetType, targetVersion, runValidation);
	}

	protected TransformerContext createContext(final List<File> pathToImplementing, final List<File> pathToImporting, final String outDir, final String targetType, final String targetVersion, final boolean runValidation) {

		if(runValidation) {
			runPreTransformationValidation(pathToImplementing, pathToImporting);
		}

		return new TransformerContext(pathToImplementing, pathToImporting, new File(outDir), targetType, targetVersion);
	}

	protected Properties transformThroughNmtPlugin(final File pathToImplementing, final Properties overwritingProperties) {
		return transformThroughNmtPlugin(Collections.singletonList(pathToImplementing), Collections.<File>emptyList(), overwritingProperties);
	}

	protected Properties transformThroughNmtPlugin(final List<File> pathsToImplementing, final Properties overwritingProperties) {
		return transformThroughNmtPlugin(pathsToImplementing, Collections.<File>emptyList(), overwritingProperties);
	}

	protected Properties transformThroughNmtPluginWith3ppModules(final File pathToImplementing, final Properties overwritingProperties) {
		return transformThroughNmtPlugin(Collections.singletonList(pathToImplementing), Arrays.asList(new File(ERI_YANG_EXT), new File(ERI_NOTIF), new File(ORAN_TEIV_YANG_EXT), new File(THREEGPP_YANG_EXT)), overwritingProperties);
	}

	@SuppressWarnings("deprecation")
	protected Properties transformThroughNmtPlugin(final List<File> pathToImplementing, final List<File> pathToImporting, final Properties overwritingProperties) {

		final Properties properties = new Properties();

		properties.setProperty(YangTransformer2PluginProperties.TARGET_TYPE, DEFAULT_TARGET_TYPE);
		properties.setProperty(YangTransformer2PluginProperties.TARGET_VERSION, DEFAULT_TARGET_VERSION);
		properties.setProperty(YangTransformer2PluginProperties.IMPLEMENTING_MODULES, StringHelper.toString(pathToImplementing, null, null, ",", null, null));
		properties.setProperty(YangTransformer2PluginProperties.IMPORTING_MODULES, StringHelper.toString(pathToImporting, null, null, ",", null, null));
		properties.setProperty(YangTransformer2PluginProperties.OUTPUT_DIR, GENERATED_MODELS_BASE_DIR);
		properties.setProperty(YangTransformer2PluginProperties.REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE, "false");
		properties.setProperty(YangTransformer2PluginProperties.GENERATE_NP_CONTAINER_AS_SYSTEM_CREATED, "false");

		if(overwritingProperties != null) {
			overwritingProperties.forEach((name, value) -> properties.setProperty((String) name, (String) value));
		}

		final YangTransformer2Plugin plugin = new YangTransformer2Plugin();

		try {
			plugin.transform(properties);
		} catch (final NetworkModelTransformerPluginException ex) {
			throw new RuntimeException("Exception during testing: " + ex.getMessage());
		}

		return properties;
	}

	protected void transformNrm(final TransformerContext context, final String name) {
		System.out.println("Transforming and testing '" + name + "', this will take some time, have patience...");

		final long start = System.currentTimeMillis();

		try {
			YangTransformer2.transform(context);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		final long end = System.currentTimeMillis();

		System.out.println("Transformed '" + name + "' in " + (end - start) + " ms and generated " + context.getCreatedEmodels().getGeneratedEModels().size() + " models, now test-deploying into Model Service...");

		orderedUnique(context.getErrors()).forEach(w -> System.err.println("ERROR: " + w));
		orderedUnique(context.getWarnings()).forEach(w -> System.out.println("WARN: " + w));
		orderedUnique(context.getInfos()).forEach(w -> System.out.println("INFO: " + w));
	}

	private static List<String> orderedUnique(final List<String> entries) {
		final List<String> uniqueEntries = new ArrayList<>(new HashSet<>(entries));
		Collections.sort(uniqueEntries);
		return uniqueEntries;
	}

	private static void createCleanTargetDir() {

		final File outDir = new File(BASE_OUT_DIR);

		if(!outDir.exists()) {
			outDir.mkdirs();
		}

		deleteContentsOfDir(outDir);
	}

	private static void deleteContentsOfDir(File file) {
		Arrays.stream(file.listFiles()).forEach(child -> {
			if(child.isDirectory()) {
				deleteContentsOfDir(child);
			}
			child.delete();
		});
	}

	protected static Target getModelServiceTarget(final TransformerContext context, final Properties actualNmtProperties) {
		if(context != null) {
			return new Target(context.getTarget().getCategory(), context.getTarget().getType(), "<target-name>", context.getTarget().getVersion());
		}
		return new Target("NODE", actualNmtProperties.getProperty(YangTransformer2PluginProperties.TARGET_TYPE), "<target-name>", actualNmtProperties.getProperty(YangTransformer2PluginProperties.TARGET_VERSION));
	}

	protected static Target getModelServiceTargetOfUnknownTargetTypeVersion() {
		return new Target("NODE", "XXX", "<target-name>", "<unknown-target-version>");
	}

	protected static <T> void assertSize(final int expected, final Collection<T> list) {
		final int actual = list.size();
		if(expected != actual) {
			fail("Expected " + expected + " list element(s), but list has " + actual + " element(s).");
		}
	}

	protected static <T> void assertEmpty(final Collection<T> list) {
		if(!list.isEmpty()) {
			fail("Expected list to be empty, but it has " + list.size() + " element(s).");
		}
	}

	protected static <T> void assertInstanceOf(final Class<T> clazz, final Object obj) {
		if(obj.getClass() != clazz) {
			fail("Expected class '" + clazz.getName() + "' but received class '" + obj.getClass().getName() + "'.");
		}
	}

	protected static void assertEModelCreatedCount(final int dpsPt, final int dpsPtExt, final int edt, final int edtExt, final int yams, final int cdt) {
		assertEModelCreatedCount(SchemaConstants.DPS_PRIMARYTYPE, dpsPt);
		assertEModelCreatedCount(SchemaConstants.DPS_PRIMARYTYPE_EXT, dpsPtExt);
		assertEModelCreatedCount(SchemaConstants.OSS_EDT, edt);
		assertEModelCreatedCount(SchemaConstants.OSS_EDT_EXT, edtExt);
		assertEModelCreatedCount(SchemaConstants.NET_YANG, yams);
		assertEModelCreatedCount(SchemaConstants.OSS_CDT, cdt);
		assertEModelCreatedCount(SchemaConstants.CFM_MIMINFO, 1);
		assertEModelCreatedCount(SchemaConstants.DPS_RELATIONSHIP, 1);
	}

	protected static void assertEModelCreatedCount(final String schemaName, final int expected) {
		final int actual = countFiles(new File(GENERATED_MODELS_ETC_MODEL_DIR + "/" + schemaName));
		if(expected != actual) {
			fail("Expected " + expected + " model(s) of type " + schemaName + " to be generated, but " + actual + " were generated.");
		}
	}

	protected static void assertNoEModelsCreated() {
		final int countFiles = countFiles(new File(GENERATED_MODELS_ETC_MODEL_DIR));
		if(countFiles != 0) {
			fail("Expected no models to be created / copied, but " + countFiles + " was generated / copied.");
		}
	}

	private static int countFiles(final File dir) {

		if(!dir.exists()) {
			return 0;
		}

		int count = 0;
		for(final File child : dir.listFiles()) {
			if(child.isDirectory()) {
				count += countFiles(child);
			} else {
				count++;
			}
		}
		return count;
	}

	protected static void assertModelExists(final ModelInfo mi) {
		assertModelExists(mi.getSchema(), mi.getNamespace(), mi.getName(), mi.getVersion() == null ? null : mi.getVersion().toString());
	}

	protected static void assertModelDoesNotExist(final ModelInfo mi) {
		assertModelDoesNotExist(mi.getSchema(), mi.getNamespace(), mi.getName(), mi.getVersion() == null ? null : mi.getVersion().toString());
	}

	protected static void assertModelExists(final String schema, final String namespace, final String name, final String version) {
		if(!modelExists(schema, namespace, name, version)) {
			fail("File not found.");
		}
	}

	protected static void assertModelDoesNotExist(final String schema, final String namespace, final String name, final String version) {
		if(modelExists(schema, namespace, name, version)) {
			fail("File should not exist, but it does.");
		}
	}

	private static boolean modelExists(final String schema, final String namespace, final String name, final String version) {
		return XmlGenerator.getFileForModel(new File(GENERATED_MODELS_ETC_MODEL_DIR), schema, namespace, name, version).exists();
	}

	protected static void assertModelExistsInModelService(final ModelService modelService, final ModelInfo mi) {
		final ModelMetaInformation modelMetaInformation = modelService.getModelMetaInformation();
		final boolean modelDeployed = modelMetaInformation.isModelDeployed(mi);
		if(!modelDeployed) {
			fail("Model '" + mi.toUrn() + "' does not exist in Model Service, although it should.");
		}
	}

	protected static void assertModelDoesNotExistInModelService(final ModelService modelService, final ModelInfo mi) {
		final ModelMetaInformation modelMetaInformation = modelService.getModelMetaInformation();
		final boolean modelDeployed = modelMetaInformation.isModelDeployed(mi);
		if(modelDeployed) {
			fail("Model '" + mi.toUrn() + "' exists in Model Service, although it should not.");
		}
	}

	protected static <T extends EModelDefinition> T load(final ModelInfo mi) {
		return load(mi.getSchema(), mi.getNamespace(), mi.getName(), mi.getVersion() == null ? null : mi.getVersion().toString());
	}

	protected static String loadRaw(final InputStream is) {
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			int nRead;
			byte[] data = new byte[100000];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
		} catch (final Exception ex) {
			fail("Excepton while loading raw file contents: " + ex.getMessage());
		}

		return new String(buffer.toByteArray());
	}

	@SuppressWarnings("unchecked")
	protected static <T extends EModelDefinition> T load(final String schema, final String namespace, final String name, final String version) {
		final File file = XmlGenerator.getFileForModel(new File(GENERATED_MODELS_ETC_MODEL_DIR), schema, namespace, name, version);
		if(!file.exists()) {
			fail("File not found: " + file.getAbsolutePath());
		}

		try (final FileInputStream fis = new FileInputStream(file)) {
			final Unmarshaller unmarshaller = ModelHandlingUtil.getUnmarshaller(schema, false);
			return (T) unmarshaller.unmarshal(fis);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Unmarshalling error");
		}

		return null;
	}

	protected static ManagementInformationModelInformation loadCfmMimInfo(final TransformerContext context, final Properties actualNmtProperties) {

		final File outdir = context != null ? context.getOutDir() : new File(actualNmtProperties.getProperty(YangTransformer2PluginProperties.OUTPUT_DIR) + "/etc/model");
		final String targetType = context != null ? context.getTarget().getType() : actualNmtProperties.getProperty(YangTransformer2PluginProperties.TARGET_TYPE);

		final File neDefinedDir = new File(new File(new File(outdir, SchemaConstants.CFM_MIMINFO), targetType), "NE-defined");
		final File cfmMimInfoFile = neDefinedDir.listFiles()[0];

		try (final FileInputStream fis = new FileInputStream(cfmMimInfoFile)) {
			final Unmarshaller unmarshaller = ModelHandlingUtil.getUnmarshaller(SchemaConstants.CFM_MIMINFO, false);
			return (ManagementInformationModelInformation) unmarshaller.unmarshal(fis);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("Unmarshalling error");
		}

		return null;
	}

	protected static void assertModelIdentity(final EModelDefinition emodel, final String namespace, final String name, final String version) {

		if(!emodel.getNs().equals(namespace)) {
			fail("Expected namespace '" + namespace + "' but got '" + emodel.getNs() + "'.");
		}
		if(!emodel.getName().equals(name)) {
			fail("Expected name '" + name + "' but got '" + emodel.getName() + "'.");
		}
		if(!emodel.getVersion().equals(version)) {
			fail("Expected version '" + version + "' but got '" + emodel.getVersion() + "'.");
		}
	}

	protected static void assertUsualValuesForPrimaryType(final PrimaryTypeDefinition ptd) {
		/*
		 * A lot of model properties are always the same for a generated MOC, these are all checked for here. 
		 */
		assertLifecycle(LifeCycleType.CURRENT, ptd);
		assertLifecycleDescription(null, ptd);
		assertInheritsFrom(null, ptd);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, ptd);
		assertTrue(ptd.getDefinedBy() == NetworkManagementDomainType.NE);
		assertTrue(ptd.getHierarchyType() == HierarchyType.HIERARCHICAL);

		/*
		 * A whole bunch of stuff is not generated by the transformer, we check that this is the case.
		 */
		assertTrue(ptd.getAuditTrailLogging() == null);
		assertTrue(ptd.getChangeEventHandling() == null);
		assertTrue(ptd.getChangeLogHandling() == ChangeLogHandlingType.INHERITED);		// INHERITED is the default value in the schema
		assertTrue(ptd.getInheritanceQualifier() == null);
		assertTrue(ptd.getLockBeforeDelete() == null);	// correct - no support for this (yet ?) in the ericsson-yang-extensions.
		assertTrue(ptd.getRequires() == null);
		assertTrue(ptd.getRequiresCapability().size() == 0);	// may change in the future if we ever support 'if-feature'.
		assertTrue(ptd.getRoleBasedAccessControlled() == null);
		assertTrue(ptd.getTbacTarget().size() == 0);
		assertTrue(ptd.getUserExposure() == null);
	}

	protected static void assertLifecycle(final LifeCycleType expected, final Object emodelOrNamedEntity) {

		LifeCycleType modelLifecycle = null;

		if(emodelOrNamedEntity instanceof EModelDefinition) {
			modelLifecycle = ((EModelDefinition)emodelOrNamedEntity).getLifeCycle();
		} else if (emodelOrNamedEntity instanceof EModelNamedEntityDefinition) {
			modelLifecycle = ((EModelNamedEntityDefinition)emodelOrNamedEntity).getLifeCycle();
		}

		assertEquals(expected, modelLifecycle);
	}

	protected static void assertLifecycleDescription(final String expected, final Object emodelOrNamedEntity) {

		String modelLifecycleDesc = null;

		if(emodelOrNamedEntity instanceof EModelDefinition) {
			modelLifecycleDesc = ((EModelDefinition)emodelOrNamedEntity).getLifeCycleDesc();
		} else if (emodelOrNamedEntity instanceof EModelNamedEntityDefinition) {
			modelLifecycleDesc = ((EModelNamedEntityDefinition)emodelOrNamedEntity).getLifeCycleDesc();
		}

		assertEquals(expected, modelLifecycleDesc);
	}

	protected static void assertInheritsFrom(final String expectedImpliedUrn, final EModelDefinition emodel) {

		final ImpliedUrnType inheritsFrom = emodel.getInheritsFrom();
		final String modelInheritsFrom = inheritsFrom != null ? inheritsFrom.getUrn() : null;

		assertEquals(expectedImpliedUrn, modelInheritsFrom);
	}

	protected static void assertReadBehaviour(final ReadBehaviorType expectedBehaviour, final Object prOrPtAttr) {
		ReadBehaviorType modelBehaviour = null;

		if(prOrPtAttr instanceof PrimaryTypeDefinition) {
			modelBehaviour = ((PrimaryTypeDefinition)prOrPtAttr).getReadBehavior();
		} else if (prOrPtAttr instanceof PrimaryTypeAttribute) {
			modelBehaviour = ((PrimaryTypeAttribute)prOrPtAttr).getReadBehavior();
		}

		assertEquals(expectedBehaviour, modelBehaviour);
	}

	protected static void assertWriteBehaviour(final WriteBehaviorType expectedBehaviour, final Object prOrPtAttr) {
		WriteBehaviorType modelBehaviour = null;

		if(prOrPtAttr instanceof PrimaryTypeDefinition) {
			modelBehaviour = ((PrimaryTypeDefinition)prOrPtAttr).getWriteBehavior();
		} else if (prOrPtAttr instanceof PrimaryTypeAttribute) {
			modelBehaviour = ((PrimaryTypeAttribute)prOrPtAttr).getWriteBehavior();
		}

		assertEquals(expectedBehaviour, modelBehaviour);
	}

	protected static void assertHasMeta(final String metaName, final Object emodelOrNamedEntity) {
		assertHasMeta(metaName, null, emodelOrNamedEntity);
	}

	protected static void assertHasMeta(final String metaName, final String metaValue, final Object emodelOrNamedEntity) {
		if(!hasMeta(metaName, metaValue, emodelOrNamedEntity)) {
			fail("Does not have meta name '" + metaName + "' with value '" + metaValue + "'.");
		}
	}

	protected static void assertHasNotMeta(final String metaName, final Object emodelOrNamedEntity) {
		assertHasNotMeta(metaName, null, emodelOrNamedEntity);
	}

	protected static void assertHasNotMeta(final String metaName, final String metaValue, final Object emodelOrNamedEntity) {
		if(hasMeta(metaName, metaValue, emodelOrNamedEntity)) {
			fail("Should not have had meta name '" + metaName + "' with value '" + metaValue + "'.");
		}
	}

	private static boolean hasMeta(final String metaName, final String metaValue, final Object emodelOrNamedEntityOrEnumMember) {
		List<MetaInformation> metaInfos = null;

		if(emodelOrNamedEntityOrEnumMember instanceof EModelDefinition) {
			metaInfos = ((EModelDefinition)emodelOrNamedEntityOrEnumMember).getMeta();
		} else if (emodelOrNamedEntityOrEnumMember instanceof EModelNamedEntityDefinition) {
			metaInfos = ((EModelNamedEntityDefinition)emodelOrNamedEntityOrEnumMember).getMeta();
		} else if (emodelOrNamedEntityOrEnumMember instanceof EnumDataTypeMember) {
			metaInfos = ((EnumDataTypeMember)emodelOrNamedEntityOrEnumMember).getMeta();
		}

		return metaInfos.stream()
				.filter(mi -> mi.getMetaName().equals(metaName))
				.filter(mi -> metaValue == null || areEqual(mi.getMetaValue(), metaValue))
				.findAny()
				.isPresent();
	}

	protected static void assertHasMetaInModelService(final String metaName, final Object specification) {
		assertHasMetaInModelService(metaName, null, specification);
	}

	protected static void assertHasMetaInModelService(final String metaName, final String metaValue, final Object specification) {
		if(!hasMetaInModelService(metaName, metaValue, specification)) {
			fail("Does not have meta name '" + metaName + "' with value '" + metaValue + "'.");
		}
	}

	protected static void assertHasNotMetaInModelService(final String metaName, final Object specification) {
		assertHasNotMetaInModelService(metaName, null, specification);
	}

	protected static void assertHasNotMetaInModelService(final String metaName, final String metaValue, final Object specification) {
		if(hasMetaInModelService(metaName, metaValue, specification)) {
			fail("Should not have had meta name '" + metaName + "' with value '" + metaValue + "'.");
		}
	}

	private static boolean hasMetaInModelService(final String metaName, final String metaValue, final Object specification) {

		Map<String, String> metaInformation = null;

		if(specification instanceof EModelSpecification) {
			metaInformation = ((EModelSpecification) specification).getMetaInformation();
		} else if (specification instanceof PrimaryTypeAttributeSpecification) {
			metaInformation = ((PrimaryTypeAttributeSpecification) specification).getMetaInformation();
		} else if (specification instanceof PrimaryTypeActionSpecification) {
			metaInformation = ((PrimaryTypeActionSpecification) specification).getMetaInformation();
		} else if (specification instanceof EnumDataTypeMemberSpecification) {
			metaInformation = ((EnumDataTypeMemberSpecification) specification).getMetaInformation();
		}

		if(!metaInformation.containsKey(metaName)) {
			return false;
		}

		return areEqual(metaInformation.get(metaName), metaValue);
	}

	protected static void assertNonGeneratedDataNodeCount(final int expected, final List<MetaInformation> meta) {
		final int found = (int) meta.stream().filter(mi -> mi.getMetaName().startsWith(Constants.META_NON_GENERATED_DATA_NODE_PATH)).count();
		if(found != expected) {
			fail("Expected " + expected + " entries for non-generated data node count, but there are " + found);
		}
	}

	protected static void assertHasNonGeneratedDataNode(final String[] parts, final List<MetaInformation> meta) {
		if(!hasNonGeneratedDataNode(parts, meta)) {
			fail("Could not find non-generated data node entry.");
		}
	}

	protected static void assertHasNotNonGeneratedDataNode(final String[] parts, final List<MetaInformation> meta) {
		if(hasNonGeneratedDataNode(parts, meta)) {
			fail("Found non-generated data node entry although it should not exist.");
		}
	}

	private static boolean hasNonGeneratedDataNode(final String[] parts, final List<MetaInformation> meta) {

		final String dataNodePath = constructDataNodePath(parts);

		return meta.stream()
				.filter(mi -> mi.getMetaName().startsWith(Constants.META_NON_GENERATED_DATA_NODE_PATH))
				.filter(mi -> mi.getMetaValue().equals(dataNodePath))
				.findAny().isPresent();
	}

	protected void assertNonGeneratedDataNodeCountInModelService(final int expected, final Map<String, String> metaInformation) {
		final int found = (int) metaInformation.entrySet().stream().filter(entry -> entry.getKey().startsWith(Constants.META_NON_GENERATED_DATA_NODE_PATH)).count();
		if(found != expected) {
			fail("Expected " + expected + " entries for non-generated data node count in Model Service, but there are " + found);
		}
	}

	protected void assertHasNonGeneratedDataNodeInModelService(final String[] parts, final Map<String, String> metaInformation) {
		if(!hasNonGeneratedDataNode(parts, metaInformation)) {
			fail("Could not find non-generated data node entry in Model Service.");
		}
	}

	private static boolean hasNonGeneratedDataNode(final String[] parts, final Map<String, String> metaInformation) {

		final String dataNodePath = constructDataNodePath(parts);

		return metaInformation.entrySet().stream()
				.filter(entry -> entry.getKey().startsWith(Constants.META_NON_GENERATED_DATA_NODE_PATH))
				.filter(entry -> entry.getValue().equals(dataNodePath))
				.findAny().isPresent();
	}

	private static String constructDataNodePath(final String[] parts) {

		final NamespaceNamePath namespaceNamePath = new NamespaceNamePath();
		for(int i = 0 ; i < parts.length / 2 ; ++i) {
			namespaceNamePath.addPathSegment(parts[i*2], parts[i*2+1]);
		}

		return namespaceNamePath.toEscapedString();
	}

	protected static void assertHasAutogeneratedKey(final PrimaryTypeDefinition ptd, final String origContainerName, final WriteBehaviorType expectedWriteBehaviour) {

		final PrimaryTypeAttribute attr = findAttribute(ptd, origContainerName + "-key");
		assertTrue(attr != null);

		assertTrue(attr.isKey() == true);
		assertTrue(attr.isMandatory() == true);
		assertTrue(attr.isImmutable() == true);
		assertTrue(attr.isSensitive() == false);
		assertReadBehaviour(ReadBehaviorType.FROM_PERSISTENCE, attr);
		assertWriteBehaviour(expectedWriteBehaviour, attr);
		assertTrue(attr.getDefinedBy() == NetworkManagementDomainType.OSS);

		assertHasMeta(Constants.META_ARTIFIAL_KEY, attr);
		assertTrue(attr.getType() instanceof StringType);
		assertTrue(attr.getType().getNotNullConstraint() != null);
		assertTrue(attr.getDefault() instanceof StringValue);
		assertTrue(((StringValue) attr.getDefault()).getValue().equals("1"));

		/*
		 * Stuff that always remains unset
		 */
		assertTrue(attr.getAuditTrailLogging() == null);
		assertTrue(attr.getChangeEventHandling() == null);
		assertTrue(attr.getChangeLogHandling() == ChangeLogHandlingType.INHERITED);
		assertTrue(attr.getDependencies() == null);
		assertTrue(attr.getDisturbances() == null);
		assertTrue(attr.getLifeCycleDesc() == null);
		assertTrue(attr.getLockBeforeModify() == null);
		assertTrue(attr.getPreCondition() == null);
		assertTrue(attr.getRequires() == null);
		assertTrue(attr.getRequiresCapability().size() == 0);
		assertTrue(attr.getRoleBasedAccessControlled() == null);
		assertTrue(attr.getSideEffects() == null);
		assertTrue(attr.getTakesEffect() == null);
		assertTrue(attr.getUnit() == null);
		assertTrue(attr.getUserExposure() == null);
	}

	protected static PrimaryTypeAttribute findAttribute(final PrimaryTypeDefinition ptd, final String attrName) {
		return findAttribute(ptd.getPrimaryTypeAttribute(), attrName);
	}

	protected static AssociationAttribute findAttribute(final PrimaryTypeAssociation pta, final String attrName) {
		return pta.getAttribute().stream().filter(attr -> attr.getName().equals(attrName)).findAny().orElse(null);
	}

	protected static PrimaryTypeAttribute findAddedAttribute(final PrimaryTypeExtensionDefinition pted, final String attrName) {
		return findAttribute(pted.getPrimaryTypeExtension().getPrimaryTypeAttribute(), attrName);
	}

	protected static PrimaryTypeAttribute findAddedAttribute(final PrimaryTypeExtensionDefinition pted, final String namespace, final String attrName) {
		return findAttribute(pted.getPrimaryTypeExtension().getPrimaryTypeAttribute(), namespace, attrName);
	}

	protected static PrimaryTypeAttribute findReplacedAttribute(final PrimaryTypeExtensionDefinition pted, final String attrName) {
		return findAttribute(pted.getPrimaryTypeAttributeReplacement().getPrimaryTypeAttribute(), attrName);
	}

	protected static PrimaryTypeAttributeRemovalType findRemovedAttribute(final PrimaryTypeExtensionDefinition pted, final String attrName) {
		return pted.getPrimaryTypeAttributeRemoval().stream().filter(attr -> attr.getAttributeName().equals(attrName)).findAny().orElse(null);
	}

	protected static PrimaryTypeAttribute findAttribute(final List<PrimaryTypeAttribute> attrs, final String attrName) {
		return attrs.stream().filter(attr -> attr.getName().equals(attrName)).findAny().orElse(null);
	}

	protected static PrimaryTypeAttribute findAttribute(final List<PrimaryTypeAttribute> attrs, final String namespace, final String attrName) {
		return attrs.stream().filter(attr -> attr.getName().equals(attrName) && namespace.equals(attr.getNs())).findAny().orElse(null);
	}

	protected static boolean isAttributeRemoved(final List<PrimaryTypeAttributeRemovalType> attrs, final String attrName) {
		return attrs.stream().filter(attr -> attr.getAttributeName().equals(attrName)).findAny().isPresent();
	}

	protected static PrimaryTypeAttributeSpecification findAttribute(final HierarchicalPrimaryTypeSpecification ptSpec, final String attrName) {
		return ptSpec.getAttributeSpecifications().stream().filter(attr -> attr.getName().equals(attrName)).findAny().orElse(null);
	}

	protected static PrimaryTypeAttributeSpecification findAttribute(final HierarchicalPrimaryTypeSpecification ptSpec, final String namespace, final String attrName) {
		return ptSpec.getAttributeSpecifications().stream().filter(attr -> attr.getName().equals(attrName) && namespace.equals(attr.getNamespace())).findAny().orElse(null);
	}

	protected static PrimaryTypeAction findAction(final List<PrimaryTypeAction> actions, final String actionName) {
		return actions.stream().filter(act -> act.getName().equals(actionName)).findAny().orElse(null);
	}

	protected static PrimaryTypeActionParameter findActionParameter(final List<PrimaryTypeActionParameter> parameters, final String paramName) {
		return parameters.stream().filter(attr -> attr.getName().equals(paramName)).findAny().orElse(null);
	}

	protected PrimaryTypeActionParameterSpecification findActionParameterSpec(final Collection<PrimaryTypeActionParameterSpecification> parameters, String paramName) {
		return parameters.stream().filter(attr -> attr.getName().equals(paramName)).findAny().orElse(null);
	}

	protected static ComplexDataTypeAttribute findStructMember(final List<ComplexDataTypeAttribute> structMembers, final String name) {
		return structMembers.stream().filter(attr -> attr.getName().equals(name)).findAny().orElse(null);
	}

	protected static EnumDataType findEdtInCombined(final CombinedEnumDataTypeDefinition combinedEdt, final String edtName) {
		return combinedEdt.getEnumDataType().stream().filter(edt -> edt.getName().equals(edtName)).findAny().orElse(null);
	}

	@SuppressWarnings("unchecked")
	protected static <T extends Constraint> T findConstraint(final Collection<Constraint> constraints, final Class<T> clazz) {
		return (T) constraints.stream().filter(constraint -> clazz.isAssignableFrom(constraint.getClass())).findAny().orElse(null);
	}

	@SuppressWarnings("unchecked")
	protected static <T extends Requires> T findRequires(final Collection<Requires> requires, final Class<T> clazz) {
		return (T) requires.stream().filter(constraint -> clazz.isAssignableFrom(constraint.getClass())).findAny().orElse(null);
	}

	protected static ChoiceType findChoice(final PrimaryTypeDefinition ptd, final String name) {
		return ptd.getChoice().stream().filter(ch -> ch.getName().equals(name)).findAny().orElse(null);
	}

	protected static ChoiceType findAddedOrReplacedChoice(final PrimaryTypeExtensionDefinition pted, final String name) {
		return pted.getChoiceHandling().getReplaceChoice().stream().filter(ch -> ch.getName().equals(name)).findAny().orElse(null);
	}

	protected static void assertChoiceRemoved(final PrimaryTypeExtensionDefinition pted, final String name) {
		if(!pted.getChoiceHandling().getRemoveChoice().stream().filter(ch -> ch.equals(name)).findAny().isPresent()) {
			fail("Could not find removed choice '" + name+ "'.");
		}
	}

	protected static ChoiceCaseType findCase(final ChoiceType choiceType, final String name) {
		return choiceType.getCase().stream().filter(ch -> ch.getName().equals(name)).findAny().orElse(null);
	}

	protected static ChoiceCasePrimaryTypeType findChoiceCasePrimaryTypeType(final ChoiceCaseType choiceCaseType, final String ptName) {
		return choiceCaseType.getAttributeOrPrimaryType().stream()
				.filter(o -> o instanceof ChoiceCasePrimaryTypeType)
				.map(o -> (ChoiceCasePrimaryTypeType) o)
				.filter(ptt -> ModelInfo.fromImpliedUrn(ptt.getPrimaryTypeUrn(), SchemaConstants.DPS_PRIMARYTYPE).getName().equals(ptName))
				.findAny().orElse(null);
	}

	protected static ChoiceCaseAttributeType findChoiceCaseAttributeType(final ChoiceCaseType choiceCaseType, final String attrName) {
		return choiceCaseType.getAttributeOrPrimaryType().stream()
				.filter(o -> o instanceof ChoiceCaseAttributeType)
				.map(o -> (ChoiceCaseAttributeType) o)
				.filter(at -> at.getName().equals(attrName))
				.findAny().orElse(null);
	}

	protected static Choice findChoice(final Collection<Choice> choices, final String name) {
		return choices.stream().filter(ch -> ch.getName().equals(name)).findAny().orElse(null);
	}

	protected static Case findCase(final Collection<Case> cases, final String name) {
		return cases.stream().filter(ch -> ch.getName().equals(name)).findAny().orElse(null);
	}

	protected static HierarchicalPrimaryTypeSpecification findCasePrimaryTypeSpec(final Collection<HierarchicalPrimaryTypeSpecification> pts, final String name) {
		return pts.stream().filter(pt -> pt.getModelInfo().getName().equals(name)).findAny().orElse(null);
	}

	protected static PrimaryTypeAttributeSpecification findCaseAttributeSpec(final Collection<PrimaryTypeAttributeSpecification> ptas, final String name) {
		return ptas.stream().filter(attr -> attr.getName().equals(name)).findAny().orElse(null);
	}

	protected static void assertContainmentsCreatedCount(final PrimaryTypeRelationshipDefinition rels, final int expected) {
		final int actual = rels.getContainment().size();

		if(actual != expected) {
			fail("Expected " + expected + " containment(s), but " + actual + " were generated.");
		}
	}

	protected static void assertAssociationsCreatedCount(final PrimaryTypeRelationshipDefinition rels, final int expected) {
		final int actual = rels.getAssociation().size();

		if(actual != expected) {
			fail("Expected " + expected + " association(s), but " + actual + " were generated.");
		}
	}

	protected static PrimaryTypeContainment findAndAssertContainment(final PrimaryTypeRelationshipDefinition ptrd, final String parentMocName, final String childMocName) {
		final PrimaryTypeContainment result = ptrd.getContainment().stream()
				.filter(ptc -> {
					final String childImpliedUrn = ptc.getChild().getPrimaryTypeUrn();
					return childMocName.equals(ModelInfo.fromImpliedUrn(childImpliedUrn, SchemaConstants.DPS_PRIMARYTYPE).getName());
				})
				.filter(ptc -> {
					final String parentImpliedUrn = ptc.getParent().getPrimaryTypeUrn();
					return parentMocName.equals(ModelInfo.fromImpliedUrn(parentImpliedUrn, SchemaConstants.DPS_PRIMARYTYPE).getName());
				})
				.findAny()
				.orElse(null);

		if(result == null) {
			fail("Could not find containment from parent '" + parentMocName + "' to child '" + childMocName + "'.");
		}

		return result;
	}

	protected static PrimaryTypeContainment findAndAssertContainment(final PrimaryTypeRelationshipDefinition ptrd, final String parentMocNamespace, final String parentMocName, final String childMocNamespace, final String childMocName) {
		final PrimaryTypeContainment result = ptrd.getContainment().stream()
				.filter(ptc -> {
					final ModelInfo child = ModelInfo.fromImpliedUrn(ptc.getChild().getPrimaryTypeUrn(), SchemaConstants.DPS_PRIMARYTYPE);
					return childMocNamespace.equals(child.getNamespace()) && childMocName.equals(child.getName());
				})
				.filter(ptc -> {
					final ModelInfo parent = ModelInfo.fromImpliedUrn(ptc.getParent().getPrimaryTypeUrn(), SchemaConstants.DPS_PRIMARYTYPE);
					return parentMocNamespace.equals(parent.getNamespace()) && parentMocName.equals(parent.getName());
				})
				.findAny()
				.orElse(null);

		if(result == null) {
			fail("Could not find containment from parent '" + parentMocName + "' to child '" + childMocName + "'.");
		}

		return result;
	}

	protected static void assertContainment(final PrimaryTypeContainment ptc, final TransformerContext context, final Properties actualNmtProperties,
			final String parentModelNamespace, final String parentModelName, final String parentModelVersion,
			final String childModelNamespace, final String childModelName, final String childModelVersion,
			final Long expectedMin, final Long expectedMax) {

		assertRequiresTargetType(context, actualNmtProperties, ptc.getRequiresTargetType());

		final String parentExpectedImpliedUrn = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, parentModelNamespace, parentModelName, parentModelVersion).toImpliedUrn();
		assertEquals(parentExpectedImpliedUrn, ptc.getParent().getPrimaryTypeUrn());

		final String childExpectedImpliedUrn = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, childModelNamespace, childModelName, childModelVersion).toImpliedUrn();
		assertEquals(childExpectedImpliedUrn, ptc.getChild().getPrimaryTypeUrn());

		final MinMaxValue minSizeConstraint = ptc.getMinSizeConstraint();
		final Long actualMin = minSizeConstraint != null ? minSizeConstraint.getValue() : null;
		assertEquals(expectedMin, actualMin);

		final MinMaxValue maxSizeConstraint = ptc.getMaxSizeConstraint();
		final Long actualMax = maxSizeConstraint != null ? maxSizeConstraint.getValue() : null;
		assertEquals(expectedMax, actualMax);
	}

	protected static PrimaryTypeAssociation findAssociation(final PrimaryTypeRelationshipDefinition ptrd, final String soughtAssocName) {
		return ptrd.getAssociation().stream()
				.filter(pta -> pta.getName().equals(soughtAssocName))
				.findAny().orElse(null);
	}

	protected static void assertRequiresTargetType(final TransformerContext context, final Properties actualNmtProperties, final RequiresTargetType requiresTargetType) {
		assertNotNull(requiresTargetType);

		if(context != null) {
			assertTrue(requiresTargetType.getCategory().equals(context.getTarget().getCategory()));
			assertTrue(requiresTargetType.getType().equals(context.getTarget().getType()));
			assertTrue(requiresTargetType.getVersion().equals(context.getTarget().getVersion()));
		} else {
			assertTrue(requiresTargetType.getCategory().equals("NODE"));
			assertTrue(requiresTargetType.getType().equals(actualNmtProperties.getProperty(YangTransformer2PluginProperties.TARGET_TYPE)));
			assertTrue(requiresTargetType.getVersion().equals(actualNmtProperties.getProperty(YangTransformer2PluginProperties.TARGET_VERSION)));
		}
	}

	protected static void assertHasSupportedMim(final ManagementInformationModelInformation mimInfo, final String namespace, final String version, final String originalModelUrn) {

		final boolean present = mimInfo.getSupportedMims().getMimMappedTo().stream()
				.filter(smt -> {
					return smt.getNamespace().equals(namespace) && smt.getVersion().equals(version) && smt.getOriginalModelUrn().equals(originalModelUrn);
				})
				.findAny().isPresent();

		if(!present) {
			fail("Could not find supported MIM entry with ns '" + namespace + "', version '" + version + "', original-urn '" + originalModelUrn + "'.");
		}
	}

	protected static void assertHasSupportedMimModule(final ManagementInformationModelInformation mimInfo, final String soughtNamespace, final String soughtVersion, final String soughtModelUrn, final String soughtYamName) {
		assertHasSupportedMimModule(mimInfo, soughtNamespace, soughtVersion, soughtModelUrn, CfmMimInfoSupport.CONFORMANCE_IMPLEMENT, CfmMimInfoSupport.FEATURE_HANDLING_ALL_REMOVED, Collections.emptyList(), soughtYamName);
	}

	protected static void assertHasSupportedMimModule(final ManagementInformationModelInformation mimInfo, final String soughtNamespace, final String soughtVersion, final String soughtModelUrn, final String soughtConformance, final String soughtFeatureHandling, final List<String> soughtFeatures, final String soughtYamName) {

		final boolean present = mimInfo.getSupportedMims().getMimMappedTo().stream()
				.filter(smt -> {
					return smt.getType().equals(CfmMimInfoSupport.YAM_TYPE_MODULE)
							&& smt.getNamespace().equals(soughtNamespace)
							&& smt.getVersion().equals(soughtVersion)
							&& smt.getOriginalModelUrn().equals(soughtModelUrn)
							&& smt.getConformance().equals(soughtConformance)
							&& smt.getFeatureHandling().equals(soughtFeatureHandling)
							&& smt.getFeatures().equals(soughtFeatures)
							&& smt.getYamName().equals(soughtYamName);
				})
				.findAny().isPresent();

		if(!present) {
			fail("Could not find supported MIM entry for module with ns '" + soughtNamespace + "', version '" + soughtVersion + "' and various properties.");
		}
	}

	protected static void assertHasSupportedMimSubmodule(final ManagementInformationModelInformation mimInfo, final String soughtNamespace, final String soughtVersion, final String soughtModelUrn, final String soughtYamName) {

		final boolean present = mimInfo.getSupportedMims().getMimMappedTo().stream()
				.filter(smt -> {
					return smt.getType().equals(CfmMimInfoSupport.YAM_TYPE_SUBMODULE)
							&& smt.getNamespace().equals(soughtNamespace)
							&& smt.getVersion().equals(soughtVersion)
							&& smt.getOriginalModelUrn().equals(soughtModelUrn)
							&& smt.getYamName().equals(soughtYamName);
				})
				.findAny().isPresent();

		if(!present) {
			fail("Could not find supported MIM entry for submodule with ns '" + soughtNamespace + "', version '" + soughtVersion + "' and various properties.");
		}
	}

	protected static EnumDataTypeMember findAndAssertEnumMember(final EnumDataTypeDefinition edt, final String memberName) {
		final EnumDataTypeMember result = findEnumMember(edt, memberName);
		if(result == null) {
			fail("Could not find enum member '" + memberName + "'.");
		}
		return result;
	}

	protected static EnumDataTypeMember findEnumMember(final EnumDataTypeDefinition edt, final String memberName) {
		return edt.getMember().stream().filter(m -> m.getName().equals(memberName)).findAny().orElse(null);
	}

	protected static EnumDataTypeMember findEnumMember(final EnumDataType edt, final String memberName) {
		return edt.getMember().stream().filter(m -> m.getName().equals(memberName)).findAny().orElse(null);
	}

	protected static EnumDataTypeMember findAndAssertAddedEnumMember(final EnumDataTypeExtensionDefinition edtext, final String memberName) {
		final EnumDataTypeMember result = findAddedEnumMember(edtext, memberName);
		if(result == null) {
			fail("Did not find member with name '" + memberName + "'.");
		}
		return result;
	}

	protected static EnumDataTypeMember findAddedEnumMember(final EnumDataTypeExtensionDefinition edtext, final String memberName) {
		if(edtext.getEnumDataTypeExtension() == null) {
			return null;
		}
		return edtext.getEnumDataTypeExtension().getMember().stream()
				.filter(m -> m.getName().equals(memberName))
				.findAny().orElse(null);
	}

	protected static EnumDataTypeMember findAndAssertAddedEnumMember(final EnumDataTypeExtensionDefinition edtext, final String memberNamespace, final String memberName) {
		final EnumDataTypeMember result = findAddedEnumMember(edtext, memberNamespace, memberName);
		if(result == null) {
			fail("Did not find member with name '" + memberName + "' in namespace '" + memberNamespace + "'.");
		}
		return result;
	}

	protected static EnumDataTypeMember findAddedEnumMember(final EnumDataTypeExtensionDefinition edtext, final String memberNamespace, final String memberName) {
		if(edtext.getEnumDataTypeExtension() == null) {
			return null;
		}

		return edtext.getEnumDataTypeExtension().getMember().stream()
				.filter(m -> m.getName().equals(memberName))
				.filter(m -> m.getNamespace().equals(memberNamespace))
				.findAny().orElse(null);
	}

	protected static void assertEnumMemberRemoved(final EnumDataTypeExtensionDefinition edtext, final String memberNamespace, final String memberName) {

		if(edtext.getEnumDataTypeRemoval() == null) {
			fail("Did not find removed member with name '" + memberName + "' in namespace '" + memberNamespace + "'.");
		}

		final boolean present = edtext.getEnumDataTypeRemoval().getMemberWithNamespace().stream()
				.filter(m -> m.getName().equals(memberName))
				.filter(m -> m.getNamespace().equals(memberNamespace))
				.findAny().isPresent();

		if(!present) {
			fail("Did not find removed member with name '" + memberName + "' in namespace '" + memberNamespace + "'.");
		}
	}

	protected static EnumDataTypeMemberSpecification findAndAssertEnumMember(final EnumDataTypeSpecification edtSpec, final String name) {
		final EnumDataTypeMemberSpecification result = findEnumMember(edtSpec, name);
		if(result == null) {
			fail("Did not find member with name '" + name + "'.");
		}
		return result;
	}

	protected static EnumDataTypeMemberSpecification findEnumMember(final EnumDataTypeSpecification edtSpec, final String name) {
		return edtSpec.getAllMembers().stream().filter(member -> member.getMemberName().equals(name)).findAny().orElse(null);
	}

	protected static EnumDataTypeMemberSpecification findEnumMember(final EnumDataTypeSpecification edtSpec, final String namespace, final String name) {
		return edtSpec.getAllMembers().stream()
				.filter(member -> member.getMemberName().equals(name))
				.filter(member -> member.getMemberNamespace().equals(namespace))
				.findAny().orElse(null);
	}

	protected static void assertSingleContainmentParent(final HierarchicalPrimaryTypeSpecification child, final ModelInfo expected, final Long expectedMinElements, final Long expectedMaxElements) {

		final ArrayList<HierarchicalPrimaryTypeSpecification> parentsList = new ArrayList<>(child.getParentTypes());
		if(parentsList.size() != 1) {
			fail("Expected exactly one parent type only");
		}

		final HierarchicalPrimaryTypeSpecification parentSpec = parentsList.get(0);
		final ModelInfo actual = parentSpec.getModelInfo();

		if(!actual.getNamespace().equals(expected.getNamespace())) {
			fail("Expected '" + expected.toImpliedUri() + "' as parent, but got '" + actual.toImpliedUrn() + "'.");
		}
		if(!actual.getName().equals(expected.getName())) {
			fail("Expected '" + expected.toImpliedUri() + "' as parent, but got '" + actual.toImpliedUrn() + "'.");
		}
		if(!expected.getVersion().toString().equals("*") && !actual.getVersion().equals(expected.getVersion())) {
			fail("Expected '" + expected.toImpliedUri() + "' as parent, but got '" + actual.toImpliedUrn() + "'.");
		}
		if(expectedMinElements != null) {
			if(parentSpec.getMinimumCardinalityforChildType(child) != expectedMinElements) {
				fail("Expected '" + expectedMinElements + "' min-elements, but got '" + parentSpec.getMinimumCardinalityforChildType(child) + "'.");
			}
		}
		if(expectedMaxElements != null) {
			if(parentSpec.getMaximumCardinalityforChildType(child) != expectedMaxElements) {
				fail("Expected '" + expectedMaxElements + "' max-elements, but got '" + parentSpec.getMaximumCardinalityforChildType(child) + "'.");
			}
		}
	}

	protected static void assertHasContainmentParent(final HierarchicalPrimaryTypeSpecification child, final ModelInfo expected, final Long expectedMinElements, final Long expectedMaxElements) {

		final ArrayList<HierarchicalPrimaryTypeSpecification> parentsList = new ArrayList<>(child.getParentTypes());

		for(final HierarchicalPrimaryTypeSpecification parent : parentsList) {
			if(expected.equals(parent.getModelInfo())) {
				return;
			}
		}

		fail("Did not find '" + expected.toImpliedUri() + "' as containment parent.");
	}

	protected static <T extends Object> void assertValidValue(final EModelAttributeSpecification attr, final T value) {
		if(!isValidValue(attr, value)) {
			fail("Value '" + value + "' is not valid in ModelService.");
		}
	}

	protected static <T extends Object> void assertInvalidValue(final EModelAttributeSpecification attr, final T value) {
		if(isValidValue(attr, value)) {
			fail("Value '" + value + "' is valid in ModelService, although it should have been invalid.");
		}
	}

	protected static <T extends Object> boolean isValidValue(final EModelAttributeSpecification attr, final T value) {
		try {
			attr.getDataTypeSpecification().checkConstraints(value);
			return true;
		} catch (final ConstraintViolationException e) {}
		return false;
	}

	private static boolean areEqual(final Object expected, final Object actual) {

		if(expected == null && actual == null) {
			return true;
		}

		if(expected != null) {
			return expected.equals(actual);
		}

		return actual.equals(expected);
	}

	// ====================================================================================================================================
	// ====================================================================================================================================
	// ====================================================================================================================================

	private static final List<String> FINDINGS_TO_ALWAYS_IGNORE = Arrays.asList(
			ParserFindingType.P055_SUPERFLUOUS_STATEMENT.toString(),
			ParserFindingType.P101_EMPTY_DOCUMENTATION_VALUE.toString(),
			ParserFindingType.P114_TYPEDEF_NOT_USED.toString(),
			ParserFindingType.P115_TYPEDEF_USED_ONCE_ONLY.toString(),
			ParserFindingType.P132_GROUPING_NOT_USED.toString(),
			ParserFindingType.P133_GROUPING_USED_ONCE_ONLY.toString(),
			ParserFindingType.P143_ENUM_WITHOUT_VALUE.toString(),
			ParserFindingType.P144_BIT_WITHOUT_POSITION.toString(),
			ValidatorFindingTypeYang.S326_UNION_ONLY_HAS_SINGLE_MEMBER.toString(),
			ValidatorFindingTypeYang.S342_IDENTITY_NOT_USED.toString(),
			ValidatorFindingTypeYang.S352_FEATURE_NOT_USED.toString(),
			ValidatorFindingTypeYang.S362_DUPLICATE_SIBLING_IN_OTHER_NAMESPACE.toString(),
			ValidatorFindingTypeYang.S431_EMPTY_CHOICE.toString(),
			ValidatorFindingTypeYang.S432_CHOICE_HAS_SINGLE_BRANCH.toString(),
			ValidatorFindingTypeEri.E601_MISSING_VERSION_RELEASE_CORRECTION_FOR_CURRENT_REVISION.toString(),
			ValidatorFindingTypeEri.E647_NON_ERICSSON_MODULE_IMPORTS_ERICSSON_MODULE.toString()
			);

	/**
	 * Runs validation on the test modules before transformation is applied in order to find any
	 * syntax orders that might cause false test results.
	 */
	private void runPreTransformationValidation(final List<File> pathToImplementing, final List<File> pathToImporting) {

		final List<YangModel> yangModelFiles = new ArrayList<>();
		final FileBasedYangInputResolver implementingResolver = new FileBasedYangInputResolver(pathToImplementing, Arrays.asList(FileBasedYangInputResolver.FILE_EXTENSION_YANG));
		final FileBasedYangInputResolver importOnlyResolver = new FileBasedYangInputResolver(pathToImporting, Arrays.asList(FileBasedYangInputResolver.FILE_EXTENSION_YANG));
		implementingResolver.getResolvedYangInput().forEach(yangInput -> yangModelFiles.add(new YangModel(yangInput, ConformanceType.IMPLEMENT)));
		importOnlyResolver.getResolvedYangInput().forEach(yangInput -> yangModelFiles.add(new YangModel(yangInput, ConformanceType.IMPORT)));

		final ModifyableFindingSeverityCalculator severityCalculator = new ModifyableFindingSeverityCalculator();
		final FindingsManager findingsManager = new FindingsManager(severityCalculator);
		FINDINGS_TO_ALWAYS_IGNORE.forEach(severityCalculator::suppressFinding);
		additionalFindingsToIgnoreDuringValidation.forEach(severityCalculator::suppressFinding);

		final List<StatementClassSupplier> extensionCreators = Arrays.asList(new EricssonExtensionsClassSupplier(), new IetfExtensionsClassSupplier(), new ThreeGppExtensionsClassSupplier());
		final List<CustomProcessor> customProcessors = Arrays.asList(new EriCustomProcessor());
		final ParserExecutionContext parserContext = new ParserExecutionContext(findingsManager, extensionCreators, customProcessors);

		parserContext.setFailFast(false);
		parserContext.setIgnoreImportedProtocolAccessibleObjects(true);
		parserContext.setResolveAugments(true);
		parserContext.setResolveDeviations(true);
		parserContext.setRemoveSchemaNodesNotSatisfyingIfFeature(false);

		final YangDeviceModel yangDeviceModel = new YangDeviceModel("Parsed modules");
		yangDeviceModel.parseIntoYangModels(parserContext, yangModelFiles);

		final StandaloneValidatorContext standaloneValidatorContext = new StandaloneValidatorContext(parserContext);
		new StandaloneValidationManagerYang(yangDeviceModel).validate(standaloneValidatorContext);
		new StandaloneValidationManagerEri(yangDeviceModel).validate(standaloneValidatorContext);
		new StandaloneValidationManagerEnm(yangDeviceModel).validate(standaloneValidatorContext);

		final Set<Finding> allFindings = findingsManager.getAllFindings();
		if(!allFindings.isEmpty()) {
			allFindings.forEach(System.err::println);
			System.err.println("There are validation findings on the test models. Correct the models before running tests.");
			fail("There are validation findings on the test models. Correct the models before running tests.");
		}
	}

	// ====================================================================================================================================
	// ====================================================================================================================================
	// ====================================================================================================================================

	private static final String XXX_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/oss_targettype/NODE/XXX.xml";
	private static final String PCC_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/oss_targettype/NODE/PCC.xml";
	private static final String vDU_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/oss_targettype/NODE/vDU.xml";
	private static final String vCU_UP_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/oss_targettype/NODE/vCU-UP.xml";
	private static final String EPG_OI_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/oss_targettype/NODE/EPG-OI.xml";
	private static final String ER6274_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/oss_targettype/NODE/ER6274.xml";
	private static final String OSS_TOP__Relationships_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/dps_relationship/OSS_TOP/ManagedElement/ManagedElement-3.0.0.xml";
	private static final String OSS_TOP__NodeRoot_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/dps_primarytype/OSS_TOP/NodeRoot/NodeRoot-3.0.0.xml";
	private static final String OSS_TOP__MeContext_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/dps_primarytype/OSS_TOP/MeContext/MeContext-3.0.0.xml";
	private static final String OSS_TOP__ManagedElement_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/dps_primarytype/OSS_TOP/ManagedElement/ManagedElement-3.0.0.xml";
	private static final String OSS_NE_DEF__NeType_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/oss_edt/OSS_NE_DEF/NeType/NeType-2.0.0.xml";
	private static final String OSS_NE_DEF__PlatformType_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/oss_edt/OSS_NE_DEF/PlatformType/PlatformType-2.0.0.xml";
	private static final String ComTop__ManagedElement_Path = GENERATED_MODELS_ETC_MODEL_DIR + "/dps_primarytype/ComTop/ManagedElement/ManagedElement-10.22.1.xml";

	// ====================================================================================================================================

	protected void processAndDeployIntoModelService() {
		processAndDeployIntoModelService(true);
	}

	protected void processAndDeployIntoModelService(final boolean copyOverNrmStuff) {

		if(copyOverNrmStuff) {
			/*
			 * Copy over a few files we will need later on to satisfy dependencies
			 */
			copyTestEModelsOverToModelBuild("XXX.xml", XXX_Path);
			copyTestEModelsOverToModelBuild("PCC.xml", PCC_Path);
			copyTestEModelsOverToModelBuild("vDU.xml", vDU_Path);
			copyTestEModelsOverToModelBuild("vCU-UP.xml", vCU_UP_Path);
			copyTestEModelsOverToModelBuild("EPG-OI.xml", EPG_OI_Path);
			copyTestEModelsOverToModelBuild("ER6274.xml", ER6274_Path);
			copyTestEModelsOverToModelBuild("OSS_TOP__Relationships.xml", OSS_TOP__Relationships_Path);
			copyTestEModelsOverToModelBuild("OSS_TOP__NodeRoot__300.xml", OSS_TOP__NodeRoot_Path);
			copyTestEModelsOverToModelBuild("OSS_TOP__MeContext__300.xml", OSS_TOP__MeContext_Path);
			copyTestEModelsOverToModelBuild("OSS_TOP__ManagedElement__300.xml", OSS_TOP__ManagedElement_Path);
			copyTestEModelsOverToModelBuild("OSS_NE_DEF__NeType__200.xml", OSS_NE_DEF__NeType_Path);
			copyTestEModelsOverToModelBuild("OSS_NE_DEF__PlatformType__200.xml", OSS_NE_DEF__PlatformType_Path);
			copyTestEModelsOverToModelBuild("ComTop__ManagedElement_10221.xml", ComTop__ManagedElement_Path);
		}

		/*
		 * Run all the models through model-processing - will also create us a nice JAR (thanks!)
		 */
		final ProcessingRunner processingRunner = new ProcessingRunner(new File(GENERATED_MODELS_BASE_DIR), true);
		processingRunner.initialise();

		/*
		 * Disable some logging from model processing.
		 * 
		 * Fun fact - java.util.logging holds on to loggers with WeakReferencesMap. So unless we hold on to
		 * the logger while model processing runs it will be GC'ed straight away, meaning that the logger
		 * that model processing will fetch will be a different object, which will log of course...
		 */
		final Logger logger = Logger.getLogger(ModelsMetaInformationGenerator.class.getPackage().getName());
		logger.setLevel(Level.SEVERE);
		processingRunner.process(GENERATED_JAR_FILE);

		/*
		 * Deploy the lot into MDT.
		 */
		final Properties properties = new Properties();
		properties.setProperty(XmlBasedModelRepo.PROP_XML_REPO_PATH, MDT_REPO);
		properties.setProperty(XmlBasedModelRepo.PROP_CREATE_REPO_IF_NEEDED, "true");
		properties.setProperty(ModelRepo.PROP_READ_WRITE, "true");
		final ModelRepo modelRepo = ModelRepoFactory.getModelRepo(properties);

		System.setProperty(SystemProperties.AUTO_GENERATE_NAME_VERSION.getName(), "true");
		System.setProperty(SystemProperties.SEND_EVENTS_PROPERTY.getName(), "false");
		System.setProperty(SystemProperties.MODEL_REPO_CHECKSUM.getName(), MDT_REPORTS_BASE_DIR + "/repochecksum");
		System.setProperty(SystemProperties.REPORT_DIRECTORY_PROPERTY.getName(), MDT_REPORTS_BASE_DIR);
		System.setProperty(SystemProperties.GENERATED_HTML_DIR.getName(), MDT_REPORTS_BASE_DIR + "/html");

		final DeploymentInputs deploymentInputs = new ModelDeploymentInputs(modelRepo, MDT_BASE_DIR, Arrays.asList(new File(GENERATED_JAR_FILE)), InvocationContext.OSS_INSTALLATION);
		final MdtApplication mdtApplication = new MdtApplication(deploymentInputs);
		mdtApplication.run();

		/*
		 * Fire up Model Service and do a force re-load of the model repo and force-flush the cache.
		 * This is necessary as Model Service internally holds on to the repo and cached data in static
		 * variables. If multiple test cases are executed, these need to be cleaned, or there will be
		 * wrong test results.
		 */
		System.setProperty(ModelRepoBasedModelMetaInformation.MODEL_REPO_PATH_PROPERTY, MDT_REPO);
		this.modelService = new ModelServiceImpl();
		ModelRepoRetriever.reloadModelRepo();
		CacheManager.flushCaches();
	}

	private static final String TEST_EMODELS = "src/test/resources/test-emodels/";

	private static void copyTestEModelsOverToModelBuild(final String fileName, final String to) {

		final File source = new File(TEST_EMODELS + fileName);
		final File target = new File(to);

		try {
			target.getParentFile().mkdirs();
			Files.deleteIfExists(target.toPath());
			Files.createFile(target.toPath());
			Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (final Exception ex) {
			ex.printStackTrace();
			fail("Problem copying EModel test files for model-processing...?");
		}
	}
}
