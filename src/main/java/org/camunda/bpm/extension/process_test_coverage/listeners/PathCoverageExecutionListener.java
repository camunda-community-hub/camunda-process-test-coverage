package org.camunda.bpm.extension.process_test_coverage.listeners;

import java.util.logging.Logger;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredFlowNode;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredSequenceFlow;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

/**
 * Listener taking note of covered sequence flows.
 * 
 * @author grossax
 * @author z0rbas
 *
 */
public class PathCoverageExecutionListener implements ExecutionListener {

    private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    /**
     * The state of the currently running coverage test.
     */
    private CoverageTestRunState coverageTestRunState;

    public PathCoverageExecutionListener(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {

        if (coverageTestRunState == null) {
            logger.warning("Coverage execution listener in use but no coverage run state assigned!");
            return;
        }

        final RepositoryService repositoryService = execution.getProcessEngineServices().getRepositoryService();

        // Get the process definition in order to obtain the key
        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(
                execution.getProcessDefinitionId()).singleResult();

        final String transitionId = execution.getCurrentTransitionId();

        // Record sequence flow coverage
        final CoveredSequenceFlow coveredSequenceFlow = new CoveredSequenceFlow(processDefinition.getKey(),
                transitionId);
        coverageTestRunState.addCoveredElement(coveredSequenceFlow);

        // Record possible event coverage
        handleEvent(transitionId, processDefinition, repositoryService);

    }

    /**
     * Events aren't reported like SequenceFlows and Activities, so we need
     * special handling. If a sequence flow has an event as the source or the
     * target, we add it to the coverage. It's pretty straight forward if a
     * sequence flow is active, then it's source has been covered anyway and it
     * will most definitely arrive at its target.
     * 
     * @param transitionId
     * @param processDefinition
     * @param repositoryService
     */
    private void handleEvent(String transitionId, ProcessDefinition processDefinition,
            RepositoryService repositoryService) {

        final BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinition.getId());

        final ModelElementInstance modelElement = modelInstance.getModelElementById(transitionId);
        if (modelElement.getElementType().getInstanceType() == SequenceFlow.class) {

            final SequenceFlow sequenceFlow = (SequenceFlow) modelElement;

            // If there is an event at the sequence flow source add it to the
            // coverage
            final FlowNode source = sequenceFlow.getSource();
            addEventToCoverage(processDefinition, source);

            // If there is an event at the sequence flow target add it to the
            // coverage
            final FlowNode target = sequenceFlow.getTarget();
            addEventToCoverage(processDefinition, target);

        }

    }

    private void addEventToCoverage(ProcessDefinition processDefinition, FlowNode node) {

        if (node instanceof IntermediateThrowEvent) {

            final CoveredFlowNode coveredElement = new CoveredFlowNode(processDefinition.getKey(), node.getId());
            // We consider entered throw elements as also ended
            coveredElement.setEnded(true);

            coverageTestRunState.addCoveredElement(coveredElement);
        }
    }

}
