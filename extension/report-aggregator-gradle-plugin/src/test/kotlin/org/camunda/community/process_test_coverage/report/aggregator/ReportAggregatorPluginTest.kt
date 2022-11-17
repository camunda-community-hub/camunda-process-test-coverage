package org.camunda.community.process_test_coverage.report.aggregator

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReportAggregatorPluginTest {

    @Test
    fun reportAggregatorTest() {
        val project: Project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.camunda.community.process_test_coverage.report-aggregator")
        assertTrue(
            project.pluginManager.hasPlugin("org.camunda.community.process_test_coverage.report-aggregator")
        )
        assertNotNull(project.tasks.getByName("aggregate-process-test-coverage"))
    }

}