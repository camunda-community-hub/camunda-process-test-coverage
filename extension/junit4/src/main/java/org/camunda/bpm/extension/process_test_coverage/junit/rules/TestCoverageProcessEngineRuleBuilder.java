package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.camunda.bpm.engine.ProcessEngine;

import java.util.Arrays;

/**
 * Fluent Builder for TestCoverageProcessEngineRule.
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

    private TestCoverageProcessEngineRuleBuilder(final ProcessEngine processEngine) {
        this.rule = new TestCoverageProcessEngineRule(processEngine);
    }

    /**
     * Creates a TestCoverageProcessEngineRuleBuilder with the default class
     * coverage assertion property activated.
     */
    public static TestCoverageProcessEngineRuleBuilder create() {
        return createBase().optionalAssertCoverageAtLeastProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY);
    }

    /**
     * Creates a TestCoverageProcessEngineRuleBuilder with the default class
     * coverage assertion property activated.
     */
    public static TestCoverageProcessEngineRuleBuilder create(final ProcessEngine processEngine) {
        return createBase(processEngine).optionalAssertCoverageAtLeastProperty(DEFAULT_ASSERT_AT_LEAST_PROPERTY);
    }

    /**
     * Set the system property name for minimal class coverage assertion.
     *
     * @param key System property name.
     */
    public TestCoverageProcessEngineRuleBuilder optionalAssertCoverageAtLeastProperty(final String key) {

        final String assertAtLeast = System.getProperty(key);
        if (assertAtLeast != null) {
            try {

                final MinimalCoverageMatcher minimalCoverageMatcher = new MinimalCoverageMatcher(
                        Double.parseDouble(assertAtLeast));
                this.rule.addClassCoverageAssertionMatcher(minimalCoverageMatcher);

            } catch (final NumberFormatException e) {
                throw new RuntimeException("BAD TEST CONFIGURATION: optionalAssertCoverageAtLeastProperty( \"" + key
                        + "\" ) must be double");
            }
        }
        return this;
    }

    /**
     * @return a basic builder with nothing preconfigured
     */
    public static TestCoverageProcessEngineRuleBuilder createBase() {
        return new TestCoverageProcessEngineRuleBuilder();
    }

    /**
     * @return a basic builder with nothing preconfigured
     */
    public static TestCoverageProcessEngineRuleBuilder createBase(final ProcessEngine processEngine) {
        return new TestCoverageProcessEngineRuleBuilder(processEngine);
    }

    /**
     * Enables detailed logging of individual class and method coverage objects.
     */
    public TestCoverageProcessEngineRuleBuilder withDetailedCoverageLogging() {
        this.rule.setDetailedCoverageLogging(true);
        return this;
    }

    /**
     * Configures whenever test method coverage handling is needed.
     *
     * @param needHandleTestMethodCoverage boolean
     */
    public TestCoverageProcessEngineRuleBuilder handleTestMethodCoverage(final boolean needHandleTestMethodCoverage) {
        this.rule.setHandleTestMethodCoverage(needHandleTestMethodCoverage);
        return this;
    }

    /**
     * Asserts if the class coverage is greater than the passed percentage.
     *
     * @param percentage
     */
    public TestCoverageProcessEngineRuleBuilder assertClassCoverageAtLeast(final double percentage) {

        if (0 > percentage || percentage > 1) {
            throw new RuntimeException(
                    "BAD TEST CONFIGURATION: coverageAtLeast " + percentage + " (" + 100 * percentage + "%) ");
        }

        this.rule.addClassCoverageAssertionMatcher(new MinimalCoverageMatcher(percentage));
        return this;

    }

    public TestCoverageProcessEngineRuleBuilder excludeProcessDefinitionKeys(final String... processDefinitionKeys) {
        this.rule.setExcludedProcessDefinitionKeys(Arrays.asList(processDefinitionKeys));
        return this;
    }

    /**
     * Builds the coverage rule.
     */
    public TestCoverageProcessEngineRule build() {
        return this.rule;
    }
}
