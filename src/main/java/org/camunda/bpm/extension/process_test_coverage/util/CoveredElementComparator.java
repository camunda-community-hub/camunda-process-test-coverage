package org.camunda.bpm.extension.process_test_coverage.util;

import java.util.Comparator;

import org.camunda.bpm.extension.process_test_coverage.model.CoveredElement;

/**
 * Compared covered elements by their process definition keys and element IDs.
 * 
 * @author z0rbas
 *
 */
public class CoveredElementComparator implements Comparator<CoveredElement> {
    
    private static CoveredElementComparator singleton;
    
    private CoveredElementComparator(){};
    
    public static CoveredElementComparator instance() {
        
        if (singleton == null) {
            singleton = new CoveredElementComparator();
        }
        
        return singleton;
    }

    @Override
    public int compare(CoveredElement o1, CoveredElement o2) {
        
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        }
        
        final int processDefinitionIdComparison =  
                o1.getProcessDefinitionKey()
                .compareTo(o2.getProcessDefinitionKey());
        
        if (processDefinitionIdComparison == 0) {
            return o1.getElementId().compareTo(o2.getElementId());
        }
        
       return processDefinitionIdComparison;
    }

}
