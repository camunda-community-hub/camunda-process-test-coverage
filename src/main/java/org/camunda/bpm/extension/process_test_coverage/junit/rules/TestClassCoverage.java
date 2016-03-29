package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.Coverage;
import org.camunda.bpm.extension.process_test_coverage.ProcessCoverage;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredSequenceFlow;
import org.camunda.bpm.extension.process_test_coverage.trace.TestMethodCoverage;
import org.camunda.bpm.extension.process_test_coverage.util.CoveredElementComparator;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.junit.Assert;

/**
 * Test class coverage model. The class coverage is an aggregation of all test method coverages.
 * 
 * @author okicir
 *
 */
public class TestClassCoverage implements Coverage {
    
    /**
     * Map connecting the test method to the test method run coverage.
     */
    private Map<String, TestMethodCoverage> testToDeploymentCoverage = new HashMap<String, TestMethodCoverage>();

    /**
     * Adds a covered element to the test method coverage.
     * 
     * @param testName
     * @param coveredElement
     */
    public void addCoveredElement(String testName, CoveredElement coveredElement) {
        testToDeploymentCoverage.get(testName).addCoveredElement(coveredElement);
    }
    
    /**
     * Retrieves a test methods coverage.
     * 
     * @param testName The name of the test method.
     * @return
     */
    public TestMethodCoverage getTestMethodCoverage(String testName) {
        return testToDeploymentCoverage.get(testName);
    }
    
    /**
     * Add a test method coverage to the class coverage.
     * 
     * @param testName
     * @param testCoverage
     */
    public void addTestMethodCoverage(String testName, TestMethodCoverage testCoverage) {
        testToDeploymentCoverage.put(testName, testCoverage);
    }
    
    /**
     * Retrieves the class coverage percentage.
     * All covered test methods' elements are aggregated and checked against the 
     * process definition elements.
     * 
     * @return The coverage percentage.
     */
    public double getCoveragePercentage() {
        
        // All deployments must be the same, so we take the first one
        final TestMethodCoverage anyDeployment = getAnyDeployment();
        
        final Set<FlowNode> definitionsFlowNodes = anyDeployment.getProcessDefinitionsFlowNodes();
        final Set<SequenceFlow> definitionsSeqenceFlows = anyDeployment.getProcessDefinitionsSequenceFlows();
        
        final Set<CoveredActivity> coveredFlowNodes = getCoveredFlowNodes();
        final Set<CoveredSequenceFlow> coveredSequenceFlows = getCoveredSequenceFlows();
        
        final double bpmnElementsCount = definitionsFlowNodes.size() + definitionsSeqenceFlows.size();
        final double coveredElementsCount = coveredFlowNodes.size() + coveredSequenceFlows.size();
        
        return coveredElementsCount / bpmnElementsCount;
    }
    
    public Set<CoveredActivity> getCoveredFlowNodes() {

        final Set<CoveredActivity> coveredFlowNodes = new TreeSet<CoveredActivity>(CoveredElementComparator.instance());

        for (TestMethodCoverage deploymentCoverage : testToDeploymentCoverage.values()) {

            coveredFlowNodes.addAll(deploymentCoverage.getCoveredFlowNodes());

        }

        return coveredFlowNodes;
    }
    
    public Set<String> getCoveredFlowNodeIds(String processDefinitionKey) {
        
        final Set<String> coveredFlowNodeIds = new HashSet<String>();
        for (TestMethodCoverage methodCoverage : testToDeploymentCoverage.values()) {

            coveredFlowNodeIds.addAll(methodCoverage.getCoveredFlowNodeIds(processDefinitionKey));
        }

        return coveredFlowNodeIds;
    }
    
    public Set<CoveredSequenceFlow> getCoveredSequenceFlows() {

        final Set<CoveredSequenceFlow> coveredSequenceFlows = new TreeSet<CoveredSequenceFlow>(CoveredElementComparator.instance());

        for (TestMethodCoverage deploymentCoverage : testToDeploymentCoverage.values()) {

            coveredSequenceFlows.addAll(deploymentCoverage.getCoveredSequenceFlows());

        }

        return coveredSequenceFlows;
    }
    
    public Set<String> getCoveredSequenceFlowIds(String processDefinitionKey) {
        
        final Set<String> coveredSequenceFlowIds = new HashSet<String>();
        for (TestMethodCoverage methodCoverage : testToDeploymentCoverage.values()) {
            
            coveredSequenceFlowIds.addAll(methodCoverage.getCoveredSequenceFlowIds(processDefinitionKey));
        }
        
        return coveredSequenceFlowIds;
    }
    
    public Set<ProcessCoverage> getProcessCoverages() {
        
        final Set<ProcessCoverage> classCoverages = new HashSet<ProcessCoverage>();
        for (TestMethodCoverage deploymentCoverage : testToDeploymentCoverage.values()) {
            
            final Collection<ProcessCoverage> processCoverages = deploymentCoverage.getProcessCoverages();
            classCoverages.addAll(processCoverages);
            
        }
        
        return classCoverages;
    }
    
    public Set<ProcessDefinition> getProcessDefinitions() {
        return getAnyDeployment().getProcessDefinitions();
    }
    
    private TestMethodCoverage getAnyDeployment() {
        
        // All deployments must be the same, so we take the first one
        final TestMethodCoverage anyDeployment = testToDeploymentCoverage.values().iterator().next();
        return anyDeployment;
    }    
    
    public void assertAllDeploymentsEqual() {
        
        Set<ProcessDefinition> processDefinitions = null;
        for (TestMethodCoverage deploymentCoverage : testToDeploymentCoverage.values()) {
            
            Set<ProcessDefinition> deploymentProcessDefinitions = deploymentCoverage.getProcessDefinitions();
            
            if (processDefinitions == null) {
                processDefinitions = deploymentProcessDefinitions;
            }
                
            // TODO check resource names instead of set equality with some unknown comparator
            Assert.assertEquals("Class coverage can only be calculated if all test deploy the same process definition", 
                    processDefinitions, deploymentProcessDefinitions);
            
        }
        
    }
    
}