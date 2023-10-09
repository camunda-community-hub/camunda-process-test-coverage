package org.camunda.community.process_test_coverage.spring_test.platform7

import mu.KLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Condition
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.community.process_test_coverage.core.engine.ExcludeFromProcessCoverage
import org.camunda.community.process_test_coverage.engine.platform7.ExecutionContextModelProvider
import org.camunda.community.process_test_coverage.engine.platform7.ProcessEngineAdapter
import org.camunda.community.process_test_coverage.core.model.DefaultCollector
import org.camunda.community.process_test_coverage.core.model.Run
import org.camunda.community.process_test_coverage.core.model.Suite
import org.camunda.community.process_test_coverage.report.CoverageReportUtil
import org.springframework.core.Ordered
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

/**
 * Test execution listener for process test coverage.
 * Can be used with spring testing framework to get process test coverage in spring tests.
 *
 * @author Jan Rohwer
 */
class ProcessEngineCoverageTestExecutionListener : TestExecutionListener, Ordered {

    companion object : KLogging()

    /**
     * The state of the current run (class and current method).
     */
    private val coverageCollector = DefaultCollector(ExecutionContextModelProvider())

     private lateinit var processEngineCoverageProperties: ProcessEngineCoverageProperties

    private var suiteInitialized = false

    private fun loadConfiguration(testContext: TestContext) {
        processEngineCoverageProperties = testContext.applicationContext.getBean(ProcessEngineCoverageProperties::class.java)
    }

    override fun getOrder() = Integer.MAX_VALUE

    /**
     * Handles creating the run if a relevant test method is called.
     */
    override fun beforeTestMethod(testContext: TestContext) {
        if (!isTestMethodExcluded(testContext)) {
            if (!suiteInitialized) {
                initializeSuite(testContext)
            }
            // method name is set only on test methods (not on classes or suites)
            val runId: String = testContext.testMethod.name
            coverageCollector.createRun(Run(runId, testContext.testMethod.name), coverageCollector.activeSuite.id)
            coverageCollector.activateRun(runId)
        }
    }

    /**
     * Handles evaluating the test method coverage after a relevant test method is finished.
     */
    override fun afterTestMethod(testContext: TestContext) {
        if (!isTestMethodExcluded(testContext) && processEngineCoverageProperties.handleTestMethodCoverage) {
            handleTestMethodCoverage(testContext)
        }
    }

    private fun isTestClassExcluded(testContext: TestContext) =
            testContext.testClass.annotations.any { it is ExcludeFromProcessCoverage }

    private fun isTestMethodExcluded(testContext: TestContext) =
            testContext.testClass.annotations.any { it is ExcludeFromProcessCoverage}
                    || testContext.testMethod.annotations.any { it is ExcludeFromProcessCoverage }


    /**
     * Initializes the suite for all upcoming tests.
     */
    override fun beforeTestClass(testContext: TestContext) {
        if (!isTestClassExcluded(testContext)) {
            loadConfiguration(testContext)
            ProcessEngineAdapter(getProcessEngine(testContext), coverageCollector).initializeListeners()
            initializeSuite(testContext)
        }
    }

    private fun initializeSuite(testContext: TestContext) {
        val suiteId: String = testContext.testClass.name
        coverageCollector.createSuite(Suite(suiteId, testContext.testClass.name))
        coverageCollector.setExcludedProcessDefinitionKeys(processEngineCoverageProperties.excludedProcessDefinitionKeys)
        coverageCollector.activateSuite(suiteId)
        suiteInitialized = true
    }

    /**
     * If the extension is registered on the class level, log and assert the coverage and create a
     * graphical report. For the class coverage to work all the test method
     * deployments have to be equal.
     */
    override fun afterTestClass(testContext: TestContext) {
        if (!isTestClassExcluded(testContext)) {
            val suite = coverageCollector.activeSuite

            // Make sure the class coverage deals with the same deployments for
            // every test method
            // classCoverage.assertAllDeploymentsEqual();
            val suiteCoveragePercentage = suite.calculateCoverage(coverageCollector.getModels())

            // Log coverage percentage
            logger.info("${suite.name} test class coverage is: $suiteCoveragePercentage")
            logCoverageDetail(suite)
            
            // Create graphical report
            CoverageReportUtil.createReport(coverageCollector)
            CoverageReportUtil.createJsonReport(coverageCollector)

            assertCoverage(suiteCoveragePercentage, processEngineCoverageProperties.classCoverageAssertionConditions)
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

        processEngineCoverageProperties.testMethodCoverageConditions[run.name]?.let {
            assertCoverage(coveragePercentage, it)
        }
    }

    /**
     * Logs the string representation of the passed suite object.
     */
    private fun logCoverageDetail(suite: Suite) {
        if (logger.isDebugEnabled && processEngineCoverageProperties.detailedCoverageLogging) {
            logger.debug(suite.toString())
        }
    }

    /**
     * Logs the string representation of the passed run object.
     */
    private fun logCoverageDetail(run: Run) {
        if (logger.isDebugEnabled && processEngineCoverageProperties.detailedCoverageLogging) {
            logger.debug(run.toString())
        }
    }

    private fun assertCoverage(coverage: Double, conditions: List<Condition<Double>>) {
        conditions.forEach { Assertions.assertThat(coverage).satisfies(it) }
    }


}
