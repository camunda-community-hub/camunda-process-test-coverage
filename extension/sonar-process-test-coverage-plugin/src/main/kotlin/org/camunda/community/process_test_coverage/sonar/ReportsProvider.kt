package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.utils.log.Logger
import org.sonar.api.utils.log.Loggers
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList as streamToList


class ReportsProvider(
    private val context: SensorContext
) {

    companion object {
        private val LOG: Logger = Loggers.get(ReportsProvider::class.java)
        private val DEFAULT_PATHS = arrayOf("target/process-test-coverage/**/report.json")
        const val REPORT_PATHS_PROPERTY_KEY = "sonar.process-test-coverage.jsonReportPaths"
    }

    fun getProjectReports(): Collection<Path> {
        val pathPattern = getPathPattern("**/")
        val matcher = FileSystems.getDefault().getPathMatcher("glob:$pathPattern")
        return Files.find(context.fileSystem().baseDir().toPath(), Int.MAX_VALUE, { path, _ ->
            matcher.matches(path)
        }).streamToList()
    }

    fun getModuleReports(): Collection<Path> {
        val pathPattern = getPathPattern("")
        val matcher = FileSystems.getDefault().getPathMatcher("glob:$pathPattern")
        return Files.find(context.fileSystem().baseDir().toPath(), Int.MAX_VALUE, { path, _ ->
            val relativePath = context.fileSystem().baseDir().toPath().relativize(path)
            matcher.matches(relativePath)
        }).streamToList()
    }

    private fun getPathPattern(prefix: String): String  {
        val paths = context.config().getStringArray(REPORT_PATHS_PROPERTY_KEY)
                .filter { it.isNotEmpty() }
                .plus(DEFAULT_PATHS)
        LOG.info("Configured paths are $paths")
        val pattern = paths.joinToString(prefix = "{", postfix = "}", separator = ",") { "$prefix$it" }
        LOG.info("Using pattern $pattern for reports")
        return pattern
    }

}