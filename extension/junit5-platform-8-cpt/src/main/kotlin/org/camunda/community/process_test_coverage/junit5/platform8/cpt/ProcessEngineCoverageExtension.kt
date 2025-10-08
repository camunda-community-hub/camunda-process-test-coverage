package org.camunda.community.process_test_coverage.junit5.platform8.cpt

import io.camunda.process.test.api.CamundaProcessTestContext
import io.camunda.process.test.api.CamundaProcessTestExtension
import org.assertj.core.api.Condition
import org.camunda.community.process_test_coverage.core.model.DefaultCollector
import org.camunda.community.process_test_coverage.engine.platform8.cpt.Camunda8ModelProvider
import org.camunda.community.process_test_coverage.engine.platform8.cpt.createEvents
import org.camunda.community.process_test_coverage.junit5.common.ProcessEngineCoverageExtensionBuilder
import org.camunda.community.process_test_coverage.junit5.common.ProcessEngineCoverageExtensionHelper
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Extension for JUnit 5 which allows the tracking of coverage information for Camunda Platform 8 (Zeebe) process tests.
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

        /**
         * Output directory for the reports.
         */
        private val reportDirectory: String? = null

) : CamundaProcessTestExtension(), BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    init {
        withCoverageReportDirectory("target/cpt-process-test-coverage")
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    private var camundaProcessTestContext: CamundaProcessTestContext? = null

    /**
     * The state of the current run (class and current method).
     */
    private val coverageCollector = DefaultCollector(
        Camunda8ModelProvider { camundaProcessTestContext ?: throw IllegalStateException() }
    )

    /**
     * Conditions to be asserted on the class coverage percentage.
     */
    private val classCoverageAssertionConditions: MutableList<Condition<Double>> = mutableListOf()

    /**
     * Conditions to be asserted on the individual test method coverages.
     */
    private val testMethodNameToCoverageConditions: MutableMap<String, MutableList<Condition<Double>>> = mutableMapOf()

    private val processEngineCoverageExtensionHelper = ProcessEngineCoverageExtensionHelper(
        coverageCollector,
        detailedCoverageLogging, handleTestMethodCoverage, excludedProcessDefinitionKeys,
        classCoverageAssertionConditions, testMethodNameToCoverageConditions, reportDirectory
    )

    /**
     * Handles creating the run if a relevant test method is called.
     */
    override fun beforeTestExecution(context: ExtensionContext) {
        // get runtime from store
        val store = context.getStore(NAMESPACE)
        camundaProcessTestContext = store.get(STORE_KEY_CONTEXT) as CamundaProcessTestContext
        if (!processEngineCoverageExtensionHelper.isTestMethodExcluded(context)) {
            processEngineCoverageExtensionHelper.beforeTestExecution(context)
        }
    }

    /**
     * Handles evaluating the test method coverage after a relevant test method is finished.
     */
    override fun afterTestExecution(context: ExtensionContext) {
        if (!processEngineCoverageExtensionHelper.isTestMethodExcluded(context)) {
            createEvents(camundaProcessTestContext ?: throw IllegalStateException(), coverageCollector)
            processEngineCoverageExtensionHelper.afterTestExecution(context)
        }
        camundaProcessTestContext = null
    }

    /**
     * Initializes the suite for all upcoming tests.
     */
    override fun beforeAll(context: ExtensionContext) {
        super.beforeAll(context)
        processEngineCoverageExtensionHelper.beforeAll(context)
    }

    /**
     * If the extension is registered on the class level, log and assert the coverage and create a
     * graphical report. For the class coverage to work all the test method
     * deployments have to be equal.
     */
    override fun afterAll(context: ExtensionContext) {
        processEngineCoverageExtensionHelper.afterAll(context)
        super.afterAll(context)
    }

    fun addTestMethodCoverageCondition(methodName: String, condition: Condition<Double>) =
            testMethodNameToCoverageConditions.getOrPut(methodName) { mutableListOf() }.add(condition)

    private fun addClassCoverageAtLeast(percentage: Double) =
            classCoverageAssertionConditions.add(
                Condition<Double>({ p -> p >= percentage }, "matches if the coverage ratio is at least $percentage")
            )

    class Builder : ProcessEngineCoverageExtensionBuilder<ProcessEngineCoverageExtension>() {

        override fun build(): ProcessEngineCoverageExtension {
            return ProcessEngineCoverageExtension(
                    detailedCoverageLogging = detailedCoverageLogging,
                    handleTestMethodCoverage = handleTestMethodCoverage,
                    excludedProcessDefinitionKeys = excludedProcessDefinitionKeys,
                    reportDirectory = reportDirectory
            ).apply {
                coverageFromSystemProperty(this@Builder.optionalAssertCoverageAtLeastProperty)?.let {
                    addClassCoverageAtLeast(it)
                }
                this@Builder.coverageAtLeast?.let { addClassCoverageAtLeast(it) }
            }
        }
    }

}