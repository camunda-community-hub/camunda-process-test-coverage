# Camunda Process Test Coverage

## Introduction
This library supports visualizing and asserting the process test coverage of a BPMN process.

![Screenshot](screenshot.png)

Running your process unit tests with the library creates test coverage reports for:

* Single test cases: The process coverage is visualized by marking those tasks and events with a green color which have been traversed by the test case.
* Entire test suites: The process coverage is visualized by marking those tasks and events with a green color which have been traversed by any of the test suite's test cases.

It also supports coverage checks for sequence flows and flow nodes in the junit tests. 
* Check coverage after running a single test case: supported via junit @Rule or manual calls  
* Check coverage after running a test class: support via junit @ClassRule or manual calls
* Other setups:  manual calls

## Getting Started

Add this Maven Dependency to your project:

```
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-process-test-coverage</artifactId>
  <version>0.2.5-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```

Have a look at this project's tests. E.g.
- Class rule usage: [ProcessTestClassRuleCoverageTest](src/test/java/org/camunda/bpm/extension/process_test_coverage/ProcessTestClassRuleCoverageTest.java):
- Method rule usage: [ProcessTestMethodRuleCoverageTest](src/test/java/org/camunda/bpm/extension/process_test_coverage/ProcessTestMethodRuleCoverageTest.java):
- Manual usage: [ProcessTestNoRulesCoverageTest](src/test/java/org/camunda/bpm/extension/process_test_coverage/ProcessTestNoRulesCoverageTest.java):

### Checking Coverage for the Examples
You can use the junit tests of this project to get comfortable with the library

1. clone the project
2. mvn clean test
3. Open the report html files which are created in the directory target/process-test-coverage/

### Checking Coverage for Your Own Processes
The following steps show how to integrate the camunda-process-test-coverage into you own setup. Our tests should provide a good base for your usage. If you use a single junit class per process, the class rule usage may be the perfect way to go.

1.   add library jar to your project classpath (e.g. via the maven dependency)
2.a) add the [TraceActivitiesHistoryEventHandler](src/main/java/org/camunda/bpm/extension/process_test_coverage/trace/TraceActivitiesHistoryEventHandler.java) as history handler to your test camunda setup (see the [camunda.cfg.xml](src/test/resources/camunda.cfg.xml) we use)
2.b) add the [PathCoverageParseListenerPlugin](src/main/java/org/camunda/bpm/extension/process_test_coverage/trace/PathCoverageParseListenerPlugin.java) as process engine plugin to your test camunda setup (see the [camunda.cfg.xml](src/test/resources/camunda.cfg.xml) we use)
3.   adapt your process unit test to generate and check the coverage.
4.   run your unit tests

## Environment Restrictions
* Built and tested against Camunda BPM version 7.2.0.
* Expected to work in Camunda BPM 7.x versions starting from 7.2.0 (7.2.x, 7.3.x, 7.4.x, 7.5.x).

## Known Limitations
* Sequence flows are not visually marked. Coverage percentage of sequence flows can be asserted though.
* Test cases that deploy different versions of the same process (same process definition key) are not supported and will result in misleading reports. Just make sure all your processes have unique process definition keys (in BPMN XML //process@id).

* Reports for an individual test method can only contain one process

## Implementation
- Via the parse listener plugin we register execution listeners on sequence flows and elements so coverage on these can be recorded.
- When the tests are run, for each passing token information about the process instance and the covered element is recorded as [CoveredElement](src/main/java/org/camunda/bpm/extension/process_test_coverage/trace/CoveredElement.java) in the trace of covered elements. Also the visual reports are updated with the covered element.
- In the tests you specify which kind and percentage of coverage you want to check. The trace of covered elements gets filtered accordingly and is used to assert certain properties (e.g. percentage of flow nodes covered, percentage of sequence flows covered, or that certain elements have been covered). 
- Builders are used to abstract away the construction of Coverages and junit Rules and provide a nice programming experience
- The bpmns used in unit tests use both the old and new camunda namespaces for easy testing with different camunda versions (new namespaces starting with camunda 7.2.6, 7.3.3, 7.4.0)

## Resources
* [Issue Tracker](https://github.com/camunda/camunda-process-test-coverage/issues)
* [Roadmap](#roadmap)
* [Changelog](https://github.com/camunda/camunda-process-test-coverage/commits/master)
* [Contributing](CONTRIBUTE.md)


## Roadmap

**To Do**

- Text report of covered elements 
- Visualize covered sequence flow
- Visualize technical attributes
- Jenkins integration

**Done**

- JUnit Rule
- Calculate Flow Node Coverage in percent
- Calculate Path Coverage in percent
- Visualize test coverage using [bpmn.io](http://bpmn.io)
- Visualize transaction boundaries

## Contributors
The Software Development Team of [WDW eLab GmbH](http://www.wdw-elab.de) is responsible for the Design and Implementation of this project.

![Screenshot](elab_logo.png)

WDW eLab GmbH is an innovative IT company and has great experience with complex business support processes in a complex IT environment. One of our specialities are customer support processes in telecommunication. 

We are proud to be an official camunda partner!

Feel free to contact us via [Email](mailto:kontakt@wdw-elab.de)

## Maintainer

People responsible for this project

[Irmin Okic (wdw-elab)](https://github.com/z0rbas)

[Axel Groß (wdw-elab)](https://github.com/phax1)

[Falko Menge (Camunda)](https://github.com/falko)

## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).


## License

Apache License, Version 2.0


