[![](https://img.shields.io/badge/Lifecycle-Stable-brightgreen)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#stable-)
[![](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
![Compatible with: Camunda Platform 7](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%207-26d07c)
![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-26d07c)


![Camunda Logo](docs/assets/img/Favicons-Circle-Colour.png)

# Camunda Process Test Coverage

This Camunda Platform 7 and Platform 8 ommunity extension **visualises** test process **paths** and **checks** your process model **coverage** ratio. Running  typical JUnit tests now leaves **html** files in your build output. Just open one and check yourself what your test did:

![Coverage report](docs/assets/img/flowcov_coverage_report.png)

## Highlights

* **Visually verify** the paths covered by individual tests **methods** and whole test **classes**
* Visually check gateway **expressions** and transaction borders (**save points**) used by your process
* Calculate and **verify** the nodes (_and_ sequence flow) **coverage** ratio reached by tests methods and classes.

## Just use it

* Integrates with all versions of Camunda Platform 7 starting with 7.10.0 and upwards as well as Camunda Platform 8
* Is continuously checked against the latest Camunda Platform 7 releases (check out our compatibility CI/CD pipeline)
* Tested with JDKs 11 and 17
* Works with Java starting with 1.8 and following
* Supports **JUnit 4.13.1+** (4.11 does not work) or **JUnit 5**
* Can be used inside Spring Tests

## Documentation

If you are interested in further documentation, please check our [Documentation Page](https://)

## Installation

 Add a **Maven test dependency** to your project <a href="https://maven-badges.herokuapp.com/maven-central/org.camunda.community.process_test_coverage/camunda-process-test-coverage"><img src="https://maven-badges.herokuapp.com/maven-central/org.camunda.bpm.extension/camunda-process-test-coverage-core/badge.svg" align="right" /></a>

### JUnit5 (Platform 7 or Platform 8)

```xml
<dependency>
  <groupId>org.camunda.community.process_test_coverage</groupId>
  <artifactId>camunda-process-test-coverage-junit5-platform7</artifactId>
  <!-- <artifactId>camunda-process-test-coverage-junit5-platform8</artifactId> -->
  <version>${camunda-process-test-coverage.version}</version>
  <scope>test</scope>
</dependency>
```

## Configuration

Use the **ProcessCoverageInMemProcessEngineConfiguration**, e.g. in your `camunda.cfg.xml` (only needed for Platform 7)

```xml
<bean id="processEngineConfiguration"
   class="org.camunda.community.process_test_coverage.engine.platform7.ProcessCoverageInMemProcessEngineConfiguration">
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

Please check the release notes of [individual releases](https://github.com/camunda-community-hub/camunda-process-test-coverage/releases) for the changes and involved contributors.

## License
[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). See [LICENSE](LICENSE.md) file.
