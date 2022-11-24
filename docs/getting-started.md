## Install Dependency

The library supports multiple execution platforms for the test and multiple process engines, so we supply different artifacts which you need to add to your project dependencies:

Please define the version of the library using properties like this:

```xml
<properties>
  <camunda-process-test-coverage.version>{{ POM_VERSION }}</camunda-process-test-coverage.version>
</properties>
```

### JUnit4 (only Platform 7)

```xml
<dependency>
  <groupId>org.camunda.community.process_test_coverage</groupId>
  <artifactId>camunda-process-test-coverage-junit4-platform-7</artifactId>
  <version>${camunda-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

### JUnit5 (Platform 7 or Platform 8)

```xml
<dependency>
  <groupId>org.camunda.community.process_test_coverage</groupId>
  <artifactId>camunda-process-test-coverage-junit5-platform-7</artifactId>
  <!-- <artifactId>camunda-process-test-coverage-junit5-platform-8</artifactId> -->
  <version>${camunda-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

### Spring-Testing (Platform 7 or Platform 8)

```xml
<dependency>
  <groupId>org.camunda.community.process_test_coverage</groupId>
  <artifactId>camunda-process-test-coverage-spring-test-platform-7</artifactId>
  <!-- <artifactId>camunda-process-test-coverage-spring-test-platform-8</artifactId> -->
  <version>${camunda-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

### Spring-Testing with starter (Platform 7 or Platform 8)

```xml
<dependency>
  <groupId>org.camunda.community.process_test_coverage</groupId>
  <artifactId>camunda-process-test-coverage-starter-platform-7</artifactId>
  <!-- <artifactId>camunda-process-test-coverage-starter-platform-8</artifactId> -->
  <version>${camunda-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

With the `starter` the further configuration steps are not needed anymore, as everything is auto-configured. This means you have to explicitly exclude all test classes and test methods,
that should not be included in the test coverage.

## Configuration

Use the **ProcessCoverageInMemProcessEngineConfiguration**, e.g. in your `camunda.cfg.xml` (only needed for Platform 7)

### JUnit4 and JUnit5

```xml
<bean id="processEngineConfiguration"
   class="org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageInMemProcessEngineConfiguration">
   ...
</bean>
```

### Spring-Testing

Import test configuration to enable coverage in process engine.
```java
@Import(ProcessEngineCoverageConfiguration.class)
```

## Usage

Wire the process engine in your JUnit test:

### JUnit4

Use the **TestCoverageProcessEngineRule** as your process engine JUnit rule

```java
@Rule
@ClassRule
public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();
```
### JUnit5

Use the **ProcessEngineCoverageExtension** as your process engine JUnit extension (available for Platform 7 and Platform 8)

Either use `@ExtendWith`

Java
```java
@ExtendWith(ProcessEngineCoverageExtension.class)
public class MyProcessTest
```

Kotlin
```kotlin
@ExtendWith(ProcessEngineCoverageExtension::class)
class MyProcessTest
```
or `@RegisterExtension`

If you register the extension on a non-static field, no class coverage and therefore no report will be generated. This is due to the fact, that an instance of the extension will be created per test method.

The extension provides a Builder for programmatic creation, which takes either a path to a configuration resource, a process engine configuration or if nothing is passed uses the default configuration resources path (`camunda.cfg.xml`).

The process engine configuration needs to be configured for test coverage. So use **either** the provided `ProcessCoverageInMemProcessEngineConfiguration`, `SpringProcessWithCoverageEngineConfiguration` or initialize the configuration with `ProcessCoverageConfigurator.initializeProcessCoverageExtensions(configuration)`.

In Java it looks like this:
```java
@RegisterExtension
static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension
        .builder().assertClassCoverageAtLeast(0.9).build();
```

If you prefer Kotlin, this is the way to go:
```kotlin
companion object {
    @JvmField
    @RegisterExtension
    var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension
            .builder(ProcessCoverageInMemProcessEngineConfiguration())
            .assertClassCoverageAtLeast(1.0).build()
}
```

### Spring-Testing

TestExecutionListener is automatically registered.
You can exclude test methods or classes from the coverage by annotating them like this

```java
@ExcludeFromProcessCoverage
public void testWithoutCoverage() {}
```
## Running the tests

Running your JUnit tests now leaves **html** files for individual test methods as well as whole test classes in your project's `target/process-test-coverage` folder. Just open one, check yourself - and have fun with your process tests! :smile:

## Aggregating results

Normally the results are written for each class under test. For aggregation of the results, there is a maven plugin to do the job.
This can be done differently depending on your needs.

### Aggregating reports in one maven module

If you just want to aggregate all reports in one maven module, the plugin can be defined in the POM under the build -> plugins section.

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.camunda.community.process_test_coverage</groupId>
                <artifactId>camunda-process-test-coverage-report-aggregator-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>aggregate-reports</id>
                        <goals>
                            <goal>aggregate</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

This will activate the plugin in the correct lifecycle and generate an aggregated report in the directory target/process_test_coverage/all (if not otherwise configured).

### Aggregating reports of multiple modules in reactor

The plugin will try to collect all reports for the modules in the current reactor. For an example configuration see the pom.xml in the examples directory.
Please note, that the plugin needs to run *after* the tests are run for all modules, therefore the plugin cannot be added to the plugins section in this use case.

You can still configure the plugin in the pluginManagement section.

```xml
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.camunda.community.process_test_coverage</groupId>
                    <artifactId>camunda-process-test-coverage-report-aggregator-maven-plugin</artifactId>
                    <version>${camunda-process-test-coverage.version}</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
```

It can then be invoked by calling *mvn camunda-process-test-coverage-report-aggregator:aggregate*.

### Usage as a reporting plugin

The plugin can also be used in the site generation. For this to work, the same conditions hold as before.
The configuration for this use case looks like this:

```xml
    <reporting>
        <plugins>
            <plugin>
                <groupId>org.camunda.community.process_test_coverage</groupId>
                <artifactId>camunda-process-test-coverage-report-aggregator-maven-plugin</artifactId>
                <version>${camunda-process-test-coverage.version}</version>
            </plugin>
        </plugins>
    </reporting>
```

### Usage of the gradle plugin

The functionality to aggregate the process test coverage reports is also provided as a gradle plugin.
For this to work you have to include the plugin in the gradle build file.

```kotlin
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.camunda.community.process_test_coverage:camunda-process-test-coverage-report-aggregator-gradle-plugin:{{ POM_VERSION }}")
    }
}

plugins {
    id 'org.camunda.community.process_test_coverage.report-aggregator'
}
```

Afterwards the reports can be aggregated by calling `gradle aggregateProcessTestCoverage`.