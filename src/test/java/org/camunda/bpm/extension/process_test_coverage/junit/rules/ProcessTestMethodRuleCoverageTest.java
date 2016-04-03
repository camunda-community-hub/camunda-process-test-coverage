package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ProcessTestMethodRuleCoverageTest {

	private static final String PROCESS_DEFINITION_KEY = "process-test-coverage";
	
	@Rule
	public TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create()
			.reportCoverageAfter().build();

	@Test
	@Deployment(resources = "process.bpmn")
	public void testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("path", "A");
		ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
				variables);
		
		rule.addTestMethodCoverageAssertionMatcher("testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered", greaterThan(6.9 / 11.0));
	    rule.addTestMethodCoverageAssertionMatcher("testCoverageWhenRunningPathAShouldReportSevenOutOfElevenElementsCovered", lessThan(7.1 / 11.0));

	}

	@Test
	@Deployment(resources = "process.bpmn")
	public void testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("path", "B");
		ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
				variables);
		
		rule.addTestMethodCoverageAssertionMatcher("testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered", greaterThan(6.9 / 11.0));
        rule.addTestMethodCoverageAssertionMatcher("testCoverageWhenRunningPathBShouldReportSevenOutOfElevenElementsCovered", lessThan(7.1 / 11.0));
	}

}
