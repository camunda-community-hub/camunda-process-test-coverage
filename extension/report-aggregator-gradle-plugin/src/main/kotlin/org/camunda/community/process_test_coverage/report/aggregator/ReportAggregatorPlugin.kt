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