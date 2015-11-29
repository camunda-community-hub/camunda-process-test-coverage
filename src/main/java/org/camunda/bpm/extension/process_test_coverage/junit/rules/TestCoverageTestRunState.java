package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.HashSet;
import java.util.Set;

import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElements;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredSequenceFlow;

public class TestCoverageTestRunState {

	/** NonNull global variable holding a list of the flow trace elements of the test run.<br>
	 * clear this (e.g. in @BeforeClass) if you want to */
	Set<CoveredElement> currentFlowTrace = new HashSet<CoveredElement>();
	
	double highestSeenCoverage = -2.0;
	/** clears the current flow trace, e.g. in @BeforeClass */
	public void resetCurrentFlowTrace(){
		System.out.println("Clearing flow trace");
		currentFlowTrace.clear();
		highestSeenCoverage = -2.0;
	}
	public void notifyCoveredElement(CoveredElement coveredElement) {
		currentFlowTrace.add(coveredElement);
	}
	
	public Set<CoveredElement> getCoveredSequenceFlows() {
		return CoveredElements.findProcessInstances(null, CoveredSequenceFlow.class, currentFlowTrace);
	}
	
	public Set<CoveredElement> getCoveredFlowNodes() {
		return CoveredElements.findProcessInstances(null, CoveredActivity.class, currentFlowTrace);
	}
	
	public Set<String> findCoveredActivityIds() {
		return mapElementIds(CoveredElements.findProcessInstances(null, CoveredActivity.class, currentFlowTrace));
	}
	
	private static Set<String> mapElementIds(Set<CoveredElement> elements) {
		Set<String> mapped = new HashSet<String>();
		for(CoveredElement coveredElement : elements) {
			mapped.add(coveredElement.getElementId());
		}
		return mapped;
	}

	private static TestCoverageTestRunState theInstance = new TestCoverageTestRunState();
	public static TestCoverageTestRunState INSTANCE() {
		return theInstance;
	}
	
	

	
	
	
	

}
