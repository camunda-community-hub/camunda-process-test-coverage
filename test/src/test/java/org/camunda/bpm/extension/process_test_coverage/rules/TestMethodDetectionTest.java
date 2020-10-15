package org.camunda.bpm.extension.process_test_coverage.rules;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.AggregatedCoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.AggregatedCoverageTestRunStateFactory;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.extension.process_test_coverage.util.CoverageReportUtil;
import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(Enclosed.class)
public class TestMethodDetectionTest {

    private static AggregatedCoverageTestRunState coverageRunState = new AggregatedCoverageTestRunState();
    private static final String AGGREGATED_REPORT_PATH = CoverageReportUtil.TARGET_DIR_ROOT + "/aggregated_report_test_method_detect/coverage";

    @Deployment(resources = CoverageTestProcessConstants.BPMN_PATH)
    public static class DeploymentAnnotatedTestClass {
        @Rule
        @ClassRule
        public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder
            .create()
            .setCoverageTestRunStateFactory(new AggregatedCoverageTestRunStateFactory(coverageRunState))
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

    public static class DeploymentAnnotatedTestMethod {

        @Rule
        @ClassRule
        public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder
            .create()
            .setCoverageTestRunStateFactory(new AggregatedCoverageTestRunStateFactory(coverageRunState))
            .build();

        @Deployment(resources = CoverageTestProcessConstants.BPMN_PATH)
        @Test
        public void testPathB() {
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("path", "B");
            rule.getRuntimeService().startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables);
        }

    }

    @AfterClass
    public static void reportCoverage() {

        CoverageReportUtil.createReport(coverageRunState.getAggregatedCoverage(), AGGREGATED_REPORT_PATH);
    }

}
