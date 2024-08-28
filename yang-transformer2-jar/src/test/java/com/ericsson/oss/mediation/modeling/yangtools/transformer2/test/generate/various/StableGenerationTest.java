package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.generate.various;

import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaUtil;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;

public class StableGenerationTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "various/";

	@Test
	public void test___stable_generation___schema_nodes_reordered() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N671_UNSUPPORTED_EXTENSION.toString());

		final TransformerContext context1 = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "stable-generation-order/one")));
		YangTransformer2.transform(context1);

		final Map<String, Integer> generatedOne = getGeneratedModelsHash();

		cleanup();
		setUp();

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N661_RPC_NOT_SUPPORTED.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N671_UNSUPPORTED_EXTENSION.toString());

		final TransformerContext context2 = createContextWith3ppModules(Arrays.asList(new File(TEST_SUB_DIR + "stable-generation-order/two")));
		YangTransformer2.transform(context2);

		final Map<String, Integer> generatedTwo = getGeneratedModelsHash();

		if(generatedOne.size() != generatedTwo.size()) {
			fail("Didn't generate the same number of models");
		}

		for(final Entry<String, Integer> entryOne : generatedOne.entrySet()) {
			if(entryOne.getKey().startsWith(SchemaConstants.CFM_MIMINFO)) {		// the cfm miminfo is a reflection of the input, so usually unstable, so ignore.
				continue;
			}
			final Integer hashTwo = generatedTwo.get(entryOne.getKey());
			if(!entryOne.getValue().equals(hashTwo)) {
				fail("Different hashes for model " + entryOne.getKey());
			}
		}
	}

	/**
	 * In this test a module ("one") will be extended by means of a submodule and another module. This happens
	 * twice, with different revisions and content of the submodule and other module. The expectation is that
	 * the contents of the models generated is the *exact* same if they have the exact same URN.
	 */
	@Test
	public void test___stable_generation___base_generated_same() {

		final TransformerContext context1 = createContext(Arrays.asList(
				new File(TEST_SUB_DIR + "stable-generation-base/one.yang"),
				new File(TEST_SUB_DIR + "stable-generation-base/submodule-two@2021-11-11.yang"),
				new File(TEST_SUB_DIR + "stable-generation-base/three@2021-05-05.yang")),
			Collections.emptyList(), DEFAULT_TARGET_TYPE, "v1", true);
		YangTransformer2.transform(context1);

		final Map<String, Integer> generatedOne = getGeneratedModelsHash();

		cleanup();
		setUp();

		final TransformerContext context2 = createContext(Arrays.asList(
				new File(TEST_SUB_DIR + "stable-generation-base/one.yang"),
				new File(TEST_SUB_DIR + "stable-generation-base/submodule-two@2021-12-12.yang"),
				new File(TEST_SUB_DIR + "stable-generation-base/three@2021-06-06.yang")),
			Collections.emptyList(), DEFAULT_TARGET_TYPE, "v2", true);
		YangTransformer2.transform(context2);

		final Map<String, Integer> generatedTwo = getGeneratedModelsHash();

		final Set<String> commonModelUrns = new HashSet<>(generatedOne.keySet());
		commonModelUrns.retainAll(generatedTwo.keySet());

		/*
		 * 2x DPS PRIMARYTYPE
		 * 2x OSS EDT for the identities
		 */
		assertSize(4, commonModelUrns);

		for(final String urn : commonModelUrns) {
			final Integer hash1 = generatedOne.get(urn);
			final Integer hash2 = generatedTwo.get(urn);

			if(!hash1.equals(hash2)) {
				fail("Different hashes for model " + urn);
			}
		}
	}

	private Map<String, Integer> getGeneratedModelsHash() {

		final Map<String, Integer> result = new HashMap<>();

		for(final File schemaDir : new File(GENERATED_MODELS_ETC_MODEL_DIR).listFiles()) {
			final String schema = schemaDir.getName();

			if(schema.equals(SchemaConstants.NET_YANG)) {
				continue;
			}

			final boolean versioned = SchemaUtil.areModelsVersionedFor(schema);
			for(final File namespaceDir : schemaDir.listFiles()) {
				final String namespace = namespaceDir.getName();
				for(final File nameFile : namespaceDir.listFiles()) {
					final String name = nameFile.getName();
					if(versioned) {
						for(final File versionFile : nameFile.listFiles()) {
							final String version = versionFile.getName();
							result.put(schema + "/" + namespace + "/" + name + "/" + version, hashFile(versionFile));
						}
					} else {
						result.put(schema + "/" + namespace + "/" + name, hashFile(nameFile));
					}
				}
			}
		}

		return result;
	}

	private Integer hashFile(final File file) {

		try {
			final byte[] bytes = Files.readAllBytes(file.toPath());

			int hash = 0;
			for(int i = 0; i < bytes.length; ++i) {
				hash = (hash * 31) + bytes[i];
			}

			return hash;

		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		return 0;
	}

}
