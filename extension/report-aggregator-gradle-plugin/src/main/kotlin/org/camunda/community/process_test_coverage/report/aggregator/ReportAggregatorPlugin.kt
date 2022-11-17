package org.camunda.community.process_test_coverage.report.aggregator

import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter
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
        project.task("aggregate-process-test-coverage")
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
                    .map { CoverageStateJsonExporter.readCoverageStateResult(it) }
                    .reduceOrNull { result1, result2 -> CoverageStateResult(
                        result1.suites + result2.suites,
                        result1.models.plus(result2.models.filter { new -> !result1.models.map { model -> model.key }.contains(new.key) })
                    )
                    }
                    ?.let {
                        CoverageReportUtil.writeReport(
                            CoverageStateJsonExporter.createCoverageStateResult(it.suites, it.models), false,
                            calcOutputDirectory(project), "report.json"
                        ) { result -> result }
                        CoverageReportUtil.writeReport(
                            CoverageStateJsonExporter.createCoverageStateResult(it.suites, it.models), true,
                            calcOutputDirectory(project), "report.html", CoverageReportUtil::generateHtml)
                    } ?: project.logger.warn("No coverage results found, skipping execution")
            }
    }

    private fun calcOutputDirectory(project: Project): String {
        val extension = project.extensions.create("process-test-coverage", ReportAggregatorPluginExtension::class.java)
        return extension.outputDirectory ?: File(project.buildDir, "process-test-coverage/all").absolutePath
    }

}