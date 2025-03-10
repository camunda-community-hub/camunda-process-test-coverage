[![CIB seven 1.1.0](https://img.shields.io/badge/CIB%20seven-1.1.0-orange.svg)](https://docs.cibseven.org/manual/1.1/)
[![Maven Central](https://img.shields.io/maven-central/v/org.cibseven.community.process_test_coverage/cibseven-process-test-coverage-starter-platform-7?label=Maven%20Central)](https://central.sonatype.com/artifact/org.cibseven.community.process_test_coverage/cibseven-process-test-coverage-starter-platform-7)

# CIB seven Process Test Coverage

This CIB seven community extension **visualises** test process **paths** and **checks** your process model **coverage** ratio. Running  typical JUnit tests now leaves **html** files in your build output. Just open one and check yourself what your test did:

![Coverage report](docs/assets/img/flowcov_coverage_report.png)

## Highlights

* **Visually verify** the paths covered by individual tests **methods** and whole test **classes**
* Visually check gateway **expressions** and transaction borders (**save points**) used by your process
* Calculate and **verify** the nodes (_and_ sequence flow) **coverage** ratio reached by tests methods and classes.

## Just use it

* Integrates with all versions of CIB seven
* Is continuously checked against the latest CIB seven releases (check out our compatibility CI/CD pipeline)
* Tested with JDKs 11 and 17
* Supports **JUnit 4.13.1+** (4.11 does not work) or **JUnit 5**
* Can be used inside Spring Tests

## Installation

 Add a **Maven test dependency** to your project <a href="https://maven-badges.herokuapp.com/maven-central/org.cibseven.community.process_test_coverage/process-test-coverage-bom"><img src="https://maven-badges.herokuapp.com/maven-central/org.cibseven.community.process_test_coverage/cibseven-process-test-coverage-bom/badge.svg" align="right" /></a>
0
### JUnit5

```xml
<dependency>
  <groupId>org.cibseven.community.process_test_coverage</groupId>
  <artifactId>cibseven-process-test-coverage-junit5-platform-7</artifactId>
  <version>${cibseven-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

## Configuration

Use the **ProcessCoverageInMemProcessEngineConfiguration**, e.g. in your `camunda.cfg.xml` (only needed for Platform 7)

```xml
<bean id="processEngineConfiguration"
   class="org.cibseven.community.process_test_coverage.engine.platform7.ProcessCoverageInMemProcessEngineConfiguration">
   ...
</bean>
```

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

If you use Java:
```java
@RegisterExtension
static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension
        .builder().assertClassCoverageAtLeast(0.9).build();
```

If you prefer Kotlin:
```kotlin
companion object {
    @JvmField
    @RegisterExtension
    var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension
            .builder(ProcessCoverageInMemProcessEngineConfiguration())
            .assertClassCoverageAtLeast(1.0).build()
}
```

## Running the tests

Running your JUnit tests now leaves **html** files for individual test methods as well as whole test classes in your project's `target/process-test-coverage` folder. Just open one, check yourself - and have fun with your process tests! :smile:


## News and Noteworthy & Contributors

There are plenty of contributors to this project. Its initial design has been created by the WDW eLab GmbH and some others,
but then the project has been abandoned for some time and received a full rewrite including the new architecture by members
of flowcov.io squad and BPM craftsmen from Holisticon AG. We appreciate any help and effort you put into maintenance
discussion and further development.

Please check the release notes of [individual releases](https://github.com/cibseven-community-hub/process-test-coverage/releases) for the changes and involved contributors.

## License
[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). See [LICENSE](LICENSE.md) file.
