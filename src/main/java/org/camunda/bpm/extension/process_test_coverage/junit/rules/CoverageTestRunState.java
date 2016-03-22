package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.CoverageMappings;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElements;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredSequenceFlow;

/**
 * State tracking the coverage percentage and flows covered by the current test method and class. 
 * 
 * @author grossax
 * @author okicir
 */
public class CoverageTestRunState {
    
	private Logger log = Logger.getLogger(CoverageTestRunState.class.getCanonicalName());
    
    /**
     * Map of covered elements per test. An entry represents the covered for a single test, while the map represents the class coverage.
     */
    private Map<String, Set<CoveredElement>> testToCoveredElements = new HashMap<String, Set<CoveredElement>>();
	
	/** NonNull global variable holding a list relevant deployment id's of the coverage's tests run.<br>
     * clear this (e.g. in @BeforeClass) if you want to start collecting coverage */
	private Map<String, Set<ProcessDefinition>> deployments = new HashMap<String, Set<ProcessDefinition>>();

    private String currentTestName;
	
	public void notifyCoveredElement(/*@NotNull*/ CoveredElement coveredElement) {
	    
	    if (log.isLoggable(Level.FINE)) {
	        log.info("notifyCoveredElement(" + coveredElement + ")");
	    }
	    
	    // JDK7 putIfAbsent
	    Set<CoveredElement> currentTestCoveredElements = testToCoveredElements.get(currentTestName);
	    if (currentTestCoveredElements == null) {
	        currentTestCoveredElements = new HashSet<CoveredElement>();
	        testToCoveredElements.put(currentTestName, currentTestCoveredElements);
	    }
	    
	    currentTestCoveredElements.add(coveredElement);
	}
	
	public Set<CoveredElement> getCurrentCoveredSequenceFlows() {
		return getCoveredSequenceFlowsForTest(currentTestName);
	}
	
	public Set<CoveredElement> getCurrentCoveredFlowNodes() {
        return getCoveredFlowNodesForTest(currentTestName);
    }
	
	public Set<CoveredElement> getCoveredSequenceFlowsForTest(String testName) {
        return CoveredElements.findCoveredElementsOfType(
                CoveredSequenceFlow.class, testToCoveredElements.get(testName));
    }
	
	public Set<CoveredElement> getCoveredFlowNodesForTest(String testName) {
        return CoveredElements.findCoveredElementsOfType(CoveredActivity.class, testToCoveredElements.get(testName));
    }
	
	public Set<CoveredElement> getGlobalCoveredSequenceFlows() {
        return CoveredElements.findCoveredElementsOfType(CoveredSequenceFlow.class, getGlobalCoveredElements());
    }
    
    public Set<CoveredElement> getGlobalCoveredFlowNodes() {
        return CoveredElements.findCoveredElementsOfType(CoveredActivity.class, getGlobalCoveredElements());
    }
	
	public Set<String> findCoveredActivityIdsForTest(String testName) {
		return CoverageMappings.mapElementsToIds(getCoveredFlowNodesForTest(testName));
	}
	
	public Set<String> findGlobalCoveredActivityIds() {
        return CoverageMappings.mapElementsToIds(getGlobalCoveredFlowNodes());
    }
	
	private Set<CoveredElement> getGlobalCoveredElements() {
	    
	    final HashSet<CoveredElement> flatSet = new HashSet<CoveredElement>();
	    for (Set<CoveredElement> elements : testToCoveredElements.values()) {
	        flatSet.addAll(elements);
	    }
	    
	    return flatSet;
	}
	
    public Map<String, Set<ProcessDefinition>> getDeployments() {
        return deployments;
    }

    public String getCurrentTestName() {
        return currentTestName;
    }

    public void setCurrentTestName(String currentTestName) {
        this.currentTestName = currentTestName;
    }

}
