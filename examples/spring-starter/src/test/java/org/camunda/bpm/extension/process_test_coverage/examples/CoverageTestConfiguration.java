package org.camunda.bpm.extension.process_test_coverage.examples;

import org.camunda.bpm.extension.process_test_coverage.spring_test.ProcessEngineCoverageProperties;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CoverageTestConfiguration {

    @Bean
    public ProcessEngineCoverageProperties processEngineCoverageProperties() {
        return new ProcessEngineCoverageProperties() {
            @Nullable
            @Override
            public Double coverageAtLeast() {
                return 0.9;
            }
        };
    }

}
