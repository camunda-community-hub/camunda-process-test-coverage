## Implementation Notes
- Via the parse listener plugin we register execution listeners on sequence flows so coverage on these can be recorded.
- Via the history handler we trace execution of flow elements so coverage on these can be recorded.
- When the tests are run, for each passing token information about the process instance and the covered element is recorded as [CoveredElement](src/main/java/org/camunda/bpm/extension/process_test_coverage/trace/CoveredElement.java) in the trace of covered elements. Also the visual reports are updated with the covered element.
- Also the information which deployments happened is recorded so the expected number of traced elements can be calculated from the bpmns 
- In the tests you specify which kind and percentage of coverage you want to check. The trace of covered elements gets filtered accordingly and is used to assert certain properties (e.g. percentage of flow nodes covered, percentage of sequence flows covered, or that certain elements have been covered). 
- Builders are used to abstract away the construction of Coverages and JUnit Rules and provide a stable interface and a nice programming experience
- The bpmns used in unit tests use both the old and new camunda namespaces for easy testing with different camunda versions (new namespaces starting with camunda 7.2.6, 7.3.3, 7.4.0)
- As example use case, in wdw-elab, we set the global minimal coverage property defined in [TestCoverageProcessEngineRuleBuilder.DEFAULT_ASSERT_AT_LEAST_PROPERTY](src/main/java/org/camunda/bpm/extension/process_test_coverage/junit/rules/TestCoverageProcessEngineRuleBuilder.java) as build parameter in our jenkins builds to specify the necessary coverage per project. 

## Resources
* Use the source, Luke.
* [JavaDoc](https://camunda.github.io/camunda-process-test-coverage/javadoc)
* [Contributing](CONTRIBUTING.md)
* [Readme](README.md)
