/*-
 * #%L
 * Camunda Process Test Coverage JUnit5 Platform 8
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.camunda.community.process_test_coverage.junit5.platform8

import io.camunda.zeebe.process.test.assertions.BpmnAssert
import mu.KLogging
import org.assertj.core.api.Condition
import org.camunda.community.process_test_coverage.core.model.DefaultCollector
import org.camunda.community.process_test_coverage.engine.platform8.ZeebeModelProvider
import org.camunda.community.process_test_coverage.engine.platform8.createEvents
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

    /**
     * Map of test method to last event time, when test method was started.
     */
    private val methodRecordPosition = mutableMapOf<String, Long>()

    private val processEngineCoverageExtensionHelper = ProcessEngineCoverageExtensionHelper(coverageCollector,
        detailedCoverageLogging, handleTestMethodCoverage, excludedProcessDefinitionKeys,
        classCoverageAssertionConditions, testMethodNameToCoverageConditions, reportDirectory)

    /**
     * Handles creating the run if a relevant test method is called.
     */
    override fun beforeTestExecution(context: ExtensionContext) {
        if (!processEngineCoverageExtensionHelper.isTestMethodExcluded(context)) {
            processEngineCoverageExtensionHelper.beforeTestExecution(context)
            methodRecordPosition[context.requiredTestMethod.name] = BpmnAssert.getRecordStream().records().maxOfOrNull { it.position } ?: -1
        }
    }

    /**
     * Handles evaluating the test method coverage after a relevant test method is finished.
     */
    override fun afterTestExecution(context: ExtensionContext) {
        if (!processEngineCoverageExtensionHelper.isTestMethodExcluded(context)) {
            createEvents(coverageCollector, methodRecordPosition[context.requiredTestMethod.name]!!)
            processEngineCoverageExtensionHelper.afterTestExecution(context)
        }
    }

    /**
     * Initializes the suite for all upcoming tests.
     */
    override fun beforeAll(context: ExtensionContext) {
        processEngineCoverageExtensionHelper.beforeAll(context)
    }

    /**
     * If the extension is registered on the class level, log and assert the coverage and create a
     * graphical report. For the class coverage to work all the test method
     * deployments have to be equal.
     */
    override fun afterAll(context: ExtensionContext) {
        processEngineCoverageExtensionHelper.afterAll(context)
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
