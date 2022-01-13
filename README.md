[![](https://img.shields.io/badge/Lifecycle-Stable-brightgreen)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#stable-)
[![](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)


![Camunda Logo](doc/img/Favicons-Circle-Colour.png)

# Camunda BPM Process Test Coverage 

This Camunda BPM community extension **visualises** test process **paths** and **checks** your process model **coverage** ratio. Running  typical JUnit tests now leaves **html** files in your build output. Just open one and check yourself what your test did:

![Insurance Application](doc/img/flowcov_coverage_report.png)

## Highlights

* **Visually verify** the paths covered by individual tests **methods** and whole test **classes**
* Visually check gateway **expressions** and transaction borders (**save points**) used by your process
* Calculate and **verify** the nodes (_and_ sequence flow) **coverage** ratio reached by tests methods and classes

## Just use it

* Integrates with all versions of Camunda BPM starting with 7.12.0 and upwards 
* Works with Java version 11 and following - using **JUnit 4.13.1** (4.11 does not work) or **JUnit 5**
* Is continuously checked against the latest Camunda BPM releases 

## Get started with *3 simple steps*

<a href="https://maven-badges.herokuapp.com/maven-central/org.camunda.bpm.extension/camunda-bpm-process-test-coverage"><img src="https://maven-badges.herokuapp.com/maven-central/org.camunda.bpm.extension/camunda-bpm-process-test-coverage-core/badge.svg" align="right" /></a>

**1.** Add a **Maven test dependency** to your project

#### JUnit4

```xml
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-process-test-coverage-junit4</artifactId>
  <version>${camunda-bpm-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

#### JUnit5

```xml
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-process-test-coverage-junit5</artifactId>
  <version>${camunda-bpm-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

#### Spring-Testing

```xml
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-process-test-coverage-spring-test</artifactId>
  <version>${camunda-bpm-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

#### Spring-Testing with starter

```xml
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-process-test-coverage-starter</artifactId>
  <version>${camunda-bpm-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

With the starter steps #2 and #3 are not needed anymore, as everything is auto-configured. This means you have to explicitly exclude all test classes and test methods,
that should not be included in the test coverage.

You can do that, by using the following annotation on the class or method level.

```java
@ExcludeFromProcessCoverage
```

**2.** Use the **ProcessCoverageInMemProcessEngineConfiguration**, e.g. in your `camunda.cfg.xml`

#### JUnit4 and JUnit5

```xml
<bean id="processEngineConfiguration"
   class="org.camunda.bpm.extension.process_test_coverage.engine.ProcessCoverageInMemProcessEngineConfiguration">
   ...
</bean>
```

#### Spring-Testing

Import test configuration to enable coverage in process engine.
```java
@Import(ProcessEngineCoverageConfiguration.class)
```

**3.** Wire the process engine in your JUnit test

#### JUnit4

Use the **TestCoverageProcessEngineRule** as your process engine JUnit rule

```java
@Rule
@ClassRule
public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();
```

#### JUnit5

Use the **ProcessEngineCoverageExtension** as your process engine JUnit extension

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

Java
```java
@RegisterExtension
static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension
        .builder().assertClassCoverageAtLeast(0.9).build();
```

Kotlin
```kotlin
    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension
                .builder(ProcessCoverageInMemProcessEngineConfiguration())
                .assertClassCoverageAtLeast(1.0).build()
    }
```

#### Spring-Testing

```java
@TestExecutionListeners(value = ProcessEngineCoverageTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
```

You can exlude test methods from the coverage by annotating them like this

```java
@ExcludeFromProcessCoverage
public void testWithoutCoverage() {}
```

### Running the tests

Running your JUnit tests now leaves **html** files for individual test methods as well as whole test classes in your project's `target/process-test-coverage` folder. Just open one, check yourself - and have fun with your process tests! :smile:

**4. (Optional)** configure output path for reports

by default the reports are written to `./target/process-test-coverage/`. To change it you can set the system property `camunda-bpm-process-test-coverage.target-dir-root`

##### in maven pom.xml
```xml
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-surefire-plugin</artifactId>
<version>2.22.2</version>
<configuration>
  <systemPropertyVariables>
    <camunda-bpm-process-test-coverage.target-dir-root>${project.build.directory}/my-coverage-reports/</camunda-bpm-process-test-coverage.target-dir-root>
  </systemPropertyVariables>
</configuration>
</plugin>
```

##### in build.gradle.kts
```kotlin
tasks {
    withType<Test> {
        systemProperties = mapOf(
            "camunda-bpm-process-test-coverage.target-dir-root" to "$buildDir/my-coverage-reports/"
        )
    }
}
```
## New! Get Started with Spring Testing

Look at the examples and unit tests for further configuration options.

## News and Noteworthy & Contributors

There are a plenty of contributors to this project. Its initial design has been created by the WDW eLab GmbH and some others, but then the project has been abandoned for some time 
and received a full rewrite including the new architecture. We appreciate any help and effort you put into maintenance discussion and 
further development. Please check the release notes of [individual releases](https://github.com/camunda-community-hub/camunda-bpm-process-test-coverage/releases) for the changes 
and involved contributors.

## License
[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). See [LICENSE](LICENSE) file.
