package org.camunda.bpm.extension.process_test_coverage.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredFlowNode;

/**
 * Util generating graphical process test coverage reports.
 * 
 */
public class BpmnJsReport {

    /**
     * Template used for all coverage reports.
     */
    private static final String REPORT_TEMPLATE = "bpmn.js-report-template.html";

    /**
     * Placeholder to be replaced with the process key.
     */
    protected static final String PLACEHOLDER_PROCESS_KEY = "//PROCESSKEY";

    /**
     * Placeholder to be replaced with the process coverage percentage.
     */
    protected static final String PLACEHOLDER_COVERAGE = "//COVERAGE";

    /**
     * Placeholder to be replaced with the test class full qualified name.
     */
    protected static final String PLACEHOLDER_TESTCLASS = "//TESTCLASS";

    /**
     * Placeholder to be replaced with the test method name.
     */
    protected static final String PLACEHOLDER_TESTMETHOD = "//TESTMETHOD";

    /**
   * Placeholder to be replaced with the addMarker annotation for all flow nodes. (canvas.addMarker())
     */
    protected static final String PLACEHOLDER_ANNOTATIONS = "          //YOUR ANNOTATIONS GO HERE";

    /**
     * Placeholder to be replaces with the BPMN content.
     */
    protected static final String PLACEHOLDER_BPMN_XML = "YOUR BPMN XML CONTENT";

    /**
     * JQuery command used for sequence flow SVG arrows coverage coloring.
     */
    protected static final String JQUERY_SEQUENCEFLOW_MARKING_COMMAND = "\\$(\"g[data-element-id=''{0}'']\").find(''path'').attr(''stroke'', ''#00ff00'');\n";

    protected static final String JQUERY_RUNNING_FLOWNODE_MARKING_COMMAND = "\\$(\"g[data-element-id=''{0}'']\").addClass(''highlight-running'');\n";

    /**
   * Generates a html coverage report for a process definition from the passed parameters.
     * 
   * @param bpmnXml The BPMN XML of the report process definition. 
   * @param flowNodes Flow nodes to be highlighted.
   * @param sequenceFlowIds Sequence flows to be highlighted.
   * @param reportName The file name of the report.
   * @param processDefinitionKey The key of the report process definition.
   * @param coverage The coverage percentage.
   * @param testClass The name of the test class.
   * @param testMethod The name of the test method if applicable.
   * @param classReport When true a class report will be generated, otherwise a method report.
   * The difference is mainly in the info-box method name field not appearing.
   * @param targetDir The directory where the report will be stored.
     * 
   * @throws IOException Thrown if an error occurs on report template read or report write.
     */
    public static void generateReportWithHighlightedFlowNodesAndSequenceFlows(String bpmnXml,
            Collection<CoveredFlowNode> flowNodes, Collection<String> sequenceFlowIds, String reportName,
            String processDefinitionKey, double coverage, String testClass, String testMethod, boolean classReport,
            String targetDir) throws IOException {

        final String flowNodeMarkers = generateJavaScriptFlowNodeAnnotations(flowNodes);
        final String sequenceFlowMarkers = generateJavaScriptSequenceFlowAnnotations(sequenceFlowIds);
        final String markers = flowNodeMarkers + sequenceFlowMarkers;

        final String html = generateHtml(markers,
                bpmnXml,
                processDefinitionKey,
                coverage,
                testClass,
                testMethod,
                classReport);
        writeToFile(targetDir, reportName, html);

    }

    /**
     * Generates the report html.
     * 
   * @param javaScript The covered element markers.
   * @param bpmnXml The BPMN XML of the report process definition. 
   * @param processDefinitionKey The key of the report process definition.
   * @param coverage The coverage percentage.
   * @param testClass The name of the test class.
   * @param testMethod The name of the test method if applicable.
   * @param classReport When true a class report will be generated, otherwise a method report.
   * The difference is mainly in the info-box method name field not appearing.
     * @return
   * @throws IOException Thrown if an error occurs on report template read.
     */
  protected static String generateHtml(String javaScript, String bpmnXml,
          String processDefinitionKey, double coverage, String testClass, 
          String testMethod, boolean classReport) throws IOException {

        String html = IOUtils.toString(CoverageReportUtil.class.getClassLoader().getResourceAsStream(REPORT_TEMPLATE));
		return injectIntoHtmlTemplate(javaScript, bpmnXml, html, processDefinitionKey, coverage,
		        testClass, testMethod, classReport);
    }

