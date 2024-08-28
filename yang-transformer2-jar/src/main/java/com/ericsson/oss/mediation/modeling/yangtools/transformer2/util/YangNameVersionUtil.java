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

import java.util.Collections;
import java.util.Objects;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.common.info.XyzModelVersionInfo;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.tools.networkmodelidentityconverter.NetworkModelIdentityConverterImpl;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.SupportedMimType;

public class YangNameVersionUtil {

	/**
	 * Returns the EModel x.y.z version for a given YANG module. Will return:
	 * <ul>
	 * <li>The version/release/correction if it is present and it is an Ericsson module</li>
	 * <li>Otherwise, if the revision is present, the revision translated to x.y.z format</li>
	 * <li>Otherwise, 0.0.0</li> 
	 * </ul>
	 * 
	 * @param yamName The name of the module or submodule.
	 * @param versionReleaseCorrection The version/release/correction, placed into fields x/y/z respectively,
	 * 			if any, as extracted from the Ericsson version/release/correction extensions located under
	 *			the revision statement. May be null.
	 * @param yamRevision The revision of the module or submodule. May be null if the YAM does not
	 * 			have a revision.
	 */
	public static String getXyzVersionForYam(final String yamName, final XyzModelVersionInfo versionReleaseCorrection, final String yamRevision) {

		if(isEricssonModule(yamName) && versionReleaseCorrection != null) {
			return versionReleaseCorrection.toString();
		}

		if(yamRevision == null) {
			return "0.0.0";
		}

		return new NetworkModelIdentityConverterImpl().getEModelVersionForYang(Collections.singletonList(yamRevision));
	}

	/**
	 * Denotes whether the module is an Ericsson module. This decision is based on the EOI design rule that
	 * all Ericsson modules shall begin with "ericsson-". Note, however, that there is an edge case where
	 * cIMS modifies the name of a module at runtime, resulting in the module name beginning with "eric-".
	 */
	public static boolean isEricssonModule(final String yamName) {
		return yamName.startsWith("ericsson-") || yamName.startsWith("eric-");
	}

	/**
	 * Given the namespace declared by a YAM, returns the EModel namespace.
	 * 
	 * @param yamNamespace The namespace declared by the YAM. If the YAM is a submodule, the namespace
	 * 			of the module that owns the submodule.
	 */
	public static String getEModelNamespaceForYam(final String yamNamespace) {
		return Objects.requireNonNull(yamNamespace);
	}

	/**
	 * Creates the NET_YANG ModelInfo for a given YAM. The produced name follows the convention as laid down in
	 * the RFC. Examples of produced URNs:
	 * <p>
	 * /net_yang/urn%3a3gpp%3asa5%3a_3gpp-nr-nrm-nrcelldu/_3gpp-nr-nrm-nrcelldu@2019-09-03<br/>
	 * /net_yang/http%3a%3c%3ctail-f.com%3cyang%3ccommon/tailf-meta-extensions<br/>
	 * 
	 * @param yamNamespace If the YAM is a module: The namespace declared by the module; if the YAM is a
	 * 			submodule: The namespace of the module that owns the submodule.
	 * @param yamName The name of the YAM (the module name, or submodule name, respectively) 
	 * @param yamRevision The revision of the YAM (the module revision, or submodule revision, respectively). 
	 * 			May be null if the YAM does not have a revision. 
	 */
	public static ModelInfo getNetYangModelInfoForYam(final String yamNamespace, final String yamName, final String yamRevision) {
		final String modelName = yamRevision == null ? Objects.requireNonNull(yamName) : Objects.requireNonNull(yamName) + "@" + yamRevision;
		return new ModelInfo(SchemaConstants.NET_YANG, Objects.requireNonNull(yamNamespace), modelName);
	}

	/**
	 * @deprecated This method has moved to class CfmMimInfoSupport.
	 */
	@Deprecated
	public static SupportedMimType createSupportedMimTypeForModule(final String emodelNamespace, final String emodelXyzVersion, final ModelInfo yamModelInfo) {
		return CfmMimInfoSupport.createSupportedMimTypeForModule(emodelNamespace, emodelXyzVersion, yamModelInfo);
	}

	/**
	 * @deprecated This method has moved to class CfmMimInfoSupport.
	 */
	@Deprecated
	public static SupportedMimType createSupportedMimTypeForSubmodule(final String emodelNamespace, final String submoduleName, final String emodelXyzVersion, final ModelInfo submoduleYamModelInfo) {
		return CfmMimInfoSupport.createSupportedMimTypeForSubmodule(emodelNamespace, submoduleName, emodelXyzVersion, submoduleYamModelInfo);
	}

	/**
	 * This function has been introduced to handle cloud IMS, and <b>runtime</b> modifications made by them
	 * to the YAMG models exposed over the YANG Library.
	 * <p>
	 * In cloud IMS, the node application <b>instance name</b> is injected into the module name/namespace
	 * etc. when the app instance is deployed in the cluster. This completely messes up RI in ENM, which
	 * requires us to cleanse the names when transforming.
	 * <p>
	 * This affects the module name, namespace and top-level MO in the module.
	 * <p>
	 * More details can be found in study BDGS-21:007093 Uen. <b>Note:</b> The format was updated after the study
	 * completed. The pattern agreed with cIMS is now:
	 * <p>
	 * module/namespace/mo-name---instance-name---version
	 * <p>
	 * Some examples for names that have to be cleaned up:
	 * <p>
	 * Module name: "eric-ims-bgf---london3---2-2-5-280"  ->  "eric-ims-bgf-2-2-5-280" ('london3' is the instance name)<br/>
	 * Module namespace: "urn:ericsson:eric-ims-bgf---london3---2-2-5-280"  ->  "urn:ericsson:eric-ims-bgf-2-2-5-280"
	 * <p>
	 * The "---" can be reliably used as separator for the string to separate out the module name (or whatever),
	 * the instance name, and the version. The version will not be semantically parsed.
	 */
	public static String removeNodeAppInstanceName(final String input) {

		final String[] split = input.split("---");

		if(split.length != 3) {
			return input;
		}

		if(split[0].trim().isEmpty()) {
			return input;
		}
		if(split[1].trim().isEmpty()) {
			return input;
		}
		if(split[2].trim().isEmpty()) {
			return input;
		}

		return split[0].trim() + "-" + split[2].trim();
	}
}
