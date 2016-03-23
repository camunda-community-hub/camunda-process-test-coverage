package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.CoverageBuilder;
import org.camunda.bpm.extension.process_test_coverage.CoverageMappings;
import org.camunda.bpm.extension.process_test_coverage.ProcessCoverage;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredSequenceFlow;
import org.camunda.bpm.extension.process_test_coverage.trace.DeploymentCoverage;
import org.camunda.bpm.extension.process_test_coverage.util.CoveredElementIdComparator;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.junit.Assert;

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
//    private Map<String, Set<CoveredElement>> testToCoveredElements = new HashMap<String, Set<CoveredElement>>();
	
    private Map<String, DeploymentCoverage> testToDeploymentCoverage = new HashMap<String, DeploymentCoverage>();

    private String currentTestName;
	
	public void notifyCoveredElement(/*@NotNull*/ CoveredElement coveredElement) {
	    
	    if (log.isLoggable(Level.FINE)) {
	        log.info("notifyCoveredElement(" + coveredElement + ")");
	    }
	    
	    final DeploymentCoverage deploymentCoverage = testToDeploymentCoverage.get(currentTestName);
	    deploymentCoverage.addCoveredElement(coveredElement);
	    
	}
	
	public void addDeployment(ProcessEngine processEngine, String deploymentId, 
	        List<ProcessDefinition> processDefinitions, String testName) {
	    
	    final DeploymentCoverage deploymentCoverage = new DeploymentCoverage(deploymentId);
	    for (ProcessDefinition processDefinition : processDefinitions) {
	        
	        final String processDefinitionId = processDefinition.getId();
	        
	        // Get the BPMN model elements
	        BpmnModelInstance modelInstance = processEngine.getRepositoryService().getBpmnModelInstance(processDefinitionId);
	        Set<FlowNode> flowNodes = new HashSet<FlowNode>(modelInstance.getModelElementsByType(FlowNode.class));
	        Set<SequenceFlow> sequenceFlows = new HashSet<SequenceFlow>(modelInstance.getModelElementsByType(SequenceFlow.class));
	        
	        final ProcessCoverage processCoverage = CoverageBuilder.createFlowNodeCoverage().forProcess(processDefinition)
	            .withExpectedFlowNodes(flowNodes).withExpectedSequenceFlows(sequenceFlows).build();
	        
	        deploymentCoverage.getProcessDefinitionIdToProcessCoverage().put(processDefinitionId, processCoverage);
	    }
	    
	    testToDeploymentCoverage.put(testName, deploymentCoverage);
	}
	
	public DeploymentCoverage getDeploymentCoverage(String testName) {
	    return testToDeploymentCoverage.get(testName);
	}
	
	public Set<String> findCoveredActivityIdsForTest(String testName) {
	    
	    final DeploymentCoverage deploymentCoverage = testToDeploymentCoverage.get(testName);
		return CoverageMappings.mapElementsToIds(deploymentCoverage.getCoveredFlowNodes());
		
	}
	
	public Set<String> findGlobalCoveredActivityIds() {
        return CoverageMappings.mapElementsToIds(getClassCoverageFlowNodes());
    }
	
	public Set<ProcessCoverage> getClassProcessCoverages() {
	    
	    final Set<ProcessCoverage> classCoverages = new HashSet<ProcessCoverage>();
	    for (DeploymentCoverage deploymentCoverage : testToDeploymentCoverage.values()) {
	        
	        final Collection<ProcessCoverage> processCoverages = deploymentCoverage.getProcessCoverages();
	        classCoverages.addAll(processCoverages);
	        
	    }
	    
	    return classCoverages;
	}
	
	public double getClassCoverage() {
	    
	    // All deployments must be the same, so we take the first one
	    final DeploymentCoverage anyDeployment = getAnyDeployment();
	    
	    final Set<FlowNode> definitionsFlowNodes = anyDeployment.getProcessDefinitionsFlowNodes();
	    final Set<SequenceFlow> definitionsSeqenceFlows = anyDeployment.getProcessDefinitionsSequenceFlows();
	    
	    final Set<CoveredActivity> coveredFlowNodes = getClassCoverageFlowNodes();
	    final Set<CoveredSequenceFlow> coveredSequenceFlows = getClassCoverageSequenceFlows();
	    
	    final double bpmnElementsCount = definitionsFlowNodes.size() + definitionsSeqenceFlows.size();
	    final double coveredElementsCount = coveredFlowNodes.size() + coveredSequenceFlows.size();
	    
	    return coveredElementsCount / bpmnElementsCount;
	}
	
	public DeploymentCoverage getAnyDeployment() {
	    
	    // All deployments must be the same, so we take the first one
        final DeploymentCoverage anyDeployment = testToDeploymentCoverage.values().iterator().next();
        return anyDeployment;
	}
	
    public Set<CoveredActivity> getClassCoverageFlowNodes() {

        final Set<CoveredActivity> coveredFlowNodes = new TreeSet<CoveredActivity>(CoveredElementIdComparator.instance());

        for (DeploymentCoverage deploymentCoverage : testToDeploymentCoverage.values()) {

            coveredFlowNodes.addAll(deploymentCoverage.getCoveredFlowNodes());

        }

        return coveredFlowNodes;
    }
    
    public Set<CoveredSequenceFlow> getClassCoverageSequenceFlows() {

        final Set<CoveredSequenceFlow> coveredSequenceFlows = new TreeSet<CoveredSequenceFlow>(CoveredElementIdComparator.instance());

        for (DeploymentCoverage deploymentCoverage : testToDeploymentCoverage.values()) {

            coveredSequenceFlows.addAll(deploymentCoverage.getCoveredSequenceFlows());

        }

        return coveredSequenceFlows;
    }
    
    public void assertAllDeploymentsEqual() {
        
        Set<ProcessDefinition> processDefinitions = null;
        for (DeploymentCoverage deploymentCoverage : testToDeploymentCoverage.values()) {
            
            Set<ProcessDefinition> deploymentProcessDefinitions = deploymentCoverage.getProcessDefinitions();
            
            if (processDefinitions == null) {
                processDefinitions = deploymentProcessDefinitions;
            }
                
            // TODO check resource names instead of set equality with some unknown comparator
            Assert.assertEquals("Class coverage can only be calculated if all test deploy the same process definition", 
                    processDefinitions, deploymentProcessDefinitions);
            
        }
        
    }
	
    public String getCurrentTestName() {
        return currentTestName;
    }

    public void setCurrentTestName(String currentTestName) {
        this.currentTestName = currentTestName;
    }

    public Map<String, DeploymentCoverage> getTestToDeploymentCoverage() {
        return testToDeploymentCoverage;
    }

}
