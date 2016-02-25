package org.camunda.bpm.extension.process_test_coverage;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageTestRunState;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class Coverages {

    private static final Logger log = Logger.getLogger(Coverages.class.getCanonicalName());

    public static final String targetDir = "target/process-test-coverage";

    private static TestCoverageTestRunState getTestCoverageTestRunState() {
        return TestCoverageTestRunState.INSTANCE();
    }

    public static Map<String, Coverage> calculate(ProcessEngine processEngine, ProcessInstance processInstance) {
        String definitionId = processInstance.getProcessDefinitionId();
        List<ProcessDefinition> processDefinitions = new ArrayList<ProcessDefinition>();
        processDefinitions.addAll(
                processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(
                        definitionId).list());
        CoverageBuilder builder = null;
        // builder = CoverageBuilder.create("flowNodeAndSequenceFlowCoverage")
        return _calculate(processEngine, builder, processDefinitions, true);
    }

    public static Map<String, Coverage> calculateForDeploymentIds(ProcessEngine processEngine,
            Set<String> relevantDeploymentIds) {
        List<ProcessDefinition> processDefinitions = new ArrayList<ProcessDefinition>();
        for (String deploymentId : relevantDeploymentIds) {
            processDefinitions.addAll(processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(
                    deploymentId).list());
        }
        CoverageBuilder builder = null;
        // builder = CoverageBuilder.create("flowNodeAndSequenceFlowCoverage")
        return _calculate(processEngine, builder, processDefinitions, false);
    }

    private static Map<String, Coverage> _calculate(ProcessEngine processEngine, CoverageBuilder builder,
            Collection<ProcessDefinition> processDefinitions, boolean useDefinitionId) {
        // FIXME weird: passing a builder leads to concurrency issues
        log.info("Calculating coverage");
        try {
            Map<String, Coverage> processesCoverage = new HashMap<String, Coverage>();
            for (ProcessDefinition processDefinition : processDefinitions) {
                BpmnModelInstance modelInstance = processEngine.getRepositoryService().getBpmnModelInstance(
                        processDefinition.getId());
                Collection<FlowNode> flowNodes = modelInstance.getModelElementsByType(FlowNode.class);
                Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);

                String bpmnXml = getBpmnXml(processDefinition);

                Set<String> coveredActivityIds = getTestCoverageTestRunState().findCoveredActivityIds();
                String reportName = processDefinition.getKey() + ".html";
                BpmnJsReport.highlightActivities(bpmnXml, coveredActivityIds, reportName, targetDir);

                CoverageBuilder builder2 = builder;
                if (builder2 == null) {
                    builder2 = CoverageBuilder.create("flowNodeAndSequenceFlowCoverage");
                }
                builder2.forProcess(processDefinition) //
                .withActualFlowNodesAndSequenceFlows(getTestCoverageTestRunState()) //
                .withExpectedFlowNodes(flowNodes) //
                .withExpectedSequenceFlows(sequenceFlows);
                if (useDefinitionId) {
                    builder2.filterByDefinitionIdInsteadOfKey();
                }
                Coverage coverage = builder2.build();
                processesCoverage.put(processDefinition.getKey(), coverage);
                log.info("Calculated coverage for " + processDefinition + " : " + coverage);
            }
            return processesCoverage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String getBpmnXml(ProcessDefinition processDefinition) throws IOException {
        InputStream inputStream = Coverages.class.getClassLoader().getResourceAsStream(
                processDefinition.getResourceName());
        if (inputStream == null) {
            inputStream = new FileInputStream(processDefinition.getResourceName());
        }
        return IOUtils.toString(inputStream);
    }

}