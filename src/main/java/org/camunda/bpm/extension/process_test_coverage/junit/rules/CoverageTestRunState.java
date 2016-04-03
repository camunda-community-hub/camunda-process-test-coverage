package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.model.ClassCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.model.MethodCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.ProcessCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.ProcessCoverageBuilder;

/**
 * State tracking the current class and method coverage run.
 * 
 * @author grossax
 * @author okicir
 */
public class CoverageTestRunState {
    
	private Logger log = Logger.getLogger(CoverageTestRunState.class.getCanonicalName());
    
	/**
	 * The actual class coverage object.
	 */
    private ClassCoverage classCoverage = new ClassCoverage();
    
    /**
     * The test class name.
     */
    private String className;

    /**
     * The name of the currently executing test method.
     */
    private String currentTestName;
	
    /**
     * Adds the covered element to the current test run coverage.
     * 
     * @param coveredElement
     */
	public void addCoveredElement(/*@NotNull*/ CoveredElement coveredElement) {
	    
	    if (log.isLoggable(Level.FINE)) {
	        log.info("notifyCoveredElement(" + coveredElement + ")");
	    }
	    
	    classCoverage.addCoveredElement(currentTestName, coveredElement);
	    
	}
	
	/** 
	 * Adds a test method to the class coverage.
	 * 
	 * @param processEngine
	 * @param deploymentId The deployment ID of the test method run. (Hint: Every test method run has its own deployment.)
	 * @param processDefinitions The process definitions of the test method deployment.
	 * @param testName The name of the test method.
	 */
	public void addTestMethodRun(ProcessEngine processEngine, String deploymentId, 
	        List<ProcessDefinition> processDefinitions, String testName) {
	    
	    final MethodCoverage testCoverage = new MethodCoverage(deploymentId);
	    for (ProcessDefinition processDefinition : processDefinitions) {
	        
	        // Construct the pristine coverage object
	        
	        // TODO decide on the builders fate 
	        final ProcessCoverage processCoverage = ProcessCoverageBuilder.createFlowNodeAndSequenceFlowCoverage(processEngine)
	                .forProcess(processDefinition).build();
	        
	        testCoverage.addProcessCoverage(processCoverage);
	    }
	    
	    classCoverage.addTestMethodCoverage(testName, testCoverage);
	}
	
	/**
	 * Retrieves the coverage for a test method.
	 * 
	 * @param testName
	 * @return
	 */
	public MethodCoverage getTestMethodCoverage(String testName) {
	    return classCoverage.getTestMethodCoverage(testName);
	}
	
	/**
	 * Retrieves the currently executing test method coverage.
	 * 
	 * @return
	 */
	public MethodCoverage getCurrentTestMethodCoverage() {
        return classCoverage.getTestMethodCoverage(currentTestName);
    }
	
	/**
	 * Retrieves the class coverage.
	 * 
	 * @return
	 */
	public ClassCoverage getClassCoverage() {
	    return classCoverage;
	}
	
	/**
	 * Retrieves the name of the currently executing test method.
	 *  
	 * @return
	 */
    public String getCurrentTestName() {
        return currentTestName;
    }

    /**
     * Sets the name of the currently executing test mehod.
     * 
     * @param currentTestName
     */
    public void setCurrentTestName(String currentTestName) {
        this.currentTestName = currentTestName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
