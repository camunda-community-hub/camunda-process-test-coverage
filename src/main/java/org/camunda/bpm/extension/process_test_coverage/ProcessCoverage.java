package org.camunda.bpm.extension.process_test_coverage;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredSequenceFlow;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class ProcessCoverage {
    
    private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

	String description;

	String processDefinitionId;
	
	String processDefinitionKey;
	
	ProcessDefinition processDefinition;

	Set<CoveredActivity> coveredFlowNodes = new HashSet<CoveredActivity>();
	
	Set<FlowNode> definitionFlowNodes = new HashSet<FlowNode>();

	// TODO use another collection to take into account multiple flow passes
	Set<CoveredSequenceFlow> coveredSequenceFlows = new HashSet<CoveredSequenceFlow>();

	Set<SequenceFlow> definitionSequenceFlows = new HashSet<SequenceFlow>();
	
	public void addCoveredElement(CoveredElement element) {
	    
	    if (element instanceof CoveredActivity) {
	        
	        coveredFlowNodes.add((CoveredActivity) element);
	        
	    } else if (element instanceof CoveredSequenceFlow) {
	        
	        coveredSequenceFlows.add((CoveredSequenceFlow) element);
	        
	    } else {
	        logger.log(Level.SEVERE, 
	                "Attempted adding unsupported element to process coverage. Process definition ID: {0} Element ID: {1}", 
	                new Object[]{ element.getProcessDefinitionId(), element.getElementId() });
	    }
	    
	}

	@Override
	public String toString() {
		return "Coverage [" + "description=" + description + ", processDefinitionId=" + processDefinitionId + ", " +
				"coverage=" + getActualPercentage()+ " (" + getNumberOfAllActual() + "/" + getNumberOfAllExpected()+ ")" +
				"flowNodes=(" + coveredFlowNodes.size() + "/" + definitionFlowNodes.size() + ")" +
			 "sequenceFlows=(" + coveredSequenceFlows.size() + "/" + definitionSequenceFlows.size()+ ")" +
				", coveredActivityIds=" + coveredFlowNodes + ", expectedFlowNodes=" + definitionFlowNodes + "]";
	}
	
	public double calculateCoveragePercentage() {
	    return ((double) getNumberOfAllActual()) / ((double) getNumberOfAllExpected());
	}

	public int getNumberOfAllActual() {
		return coveredFlowNodes.size() + coveredSequenceFlows.size();
	}

	public int getNumberOfAllExpected() {
		return definitionFlowNodes.size() + definitionSequenceFlows.size();
	}

	public double getActualPercentage() {

		return ((double) getNumberOfAllActual()) / ((double) getNumberOfAllExpected());
	}

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

    public void setCoveredFlowNodes(Set<CoveredActivity> coveredFlowNodes) {
        this.coveredFlowNodes = coveredFlowNodes;
    }

    public Set<CoveredSequenceFlow> getCoveredSequenceFlows() {
        return coveredSequenceFlows;
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
	
    
}
