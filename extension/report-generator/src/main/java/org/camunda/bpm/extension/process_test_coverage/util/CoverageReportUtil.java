package org.camunda.bpm.extension.process_test_coverage.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.export.CoverageStateJsonExporter;
import org.camunda.bpm.extension.process_test_coverage.model.Coverage;
import org.camunda.bpm.extension.process_test_coverage.model.DefaultCollector;
import org.camunda.bpm.extension.process_test_coverage.model.Event;
import org.camunda.bpm.extension.process_test_coverage.model.EventSource;
import org.camunda.bpm.extension.process_test_coverage.model.EventType;
import org.camunda.bpm.extension.process_test_coverage.model.Model;
import org.camunda.bpm.extension.process_test_coverage.model.Run;
import org.camunda.bpm.extension.process_test_coverage.model.Suite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Utility for generating graphical class and method coverage reports.
 *
 * @author z0rbas
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
     * @param coverageCollector coverage run state to get the coverage data from.
     */
    public static void createClassReport(final DefaultCollector coverageCollector) {
        final Suite suite = coverageCollector.getActiveSuite();
        final String reportDirectory = getReportDirectoryPath(suite.getName());

        createReport(suite, coverageCollector, reportDirectory, suite.getName(), null);
    }

    public static void createJsonReport(final DefaultCollector coverageCollector) {
        final String result = CoverageStateJsonExporter.createCoverageStateResult(
            coverageCollector.getSuites().values(),
            coverageCollector.getModels());
        final Suite suite = coverageCollector.getActiveSuite();
        final String reportDirectory = getReportDirectoryPath(suite.getName());

        try {
            writeToFile(reportDirectory + "/report.json", result);
        } catch (final IOException ex) {
            logger.log(Level.SEVERE, "Unable to save JSON Report!", ex);
            throw new RuntimeException();
        }
    }

    private static void writeToFile(final String filePath, final String json) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), json);
    }

    /**
     * Generates graphical test coverage reports for the current test method
     * run.
     *
     * @param collector
     * @param runId
     */
    public static void createCurrentTestMethodReport(final DefaultCollector collector, final String runId) {
        final Suite suite = collector.getActiveSuite();
        final Run coverage = collector.getActiveSuite().getRun(runId);
        final String reportDirectory = getReportDirectoryPath(suite.getName());

        createReport(coverage, collector, reportDirectory, suite.getName(), collector.getActiveRun().getName());

    }

    /**
     * Generates a coverage report.
     *
     * @param coverage        coverage run state to get the coverage data from
     * @param reportDirectory The directory where the report will be stored.
     */
    public static void createReport(final Coverage coverage, final DefaultCollector collector, final String reportDirectory) {
        createReport(coverage, collector, reportDirectory, null, null);
    }

    /**
     * Generates a coverage report.
     *
     * @param coverage        coverage run state to get the coverage data from
     * @param coverageState   coverage run state to get the coverage data from
     * @param reportDirectory The directory where the report will be stored.
     * @param testClass       Optional test class name for info box
     * @param testName        Optional test method name for info box. Also used as reportName prefix
     */
    private static void createReport(final Coverage coverage, final DefaultCollector coverageState, final String reportDirectory, final String testClass, final String testName) {

        installBowerComponents(reportDirectory);

        // Generate a report for every process definition
        final Collection<Model> models = coverageState.getModels();
        for (final Model model : models) {

            try {

                // Assemble data
                final Collection<Event> events = coverage.getEvents(model.getKey());
                final Collection<Event> distinctEvents = coverage.getEventsDistinct(model.getKey());

                final Collection<Event> coveredFlowNodes = distinctEvents.stream()
                                                                         .filter(event -> EventSource.FLOW_NODE.equals(event.getSource()))
                                                                         .collect(Collectors.toList());

                final Collection<String> coveredSequenceFlowIds = distinctEvents.stream()
                                                                                .filter(event -> EventSource.SEQUENCE_FLOW.equals(event.getSource()))
                                                                                .map(Event::getDefinitionKey)
                                                                                .collect(Collectors.toList());

                final String reportName = getReportName(model, testName);
                final String bpmnXml = model.getXml();

                // Generate report
                BpmnJsReport.generateReportWithHighlightedFlowNodesAndSequenceFlows(
                    bpmnXml,
                    events,
                    coveredFlowNodes,
                    coveredSequenceFlowIds,
                    reportDirectory + '/' + reportName,
                    model.getKey(),
                    coverage.calculateCoverage(model),
                    testClass,
                    testName);

            } catch (final IOException ex) {
                logger.log(Level.SEVERE, "Unable to load process definition!", ex);
                throw new RuntimeException();
            }
        }
    }

    private static void installBowerComponents(final String reportDirectory) {
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

        } catch (final Exception e) {

            logger.log(Level.SEVERE, "Unable to copy bower_components!", e);
        }
    }

    /**
     * Retrieves directory path for all coverage reports of a test class.
     *
     * @param className class name of the class to generate report for.
     *
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
     * @param model            process definition to generate report for.
     * @param reportNamePrefix prefix of the report filename.
     *
     * @return full path to report file.
     */
    private static String getReportName(final Model model, final String reportNamePrefix) {

        final StringBuilder builder = new StringBuilder();

        if (StringUtils.isNotBlank(reportNamePrefix)) {
            builder.append(reportNamePrefix);
            builder.append('_');
        }

        builder.append(model.getKey());
        builder.append(".html");

        return builder.toString();

    }

    /**
     * Retrieves a process definitions BPMN XML.
     *
     * @param processDefinition process definition to generate report for.
     *
     * @return XML pf process definition as string.
     *
     * @throws IOException Thrown if the BPMN resource is not found.
     */
    protected static String getBpmnXml(final ProcessDefinition processDefinition) throws IOException {

        InputStream inputStream = CoverageReportUtil.class.getClassLoader().getResourceAsStream(
            processDefinition.getResourceName());
        if (inputStream == null) {
            inputStream = new FileInputStream(processDefinition.getResourceName());
        }

        return IOUtils.toString(inputStream, Charset.defaultCharset());
    }

    /**
     * This method checks if an event has ended.
     *
     * @param event  Event that should be ended
     * @param events List of all events
     *
     * @return true if event has ended
     */
    public static boolean eventHasEnded(final Event event, final Collection<Event> events) {
        return events.stream()
                     .anyMatch(evt -> evt.getDefinitionKey().equals(event.getDefinitionKey()) && EventType.END.equals(evt.getType()));
    }

    /**
     * Changes report directory.
     *
     * @param reportDirectory report directory.
     */
    public static void setReportDirectory(final String reportDirectory) {
        TARGET_DIR_ROOT = reportDirectory;
    }
}
