## Setup reporting directory

Optionally, you might configure output path for reports. By default, the reports are written to `./target/process-test-coverage/`.
Depending on your preference there are multiple ways to change this.

### JUnit5

When registering the extension programmatically, there is an option to change the report directory.
See the following example, that uses the directory build which will typically be used by grade builds.

```kotlin
@RegisterExtension
var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder()
    .assertClassCoverageAtLeast(1.0)
    .reportDirectory("build/process-test-coverage")
    .build()
```

### Spring Testing

For the spring test execution listener, the configuration is provided through a configuration in the application context.
If a bean of type ProcessEngineCoverageProperties is present in the application context, it will be used for configuration
and supports changing the report directory.

```java
@Bean
public ProcessEngineCoverageProperties processEngineCoverageProperties() {
    return ProcessEngineCoverageProperties.builder()
            .assertClassCoverageAtLeast(1.0)
            .reportDirectory("build/process-test-coverage")
            .build();
}
```

### Maven aggregation plugin

For the maven aggregation plugin the report directory can be changed via the plugin configuration. It's also
possible to configure the output directory for the aggregation report inside the report directory (by default it's "all").

```xml
<plugin>
    <groupId>org.camunda.community.process_test_coverage</groupId>
    <artifactId>camunda-process-test-coverage-report-aggregator-maven-plugin</artifactId>
    <version>@project.version@</version>
    <executions>
        <execution>
            <id>aggregate-reports</id>
            <goals>
                <goal>aggregate</goal>
            </goals>
            <configuration>
                <reportDirectory>build/camunda-tests</reportDirectory>
                <outputDirectory>aggregation</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Gradle aggregation plugin

For the gradle plugin it's also possible to change the report directory as well as the output directory for the 
aggregation report.
This can be done via an extension setting in the gradle build file.

```groovy
plugins {
    id 'org.camunda.community.process_test_coverage.report-aggregator'
}

aggregateProcessTestCoverage {
    reportDirectory = 'build/camunda-tests'
    outputDirectory = 'aggregation'
}
```

### Legacy way via system property

We still support the old way to configure the reporting directory via system property `camunda-process-test-coverage.target-dir-root`.

#### Maven
```xml
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-surefire-plugin</artifactId>
<configuration>
  <systemPropertyVariables>
    <camunda-process-test-coverage.target-dir-root>${project.build.directory}/my-coverage-reports/</camunda-process-test-coverage.target-dir-root>
  </systemPropertyVariables>
</configuration>
</plugin>
```

#### Gradle (KTS)
```kotlin
tasks {
    withType<Test> {
        systemProperties = mapOf(
            "camunda-process-test-coverage.target-dir-root" to "$buildDir/my-coverage-reports/"
        )
    }
}
```
