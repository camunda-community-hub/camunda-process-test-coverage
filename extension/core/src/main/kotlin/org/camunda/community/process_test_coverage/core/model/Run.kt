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

