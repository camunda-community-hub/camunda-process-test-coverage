package org.camunda.bpm.extension.process_test_coverage;

import java.util.Set;

import org.camunda.bpm.engine.repository.ProcessDefinition;

public interface Coverage {
    
    /**
     * Retrieves covered flow node IDs for the given process definition key.
     * 
     * @param processDefinitionKey
     * @return
     */
    Set<String> getCoveredFlowNodeIds(String processDefinitionKey);
    
    /**
     * Retrieves covered sequence flow IDs for the given process definition key.
     * 
     * @param processDefinitionKey
     * @return
     */
    Set<String> getCoveredSequenceFlowIds(String processDefinitionKey);
    
    /**
     * Retrieves the process definitions of the coverage.
     * 
     * @return
     */
    Set<ProcessDefinition> getProcessDefinitions();
    
    /**
     * Retrives the coverage percentage for all process definitions.
     * 
     * @return
     */
    double getCoveragePercentage();

}
