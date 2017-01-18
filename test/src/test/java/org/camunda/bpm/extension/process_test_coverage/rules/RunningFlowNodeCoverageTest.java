package org.camunda.bpm.extension.process_test_coverage.rules;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This test should register a still uncompleted/running flow node as such. The
 * color of the flow node should differ from completed flow nodes in the
 * coverage diagram.
 * 
 * @author z0rbas
 *
 */
public class RunningFlowNodeCoverageTest {

    public static final String BPMN_PATH = "processStillRunning.bpmn";

    @Rule
    public TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    // TODO implement incident coverage handling and refactor this test to a
    // separate class
    @Test(expected = ProcessEngineException.class)
    @Deployment(resources = { RunningFlowNodeCoverageTest.BPMN_PATH })
    public void shouldCoverGatewayAsStillRunningWithException() {

        rule.getRuntimeService().startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY);

    }

    @Test
    @Deployment(resources = { RunningFlowNodeCoverageTest.BPMN_PATH })
    public void shouldCoverManualTaskAsStillRunning() {

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "B");
        final ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(
                CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables);

        assertFalse("The process instance should still be running!", processInstance.isEnded());

        final Task runningTask = rule.getTaskService().createTaskQuery().active().taskDefinitionKey(
                "UserTask_B").singleResult();
        assertNotNull("One task instance should still be running!", runningTask);

    }

}
