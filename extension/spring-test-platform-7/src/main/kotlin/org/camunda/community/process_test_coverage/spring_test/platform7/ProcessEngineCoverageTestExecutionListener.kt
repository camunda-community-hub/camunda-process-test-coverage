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
