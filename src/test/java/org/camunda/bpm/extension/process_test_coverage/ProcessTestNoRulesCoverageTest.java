package org.camunda.bpm.extension.process_test_coverage;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineTestCase;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageMappings;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.MinimalCoverageMatcher;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.hamcrest.Matchers;
import org.junit.Ignore;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class ProcessTestNoRulesCoverageTest extends ProcessEngineTestCase {

  private static final String PROCESS_DEFINITION_KEY = "process-test-coverage-clone";
  private static Set<String> relevantDeploymentIds = new HashSet<String>();
  private static Map<String, Coverage> theLastCoverage;

  
  public static junit.framework.Test suite() {
	    return new junit.extensions.TestSetup(new junit.framework.TestSuite(ProcessTestNoRulesCoverageTest.class)) {

	        protected void setUp() throws Exception {
	            System.out.println(" Global setUp ");
	            TestCoverageTestRunState.INSTANCE().resetCurrentFlowTrace();
	        }
	        protected void tearDown() throws Exception {
	            System.out.println(" Checking global coverage ");
	            assertThat(theLastCoverage.get(PROCESS_DEFINITION_KEY).getActualPercentage(), equalTo(1.0));
	        }
	    };
	}
  
  @Override
  protected  void tearDown() throws Exception {
    // remember coverage for all tests in the suite
    relevantDeploymentIds.add(super.deploymentId);
    theLastCoverage = ProcessTestCoverage.calculateForDeploymentIds(processEngine, relevantDeploymentIds);
  }

  @Deployment(resources = "process-clone.bpmn")
  public void testPathA() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("path", "A");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);

    // calculate coverage for this method, but also add to the overall coverage of the process
    Map<String, Coverage> currentCoverage = ProcessTestCoverage.calculate(processEngine, processInstance);
    System.err.println("testPathA-"+currentCoverage.get(PROCESS_DEFINITION_KEY));

    String processDefinitionId = processInstance.getProcessDefinitionId(); // changes with every deployment
    CoveredActivity activityA = new CoveredActivity(processDefinitionId , "ManualTask_3");
    CoveredActivity activityB = new CoveredActivity(processDefinitionId, "ManualTask_4");
    assertThat(currentCoverage.get(PROCESS_DEFINITION_KEY).coveredActivities, Matchers.hasItem(activityA));
    assertThat(currentCoverage.get(PROCESS_DEFINITION_KEY).coveredActivities, not(Matchers.hasItem(activityB)));
        
    assertThat(currentCoverage.get(PROCESS_DEFINITION_KEY).getActualPercentage(), new MinimalCoverageMatcher(7.0/11.0));
    assertThat(currentCoverage.get(PROCESS_DEFINITION_KEY).getActualPercentage(), equalTo(7.0/11.0));
  }

  String[] pathB = new String[]{
		  "StartEvent_1",
		  "SequenceFlow_Start1ToExclusive3",
		  "ExclusiveGateway_3",
		  "SequenceFlow_Exclusive3ToManualB",
		  "ManualTask_4",
		  "SequenceFlow_ManualBToEnd3",
		  "EndEvent_3"
  };
  @Deployment(resources = "process-clone.bpmn")
  public void testPathB() {
	  System.out.println("RUNNING testPathB");
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("path", "B");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
    
    // calculate coverage for this method, but also add to the overall coverage of the process
    Map<String, Coverage> currentCoverage = ProcessTestCoverage.calculate(processEngine, processInstance);

    String processDefinitionId = processInstance.getProcessDefinitionId(); // changes with every deployment
    CoveredActivity activityB = new CoveredActivity(processDefinitionId, "ManualTask_4");
    assertThat(currentCoverage.get(PROCESS_DEFINITION_KEY).coveredActivities, Matchers.hasItem(activityB));
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.addAll(CoverageMappings.mapElementsToIds(currentCoverage.get(PROCESS_DEFINITION_KEY).coveredActivities));
    arrayList.addAll(CoverageMappings.mapElementsToIds(currentCoverage.get(PROCESS_DEFINITION_KEY).coveredSequenceFlowIds));
    assertThat(arrayList, containsInAnyOrder(pathB));
    assertThat(currentCoverage.get(PROCESS_DEFINITION_KEY).getActualPercentage(), new MinimalCoverageMatcher(7.0/11.0));
    assertThat(currentCoverage.get(PROCESS_DEFINITION_KEY).getActualPercentage(), equalTo(7.0/11.0));
    
  }
  
  @Deployment(resources = "transactionBoundaryTest.bpmn")
  public void tttestTxBoundaries() {
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("transactionBoundaryTest");
    
    // calculate coverage for this method, but also add to the overall coverage of the process
    Map<String, Coverage> currentCoverage = ProcessTestCoverage.calculate(processEngine, processInstance);
    assertThat(currentCoverage.get("transactionBoundaryTest").getActualPercentage(), equalTo(14.0/14.0));
  }

 
}
