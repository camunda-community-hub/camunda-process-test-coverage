/*-
 * #%L
 * Camunda Process Test Coverage JUnit5 Platform 7
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
package org.camunda.community.process_test_coverage.junit5.platform7

import mu.KLogging
import org.assertj.core.api.Condition
import org.camunda.bpm.engine.ProcessEngineConfiguration
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension
import org.camunda.community.process_test_coverage.core.model.DefaultCollector
import org.camunda.community.process_test_coverage.engine.platform7.ExecutionContextModelProvider
import org.camunda.community.process_test_coverage.engine.platform7.ProcessEngineAdapter
import org.camunda.community.process_test_coverage.junit5.common.ProcessEngineCoverageExtensionBuilder
import org.camunda.community.process_test_coverage.junit5.common.ProcessEngineCoverageExtensionHelper
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Extension for JUnit 5 which allows the tracking of coverage information for Camunda process tests.
 * Based on the ProcessEngineExtension from the camunda-bpm-junit5 from the camunda community.
 * https://github.com/camunda-community-hub/camunda-bpm-junit5
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

        ) : ProcessEngineExtension(), BeforeAllCallback, AfterAllCallback {

    companion object : KLogging() {
        @JvmStatic
        fun builder() = Builder()
        @JvmStatic
        fun builder(configurationResource: String) = Builder(configurationResource = configurationResource)
        @JvmStatic
        fun builder(processEngineConfiguration: ProcessEngineConfiguration) = Builder(processEngineConfiguration = processEngineConfiguration)
    }

    /**
     * The state of the current run (class and current method).
     */
    private val coverageCollector = DefaultCollector(ExecutionContextModelProvider())

    /**
     * Conditions to be asserted on the class coverage percentage.
     */
    private val classCoverageAssertionConditions: MutableList<Condition<Double>> = mutableListOf()

    /**
     * Conditions to be asserted on the individual test method coverages.
     */
    private val testMethodNameToCoverageConditions: MutableMap<String, MutableList<Condition<Double>>> = mutableMapOf()

    private val processEngineCoverageExtensionHelper = ProcessEngineCoverageExtensionHelper(coverageCollector,
        detailedCoverageLogging, handleTestMethodCoverage, excludedProcessDefinitionKeys,
        classCoverageAssertionConditions, testMethodNameToCoverageConditions)

    override fun postProcessTestInstance(testInstance: Any?, context: ExtensionContext) {
        super.postProcessTestInstance(testInstance, context)
        initializeServices()
        ProcessEngineAdapter(processEngine, coverageCollector).initializeListeners()
    }

    /**
     * Handles creating the run if a relevant test method is called.
     */
    override fun beforeTestExecution(context: ExtensionContext) {
        super.beforeTestExecution(context)
        processEngineCoverageExtensionHelper.beforeTestExecution(context)
    }

    /**
     * Handles evaluating the test method coverage after a relevant test method is finished.
     */
    override fun afterTestExecution(context: ExtensionContext) {
        if (!processEngineCoverageExtensionHelper.isTestMethodExcluded(context)) {
            processEngineCoverageExtensionHelper.afterTestExecution(context)
        }
        super.afterTestExecution(context)
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
        super.afterAll(context)
    }

    fun addTestMethodCoverageCondition(methodName: String, condition: Condition<Double>) =
            testMethodNameToCoverageConditions.getOrPut(methodName) { mutableListOf() }.add(condition)

    private fun addClassCoverageAtLeast(percentage: Double) =
            classCoverageAssertionConditions.add(
                    Condition<Double>({ p -> p >= percentage }, "matches if the coverage ratio is at least $percentage")
            )

    data class Builder(
        var configurationResource: String? = null,
        val processEngineConfiguration: ProcessEngineConfiguration? = null,
    ) : ProcessEngineCoverageExtensionBuilder<ProcessEngineCoverageExtension>() {

        /**
         * Set the configuration resource for initializing the process engine.
         */
        @Deprecated("Pass the configuration resource directly when creating the builder",
                ReplaceWith("ProcessEngineCoverageExtension.builder(configurationResource)",
                        "org.camunda.community.process_test_coverage.junit5.ProcessEngineCoverageExtension"))
        fun configurationResource(configurationResource: String): Builder {
            return if (this.configurationResource != null) {
                logger.warn { "configuration resource ${this.configurationResource} already configured, ignoring $configurationResource" }
                this
            } else if (this.processEngineConfiguration != null) {
                logger.warn { "process engine configuration ${this.processEngineConfiguration} already configured, ignoring $configurationResource" }
                this
            } else {
                this.apply { this.configurationResource = configurationResource }
            }
        }

        override fun build(): ProcessEngineCoverageExtension {
            return ProcessEngineCoverageExtension(
                    detailedCoverageLogging = detailedCoverageLogging,
                    handleTestMethodCoverage = handleTestMethodCoverage,
                    excludedProcessDefinitionKeys = excludedProcessDefinitionKeys
            ).apply {
                coverageFromSystemProperty(this@Builder.optionalAssertCoverageAtLeastProperty)?.let {
                    addClassCoverageAtLeast(it)
                }
                this@Builder.coverageAtLeast?.let { addClassCoverageAtLeast(it) }
                this@Builder.configurationResource?.let { this.configurationResource(it) }
                if (this@Builder.processEngineConfiguration != null) {
                    processEngine = this@Builder.processEngineConfiguration.buildProcessEngine()
                    processEngineConfiguration = processEngine.processEngineConfiguration as ProcessEngineConfigurationImpl
                } else if (processEngine == null) {
                    initializeProcessEngine()
                }
            }
        }
    }

}
