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
 * A suite includes several [Run] and contains the data for the coverage calculation.
 *
 * @author dominikhorn
 */
data class Suite(
    /**
     * The id of the suite
     */
    val id: String,
    /**
     * The name of the suite
     */
    val name: String
) : Coverage {

    /**
     * List of runs that are in
     */
    private val runs: MutableList<Run> = mutableListOf()

    /**
     * Adds a [Run] to the suite
     *
     * @param run
     */
    fun addRun(run: Run) {
        runs.add(run)
    }

    /**
     * Returns all events of the suite.
     *
     * @return events
     */
    override fun getEvents(): Collection<Event> =
        runs.map { it.getEvents() }.flatten()


    /**
     * Returns all events for the given modelkey.
     *
     * @param modelKey The key of the model.
     * @return events
     */
    override fun getEvents(modelKey: String): Collection<Event> =
        runs.map { it.getEvents(modelKey) }.flatten()

    fun getRun(runId: String): Run? {
        return runs.firstOrNull { it.id == runId }
    }
}
