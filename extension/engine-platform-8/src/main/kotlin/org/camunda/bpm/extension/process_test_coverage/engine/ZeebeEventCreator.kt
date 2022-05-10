package org.camunda.bpm.extension.process_test_coverage.engine

import io.camunda.zeebe.process.test.assertions.BpmnAssert
import io.camunda.zeebe.process.test.filters.RecordStream
import io.camunda.zeebe.protocol.record.RecordType
import io.camunda.zeebe.protocol.record.intent.ProcessInstanceIntent
import io.camunda.zeebe.protocol.record.value.BpmnElementType
import org.camunda.bpm.extension.process_test_coverage.model.Collector
import org.camunda.bpm.extension.process_test_coverage.model.Event
import org.camunda.bpm.extension.process_test_coverage.model.EventSource
import org.camunda.bpm.extension.process_test_coverage.model.EventType

fun createEvents(collector: Collector, lastEventTime: Long) {
    BpmnAssert.getRecordStream().processInstanceRecords()
        .filter { it.value.bpmnElementType == BpmnElementType.SEQUENCE_FLOW && it.recordType == RecordType.EVENT }
        .filter { it.timestamp > lastEventTime }
        .forEach { collector.addEvent(Event(EventSource.SEQUENCE_FLOW, EventType.TAKE, it.value.elementId,
            it.value.bpmnElementType.elementTypeName.orElseGet { "" }, it.value.bpmnProcessId, it.timestamp)) }
    BpmnAssert.getRecordStream().processInstanceRecords()
        .filter { it.recordType == RecordType.EVENT && it.intent == ProcessInstanceIntent.ELEMENT_ACTIVATED
                && it.value.bpmnElementType != BpmnElementType.PROCESS }
        .filter { it.timestamp > lastEventTime }
        .forEach { collector.addEvent(Event(EventSource.FLOW_NODE, EventType.START, it.value.elementId,
            it.value.bpmnElementType.elementTypeName.orElseGet { "" }, it.value.bpmnProcessId, it.timestamp)) }
    BpmnAssert.getRecordStream().processInstanceRecords()
        .filter { it.recordType == RecordType.EVENT && it.intent == ProcessInstanceIntent.ELEMENT_COMPLETED
                && it.value.bpmnElementType != BpmnElementType.PROCESS }
        .filter { it.timestamp > lastEventTime }
        .forEach { collector.addEvent(Event(EventSource.FLOW_NODE, EventType.END, it.value.elementId,
            it.value.bpmnElementType.elementTypeName.orElseGet { "" }, it.value.bpmnProcessId, it.timestamp)) }
}
