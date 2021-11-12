package org.camunda.bpm.extension.process_test_coverage.spring_test

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcludeFromProcessCoverage()
