package org.camunda.community.process_test_coverage.core.engine

import org.springframework.util.PatternMatchUtils
import java.lang.reflect.Method

fun Class<*>.isExcluded(inclusionPatterns: List<String> = emptyList()) =
    annotations.any { it is ExcludeFromProcessCoverage } || !matchesInclusionPattern(this, inclusionPatterns)

fun Method.isExcluded() = annotations.any { it is ExcludeFromProcessCoverage }

private fun matchesInclusionPattern(testClass: Class<*>, inclusionPatterns: List<String>) =
    inclusionPatterns.isEmpty() || PatternMatchUtils.simpleMatch(inclusionPatterns.toTypedArray(), testClass.name)
