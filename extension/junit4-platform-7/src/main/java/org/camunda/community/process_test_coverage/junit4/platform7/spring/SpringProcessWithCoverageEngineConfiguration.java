package org.camunda.community.process_test_coverage.junit4.platform7.spring;

/*-
 * #%L
 * Camunda Process Test Coverage JUnit4 Platform 7
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

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageConfigurator;

/**
 * Spring process engine configuration additionally configuring
 * flow node, sequence flow and compensation listeners for process coverage
 * testing.
 * <p>
 * Created by lldata on 20-10-2016.
 */
public class SpringProcessWithCoverageEngineConfiguration extends SpringProcessEngineConfiguration {

    @Override
    protected void init() {
        ProcessCoverageConfigurator.initializeProcessCoverageExtensions(this);
        super.init();
    }
}
