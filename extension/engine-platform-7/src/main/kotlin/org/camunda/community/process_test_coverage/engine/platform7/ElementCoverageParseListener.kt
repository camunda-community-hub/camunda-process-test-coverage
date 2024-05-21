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
package org.camunda.community.process_test_coverage.engine.platform7

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.ExecutionListener
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl
import org.camunda.bpm.engine.impl.util.xml.Element
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent
import org.camunda.bpm.model.bpmn.instance.LinkEventDefinition
import org.camunda.community.process_test_coverage.core.model.Collector
import org.camunda.community.process_test_coverage.core.model.Event
import org.camunda.community.process_test_coverage.core.model.EventSource
import org.camunda.community.process_test_coverage.core.model.EventType
import java.time.Instant

/**
 * Parse listener for adding coverage information to the test state.
 */
class ElementCoverageParseListener : AbstractBpmnParseListener() {

    companion object {
        private val EXECUTION_EVENTS = listOf(
            ExecutionListener.EVENTNAME_START,
            ExecutionListener.EVENTNAME_END
        )
    }

    /**
     * The collector of the currently running test.
     */
    private lateinit var coverageState: Collector
    private val executionListener: ExecutionListener = ExecutionListener { execution: DelegateExecution -> execute(execution) }
    private val eventBasedGatewayFlows: MutableSet<String> = mutableSetOf()
    private val activityToFlow: MutableMap<String, String> = mutableMapOf()
    private val linkedEventDefinitions: MutableMap<String, String> = mutableMapOf()

    fun setCoverageState(coverageState: Collector) {
        this.coverageState = coverageState
    }


    private fun execute(execution: DelegateExecution) {
        require(this::coverageState.isInitialized) { "Coverage state must be initialized" }
        if (ExecutionListener.EVENTNAME_START == execution.eventName) {
            // if activity after an event based gateway is triggered, also create event for sequence flow
            if (activityToFlow.containsKey(execution.currentActivityId)) {
                val flowEvent = Event(
                    EventSource.SEQUENCE_FLOW,
                    EventType.TAKE,
                    activityToFlow[execution.currentActivityId]!!,
                    "sequenceFlow",
                    getProcessKey(execution),
                    Instant.now().epochSecond
                )
                coverageState.addEvent(flowEvent)
            }
            // if activity is catch event of a linked event definition also mark the throw event
            if (linkedEventDefinitions.containsKey(execution.currentActivityId)) {
                val linkEventDefinition = linkedEventDefinitions[execution.currentActivityId]!!
                val intermediateThrowEvents =
                    execution.bpmnModelInstance.getModelElementsByType(IntermediateThrowEvent::class.java)
                intermediateThrowEvents
                    .find {
                        it.eventDefinitions
                            .filterIsInstance<LinkEventDefinition>()
                            .any { eventDefinition -> eventDefinition.name == linkEventDefinition }
                    }
                    ?.let {
                        val start = Event(
                            EventSource.FLOW_NODE,
                            EventType.START,
                            it.id,
                            it.elementType.typeName,
                            getProcessKey(execution)
                        )
                        coverageState.addEvent(start)
                        val end = Event(
                            EventSource.FLOW_NODE,
                            EventType.END,
                            it.id,
                            it.elementType.typeName,
                            getProcessKey(execution)
                        )
                        coverageState.addEvent(end)
                    }
            }
            val event = createEvent(
                execution,
                execution.currentActivityId,
                EventSource.FLOW_NODE,
                EventType.START
            )
            coverageState.addEvent(event)
        }
        if (ExecutionListener.EVENTNAME_END == execution.eventName) {
            val event = createEvent(
                execution,
                execution.currentActivityId,
                EventSource.FLOW_NODE,
                EventType.END
            )
            coverageState.addEvent(event)
        }
        if (ExecutionListener.EVENTNAME_TAKE == execution.eventName) {
            val event = createEvent(
                execution,
                execution.currentTransitionId,
                EventSource.SEQUENCE_FLOW,
                EventType.TAKE
            )
            coverageState.addEvent(event)
        }
    }

    private fun createEvent(execution: DelegateExecution, activityId: String, eventSource: EventSource, eventType: EventType): Event {
        return Event(
            eventSource,
            eventType,
            activityId,
            execution.bpmnModelElementInstance.elementType.typeName,
            getProcessKey(execution),
            Instant.now().epochSecond
        )
    }

