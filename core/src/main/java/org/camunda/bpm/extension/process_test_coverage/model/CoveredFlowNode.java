package org.camunda.bpm.extension.process_test_coverage.model;

/**
 * An activity covered by a test.
 * 
 * @author grossax
 * @author z0rbas
 *
 */
public class CoveredFlowNode extends CoveredElement {

    /**
     * Element ID of the activity.
     */
    private final String flowNodeId;

    /**
     * A flow node object is created in the coverage once it has started
     * execution. This flag is set to true once the flow node has finished (end
     * event).
     */
    private boolean ended = false;

    public CoveredFlowNode(String processDefinitionKey, String flowNodeId) {
        this.flowNodeId = flowNodeId;
        this.processDefinitionKey = processDefinitionKey;
    }

    @Override
    public String getElementId() {
        return flowNodeId;
    }

    public boolean hasEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    @Override
    public String toString() {
        return "CoveredActivity [flowNodeId=" + flowNodeId + ", processDefinitionKey=" + processDefinitionKey + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((flowNodeId == null) ? 0 : flowNodeId.hashCode());
        result = prime * result + ((processDefinitionKey == null) ? 0 : processDefinitionKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CoveredFlowNode other = (CoveredFlowNode) obj;
        if (flowNodeId == null) {
            if (other.flowNodeId != null)
                return false;
        } else if (!flowNodeId.equals(other.flowNodeId))
            return false;
        if (processDefinitionKey == null) {
            if (other.processDefinitionKey != null)
                return false;
        } else if (!processDefinitionKey.equals(other.processDefinitionKey))
            return false;
        return true;
    }

}
