package org.camunda.community.process_test_coverage.core.export

import com.google.gson.Gson
import org.camunda.community.process_test_coverage.core.model.Model
import org.camunda.community.process_test_coverage.core.model.Suite

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
    fun createCoverageStateResult(suites: Collection<Suite> = emptyList(), models: Collection<Model> = emptyList()): String =
        Gson().toJson(CoverageStateResult(suites, models))

    @JvmStatic
    fun readCoverageStateResult(json: String): CoverageStateResult =
        Gson().fromJson(json, CoverageStateResult::class.java)

    @JvmStatic
    fun combineCoverageStateResults(json1: String, json2: String): String {
        val result1 = readCoverageStateResult(json1)
        val result2 = readCoverageStateResult(json2)
        return createCoverageStateResult(
                result1.suites + result2.suites,
                result1.models.plus(result2.models.filter { new -> !result1.models.map { model -> model.key }.contains(new.key) })
        )
    }

}