package com.ericsson.oss.mediation.modeling.yangtools.transformer2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A utility class to encode and decode path names containing namespaces. In YANG, namespaces can contain pretty
 * much any character, making it difficult to use any one separator character. The solution is to escape special
 * characters in the namespaces (and names) before flattening it into a string. Use this class to help with
 * escaping and un-escaping.
 */
public class NamespaceNamePath {

	private static final char SLASH = '/';
	private static final char BACKSLASH = '\\';
	private static final char COLON = ':';

	/**
	 * Creates an immutable NamespaceNamePath object from the supplied escaped representation of the path.
	 */
	public static NamespaceNamePath fromEscapedString(final String input) {

		final List<PathSegment> segments = new ArrayList<>();
		if(input.isEmpty()) {
			return new NamespaceNamePath(segments);
		}

		final List<String> strings = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		for(int i=1; i<input.length(); ++i) {
			char c = input.charAt(i);
			switch(c) {
			case SLASH:
			case COLON:
				strings.add(sb.toString());
				sb = new StringBuilder();
				break;
			case BACKSLASH:
				c = input.charAt(++i);
			default:
				sb.append(c);
			}
		}

		strings.add(sb.toString());
		if(strings.size() % 2 == 1) {
			throw new IllegalArgumentException();
		}

		for(int i=0; i<strings.size() / 2; ++i) {
			segments.add(new PathSegment(strings.get(i*2), strings.get(i*2+1)));
		}

		return new NamespaceNamePath(segments);
	}

	private final List<PathSegment> segments;

	/**
	 * Creates a NamespaceNamePath that can be modified by adding path segments.
	 */
	public NamespaceNamePath() {
		segments = new ArrayList<>();
	}

	private NamespaceNamePath(final List<PathSegment> segments) {
		this.segments = Collections.unmodifiableList(segments);
	}

	/**
	 * Adds a segment at the end of the path.
	 */
	public NamespaceNamePath addPathSegment(final String namespace, final String name) {
		segments.add(new PathSegment(namespace, name));
		return this;
	}

	/**
	 * Adds a segment at the start of the path.
	 */
	public NamespaceNamePath addPathSegmentAtStart(final String namespace, final String name) {
		segments.add(0, new PathSegment(namespace, name));
		return this;
	}

	public List<PathSegment> getPathSegments() {
		return Collections.unmodifiableList(segments);
	}

	/**
	 * Returns whether this path points to a data node in the data tree that is below, and in the same branch,
	 * as the data node denoted by the other path. I.e. this path here is an extension of the other path.
	 * <p>
	 * In order for this path to sit below the other path, this path must have more segments than the other path,
	 * and the path segments of the other path and this path must match.
	 */
	public boolean sitsBelow(final NamespaceNamePath otherPath) {

		final List<PathSegment> thisPathSegments = this.getPathSegments();
		final List<PathSegment> otherPathSegments = otherPath.getPathSegments();

		if(thisPathSegments.size() <= otherPathSegments.size()) {
			return false;
		}

		for(int i=0; i<otherPathSegments.size(); ++i) {
			if(!thisPathSegments.get(i).equals(otherPathSegments.get(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return toUnescapedString();
	}

	/**
	 * Returns a flattened and escaped representation of the path. Use fromEscapedString() to re-create
	 * the path subsequently.
	 */
	public String toEscapedString() {

		final StringBuilder sb = new StringBuilder();

		segments.forEach(segment -> {
			sb.append(SLASH);
			appendEscaped(sb, segment.namespace);
			sb.append(COLON);
			appendEscaped(sb, segment.name);
		});

		return sb.toString();
	}

	/**
	 * Returns a flattened representation of the path. This is typically only useful for logging purposes,
	 * as the generated string cannot be parsed-back into a path.
	 */
	public String toUnescapedString() {

		final StringBuilder sb = new StringBuilder();

		segments.forEach(segment -> {
			sb.append(SLASH).append(segment.namespace);
			sb.append(COLON).append(segment.name);
		});

		return sb.toString();
	}

	private void appendEscaped(final StringBuilder sb, final String input) {
		for(int i=0; i<input.length(); ++i) {
			final char c = input.charAt(i);
			switch(c) {
			case SLASH:
			case BACKSLASH:
			case COLON:
				sb.append(BACKSLASH);
			default:
				sb.append(c);
			}
		}
	}

	public static class PathSegment {

		public final String namespace;
		public final String name;

		PathSegment(final String namespace, final String name) {
			this.namespace = Objects.requireNonNull(namespace);
			this.name = Objects.requireNonNull(name);
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof PathSegment)) {
				return false;
			}
			return this.namespace.equals(((PathSegment)obj).namespace) && this.name.equals(((PathSegment)obj).name);
		}
	}
}
