package org.camunda.community.process_test_coverage.examples.junit5.platform7;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

@Deployment(resources = {"intermediate-link-throw-event.bpmn"})
class IntermediateLinkThrowEventTest {

    @RegisterExtension
    static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder()
            .assertClassCoverageAtLeast(1.0d)
            .build();

    @Test
    void should_have_100_percent_coverage_with_intermediate_link_throw_event() {
        ProcessInstance processInstance = extension.getProcessEngine().getRuntimeService().startProcessInstanceByKey("Testprocess");
        assertThat(processInstance).isEnded();
    }

}
