package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

public class TestCoverageProcessEngineRuleBuilder {

	TestCoverageProcessEngineRule rule = new TestCoverageProcessEngineRule();
	
	public static TestCoverageProcessEngineRuleBuilder create() {
		return new TestCoverageProcessEngineRuleBuilder();
	}

	public static TestCoverageProcessEngineRuleBuilder createClassRule() {
		return create().startWithFreshFlowTrace().reportCoverageAfter();
	}
	
	/** will delete/ignore flow trace information from previous runs */
	public TestCoverageProcessEngineRuleBuilder startWithFreshFlowTrace() {
		rule.resetFlowWhenStarting = true;
		return this;
	}

	public TestCoverageProcessEngineRuleBuilder reportCoverageAfter() {
		rule.reportCoverageWhenFinished = true;
		return this;
	}

	public TestCoverageProcessEngineRuleBuilder assertCoverageAtLeast(double percentage) {
		if (0 > percentage || percentage > 1) {
			throw new RuntimeException("BAD TEST CONFIGURATION: coverageAtLeast " + percentage);
		}
		rule.assertCoverageMatchers.add(new MinimalCoverageMatcher(percentage));
		return this;
		
	}

	public TestCoverageProcessEngineRuleBuilder assertCoverage(Matcher<Double> matcher) {
		rule.assertCoverageMatchers.add( Matchers.describedAs("coverage with %0", matcher, StringDescription.asString(matcher)));
		return this;
	}

	public TestCoverageProcessEngineRule build() {
		return rule;
	}

}
