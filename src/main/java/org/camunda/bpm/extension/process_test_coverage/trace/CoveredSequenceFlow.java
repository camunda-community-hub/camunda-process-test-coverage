package org.camunda.bpm.extension.process_test_coverage.trace;

import java.util.Date;

public class CoveredSequenceFlow extends CoveredElement {

	private Date timestamp;
	
	// relevant parts
	private String coveredTransitionId;

	public CoveredSequenceFlow(String processDefinitionId, String coveredTransitionId) {
		this.timestamp = new Date();
		this.coveredTransitionId = coveredTransitionId;
		this.processDefinitionId = processDefinitionId;
	}

	@Override
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	@Override
	public String getElementId() {
		return coveredTransitionId;
	}

	@Override
	public String toString() {
		return "CoveredSequenceFlow [coveredTransitionId=" + coveredTransitionId + ", processDefinitionId="
				+ processDefinitionId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coveredTransitionId == null) ? 0 : coveredTransitionId.hashCode());
		result = prime * result + ((processDefinitionId == null) ? 0 : processDefinitionId.hashCode());
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
		if (processDefinitionId == null) {
			if (other.processDefinitionId != null)
				return false;
		} else if (!processDefinitionId.equals(other.processDefinitionId))
			return false;
		return true;
	}

}
