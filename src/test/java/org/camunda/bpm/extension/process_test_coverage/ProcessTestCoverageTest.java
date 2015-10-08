package org.camunda.bpm.extension.process_test_coverage;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.TestCoverageProcessEngineRule;
import org.camunda.bpm.engine.test.coverage.ProcessTestCoverage;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ProcessTestCoverageTest {

  private static final String PROCESS_DEFINITION_KEY = "process-test-coverage";
  
  @Rule
  public TestCoverageProcessEngineRule rule = new TestCoverageProcessEngineRule();

  @Test
  @Deployment(resources = "process.bpmn")
  public void testPathA() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("path", "A");
    ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

    // calculate coverage for this method, but also add to the overall coverage of the process
    ProcessTestCoverage.calculate(processInstance, rule.getProcessEngine());
  }

  @Test
  @Deployment(resources = "process.bpmn")
  public void testPathB() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("path", "B");
    ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
    
    // calculate coverage for this method, but also add to the overall coverage of the process
    ProcessTestCoverage.calculate(processInstance.getId(), rule.getProcessEngine());
  }
  
  @Test
  @Deployment(resources = "transactionBoundaryTest.bpmn")
  public void testTxBoundaries() {
    ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey("transactionBoundaryTest");
    
    // calculate coverage for this method, but also add to the overall coverage of the process
    ProcessTestCoverage.calculate(processInstance.getId(), rule.getProcessEngine());
  }
  
}
