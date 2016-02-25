package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.Coverage;
import org.camunda.bpm.extension.process_test_coverage.Coverages;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.runner.Description;

public class TestCoverageProcessEngineRule extends ProcessEngineRule implements ProcessDeploymentRule {

	public static Logger log = Logger.getLogger(TestCoverageProcessEngineRule.class.getCanonicalName());
	
	private TestCoverageTestRunState testCoverageTestRunState;
	
	private final static Lock sequential = new ReentrantLock();
	
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
	
	// default access for builder
	boolean runSequentially = false;
	boolean resetProcessEngineWhenStarting = false;
	boolean resetFlowWhenStarting = false;
	boolean reportCoverageWhenFinished = false;
	Collection<Matcher<Double>> assertCoverageMatchers = new ArrayList<Matcher<Double>>();
	
	@Override
    protected void succeeded(Description description) {
        super.succeeded(description);
        log.info(description.getDisplayName() + " succeeded.");
    }
	
    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
        log.info(description.getDisplayName() + " failed.");
    }

	@Override
	public void starting(Description description) {
	    if (runSequentially) {
	        if (sequential.tryLock()) {
	            sequential.unlock();
	        }else {
	            log.info("Have to wait for the lock");
	        }
	        sequential.lock();
	    }
		testCoverageTestRunState =  TestCoverageTestRunState.INSTANCE(); // XXX inject??
		if (resetProcessEngineWhenStarting) {
		    super.processEngine = null;
		}
		if (resetFlowWhenStarting) {
			testCoverageTestRunState.resetCurrentFlowTrace();
		}
		// initialize engine and process
		initializeProcessEngine();
		super.starting(description);
		if (deploymentId != null) {
		    testCoverageTestRunState.relevantDeploymentIds.add(deploymentId);
		}
	}
	
	@Override
	public void finished(Description description) {
	    try { // before unlock
    	    // calculate coverage for all tests
    	    Map<String, Coverage> processesCoverage = Coverages.calculateForDeploymentIds(processEngine, testCoverageTestRunState.getRelevantDeploymentIds());
    
    		// calculate possible coverage		
    		if (this.deploymentId != null) {
    			if (this.reportCoverageWhenFinished) {
    				log.info(processesCoverage.toString());
    			} else {
    				if (log.isLoggable(Level.FINE)) {
    					log.log(Level.FINE, processesCoverage.toString());
    				}
    			}
    		} 	
    		
    		double actualCoverage = Double.NaN;
    		if (description.isSuite()){
    		    if (! testCoverageTestRunState.isHighestSeenCoverageSet()) {
    		        testCoverageTestRunState.highestSeenCoverage = 0;
    		        log.warning("initializing highestSeenCoverage to " + testCoverageTestRunState.highestSeenCoverage);
    		    }
    		    actualCoverage = testCoverageTestRunState.highestSeenCoverage;
    		    log.info("finishing @ClassRule execution with coverage " + actualCoverage);
    		} else {
    			actualCoverage = Coverage.calculateMeanPercentage(processesCoverage);
    			log.info("finishing @Class " + description.getMethodName() + " execution with coverage " + actualCoverage);
    		}
    		for(Matcher<Double> assertCoverageMatcher : assertCoverageMatchers){
   		        Assert.assertThat(actualCoverage, assertCoverageMatcher);
    		}
    		testCoverageTestRunState.highestSeenCoverage = Math.max(actualCoverage, testCoverageTestRunState.highestSeenCoverage);

    		// run derived functionality
    		super.finished(description);
	    } finally {
    	    if (runSequentially) {
    	        sequential.unlock();
    		}
	    }
	}

}
