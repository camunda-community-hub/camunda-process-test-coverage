package org.camunda.community.process_test_coverage.core.export

import org.camunda.community.process_test_coverage.core.model.Model
import org.camunda.community.process_test_coverage.core.model.Suite

data class CoverageStateResult(
    val suites: Collection<Suite>,
    val models: Collection<Model>
)