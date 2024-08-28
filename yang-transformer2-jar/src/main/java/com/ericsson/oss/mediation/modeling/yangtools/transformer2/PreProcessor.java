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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.schema.Schema;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.YangModelRoot;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EriCustomProcessor;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.oran.CORAN;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YKey;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YOutput;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.generate.CfmMimInfoGenerator;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.AppInstanceNameSupport;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ExtractionHelper;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.NamespaceAndIdentifier;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.PropertyUtils;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.SchemaTrimmer;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.ThreeGPPSupport;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.util.YangNameVersionUtil;

public class PreProcessor {

	/**
	 * The translation from YANG to EModel is in large parts straightforward; however, there are some areas where
	 * the translation is tricky. One such area is the naming of MO types (the famous $$ syntax). To simplify the
	 * generation code later on, the schemas are cleaned up here (various bits and bobs are removed, for example,
	 * excluded elements) and meta-data is attached to various model elements.
	 */
	static void processVariantsBeforeGeneration(final TransformerContext context) {

		/*
		 * We need to force-generate the IETF YangLibrary into EModel.
		 */
		ForceGenerateIetfYangLibrary.tweakVariants(context);

		/*
		 * We attach some meta-information to containers and lists and remove anything that
		 * would result in struct-in-struct (which we can't handle in ENM).
		 */
		preProcessSchemaTree(context);

		/*
		 * We apply the notification behaviour. This depends on the target type / node NBI.
		 */
		applyNotificationBehaviour(context);

		/*
		 * Before we do anything else, we remove from the schema as much as possible. This will make
		 * subsequent processing considerably easier and also faster. We will:
		 * 
		 * - remove any schema node having an if-feature statement that computes to false.
		 * - remove any schema node that is excluded based on namespace.
		 * - remove any RPC that is not desired.
		 * - remove anything that is not notified and not static.
		 * - remove anything that should be explicitly removed.
		 */
		SchemaTrimmer.removeIfFeatureDependentSchemaNodes(context);
		SchemaTrimmer.removeSchemaNodesOfExcludedNamespaces(context);
		SchemaTrimmer.removeRpcsBasedOnNamespace(context);
		SchemaTrimmer.removeNonNotifiedNonStaticContainersAndLists(context);
		SchemaTrimmer.removeExplicitlyRemovedSchemaNodes(context);

		/*
		 * We process the schema and attach various bits of meta information to it.
		 */
		linkMocsBetweenVariants(context);
		applyNodeAppInstanceNameHandling(context);
		renameSchemaNodes(context);
		assignMocName(context);
		attachAppDataForLists(context);

		/*
		 * A number of changes have been made to the schema, possibly affecting identities. Re-build
		 * the IdentityRegistry for each variant.
		 */
		context.getBaseSchema().buildIdentityRegistry();
		context.getAugmentedSchema().buildIdentityRegistry();
		context.getDeviatedSchema().buildIdentityRegistry();

		/*
		 * There are more things to be removed now before doing anything else.
		 */
		SchemaTrimmer.removeUnreliableStructMembers(context);
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	private static final String YT_APP_DATA_VARIANT = "YT_APP_DATA_VARIANT";

	/**
	 * Attaches the information about which variant each module is in. This will be required later on when we are
	 * processing data nodes deep down the tree and we need to know in which variant we are in order to either
	 * create a regular model or an extension model.
	 */
	public static void assignVariantToAllYangModelRootsInSchema(final Schema schema, final Variant variant) {

		schema.getModuleRegistry().getAllYangModels().stream()
				.forEach(yangModel -> yangModel.getYangModelRoot().setCustomAppData(YT_APP_DATA_VARIANT, variant));
	}

	public static Variant getVariantForStatement(final AbstractStatement stmt) {
		return stmt.getYangModelRoot().getCustomAppData(YT_APP_DATA_VARIANT);
	}

	public static boolean isInAugmentedVariant(final AbstractStatement stmt) {
		return Variant.AUGMENTED == getVariantForStatement(stmt); 
	}

	public static boolean isInDeviatedVariant(final AbstractStatement stmt) {
		return Variant.DEVIATED == getVariantForStatement(stmt); 
	}

	public static boolean isInOriginalVariant(final AbstractStatement stmt) {
		return !isInDeviatedVariant(stmt);
	}

	public static String getVariant(final AbstractStatement stmt) {
		return getVariantForStatement(stmt).toString().toLowerCase();
	}

	public enum Variant {
		BASE, AUGMENTED, DEVIATED;
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	private static final String YT_APP_DATA_XYZ_VERSION = "YT_APP_DATA_XYZ_VERSION";

	/**
	 * For each module and submodule computes the x.y.z version based on the revision, and attaches the x.y.z value to the
	 * root model object (the parent of YModule or YSubmodule). This information will be required later on when artifacts
	 * for original are generated.
	 */
	public static void attachXyzVersionToAllYangModelRootsInSchema(final Schema schema) {

		schema.getModuleRegistry().getAllYangModels().forEach(yangModel -> {
			final String ericssonXyzVersionForYam = CfmMimInfoGenerator.getEricssonXyzVersionForYam(yangModel);
			yangModel.getYangModelRoot().setCustomAppData(YT_APP_DATA_XYZ_VERSION, ericssonXyzVersionForYam);
		});
	}

	/**
	 * Returns the x.y.z version for the module declaring the namespace in which the supplied schema node sits.
	 */
	public static String getXyzVersionForNamespace(final AbstractStatement schemaNode) {

		final Schema owningSchema = schemaNode.getYangModelRoot().getOwningSchema();
		final String originalNamespace = getOriginalNamespace(schemaNode);

		final YangModel yangModelForNamespace = ExtractionHelper.findModuleDeclaringOriginalNamespace(owningSchema, originalNamespace);
		return getXyzVersionForYangModelRoot(yangModelForNamespace.getYangModelRoot());
	}

	/**
	 * Returns the x.y.z version of a module or submodule, represented by the model root. 
	 */
	public static String getXyzVersionForYangModelRoot(final YangModelRoot yangModelRoot) {
		return yangModelRoot.getCustomAppData(YT_APP_DATA_XYZ_VERSION);
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Cleans the namespaces of all node app instance names. This is required as cIMS will tweak the
	 * namespaces at runtime during deployment of the software. Example:
	 * <p>
	 * Module name - original: <b>ericsson-config-bgf</b>  -> after deployment: <b>eric-ims-bgf---london3-2.2.5</b><br/>
	 * Module namespace - original: <b>urn:ericsson:ericsson-config-bgf</b>  -> after deployment: <b>urn:ericsson:eric-ims-bgf---london3-2.2.5</b><br/>
	 * Module root MOC name - original: <b>ericsson-config-bgf</b>  -> after deployment: <b>eric-ims-bgf---london3-2.2.5</b><br/>
	 * <p> 
	 * We will strip out the node app instance name (in the above example, this is 'london3'), which
	 * will result in us producing the following:
	 * <p>
	 * Module name - input: <b>eric-ims-bgf---london3-2.2.5</b>  -> after transformation: <b>eric-ims-bgf-2.2.5</b><br/>
	 * Module namespace - input: <b>urn:ericsson:eric-ims-bgf---london3-2.2.5</b>  -> after transformation: <b>urn:ericsson:eric-ims-bgf-2.2.5</b><br/>
	 * Module root MOC name - input: <b>eric-ims-bgf---london3-2.2.5</b>  -> after transformation: <b>eric-ims-bgf-2.2.5</b><br/>
	 * <p>
	 * This transformation loses the real namespace, of course, but we cannot place the real namespace
	 * into meta-data, as the following example shows:
	 * <p>
	 * Module namespace - input: <b>urn:ericsson:eric-ims-bgf---london3-2.2.5</b>  -> after transformation: <b>urn:ericsson:eric-ims-bgf-2.2.5</b><br/>
	 * Module namespace - input: <b>urn:ericsson:eric-ims-bgf---paris7-2.2.5</b>  -> after transformation: <b>urn:ericsson:eric-ims-bgf-2.2.5</b><br/>
	 * <p>
	 * So basically, we would be generating the exact same output models, but the input models are different.
	 * The solution to this is that the real namespace is held with data in DPS, and we only give mediation
	 * a hint to check DPS for it.
	 */
	public static void applyNodeAppInstanceNameHandling(final TransformerContext context) {

		if(!context.applyNodeAppInstanceNameHandling()) {
			return;
		}

		final Map<String, String> origNamespaceToCleaned = new HashMap<>();

		context.getDeviatedSchema().getModuleRegistry().getAllYangModels().stream()
			.filter(yangModel -> yangModel.getYangModelRoot().isModule())
			.forEach(yangModel -> {
				final String origNamespace = yangModel.getYangModelRoot().getNamespace();
				final String cleanedNamespace = YangNameVersionUtil.removeNodeAppInstanceName(origNamespace);
				if(!origNamespace.equals(cleanedNamespace)) {
					origNamespaceToCleaned.put(origNamespace, cleanedNamespace);
				}
			});

		if(origNamespaceToCleaned.isEmpty()) {
			return;
		}

		applyNodeAppInstanceNameHandling(context.getBaseSchema(), origNamespaceToCleaned);
		applyNodeAppInstanceNameHandling(context.getAugmentedSchema(), origNamespaceToCleaned);
		applyNodeAppInstanceNameHandling(context.getDeviatedSchema(), origNamespaceToCleaned);
	}

	private static final String YT_APP_DATA_MODULE_NAME_CLEANED_OF_APP_INST_NAME = "YT_APP_DATA_MODULE_NAME_CLEANED_OF_APP_INST_NAME";

	/**
	 * Returns the cleaned name of the module or submodule, or the original name if the name
	 * has not been cleaned.
	 */
	public static String getModuleNameCleanedOfNodeAppInstanceName(final YangModelRoot yangModelRoot) {

		final String cleanedName = yangModelRoot.getCustomAppData(YT_APP_DATA_MODULE_NAME_CLEANED_OF_APP_INST_NAME);
		return cleanedName != null ? cleanedName : yangModelRoot.getModuleOrSubModuleName();
	}

	private static void applyNodeAppInstanceNameHandling(final Schema schema, final Map<String, String> origNamespaceToCleaned) {

		if(schema == null) {
			return;
		}

		schema.getModuleRegistry().getAllYangModels().forEach(yangModel -> {
			final YangModelRoot yangModelRoot = yangModel.getYangModelRoot();
			applyNodeAppInstanceNameHandling(Collections.singletonList(yangModelRoot), origNamespaceToCleaned);
		});

		/*
		 * We also fix up the module/submodule names, as this will be needed later on for the derivedBy
		 * and the cfm miminfos.
		 */
		schema.getModuleRegistry().getAllYangModels().stream()
			.forEach(yangModel -> {
				final String origName = yangModel.getYangModelRoot().getModuleOrSubModuleName();
				final String cleanedName = YangNameVersionUtil.removeNodeAppInstanceName(origName);

				if(!origName.equals(cleanedName)) {
					yangModel.getYangModelRoot().setCustomAppData(YT_APP_DATA_MODULE_NAME_CLEANED_OF_APP_INST_NAME, cleanedName);
				}
			});
	}

	private static void applyNodeAppInstanceNameHandling(final List<AbstractStatement> statements, final Map<String, String> origNamespaceToCleaned) {

		statements.forEach(stmt -> {

			/*
			 * We are not trying to be super-smart about this - we simply add meta information to any
			 * statement (even those that we don't really care about) that has a namespace that needs
			 * to be cleaned. A bit wasteful, but at least we are certain that we are not missing out
			 * on any statements.
			 */
			final String origStatementNamespace = stmt.getEffectiveNamespace();

			if(origNamespaceToCleaned.containsKey(origStatementNamespace)) {
				stmt.setCustomAppData(YT_APP_DATA_NAMESPACE_CLEANED_OF_APP_INST_NAME, origNamespaceToCleaned.get(origStatementNamespace));
			}

			final List<AbstractStatement> children = stmt.getChildStatements();
			if(!children.isEmpty()) {
				applyNodeAppInstanceNameHandling(children, origNamespaceToCleaned);
			}
		});
	}

	private static final String YT_APP_DATA_NAMESPACE_CLEANED_OF_APP_INST_NAME = "YT_APP_DATA_NAMESPACE_CLEANED_OF_APP_INST_NAME";

	/**
	 * Denotes whether the statement namespace has been cleaned.
	 */
	public static boolean namespaceHasBeenCleanedOfNodeAppInstanceName(final AbstractStatement statement) {
		return statement.hasCustomAppData(YT_APP_DATA_NAMESPACE_CLEANED_OF_APP_INST_NAME);
	}

	/**
	 * Returns the cleaned namespace of the statement, or the original namespace if the namespace
	 * has not been cleaned.
	 */
	public static String getNamespaceCleanedOfNodeAppInstanceName(final AbstractStatement statement) {
		final String cleanedNamespace = statement.getCustomAppData(YT_APP_DATA_NAMESPACE_CLEANED_OF_APP_INST_NAME);
		return cleanedNamespace != null ? cleanedNamespace : getOriginalNamespace(statement);
	}

	/**
	 * Returns the original namespace for the statement, irrespective of whether the namespace has
	 * been cleaned.
	 */
	public static String getOriginalNamespace(final AbstractStatement statement) {
		return statement.getEffectiveNamespace();
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	private static final String YT_APP_DATA_RENAMED_STATEMENT_IDENTIFIER = "YT_APP_DATA_RENAMED_STATEMENT_IDENTIFIER";

	/**
	 * We are implementing a facility here whereby it is possible to rename schema nodes, e.g. a container, thereby allowing
	 * clashing schema nodes to be reflected in the schema. One example of where this comes in really useful is two data nodes
	 * with the same name, but different namespace, sitting under a common parent. DPS, and model-driven tools, would have
	 * problems with handling this, so by renaming one of the data nodes this problem is avoided. Of course, mediation must
	 * cater for the renaming, and perform renaming as well then.
	 * <p>
	 * This potential for renaming requires the generation code later on to make use of the getRenamedStatementIdentifier(),
	 * and not blindly to use stmt.getSchemaIdentifier().
	 */
	private static void renameSchemaNodes(final TransformerContext context) {
		renameSchemaNodes(context, context.getBaseSchema());
		renameSchemaNodes(context, context.getAugmentedSchema());
		renameSchemaNodes(context, context.getDeviatedSchema());
	}

	private static void renameSchemaNodes(final TransformerContext context, final Schema schema) {

		context.getSchemaNodesToRename().forEach((path, newName) -> {

			final List<String> schemaPathElements = new ArrayList<>(Arrays.asList(path.substring(1).split("/")));
			final List<AbstractStatement> candidates = ExtractionHelper.getStatementsAtSchemaRoot(schema, AbstractStatement::definesSchemaNode);

			final AbstractStatement targetSchemaNode = ExtractionHelper.findTargetSchemaNode(schemaPathElements, candidates);
			if(targetSchemaNode != null) {
				context.logInfo(Constants.TEXT_RENAMED_EXPLICIT + " Renamed schema node '" + ExtractionHelper.getHumanReadablePathToSchemaNode(targetSchemaNode) + "' to '" + newName + "' in " + getVariant(targetSchemaNode) + " schema during pre-processing.");
				setRenamedStatementIdentifier(targetSchemaNode, newName);
			} else {
				context.logInfo(Constants.TEXT_IGNORED_UNRESOLVABLE + " Cannot rename schema node for path '" + path + "' as it was not found in the " + PreProcessor.getVariant(candidates.get(0)) + " schema during pre-processing.");
			}
		});
	}

	private static void setRenamedStatementIdentifier(final AbstractStatement identifyableStatement, final String newName) {
		identifyableStatement.setCustomAppData(YT_APP_DATA_RENAMED_STATEMENT_IDENTIFIER, newName);
	}

	/**
	 * Returns the identity (the name) of the statement.
	 * <p>
	 * Will return the renamed value if the schema node has been renamed, otherwise the original name. If the
	 * data node represents a 3GPP non-unique sequence ("wrap"), will strip the "Wrap" suffix from the name
	 * before returning.
	 */
	public static String getRenamedStatementIdentifier(final AbstractStatement identifyableStatement) {
		final String replacedName = identifyableStatement.getCustomAppData(YT_APP_DATA_RENAMED_STATEMENT_IDENTIFIER);
		if(replacedName != null) {
			return replacedName;
		}

		final String originalStatementIdentifier = getOriginalStatementIdentifier(identifyableStatement);
		/*
		 * Need to split off the "Wrap" if it is a 3GPP non-unique sequence.
		 */
		return is3gppNonUniqueSequence(identifyableStatement) ? ThreeGPPSupport.getRealNonUniqueSequenceName(originalStatementIdentifier) : originalStatementIdentifier;
	}

	/**
	 * Returns the original identity (the name) of the statement as it was defined in the original YANG model.
	 */
	public static String getOriginalStatementIdentifier(final AbstractStatement identifyableStatement) {
		return identifyableStatement.getStatementIdentifier();
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Attaches meta-information to containers and lists. We figure out what each container/list
	 * represents - a MOC, a struct, a wrap, etc. - this information is vital for the processing later on.
	 * <p>
	 * We also remove all constructs that would result in a struct-in-struct situation (which ENM can't handle).
	 */
	private static void preProcessSchemaTree(final TransformerContext context) {
		preProcessSchemaTree(context, context.getBaseSchema());
		preProcessSchemaTree(context, context.getAugmentedSchema());
		preProcessSchemaTree(context, context.getDeviatedSchema());
	}

	private static void preProcessSchemaTree(final TransformerContext context, final Schema schema) {

		final List<AbstractStatement> topLevelContainersAndLists = ExtractionHelper.getStatementsAtSchemaRoot(schema, ExtractionHelper::isContainerOrList);

		/*
		 * The 3GPP branches are self-contained, meaning that within a 3GPP branch everything is considered a
		 * 3GPP MOC - i.e. it cannot happen that 3GPP and IETF-style are mixed in the same branch.
		 */
		topLevelContainersAndLists.forEach(containerOrList -> {
			final boolean in3gppBranch = context.apply3gppHandling() && ThreeGPPSupport.is3gppIoc(context, containerOrList);
			preProcessContainerOrList(context, containerOrList, in3gppBranch, 0);
		});

		/*
		 * Don't forget that there are RPCs as well at the top-level, that we need to handle in terms of
		 * containers / lists and struct-in-struct.
		 */
		final List<AbstractStatement> allRpcs = ExtractionHelper.getStatementsAtSchemaRoot(schema, stmt -> stmt.is(CY.STMT_RPC));
		preProcessActionsOrRpcs(context, allRpcs, false);
	}

	private static void preProcessContainerOrList(final TransformerContext context, final AbstractStatement containerOrList, final boolean in3gppBranch, final int level) {

		/*
		 * In a 3GPP branch the containers / lists are considered "IOCs" (as opposed to simple "MOCs"). Some
		 * additional processing is required to handle the 'attributes' container correctly.
		 */
		if(in3gppBranch) {
			setContainerOrListType(containerOrList, ContainerOrListType.THREEGPP_MOC);
			handle3gppAttributesContainer(context, containerOrList);
		} else if(isTopLevelContainerWithAppInstanceName(context, containerOrList, level)) {
			setContainerOrListType(containerOrList, ContainerOrListType.NAIN_MOC);
		} else {
			setContainerOrListType(containerOrList, ContainerOrListType.MOC);
		}

		/*
		 * TODO: [STRUCT_IN_STRUCT] - Clean up any possible struct-in-struct in any action of the MOC.
		 */
		final List<AbstractStatement> actions = containerOrList.getChildren(CY.STMT_ACTION);
		preProcessActionsOrRpcs(context, actions, in3gppBranch);

		/*
		 * Work down the tree.
		 */
		final List<AbstractStatement> childContainersAndLists = getChildContainersAndListsRepresentingMocs(containerOrList, in3gppBranch);
		childContainersAndLists.forEach(child -> preProcessContainerOrList(context, child, in3gppBranch, level + 1));
	}

	/**
	 * Returns whether the container represents a data node whose name has been changed at runtime.
	 */
	private static boolean isTopLevelContainerWithAppInstanceName(final TransformerContext context, final AbstractStatement containerOrList, final int level) {

		/*
		 * They can only sit at the top-level of the schema, so anything below that can be safely
		 * assumed to be not such a container.
		 */
		if(level > 0) {
			return false;
		}
		if(!context.applyNodeAppInstanceNameHandling()) {
			return false;
		}

		return AppInstanceNameSupport.isContainerWithAppInstanceName(containerOrList);
	}

	private static void preProcessActionsOrRpcs(final TransformerContext context, final List<AbstractStatement> actionsOrRpcs, final boolean in3gppMoc) {

		actionsOrRpcs.forEach(actionOrRpc -> {
			/*
			 * Have to take care of both input and output.
			 * 
			 * Input is fairly easy - we allow container / list directly under 'input', but not further below.
			 * And have to take care of "wrapped" lists...
			 */
			ExtractionHelper.getChildDataNodes(actionOrRpc.getChild(CY.STMT_INPUT), ExtractionHelper::isContainerOrList).forEach(child -> {

				if(in3gppMoc && ThreeGPPSupport.isNonUniqueWritableMultiValuedAttribute(child)) {
					setContainerOrListType(child, ContainerOrListType.THREE_GPP_NON_UNIQUE_SEQUENCE);
				} else {
					setContainerOrListType(child, ContainerOrListType.STRUCT_OR_STRUCT_SEQUENCE);
					removeContainersAndListsBelow(context, child, in3gppMoc);
				}
			});

			/*
			 * Output is a bit more tricky. The EModel only supports a single type as result of an action, but in
			 * YANG there could be multiple leafs (or in general, multiple data nodes) as output of an action. If
			 * this is the case we need to generate an artificial struct. However, we could not support any
			 * containers/lists underneath an artificial struct.
			 */
			final YOutput yOutput = actionOrRpc.getChild(CY.STMT_OUTPUT);

			final List<AbstractStatement> outputDataNodes = ExtractionHelper.getChildDataNodes(yOutput, ExtractionHelper::isContainerOrListOrLeafOrLeafList);

			if(outputDataNodes.size() == 1) {
				final AbstractStatement outputDataNode = outputDataNodes.get(0);
				/*
				 * Single data node in the output. It could be a container/list, in which case any container/list
				 * below that could not be supported.
				 */
				if(in3gppMoc && ThreeGPPSupport.isNonUniqueWritableMultiValuedAttribute(outputDataNode)) {
					setContainerOrListType(outputDataNode, ContainerOrListType.THREE_GPP_NON_UNIQUE_SEQUENCE);
				} else if(ExtractionHelper.isContainerOrList(outputDataNode)) {
					setContainerOrListType(outputDataNode, ContainerOrListType.STRUCT_OR_STRUCT_SEQUENCE);
					removeContainersAndListsBelow(context, outputDataNode, in3gppMoc);
				}
			} else if(outputDataNodes.size() > 1) {
				/*
				 * More than one data node. The only way how we can support this in ENM is with an artificial struct.
				 * That means removing all containers/lists as these would be struct-in-struct, so effectively only
				 * leafs/leaf-lists remain.
				 */
				removeContainersAndListsBelow(context, yOutput, in3gppMoc);
				/*
				 * There could be a "wrap" inside the struct / struct-sequence, need to identify those as well
				 * for processing later on.
				 */
				if(in3gppMoc) {
					ExtractionHelper.getChildDataNodes(yOutput, child -> child.is(CY.STMT_LIST)).forEach(
							member -> setContainerOrListType(member, ContainerOrListType.THREE_GPP_NON_UNIQUE_SEQUENCE));
				}
			}
		});
	}

	/**
	 * Given an MOC, returns child containers and lists that should be considered child MOCs. Special handling is
	 * applied for the 3GPP 'attributes' container.
	 */
	private static List<AbstractStatement> getChildContainersAndListsRepresentingMocs(final AbstractStatement parentMoc, final boolean in3gppBranch) {

		return ExtractionHelper.getChildDataNodes(parentMoc, stmt -> {
			if(!ExtractionHelper.isContainerOrList(stmt)) {
				return false;
			}
			return !in3gppBranch || !is3gppAttributesContainer(stmt);
		});
	}

	/**
	 * The 3GPP attributes container requires a bit of special handling, which is exactly what is happening
	 * here. See inline comments...
	 */
	private static void handle3gppAttributesContainer(final TransformerContext context, final AbstractStatement moc) {

		final AbstractStatement attributesContainer = ThreeGPPSupport.get3gppAttributesContainer(moc);
		if(attributesContainer == null) {
			/*
			 * Sometimes, but rarely, an IOC does not have an "attributes" container. Nothing else to do here.
			 */
			return;
		}

		setContainerOrListType(attributesContainer, ContainerOrListType.THREE_GPP_ATTRIBUTES_CONTAINER);

		/*
		 * Within the attributes container we can have containers and lists, which represent structs / struct-sequences.
		 * There is a corner case in respect of 'non-unique sequence' ("wrap") that we must handle.
		 */
		final List<AbstractStatement> containersAndListsWithinAttributes = ExtractionHelper.getChildDataNodes(attributesContainer, ExtractionHelper::isContainerOrList);

		containersAndListsWithinAttributes.forEach(containerOrListWithinAttributes -> {

			if (ThreeGPPSupport.isNonUniqueWritableMultiValuedAttribute(containerOrListWithinAttributes)) {
				/*
				 * It's a "wrap" - There will not be any containers / lists below that, so no need to handle struct-in-struct.
				 */
				setContainerOrListType(containerOrListWithinAttributes, ContainerOrListType.THREE_GPP_NON_UNIQUE_SEQUENCE);
			} else {
				/*
				 * Either container = struct, or a non-wrap list = struct-sequence.
				 * 
				 * TODO: [STRUCT_IN_STRUCT] - We don't support struct-in-struct, so any container / list below this
				 * struct / struct-sequence must be removed (but not any "wrap").
				 */
				setContainerOrListType(containerOrListWithinAttributes, ContainerOrListType.STRUCT_OR_STRUCT_SEQUENCE);
				removeContainersAndListsBelow(context, containerOrListWithinAttributes, true);
				/*
				 * There could be a "wrap" inside the struct / struct-sequence, need to identify those as well
				 * for processing later on. No need to check for the data node type - any list left as child of
				 * the struct must be a wrap, can't be anything else.
				 */
				ExtractionHelper.getChildDataNodes(containerOrListWithinAttributes, child -> child.is(CY.STMT_LIST)).forEach(
						member -> setContainerOrListType(member, ContainerOrListType.THREE_GPP_NON_UNIQUE_SEQUENCE));
			}
		});
	}

	/**
	 * Removes any container or list sitting below the supplied container/list. 
	 */
	private static void removeContainersAndListsBelow(final TransformerContext context, final AbstractStatement containerOrList, final boolean dontRemove3gppNonUniqueSequence) {

		final List<AbstractStatement> childContainersAndLists = ExtractionHelper.getChildDataNodes(containerOrList, ExtractionHelper::isContainerOrList);

		final List<AbstractStatement> toRemove = dontRemove3gppNonUniqueSequence ?
				childContainersAndLists.stream().filter(stmt -> stmt.is(CY.STMT_CONTAINER) || !ThreeGPPSupport.isNonUniqueWritableMultiValuedAttribute(stmt)).collect(Collectors.toList())
				: childContainersAndLists;

		toRemove.forEach(dataNodeToRemove -> {
			if(isInDeviatedVariant(dataNodeToRemove)) {
				/*
				 * We only log in DEVIATED to reduce noise in the log.
				 */
				context.logWarn(Constants.TEXT_REMOVED_STRUCT_IN_STRUCT + " Data node '" + ExtractionHelper.getHumanReadablePathToSchemaNode(dataNodeToRemove) + "' removed during pre-processing as it would result in struct-in-struct.");
			}

			dataNodeToRemove.getParentStatement().removeChild(dataNodeToRemove);
		});
	}

	private static final String YT_APP_DATA_CONTAINER_OR_LIST_TYPE = "YT_APP_DATA_CONTAINER_OR_LIST_TYPE";

	/*
	 * Every container and list in the schema is of a type. The type depends on where the container/list
	 * is used (e.g. in an action or rpc, or in a 3GPP branch, ...)
	 */
	public enum ContainerOrListType {
		/**
		 * The container or list represents a MOC.
		 */
		MOC,
		/**
		 * The container or list represents a 3GPP IOC, meaning that special handling applies to
		 * the containers / lists within it. Of course it is also an MOC.
		 * <p>
		 * For more details, see "3GPP TS 32.160", chapter 6.2
		 */
		THREEGPP_MOC,
		/**
		 * The container is a MOC that has been renamed by the node at runtime to include the node
		 * application instance name. Used by IMS.
		 */
		NAIN_MOC,
		/**
		 * The container represents the 3GPP 'attributes' container.
		 * <p>
		 * For more details, see "3GPP TS 32.160", chapter 6.2.4.1
		 */
		THREE_GPP_ATTRIBUTES_CONTAINER,
		/**
		 * The container or list represents a struct / struct-sequence.
		 */
		STRUCT_OR_STRUCT_SEQUENCE,
		/**
		 * The list represents a 3GPP non-unique sequence ("Wrap")
		 * <p>
		 * For more details, see "3GPP TS 32.160", chapter 6.2.11.2
		 */
		THREE_GPP_NON_UNIQUE_SEQUENCE;
	}

	private static void setContainerOrListType(final AbstractStatement containerOrList, final ContainerOrListType type) {
		containerOrList.setCustomAppData(YT_APP_DATA_CONTAINER_OR_LIST_TYPE, type);
	}

	private static ContainerOrListType getContainerOrListType(final AbstractStatement stmt) {
		return stmt.getCustomAppData(YT_APP_DATA_CONTAINER_OR_LIST_TYPE);
	}

	/**
	 * The container or list represents a MOC. This could be a regular MOC, or a 3GPP MOC, or a MOC whose
	 * name has been changed at runtime.
	 */
	public static boolean isMoc(final AbstractStatement stmt) {
		final ContainerOrListType type = getContainerOrListType(stmt);
		return ContainerOrListType.THREEGPP_MOC == type || ContainerOrListType.NAIN_MOC == type || ContainerOrListType.MOC == type;
	}

	/**
	 * The container or list represents a 3GPP IOC, meaning that special handling applies to
	 * the containers / lists within it.
	 */
	public static boolean is3gppMoc(final AbstractStatement stmt) {
		return ContainerOrListType.THREEGPP_MOC == getContainerOrListType(stmt);
	}

	/**
	 * The YANG 'container' is a MOC that has been renamed by the node at runtime to include the node
	 * application instance name.
	 */
	public static boolean isNainMoc(final AbstractStatement stmt) {
		return ContainerOrListType.NAIN_MOC == getContainerOrListType(stmt);
	}

	/**
	 * The YANG 'container' represents the 3GPP 'attributes' container.
	 */
	public static boolean is3gppAttributesContainer(final AbstractStatement stmt) {
		return ContainerOrListType.THREE_GPP_ATTRIBUTES_CONTAINER == getContainerOrListType(stmt);
	}

	/**
	 * The YANG 'container' or 'list' represents a struct or struct-sequence.
	 */
	public static boolean isStructOrStructSequence(final AbstractStatement stmt) {
		return ContainerOrListType.STRUCT_OR_STRUCT_SEQUENCE == getContainerOrListType(stmt);
	}

	/**
	 * The YANG 'list' represents a 3GPP non-unique sequence ("Wrap") attribute.
	 */
	public static boolean is3gppNonUniqueSequence(final AbstractStatement stmt) {
		return ContainerOrListType.THREE_GPP_NON_UNIQUE_SEQUENCE == getContainerOrListType(stmt);
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * During processing later on we will have to frequently compare BASE, AUGMENTING and DEVIATING variants of MOCs,
	 * and their content to each other. By far the easiest way of handling this is to use meta data that contains
	 * pointers between the different variants of the same MOC.
	 */
	private static void linkMocsBetweenVariants(final TransformerContext context) {

		final List<AbstractStatement> statementsAtBaseSchemaRoot = getMocsAtSchemaRoot(context.getBaseSchema());
		final List<AbstractStatement> mocsAtAugmentedSchemaRoot = getMocsAtSchemaRoot(context.getAugmentedSchema());
		final List<AbstractStatement> statementsAtDeviatedSchemaRoot = getMocsAtSchemaRoot(context.getDeviatedSchema());

		linkMocsBetweenVariants(statementsAtBaseSchemaRoot, mocsAtAugmentedSchemaRoot, statementsAtDeviatedSchemaRoot);
	}

	private static final String YT_POINTER_TO_MOC_IN_BASE = "YT_POINTER_TO_MOC_IN_BASE";
	private static final String YT_POINTER_TO_MOC_IN_AUGMENTED = "YT_POINTER_TO_MOC_IN_AUGMENTED";
	private static final String YT_POINTER_TO_MOC_IN_DEVIATED = "YT_POINTER_TO_MOC_IN_DEVIATED";

	private static void linkMocsBetweenVariants(final List<AbstractStatement> mocsInBase, final List<AbstractStatement> mocsInAugmented, final List<AbstractStatement> mocsInDeviated) {

		/*
		 * We use the AUGMENTING variant for navigation, as this variant will have the "most complete" tree
		 * (BASE and DEVIATING could have fewer data nodes).
		 */
		mocsInAugmented.forEach(mocInAugmented -> {

			final AbstractStatement mocInBase = ExtractionHelper.findSchemaNode(mocsInBase, mocInAugmented);
			final AbstractStatement mocInDeviated = ExtractionHelper.findSchemaNode(mocsInDeviated, mocInAugmented);

			if(mocInBase != null) {
				mocInBase.setCustomAppData(YT_POINTER_TO_MOC_IN_AUGMENTED, mocInAugmented);
				mocInAugmented.setCustomAppData(YT_POINTER_TO_MOC_IN_BASE, mocInBase);
			}
			if(mocInDeviated != null) {
				mocInDeviated.setCustomAppData(YT_POINTER_TO_MOC_IN_AUGMENTED, mocInAugmented);
				mocInAugmented.setCustomAppData(YT_POINTER_TO_MOC_IN_DEVIATED, mocInDeviated);
			}

			if(mocInBase != null || mocInDeviated != null) {
				final List<AbstractStatement> childDataNodesInBase = mocInBase != null ? getChildMocs(mocInBase) : Collections.emptyList();
				final List<AbstractStatement> childDataNodesInAugmented = getChildMocs(mocInAugmented);
				final List<AbstractStatement> childDataNodesInDeviated = mocInDeviated != null ? getChildMocs(mocInDeviated) : Collections.emptyList();

				linkMocsBetweenVariants(childDataNodesInBase, childDataNodesInAugmented, childDataNodesInDeviated);
			}
		});
	}

//	public static final AbstractStatement getBaseVariantMoc(final AbstractStatement mocInAugmented) {
//		return mocInAugmented.getCustomAppData(YT_POINTER_TO_MOC_IN_BASE);
//	}
//	
//	public static final AbstractStatement getDeviatedVariantMoc(final AbstractStatement mocInAugmented) {
//		return mocInAugmented.getCustomAppData(YT_POINTER_TO_MOC_IN_DEVIATED);
//	}

	public static final AbstractStatement getAugmentedVariantMoc(final AbstractStatement mocInBaseOrDeviated) {
		return mocInBaseOrDeviated.getCustomAppData(YT_POINTER_TO_MOC_IN_AUGMENTED);
	}

	// -------------------------------------------------------------------------------------------------------------------------

	private static final String YT_APP_DATA_MOC_NAME_IN_AUGMENTED = "YT_APP_DATA_MOC_NAME_IN_AUGMENTED";

	/**
	 * Assigns the name to the MOCs. This is not quite so simple. Consider the following scenario, assuming that
	 * unique MOs shall always be generated without the $$:
	 * <p>
	 * <u>Version 1 of node:</u><br/>
	 * X -> Z<br/>
	 * Y -> Z<br/>
	 * Generated MOC names for Z: X$$Z, Y$$Z (because they are not unique the $$ syntax is required)
	 * <p>
	 * <u>Version 2 of node:</u><br/>
	 * (suppose X -> Z is deviated out)<br/>
	 * Y -> Z<br/>
	 * Generated MOC names for Z: Z (the <b>second</b> Z --- not using $$ as Z is unique)
	 * <p>
	 * <u>Version 3 of node: (or perhaps a different node type!)</u><br/>
	 * X -> Z<br/>
	 * (suppose Y -> Z is deviated out)<br/>
	 * Generated MOC names for Z: Z (the <b>first</b> Z --- not using $$ as Z is unique)
	 * <p>
	 * Of course, the content of first and second "Z" can be different, which means that they would overwrite each
	 * other when they get deployed! (and good luck debugging this one...).
	 * <p>
	 * The root cause of the issue, as so often, is deviations, which distort the tree. This implies we cannot use
	 * the DEVIATED variant to compute the MOC names, as this is not stable from one node (version) to the next. We
	 * must instead compute the MOC names based on the AUGMENTED variant, as this is stable.
	 * <p>
	 * There is a second issue, which rarely occurs, but when it does, it causes significant problems. In YANG,
	 * namespaces do not have a singleton root. In a situation where a container/list is augmented into another module
	 * multiple times, and with the same name, there is non-uniqueness at the namespace boundary. Example:
	 * <p>
	 * ns-A : container-X {<br/>
	 * &nbsp;&nbsp;ns-B : container-Z {...}<br/>
	 * }<br/>
	 * ns-A : container-Y {<br/>
	 * &nbsp;&nbsp;ns-B : container-Z {...}<br/>
	 * }
	 * <p>
	 * This requires the name of container-Z to be adjusted such to make it unique (by prefixing it with parent MOC
	 * names until it becomes unique). This results in having to use prefixes of MOCs that sit in a different
	 * namespace, which is not ideal as it can be confusing to the user later on - but there is nothing else we can
	 * do (except explicit renaming maybe).
	 */
	private static void assignMocName(final TransformerContext context) {
		/*
		 * Special handling as discussed above
		 */
		handleMocsAtNamespaceBoundary(context, context.getAugmentedSchema());
		/*
		 * Assign them in AUGMENTED
		 */
		assignMocNamesInAugmented(context, context.getAugmentedSchema());
	}

	private static void handleMocsAtNamespaceBoundary(final TransformerContext context, final Schema augmentedSchema) {

		final Map<NamespaceAndIdentifier, List<AbstractStatement>> namespaceBoundaryRootMocs = new HashMap<>();

		/*
		 * 3GPP prohibits duplication of IOC name within a given namespace, hence no need to check for that.
		 */
		final List<AbstractStatement> topLevelMocs = getMocsAtSchemaRoot(augmentedSchema);
		topLevelMocs.forEach(topLevelMoc -> {
			final boolean in3gppBranch = PreProcessor.is3gppMoc(topLevelMoc);
			if(!in3gppBranch) {
				collectNamespaceBoundaryRootMocs(topLevelMoc, namespaceBoundaryRootMocs, null);
			}
		});

		/*
		 * We ignore all non-duplicates, i.e. any namespace/name combination that only exists once.
		 */
		final Map<NamespaceAndIdentifier, List<AbstractStatement>> duplicateNamespaceRootMocs = new HashMap<>();
		namespaceBoundaryRootMocs.entrySet().stream()
				.filter(entry -> entry.getValue().size() > 1)
				.forEach(entry -> duplicateNamespaceRootMocs.put(entry.getKey(), entry.getValue()));

		/*
		 * Whatever is left now we must modify in terms of name to make them unique.
		 */
		duplicateNamespaceRootMocs.values().forEach(duplicates -> makeMocNamesUnique(context, duplicates));
	}

	/**
	 * Recursively creates the mapping between MOC at namespace root and the actual MOCs having
	 * that very namespace/name combination.
	 */
	private static void collectNamespaceBoundaryRootMocs(final AbstractStatement mocInAugmented, final Map<NamespaceAndIdentifier, List<AbstractStatement>> namespaceRootMocs, final String parentMocNamespace) {

		final String mocNamespace = mocInAugmented.getEffectiveNamespace();

		if(!mocNamespace.equals(parentMocNamespace)) {
			/*
			 * We are at the boundary (parent has different namespace, or this is a top-level MOC).
			 */
			final NamespaceAndIdentifier mocNamespaceAndIdentifier = getNamespaceAndIdentifierForMoc(mocInAugmented);

			if(!namespaceRootMocs.containsKey(mocNamespaceAndIdentifier)) {
				namespaceRootMocs.put(mocNamespaceAndIdentifier, new ArrayList<>());
			}
			namespaceRootMocs.get(mocNamespaceAndIdentifier).add(mocInAugmented);
		}

		final List<AbstractStatement> childMocs = getChildMocs(mocInAugmented);
		childMocs.forEach(childMoc -> collectNamespaceBoundaryRootMocs(childMoc, namespaceRootMocs, mocNamespace));
	}

	private static void makeMocNamesUnique(final TransformerContext context, final List<AbstractStatement> duplicateMocs) {
		/*
		 * The supplied statements all have the same name. For each of the statements we now
		 * navigate upwards in the MOC tree, prefixing ancestor names as we go along, until
		 * each entry is unique.
		 * 
		 * A naive implementation would simply prefix the parent MOC name, assuming that the
		 * parent MOC name is unique - but this cannot be guaranteed. So we need to iterate
		 * up the tree. 
		 * 
		 * Map<the-original-MOC-that-is-duplicated, the-current-parent-MOC-during-computation>
		 */
		final Map<AbstractStatement, AbstractStatement> originalMocToCurrentMocAncestor = new HashMap<>();
		duplicateMocs.forEach(duplicateMoc -> originalMocToCurrentMocAncestor.put(duplicateMoc, getParentMocOrModule(duplicateMoc)));

		prefixWithAncestorAndCheckForUniqueness(context, originalMocToCurrentMocAncestor);
	}

	private static void prefixWithAncestorAndCheckForUniqueness(final TransformerContext context, final Map<AbstractStatement, AbstractStatement> originalMocToCurrentMocAncestor) {
		/*
		 * There are some weird corner cases that we need to cater for here.
		 * 
		 * The first corner case is where the same MOC (as identified by original name and namespace) exists
		 * multiple times, but it is not possible to arrive at uniqueness using the usual algorithm because
		 * we are reaching the top level of the schema. Happens, e.g., in PCC. Example:
		 * 
		 * ns1:cont1
		 * ns1:cont1/ns2:cont2
		 * ns2:cont2
		 * 
		 * The most obvious solution to this is to simply not concatenate the parent for the third entry
		 * (as there is no parent). However, this here will prevent that solution from working: 
		 * 
		 * ns1:cont4
		 * ns1:cont4/ns3:cont5
		 * ns2:cont4
		 * ns2:cont4/ns3:cont5
		 *
		 * In this case it is simply not possible to "not prefix", we need to prefix both MOC names for 'cont5'
		 * with something - the easiest here is the owning module name. However, we only want to do this if it
		 * is really, really, necessary - the scenario has never been seen in the wild.
		 *
		 * In short, we will adapt the first solution if there is only a single entry left in the map where we
		 * have arrived at the top-level; the second solution is adopted if there are more than one entries at
		 * the top-level now.
		 */
		final long nrMapEntriesWhereCurrentMocAncestorIsAtModule = originalMocToCurrentMocAncestor.values().stream()
				.filter(stmt -> stmt.is(CY.STMT_MODULE))
				.count();

		/*
		 * Each original MOC gets prefixed. What it gets prefixed with depends very much on the number of
		 * ancestors that are at the top-level
		 */
		originalMocToCurrentMocAncestor.entrySet().forEach(entry -> {

			final AbstractStatement originalMoc = entry.getKey();
			final AbstractStatement currentMocAncestor = entry.getValue();

			final String nameOfOriginalMoc = getRenamedStatementIdentifier(originalMoc);
			final String nameOfCurrentMocAncestor = getNameOfCurrentMocAncestor(context, currentMocAncestor, nrMapEntriesWhereCurrentMocAncestorIsAtModule);

			final String newNameOfOriginalMoc = nameOfCurrentMocAncestor == null ? nameOfOriginalMoc : nameOfCurrentMocAncestor + "$$" + nameOfOriginalMoc;

			setRenamedStatementIdentifier(originalMoc, newNameOfOriginalMoc);
		});

		/*
		 * Now we check if some of the (now amended) MOC names have become unique. Whatever is unique we don't need
		 * to process any further.
		 */
		final Set<String> encountered = new HashSet<>();
		final Set<String> stillDuplicatedMocNames = originalMocToCurrentMocAncestor.keySet().stream()
				.map(originalMoc -> getRenamedStatementIdentifier(originalMoc))
				.filter(name -> !encountered.add(name))
				.collect(Collectors.toSet());

		/*
		 * Whatever is not unique gets processed further. The new map that is constructed retains as keys the original
		 * MOCs (i.e. the MOCs that were originally found at the namespace boundary to be not unique); as values, the
		 * parent MOC of the current parent MOC is used (and thereby we are moving up the tree).
		 */
		final Map<AbstractStatement, AbstractStatement> newMap = new HashMap<>();

		originalMocToCurrentMocAncestor.entrySet().stream()
		.filter(entry -> stillDuplicatedMocNames.contains(getRenamedStatementIdentifier(entry.getKey())))
		.forEach(entry -> newMap.put(entry.getKey(), getParentMocOrModule(entry.getValue())));

		if(newMap.isEmpty()) {
			return;
		}

		prefixWithAncestorAndCheckForUniqueness(context, newMap);
	}

	private static AbstractStatement getParentMocOrModule(final AbstractStatement moc) {

		final AbstractStatement parentMoc = ExtractionHelper.getParentMoc(moc);
		if(parentMoc != null) {
			return parentMoc;
		}

		/*
		 * MOC sits at the top-level of the schema. Return the module, which will be the direct parent.
		 */
		return moc.getParentStatement();
	}

	/**
	 * Given a current ancestor, returns its name. Special handling is applied where the ancestor is not
	 * a data node, but where we have reached module level.
	 */
	private static String getNameOfCurrentMocAncestor(final TransformerContext context, final AbstractStatement currentMocAncestor, final long nrMapEntriesWhereCurrentMocAncestorIsAtModule) {

		if(nrMapEntriesWhereCurrentMocAncestorIsAtModule == 0) {
			/*
			 * Business as usual, simply return the name of the ancestor. This will be the 99.9% case.
			 */
			return getRenamedStatementIdentifier(currentMocAncestor);

		} else if (nrMapEntriesWhereCurrentMocAncestorIsAtModule == 1) {
			/*
			 * Only a single entry in the map has an ancestor at the top-level. So we have for example
			 * this scenario:
			 * 
			 * ns1:cont1/ns2:cont2
			 * ns2:cont2
			 * 
			 * The entry that sist at mosule level (in the above example the second entry) will simply
			 * not be given a value.
			 */
			return currentMocAncestor.is(CY.STMT_MODULE) ? null : getRenamedStatementIdentifier(currentMocAncestor);

		} else {
			/*
			 * Multiple entries in the map have an ancestor at the top-level. Those entries (which will be
			 * modules) will get the module name. So we have for example this here (very constructed TBH):
			 * 
			 * ns1:cont4/ns4:cont8
			 * ns2:cont4/ns4:cont8
			 * ns3:cont5/ns3:cont4/ns4:cont8
			 * 
			 * As can be seen, cont4$$cont8 cannot be used, so it has to be further prefixed. Since there are
			 * two entries in the example (first and second) where there is no other parent, the only thing we
			 * can do is to use the module names for ns1 and ns2, respectively. However, the third entry still
			 * has another ancestor (cont5), so that can be used as prefix.
			 */
			if(currentMocAncestor.is(CY.STMT_MODULE)) {
				final String moduleName = currentMocAncestor.getStatementIdentifier();
				context.logWarn(Constants.TEXT_RENAMED + " Must use module name '" + moduleName + "' as prefix for MOC. Consider renaming the data node!");
				return moduleName;
			} else {
				return getRenamedStatementIdentifier(currentMocAncestor);
			}
		}
	}

	private static void assignMocNamesInAugmented(final TransformerContext context, final Schema augmentedSchema) {
		/*
		 * Unique MOCs may not get the $$, depending on context settings, so collect them.
		 */
		final Set<NamespaceAndIdentifier> uniqueMocs = computeUniqueMocs(context, augmentedSchema);

		/*
		 * Now (eventually!) calculate the MOC names
		 */
		final List<AbstractStatement> topLevelMocs = getMocsAtSchemaRoot(augmentedSchema);
		topLevelMocs.forEach(topLevelMoc -> {
			final boolean in3gppBranch = PreProcessor.is3gppMoc(topLevelMoc);
			assignMocNamesInAugmented(context, topLevelMoc, in3gppBranch, uniqueMocs, null, null);
		});
	}

	private static void assignMocNamesInAugmented(final TransformerContext context, final AbstractStatement mocInAugmented, final boolean in3gppBranch, final Set<NamespaceAndIdentifier> uniqueMocs, final String ancestorNamespace, final String ancestorMocName) {
		/*
		 * - If the MOC name has been modified by the node to include the node app instance name
		 *   then it will sit at the top level of the module and will be unique. No need to prefix. 
		 * - If we are in a 3GPP branch, then we will never use $$ syntax, because 3GPP stipulates
		 *   that all class names within a namespace are unique.
		 * - If it is a unique MO, and we shall never use $$ syntax for unique MOs, well, then we
		 *   will not generate $$ syntax.
		 * - Otherwise check if namespace boundaries are crossed - if no, then it is a MOC at a
		 *   namespace boundary and we won't use $$ - otherwise we need to use $$.
		 * 
		 * Note some differences:
		 * 
		 * - The 3GPP branch is self-contained, meaning that all MOCs in the branch will not use the $$.
		 * - In other branches, there can be a mixture of MOC using $$ and some not using $$. That is
		 *   fine, since the path using $$ under a unique MOC is always unique.  
		 */

		final boolean isUniqueMoc = uniqueMocs.contains(getNamespaceAndIdentifierForMoc(mocInAugmented));

		boolean use$$prefix = true;

		/*
		 * And yes, the below could be written on one line, but isn't this so much easier to read and understand?
		 * Of course SonarQube would complain about the duplication of branches - yet another reason to disable it!
		 */
		if(isNainMoc(mocInAugmented)) {
			use$$prefix = false;
		} else if(in3gppBranch) {
			use$$prefix = false;
		} else if (isUniqueMoc && context.dontUse$$syntaxForUniqueMocs()) {
			use$$prefix = false;
		} else if (!mocInAugmented.getEffectiveNamespace().equals(ancestorNamespace)) {
			use$$prefix = false;
		}

		final String mocPrefix = (use$$prefix && ancestorMocName != null) ? ancestorMocName + "$$" : "";
		final String mocName = mocPrefix + getRenamedStatementIdentifier(mocInAugmented);

		/*
		 * Final adjustment to the name - need to remove the app instance name from it.
		 */
		final String adjustedMocName = isNainMoc(mocInAugmented) ? YangNameVersionUtil.removeNodeAppInstanceName(mocName) : mocName;

		mocInAugmented.setCustomAppData(YT_APP_DATA_MOC_NAME_IN_AUGMENTED, adjustedMocName);

		/*
		 * Work down the tree.
		 */
		final List<AbstractStatement> childMocs = getChildMocs(mocInAugmented);
		childMocs.forEach(childMoc -> assignMocNamesInAugmented(context, childMoc, in3gppBranch, uniqueMocs, mocInAugmented.getEffectiveNamespace(), mocName));
	}

	/**
	 * Goes through the schema and figures out which MOCs are unique (= there is only a single MOC of that name in the namespace).
	 */
	private static Set<NamespaceAndIdentifier> computeUniqueMocs(final TransformerContext context, final Schema schema) {

		final Set<NamespaceAndIdentifier> uniques = new HashSet<>();
		final Set<NamespaceAndIdentifier> duplicates = new HashSet<>();

		if(context.dontUse$$syntaxForUniqueMocs()) {

			final List<AbstractStatement> topLevelMocs = getMocsAtSchemaRoot(schema);
			topLevelMocs.forEach(topLevelMoc -> {
				populateUniquesAndDuplicates(topLevelMoc, uniques, duplicates);
			});
		}

		return uniques;
	}

	private static void populateUniquesAndDuplicates(final AbstractStatement moc, final Set<NamespaceAndIdentifier> uniques, final Set<NamespaceAndIdentifier> duplicates) {

		final NamespaceAndIdentifier mocNsai = getNamespaceAndIdentifierForMoc(moc);

		if(!duplicates.contains(mocNsai)) {
			if (uniques.contains(mocNsai)) {
				uniques.remove(mocNsai);		// Unique until now, now it's a duplicate.
				duplicates.add(mocNsai);
			} else {							// newly encountered
				uniques.add(mocNsai);
			}
		}

		final List<AbstractStatement> childMocs = getChildMocs(moc);
		childMocs.forEach(childMoc -> populateUniquesAndDuplicates(childMoc, uniques, duplicates));
	}

	private static NamespaceAndIdentifier getNamespaceAndIdentifierForMoc(final AbstractStatement moc) {
		return new NamespaceAndIdentifier(moc.getEffectiveNamespace(), getRenamedStatementIdentifier(moc));
	}

	public static String getMocName(final AbstractStatement mocInAnyVariant) {
		/*
		 * The MOC name has been calculated in the augmented variant. We must navigate
		 * to it if required.
		 */
		final AbstractStatement mocInAugmented = isInAugmentedVariant(mocInAnyVariant) ? mocInAnyVariant : getAugmentedVariantMoc(mocInAnyVariant);
		return mocInAugmented.getCustomAppData(YT_APP_DATA_MOC_NAME_IN_AUGMENTED);
	}

	private static List<AbstractStatement> getMocsAtSchemaRoot(final Schema schema) {
		return ExtractionHelper.getStatementsAtSchemaRoot(schema, PreProcessor::isMoc);
	}

	private static List<AbstractStatement> getChildMocs(final AbstractStatement parentMoc) {
		return ExtractionHelper.getChildDataNodes(parentMoc, PreProcessor::isMoc);
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	private static final String YT_APP_DATA_LIST_KEY = "YT_APP_DATA_LIST_KEY";
	private static final String YT_APP_DATA_KEY_NAMES = "YT_APP_DATA_KEY_NAMES";
	private static final String YT_APP_DATA_KEYLESS_LIST = "YT_APP_DATA_KEYLESS_LIST";

	private static void attachAppDataForLists(final TransformerContext context) {
		attachAppDataForLists(context, context.getBaseSchema());
		attachAppDataForLists(context, context.getAugmentedSchema());
		attachAppDataForLists(context, context.getDeviatedSchema());
	}

	/**
	 * For each list, figure out its keys (or whether it does not have any keys).
	 */
	private static void attachAppDataForLists(final TransformerContext context, final Schema schema) {

		final List<AbstractStatement> canHaveKeys = ExtractionHelper.getStatementsInSchema(schema, PreProcessor::canHaveKeyAsChild);
		canHaveKeys.forEach(parent -> {

			final YKey yKey = parent.getChild(CY.STMT_KEY);
			final List<String> keyNames = yKey != null ? yKey.getKeys() : Collections.<String>emptyList();

			if(keyNames.isEmpty()) {
				/*
				 * The list has no keys. Can happen in 'config false' branches.
				 */
				parent.setCustomAppData(YT_APP_DATA_KEYLESS_LIST, null);

			} else {
				/*
				 * We hold onto the list of key names in the original order. Needed later on to handle
				 * multi-key lists.
				 */
				parent.setCustomAppData(YT_APP_DATA_KEY_NAMES, keyNames);

				/*
				 * Find each key and mark them, and record their position
				 */
				ExtractionHelper.getChildDataNodes(parent, stmt -> stmt.is(CY.STMT_LEAF) && keyNames.contains(PreProcessor.getRenamedStatementIdentifier(stmt)))
					.forEach(keyLeaf -> keyLeaf.setCustomAppData(YT_APP_DATA_LIST_KEY, null));
			}
		});

		/*
		 * We also mark the "id" attribute of any container that has been renamed with a node app instance
		 * name as key, as we will make a "list" out of it in ENM and we will use the 'id' attribute that
		 * will contain the instance name as MO ID.
		 */
		ExtractionHelper.getStatementsAtSchemaRoot(schema, PreProcessor::isNainMoc).forEach(container -> {
			final AbstractStatement idLeaf = AppInstanceNameSupport.getIdLeafOfContainerWithAppInstanceName(container);
			idLeaf.setCustomAppData(YT_APP_DATA_LIST_KEY, null);
			container.setCustomAppData(YT_APP_DATA_KEY_NAMES, Collections.singletonList(idLeaf.getStatementIdentifier()));
		});
	}

	private static boolean canHaveKeyAsChild(final AbstractStatement stmt) {
		return stmt.is(CY.STMT_LIST) || stmt.is(CORAN.ORAN_SMO_TEIV_COMMON_YANG_EXTENSIONS__BI_DIRECTIONAL_TOPOLOGY_RELATIONSHIP);
	}

	public static boolean isKeyLessList(final AbstractStatement containerOrList) {
		return containerOrList.hasCustomAppData(YT_APP_DATA_KEYLESS_LIST);
	}

	public static boolean isListKey(final AbstractStatement dataNode) {
		return dataNode.hasCustomAppData(YT_APP_DATA_LIST_KEY);
	}

	public static List<String> getListKeyNames(final AbstractStatement dataNode) {
		return dataNode.getCustomAppData(YT_APP_DATA_KEY_NAMES);
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------

	private static final String YT_APP_DATA_NOTIFIED = "YT_APP_DATA_NOTIFIED";

	private static void applyNotificationBehaviour(final TransformerContext context) {
		applyNotificationBehaviour(context, context.getBaseSchema());
		applyNotificationBehaviour(context, context.getAugmentedSchema());
		applyNotificationBehaviour(context, context.getDeviatedSchema());
	}

	/**
	 * Applies the notification behaviour. Anything that is not notified cannot be stored in the ENM mirror. The
	 * notification behaviour of a node depends on the node's NBI. Most supported nodes are EOI nodes, but in the
	 * future we will also support O1 nodes (and perhaps others).
	 */
	private static void applyNotificationBehaviour(final TransformerContext context, final Schema schema) {
		switch(context.getNotificationBehaviour()) {
		case EOI:
			applyNotificationBehaviourEoi(context, schema);
			break;
//		case EOI_O1:
//			applyNotificationBehaviourEoiO1(context, schema);
//			break;
		}
	}

	/*
	 * TODO - there will be an issue when we implement support for 3GPP. We always generate a DPS PT and an extension,
	 * and we go to some lengths to make sure that the generated DPS PT is always the exact same. However, the semantics
	 * in respect of notification handling for config false differs between EOI and 3GPP - in EOI, they would not be
	 * notifiable; in 3GPP they would. Also in 3GPP it is possible to mark config true data nodes as not-notified, which
	 * is not possible in EOI. We should generate the DPS PT always the same, e.g. by assuming that config true will
	 * always generate notifications, and then apply interface-based notification behaviour only on DEVIATED so that it
	 * gets generated into the extension. Might not work very well for m-plane where really nothing is notified, but so
	 * be it - we would be generating a lot of extension models then.
	 */

	/**
	 * Applies notification behaviour for EOI nodes.
	 */
	private static void applyNotificationBehaviourEoi(final TransformerContext context, final Schema schema) {
		final List<AbstractStatement> dataNodesAtRoot = ExtractionHelper.getStatementsAtSchemaRoot(schema, AbstractStatement::definesDataNode);
		applyNotificationBehaviourEoi(context, dataNodesAtRoot);
	}

	private static void applyNotificationBehaviourEoi(final TransformerContext context, final List<AbstractStatement> dataNodes) {

		for(final AbstractStatement dataNode : dataNodes) {
			/*
			 * In EOI, a data node is notified when it is 'config true', or effectively 'NSD true'.
			 * 
			 * Recently there has been a change in interpretation of the NSD extension - upwards inheritance was
			 * formally removed, as it was not implemented by anybody. So only downwards inheritance remains.
			 * That's ok in general, but there is a gotcha - some models are placing 'NSD true' on an attribute,
			 * but not the enclosing container / list. This is OK as long as the parent data node is an NP-container,
			 * because mediation can figure out to implicitly create the parent instance as well from the NETCONF
			 * notification. So we must also consider the ancestors.
			 */
			final boolean configTrue = dataNode.isEffectiveConfigTrue();
			final boolean nsdTrue = EriCustomProcessor.isStatementEffectivelyNsdTrue(dataNode);

			if(configTrue || nsdTrue) {
				setNotified(dataNode);
			}
			if(!configTrue && nsdTrue) {
				setAncestorNpContainersToNotified(context, dataNode.getParentStatement());
			}

			applyNotificationBehaviourEoi(context, ExtractionHelper.getChildDataNodes(dataNode));
		}
	}

	private static void setAncestorNpContainersToNotified(final TransformerContext context, final AbstractStatement stmt) {

		if(isNotified(stmt)) {
			/*
			 * The statement is notified, there is no need to navigate up the tree any further.
			 */
		}
		if(stmt.is(CY.STMT_MODULE)) {
			/*
			 * We have reached the top of the module, we are finished.
			 */
			return;
		}
		if(stmt.is(CY.STMT_CHOICE) || stmt.is(CY.STMT_CASE)) {
			/*
			 * Need to ignore choice/case, keep moving upwards.
			 */
			setAncestorNpContainersToNotified(context, stmt.getParentStatement());
		}
		if(PropertyUtils.isNPContainer(stmt)) {
			/*
			 * NP container. Implicitly also notified. Mark as such, and go upwards.
			 */
			setNotified(stmt);
			setAncestorNpContainersToNotified(context, stmt.getParentStatement());
		}

		/*
		 * Something else (maybe a list). If we have come to this point then there is an issue: An ancestor is
		 * not notifiable somewhere. This means that the "chain of notifiable" is broken, so there is a gap in
		 * 'NSD true' somewhere along the line. Validator would have found this, be sure to issue a warning
		 * because it will trip us up.
		 */
		context.logWarn(Constants.TEXT_IGNORED_NOTIFIED_BROKEN_CHAIN + " data node '" + ExtractionHelper.getHumanReadablePathToSchemaNode(stmt) + "' is not notified; descendent data nodes having 'NSD true' will be ignored.");
	}

	public static void setNotified(final AbstractStatement dataNode) {
		dataNode.setCustomAppData(YT_APP_DATA_NOTIFIED);
	}

	public static boolean isNotified(final AbstractStatement dataNode) {
		return dataNode.hasCustomAppData(YT_APP_DATA_NOTIFIED);
	}
}
