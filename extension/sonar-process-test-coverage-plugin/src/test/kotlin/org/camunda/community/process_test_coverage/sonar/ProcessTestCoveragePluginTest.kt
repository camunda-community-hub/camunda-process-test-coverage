package org.camunda.community.process_test_coverage.sonar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.sonar.api.SonarEdition
import org.sonar.api.SonarQubeSide
import org.sonar.api.internal.PluginContextImpl
import org.sonar.api.internal.SonarRuntimeImpl
import org.sonar.api.utils.Version


class ProcessTestCoveragePluginTest {

    @Test
    fun testExtensions() {
        val runtime = SonarRuntimeImpl.forSonarQube(Version.create(7, 9), SonarQubeSide.SCANNER,
                SonarEdition.COMMUNITY)
        val context = PluginContextImpl.Builder().setSonarRuntime(runtime).build()
        val plugin = ProcessTestCoveragePlugin()
        plugin.define(context)
        assertEquals(7, context.extensions.size)
    }

}