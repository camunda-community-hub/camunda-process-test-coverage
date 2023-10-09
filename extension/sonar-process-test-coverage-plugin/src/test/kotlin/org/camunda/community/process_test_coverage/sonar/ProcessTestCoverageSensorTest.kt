package org.camunda.community.process_test_coverage.sonar

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.sonar.api.batch.fs.internal.DefaultInputFile
import org.sonar.api.batch.fs.internal.TestInputFileBuilder
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.batch.sensor.internal.SensorContextTester
import org.sonar.api.config.internal.MapSettings
import java.io.File


class ProcessTestCoverageSensorTest {

    private val sensor = ProcessTestCoverageSensor()

    @Test
    fun testDescribe() {
        val descriptor: SensorDescriptor = mock(SensorDescriptor::class.java)
        sensor.describe(descriptor)
        verify(descriptor).name("Camunda Process Test Coverage")
    }

    @Test
    fun shouldSkipIfNoReport() {
        val context = SensorContextTester.create(File(""))
        val file = TestInputFileBuilder.create(
                "target/test-classes/one_result",
                "process.bpmn"
        ).setLanguage(BpmnLanguage.KEY).build()
        context.fileSystem().add(file)
        val settings = MapSettings()
        settings.setProperty(ReportsProvider.REPORT_PATHS_PROPERTY_KEY, "target/test-classes/one_result/unknown.json")
        context.setSettings(settings)
        sensor.execute(context)
        assertThat(context.measure(file.key(), ProcessTestCoverageMetrics.PROCESS_TEST_COVERAGE)).isNull()
    }

    @Test
    fun shouldAnalyseOneResult() {
        val context = SensorContextTester.create(File(""))
        val file = TestInputFileBuilder.create(
                "target/test-classes",
                "process.bpmn"
        ).setLanguage(BpmnLanguage.KEY).build()
        context.fileSystem().add(file)
        val settings = MapSettings()
        settings.setProperty(ReportsProvider.REPORT_PATHS_PROPERTY_KEY, "target/test-classes/one_result/report.json")
        context.setSettings(settings)
        sensor.execute(context)
        assertThat(context.measure(file.key(), ProcessTestCoverageMetrics.PROCESS_TEST_COVERAGE).value()).isEqualTo(100.0)
    }

    @Test
    fun shouldAnalyseTwoResults() {
        val context = SensorContextTester.create(File(""))
        val file = TestInputFileBuilder.create(
                "target/test-classes",
                "process.bpmn"
        ).setLanguage(BpmnLanguage.KEY).build()
        context.fileSystem().add(file)
        val settings = MapSettings()
        settings.setProperty(ReportsProvider.REPORT_PATHS_PROPERTY_KEY, "target/test-classes/two_results/**/report.json")
        context.setSettings(settings)
        sensor.execute(context)
        assertThat(context.measure(file.key(), ProcessTestCoverageMetrics.PROCESS_TEST_COVERAGE).value()).isEqualTo(100.0)
    }

    @Test
    fun shouldAnalyseMultipleModules() {
        val context = SensorContextTester.create(File(""))
        val file = TestInputFileBuilder.create(
                "target/test-classes",
                "process.bpmn"
        ).setLanguage(BpmnLanguage.KEY).build()
        context.fileSystem().add(file)
        val file2 = TestInputFileBuilder.create(
                "target/test-classes",
                "process2.bpmn"
        ).setLanguage(BpmnLanguage.KEY).build()
        context.fileSystem().add(file2)
        val settings = MapSettings()
        settings.setProperty(ReportsProvider.REPORT_PATHS_PROPERTY_KEY, "target/test-classes/multiple_projects/**/report.json")
        context.setSettings(settings)
        sensor.execute(context)
        assertThat(context.measure(file.key(), ProcessTestCoverageMetrics.PROCESS_TEST_COVERAGE).value()).isEqualTo(100.0)
        assertThat(context.measure(file2.key(), ProcessTestCoverageMetrics.PROCESS_TEST_COVERAGE).value()).isEqualTo((2.0 / 3.0) * 100.0)
    }

}