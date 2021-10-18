[![](https://img.shields.io/badge/Lifecycle-Stable-brightgreen)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#stable-)
[![](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)


![Camunda Logo](doc/img/Favicons-Circle-Colour.png)

# Camunda BPM Process Test Coverage 

This Camunda BPM community extension **visualises** test process **paths** and **checks** your process model **coverage** ratio. Running  typical JUnit tests now leaves **html** files in your build output. Just open one and check yourself what your test did:

![Insurance Application](doc/img/insurance-application.png)

## Highlights

* **Visually verify** the paths covered by individual tests **methods** and whole test **classes**
* Visually check gateway **expressions** and transaction borders (**savepoints**) used by your process
* Calculate and **verify** the nodes (_and_ sequence flow) **coverage** ratio reached by tests methods and classes

## Just use it

* Integrates with all versions of Camunda BPM starting with 7.3.0 and upwards 
* Works with all relevant Java versions: 1.8 and 1.11 - using **JUnit 4.13.1** (4.11 does not work) or **JUnit 5**
* Is continuously checked against latest Camunda BPM releases 

## Get started with *3 simple steps*

<a href="https://maven-badges.herokuapp.com/maven-central/org.camunda.bpm.extension/camunda-bpm-process-test-coverage"><img src="https://maven-badges.herokuapp.com/maven-central/org.camunda.bpm.extension/camunda-bpm-process-test-coverage/badge.svg" align="right" /></a>**1.** Add a **Maven test dependency** to your project

#### JUnit4

```xml
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-process-test-coverage-junit4</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```

#### JUnit5

```xml
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-process-test-coverage-junit5</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```

**2.** Use the **ProcessCoverageInMemProcessEngineConfiguration**, e.g. in your `camunda.cfg.xml`

```xml
<bean id="processEngineConfiguration"
   class="org.camunda.bpm.extension.ProcessCoverageInMemProcessEngineConfiguration">
   ...
</bean>
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
```java
@ExtendWith(ProcessEngineCoverageExtension.class)
public class MyProcessTest
```
or `@RegisterExtension`
```java
@RegisterExtension
static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension
        .builder().assertClassCoverageAtLeast(0.9).build();
```

Running your JUnit tests now leaves **html** files for inidividual test methods as well as whole test classes in your project's `target/process-test-coverage` folder. Just open one, check yourself - and have fun with your process tests! :smile:

## New! Get Started with Spring Testing

See a unit test example wired for Spring Testing [here](https://github.com/camunda/camunda-bpm-process-test-coverage/blob/master/test/src/test/java/org/camunda/bpm/extension/process_test_coverage/spring/SpringProcessWithCoverageTest.java).

## News and Noteworthy & Contributors

There are a plenty of contributors to this project. Its initial design has been created by the WDW eLab GmbH and some others, but then the project has been abandoned for some time 
and received a full rewrite including the new architecture. We appreciate any help and effort you put into maintenance discussion and 
further development. Please check the release notes of [individual releases](https://github.com/camunda-community-hub/camunda-bpm-process-test-coverage/releases) for the changes 
and involved contributors.

## License
[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). See [LICENSE](LICENSE) file.
