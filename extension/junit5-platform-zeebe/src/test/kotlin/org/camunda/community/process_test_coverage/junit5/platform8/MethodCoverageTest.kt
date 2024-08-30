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
import org.assertj.core.api.HamcrestCondition
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Duration

@ZeebeProcessTest
class MethodCoverageTest {

    companion object {
        const val PROCESS_DEFINITION_KEY = "process-test-coverage"
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().withDetailedCoverageLogging().build()

    }

    private lateinit var client: ZeebeClient
    private lateinit var engine: ZeebeTestEngine

    @Test
    fun testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered() {
        CoverageTestProcessConstants.deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "A"
        client.newCreateInstanceCommand().bpmnProcessId(PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.greaterThan(6.9 / 11.0))
        )
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.lessThan(7.1 / 11.0))
        )
        engine.waitForIdleState(Duration.ofSeconds(5))
    }

    @Test
    fun testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered() {
        CoverageTestProcessConstants.deploy(client)
        val variables: MutableMap<String, Any> = HashMap()
        variables["path"] = "B"
        client.newCreateInstanceCommand().bpmnProcessId(PROCESS_DEFINITION_KEY).latestVersion().variables(variables).send().join()
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.greaterThan(6.9 / 11.0))
        )
        extension.addTestMethodCoverageCondition(
                "testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered",
                HamcrestCondition(Matchers.lessThan(7.1 / 11.0))
        )
        engine.waitForIdleState(Duration.ofSeconds(5))
    }


}
