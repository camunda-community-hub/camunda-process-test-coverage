package org.camunda.bpm.extension.process_test_coverage.rules;

import org.camunda.bpm.extension.process_test_coverage.util.CoverageReportUtil;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

/**
 * Shared coverageTestRunStateTest
 *
 * @author ov7a
 */
@RunWith(Enclosed.class)
public class AggregatedCoverageTestRunStateTest {
    private static final String SUB_PROCESS_DEFINITION_KEY = "process-test-coverage";
    private static final String AGGREGATED_REPORT_PATH = CoverageReportUtil.TARGET_DIR_ROOT + "/aggregated_report/coverage";
    private static final double THRESHOLD = 0.05;
    private static final Pattern coveragePattern = Pattern.compile("<div>Coverage:\\s*?([\\d.]*?)\\s*?%</div>");

//    private static final AggregatedCoverageTestRunState sharedCoverageState = new AggregatedCoverageTestRunState();
//    private static final CoverageTestRunStateFactory coverageTestRunStateFactory = new AggregatedCoverageTestRunStateFactory(sharedCoverageState);
//
//    public static class FirstNested {
//        public static final String PROCESS_DEFINITION_KEY = "super-process-test-coverage-single-branch";
//
//        @Rule
//        @ClassRule
//        public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder
//                .create()
//                .setCoverageTestRunStateFactory(coverageTestRunStateFactory)
//                .build();
//
//        @Test
//        @Deployment(resources = {"superProcess-single-branch.bpmn", "process.bpmn"})
//        public void runTestForSinglePath() {
//            Map<String, Object> variables = new HashMap<String, Object>();
//            variables.put("path", "A");
//            rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
//        }
//    }
//
//    public static class SecondNested {
//        public static final String PROCESS_DEFINITION_KEY = "super-process-test-coverage";
//
//        @Rule
//        @ClassRule
//        public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder
//                .create()
//                .setCoverageTestRunStateFactory(coverageTestRunStateFactory)
//                .build();
//
//        @Test
//        @Deployment(resources = { "superProcess.bpmn", "process.bpmn" })
//        public void testPathAAndSuperPathA() {
//            Map<String, Object> variables = new HashMap<String, Object>();
//            variables.put("path", "B");
//            variables.put("superPath", "A");
//            rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
//        }
//    }
//
//    @AfterClass
//    public static void createAndValidateCombinedReport(){
//        CoverageReportUtil.createReport(sharedCoverageState.getAggregatedCoverage(), AGGREGATED_REPORT_PATH);
//        checkReports();
//    }
//
//    private static String getReportPath(String processDefinitionKey) {
//        return String.format("%s/%s.html", AGGREGATED_REPORT_PATH, processDefinitionKey);
//    }
//
//    private static double getCoverageInReport(String reportPath) {
//        try (Scanner scanner = new Scanner(new File(reportPath))){
//            while (scanner.hasNext()) {
//                String line = scanner.nextLine();
//                Matcher matcher = coveragePattern.matcher(line);
//                if (matcher.find()){
//                    return Double.parseDouble(matcher.group(1));
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        fail("No coverage information was found for " + reportPath);
//        return -1.0;
//    }
//
//    private static void assertCoverageInReport(String processDefinitionKey, double expectedCoverage) {
//        String reportPath = getReportPath(processDefinitionKey);
//        double coverage = getCoverageInReport(reportPath);
//        assertEquals(expectedCoverage, coverage, THRESHOLD);
//    }
//
//    public static void checkReports() {
//        assertCoverageInReport(FirstNested.PROCESS_DEFINITION_KEY, 100.0);
//        assertCoverageInReport(SecondNested.PROCESS_DEFINITION_KEY, 69.2);
//        assertCoverageInReport(SUB_PROCESS_DEFINITION_KEY, 100.0);
//    }
}
