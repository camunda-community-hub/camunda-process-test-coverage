/*
 * Copyright 2020 FlowSquad GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.camunda.bpm.extension.process_test_coverage.engine;

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * Helper methods to configure the process coverage extensions on a given ProcessEngineConfigurationImpl
 */
public class ProcessCoverageConfigurator {

    public static void initializeProcessCoverageExtensions(final ProcessEngineConfigurationImpl configuration) {
        initializeElementCoverageParseListener(configuration);
        initializeCompensationEventHandler(configuration);
    }

    private static void initializeElementCoverageParseListener(final ProcessEngineConfigurationImpl configuration) {
        List<BpmnParseListener> bpmnParseListeners = configuration.getCustomPostBPMNParseListeners();
        if (bpmnParseListeners == null) {
            bpmnParseListeners = new LinkedList<>();
            configuration.setCustomPostBPMNParseListeners(bpmnParseListeners);
        }

        bpmnParseListeners.add(new ElementCoverageParseListener());
    }

    private static void initializeCompensationEventHandler(final ProcessEngineConfigurationImpl configuration) {
        if (configuration.getCustomEventHandlers() == null) {
            configuration.setCustomEventHandlers(new LinkedList<>());
        }

        configuration.getCustomEventHandlers().add(new CompensationEventCoverageHandler());
    }


}
