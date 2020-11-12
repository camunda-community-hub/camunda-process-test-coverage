package org.camunda.bpm.extension.process_test_coverage.model;

import java.util.Collection;

public interface Coverage {

    Collection<Event> getEvents();

    Collection<Event> getEvents(String modelKey);

    Collection<Event> getEventsDistinct(String modelKey);

    double calcuateCoverage(Model model);

    double calcuateCoverage(Collection<Model> models);
}
