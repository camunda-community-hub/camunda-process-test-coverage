package org.camunda.bpm.extension.process_test_coverage.trace;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.Coverage;
import org.camunda.bpm.extension.process_test_coverage.ProcessCoverage;
import org.camunda.bpm.extension.process_test_coverage.util.CoveredElementComparator;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class TestMethodCoverage implements Coverage {
    
    private String deploymentId;
    
    private Map<String, ProcessCoverage> processDefinitionKeyToProcessCoverage = new HashMap<String, ProcessCoverage>();
    
    public TestMethodCoverage(String deploymentId) {
        this.deploymentId = deploymentId;
    }
    
    public void addProcessCoverage(ProcessCoverage processCoverage) {
        final String processDefinitionId = processCoverage.getProcessDefinitionKey();
        processDefinitionKeyToProcessCoverage.put(processDefinitionId, processCoverage);
    }
    
    public void addCoveredElement(CoveredElement element) {

        final String processDefinitionKey = element.getProcessDefinitionKey();
        final ProcessCoverage processCoverage = processDefinitionKeyToProcessCoverage.get(processDefinitionKey);
        
        processCoverage.addCoveredElement(element);
    }

    public double getCoveragePercentage() {
        
        final Set<CoveredElement> deploymentCoveredFlowNodes = new HashSet<CoveredElement>();
        final Set<FlowNode> deploymentDefinitionsFlowNodes = new HashSet<FlowNode>();
        
        final Set<CoveredElement> deploymentCoveredSequenceFlows = new HashSet<CoveredElement>();
        final Set<SequenceFlow> deploymentDefinitionsSequenceFlows = new HashSet<SequenceFlow>();
        
        // Collect defined and covered elements from all definitions in the deployment
        for (ProcessCoverage processCoverage : processDefinitionKeyToProcessCoverage.values()) {
            
            final Set<CoveredActivity> coveredFlowNodes = processCoverage.getCoveredFlowNodes();
            deploymentCoveredFlowNodes.addAll(coveredFlowNodes);
            
            final Collection<FlowNode> definitionFlowNodes = processCoverage.getDefinitionFlowNodes();
            deploymentDefinitionsFlowNodes.addAll(definitionFlowNodes);
            
            final Set<CoveredSequenceFlow> coveredSequenceFlows = processCoverage.getCoveredSequenceFlows();
            deploymentCoveredSequenceFlows.addAll(coveredSequenceFlows);
            
            final Collection<SequenceFlow> definitionSequenceFlows = processCoverage.getDefinitionSequenceFlows();
            deploymentDefinitionsSequenceFlows.addAll(definitionSequenceFlows);
            
        }
        
        final double coveragePercentage = getCoveragePercentage(
                deploymentCoveredFlowNodes, deploymentDefinitionsFlowNodes, 
                deploymentCoveredSequenceFlows, deploymentDefinitionsSequenceFlows);
        
        return coveragePercentage;
    }
    
    private double getCoveragePercentage(Set<CoveredElement> deploymentCoveredFlowNodes, Set<FlowNode> deploymentDefinitionsFlowNodes,
            Set<CoveredElement> deploymentCoveredSequenceFlows, Set<SequenceFlow> deploymentDefinitionsSequenceFlows) {
        
        final int numberOfDefinedElements = deploymentDefinitionsFlowNodes.size() + deploymentDefinitionsSequenceFlows.size();
        final int numberOfCoveredElemenets = deploymentCoveredFlowNodes.size() + deploymentCoveredSequenceFlows.size();
        
        return (double) numberOfCoveredElemenets / (double) numberOfDefinedElements;
        
    }

    
    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public void setProcessDefinitionIdToProcessCoverage(Map<String, ProcessCoverage> processDefinitionIdToProcessCoverage) {
        this.processDefinitionKeyToProcessCoverage = processDefinitionIdToProcessCoverage;
    }
    
    public Set<FlowNode> getProcessDefinitionsFlowNodes() {
        
        final Set<FlowNode> flowNodes = new HashSet<FlowNode>();
        for (ProcessCoverage processCoverage : processDefinitionKeyToProcessCoverage.values()) {
            
            final Set<FlowNode> definitionFlowNodes = processCoverage.getDefinitionFlowNodes();
            flowNodes.addAll(definitionFlowNodes);
            
        }
        
        return flowNodes;
    }
    
    public Set<SequenceFlow> getProcessDefinitionsSequenceFlows() {
        
        final Set<SequenceFlow> sequenceFlows = new HashSet<SequenceFlow>();
        for (ProcessCoverage processCoverage : processDefinitionKeyToProcessCoverage.values()) {
            
            final Set<SequenceFlow> definitionSequenceFlows = processCoverage.getDefinitionSequenceFlows();
            sequenceFlows.addAll(definitionSequenceFlows);
            
        }
        
        return sequenceFlows;
    }
    
    public Set<CoveredActivity> getCoveredFlowNodes() {
        
        final Set<CoveredActivity> flowNodes = new TreeSet<CoveredActivity>(CoveredElementComparator.instance());
        for (ProcessCoverage processCoverage : processDefinitionKeyToProcessCoverage.values()) {
            
            final Set<CoveredActivity> definitionFlowNodes = processCoverage.getCoveredFlowNodes();
            flowNodes.addAll(definitionFlowNodes);
            
        }
        
        return flowNodes;
    }
    
    public Set<CoveredSequenceFlow> getCoveredSequenceFlows() {
        
        final Set<CoveredSequenceFlow> sequenceFlows = new TreeSet<CoveredSequenceFlow>(CoveredElementComparator.instance());
        for (ProcessCoverage processCoverage : processDefinitionKeyToProcessCoverage.values()) {
            
            final Set<CoveredSequenceFlow> definitionSequenceFlows = processCoverage.getCoveredSequenceFlows();
            sequenceFlows.addAll(definitionSequenceFlows);
            
        }
        
        return sequenceFlows;
    }
    
    public Set<String> getCoveredFlowNodeIds(String processDefinitionKey) {
        
        final ProcessCoverage processCoverage = processDefinitionKeyToProcessCoverage.get(processDefinitionKey);
        return processCoverage.getCoveredFlowNodeIds();
    }
    
    public Set<String> getCoveredSequenceFlowIds(String processKey) {
        
        final ProcessCoverage processCoverage = processDefinitionKeyToProcessCoverage.get(processKey);
        return processCoverage.getCoveredSequenceFlowIds();
    }

    @Override
    public String toString() {
        
        StringBuilder builder = new StringBuilder();
        builder.append("Deployment ID: ");
        builder.append(deploymentId);
        builder.append("\nDeployment process definitions:\n");
        
        // List of string process coverage string representations
        for (ProcessCoverage processCoverage : processDefinitionKeyToProcessCoverage.values()) {
            builder.append(processCoverage);
            builder.append('\n');
        }
        
        return builder.toString();
    }

    public Set<ProcessDefinition> getProcessDefinitions() {
        
        final Set<ProcessDefinition> processDefinitions = new TreeSet<ProcessDefinition>(
                new Comparator<ProcessDefinition>() {

                    @Override
                    public int compare(ProcessDefinition o1, ProcessDefinition o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });
        for (ProcessCoverage processCoverage : processDefinitionKeyToProcessCoverage.values()) {
            processDefinitions.add(processCoverage.getProcessDefinition());
        }
     
        return processDefinitions;
    }
    
    public Collection<ProcessCoverage> getProcessCoverages() {
        return processDefinitionKeyToProcessCoverage.values();
    }
}
