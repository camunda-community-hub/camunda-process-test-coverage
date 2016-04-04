package org.camunda.bpm.extension.process_test_coverage.model;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

/**
 * Coverage of a process definition.
 * 
 * @author z0rbas
 *
 */
public class ProcessCoverage {

    private final static String TOSTRING_TEMPLATE = "ProcessCoverage [processDefinitionId=''{0}'', " +
            "coverage={1} ({2}/{3}), flowNodes=({4}/{5}), sequenceFlows=({6}/{7}), " +
            "coveredActivityIds={8}, definitionFlowNodes={9}]";

    private static final Logger logger = Logger.getLogger(ProcessCoverage.class.getCanonicalName());

    /**
     * The process definition being covered.
     */
    private ProcessDefinition processDefinition;

    /**
     * Covered flow nodes.
     */
    private Set<CoveredActivity> coveredFlowNodes = new HashSet<CoveredActivity>();

    /**
     * Flow nodes of the process definition.
     */
    private Set<FlowNode> definitionFlowNodes;

    /**
     * Covered sequence flows.
     */
    private Set<CoveredSequenceFlow> coveredSequenceFlows = new HashSet<CoveredSequenceFlow>();

    /**
     * Sequence flows of the process definition.
     */
    private Set<SequenceFlow> definitionSequenceFlows;

    /**
     * Constructor assembling a pristine process coverage object from the
     * process definition and BPMN model information retrieved from the process
     * engine.
     * 
     * @param processEngine
     * @param processDefinition
     */
    public ProcessCoverage(ProcessEngine processEngine, ProcessDefinition processDefinition) {

        this.processDefinition = processDefinition;

        final BpmnModelInstance modelInstance = processEngine.getRepositoryService().getBpmnModelInstance(
                getProcessDefinitionId());

        definitionFlowNodes = new HashSet<FlowNode>(modelInstance.getModelElementsByType(FlowNode.class));
        definitionSequenceFlows = new HashSet<SequenceFlow>(modelInstance.getModelElementsByType(SequenceFlow.class));

    }

    /**
     * Adds a covered element to the coverage.
     * 
     * @param element
     */
    public void addCoveredElement(CoveredElement element) {

        if (element instanceof CoveredActivity) {

            coveredFlowNodes.add((CoveredActivity) element);

        } else if (element instanceof CoveredSequenceFlow) {

            coveredSequenceFlows.add((CoveredSequenceFlow) element);

        } else {
            logger.log(Level.SEVERE,
                    "Attempted adding unsupported element to process coverage. Process definition ID: {0} Element ID: {1}",
                    new Object[] { element.getProcessDefinitionKey(), element.getElementId() });
        }

    }

    /**
     * Retrieves the coverage percentage for all elements.
     * 
     * @return
     */
    public double getCoveragePercentage() {
        return ((double) getNumberOfAllCovered()) / ((double) getNumberOfAllDefined());
    }

    /**
     * Retrieves the process definitions flow nodes.
     * 
     * @return
     */
    public Set<FlowNode> getDefinitionFlowNodes() {
        return definitionFlowNodes;
    }

    public Set<SequenceFlow> getDefinitionSequenceFlows() {
        return definitionSequenceFlows;
    }

    public Set<CoveredActivity> getCoveredFlowNodes() {
        return coveredFlowNodes;
    }

    public Set<String> getCoveredFlowNodeIds() {

        final Set<String> coveredFlowNodeIds = new HashSet<String>();
        for (CoveredActivity activity : coveredFlowNodes) {

            coveredFlowNodeIds.add(activity.getElementId());
        }

        return coveredFlowNodeIds;
    }
    
    private Set<String> getDefinitionSequenceFlowIds() {
    	
    	final Set<String> definitionFlowNodeIds = new HashSet<String>();
        for (FlowNode activity : definitionFlowNodes) {

            definitionFlowNodeIds.add(activity.getId());
        }

        return definitionFlowNodeIds;
    }

    public Set<CoveredSequenceFlow> getCoveredSequenceFlows() {
        return coveredSequenceFlows;
    }

    public Set<String> getCoveredSequenceFlowIds() {

        final Set<String> sequenceFlowIds = new HashSet<String>();
        for (CoveredSequenceFlow sequenceFlow : coveredSequenceFlows) {

            sequenceFlowIds.add(sequenceFlow.getElementId());
        }

        return sequenceFlowIds;
    }

    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public String getProcessDefinitionId() {
        return processDefinition.getId();
    }

    public String getProcessDefinitionKey() {
        return processDefinition.getKey();
    }

    @Override
    public String toString() {

        return MessageFormat.format(TOSTRING_TEMPLATE,
                getProcessDefinitionId(),
                getCoveragePercentage(),
                getNumberOfAllCovered(),
                getNumberOfAllDefined(), // All
                coveredFlowNodes.size(), // Flow nodes
                definitionFlowNodes.size(),
                coveredSequenceFlows.size(), // Sequence flows
                definitionSequenceFlows.size(),
                getCoveredFlowNodeIds(), // IDs
                getDefinitionSequenceFlowIds());
    }
    
    /**
     * Retrieves the number of covered flow node and sequence flow elements.
     * 
     * @return
     */
    private int getNumberOfAllCovered() {
        return coveredFlowNodes.size() + coveredSequenceFlows.size();
    }

    /**
     * Retrieves the number of flow node and sequence flow elements for the
     * process definition.
     * 
     * @return
     */
    private int getNumberOfAllDefined() {
        return definitionFlowNodes.size() + definitionSequenceFlows.size();
    }

}
