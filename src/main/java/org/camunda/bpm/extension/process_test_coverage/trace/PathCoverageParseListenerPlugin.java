package org.camunda.bpm.extension.process_test_coverage.trace;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;

public class PathCoverageParseListenerPlugin extends AbstractProcessEnginePlugin {

	@Override
	public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

		// get all existing postParseListeners
		List<BpmnParseListener> postParseListeners = processEngineConfiguration.getCustomPostBPMNParseListeners();

		if (postParseListeners == null) {

			// if no preParseListener exists, create new list
			postParseListeners = new ArrayList<BpmnParseListener>();
			processEngineConfiguration.setCustomPostBPMNParseListeners(postParseListeners);
		}

		// add new BPMN Parse Listener
		postParseListeners.add(new PathCoverageParseListener());
	}
}