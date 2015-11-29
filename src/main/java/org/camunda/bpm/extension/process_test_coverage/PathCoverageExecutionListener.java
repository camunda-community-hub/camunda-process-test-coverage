package org.camunda.bpm.extension.process_test_coverage;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElementBuilder;

public class PathCoverageExecutionListener implements ExecutionListener {

	private static final Logger log = java.util.logging.Logger
			.getLogger(PathCoverageExecutionListener.class.getCanonicalName());

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		// TODO cleanup
		log.info("  [transitionId: " + execution.getCurrentTransitionId() + "]" + execution);
		System.err.println("notify(" + execution + ")");
		CoveredElement coveredElement = CoveredElementBuilder
				.createTrace(execution.getProcessDefinitionId())
				.withCurrentTransitionId(execution.getCurrentTransitionId()).build();
		getTestCoverageTestRunState().notifyCoveredElement(coveredElement);
	}

	TestCoverageTestRunState testCoverageTestRunState;
	private TestCoverageTestRunState getTestCoverageTestRunState() {
		// FIXME do via injection, fallback to singleton
		if (testCoverageTestRunState == null) {
			testCoverageTestRunState = TestCoverageTestRunState.INSTANCE();
		}
		return testCoverageTestRunState;
	}

}
