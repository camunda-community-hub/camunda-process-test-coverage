package org.camunda.bpm.extension.process_test_coverage.trace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoveredElements {
    
	public static Set<CoveredElement> findCoveredElementsOfType(
	        Class<? extends CoveredElement> elementClass, 
	        Set<CoveredElement> elements) {
	    
	    Set<CoveredElement> result = new HashSet<CoveredElement>();
	    if (elements == null) {
	        return result;
	    }

		Map<String, CoveredElement> foundWithBenefits = new HashMap<String, CoveredElement>();	
		for (CoveredElement el : elements) {
			
			// Skip elements that are not of the requested elementClass
			if (elementClass != null && ! elementClass.isInstance(el)) {
				continue;
			}
			
			foundWithBenefits.put(el.getElementId(), el);
		}
		
		result.addAll(foundWithBenefits.values());
		return result;
	}
	
}
