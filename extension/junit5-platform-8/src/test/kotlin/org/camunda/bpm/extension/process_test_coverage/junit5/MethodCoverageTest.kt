package org.camunda.bpm.extension.process_test_coverage.junit5

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.process.test.api.ZeebeTestEngine
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest
import org.assertj.core.api.HamcrestCondition
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Duration

@ZeebeProcessTest
class MethodCoverageTest {

    companion object {
        const val PROCESS_DEFINITION_KEY = "process-test-coverage"
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().withDetailedCoverageLogging().build()

    }

    private lateinit var client: ZeebeClient
    private lateinit var engine: ZeebeTestEngine

    @Test
    fun testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered() {
        CoverageTestProcessConstants.deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        client.newCreateInstanceCommand().bpmnProcessId(PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.greaterThan(6.9 / 11.0))
        )
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.lessThan(7.1 / 11.0))
        )
        engine.waitForIdleState(Duration.ofSeconds(5))
    }

    @Test
    fun testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered() {
        CoverageTestProcessConstants.deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        client.newCreateInstanceCommand().bpmnProcessId(PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.greaterThan(6.9 / 11.0))
        )
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.lessThan(7.1 / 11.0))
        )
        engine.waitForIdleState(Duration.ofSeconds(5))
    }


}