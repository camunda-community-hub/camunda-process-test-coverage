package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.Coverage;
import org.camunda.bpm.extension.process_test_coverage.ProcessTestCoverage;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.runner.Description;

public class TestCoverageProcessEngineRule extends ProcessEngineRule {

	public static Logger log = Logger.getLogger(TestCoverageProcessEngineRule.class.getCanonicalName());
	
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
		if (resetFlowWhenStarting) {
			TestCoverageTestRunState.resetCurrentFlowTrace();
			deleteWholeHistory(processEngine);
		}
		// run derived functionality
		super.starting(description);

	}

	private void deleteWholeHistory(ProcessEngine processEngine) {
		// processEngine.getHistoryService().
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
			actualCoverage = TestCoverageTestRunState.FIXME_GLOBAL_VAR__HIGHEST_SEEN_COVERAGE;
		} else {
			actualCoverage = Coverage.calculateMeanPercentage(processesCoverage);
		}
		for(Matcher<Double> assertCoverageMatcher : assertCoverageMatchers){
			Assert.assertThat(actualCoverage, assertCoverageMatcher);
		}
		TestCoverageTestRunState.FIXME_GLOBAL_VAR__HIGHEST_SEEN_COVERAGE = Math.max(actualCoverage, TestCoverageTestRunState.FIXME_GLOBAL_VAR__HIGHEST_SEEN_COVERAGE);
		// run derived functionality
		super.finished(description);
	}

	
	private boolean isFinishedRunningInClassRule(Map<String, Coverage> processesFlowNodeCoverage) {
		return processesFlowNodeCoverage.isEmpty() && TestCoverageTestRunState.FIXME_GLOBAL_VAR__HIGHEST_SEEN_COVERAGE != -2.0;
	}

}
