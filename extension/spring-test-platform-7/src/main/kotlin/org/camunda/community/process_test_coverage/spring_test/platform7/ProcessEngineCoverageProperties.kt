package org.camunda.community.process_test_coverage.spring_test.platform7

import org.assertj.core.api.Condition

data class ProcessEngineCoverageProperties(
    /**
     * Log class and test method coverages?
     */
    val detailedCoverageLogging: Boolean = false,
    /**
     * Is method coverage handling needed?
     */
    val handleTestMethodCoverage: Boolean = true,
    /**
     * A list of process definition keys excluded from the test run.
     */
    val excludedProcessDefinitionKeys: List<String> = listOf(),
    /**
     * Conditions to be asserted on the class coverage percentage.
     */
    val classCoverageAssertionConditions: List<Condition<Double>> = listOf(),
    /**
     * Conditions to be asserted on the individual test method coverages.
     */
    val testMethodCoverageConditions: Map<String, List<Condition<Double>>> = mapOf()

) {

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {

        companion object {
            /**
             * If you set this property to a ratio (e.g. "1.0" for full coverage),
             * the Extension will fail the test run if the coverage is less.<br></br>
             * Example parameter for running java:<br></br>
             * `-Dorg.camunda.community.process_test_coverage.ASSERT_AT_LEAST=1.0`
             */
            const val DEFAULT_ASSERT_AT_LEAST_PROPERTY = "org.camunda.community.process_test_coverage.ASSERT_AT_LEAST"
        }

        private var detailedCoverageLogging: Boolean = false
        private var handleTestMethodCoverage: Boolean = true
        private var excludedProcessDefinitionKeys: List<String> = listOf()
        private var coverageAtLeast: Double? = null
        private var optionalAssertCoverageAtLeastProperty: String = DEFAULT_ASSERT_AT_LEAST_PROPERTY
        private var testMethodCoverageConditions: MutableMap<String, MutableList<Condition<Double>>> = mutableMapOf()

        /**
         * Log class and test method coverages?
         */
        fun withDetailedCoverageLogging() = apply { this.detailedCoverageLogging = true }

        /**
         * Is method coverage handling needed?
         */
        fun handleTestMethodCoverage(handleTestMethodCoverage: Boolean) = apply { this.handleTestMethodCoverage = handleTestMethodCoverage }

        /**
         * Asserts if the class coverage is greater than the percentage.
         */
        fun assertClassCoverageAtLeast(coverageAtLeast: Double) = apply { this.coverageAtLeast = coverageAtLeast }

        /**
         * A list of process definition keys excluded from the test run.
         */
        fun excludeProcessDefinitionKeys(vararg processDefinitionKeys: String) = apply { this.excludedProcessDefinitionKeys = processDefinitionKeys.toList() }

        /**
         * Specifies the key of the system property for optionally reading a minimal assertion coverage.
         */
        fun optionalAssertCoverageAtLeastProperty(property: String) = apply { this.optionalAssertCoverageAtLeastProperty = property }

        /**
         * Add a condition to be asserted on the individual test method coverages.
         */
        fun addTestMethodCoverageCondition(methodName: String, condition: Condition<Double>) =
                apply { this.testMethodCoverageConditions.getOrPut(methodName) { mutableListOf() }.add(condition) }

        fun build(): ProcessEngineCoverageProperties {
            val classCoverageConditions: MutableList<Condition<Double>> = mutableListOf()
            coverageAtLeast?.let { classCoverageConditions.add(buildClassCoverageCondition(it)) }
            readCoverageFromSystemProperty()?.let { classCoverageConditions.add(buildClassCoverageCondition(it)) }
            return ProcessEngineCoverageProperties(
                    detailedCoverageLogging = detailedCoverageLogging,
                    handleTestMethodCoverage = handleTestMethodCoverage,
                    excludedProcessDefinitionKeys = excludedProcessDefinitionKeys,
                    classCoverageAssertionConditions = classCoverageConditions,
                    testMethodCoverageConditions = testMethodCoverageConditions
            )
        }

        private fun buildClassCoverageCondition(percentage: Double): Condition<Double> {
            require(percentage in 0.0..1.0) { "Not a valid percentage: $percentage (${percentage * 100}%)" }
            return Condition<Double>({ p -> p >= percentage }, "matches if the coverage ratio is at least $percentage")
        }

        private fun readCoverageFromSystemProperty(): Double? {
            try {
                return System.getProperty(optionalAssertCoverageAtLeastProperty)?.toDouble()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("System property \"$optionalAssertCoverageAtLeastProperty\" is not a number")
            }
        }

    }
}