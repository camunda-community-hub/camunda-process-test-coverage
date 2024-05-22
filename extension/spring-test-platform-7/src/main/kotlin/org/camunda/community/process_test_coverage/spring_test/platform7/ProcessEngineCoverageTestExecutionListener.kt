/*-
 * #%L
 * Camunda Process Test Coverage Spring-Testing Platform 7
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
package org.camunda.community.process_test_coverage.spring_test.platform7

import org.camunda.bpm.engine.ProcessEngine
import org.camunda.community.process_test_coverage.core.model.DefaultCollector
import org.camunda.community.process_test_coverage.engine.platform7.ExecutionContextModelProvider
import org.camunda.community.process_test_coverage.engine.platform7.ProcessEngineAdapter
import org.camunda.community.process_test_coverage.spring_test.common.BaseProcessEngineCoverageTestExecutionListener
import org.springframework.test.context.TestContext

/**
 * Test execution listener for process test coverage.
 * Can be used with spring testing framework to get process test coverage in spring tests.
 *
 * @author Jan Rohwer
 */
class ProcessEngineCoverageTestExecutionListener : BaseProcessEngineCoverageTestExecutionListener() {

    /**
     * The state of the current run (class and current method).
     */
    private val coverageCollector = DefaultCollector(ExecutionContextModelProvider())

    override fun getCoverageCollector() = coverageCollector

    override fun isTestClassExcluded(testContext: TestContext): Boolean {
        return super.isTestClassExcluded(testContext)
                || testContext.applicationContext.getBeanNamesForType(ProcessEngine::class.java).isEmpty()
    }

    override fun beforeTestClass(testContext: TestContext) {
        super.beforeTestClass(testContext)
        if (!isTestClassExcluded(testContext)) {
            ProcessEngineAdapter(getProcessEngine(testContext), coverageCollector).initializeListeners()
        }
    }

    private fun getProcessEngine(testContext: TestContext) = testContext.applicationContext.getBean(ProcessEngine::class.java)

}
