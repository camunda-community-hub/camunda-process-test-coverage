/*-
 * #%L
 * Camunda Process Test Coverage Spring-Testing Platform 8
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
