# Camunda Process Test Coverage

## Introduction
This tool supports in analyzing and visualizing the process test coverage of a BPMN process.

![Screenshot](screenshot.png)

The tool creates test coverage reports for:

* Single test cases: The process coverage is visualized by marking those tasks and events with a green color which have be traversed by the test case.
* Entire test suites: The process coverage is visualized by marking those tasks and events with a green color which have be traversed by any of the test suite's test cases.

It also supports coverage checks for sequence flows and flow nodes in the junit tests. 
* Single test cases: supported via @Rule or manual calls  
* Single test classes: support via @ClassRule or manual calls
* Other setups:  manual calls

## Get started

Add this Maven Dependency to your project:

```
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-process-test-coverage</artifactId>
  <version>0.2.4-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```

Have a look at the tests. E.g.
- Class rule usage: [ProcessTestClassRuleCoverageTest](src/test/java/org/camunda/bpm/extension/process_test_coverage/ProcessTestClassRuleCoverageTest.java):
- Method rule usage: [ProcessTestMethodRuleCoverageTest](src/test/java/org/camunda/bpm/extension/process_test_coverage/ProcessTestMethodRuleCoverageTest.java):
- Manual usage: [ProcessTestNoRulesCoverageTest](src/test/java/org/camunda/bpm/extension/process_test_coverage/ProcessTestNoRulesCoverageTest.java):

## Remarks to run this application
1. mvn clean test
2. Open html files which are created in the directory target/process-test-coverage/

## Known Limitations
* Sequence flows are not visually marked.
* Test cases that deploy different version of the same process (same process definition key) are currently not supported and will result in misleading reports. Just make sure all your processes have unique process definition keys (in BPMN XML //process@id).

* Reports for an individual test method can only contain one process

## Resources

* [Issue Tracker](https://github.com/camunda/camunda-process-test-coverage/issues)
* [Roadmap](#Roadmap)
* [Changelog](https://github.com/camunda/camunda-process-test-coverage/commits/master)
* [Contributing](CONTRIBUTE.md)


## Roadmap

**todo**

- Text report
- Visualize technical attributes
- Jenkins integration

**done**

- JUnit Rule
- Calculate Flow Node Coverage in percent
- Calculate Path Coverage in percent
- Visualize test coverage using [bpmn.io](http://bpmn.io)
- Visualize transaction boundaries


## Maintainer

[Axel Groß (wdw-elab)](https://github.com/phax1)
[Falko Menge (Camunda)](https://github.com/falko)


## License

Apache License, Version 2.0
