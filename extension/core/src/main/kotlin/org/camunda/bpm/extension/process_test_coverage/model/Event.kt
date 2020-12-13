package org.camunda.bpm.extension.process_test_coverage.model

import java.time.Instant

/**
 * An event is a recording of a specific action that happened in the engine.
 *
 * @author dominikhorn
 */
data class Event(
        /**
         * Source of the Event
         */
        val source: EventSource,
        /**
         * Type of the event
         */
        val type: EventType,
        /**
         * Definition key of the events element
         */
        val definitionKey: String,
        /**
         * Type of the events element
         */
        val elementType: String,
        /**
         * Key of the model in which the event happened
         */
        val modelKey: String,
        /**
         * Timestamp when the event happened
         */
        val timestamp: Long = Instant.now().epochSecond
)