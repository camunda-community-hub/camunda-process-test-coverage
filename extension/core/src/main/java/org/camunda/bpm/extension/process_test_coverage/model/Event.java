package org.camunda.bpm.extension.process_test_coverage.model;

import java.time.Instant;

/**
 * An event is a recording of a specific action that happened in the engine.
 *
 * @author dominikhorn
 */
public class Event {

    /**
     * Source of the Event
     */
    private final EventSource source;

    /**
     * Type of the event
     */
    private final EventType type;

    /**
     * Id of the model in which the event happened
     */
    private final String modelId;

    /**
     * Key of the model in which the event happened
     */
    private final String modelKey;

    /**
     * Definition key of the events element
     */
    private final String definitionKey;

    /**
     * Type of the events element
     */
    private final String elementType;

    /**
     * Timestamp when the event happened
     */
    private final Long timestamp;

    public Event(final EventSource source, final EventType type, final String definitionKey, final String elementType, final String modelId, final String modelkey) {
        this.source = source;
        this.type = type;
        this.modelId = modelId;
        this.definitionKey = definitionKey;
        this.elementType = elementType;
        this.modelKey = modelkey;
        this.timestamp = Instant.now().getEpochSecond();
    }

    public EventSource getSource() {
        return this.source;
    }

    public EventType getType() {
        return this.type;
    }

    public String getModelId() {
        return this.modelId;
    }

    public String getModelKey() {
        return this.modelKey;
    }

    public String getDefinitionKey() {
        return this.definitionKey;
    }

    public String getElementType() {
        return this.elementType;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }
}
