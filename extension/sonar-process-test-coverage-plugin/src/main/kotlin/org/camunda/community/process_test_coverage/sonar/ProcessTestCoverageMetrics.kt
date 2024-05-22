/*-
 * #%L
 * Camunda Process Test Coverage Sonar Plugin
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.Metric
import org.sonar.api.measures.Metrics


class ProcessTestCoverageMetrics : Metrics {

    override fun getMetrics(): List<Metric<*>> {
        return listOf<Metric<*>>(PROCESS_TEST_COVERAGE, PROCESS_TEST_COVERAGE_REPORT)
    }

    companion object {
        val PROCESS_TEST_COVERAGE: Metric<Double> = Metric.Builder("process_test_coverage", "Process Test Coverage", Metric.ValueType.PERCENT)
                .setDescription("Coverage on process nodes")
                .setDirection(Metric.DIRECTION_BETTER)
                .setQualitative(true)
                .setDomain(CoreMetrics.DOMAIN_COVERAGE)
                .create()

        val PROCESS_TEST_COVERAGE_REPORT: Metric<String> = Metric.Builder("process_test_coverage_report", "Process Test Coverage Report", Metric.ValueType.DATA)
                .setDescription("JSON Report for process test coverage")
                .setQualitative(false)
                .setDomain(CoreMetrics.DOMAIN_COVERAGE)
                .setHidden(false)
                .setDeleteHistoricalData(true)
                .create()
    }
}
