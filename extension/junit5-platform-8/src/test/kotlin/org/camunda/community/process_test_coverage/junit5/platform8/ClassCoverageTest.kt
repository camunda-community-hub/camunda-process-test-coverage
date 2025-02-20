/*-
 * #%L
 * Camunda Process Test Coverage JUnit5 Platform 8
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
package org.camunda.community.process_test_coverage.junit5.platform8

import io.camunda.client.CamundaClient
import io.camunda.process.test.api.CamundaAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ClassCoverageTest {

    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder()
            .assertClassCoverageAtLeast(1.0)
            .build()
    }

    private lateinit var client: CamundaClient

    @Test
    fun testPathA() {
        CoverageTestProcessConstants.deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        val processInstanceEvent = client.newCreateInstanceCommand()
            .bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY)
            .latestVersion()
            .variables(variables)
            .send().join()
        CamundaAssert.assertThat(processInstanceEvent).isCompleted
    }

    @Test
    fun testPathB() {
        CoverageTestProcessConstants.deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        val processInstanceEvent = client.newCreateInstanceCommand()
            .bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY)
            .latestVersion()
            .variables(variables)
            .send().join()
        CamundaAssert.assertThat(processInstanceEvent).isCompleted
    }

}
