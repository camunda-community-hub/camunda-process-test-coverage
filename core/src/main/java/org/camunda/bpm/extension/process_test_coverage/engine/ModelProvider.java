package org.camunda.bpm.extension.process_test_coverage.engine;

import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.model.Model;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provider that is used to load processes from the engine.
 * The Thread Local Context of Camunda is used for this.
 */
public class ModelProvider {

    public Model getModel(final String key) {
        final ProcessEngineServices services = Context.getBpmnExecutionContext()
                .getExecution()
                .getProcessEngineServices();

        final ProcessDefinition processDefinition = services.getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .latestVersion()
                .singleResult();

        final BpmnModelInstance modelInstance = services.getRepositoryService().getBpmnModelInstance(
                processDefinition.getId());

        final Set<FlowNode> definitionFlowNodes = this.getExecutableFlowNodes(modelInstance.getModelElementsByType(FlowNode.class), key);
        final Set<SequenceFlow> definitionSequenceFlows = this.getExecutableSequenceNodes(modelInstance.getModelElementsByType(SequenceFlow.class), definitionFlowNodes);

        return new Model(
                processDefinition.getId(),
                key,
                definitionFlowNodes.size() + definitionSequenceFlows.size(),
                processDefinition.getVersionTag(),
                Bpmn.convertToString(modelInstance));
    }

    private Set<FlowNode> getExecutableFlowNodes(final Collection<FlowNode> flowNodes, final String processId) {
        return flowNodes.stream()
                .filter(node -> this.isExecutable(node, processId))
                .collect(Collectors.toSet());
    }

    private Set<SequenceFlow> getExecutableSequenceNodes(final Collection<SequenceFlow> sequenceFlows, final Set<FlowNode> definitionFlowNodes) {
        return sequenceFlows.stream()
                .filter(s -> definitionFlowNodes.contains(s.getSource()))
                .collect(Collectors.toSet());
    }


    private boolean isExecutable(final ModelElementInstance node, final String processId) {

        if (node == null) {
            return false;
        }

        if (node instanceof org.camunda.bpm.model.bpmn.instance.Process) {
            return ((org.camunda.bpm.model.bpmn.instance.Process) node).isExecutable() && ((org.camunda.bpm.model.bpmn.instance.Process) node).getId().equals(processId);
        }

        return this.isExecutable(node.getParentElement(), processId);
    }

}
