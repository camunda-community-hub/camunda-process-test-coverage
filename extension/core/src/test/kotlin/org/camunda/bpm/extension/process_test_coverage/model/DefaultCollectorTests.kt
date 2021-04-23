package org.camunda.bpm.extension.process_test_coverage.model

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.camunda.bpm.extension.process_test_coverage.engine.ExecutionContextModelProvider
import org.camunda.bpm.extension.process_test_coverage.engine.ModelProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
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