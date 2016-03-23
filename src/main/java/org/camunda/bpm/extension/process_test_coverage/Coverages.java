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
import org.camunda.bpm.extension.process_test_coverage.trace.DeploymentCoverage;
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
    
    /**
     * Generates a covered report for the whole test class.
     * This method requires that all tests have been executed the same resources deployed.
     * 
     * @param processEngine
     * @param coverageTestRunState
     * @param className
     * @throws IOException 
     */
    public static void createClassReport(ProcessEngine processEngine,
            CoverageTestRunState coverageTestRunState, String className,
            double coverage) throws IOException {
        
        final Set<ProcessCoverage> classProcessCoverages = coverageTestRunState.getClassProcessCoverages();
        createReport(coverageTestRunState, className, null, classProcessCoverages, coverage);
        
    }

    public static void createTestMethodReport(ProcessEngine processEngine,
            CoverageTestRunState coverageTestRunState, String className,
            String testName, double coverage) throws IOException {
        
        final DeploymentCoverage deploymentCoverage = coverageTestRunState.getDeploymentCoverage(testName);
        
        createReport(coverageTestRunState, className, testName, deploymentCoverage.getProcessCoverages(), coverage);
        
    }

    private static void createReport(CoverageTestRunState coverageTestRunState, String className, String testName,
            Collection<ProcessCoverage> processCoverages, double coverage) throws IOException {
        
        // Split the activities reading to their definitions
        Map<String, Set<String>> processDefKeyToCoveredFlowNodes = new HashMap<String, Set<String>>();
        Map<String, Set<String>> processDefKeyToCoveredSequenceFlows = new HashMap<String, Set<String>>();
        for (ProcessCoverage processCoverage : processCoverages) {
            
            final String processDefinitionKey = processCoverage.getProcessDefinition().getKey();
            
            // Flow nodes
            
            Set<String> coveredFlowNodes = processDefKeyToCoveredFlowNodes.get(processDefinitionKey);
            if (coveredFlowNodes == null) {
                
                coveredFlowNodes = new HashSet<String>();
                processDefKeyToCoveredFlowNodes.put(processDefinitionKey, coveredFlowNodes);
            }
            
            for(CoveredElement coveredElement : processCoverage.getCoveredFlowNodes()) {
                
                coveredFlowNodes.add(coveredElement.getElementId());
            }
            
            // Sequence flows
            
            Set<String> coveredSequenceFlows = processDefKeyToCoveredSequenceFlows.get(processDefinitionKey);
            if (coveredSequenceFlows == null) {
                
                coveredSequenceFlows = new HashSet<String>();
                processDefKeyToCoveredSequenceFlows.put(processDefinitionKey, coveredSequenceFlows);
            }
            
            for(CoveredElement coveredElement : processCoverage.getCoveredSequenceFlows()) {
                
                coveredSequenceFlows.add(coveredElement.getElementId());
            }
        }
        
        final Set<ProcessDefinition> processDefinitions = coverageTestRunState.getAnyDeployment().getProcessDefinitions();
        for (ProcessDefinition processDefinition : processDefinitions) {
            
            String bpmnXml = getBpmnXml(processDefinition);
            String reportName = getReportName(processDefinition, testName);
            Set<String> coveredFlowNodes = processDefKeyToCoveredFlowNodes.get(processDefinition.getKey());
            Set<String> coveredSequenceFlows = processDefKeyToCoveredSequenceFlows.get(processDefinition.getKey());
            
            final String reportDirectory = getReportDirectoryPath(className);
            BpmnJsReport.highlightFlowNodesAndSequenceFlows(
                    bpmnXml, coveredFlowNodes, coveredSequenceFlows, reportName, reportDirectory, coverage);    
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