package org.camunda.bpm.extension.process_test_coverage.trace;

import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;

/**
 * Adds a PathCoverageExecutionListener to every transition. 
 */
public class PathCoverageParseListener extends AbstractBpmnParseListener {
	
	@Override
	public void parseSequenceFlow(Element sequenceFlowElement, ScopeImpl scopeElement, org.camunda.bpm.engine.impl.pvm.process.TransitionImpl transition) {
		// XXX deprecated - anybody with a better way please step up
		transition.addExecutionListener(new PathCoverageExecutionListener());
	};
	
}
