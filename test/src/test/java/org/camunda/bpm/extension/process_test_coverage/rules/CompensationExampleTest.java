package org.camunda.bpm.extension.process_test_coverage.rules;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CompensationExampleTest {

    @ClassRule
    @Rule
    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    private static final String PROCESS_DEFINITION_KEY = "compensation-test";

    static {
        LogFactory.useSlf4jLogging(); // MyBatis
    }

    @Test
    @Deployment(resources = "CompensationExample.bpmn")
    public void testHappyPath() {

        ProcessInstance processInstance = rule.getProcessEngine().getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
        rule.getRuntimeService().setVariable(processInstance.getId(), "testVar", true);

    }

}
