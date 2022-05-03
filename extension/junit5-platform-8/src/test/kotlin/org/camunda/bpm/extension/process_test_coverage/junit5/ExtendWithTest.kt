package org.camunda.bpm.extension.process_test_coverage.junit5

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest
import org.camunda.bpm.extension.process_test_coverage.junit5.CoverageTestProcessConstants.deploy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ZeebeProcessTest
@ExtendWith(ProcessEngineCoverageExtension::class)
class ExtendWithTest {

    private lateinit var client: ZeebeClient

    @Test
    fun testPathA() {
        deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
    }

    @Test
    fun testPathB() {
        deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
    }

}