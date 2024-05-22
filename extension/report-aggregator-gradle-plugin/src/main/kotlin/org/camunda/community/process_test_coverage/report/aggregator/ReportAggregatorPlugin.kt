/*-
 * #%L
 * Camunda Process Test Coverage Report Aggregator Gradle Plugin
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
package org.camunda.community.process_test_coverage.report.aggregator

import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.combineCoverageStateResults
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.createCoverageStateResult
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.readCoverageStateResult
import org.camunda.community.process_test_coverage.core.export.CoverageStateResult
import org.camunda.community.process_test_coverage.report.CoverageReportUtil
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

abstract class ReportAggregatorPluginExtension {
    var outputDirectory: String? = null
}

class ReportAggregatorPlugin : Plugin<Project> {

    companion object {
        var TARGET_DIR_ROOT: String = System.getProperty("camunda-process-test-coverage.target-dir-root", "target/process-test-coverage/")
    }

    override fun apply(project: Project) {
        val outputDirectory = calcOutputDirectory(project)
        project.task("aggregateProcessTestCoverage")
            .doLast {
                project.allprojects
                    .asSequence()
                    .map {
                        project.logger.debug("Processing module ${it.name}")
                        it.fileTree(".").apply { this.include("$TARGET_DIR_ROOT/**/report.json", "**/all/report.json") }.files
                    }
                    .flatten()
                    .map {
                        project.logger.debug("Reading file ${it.path}")
                        it.readText(Charsets.UTF_8)
                    }
                    .reduceOrNull { result1, result2 -> combineCoverageStateResults(result1, result2) }
                    ?.let {
                        val report = readCoverageStateResult(it)
                        CoverageReportUtil.writeReport(
                            createCoverageStateResult(report.suites, report.models), false,
                            outputDirectory, "report.json"
                        ) { result -> result }
                        CoverageReportUtil.writeReport(
                            createCoverageStateResult(report.suites, report.models), true,
                            outputDirectory, "report.html", CoverageReportUtil::generateHtml)
                    } ?: project.logger.warn("No coverage results found, skipping execution")
            }
    }

    private fun calcOutputDirectory(project: Project): String {
        val extension = project.extensions.create("aggregateProcessTestCoverage", ReportAggregatorPluginExtension::class.java)
        return extension.outputDirectory ?: File(project.projectDir, TARGET_DIR_ROOT + "all").absolutePath
    }

}
