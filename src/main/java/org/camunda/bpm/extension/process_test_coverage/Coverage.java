package org.camunda.bpm.extension.process_test_coverage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class Coverage {

	String description;

	ProcessDefinition processDefinition;

	Set<String> coveredActivityIds;

	Collection<FlowNode> expectedFlowNodes;

	Set<String> coveredSequenceFlowIds;

	Collection<SequenceFlow> expectedSequenceFlows;

	@Override
	public String toString() {
		return "Coverage [" + "description=" + description + ", process=" + processDefinition.getName() + ", " +
				"coverage=" + getActualPercentage()+ " (" + getNumberOfAllActual() + "/" + getNumberOfAllExpected()+ ")" +
				"flowNodes=(" + coveredActivityIds.size() + "/" + expectedFlowNodes.size() + ")" +
			 "sequenceFlows=(" + coveredSequenceFlowIds.size() + "/" + expectedSequenceFlows.size()+ ")" +
				", coveredActivityIds=" + coveredActivityIds + ", expectedFlowNodes=" + expectedFlowNodes + "]";
	}

	public static double calculateMeanPercentage(Map<String, Coverage> processesFlowNodeCoverage) {
		return calculateMeanPercentage(processesFlowNodeCoverage.values());
	}

	public static double calculateMeanPercentage(Collection<Coverage> processesFlowNodeCoverage) {
		int sumOfActual = 0;
		int sumOfExpected = 0;
		for (Coverage coverage : processesFlowNodeCoverage) {
			sumOfActual += coverage.getNumberOfAllActual();
			sumOfExpected += coverage.getNumberOfAllExpected();
		}
		return ((double) sumOfActual) / ((double) sumOfExpected) / ((double) processesFlowNodeCoverage.size());
	}

	public int getNumberOfAllActual() {
		return coveredActivityIds.size() + coveredSequenceFlowIds.size();
	}

	public int getNumberOfAllExpected() {
		return expectedFlowNodes.size() + expectedSequenceFlows.size();
	}

	public double getActualPercentage() {

		return ((double) getNumberOfAllActual()) / ((double) getNumberOfAllExpected());
	}

}
