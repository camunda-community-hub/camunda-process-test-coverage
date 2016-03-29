package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.CoverageBuilder;
import org.camunda.bpm.extension.process_test_coverage.ProcessCoverage;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.TestMethodCoverage;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

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
    private TestClassCoverage classCoverage = new TestClassCoverage();
    
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
	    
	    final TestMethodCoverage testCoverage = new TestMethodCoverage(deploymentId);
	    for (ProcessDefinition processDefinition : processDefinitions) {
	        
	        final String processDefinitionId = processDefinition.getId();
	        
	        // Get the BPMN model elements
	        
	        BpmnModelInstance modelInstance = processEngine.getRepositoryService().getBpmnModelInstance(processDefinitionId);
	        Set<FlowNode> flowNodes = new HashSet<FlowNode>(modelInstance.getModelElementsByType(FlowNode.class));
	        Set<SequenceFlow> sequenceFlows = new HashSet<SequenceFlow>(modelInstance.getModelElementsByType(SequenceFlow.class));
	        
	        // Construct the pristine coverage object
	        
	        // TODO decide on the builders fate 
	        final ProcessCoverage processCoverage = CoverageBuilder.createFlowNodeCoverage().forProcess(processDefinition)
	            .withDefinitionFlowNodes(flowNodes).withDefinitionSequenceFlows(sequenceFlows).build();
	        
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
	public TestMethodCoverage getTestMethodCoverage(String testName) {
	    return classCoverage.getTestMethodCoverage(testName);
	}
	
	/**
	 * Retrieves the currently executing test method coverage.
	 * 
	 * @return
	 */
	public TestMethodCoverage getCurrentTestMethodCoverage() {
        return classCoverage.getTestMethodCoverage(currentTestName);
    }
	
	/**
	 * Retrieves the class coverage.
	 * 
	 * @return
	 */
	public TestClassCoverage getClassCoverage() {
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
