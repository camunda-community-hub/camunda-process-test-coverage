package org.camunda.bpm.extension.process_test_coverage.trace;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;

public class PathCoverageExecutionListener implements ExecutionListener {
    
    private CoverageTestRunState coverageTestRunState;

	@Override
    public void notify(DelegateExecution execution) throws Exception {
	    
	    final ProcessDefinition processDefinition = execution.getProcessEngineServices().getRepositoryService()
	            .createProcessDefinitionQuery().processDefinitionId(execution.getProcessDefinitionId()).singleResult();
	    
    	CoveredElement coveredElement = CoveredElementBuilder
    			.createTrace(processDefinition.getKey())
    			.withCurrentTransitionId(execution.getCurrentTransitionId()).build();
    	getCoverageTestRunState().addCoveredElement(coveredElement);
    }

    public CoverageTestRunState getCoverageTestRunState() {
        return coverageTestRunState;
    }

    public void setCoverageTestRunState(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }

}
