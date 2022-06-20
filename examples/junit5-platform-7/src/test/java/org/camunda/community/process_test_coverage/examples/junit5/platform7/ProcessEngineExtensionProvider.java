package org.camunda.community.process_test_coverage.examples.junit5.platform7;

import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageInMemProcessEngineConfiguration;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;

public class ProcessEngineExtensionProvider {

    public static final ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder(
            new ProcessCoverageInMemProcessEngineConfiguration().setHistory(ProcessEngineConfiguration.HISTORY_FULL)
    ).build();

}
