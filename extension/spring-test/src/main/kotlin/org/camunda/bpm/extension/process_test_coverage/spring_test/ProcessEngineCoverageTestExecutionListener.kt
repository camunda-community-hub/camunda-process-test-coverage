package org.camunda.bpm.extension.process_test_coverage.spring_test

import mu.KLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Condition
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.extension.process_test_coverage.engine.CompensationEventCoverageHandler
import org.camunda.bpm.extension.process_test_coverage.engine.ElementCoverageParseListener
import org.camunda.bpm.extension.process_test_coverage.engine.ExecutionContextModelProvider
import org.camunda.bpm.extension.process_test_coverage.model.DefaultCollector
import org.camunda.bpm.extension.process_test_coverage.model.Run
import org.camunda.bpm.extension.process_test_coverage.model.Suite
import org.camunda.bpm.extension.process_test_coverage.util.CoverageReportUtil
import org.springframework.core.Ordered
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

/**
 * Extension for JUnit 5 which allows the tracking of coverage information for Camunda BPM process tests.
 * Based on the ProcessEngineExtension from the camunda-bpm-junit5 from the camunda community.
 * https://github.com/camunda-community-hub/camunda-bpm-junit5
 *
 * @author Jan Rohwer
 */
class ProcessEngineCoverageTestExecutionListener : TestExecutionListener, Ordered {

    companion object : KLogging() {
        /**
         * If you set this property to a ratio (e.g. "1.0" for full coverage),
         * the Extension will fail the test run if the coverage is less.<br></br>
         * Example parameter for running java:<br></br>
         * `-Dorg.camunda.bpm.extension.process_test_coverage.ASSERT_AT_LEAST=1.0`
         */
        const val DEFAULT_ASSERT_AT_LEAST_PROPERTY = "org.camunda.bpm.extension.process_test_coverage.ASSERT_AT_LEAST"
    }

    /**
     * The state of the current run (class and current method).
     */
    private val coverageCollector = DefaultCollector(ExecutionContextModelProvider())

    /**
     * Conditions to be asserted on the class coverage percentage.
     */
    private val classCoverageAssertionConditions: MutableList<Condition<Double>> = mutableListOf()

    private lateinit var processEngineCoverageProperties: ProcessEngineCoverageProperties

    private var suiteInitialized = false

    private fun loadConfiguration(testContext: TestContext) {
        processEngineCoverageProperties = testContext.applicationContext.getBean(ProcessEngineCoverageProperties::class.java)
        processEngineCoverageProperties.coverageAtLeast()?.let { addClassCoverageAtLeast(it) }
        System.getProperty(processEngineCoverageProperties.optionalAssertCoverageAtLeastProperty())?.let {
            try {
                addClassCoverageAtLeast(it.toDouble())
            } catch (e: NumberFormatException) {
                throw RuntimeException("BAD TEST CONFIGURATION: system property \"${processEngineCoverageProperties.optionalAssertCoverageAtLeastProperty()}\" must be double")
            }
        }
    }

    override fun getOrder() = Integer.MAX_VALUE

    /**
     * Handles creating the run if a relevant test method is called.
     */
    override fun beforeTestMethod(testContext: TestContext) {
        if (!suiteInitialized) {
            initializeSuite(testContext)
        }
        // method name is set only on test methods (not on classes or suites)
        val runId: String = testContext.testMethod.name
        coverageCollector.createRun(Run(runId, testContext.testMethod.name), coverageCollector.activeSuite.id)
        coverageCollector.activateRun(runId)
    }

    /**
     * Handles evaluating the test method coverage after a relevant test method is finished.
     */
    override fun afterTestMethod(testContext: TestContext) {
        if (processEngineCoverageProperties.handleTestMethodCoverage()) {
            handleTestMethodCoverage(testContext)
        }
    }

    /**
     * Initializes the suite for all upcoming tests.
     */
    override fun beforeTestClass(testContext: TestContext) {
        loadConfiguration(testContext)
        initializeListeners(testContext)
        initializeSuite(testContext)
    }

