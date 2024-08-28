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

package com.ericsson.oss.mediation.modeling.yangtools.transformer2.nmt;

import com.ericsson.oss.mediation.modeling.tools.networkmodeltransformer.NetworkModelTransformerProperties;

/**
 * Defines the Network Model Transformer properties supported by the YANG Transformer.  
 */
public class YangTransformer2PluginProperties {
	
	// ----------------------------- Mandatory ------------------------------

	/**
	 * The target version. This is defined using a NMT property.
	 */
	public static final String TARGET_TYPE = NetworkModelTransformerProperties.NE_TYPE;

	/**
	 * The target version. This is defined using a NMT property.
	 * <p>
	 * <b>NOTE:</b> This property is not required when the YANG Transformer is invoked via API method <i>generateSupportedModelInformation</i>.
	 */
	public static final String TARGET_VERSION = NetworkModelTransformerProperties.TARGET_MODEL_IDENTITY;

	/**
	 * One or more paths, typically directories, denoting the file system location of all modules
	 * of conformance type IMPLEMENT. Multiple paths are separated by comma.
	 */
	public static final String IMPLEMENTING_MODULES = "yangTransformer.implementingModuleDir";

	/**
	 * The directory into which models shall be generated. This is defined using a NMT property.
	 * <p>
	 * <b>NOTE:</b> This property is not required when the YANG Transformer is invoked via API method <i>generateSupportedModelInformation</i>.
	 */
	public static final String OUTPUT_DIR = NetworkModelTransformerProperties.OUTPUT_PATH;

	// ----------------------------- Optional ------------------------------

	/**
	 * One or more paths, typically directories, denoting the file system location of all modules
	 * of conformance type IMPORT. Multiple paths are separated by comma.
	 * <p>
	 * Does not have a default value.
	 */
	public static final String IMPORTING_MODULES = "yangTransformer.importModuleDir";

	/**
	 * One or more paths, typically directories, denoting the file system location of all modules
	 * of conformance type IMPLEMENT. If supplied, will be used as source of YAMs when copying the
	 * YAMs from the input to the net_yang directory (as opposed to using the YAMs in IMPLEMENTING_MODULES).
	 * Multiple paths are separated by comma.
	 * <p>
	 * Supplying this property has no effect on the transformation - i.e. for purposes of
	 * transformation, the YAMs found in IMPLEMENTING_MODULES will be used.
	 * <p>
	 * This property would typically only be set when transformer is invoked as part of runtime
	 * integration of an NRM.
	 * <p>
	 * Does not have a default value.
	 */
	public static final String ORIGINAL_IMPLEMENTING_MODULES = "yangTransformer.originalImplementingModuleDir";

	/**
	 * One or more paths, typically directories, denoting the file system location of all modules
	 * of conformance type IMPORT. If supplied, will be used as source of YAMs when copying the
	 * YAMs from the input to the net_yang directory (as opposed to using the YAMs in IMPORTING_MODULES).
	 * Multiple paths are separated by comma.
	 * <p>
	 * Supplying this property has no effect on the transformation - i.e. for purposes of
	 * transformation, the YAMs found in IMPORTING_MODULES will be used.
	 * <p>
	 * This property would typically only be set when transformer is invoked as part of runtime
	 * integration of an NRM.
	 * <p>
	 * Does not have a default value.
	 */
	public static final String ORIGINAL_IMPORTING_MODULES = "yangTransformer.originalImportModuleDir";

	/**
	 * The file path to an XML or JSON file that contains Yang Library instance data.
	 * <p>
	 * Does not have a default value.
	 */
	public static final String YANG_LIBRARY = "yangTransformer.yangLibrary";

	/**
	 * The DPS PrimaryType namespace for the mock ManagedElement.
	 * <p>
	 * Does not have a default value.
	 */
	public static final String MOCK_MANAGED_ELEMENT_NAMESPACE = "yangTransformer.rootMoNameSpace";

