package org.camunda.community.process_test_coverage.examples.spring_starter.platform8;

import org.camunda.community.process_test_coverage.spring_test.platform8.ProcessEngineCoverageProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CoverageTestConfiguration {

    @Bean
    public ProcessEngineCoverageProperties processEngineCoverageProperties() {
        return ProcessEngineCoverageProperties.builder()
                .assertClassCoverageAtLeast(0.9)
                .build();
    }

}
