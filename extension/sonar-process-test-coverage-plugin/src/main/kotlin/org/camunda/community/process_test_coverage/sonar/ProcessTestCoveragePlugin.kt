package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.Plugin
import org.sonar.api.config.PropertyDefinition
import org.sonar.api.resources.Qualifiers


class ProcessTestCoveragePlugin : Plugin {

    override fun define(context: Plugin.Context) {
        context.addExtensions(BpmnLanguage::class.java, BpmnQualityProfile::class.java)
        context.addExtension(ProcessTestCoverageMetrics::class.java)
        context.addExtensions(ProcessTestCoverageSensor::class.java, ProcessTestCoverageProjectSensor::class.java)
        context.addExtension(ProcessTestCoveragePage::class.java)
        context.addExtension(
            PropertyDefinition.builder(ReportsProvider.REPORT_PATHS_PROPERTY_KEY)
                .onQualifiers(Qualifiers.PROJECT)
                .multiValues(true)
                .category("Process Test Coverage")
                .description(
                    "Paths to Camunda Process Test Coverage JSON report files. Each path can be either absolute or relative" +
                            " to the project base directory. Wildcard patterns are accepted (*, ** and ?)."
                )
                .build()
        )
    }

}