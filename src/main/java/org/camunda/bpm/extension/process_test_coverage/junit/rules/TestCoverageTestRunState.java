package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElements;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredSequenceFlow;

public class TestCoverageTestRunState {

    public static final double COVERAGE_NOT_SET = -2.0;
    
	Logger log = Logger.getLogger(TestCoverageTestRunState.class.getCanonicalName());
	

    private static final ThreadLocal<TestCoverageTestRunState> threadLocalInstance =
             new ThreadLocal<TestCoverageTestRunState>() {
                 @Override protected TestCoverageTestRunState initialValue() {
                     return new TestCoverageTestRunState();
             }
         }; 
    public static TestCoverageTestRunState INSTANCE() {
        return threadLocalInstance.get();
    }
	
	/** NonNull global variable holding a list of the flow trace elements of the coverage's tests run.<br>
	 * clear this (e.g. in @BeforeClass) if you want to start collecting coverage */
	Set<CoveredElement> currentFlowTrace = new HashSet<CoveredElement>();
	
	/** NonNull global variable holding a list relevant deployment id's of the coverage's tests run.<br>
     * clear this (e.g. in @BeforeClass) if you want to start collecting coverage */
	Set<String> relevantDeploymentIds = new HashSet<String>();
	
	double highestSeenCoverage = COVERAGE_NOT_SET;
	
	/** clears the current trace information, e.g. in @BeforeClass */
	public void resetCurrentFlowTrace(){
	    log.info("Resetting complete flow trace, relevantDeploymentIds and highest seen coverage");
		currentFlowTrace.clear();
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
	
    public Set<String> getRelevantDeploymentIds() {
        return relevantDeploymentIds;
    }
    
	public boolean isHighestSeenCoverageSet() {
        return highestSeenCoverage != COVERAGE_NOT_SET;
    }

}
