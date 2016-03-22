package org.camunda.bpm.extension.process_test_coverage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.util.BitMaskUtil;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredActivity;
import org.camunda.bpm.extension.process_test_coverage.trace.CoveredElement;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Coverages {

    private static final Logger log = Logger.getLogger(Coverages.class.getCanonicalName());

    public static final String TARGET_DIR_ROOT = "target/process-test-coverage";

    public  static Coverage calculateTestMethodCoverage(
            ProcessEngine processEngine, String deploymentId, 
            CoverageTestRunState state, String testMethod) throws IOException {
        
        final Set<CoveredElement> coveredFlowNodes = state.getCoveredFlowNodesForTest(testMethod);
        final Set<CoveredElement> coveredSequenceFlows = state.getCoveredSequenceFlowsForTest(testMethod);
        
        final Coverage result = calculateCoverageForDeploymentId(processEngine, deploymentId, coveredSequenceFlows, coveredFlowNodes);

        return result;
    }
    
    public static Coverage calculateGlobalTestCoverage(
            ProcessEngine processEngine, String processDefinitionId, 
            CoverageTestRunState state) throws IOException {
        
        final Set<CoveredElement> coveredFlowNodes = state.getGlobalCoveredFlowNodes();
        final Set<CoveredElement> coveredSequenceFlows = state.getGlobalCoveredSequenceFlows();
        
        return calculateCoverageForProcessDefinitionId(processEngine, processDefinitionId, coveredSequenceFlows, coveredFlowNodes);
    }
    
    public static Coverage calculateCoverageForDeploymentId(ProcessEngine processEngine, String deploymentId,
            final Set<CoveredElement> coveredSequenceFlows, final Set<CoveredElement> coveredFlowNodes) {
        

        final ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        final String processDefinitionId = processDefinition.getId();
        
        return calculateCoverageForProcessDefinitionId(processEngine, processDefinitionId, coveredSequenceFlows, coveredFlowNodes);
    }

    private static Coverage calculateCoverageForProcessDefinitionId(ProcessEngine processEngine, final String processDefinitionId,
            final Set<CoveredElement> coveredSequenceFlows, final Set<CoveredElement> coveredFlowNodes) {
        
        // Get process definition elements
        
        BpmnModelInstance modelInstance = processEngine.getRepositoryService().getBpmnModelInstance(
                processDefinitionId);
        Collection<FlowNode> flowNodes = modelInstance.getModelElementsByType(FlowNode.class);
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);

        // Build the coverage object
        
        final CoverageBuilder builder = CoverageBuilder.create("flowNodeAndSequenceFlowCoverage");
        builder.forProcess(processDefinitionId) //
        .withActualFlowNodes(coveredFlowNodes) //
        .withActualSequenceFlows(coveredSequenceFlows)
        .withExpectedFlowNodes(flowNodes) //
        .withExpectedSequenceFlows(sequenceFlows);
        
        Coverage coverage = builder.build();
       
        if(log.isLoggable(Level.FINE)) {
            log.fine("Calculated coverage for " + processDefinitionId + " : " + coverage);
        } else {
            log.info("Calculated coverage for " + processDefinitionId );
        }
        
        return coverage;
    }
    
    /**
     * Generates a covered report for the whole test class.
     * This method requires that all tests have been executed the same resources deployed.
     * 
     * @param processEngine
     * @param coverageTestRunState
     * @param className
     * @throws IOException 
     */
    public static void createGlobalReport(ProcessEngine processEngine,
            CoverageTestRunState coverageTestRunState, String className,
            double coverage) throws IOException {
        
        final Map<String, Set<ProcessDefinition>> testRunDeployments = coverageTestRunState.getDeployments();
        
        // Since all deployments are the same, grab any
        final Set<ProcessDefinition> anyDeployment = testRunDeployments.values().iterator().next();
        final Set<String> coveredElementIds = coverageTestRunState.findGlobalCoveredActivityIds();
        
        createReport(coverageTestRunState, className, null, anyDeployment, coveredElementIds, coverage);
        
    }

    public static void createTestMethodReport(ProcessEngine processEngine, String deploymentId,
            CoverageTestRunState coverageTestRunState, String className,
            String testName, double coverage) throws IOException {
        
        final List<ProcessDefinition> processDefinitions = processEngine.getRepositoryService().
                createProcessDefinitionQuery().deploymentId(deploymentId).list();
        final Set<String> coveredElementIds = coverageTestRunState.findCoveredActivityIdsForTest(testName);
        
        createReport(coverageTestRunState, className, testName, processDefinitions, coveredElementIds, coverage);
        
    }

    private static void createReport(CoverageTestRunState coverageTestRunState, String className, String testName,
            Collection<ProcessDefinition> processDefinitions, Set<String> coveredElementIds,
            double coverage) throws IOException {
        
        for (ProcessDefinition processDefinition : processDefinitions) {
            
            String bpmnXml = getBpmnXml(processDefinition);
            String reportName = getReportName(processDefinition, testName);
            
            final String reportDirectory = getReportDirectoryPath(className);
            BpmnJsReport.highlightActivitiesWithCoverage(
                    bpmnXml, coveredElementIds, reportName, reportDirectory, coverage);    
        }
        
    }
    
    private static String getReportDirectoryPath(final String className) {
        return TARGET_DIR_ROOT + '/' + className;
    }

    private static String getReportName(ProcessDefinition processDefinition, 
            final String reportNamePrefix) {
        
        
        final StringBuilder builder = new StringBuilder();
        
        if (StringUtils.isNotBlank(reportNamePrefix)) {
            builder.append(reportNamePrefix);
            builder.append('_');
        }
        
        builder.append(processDefinition.getKey());
        builder.append(".html");
        
        return builder.toString();
        
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