/*-
 * #%L
 * Camunda Process Test Coverage Engine Platform 7
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.camunda.community.process_test_coverage.engine.platform7

import io.github.oshai.kotlinlogging.KotlinLogging
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.community.process_test_coverage.core.model.Collector

private val logger = KotlinLogging.logger {}

class ProcessEngineAdapter(
    private val processEngine: ProcessEngine,
    private val coverageCollector: Collector
) {

    /**
     * Sets the test run state for the coverage listeners. logging.
     * {@see ProcessCoverageInMemProcessEngineConfiguration}
     */
    fun initializeListeners() {
        val processEngineConfiguration = processEngine.processEngineConfiguration as ProcessEngineConfigurationImpl

        val bpmnParseListeners = processEngineConfiguration.customPostBPMNParseListeners

        for (parseListener: BpmnParseListener? in bpmnParseListeners) {
            if (parseListener is ElementCoverageParseListener) {
                parseListener.setCoverageState(this.coverageCollector)
            }
        }

        // Compensation event handler

        // Compensation event handler
        val compensationEventHandler = processEngineConfiguration.getEventHandler("compensate")
        if (compensationEventHandler is CompensationEventCoverageHandler) {
            compensationEventHandler.setCoverageState(this.coverageCollector)
        } else {
            logger.warn(
                "CompensationEventCoverageHandler not registered with process engine configuration!"
                        + " Compensation boundary events coverage will not be registered."
            )
        }
    }

}
