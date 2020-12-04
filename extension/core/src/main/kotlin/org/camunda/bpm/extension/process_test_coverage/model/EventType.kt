package org.camunda.bpm.extension.process_test_coverage.model

/**
 * Type of an event as happend during Camunda execution.
 *
 * @author dominikhorn
 */
enum class EventType {
    /**
     * Flow node start.
     */
    START,

    /**
     * Flow node end.
     */
    END,

    /**
     * Transition take.
     */
    TAKE
}