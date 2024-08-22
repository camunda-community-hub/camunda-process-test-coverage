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

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Consumer


class ReportAggregatorPluginTest {


    // creates temp directory for a gradle project
    @TempDir
    private lateinit var testProjectDir: File

    private lateinit var buildFile: File
    private lateinit var settingsFile: File
    private lateinit var gradleRunner: GradleRunner

    @Test
    fun reportAggregatorPluginTest() {
        val project: Project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.camunda.community.process_test_coverage.report-aggregator")
        assertTrue(
            project.pluginManager.hasPlugin("org.camunda.community.process_test_coverage.report-aggregator")
        )
        assertNotNull(project.tasks.getByName("aggregateProcessTestCoverage"))
    }

    @BeforeEach
    fun setup() {
        settingsFile = File(testProjectDir, "settings.gradle")
        buildFile = File(testProjectDir, "build.gradle")
        Files.write(settingsFile.toPath(), "rootProject.name = 'process-coverage-gradle-plugin-test'".toByteArray())
        val buildFileContent = """
            plugins {
                id 'org.camunda.community.process_test_coverage.report-aggregator'
            }
        """
        Files.write(buildFile.toPath(), buildFileContent.toByteArray())
        gradleRunner = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("aggregateProcessTestCoverage")
    }

    @Test
    fun should_not_create_any_file_when_run_on_empty_dir() {
        val result = gradleRunner.build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":aggregateProcessTestCoverage")?.outcome)
        assertThat(File(testProjectDir, "build/process-test-coverage/all/report.json"))
            .doesNotExist()
    }

    @Test
    fun should_generate_expected_result_when_run_with_one_result() {
        copyDirectory(Paths.get("src/test/resources/one_result/"), testProjectDir.toPath())
        val result = gradleRunner.build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":aggregateProcessTestCoverage")?.outcome)
        assertResult()
    }

    @Test
    fun should_generate_expected_result_when_run_with_two_results() {
        copyDirectory(Paths.get("src/test/resources/two_results/"), testProjectDir.toPath())
        val result = gradleRunner.build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":aggregateProcessTestCoverage")?.outcome)
        assertResult()
    }

    @Test
    fun should_generate_expected_result_when_run_with_multiple_projects() {
        copyDirectory(Paths.get("src/test/resources/multiple_projects/"), testProjectDir.toPath())
        val settingsFileContent = """
            rootProject.name = 'process-coverage-gradle-plugin-test'
            include("module1")
            include("module2")
        """
        Files.write(settingsFile.toPath(), settingsFileContent.toByteArray())
        val result = gradleRunner.build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":aggregateProcessTestCoverage")?.outcome)
        assertResult()
    }

    @Test
    fun should_generate_expected_result_when_run_with_different_report_directory() {
        copyDirectory(Paths.get("src/test/resources/different_report_directory/"), testProjectDir.toPath())
        val buildFileContent = """
            plugins {
                id 'org.camunda.community.process_test_coverage.report-aggregator'
            }
            
            aggregateProcessTestCoverage {
                reportDirectory = 'build/camunda-tests'
                outputDirectory = 'aggregation'
            }
            
        """
        Files.write(buildFile.toPath(), buildFileContent.toByteArray())
        val result = gradleRunner.build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":aggregateProcessTestCoverage")?.outcome)
        assertResult("build/camunda-tests/aggregation/report.json")
    }

    private fun assertResult(expectedFile: String = "build/process-test-coverage/all/report.json") {
        assertThat(File(testProjectDir, expectedFile))
            .exists().isFile
            .content()
            .satisfies(Consumer {
                val actual = Gson().fromJson(it, JsonObject::class.java)
                val expected = Gson().fromJson(
                    File(testProjectDir, "expected_result.json").readText(),
                    JsonObject::class.java
                )
                assertThat(actual.getAsJsonArray("suites")).containsExactlyInAnyOrderElementsOf(
                    expected.getAsJsonArray("suites"))
                assertThat(actual.getAsJsonArray("models")).containsExactlyInAnyOrderElementsOf(
                    expected.getAsJsonArray("models"))
            })
    }

    @Throws(IOException::class)
    private fun copyDirectory(sourceDirectoryLocation: Path, destinationDirectoryLocation: Path) {
        Files.walk(sourceDirectoryLocation)
            .forEach { source: Path ->
                if (source != sourceDirectoryLocation) {
                    val destination: Path = Paths.get(
                        destinationDirectoryLocation.toString(),
                        source.toString().substring(sourceDirectoryLocation.toString().length)
                    )
                    Files.copy(source, destination)
                }
            }
    }

}
