package org.camunda.bpm.extension.process_test_coverage.rules;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.AggregatedCoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.AggregatedCoverageTestRunStateFactory;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Deployment(resources = CoverageTestProcessConstants.BPMN_PATH)
public class ClassHierarchyTest {

    private static AggregatedCoverageTestRunState coverageRunState = new AggregatedCoverageTestRunState();

    @Rule
    @ClassRule
    public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create()
                                                                                           .setCoverageTestRunStateFactory(new AggregatedCoverageTestRunStateFactory(
                                                                                               coverageRunState))

                                                                                           .build();
    @Test
    public void testPathA() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "A");
        rule.getRuntimeService().startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables);
    }

    @AfterClass
    public static void reportCoverage() {
        coverageRunState.getAggregatedCoverage().getCoveragePercentage();
    }

}