    private fun getProcessKey(delegateExecution: DelegateExecution): String {
        return delegateExecution.processEngineServices.repositoryService.getProcessDefinition(delegateExecution.processDefinitionId).key
    }

    override fun parseUserTask(userTaskElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseBoundaryErrorEventDefinition(errorEventDefinition: Element, interrupting: Boolean, activity: ActivityImpl, nestedErrorEventActivity: ActivityImpl) {
        // this.addExecutionListener(activity);
    }

    override fun parseBoundaryEvent(boundaryEventElement: Element, scopeElement: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseBoundaryMessageEventDefinition(element: Element, interrupting: Boolean, activity: ActivityImpl) {
        //this.addExecutionListener(activity);
    }

    override fun parseBoundarySignalEventDefinition(signalEventDefinition: Element, interrupting: Boolean, activity: ActivityImpl) {
        //this.addExecutionListener(activity);
    }

    override fun parseBoundaryTimerEventDefinition(timerEventDefinition: Element, interrupting: Boolean, activity: ActivityImpl) {
        //this.addExecutionListener(activity);
    }

    override fun parseBusinessRuleTask(businessRuleTaskElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseCallActivity(callActivityElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseCompensateEventDefinition(compensateEventDefinition: Element, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseEndEvent(endEventElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseEventBasedGateway(eventBasedGwElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
        eventBasedGwElement.elements()
            .filter { it.tagName == "outgoing" }
            .forEach { eventBasedGatewayFlows.add(it.text) }
    }

    override fun parseExclusiveGateway(exclusiveGwElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseInclusiveGateway(inclusiveGwElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseIntermediateCatchEvent(intermediateEventElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        // check whether activity is after event based gateway
        intermediateEventElement.elements()
            .filter { it.tagName == "incoming" }
            .filter { eventBasedGatewayFlows.contains(it.text) }
            .forEach { activityToFlow[intermediateEventElement.attribute("id")] = it.text }
        intermediateEventElement.elements()
            .filter { it.tagName == "linkEventDefinition" }
            .map { it.attribute("name") }
            .forEach { linkedEventDefinitions[intermediateEventElement.attribute("id")] = it }
        this.addExecutionListener(activity)
    }

    override fun parseIntermediateMessageCatchEventDefinition(messageEventDefinition: Element, activity: ActivityImpl) {
        // this.addExecutionListener(activity);
    }

    override fun parseIntermediateSignalCatchEventDefinition(signalEventDefinition: Element, activity: ActivityImpl) {
        // this.addExecutionListener(activity);
    }

    override fun parseIntermediateThrowEvent(intermediateEventElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseIntermediateTimerEventDefinition(timerEventDefinition: Element, activity: ActivityImpl) {
        // this.addExecutionListener(activity);
    }

    override fun parseManualTask(manualTaskElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseMultiInstanceLoopCharacteristics(activityElement: Element, multiInstanceLoopCharacteristicsElement: Element, activity: ActivityImpl) {
        // DO NOT IMPLEMENT
        // we do not notify on entering a multi-instance activity, this will be done for every single execution inside that loop.
    }

    override fun parseParallelGateway(parallelGwElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseReceiveTask(receiveTaskElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        // check whether activity is after event based gateway
        receiveTaskElement.elements()
            .filter { it.tagName == "incoming" }
            .filter { eventBasedGatewayFlows.contains(it.text) }
            .forEach { activityToFlow[receiveTaskElement.attribute("id")] = it.text }
        this.addExecutionListener(activity)
    }

    override fun parseScriptTask(scriptTaskElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseSendTask(sendTaskElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseSequenceFlow(sequenceFlowElement: Element, scopeElement: ScopeImpl, transition: TransitionImpl) {
        this.addExecutionListener(transition)
    }

    override fun parseServiceTask(serviceTaskElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseStartEvent(startEventElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseSubProcess(subProcessElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseTask(taskElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    override fun parseTransaction(transactionElement: Element, scope: ScopeImpl, activity: ActivityImpl) {
        this.addExecutionListener(activity)
    }

    private fun addExecutionListener(activity: ActivityImpl) {
        for (event in EXECUTION_EVENTS) {
            activity.addListener(event, executionListener)
        }
    }

    private fun addExecutionListener(transition: TransitionImpl) {
        transition.addListener(ExecutionListener.EVENTNAME_TAKE, executionListener)
    }

}