	/**
	 * The implied URN of the containment parent of all top-level data nodes.
	 * <p>
	 * Does not have a default value.
	 */
	public static final String EXPLICIT_CONTAINMENT_PARENT_IMPLIED_URN = "yangTransformer.containmentParentImpliedUrn";

	/**
	 * A list of possibly wildcarded namespaces. Each schema node matching any of the supplied
	 * namespaces will be removed before transformation. Multiple entries are separated
	 * by a 'space' character.
	 * <p>
	 * The default value is "http://tail-f.*"
	 */
	public static final String EXCLUDED_NAMESPACES = "yangTransformer.blacklist";

	/**
	 * If 'true', will cause all schema nodes dependent on a feature to be removed before transformation.
	 * <p>
	 * The default value is 'true'.
	 * 
	 * @deprecated use property "ifFeatureHandling" instead.
	 */
	public static final String REMOVE_SCHEMA_NODES_HAVING_IF_FEATURE = "yangTransformer.skipIfFeature";

	/**
	 * Denotes how if-features shall be handled:
	 * <ul>
	 * <li>RETAIN_ALL - all schema nodes dependent on a feature are retained, and the if-feature is not evaluated.</li>
	 * <li>REMOVE_ALL - all schema nodes dependent on a feature are removed before transformation.</li>
	 * <li>CONFIGURED - the list of enabled features is taken from the Yang Library. All schema nodes dependent
	 *     on a feature not listed in the Yang Library are removed before transformation.</li>
	 * </ul>
	 * The default value is "REMOVE_ALL".
	 */
	public static final String IF_FEATURE_HANDLING = "yangTransformer.ifFeatureHandling";

	/**
	 * If 'true', will cause all NP-containers to be generated as 'system-created'.
	 * <p>
	 * The default value is 'true'.
	 */
	public static final String GENERATE_NP_CONTAINER_AS_SYSTEM_CREATED = "yangTransformer.markNPContainerAsSystemCreated";

	/**
	 * If set to 'true', will generate RPCs as actions on the containment parent of all top-level data nodes.
	 * <p>
	 * The default value is 'false'.
	 */
	public static final String GENERATE_RPCS = "yangTransformer.generateRpcs";

	/**
	 * A list of namespaces for which RPCs will be suppressed, i.e. not generated. The namespaces
	 * are wildcarded. Multiple namespaces are separated with the 'space' character. 
	 * <p>
	 * The default value is "urn:ietf:params:xml:ns:netconf:base:1.0 urn:ietf:params:xml:ns:yang:ietf-netconf-monitoring"
	 */
	public static final String EXCLUDED_RPC_NAMESPACES = "yangTransformer.blacklistedRpcNamespaces";

	/**
	 * If set to true, will re-aggregate 3GPP MOCs in-line with the YANG-style conventions laid down by 3GPP.
	 * <p>
	 * The default value is 'false'.
	 */
	public static final String APPLY_3GPP_HANDLING = "yangTransformer.apply3gppHandling";

	/**
	 * A list of schema nodes that will be renamed prior to processing. This might be necessary at times to
	 * give data nodes a better name (as the generated name might be too long, or include the module name).
	 * <p>
	 * The value supplied is a stringefied representation of a map, with map entries separated by the 'comma'
	 * character, and each entry key and entry value separated by the 'space' character. The entry key is a
	 * schema node identifier (for syntax, see SCHEMA_NODES_TO_REMOVE); the entry value is the new name of
	 * the data node.
	 * <p>
	 * Does not have a default value.
	 */
	public static final String SCHEMA_NODES_TO_RENAME = "yangTransformer.schemaNodesToRename";

