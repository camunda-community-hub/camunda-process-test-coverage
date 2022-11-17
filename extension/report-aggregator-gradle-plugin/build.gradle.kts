plugins {
    kotlin("jvm").version("1.7.21")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish").version("1.0.0")
}

repositories {
    mavenCentral()
    mavenLocal {
        uri("target/dependencies")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(group = "org.camunda.community.process_test_coverage", name = "camunda-process-test-coverage-report-generator", version = "$version")
    testImplementation(gradleTestKit())
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.8.2")
}

gradlePlugin {
    plugins {
        create("processTestConveragePlugin") {
            id = "org.camunda.community.process_test_coverage.report-aggregator"
            implementationClass = "org.camunda.community.process_test_coverage.report.aggregator.ReportAggregatorPlugin"
        }
    }
}
