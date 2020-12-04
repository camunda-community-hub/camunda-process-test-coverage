package org.camunda.bpm.extension.process_test_coverage.export

import camundajar.impl.com.google.gson.Gson
import org.camunda.bpm.extension.process_test_coverage.model.Model
import org.camunda.bpm.extension.process_test_coverage.model.Suite

/**
 * Exporter for Coverage State
 *
 * @author dominikhorn
 */
object CoverageStateJsonExporter {
    /**
     * Creates a Json String from the given input.
     *
     * @param suites Suites that should be exported
     * @param models Models that should be exported
     * @return XML String representation.
     */
    @JvmStatic
    fun createCoverageStateResult(suites: Collection<Suite> = emptyList(), models: Collection<Model> = emptyList()): String {
        val result = CoverageStateResult(suites, models)
        val gson = Gson()
        return gson.toJson(result)
    }
}