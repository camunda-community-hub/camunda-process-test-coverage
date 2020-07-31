package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.extension.process_test_coverage.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ov7a
 */
public class AggregatedCoverageTestRunState implements CoverageTestRunState {

    private CoverageTestRunState currentCoverageTestRunState = null;

    private List<ClassCoverage> classCoverages = new ArrayList<ClassCoverage>();

    public void switchToNewState(CoverageTestRunState newState){
        finishState();
        currentCoverageTestRunState = newState;
    }

    private void finishState(){
        if (currentCoverageTestRunState != null){
            classCoverages.add(currentCoverageTestRunState.getClassCoverage());
            currentCoverageTestRunState = null;
        }
    }

    public AggregatedCoverage getAggregatedCoverage(){
        finishState();
        return new AggregatedClassCoverage(classCoverages);
    }

    @Override
    public void addCoveredElement(CoveredElement coveredElement) {
        currentCoverageTestRunState.addCoveredElement(coveredElement);
    }

    @Override
    public void endCoveredElement(CoveredElement coveredElement) {
        currentCoverageTestRunState.endCoveredElement(coveredElement);
    }

    @Override
    public void initializeTestMethodCoverage(ProcessEngine processEngine, String deploymentId, List<ProcessDefinition> processDefinitions, String testName) {
        currentCoverageTestRunState.initializeTestMethodCoverage(processEngine, deploymentId, processDefinitions, testName);
    }

    @Override
    public MethodCoverage getTestMethodCoverage(String testName) {
        return currentCoverageTestRunState.getTestMethodCoverage(testName);
    }

    @Override
    public MethodCoverage getCurrentTestMethodCoverage() {
        return currentCoverageTestRunState.getCurrentTestMethodCoverage();
    }

    @Override
    public ClassCoverage getClassCoverage() {
        return currentCoverageTestRunState.getClassCoverage();
    }

    @Override
    public String getCurrentTestMethodName() {
        return currentCoverageTestRunState.getCurrentTestMethodName();
    }

    @Override
    public void setCurrentTestMethodName(String currentTestName) {
        currentCoverageTestRunState.setCurrentTestMethodName(currentTestName);
    }

    @Override
    public String getTestClassName() {
        return currentCoverageTestRunState.getTestClassName();
    }

    @Override
    public void setTestClassName(String className) {
        currentCoverageTestRunState.setTestClassName(className);
    }

    @Override
    public void setExcludedProcessDefinitionKeys(List<String> excludedProcessDefinitionKeys) {
        currentCoverageTestRunState.setExcludedProcessDefinitionKeys(excludedProcessDefinitionKeys);
    }
}
