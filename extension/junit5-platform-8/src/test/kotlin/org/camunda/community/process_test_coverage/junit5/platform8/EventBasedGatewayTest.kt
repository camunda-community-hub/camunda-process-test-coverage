package org.camunda.community.process_test_coverage.junit5.platform8

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.process.test.api.ZeebeTestEngine
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Duration

@ZeebeProcessTest
class EventBasedGatewayTest {

    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().assertClassCoverageAtLeast(1.0).build()
    }

    private lateinit var client: ZeebeClient
    private lateinit var engine: ZeebeTestEngine

    @Test
    fun testPathSignal() {
        CoverageTestProcessConstants.deploy(client, resourcePath = "eventBasedGateway.bpmn")
        client.newCreateInstanceCommand().bpmnProcessId("event_based_gateway").latestVersion().send().join()
        engine.waitForIdleState(Duration.ofSeconds(5))
        client.newBroadcastSignalCommand().signalName("Signal_1").send().join()
    }

    @Test
    fun testPathMessage() {
        CoverageTestProcessConstants.deploy(client, resourcePath = "eventBasedGateway.bpmn")
        client.newCreateInstanceCommand().bpmnProcessId("event_based_gateway").latestVersion().send().join()
        engine.waitForIdleState(Duration.ofSeconds(5))
        client.newPublishMessageCommand().messageName("Message_1").correlationKey("1").send().join()
    }

}