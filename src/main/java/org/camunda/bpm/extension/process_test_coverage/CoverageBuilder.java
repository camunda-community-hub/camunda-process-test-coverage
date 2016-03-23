package org.camunda.bpm.extension.process_test_coverage;

import java.util.Set;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredSequenceFlow;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class CoverageBuilder {

	private ProcessCoverage coverage;
	private boolean collecting = true;

	public static CoverageBuilder createFlowNodeCoverage() {
		return create("flowNodeCoverage");
	}
	static CoverageBuilder create(String coverageDescription) {
		CoverageBuilder cb = new CoverageBuilder();
		cb.coverage = new ProcessCoverage();
		cb.coverage.description = ""+coverageDescription;
		return cb;
	}
	
	public CoverageBuilder withActualFlowNodes(Set<CoveredActivity> coveredActivities) {
		checkIsCollecting();
		coverage.coveredFlowNodes = coveredActivities;
		return this;
	}

	public CoverageBuilder withExpectedFlowNodes(Set<FlowNode> flowNodes) {
		checkIsCollecting();
		coverage.definitionFlowNodes = flowNodes;
		return this;
	}

	public CoverageBuilder forProcess(ProcessDefinition processDefinition) {
		checkIsCollecting();
		coverage.processDefinition = processDefinition;
		coverage.processDefinitionId = processDefinition.getId();
		return this;
	}

	public CoverageBuilder withActualSequenceFlows(Set<CoveredSequenceFlow> coveredTransitionIds) {
		checkIsCollecting();
		coverage.coveredSequenceFlows = coveredTransitionIds;
		return this;
	}
	
	public CoverageBuilder withExpectedSequenceFlows(Set<SequenceFlow> expectedSequenceFlows) {
		checkIsCollecting();
		coverage.definitionSequenceFlows = expectedSequenceFlows;
		return this;
	}
	
	public ProcessCoverage build() {
		checkIsCollecting();
		collecting  = false;
		return coverage;
	}
	private void checkIsCollecting() {
		if (collecting == false) {
			throw new IllegalStateException();
		}
	}
	


}
