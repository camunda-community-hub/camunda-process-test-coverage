package org.camunda.bpm.extension.process_test_coverage.engine

import org.camunda.bpm.extension.process_test_coverage.engine.ProcessCoverageConfigurator.initializeProcessCoverageExtensions
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration
import org.camunda.bpm.extension.process_test_coverage.engine.ProcessCoverageConfigurator

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