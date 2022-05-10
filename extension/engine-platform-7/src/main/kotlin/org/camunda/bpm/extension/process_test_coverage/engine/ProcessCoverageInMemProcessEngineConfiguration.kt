package org.camunda.bpm.extension.process_test_coverage.engine

import org.camunda.bpm.extension.process_test_coverage.engine.ProcessCoverageConfigurator.initializeProcessCoverageExtensions
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration
import org.camunda.bpm.extension.process_test_coverage.engine.ProcessCoverageConfigurator

/**
 * Standalone in memory process engine configuration additionally configuring
 * flow node, sequence flow and compensation listeners for process coverage
 * testing.
 *
 * @author z0rbas
 */
class ProcessCoverageInMemProcessEngineConfiguration : StandaloneInMemProcessEngineConfiguration() {
    override fun init() {
        initializeProcessCoverageExtensions(this)
        super.init()
    }
}