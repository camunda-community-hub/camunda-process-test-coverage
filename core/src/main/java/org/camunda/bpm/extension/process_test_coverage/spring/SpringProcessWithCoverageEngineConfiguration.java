package org.camunda.bpm.extension.process_test_coverage.spring;

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.ProcessCoverageConfigurator;

/**
 * Spring process engine configuration additionally configuring
 * flow node, sequence flow and compensation listeners for process coverage
 * testing.
 *
 * Created by lldata on 20-10-2016.
 */
public class SpringProcessWithCoverageEngineConfiguration extends SpringProcessEngineConfiguration {

	public void init() {
		ProcessCoverageConfigurator.initializeProcessCoverageExtensions(this);
		super.init();
	}
}
