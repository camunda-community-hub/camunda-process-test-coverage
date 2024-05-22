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

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.function.Supplier;

public class OrderProcessBean implements Supplier<ProcessInstance> {

    private final ProcessEngine processEngine;
    private ProcessInstance processInstance;

    public OrderProcessBean(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Override
    public ProcessInstance get() {
        return processInstance;
    }

    public void start() {
        processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("order-process");
    }

}
