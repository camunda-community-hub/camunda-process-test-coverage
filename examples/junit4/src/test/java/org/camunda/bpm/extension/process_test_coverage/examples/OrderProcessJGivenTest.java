package org.camunda.bpm.extension.process_test_coverage.examples;

import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

@Deployment(resources = "order-process.bpmn")
public class OrderProcessJGivenTest extends ScenarioTest<OrderProcessStage, OrderProcessStage, OrderProcessStage> {

    @Rule
    @ClassRule
    @ScenarioState
    public static final ProcessEngineRule processEngineRule = TestCoverageProcessEngineRuleBuilder.create().build();

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
