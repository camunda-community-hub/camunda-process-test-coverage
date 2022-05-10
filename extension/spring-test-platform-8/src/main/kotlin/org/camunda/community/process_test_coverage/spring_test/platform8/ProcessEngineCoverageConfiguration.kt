package org.camunda.community.process_test_coverage.spring_test.platform8

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