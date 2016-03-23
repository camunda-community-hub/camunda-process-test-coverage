package org.camunda.bpm.extension.process_test_coverage.util;

import java.util.Comparator;

import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;

public class CoveredElementIdComparator implements Comparator<CoveredElement> {
    
    private static CoveredElementIdComparator singleton;
    
    private CoveredElementIdComparator(){};
    
    public static CoveredElementIdComparator instance() {
        
        if (singleton == null) {
            singleton = new CoveredElementIdComparator();
        }
        
        return singleton;
    }

    @Override
    public int compare(CoveredElement o1, CoveredElement o2) {
        final int processDefinitionIdComparison =  
                getProcessKey(o1.getProcessDefinitionId())
                .compareTo(getProcessKey(o2.getProcessDefinitionId()));
        
        if (processDefinitionIdComparison == 0) {
            return o1.getElementId().compareTo(o2.getElementId());
        }
        
       return processDefinitionIdComparison;
    }
    
    // FIXME hack!
    private String getProcessKey(final String processDefinitionId) {
        return processDefinitionId.substring(0, processDefinitionId.indexOf(':'));
    }

}
