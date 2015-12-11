package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

public class CoverageMappings {

	public static Set<String> mapElementsToIds(/*@NotNull*/ Collection<CoveredElement> elements) {
		Set<String> ids = new HashSet<String>(elements.size());
		for (CoveredElement element : elements) {
			ids.add(element.getElementId());
		}
		return ids;
	}
	
	public static Collection<String> mapNodesToIds(/*@NotNull*/ Collection<FlowNode> nodes) {
		List<String> ids = new ArrayList<String>(nodes.size());
		for (FlowNode element : nodes) {
			ids.add(element.getId());
		}
		return ids;
	}
	
}
