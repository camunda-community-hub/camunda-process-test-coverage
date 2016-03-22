package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.Coverage;
import org.camunda.bpm.extension.process_test_coverage.Coverages;
import org.camunda.bpm.extension.process_test_coverage.trace.PathCoverageExecutionListener;
import org.camunda.bpm.extension.process_test_coverage.trace.PathCoverageParseListener;
import org.camunda.bpm.extension.process_test_coverage.trace.TraceActivitiesHistoryEventHandler;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.runner.Description;

/**
 * Rule calculating the test coverage for a process definition. 
 * The coverage percentage is asserted and graphical coverage reports are generated, per class or per method.
 *  
 * @author grossax
 * @author okicir
 *
 */
public class TestCoverageProcessEngineRule extends ProcessEngineRule {

	public static Logger log = Logger.getLogger(TestCoverageProcessEngineRule.class.getCanonicalName());
	
	/**
	 * The state of the current run (class and current method).
	 */
	private CoverageTestRunState testCoverageTestRunState;
		
	private boolean firstRun = true;
	
	// default access for builder
	private boolean calculateGlobal = false;
	private boolean reportCoverageWhenFinished = false;
	private Collection<Matcher<Double>> assertGlobalCoverageMatchers = new LinkedList<Matcher<Double>>();
	private Map<String, Collection<Matcher<Double>>> testMethodNameToCoverageMatchers = new HashMap<String, Collection<Matcher<Double>>>();
	
	public void assertTestCoverage(final String testMethodName, final Matcher<Double> matcher) {
	    
	    // JDK7 ifAbsent
	    Collection<Matcher<Double>> matchers = testMethodNameToCoverageMatchers.get(testMethodName);
	    if (matchers == null) {
	        matchers = new LinkedList<Matcher<Double>>();
	        testMethodNameToCoverageMatchers.put(testMethodName, matchers);
	    }
	    
	    matchers.add(matcher);
	    
	}

    public void addGlobalAssertCoverageMatcher(MinimalCoverageMatcher matcher) {
        assertGlobalCoverageMatchers.add(matcher);        
    }
	
	@Override
	public org.junit.runners.model.Statement apply(org.junit.runners.model.Statement base, Description description) {
	    return super.apply(base, description);
	    
	};
	
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
	    
	    super.initializeProcessEngine();
		
	    initializeRunState(description);
	    
		super.starting(description);
		
