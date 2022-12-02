package org.camunda.community.process_test_coverage.sonar

import org.camunda.community.process_test_coverage.core.export.CoverageStateResult
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.utils.log.Loggers


class ReportImporter(private val ctx: SensorContext) {

    companion object {
        private val LOG = Loggers.get(ReportImporter::class.java)
    }

    fun importCoverage(result: CoverageStateResult) {
        if (result.suites.size == 1) {
            result.suites.first().let {

                val lastDot = it.name.lastIndexOf('.')
                val className = it.name.substring(lastDot + 1)
                val packageName = it.name.substring(0, lastDot)
                val path = "**/${packageName.replace('.', '/')}/$className.*"
                val inputFile = ctx.fileSystem().inputFile(ctx.fileSystem().predicates().matchesPathPattern(path))

                inputFile?.let { file ->
                    ctx.newMeasure<Double>()
                        .on(file)
                        .forMetric(ProcessTestCoverageMetrics.PROCESS_TEST_COVERAGE)
                        .withValue(it.calculateCoverage(result.models).asPercent())
                        .save()
                }
            }
        } else {
            LOG.warn("Cannot import coverage results for more than one suite")
        }
    }

    private fun Double.asPercent() = this * 100

}
