package org.camunda.bpm.extension.process_test_coverage.junit5

import io.camunda.zeebe.process.test.assertions.BpmnAssert
import mu.KLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Condition
import org.camunda.bpm.extension.process_test_coverage.engine.ExcludeFromProcessCoverage
import org.camunda.bpm.extension.process_test_coverage.engine.ZeebeModelProvider
import org.camunda.bpm.extension.process_test_coverage.engine.createEvents
import org.camunda.bpm.extension.process_test_coverage.model.DefaultCollector
import org.camunda.bpm.extension.process_test_coverage.model.Run
import org.camunda.bpm.extension.process_test_coverage.model.Suite
import org.camunda.bpm.extension.process_test_coverage.util.CoverageReportUtil
import org.junit.jupiter.api.extension.*

/**
 * Extension for JUnit 5 which allows the tracking of coverage information for Camunda BPM Platform 8 (Zeebe) process tests.
 *
 * @author Jan Rohwer
 */
class ProcessEngineCoverageExtension(
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

        ) : BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    companion object : KLogging() {
        @JvmStatic
        fun builder() = Builder()
    }

    /**
     * The state of the current run (class and current method).
     */
    private val coverageCollector = DefaultCollector(ZeebeModelProvider())

    /**
     * Conditions to be asserted on the class coverage percentage.
     */
    private val classCoverageAssertionConditions: MutableList<Condition<Double>> = mutableListOf()

    /**
     * Conditions to be asserted on the individual test method coverages.
     */
    private val testMethodNameToCoverageConditions: MutableMap<String, MutableList<Condition<Double>>> = mutableMapOf()

    private var suiteInitialized = false

    private val lastEventTime = mutableMapOf<String, Long>()

    /**
     * Handles creating the run if a relevant test method is called.
     */
    override fun beforeTestExecution(context: ExtensionContext) {
        if (!isTestMethodExcluded(context)) {
            if (!suiteInitialized) {
                initializeSuite(context)
            }
            // method name is set only on test methods (not on classes or suites)
            val runId: String = context.uniqueId
            coverageCollector.createRun(Run(runId, context.requiredTestMethod.name), coverageCollector.activeSuite.id)
            lastEventTime[context.requiredTestMethod.name] = BpmnAssert.getRecordStream().processInstanceRecords().maxOfOrNull { it.timestamp } ?: -1
            coverageCollector.activateRun(runId)
        }
    }

    /**
     * Handles evaluating the test method coverage after a relevant test method is finished.
     */
    override fun afterTestExecution(context: ExtensionContext) {
        if (!isTestMethodExcluded(context)) {
            createEvents(coverageCollector, lastEventTime[context.requiredTestMethod.name]!!)
            if (handleTestMethodCoverage) {
                handleTestMethodCoverage(context)
            }
        }
    }

    private fun isTestMethodExcluded(context: ExtensionContext) =
        context.requiredTestClass.annotations.any { it is ExcludeFromProcessCoverage }
                || context.requiredTestMethod.annotations.any { it is ExcludeFromProcessCoverage }

    /**
     * Initializes the suite for all upcoming tests.
     */
    override fun beforeAll(context: ExtensionContext) {
        if (!suiteInitialized) {
            initializeSuite(context)
        }
    }

    private fun initializeSuite(context: ExtensionContext) {
        val suiteId: String = context.uniqueId
        coverageCollector.createSuite(Suite(suiteId, context.requiredTestClass.name))
        coverageCollector.setExcludedProcessDefinitionKeys(excludedProcessDefinitionKeys)
        coverageCollector.activateSuite(suiteId)
        suiteInitialized = true
    }

    /**
     * If the extension is registered on the class level, log and assert the coverage and create a
     * graphical report. For the class coverage to work all the test method
     * deployments have to be equal.
     */
    override fun afterAll(context: ExtensionContext) {
        val suite = coverageCollector.activeSuite
        if (context.uniqueId == suite.id) {

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

    fun addTestMethodCoverageCondition(methodName: String, condition: Condition<Double>) =
            testMethodNameToCoverageConditions.getOrPut(methodName) { mutableListOf() }.add(condition)

    private fun addClassCoverageAtLeast(percentage: Double) =
            classCoverageAssertionConditions.add(
                    Condition<Double>({ p -> p >= percentage }, "matches if the coverage ratio is at least $percentage")
            )

    data class Builder(
            var detailedCoverageLogging: Boolean = false,
            var handleTestMethodCoverage: Boolean = true,
            var coverageAtLeast: Double? = null,
            var excludedProcessDefinitionKeys: List<String> = listOf(),
            var optionalAssertCoverageAtLeastProperty: String = DEFAULT_ASSERT_AT_LEAST_PROPERTY
    ) {

        companion object {

            /**
             * If you set this property to a ratio (e.g. "1.0" for full coverage),
             * the Extension will fail the test run if the coverage is less.<br></br>
             * Example parameter for running java:<br></br>
             * `-Dorg.camunda.bpm.extension.process_test_coverage.ASSERT_AT_LEAST=1.0`
             */
            const val DEFAULT_ASSERT_AT_LEAST_PROPERTY = "org.camunda.bpm.extension.process_test_coverage.ASSERT_AT_LEAST"

        }

        /**
         * Turns on detailed coverage logging in debug scope.
         */
        fun withDetailedCoverageLogging() = this.apply { detailedCoverageLogging = true }

        /**
         * Controls whether method coverage should be evaluated.
         */
        fun handleTestMethodCoverage(handleTestMethodCoverage: Boolean) = this.apply { this.handleTestMethodCoverage = handleTestMethodCoverage }

        /**
         * Asserts if the class coverage is greater than the passed percentage.
         * @param percentage minimal percentage for class coverage
         */
        fun assertClassCoverageAtLeast(percentage: Double) = this.apply {
            coverageAtLeast = percentage.checkPercentage()
        }

        /**
         * Specifies keys of process definitions, that should be excluded from the coverage analysis.
         */
        fun excludeProcessDefinitionKeys(vararg processDefinitionKeys: String) = this.apply { excludedProcessDefinitionKeys = processDefinitionKeys.toList() }

        /**
         * Specifies the key of the system property for optionally reading a minimal assertion coverage.
         */
        fun optionalAssertCoverageAtLeastProperty(property: String) = this.apply { optionalAssertCoverageAtLeastProperty = property }

        private fun coverageFromSystemProperty(key: String): Double? {
            return System.getProperty(key)?.let {
                try {
                    it.toDouble().checkPercentage()
                } catch (e: NumberFormatException) {
                    throw RuntimeException("BAD TEST CONFIGURATION: system property \"$key\" must be double")
                }
            }
        }

        fun build(): ProcessEngineCoverageExtension {
            return ProcessEngineCoverageExtension(
                    detailedCoverageLogging = detailedCoverageLogging,
                    handleTestMethodCoverage = handleTestMethodCoverage,
                    excludedProcessDefinitionKeys = excludedProcessDefinitionKeys
            ).apply {
                coverageFromSystemProperty(this@Builder.optionalAssertCoverageAtLeastProperty)?.let {
                    addClassCoverageAtLeast(it)
                }
                this@Builder.coverageAtLeast?.let { addClassCoverageAtLeast(it) }
            }
        }
    }

}

fun Double.checkPercentage() =
    if (0 > this || this > 1) {
        throw RuntimeException(
                "BAD TEST CONFIGURATION: coverageAtLeast " + this + " (" + 100 * this + "%) ")
    } else this