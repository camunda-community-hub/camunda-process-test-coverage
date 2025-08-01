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

import org.sonar.api.config.Configuration
import org.sonar.api.resources.AbstractLanguage


class BpmnLanguage(private val config: Configuration) : AbstractLanguage(KEY, NAME) {

    companion object {
        const val NAME = "BPMN"
        const val KEY = "bpmn"
        const val DEFAULT_FILE_SUFFIXES = ".bpmn"
    }

    override fun getFileSuffixes(): Array<String> {
        var suffixes = config.getStringArray(ProcessTestCoveragePlugin.FILE_SUFFIXES_KEY)
            .filterNotNull()
            .toTypedArray()
        if (suffixes.isEmpty()) {
            suffixes = arrayOf(DEFAULT_FILE_SUFFIXES)
        }
        return suffixes
    }

}
