package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class LinkEventCoverageTest {

    private static final String BPMN_PATH = "linkEventTest.bpmn";
    private static final String PROCESS_DEFINITION_KEY = "LinkEventTest";

    @Rule
    public TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().withDetailedCoverageLogging().build();

    @Test
    @Deployment(resources = BPMN_PATH)
    public void testPathA() {

        System.out.println("Deployments: " + rule.getRepositoryService().getDeploymentResourceNames("1") + " "
                + rule.getRepositoryService().createProcessDefinitionQuery().list());
        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
    }

    // public void testPathB() {
    // Map<String, Object> variables = new HashMap<String, Object>();
    // variables.put("path", "B");
    // rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
    // variables);
    // }

}
