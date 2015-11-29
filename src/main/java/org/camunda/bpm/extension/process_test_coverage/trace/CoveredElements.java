package org.camunda.bpm.extension.process_test_coverage.trace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoveredElements {
	public static Set<CoveredElement> findProcessInstances(String processDefinitionIdStart, Class<? extends CoveredElement> class1, Set<CoveredElement> elements) {
		Map<String, CoveredElement> foundWithBenefits = new HashMap<String, CoveredElement>();
		for (CoveredElement el : elements) {
			if (processDefinitionIdStart != null && ! el.getProcessDefinitionId().startsWith(processDefinitionIdStart)) {
				continue;
			}
			if (class1 != null && ! class1.isInstance(el)) {
				continue;
			}
			// FIXME this is to remove "dupes" from different deployments; it will result in elements from random deployments
			String equalityKey = processDefinitionIdStart + "#" + el.getElementId();
			foundWithBenefits.put(equalityKey, el);
		}
		Set<CoveredElement> found = new HashSet<CoveredElement>();
		found.addAll(foundWithBenefits.values());
		return found;
	}
}
