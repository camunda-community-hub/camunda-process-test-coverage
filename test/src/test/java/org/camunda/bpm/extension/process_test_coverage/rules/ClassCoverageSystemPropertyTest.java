package org.camunda.bpm.extension.process_test_coverage.rules;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Test case starting an in-memory database-backed Process Engine.<br>
 * In your tests don't set the property using <code>System.setProperty</code> -
 * use your runtime environment to do that.
 */
public class ClassCoverageSystemPropertyTest {

    static final double EXPECTED = CoverageTestProcessConstants.PATH_B_ELEMENTS.length;
    static final double ALL = CoverageTestProcessConstants.ALL_ELEMENTS.length;
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
            TestCoverageProcessEngineRuleBuilder.create()
            	.optionalAssertCoverageAtLeastProperty(TestCoverageProcessEngineRuleBuilder.DEFAULT_ASSERT_AT_LEAST_PROPERTY)
            	.build();

    @Test
    @Deployment(resources = CoverageTestProcessConstants.BPMN_PATH)
    public void testPathB() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("path", "B");
        classRule.getRuntimeService().startProcessInstanceByKey(CoverageTestProcessConstants.PROCESS_DEFINITION_KEY, variables);

        classRule.addTestMethodCoverageAssertionMatcher("testPathB", lessThan(EXPECTED_COVERAGE + 0.0001));
    }

}
