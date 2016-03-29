package org.camunda.bpm.extension.process_test_coverage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;

public class CoverageReportUtil {

    private static final Logger logger = Logger.getLogger(CoverageReportUtil.class.getCanonicalName());

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
            double coverage) {
        
        createReport(coverageTestRunState, true);
        
    }

    public static void createCurrentTestMethodReport(ProcessEngine processEngine,
            CoverageTestRunState coverageTestRunState, double coverage) {
        
        createReport(coverageTestRunState, false);
        
    }

    private static void createReport(CoverageTestRunState coverageTestRunState, boolean classReport) {
               
        // Get the appropriate coverage
        Coverage coverage;
        if (classReport) {
            coverage = coverageTestRunState.getClassCoverage();
        } else {
            coverage = coverageTestRunState.getCurrentTestMethodCoverage();
        }
        
        // Generate a report for every process definition
        final Set<ProcessDefinition> processDefinitions = coverage.getProcessDefinitions();
        for (ProcessDefinition processDefinition : processDefinitions) {
            
            try {
                
                // Assemble data
                final String testClass = coverageTestRunState.getClassName();
                final String testName = coverageTestRunState.getCurrentTestName();
                final Set<String> coveredFlowNodeIds = coverage.getCoveredFlowNodeIds(processDefinition.getKey());
                final Set<String> coveredSequenceFlowIds = coverage.getCoveredSequenceFlowIds(processDefinition.getKey());
                final String reportName = classReport ?
                        getReportName(processDefinition, null) : 
                        getReportName(processDefinition, testName);
                
                final String reportDirectory = getReportDirectoryPath(testClass);
                final String bpmnXml = getBpmnXml(processDefinition);
                
                // Generate report
                
                BpmnJsReport.highlightFlowNodesAndSequenceFlows(
                        bpmnXml, coveredFlowNodeIds, coveredSequenceFlowIds, reportName, 
                        processDefinition.getKey(), coverage.getCoveragePercentage(), testClass, testName, classReport, 
                        reportDirectory);
                
            } catch (IOException ex) {
                
                logger.log(Level.SEVERE, "Unable to load process definition!", ex);
                throw new RuntimeException();
                
            }
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
        
        InputStream inputStream = CoverageReportUtil.class.getClassLoader().getResourceAsStream(
                processDefinition.getResourceName());
        if (inputStream == null) {
            inputStream = new FileInputStream(processDefinition.getResourceName());
        }
        
        return IOUtils.toString(inputStream);
    }

}