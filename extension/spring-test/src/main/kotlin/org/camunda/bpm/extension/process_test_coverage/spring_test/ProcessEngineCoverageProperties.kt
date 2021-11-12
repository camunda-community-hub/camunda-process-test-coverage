package org.camunda.bpm.extension.process_test_coverage.spring_test

import org.assertj.core.api.Condition
import org.camunda.bpm.extension.process_test_coverage.spring_test.ProcessEngineCoverageTestExecutionListener.Companion.DEFAULT_ASSERT_AT_LEAST_PROPERTY

open class ProcessEngineCoverageProperties {

    /**
     * Log class and test method coverages?
     */
    open fun detailedCoverageLogging(): Boolean = false

    /**
     * Is method coverage handling needed?
     */
    open fun handleTestMethodCoverage(): Boolean = true

    /**
     * A list of process definition keys excluded from the test run.
     */
    open fun excludedProcessDefinitionKeys(): List<String> = listOf()

    /**
     * Asserts if the class coverage is greater than the percentage.
     */
    open fun coverageAtLeast(): Double? = null

    /**
     * Specifies the key of the system property for optionally reading a minimal assertion coverage.
     */
    open fun optionalAssertCoverageAtLeastProperty(): String = DEFAULT_ASSERT_AT_LEAST_PROPERTY

    /**
     * Conditions to be asserted on the individual test method coverages.
     */
    open fun testMethodCoverageConditions(): Map<String, List<Condition<Double>>> = mapOf()

}