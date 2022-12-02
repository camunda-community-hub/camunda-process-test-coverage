package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.Metric
import org.sonar.api.measures.Metrics


class ProcessTestCoverageMetrics : Metrics {

    override fun getMetrics(): List<Metric<*>> {
        return listOf<Metric<*>>(PROCESS_TEST_COVERAGE)
    }

    companion object {
        val PROCESS_TEST_COVERAGE: Metric<Double> = Metric.Builder("process_test_coverage", "Process Test Coverage", Metric.ValueType.PERCENT)
            .setDescription("Coverage on process nodes")
            .setDirection(Metric.DIRECTION_BETTER)
            .setQualitative(true)
            .setDomain(CoreMetrics.DOMAIN_COVERAGE)
            .create()
    }
}