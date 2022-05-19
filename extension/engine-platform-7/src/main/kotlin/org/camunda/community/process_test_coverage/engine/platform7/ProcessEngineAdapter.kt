package org.camunda.community.process_test_coverage.engine.platform7

import mu.KLogging
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.community.process_test_coverage.core.model.Collector

class ProcessEngineAdapter(
    private val processEngine: ProcessEngine,
    private val coverageCollector: Collector
) {

    companion object : KLogging()

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