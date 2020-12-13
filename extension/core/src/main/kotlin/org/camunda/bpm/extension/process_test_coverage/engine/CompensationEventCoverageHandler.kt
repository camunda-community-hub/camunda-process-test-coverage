/*
 * Copyright 2020 FlowSquad GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.camunda.bpm.extension.process_test_coverage.engine

import org.camunda.bpm.engine.impl.bpmn.helper.BpmnProperties
import org.camunda.bpm.engine.impl.event.CompensationEventHandler
import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl
import org.camunda.bpm.extension.process_test_coverage.model.Collector
import org.camunda.bpm.extension.process_test_coverage.model.Event
import org.camunda.bpm.extension.process_test_coverage.model.EventSource
import org.camunda.bpm.extension.process_test_coverage.model.EventType
import java.time.Instant

/**
 * Handler for Compensation Boundary Events
 */
class CompensationEventCoverageHandler : CompensationEventHandler() {
    /**
     * The collector the currently running coverage.
     */
    private lateinit var coverageState: Collector

    fun setCoverageState(coverageState: Collector) {
        this.coverageState = coverageState
    }

    override fun handleEvent(
        eventSubscription: EventSubscriptionEntity, payload: Any?, localPayload: Any?,
        businessKey: String?, commandContext: CommandContext
    ) {
        addCompensationEventCoverage(eventSubscription)
        super.handleEvent(eventSubscription, payload, localPayload, businessKey, commandContext)
    }

    private fun addCompensationEventCoverage(eventSubscription: EventSubscriptionEntity) {
        val activity = eventSubscription.activity

        // Get process definition key
        val processDefinition = activity.processDefinition as ProcessDefinitionEntity

        // Get compensation boundary event ID
        val sourceEvent = activity.getProperty(
            BpmnProperties.COMPENSATION_BOUNDARY_EVENT.name
        ) as ActivityImpl?
        if (sourceEvent != null) {
            requireNotNull(this::coverageState.isInitialized) { "Coverage state must be initialized" }
            val sourceEventId = sourceEvent.activityId

            // Register event
            val startEvent = Event(
                EventSource.FLOW_NODE,
                EventType.START,
                sourceEventId,
                "boundaryEvent",
                processDefinition.key,
                Instant.now().epochSecond
            )
            coverageState.addEvent(startEvent)
            val endEvent = Event(
                EventSource.FLOW_NODE,
                EventType.END,
                sourceEventId,
                "boundaryEvent",
                processDefinition.key,
                Instant.now().epochSecond
            )
            coverageState.addEvent(endEvent)
        }
    }


}