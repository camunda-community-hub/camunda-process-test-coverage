package org.camunda.bpm.extension.process_test_coverage.trace.builderapi;

import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElementBuilder;

/**
 * Interface for restricting functionality to the current role of
 * {@link CoveredElementBuilder}
 */
public interface CoveredElementWithProcessDefinitionId {
	public FinishedBuilder withCurrentTransitionId(String currentTransitionId);
	public FinishedBuilder withActivityId(String activityId);
}

