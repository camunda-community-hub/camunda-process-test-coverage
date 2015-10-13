package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.HashSet;
import java.util.Set;

public class TestCoverageTestRunState {

	/** NonNull global variable holding a list of the flow trace elements of the test run.<br>
	 * clear this (e.g. in @BeforeClass) if you want to */
	static Set<String> FIXME_GLOBAL_VAR__CURRENT_FLOW_TRACE = new HashSet<String>();
	static double FIXME_GLOBAL_VAR__HIGHEST_SEEN_COVERAGE = -2.0;
	/** clears the current flow trace, e.g. in @BeforeClass */
	public static void resetCurrentFlowTrace(){
		System.out.println("Clearing flow trace");
		FIXME_GLOBAL_VAR__CURRENT_FLOW_TRACE.clear();
		FIXME_GLOBAL_VAR__HIGHEST_SEEN_COVERAGE = -2.0;
	}
	public static void notifyCoveredSequenceFlow(String processDefinitionId, String currentTransitionId) {
		// TODO processDefinitionId
		FIXME_GLOBAL_VAR__CURRENT_FLOW_TRACE.add(currentTransitionId);
	}
	public static Set<String> getCoveredSequenceFlows() {
		return FIXME_GLOBAL_VAR__CURRENT_FLOW_TRACE;
	}

}
