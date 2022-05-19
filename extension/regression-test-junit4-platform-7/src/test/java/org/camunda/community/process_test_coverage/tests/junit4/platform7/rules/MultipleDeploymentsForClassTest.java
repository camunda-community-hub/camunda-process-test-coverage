package org.camunda.community.process_test_coverage.tests.junit4.platform7.rules;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit4.platform7.rules.TestCoverageProcessEngineRule;
import org.camunda.community.process_test_coverage.junit4.platform7.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.community.process_test_coverage.report.CoverageReportUtil;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.FileNotFoundException;
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
@Deployment(resources = { "superProcess-single-branch.bpmn", "process.bpmn" })
@Ignore
public class MultipleDeploymentsForClassTest {

    private static final String PROCESS_DEFINITION_KEY = "super-process-test-coverage-single-branch";
    private static final String SUB_PROCESS_DEFINITION_KEY = "process-test-coverage";
    private static final double THRESHOLD = 0.05;
    private static final Pattern coveragePattern = Pattern.compile("<div>Coverage:\\s*?([\\d.]*?)\\s*?%</div>");

    public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    public static TestWatcher checkReportRule = new TestWatcher() {
        @Override
        protected void finished(final Description description) {
            if (!description.isTest()) {
                checkReports(description);
            }
        }
    };

    private static String getReportPath(final String processDefinitionKey, final String className) {
        return String.format("%s/%s/%s.html", CoverageReportUtil.TARGET_DIR_ROOT, className, processDefinitionKey);
    }

    private static double getCoverageInReport(final String reportPath) {
        try (Scanner scanner = new Scanner(new File(reportPath))) {
            while (scanner.hasNext()) {
                final String line = scanner.nextLine();
                final Matcher matcher = coveragePattern.matcher(line);
                if (matcher.find()) {
                    return Double.parseDouble(matcher.group(1));
                }
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        fail("No coverage information was found for " + reportPath);
        return -1.0;
    }

    private static void assertCoverageInReport(final String processDefinitionKey, final String className, final double expectedCoverage) {
        final String reportPath = getReportPath(processDefinitionKey, className);
        final double coverage = getCoverageInReport(reportPath);
        assertEquals(expectedCoverage, coverage, THRESHOLD);
    }

    public static void checkReports(final Description description) {
        final String className = description.getClassName();
        assertCoverageInReport(PROCESS_DEFINITION_KEY, className, 100.0);
        assertCoverageInReport(SUB_PROCESS_DEFINITION_KEY, className, 63.6);
    }

    @Rule
    @ClassRule
    public static RuleChain chain = RuleChain.outerRule(checkReportRule).around(rule);

    @Test
    //duplicating Deployment annotation for backward compatibility with camunda-bpm-engine-7.3.0
    @Deployment(resources = { "superProcess-single-branch.bpmn", "process.bpmn" })
    public void runTestForSinglePath() {
        final Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "A");
        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
    }
}
