package org.camunda.community.process_test_coverage.junit5.platform8

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.process.test.api.ZeebeTestEngine
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest
import org.camunda.community.process_test_coverage.junit5.platform8.CoverageTestProcessConstants.deploy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ZeebeProcessTest
@ExtendWith(ProcessEngineCoverageExtension::class)
class ExtendWithTest {

    private lateinit var client: ZeebeClient
    private lateinit var engine: ZeebeTestEngine

    @Test
    fun testPathA() {
        deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        engine.waitForIdleState(Duration.ofSeconds(5))
    }

    @Test
    fun testPathB() {
        deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        engine.waitForIdleState(Duration.ofSeconds(5))
    }

}