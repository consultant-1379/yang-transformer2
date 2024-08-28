/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.mediation.modeling.yangtools.transformer2.util;

import java.util.Objects;

/**
 * A simple class to encapsulate a namespace and identifier.
 */
public class NamespaceAndIdentifier {

	public final String namespace;
	public final String identifier;

	public NamespaceAndIdentifier(final String namespace, final String identifier) {
		this.namespace = Objects.requireNonNull(namespace);
		this.identifier = Objects.requireNonNull(identifier);
	}

	public String getNamespace() {
		return namespace;
	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof NamespaceAndIdentifier
				&& this.namespace.equals(((NamespaceAndIdentifier)other).namespace)
				&& this.identifier.equals(((NamespaceAndIdentifier)other).identifier);
	}
}
