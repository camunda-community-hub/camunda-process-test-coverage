package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

public class TestCoverageProcessEngineRuleBuilder {

    public static final String DEFAULT_ASSERT_AT_LEAST_PROPERTY = "org.camunda.bpm.extension.process_test_coverage.ASSERT_AT_LEAST";
    
	TestCoverageProcessEngineRule rule = new TestCoverageProcessEngineRule();
	
	/** @return a builder with typical method @Rule configuration */
	public static TestCoverageProcessEngineRuleBuilder create() {
        return createBase()
                .startWithFreshFlowTrace() // makes sure that the methods are independent
                .startWithFreshProcessEngine()
                .runTestsSequentially();
    }

    public TestCoverageProcessEngineRuleBuilder optionalAssertCoverageAtLeastProperty(
            String assertAtLeastProperty) {
        String assertAtLeast = System.getProperty(assertAtLeastProperty);
        if (assertAtLeast != null) {
            try{ 
                rule.assertCoverageMatchers.add( new MinimalCoverageMatcher(Double.parseDouble(assertAtLeast)) );
            }catch(NumberFormatException e) {
                throw new RuntimeException("BAD TEST CONFIGURATION: optionalAssertCoverageAtLeastProperty( \"" + assertAtLeastProperty + "\" ) must be double");
            }
        }
        return this;
    }

    public static ProcessDeploymentRule buildDeployRule() {
        return createBase().runTestsSequentially().build();
    }

	/** @return a builder with typical method @ClassRule configuration */
	public static TestCoverageProcessEngineRuleBuilder createClassRule() {
		return createBase().reportCoverageAfter()
                .optionalAssertCoverageAtLeastProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY)
		        .startWithFreshFlowTrace() // makes sure we dont see traces from other runs
		        .startWithFreshProcessEngine()
		        .runTestsSequentially();
	}

	/** @return a basic builder with nothing preconfigured */
	public static TestCoverageProcessEngineRuleBuilder createBase() {
	    return new TestCoverageProcessEngineRuleBuilder();
	}

	/** will delete/ignore flow trace information from previous runs */
    public TestCoverageProcessEngineRuleBuilder startWithFreshFlowTrace() {
        rule.resetFlowWhenStarting = true;
        return this;
    }
    
    /** will delete/ignore flow trace information from previous runs */
    public TestCoverageProcessEngineRuleBuilder startWithFreshProcessEngine() {
        rule.resetProcessEngineWhenStarting = true;
        return this;
    }
    
    public TestCoverageProcessEngineRuleBuilder runTestsSequentially() {
        rule.runSequentially = true;
        return this;
    }

	public TestCoverageProcessEngineRuleBuilder reportCoverageAfter() {
		rule.reportCoverageWhenFinished = true;
		return this;
	}

	public TestCoverageProcessEngineRuleBuilder assertCoverageAtLeast(double percentage) {
		if (0 > percentage || percentage > 1) {
			throw new RuntimeException("BAD TEST CONFIGURATION: coverageAtLeast " + percentage + " (" + 100*percentage + "%) ");
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
