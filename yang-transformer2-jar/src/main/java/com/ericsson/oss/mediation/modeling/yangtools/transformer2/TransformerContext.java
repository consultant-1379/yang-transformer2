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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.parser.input.FileBasedYangInputResolver;
import com.ericsson.oss.mediation.modeling.yangtools.parser.input.YangInput;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.util.StringHelper;
import com.ericsson.oss.mediation.modeling.yangtools.parser.yanglibrary.YangLibrary;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.NamespaceNamePath;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;

public class TransformerContext {

	/*
	 * Paths to the directories containing IMPLEMENTING modules. May be empty if we only generate the CFM MIMINFO.
	 */
	private final Set<YangInput> implementingYangInput;

	/*
	 * Paths to the directories containing IMPORT-ONLY modules. May be empty.
	 */
	private final Set<YangInput> importingYangInput;

	/*
	 * Directory where to write the generated models to. May be null in case we
	 * are not actually writing out models.
	 */
	private final File outDir;

	/*
	 * The target, denoting the target category, type and version.
	 */
	private final Target target;

	/*
	 * The extra supported MIM files. May or may not be present when generating the CFM MIMINFO.
	 */
	private final File supportedMimsFile;

	/**
	 * Constructor invoked for transformation.
	 */
	public TransformerContext(final List<File> implementingPaths, final List<File> importingPaths, final File outDir, final String targetType, final String targetVersion) {
		this.outDir = Objects.requireNonNull(outDir);
		this.target = new Target("NODE", Objects.requireNonNull(targetType).trim(), Objects.requireNonNull(targetVersion).trim());
		this.supportedMimsFile = null;

		this.implementingYangInput = resolveYangInput(Objects.requireNonNull(implementingPaths));
		this.importingYangInput = resolveYangInput(Objects.requireNonNull(importingPaths));
	}

	/**
	 * Constructor invoked for CFM MIM INFO generation-only.
	 */
	public TransformerContext(final List<File> implementingPaths, final List<File> importingPaths, final String targetType, final File supportedMimsFile) {
		this.outDir = null;
		this.target = new Target("NODE", Objects.requireNonNull(targetType).trim(), null);
		this.supportedMimsFile = supportedMimsFile;		// possibly null

		this.implementingYangInput = resolveYangInput(Objects.requireNonNull(implementingPaths));
		this.importingYangInput = resolveYangInput(Objects.requireNonNull(importingPaths));
	}

	private static Set<YangInput> resolveYangInput(final List<File> paths) {
		final FileBasedYangInputResolver resolver = new FileBasedYangInputResolver(paths, Arrays.asList(FileBasedYangInputResolver.FILE_EXTENSION_YANG));
		return resolver.getResolvedYangInput();
	}

	/**
	 * Returns the YANG Input for implementing modules. May return an empty Set, but never null.
	 */
	public Set<YangInput> getYangInputForImplementing() {
		return implementingYangInput;
	}

	/**
	 * Returns the YANG Input for importing modules. May return an empty Set, but never null.
	 */
	public Set<YangInput> getYangInputForImporting() {
		return importingYangInput;
	}

	public File getOutDir() {
		return outDir;
	}

	public Target getTarget() {
		return target;
	}

	public File getSupportedMimsFile() {
		return supportedMimsFile;
	}

	/**
	 * A helper class encapsulating the target's category, type and version. Not to be confused with the
	 * class of the same name as defined by Model Service, which is quite similar in purpose.
	 */
	public static class Target {
		/*
		 * The target category. Always required.
		 */
		private final String category;

		/*
		 * The target type. For example, "Shared-CNF". Always required.
		 */
		private final String type;

		/*
		 * The target version, also called TMI. Will be null if we only generate the CFM MIMINFO.
		 */
		private final String version;

		public Target(final String targetCategory, final String targetType, final String targetVersion) {
			this.category = Objects.requireNonNull(targetCategory);
			this.type = Objects.requireNonNull(targetType);
			this.version = targetVersion;
		}

