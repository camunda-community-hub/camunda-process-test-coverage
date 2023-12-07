package org.camunda.community.process_test_coverage.spring_test.platform7;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CoverageTestConfiguration {

    @Bean
    public ProcessEngineCoverageProperties processEngineCoverageProperties() {
        return ProcessEngineCoverageProperties.builder()
                .assertClassCoverageAtLeast(1.0)
                .build();
    }

}
