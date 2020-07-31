package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.model.ClassCoverage;
import org.camunda.bpm.extension.process_test_coverage.model.CoveredElement;
import org.camunda.bpm.extension.process_test_coverage.model.MethodCoverage;

import java.util.List;

/**
 * State tracking the current class and method coverage run.
  */
public interface CoverageTestRunState {

    /**
     * Adds the covered element to the current test run coverage.
     * 
     * @param coveredElement
     */
    void addCoveredElement(/* @NotNull */ CoveredElement coveredElement);

    /**
     * Mark a covered element execution as ended.
     * 
     * @param coveredElement
     */
    void endCoveredElement(CoveredElement coveredElement);

    /**
     * Adds a test method to the class coverage.
     * 
     * @param processEngine
     * @param deploymentId
     *            The deployment ID of the test method run. (Hint: Every test
     *            method run has its own deployment.)
     * @param processDefinitions
     *            The process definitions of the test method deployment.
     * @param testName
     *            The name of the test method.
     */
    void initializeTestMethodCoverage(ProcessEngine processEngine, String deploymentId,
            List<ProcessDefinition> processDefinitions, String testName);

    /**
     * Retrieves the coverage for a test method.
     * 
     * @param testName
     * @return
     */
    MethodCoverage getTestMethodCoverage(String testName);

    /**
     * Retrieves the currently executing test method coverage.
     * 
     * @return
     */
    MethodCoverage getCurrentTestMethodCoverage();

    /**
     * Retrieves the class coverage.
     * 
     * @return
     */
    ClassCoverage getClassCoverage();

    /**
     * Retrieves the name of the currently executing test method.
     * 
     * @return
     */
    String getCurrentTestMethodName();

    /**
     * Sets the name of the currently executing test mehod.
     * 
     * @param currentTestName
     */
    void setCurrentTestMethodName(String currentTestName);

    String getTestClassName();

    void setTestClassName(String className);

    void setExcludedProcessDefinitionKeys(List<String> excludedProcessDefinitionKeys);

}
