package org.camunda.community.process_test_coverage.junit5.platform7

import org.assertj.core.api.HamcrestCondition
import org.camunda.bpm.engine.test.Deployment
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class MultipleDeploymentsForIndividualTestsTest {

    companion object {

        private const val PROCESS_DEFINITION_KEY = "super-process-test-coverage"

        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().build()
    }

    @Test
    @Deployment(resources = ["superProcess.bpmn", "process.bpmn"])
    fun testPathAAndSuperPathA() {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        variables["superPath"] = "A"
        extension.processEngine.runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables)
        extension.addTestMethodCoverageCondition("testPathAAndSuperPathA", HamcrestCondition(Matchers.greaterThan(6.9 / 11.0)))
        extension.addTestMethodCoverageCondition("testPathAAndSuperPathA", HamcrestCondition(Matchers.lessThan(9 / 11.0)))
    }

    @Test
    @Deployment(resources = ["superProcess.bpmn", "process.bpmn"])
    fun testPathBAndSuperPathB() {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        variables["superPath"] = "B"
        extension.processEngine.runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables)
        extension.addTestMethodCoverageCondition("testPathBAndSuperPathB", HamcrestCondition(Matchers.greaterThan(6.9 / 11.0)))
        extension.addTestMethodCoverageCondition("testPathBAndSuperPathB", HamcrestCondition(Matchers.lessThan(9 / 11.0)))
    }

}