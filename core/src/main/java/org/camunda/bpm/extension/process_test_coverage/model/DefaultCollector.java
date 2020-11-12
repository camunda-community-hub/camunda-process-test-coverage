package org.camunda.bpm.extension.process_test_coverage.model;

import org.camunda.bpm.extension.process_test_coverage.engine.ModelProvider;

import java.util.*;

/**
 * Default Collector for the coverage.
 *
 * @author dominikhorn
 */
public class DefaultCollector implements Collector {

    private final List<String> excludedProcessDefinitionKeys = new ArrayList<>();
    private final Map<String, Suite> suites = new HashMap<>();
    private final Map<String, Model> models = new HashMap<>();
    private final ModelProvider modelProvider = new ModelProvider();
    private Run activeRun;
    private Suite activeSuite;

    @Override
    public void addEvent(final Event event) {
        if (this.excludedProcessDefinitionKeys.contains(event.getModelKey())) {
            return;
        }

        if (this.activeRun == null) {
            throw new IllegalArgumentException("No active suite available");
        }
        this.saveModel(event);
        this.activeRun.addEvent(event);
    }

    public Suite createSuite(final Suite suite) {
        if (this.suites.containsKey(suite.getId())) {
            throw new IllegalArgumentException("Suite already exists");
        }

        this.suites.put(suite.getId(), suite);
        return suite;
    }

    public Run createRun(final Run run, final String suiteId) {
        final Suite suite = this.getSuite(suiteId);
        suite.addRun(run);
        return run;
    }

    public void activateSuite(final String suiteId) {
        final Suite suite = this.getSuite(suiteId);
        this.activeSuite = suite;
    }

    public void activateRun(final String runId) {
        if (this.activeSuite == null) {
            throw new IllegalArgumentException("No active suite available");
        }

        final Run run = this.activeSuite.getRun(runId)
                .orElseThrow(() -> new IllegalArgumentException("Run doesn't exist"));

        this.activeRun = run;
    }

    public void setExcludedProcessDefinitionKeys(final List<String> excludedProcessDefinitionKeys) {
        this.excludedProcessDefinitionKeys.clear();
        if (excludedProcessDefinitionKeys != null) {
            this.excludedProcessDefinitionKeys.addAll(excludedProcessDefinitionKeys);
        }
    }

    private Suite getSuite(final String suiteId) {
        if (!this.suites.containsKey(suiteId)) {
            throw new IllegalArgumentException("Suite doesn't exist");
        }
        return this.suites.get(suiteId);
    }

    private void saveModel(final Event event) {
        if (this.models.containsKey(event.getModelKey())) {
            return;
        }

        final Model model = this.modelProvider.getModel(event.getModelKey());
        this.models.put(model.getKey(), model);
    }

    public Run getActiveRun() {
        return this.activeRun;
    }

    public Suite getActiveSuite() {
        return this.activeSuite;
    }

    public Collection<Model> getModels() {
        return this.models.values();
    }

    public Map<String, Suite> getSuites() {
        return this.suites;
    }
}
