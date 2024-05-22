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

import org.camunda.community.process_test_coverage.core.model.Coverage
import org.camunda.community.process_test_coverage.core.model.Event
import org.camunda.community.process_test_coverage.core.model.Model
import org.camunda.community.process_test_coverage.core.model.Suite

data class CoverageStateResult(
    val suites: Collection<Suite>,
    val models: Collection<Model>
) : Coverage {
    override fun getEvents() = suites.map { it.getEvents() }.flatten()

    override fun getEvents(modelKey: String) = suites.map { it.getEvents(modelKey) }.flatten()

}
