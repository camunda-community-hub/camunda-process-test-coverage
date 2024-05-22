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
 * A run is a specific execution in a [Suite]
 *
 * @author dominikhorn
 */
data class Run(
    /**
     * The id of the run
     */
    val id: String,
    /**
     * The name of the run
     */
    val name: String
) : Coverage {

    /**
     * List of [Event] that happened during the run.
     */
    private val events: MutableList<Event> = ArrayList()

    /**
     * Adds an [Event] to the run.
     *
     * @param event
     */
    fun addEvent(event: Event) {
        events.add(event)
    }

    /**
     * Returns all events of the run.
     *
     * @return events
     */
    override fun getEvents(): Collection<Event> {
        return events
    }

    /**
     * Returns all events for the given modelkey.
     *
     * @param modelKey The key of the model.
     * @return events
     */
    override fun getEvents(modelKey: String): Collection<Event> {
        return events.filter { it.modelKey == modelKey }

    }

}

