package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractSingleDataType;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.AbstractValue;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelExtensionDefinition;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.YangModelRoot;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.yangdom.YangDomDocumentRoot;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.yangdom.YangDomElement;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.ForceGenerateIetfYangLibrary;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Generator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.NetYangHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.PreProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.ReadMe;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2Version;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.CfmMimInfoGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.ComplexTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.DataTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.DefaultValueGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EModelHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.EnumerationTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.IdentityRefGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.LeafRefHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeActionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeAttributeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeChoiceAndRequiresGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeExtensionGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.PrimaryTypeRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.TopologyInventoryRelationshipGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt.YangTransformer2PluginProperties;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.AppInstanceNameSupport;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.CfmMimInfoSupport;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExcludeHandler;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.IfFeatureHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.NamespaceAndIdentifier;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.SchemaTrimmer;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ThreeGPPSupport;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangLibraryHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;

/**
 * Increases code coverage only ... doesn't really do anything useful from a functional point of view...
 */
public class CodeCoverageTest {

	@Test
	public void test___increase_code_coverage_for_fail_safe_exceptions() {

		try {
			DefaultValueGenerator.translateToJavaObject(null, new UnknownSingleDataType(), "10", null);
			fail("Should have thrown");
		}
		catch (final Exception expected) {}

		try {
			DefaultValueGenerator.javaToAbstractValue(new HashMap<>());
			fail("Should have thrown");
		}
		catch (final Exception expected) {}

		try {
			DefaultValueGenerator.defaultsAreEqual(new UnknownValue(), new UnknownValue());
			fail("Should have thrown");
		}
		catch (final Exception expected) {}

		try {
			final YangDomDocumentRoot yangDomDocumentRoot = new YangDomDocumentRoot(null, null);
			final YangDomElement yangDomElement = new YangDomElement("type", "union", yangDomDocumentRoot, 0);

			final YangModelRoot yangModelRoot = new YangModelRoot(null, null);
			final YType yType = new YType(yangModelRoot, yangDomElement);

			DataTypeGenerator.generateAbstractSingleDataType(null, null, yType, null);
			fail("Should have thrown");
		}
		catch (final Exception expected) {}

		try {
			DataTypeGenerator.typesAreEqual(new UnknownDataType(), new UnknownDataType());
			fail("Should have thrown");
		}
		catch (final Exception expected) {}

		try {
			EModelHelper.getExtendedEModelClass(new UnknownEModelExtensionDefinition());
			fail("Should have thrown");
		}
		catch (final Exception expected) {}
	}

	@Test
	public void test___increase_code_coverage_for_constructors() {

		new Constants();
		new Generator();
		new PreProcessor();
		new ReadMe();
		new YangTransformer2();
		new ForceGenerateIetfYangLibrary();
		new YangTransformer2Version();
		new NetYangHelper();

		new CfmMimInfoGenerator();
		new ComplexTypeGenerator();
		new DataTypeGenerator();
		new DefaultValueGenerator();
		new EModelHelper();
		new EnumerationTypeGenerator();
		new IdentityRefGenerator();
		new LeafRefHelper();
		new PrimaryTypeActionGenerator();
		new PrimaryTypeAttributeGenerator();
		new PrimaryTypeChoiceAndRequiresGenerator();
		new PrimaryTypeExtensionGenerator();
		new PrimaryTypeGenerator();
		new PrimaryTypeRelationshipGenerator();
		new TopologyInventoryRelationshipGenerator();

		new YangTransformer2PluginProperties();

		new AppInstanceNameSupport();
		new ExcludeHandler();
		new ExtractionHelper();
		new PropertyUtils();
		new SchemaTrimmer();
		new ThreeGPPSupport();
		new YangNameVersionUtil();
		new IfFeatureHelper();
		new YangLibraryHelper();
		new CfmMimInfoSupport();
	}

	@Test
	public void test___increase_code_coverage_for_enums() {

		PreProcessor.ContainerOrListType.values();
		PreProcessor.ContainerOrListType.valueOf(PreProcessor.ContainerOrListType.MOC.toString());

		PreProcessor.Variant.values();
		PreProcessor.Variant.valueOf(PreProcessor.Variant.BASE.toString());

		TransformerContext.FeatureHandling.values();
		TransformerContext.FeatureHandling.valueOf(TransformerContext.FeatureHandling.CONFIGURED.toString());

		IfFeatureHelper.Operand.values();
		IfFeatureHelper.Operand.valueOf(IfFeatureHelper.Operand.AND.toString());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void test___increase_branch_coverage_for_namespaceandidentifier() {

		final NamespaceAndIdentifier nsai = new NamespaceAndIdentifier("ns", "name");

		assertFalse(nsai.equals(null));
		assertFalse(nsai.equals("Hello"));

		assertTrue(nsai.equals(nsai));
		assertTrue(nsai.equals(new NamespaceAndIdentifier("ns", "name")));

		assertFalse(nsai.equals(new NamespaceAndIdentifier("ns", "name2")));
		assertFalse(nsai.equals(new NamespaceAndIdentifier("ns2", "name")));
		assertFalse(nsai.equals(new NamespaceAndIdentifier("ns2", "name2")));
	}

	@SuppressWarnings("serial")
	public class UnknownDataType extends AbstractDataType {}

	@SuppressWarnings("serial")
	public class UnknownSingleDataType extends AbstractSingleDataType {}

	@SuppressWarnings("serial")
	public class UnknownValue extends AbstractValue {}

	@SuppressWarnings("serial")
	public class UnknownEModelExtensionDefinition extends EModelExtensionDefinition {}

}