package org.camunda.bpm.extension.process_test_coverage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.model.AggregatedCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredFlowNode;

/**
 * Utility for generating graphical class and method coverage reports.
 * 
 * @author z0rbas
 *
 */
public class CoverageReportUtil {

    private static final Logger logger = Logger.getLogger(CoverageReportUtil.class.getCanonicalName());

    /**
     * Root directory for all coverage reports.
     */
    public static final String TARGET_DIR_ROOT = "target/process-test-coverage/";
    public static final String BOWER_DIR_NAME = "bower_components";
    public static final String BOWER_DIR_PATH = TARGET_DIR_ROOT + BOWER_DIR_NAME;

    /**
     * Generates a coverage report for the whole test class. This method
     * requires that all tests have been executed with the same resources
     * deployed.
     * 
     * @param processEngine
     * @param coverageTestRunState
     */
    public static void createClassReport(ProcessEngine processEngine, CoverageTestRunState coverageTestRunState) {

        createReport(coverageTestRunState, true);

    }

    /**
     * Generates graphical test coverage reports for the current test method
     * run.
     * 
     * @param processEngine
     * @param coverageTestRunState
     */
    public static void createCurrentTestMethodReport(ProcessEngine processEngine,
            CoverageTestRunState coverageTestRunState) {

        createReport(coverageTestRunState, false);

    }

    /**
     * Generates a coverage report.
     * 
     * @param coverageTestRunState
     * @param classReport
     *            If false the current test method coverage reports are
     *            generated. When false an aggregated class coverage report is
     *            generated.
     */
    private static void createReport(CoverageTestRunState coverageTestRunState, boolean classReport) {

        installBowerComponents();

        // Get the appropriate coverage
        AggregatedCoverage coverage;
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
                final String testClass = coverageTestRunState.getTestClassName();
                final String testName = coverageTestRunState.getCurrentTestMethodName();
                final Set<CoveredFlowNode> coveredFlowNodes = coverage.getCoveredFlowNodes(processDefinition.getKey());
                final Set<String> coveredSequenceFlowIds = coverage.getCoveredSequenceFlowIds(
                        processDefinition.getKey());
                final String reportName = classReport ? getReportName(processDefinition, null)
                        : getReportName(processDefinition, testName);

                final String reportDirectory = getReportDirectoryPath(testClass);
                final String bpmnXml = getBpmnXml(processDefinition);

                // Generate report

                BpmnJsReport.generateReportWithHighlightedFlowNodesAndSequenceFlows(bpmnXml,
                        coveredFlowNodes,
                        coveredSequenceFlowIds,
                        reportName,
                        processDefinition.getKey(),
                        coverage.getCoveragePercentage(),
                        testClass,
                        testName,
                        classReport,
                        reportDirectory);

            } catch (IOException ex) {

                logger.log(Level.SEVERE, "Unable to load process definition!", ex);
                throw new RuntimeException();

            }
        }

    }

    private static void installBowerComponents() {

        final File bowerComponents = new File(BOWER_DIR_PATH);
        if (bowerComponents.exists()) {
            // No need to install
            return;
        }

        try {

            final File resourcesRoot = ClassLocationURL.fileFor(CoverageReportUtil.class);

            // Tests executed by maven use JAR resources
            if (resourcesRoot.isFile()) {

                final JarFile coverageJar = new JarFile(resourcesRoot);
                final Enumeration<JarEntry> entries = coverageJar.entries();

                while (entries.hasMoreElements()) {

                    final String resourcePath = entries.nextElement().getName();
                    if (resourcePath.startsWith(BOWER_DIR_NAME)) {

                        final File resource = new File(TARGET_DIR_ROOT + resourcePath);

                        final InputStream source = CoverageReportUtil.class.getResourceAsStream("/" + resourcePath);
                        if (resourcePath.endsWith("/")) {

                            resource.mkdirs();

                        } else {

                            resource.getParentFile().mkdirs();
                            FileUtils.copyInputStreamToFile(source, resource);
                        }

                    }
                }
                coverageJar.close();
            }

            // Tests executed in the IDE use directories
            else {

                final File bowerSrc = new File(CoverageReportUtil.class.getResource("/" + BOWER_DIR_NAME).toURI());
                FileUtils.copyDirectory(bowerSrc, bowerComponents);

            }

        } catch (Exception e) {

            logger.log(Level.SEVERE, "Unable to copy bower_components!", e);
        }
    }

    /**
     * Retrieves directory path for all coverage reports of a test class.
     * 
     * @param className
     * @return
     */
    private static String getReportDirectoryPath(final String className) {
        return TARGET_DIR_ROOT + '/' + className;
    }

    /**
     * Retrieves the coverage report file name for the given process definition.
     * The report name prefix is set for individual test method runs and left
     * blank for aggregated (class) process coverages.
     * 
     * @param processDefinition
     * @param reportNamePrefix
     * @return
     */
    private static String getReportName(ProcessDefinition processDefinition, final String reportNamePrefix) {

        final StringBuilder builder = new StringBuilder();

        if (StringUtils.isNotBlank(reportNamePrefix)) {
            builder.append(reportNamePrefix);
            builder.append('_');
        }

        builder.append(processDefinition.getKey());
        builder.append(".html");

        return builder.toString();

    }

    /**
     * Retrieves a process definitions BPMN XML.
     * 
     * @param processDefinition
     * @return
     * @throws IOException
     *             Thrown if the BPMN resource is not found.
     */
    protected static String getBpmnXml(ProcessDefinition processDefinition) throws IOException {

        InputStream inputStream = CoverageReportUtil.class.getClassLoader().getResourceAsStream(
                processDefinition.getResourceName());
        if (inputStream == null) {
            inputStream = new FileInputStream(processDefinition.getResourceName());
        }

        return IOUtils.toString(inputStream);
    }

}