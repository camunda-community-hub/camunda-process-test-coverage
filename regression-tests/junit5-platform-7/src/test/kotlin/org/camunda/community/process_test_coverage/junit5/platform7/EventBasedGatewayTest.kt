package org.camunda.community.process_test_coverage.junit5.platform7

import org.camunda.bpm.engine.test.Deployment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class EventBasedGatewayTest {

    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().assertClassCoverageAtLeast(1.0).build()
    }

    @Test
    @Deployment(resources = [CoverageTestProcessConstants.BPMN_PATH_EVENT_BASED_GATEWAY])
    fun testPathMessageCorrelation() {
        extension.processEngine.runtimeService.startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY_EVENT_BASED_GATEWAY)
        extension.processEngine.runtimeService.correlateMessage("Message_1")
    }

    @Test
    @Deployment(resources = [CoverageTestProcessConstants.BPMN_PATH_EVENT_BASED_GATEWAY])
    fun testPathSignal() {
        extension.processEngine.runtimeService.startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY_EVENT_BASED_GATEWAY)
        extension.processEngine.runtimeService.signalEventReceived("Signal_1")
    }

}