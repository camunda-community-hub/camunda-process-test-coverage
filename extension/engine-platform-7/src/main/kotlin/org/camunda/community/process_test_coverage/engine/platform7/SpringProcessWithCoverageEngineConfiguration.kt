package org.camunda.community.process_test_coverage.engine.platform7

import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageConfigurator.initializeProcessCoverageExtensions
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration

/**
 * Spring process engine configuration additionally configuring
 * flow node, sequence flow and compensation listeners for process coverage
 * testing.
 *
 *
 * Created by lldata on 20-10-2016.
 */
class SpringProcessWithCoverageEngineConfiguration : SpringProcessEngineConfiguration() {
    override fun init() {
        initializeProcessCoverageExtensions(this)
        super.init()
    }
}