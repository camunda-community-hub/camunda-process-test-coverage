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

package org.camunda.bpm.extension.process_test_coverage.engine;

import org.camunda.bpm.engine.impl.bpmn.helper.BpmnProperties;
import org.camunda.bpm.engine.impl.event.CompensationEventHandler;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.extension.process_test_coverage.model.Collector;
import org.camunda.bpm.extension.process_test_coverage.model.Event;
import org.camunda.bpm.extension.process_test_coverage.model.EventSource;
import org.camunda.bpm.extension.process_test_coverage.model.EventType;

/**
 * Handler for Compensation Boundary Events
 */
public class CompensationEventCoverageHandler extends CompensationEventHandler {

    /**
     * The collector the currently running coverage.
     */
    private Collector coverageState;

    @Override
    public void handleEvent(final EventSubscriptionEntity eventSubscription, final Object payload, final Object localPayload,
                            final String businessKey, final CommandContext commandContext) {
        this.addCompensationEventCoverage(eventSubscription);
        super.handleEvent(eventSubscription, payload, localPayload, businessKey, commandContext);
    }

    private void addCompensationEventCoverage(final EventSubscriptionEntity eventSubscription) {
        final ActivityImpl activity = eventSubscription.getActivity();

        // Get process definition key
        final ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) activity.getProcessDefinition();

        // Get compensation boundary event ID
        final ActivityImpl sourceEvent = (ActivityImpl) activity.getProperty(
                BpmnProperties.COMPENSATION_BOUNDARY_EVENT.getName());

        if (sourceEvent != null) {
            final String sourceEventId = sourceEvent.getActivityId();

            // Register event
            final Event startEvent = new Event(
                    EventSource.FlOW_NODE,
                    EventType.START,
                    sourceEventId,
                    "boundaryEvent",
                    processDefinition.getId(),
                    processDefinition.getKey());
            this.coverageState.addEvent(startEvent);

            final Event endEvent = new Event(
                    EventSource.FlOW_NODE,
                    EventType.END,
                    sourceEventId,
                    "boundaryEvent",
                    processDefinition.getId(),
                    processDefinition.getKey());
            this.coverageState.addEvent(endEvent);
        }
    }

    public void setCoverageState(final Collector coverageState) {
        this.coverageState = coverageState;
    }
}