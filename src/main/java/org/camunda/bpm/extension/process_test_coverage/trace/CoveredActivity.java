package org.camunda.bpm.extension.process_test_coverage.trace;

public class CoveredActivity implements CoveredElement {

	private final String activityId;
	private final String processDefinitionId;

	public CoveredActivity(String processDefinitionId, String activityId) {
		this.activityId = activityId;
		this.processDefinitionId = processDefinitionId;
	}

	@Override
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	@Override
	public String getElementId() {
		return activityId;
	}

	@Override
	public String toString() {
		return "CoveredActivity [activityId=" + activityId + ", processDefinitionId=" + processDefinitionId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityId == null) ? 0 : activityId.hashCode());
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
		CoveredActivity other = (CoveredActivity) obj;
		if (activityId == null) {
			if (other.activityId != null)
				return false;
		} else if (!activityId.equals(other.activityId))
			return false;
		if (processDefinitionId == null) {
			if (other.processDefinitionId != null)
				return false;
		} else if (!processDefinitionId.equals(other.processDefinitionId))
			return false;
		return true;
	}

}
