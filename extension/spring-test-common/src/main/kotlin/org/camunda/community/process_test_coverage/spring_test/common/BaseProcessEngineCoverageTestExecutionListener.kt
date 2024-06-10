/*-
 * #%L
 * Camunda Process Test Coverage Spring-Testing Common
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
package org.camunda.community.process_test_coverage.spring_test.common

import mu.KLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Condition
import org.camunda.community.process_test_coverage.core.engine.ExcludeFromProcessCoverage
import org.camunda.community.process_test_coverage.core.model.DefaultCollector
import org.camunda.community.process_test_coverage.core.model.Run
import org.camunda.community.process_test_coverage.core.model.Suite
import org.camunda.community.process_test_coverage.report.CoverageReportUtil
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.core.Ordered
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener
import org.springframework.util.PatternMatchUtils

abstract class BaseProcessEngineCoverageTestExecutionListener : TestExecutionListener, Ordered {

    companion object : KLogging()

    private lateinit var processEngineCoverageProperties: ProcessEngineCoverageProperties

    private var suiteInitialized = false

    private fun loadConfiguration(testContext: TestContext) {
        processEngineCoverageProperties = try {
            testContext.applicationContext.getBean(ProcessEngineCoverageProperties::class.java)
        } catch (ex: NoSuchBeanDefinitionException) {
            ProcessEngineCoverageProperties()
        }
    }

    protected abstract fun getCoverageCollector(): DefaultCollector

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
            getCoverageCollector().createRun(Run(runId, testContext.testMethod.name), getCoverageCollector().activeSuite.id)
            getCoverageCollector().activateRun(runId)
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

    protected open fun isTestClassExcluded(testContext: TestContext) =
        testContext.testClass.annotations.any { it is ExcludeFromProcessCoverage }
                || !matchesInclusionPattern(testContext)

    private fun matchesInclusionPattern(testContext: TestContext) =
        processEngineCoverageProperties.inclusionPatternsForTestClasses.isEmpty()
                || PatternMatchUtils.simpleMatch(processEngineCoverageProperties.inclusionPatternsForTestClasses.toTypedArray(),
            testContext.testClass.name)

    protected fun isTestMethodExcluded(testContext: TestContext) =
        isTestClassExcluded(testContext)
                || testContext.testMethod.annotations.any { it is ExcludeFromProcessCoverage }

    /**
     * Initializes the suite for all upcoming tests.
     */
    override fun beforeTestClass(testContext: TestContext) {
        loadConfiguration(testContext)
        if (!isTestClassExcluded(testContext)) {
            initializeSuite(testContext)
        }
    }

    private fun initializeSuite(testContext: TestContext) {
        val suiteId: String = testContext.testClass.name
        getCoverageCollector().createSuite(Suite(suiteId, testContext.testClass.name))
        getCoverageCollector().setExcludedProcessDefinitionKeys(processEngineCoverageProperties.excludedProcessDefinitionKeys)
        getCoverageCollector().activateSuite(suiteId)
        suiteInitialized = true
    }

    /**
     * Handles the coverage report after the test class was executed.
     */
    override fun afterTestClass(testContext: TestContext) {
        if (suiteInitialized) {
            val suite = getCoverageCollector().activeSuite

            val suiteCoveragePercentage = suite.calculateCoverage(getCoverageCollector().getModels())

            if (suiteCoveragePercentage.isNaN()) {
                logger.warn { "${suite.name} test class coverage could not be calculated, check configuration" }
            } else {
                // Log coverage percentage
                logger.info("${suite.name} test class coverage is: $suiteCoveragePercentage")
                logCoverageDetail(suite)

                // Create graphical report
                CoverageReportUtil.createReport(getCoverageCollector())
                CoverageReportUtil.createJsonReport(getCoverageCollector())

                assertCoverage(
                    suiteCoveragePercentage,
                    processEngineCoverageProperties.classCoverageAssertionConditions
                )
            }
        }
    }

    /**
     * Logs and asserts the test method coverage and creates a graphical report.
     *
     * @param testContext test context
     */
    private fun handleTestMethodCoverage(testContext: TestContext) {
        val suite = getCoverageCollector().activeSuite
        val run = suite.getRun(testContext.testMethod.name) ?: return
        val coveragePercentage = run.calculateCoverage(getCoverageCollector().getModels())

        if (coveragePercentage.isNaN()) {
            logger.warn { "${run.name} test method coverage could not be calculated, check configuration" }
        } else {
            // Log coverage percentage
            logger.info("${run.name} test method coverage is $coveragePercentage")
            logCoverageDetail(run)

            processEngineCoverageProperties.testMethodCoverageConditions[run.name]?.let {
                assertCoverage(coveragePercentage, it)
            }
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
