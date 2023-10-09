package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.web.page.Context
import org.sonar.api.web.page.Page
import org.sonar.api.web.page.PageDefinition

class ProcessTestCoveragePage : PageDefinition {
    override fun define(context: Context) {
        context.addPage(
                Page.builder("camundaProcessTestCoverage/report_page")
                        .setScope(Page.Scope.COMPONENT)
                        .setComponentQualifiers(Page.Qualifier.PROJECT, Page.Qualifier.MODULE)
                        .setName("Process Test Coverage")
                        .setAdmin(false)
                        .build());

    }

}