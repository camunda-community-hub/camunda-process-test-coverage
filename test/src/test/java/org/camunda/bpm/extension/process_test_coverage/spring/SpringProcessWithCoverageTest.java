package org.camunda.bpm.extension.process_test_coverage.spring;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { InMemProcessEngineConfiguration.class })
public class SpringProcessWithCoverageTest {

  @Autowired
  ProcessEngine processEngine;

  @Rule @ClassRule
  public static ProcessEngineRule rule;

  @PostConstruct
  void initRule() {
    rule = TestCoverageProcessEngineRuleBuilder.create(processEngine).build();
  }

  @Test
  @Deployment(resources="process.bpmn")
  public void start_and_finish_process() {

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("path", "A");
    final ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("process-test-coverage", variables);

    Assert.assertNotNull(processInstance);
    Assert.assertTrue(processInstance.isEnded());

  }

}
