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
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.10.2")
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.21.0")
}

tasks.withType<Test>().configureEach {
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
