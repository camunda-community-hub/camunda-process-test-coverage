package org.camunda.bpm.extension.process_test_coverage.util;

import java.util.Comparator;

import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;

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
        final int processDefinitionIdComparison =  
                o1.getProcessDefinitionKey()
                .compareTo(o2.getProcessDefinitionKey());
        
        if (processDefinitionIdComparison == 0) {
            return o1.getElementId().compareTo(o2.getElementId());
        }
        
       return processDefinitionIdComparison;
    }

}
