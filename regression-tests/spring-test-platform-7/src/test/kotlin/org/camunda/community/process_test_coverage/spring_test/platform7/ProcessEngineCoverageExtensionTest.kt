package org.camunda.community.process_test_coverage.spring_test.platform7

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit
import java.io.File


class ProcessEngineCoverageExtensionTest {

    @Test
    fun should_use_different_report_path_if_configured() {

        val reportPath = File("build/process-test-coverage")

        EngineTestKit.engine(JupiterEngineDescriptor.ENGINE_ID)
            .selectors(selectClass(DifferentReportDirectoryTest::class.java))
            .configurationParameter("junit.jupiter.conditions.deactivate", "org.junit.*DisabledCondition")
            .execute()
            .containerEvents()
            .assertStatistics { stats -> stats.started(2).succeeded(2) }

        // Assert all files were generated.
        assertThat(reportPath).isDirectory()
        assertThat(reportPath.resolve("org.camunda.community.process_test_coverage.spring_test.platform7.DifferentReportDirectoryTest/report.json")).isFile()
    }

}