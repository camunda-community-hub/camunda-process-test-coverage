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
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@Deployment(resources = "collapsed-subprocess.bpmn")
public class CollapsedSubProcessTest {

    @RegisterExtension
    public static ProcessEngineCoverageExtension extension = ProcessEngineExtensionProvider.extension;

    @Test
    public void shouldExecuteHappyPath() {
        final ProcessInstance instance = this.startProcess();

        assertThat(instance).isWaitingAt("SubTask");

        complete(task());

        assertThat(instance).isWaitingAt("TheTask");

        complete(task());

        assertThat(instance).isEnded();
    }


    private ProcessInstance startProcess() {
        return runtimeService().startProcessInstanceByKey("CollapsedSubProcessTest");
    }


}
