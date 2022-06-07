package org.camunda.community.process_test_coverage.report.aggregator

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.createCoverageStateResult
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.readCoverageStateResult
import org.camunda.community.process_test_coverage.core.export.CoverageStateResult
import org.camunda.community.process_test_coverage.report.CoverageReportUtil
import org.codehaus.plexus.util.FileUtils
import java.io.File

@Mojo(name = "aggregate", threadSafe = true, defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
class ReportAggregatorPlugin : AbstractMojo() {

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

    override fun execute() {
        reactorProjects
            .asSequence()
            .map {
                log.debug("Processing module ${it.name}")
                FileUtils.getFiles(it.basedir, "$TARGET_DIR_ROOT/**/report.json", "")
            }
            .flatten()
            .map {
                log.debug("Reading file ${it.path}")
                FileUtils.fileRead(it)
            }
            .map { readCoverageStateResult(it) }
            .reduce { result1, result2 -> CoverageStateResult(result1.suites + result2.suites, result1.models + result2.models) }
            .let {
                CoverageReportUtil.writeReport(createCoverageStateResult(it.suites, it.models), true,
                    outputDirectory.path, "report.html", CoverageReportUtil::generateHtml)
            }
    }

}