package org.camunda.community.process_test_coverage.report.aggregator

import org.apache.maven.plugin.testing.MojoRule
import org.codehaus.plexus.PlexusTestCase.getTestFile
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class ReportAggregatorMojoTest {

    @Rule
    @JvmField
    val rule = MojoRule()

    @Test
    fun testExecute() {
        val pom: File = getTestFile("src/test/resources/test-project/pom.xml")
        assertNotNull(pom)
        assertTrue(pom.exists())

        val myMojo: ReportAggregatorMojo = rule.lookupMojo("aggregate", pom) as ReportAggregatorMojo
        assertNotNull(myMojo)
        myMojo.execute()

    }

}