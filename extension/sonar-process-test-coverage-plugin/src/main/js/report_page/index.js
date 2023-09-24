/*
 * Process Test Coverage Plugin for SonarQube
 */
import React from "react";
import ProcessTestCoverageReportApp from "./components/ProcessTestCoverageReportApp";

// This creates a page for process-test-coverage, which shows a html report

window.registerExtension("camundaProcessTestCoverage/report_page", (options) => {
    return <ProcessTestCoverageReportApp options={options} />;
});
