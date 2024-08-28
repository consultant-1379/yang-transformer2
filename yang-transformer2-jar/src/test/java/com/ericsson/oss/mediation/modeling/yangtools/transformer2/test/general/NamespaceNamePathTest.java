package com.ericsson.oss.mediation.modeling.yangtools.transformer2.test.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.NamespaceNamePath;

public class NamespaceNamePathTest {

	@Test
	public void test___namespace_name_path() {

		assertEquals(0, NamespaceNamePath.fromEscapedString(new NamespaceNamePath().toEscapedString()).getPathSegments().size());
		assertEquals(1, NamespaceNamePath.fromEscapedString(new NamespaceNamePath().addPathSegment("ns1", "name1").toEscapedString()).getPathSegments().size());
		assertEquals(2, NamespaceNamePath.fromEscapedString(new NamespaceNamePath().addPathSegment("ns1", "name1").addPathSegment("ns2", "name2").toEscapedString()).getPathSegments().size());
		assertEquals(3, NamespaceNamePath.fromEscapedString(new NamespaceNamePath().addPathSegment("ns1", "name1").addPathSegment("ns2", "name2").addPathSegment("ns2", "name2").toEscapedString()).getPathSegments().size());

		try {
			NamespaceNamePath.fromEscapedString(null);
			fail("Expected exception");
		} catch (final Exception expected) {}

		try {
			NamespaceNamePath.fromEscapedString("?");
			fail("Expected exception");
		} catch (final Exception expected) {}

		try {
			NamespaceNamePath.fromEscapedString("1ns1");
			fail("Expected exception");
		} catch (final Exception expected) {}

		try {
			NamespaceNamePath.fromEscapedString(new NamespaceNamePath().toEscapedString()).addPathSegment("ns1", "name1");
			fail("Expected exception");
		} catch (final Exception expected) {}

		try {
			new NamespaceNamePath().addPathSegment(null, "name1");
			fail("Expected exception");
		} catch (final Exception expected) {}

		try {
			new NamespaceNamePath().addPathSegment("ns1", null);
			fail("Expected exception");
		} catch (final Exception expected) {}


		final NamespaceNamePath path1 = NamespaceNamePath.fromEscapedString(new NamespaceNamePath().addPathSegment("ns1", "name1").toEscapedString());
		assertEquals(1, path1.getPathSegments().size());
		assertEquals("ns1", path1.getPathSegments().get(0).namespace);
		assertEquals("name1", path1.getPathSegments().get(0).name);

		final NamespaceNamePath path2 = NamespaceNamePath.fromEscapedString(new NamespaceNamePath().addPathSegment("ns1", "name1").addPathSegment("ns2", "name2").toEscapedString());
		assertEquals(2, path2.getPathSegments().size());
		assertEquals("ns1", path2.getPathSegments().get(0).namespace);
		assertEquals("name1", path2.getPathSegments().get(0).name);
		assertEquals("ns2", path2.getPathSegments().get(1).namespace);
		assertEquals("name2", path2.getPathSegments().get(1).name);

		final NamespaceNamePath path3 = NamespaceNamePath.fromEscapedString(new NamespaceNamePath().addPathSegmentAtStart("ns1", "name1").addPathSegmentAtStart("ns2", "name2").toEscapedString());
		assertEquals(2, path3.getPathSegments().size());
		assertEquals("ns2", path3.getPathSegments().get(0).namespace);
		assertEquals("name2", path3.getPathSegments().get(0).name);
		assertEquals("ns1", path3.getPathSegments().get(1).namespace);
		assertEquals("name1", path3.getPathSegments().get(1).name);

		final NamespaceNamePath path4 = NamespaceNamePath.fromEscapedString(new NamespaceNamePath().addPathSegment(":/\\ :", "//:").addPathSegment("\\\\//", " : ").toEscapedString());
		assertEquals(2, path4.getPathSegments().size());
		assertEquals(":/\\ :", path4.getPathSegments().get(0).namespace);
		assertEquals("//:", path4.getPathSegments().get(0).name);
		assertEquals("\\\\//", path4.getPathSegments().get(1).namespace);
		assertEquals(" : ", path4.getPathSegments().get(1).name);

		final NamespaceNamePath path5 = NamespaceNamePath.fromEscapedString(new NamespaceNamePath().addPathSegment("http://tail-f.com/confd", "confd").addPathSegment("urn:_3gpp:sa5", "ManagedElement").toEscapedString());
		assertEquals(2, path5.getPathSegments().size());
		assertEquals("http://tail-f.com/confd", path5.getPathSegments().get(0).namespace);
		assertEquals("confd", path5.getPathSegments().get(0).name);
		assertEquals("urn:_3gpp:sa5", path5.getPathSegments().get(1).namespace);
		assertEquals("ManagedElement", path5.getPathSegments().get(1).name);

		final NamespaceNamePath path6 = new NamespaceNamePath().addPathSegment(":/\\ :", "//:");
		assertEquals(path6.toString(), path6.toUnescapedString());

		assertFalse(path5.getPathSegments().get(0).equals(null));
		assertFalse(path5.getPathSegments().get(0).equals("a-string"));
	}

	@Test
	public void test___namespace_name_path___sits_below() {

		final NamespaceNamePath path1 = new NamespaceNamePath();
		final NamespaceNamePath path2 = new NamespaceNamePath().addPathSegment("ns1", "name1");
		final NamespaceNamePath path3 = new NamespaceNamePath().addPathSegment("ns2", "name2").addPathSegment("ns3", "name3");

		assertFalse(new NamespaceNamePath().sitsBelow(path1));
		assertFalse(new NamespaceNamePath().sitsBelow(path2));
		assertFalse(new NamespaceNamePath().sitsBelow(path3));

		assertTrue(new NamespaceNamePath().addPathSegment("ns1", "name1").sitsBelow(path1));
		assertFalse(new NamespaceNamePath().addPathSegment("ns1", "name1").sitsBelow(path2));
		assertFalse(new NamespaceNamePath().addPathSegment("ns1", "name1").sitsBelow(path3));

		assertTrue(new NamespaceNamePath().addPathSegment("ns1", "name1").addPathSegment("ns1b", "name1b").sitsBelow(path1));
		assertTrue(new NamespaceNamePath().addPathSegment("ns1", "name1").addPathSegment("ns1b", "name1b").sitsBelow(path2));
		assertFalse(new NamespaceNamePath().addPathSegment("ns 1", "name 1").addPathSegment("ns1b", "name1b").sitsBelow(path2));

		assertFalse(new NamespaceNamePath().addPathSegment("ns2", "name2").addPathSegment("ns3", "name3").sitsBelow(path3));
		assertTrue(new NamespaceNamePath().addPathSegment("ns2", "name2").addPathSegment("ns3", "name3").addPathSegment("ns3b", "name3b").sitsBelow(path3));
	}
}
