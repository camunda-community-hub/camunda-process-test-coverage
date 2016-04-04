package org.camunda.bpm.extension.process_test_coverage.model;

/**
 * An element covered by a test.
 * 
 * @author z0rbas
 *
 */
public abstract class CoveredElement {
    
    /**
     * The key of the elements process definition.
     */
    protected String processDefinitionKey;
    
    /**
     * Retrieves the element's ID.
     * @return
     */
    public abstract String getElementId();
    
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }
    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }
    
}
