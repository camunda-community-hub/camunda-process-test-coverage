package org.camunda.bpm.extension.process_test_coverage.bpmn;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.ProcessDeploymentRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/** Also deploys businessRuleTask.bpmn, but doesn't cover anything and expecting zero coverage. This is for checking side-effects by the other test class. */
public class BpmnBusinessRuleTaskNotCoveredTest {

    @ClassRule
    public static TestCoverageProcessEngineRule classRule = TestCoverageProcessEngineRuleBuilder.createClassRule() //
    .reportCoverageAfter().assertCoverage(Matchers.equalTo(0.0)).build();

    @Rule // Method rule does the deployment ATM
    public ProcessDeploymentRule deployRule = TestCoverageProcessEngineRuleBuilder.buildDeployRule();

     
    @Test
    @Deployment(resources = "businessRuleTask.bpmn")
    /**
     * this test class/method exists only to trigger coverage checks. Another
     * class should have tested the same deployments before. There should be no
     * previous results left.
     */
    public void testDoingNothing() {
    }

}
