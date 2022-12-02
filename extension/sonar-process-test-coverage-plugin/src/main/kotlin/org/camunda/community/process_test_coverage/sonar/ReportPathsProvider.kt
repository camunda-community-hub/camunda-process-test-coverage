package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.utils.log.Logger
import org.sonar.api.utils.log.Loggers
import java.nio.file.Path


class ReportPathsProvider(
    private val context: SensorContext
) {

    companion object {
        private val LOG: Logger = Loggers.get(ReportPathsProvider::class.java)
        private val DEFAULT_PATHS = arrayOf("target/process-test-coverage/**/report.json")
        const val REPORT_PATHS_PROPERTY_KEY = "sonar.process-test-coverage.jsonReportPaths"
    }

    fun getPaths(): Collection<Path> {
        val baseDir: Path = context.fileSystem().baseDir().toPath().toAbsolutePath()
        val patternPathList = context.config().getStringArray(REPORT_PATHS_PROPERTY_KEY)
            .filter { it.isNotEmpty() }
            .plus(DEFAULT_PATHS)
        val reportPaths: MutableSet<Path> = HashSet()
        if (patternPathList.isNotEmpty()) {
            for (patternPath in patternPathList) {
                val paths: List<Path> = WildcardPatternFileScanner.scan(baseDir, patternPath)
                if (paths.isEmpty() && patternPathList.size > 1) {
                    LOG.info("Coverage report doesn't exist for pattern: '{}'", patternPath)
                }
                reportPaths.addAll(paths)
            }
        }
        return reportPaths
    }

}