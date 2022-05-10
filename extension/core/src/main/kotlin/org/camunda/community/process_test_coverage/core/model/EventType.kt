package org.camunda.community.process_test_coverage.core.model

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
     * Transition take (only for sequence flow).
     */
    TAKE
}