package org.camunda.bpm.extension.process_test_coverage.trace;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;

public class PathCoverageExecutionListener implements ExecutionListener {
    
    private CoverageTestRunState coverageTestRunState;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		CoveredElement coveredElement = CoveredElementBuilder
				.createTrace(execution.getProcessDefinitionId())
				.withCurrentTransitionId(execution.getCurrentTransitionId()).build();
		getCoverageTestRunState().notifyCoveredElement(coveredElement);
	}

    public CoverageTestRunState getCoverageTestRunState() {
        return coverageTestRunState;
    }

    public void setCoverageTestRunState(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }

}
