package org.camunda.bpm.extension.process_test_coverage.model;

/**
 * A sequence flow covered by a test.
 * 
 * @author z0rbas
 */
public class CoveredSequenceFlow extends CoveredElement {
	
    /**
     * The ID of the sequence flow.
     */
	private String transitionId;

	public CoveredSequenceFlow(String processDefinitionKey, String transitionId) {
		this.transitionId = transitionId;
		this.processDefinitionKey = processDefinitionKey;
	}

	@Override
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	@Override
	public String getElementId() {
		return transitionId;
	}

	@Override
	public String toString() {
		return "CoveredSequenceFlow [transitionId=" + transitionId + ", processDefinitionKey="
				+ processDefinitionKey + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transitionId == null) ? 0 : transitionId.hashCode());
		result = prime * result + ((processDefinitionKey == null) ? 0 : processDefinitionKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
	    
	    /*
	     * A covered sequence flow is equal to another if the process definition key
	     * and transition ID are equal.
	     */
	    
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		CoveredSequenceFlow other = (CoveredSequenceFlow) obj;
		
		if (transitionId == null) {
		    
			if (other.transitionId != null)
				return false;
			
		} else if (!transitionId.equals(other.transitionId))
		    return false;
		
		
		if (processDefinitionKey == null) {
		    
			if (other.processDefinitionKey != null)
				return false;
			
		} else if (!processDefinitionKey.equals(other.processDefinitionKey))
			return false;
		
		return true;
	}

}
