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

import org.slf4j.LoggerFactory
import org.sonar.api.batch.sensor.SensorContext
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList as streamToList


class ReportsProvider(
    private val context: SensorContext
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ReportsProvider::class.java)
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
