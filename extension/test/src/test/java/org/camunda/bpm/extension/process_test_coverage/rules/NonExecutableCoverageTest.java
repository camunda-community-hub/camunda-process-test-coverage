package org.camunda.bpm.extension.process_test_coverage.rules;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class NonExecutableCoverageTest {

	private static final String PROCESS_DEFINITION_KEY = "process-test-coverage";

	@Rule
	public TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create()
			.withDetailedCoverageLogging().build();

	@Test
    @Deployment(resources = "nonExecutables.bpmn")
    public void testCoverageIgnoringNonExecutables() {

        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);

        rule.addTestMethodCoverageAssertionMatcher("testCoverageIgnoringNonExecutables", is(1.0));

	}

}
