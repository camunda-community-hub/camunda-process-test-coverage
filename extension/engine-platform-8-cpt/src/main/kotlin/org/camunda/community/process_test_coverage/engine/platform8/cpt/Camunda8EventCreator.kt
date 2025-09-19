/*-
 * #%L
 * Camunda Process Test Coverage Engine Platform 8
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
package org.camunda.community.process_test_coverage.engine.platform8.cpt

import io.camunda.client.CamundaClient
import io.camunda.client.api.search.enums.ElementInstanceState
import io.camunda.client.api.search.enums.ElementInstanceType
import io.camunda.client.api.search.response.ElementInstance
import io.camunda.client.api.search.response.ProcessInstance
import io.camunda.client.api.search.response.ProcessInstanceSequenceFlow
import io.camunda.process.test.api.CamundaProcessTestContext
import io.camunda.zeebe.model.bpmn.Bpmn
import io.camunda.zeebe.model.bpmn.instance.SequenceFlow
import org.camunda.community.process_test_coverage.core.model.Collector
import org.camunda.community.process_test_coverage.core.model.Event
import org.camunda.community.process_test_coverage.core.model.EventSource
import org.camunda.community.process_test_coverage.core.model.EventType
import java.io.ByteArrayInputStream
import java.time.OffsetDateTime

const val DEFAULT_PAGE_REQUEST = 100

/**
 * Creates events for all process instances in the current Camunda 8 engine.
 * @param camundaProcessTestContext camunda process test context
 * @param collector collector in use for events
 */
fun createEvents(camundaProcessTestContext: CamundaProcessTestContext, collector: Collector) {

    val client = camundaProcessTestContext.createClient()

    val events = client.findProcessInstances()
        .asSequence()
        .flatMap {
            val flowNodes = client.findElementInstancesByProcessInstanceKey(it.processInstanceKey)
            val events = flowNodes
                .filter { node -> node.type != ElementInstanceType.PROCESS }
                .map {
                    node -> Pair(node, it.processDefinitionId)
                }
                .flatMap { mapEvent(it.first, it.second) }

            val sequenceFlowEvents = client.findSequenceFlowsByProcessInstanceKey(it.processInstanceKey)
                .map { sequenceFlow -> mapEvent(sequenceFlow, it.processDefinitionId ) }

            events.plus(sequenceFlowEvents)
        }
        .toMutableList()

    val eventsToInsert = mutableSetOf<Pair<Event, Event>>()

    // for event based gateways we need to find out how the flow continues to get the correct sequence flow
    // and add that as an event, as sequence flows after an event based gateway are not reflected in the records
    events.withIndex().forEach {
        if (it.value.elementType == "EVENT_BASED_GATEWAY" && it.value.type == EventType.END
        ) {
            // if event based gateway is found look at the remaining sublist to find the next activity
            val model = Camunda8ModelProvider { camundaProcessTestContext }.getModel(it.value.modelKey)
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
                                "sequenceFlow",
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
 * Maps a flow node instance to an event or null, if the record is not of importance for the coverage.
 */
fun mapEvent(flowNodeInstance: ElementInstance, bpmnProcessId: String): List<Event> {
    val eventSource =
        if (flowNodeInstance.type == ElementInstanceType.SEQUENCE_FLOW) EventSource.SEQUENCE_FLOW
        else EventSource.FLOW_NODE
    val eventTypes =
        if (flowNodeInstance.type == ElementInstanceType.SEQUENCE_FLOW) setOf(EventType.TAKE)
        else if (flowNodeInstance.state == ElementInstanceState.COMPLETED
            || flowNodeInstance.state == ElementInstanceState.TERMINATED) setOf(EventType.START, EventType.END)
        else setOf(EventType.START)
    return eventTypes.map { Event(eventSource, it, flowNodeInstance.elementId,
        flowNodeInstance.type.name,
        bpmnProcessId,
        OffsetDateTime.parse(flowNodeInstance.startDate).toInstant().toEpochMilli()) }
}

/**
 * Maps a flow node instance to an event or null, if the record is not of importance for the coverage.
 */
fun mapEvent(sequenceFlow: ProcessInstanceSequenceFlow, bpmnProcessId: String): Event {
    return Event(EventSource.SEQUENCE_FLOW, EventType.TAKE, sequenceFlow.elementId,
        ElementInstanceType.SEQUENCE_FLOW.name, bpmnProcessId,
        OffsetDateTime.now().toInstant().toEpochMilli())
}

fun CamundaClient.findProcessInstances(): List<ProcessInstance> {
    return this.newProcessInstanceSearchRequest()
        .filter {}
        .sort { it.startDate().asc() }
        .page { it.limit(DEFAULT_PAGE_REQUEST) }
        .send().join()
        .items()
}

fun CamundaClient.findElementInstancesByProcessInstanceKey(processInstanceKey: Long): List<ElementInstance> {
    return this.newElementInstanceSearchRequest()
        .filter { it.processInstanceKey(processInstanceKey) }
        .sort { it.startDate().asc() }
        .page { it.limit(DEFAULT_PAGE_REQUEST) }
        .send().join()
        .items()
}

fun CamundaClient.findSequenceFlowsByProcessInstanceKey(processInstanceKey: Long): List<ProcessInstanceSequenceFlow> {
    return this.newProcessInstanceSequenceFlowsRequest(processInstanceKey)
        .send().join()
}
