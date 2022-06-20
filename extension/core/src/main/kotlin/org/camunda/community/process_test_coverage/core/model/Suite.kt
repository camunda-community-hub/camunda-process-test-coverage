package org.camunda.community.process_test_coverage.core.model

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

    fun getRun(runId: String): Run? {
        return runs.firstOrNull { it.id == runId }
    }
}