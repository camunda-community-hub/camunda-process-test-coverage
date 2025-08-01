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

import org.sonar.api.Plugin
import org.sonar.api.config.PropertyDefinition

class ProcessTestCoveragePlugin : Plugin {

    companion object {
        const val FILE_SUFFIXES_KEY = "sonar.bpmn.file.suffixes"
        const val PLUGIN_CATEGORY = "Process Test Coverage"
    }

    override fun define(context: Plugin.Context) {
        context.addExtensions(
            BpmnLanguage::class.java,
            BpmnQualityProfile::class.java,
            ProcessTestCoverageMetrics::class.java,
            ProcessTestCoverageSensor::class.java,
            ProcessTestCoverageProjectSensor::class.java,
            ProcessTestCoveragePage::class.java,
            PropertyDefinition.builder(FILE_SUFFIXES_KEY)
                .defaultValue(BpmnLanguage.DEFAULT_FILE_SUFFIXES)
                .name("File suffixes")
                .description("List of suffixes for files to analyze. To not filter, leave the list empty.")
                .onConfigScopes(PropertyDefinition.ConfigScope.PROJECT)
                .category(PLUGIN_CATEGORY)
                .multiValues(true)
                .build(),
            PropertyDefinition.builder(ReportsProvider.REPORT_PATHS_PROPERTY_KEY)
                .name("Report paths")
                .onConfigScopes(PropertyDefinition.ConfigScope.PROJECT)
                .multiValues(true)
                .category(PLUGIN_CATEGORY)
                .description(
                    "Paths to Camunda Process Test Coverage JSON report files. Each path can be either absolute or relative" +
                            " to the project base directory. Wildcard patterns are accepted (*, ** and ?)."
                )
                .build(),
        )
    }

}
