package org.camunda.bpm.extension.process_test_coverage.model

/**
 * The source of an event.
 *
 * @author dominikhorn
 */
enum class EventSource {
    FLOW_NODE,
    SEQUENCE_FLOW,
    DMN_RULE
}