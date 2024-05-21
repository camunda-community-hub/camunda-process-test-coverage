package org.camunda.community.process_test_coverage.spring_test.platform8

import io.camunda.zeebe.process.test.assertions.BpmnAssert
import mu.KLogging
import org.camunda.community.process_test_coverage.core.model.DefaultCollector
import org.camunda.community.process_test_coverage.engine.platform8.ZeebeModelProvider
import org.camunda.community.process_test_coverage.engine.platform8.createEvents
import org.camunda.community.process_test_coverage.spring_test.common.BaseProcessEngineCoverageTestExecutionListener
import org.springframework.test.context.TestContext

/**
 * Test execution listener for process test coverage.
 * Can be used with spring testing framework to get process test coverage in spring tests.
 *
 * @author Jan Rohwer
 */
class ProcessEngineCoverageTestExecutionListener : BaseProcessEngineCoverageTestExecutionListener() {

    companion object : KLogging()

    /**
     * The state of the current run (class and current method).
     */
    private val coverageCollector = DefaultCollector(ZeebeModelProvider())

    /**
     * Map of test method name to start record position of the test.
     */
    private val methodRecordPosition = mutableMapOf<String, Long>()


    override fun getCoverageCollector() = coverageCollector

    /**
     * Handles creating the run if a relevant test method is called.
     */
    override fun beforeTestMethod(testContext: TestContext) {
        super.beforeTestMethod(testContext)
        if (!isTestMethodExcluded(testContext)) {
            methodRecordPosition[testContext.testMethod.name] = BpmnAssert.getRecordStream().records().maxOfOrNull { it.position } ?: -1
        }
    }

    /**
     * Handles evaluating the test method coverage after a relevant test method is finished.
     */
    override fun afterTestMethod(testContext: TestContext) {
        if (!isTestMethodExcluded(testContext)) {
            createEvents(coverageCollector, methodRecordPosition[testContext.testMethod.name]!!)
        }
        super.afterTestMethod(testContext)
    }

    override fun isTestClassExcluded(testContext: TestContext) =
        super.isTestClassExcluded(testContext)
                || !isRecordStreamSet()

    private fun isRecordStreamSet() =
        try {
            BpmnAssert.getRecordStream()
            true
        } catch (e: AssertionError) {
            false
        }

}
