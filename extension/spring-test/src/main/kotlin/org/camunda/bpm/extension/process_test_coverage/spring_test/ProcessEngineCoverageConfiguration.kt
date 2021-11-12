package org.camunda.bpm.extension.process_test_coverage.spring_test

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration
import org.camunda.bpm.extension.process_test_coverage.engine.ProcessCoverageConfigurator
import org.camunda.bpm.spring.boot.starter.configuration.Ordering
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

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