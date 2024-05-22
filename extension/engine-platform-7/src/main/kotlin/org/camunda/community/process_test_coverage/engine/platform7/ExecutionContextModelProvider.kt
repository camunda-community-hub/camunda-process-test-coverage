/*-
 * #%L
 * Camunda Process Test Coverage Engine Platform 7
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
package org.camunda.community.process_test_coverage.engine.platform7

import org.camunda.bpm.engine.impl.context.Context
import org.camunda.bpm.model.bpmn.Bpmn
import org.camunda.bpm.model.bpmn.instance.FlowNode
import org.camunda.bpm.model.bpmn.instance.Process
import org.camunda.bpm.model.bpmn.instance.SequenceFlow
import org.camunda.bpm.model.xml.instance.ModelElementInstance
import org.camunda.community.process_test_coverage.core.engine.ModelProvider
import org.camunda.community.process_test_coverage.core.model.Model
import java.util.stream.Collectors

/**
 * Provider that is used to load processes from the engine.
 * The Thread Local Context of Camunda is used for this.
 */
class ExecutionContextModelProvider: ModelProvider {

    override fun getModel(key: String): Model {

        val services = Context.getBpmnExecutionContext()
            .execution
            .processEngineServices
        val processDefinition = services.repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(key)
            .latestVersion()
            .singleResult()
        val modelInstance = services.repositoryService.getBpmnModelInstance(
            processDefinition.id
        )
        val definitionFlowNodes = getExecutableFlowNodes(modelInstance.getModelElementsByType(FlowNode::class.java), key)
        val definitionSequenceFlows = getExecutableSequenceNodes(modelInstance.getModelElementsByType(SequenceFlow::class.java), definitionFlowNodes)

        return Model(
            key,
            definitionFlowNodes.size + definitionSequenceFlows.size,
            processDefinition.versionTag,
            Bpmn.convertToString(modelInstance)
        )
    }

    private fun getExecutableFlowNodes(flowNodes: Collection<FlowNode>, processId: String): Set<FlowNode> {
        return flowNodes.stream()
            .filter { node: FlowNode? -> isExecutable(node, processId) }
            .collect(Collectors.toSet())
    }

    private fun getExecutableSequenceNodes(sequenceFlows: Collection<SequenceFlow>, definitionFlowNodes: Set<FlowNode>): Set<SequenceFlow> {
        return sequenceFlows.stream()
            .filter { s: SequenceFlow -> definitionFlowNodes.contains(s.source) }
            .collect(Collectors.toSet())
    }

    private fun isExecutable(node: ModelElementInstance?, processId: String): Boolean {
        if (node == null) {
            return false
        }
        return when (node) {
            is Process -> node.isExecutable && node.id == processId
            else -> isExecutable(node.parentElement, processId)
        }
    }
}
