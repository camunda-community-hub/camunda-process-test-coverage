package org.camunda.bpm.extension.process_test_coverage.examples;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

// @ExtendWith(ProcessEngineCoverageExtension.class)
@Deployment(resources = {"test.bpmn"})
class CoverageTest {

    /**
     * Using static ProcessEngineCoverageExtension does not write any html file
     * <p>
     * To identify problem remove static from variable extension and add commented out @ExtendWith(ProcessEngineCoverageExtension.class)
     */
    @RegisterExtension
    static ProcessEngineCoverageExtension extension = ProcessEngineCoverageExtension.builder()
            .assertClassCoverageAtLeast(1.0d)
            .build();

    @Test
    void coverage_100_fails_because_of_Link_Intermediate_Throw_Event() {
        ProcessInstance processInstance = extension.getProcessEngine().getRuntimeService().startProcessInstanceByKey("Testprocess");
        assertThat(processInstance).isEnded();
    }

}
