package org.camunda.community.process_test_coverage.spring_starter.platform7

import org.camunda.bpm.engine.RuntimeService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootApplication
class Application

@SpringBootTest
@Import(CoverageTestConfiguration::class)
class ClassCoverageProcessTest {

    @Autowired
    private lateinit var runtimeService: RuntimeService

    @Test
    fun testPathA() {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        runtimeService.startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables)
    }

    @Test
    fun testPathB() {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        runtimeService.startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables)
    }

}