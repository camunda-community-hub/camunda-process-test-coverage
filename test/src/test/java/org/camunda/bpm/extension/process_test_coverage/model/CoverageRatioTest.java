package org.camunda.bpm.extension.process_test_coverage.model;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
@Deployment(resources = "coverage-ratio-test.bpmn")
public class CoverageRatioTest {

    @Rule
    @ClassRule
    public static TestCoverageProcessEngineRule rule
        = TestCoverageProcessEngineRuleBuilder.create().build();

    private Map<String, Object> variables = new HashMap<String, Object>();

    @Before
    public void init() {
        List all = new ArrayList();
        all.add(1);
        all.add(2);
        variables.put("all", all);
        variables.put("ok", true);
    }

    public void startProcess() {
        rule.getRuntimeService().startProcessInstanceByKey("testCoverageRatio", variables);
    }

    public void completeTask() {
        TaskService taskService = rule.getProcessEngine().getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        taskService.complete(task.getId());
    }

    @Test
    public void testClassOk() {
        startProcess();
        completeTask();
    }

    @Test
    public void testClassNotOk() {
        variables.put("ok", false);
        startProcess();
        completeTask();
    }

}
