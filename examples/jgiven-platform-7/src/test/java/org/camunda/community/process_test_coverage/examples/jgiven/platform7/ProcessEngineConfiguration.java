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
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageConfigurator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ProcessEngineConfiguration {

    private static final ProcessEngine processEngine;

    static {
        ProcessEngineConfigurationImpl configuration = new StandaloneInMemoryTestConfiguration();
        ProcessCoverageConfigurator.initializeProcessCoverageExtensions(configuration);
        processEngine = configuration.buildProcessEngine();
    }

    @Bean
    public static ProcessEngine getProcessEngine() {
        return processEngine;
    }

}
