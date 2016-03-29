package org.camunda.bpm.extension.process_test_coverage.trace;

import java.util.List;
import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.DbHistoryEventHandler;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;

/**
 * Extends the {@link DbHistoryEventHandler} in order to notify the process test
 * coverage of a covered activity.
 */
public class TraceActivitiesHistoryEventHandler extends DbHistoryEventHandler implements HistoryEventHandler {

    Logger log = Logger.getLogger(TraceActivitiesHistoryEventHandler.class.getCanonicalName());

    CoverageTestRunState testCoverageTestRunState;

    @Override
    public void handleEvent(HistoryEvent historyEvent) {
        super.handleEvent(historyEvent);
        
        if (historyEvent instanceof HistoricActivityInstanceEventEntity) {
            
            // not interested in end-events of scopes
            HistoricActivityInstanceEventEntity activityEvent = (HistoricActivityInstanceEventEntity) historyEvent;
            if (activityEvent.getStartTime() != null) {
                
                CoveredElement activity = CoveredElementBuilder.createTrace(
                        activityEvent.getProcessDefinitionKey()).withActivityId(activityEvent.getActivityId()).build();
                testCoverageTestRunState.addCoveredElement(activity);
                
            } else {
                // Skipping end event
            }
        } else {
            // Skipping unknown event
        }
    }

    @Override
    public void handleEvents(List<HistoryEvent> historyEvents) {
        for (HistoryEvent historyEvent : historyEvents) {
            handleEvent(historyEvent);
        }
    }
    
    public void setTestCoverageRunState(CoverageTestRunState testCoverageTestRunState){
        this.testCoverageTestRunState = testCoverageTestRunState;
    }

}
