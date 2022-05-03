package org.camunda.bpm.extension.process_test_coverage.junit5

import io.camunda.zeebe.client.ZeebeClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class CoverageRatioTest {

    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().assertClassCoverageAtLeast(1.0).build()
    }

    private lateinit var client: ZeebeClient

    fun completeTask() {
    }

    @Test
    fun testCoverageRatio_Ok() {
        CoverageTestProcessConstants.deploy(client, "coverage-ratio-test.bpmn")
        val variables: MutableMap<String, Any> = HashMap()
        variables["ok"] = true
        client.newCreateInstanceCommand().bpmnProcessId("testCoverageRatio").latestVersion().variables(variables).send().join()
        completeTask()
    }

    @Test
    fun testCoverageRatio_NotOk() {
        CoverageTestProcessConstants.deploy(client, "coverage-ratio-test.bpmn")
        val variables: MutableMap<String, Any> = HashMap()
        variables["ok"] = false
        client.newCreateInstanceCommand().bpmnProcessId("testCoverageRatio").latestVersion().variables(variables).send().join()
        completeTask()
    }

}