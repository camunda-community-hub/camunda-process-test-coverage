package org.camunda.bpm.extension.process_test_coverage.trace;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageTestRunState;

public class PathCoverageExecutionListener implements ExecutionListener {

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		CoveredElement coveredElement = CoveredElementBuilder
				.createTrace(execution.getProcessDefinitionId())
				.withCurrentTransitionId(execution.getCurrentTransitionId()).build();
		getTestCoverageTestRunState().notifyCoveredElement(coveredElement);
	}

	private TestCoverageTestRunState getTestCoverageTestRunState() {
		return TestCoverageTestRunState.INSTANCE();
	}

}
