package org.camunda.bpm.extension.process_test_coverage.rules;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ClassHalfCoverageTest {

	private static final double EXPECTED = CoverageTestProcessConstants.PATH_B_ELEMENTS.length;
	private static final double ALL = CoverageTestProcessConstants.ALL_ELEMENTS.length;
	private static final double EXPECTED_COVERAGE = EXPECTED / ALL;

	// Note, if you assert a coverage on the ClassRule, it means if you run a
	// test without the others, it will probably fail
	@ClassRule
	@Rule
	public static TestCoverageProcessEngineRule classRule = TestCoverageProcessEngineRuleBuilder.create()
			.assertClassCoverageAtLeast(EXPECTED_COVERAGE).build();

	@Test
	@Deployment(resources = CoverageTestProcessConstants.BPMN_PATH)
	public void testPathB() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("path", "B");
		classRule.getRuntimeService().startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables);
	}

	@Test
	@Deployment(resources = CoverageTestProcessConstants.BPMN_PATH)
	public void testPathBAgain() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("path", "B");
		classRule.getRuntimeService().startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables);
	}

}
