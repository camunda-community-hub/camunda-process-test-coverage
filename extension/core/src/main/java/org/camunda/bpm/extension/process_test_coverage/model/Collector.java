package org.camunda.bpm.extension.process_test_coverage.model;

/**
 * Interface for collection coverage data.
 *
 * @author dominikhorn
 */
public interface Collector {

    void addEvent(Event event);
}
