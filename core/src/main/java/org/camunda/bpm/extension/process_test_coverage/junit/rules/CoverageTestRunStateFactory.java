package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.List;

/**
 * Factory for creting coverage run state.
 * @author ov7a
 */
public interface CoverageTestRunStateFactory {
    /**
     * Create the run state for a given class.
     * @param className class name.
     * @param excludedProcessDefinitionKeys list of excluded process definition keys.
     * @return coverage test run state.
     */
    CoverageTestRunState create(String className, List<String> excludedProcessDefinitionKeys);
}
