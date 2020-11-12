package org.camunda.bpm.extension.process_test_coverage.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A suite includes several {@link Run} and contains the data for the coverage calculation.
 *
 * @author dominikhorn
 */
public class Suite implements Coverage {

    /**
     * The id of the suite
     */
    private final String id;

    /**
     * The name of the suite
     */
    private final String name;

    /**
     * List of runs that are in
     */
    private final List<Run> runs = new ArrayList<>();

    public Suite(final String id, final String name) {
        this.name = name;
        this.id = id;
    }

    /**
     * Adds a {@link Run} to the suite
     *
     * @param run
     */
    public void addRun(final Run run) {
        this.runs.add(run);
    }

    /**
     * Returns all events of the suite.
     *
     * @return events
     */
    @Override
    public Collection<Event> getEvents() {
        return this.runs.stream()
                .map(Run::getEvents)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns all events for the given modelkey.
     *
     * @param modelKey The key of the model.
     * @return events
     */
    @Override
    public Collection<Event> getEvents(final String modelKey) {
        return this.runs.stream()
                .map(run -> run.getEvents(modelKey))
                .flatMap(Collection::stream)
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

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Optional<Run> getRun(final String runId) {
        return this.runs.stream()
                .filter(run -> run.getId().equals(runId))
                .findFirst();
    }

}