		public String getCategory() {
			return category;
		}

		public String getType() {
			return type;
		}

		public String getVersion() {
			return version;
		}
	}

	// --------------------------------------------------------------------------------------

	/*
	 * Paths to the directories containing IMPLEMENTING modules that should be used when
	 * copying-over YAMs into the net_yang directory. May be empty, indicating that the
	 * YAMs in "implementingYangInput" will be used when copying.
	 */
	private Set<YangInput> originalImplementingYangInput = Collections.emptySet();

	/*
	 * Paths to the directories containing IMPORT-ONLY modules that should be used when
	 * copying-over YAMs into the net_yang directory. May be empty, indicating that the
	 * YAMs in "importingYangInput" will be used when copying.
	 */
	private Set<YangInput> originalImportingYangInput = Collections.emptySet();

	public void setOriginalImplementingFiles(final List<File> originalImplementingPaths) {
		this.originalImplementingYangInput = resolveYangInput(Objects.requireNonNull(originalImplementingPaths));
	}

	public void setOriginalImportingFiles(final List<File> originalImportingPaths) {
		this.originalImportingYangInput = resolveYangInput(Objects.requireNonNull(originalImportingPaths));
	}

	public Set<YangInput> getOriginalImplementingYangInput() {
		return originalImplementingYangInput;
	}

	public Set<YangInput> getOriginalImportingYangInput() {
		return originalImportingYangInput;
	}

	// --------------------------------------------------------------------------------------

	private ModelInfo mockManagedElement = null;

