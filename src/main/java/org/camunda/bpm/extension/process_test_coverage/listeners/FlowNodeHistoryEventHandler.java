package org.camunda.bpm.extension.process_test_coverage.listeners;

import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.DbHistoryEventHandler;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredActivity;

/**
 * Extends the {@link DbHistoryEventHandler} in order to notify the process test
 * coverage of a covered activity.
 */
public class FlowNodeHistoryEventHandler extends DbHistoryEventHandler implements HistoryEventHandler {

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

        // TODO collect jobs for highlighting (e.g. boundary timer event)

        // We are only interested in activity start events
        if (historyEvent instanceof HistoricActivityInstanceEventEntity && isInitialEvent(historyEvent)) {

            HistoricActivityInstanceEventEntity activityEvent = (HistoricActivityInstanceEventEntity) historyEvent;

            final CoveredActivity coveredActivity = new CoveredActivity(historyEvent.getProcessDefinitionKey(),
                    activityEvent.getActivityId());

            coverageTestRunState.addCoveredElement(coveredActivity);
        }

    }

    public void setCoverageTestRunState(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }
}
