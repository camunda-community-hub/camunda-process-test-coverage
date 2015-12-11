package org.camunda.bpm.extension.process_test_coverage;

import java.util.Collection;
import java.util.Set;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElements;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class CoverageBuilder {

	Coverage coverage;
	private boolean filterByDefinitionId = false;
	private boolean collecting = true;
	private TestCoverageTestRunState testCoverageTestRunState;

	public static CoverageBuilder createFlowNodeCoverage() {
		return create("flowNodeCoverage");
	}
	static CoverageBuilder create(String coverageDescription) {
		CoverageBuilder cb = new CoverageBuilder();
		cb.coverage = new Coverage();
		cb.coverage.description = ""+coverageDescription;
		return cb;
	}

	public CoverageBuilder filterByDefinitionIdInsteadOfKey() {
		checkIsCollecting();
		filterByDefinitionId = true;
		return this;
	}
	
	public CoverageBuilder withActualFlowNodes(Set<CoveredElement> coveredActivities) {
		checkIsCollecting();
		coverage.coveredActivities = coveredActivities;
		return this;
	}

	public CoverageBuilder withExpectedFlowNodes(Collection<FlowNode> flowNodes) {
		checkIsCollecting();
		coverage.expectedFlowNodes = flowNodes;
		return this;
	}

	public CoverageBuilder forProcess(ProcessDefinition processDefinition) {
		checkIsCollecting();
		coverage.processDefinition = processDefinition;
		return this;
	}

	public CoverageBuilder withActualSequenceFlows(Set<CoveredElement> coveredTransitionIds) {
		checkIsCollecting();
		coverage.coveredSequenceFlowIds = coveredTransitionIds;
		return this;
	}
	
	public CoverageBuilder withExpectedSequenceFlows(Collection<SequenceFlow> expectedSequenceFlows) {
		checkIsCollecting();
		coverage.expectedSequenceFlows = expectedSequenceFlows;
		return this;
	}
	
	public CoverageBuilder withActualFlowNodesAndSequenceFlows(TestCoverageTestRunState testCoverageTestRunState) {
		checkIsCollecting();
		this.testCoverageTestRunState = testCoverageTestRunState;
		return this;
	}
	
	public Coverage build() {
		checkIsCollecting();
		collecting  = false;
		// lets filter the entries relevant to this coverage
		if (testCoverageTestRunState != null) {
			if (coverage.coveredActivities != null || coverage.coveredSequenceFlowIds != null) {
				throw new RuntimeException("Bad builder setup");
			}
			if (filterByDefinitionId) {
				coverage.coveredActivities = testCoverageTestRunState.getCoveredFlowNodes(coverage.processDefinition.getId());
				coverage.coveredSequenceFlowIds = testCoverageTestRunState.getCoveredSequenceFlows(coverage.processDefinition.getId());
			} else {
				coverage.coveredActivities = testCoverageTestRunState.getCoveredFlowNodes();
				coverage.coveredSequenceFlowIds = testCoverageTestRunState.getCoveredSequenceFlows();
			}
		}
		if (coverage.processDefinition != null) {
			if (filterByDefinitionId) {
				coverage.description += "-filterByDefinitionId";
				coverage.coveredActivities = CoveredElements.findProcessInstances(coverage.processDefinition.getId(), null, coverage.coveredActivities);
				coverage.coveredSequenceFlowIds = CoveredElements.findProcessInstances(coverage.processDefinition.getId(), null, coverage.coveredSequenceFlowIds);
			} else {
				// we only care about the key, not the deployment in this case
				coverage.description += "-filterByDefinitionKey";
				coverage.coveredActivities = CoveredElements.findProcessInstances(coverage.processDefinition.getKey() + ":", null, coverage.coveredActivities);
				coverage.coveredSequenceFlowIds = CoveredElements.findProcessInstances(coverage.processDefinition.getKey() + ":", null, coverage.coveredSequenceFlowIds);				
			}
		}
		return coverage;
	}
	private void checkIsCollecting() {
		if (collecting == false) {
			throw new IllegalStateException();
		}
	}
	


}
