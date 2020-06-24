package org.camunda.bpm.extension.process_test_coverage.listeners;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

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
    private MethodHandle handleEvent;

    /**
     * @since 7.10.0
     */
    @Override
    public void handleEvent(EventSubscriptionEntity eventSubscription, Object payload, Object localPayload,
      String businessKey, CommandContext commandContext) {
      addCompensationEventCoverage(eventSubscription);
      super.handleEvent(eventSubscription, payload, localPayload, businessKey, commandContext);
    }
    
    private void addCompensationEventCoverage(EventSubscriptionEntity eventSubscription) {
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
    }

    public void setCoverageTestRunState(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }

    /**
     * Implementation for older Camunda versions prior to 7.10.0
     */
    public void handleEvent(EventSubscriptionEntity eventSubscription, Object payload, CommandContext commandContext) {
        addCompensationEventCoverage(eventSubscription);

        // invoke super.handleEvent() in a backwards compatible way
        try {
          if (handleEvent == null) {
            handleEvent = MethodHandles.lookup()
                .findSpecial(CompensationEventHandler.class, "handleEvent",
                    MethodType.methodType(void.class, EventSubscriptionEntity.class, Object.class, CommandContext.class),
                    CompensationEventCoverageHandler.class);
          }
          handleEvent.invoke(this, eventSubscription, payload, commandContext);
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
    }

}
