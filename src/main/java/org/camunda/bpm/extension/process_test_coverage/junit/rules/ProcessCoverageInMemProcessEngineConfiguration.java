package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.LinkedList;
import java.util.List;

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.extension.process_test_coverage.listeners.FlowNodeHistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.PathCoverageParseListener;

/**
 * Standalone in memory process engine configuration additionaly configuring
 * flow node and sequence flow listeners for process coverage testing.
 * 
 * @author z0rbas
 *
 */
public class ProcessCoverageInMemProcessEngineConfiguration extends StandaloneInMemProcessEngineConfiguration {

    @Override
    protected void init() {

        initializeFlowNodeHandler();

        initializePathCoverageParseListener();

        super.init();
    }

    private void initializePathCoverageParseListener() {

        List<BpmnParseListener> bpmnParseListeners = getCustomPostBPMNParseListeners();
        if (bpmnParseListeners == null) {
            bpmnParseListeners = new LinkedList<BpmnParseListener>();
            setCustomPostBPMNParseListeners(bpmnParseListeners);
        }

        bpmnParseListeners.add(new PathCoverageParseListener());
    }

    private void initializeFlowNodeHandler() {

        final FlowNodeHistoryEventHandler historyEventHandler = new FlowNodeHistoryEventHandler();
        setHistoryEventHandler(historyEventHandler);

    }

}
