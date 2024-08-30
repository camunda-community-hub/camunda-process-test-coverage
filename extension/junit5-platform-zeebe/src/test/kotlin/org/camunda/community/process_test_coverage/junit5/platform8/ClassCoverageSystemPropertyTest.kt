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
import org.camunda.community.process_test_coverage.junit5.platform8.CoverageTestProcessConstants.deploy
import org.camunda.community.process_test_coverage.junit5.common.ProcessEngineCoverageExtensionBuilder.Companion.DEFAULT_ASSERT_AT_LEAST_PROPERTY
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
