package org.camunda.community.process_test_coverage.report.aggregator

import com.soebes.itf.extension.assertj.MavenITAssertions.assertThat

import com.soebes.itf.jupiter.extension.MavenJupiterExtension
import com.soebes.itf.jupiter.extension.MavenTest
import com.soebes.itf.jupiter.maven.MavenExecutionResult
import java.io.File

@MavenJupiterExtension
class ReportAggregatorMojoIT {

    @MavenTest
    fun should_generate_same_file_with_one_result(result: MavenExecutionResult) {
        assertThat(result).isSuccessful
            .project()
            .hasTarget()
            .withFile("process-test-coverage/all/report.json")
            .hasSameTextualContentAs(File(result.mavenProjectResult.targetProjectDirectory,
                "target/process-test-coverage/org.camunda.community.process_test_coverage.report.aggregator.test.FirstTest/report.json"))
    }

    @MavenTest
    fun should_generate_combined_file_with_two_results(result: MavenExecutionResult) {
        assertThat(result).isSuccessful
            .project()
            .hasTarget()
            .withFile("process-test-coverage/all/report.json")
            .hasSameTextualContentAs(File(result.mavenProjectResult.targetProjectDirectory, "expected_result.json"))
    }

    @MavenTest
    fun should_generate_combined_file_with_multiple_projects(result: MavenExecutionResult) {
        assertThat(result).isSuccessful
            .project()
            .hasTarget()
            .withFile("process-test-coverage/all/report.json")
            .hasSameTextualContentAs(File(result.mavenProjectResult.targetProjectDirectory, "expected_result.json"))
    }

}