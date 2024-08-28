/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.mediation.modeling.yangtools.transformer2;

import com.ericsson.oss.itpf.modeling.common.info.XyzModelVersionInfo;

public class YangTransformer2Version {

	/**
	 * This constant, along with the mappings of namespace and version for each MIM, is used during the generation of the cfm_miminfo model's version.
	 * It ensures that a different URN is generated for the cfm_miminfo model when the transformer changes its behaviour.
	 * <p>
	 * The x, y, and z values in the constant must adhere to the rules below:
	 * <ol>
	 *  <li>An incompatible change to the transformer output results in the x value being stepped, and the y and z values to be reset to 0.
	 *  <li>A compatible change (typically a functional addition) to the transformer will leave the x value unchanged, but will step the y value and reset the z value.
	 *  <li>A minor bugfix to the produced output that has no significant consequences to the system results in the z value being stepped.
	 * </ol>
	 * 
	 * Version history:
	 * <ul>
	 * <li>1.1.4 - fix to make conditional action parameters non-mandatory</li>
	 * <li>1.1.3 - fix to generate non-notified 3GPP structs/struct-sequences</li>
	 * <li>1.1.2 - fix to generate meta-data on CDTs with multiple keys</li>
	 * <li>1.1.1 - fix to make conditional data nodes non-mandatory</li>
	 * <li>1.1.0 - discovery of network functions; listing of non-generated paths in meta-data</li>
	 * <li>1.0.0 - initial delivery of YT2</li>
	 * </ul>
	 */
	public static final XyzModelVersionInfo YANG_TRANSFORMER2_VERSION = new XyzModelVersionInfo(1, 1, 4);
}
