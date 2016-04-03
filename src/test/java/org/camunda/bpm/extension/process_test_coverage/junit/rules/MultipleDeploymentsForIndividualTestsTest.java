package org.camunda.bpm.extension.process_test_coverage.junit.rules;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * 
 * @author dev
 *
 */
public class MultipleDeploymentsForIndividualTestsTest {

    private static final String PROCESS_DEFINITION_KEY = "super-process-test-coverage";
    
    @Rule
    @ClassRule
    public static TestCoverageProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.createClassRule()
            .build();

    @Test
    @Deployment(resources = { "superProcess.bpmn", "process.bpmn" })
    public void testPathAAndSuperPathA() {
        
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "A");
        variables.put("superPath", "A");
        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
        
        rule.addTestMethodCoverageAssertionMatcher("testPathAAndSuperPathA", greaterThan(6.9 / 11.0));
        rule.addTestMethodCoverageAssertionMatcher("testPathAAndSuperPathA", lessThan(9 / 11.0));

    }

    @Test
    @Deployment(resources = { "superProcess.bpmn", "process.bpmn" })
    public void testPathBAndSuperPathB() {
        
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "B");
        variables.put("superPath", "B");
        
        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
        
        rule.addTestMethodCoverageAssertionMatcher("testPathBAndSuperPathB", greaterThan(6.9 / 11.0));
        rule.addTestMethodCoverageAssertionMatcher("testPathBAndSuperPathB", lessThan(9 / 11.0));
    }
}
