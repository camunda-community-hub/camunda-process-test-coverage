package org.camunda.bpm.extension.process_test_coverage.examples;

import org.camunda.bpm.extension.process_test_coverage.spring_test.ProcessEngineCoverageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public ProcessEngineCoverageProperties coverageProperties() {
        return new ProcessEngineCoverageProperties() {

            @Override
            public Double coverageAtLeast() {
                return 0.6;
            }

            @Override
            public boolean handleTestMethodCoverage() {
                return false;
            }
        };
    }

}