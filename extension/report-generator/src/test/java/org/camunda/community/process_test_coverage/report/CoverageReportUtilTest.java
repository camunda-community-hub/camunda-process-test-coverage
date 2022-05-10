package org.camunda.community.process_test_coverage.report;

import junit.framework.TestCase;
import org.camunda.community.process_test_coverage.report.CoverageReportUtil;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Test is isolated not to infer with modified test.
 */
public class CoverageReportUtilTest {

    @Test
    public void get_default_report_path() {
        TestCase.assertEquals("target/process-test-coverage/", CoverageReportUtil.TARGET_DIR_ROOT);
    }

}
