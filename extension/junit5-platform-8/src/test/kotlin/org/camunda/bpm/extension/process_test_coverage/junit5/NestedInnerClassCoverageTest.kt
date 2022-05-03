package org.camunda.bpm.extension.process_test_coverage.junit5

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@ZeebeProcessTest
class NestedInnerClassCoverageTest {

    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().assertClassCoverageAtLeast(1.0).build()
    }

    private lateinit var client: ZeebeClient

    @Nested
    inner class PathA {
        @Test
        fun testPathA() {
            CoverageTestProcessConstants.deploy(client)
            val variables: MutableMap<String, Any> = HashMap()
            variables["path"] = "A"
            client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        }
    }

    @Nested
    inner class PathB {
        @Test
        fun testPathB() {
            CoverageTestProcessConstants.deploy(client)
            val variables: MutableMap<String, Any> = HashMap()
            variables["path"] = "B"
            client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        }
    }

}