	/**
	 * A list of schema nodes that will be removed prior to processing. This may be done to permanently suppress
	 * individual undesirable data nodes, or to remove from the schema some data nodes that cause problems with
	 * transformation.
	 * <p>
	 * Each entry denotes the absolute schema path to the data node. Data nodes within the path are separated
	 * by the '/' character. Namespaces (not prefixes or module name) may be used where necessary (e.g. if there
	 * is duplication in sibling data nodes). All schema nodes along the path must be specified, including any
	 * choice, case, input and output nodes. Examples:
	 * <p>
	 * /pm/group<br/>
	 * /urn:3gpp:sa5:_3gpp-common-managed-element:ManagedElement/GNBDUFunction/NRCellDU/attributes/ssbOffset<br/>
	 * /brm/backup-manager/import-backup/input/password<br/>
	 * /urn:ietf:params:xml:ns:yang:ietf-system:system/radius/server/transport/udp/udp<br/>
	 * <p>
	 * Multiple entries are separated by the 'comma' character.
	 * <p>
	 * Does not have a default value.
	 */
	public static final String SCHEMA_NODES_TO_REMOVE = "yangTransformer.schemaNodesToRemove";

	/**
	 * If set to true, will not use the $$ syntax for MOCs that are unique within a given namespace.
	 * Note that 3GPP MOCs will <b>never</b> use the $$ syntax, as 3GPP stipulates that all IOCs in
	 * a given namespace are uniquely named. Likewise, if the uniqueness of the MOC is a
	 * result of the deviation of another MOC in the same namespace and the same name, the $$ syntax
	 * will still be used.
	 * <p>
	 * The default value is 'true'
	 */
	public static final String DONT_USE_DOLLARDOLLAR_FOR_UNIQUE_MOCS = "yangTransformer.dontUseDollarDollarForUniqueMocs";

	/**
	 * If set to true, will aggregate all generated EDTs into combined EDTs.
	 * <p>
	 * The default value is 'false'
	 * 
	 * TODO: [COMBINED_EDT] once this is working in MS, flip this default to 'true' to always create them, to
	 * always save on models generated. Update the JavaDoc above accordingly.
	 */
	public static final String GENERATE_COMBINED_EDT = "yangTransformer.generateCombinedEdt";

	/**
	 * If set to true, will clean the name of modules, namespace and top-level containers where the node
	 * has injected at runtime the name of an application instance. 
	 * <p>
	 * The default value is 'false'.
	 */
	public static final String APPLY_NODE_APP_INSTANCE_NAME_HANDLING = "yangTransformer.applyNodeAppInstanceNameHandling";

	// ----------------------------- Unsupported (anymore) ------------------------------

	public static final String LEGACY_NS_PREFIX = "yangTransformer.namespacePrefix";

	public static final String LEGACY_SKIP_MISSING_IMPORTS = "yangTransformer.skipMissingImports";

	public static final String LEGACY_SKIP_MODULES_WITH_TOO_LONG_NAMES = "yangTransformer.skipModulesWithTooLongNames";

	public static final String LEGACY_INPUT_DIR = "yangTransformer.inputDir";

	public static final String LEGACY_INPUT_COMMON_MODELS_DIR = "yangTransformer.inputCommonModelsDir";

	// ----------------------------- Other ------------------------------

	/**
	 * This property indicates to the transformer to skip the transform operation.
	 * <p>
	 * The default value is 'false'
	 */
	public static final String SKIP_TRANSFORM = "yangTransformer.skip";

	/**
	 * This optional property indicates to the transformer the path to the model file adhering to the cfm_miminfo schema
	 * which contains additional supported MIMs that are not supplied as part of the input models.
	 * <p>
	 * <b>NOTE:</b><br>
	 * This property is only supported when the YANG Transformer is invoked via API method <i>generateSupportedModelInformation</i>.
	 * Supplying this property to API method <i>transform</i> will result in an exception.
	 * <p>
	 * Does not have a default value.
	 */
	public static final String SUPPORTED_MIMS_FILE = "yangTransformer.supportedMimsFile";
}
