package org.camunda.bpm.extension.process_test_coverage;

import static org.camunda.bpm.extension.process_test_coverage.ProcessTestCoverageProcessConstants.ALL_ELEMENTS;
import static org.camunda.bpm.extension.process_test_coverage.ProcessTestCoverageProcessConstants.BPMN_PATH;
import static org.camunda.bpm.extension.process_test_coverage.ProcessTestCoverageProcessConstants.PATH_B_ELEMENTS;
import static org.camunda.bpm.extension.process_test_coverage.ProcessTestCoverageProcessConstants.PROCESS_DEFINITION_KEY;
import static org.hamcrest.Matchers.lessThan;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case starting an in-memory database-backed Process Engine.<br>
 * In your tests don't set the property using <code>System.setProperty</code>
 * - use your runtime environment to do that.  
 */
public class ProcessTestClassRulePropertyTest {
    
    static final double EXPECTED = PATH_B_ELEMENTS.length;
    static final double ALL = ALL_ELEMENTS.length;
    static final double EXPECTED_COVERAGE = EXPECTED / ALL;   
    
    static {
        setSysProperty();
    }
    
    public static void setSysProperty() {
        System.setProperty(TestCoverageProcessEngineRuleBuilder.DEFAULT_ASSERT_AT_LEAST_PROPERTY,
                "" + EXPECTED_COVERAGE);
    }

    @AfterClass
    public static void delSysProperty() {
        System.clearProperty(TestCoverageProcessEngineRuleBuilder.DEFAULT_ASSERT_AT_LEAST_PROPERTY);
    }
    
    @ClassRule
    @Rule
    public static TestCoverageProcessEngineRule classRule = 
                    TestCoverageProcessEngineRuleBuilder.createClassRule()
                    .optionalAssertCoverageAtLeastProperty(TestCoverageProcessEngineRuleBuilder.DEFAULT_ASSERT_AT_LEAST_PROPERTY)
                    .build();

    @Test
    @Deployment(resources = BPMN_PATH)
    public void testPathB() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "B");
        classRule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
        
        classRule.assertTestCoverage("testPathB", lessThan(EXPECTED_COVERAGE + 0.0001));
    }


}
