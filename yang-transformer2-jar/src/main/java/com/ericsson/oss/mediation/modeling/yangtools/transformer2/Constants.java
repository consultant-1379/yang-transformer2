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

package com.ericsson.oss.mediation.modeling.yangtools.transformer2;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;

public class Constants {

	/*
	 * 
	 * Some constants used in various parts of the code. Don't change these.
	 */

	public static final String ONE_ZERO_ZERO = "1.0.0";

	public static final String OSS_TOP = "OSS_TOP";

	public static final String COM_TOP = "ComTop";

	public static final String MANAGEDELEMENT = "ManagedElement";

	public static final String MECONTEXT = "MeContext";

	/**
	 * The OSS_TOP::ManagedElement is the model-inherits super-model for any generated mock ManagedElement and
	 * (usually) for the 3GPP ManagedElement. Model inheritance requires an exact version for the super-model,
	 * hence the 3.0.0.
	 */
	public static final ModelInfo OSS_TOP_MANAGEDELEMENT_300 = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, OSS_TOP, MANAGEDELEMENT, "3.0.0");

	/**
	 * The ComTop::ManagedElement is the containment-parent for all top-level data nodes if no mock
	 * ManagedElement was generated, and no explicit containment parent has been supplied. Transport
	 * nodes typically do this.
	 */
	public static final ModelInfo COM_TOP_MANAGEDELEMENT_STAR = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, COM_TOP, MANAGEDELEMENT, "*");

	/**
	 * The OSS_TOP::MeContext is the containment-parent for any generated mock ManagedElement.
	 */
	public static final ModelInfo OSS_TOP_MECONTEXT_STAR = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, OSS_TOP, MECONTEXT, "*");

	/**
	 * ModelInfo for any MO.
	 */
	public static final ModelInfo ANY_MANAGED_OBJECT = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, SchemaConstants.GLOBAL_MODEL_NAMESPACE, "ManagedObject", "*");

	/*
	 * =========================================================================================
	 * 
	 * The following are used in meta-data included in the generated models. The meta-data will
	 * typically be used by mediation to direct its behaviour. The names of the constants below
	 * should therefore never be changed!
	 */

	/**
	 * The MOC is the mock ManagedElement that serves as singleton root of the data tree. All top-level data
	 * nodes in the schema will be containment parents of this MOC.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE. The meta-value is always null.
	 */
	public static final String META_MOCK_MANAGED_ELEMENT = "YANG_MOCK_MANAGED_ELEMENT";

	/**
	 * The MOC or attribute is defined at the top-level of the schema.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE, DPS PRIMARYTYPE ATTRIBUTE. The meta-value
	 * is always null.
	 */
	public static final String META_TOP_LEVEL_DATA_NODE = "YANG_TOP_LEVEL_DATA_NODE";

	/**
	 * The container used for the MOC is a presence container.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE. The meta-value is always null.
	 */
	public static final String META_PRESENCE_CONTAINER = "YANG_PRESENCE_CONTAINER";

	/**
	 * The container used for the MOC is a non-presence container.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE. The meta-value is always null.
	 */
	public static final String META_NON_PRESENCE_CONTAINER = "YANG_NON_PRESENCE_CONTAINER";

	/**
	 * The list used for the MOC does not declare any keys. It will typically be read-only.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE. The meta-value is always null.
	 */
	public static final String META_KEYLESS_LIST = "YANG_KEYLESS_LIST";

	/**
	 * The names of multiple keys on a list.
	 * <p>
	 * This property will only ever be set on an OSS CDT if it represents a 3GPP struct-sequence, and it
	 * has multiple keys. (It will never be set on a DPS PRIMARYTYPE as there is in-build support for
	 * multiple keys in the schema).
	 * <p>
	 * The meta-value is a string holding the key names (separated from each other by the space character).
	 */
	public static final String META_MULTIPLE_KEY_NAMES = "YANG_MULTIPLE_KEY_NAMES";

	/**
	 * This key attribute is artificial and does not exist in the YANG model (as the MOC is derived from
	 * a container, or the MOC is derived from a keyless-list).
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE ATTRIBUTE. The meta-value is always null.
	 */
	public static final String META_ARTIFIAL_KEY = "YANG_ARTIFIAL_KEY";

	/**
	 * This OSS EDT has been generated from YANG 'identity' statements (as opposed to generated from a
	 * leaf of 'type enumeration').
	 * <p>
	 * This property may be set on a OSS EDT. The meta-value is always null.
	 */
	public static final String META_DERIVED_FROM_IDENTITIES = "YANG_DERIVED_FROM_IDENTITIES";

	/**
	 * The netconflib management agent used by the router nodes does not handle the namespaces of identities
	 * correctly. It expects as identityref values a prefix that is equal to the prefix of the module that
	 * defines the identity. This forces mediation to use a specific prefix, instead of a random-generated
	 * prefix.
	 * <p>
	 * This property may be set on a OSS EDT MEMBER representing an identity. The meta-value is the prefix
	 * used by the YANG module that declares the identity.
	 */
	public static final String META_ORIG_MODULE_PREFIX = "YANG_ORIG_MODULE_PREFIX";

	/**
	 * Denotes that the entries of the sequence attribute have been explicitly ordered by the user, and
	 * their order is important.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE ATTRIBUTE. The meta-value is always null.
	 */
	public static final String META_USER_ORDERED = "YANG_USER_ORDERED";

	/**
	 * Denotes that the action has an auto-generated struct type as return type, with the struct type
	 * containing the real output of the action or RPC.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE ACTION. The meta-value is always null.
	 */
	public static final String META_RETURN_TYPE_STRUCT_AUTO_GENERATED = "YANG_RETURN_TYPE_STRUCT_AUTO_GENERATED";

	/**
	 * Denotes that the action is realized by an RPC on the node (as opposed to an action).
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE ACTION. The meta-value is always null.
	 */
	public static final String META_RPC = "YANG_RPC";

	/**
	 * Denotes that the MOC is the result of a re-aggregation of an IOC and its 'attributes' container.
	 * This requires special handling in mediation.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE. The meta-value is always null.
	 */
	public static final String META_3GPP_REAGGREGATED_IOC = "YANG_3GPP_REAGGREGATED_IOC";

	/**
	 * Denotes that this non-unique sequence attribute is realized on the node using the 3GPP "Wrap"
	 * mechanism. 
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE ATTRIBUTE, CDT ATTRIBUTE, DPS PT ACTION PARAMETER.
	 * The meta-value is always null.
	 */
	public static final String META_3GPP_NON_UNIQUE_SEQUENCE = "YANG_3GPP_NON_UNIQUE_SEQUENCE";

	/**
	 * Denotes that the result of an action is a non-unique sequence, realized on the node using the
	 * 3GPP "Wrap" mechanism. 
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE ACTION. The meta-value is always null.
	 */
	public static final String META_3GPP_ACTION_RETURN_TYPE_IS_NON_UNIQUE_SEQUENCE = "YANG_3GPP_ACTION_RETURN_TYPE_IS_NON_UNIQUE_SEQUENCE";

	/**
	 * Denotes the original name of the element. It is possible for MOCs, attributes, actions, parameters
	 * and struct members to get a name different from what it is in the YANG model. This requires mediation
	 * to perform mapping.
	 * <p>
	 * This applies mostly to the names of MOCs, where duplication of data node names within a namespace has
	 * resulted in the usage of the $$ syntax for a MOC name. However, it is also possible to explicitly
	 * rename data nodes in the schema.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE, DPS PT ATTRIBUTE, DPS PT ACTION, DPS PT ACTION PARAMETER,
	 * CDT MEMBER. The meta-value is the original name of the element as defined in the YANG model.
	 */
	public static final String META_ORIGINAL_NAME = "YANG_ORIGINAL_NAME";

	/**
	 * Denotes the DPS PrimaryType that is the containment parent of all top-level data nodes of the node
	 * (which could be ComTop::ManagedElement (for the routers), a mock ManagedElement (for EPG),
	 * OSS_TOP::MeContext (for Shared-CNF)).
	 * <p>
	 * This property may be set on a CFM MIMINFO model. The meta-value is the implied URN of the DPS PT that
	 * is the containment parent of all top-level data nodes of the node. Example: "//OSS_TOP/MeContext/*"
	 */
	public static final String META_CONTAINMENT_PARENT_URN = "YANG_CONTAINMENT_PARENT_URN";

	/**
	 * Denotes that the name of the element is not the same name as defined originally in the YAM,
	 * and that the actual name is attached to <i>data</i>.
	 * <p>
	 * This property may be set on a DPS PRIMARYTYPE.
	 */
	public static final String META_REAL_NAME_ATTACHED_TO_DATA = "YANG_REAL_NAME_ATTACHED_TO_DATA";

	/**
	 * Denotes that the namespace of the element is not the same namespace as defined originally in the YAM,
	 * and that the actual namespace is attached to <i>data</i>.
	 * <p>
	 * This property may be set on all model elements that can have a namespace.
	 */
	public static final String META_REAL_NAMESPACE_ATTACHED_TO_DATA = "YANG_REAL_NAMESPACE_ATTACHED_TO_DATA";

	/**
	 * Denotes the path to a data node that has not been transformed. Typically used by mediation to not read
	 * up the data node from the network element.
	 * <p>
	 * The path is formed by one or more path segments. Each path segment contains both namespace and name of the
	 * data node in an encoded manner. Special encoding is necessary as in YANG the value of a namespace can
	 * contain pretty much any character, making it impossible to use a dedicated single separator character between
	 * path segments. For decoding of each path into segments, use class NamespaceNamePath.
	 * <p>
	 * This property may be set multiple times on a DPS PRIMARYTYPE EXT. A numeric suffix is used for each entry:<br/>
	 * YANG_NON_GENERATED_DATA_NODE_PATH.0<br/>
	 * YANG_NON_GENERATED_DATA_NODE_PATH.1<br/>
	 * YANG_NON_GENERATED_DATA_NODE_PATH.2<br/>
	 * ...and so on.
	 */
	public static final String META_NON_GENERATED_DATA_NODE_PATH = "YANG_NON_GENERATED_DATA_NODE_PATH.";

	/**
	 * Indicates that the attribute contains information about the actor that created the enclosing MOC instance.
	 * If this property is present, and the attribute value at runtime is 'true', indicates that the MO instance
	 * has been system-created, and ENM may not be able to delete the MO instance, and/or may not be able to modify
	 * certain parts of the MO instance.
	 * <p>
	 * Typically, model-driven applications in ENM may wish to use this property to indicate to the user, in a
	 * suitable manner, potential edit/delete restrictions of the MO instance.
	 * <p>
	 * This property may be set on a DPS PT ATTRIBUTE. The meta-value is always null.
	 */
	/*
	 * Note the value of the constant does not have a prefix of "YANG_" like other constants of this class have.
	 * This is correct, as this functionality is not YANG-specific (it could likewise be valid for ECIM).
	 */
	public static final String META_INSTANCE_SYSTEM_CREATED_INDICATOR = "INSTANCE_SYSTEM_CREATED_INDICATOR";

	/*
	 * =====================================================================================================
	 * 
	 * The following are used in textual output from the transformer. These are not API, but somebody might
	 * want to grep on those, so it's probably a good idea to not change these.
	 */

	public static final String TEXT_MODULES = "[MODULES]";

	public static final String TEXT_SETTINGS = "[SETTINGS]";

	public static final String TEXT_STATS = "[STATS]";

	public static final String TEXT_PARSER = "[PARSER]";

	public static final String TEXT_TRANSFORMER = "[TRANSFORMER]";

	public static final String TEXT_REMOVED_STRUCT_IN_STRUCT = "[REMOVED (struct-in-struct)]";

	public static final String TEXT_REMOVED_DATA_TYPE_CHANGE = "[REMOVED (data-type-change)]";

	public static final String TEXT_REMOVED_NON_PERSISTENT = "[REMOVED (non-persistent)]";

	public static final String TEXT_REMOVED_RPC_NAMESPACE = "[REMOVED (rpc-namespace)]";

	public static final String TEXT_REMOVED_IF_FEATURE = "[REMOVED (if-feature)]";

	public static final String TEXT_REMOVED_NS_EXCLUDE_LISTED = "[REMOVED (namespace-exclude-listed)]";

	public static final String TEXT_REMOVED_EXPLICIT = "[REMOVED (explicit)]";

	public static final String TEXT_REMOVED_EMPTY = "[REMOVED (empty)]";

	public static final String TEXT_REMOVED_DEVIATED_OUT = "[REMOVED (deviated-out)]";

	public static final String TEXT_REMOVED_UNRELIABLE = "[REMOVED (unreliable-generation)]";

	public static final String TEXT_REPLACED = "[REPLACED]";

	public static final String TEXT_RENAMED = "[RENAMED]";

	public static final String TEXT_RENAMED_EXPLICIT = "[RENAMED (explicit)]";

	public static final String TEXT_NOT_GENERATED_WRONG_LOCATION = "[NOT-GENERATED (wrong-location)]";

	public static final String TEXT_NOT_GENERATED_DEFAULT_VALUE = "[NOT-GENERATED (default-value)]";

	public static final String TEXT_GENERATED_AS_STRING = "[GENERATED-AS-STRING]";

	public static final String TEXT_IGNORED_SCHEMA_SUPPORT = "[IGNORED (missing-schema-support)]";

	public static final String TEXT_IGNORED_UNRESOLVABLE = "[IGNORED (unresolvable)]";

	public static final String TEXT_IGNORED_NOTIFIED_BROKEN_CHAIN = "[IGNORED (broken notification chain)]";
}
