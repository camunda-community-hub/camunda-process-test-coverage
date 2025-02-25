plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
}

repositories {
    mavenCentral()
    maven {
        url = uri("file://${projectDir}/target/dependencies")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(group = "org.camunda.community.process_test_coverage", name = "camunda-process-test-coverage-report-generator", version = "$version")
    testImplementation(gradleTestKit())
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.10.3")
    testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher")
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.27.3")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("aggregateProcessTestCoverage") {
            id = "org.camunda.community.process_test_coverage.report-aggregator"
            implementationClass = "org.camunda.community.process_test_coverage.report.aggregator.ReportAggregatorPlugin"
        }
    }
}
