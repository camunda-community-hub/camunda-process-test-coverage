package org.camunda.bpm.extension.process_test_coverage.listeners;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredSequenceFlow;

/**
 * Listener taking note of covered sequence flows.
 * 
 * @author grossax
 * @author z0rbas
 *
 */
public class PathCoverageExecutionListener implements ExecutionListener {
    
    /**
     * The state of the currently running coverage test.
     */
    private CoverageTestRunState coverageTestRunState;
    
    public PathCoverageExecutionListener(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }

	@Override
    public void notify(DelegateExecution execution) throws Exception {
	    
	    // Get the process definition in order to obtain the key
	    final ProcessDefinition processDefinition = execution.getProcessEngineServices().getRepositoryService()
	            .createProcessDefinitionQuery().processDefinitionId(execution.getProcessDefinitionId()).singleResult();
	    
	    final CoveredSequenceFlow coveredSequenceFlow = 
	            new CoveredSequenceFlow(processDefinition.getKey(), execution.getCurrentTransitionId());

    	coverageTestRunState.addCoveredElement(coveredSequenceFlow);
    }

}
