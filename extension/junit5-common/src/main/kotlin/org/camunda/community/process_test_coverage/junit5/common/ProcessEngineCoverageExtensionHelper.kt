package org.camunda.community.process_test_coverage.junit5.common

import mu.KLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Condition
import org.camunda.community.process_test_coverage.core.model.DefaultCollector
import org.camunda.community.process_test_coverage.core.model.Run
import org.camunda.community.process_test_coverage.core.model.Suite
import org.camunda.community.process_test_coverage.report.CoverageReportUtil
import org.junit.jupiter.api.extension.ExtensionContext

class ProcessEngineCoverageExtensionHelper(
    private val coverageCollector: DefaultCollector,
    /**
     * Log class and test method coverages?
     */
    private val detailedCoverageLogging: Boolean = false,
    /**
     * Is method coverage handling needed?
     */
    private val handleTestMethodCoverage: Boolean = true,
    /**
     * A list of process definition keys excluded from the test run.
     */
    private val excludedProcessDefinitionKeys: List<String> = listOf(),
    /**
     * Conditions to be asserted on the class coverage percentage.
     */
    private val classCoverageAssertionConditions: MutableList<Condition<Double>> = mutableListOf(),

    /**
     * Conditions to be asserted on the individual test method coverages.
     */
    private val testMethodNameToCoverageConditions: MutableMap<String, MutableList<Condition<Double>>> = mutableMapOf()

) {

    companion object : KLogging() {
        private const val KEY_SUITE_CONTEXT_ID = "SUITE_CONTEXT_ID"
    }

    private var suiteInitialized = false

    fun beforeAll(context: ExtensionContext) {
        if (!suiteInitialized || (context.uniqueId != context.getActiveSuiteContextId()) && !isNested(context)) {
            initializeSuite(context, context.displayName)
        }
    }

    fun afterAll(context: ExtensionContext) {
        if (context.uniqueId == context.getActiveSuiteContextId()) {
            val suite = coverageCollector.activeSuite

            // only generate report and coverage if the current context is the one, that started the suite

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

            assertCoverage(suiteCoveragePercentage, classCoverageAssertionConditions)

        }
    }

    fun beforeTestExecution(context: ExtensionContext) {
        if (!suiteInitialized) {
            initializeSuite(context, context.requiredTestClass.name)
        }
        // method name is set only on test methods (not on classes or suites)
        val runId: String = context.uniqueId
        coverageCollector.createRun(Run(runId, context.displayName), coverageCollector.activeSuite.id)
        coverageCollector.activateRun(runId)

    }

    fun afterTestExecution(context: ExtensionContext) {
        if (handleTestMethodCoverage) {
            handleTestMethodCoverage(context)
        }
    }

    private fun isNested(context: ExtensionContext) = context.parent.map { it.uniqueId == context.getActiveSuiteContextId() }.orElse(false)

    private fun initializeSuite(context: ExtensionContext, name: String) {
        val suiteId = context.requiredTestClass.name
        coverageCollector.createSuite(Suite(suiteId, name))
        coverageCollector.setExcludedProcessDefinitionKeys(excludedProcessDefinitionKeys)
        coverageCollector.activateSuite(suiteId)
        context.setActiveSuiteContextId()
        suiteInitialized = true
    }

    private fun ExtensionContext.setActiveSuiteContextId() {
        getStore(this.root).put(KEY_SUITE_CONTEXT_ID, this.uniqueId)
    }

    private fun ExtensionContext.getActiveSuiteContextId() =
        getStore(this.root).get(KEY_SUITE_CONTEXT_ID)

    private fun getStore(context: ExtensionContext): ExtensionContext.Store {
        return context.getStore(ExtensionContext.Namespace.create(javaClass, context.uniqueId))
    }

    /**
     * Logs the string representation of the passed suite object.
     */
    private fun logCoverageDetail(suite: Suite) {
        if (logger.isDebugEnabled && detailedCoverageLogging) {
            logger.debug(suite.toString())
        }
    }

    /**
     * Logs the string representation of the passed run object.
     */
    private fun logCoverageDetail(run: Run) {
        if (logger.isDebugEnabled && detailedCoverageLogging) {
            logger.debug(run.toString())
        }
    }

    private fun assertCoverage(coverage: Double, conditions: List<Condition<Double>>) {
        conditions.forEach { Assertions.assertThat(coverage).satisfies(it) }
    }

    /**
     * Logs and asserts the test method coverage and creates a graphical report.
     *
     * @param context extension context
     */
    private fun handleTestMethodCoverage(context: ExtensionContext) {
        val suite = coverageCollector.activeSuite
        val run = suite.getRun(context.uniqueId) ?: return
        val coveragePercentage = run.calculateCoverage(coverageCollector.getModels())

        // Log coverage percentage
        logger.info("${run.name} test method coverage is $coveragePercentage")
        logCoverageDetail(run)

        testMethodNameToCoverageConditions[run.name]?.let {
            assertCoverage(coveragePercentage, it)
        }
    }

}