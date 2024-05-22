package org.camunda.community.process_test_coverage.examples.junit5.platform7;

/*-
 * #%L
 * Camunda Process Test Coverage Example JUnit5 Platform 7
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

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

//@ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = "order-process.bpmn")
public class OrderProcessTest {

    @RegisterExtension
    public static ProcessEngineCoverageExtension extension = ProcessEngineExtensionProvider.extension;
//    public static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder(
//            new ProcessCoverageInMemProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_FULL)).build();

    @Test
    public void shouldExecuteHappyPath() {
        final ProcessInstance instance = this.startProcess();

        assertThat(instance).isWaitingAt("Task_ProcessOrder");

        complete(task(), withVariables("orderOk", true));

        assertThat(instance).isWaitingAt("Task_DeliverOrder");

        complete(task());

        assertThat(instance)
                .hasPassed("Event_OrderProcessed")
                .isEnded();
    }

    @Test
    public void shouldCancelOrder() {
        final ProcessInstance instance = this.startProcess();

        assertThat(instance).isWaitingAt("Task_ProcessOrder");

        complete(task(), withVariables("orderOk", false));

        assertThat(instance)
                .hasPassed("Event_OrderCancelled")
                .isEnded();
    }


    private ProcessInstance startProcess() {
        return runtimeService().startProcessInstanceByKey("order-process");
    }


}
