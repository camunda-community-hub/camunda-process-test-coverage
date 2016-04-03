package org.camunda.bpm.extension.process_test_coverage.model;

import java.util.HashSet;
import java.util.Set;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

/**
 * Builds pristine process coverage objects.
 * 
 * @author grossax
 * @author z0rbas
 *
 */
public class ProcessCoverageBuilder {

    private ProcessEngine processEngine;
	private ProcessCoverage coverage;
	private boolean collecting = true;

	public static ProcessCoverageBuilder createFlowNodeCoverage(ProcessEngine processEngine) {
		return create(processEngine, "flowNodeCoverage");
	}
	
	public static ProcessCoverageBuilder createFlowNodeAndSequenceFlowCoverage(ProcessEngine processEngine) {
        return create(processEngine, "flowNodeAndSequenceFlowCoverage");
    }
	
	static ProcessCoverageBuilder create(ProcessEngine processEngine, String coverageDescription) {
		ProcessCoverageBuilder cb = new ProcessCoverageBuilder();
		cb.processEngine = processEngine;
		cb.coverage = new ProcessCoverage();
		cb.coverage.description = ""+coverageDescription;
		return cb;
	}

	public ProcessCoverageBuilder withDefinitionFlowNodes(Set<FlowNode> flowNodes) {
		checkIsCollecting();
		coverage.definitionFlowNodes = flowNodes;
		return this;
	}

	public ProcessCoverageBuilder forProcess(ProcessDefinition processDefinition) {
		checkIsCollecting();
		coverage.processDefinition = processDefinition;
		return this;
	}
	
	public ProcessCoverageBuilder withDefinitionSequenceFlows(Set<SequenceFlow> expectedSequenceFlows) {
		checkIsCollecting();
		coverage.definitionSequenceFlows = expectedSequenceFlows;
		return this;
	}
	
	public ProcessCoverage build() {
		checkIsCollecting();
		
		assignProcessDefinitionElements();
		
		collecting  = false;
		return coverage;
	}

	/**
	 * Reads the process definition elements and assigns them to the coverage object.
	 */
    private void assignProcessDefinitionElements() {
        
        final BpmnModelInstance modelInstance = processEngine.getRepositoryService()
		        .getBpmnModelInstance(coverage.getProcessDefinitionId());
		
        Set<FlowNode> flowNodes = new HashSet<FlowNode>(modelInstance.getModelElementsByType(FlowNode.class));
        coverage.setDefinitionFlowNodes(flowNodes);
        
        Set<SequenceFlow> sequenceFlows = new HashSet<SequenceFlow>(modelInstance.getModelElementsByType(SequenceFlow.class));
        coverage.setDefinitionSequenceFlows(sequenceFlows);
    }
    
	private void checkIsCollecting() {
		if (collecting == false) {
			throw new IllegalStateException();
		}
	}
	


}
