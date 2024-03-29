package org.camunda.community.process_test_coverage.spring_test.platform8

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.spring.client.EnableZeebeClient
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment
import io.camunda.zeebe.spring.test.ZeebeSpringTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import


@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = ["classpath*:*.bpmn"])
class Application

@SpringBootTest
@ZeebeSpringTest
@Import(ProcessEngineCoverageConfiguration::class)
class ClassCoverageTest {

    @Autowired
    private lateinit var zeebe: ZeebeClient

    @Test
    fun testPathA() {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        zeebe.newCreateInstanceCommand() //
            .bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion() //
            .variables(variables) //
            .send().join()
    }

    @Test
    fun testPathB() {
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        zeebe.newCreateInstanceCommand() //
            .bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion() //
            .variables(variables) //
            .send().join()
    }

}