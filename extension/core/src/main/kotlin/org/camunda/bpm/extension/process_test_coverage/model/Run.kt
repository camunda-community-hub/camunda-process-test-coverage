package org.camunda.bpm.extension.process_test_coverage.model

import java.util.*
import java.util.function.Consumer

/**
 * A run is a specific execution in a [Suite]
 *
 * @author dominikhorn
 */
class Run(
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

    /**
     * Returns all events for the given modelkey distinct by definitionKey.
     *
     * @param modelKey The key of the model.
     * @return events
     */
    override fun getEventsDistinct(modelKey: String): Collection<Event> {

        // FIXME: WHAT DOES IT SUPPOSE TO DO?

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
        return getEventsDistinct(model.key).size.toDouble() / model.totalElementCount
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
        val coveredElementCount = models.map { getEventsDistinct(it.key).size }.sum()
        return coveredElementCount.toDouble() / totalElementCount
    }
}

