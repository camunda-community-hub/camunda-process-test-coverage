package org.camunda.community.process_test_coverage.junit4.platform7.rules;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.community.process_test_coverage.engine.platform7.ExecutionContextModelProvider;
import org.camunda.community.process_test_coverage.engine.platform7.ProcessEngineAdapter;
import org.camunda.community.process_test_coverage.core.model.DefaultCollector;
import org.camunda.community.process_test_coverage.core.model.Run;
import org.camunda.community.process_test_coverage.core.model.Suite;
import org.camunda.community.process_test_coverage.report.CoverageReportUtil;
import org.hamcrest.Matcher;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Rule handling the process test coverage for individual test methods and the
 * whole class. Every coverage can be logged and asserted. Graphical reports can
 * be generated for individual test method runs and for the class.
 * <p>
 * With the @Rule annotation the individual test method coverages are
 * calculated, asserted and reported. With the @ClassRule annotation the
 * individual test method coverages are summarized, asserted and reported.
 * <p>
 * This rule cannot be used as a @ClassRule without the @Rule annotation.
 *
 * @author grossax
 * @author z0rbas
 */
public class TestCoverageProcessEngineRule extends ProcessEngineRule {

    private static final Logger logger = Logger.getLogger(TestCoverageProcessEngineRule.class.getCanonicalName());

    /**
     * The state of the current run (class and current method).
     */
    private final DefaultCollector coverageCollector = new DefaultCollector(new ExecutionContextModelProvider());

    /**
     * Controls run state initialization.
     * {@see #initializeClassRunState(Description)}
     */
    private final AtomicBoolean firstRun = new AtomicBoolean(true);

    /**
     * Log class and test method coverages?
     */
    private boolean detailedCoverageLogging = false;

    /**
     * Is method coverage handling needed?
     */
    private boolean handleTestMethodCoverage = true;

    /**
     * Matchers to be asserted on the class coverage percentage.
     */
    private final Collection<Matcher<Double>> classCoverageAssertionMatchers = new LinkedList<>();

    /**
     * Matchers to be asserted on the individual test method coverages.
     */
    private final Map<String, Collection<Matcher<Double>>> testMethodNameToCoverageMatchers = new HashMap<>();

    /**
     * A list of process definition keys excluded from the test run.
     */
    private List<String> excludedProcessDefinitionKeys = Collections.emptyList();

    TestCoverageProcessEngineRule() {
        super();
    }

    TestCoverageProcessEngineRule(final ProcessEngine processEngine) {
        super(processEngine);
    }

    /**
     * Adds an assertion for a test method's coverage percentage.
     *
     * @param testMethodName name of the test method.
     * @param matcher        coverage matcher map
     */
    public void addTestMethodCoverageAssertionMatcher(final String testMethodName, final Matcher<Double> matcher) {
        final Collection<Matcher<Double>> matchers = this.testMethodNameToCoverageMatchers.computeIfAbsent(testMethodName, k -> new LinkedList<>());
        matchers.add(matcher);
    }

    /**
     * Adds an assertion for the class coverage percentage.
     *
     * @param matcher coverage matcher
     */
    public void addClassCoverageAssertionMatcher(final MinimalCoverageMatcher matcher) {
        this.classCoverageAssertionMatchers.add(matcher);
    }

    public void setExcludedProcessDefinitionKeys(final List<String> excludedProcessDefinitionKeys) {
        this.excludedProcessDefinitionKeys = excludedProcessDefinitionKeys;
    }

    @Override
    public void starting(final Description description) {

        // make sure the rule is used correctly
        this.validateClassRuleAnnotations(description);

        if (this.processEngine == null) {
            super.initializeProcessEngine();
        }
        // initialize the run state for class
        this.initializeSuite(description);
        // let the process engine rule identify the deployed process
        super.starting(description);

        if (this.isRelevantTestMethod(description)) {
            // initialize the run state for method
            this.initializeRun(description);
        }
    }

    @Override
    public void finished(final Description description) {

        // only apply for test methods annotated directly or contained in the class annotated with @Deployment.
        if (this.handleTestMethodCoverage && this.isRelevantTestMethod(description)) {
            this.handleTestMethodCoverage(description);
        }

        // If the rule is a class rule get the class coverage
        if (!description.isTest()) {
            this.handleClassCoverage();
        }

        // run derived finalization only of not used as a class rule
        if (this.processEngineConfiguration != null) {
            super.finished(description);
        }

    }

    /**
     * Validates the annotation of the rule field in the test class.
     *
     * @param description test description
     */
    private void validateClassRuleAnnotations(final Description description) {

        // If the first run is a @ClassRule run, check if @Rule is annotated
        if (!description.isTest() && this.firstRun.get()) {

            /*
             * Get the fields of the test class and check if there is only one
             * coverage rule and if the coverage rule field is annotation with
             * both @ClassRule and @Rule.
             */

            int numberOfCoverageRules = 0;
            for (final Field field : description.getTestClass().getFields()) {

                final Class<?> fieldType = field.getType();
                if (this.getClass().isAssignableFrom(fieldType)) {

                    ++numberOfCoverageRules;

                    final boolean isClassRule = field.isAnnotationPresent(ClassRule.class);
                    final boolean isRule = field.isAnnotationPresent(Rule.class);
                    if (isClassRule && !isRule) {
                        throw new RuntimeException(this.getClass().getCanonicalName() + " can only be used as a @ClassRule if it is also a @Rule!");
                    }
                }
            }

            // TODO if they really want to have multiple runs, let them?
            if (numberOfCoverageRules > 1) {
                throw new RuntimeException("Only one coverage rule can be used per test class!");
            }
        }
    }

