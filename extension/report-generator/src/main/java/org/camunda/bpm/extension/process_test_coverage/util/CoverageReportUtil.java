package org.camunda.bpm.extension.process_test_coverage.util;

import org.camunda.bpm.extension.process_test_coverage.export.CoverageStateJsonExporter;
import org.camunda.bpm.extension.process_test_coverage.model.DefaultCollector;
import org.camunda.bpm.extension.process_test_coverage.model.Suite;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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
    public static final String REPORT_RESOURCES = "static";
    private static final String REPORT_TEMPLATE = "bpmn.report-template.html";


    public static void createReport(final DefaultCollector coverageCollector) {
        final String result = CoverageStateJsonExporter.createCoverageStateResult(
            coverageCollector.getSuites().values(),
            coverageCollector.getModels());
        final Suite suite = coverageCollector.getActiveSuite();
        final String reportDirectory = getReportDirectoryPath(suite.getName());
        installReportDependencies(reportDirectory);

        try {
            final String report = generateHtml(result);
            Files.createDirectories(Path.of(reportDirectory));
            writeToFile(reportDirectory + "/report.html", report);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Unable to create HTML report.", e);
        }
    }

    public static void createJsonReport(final DefaultCollector coverageCollector) {
        final String result = CoverageStateJsonExporter.createCoverageStateResult(
            coverageCollector.getSuites().values(),
            coverageCollector.getModels());
        final Suite suite = coverageCollector.getActiveSuite();
        final String reportDirectory = getReportDirectoryPath(suite.getName());

        try {
            Files.createDirectories(Path.of(reportDirectory));
            writeToFile(reportDirectory + "/report.json", result);
        } catch (final IOException ex) {
            throw new RuntimeException("Unable to create JSON report.", ex);
        }
    }

    protected static String generateHtml(String result) throws IOException, URISyntaxException {
        String html = new BufferedReader(
            new InputStreamReader(CoverageReportUtil.class.getClassLoader().getResourceAsStream(REPORT_TEMPLATE), StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
        return html.replace("{{__REPORT_JSON_PLACEHOLDER__}}", result);
    }

    private static void writeToFile(final String filePath, final String json) throws IOException {
        Files.writeString(Path.of(filePath), json);
    }

    private static void installReportDependencies(final String reportDirectory) {
        final File parent = new File(reportDirectory).getParentFile();
        final File bowerComponents = new File(parent, REPORT_RESOURCES);
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
                    if (resourcePath.startsWith(REPORT_RESOURCES)) {

                        final File resource = new File(parent, resourcePath);

                        final InputStream source = CoverageReportUtil.class.getResourceAsStream("/" + resourcePath);
                        if (resourcePath.endsWith("/")) {
                            resource.mkdirs();
                        } else {
                            resource.getParentFile().mkdirs();
                            Files.copy(source, resource.toPath());
                        }
                    }
                }
                coverageJar.close();
            } else {
                // Tests executed in the IDE use directories
                final File bowerSrc = new File(CoverageReportUtil.class.getResource("/" + REPORT_RESOURCES).toURI());
                Files.copy(bowerSrc.toPath(), bowerComponents.toPath());
            }

        } catch (final Exception e) {
            throw new RuntimeException("Unable to copy bower_components", e);
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

}
