package org.camunda.bpm.extension.process_test_coverage.spring;

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.extension.process_test_coverage.engine.ProcessCoverageConfigurator;

/**
 * Spring process engine configuration additionally configuring
 * flow node, sequence flow and compensation listeners for process coverage
 * testing.
 * <p>
 * Created by lldata on 20-10-2016.
 */
public class SpringProcessWithCoverageEngineConfiguration extends SpringProcessEngineConfiguration {

    @Override
    protected void init() {
        ProcessCoverageConfigurator.initializeProcessCoverageExtensions(this);
        super.init();
    }
}
