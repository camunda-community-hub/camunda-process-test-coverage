package org.camunda.community.process_test_coverage.examples.junit5.platform8.cpt;

/*-
 * #%L
 * Camunda Process Test Coverage Example JUnit5 Platform 8
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

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.response.ProcessInstanceEvent;
import io.camunda.process.test.api.CamundaAssert;
import io.camunda.process.test.api.CamundaProcessTest;
import org.camunda.community.process_test_coverage.junit5.platform8.cpt.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CamundaProcessTest
@ExtendWith(ProcessEngineCoverageExtension.class)
public class OrderProcessTest {

//    @RegisterExtension
//    public static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder().build();

    private CamundaClient camundaClient;

    @BeforeEach
    public void setup() {
        camundaClient.newDeployResourceCommand()
                .addResourceFromClasspath("order-process.bpmn")
                .send()
                .join();
    }

    @Test
    public void shouldExecuteHappyPath() throws InterruptedException, TimeoutException {
        final ProcessInstanceEvent instance = this.startProcess();

        waitForUserTaskAndComplete("Task_ProcessOrder", Collections.singletonMap("orderOk", true));

        CamundaAssert.assertThat(instance).hasActiveElement("Task_DeliverOrder", 1);

        waitForUserTaskAndComplete("Task_DeliverOrder", Collections.emptyMap());

        CamundaAssert.assertThat(instance)
                .hasCompletedElement("Event_OrderProcessed", 1)
                .isCompleted();
    }

    @Test
    public void shouldCancelOrder() throws Exception {
        final ProcessInstanceEvent instance = this.startProcess();

        waitForUserTaskAndComplete("Task_ProcessOrder", Collections.singletonMap("orderOk", false));

        CamundaAssert.assertThat(instance)
                .hasCompletedElement("Event_OrderCancelled", 1)
                .isCompleted();
    }

    private void waitForUserTaskAndComplete(String userTaskId, Map<String, Object> variables) throws InterruptedException, TimeoutException {
        // Now get all user tasks
        List<ActivatedJob> jobs = camundaClient.newActivateJobsCommand()
                .jobType("io.camunda.zeebe:userTask").maxJobsToActivate(1).workerName("waitForUserTaskAndComplete").send().join().getJobs();

        // Should be only one
        assertFalse(jobs.isEmpty(), "Job for user task '" + userTaskId + "' does not exist");
        ActivatedJob userTaskJob = jobs.get(0);
        // Make sure it is the right one
        if (userTaskId != null) {
            assertEquals(userTaskId, userTaskJob.getElementId());
        }

        // And complete it passing the variables
        if (variables!=null && !variables.isEmpty()) {
            camundaClient.newCompleteCommand(userTaskJob.getKey()).variables(variables).send().join();
        } else {
            camundaClient.newCompleteCommand(userTaskJob.getKey()).send().join();
        }
    }

    private ProcessInstanceEvent startProcess() {
        return camundaClient.newCreateInstanceCommand() //
                .bpmnProcessId("order-process")
                .latestVersion() //
                .send().join();
    }
}
