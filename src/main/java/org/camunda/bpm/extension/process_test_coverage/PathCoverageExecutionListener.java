package org.camunda.bpm.extension.process_test_coverage;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class PathCoverageExecutionListener implements ExecutionListener {

	private static final Logger log = java.util.logging.Logger
			.getLogger(PathCoverageExecutionListener.class.getCanonicalName());

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		log.info("  [transitionId: " + execution.getCurrentTransitionId() + "]" + execution);
		System.out.println("notify(" + execution + ")");
	}

}
