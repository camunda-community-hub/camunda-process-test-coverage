package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.List;

/**
 * @author ov7a
 */
public interface CoverageTestRunStateFactory {
    CoverageTestRunState create(String className, List<String> excludedProcessDefinitionKeys);
}
