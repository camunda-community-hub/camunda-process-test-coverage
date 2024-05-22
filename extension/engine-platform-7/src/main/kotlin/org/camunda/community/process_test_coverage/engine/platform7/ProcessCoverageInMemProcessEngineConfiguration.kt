/*-
 * #%L
 * Camunda Process Test Coverage Engine Platform 7
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
package org.camunda.community.process_test_coverage.engine.platform7

import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageConfigurator.initializeProcessCoverageExtensions
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration

/**
 * Standalone in memory process engine configuration additionally configuring
 * flow node, sequence flow and compensation listeners for process coverage
 * testing.
 *
 * @author z0rbas
 */
class ProcessCoverageInMemProcessEngineConfiguration : StandaloneInMemProcessEngineConfiguration() {
    override fun init() {
        initializeProcessCoverageExtensions(this)
        super.init()
    }
}
