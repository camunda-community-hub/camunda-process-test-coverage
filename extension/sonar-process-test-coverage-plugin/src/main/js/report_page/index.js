/*-
 * #%L
 * Camunda Process Test Coverage Sonar Plugin
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
/*
 * Process Test Coverage Plugin for SonarQube
 */
import React from "react";
import ProcessTestCoverageReportApp from "./components/ProcessTestCoverageReportApp";
import "../style.css"

// This creates a page for process-test-coverage, which shows a html report

window.registerExtension("processtestcoverage/report_page", (options) => {
    return <ProcessTestCoverageReportApp options={options} />;
});