		registerDeployments();
	}

    private void registerDeployments() {
    
        if (deploymentId != null) {

            final Set<ProcessDefinition> deployedProcessDefinitions = new TreeSet<ProcessDefinition>(getProcessDefinitionKeyComparator());
            deployedProcessDefinitions.addAll(processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deploymentId).list());
            
            // TODO do we need this
            testCoverageTestRunState.getDeployments().put(deploymentId, deployedProcessDefinitions);
        }
    }
    
    
    private void initializeRunState(final Description description) {
                
        // Initialize new state only on @ClasRule run
        if (firstRun) {
            
            testCoverageTestRunState = new CoverageTestRunState();
            
            final ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();

            final TraceActivitiesHistoryEventHandler historyEventHandler = (TraceActivitiesHistoryEventHandler) processEngineConfiguration.getHistoryEventHandler();
            historyEventHandler.setTestCoverageRunState(testCoverageTestRunState);

            final List<BpmnParseListener> bpmnParseListeners = processEngineConfiguration.getCustomPostBPMNParseListeners();
            for (BpmnParseListener parseListener : bpmnParseListeners) {
                if (parseListener instanceof PathCoverageParseListener) {
                    final PathCoverageParseListener listener = (PathCoverageParseListener) parseListener;
                    listener.setCoverageTestRunState(testCoverageTestRunState);
                }
            }
            
            firstRun = false;
        }
        
        testCoverageTestRunState.setCurrentTestName(description.getMethodName());

    }
    
    private Comparator<ProcessDefinition> getProcessDefinitionKeyComparator() {
        return new Comparator<ProcessDefinition>() {

            @Override
            public int compare(ProcessDefinition o1, ProcessDefinition o2) {
               return o1.getKey().compareTo(o2.getKey());
            }
            
        };
    }
	
	@Override
    public void finished(Description description) {

        // In case the class does not only contain bpmn tests
        // TODO check if this is already taken care of by super
        try {

            final Deployment deploymentAnnotation = description.getAnnotation(Deployment.class);
            if (deploymentAnnotation != null) {

                final String testMethodName = description.getMethodName();

                Coverage testCoverage;
                testCoverage = Coverages.calculateTestMethodCoverage(processEngine,
                        deploymentId,
                        testCoverageTestRunState,
                        testMethodName);


                logCoverage(testCoverage);

                double coveragePercentage = testCoverage.calculateMeanPercentage();
                log.info(testMethodName + " process coverage is " + coveragePercentage);

                Coverages.createTestMethodReport(processEngine,
                        deploymentId,
                        testCoverageTestRunState,
                        description.getClassName(),
                        testMethodName,
                        coveragePercentage);
                
                if (testMethodNameToCoverageMatchers.containsKey(testMethodName)) {

                    assertCoverage(coveragePercentage, testMethodNameToCoverageMatchers.get(testMethodName));

                }

            }

                handleGlobalCoverage(description);
                
            } catch (IOException e) {
                
                Assert.fail("Unable to load process definition resource!");
                e.printStackTrace();
                
            }

        // run derived functionality
        super.finished(description);

    }
	
	private void handleGlobalCoverage(Description description) throws IOException {
	            
        // Calculate global coverage if requested and all individual tests done
        if (!description.isTest()){
            
            final ProcessDefinition processDefinition = assertSingleProcessDefinitionDeployed();

            
            // Calculate coverage
            
            final Coverage globaCoverage = Coverages.calculateGlobalTestCoverage(processEngine, processDefinition.getId(), testCoverageTestRunState);
            final double coveragePercentage = globaCoverage.calculateMeanPercentage();
            log.info("finishing @ClassRule execution with coverage " + coveragePercentage);

            // Create graphical report
            Coverages.createGlobalReport(processEngine, testCoverageTestRunState, description.getClassName(), coveragePercentage);
            
            assertCoverage(coveragePercentage, assertGlobalCoverageMatchers);
                                   
        }
	}
	
	private ProcessDefinition assertSingleProcessDefinitionDeployed() {
	    
	    Set<ProcessDefinition> processDefinitions = null;
        for (Set<ProcessDefinition> deploymentProcessDefinitions : testCoverageTestRunState.getDeployments().values()) {
            
            if (processDefinitions == null) {
                processDefinitions = deploymentProcessDefinitions;
            }
                
            Assert.assertEquals("Class coverage can only be calculated if all test deploy the same process definition", processDefinitions, deploymentProcessDefinitions);
            
        }
        
        // TODO take into account multiple process definitions!
        return processDefinitions.iterator().next();
        
	}
	
	private void assertCoverage(double coverage, Collection<Matcher<Double>> matchers) {
	    
        for(Matcher<Double> matcher : matchers){
            
            Assert.assertThat(coverage, matcher);
        }
        
	}

    private void logCoverage(Coverage processesCoverage) {
        
        if (deploymentId != null) {
            
        	if (log.isLoggable(Level.FINE) || isReportCoverageWhenFinished()) {
        		log.log(Level.ALL, processesCoverage.toString());
        	}
        	
        }
    }

    public boolean isCalculateGlobal() {
        return calculateGlobal;
    }

    public void setCalculateGlobal(boolean calculateGlobal) {
        this.calculateGlobal = calculateGlobal;
    }

    public boolean isReportCoverageWhenFinished() {
        return reportCoverageWhenFinished;
    }

    public void setReportCoverageWhenFinished(boolean reportCoverageWhenFinished) {
        this.reportCoverageWhenFinished = reportCoverageWhenFinished;
    }

}
