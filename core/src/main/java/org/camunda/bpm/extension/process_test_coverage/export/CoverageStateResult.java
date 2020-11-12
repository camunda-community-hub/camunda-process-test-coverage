package org.camunda.bpm.extension.process_test_coverage.export;

import org.camunda.bpm.extension.process_test_coverage.model.Model;
import org.camunda.bpm.extension.process_test_coverage.model.Suite;

import java.util.Collection;

public class CoverageStateResult {

    private final Collection<Suite> suites;

    private final Collection<Model> models;

    public CoverageStateResult(final Collection<Suite> suites, final Collection<Model> models) {
        this.suites = suites;
        this.models = models;
    }

    public Collection<Suite> getSuites() {
        return this.suites;
    }

    public Collection<Model> getModels() {
        return this.models;
    }
}
