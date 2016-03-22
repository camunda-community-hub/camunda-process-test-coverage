package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class MultipleDeploymentsForIndividualTestsTest {

    private static final String PROCESS_DEFINITION_KEY = "process-test-coverage";
    
    @Rule
    @ClassRule
    public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create()
            .reportCoverageAfter().build();

    @Test
    @Deployment(resources = "process.bpmn")
    public void testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "A");
        ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
                variables);
        
        rule.assertTestCoverage("testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered", greaterThan(6.9 / 11.0));
        rule.assertTestCoverage("testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered", lessThan(7.1 / 11.0));

    }

    @Test
    @Deployment(resources = "process.bpmn")
    public void testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "B");
        ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
                variables);
        
        rule.assertTestCoverage("testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered", greaterThan(6.9 / 11.0));
        rule.assertTestCoverage("testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered", lessThan(7.1 / 11.0));
    }
}
