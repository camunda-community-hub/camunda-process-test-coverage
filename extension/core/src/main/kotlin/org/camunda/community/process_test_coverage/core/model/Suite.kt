package org.camunda.community.process_test_coverage.core.model

import java.util.*
import java.util.function.Consumer

/**
 * A suite includes several [Run] and contains the data for the coverage calculation.
 *
 * @author dominikhorn
 */
class Suite(
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


    /**
     * Returns all events for the given modelkey distinct by definitionKey.
     *
     * @param modelKey The key of the model.
     * @return events
     */
    override fun getEventsDistinct(modelKey: String): Collection<Event> {

        // FIXME: WHAT IS IT TRYING TO DO?
        val eventMap: MutableMap<String, Event> = HashMap()
        this.getEvents(modelKey).forEach(Consumer { event: Event ->
            if (!eventMap.containsKey(event.definitionKey)) {
                eventMap[event.definitionKey] = event
            }
        })
        return eventMap.values
    }

    /**
     * Calculates the coverage for the given model.
     *
     * @param model
     * @return coverage
     */
    override fun calculateCoverage(model: Model): Double {
        return getEventsDistinct(model.key).size.toDouble() / model.totalElementCount.toDouble()
    }

    /**
     * Calculates the coverage for the given models.
     *
     * @param models
     * @return coverage
     */
    override fun calculateCoverage(models: Collection<Model>): Double {
        //Todo what about elements that are only started
        val totalElementCount = models.map { it.totalElementCount }.sum()
        val coveredElementCount = models.map { getEventsDistinct(modelKey = it.key).size }.sum()
        return coveredElementCount.toDouble() / totalElementCount.toDouble()
    }

    fun getRun(runId: String): Run? {
        return runs.firstOrNull { it.id == runId }
    }
}