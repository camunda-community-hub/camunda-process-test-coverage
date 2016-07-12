package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case starting an in-memory database-backed Process Engine. The aim of
 * the test is class coverage calculation with the @Deployment annotated at
 * class level.
 */
@Deployment(resources = "process.bpmn")
public class ClassDeploymentClassCoverageTest {

    private static final String PROCESS_DEFINITION_KEY = "process-test-coverage";

    @ClassRule
    @Rule
    public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().withDetailedCoverageLogging().build();

    @Test
    public void testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered() {

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "A");
        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        rule.addTestMethodCoverageAssertionMatcher(
                "testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered", greaterThan(6.9 / 11.0));
        rule.addTestMethodCoverageAssertionMatcher(
                "testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered", lessThan(7.1 / 11.0));

    }

    @Test
    public void testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "B");
        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

        rule.addTestMethodCoverageAssertionMatcher(
                "testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered", greaterThan(6.9 / 11.0));
        rule.addTestMethodCoverageAssertionMatcher(
                "testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered", lessThan(7.1 / 11.0));
    }

}
