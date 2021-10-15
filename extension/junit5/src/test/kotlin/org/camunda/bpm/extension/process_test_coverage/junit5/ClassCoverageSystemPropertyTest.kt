package org.camunda.bpm.extension.process_test_coverage.junit5

import org.assertj.core.api.HamcrestCondition
import org.camunda.bpm.engine.test.Deployment
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ClassCoverageSystemPropertyTest {

    companion object {

        val EXPECTED: Double = CoverageTestProcessConstants.PATH_B_ELEMENTS.size.toDouble()
        val ALL: Double = CoverageTestProcessConstants.ALL_ELEMENTS.size.toDouble()
        val EXPECTED_COVERAGE = EXPECTED / ALL

        init {
            System.setProperty(ProcessEngineCoverageExtension.DEFAULT_ASSERT_AT_LEAST_PROPERTY,
                    "" + EXPECTED_COVERAGE)
        }

        @AfterAll
        @JvmStatic
        fun delSysProperty() {
            System.clearProperty(ProcessEngineCoverageExtension.DEFAULT_ASSERT_AT_LEAST_PROPERTY)
        }

        @RegisterExtension
        @JvmField
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder()
                .optionalAssertCoverageAtLeastProperty(ProcessEngineCoverageExtension.DEFAULT_ASSERT_AT_LEAST_PROPERTY)
                .build()
    }

    @Test
    @Deployment(resources = [CoverageTestProcessConstants.BPMN_PATH])
    fun testPathB() {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        extension.processEngine.runtimeService.startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables)
        extension.addTestMethodCoverageCondition("testPathB", HamcrestCondition(Matchers.lessThan(EXPECTED_COVERAGE + 0.0001)))
    }


}