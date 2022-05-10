package org.camunda.bpm.extension.process_test_coverage.engine

import io.camunda.zeebe.model.bpmn.Bpmn.*
import io.camunda.zeebe.model.bpmn.instance.FlowNode
import io.camunda.zeebe.model.bpmn.instance.IntermediateThrowEvent
import io.camunda.zeebe.model.bpmn.instance.LinkEventDefinition
import io.camunda.zeebe.model.bpmn.instance.Process
import io.camunda.zeebe.model.bpmn.instance.SequenceFlow
import io.camunda.zeebe.process.test.assertions.BpmnAssert.getRecordStream
import org.camunda.bpm.extension.process_test_coverage.model.Model
import org.camunda.bpm.model.xml.instance.ModelElementInstance
import java.io.ByteArrayInputStream
import java.util.stream.Collectors

/**
 * Provider that is used to load processes from the engine.
 * The record stream from zeebe is used for this.
 */
class ZeebeModelProvider: ModelProvider {

    override fun getModel(key: String): Model {
        val processMetadata = getRecordStream().deploymentRecords()
            .firstNotNullOfOrNull { it.value.processesMetadata.firstOrNull { p -> p.bpmnProcessId == key } }
        val resource = getRecordStream().deploymentRecords()
            .firstNotNullOfOrNull { it.value.resources.firstOrNull { res -> res.resourceName == processMetadata?.resourceName } }

        return resource?.let {
            val modelInstance = readModelFromStream(ByteArrayInputStream(resource.resource))
            val definitionFlowNodes = getExecutableFlowNodes(modelInstance.getModelElementsByType(FlowNode::class.java), key)
            val definitionSequenceFlows = getExecutableSequenceNodes(modelInstance.getModelElementsByType(SequenceFlow::class.java), definitionFlowNodes)
            Model(
                key,
                definitionFlowNodes.size + definitionSequenceFlows.size,
                "${processMetadata!!.version}",
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
        return if (node is Process) {
            node.isExecutable && node.id == processId
        } else if (node is IntermediateThrowEvent) {
            node.eventDefinitions.none { it is LinkEventDefinition }
        } else {
            isExecutable(node.parentElement, processId)
        }
    }

}