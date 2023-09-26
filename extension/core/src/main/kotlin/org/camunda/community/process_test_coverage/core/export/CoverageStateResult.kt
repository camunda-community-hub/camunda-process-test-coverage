package org.camunda.community.process_test_coverage.core.export

import org.camunda.community.process_test_coverage.core.model.Coverage
import org.camunda.community.process_test_coverage.core.model.Event
import org.camunda.community.process_test_coverage.core.model.Model
import org.camunda.community.process_test_coverage.core.model.Suite

data class CoverageStateResult(
    val suites: Collection<Suite>,
    val models: Collection<Model>
) : Coverage {
    override fun getEvents() = suites.map { it.getEvents() }.flatten()

    override fun getEvents(modelKey: String) = suites.map { it.getEvents(modelKey) }.flatten()

}