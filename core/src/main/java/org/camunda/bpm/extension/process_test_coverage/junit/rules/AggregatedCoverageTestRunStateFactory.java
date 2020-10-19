package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.List;

/**
 * @author ov7a
 */
public class AggregatedCoverageTestRunStateFactory implements CoverageTestRunStateFactory {

    private AggregatedCoverageTestRunState coverageTestRunStateInstance;
    private CoverageTestRunStateFactory coverageTestRunStateFactory;

    public AggregatedCoverageTestRunStateFactory(AggregatedCoverageTestRunState coverageTestRunStateInstance) {
        this(coverageTestRunStateInstance, new DefaultCoverageTestRunStateFactory());
    }

    public AggregatedCoverageTestRunStateFactory(AggregatedCoverageTestRunState coverageTestRunStateInstance, CoverageTestRunStateFactory coverageTestRunStateFactory) {
        this.coverageTestRunStateInstance = coverageTestRunStateInstance;
        this.coverageTestRunStateFactory = coverageTestRunStateFactory;
    }

    @Override
    public CoverageTestRunState create(String className, List<String> excludedProcessDefinitionKeys) {
        CoverageTestRunState newState = coverageTestRunStateFactory.create(className, excludedProcessDefinitionKeys);
        coverageTestRunStateInstance.switchToNewState(newState);
        return coverageTestRunStateInstance;
    }
}
