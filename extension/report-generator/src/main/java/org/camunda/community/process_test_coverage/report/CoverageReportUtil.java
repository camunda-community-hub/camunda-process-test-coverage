package org.camunda.community.process_test_coverage.report;

import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter;
import org.camunda.community.process_test_coverage.core.model.DefaultCollector;
import org.camunda.community.process_test_coverage.core.model.Suite;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Function;
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
    public static String TARGET_DIR_ROOT = System.getProperty("camunda-process-test-coverage.target-dir-root", "target/process-test-coverage/");
    public static final String REPORT_RESOURCES = "static";
    private static final String REPORT_TEMPLATE = "bpmn.report-template.html";


    public static void createReport(final DefaultCollector coverageCollector) {
        writeReport(createCoverageStateResult(coverageCollector), true,
                getReportDirectoryPath(coverageCollector.getActiveSuite().getName()),
                "report.html", CoverageReportUtil::generateHtml);
    }

    public static void createJsonReport(final DefaultCollector coverageCollector) {
        writeReport(createCoverageStateResult(coverageCollector), false,
                getReportDirectoryPath(coverageCollector.getActiveSuite().getName()),
                "report.json", result -> result);
    }

    private static String createCoverageStateResult(final DefaultCollector coverageCollector) {
        Suite suite = coverageCollector.getActiveSuite();
        return CoverageStateJsonExporter.createCoverageStateResult(
                Collections.singleton(suite),
                coverageCollector.getModels().stream().filter(
                        it -> suite.getEvents(it.getKey()).size() > 0).collect(Collectors.toSet()));
    }

    public static void writeReport(final String coverageResult, boolean installReportDependencies,
                                   final String reportDirectory, final String fileName, final Function<String, String> reportCreator) {

        if (installReportDependencies) {
            installReportDependencies(reportDirectory);
        }

        try {
            Files.createDirectories(FileSystems.getDefault().getPath(reportDirectory));
            writeToFile(reportDirectory + "/" + fileName, reportCreator.apply(coverageResult));
        } catch (final IOException ex) {
            throw new RuntimeException("Unable to write report.", ex);
        }
    }

    public static String generateHtml(String result) {
        InputStream template =  CoverageReportUtil.class.getClassLoader().getResourceAsStream(REPORT_TEMPLATE);
        Objects.requireNonNull(template);
        String html = new BufferedReader(
            new InputStreamReader(template, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
        return html.replace("{{__REPORT_JSON_PLACEHOLDER__}}", result);
    }

    private static void writeToFile(final String filePath, final String json) throws IOException {
        Files.write(FileSystems.getDefault().getPath(filePath), json.getBytes(StandardCharsets.UTF_8));
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
                        Objects.requireNonNull(source);
                        if (resourcePath.endsWith("/")) {
                            logger.info("Creating directory " + resource.getAbsolutePath());
                            if (!resource.exists() && !resource.mkdirs()) {
                                throw new IllegalStateException("Could not create report directory " + resource.getAbsolutePath());
                            }
                        } else {
                            if (!resource.getParentFile().exists() && !resource.getParentFile().mkdirs()) {
                                throw new IllegalStateException("Could not create report directory " + resource.getParentFile().getAbsolutePath());
                            }
                            Files.copy(source, resource.toPath());
                        }
                    }
                }
                coverageJar.close();
            } else {
                // Tests executed in the IDE use directories
                URL reportResources = CoverageReportUtil.class.getResource("/" + REPORT_RESOURCES);
                Objects.requireNonNull(reportResources);
                final File bowerSrc = new File(reportResources.toURI());
                if (!bowerComponents.getParentFile().exists() && !bowerComponents.getParentFile().mkdirs()) {
                    throw new IllegalStateException("Could not create report directory " + bowerComponents.getParentFile().getAbsolutePath());
                }
                copyFolder(bowerSrc.toPath(), bowerComponents.toPath());
            }

        } catch (final Exception e) {
            throw new RuntimeException("Unable to copy bower_components", e);
        }
    }

    private static void copyFolder(Path source, Path target)
            throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
        });
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
