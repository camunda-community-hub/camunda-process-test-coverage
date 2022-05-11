package org.camunda.community.process_test_coverage.engine.platform8

import io.camunda.zeebe.process.test.assertions.BpmnAssert
import io.camunda.zeebe.protocol.record.RecordType
import io.camunda.zeebe.protocol.record.intent.ProcessInstanceIntent
import io.camunda.zeebe.protocol.record.value.BpmnElementType
import io.camunda.zeebe.protocol.record.value.ProcessInstanceRecordValue
import org.camunda.community.process_test_coverage.core.model.Collector
import org.camunda.community.process_test_coverage.core.model.Event
import org.camunda.community.process_test_coverage.core.model.EventSource
import org.camunda.community.process_test_coverage.core.model.EventType

/**
 * Creates events for the collector from the record stream of the zeebe process engine.
 */
fun createEvents(collector: Collector, lastEventTime: Long) {
    BpmnAssert.getRecordStream().processInstanceRecords()
        .filter { it.recordType == RecordType.EVENT }
        .filter { it.timestamp > lastEventTime }
        .filter { it.value.bpmnElementType != BpmnElementType.PROCESS }
        .mapNotNull { mapEvent(it) }
        .forEach { collector.addEvent(it) }
}

/**
 * Maps a record in the stream to an event or null, if the record is not of importance for the coverage.
 */
fun mapEvent(record: io.camunda.zeebe.protocol.record.Record<ProcessInstanceRecordValue>): Event? {
    val eventSource =
        if (record.value.bpmnElementType == BpmnElementType.SEQUENCE_FLOW) EventSource.SEQUENCE_FLOW
        else EventSource.FLOW_NODE
    val eventType =
        if (record.value.bpmnElementType == BpmnElementType.SEQUENCE_FLOW) EventType.TAKE
        else if (record.intent == ProcessInstanceIntent.ELEMENT_ACTIVATED) EventType.START
        else if (record.intent == ProcessInstanceIntent.ELEMENT_COMPLETED) EventType.END
        else null
    return eventType?.let { Event(eventSource, it, record.value.elementId,
        record.value.bpmnElementType.elementTypeName.orElseGet { "" }, record.value.bpmnProcessId, record.timestamp) }
}