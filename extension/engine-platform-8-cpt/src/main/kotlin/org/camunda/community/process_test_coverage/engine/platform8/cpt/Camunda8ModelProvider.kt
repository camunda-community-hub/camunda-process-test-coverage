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
import io.camunda.client.api.search.response.ProcessDefinition
import io.camunda.process.test.api.CamundaProcessTestContext
import io.camunda.zeebe.model.bpmn.Bpmn
import io.camunda.zeebe.model.bpmn.Bpmn.convertToString
import io.camunda.zeebe.model.bpmn.instance.FlowNode
import io.camunda.zeebe.model.bpmn.instance.Process
import io.camunda.zeebe.model.bpmn.instance.SequenceFlow
import org.camunda.bpm.model.xml.instance.ModelElementInstance
import org.camunda.community.process_test_coverage.core.engine.ModelProvider
import org.camunda.community.process_test_coverage.core.model.Model
import java.util.stream.Collectors

/**
 * Provider that is used to load process models from the engine.
 * The operate rest api is used for this.
 */
class Camunda8ModelProvider(
    private val camundaProcessTestContext: () -> CamundaProcessTestContext
): ModelProvider {

    override fun getModel(key: String): Model {

        val client = camundaProcessTestContext.invoke().createClient()

        val processDefinition = client.getProcessDefinitionByProcessDefinitionId(key)
        val modelInstance = Bpmn.readModelFromStream(client.getProcessDefinitionXml(
            processDefinition.processDefinitionKey).byteInputStream())

        return modelInstance?.let {
            val definitionFlowNodes = getExecutableFlowNodes(modelInstance.getModelElementsByType(FlowNode::class.java), key)
            val definitionSequenceFlows = getExecutableSequenceNodes(modelInstance.getModelElementsByType(SequenceFlow::class.java), definitionFlowNodes)
            Model(
                key,
                definitionFlowNodes.size + definitionSequenceFlows.size,
                "${processDefinition.version}",
                convertToString(modelInstance)
            )
        } ?: throw IllegalArgumentException()

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

fun CamundaClient.getProcessDefinitionByProcessDefinitionId(processDefinitionId: String): ProcessDefinition {
    return this.newProcessDefinitionSearchRequest()
        .filter { it.processDefinitionId(processDefinitionId) }
        .page { it.limit(DEFAULT_PAGE_REQUEST) }
        .sort { it.version().desc() }
        .send().join()
        .items().first()
}

fun CamundaClient.getProcessDefinitionXml(processDefinitionKey: Long): String {
    return this.newProcessDefinitionGetXmlRequest(processDefinitionKey)
        .send().join()
}
