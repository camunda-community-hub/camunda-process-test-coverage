package org.camunda.community.process_test_coverage.junit5.platform7;

import org.assertj.core.api.Condition;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@Deployment(resources = {"intermediate-link-throw-event.bpmn"})
public class IntermediateLinkThrowEventTest {

    @RegisterExtension
    public static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder().build();

    @Test
    public void should_have_100_percent_coverage_with_intermediate_link_throw_event() {
        extension.addTestMethodCoverageCondition("should_have_100_percent_coverage_with_intermediate_link_throw_event()",
                new Condition<>(percentage -> percentage == 1, "matches if the coverage ratio is 100%"));
        extension.getProcessEngine().getRuntimeService().startProcessInstanceByKey("Testprocess");
    }

}
