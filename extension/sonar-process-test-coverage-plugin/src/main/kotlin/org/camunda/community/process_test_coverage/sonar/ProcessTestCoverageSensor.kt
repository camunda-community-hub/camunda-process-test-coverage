package org.camunda.community.process_test_coverage.sonar

import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.combineCoverageStateResults
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.readCoverageStateResult
import org.sonar.api.batch.sensor.Sensor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.utils.log.Loggers
import java.nio.file.Files


class ProcessTestCoverageSensor : Sensor {

    companion object {
        private val LOG = Loggers.get(ProcessTestCoverageSensor::class.java)
    }

    override fun describe(descriptor: SensorDescriptor) {
        descriptor.name("Camunda Process Test Coverage")
    }

    override fun execute(context: SensorContext) {
        val reportsProvider = ReportsProvider(context)
        val importer = ReportImporter(context)
        importReports(reportsProvider, importer)
    }

    private fun importReports(reportsProvider: ReportsProvider, importer: ReportImporter) {
        val reportPaths = reportsProvider.getModuleReports()
        if (reportPaths.isEmpty()) {
            LOG.info("No report imported, no coverage information will be imported by Process Test Coverage Report Importer")
            return
        }
        LOG.info(
            "Importing {} report(s). Turn your logs in debug mode in order to see the exhaustive list.",
            reportPaths.size
        )
        try {
            reportPaths
                    .map {
                        LOG.debug("Reading report '{}'", it)
                        Files.readAllBytes(it).decodeToString()
                    }
                    .reduceOrNull { result1, result2 -> combineCoverageStateResults(result1, result2) }
                    ?.let { importer.importCoverage(readCoverageStateResult(it)) }
                    ?: LOG.warn("No coverage results found, skipping analysis")
        } catch (e: Exception) {
            LOG.error("Coverage reports could not be read/imported. Error: {}", e)
        }
    }

}