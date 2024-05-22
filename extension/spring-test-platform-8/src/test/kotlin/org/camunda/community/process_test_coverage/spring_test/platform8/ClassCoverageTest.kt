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
