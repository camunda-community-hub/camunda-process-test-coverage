package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.camunda.bpm.engine.ProcessEngine;

import java.util.Arrays;

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

    private final TestCoverageProcessEngineRule rule;

    private TestCoverageProcessEngineRuleBuilder() {
        this.rule = new TestCoverageProcessEngineRule();
    }

    private TestCoverageProcessEngineRuleBuilder(ProcessEngine processEngine) {
        this.rule = new TestCoverageProcessEngineRule(processEngine);
    }

    /**
     * Creates a TestCoverageProcessEngineRuleBuilder with the default class
     * coverage assertion property activated.
     *
     * @return
     */
    public static TestCoverageProcessEngineRuleBuilder create() {
        return createBase().optionalAssertCoverageAtLeastProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY);
    }

    /**
     * Creates a TestCoverageProcessEngineRuleBuilder with the default class
     * coverage assertion property activated.
     *
     * @return
     */
    public static TestCoverageProcessEngineRuleBuilder create(ProcessEngine processEngine) {
        return createBase(processEngine).optionalAssertCoverageAtLeastProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY);
    }

    /**
     * Set the system property name for minimal class coverage assertion.
     * 
     * @param key
     *            System property name.
     * @return
     */
    public TestCoverageProcessEngineRuleBuilder optionalAssertCoverageAtLeastProperty(String key) {

        String assertAtLeast = System.getProperty(key);
        if (assertAtLeast != null) {
            try {

                final MinimalCoverageMatcher minimalCoverageMatcher = new MinimalCoverageMatcher(
                        Double.parseDouble(assertAtLeast));
                rule.addClassCoverageAssertionMatcher(minimalCoverageMatcher);

            } catch (NumberFormatException e) {
                throw new RuntimeException("BAD TEST CONFIGURATION: optionalAssertCoverageAtLeastProperty( \"" + key
                        + "\" ) must be double");
            }
        }
        return this;
    }

    /** @return a basic builder with nothing preconfigured */
    public static TestCoverageProcessEngineRuleBuilder createBase() {
        return new TestCoverageProcessEngineRuleBuilder();
    }

    /** @return a basic builder with nothing preconfigured */
    public static TestCoverageProcessEngineRuleBuilder createBase(ProcessEngine processEngine) {
        return new TestCoverageProcessEngineRuleBuilder(processEngine);
    }

    /**
     * Enables detailed logging of individual class and method coverage objects.
     * 
     * @return
     */
    public TestCoverageProcessEngineRuleBuilder withDetailedCoverageLogging() {
        rule.setDetailedCoverageLogging(true);
        return this;
    }

    /**
     * Asserts if the class coverage is greater than the passed percentage.
     * 
     * @param percentage
     * @return
     */
    public TestCoverageProcessEngineRuleBuilder assertClassCoverageAtLeast(double percentage) {

        if (0 > percentage || percentage > 1) {
            throw new RuntimeException(
                    "BAD TEST CONFIGURATION: coverageAtLeast " + percentage + " (" + 100 * percentage + "%) ");
        }

        rule.addClassCoverageAssertionMatcher(new MinimalCoverageMatcher(percentage));
        return this;

    }

    public TestCoverageProcessEngineRuleBuilder excludeProcessDefinitionKeys(String... processDefinitionKeys) {
        rule.setExcludedProcessDefinitionKeys(Arrays.asList(processDefinitionKeys));
        return this;
    }

    /**
     * Builds the coverage rule.
     * 
     * @return
     */
    public TestCoverageProcessEngineRule build() {
        return rule;
    }
}
