package org.camunda.community.process_test_coverage.core.model

import java.util.function.Consumer

interface Coverage {

    /**
     * Retrieve all collected events.
     */
    fun getEvents(): Collection<Event>

    /**
     * Retrieve events collected for particular model.
     * @param modelKey process model definition key.
     * @return list of events collected for a provided model.
     */
    fun getEvents(modelKey: String): Collection<Event>

    /**
     * Returns all events for the given modelkey distinct by definitionKey.
     *
     * @param modelKey The key of the model.
     * @return events
     */
    fun getEventsDistinct(modelKey: String): Collection<Event> {
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
    fun calculateCoverage(model: Model) =
        getEventsDistinct(model.key).size.toDouble() / model.totalElementCount.toDouble()

    /**
     * Calculates the coverage for the given models.
     *
     * @param models
     * @return coverage
     */
    fun calculateCoverage(models: Collection<Model>): Double {
        //Todo what about elements that are only started
        val filteredModels = models.filter { getEvents().any { evt -> evt.modelKey == it.key } }
        val totalElementCount = filteredModels.sumOf { it.totalElementCount }
        val coveredElementCount = filteredModels.sumOf { getEventsDistinct(modelKey = it.key).size }
        return coveredElementCount.toDouble() / totalElementCount.toDouble()
    }

}