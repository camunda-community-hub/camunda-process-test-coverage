package org.camunda.bpm.extension.process_test_coverage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class Coverage {

	String description;

	String processDefinitionId;

	Set<CoveredElement> coveredActivities;

	Collection<FlowNode> expectedFlowNodes;

	Set<CoveredElement> coveredSequenceFlowIds;

	Collection<SequenceFlow> expectedSequenceFlows;

	@Override
	public String toString() {
		return "Coverage [" + "description=" + description + ", processDefinitionId=" + processDefinitionId + ", " +
				"coverage=" + getActualPercentage()+ " (" + getNumberOfAllActual() + "/" + getNumberOfAllExpected()+ ")" +
				"flowNodes=(" + coveredActivities.size() + "/" + expectedFlowNodes.size() + ")" +
			 "sequenceFlows=(" + coveredSequenceFlowIds.size() + "/" + expectedSequenceFlows.size()+ ")" +
				", coveredActivityIds=" + coveredActivities + ", expectedFlowNodes=" + expectedFlowNodes + "]";
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
	
	// TODO only useful calculation here? does it need to be static? we already have the coverage ...
	public double calculateMeanPercentage() {
	    return ((double) getNumberOfAllActual()) / ((double) getNumberOfAllExpected());
	}

	public int getNumberOfAllActual() {
		return coveredActivities.size() + coveredSequenceFlowIds.size();
	}

	public int getNumberOfAllExpected() {
		return expectedFlowNodes.size() + expectedSequenceFlows.size();
	}

	public double getActualPercentage() {

		return ((double) getNumberOfAllActual()) / ((double) getNumberOfAllExpected());
	}

}
