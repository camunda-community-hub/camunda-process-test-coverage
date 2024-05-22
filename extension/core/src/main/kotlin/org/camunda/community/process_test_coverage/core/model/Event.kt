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

import java.time.Instant

/**
 * An event is a recording of a specific action that happened in the engine.
 *
 * @author dominikhorn
 */
data class Event(
    /**
         * Source of the event (flow node, sequence flow or dmn rule).
         */
        val source: EventSource,
    /**
         * Type of the event.
         */
        val type: EventType,
    /**
         * Definition key of the element where the event happened.
         */
        val definitionKey: String,
    /**
         * Type of the events element.
         */
        val elementType: String,
    /**
         * Key of the model in which the event happened (process definition key).
         */
        val modelKey: String,
    /**
         * Timestamp when the event happened.
         */
        val timestamp: Long = Instant.now().epochSecond
)
