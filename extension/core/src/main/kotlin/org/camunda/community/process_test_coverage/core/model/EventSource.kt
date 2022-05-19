package org.camunda.community.process_test_coverage.core.model

/**
 * The source of an event.
 *
 * @author dominikhorn
 */
enum class EventSource {
    /**
     * A flow node as a source.
     */
    FLOW_NODE,

    /**
     * Sequence flow as a source.
     */
    SEQUENCE_FLOW,

    /**
     * DMN Rule evaluation.
     */
    DMN_RULE
}