package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.Coverage;
import org.camunda.bpm.extension.process_test_coverage.ProcessTestCoverage;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runner.Description;

public class TestCoverageProcessEngineRule extends ProcessEngineRule {

	public static Logger log = Logger.getLogger(TestCoverageProcessEngineRule.class.getCanonicalName());
	
	private TestCoverageTestRunState testCoverageTestRunState;
	
	@Override
	public org.junit.runners.model.Statement apply(org.junit.runners.model.Statement base, Description description) {
		return super.apply(base, description);
		
	};
	
	public static enum TypeIncludedInCoverage {
	
		FlowElement(org.camunda.bpm.model.bpmn.instance.FlowElement.class);
		
		private Class<? extends FlowElement> coveredTypeClass;
		
		private TypeIncludedInCoverage(final Class<? extends FlowElement> coveredTypeClass) {
			this.coveredTypeClass = coveredTypeClass;
		}
		
		public Class<? extends FlowElement> getCoveredTypeClass() {
			return coveredTypeClass;
		}
		
	};
	
	public boolean resetFlowWhenStarting = false;
	public boolean reportCoverageWhenFinished = false;
	public Collection<Matcher<Double>> assertCoverageMatchers = new ArrayList();
	
	@Override
	public void starting(Description description) {
		testCoverageTestRunState =  TestCoverageTestRunState.INSTANCE(); // XXX inject??
		if (resetFlowWhenStarting) {
			testCoverageTestRunState.resetCurrentFlowTrace();
//			deleteWholeHistory(processEngine);
		}
		// run derived functionality
		super.starting(description); // initialised engine and process 
		if (resetFlowWhenStarting) {
			deleteWholeHistory(processEngine);
		}
	}

	private void deleteWholeHistory(ProcessEngine processEngine) {
		Object query = processEngine.getHistoryService().createHistoricProcessInstanceQuery().orderByProcessDefinitionId().asc().count();
		for (HistoricProcessInstance hpi : processEngine.getHistoryService().createHistoricProcessInstanceQuery().processDefinitionNameLike("%").list()) {
		 processEngine.getHistoryService().deleteHistoricProcessInstance(hpi.getProcessDefinitionId());
		}
		//processDefinition.getId()
		for (HistoricTaskInstance hpi : processEngine.getHistoryService().createHistoricTaskInstanceQuery()
				.processDefinitionName("process-test-coverage").list()) {
			 processEngine.getHistoryService().deleteHistoricProcessInstance(hpi.getProcessDefinitionId());
			}
		processEngine.getHistoryService().createHistoricProcessInstanceQuery().list();
		ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey("process-test-coverage").singleResult();
//		if (processDefinition == null){
//			throw new RuntimeException("Process not found. Found " + processEngine.getRepositoryService().createProcessDefinitionQuery().list());
//		}
//		List<HistoricActivityInstance> activities = processEngine.getHistoryService()
//		.createHistoricActivityInstanceQuery().processDefinitionId(processDefinition.getId()).list();
//		List<HistoricActivityInstance> activities2 = processEngine.getHistoryService()
//				.createHistoricActivityInstanceQuery().list();
//		if (activities.size() != activities2.size()) {
//			throw new RuntimeException(activities + "\n" + activities2);
//		}
	}

	private static <T> Collection<T> nullToEmpty(Collection<T> nullable) {
		if (nullable == null) {
			return (Collection<T>) Collections.emptyList();
		}
		return nullable;
	}

	@Override
	public void finished(Description description) {

	    // calculate coverage for all tests
	    Map<String, Coverage> processesCoverage = ProcessTestCoverage.calculate(processEngine);

		// calculate possible coverage
		
		if (this.deploymentId != null) {
			
			if (this.reportCoverageWhenFinished) {
				log.info(processesCoverage.toString());
			} else {
				log.log(Level.FINE, processesCoverage.toString());
			}

		}
		double actualCoverage = 1;
		if (isFinishedRunningInClassRule(processesCoverage)){
			actualCoverage = testCoverageTestRunState.highestSeenCoverage;
		} else {
			actualCoverage = Coverage.calculateMeanPercentage(processesCoverage);
		}
		for(Matcher<Double> assertCoverageMatcher : assertCoverageMatchers){
			Assert.assertThat(actualCoverage, assertCoverageMatcher);
		}
		testCoverageTestRunState.highestSeenCoverage = Math.max(actualCoverage, testCoverageTestRunState.highestSeenCoverage);
		// run derived functionality
		super.finished(description);
	}

	
	private boolean isFinishedRunningInClassRule(Map<String, Coverage> processesFlowNodeCoverage) {
		return processesFlowNodeCoverage.isEmpty() && testCoverageTestRunState.highestSeenCoverage != -2.0;
	}

}
