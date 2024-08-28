package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Test;

import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;

public class PropertyUtilsTest {

	@Test
	public void test___various() {

		assertFalse(PropertyUtils.listsAreEqual(null, Collections.emptyList(), null));
		assertFalse(PropertyUtils.listsAreEqual(Collections.emptyList(), null, null));
		assertTrue(PropertyUtils.listsAreEqual(null, null, null));

		try {
			PropertyUtils.requireNotNullNotEmpty(null);
			fail("Expected exception");
		} catch (final Exception expected) {}

		try {
			PropertyUtils.requireNotNullNotEmpty("");
			fail("Expected exception");
		} catch (final Exception expected) {}

		assertEquals("", PropertyUtils.getTrimmedOrigOrEmpty(null));
		assertEquals("", PropertyUtils.getTrimmedOrigOrEmpty(""));
		assertEquals("", PropertyUtils.getTrimmedOrigOrEmpty("    "));
		assertEquals("abc", PropertyUtils.getTrimmedOrigOrEmpty(" abc "));
		assertEquals("abc", PropertyUtils.getTrimmedOrigOrEmpty("abc"));
	}
}
