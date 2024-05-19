package org.camunda.community.process_test_coverage.junit5.common

abstract class ProcessEngineCoverageExtensionBuilder<T>(
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
         * `-Dorg.camunda.community.process_test_coverage.ASSERT_AT_LEAST=1.0`
         */
        const val DEFAULT_ASSERT_AT_LEAST_PROPERTY = "org.camunda.community.process_test_coverage.ASSERT_AT_LEAST"

    }

    /**
     * Turns on detailed coverage logging in debug scope.
     */
    fun withDetailedCoverageLogging() = this.apply { detailedCoverageLogging = true }

    /**
     * Controls whether method coverage should be evaluated.
     */
    fun handleTestMethodCoverage(handleTestMethodCoverage: Boolean) =
        this.apply { this.handleTestMethodCoverage = handleTestMethodCoverage }

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
    fun excludeProcessDefinitionKeys(vararg processDefinitionKeys: String) =
        this.apply { excludedProcessDefinitionKeys = processDefinitionKeys.toList() }

    /**
     * Specifies the key of the system property for optionally reading a minimal assertion coverage.
     */
    fun optionalAssertCoverageAtLeastProperty(property: String) =
        this.apply { optionalAssertCoverageAtLeastProperty = property }

    abstract fun build() : T

    protected fun coverageFromSystemProperty(key: String): Double? {
        return System.getProperty(key)?.let {
            try {
                it.toDouble().checkPercentage()
            } catch (e: NumberFormatException) {
                throw RuntimeException("BAD TEST CONFIGURATION: system property \"$key\" must be double")
            }
        }
    }
}


fun Double.checkPercentage() =
    if (0 > this || this > 1) {
        throw RuntimeException(
            "BAD TEST CONFIGURATION: coverageAtLeast " + this + " (" + 100 * this + "%) ")
    } else this