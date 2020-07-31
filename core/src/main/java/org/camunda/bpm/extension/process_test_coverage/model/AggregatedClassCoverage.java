package org.camunda.bpm.extension.process_test_coverage.model;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.util.CoveredElementComparator;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.*;

/**
 * Test class coverage model. The class coverage is an aggregation of all test method coverages.
 *
 * @author ov7a
 */
public class AggregatedClassCoverage implements AggregatedCoverage {

    private List<ClassCoverage> classCoverages;
    private Map<String, List<ClassCoverage>> classCoveragesByProcessDefinitionKey = new HashMap<String, List<ClassCoverage>>();

    public AggregatedClassCoverage(List<ClassCoverage> classCoverages) {
        this.classCoverages = classCoverages;
        organizeByProcessDefinitionKey();
    }

    private void organizeByProcessDefinitionKey() {
        for (ClassCoverage classCoverage : classCoverages) {
            for (ProcessDefinition processDefinition : classCoverage.getProcessDefinitions()) {
                String key = processDefinition.getKey();
                if (!classCoveragesByProcessDefinitionKey.containsKey(key)) {
                    classCoveragesByProcessDefinitionKey.put(key, new ArrayList<ClassCoverage>());
                }
                classCoveragesByProcessDefinitionKey.get(key).add(classCoverage);
            }
        }
    }

    @Override
    public Set<String> getCoveredFlowNodeIds(String processDefinitionKey) {

        final Set<String> coveredFlowNodeIds = new HashSet<String>();
        for (ClassCoverage classCoverage : classCoveragesByProcessDefinitionKey.get(processDefinitionKey)) {

            coveredFlowNodeIds.addAll(classCoverage.getCoveredFlowNodeIds(processDefinitionKey));
        }

        return coveredFlowNodeIds;
    }

    @Override
    public Set<CoveredFlowNode> getCoveredFlowNodes(String processDefinitionKey) {

        final Set<CoveredFlowNode> coveredFlowNodes = new TreeSet<CoveredFlowNode>(CoveredElementComparator.instance());

        for (ClassCoverage classCoverage : classCoveragesByProcessDefinitionKey.get(processDefinitionKey)) {

            coveredFlowNodes.addAll(classCoverage.getCoveredFlowNodes(processDefinitionKey));
        }

        return coveredFlowNodes;
    }

    @Override
    public Set<String> getCoveredSequenceFlowIds(String processDefinitionKey) {

        final Set<String> coveredSequenceFlowIds = new HashSet<String>();
        for (ClassCoverage classCoverage : classCoveragesByProcessDefinitionKey.get(processDefinitionKey)) {

            coveredSequenceFlowIds.addAll(classCoverage.getCoveredSequenceFlowIds(processDefinitionKey));
        }

        return coveredSequenceFlowIds;
    }

    @Override
    public Set<CoveredSequenceFlow> getCoveredSequenceFlows(String processDefinitionKey) {

        final Set<CoveredSequenceFlow> coveredSequenceFlows = new TreeSet<CoveredSequenceFlow>(CoveredElementComparator.instance());
        for (ClassCoverage classCoverage : classCoveragesByProcessDefinitionKey.get(processDefinitionKey)) {

            coveredSequenceFlows.addAll(classCoverage.getCoveredSequenceFlows(processDefinitionKey));
        }

        return coveredSequenceFlows;
    }

    @Override
    public Set<ProcessDefinition> getProcessDefinitions() {

        final Set<ProcessDefinition> processDefinitions = new TreeSet<ProcessDefinition>(
                new Comparator<ProcessDefinition>() {

                    // Avoid removing process definitions with the same key, but coming from different BPMNs.
                    @Override
                    public int compare(ProcessDefinition o1, ProcessDefinition o2) {
                        return o1.getResourceName().compareTo(o2.getResourceName());
                    }
                });

        for (ClassCoverage classCoverage : classCoverages) {
            processDefinitions.addAll(classCoverage.getProcessDefinitions());
        }

        return processDefinitions;
    }

    @Override
    public double getCoveragePercentage() {

        Set<ProcessDefinition> processDefinitions = getProcessDefinitions();

        final Set<FlowNode> definitionsFlowNodes = new HashSet<FlowNode>();
        final Set<SequenceFlow> definitionsSequenceFlows = new HashSet<SequenceFlow>();

        final Set<CoveredFlowNode> coveredFlowNodes = new TreeSet<CoveredFlowNode>(CoveredElementComparator.instance());
        final Set<CoveredSequenceFlow> coveredSequenceFlows = new TreeSet<CoveredSequenceFlow>(CoveredElementComparator.instance());

        for (ProcessDefinition processDefinition : processDefinitions) {
            String processDefinitionKey = processDefinition.getKey();

            final MethodCoverage deploymentWithProcessDefinition = getMethodCoverage(processDefinitionKey);

            definitionsFlowNodes.addAll(deploymentWithProcessDefinition.getProcessDefinitionsFlowNodes(processDefinitionKey));
            definitionsSequenceFlows.addAll(deploymentWithProcessDefinition.getProcessDefinitionsSequenceFlows(processDefinitionKey));

            coveredFlowNodes.addAll(getCoveredFlowNodes(processDefinitionKey));
            coveredSequenceFlows.addAll(getCoveredSequenceFlows(processDefinitionKey));
        }

        final double bpmnElementsCount = definitionsFlowNodes.size() + definitionsSequenceFlows.size();
        final double coveredElementsCount = coveredFlowNodes.size() + coveredSequenceFlows.size();

        return coveredElementsCount / bpmnElementsCount;
    }

    @Override
    public double getCoveragePercentage(String processDefinitionKey) {
        final MethodCoverage deploymentWithProcessDefinition = getMethodCoverage(processDefinitionKey);

        final Set<FlowNode> definitionsFlowNodes = deploymentWithProcessDefinition.getProcessDefinitionsFlowNodes(processDefinitionKey);
        final Set<SequenceFlow> definitionsSequenceFlows = deploymentWithProcessDefinition.getProcessDefinitionsSequenceFlows(processDefinitionKey);

        final Set<CoveredFlowNode> coveredFlowNodes = getCoveredFlowNodes(processDefinitionKey);
        final Set<CoveredSequenceFlow> coveredSequenceFlows = getCoveredSequenceFlows(processDefinitionKey);

        final double bpmnElementsCount = definitionsFlowNodes.size() + definitionsSequenceFlows.size();
        final double coveredElementsCount = coveredFlowNodes.size() + coveredSequenceFlows.size();

        return coveredElementsCount / bpmnElementsCount;
    }

    private MethodCoverage getMethodCoverage(String processDefinitionKey) {
        return classCoveragesByProcessDefinitionKey.get(processDefinitionKey).get(0).getAnyMethodCoverage();
    }
}
