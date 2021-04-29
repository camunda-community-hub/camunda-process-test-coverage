package org.camunda.bpm.extension.process_test_coverage.export

import org.camunda.bpm.extension.process_test_coverage.model.Model
import org.camunda.bpm.extension.process_test_coverage.model.Suite

data class CoverageStateResult(
    val suites: Collection<Suite>,
    val models: Collection<Model>
)