package org.camunda.bpm.extension.process_test_coverage.junit5

import org.assertj.core.api.HamcrestCondition
import org.camunda.bpm.engine.test.Deployment
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class MethodCoverageTest {

    companion object {
        const val PROCESS_DEFINITION_KEY = "process-test-coverage"
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().withDetailedCoverageLogging().build()

    }

    @Test
    @Deployment(resources = ["process.bpmn"])
    fun testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered() {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        extension.processEngine.runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables)
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.greaterThan(6.9 / 11.0))
        )
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.lessThan(7.1 / 11.0))
        )
    }

    @Test
    @Deployment(resources = ["process.bpmn"])
    fun testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered() {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        extension.processEngine.runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables)
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.greaterThan(6.9 / 11.0))
        )
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.lessThan(7.1 / 11.0))
        )
    }


}