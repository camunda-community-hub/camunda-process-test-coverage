package org.camunda.bpm.extension.process_test_coverage.trace;

public class CoveredSequenceFlow extends CoveredElement {
	
	// relevant parts
	private String coveredTransitionId;

	public CoveredSequenceFlow(String processDefinitionKey, String coveredTransitionId) {
		this.coveredTransitionId = coveredTransitionId;
		this.processDefinitionKey = processDefinitionKey;
	}

	@Override
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	@Override
	public String getElementId() {
		return coveredTransitionId;
	}

	@Override
	public String toString() {
		return "CoveredSequenceFlow [coveredTransitionId=" + coveredTransitionId + ", processDefinitionKey="
				+ processDefinitionKey + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coveredTransitionId == null) ? 0 : coveredTransitionId.hashCode());
		result = prime * result + ((processDefinitionKey == null) ? 0 : processDefinitionKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoveredSequenceFlow other = (CoveredSequenceFlow) obj;
		if (coveredTransitionId == null) {
			if (other.coveredTransitionId != null)
				return false;
		} else if (!coveredTransitionId.equals(other.coveredTransitionId))
			return false;
		if (processDefinitionKey == null) {
			if (other.processDefinitionKey != null)
				return false;
		} else if (!processDefinitionKey.equals(other.processDefinitionKey))
			return false;
		return true;
	}

}
