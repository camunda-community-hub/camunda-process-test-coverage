package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.List;

/**
 * @author ov7a
 */
public class DefaultCoverageTestRunStateFactory implements CoverageTestRunStateFactory {
    @Override
    public CoverageTestRunState create(String className, List<String> excludedProcessDefinitionKeys) {
        CoverageTestRunState coverageTestRunState = new DefaultCoverageTestRunState();
        coverageTestRunState.setTestClassName(className);
        coverageTestRunState.setExcludedProcessDefinitionKeys(excludedProcessDefinitionKeys);
        return coverageTestRunState;
    }
}
