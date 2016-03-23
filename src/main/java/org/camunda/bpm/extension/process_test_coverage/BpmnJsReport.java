package org.camunda.bpm.extension.process_test_coverage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class BpmnJsReport {

  private static final String REPORT_TEMPLATE = "bpmn.js-report-template.html";
  
  protected static final String PLACEHOLDER_PROCESS_KEY = "//PROCESSKEY";
  protected static final String PLACEHOLDER_COVERAGE = "//COVERAGE";
  protected static final String PLACEHOLDER_TESTCLASS = "//TESTCLASS";
  protected static final String PLACEHOLDER_TESTMETHOD = "//TESTMETHOD";
  protected static final String PLACEHOLDER_ANNOTATIONS = "          //YOUR ANNOTATIONS GO HERE";
  protected static final String PLACEHOLDER_BPMN_XML = "YOUR BPMN XML CONTENT";
  
  protected static final String SEQUENCEFLOW_ANNOTATION_PREFIX = "\\$( \"g[data-element-id*='";
  protected static final String SEQUENCEFLOW_ANNOTATION_POSTFIX  = "']\" ).find('path').attr('stroke', '#00ff00');\n";

  public static void highlightFlowNodesAndSequenceFlows(String bpmnXml, Collection<String> activityIds, 
          Collection<String> sequenceFlowIds, String reportName, String processDefinitionKey, 
          double coverage, String testClass, String testMethod, String targetDir) throws IOException {
      
    String flowNodeAnnotations = generateJavaScriptFlowNodeAnnotations(activityIds);
    final String sequenceFlowAnnotations = generateJavaScriptSequenceFlowAnnotations(sequenceFlowIds);
    String html = generateHtml((flowNodeAnnotations + sequenceFlowAnnotations), 
            bpmnXml, processDefinitionKey, coverage, testClass, testMethod);
    writeToFile(targetDir, reportName, html);
    
  }

  protected static String generateHtml(String javaScript, String bpmnXml,
          String processDefinitionKey, double coverage, String testClass, String testMethod) throws IOException {
		String html = IOUtils.toString(Coverages.class.getClassLoader().getResourceAsStream(REPORT_TEMPLATE));
		return injectIntoHtmlTemplate(javaScript, bpmnXml, html, processDefinitionKey, coverage,
		        testClass, testMethod);
	}

  protected static String injectIntoHtmlTemplate(
          String javaScript, String bpmnXml,
          String html, String processDefinitionKey,
          double coverage, String testClass,
          String testMethod) {
		html = html.replace(PLACEHOLDER_BPMN_XML, StringEscapeUtils.escapeEcmaScript(bpmnXml));
		html = html.replaceAll(PLACEHOLDER_ANNOTATIONS, javaScript + PLACEHOLDER_ANNOTATIONS);
		html = html.replaceAll(PLACEHOLDER_PROCESS_KEY, processDefinitionKey);
		html = html.replaceAll(PLACEHOLDER_COVERAGE, getCoveragePercent(coverage));
        html = html.replaceAll(PLACEHOLDER_TESTCLASS, testClass);
        if (testMethod != null) {
            html = html.replaceAll(PLACEHOLDER_TESTMETHOD, "<div>TestMethod: " + testMethod + "</div>");            
        }

		return html;
	}
  
  private static String getCoveragePercent(double coverage) {
      
      NumberFormat percentFormat = NumberFormat.getPercentInstance();
      percentFormat.setMaximumFractionDigits(1);
      
      return percentFormat.format(coverage);
  }

  protected static String generateJavaScriptFlowNodeAnnotations(Collection<String> acivityIds) {
    StringBuilder javaScript = new StringBuilder();
    for (String activityId : acivityIds) {
      javaScript.append("\t\t\t");
      javaScript.append("canvas.addMarker('" + activityId + "', 'highlight');\n");
    }
    return javaScript.toString();
  }
  
  protected static String generateJavaScriptSequenceFlowAnnotations(Collection<String> sequenceFlowIds) {
      StringBuilder javaScript = new StringBuilder();
      for (String sequenceFlowId : sequenceFlowIds) {
        javaScript.append("\t\t\t");
        javaScript.append(SEQUENCEFLOW_ANNOTATION_PREFIX);
        javaScript.append(sequenceFlowId);
        javaScript.append(SEQUENCEFLOW_ANNOTATION_POSTFIX);
      }
      return javaScript.toString();
    }

  protected static void writeToFile(String targetDir, String fileName,	String html) throws IOException {
		prepareTargetDir(targetDir);
		FileUtils.writeStringToFile(new File(targetDir + "/" + fileName), html);
	}

  protected static void prepareTargetDir(String targetDir) throws IOException {
    File targetDirectory = new File(targetDir);
    FileUtils.forceMkdir(targetDirectory);
    extractBowerComponents(targetDirectory);
    }
  
  private static void extractBowerComponents(File targetDirectory) {
      final File parentDirectory = targetDirectory.getParentFile();
      
      if (!new File(parentDirectory.getPath() + "/bower_components").exists()) {
        extractBpmnJs(parentDirectory);
      } 
  }

  protected static void extractBpmnJs(File targetDirectory) {
    InputStream bpmnJsSeed = BpmnJsReport.class.getClassLoader().getResourceAsStream("bpmn-js-seed-master.zip");
    ZipInputStream zin = new ZipInputStream(bpmnJsSeed);
    try {
      ZipEntry entry = null;
      while ((entry = zin.getNextEntry()) != null) {
        String entryName = entry.getName().replace("bpmn-js-seed-master" + "/", "");
        if (entryName.startsWith("bower_components")) {
          File entryDestination = new File(targetDirectory,  entryName);
          if (entry.isDirectory())
              entryDestination.mkdirs();
          else {
              entryDestination.getParentFile().mkdirs();
              OutputStream out = new FileOutputStream(entryDestination);
              IOUtils.copy(zin, out);
              out.close();
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(zin);
    }
  }

}
