package org.camunda.bpm.extension.process_test_coverage.trace;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;

/**
 * Adds a PathCoverageExecutionListener to every transition. 
 */
public class PathCoverageParseListener extends AbstractBpmnParseListener {
	
    /**
     * The state of the coverage test run.
     */
    private CoverageTestRunState coverageTestRunState;
    
    public PathCoverageParseListener() {}
    
	public PathCoverageParseListener(CoverageTestRunState coverageTestRunState) {
        this.coverageTestRunState = coverageTestRunState;
    }

    @Override
	public void parseSequenceFlow(Element sequenceFlowElement, ScopeImpl scopeElement, org.camunda.bpm.engine.impl.pvm.process.TransitionImpl transition) {
        
        final PathCoverageExecutionListener pathCoverageExecutionListener = new PathCoverageExecutionListener(coverageTestRunState);
        transition.addListener(ExecutionListener.EVENTNAME_TAKE, pathCoverageExecutionListener);
	}
	
	public void setCoverageTestRunState(CoverageTestRunState coverageTestRunState) {
	    this.coverageTestRunState = coverageTestRunState;
	}
}
