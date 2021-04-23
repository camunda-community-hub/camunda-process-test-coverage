package org.camunda.bpm.extension.process_test_coverage.model

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
    fun getEventsDistinct(modelKey: String): Collection<Event>
    fun calculateCoverage(model: Model): Double
    fun calculateCoverage(models: Collection<Model>): Double
}