	/**
	 * Sets the namespace of the mock ManagedElement, and thereby causing it to be generated. The mock
	 * ManagedElement will implicitly become the containment parent of all top-level data nodes in
	 * the schema.
	 * <p>
	 * Defaults to null, i.e. a mock ManagedElement is not generated. 
	 */
	public void setMockManagedElementNamespace(final String namespace) {
		this.mockManagedElement = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, PropertyUtils.requireNotNullNotEmpty(namespace), Constants.MANAGEDELEMENT, Constants.ONE_ZERO_ZERO);
	}

	public boolean generateMockManagedElement() {
		return mockManagedElement != null;
	}

	public ModelInfo getMockManagedElement() {
		return mockManagedElement;
	}

	private ModelInfo containmentParent = null;

	/**
	 * Explicitly set the containment parent (the "parent MO") of all top-level data nodes.
	 * <p>
	 * Defaults to null, causing MO "ComTop::ManagedElement::*" to be the containment parent.
	 */
	public void setExplicitContainmentParent(final ModelInfo parent) {
		this.containmentParent = parent;
	}

	/**
	 * Returns the containment parent, if any, that has been explicitly supplied. May return null.
	 */
	public ModelInfo getExplicitContainmentParent() {
		return containmentParent;
	}

	/**
	 * The effective ModelInfo of the containment parent MO of all top-level data nodes. The
	 * returned value depends on what properties were fed into the transformer.
	 */
	public ModelInfo getEffectiveContainmentParent() {

		if(mockManagedElement == null && containmentParent == null) {
			/*
			 * No mock ManagedElement, and no explicit parent. This is what the routers are using.
			 * Their YANG top-level nodes hang under ComTop::ManagedElement.
			 */
			return Constants.COM_TOP_MANAGEDELEMENT_STAR;
		}

		if (mockManagedElement != null && containmentParent == null) {
			/*
			 * No containment parent, but a mock ManagedElement, so we use that. This is what the
			 * core nodes are typically doing, and the (legacy) vDU, vCUUP, vCUCP.
			 */
			return mockManagedElement;
		}

		if (mockManagedElement == null) {
			/*
			 * No mock ManagedElement, but we have an explicit containment parent, return that.
			 * Typically the containment parent would be the MeContext; used by Shared-CNF, for
			 * example.
			 */
			return containmentParent;
		}

		/*
		 * Invalid combination!
		 */
		throw new YangTransformer2Exception("Cannot supply both containment parent URN and also mock ME namespace.");
	}

	// --------------------------------------------------------------------------------------

	private List<String> excludedNamespaces = Collections.emptyList();

	/**
	 * A list of possibly wildcarded namespaces. Each schema node (containers, lists, leafs, leaf-lists,
	 * actions, rpcs) matching any of the supplied namespaces will be removed before transformation. The
	 * idea behind this is to completely hide the existence of specific modules from the user - e.g.,
	 * the tail-f modules.
	 * <p>
	 * The default value is an empty list.
	 */
	public void setExcludedNamespaces(final List<String> namespaces) {
		this.excludedNamespaces = Objects.requireNonNull(namespaces);
	}

	/**
	 * The (possibly wild-carded) namespaces for which data nodes shall not be generated.
	 */
	public List<String> getExcludedNamespaces() {
		return excludedNamespaces;
	}

	// --------------------------------------------------------------------------------------

	/**
	 * Setting this to true will cause all schema nodes to be removed that are dependent on a
	 * feature (i.e. those schema nodes having an 'if-feature' statement).
	 * <p>
	 * There should really be no actor using this method, keeping it for backwards-compatibility.
	 * 
	 * @deprecated Use setFeatureHandling() instead.
	 */
	@Deprecated
	public void setRemoveSchemaNodesHavingIfFeature(final boolean removeSchemaNodes) {
		setFeatureHandling(removeSchemaNodes ? FeatureHandling.REMOVE_ALL : FeatureHandling.RETAIN_ALL);
	}

	/**
	 * If true, any schema node depending on a if-feature will be removed from the schema.
	 * <p>
	 * There should really be no actor using this method, keeping it for backwards-compatibility.
	 * 
	 * @deprecated Use getFeatureHandling() instead.
	 */
	@Deprecated
	public boolean removeSchemaNodesHavingIfFeature() {
		return featureHandling == FeatureHandling.REMOVE_ALL;
	}

	private FeatureHandling featureHandling = FeatureHandling.RETAIN_ALL;

	/**
	 * Set how features shall be handled.
	 */
	public void setFeatureHandling(final FeatureHandling featureHandling) {
		this.featureHandling = Objects.requireNonNull(featureHandling);
	}

	/**
	 * Get how features shall be handled.
	 */
	public FeatureHandling getFeatureHandling() {
		return featureHandling;
	}

	public enum FeatureHandling {
		/**
		 * Completely ignore feature handling and retain all schema nodes that have an if-feature.
		 */
		RETAIN_ALL,
		/**
		 * Remove all schema nodes that have an if-feature statement.
		 */
		REMOVE_ALL,
		/**
		 * The information about the enabled features is taken from the Yang Library.
		 */
		CONFIGURED;

		/**
		 * Parses a string into a FeatureHandling object
		 */
		public static FeatureHandling fromString(final String input) {
			/*
			 * So what are the chances that designers will get the capitalization wrong, or use a hyphen instead
			 * of the underscore (or omit it altogether) in the POM or in the capabilities...
			 */
			final String cleaned = Objects.requireNonNull(input).replace("-", "").replace("_", "").replace(" ", "").toLowerCase();	// Keep it simple and not use a pattern
			switch(cleaned) {
			case "retainall":
				return FeatureHandling.RETAIN_ALL;
			case "removeall":
				return FeatureHandling.REMOVE_ALL;
			case "configured":
				return FeatureHandling.CONFIGURED;
			default:
				throw new RuntimeException("Illegal value '" + input + "' for feature handling. Use one of 'retain-all', 'remove-all' or 'configured'.");
			}
		}
	}

	// --------------------------------------------------------------------------------------

	private File yangLibraryInstanceDataFile;

	public void setYangLibraryInstanceData(final File yangLibraryInstanceDataFile) {
		this.yangLibraryInstanceDataFile = yangLibraryInstanceDataFile;
	}

	public File getYangLibraryInstanceDataFile() {
		return yangLibraryInstanceDataFile;
	}

	private YangLibrary topLevelYangLibrary = null;

	public void setTopLevelYangLibrary(final YangLibrary topLevelYangLibrary) {
		this.topLevelYangLibrary = topLevelYangLibrary;
	}

	public YangLibrary getTopLevelYangLibrary() {
		return topLevelYangLibrary;
	}

	// --------------------------------------------------------------------------------------

	private boolean generateNPcontainersAsSystemCreated = false;

	/**
	 * Setting this to true will cause all NP containers to be generated as 'system-created'.
	 * <p>
	 * Default value is FALSE.
	 */
	public void setGenerateNPcontainersAsSystemCreated(final boolean generateNPcontainersAsSystemCreated) {
		this.generateNPcontainersAsSystemCreated = generateNPcontainersAsSystemCreated;
	}

	public boolean generateNPcontainersAsSystemCreated() {
		return generateNPcontainersAsSystemCreated;
	}

	// --------------------------------------------------------------------------------------

	private boolean generateRpcs = false;

	/**
	 * If set to true, will generate DPS PT actions for RPCs. The actions will be generated on the
	 * supplied (or implied) containment-parent of the top-level data nodes (i.e. what is returned
	 * by getEffectiveContainmentParent()).
	 * <p>
	 * Default value is FALSE.
	 */
	public void setGenerateRpcs(final boolean generateRpcs) {
		this.generateRpcs = generateRpcs;
	}

	public boolean generateRpcs() {
		return generateRpcs;
	}

	// --------------------------------------------------------------------------------------

	private List<String> excludedNamespacesForRpcs = Collections.emptyList();

	/**
	 * If RPCs are generated, any RPC in a namespace supplied here will not be generated. This is similar to the
	 * namespace exclude-listing mechanism; however, here it only applies to RPCs. The thinking behind this is
	 * that certain IETF modules define a number of RFCs that we really don't want the operator to invoke, so we
	 * effectively hide them.
	 * <p>
	 * The default value is an empty list.
	 */
	public void setExcludedNamespacesForRpcs(final List<String> namespaces) {
		this.excludedNamespacesForRpcs = Objects.requireNonNull(namespaces);
	}

	public List<String> getExcludedNamespacesForRpcs() {
		return excludedNamespacesForRpcs;
	}

	// --------------------------------------------------------------------------------------

	private boolean apply3gppHandling = false;

	/**
	 * If set to true, will cause the transformer to re-aggregate any 3GPP list/container encountered.
	 * 3GPP IOCs are expected to be self-contained in a branch, starting with a top-level data node (e.g.
	 * 3GPP ManagedElement).
	 * <p>
	 * The transformer will make an educated guess to identify lists/containers that represents IOCs;
	 * where this fails, the top-level schema nodes denoting (or otherwise) 3GPP data nodes can be
	 * explicitly set as hint.
	 * <p>
	 * The default value is 'false'.
	 */
	public void setApply3gppHandling(boolean apply3gppHandling) {
		this.apply3gppHandling = apply3gppHandling;
	}

	public boolean apply3gppHandling() {
		return apply3gppHandling;
	}

	// --------------------------------------------------------------------------------------

	private Map<String, String> schemaNodesToRename = Collections.emptyMap();

	/**
	 * Sets the schema nodes that shall be renamed before processing. This might be necessary at times to make
	 * data nodes unique and to avoid MOCs with the exact same name being generated. One example of this the
	 * 'cmp' container inside the ADP DDC module.
	 * <p>
	 * Each map key is the absolute schema path to a data node that shall be renamed. For the syntax and
	 * examples, see the setSchemaNodesToRemove() method. The map key is the new name.
	 */
	public void setSchemaNodesToRename(final Map<String, String> schemaNodesToRename) {
		this.schemaNodesToRename = Objects.requireNonNull(schemaNodesToRename);
	}

	public Map<String, String> getSchemaNodesToRename() {
		return schemaNodesToRename;
	}

	// --------------------------------------------------------------------------------------

	private List<String> schemaNodesToRemove = Collections.emptyList();

	/**
	 * Sets the schema nodes that shall be removed from the schema before processing. This may be done to
	 * permanently suppress individual undesirable data nodes, or to remove from the schema some data nodes
	 * that cause problems with transformation.
	 * <p>
	 * Each entry denotes the absolute schema path to the data node. Data nodes are separated by the '/' character.
	 * Namespaces (not prefixes or module name) may be used where necessary (e.g. if there is duplication in data
	 * nodes). All schema nodes along the path must be specified, including any choice, case, input and output
	 * nodes. Examples:
	 * <p>
	 * /pm/group<br/>
	 * /urn:3gpp:sa5:_3gpp-common-managed-element:ManagedElement/GNBDUFunction/NRCellDU/attributes/ssbOffset<br/>
	 * /brm/backup-manager/import-backup/input/password<br/>
	 * /urn:ietf:params:xml:ns:yang:ietf-system:system/radius/server/transport/udp/udp<br/>
	 * <p>
	 * The default value is an empty list.
	 */
	public void setSchemaNodesToRemove(final List<String> schemaNodesToRemove) {
		this.schemaNodesToRemove = Objects.requireNonNull(schemaNodesToRemove);
	}

	public List<String> getSchemaNodesToRemove() {
		return schemaNodesToRemove;
	}

	// --------------------------------------------------------------------------------------

	private boolean dontUse$$syntaxForUniqueMocs = true;

	/**
	 * If set to true, will not use the $$ syntax for any MOC generated whose name is unique in the
	 * namespace. If set to false, the $$ will apply to all MOCs.
	 * <p>
	 * Note that 3GPP IOCs are always considered unique in a given namespace, and will <b>always</b>
	 * be generated without the $$ syntax, irrespective of what this here is set to.
	 * <p>
	 * The default value is 'true'.
	 */
	public void setDontUse$$syntaxForUniqueMocs(final boolean dontUse$$syntaxForUniqueMocs) {
		this.dontUse$$syntaxForUniqueMocs = dontUse$$syntaxForUniqueMocs;
	}

	public boolean dontUse$$syntaxForUniqueMocs() {
		return dontUse$$syntaxForUniqueMocs;
	}

	// --------------------------------------------------------------------------------------

	private boolean generateCombinedEdt = false;

	/**
	 * If set to true, will generate oss_edt_combined models instead of oss_edt models.
	 * <p>
	 * The default value is 'false'.
	 */
	public void setGenerateCombinedEdt(final boolean generateCombinedEdt) {
		this.generateCombinedEdt = generateCombinedEdt;
	}

	public boolean generateCombinedEdt() {
		return generateCombinedEdt;
	}

	// --------------------------------------------------------------------------------------

	private boolean applyNodeAppInstanceNameHandling = false;

	/**
	 * If set to true, will apply special handling to cloud IMS models that inject the application instance
	 * name into the YANG models at runtime.
	 * <p>
	 * The default value is 'false'.
	 */
	public void setApplyNodeAppInstanceNameHandling(final boolean applyNodeAppInstanceNameHandling) {
		this.applyNodeAppInstanceNameHandling = applyNodeAppInstanceNameHandling;
	}

	public boolean applyNodeAppInstanceNameHandling() {
		return applyNodeAppInstanceNameHandling;
	}

	// --------------------------------------------------------------------------------------

	private boolean suppressWrapGeneration = true;

	/**
	 * There is currently a problem with handling of the 3GPP "Wrap" construct. Details are documented on this
	 * page: https://confluence-oss.seli.wh.rnd.internal.ericsson.com/pages/viewpage.action?spaceKey=TORCA&title=Handling+of+3GPP+wrap+construct
	 * <p>
	 * The solution is non-trivial, and will require quite a bit of extra implementation. There does not appear to
	 * be any usage of the Wrap construct at the moment in any of the cRAN MOMs, so for now we will always suppress
	 * generation of these to avoid issues in mediation.
	 * 
	 * This setting should be removed again in the future once the wrap handling is properly systemized.
	 */
	public void setSuppressWrapGeneration(final boolean suppressWrapGeneration) {
		this.suppressWrapGeneration = suppressWrapGeneration;
	}

	public boolean suppressWrapGeneration() {
		return suppressWrapGeneration;
	}

	// --------------------------------------------------------------------------------------

	private boolean generateForDomainApplicationModel = false;

	/**
	 * It may be desirable to simply generate the DPS Primary types, and associated models (EDTs, CDTs,...), only,
	 * and none of the target-type-specific stuff. This is especially useful in the context of generating domain
	 * application models.
	 */
	public void setGenerateForDomainApplicationModel(final boolean val) {
		generateForDomainApplicationModel = val;
	}

	public boolean generateForDomainApplicationModel() {
		return generateForDomainApplicationModel;
	}

	// --------------------------------------------------------------------------------------

	/*
	 * For backwards-compatibility reasons, the default notification behaviour is EOI (since the YT2 code base
	 * was originally written for EOI nodes.)
	 */
	private NotificationBehaviour notificationBehaviour = NotificationBehaviour.EOI;

	/**
	 * Sets how notifications shall be handled.
	 */
	public void setNotificationBehaviour(final NotificationBehaviour notificationBehaviour) {
		this.notificationBehaviour = Objects.requireNonNull(notificationBehaviour);
	}

	/**
	 * Returns the behaviour of notifications
	 */
	public NotificationBehaviour getNotificationBehaviour() {
		return notificationBehaviour;
	}

	public enum NotificationBehaviour {
		/**
		 * Ericsson Open Interface. According to EOI, all 'config true' data nodes are notified. A 'config false'
		 * data node is only notified if it is effectively 'notifiable-state-data true'.
		 */
		EOI;
//		/**
//		 * An Ericsson node that uses the O1 interface for management of the 3GPP-branch(es), and uses EOI for
//		 * management of the other branches. O1 refers to 3GPP. In 3GPP, all MOCs and attributes are notifiable
//		 * by default. Some attributes may be non-notifiable. MOCs are always notified.
//		 */
//		EOI_O1;

		/**
		 * Parses a string into a NotificationBehaviour object
		 */
		public static NotificationBehaviour fromString(final String input) {

			final String cleaned = Objects.requireNonNull(input).replace("-", "").replace("_", "").replace(" ", "").toLowerCase();

			switch(cleaned) {
			case "eoi":
				return NotificationBehaviour.EOI;
//			case "eoio1":
//			case "eoi01":								// yes, they will get this wrong and use a zero instead of an 'o'...
//				return NotificationBehaviour.EOI_O1;
			default:
				throw new RuntimeException("Illegal value '" + input + "' for notification behaviour. Only 'EOI' supported for now.");
			}
		}
	}

	// --------------------------------------------------------------------------------------

	/*
	 * It is conceivable that the very same data node denotes the existence of more than one network function.
	 * To cater for this, we choose the network function as key, hence: Map<network-function, path>
	 */
	final Map<String, String> networkFunctionsToPaths = new HashMap<>();

	/**
	 * Sets the paths to the network functions. Each path identifies a data node in the data tree that represents a
	 * network function. At runtime, the existence of an instance of such a data node indicates that the respective
	 * network function has been instantiated.
	 */
	public void setNetworkFunctionsToPaths(final Map<String, String> toSet) {
		this.networkFunctionsToPaths.clear();
		this.networkFunctionsToPaths.putAll(toSet);
	}

	public Map<String, String> getNetworkFunctionsToPath() {
		return Collections.unmodifiableMap(networkFunctionsToPaths);
	}

	// --------------------------------------------------------------------------------------

	final Set<String> discoveredNetworkFunctions = new HashSet<>();

	/**
	 * Adds a network function that has been discovered in the model.
	 */
	public void addDiscoveredNetworkFunction(final String networkFunction) {
		this.discoveredNetworkFunctions.add(networkFunction);
	}

	public Set<String> getDiscoveredNetworkFunctions() {
		return Collections.unmodifiableSet(discoveredNetworkFunctions);
	}

	// --------------------------------------------------------------------------------------

	private List<NamespaceNamePath> pathsOfNonGeneratedContainersAndLists = new ArrayList<>();

	/**
	 * A container or list may not be transformed, for a variety of reasons (e.g., it has been exclude-listed; it is not
	 * notified). The paths to these containers/list is captured here during processing of the model so that the paths
	 * to these data nodes can be placed into meta-data into an extension model, allowing mediation to not read these
	 * up from the node, thereby speeding up reads.
	 */
	public void addPathOfNonGeneratedContainerOrList(final NamespaceNamePath pathToDataNode) {
		this.pathsOfNonGeneratedContainersAndLists.add(Objects.requireNonNull(pathToDataNode));
	}

	/**
	 * The containers/lists that were not transformed.
	 */
	public List<NamespaceNamePath> getPathsOfNonGeneratedContainersAndLists() {
		return pathsOfNonGeneratedContainersAndLists;
	}

	// --------------------------------------------------------------------------------------

	final List<String> errors = new ArrayList<>();

	public void logError(final String str) {
		errors.add(str);
	}

	public List<String> getErrors() {
		return errors;
	}

	final List<String> warnings = new ArrayList<>();

	public void logWarn(final String str) {
		warnings.add(str);
	}

	public List<String> getWarnings() {
		return warnings;
	}

	final List<String> infos = new ArrayList<>();

	public void logInfo(final String str) {
		infos.add(str);
	}

	public List<String> getInfos() {
		return infos;
	}

	// --------------------------------------------------------------------------------------
	//
	// These are the YANG schemas parsed-in.

	private Schema baseSchema = null;
	private Schema augmentedSchema = null;
	private Schema deviatedSchema = null;

	public void setSchemas(final Schema baseSchema, final Schema augmentedSchema, final Schema deviatedSchema) {
		this.baseSchema = baseSchema;
		this.augmentedSchema = augmentedSchema;
		this.deviatedSchema = Objects.requireNonNull(deviatedSchema);
	}

	public Schema getBaseSchema() {
		return baseSchema;
	}

	public Schema getAugmentedSchema() {
		return augmentedSchema;
	}

	public Schema getDeviatedSchema() {
		return deviatedSchema;
	}

	// --------------------------------------------------------------------------------------
	//
	// These are the EModels that have been created and that need to be written out.

	private final CreatedEModels createdEmodels = new CreatedEModels();

	public CreatedEModels getCreatedEmodels() {
		return createdEmodels;
	}

	/*
	 * The below is purely to help with some logging to give extra information in respect of
	 * what is happening in terms of transformation of the model.
	 */
	private String lastRelevantInfo;

	public void setLastRelevantInfo(final String string) {
		lastRelevantInfo = string;
	}

	public String getLastRelevantInfo() {
		return lastRelevantInfo;
	}

	// --------------------------------------------------------------------------------------

	public void logSettingsForTransform() {

		logInfo(Constants.TEXT_TRANSFORMER + " transformer version: " + YangTransformer2Version.YANG_TRANSFORMER2_VERSION.toString());

		final List<String> implementingPathsAsStrings = implementingYangInput.stream().map(yangInput -> yangInput.getName()).collect(Collectors.toList());
		Collections.sort(implementingPathsAsStrings);
		implementingPathsAsStrings.forEach(str -> logInfo(Constants.TEXT_MODULES + " implementing file: " + str));

		final List<String> importingPathsAsStrings = importingYangInput.stream().map(yangInput -> yangInput.getName()).collect(Collectors.toList());
		Collections.sort(importingPathsAsStrings);
		importingPathsAsStrings.forEach(str -> logInfo(Constants.TEXT_MODULES + " importing file: " + str));

		logInfo(Constants.TEXT_SETTINGS + " output directory: " + outDir.getAbsolutePath());

		logInfo(Constants.TEXT_SETTINGS + " target: " + target.getCategory() + "/" + target.getType() + "/" + target.getVersion());

		logInfo(Constants.TEXT_SETTINGS + " mock ManagedElement: " + (mockManagedElement == null ? "<none>" : mockManagedElement.toImpliedUrn()));

		logInfo(Constants.TEXT_SETTINGS + " explicit containment parent: " + (containmentParent == null ? "<none>" : containmentParent.toImpliedUrn()));

		logInfo(Constants.TEXT_SETTINGS + " effective containment parent: " + getEffectiveContainmentParent().toImpliedUrn());

		logInfo(Constants.TEXT_SETTINGS + " exclude-listed namespaces: " + StringHelper.toString(excludedNamespaces, "[", "]", ", ", null, null));

		logInfo(Constants.TEXT_SETTINGS + " if-feature handling: " + featureHandling);

		logInfo(Constants.TEXT_SETTINGS + " generate NP-containers as system-created: " + generateNPcontainersAsSystemCreated);

		logInfo(Constants.TEXT_SETTINGS + " generate RPCs: " + generateRpcs);

		logInfo(Constants.TEXT_SETTINGS + " suppress RPCs for namespaces: " + StringHelper.toString(excludedNamespacesForRpcs, "[", "]", ", ", null, null));

		logInfo(Constants.TEXT_SETTINGS + " apply 3GPP-handling: " + apply3gppHandling);

		final List<String> schemaNodesToRenameAsStrings = schemaNodesToRename.entrySet().stream().map(entry -> entry.getKey() + " -> " + entry.getValue()).collect(Collectors.toList());
		logInfo(Constants.TEXT_SETTINGS + " schema nodes to rename: " + StringHelper.toString(schemaNodesToRenameAsStrings, "[", "]", ", ", null, null));

		logInfo(Constants.TEXT_SETTINGS + " schema nodes to remove: " + StringHelper.toString(schemaNodesToRemove, "[", "]", ", ", null, null));

		logInfo(Constants.TEXT_SETTINGS + " don't use $$ syntax for unique MOCs: " + dontUse$$syntaxForUniqueMocs);

		logInfo(Constants.TEXT_SETTINGS + " generate combined EDTs: " + generateCombinedEdt);

		logInfo(Constants.TEXT_SETTINGS + " generate for domain-application model: " + generateForDomainApplicationModel);
	}

	public void logSettingsForGenerateCfmMimOnly() {

		logInfo(Constants.TEXT_TRANSFORMER + " transformer version: " + YangTransformer2Version.YANG_TRANSFORMER2_VERSION.toString());

		final List<String> implementingPathsAsStrings = implementingYangInput.stream().map(yangInput -> yangInput.getName()).collect(Collectors.toList());
		Collections.sort(implementingPathsAsStrings);
		implementingPathsAsStrings.forEach(str -> logInfo(Constants.TEXT_MODULES + " implementing file: " + str));

		final List<String> importingPathsAsStrings = importingYangInput.stream().map(yangInput -> yangInput.getName()).collect(Collectors.toList());
		Collections.sort(importingPathsAsStrings);
		importingPathsAsStrings.forEach(str -> logInfo(Constants.TEXT_MODULES + " importing file: " + str));

		logInfo(Constants.TEXT_SETTINGS + " target type: " + target.getType());

		logInfo(Constants.TEXT_SETTINGS + " supported MIMs file: " + (supportedMimsFile == null ? "<none>" : supportedMimsFile.getAbsolutePath()));

		logInfo(Constants.TEXT_SETTINGS + " apply 3GPP-handling: " + apply3gppHandling);

		networkFunctionsToPaths.forEach((nf, path) -> logInfo(Constants.TEXT_SETTINGS + " nf -> path mapping: " + nf + " -> " + path));
	}
}
