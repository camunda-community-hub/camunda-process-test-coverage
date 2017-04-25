package org.camunda.bpm.extension.process_test_coverage.listeners;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.engine.impl.history.handler.DbHistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredFlowNode;
import org.camunda.bpm.extension.process_test_coverage.util.Api;

import java.util.EnumSet;
import java.util.logging.Logger;

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

            if (activityEvent.getActivityType().equals("multiInstanceBody"))
                return;

            final CoveredFlowNode coveredActivity =
                new CoveredFlowNode(historyEvent.getProcessDefinitionKey(), activityEvent.getActivityId());

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

    protected boolean isInitialEvent(HistoryEvent historyEvent) {
        String isInitialEvent = "isInitialEvent";
        return Api.feature(DbHistoryEventHandler.class, isInitialEvent, HistoryEvent.class).isSupported() ? super.isInitialEvent(historyEvent):
            (Boolean) Api.feature(DbHistoryEventHandler.class, isInitialEvent, String.class).invoke(this, historyEvent.getEventType());
    }

    /**
     * Aimed to be the opposite of
     * {@link DbHistoryEventHandler#isInitialEvent(HistoryEvent event)}
     * for the purpose of the process test coverage - which just deals with
     * history events of type HistoricActivityInstanceEventEntity.
     * 
     * Future versions of Camunda will eventually introduce additional events
     * requiring this method to be updated. Be careful to deal with backwards
     * compatibility issues when doing that.
     * 
     * @param historyEvent
     * @return
     */
    private boolean isEndEvent(HistoryEvent historyEvent) {

        EnumSet<HistoryEventTypes> endEventTypes = EnumSet.of(
            HistoryEventTypes.ACTIVITY_INSTANCE_END,
            HistoryEventTypes.PROCESS_INSTANCE_END,
            HistoryEventTypes.TASK_INSTANCE_COMPLETE
        );

        // They should have handled compare/equals in the enum itself
        for (HistoryEventTypes endEventType : endEventTypes) {

            if (historyEvent.getEventType().equals(endEventType.getEventName())) {

                return true;
            }
        }

        return false;
    }

    public void setCoverageTestRunState(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }

}
