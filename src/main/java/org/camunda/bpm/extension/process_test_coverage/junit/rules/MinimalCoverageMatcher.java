package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.camunda.bpm.extension.process_test_coverage.model.ProcessCoverage;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class MinimalCoverageMatcher extends BaseMatcher<Double> {

	double minimalCoverage;

	public MinimalCoverageMatcher(double minimalCoveragePercentage) {
		if (0 > minimalCoveragePercentage || minimalCoveragePercentage > 1) {
			throw new RuntimeException("ILLEGAL TEST CONFIGURATION: minimal coverage percentage must be between 0.0 and 1.0 (was "+minimalCoveragePercentage+ ")");
		}
		this.minimalCoverage = minimalCoveragePercentage;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("matches if the coverage ratio is at least ").appendValue(minimalCoverage);
	}

	@Override
	public boolean matches(Object item) {
		return actualPercentage(item) >= minimalCoverage;
	}

	private double actualPercentage(Object item) {
		if (item instanceof ProcessCoverage) {
			return ((ProcessCoverage) item).getCoveragePercentage();
		}
		if (item instanceof Number) {
			return (double) ((Number) item).doubleValue();
		}
		return -1.0;
	}

	@Override
	public void describeMismatch(Object item, Description mismatchDescription) {
		if (item instanceof Number || item instanceof ProcessCoverage) {
			mismatchDescription.appendText("coverage of ").appendValue(actualPercentage(item));
			mismatchDescription.appendText(" is too low)");
			// TODO describe diff of actual and expected items
		} else {
			mismatchDescription.appendValue(item).appendText("is not a Number or Coverage");
		}
		
	}

}
