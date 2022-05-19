package org.camunda.community.process_test_coverage.tests.junit4.platform7.bpmn;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit4.platform7.rules.TestCoverageProcessEngineRule;
import org.camunda.community.process_test_coverage.junit4.platform7.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * Also deploys businessRuleTask.bpmn, but doesn't cover anything and expecting
 * zero coverage. This is for checking side-effects by the other test class.
 */
public class BpmnBusinessRuleTaskNotCoveredTest {

    @ClassRule
    @Rule
    public static TestCoverageProcessEngineRule classRule = TestCoverageProcessEngineRuleBuilder.create()
            .withDetailedCoverageLogging().build();

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
