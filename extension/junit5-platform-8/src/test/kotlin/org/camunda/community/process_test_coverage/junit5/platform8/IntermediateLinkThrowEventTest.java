package org.camunda.community.process_test_coverage.junit5.platform8;

/*-
 * #%L
 * Camunda Process Test Coverage JUnit5 Platform 8
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

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;

@ZeebeProcessTest
public class IntermediateLinkThrowEventTest {

    @RegisterExtension
    public static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder().build();

    private ZeebeClient zeebe;
    private ZeebeTestEngine engine;

    @BeforeEach
    public void setup() {
        zeebe.newDeployResourceCommand()
                .addResourceFromClasspath("intermediate-link-throw-event.bpmn")
                .send()
                .join();
    }

    @Test
    public void should_have_100_percent_coverage_with_intermediate_link_throw_event() {
        extension.addTestMethodCoverageCondition("should_have_100_percent_coverage_with_intermediate_link_throw_event()",
                new Condition<>(percentage -> percentage == 1, "matches if the coverage ratio is 100%"));
        final ProcessInstanceEvent instance = this.startProcess();
        assertThat(instance).isCompleted();
    }

    private ProcessInstanceEvent startProcess() {
        return zeebe.newCreateInstanceCommand() //
                .bpmnProcessId("Testprocess")
                .latestVersion() //
                .send().join();
    }

}
