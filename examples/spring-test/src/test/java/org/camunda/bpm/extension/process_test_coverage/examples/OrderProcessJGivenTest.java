package org.camunda.bpm.extension.process_test_coverage.examples;

import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.camunda.bpm.extension.process_test_coverage.spring_test.ProcessEngineCoverageConfiguration;
import org.camunda.bpm.extension.process_test_coverage.spring_test.ProcessEngineCoverageTestExecutionListener;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({CoverageTestConfiguration.class, ProcessEngineCoverageConfiguration.class})
@TestExecutionListeners(value = ProcessEngineCoverageTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class OrderProcessJGivenTest extends ScenarioTest<OrderProcessStage, OrderProcessStage, OrderProcessStage> {

    @Autowired
    private ProcessEngine processEngine;

    @Rule
    @ScenarioState
    public ProcessEngineRule processEngineRule;

    @PostConstruct
    public void initRule() {
        processEngineRule = new ProcessEngineRule(processEngine);
    }

    @Test
    public void shouldExecuteHappyPath() {

        given()
            .process_is_started()
            .and().process_waits_in("Task_ProcessOrder")
        .when()
            .task_is_completed_with_variables(new VariableMapImpl(BpmnAwareTests.withVariables("orderOk", true)))
            .and().process_waits_in("Task_DeliverOrder")
            .and().task_is_completed_with_variables(new VariableMapImpl())
        .then()
            .process_is_finished()
            .process_has_passed("Event_OrderProcessed");
    }

    @Test
    public void shouldCancelOrder() {
        given()
            .process_is_started()
            .and().process_waits_in("Task_ProcessOrder")
        .when()
            .task_is_completed_with_variables(new VariableMapImpl(BpmnAwareTests.withVariables("orderOk", false)))
        .then()
            .process_is_finished().process_has_passed("Event_OrderCancelled");
    }

}
