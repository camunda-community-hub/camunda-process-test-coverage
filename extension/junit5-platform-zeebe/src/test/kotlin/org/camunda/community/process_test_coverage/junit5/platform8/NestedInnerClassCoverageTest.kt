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

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.process.test.api.ZeebeTestEngine
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Duration

@ZeebeProcessTest
class NestedInnerClassCoverageTest {

    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().assertClassCoverageAtLeast(1.0).build()
    }

    @Nested
    inner class PathA {

        private lateinit var client: ZeebeClient
        private lateinit var engine: ZeebeTestEngine
        @Test
        fun testPathA() {
            CoverageTestProcessConstants.deploy(client)
            val variables: MutableMap<String, Any> = HashMap()
            variables["path"] = "A"
            client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
            engine.waitForIdleState(Duration.ofSeconds(5))
        }
    }

    @Nested
    inner class PathB {

        private lateinit var client: ZeebeClient
        private lateinit var engine: ZeebeTestEngine
        @Test
        fun testPathB() {
            CoverageTestProcessConstants.deploy(client)
            val variables: MutableMap<String, Any> = HashMap()
            variables["path"] = "B"
            client.newCreateInstanceCommand().bpmnProcessId(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
            engine.waitForIdleState(Duration.ofSeconds(5))
        }
    }

}
