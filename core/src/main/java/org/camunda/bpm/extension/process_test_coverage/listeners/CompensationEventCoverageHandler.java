package org.camunda.bpm.extension.process_test_coverage.listeners;

import org.camunda.bpm.engine.impl.bpmn.helper.BpmnProperties;
import org.camunda.bpm.engine.impl.event.CompensationEventHandler;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredFlowNode;
import org.camunda.bpm.extension.process_test_coverage.util.Api;

/**
 * Compensation event handler registering compensation tasks and their source
 * boundary events.
 * 
 * @author z0rbas
 *
 */
public class CompensationEventCoverageHandler extends CompensationEventHandler {

    private CoverageTestRunState coverageTestRunState;

    @Override
    public void handleEvent(EventSubscriptionEntity eventSubscription, Object payload, CommandContext commandContext) {

        if (Api.Camunda.supportsCompensationEventCoverage()) {

            final ActivityImpl activity = eventSubscription.getActivity();

            // Get process definition key
            final ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) activity.getProcessDefinition();
            final String processDefinitionKey = processDefinition.getKey();

            // Get compensation boundary event ID
            final ActivityImpl sourceEvent = (ActivityImpl) activity.getProperty(
                BpmnProperties.COMPENSATION_BOUNDARY_EVENT.getName());

            if (sourceEvent != null) {

                final String sourceEventId = sourceEvent.getActivityId();

                // Register covered element
                final CoveredFlowNode compensationBoundaryEvent = new CoveredFlowNode(processDefinitionKey, sourceEventId);
                compensationBoundaryEvent.setEnded(true);
                coverageTestRunState.addCoveredElement(compensationBoundaryEvent);

            }

        }

        super.handleEvent(eventSubscription, payload, commandContext);

    }

    public void setCoverageTestRunState(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }

}
