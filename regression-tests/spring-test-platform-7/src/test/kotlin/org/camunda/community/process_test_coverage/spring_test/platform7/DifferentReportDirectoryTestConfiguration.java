package org.camunda.community.process_test_coverage.spring_test.platform7;

import org.camunda.community.process_test_coverage.spring_test.common.ProcessEngineCoverageProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DifferentReportDirectoryTestConfiguration {

    @Bean
    public ProcessEngineCoverageProperties processEngineCoverageProperties() {
        return ProcessEngineCoverageProperties.builder()
                .assertClassCoverageAtLeast(1.0)
                .reportDirectory("build/process-test-coverage")
                .build();
    }

}
