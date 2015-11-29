package org.camunda.bpm.extension.process_test_coverage;

import java.util.Collection;
import java.util.Set;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class CoverageBuilder {

	Coverage coverage = new Coverage();

	public static CoverageBuilder createFlowNodeCoverage() {
		return create("flowNodeCoverage");
	}
	static CoverageBuilder create(String coverageDescription) {
		CoverageBuilder cb = new CoverageBuilder();
		cb.coverage.description = coverageDescription;
		return cb;
	}

	public CoverageBuilder withActualFlowNodes(Set<CoveredElement> coveredActivities) {
		coverage.coveredActivities = coveredActivities;
		return this;
	}

	public CoverageBuilder withExpectedFlowNodes(Collection<FlowNode> flowNodes) {
		coverage.expectedFlowNodes = flowNodes;
		return this;
	}

	public CoverageBuilder forProcess(ProcessDefinition processDefinition) {
		coverage.processDefinition = processDefinition;
		return this;
	}

	public CoverageBuilder withActualSequenceFlows(Set<CoveredElement> coveredTransitionIds) {
		coverage.coveredSequenceFlowIds = coveredTransitionIds;
		return this;
	}
	
	public CoverageBuilder withExpectedSequenceFlows(Collection<SequenceFlow> expectedSequenceFlows) {
		coverage.expectedSequenceFlows = expectedSequenceFlows;
		return this;
	}
	
	
	public Coverage build() {
		return coverage;
	}
	


}
