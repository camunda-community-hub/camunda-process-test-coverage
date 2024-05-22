/*-
 * #%L
 * Camunda Process Test Coverage Core
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
package org.camunda.community.process_test_coverage.core.export

import com.google.gson.Gson
import org.camunda.community.process_test_coverage.core.model.Model
import org.camunda.community.process_test_coverage.core.model.Suite

/**
 * Exporter for Coverage State
 *
 * @author dominikhorn
 */
object CoverageStateJsonExporter {
    /**
     * Creates a Json String from the given input.
     *
     * @param suites Suites that should be exported
     * @param models Models that should be exported
     * @return XML String representation.
     */
    @JvmStatic
    fun createCoverageStateResult(suites: Collection<Suite> = emptyList(), models: Collection<Model> = emptyList()): String =
        Gson().toJson(CoverageStateResult(suites, models))

    @JvmStatic
    fun readCoverageStateResult(json: String): CoverageStateResult =
        Gson().fromJson(json, CoverageStateResult::class.java)

    @JvmStatic
    fun combineCoverageStateResults(json1: String, json2: String): String {
        val result1 = readCoverageStateResult(json1)
        val result2 = readCoverageStateResult(json2)
        return createCoverageStateResult(
                result1.suites + result2.suites,
                result1.models.plus(result2.models.filter { new -> !result1.models.map { model -> model.key }.contains(new.key) })
        )
    }

}
