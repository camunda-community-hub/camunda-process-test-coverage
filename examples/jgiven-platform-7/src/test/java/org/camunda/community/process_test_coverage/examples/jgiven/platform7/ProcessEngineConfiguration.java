package org.camunda.community.process_test_coverage.examples.jgiven.platform7;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageConfigurator;
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

    @Bean
    public static ProcessEngine getProcessEngine() {
        return processEngine;
    }

}
