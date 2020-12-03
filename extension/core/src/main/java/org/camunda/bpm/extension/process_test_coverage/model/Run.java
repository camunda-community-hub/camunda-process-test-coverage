package org.camunda.bpm.extension.process_test_coverage.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A run is a specific execution in a {@link Suite}
 *
 * @author dominikhorn
 */
public class Run implements Coverage {

    /**
     * The id of the run
     */
    private final String id;

    /**
     * The name of the run
     */
    private final String name;

    /**
     * List of {@link Event} that happened during the run.
     */
    private final List<Event> events = new ArrayList<>();

    public Run(final String id, final String name) {
        this.name = name;
        this.id = id;
    }

    /**
     * Adds an {@link Event} to the run.
     *
     * @param event
     */
    public void addEvent(final Event event) {
        this.events.add(event);
    }

    /**
     * Returns all events of the run.
     *
     * @return events
     */
    @Override
    public Collection<Event> getEvents() {
        return this.events;
    }

    /**
     * Returns all events for the given modelkey.
     *
     * @param modelKey The key of the model.
     * @return events
     */
    @Override
    public Collection<Event> getEvents(final String modelKey) {
        return this.events.stream()
                .filter(event -> event.getModelKey().equals(modelKey))
                .collect(Collectors.toList());
    }

    /**
     * Returns all events for the given modelkey distinct by definitionKey.
     *
     * @param modelKey The key of the model.
     * @return events
     */
    @Override
    public Collection<Event> getEventsDistinct(final String modelKey) {
        final Map<String, Event> eventMap = new HashMap<>();
        this.getEvents(modelKey).forEach(event -> {
            if (!eventMap.containsKey(event.getDefinitionKey())) {
                eventMap.put(event.getDefinitionKey(), event);
            }
        });
        return eventMap.values();
    }

    /**
     * Calculates the coverage for the given model.
     *
     * @param model
     * @return coverage
     */
    @Override
    public double calcuateCoverage(final Model model) {
        return this.getEventsDistinct(model.getKey()).size() / (double) model.getTotalElementCount();
    }

    /**
     * Calculates the coverage for the given models.
     *
     * @param models
     * @return coverage
     */
    @Override
    public double calcuateCoverage(final Collection<Model> models) {
        //Todo what about elements that are only started
        final long totalElementCount = models.stream()
                .mapToLong(Model::getTotalElementCount)
                .sum();

        final long coveredElementCount = models.stream()
                .map(Model::getKey)
                .map(this::getEventsDistinct)
                .mapToLong(Collection::size)
                .sum();

        return coveredElementCount / (double) totalElementCount;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}
