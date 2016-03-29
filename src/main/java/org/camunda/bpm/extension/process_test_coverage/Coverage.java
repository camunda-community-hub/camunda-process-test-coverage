package org.camunda.bpm.extension.process_test_coverage;

import java.util.Set;

import org.camunda.bpm.engine.repository.ProcessDefinition;

public interface Coverage {
    
    Set<String> getCoveredFlowNodeIds(String processDefinitionKey);
    
    Set<String> getCoveredSequenceFlowIds(String processDefinitionKey);
    
    Set<ProcessDefinition> getProcessDefinitions();
    
    double getCoveragePercentage();

}
