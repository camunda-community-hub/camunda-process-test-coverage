package org.camunda.bpm.extension.process_test_coverage.junit5

import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.engine.test.Deployment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ProcessEngineCoverageExtension::class)
class ExtendWithTest {

    @Test
    @Deployment(resources = [CoverageTestProcessConstants.BPMN_PATH])
    fun testPathA(processEngine: ProcessEngine) {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        processEngine.runtimeService.startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables)
    }

    @Test
    @Deployment(resources = [CoverageTestProcessConstants.BPMN_PATH])
    fun testPathB(processEngine: ProcessEngine) {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        processEngine.runtimeService.startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables)
    }

}