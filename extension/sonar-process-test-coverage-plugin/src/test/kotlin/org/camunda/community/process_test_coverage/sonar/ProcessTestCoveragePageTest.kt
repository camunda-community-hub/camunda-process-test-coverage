package org.camunda.community.process_test_coverage.sonar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.sonar.api.web.page.Context

class ProcessTestCoveragePageTest {

    @Test
    fun testPage() {
        val reportPage = ProcessTestCoveragePage()
        val context = Context()
        reportPage.define(context)
        val page = context.pages.iterator().next()
        assertEquals("Process Test Coverage", page.name)
        assertEquals("camundaProcessTestCoverage/report_page", page.key)
        assertFalse(page.isAdmin)
    }

}