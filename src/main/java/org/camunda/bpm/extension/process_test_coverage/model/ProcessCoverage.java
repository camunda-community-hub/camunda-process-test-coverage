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

    private final static String TOSTRING_TEMPLATE = "ProcessCoverage [processDefinitionId='{0}', " +
            "coverage={1} ({2}/{3}), flowNodes=({4}/{5}), sequenceFlows=({6}/{7}), " +
            "coveredActivityIds={8}, expectedFlowNodes={9}]";

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
     * Retrieves the number of covered flow node and sequence flow elements.
     * 
     * @return
     */
    public int getNumberOfAllCovered() {
        return coveredFlowNodes.size() + coveredSequenceFlows.size();
    }

    /**
     * Retrieves the number of flow node and sequence flow elements for the
     * process definition.
     * 
     * @return
     */
    public int getNumberOfAllDefined() {
        return definitionFlowNodes.size() + definitionSequenceFlows.size();
    }

    /**
     * Retrieves the process definitions flow nodes.
     * 
     * @return
     */
    public Set<FlowNode> getDefinitionFlowNodes() {
        return definitionFlowNodes;
    }

    public void setDefinitionFlowNodes(Set<FlowNode> definitionFlowNodes) {
        this.definitionFlowNodes = definitionFlowNodes;
    }

    public Set<SequenceFlow> getDefinitionSequenceFlows() {
        return definitionSequenceFlows;
    }

    public void setDefinitionSequenceFlows(Set<SequenceFlow> definitionSequenceFlows) {
        this.definitionSequenceFlows = definitionSequenceFlows;
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

    public void setCoveredFlowNodes(Set<CoveredActivity> coveredFlowNodes) {
        this.coveredFlowNodes = coveredFlowNodes;
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

    public void setCoveredSequenceFlows(Set<CoveredSequenceFlow> coveredSequenceFlows) {
        this.coveredSequenceFlows = coveredSequenceFlows;
    }

    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
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
                coveredFlowNodes.size(),
                definitionFlowNodes.size(), // Flow nodes
                coveredSequenceFlows.size(),
                definitionSequenceFlows.size()); // Sequence flows
    }
}
