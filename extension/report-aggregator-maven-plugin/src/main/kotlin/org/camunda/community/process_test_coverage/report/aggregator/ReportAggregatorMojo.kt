package org.camunda.community.process_test_coverage.report.aggregator

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.apache.maven.reporting.MavenReport
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.combineCoverageStateResults
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.createCoverageStateResult
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.readCoverageStateResult
import org.camunda.community.process_test_coverage.report.CoverageReportUtil
import java.io.File
import java.util.*

@Mojo(name = "aggregate", aggregator = true, threadSafe = true, defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
class ReportAggregatorMojo : AbstractMojo(), MavenReport {

    companion object {
        var TARGET_DIR_ROOT: String = System.getProperty("camunda-process-test-coverage.target-dir-root", "target/process-test-coverage/")
    }

    @Parameter(defaultValue = "\${project.build.directory}/process-test-coverage/all")
    private lateinit var outputDirectory: File

    /**
     * The projects in the reactor.
     */
    @Parameter(property = "reactorProjects", readonly = true)
    private lateinit var reactorProjects: List<MavenProject>

    /**
     * Practical reference to the Maven project
     */
    @Parameter(defaultValue = "\${project}", readonly = true)
    private lateinit var project: MavenProject

    /**
     * Flag used to suppress execution.
     */
    @Parameter(property = "process-test-coverage.skip", defaultValue = "false")
    private var skip = false

    override fun generate(sink: org.codehaus.doxia.sink.Sink?, locale: Locale) {
        executeReport(locale)
    }

    override fun getOutputName() = "process-test-coverage/all/report"

    override fun getCategoryName() = MavenReport.CATEGORY_PROJECT_REPORTS

    override fun getName(locale: Locale) = "Process Test Coverage Aggregate"

    override fun getDescription(locale: Locale) = getName(locale)

    override fun setReportOutputDirectory(outputDirectory: File) {
        if (!outputDirectory.endsWith("process-test-coverage/all")) {
            this.outputDirectory = File(outputDirectory, "process-test-coverage/all")
        } else {
            this.outputDirectory = outputDirectory
        }
    }

    override fun getReportOutputDirectory(): File = outputDirectory

    override fun isExternalReport() = true

    override fun canGenerateReport(): Boolean {
        if (skip) {
            log.info("Skipping process test coverage aggregation execution because property process-test-coverage.skip is set.")
            return false
        }
        return true
    }

    override fun execute() {
        executeReport(Locale.getDefault())
    }

    private fun executeReport(locale: Locale) {
        if (!canGenerateReport()) {
            return
        }
        reactorProjects
            .asSequence()
            .map {
                log.debug("Processing module ${it.name} with basedir ${it.basedir}")
                it.basedir.walk()
                    .filter { file -> file.isFile && file.name == "report.json" }
                    .filter { file -> file.toRelativeString(it.basedir).startsWith(TARGET_DIR_ROOT) }
                    .filter { file -> !file.absolutePath.endsWith("/all/report.json") }
            }
            .flatten()
            .map {
                log.debug("Reading file ${it.path}")
                it.readText()
            }
            .reduceOrNull { result1, result2 -> combineCoverageStateResults(result1, result2) }
            ?.let {
                val report = readCoverageStateResult(it)
                CoverageReportUtil.writeReport(createCoverageStateResult(report.suites, report.models), false,
                    outputDirectory.path, "report.json"
                ) { result -> result }
                CoverageReportUtil.writeReport(createCoverageStateResult(report.suites, report.models), true,
                    outputDirectory.path, "report.html", CoverageReportUtil::generateHtml)
            } ?: log.warn("No coverage results found, skipping execution")
    }

}