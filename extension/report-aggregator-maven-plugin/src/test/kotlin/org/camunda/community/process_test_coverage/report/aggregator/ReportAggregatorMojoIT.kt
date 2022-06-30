package org.camunda.community.process_test_coverage.report.aggregator

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.soebes.itf.extension.assertj.MavenITAssertions.assertThat
import com.soebes.itf.jupiter.extension.MavenJupiterExtension
import com.soebes.itf.jupiter.extension.MavenProject
import com.soebes.itf.jupiter.extension.MavenTest
import com.soebes.itf.jupiter.maven.MavenExecutionResult
import java.io.File
import java.util.function.Consumer

@MavenJupiterExtension
class ReportAggregatorMojoIT {

    @MavenTest
    fun one_result(result: MavenExecutionResult) {
        assertThat(result).isSuccessful
            .project()
            .hasTarget()
            .withFile("process-test-coverage/all/report.json")
            .exists().isFile
            .hasSameTextualContentAs(File(result.mavenProjectResult.targetProjectDirectory,
                "target/process-test-coverage/test.FirstTest/report.json"))
    }

    @MavenTest
    fun two_results(result: MavenExecutionResult) {
        assertThat(result).isSuccessful
            .project()
            .hasTarget()
            .withFile("process-test-coverage/all/report.json")
            .exists().isFile
            .content()
            .satisfies(Consumer {
                val actual = Gson().fromJson(it, JsonObject::class.java)
                val expected = Gson().fromJson(
                    File(result.mavenProjectResult.targetProjectDirectory, "expected_result.json").readText(),
                    JsonObject::class.java
                )
                assertThat(actual.getAsJsonArray("suites")).containsExactlyInAnyOrderElementsOf(
                    expected.getAsJsonArray("suites"))
                assertThat(actual.getAsJsonArray("models")).containsExactlyInAnyOrderElementsOf(
                    expected.getAsJsonArray("models"))
            })
    }

    @MavenTest
    fun multiple_projects(result: MavenExecutionResult) {
        assertThat(result).isSuccessful
            .project()
            .hasTarget()
            .withFile("process-test-coverage/all/report.json")
            .exists().isFile
            .content()
            .satisfies(Consumer {
                val actual = Gson().fromJson(it, JsonObject::class.java)
                val expected = Gson().fromJson(
                    File(result.mavenProjectResult.targetProjectDirectory, "expected_result.json").readText(),
                    JsonObject::class.java
                )
                assertThat(actual.getAsJsonArray("suites")).containsExactlyInAnyOrderElementsOf(
                    expected.getAsJsonArray("suites"))
                assertThat(actual.getAsJsonArray("models")).containsExactlyInAnyOrderElementsOf(
                    expected.getAsJsonArray("models"))
            })
    }

}
