## Install Dependency

The library supports multiple execution platforms for the test and multiple process engines, so we supply different artifacts which you need to add to your project dependencies:

Please define the version of the library using properties like this:

```xml
<properties>
  <camunda-process-test-coverage.version>2.0.0</camunda-process-test-coverage.version>
</properties>
```

Current version available on Maven Central is: <a href="https://maven-badges.herokuapp.com/maven-central/org.camunda.community.process_test_coverage/camunda-process-test-coverage"><img src="https://maven-badges.herokuapp.com/maven-central/org.camunda.bpm.extension/camunda-process-test-coverage-core/badge.svg" /></a>


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

