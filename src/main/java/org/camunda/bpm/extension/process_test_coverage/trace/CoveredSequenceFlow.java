package org.camunda.bpm.extension.process_test_coverage.trace;

import java.util.Date;

public class CoveredSequenceFlow implements CoveredElement {

	private Date timestamp;
	private String coveredTransitionId;
	private String processDefinitionId;

	public CoveredSequenceFlow(String processDefinitionId, String coveredTransitionId) {
		this.timestamp = new Date();
		this.coveredTransitionId = coveredTransitionId;
	}

	@Override
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	@Override
	public String getElementId() {
		return coveredTransitionId;
	}

}
