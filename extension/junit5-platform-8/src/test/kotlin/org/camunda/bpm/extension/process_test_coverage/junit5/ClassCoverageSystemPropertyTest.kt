package org.camunda.bpm.extension.process_test_coverage.junit5

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.process.test.api.ZeebeTestEngine
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest
import org.assertj.core.api.HamcrestCondition
import org.camunda.bpm.extension.process_test_coverage.junit5.CoverageTestProcessConstants.deploy
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension.Builder.Companion.DEFAULT_ASSERT_AT_LEAST_PROPERTY
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Duration


@ZeebeProcessTest
class ClassCoverageSystemPropertyTest {

    companion object {

        val EXPECTED: Double = CoverageTestProcessConstants.PATH_B_ELEMENTS.size.toDouble()
        val ALL: Double = CoverageTestProcessConstants.ALL_ELEMENTS.size.toDouble()
        val EXPECTED_COVERAGE = EXPECTED / ALL

        init {
            System.setProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY, "$EXPECTED_COVERAGE")
        }

        @AfterAll
        @JvmStatic
        fun delSysProperty() {
            System.clearProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY)
        }

        @RegisterExtension
        @JvmField
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder()
                .optionalAssertCoverageAtLeastProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY)
                .build()
    }

    private lateinit var client: ZeebeClient
    private lateinit var engine: ZeebeTestEngine

    @Test
    fun testPathB() {
        deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        extension.addTestMethodCoverageCondition("testPathB", HamcrestCondition(Matchers.lessThan(EXPECTED_COVERAGE + 0.0001)))
        engine.waitForIdleState(Duration.ofSeconds(5))
    }


}