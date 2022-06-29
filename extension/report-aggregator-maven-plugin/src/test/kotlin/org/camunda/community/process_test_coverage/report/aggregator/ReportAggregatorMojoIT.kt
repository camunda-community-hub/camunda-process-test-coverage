package org.camunda.community.process_test_coverage.report.aggregator

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.soebes.itf.extension.assertj.MavenITAssertions.assertThat
import com.soebes.itf.jupiter.extension.MavenJupiterExtension
import com.soebes.itf.jupiter.extension.MavenTest
import com.soebes.itf.jupiter.maven.MavenExecutionResult
import java.io.File
import java.util.function.Consumer

@MavenJupiterExtension
class ReportAggregatorMojoIT {

    @MavenTest
    fun should_generate_same_file_with_one_result(result: MavenExecutionResult) {
        assertThat(result).isSuccessful
            .project()
            .hasTarget()
            .withFile("process-test-coverage/all/report.json")
            .exists().isFile
            .hasSameTextualContentAs(File(result.mavenProjectResult.targetProjectDirectory,
                "target/process-test-coverage/org.camunda.community.process_test_coverage.report.aggregator.test.FirstTest/report.json"))
    }

    @MavenTest
    fun should_generate_combined_file_with_two_results(result: MavenExecutionResult) {
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
                assertThat(actual).isEqualTo(expected)
            })
    }

    @MavenTest
    fun should_generate_combined_file_with_multiple_projects(result: MavenExecutionResult) {
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
                assertThat(actual).isEqualTo(expected)
            })
    }

}