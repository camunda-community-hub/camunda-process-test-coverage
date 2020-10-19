package org.camunda.bpm.extension.process_test_coverage.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.CoverageTestRunState;
import org.camunda.bpm.extension.process_test_coverage.model.AggregatedCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredFlowNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static String TARGET_DIR_ROOT = System.getProperty("camunda-bpm-process-test-coverage.target-dir-root", "target/process-test-coverage/");
    public static final String BOWER_DIR_NAME = "bower_components";

    /**
     * Generates a coverage report for the whole test class. This method
     * requires that all tests have been executed with the same resources
     * deployed.
     * 
     * @param coverageTestRunState coverage run state to get the coverage data from.
     */
    public static void createClassReport(CoverageTestRunState coverageTestRunState) {

        AggregatedCoverage coverage = coverageTestRunState.getClassCoverage();
        final String reportDirectory = getReportDirectoryPath(coverageTestRunState.getTestClassName());

        createReport(coverage, reportDirectory, coverageTestRunState.getTestClassName(), null);

    }

    /**
     * Generates graphical test coverage reports for the current test method
     * run.
     *
     * @param coverageTestRunState coverage run state to get the coverage data from.
     */
    public static void createCurrentTestMethodReport(CoverageTestRunState coverageTestRunState) {

        AggregatedCoverage coverage = coverageTestRunState.getCurrentTestMethodCoverage();
        final String reportDirectory = getReportDirectoryPath(coverageTestRunState.getTestClassName());

        createReport(coverage, reportDirectory, coverageTestRunState.getTestClassName(), coverageTestRunState.getCurrentTestMethodName());

    }

    /**
     * Generates a coverage report.
     *
     * @param coverage coverage run state to get the coverage data from
     * @param reportDirectory The directory where the report will be stored.
     *
     */
    public static void createReport(AggregatedCoverage coverage, String reportDirectory) {
        createReport(coverage, reportDirectory, null, null);
    }

    /**
     * Generates a coverage report.
     * 
     * @param coverage coverage run state to get the coverage data from
     * @param reportDirectory The directory where the report will be stored.
     * @param testClass Optional test class name for info box
     * @param testName Optional test method name for info box. Also used as reportName prefix
     *
     */
    private static void createReport(AggregatedCoverage coverage, String reportDirectory, String testClass, String testName) {

        installBowerComponents(reportDirectory);

        // Generate a report for every process definition
        final Set<ProcessDefinition> processDefinitions = coverage.getProcessDefinitions();
        for (ProcessDefinition processDefinition : processDefinitions) {

            try {

                // Assemble data
                final Set<CoveredFlowNode> coveredFlowNodes = coverage.getCoveredFlowNodes(processDefinition.getKey());
                final Set<String> coveredSequenceFlowIds = coverage.getCoveredSequenceFlowIds(
                        processDefinition.getKey());
                final String reportName = getReportName(processDefinition, testName);

                final String bpmnXml = getBpmnXml(processDefinition);

                // Generate report

                BpmnJsReport.generateReportWithHighlightedFlowNodesAndSequenceFlows(bpmnXml,
                        coveredFlowNodes,
                        coveredSequenceFlowIds,
                        reportDirectory + '/' + reportName,
                        processDefinition.getKey(),
                        coverage.getCoveragePercentage(processDefinition.getKey()),
                        testClass,
                        testName);

            } catch (IOException ex) {

                logger.log(Level.SEVERE, "Unable to load process definition!", ex);
                throw new RuntimeException();

            }
        }

    }

    private static void installBowerComponents(String reportDirectory) {

        final File parent = new File(reportDirectory).getParentFile();
        final File bowerComponents = new File(parent, BOWER_DIR_NAME);
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

                        final File resource = new File(parent, resourcePath);

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
     * @param className class name of the class to generate report for.
     * @return path for the report.
     */
    private static String getReportDirectoryPath(final String className) {
        return TARGET_DIR_ROOT + className;
    }

    /**
     * Retrieves the coverage report file name for the given process definition.
     * The report name prefix is set for individual test method runs and left
     * blank for aggregated (class) process coverages.
     * 
     * @param processDefinition process definition to generate report for.
     * @param reportNamePrefix prefix of the report filename.
     * @return full path to report file.
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
     * @param processDefinition process definition to generate report for.
     * @return XML pf process definition as string.
     * @throws IOException
     *             Thrown if the BPMN resource is not found.
     */
    protected static String getBpmnXml(ProcessDefinition processDefinition) throws IOException {

        InputStream inputStream = CoverageReportUtil.class.getClassLoader().getResourceAsStream(
                processDefinition.getResourceName());
        if (inputStream == null) {
            inputStream = new FileInputStream(processDefinition.getResourceName());
        }

        return IOUtils.toString(inputStream, Charset.defaultCharset());
    }

    /**
     * Changes report directory.
     * @param reportDirectory report directory.
     */
    public static void setReportDirectory(String reportDirectory) {
        TARGET_DIR_ROOT = reportDirectory;
    }
}
