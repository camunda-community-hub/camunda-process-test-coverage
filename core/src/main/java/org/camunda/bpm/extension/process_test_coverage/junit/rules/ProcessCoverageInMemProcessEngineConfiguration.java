package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;

/**
 * Standalone in memory process engine configuration additionally configuring
 * flow node, sequence flow and compensation listeners for process coverage
 * testing.
 * 
 * @author z0rbas
 *
 */
public class ProcessCoverageInMemProcessEngineConfiguration extends StandaloneInMemProcessEngineConfiguration {

    @Override
    protected void init() {
        ProcessCoverageConfigurator.initializeProcessCoverageExtensions(this);
        super.init();
    }

}
