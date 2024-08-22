/*-
 * #%L
 * Camunda Process Test Coverage Report Aggregator Maven Plugin
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
        var TARGET_DIR_ROOT: String? = System.getProperty("camunda-process-test-coverage.target-dir-root")
    }

    @Parameter(defaultValue = "target/process-test-coverage")
    private lateinit var reportDirectory: String

    @Parameter(defaultValue = "all")
    private lateinit var outputDirectory: String

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
        this.reportDirectory = outputDirectory.toRelativeString(project.basedir)
    }

    override fun getReportOutputDirectory(): File = File(project.basedir, reportDirectory)

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
        val targetDirectory = TARGET_DIR_ROOT ?: reportDirectory
        reactorProjects
            .asSequence()
            .map {
                log.debug("Processing module ${it.name} with basedir ${it.basedir}")
                it.basedir.walk()
                    .filter { file -> file.isFile && file.name == "report.json" }
                    .filter { file -> file.toRelativeString(it.basedir).startsWith(targetDirectory) }
                    .filter { file -> !file.absolutePath.endsWith("/${outputDirectory}/report.json") }
            }
            .flatten()
            .map {
                log.debug("Reading file ${it.path}")
                it.readText()
            }
            .reduceOrNull { result1, result2 -> combineCoverageStateResults(result1, result2) }
            ?.let {
                val report = readCoverageStateResult(it)
                val aggregateReportDirectory = File(File(project.basedir, targetDirectory), outputDirectory).path
                CoverageReportUtil.writeReport(createCoverageStateResult(report.suites, report.models), false,
                    aggregateReportDirectory, "report.json"
                ) { result -> result }
                CoverageReportUtil.writeReport(createCoverageStateResult(report.suites, report.models), true,
                    aggregateReportDirectory, "report.html", CoverageReportUtil::generateHtml)
            } ?: log.warn("No coverage results found, skipping execution")
    }

}
