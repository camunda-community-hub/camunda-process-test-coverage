package org.camunda.bpm.extension.process_test_coverage.examples;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.engine.ProcessCoverageConfigurator;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ProcessEngineConfiguration {

    private static final ProcessEngine processEngine;

    static {
        ProcessEngineConfigurationImpl configuration = new StandaloneInMemoryTestConfiguration();
        ProcessCoverageConfigurator.initializeProcessCoverageExtensions(configuration);
        processEngine = configuration.buildProcessEngine();
    }

    public static ProcessEngineRule getProcessEngineRule() {
        return new ProcessEngineRule(processEngine);
    }

    @Bean
    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

}
