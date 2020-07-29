package org.camunda.bpm.extension.process_test_coverage.rules;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.extension.process_test_coverage.util.CoverageReportUtil;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Multiple deployments per test method test.
 *
 * @author ov7a
 */
@Deployment(resources = {"superProcess-single-branch.bpmn", "process.bpmn"})
public class MultipleDeploymentsForClassTest {

    private static final String PROCESS_DEFINITION_KEY = "super-process-test-coverage-single-branch";
    private static final String SUB_PROCESS_DEFINITION_KEY = "process-test-coverage";
    private static final double THRESHOLD = 0.05;
    private static final Pattern coveragePattern = Pattern.compile("<div>Coverage:\\s*?([\\d.]*?)\\s*?%</div>");

    public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    public static TestWatcher checkReportRule = new TestWatcher() {
        @Override
        protected void finished(Description description) {
            if (!description.isTest()) {
                checkReports(description);
            }
        }
    };

    private static String getReportPath(String processDefinitionKey, String className) {
        return String.format("%s/%s/%s.html", CoverageReportUtil.TARGET_DIR_ROOT, className, processDefinitionKey);
    }

    private static double getCoverageInReport(String reportPath) {
        try (Scanner scanner = new Scanner(new File(reportPath))){
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                Matcher matcher = coveragePattern.matcher(line);
                if (matcher.find()){
                    return Double.parseDouble(matcher.group(1));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fail("No coverage information was found for " + reportPath);
        return -1.0;
    }

    private static void assertCoverageInReport(String processDefinitionKey, String className, double expectedCoverage) {
        String reportPath = getReportPath(processDefinitionKey, className);
        double coverage = getCoverageInReport(reportPath);
        assertEquals(expectedCoverage, coverage, THRESHOLD);
    }

    public static void checkReports(Description description) {
        String className = description.getClassName();
        assertCoverageInReport(PROCESS_DEFINITION_KEY, className, 100.0);
        assertCoverageInReport(SUB_PROCESS_DEFINITION_KEY, className, 63.6);
    }

    @Rule
    @ClassRule
    public static RuleChain chain = RuleChain.outerRule(checkReportRule).around(rule);

    @Test
    public void runTestForSinglePath() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "A");
        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
    }
}
