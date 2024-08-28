package com.ericsson.oss.mediation.modeling.yangtools.transformer2.testutils;

import com.ericsson.oss.itpf.modeling.modelservice.event.handling.spi.ModelsDeploymentEventService;
import com.ericsson.oss.itpf.modeling.modelservice.listener.ModelsDeploymentEventListener;

/**
 * This prevents an annoying error message coming out from Model Service that just adds noise to the build log.
 */
public class NullModelsDeploymentEventService implements ModelsDeploymentEventService {

	@Override
	public void addModelsDeploymentEventListener(ModelsDeploymentEventListener arg0) {
	}
}
