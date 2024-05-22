package org.camunda.community.process_test_coverage.examples.jgiven.platform7;

/*-
 * #%L
 * Camunda Process Test Coverage Example JGiven Platform 7
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.camunda.community.process_test_coverage.spring_test.platform7.ProcessEngineCoverageTestExecutionListener;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
// needed to shut down the spring context after the test, so that it doesn't interfere with the other tests
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import({CoverageTestConfiguration.class, ProcessEngineConfiguration.class})
@TestExecutionListeners(value = ProcessEngineCoverageTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@Deployment(resources = "order-process.bpmn")
public class OrderProcessJGivenInMemTest extends ScenarioTest<OrderProcessStage, OrderProcessStage, OrderProcessStage> {

    @Rule
    public final ProcessEngineRule processEngineRule = new ProcessEngineRule(ProcessEngineConfiguration.getProcessEngine());

    @ScenarioState
    private final ProcessEngine camunda = ProcessEngineConfiguration.getProcessEngine();

    @BeforeClass
    public static void reset() {
        // Process engine is cached in the thread and therefore the engine from earlier tests would be used
        AbstractAssertions.reset();
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
