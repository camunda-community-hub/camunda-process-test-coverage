package org.camunda.bpm.extension.process_test_coverage.bpmn;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class BpmnBusinessRuleTaskCoverageTest {
    /**
     * XXXX &lt;camunda&gt; namespace support works from Camunda BPM versions
     * 7.4.0, 7.3.3, 7.2.6 onwards.
     */

    private static final String PROCESS_DEFINITION_KEY = "process-business-rule-task";

    @ClassRule
    public static TestCoverageProcessEngineRule classRule = TestCoverageProcessEngineRuleBuilder.createClassRule() //
    .reportCoverageAfter().assertCoverage(Matchers.equalTo(1.0)).build();

    @Rule // Method rule does the deployment ATM
    public TestCoverageProcessEngineRule deployRule = TestCoverageProcessEngineRuleBuilder.create().build();

    @Test
    @Deployment(resources = "businessRuleTask.bpmn")
    public void testGo() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("decision", "go");
        classRule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
    }

    @Test
    @Deployment(resources = "businessRuleTask.bpmn")
    public void testStay() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("decision", "stay");
        classRule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
    }

}
