package org.camunda.community.process_test_coverage.junit5.platform8

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.process.test.api.ZeebeTestEngine
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Duration

@ZeebeProcessTest
class ClassCoverageTest {

    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().assertClassCoverageAtLeast(1.0).build()
    }

    private lateinit var client: ZeebeClient
    private lateinit var engine: ZeebeTestEngine

    @Test
    fun testPathA() {
        CoverageTestProcessConstants.deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        engine.waitForIdleState(Duration.ofSeconds(5))
    }

    @Test
    fun testPathB() {
        CoverageTestProcessConstants.deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        engine.waitForIdleState(Duration.ofSeconds(5))
    }

}