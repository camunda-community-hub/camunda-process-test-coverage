package org.camunda.community.process_test_coverage.tests.junit4.platform7.rules;

import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestMethodDetectionTest {

//    private static AggregatedCoverageTestRunState coverageRunState = new AggregatedCoverageTestRunState();
//    private static final String AGGREGATED_REPORT_PATH = CoverageReportUtil.TARGET_DIR_ROOT + "/aggregated_report_test_method_detect/coverage";
//
//    @Deployment(resources = CoverageTestProcessConstants.BPMN_PATH)
//    @Category(Api74.class)
//    public static class DeploymentAnnotatedTestClass {
//        @Rule
//        @ClassRule
//        public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder
//            .create()
//            .setCoverageTestRunStateFactory(new AggregatedCoverageTestRunStateFactory(coverageRunState))
//            .build();
//
//        @Test
//        public void testPathA() {
//            Map<String, Object> variables = new HashMap<String, Object>();
//            variables.put("path", "A");
//            rule.getRuntimeService().startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables);
//        }
//
//        @AfterClass
//        public static void reportCoverage() {
//            coverageRunState.getAggregatedCoverage().getCoveragePercentage();
//        }
//
//    }
//
//    public static class DeploymentAnnotatedTestMethod {
//
//        @Rule
//        @ClassRule
//        public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder
//            .create()
//            .setCoverageTestRunStateFactory(new AggregatedCoverageTestRunStateFactory(coverageRunState))
//            .build();
//
//        @Deployment(resources = CoverageTestProcessConstants.BPMN_PATH)
//        @Test
//        public void testPathB() {
//            Map<String, Object> variables = new HashMap<String, Object>();
//            variables.put("path", "B");
//            rule.getRuntimeService().startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables);
//        }
//
//    }
//
//    @AfterClass
//    public static void reportCoverage() {
//
//        CoverageReportUtil.createReport(coverageRunState.getAggregatedCoverage(), AGGREGATED_REPORT_PATH);
//    }

}
