package com.ericsson.oss.mediation.modeling.yangtools.transformer2.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.identifiergenerator.ModelIdentifierGenerator;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.yangtools.transformer2.Constants;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.SupportedMimType;

public class CfmMimInfoSupport {

	public static final String CONFORMANCE_IMPLEMENT = "IMPLEMENT";
	public static final String CONFORMANCE_IMPORT = "IMPORT";

	public static final String FEATURE_HANDLING_ALL_RETAINED = "ALL_RETAINED";
	public static final String FEATURE_HANDLING_ALL_REMOVED = "ALL_REMOVED";
	public static final String FEATURE_HANDLING_CONFIGURED = "CONFIGURED";

	public static final String YAM_TYPE_MODULE = "MODULE";
	public static final String YAM_TYPE_SUBMODULE = "SUBMODULE";

	/**
	 * Creates a new {@link SupportedMimType} object for a module based on the supplied information. 
	 * 
	 * @param emodelNamespace The namespace declared by the module
	 * @param emodelXyzVersion The EModel x.y.z version of the module
	 * @param yamModelInfo The ModelInfo object describing the YAM
	 * 
	 * @deprecated
	 */
	@Deprecated
	public static SupportedMimType createSupportedMimTypeForModule(final String emodelNamespace, final String emodelXyzVersion, final ModelInfo yamModelInfo) {

		final SupportedMimType mimType = new SupportedMimType();

		mimType.setNamespace(Objects.requireNonNull(emodelNamespace));
		mimType.setVersion(Objects.requireNonNull(emodelXyzVersion));
		mimType.setOriginalModelUrn(yamModelInfo.toUrn());

		return mimType;
	}

	/**
	 * Creates a new {@link SupportedMimType} object for a submodule based on the supplied information. 
	 * 
	 * @param emodelNamespace The namespace declared by the module that owns the submodule
	 * @param submoduleName The name of the submodule
	 * @param emodelXyzVersion The EModel x.y.z version of the submodule
	 * @param submoduleYamModelInfo The ModelInfo object describing the YAM
	 * 
	 * @deprecated
	 */
	@Deprecated
	public static SupportedMimType createSupportedMimTypeForSubmodule(final String emodelNamespace, final String submoduleName, final String emodelXyzVersion, final ModelInfo submoduleYamModelInfo) {

		final SupportedMimType mimType = new SupportedMimType();

		final String namespaceToUse = Objects.requireNonNull(emodelNamespace) + "::" + Objects.requireNonNull(submoduleName);

		mimType.setNamespace(namespaceToUse);
		mimType.setVersion(Objects.requireNonNull(emodelXyzVersion));
		mimType.setOriginalModelUrn(submoduleYamModelInfo.toUrn());

		return mimType;
	}

	/**
	 * Creates a new {@link SupportedMimType} object for a module based on the supplied information. 
	 * 
	 * @param emodelNamespace The namespace declared by the module
	 * @param emodelXyzVersion The EModel x.y.z version of the module
	 * @param yamModelInfo The ModelInfo object describing the YAM
	 * @param featureHandling How features shall be handled
	 * @param features The enabled features for this module, if any
	 * @param conformance The conformance type of the module
	 * @param yamName The name of the YAM
	 */
	public static SupportedMimType createSupportedMimTypeForModule(final String emodelNamespace, final String emodelXyzVersion, final ModelInfo yamModelInfo, final String featureHandling, final List<String> features, final String conformance, final String yamName) {

		final SupportedMimType mimType = new SupportedMimType();

		mimType.setNamespace(Objects.requireNonNull(emodelNamespace));
		mimType.setVersion(Objects.requireNonNull(emodelXyzVersion));
		mimType.setOriginalModelUrn(yamModelInfo.toUrn());

		mimType.setFeatureHandling(Objects.requireNonNull(featureHandling));
		mimType.getFeatures().addAll(features);
		mimType.setConformance(Objects.requireNonNull(conformance));
		mimType.setType(YAM_TYPE_MODULE);
		mimType.setYamName(Objects.requireNonNull(yamName));

		return mimType;
	}

	/**
	 * Creates a new {@link SupportedMimType} object for a submodule based on the supplied information. 
	 * 
	 * @param emodelNamespace The namespace declared by the module that owns the submodule
	 * @param submoduleName The name of the submodule
	 * @param emodelXyzVersion The EModel x.y.z version of the submodule
	 * @param submoduleYamModelInfo The ModelInfo object describing the YAM
	 * @param yamName The name of the YAM
	 */
	public static SupportedMimType createSupportedMimTypeForSubmodule(final String emodelNamespace, final String submoduleName, final String emodelXyzVersion, final ModelInfo submoduleYamModelInfo, final String yamName) {

		final SupportedMimType mimType = new SupportedMimType();

		final String namespaceToUse = Objects.requireNonNull(emodelNamespace) + "::" + Objects.requireNonNull(submoduleName);

		mimType.setNamespace(namespaceToUse);
		mimType.setVersion(Objects.requireNonNull(emodelXyzVersion));
		mimType.setOriginalModelUrn(submoduleYamModelInfo.toUrn());

		mimType.setType(YAM_TYPE_SUBMODULE);
		mimType.setYamName(Objects.requireNonNull(yamName));

		return mimType;
	}

	/**
	 * Creates a new {@link SupportedMimType} object for the mock ManagedElement.
	 * 
	 * @param mockManagedElementNamespace The namespace of the mock ManagedElement
	 */
	public static SupportedMimType createSupportedMimTypeForMockManagedElement(final String mockManagedElementNamespace) {

		final ModelInfo mockInfo = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, Objects.requireNonNull(mockManagedElementNamespace), Constants.MANAGEDELEMENT, Constants.ONE_ZERO_ZERO);

		/*
		 * The value for 'original model URN' is the URN of the mock ManagedElement itself - nothing else we can do...
		 */
		final SupportedMimType mimType = new SupportedMimType();

		mimType.setNamespace(mockInfo.getNamespace());
		mimType.setVersion(mockInfo.getVersion().toString());
		mimType.setOriginalModelUrn(mockInfo.toUrn());

		return mimType;
	}

	/**
	 * Generates the EModel version for a cfm_miminfo. The version will be a hash-code in x.y.z format, build
	 * from the contents of the supported MIMs.
	 * <p>
	 * Note: no need to sort the entries beforehand, the generator will do that for us.
	 * 
	 * @param supportedMims The supported MIM entries for the cfm_miminfo
	 * @param transformerVersion The version of the transformer
	 */
	public static String calculateCfmMimInfoEModelVersion(final List<SupportedMimType> supportedMims, final String transformerVersion) {

		final Map<String, String> mapOfNamespaceToVersion = new LinkedHashMap<>();
		for (final SupportedMimType supportedMim : supportedMims) {
			mapOfNamespaceToVersion.put(supportedMim.getNamespace(), supportedMim.getVersion());
		}

		/*
		 * TODO - this will have to become more elaborate. The various options supplied to the context have a direct influence
		 * on the output. So the options from the context have to be captured here as well as part of the hash-code generation.
		 */

		/*
		 * Add separate entry for the transformer version, which will cause a different version to be generated
		 * if the transformer version changes.
		 */
		mapOfNamespaceToVersion.put("___YANG_TRANSFORMER2_VERSION___", transformerVersion);

		final ModelIdentifierGenerator modelIdentifierGenerator = new ModelIdentifierGenerator();
		final String identifier = modelIdentifierGenerator.generateIdentifier(mapOfNamespaceToVersion);
		return modelIdentifierGenerator.formatAsXyzVersion(identifier);
	}
}
