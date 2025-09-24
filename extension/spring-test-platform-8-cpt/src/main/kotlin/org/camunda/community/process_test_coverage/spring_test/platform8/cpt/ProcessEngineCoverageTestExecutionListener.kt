/*-
 * #%L
 * Camunda Process Test Coverage Spring-Testing Platform 8
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
package org.camunda.community.process_test_coverage.spring_test.platform8.cpt

import io.camunda.process.test.api.CamundaProcessTestContext
import org.camunda.community.process_test_coverage.core.model.DefaultCollector
import org.camunda.community.process_test_coverage.engine.platform8.cpt.Camunda8ModelProvider
import org.camunda.community.process_test_coverage.engine.platform8.cpt.createEvents
import org.camunda.community.process_test_coverage.spring_test.common.BaseProcessEngineCoverageTestExecutionListener
import org.springframework.test.context.TestContext

/**
 * Test execution listener for process test coverage.
 * Can be used with spring testing framework to get process test coverage in spring tests.
 *
 * @author Jan Rohwer
 */
class ProcessEngineCoverageTestExecutionListener : BaseProcessEngineCoverageTestExecutionListener() {

    private var camundaProcessTestContext: CamundaProcessTestContext? = null

    /**
     * The state of the current run (class and current method).
     */
    private val coverageCollector = DefaultCollector(
        Camunda8ModelProvider { camundaProcessTestContext ?: throw IllegalStateException() }
    )

    override fun getCoverageCollector() = coverageCollector

    override fun beforeTestMethod(testContext: TestContext) {
        super.beforeTestMethod(testContext)
        camundaProcessTestContext = testContext.applicationContext.getBean(CamundaProcessTestContext::class.java)
    }

    /**
     * Handles evaluating the test method coverage after a relevant test method is finished.
     */
    override fun afterTestMethod(testContext: TestContext) {
        if (!isTestMethodExcluded(testContext)) {
            createEvents(camundaProcessTestContext!!, coverageCollector)
        }
        super.afterTestMethod(testContext)
        camundaProcessTestContext = null
    }

}