    /**
     * Initialize the coverage run state depending on the rule annotations and
     * notify the state of the current test name.
     *
     * @param description test class description
     */
    private void initializeSuite(final Description description) {
        // Initialize new state once on @ClassRule run or on every individual @Rule run
        if (this.firstRun.compareAndSet(true, false)) {

            final String suiteId = description.getClassName();
            this.coverageCollector.createSuite(new Suite(suiteId, description.getClassName()));
            this.coverageCollector.setExcludedProcessDefinitionKeys(this.excludedProcessDefinitionKeys);
            this.coverageCollector.activateSuite(suiteId);
            new ProcessEngineAdapter(this.processEngine, this.coverageCollector).initializeListeners();
        }
    }

    /**
     * Initialize the coverage run state depending on the rule annotations and
     * notify the state of the current test name.
     *
     * @param description test description
     */
    private void initializeRun(final Description description) {
        // method name is set only on test methods (not on classes or suites)
        final String runId = description.getMethodName();
        this.coverageCollector.createRun(new Run(runId, description.getMethodName()), this.coverageCollector.getActiveSuite().getId());
        this.coverageCollector.activateRun(runId);
    }

    /**
     * Logs and asserts the test method coverage and creates a graphical report.
     *
     * @param description test description
     */
    private void handleTestMethodCoverage(final Description description) {

        final String testName = description.getMethodName();
        final Suite suite = this.coverageCollector.getActiveSuite();
        final Run run = suite.getRun(testName);

        if (run == null) {
            return;
        }

        final double coveragePercentage = run.calculateCoverage(this.coverageCollector.getModels());

        // Log coverage percentage
        logger.info(testName + " test method coverage is " + coveragePercentage);

        this.logCoverageDetail(run);

        if (this.testMethodNameToCoverageMatchers.containsKey(testName)) {

            this.assertCoverage(coveragePercentage, this.testMethodNameToCoverageMatchers.get(testName));

        }
    }

    /**
     * Determines if the provided Description describes a method relevant for coverage metering. This is the case,
     * if the Description is provided for an atomic test and the test has access to deployed process.
     *
     * @return <code>true</code> if the description is provided for the relevant method.
     */
    private boolean isRelevantTestMethod(final Description description) {
        return description.isTest() // test method
            && (super.deploymentId != null) // deployment is set
            && this.processEngine // the deployed process is not excluded
                                  .getRepositoryService()
                                  .createProcessDefinitionQuery()
                                  .deploymentId(this.deploymentId)
                                  .list()
                                  .stream().anyMatch(it -> !this.isExcluded(it));
    }

    /**
     * If the rule is a @ClassRule log and assert the coverage and create a
     * graphical report. For the class coverage to work all the test method
     * deployments have to be equal.
     */
    private void handleClassCoverage() {

        final Suite suite = this.coverageCollector.getActiveSuite();

        // Make sure the class coverage deals with the same deployments for
        // every test method
        // classCoverage.assertAllDeploymentsEqual();

        final double suiteCoveragePercentage = suite.calculateCoverage(this.coverageCollector.getModels());

        // Log coverage percentage
        logger.info(suite.getName() + " test class coverage is: " + suiteCoveragePercentage);

        this.logCoverageDetail(suite);

        // Create graphical report
        CoverageReportUtil.createReport(this.coverageCollector);
        CoverageReportUtil.createJsonReport(this.coverageCollector);

        this.assertCoverage(suiteCoveragePercentage, this.classCoverageAssertionMatchers);

    }

    private void assertCoverage(final double coverage, final Collection<Matcher<Double>> matchers) {
        for (final Matcher<Double> matcher : matchers) {
            assertThat(coverage, matcher);
        }
    }

    /**
     * Logs the string representation of the passed suite object.
     */
    private void logCoverageDetail(final Suite suite) {

        if (logger.isLoggable(Level.FINE) || this.isDetailedCoverageLogging()) {
            logger.log(Level.INFO, suite.toString());
        }
    }

    /**
     * Logs the string representation of the passed run object.
     */
    private void logCoverageDetail(final Run run) {

        if (logger.isLoggable(Level.FINE) || this.isDetailedCoverageLogging()) {
            logger.log(Level.INFO, run.toString());
        }
    }

    private boolean isExcluded(final ProcessDefinition processDefinition) {
        if (this.excludedProcessDefinitionKeys != null) {
            return this.excludedProcessDefinitionKeys.contains(processDefinition.getKey());
        }
        return false;
    }

    public boolean isDetailedCoverageLogging() {
        return this.detailedCoverageLogging;
    }

    public void setDetailedCoverageLogging(final boolean detailedCoverageLogging) {
        this.detailedCoverageLogging = detailedCoverageLogging;
    }

    public void setHandleTestMethodCoverage(final boolean handleTestMethodCoverage) {
        this.handleTestMethodCoverage = handleTestMethodCoverage;
    }

    @Override
    public org.junit.runners.model.Statement apply(final org.junit.runners.model.Statement base, final Description description) {
        return super.apply(base, description);
    }

    @Override
    protected void succeeded(final Description description) {
        super.succeeded(description);
        logger.info(description.getDisplayName() + " succeeded.");
    }

    @Override
    protected void failed(final Throwable e, final Description description) {
        super.failed(e, description);
        logger.info(description.getDisplayName() + " failed.");
    }

}
