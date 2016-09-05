package org.camunda.bpm.extension.process_test_coverage.listeners;

import java.util.EnumSet;
import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.engine.impl.history.handler.DbHistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredFlowNode;

/**
 * Extends the {@link DbHistoryEventHandler} in order to notify the process test
 * coverage of a covered activity.
 */
public class FlowNodeHistoryEventHandler extends DbHistoryEventHandler {

    private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    /**
     * The state of the currently running coverage test.
     */
    private CoverageTestRunState coverageTestRunState;

    public FlowNodeHistoryEventHandler() {
    }

    @Override
    public void handleEvent(HistoryEvent historyEvent) {
        super.handleEvent(historyEvent);

        if (coverageTestRunState == null) {
            logger.warning("Coverage history event listener in use but no coverage run state assigned!");
            return;
        }

        if (historyEvent instanceof HistoricActivityInstanceEventEntity) {

            HistoricActivityInstanceEventEntity activityEvent = (HistoricActivityInstanceEventEntity) historyEvent;

            final CoveredFlowNode coveredActivity = new CoveredFlowNode(historyEvent.getProcessDefinitionKey(),
                    activityEvent.getActivityId());

            // Cover event start
            if (isInitialEvent(historyEvent)) {

                coverageTestRunState.addCoveredElement(coveredActivity);

            }
            // Cover event end
            else if (isEndEvent(historyEvent)) {

                coverageTestRunState.endCoveredElement(coveredActivity);
            }

        }

    }

    /**
     * Aimed to be the opposite of
     * {@link DbHistoryEventHandler#isInitialEvent()}.
     * 
     * Future versions of Camunda will probably introduce additional events
     * requiring this method to be updated.
     * 
     * @param historyEvent
     * @return
     */
    private boolean isEndEvent(HistoryEvent historyEvent) {

        EnumSet<HistoryEventTypes> endEventTypes = EnumSet.of(HistoryEventTypes.ACTIVITY_INSTANCE_END,
                HistoryEventTypes.PROCESS_INSTANCE_END,
                HistoryEventTypes.TASK_INSTANCE_COMPLETE,
                HistoryEventTypes.INCIDENT_RESOLVE,
                HistoryEventTypes.CASE_INSTANCE_CLOSE,
                HistoryEventTypes.BATCH_END);

        // They should have handled compare/equals in the enum itself
        for (HistoryEventTypes endEventType : endEventTypes) {

            if (historyEvent.isEventOfType(endEventType)) {

                return true;
            }
        }

        return false;
    }

    public void setCoverageTestRunState(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }
}
