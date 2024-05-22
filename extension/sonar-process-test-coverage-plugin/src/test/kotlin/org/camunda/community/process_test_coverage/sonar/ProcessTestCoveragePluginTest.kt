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
