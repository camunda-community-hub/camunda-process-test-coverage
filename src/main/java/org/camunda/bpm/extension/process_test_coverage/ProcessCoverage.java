package org.camunda.bpm.extension.process_test_coverage;

import java.text.MessageFormat;
import java.util.HashSet;
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
    
    private final static String TOSTRING_TEMPLATE = "Coverage [description='{0}', processDefinitionId='{1}', " +
            "coverage={2} ({3}/{4}), flowNodes=({5}/{6}), sequenceFlows=({7}/{8}), " + 
            "coveredActivityIds={9}, expectedFlowNodes={10}]";
    
    private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

	String description;
	
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
	                new Object[]{ element.getProcessDefinitionKey(), element.getElementId() });
	    }
	    
	}

	@Override
	public String toString() {
	    
	    return MessageFormat.format(TOSTRING_TEMPLATE, description, getProcessDefinitionId(),
	            getCoveragePercentage(), getNumberOfAllCovered(), getNumberOfAllDefined(), // All
	            coveredFlowNodes.size(), definitionFlowNodes.size(),  // Flow nodes
	            coveredSequenceFlows.size(), definitionSequenceFlows.size()); // Sequence flows
	}
	
	public double getCoveragePercentage() {
	    return ((double) getNumberOfAllCovered()) / ((double) getNumberOfAllDefined());
	}

	public int getNumberOfAllCovered() {
		return coveredFlowNodes.size() + coveredSequenceFlows.size();
	}

	public int getNumberOfAllDefined() {
		return definitionFlowNodes.size() + definitionSequenceFlows.size();
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
	
}
