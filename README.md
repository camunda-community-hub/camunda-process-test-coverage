# Camunda Process Test Coverage

## Introduction
This library supports visualizing and asserting the process test coverage of a BPMN process.

![Screenshot](screenshot.png)

Running your process unit tests with the library creates test coverage reports for:

* Entire test suites: The process coverage is visualized by marking those tasks and events and transitions with a green color which have been traversed by any of the test suite's test cases.

It also supports coverage ratio checks for sequence flows and flow nodes in the JUnit tests.
```
java.lang.AssertionError: 
Expected: matches if the coverage ratio is at least <1.0>
     got: <0.9516129032258065>
```
* Check coverage after running a single test case: supported via JUnit @Rule 
* Check coverage after running a test class: support via JUnit @ClassRule and @Rule

Using system properties you can set expected coverage at build time of your project.


## Getting Started

Add this Maven Dependency to your project:

```
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-process-test-coverage</artifactId>
  <version>0.2.7-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```

Have a look at this project's tests. E.g.
- Class rule usage: [ProcessTestClassRuleCoverageTest](src/test/java/org/camunda/bpm/extension/process_test_coverage/ClassCoverageTest.java):
- Method rule usage: [ProcessTestMethodRuleCoverageTest](src/test/java/org/camunda/bpm/extension/process_test_coverage/MethodCoverageTest.java):
- Test checking property usage: [ProcessTestClassRulePropertyTest](src/test/java/org/camunda/bpm/extension/process_test_coverage/ClassCoveragePropertyTest.java):

### Checking Coverage for the Examples
You can use the JUnit tests of this project to get comfortable with the library

1. clone the project
2. mvn clean test
3. Open the report html files which are created in the directory target/process-test-coverage/

### Checking Coverage for Your Own Processes
The following steps show how to integrate the camunda-process-test-coverage into you own setup. Our tests should provide a good base for your usage. If you use a single JUnit class per process, the class rule usage may be the perfect way to go.

1.   add library jar to your project classpath (e.g. via the maven dependency)
2.   adjust yout test camunda setup [camunda.cfg.xml](src/test/resources/camunda.cfg.xml)
  * use the [ProcessCoverageInMemProcessEngineConfiguration](src/test/resources/camunda.cfg.xml)
  * or add the [FlowNodeHistoryEventHandler](src/main/java/org/camunda/bpm/extension/process_test_coverage/listeners/FlowNodeHistoryEventHandler.java) & [PathCoverageParseListener](src/main/java/org/camunda/bpm/extension/process_test_coverage/trace/TraceActivitiesHistoryEventHandler.java) to your process engine configuration
3.   adapt your process unit test to generate and check the coverage. 
4.   optionally set the Java system property (org.camunda.bpm.extension.process_test_coverage.ASSERT_AT_LEAST) on your build system to assure all process tests adhere to a coverage minimum.
5.   run your unit tests

## Environment Restrictions
* Built against Camunda BPM version 7.4.0 and Java 1.6
* Tested against Camunda BPM version 7.3.0 and Java 1.6 
* Tested against Camunda BPM version 7.4.0 and Java 1.8  
* Expected to work in Camunda BPM 7.x versions starting from 7.2.6 (7.2.x, 7.3.x, 7.4.x, 7.5.x).

## [Implementation](IMPLEMENTATION.md)
## Resources
* [JavaDoc](https://camunda.github.io/camunda-process-test-coverage/javadoc)
* [Issue Tracker](https://github.com/camunda/camunda-process-test-coverage/issues)
* [Roadmap](#roadmap)
* [Changelog](https://github.com/camunda/camunda-process-test-coverage/commits/master)
* [Contributing](CONTRIBUTING.md)
* [Implementation Notes](IMPLEMENTATION.md)

## Roadmap

Our To Do list gives a rough idea what we expect to tackle next.

**To Do**
- 0.2.9 Text reports of traces
- 0.3.x Remove the history handler, use the plug-in to place listeners on flow nodes
- 0.4.x Text reports of traces as alternate way to check coverage
- Visualize technical attributes in a nice way

**Done**
- 0.2.7 single test method reporting; sequence flow coverage reporting;
- 0.2.6 "Jenkins integration" - add minimal coverage system property for build integration
- 0.2.5 fixed different multi-deployment cases
- JUnit @Rule, JUnit @ClassRule
- Calculate Flow Node Coverage in percent
- Calculate Path Coverage in percent
- Visualize test coverage using [bpmn.io](http://bpmn.io)
- Visualize transaction boundaries
- Visualize technical attributes

## Contributors
The Software Development Team of [WDW eLab GmbH](http://www.wdw-elab.de) is responsible for the Design and Implementation of this project.

![Screenshot](elab_logo.png)

WDW eLab GmbH is an innovative IT company and has great experience with complex business support processes in a complex IT environment. One of our specialties are customer support processes in telecommunication. 

We are proud to be an official camunda partner!

Feel free to contact us via [Email](mailto:kontakt@wdw-elab.de)

## Maintainer

People responsible for this project

[Irmin Okic (wdw-elab)](https://github.com/z0rbas)

[Axel Groß (wdw-elab)](https://github.com/phax1)

[Falko Menge (Camunda)](https://github.com/falko)

## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0). See [LICENSE](LICENSE) file.



