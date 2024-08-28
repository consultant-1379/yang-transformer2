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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.schema.gen.oss_common.EModelDefinition;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaUtil;
import com.ericsson.oss.services.cm.modeling.schema.gen.cfm_miminfo.ManagementInformationModelInformation;

/**
 * Keeps track of all the EModels generated
 */
public class CreatedEModels {

	private final List<EModelDefinition> generatedEModels = new ArrayList<>();

	public void addGeneratedEModel(final EModelDefinition emodel) {

		if(containsGeneratedEModel(emodel)) {
			/*
			 * This is a fail-safe to guard against any possibly code/logic bugs where the same model
			 * is generated twice. It should never fire!
			 */
			throw new YangTransformer2Exception("This model has already been created and added previously! - " + emodel.getClass().toString() + "/" + emodel.getNs() + "/" + emodel.getName() + "/" +emodel.getVersion());
		}

		generatedEModels.add(emodel);
	}

	/**
	 * Removes from the list of generated models all entries passing the supplied predicate.
	 */
	public void removeGeneratedEModels(final Predicate<EModelDefinition> predicate) {
		generatedEModels.removeIf(predicate);
	}

	public boolean containsGeneratedEModel(final ModelInfo modelInfo) {
		return getGeneratedEModel(modelInfo) != null;
	}

	private boolean containsGeneratedEModel(final EModelDefinition emodel) {
		return getGeneratedEModel(emodel.getClass(), emodel.getNs(), emodel.getName(), emodel.getVersion()) != null;
	}

	@SuppressWarnings("unchecked")
	public <T extends EModelDefinition> T getGeneratedEModel(final ModelInfo modelInfo) {
		final Class<EModelDefinition> eModelClass = (Class<EModelDefinition>) SchemaUtil.getRootJavaTypeFor(modelInfo.getSchema());
		return (T) getGeneratedEModel(eModelClass, modelInfo.getNamespace(), modelInfo.getName(), modelInfo.getVersion().toString());
	}

	@SuppressWarnings("unchecked")
	private <T extends EModelDefinition> T getGeneratedEModel(final Class<T> eModelClass, final String namespace, final String name, final String version) {
		return (T) generatedEModels.stream()
				.filter(emd -> emd.getClass().equals(eModelClass))
				.filter(emd -> emd.getNs().equals(namespace))
				.filter(emd -> emd.getName().equals(name))
				.filter(emd -> emd.getVersion().equals(version))
				.findFirst()
				.orElse(null);
	}

	public List<EModelDefinition> getGeneratedEModels() {
		return generatedEModels;
	}

	// --------------------------------------------------------------------------------------
	//
	// The CFM MIM INFO that was generated.

	private ManagementInformationModelInformation generatedCfmMimInfoModel = null;

	public void setGeneratedCfmMimInfoModel(final ManagementInformationModelInformation model) {
		this.generatedCfmMimInfoModel = model;
	}

	public ManagementInformationModelInformation getGeneratedCfmMimInfoModel() {
		return generatedCfmMimInfoModel;
	}
}
