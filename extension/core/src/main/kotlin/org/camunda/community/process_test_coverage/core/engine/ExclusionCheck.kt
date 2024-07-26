/*-
 * #%L
 * Camunda Process Test Coverage Core
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
package org.camunda.community.process_test_coverage.core.engine

import org.springframework.util.PatternMatchUtils
import java.lang.reflect.Method

fun Class<*>.isExcluded(inclusionPatterns: List<String> = emptyList()) =
    annotations.any { it is ExcludeFromProcessCoverage } || !matchesInclusionPattern(this, inclusionPatterns)

fun Method.isExcluded() = annotations.any { it is ExcludeFromProcessCoverage }

private fun matchesInclusionPattern(testClass: Class<*>, inclusionPatterns: List<String>) =
    inclusionPatterns.isEmpty() || PatternMatchUtils.simpleMatch(inclusionPatterns.toTypedArray(), testClass.name)
