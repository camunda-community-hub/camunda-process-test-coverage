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
package org.camunda.community.process_test_coverage.core.model

/**
 * The source of an event.
 *
 * @author dominikhorn
 */
enum class EventSource {
    /**
     * A flow node as a source.
     */
    FLOW_NODE,

    /**
     * Sequence flow as a source.
     */
    SEQUENCE_FLOW,

    /**
     * DMN Rule evaluation.
     */
    DMN_RULE
}
