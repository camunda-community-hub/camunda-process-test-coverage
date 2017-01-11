package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.event.EventHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.CompensationEventCoverageHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.FlowNodeHistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.PathCoverageParseListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Helper methods to configure the process coverage extensions on a given ProcessEngineConfigurationImpl
 *
 * @author z0rbas / lldata
 *
 */
public class ProcessCoverageConfigurator {

    public static void initializeProcessCoverageExtensions(ProcessEngineConfigurationImpl configuration) {
        initializeFlowNodeHandler(configuration);
        initializePathCoverageParseListener(configuration);
        initializeCompensationEventHandler(configuration);
    }

    private static void initializePathCoverageParseListener(ProcessEngineConfigurationImpl configuration) {
        List<BpmnParseListener> bpmnParseListeners = configuration.getCustomPostBPMNParseListeners();
        if (bpmnParseListeners == null) {
            bpmnParseListeners = new LinkedList<BpmnParseListener>();
            configuration.setCustomPostBPMNParseListeners(bpmnParseListeners);
        }

        bpmnParseListeners.add(new PathCoverageParseListener());
    }

    private static void initializeFlowNodeHandler(ProcessEngineConfigurationImpl configuration) {
        final FlowNodeHistoryEventHandler historyEventHandler = new FlowNodeHistoryEventHandler();
        configuration.setHistoryEventHandler(historyEventHandler);

    }

    private static void initializeCompensationEventHandler(ProcessEngineConfigurationImpl configuration) {
        if (configuration.getCustomEventHandlers() == null) {
            configuration.setCustomEventHandlers(new LinkedList<EventHandler>());
        }

        configuration.getCustomEventHandlers().add(new CompensationEventCoverageHandler());
    }

}
