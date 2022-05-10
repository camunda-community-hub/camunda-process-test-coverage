package org.camunda.community.process_test_coverage.core.model

import java.time.Instant

/**
 * An event is a recording of a specific action that happened in the engine.
 *
 * @author dominikhorn
 */
data class Event(
    /**
         * Source of the event (flow node, sequence flow or dmn rule).
         */
        val source: EventSource,
    /**
         * Type of the event.
         */
        val type: EventType,
    /**
         * Definition key of the element where the event happened.
         */
        val definitionKey: String,
    /**
         * Type of the events element.
         */
        val elementType: String,
    /**
         * Key of the model in which the event happened (process definition key).
         */
        val modelKey: String,
    /**
         * Timestamp when the event happened.
         */
        val timestamp: Long = Instant.now().epochSecond
)