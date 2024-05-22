/*-
 * #%L
 * Camunda Process Test Coverage Spring-Testing Platform 7
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
package org.camunda.community.process_test_coverage.spring_test.platform7

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration
import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageConfigurator
import org.camunda.bpm.spring.boot.starter.configuration.Ordering
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration
import org.camunda.community.process_test_coverage.spring_test.common.ProcessEngineCoverageProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

/**
 * Configuration enabling process test coverage in the camunda process engine.
 */
@Configuration
class ProcessEngineCoverageConfiguration {

    @Bean
    @Order(Ordering.DEFAULT_ORDER + 1)
    fun camundaConfiguration() = object : AbstractCamundaConfiguration() {
        override fun preInit(processEngineConfiguration: SpringProcessEngineConfiguration) {
            ProcessCoverageConfigurator.initializeProcessCoverageExtensions(processEngineConfiguration)
        }
    }

    @Bean
    @ConditionalOnMissingBean(ProcessEngineCoverageProperties::class)
    fun defaultCoverageProperties() = ProcessEngineCoverageProperties()

}
