package org.camunda.community.process_test_coverage.tests.junit4.platform7.rules;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit4.platform7.rules.TestCoverageProcessEngineRule;
import org.camunda.community.process_test_coverage.junit4.platform7.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Test case starting an in-memory database-backed Process Engine. The aim of
 * the test is coverage calculation with the @Deployment annotated at method
 * level.
 */
@Deployment(resources = "process.bpmn")
public class ClassDeploymentMethodCoverageTest {

    private static final String PROCESS_DEFINITION_KEY = "process-test-coverage";

    @Rule
    public TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().withDetailedCoverageLogging().build();

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
