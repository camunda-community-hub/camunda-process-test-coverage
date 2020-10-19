package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.event.EventHandler;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.listeners.CompensationEventCoverageHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.FlowNodeHistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.PathCoverageParseListener;
import org.camunda.bpm.extension.process_test_coverage.model.AggregatedCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.ClassCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.MethodCoverage;
import org.camunda.bpm.extension.process_test_coverage.util.Api;
import org.camunda.bpm.extension.process_test_coverage.util.CoverageReportUtil;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.Description;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rule handling the process test coverage for individual test methods and the
 * whole class. Every coverage can be logged and asserted. Graphical reports can
 * be generated for individual test method runs and for the class.
 *
 * With the @Rule annotation the individual test method coverages are
 * calculated, asserted and reported. With the @ClassRule annotation the
 * individual test method coverages are summarized, asserted and reported.
 *
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
    private CoverageTestRunState coverageTestRunState;

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
     * Is class coverage handling needed?
     */
    private boolean handleClassCoverage = true;

    /**
     * coverageTestRunStateFactory. Can be changed for aggregated/suite coverage check
     */
    private CoverageTestRunStateFactory coverageTestRunStateFactory = new DefaultCoverageTestRunStateFactory();

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
    private List<String> excludedProcessDefinitionKeys;

    TestCoverageProcessEngineRule() {
        super();
    }

    TestCoverageProcessEngineRule(ProcessEngine processEngine) {
        super(processEngine);
    }

    /**
     * Adds an assertion for a test method's coverage percentage.
     *
     * @param testMethodName name of the test method.
     * @param matcher        coverage matcher.
     */
    public void addTestMethodCoverageAssertionMatcher(String testMethodName, Matcher<Double> matcher) {
        Collection<Matcher<Double>> matchers = testMethodNameToCoverageMatchers.computeIfAbsent(testMethodName, k -> new LinkedList<>());
        matchers.add(matcher);
    }

    /**
     * Adds an assertion for the class coverage percentage.
     *
     * @param matcher coverage matcher
     */
    public void addClassCoverageAssertionMatcher(MinimalCoverageMatcher matcher) {
        classCoverageAssertionMatchers.add(matcher);
    }

    public void setExcludedProcessDefinitionKeys(List<String> excludedProcessDefinitionKeys) {
        this.excludedProcessDefinitionKeys = excludedProcessDefinitionKeys;
    }

    @Override
    public void starting(Description description) {

        // make sure the rule is used correctly
        validateClassRuleAnnotations(description);

        if (processEngine == null) {
            super.initializeProcessEngine();
        }
        // initialize the run state for class
        initializeClassRunState(description);
        // let the process engine rule identify the deployed process
        super.starting(description);

        if (isRelevantTestMethod(description)) {
            // initialize the run state for method
            initializeMethodRunState(description);
            // init coverage setup for method
            initializeMethodCoverage(description);
        }
    }

    @Override
    public void finished(Description description) {

        // only apply for test methods annotated directly or contained in the class annotated with @Deployment.
        if (handleTestMethodCoverage && isRelevantTestMethod(description)) {
            handleTestMethodCoverage(description);
        }

        // If the rule is a class rule get the class coverage
        if (handleClassCoverage && !description.isTest()) {
            handleClassCoverage(description);
        }

        // run derived finalization only of not used as a class rule
        if (identityService != null) {
            super.finished(description);
        }

    }

    /**
     * Validates the annotation of the rule field in the test class.
     *
     * @param description test description
     */
    private void validateClassRuleAnnotations(Description description) {

        // If the first run is a @ClassRule run, check if @Rule is annotated
        if (!description.isTest() && firstRun.get()) {

            /*
             * Get the fields of the test class and check if there is only one
             * coverage rule and if the coverage rule field is annotation with
             * both @ClassRule and @Rule.
             */

            int numberOfCoverageRules = 0;
            for (Field field : description.getTestClass().getFields()) {

                final Class<?> fieldType = field.getType();
                if (getClass().isAssignableFrom(fieldType)) {

                    ++numberOfCoverageRules;

                    final boolean isClassRule = field.isAnnotationPresent(ClassRule.class);
                    final boolean isRule = field.isAnnotationPresent(Rule.class);
                    if (isClassRule && !isRule) {
                        throw new RuntimeException(getClass().getCanonicalName() + " can only be used as a @ClassRule if it is also a @Rule!");
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
     * Initialize the current test method coverage.
     *
     * @param description test description
     */
    private void initializeMethodCoverage(Description description) {
        final List<ProcessDefinition> deployedProcessDefinitions = processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deploymentId).list();

        final List<ProcessDefinition> relevantProcessDefinitions = new ArrayList<>();
        for (ProcessDefinition definition : deployedProcessDefinitions) {
            if (!isExcluded(definition)) {
                relevantProcessDefinitions.add(definition);
            }
        }
        coverageTestRunState.initializeTestMethodCoverage(
            processEngine,
            deploymentId,
            relevantProcessDefinitions,
            description.getMethodName());

    }

    /**
     * Initialize the coverage run state depending on the rule annotations and
     * notify the state of the current test name.
     *
     * @param description test class description
     */
    private void initializeClassRunState(final Description description) {
        // Initialize new state once on @ClassRule run or on every individual @Rule run
        if (firstRun.compareAndSet(true, false)) {
            coverageTestRunState = coverageTestRunStateFactory.create(description.getClassName(), excludedProcessDefinitionKeys);
            initializeListenerRunState();
        }
    }

    /**
     * Initialize the coverage run state depending on the rule annotations and
     * notify the state of the current test name.
     *
     * @param description test description
     */
    private void initializeMethodRunState(final Description description) {
        // method name is set only on test methods (not on classes or suites)
        coverageTestRunState.setCurrentTestMethodName(description.getMethodName());
    }

    /**
     * Sets the test run state for the coverage listeners. logging.
     * {@see ProcessCoverageInMemProcessEngineConfiguration}
     */
    private void initializeListenerRunState() {

        final ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();

        // Configure activities listener
        HistoryEventHandler historyEventHandler = processEngineConfiguration.getHistoryEventHandler();
        if (historyEventHandler instanceof FlowNodeHistoryEventHandler) {
            ((FlowNodeHistoryEventHandler) historyEventHandler).setCoverageTestRunState(coverageTestRunState);
        }
        if (Api.Camunda.supportsCustomHistoryEventHandlers()) {
            final List<HistoryEventHandler> historyEventHandlers = processEngineConfiguration.getCustomHistoryEventHandlers();
            for (HistoryEventHandler customHistoryEventHandler : historyEventHandlers) {
                if (customHistoryEventHandler instanceof FlowNodeHistoryEventHandler) {
                    ((FlowNodeHistoryEventHandler) customHistoryEventHandler).setCoverageTestRunState(coverageTestRunState);
                }
            }
        }
        // Configure sequence flow listener

        final List<BpmnParseListener> bpmnParseListeners = processEngineConfiguration.getCustomPostBPMNParseListeners();

        for (BpmnParseListener parseListener : bpmnParseListeners) {

            if (parseListener instanceof PathCoverageParseListener) {

                final PathCoverageParseListener listener = (PathCoverageParseListener) parseListener;
                listener.setCoverageTestRunState(coverageTestRunState);
            }
        }

        // Compensation event handler

        final EventHandler compensationEventHandler = processEngineConfiguration.getEventHandler("compensate");
        if (compensationEventHandler != null && compensationEventHandler instanceof CompensationEventCoverageHandler) {

            final CompensationEventCoverageHandler compensationEventCoverageHandler = (CompensationEventCoverageHandler) compensationEventHandler;
            compensationEventCoverageHandler.setCoverageTestRunState(coverageTestRunState);

        } else {
            logger.warning("CompensationEventCoverageHandler not registered with process engine configuration!"
                               + " Compensation boundary events coverage will not be registered.");
        }

    }

    /**
     * Logs and asserts the test method coverage and creates a graphical report.
     *
     * @param description test description
     */
    private void handleTestMethodCoverage(Description description) {

        final String testName = description.getMethodName();
        final MethodCoverage testCoverage = coverageTestRunState.getTestMethodCoverage(testName);

        double coveragePercentage = testCoverage.getCoveragePercentage();

        // Log coverage percentage
        logger.info(testName + " test method coverage is " + coveragePercentage);

        logCoverageDetail(testCoverage);

        // Create graphical report
        CoverageReportUtil.createCurrentTestMethodReport(coverageTestRunState);

        if (testMethodNameToCoverageMatchers.containsKey(testName)) {

            assertCoverage(coveragePercentage, testMethodNameToCoverageMatchers.get(testName));

        }
    }

    /**
     * Determines if the provided Description describes a method relevant for coverage metering. This is the case,
     * if the Description is provided for an atomic test and the test has access to deployed process.
     *
     * @return <code>true</code> if the description is provided for the relevant method.
     */
    private boolean isRelevantTestMethod(Description description) {
        return description.isTest() // test method
            && (super.deploymentId != null) // deployment is set
            && processEngine // the deployed process is not excluded
                             .getRepositoryService()
                             .createProcessDefinitionQuery()
                             .deploymentId(deploymentId)
                             .list()
                             .stream().anyMatch(it -> !isExcluded(it));
    }

    /**
     * If the rule is a @ClassRule log and assert the coverage and create a
     * graphical report. For the class coverage to work all the test method
     * deployments have to be equal.
     *
     * @param description test description
     */
    private void handleClassCoverage(Description description) {

        final ClassCoverage classCoverage = coverageTestRunState.getClassCoverage();

        // Make sure the class coverage deals with the same deployments for
        // every test method
        classCoverage.assertAllDeploymentsEqual();

        final double classCoveragePercentage = classCoverage.getCoveragePercentage();

        // Log coverage percentage
        logger.info(coverageTestRunState.getTestClassName() + " test class coverage is: " + classCoveragePercentage);

        logCoverageDetail(classCoverage);

        // Create graphical report
        CoverageReportUtil.createClassReport(coverageTestRunState);

        assertCoverage(classCoveragePercentage, classCoverageAssertionMatchers);

    }

    private void assertCoverage(double coverage, Collection<Matcher<Double>> matchers) {

        for (Matcher<Double> matcher : matchers) {

            Assert.assertThat(coverage, matcher);
        }

    }

    /**
     * Logs the string representation of the passed coverage object.
     *
     * @param coverage aggregate coverage.
     */
    private void logCoverageDetail(AggregatedCoverage coverage) {

        if (logger.isLoggable(Level.FINE) || isDetailedCoverageLogging()) {
            logger.log(Level.INFO, coverage.toString());
        }

    }

    private boolean isExcluded(ProcessDefinition processDefinition) {
        if (excludedProcessDefinitionKeys != null) {
            return excludedProcessDefinitionKeys.contains(processDefinition.getKey());
        }
        return false;
    }

    public boolean isDetailedCoverageLogging() {
        return detailedCoverageLogging;
    }

    public void setDetailedCoverageLogging(boolean detailedCoverageLogging) {
        this.detailedCoverageLogging = detailedCoverageLogging;
    }

    public void setHandleTestMethodCoverage(boolean handleTestMethodCoverage) {
        this.handleTestMethodCoverage = handleTestMethodCoverage;
    }

    public void setHandleClassCoverage(boolean handleClassCoverage) {
        this.handleClassCoverage = handleClassCoverage;
    }

    public void setCoverageTestRunStateFactory(CoverageTestRunStateFactory coverageTestRunStateFactory) {
        this.coverageTestRunStateFactory = coverageTestRunStateFactory;
    }

    @Override
    public org.junit.runners.model.Statement apply(org.junit.runners.model.Statement base, Description description) {
        return super.apply(base, description);
    }

    @Override
    protected void succeeded(Description description) {
        super.succeeded(description);
        logger.info(description.getDisplayName() + " succeeded.");
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
        logger.info(description.getDisplayName() + " failed.");
    }

}
