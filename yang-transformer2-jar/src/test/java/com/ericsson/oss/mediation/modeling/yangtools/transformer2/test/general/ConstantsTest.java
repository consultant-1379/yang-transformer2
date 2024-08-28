package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;

@SuppressWarnings("squid:S3415")		// SQ doesn't like the constants below to be on the RHS of the assertEquals, although that is correct!
public class ConstantsTest {

	@Test
	public void test___constants_general() {

		/*
		 * ==========================================================================================
		 * 
		 *                                      WARNING
		 *
		 * If this test fails then some constants have been modified. This is very likely to
		 * cause significant issues at runtime. Any test failure must be very carefully evaluated.
		 * 
		 * ==========================================================================================
		 */

		assertEquals("1.0.0", Constants.ONE_ZERO_ZERO);

		assertEquals("OSS_TOP", Constants.OSS_TOP);

		assertEquals("ComTop", Constants.COM_TOP);

		assertEquals("ManagedElement", Constants.MANAGEDELEMENT);

		assertEquals("MeContext", Constants.MECONTEXT);

		assertEquals("//OSS_TOP/ManagedElement/3.0.0", Constants.OSS_TOP_MANAGEDELEMENT_300.toImpliedUrn());

		assertEquals("//ComTop/ManagedElement/*", Constants.COM_TOP_MANAGEDELEMENT_STAR.toImpliedUrn());

		assertEquals("//OSS_TOP/MeContext/*", Constants.OSS_TOP_MECONTEXT_STAR.toImpliedUrn());
	}

	@Test
	public void test___constants_meta() {

		/*
		 * ==========================================================================================
		 * 
		 *                                      WARNING
		 *
		 * If this test fails then some constants used for meta information have been modified. This
		 * will most likely break ENM mediation. Any test failure must be very carefully evaluated.
		 * 
		 * ==========================================================================================
		 */

		assertEquals("YANG_MOCK_MANAGED_ELEMENT", Constants.META_MOCK_MANAGED_ELEMENT);

		assertEquals("YANG_TOP_LEVEL_DATA_NODE", Constants.META_TOP_LEVEL_DATA_NODE);

		assertEquals("YANG_PRESENCE_CONTAINER", Constants.META_PRESENCE_CONTAINER);

		assertEquals("YANG_NON_PRESENCE_CONTAINER", Constants.META_NON_PRESENCE_CONTAINER);

		assertEquals("YANG_KEYLESS_LIST", Constants.META_KEYLESS_LIST);

		assertEquals("YANG_ARTIFIAL_KEY", Constants.META_ARTIFIAL_KEY);

		assertEquals("YANG_DERIVED_FROM_IDENTITIES", Constants.META_DERIVED_FROM_IDENTITIES);

		assertEquals("YANG_ORIG_MODULE_PREFIX", Constants.META_ORIG_MODULE_PREFIX);

		assertEquals("YANG_USER_ORDERED", Constants.META_USER_ORDERED);

		assertEquals("YANG_RETURN_TYPE_STRUCT_AUTO_GENERATED", Constants.META_RETURN_TYPE_STRUCT_AUTO_GENERATED);

		assertEquals("YANG_RPC", Constants.META_RPC);

		assertEquals("YANG_3GPP_REAGGREGATED_IOC", Constants.META_3GPP_REAGGREGATED_IOC);

		assertEquals("YANG_3GPP_NON_UNIQUE_SEQUENCE", Constants.META_3GPP_NON_UNIQUE_SEQUENCE);

		assertEquals("YANG_3GPP_ACTION_RETURN_TYPE_IS_NON_UNIQUE_SEQUENCE", Constants.META_3GPP_ACTION_RETURN_TYPE_IS_NON_UNIQUE_SEQUENCE);

		assertEquals("YANG_ORIGINAL_NAME", Constants.META_ORIGINAL_NAME);

		assertEquals("YANG_CONTAINMENT_PARENT_URN", Constants.META_CONTAINMENT_PARENT_URN);

		assertEquals("YANG_REAL_NAME_ATTACHED_TO_DATA", Constants.META_REAL_NAME_ATTACHED_TO_DATA);

		assertEquals("YANG_REAL_NAMESPACE_ATTACHED_TO_DATA", Constants.META_REAL_NAMESPACE_ATTACHED_TO_DATA);

		assertEquals("YANG_NON_GENERATED_DATA_NODE_PATH.", Constants.META_NON_GENERATED_DATA_NODE_PATH);

		assertEquals("INSTANCE_SYSTEM_CREATED_INDICATOR", Constants.META_INSTANCE_SYSTEM_CREATED_INDICATOR);
	}
}
