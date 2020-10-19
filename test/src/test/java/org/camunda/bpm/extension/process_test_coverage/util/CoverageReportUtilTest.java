package org.camunda.bpm.extension.process_test_coverage.util;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Test is isolated not to infer with modified test.
 */
public class CoverageReportUtilTest {

    @Test
    public void get_default_report_path() {
        assertEquals("target/process-test-coverage/", CoverageReportUtil.TARGET_DIR_ROOT);
    }

}
