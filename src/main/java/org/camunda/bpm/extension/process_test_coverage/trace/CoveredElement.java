package org.camunda.bpm.extension.process_test_coverage.trace;

public abstract class CoveredElement {
    
    protected String processDefinitionId;
    
    public abstract String getElementId();
    
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
    
}
