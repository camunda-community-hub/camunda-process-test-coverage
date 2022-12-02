package org.camunda.community.process_test_coverage.sonar

import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter
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
        descriptor.name("Process Test Coverage Report Importer")
    }

    override fun execute(context: SensorContext) {
        val reportPathsProvider = ReportPathsProvider(context)
        val importer = ReportImporter(context)
        importReports(reportPathsProvider, importer)
    }

    private fun importReports(reportPathsProvider: ReportPathsProvider, importer: ReportImporter) {
        val reportPaths = reportPathsProvider.getPaths()
        if (reportPaths.isEmpty()) {
            LOG.info("No report imported, no coverage information will be imported by Process Test Coverage Report Importer")
            return
        }
        LOG.info(
            "Importing {} report(s). Turn your logs in debug mode in order to see the exhaustive list.",
            reportPaths.size
        )
        for (reportPath in reportPaths) {
            LOG.debug("Reading report '{}'", reportPath)
            try {
                val result = CoverageStateJsonExporter.readCoverageStateResult(Files.readAllBytes(reportPath).decodeToString())
                importer.importCoverage(result)
            } catch (e: Exception) {
                LOG.error("Coverage report '{}' could not be read/imported. Error: {}", reportPath, e)
            }
        }
    }

}