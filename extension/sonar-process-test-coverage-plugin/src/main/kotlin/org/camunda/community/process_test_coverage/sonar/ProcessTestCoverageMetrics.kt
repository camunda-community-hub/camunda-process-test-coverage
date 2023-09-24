package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.Metric
import org.sonar.api.measures.Metrics


class ProcessTestCoverageMetrics : Metrics {

    override fun getMetrics(): List<Metric<*>> {
        return listOf<Metric<*>>(PROCESS_TEST_COVERAGE, PROCESS_TEST_COVERAGE_REPORT)
    }

    companion object {
        val PROCESS_TEST_COVERAGE: Metric<Double> = Metric.Builder("process_test_coverage", "Process Test Coverage", Metric.ValueType.PERCENT)
                .setDescription("Coverage on process nodes")
                .setDirection(Metric.DIRECTION_BETTER)
                .setQualitative(true)
                .setDomain(CoreMetrics.DOMAIN_COVERAGE)
                .create()

        val PROCESS_TEST_COVERAGE_REPORT: Metric<String> = Metric.Builder("process_test_coverage_report", "Process Test Coverage Report", Metric.ValueType.DATA)
                .setDescription("JSON Report for process test coverage")
                .setQualitative(false)
                .setDomain(CoreMetrics.DOMAIN_COVERAGE)
                .setHidden(false)
                .setDeleteHistoricalData(true)
                .create()
    }
}