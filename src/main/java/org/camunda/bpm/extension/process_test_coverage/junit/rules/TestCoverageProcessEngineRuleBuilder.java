package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

/**
 * Fluent Builder for TestCoverageProcessEngineRule.
 *
 */
public class TestCoverageProcessEngineRuleBuilder {

    /** 
     * If you set this property to a ratio (e.g. "1.0" for full coverage), 
     * the @ClassRule will fail the test run if the coverage is less.<br>
     * Example parameter for running java:<br>
     * <code>-Dorg.camunda.bpm.extension.process_test_coverage.ASSERT_AT_LEAST=1.0</code>
     */
    public static final String DEFAULT_ASSERT_AT_LEAST_PROPERTY = "org.camunda.bpm.extension.process_test_coverage.ASSERT_AT_LEAST";
    
	TestCoverageProcessEngineRule rule = new TestCoverageProcessEngineRule();
	
	/** @return a builder with typical method @Rule configuration */
	public static TestCoverageProcessEngineRuleBuilder create() {
        return createBase()
                .optionalAssertCoverageAtLeastProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY);
    }

    public TestCoverageProcessEngineRuleBuilder optionalAssertCoverageAtLeastProperty(String key) {
        
        String assertAtLeast = System.getProperty(key);
        if (assertAtLeast != null) {
            try{ 
                
                final MinimalCoverageMatcher minimalCoverageMatcher = new MinimalCoverageMatcher(Double.parseDouble(assertAtLeast));
                rule.addClassCoverageAssertionMatcher(minimalCoverageMatcher);
                
            }catch(NumberFormatException e) {
                throw new RuntimeException("BAD TEST CONFIGURATION: optionalAssertCoverageAtLeastProperty( \"" + key + "\" ) must be double");
            }
        }
        return this;
    }

	/** @return a builder with typical method @ClassRule configuration */
	public static TestCoverageProcessEngineRuleBuilder createClassRule() {
		return createBase().reportCoverageAfter()
                .optionalAssertCoverageAtLeastProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY);
	}

	/** @return a basic builder with nothing preconfigured */
	public static TestCoverageProcessEngineRuleBuilder createBase() {
	    return new TestCoverageProcessEngineRuleBuilder();
	}

	public TestCoverageProcessEngineRuleBuilder reportCoverageAfter() {
		rule.setReportCoverageWhenFinished(true);
		return this;
	}

	public TestCoverageProcessEngineRuleBuilder assertGlobalCoverageAtLeast(double percentage) {
		
	    if (0 > percentage || percentage > 1) {
			throw new RuntimeException("BAD TEST CONFIGURATION: coverageAtLeast " + percentage + " (" + 100*percentage + "%) ");
		}
		
		rule.addClassCoverageAssertionMatcher(new MinimalCoverageMatcher(percentage));
		return this;
		
	}

	public TestCoverageProcessEngineRule build() {
		return rule;
	}
}
