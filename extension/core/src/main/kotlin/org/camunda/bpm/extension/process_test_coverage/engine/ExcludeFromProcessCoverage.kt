package org.camunda.bpm.extension.process_test_coverage.engine

/**
 * Annotation to mark test classes or methods, that should be excluded from
 * the process test coverage.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ExcludeFromProcessCoverage
