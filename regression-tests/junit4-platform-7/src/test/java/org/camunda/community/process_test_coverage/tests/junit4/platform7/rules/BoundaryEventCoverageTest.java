package org.camunda.community.process_test_coverage.tests.junit4.platform7.rules;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit4.platform7.rules.TestCoverageProcessEngineRule;
import org.camunda.community.process_test_coverage.junit4.platform7.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class BoundaryEventCoverageTest {

    private static final String BPMN_PATH = "boundaryEventTest.bpmn";
    private static final String PROCESS_DEFINITION_KEY = "BoundaryEventTest";

    @Rule
    public TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().withDetailedCoverageLogging().build();

    @Test
    @Deployment(resources = BPMN_PATH)
    public void testPathA() {

        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    }

}
