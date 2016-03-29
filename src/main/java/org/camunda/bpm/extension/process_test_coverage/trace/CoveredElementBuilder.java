package org.camunda.bpm.extension.process_test_coverage.trace;

import org.camunda.bpm.extension.process_test_coverage.trace.builderapi.CoveredElementWithProcessDefinitionKey;
import org.camunda.bpm.extension.process_test_coverage.trace.builderapi.FinishedBuilder;

/**
 * A builder for {@link CoveredElement} instances. A fluent API via interfaces is provided.
 */
public class CoveredElementBuilder implements CoveredElementWithProcessDefinitionKey, FinishedBuilder {

	/** Entry point to building a {@link CoveredElement} */
	public static CoveredElementWithProcessDefinitionKey createTrace(String processDefinitionKey) {
		return new CoveredElementBuilder(processDefinitionKey);
	}

	/* Implementation */
	private String activityId;
	private String currentTransitionId;
	private String processDefinitionKey;
	
	private CoveredElementBuilder(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public CoveredElementBuilder withCurrentTransitionId(String currentTransitionId) {
		this.currentTransitionId = currentTransitionId; 
		return this;
	}

	public CoveredElementBuilder withActivityId(String activityId) {
		this.activityId = activityId; 
		return this;
	}

	public CoveredElement build() {
		if (activityId != null ) {
			return new CoveredActivity(processDefinitionKey, activityId);
		}
		if (currentTransitionId != null ) {
			return new CoveredSequenceFlow(processDefinitionKey, currentTransitionId);
		}
		throw new IllegalStateException("Badly configured builder. That is impossible.");
	}


}
