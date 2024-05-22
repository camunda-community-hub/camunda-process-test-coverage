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

import io.holunda.camunda.bpm.extension.jgiven.JGivenProcessStage;
import io.holunda.camunda.bpm.extension.jgiven.ProcessStage;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

@JGivenProcessStage
public class OrderProcessStage extends ProcessStage<OrderProcessStage, OrderProcessBean> {

    public OrderProcessStage process_is_started() {
        processInstanceSupplier = new OrderProcessBean(camunda);
        processInstanceSupplier.start();
        assertThat(processInstanceSupplier.get()).isNotNull();
        assertThat(processInstanceSupplier.get()).isStarted();
        return self();
    }

}
