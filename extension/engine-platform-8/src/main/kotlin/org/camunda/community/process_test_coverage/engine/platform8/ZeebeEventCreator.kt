package org.camunda.community.process_test_coverage.engine.platform8

import io.camunda.zeebe.model.bpmn.Bpmn
import io.camunda.zeebe.model.bpmn.instance.SequenceFlow
import io.camunda.zeebe.process.test.assertions.BpmnAssert
import io.camunda.zeebe.protocol.record.RecordType
import io.camunda.zeebe.protocol.record.intent.ProcessInstanceIntent
import io.camunda.zeebe.protocol.record.value.BpmnElementType
import io.camunda.zeebe.protocol.record.value.ProcessInstanceRecordValue
import org.camunda.community.process_test_coverage.core.model.Collector
import org.camunda.community.process_test_coverage.core.model.Event
import org.camunda.community.process_test_coverage.core.model.EventSource
import org.camunda.community.process_test_coverage.core.model.EventType
import java.io.ByteArrayInputStream

/**
 * Creates events for the collector from the record stream of the zeebe process engine.
 * @param collector collector in use for events
 * @param eventCutoffTimestamp timestamp for the cut-off of events in the record stream. Only events happened afterwards are relevant.
 */
fun createEvents(collector: Collector, eventCutoffTimestamp: Long) {
    val events = BpmnAssert.getRecordStream().processInstanceRecords()
        .asSequence()
        .filter { it.recordType == RecordType.EVENT }
        .filter { it.timestamp > eventCutoffTimestamp }
        .filter { it.value.bpmnElementType != BpmnElementType.PROCESS }
        .mapNotNull { mapEvent(it) }
        .toMutableList()

    val eventsToInsert = mutableSetOf<Pair<Event, Event>>()

    // for event based gateways we need to find out how the flow continues to get the correct sequence flow
    // and add that as an event, as sequence flows after an event based gateway are not reflected in the records
    events.withIndex().forEach {
        if (it.value.elementType == BpmnElementType.EVENT_BASED_GATEWAY.elementTypeName.get()
            && it.value.type == EventType.END
        ) {
            // if event based gateway is found look at the remaining sublist to find the next activity
            val model = ZeebeModelProvider().getModel(it.value.modelKey)
            val modelInstance = Bpmn.readModelFromStream(ByteArrayInputStream(model.xml.toByteArray()))
            // find all sequence flows leaving the event based gateway
            val outgoingFlows = modelInstance.getModelElementsByType(SequenceFlow::class.java)
                .filter { flow -> flow.source.id == it.value.definitionKey }

            events.subList(it.index, events.size)
                // find an event start is the start after the event based gateway
                .find { event ->
                    event.type == EventType.START
                            && outgoingFlows.any { flow -> flow.target.id == event.definitionKey }
                }
                ?.let { event ->
                    // if found create an event for the sequence flow
                    outgoingFlows.find { flow -> flow.target.id == event.definitionKey }?.let { flow ->
                        // add the sequence flow as an event to be added to the list of events
                        eventsToInsert.add(
                            event to Event(
                                EventSource.SEQUENCE_FLOW,
                                EventType.TAKE,
                                flow.id,
                                BpmnElementType.SEQUENCE_FLOW.elementTypeName.orElse(""),
                                event.modelKey,
                                event.timestamp
                            )
                        )
                    }
                }
        }
    }

    // add the events at the correct position
    eventsToInsert.forEach {
        events.add(events.indexOf(it.first), it.second)
    }

    events.forEach { collector.addEvent(it) }
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