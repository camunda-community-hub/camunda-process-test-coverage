package org.camunda.bpm.extension.process_test_coverage.spring_test

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration enabling process test coverage in the camunda process engine.
 */
@Configuration
class ProcessEngineCoverageConfiguration {

    @Bean
    @ConditionalOnMissingBean(ProcessEngineCoverageProperties::class)
    fun defaultCoverageProperties() = ProcessEngineCoverageProperties()

}