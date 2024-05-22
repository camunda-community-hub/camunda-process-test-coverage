/*-
 * #%L
 * Camunda Process Test Coverage Sonar Plugin
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
package org.camunda.community.process_test_coverage.sonar

import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.combineCoverageStateResults
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.readCoverageStateResult
import org.slf4j.LoggerFactory
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.scanner.sensor.ProjectSensor
import java.nio.file.Files


class ProcessTestCoverageProjectSensor : ProjectSensor {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProcessTestCoverageProjectSensor::class.java)
    }

    override fun describe(descriptor: SensorDescriptor) {
        descriptor.name("Camunda Process Test Coverage (Project)")
    }

    override fun execute(context: SensorContext) {
        val reportsProvider = ReportsProvider(context)
        val importer = ReportImporter(context)
        importReports(reportsProvider, importer)
    }

    private fun importReports(reportsProvider: ReportsProvider, importer: ReportImporter) {
        val reportPaths = reportsProvider.getProjectReports()
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
                    ?.let { importer.importProjectCoverage(readCoverageStateResult(it)) }
                    ?: LOG.warn("No coverage results found, skipping analysis")
        } catch (e: Exception) {
            LOG.error("Coverage reports could not be read/imported. Error: {}", e)
        }
    }

}
