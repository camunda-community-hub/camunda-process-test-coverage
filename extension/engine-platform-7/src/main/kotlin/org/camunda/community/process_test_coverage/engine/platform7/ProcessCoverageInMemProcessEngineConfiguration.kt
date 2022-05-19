package org.camunda.community.process_test_coverage.engine.platform7

import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageConfigurator.initializeProcessCoverageExtensions
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration

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