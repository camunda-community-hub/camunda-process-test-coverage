package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.CompensationEventCoverageHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.FlowNodeHistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.PathCoverageParseListener;
import org.camunda.bpm.extension.process_test_coverage.util.Api;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper methods to configure the process coverage extensions on a given ProcessEngineConfigurationImpl
 *
 * @author z0rbas / lldata
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
            bpmnParseListeners = new LinkedList<>();
            configuration.setCustomPostBPMNParseListeners(bpmnParseListeners);
        }

        bpmnParseListeners.add(new PathCoverageParseListener());
    }

    private static void initializeFlowNodeHandler(ProcessEngineConfigurationImpl configuration) {
        final FlowNodeHistoryEventHandler historyEventHandler = new FlowNodeHistoryEventHandler();

        if (Api.Camunda.supportsCustomHistoryEventHandlers()) {
            Api.Camunda.FEATURE_CUSTOM_HISTORY_EVENT_HANDLERS.invoke(configuration, Collections.singletonList((HistoryEventHandler) historyEventHandler));
            Api.Camunda.FEATURE_ENABLE_DEFAULT_DB_HISTORY_EVENT_HANDLERS.invoke(configuration, false);
        } else {
            // expected for Camunda < 7.13.0
            configuration.setHistoryEventHandler(historyEventHandler);
        }
    }

    private static void initializeCompensationEventHandler(ProcessEngineConfigurationImpl configuration) {
        if (configuration.getCustomEventHandlers() == null) {
            configuration.setCustomEventHandlers(new LinkedList<>());
        }

        configuration.getCustomEventHandlers().add(new CompensationEventCoverageHandler());
    }

}
