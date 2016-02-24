package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.camunda.bpm.extension.process_test_coverage.TraceActivitiesHistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElements;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredSequenceFlow;

public class TestCoverageTestRunState {

    public static final double COVERAGE_NOT_SET = -2.0;
    
	Logger log = Logger.getLogger(TestCoverageTestRunState.class.getCanonicalName());
	
	/** NonNull global variable holding a list of the flow trace elements of the test run.<br>
	 * clear this (e.g. in @BeforeClass) if you want to */
	Set<CoveredElement> currentFlowTrace = new HashSet<CoveredElement>();
	Set<String> relevantDeploymentIds = new HashSet<String>();
	
	public Set<String> getRelevantDeploymentIds() {
        return relevantDeploymentIds;
    }
	
	double highestSeenCoverage = COVERAGE_NOT_SET;
	/** clears the current flow trace, e.g. in @BeforeClass */
	public void resetCurrentFlowTrace(){
	    log.info("Resetting complete flow trace");
		currentFlowTrace.clear(); // FIXME
		relevantDeploymentIds.clear();
		highestSeenCoverage = COVERAGE_NOT_SET;
	}
	public void notifyCoveredElement(/*@NotNull*/ CoveredElement coveredElement) {
		log.info("notifyCoveredElement(" + coveredElement + ")");
		currentFlowTrace.add(coveredElement);
	}
	
	public Set<CoveredElement> getCoveredSequenceFlows() {
		return CoveredElements.findProcessInstances(null, CoveredSequenceFlow.class, currentFlowTrace);
	}
	public Set<CoveredElement> getCoveredSequenceFlows(String processDefinitionIdStart) {
		return CoveredElements.findProcessInstances(processDefinitionIdStart, CoveredSequenceFlow.class, currentFlowTrace);
	}
	
	public Set<CoveredElement> getCoveredFlowNodes() {
		return CoveredElements.findProcessInstances(null, CoveredActivity.class, currentFlowTrace);
	}
	
	public Set<CoveredElement> getCoveredFlowNodes(String processDefinitionIdStart) {
		return CoveredElements.findProcessInstances(processDefinitionIdStart, CoveredActivity.class, currentFlowTrace);
	}
	
	public Set<String> findCoveredActivityIds() {
		return CoverageMappings.mapElementsToIds(CoveredElements.findProcessInstances(null, CoveredActivity.class, currentFlowTrace));
	}
	
	private static final ThreadLocal<TestCoverageTestRunState> threadLocalInstance =
	         new ThreadLocal<TestCoverageTestRunState>() {
	             @Override protected TestCoverageTestRunState initialValue() {
	                 return new TestCoverageTestRunState();
	         }
	     }; 
	private static final TestCoverageTestRunState theInstance = new TestCoverageTestRunState();
	public static TestCoverageTestRunState INSTANCE() {
		return threadLocalInstance.get();
	}
    
	public boolean isHighestSeenCoverageSet() {
        return highestSeenCoverage != COVERAGE_NOT_SET;
    }

}
