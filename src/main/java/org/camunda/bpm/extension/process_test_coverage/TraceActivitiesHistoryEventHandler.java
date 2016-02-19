package org.camunda.bpm.extension.process_test_coverage;

import java.util.List;
import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.DbHistoryEventHandler;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElementBuilder;

/**
 * Extends the {@link DbHistoryEventHandler} in order to notify the process test
 * coverage of a covered activity.
 */
public class TraceActivitiesHistoryEventHandler extends DbHistoryEventHandler implements HistoryEventHandler {

    Logger log = Logger.getLogger(TraceActivitiesHistoryEventHandler.class.getCanonicalName());

    // XXX inject?
    TestCoverageTestRunState testCoverageTestRunState = TestCoverageTestRunState.INSTANCE();

    @Override
    public void handleEvent(HistoryEvent historyEvent) {
        super.handleEvent(historyEvent);
        if (historyEvent instanceof HistoricActivityInstanceEventEntity) {
            // not interested in end-events of scopes
            HistoricActivityInstanceEventEntity activityEvent = (HistoricActivityInstanceEventEntity) historyEvent;
            if (activityEvent.getStartTime() != null) {
                CoveredElement activity = CoveredElementBuilder.createTrace(
                        activityEvent.getProcessDefinitionId()).withActivityId(activityEvent.getActivityId()).build();
                log.info("Logging start event " + historyEvent); // XXX debug
                testCoverageTestRunState.notifyCoveredElement(activity);
            } else {
                log.info("Skipping endd event " + historyEvent); // XXX debug
            }
        } else {
            log.info("Skipping ????? event " + historyEvent); // XXX debug
        }
    }

    @Override
    public void handleEvents(List<HistoryEvent> historyEvents) {
        for (HistoryEvent historyEvent : historyEvents) {
            handleEvent(historyEvent);
        }
    }

}
