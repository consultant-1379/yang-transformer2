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

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.SupportedMimType;

public class ExcludeHandler {

	/**
	 * Filters the supplied modules and submodules to remove any module having a namespace that matches any
	 * of the exclude-listed namespace patterns.
	 */
	public static List<YangModel> getNonExcludedModules(final List<String> possiblyWildcardedNamespaces, final List<YangModel> modules) {

		final List<Pattern> excludePatterns = getExcludePatterns(possiblyWildcardedNamespaces);
		if(excludePatterns.isEmpty()) {
			return modules;
		}

		return modules.stream()
				.filter(yangModel -> {
					final String moduleNamespace = yangModel.getYangModelRoot().getNamespace();
					return !isNamespaceExcluded(excludePatterns, moduleNamespace); })
				.collect(Collectors.toList());
	}

	/**
	 * Filters the supplied MIM types to remove any entry having a namespace that matches any of the
	 * exclude-listed namespace patterns.
	 */
	public static List<SupportedMimType> getNonExcludedSupportedMimTypes(final List<String> possiblyWildcardedNamespaces, final List<SupportedMimType> supportedMims) {

		final List<Pattern> patterns = getExcludePatterns(possiblyWildcardedNamespaces);
		if(patterns.isEmpty()) {
			return supportedMims;
		}

		return supportedMims.stream()
				.filter(supportedMim -> {
					final String namespace = supportedMim.getNamespace();
					return !isNamespaceExcluded(patterns, namespace); })
				.collect(Collectors.toList());
	}

	static List<Pattern> getExcludePatterns(final List<String> possiblyWildcardedNamespaces) {
		return possiblyWildcardedNamespaces.stream().map(Pattern::compile).collect(Collectors.toList());
	}

	static boolean isNamespaceExcluded(final List<Pattern> patterns, final String namespace) {

		for(final Pattern pattern : patterns) {
			if(pattern.matcher(namespace).matches()) {
				return true;
			}
		}

		return false;
	}
}
