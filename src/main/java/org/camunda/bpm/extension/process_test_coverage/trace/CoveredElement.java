package org.camunda.bpm.extension.process_test_coverage.trace;

public abstract class CoveredElement {
    
    protected String processDefinitionKey;
    
    public abstract String getElementId();
    
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }
    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }
    
}