    private fun initializeSuite(testContext: TestContext) {
        val suiteId: String = testContext.testClass.name
        coverageCollector.createSuite(Suite(suiteId, testContext.testClass.name))
        coverageCollector.setExcludedProcessDefinitionKeys(processEngineCoverageProperties.excludedProcessDefinitionKeys())
        coverageCollector.activateSuite(suiteId)
        suiteInitialized = true
    }

    /**
     * If the extension is registered on the class level, log and assert the coverage and create a
     * graphical report. For the class coverage to work all the test method
     * deployments have to be equal.
     */
    override fun afterTestClass(testContext: TestContext) {
        val suite = coverageCollector.activeSuite

        // Make sure the class coverage deals with the same deployments for
        // every test method
        // classCoverage.assertAllDeploymentsEqual();
        val suiteCoveragePercentage = suite.calculateCoverage(coverageCollector.getModels())

        // Log coverage percentage
        logger.info("${suite.name} test class coverage is: $suiteCoveragePercentage")
        logCoverageDetail(suite)

        assertCoverage(suiteCoveragePercentage, classCoverageAssertionConditions)

        // Create graphical report
        CoverageReportUtil.createReport(coverageCollector)
        CoverageReportUtil.createJsonReport(coverageCollector)
    }

    /**
     * Sets the test run state for the coverage listeners. logging.
     * {@see ProcessCoverageInMemProcessEngineConfiguration}
     */
    private fun initializeListeners(testContext: TestContext) {
        val processEngineConfiguration = getProcessEngine(testContext).processEngineConfiguration as ProcessEngineConfigurationImpl
        val bpmnParseListeners = processEngineConfiguration.customPostBPMNParseListeners
        for (parseListener in bpmnParseListeners) {
            if (parseListener is ElementCoverageParseListener) {
                parseListener.setCoverageState(coverageCollector)
            }
        }

        // Compensation event handler
        val compensationEventHandler = processEngineConfiguration.getEventHandler("compensate")
        if (compensationEventHandler is CompensationEventCoverageHandler) {
            compensationEventHandler.setCoverageState(coverageCollector)
        } else {
            logger.warn("CompensationEventCoverageHandler not registered with process engine configuration!"
                    + " Compensation boundary events coverage will not be registered.")
        }
    }

    private fun getProcessEngine(testContext: TestContext) = testContext.applicationContext.getBean(ProcessEngine::class.java)

    /**
     * Logs and asserts the test method coverage and creates a graphical report.
     *
     * @param testContext test context
     */
    private fun handleTestMethodCoverage(testContext: TestContext) {
        val suite = coverageCollector.activeSuite
        val run = suite.getRun(testContext.testMethod.name) ?: return
        val coveragePercentage = run.calculateCoverage(coverageCollector.getModels())

        // Log coverage percentage
        logger.info("${run.name} test method coverage is $coveragePercentage")
        logCoverageDetail(run)

        processEngineCoverageProperties.testMethodCoverageConditions()[run.name]?.let {
            assertCoverage(coveragePercentage, it)
        }
    }

    /**
     * Logs the string representation of the passed suite object.
     */
    private fun logCoverageDetail(suite: Suite) {
        if (logger.isDebugEnabled && processEngineCoverageProperties.detailedCoverageLogging()) {
            logger.debug(suite.toString())
        }
    }

    /**
     * Logs the string representation of the passed run object.
     */
    private fun logCoverageDetail(run: Run) {
        if (logger.isDebugEnabled && processEngineCoverageProperties.detailedCoverageLogging()) {
            logger.debug(run.toString())
        }
    }

    private fun assertCoverage(coverage: Double, conditions: List<Condition<Double>>) {
        conditions.forEach { Assertions.assertThat(coverage).satisfies(it) }
    }

    private fun addClassCoverageAtLeast(percentage: Double) {
        percentage.checkPercentage()
        classCoverageAssertionConditions.add(
                Condition<Double>({ p -> p >= percentage }, "matches if the coverage ratio is at least $percentage")
        )
    }

}

fun Double.checkPercentage() =
        if (0 > this || this > 1) {
            throw RuntimeException(
                    "BAD TEST CONFIGURATION: coverageAtLeast " + this + " (" + 100 * this + "%) ")
        } else this