    /**
   * Inject the BPMN XML, javascript markers ad info-box fields into the html template.
     * 
   * @param javaScript The covered element markers.
   * @param bpmnXml The BPMN XML of the report process definition. 
   * @param html The report template html.
   * @param processDefinitionKey The key of the report process definition.
   * @param coverage The coverage percentage.
   * @param testClass The name of the test class.
   * @param testMethod The name of the test method if applicable.
   * @param classReport When true a class report will be generated, otherwise a method report.
     * 
     * @return Complete html report.
     */
  protected static String injectIntoHtmlTemplate(
          String javaScript, String bpmnXml,
          String html, String processDefinitionKey,
          double coverage, String testClass,
          String testMethod, boolean classReport) {

        html = html.replace(PLACEHOLDER_BPMN_XML, StringEscapeUtils.escapeEcmaScript(bpmnXml));
        html = html.replaceAll(PLACEHOLDER_ANNOTATIONS, javaScript + PLACEHOLDER_ANNOTATIONS);
        html = html.replaceAll(PLACEHOLDER_PROCESS_KEY, processDefinitionKey);
        html = html.replaceAll(PLACEHOLDER_COVERAGE, getCoveragePercent(coverage));
        html = html.replaceAll(PLACEHOLDER_TESTCLASS, testClass);

        // Class reports don't have the method field in the info-box
        if (classReport) {
            html = html.replaceAll(PLACEHOLDER_TESTMETHOD, "");
        } else {
            html = html.replaceAll(PLACEHOLDER_TESTMETHOD, "<div>TestMethod: " + testMethod + "</div>");
        }

        return html;
    }

    /**
     * Retrieve a formated percentage.
     * 
     * @param coverage
     * @return
     */
    private static String getCoveragePercent(double coverage) {

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(1);

        return percentFormat.format(coverage);
    }

    /**
     * Generate the add marker javascript for the passed flow node IDs.
     * 
     * @param flowNodeIds
     * @return
     */
    protected static String generateJavaScriptFlowNodeAnnotations(Collection<CoveredFlowNode> flowNodeIds) {

        final StringBuilder javaScript = new StringBuilder();
        for (CoveredFlowNode flowNode : flowNodeIds) {

            javaScript.append("\t\t\t");

            if (flowNode.hasEnded()) {

                javaScript.append("canvas.addMarker('" + flowNode.getElementId() + "', 'highlight');\n");

            } else {

                javaScript.append(
                        MessageFormat.format(JQUERY_RUNNING_FLOWNODE_MARKING_COMMAND, flowNode.getElementId()));
            }
        }
        return javaScript.toString();
    }

    /**
     * Generate jquery markers for the passed sequence flow IDs.
     * 
     * @param sequenceFlowIds
     * @return
     */
    protected static String generateJavaScriptSequenceFlowAnnotations(Collection<String> sequenceFlowIds) {

        StringBuilder javaScript = new StringBuilder();
        for (String sequenceFlowId : sequenceFlowIds) {

            javaScript.append("\t\t\t");
            javaScript.append(MessageFormat.format(JQUERY_SEQUENCEFLOW_MARKING_COMMAND, sequenceFlowId));
        }

        return javaScript.toString();
    }

    /**
     * Write the html report.
     * 
     * @param targetDir
     * @param fileName
     * @param html
     * @throws IOException
     */
    protected static void writeToFile(String targetDir, String fileName, String html) throws IOException {
        FileUtils.writeStringToFile(new File(targetDir + "/" + fileName), html);
    }

}
