package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.event.EventHandler;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.listeners.CompensationEventCoverageHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.FlowNodeHistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.PathCoverageParseListener;
import org.camunda.bpm.extension.process_test_coverage.model.AggregatedCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.ClassCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.model.MethodCoverage;
import org.camunda.bpm.extension.process_test_coverage.util.CoverageReportUtil;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.Description;

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
 *
 */
public class TestCoverageProcessEngineRule extends ProcessEngineRule {

    private static Logger logger = Logger.getLogger(TestCoverageProcessEngineRule.class.getCanonicalName());

    /**
     * The state of the current run (class and current method).
     */
    private CoverageTestRunState coverageTestRunState;

    /**
     * Controls run state initialization.
     * {@see #initializeRunState(Description)}
     */
    private boolean firstRun = true;

    /**
     * Log class and test method coverages?
     */
    private boolean detailedCoverageLogging = false;

    /**
     * Matchers to be asserted on the class coverage percentage.
     */
    private Collection<Matcher<Double>> classCoverageAssertionMatchers = new LinkedList<Matcher<Double>>();

    /**
     * Matchers to be asserted on the individual test method coverages.
     */
    private Map<String, Collection<Matcher<Double>>> testMethodNameToCoverageMatchers = new HashMap<String, Collection<Matcher<Double>>>();

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
     * @param testMethodName
     * @param matcher
     */
    public void addTestMethodCoverageAssertionMatcher(String testMethodName, Matcher<Double> matcher) {

        // JDK7 ifAbsent
        Collection<Matcher<Double>> matchers = testMethodNameToCoverageMatchers.get(testMethodName);
        if (matchers == null) {
            matchers = new LinkedList<Matcher<Double>>();
            testMethodNameToCoverageMatchers.put(testMethodName, matchers);
        }

        matchers.add(matcher);

    }

    /**
     * Adds an assertion for the class coverage percentage.
     * 
     * @param matcher
     */
    public void addClassCoverageAssertionMatcher(MinimalCoverageMatcher matcher) {
        classCoverageAssertionMatchers.add(matcher);
    }

    public void setExcludedProcessDefinitionKeys(List<String> excludedProcessDefinitionKeys) {
        this.excludedProcessDefinitionKeys = excludedProcessDefinitionKeys;
    }

    @Override
    public void starting(Description description) {

        validateRuleAnnotations(description);

        if (processEngine == null) {
            super.initializeProcessEngine();
        }

        initializeRunState(description);

        super.starting(description);

        initializeMethodCoverage(description);
    }

    @Override
    public void finished(Description description) {

        handleTestMethodCoverage(description);

        handleClassCoverage(description);

        // run derived finalization only of not used as a class rule
        if (identityService != null) {
            super.finished(description);
        }

    }

    /**
     * Validates the annotation of the rule field in the test class.
     * 
     * @param description
     */
    private void validateRuleAnnotations(Description description) {

        // If the first run is a @ClassRule run, check if @Rule is annotated
        if (firstRun && !description.isTest()) {

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

                        throw new RuntimeException(getClass().getCanonicalName()
                                + " can only be used as a @ClassRule if it is also a @Rule!");
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
     * @param description
     */
    private void initializeMethodCoverage(Description description) {

        // Not a @ClassRule run and deployments present
        if (deploymentId != null) {

            final List<ProcessDefinition> deployedProcessDefinitions = processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(
                deploymentId).list();

            final List<ProcessDefinition> relevantProcessDefinitions = new ArrayList<ProcessDefinition>();
            for (ProcessDefinition definition: deployedProcessDefinitions) {
                if (!isExcluded(definition)) {
                    relevantProcessDefinitions.add(definition);
                }
            }

            coverageTestRunState.initializeTestMethodCoverage(processEngine,
                    deploymentId,
                    relevantProcessDefinitions,
                    description.getMethodName());

        }
    }

    /**
     * Initialize the coverage run state depending on the rule annotations and
     * notify the state of the current test name.
     * 
     * @param description
     */
    private void initializeRunState(final Description description) {

        // Initialize new state once on @ClassRule run or on every individual
        // @Rule run
        if (firstRun) {

            coverageTestRunState = new CoverageTestRunState();
            coverageTestRunState.setTestClassName(description.getClassName());
            coverageTestRunState.setExcludedProcessDefinitionKeys(excludedProcessDefinitionKeys);

            initializeListenerRunState();

            firstRun = false;
        }

        coverageTestRunState.setCurrentTestMethodName(description.getMethodName());

    }

    /**
     * Sets the test run state for the coverage listeners. logging.
     * {@see ProcessCoverageInMemProcessEngineConfiguration}
     */
    private void initializeListenerRunState() {

        final ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();

        // Configure activities listener

        final FlowNodeHistoryEventHandler historyEventHandler = (FlowNodeHistoryEventHandler) processEngineConfiguration.getHistoryEventHandler();
        historyEventHandler.setCoverageTestRunState(coverageTestRunState);

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
     * @param description
     */
    private void handleTestMethodCoverage(Description description) {

        // Do test method coverage only if deployments present
        final Deployment methodDeploymentAnnotation = description.getAnnotation(Deployment.class);
        final Deployment classDeploymentAnnotation = description.getTestClass().getAnnotation(Deployment.class);
        final boolean testMethodHasDeployment = methodDeploymentAnnotation != null || classDeploymentAnnotation != null;

        if (testMethodHasDeployment) {

            final String testName = description.getMethodName();
            final MethodCoverage testCoverage = coverageTestRunState.getTestMethodCoverage(testName);

            double coveragePercentage = testCoverage.getCoveragePercentage();

            // Log coverage percentage
            logger.info(testName + " test method coverage is " + coveragePercentage);

            logCoverageDetail(testCoverage);

            // Create graphical report
            CoverageReportUtil.createCurrentTestMethodReport(processEngine, coverageTestRunState);

            if (testMethodNameToCoverageMatchers.containsKey(testName)) {

                assertCoverage(coveragePercentage, testMethodNameToCoverageMatchers.get(testName));

            }

        }
    }

    /**
     * If the rule is a @ClassRule log and assert the coverage and create a
     * graphical report. For the class coverage to work all the test method
     * deployments have to be equal.
     * 
     * @param description
     */
    private void handleClassCoverage(Description description) {

        // If the rule is a class rule get the class coverage
        if (!description.isTest()) {

            final ClassCoverage classCoverage = coverageTestRunState.getClassCoverage();

            // Make sure the class coverage deals with the same deployments for
            // every test method
            classCoverage.assertAllDeploymentsEqual();

            final double classCoveragePercentage = classCoverage.getCoveragePercentage();

            // Log coverage percentage
            logger.info(
                    coverageTestRunState.getTestClassName() + " test class coverage is: " + classCoveragePercentage);

            logCoverageDetail(classCoverage);

            // Create graphical report
            CoverageReportUtil.createClassReport(processEngine, coverageTestRunState);

            assertCoverage(classCoveragePercentage, classCoverageAssertionMatchers);

        }
    }

    private void assertCoverage(double coverage, Collection<Matcher<Double>> matchers) {

        for (Matcher<Double> matcher : matchers) {

            Assert.assertThat(coverage, matcher);
        }

    }

    /**
     * Logs the string representation of the passed coverage object.
     * 
     * @param coverage
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

    @Override
    public org.junit.runners.model.Statement apply(org.junit.runners.model.Statement base, Description description) {
        return super.apply(base, description);

    };

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
