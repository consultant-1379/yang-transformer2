package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.nmt;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ericsson.oss.mediation.modeling.yangtools.transformer2.TransformerContext;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.YangTransformer2;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils.TransformerTestUtil;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.enm.ValidatorFindingTypeEnm;
import com.ericsson.oss.mediation.modeling.yangtools.validator.standalone.yang.ValidatorFindingTypeYang;

public class NetworkFunctionDiscoveryTest extends TransformerTestUtil {

	private static final String TEST_SUB_DIR = TEST_MODULES_DIR + "nmt/network-function-discovery/";

	@Test
	public void test___simple___no_functions() {

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(0, context.getDiscoveredNetworkFunctions());
	}

	@Test
	public void test___simple___one_function() {

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("DU", "/ManagedElement/GNodeBDUFunction");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(1, context.getDiscoveredNetworkFunctions());
		assertTrue(context.getDiscoveredNetworkFunctions().contains("DU"));
	}

	@Test
	public void test___simple___one_function___wrong_capitalization() {

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("DU", "/Managedelement/GNodeBDuFunction");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(1, context.getDiscoveredNetworkFunctions());
		assertTrue(context.getDiscoveredNetworkFunctions().contains("DU"));
	}

	@Test
	public void test___simple___one_function___missing_leading_slash() {

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("PGW", "pgw");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(1, context.getDiscoveredNetworkFunctions());
		assertTrue(context.getDiscoveredNetworkFunctions().contains("PGW"));
	}

	@Test
	public void test___simple___one_function___wildcard_at_end_of_managedelement() {

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("DU", "/managedele*/GNodeBDUFunction");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(1, context.getDiscoveredNetworkFunctions());
		assertTrue(context.getDiscoveredNetworkFunctions().contains("DU"));
	}

	@Test
	public void test___simple___one_function___wildcard_at_end_of_both_path_elements___and_wrong_capitalization() {

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("DU", "/managedele*/GNODEBDU*");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(1, context.getDiscoveredNetworkFunctions());
		assertTrue(context.getDiscoveredNetworkFunctions().contains("DU"));
	}

	@Test
	public void test___simple___one_function___wildcard_in_the_middle___no_match() {

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("DU", "/ManagedElement/G*BDUFunction");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(0, context.getDiscoveredNetworkFunctions());
	}

	@Test
	public void test___simple___in_choice_case() {

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("FN-2", "/cont1/cont2");
		networkFunctionsToPaths.put("FN-3", "/cont1/leaf3");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(2, context.getDiscoveredNetworkFunctions());
		assertTrue(context.getDiscoveredNetworkFunctions().contains("FN-2"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("FN-3"));
	}

	@Test
	public void test___simple___in_choice_case___same_path_for_two_different_functions() {

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("foo", "/cont1/cont2");
		networkFunctionsToPaths.put("bar", "/cont1/cont2");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(2, context.getDiscoveredNetworkFunctions());
		assertTrue(context.getDiscoveredNetworkFunctions().contains("foo"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("bar"));
	}

	@Test
	public void test___simple___all() {

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("DU", "/ManagedElement/GNodeBDUFunction");
		networkFunctionsToPaths.put("CUUP", "/ManagedElement/GNodeBCUUPFunction");
		networkFunctionsToPaths.put("SBG", "/sbg-function-*");
		networkFunctionsToPaths.put("PGW", "/pgw");
		networkFunctionsToPaths.put("SGW", "/pgw/sgw");
		networkFunctionsToPaths.put("foo", "/cont1/cont2");
		networkFunctionsToPaths.put("bar", "/cont1/leaf3");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "simple-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(7, context.getDiscoveredNetworkFunctions());
		assertTrue(context.getDiscoveredNetworkFunctions().contains("DU"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("CUUP"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("SBG"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("PGW"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("SGW"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("foo"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("bar"));
	}

	// --------------------------------------------------------------------

	@Test
	public void test___complex___all() {

		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeYang.S363_DUPLICATE_CONTAINER_OR_LIST_IN_SAME_NAMESPACE.toString());
		additionallyIgnoreFindingDuringValidation(ValidatorFindingTypeEnm.N663_DUPLICATE_DATA_NODE.toString());

		final Map<String, String> networkFunctionsToPaths = new HashMap<>();
		networkFunctionsToPaths.put("DU", "/ManagedElement/GNodeBDUFunction");
		networkFunctionsToPaths.put("CUUP", "/ManagedElement/GNodeBCUUPFunction");
		networkFunctionsToPaths.put("NR-CELL", "/ManagedElement/GNodeBDUFunction/NrCellDU");
		networkFunctionsToPaths.put("PGW-XX", "/pgw-xx");
		networkFunctionsToPaths.put("PGW-YY", "/pgw-yy");
		networkFunctionsToPaths.put("SGW-XX", "/pgw*/sgw-xx");
		networkFunctionsToPaths.put("SGW-YY", "/pgw*/sgw-yy");

		final TransformerContext context = createContext(new File(TEST_SUB_DIR + "complex-discovery"));
		context.setNetworkFunctionsToPaths(networkFunctionsToPaths);

		YangTransformer2.generateCfmMimOnly(context);

		assertSize(7, context.getDiscoveredNetworkFunctions());
		assertTrue(context.getDiscoveredNetworkFunctions().contains("DU"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("CUUP"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("NR-CELL"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("PGW-XX"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("PGW-YY"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("SGW-XX"));
		assertTrue(context.getDiscoveredNetworkFunctions().contains("SGW-YY"));
	}

	
}
