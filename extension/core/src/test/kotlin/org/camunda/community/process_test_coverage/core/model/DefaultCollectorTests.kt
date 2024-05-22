/*-
 * #%L
 * Camunda Process Test Coverage Core
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
package org.camunda.community.process_test_coverage.core.model

import org.assertj.core.api.Assertions.assertThat
import org.camunda.community.process_test_coverage.core.engine.ModelProvider
import org.camunda.community.process_test_coverage.core.model.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*


/**
 * This tests make sure the default collector is enforcing its internal lifecycle.
 */
class DefaultCollectorTests {

    private val modelProvider: ModelProvider = mock()

    @Test
    fun `should create empty`() {

        val suiteId = UUID.randomUUID().toString()
        val runId = UUID.randomUUID().toString()


        val collector = DefaultCollector(modelProvider)

        assertThat(collector.getModels()).isEmpty()

        assertThat(collector.getSuites()).isEmpty()

        val noActiveSuite = assertThrows<IllegalArgumentException> {
            collector.activateRun(runId)
        }
        assertThat(noActiveSuite).hasMessage("No active suite available")

        val suiteNotFound = assertThrows<IllegalArgumentException> {
            collector.activateSuite(suiteId)
        }
        assertThat(suiteNotFound).hasMessage("Suite with id $suiteId doesn't exist")

        val suiteNotFound2 = assertThrows<IllegalArgumentException> {
            collector.createRun(Run(runId, "run1"), suiteId)
        }
        assertThat(suiteNotFound2).hasMessage("Suite with id $suiteId doesn't exist")
    }

    @Test
    fun `should create suite and run`() {

        val suiteId = UUID.randomUUID().toString()
        val otherSuiteId = UUID.randomUUID().toString()
        val runId = UUID.randomUUID().toString()
        val otherRunId = UUID.randomUUID().toString()

        val collector = DefaultCollector(modelProvider)

        collector.createSuite(Suite(suiteId, "suite1"))
        collector.createSuite(Suite(otherSuiteId, "suite2"))

        assertThat(assertThrows<IllegalArgumentException> {
            collector.createSuite(Suite(suiteId, "suite1"))
        }).hasMessage("Suite already exists")

        collector.createRun(Run(runId, "run1"), suiteId)
        collector.createRun(Run(otherRunId, "run1"), otherSuiteId)

        assertThat(assertThrows<IllegalArgumentException> {
            collector.activateRun(runId)
        }).hasMessage("No active suite available")

        collector.activateSuite(suiteId)

        assertThat(assertThrows<IllegalArgumentException> {
            collector.activateRun(otherRunId)
        }).hasMessage("Run $otherRunId doesn't exist in suite $suiteId")

        collector.activateRun(runId)

        whenever(modelProvider.getModel(any())).thenReturn(Model("proc.def", 12, "12", "<xml>"))

        collector.addEvent(Event(EventSource.FLOW_NODE, EventType.START, "start", "startEvent", "proc.def"))
        collector.addEvent(Event(EventSource.FLOW_NODE, EventType.START, "start", "startEvent", "proc.def"))

        verify(modelProvider).getModel("proc.def")
    }


}
