package org.camunda.bpm.extension.process_test_coverage.junit5

import org.camunda.bpm.engine.test.Deployment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class NestedInnerClassCoverageTest {

    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().assertClassCoverageAtLeast(1.0).build()
    }

    @Nested
    @Deployment(resources = [CoverageTestProcessConstants.BPMN_PATH])
    inner class PathA {
        @Test
        fun testPathA() {
            val variables: MutableMap<String, Any> = HashMap()
            variables["path"] = "A"
            extension.processEngine.runtimeService.startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables)
        }
    }

    @Nested
    @Deployment(resources = [CoverageTestProcessConstants.BPMN_PATH])
    inner class PathB {
        @Test
        fun testPathB() {
            val variables: MutableMap<String, Any> = HashMap()
            variables["path"] = "B"
            extension.processEngine.runtimeService.startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables)
        }
    }

}
