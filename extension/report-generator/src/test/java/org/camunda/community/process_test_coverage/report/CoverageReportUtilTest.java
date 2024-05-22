package org.camunda.community.process_test_coverage.report;

/*-
 * #%L
 * Camunda Process Test Coverage Report Generator
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
