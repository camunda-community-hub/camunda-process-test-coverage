package org.camunda.community.process_test_coverage.core.model

/**
 * Interface for collection coverage data.
 *
 * @author dominikhorn
 */
interface Collector {
    /**
     * Adds a new event.
     * @param event event to add.
     */
    fun addEvent(event: Event)
}