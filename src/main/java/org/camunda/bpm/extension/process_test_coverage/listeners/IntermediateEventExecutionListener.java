package org.camunda.bpm.extension.process_test_coverage.listeners;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredFlowNode;

/**
 * Execution listener registering intermediate events.
 * 
 * @author z0rbas
 *
 */
public class IntermediateEventExecutionListener implements ExecutionListener {

    private CoverageTestRunState coverageTestRunState;

    public IntermediateEventExecutionListener(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {

        final CoveredFlowNode coveredFlowNode = createCoveredFlowNode(execution);

        final String eventName = execution.getEventName();
        if (eventName.equals(ExecutionListener.EVENTNAME_START)) {

            coverageTestRunState.addCoveredElement(coveredFlowNode);

        } else if (eventName.equals(ExecutionListener.EVENTNAME_END)) {

            coverageTestRunState.endCoveredElement(coveredFlowNode);
        }

    }

    private CoveredFlowNode createCoveredFlowNode(DelegateExecution execution) {

        // Get the process definition in order to obtain the key
        final RepositoryService repositoryService = execution.getProcessEngineServices().getRepositoryService();
        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(
                execution.getProcessDefinitionId()).singleResult();

        final String currentActivityId = execution.getCurrentActivityId();

        final CoveredFlowNode coveredFlowNode = new CoveredFlowNode(processDefinition.getKey(), currentActivityId);

        return coveredFlowNode;
    }

}
