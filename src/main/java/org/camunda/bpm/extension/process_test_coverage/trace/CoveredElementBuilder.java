package org.camunda.bpm.extension.process_test_coverage.trace;

import org.camunda.bpm.extension.process_test_coverage.trace.builderapi.CoveredElementWithProcessDefinitionId;
import org.camunda.bpm.extension.process_test_coverage.trace.builderapi.FinishedBuilder;

/**
 * A builder for {@link CoveredElement} instances. A fluent API via interfaces is provided.
 */
public class CoveredElementBuilder implements CoveredElementWithProcessDefinitionId, FinishedBuilder {

	/** Entry point to building a {@link CoveredElement} */
	public static CoveredElementWithProcessDefinitionId createTrace(String processDefinitionId) {
		return new CoveredElementBuilder(processDefinitionId);
	}

	/* Implementation */
	private String activityId;
	private String currentTransitionId;
	private String processDefinitionId;
	
	private CoveredElementBuilder(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
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
			return new CoveredActivity(processDefinitionId, activityId);
		}
		if (currentTransitionId != null ) {
			return new CoveredSequenceFlow(processDefinitionId, currentTransitionId);
		}
		throw new IllegalStateException("Badly configured builder. That is impossible.");
	}


}
