package org.camunda.community.process_test_coverage.spring_test.platform8

import io.camunda.zeebe.spring.test.ZeebeSpringTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration


@Configuration
class NonCamundaApplication

@SpringBootTest(classes = [NonCamundaApplication::class])
class NonCamundaTest {

    @Test
    fun testSomethingNotCamunda() {
        val testString = "Testing"
        Assertions.assertThat(testString).isNotEmpty()
    }

}