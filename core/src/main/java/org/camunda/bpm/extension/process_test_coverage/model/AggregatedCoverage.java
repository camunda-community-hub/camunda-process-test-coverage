package org.camunda.bpm.extension.process_test_coverage.model;

import java.util.Set;

import org.camunda.bpm.engine.repository.ProcessDefinition;

/**
 * A coverage that may have multiple deployed process definitions.
 * 
 * @author z0rbas
 *
 */
public interface AggregatedCoverage {

    /**
     * Retrieves covered flow node IDs for the given process definition key.
     */
    Set<String> getCoveredFlowNodeIds(String processDefinitionKey);

    /**
     * Retrieves covered flow nodes for the given process definition key.
     */
    Set<CoveredFlowNode> getCoveredFlowNodes(String processDefinitionKey);

    /**
     * Retrieves covered sequence flow IDs for the given process definition key.
     */
    Set<String> getCoveredSequenceFlowIds(String processDefinitionKey);

    /**
     * Retrieves covered sequence flow IDs for the given process definition key.
     */
    Set<CoveredSequenceFlow> getCoveredSequenceFlows(String processDefinitionKey);

    /**
     * Retrieves the process definitions of the coverage.
     */
    Set<ProcessDefinition> getProcessDefinitions();

    /**
     * Retrives the coverage percentage for all process definitions.
     */
    double getCoveragePercentage();

    /**
     * Retrieves the coverage percentage for the given process definition key.
     */
    double getCoveragePercentage(String processDefinitionKey);
}
