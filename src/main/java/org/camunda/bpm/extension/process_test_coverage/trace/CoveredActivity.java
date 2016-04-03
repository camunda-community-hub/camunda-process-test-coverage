package org.camunda.bpm.extension.process_test_coverage.trace;

/**
 * An activity covered by a test.
 * 
 * @author grossax
 * @author okicir
 *
 */
public class CoveredActivity extends CoveredElement {

    /**
     * Element ID of the activity.
     */
	private final String activityId;

	public CoveredActivity(String processDefinitionKey, String activityId) {
		this.activityId = activityId;
		this.processDefinitionKey = processDefinitionKey;
	}

	@Override
	public String getElementId() {
		return activityId;
	}

	@Override
	public String toString() {
		return "CoveredActivity [activityId=" + activityId + ", processDefinitionKey=" + processDefinitionKey + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityId == null) ? 0 : activityId.hashCode());
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
		CoveredActivity other = (CoveredActivity) obj;
		if (activityId == null) {
			if (other.activityId != null)
				return false;
		} else if (!activityId.equals(other.activityId))
			return false;
		if (processDefinitionKey == null) {
			if (other.processDefinitionKey != null)
				return false;
		} else if (!processDefinitionKey.equals(other.processDefinitionKey))
			return false;
		return true;
	}

}
