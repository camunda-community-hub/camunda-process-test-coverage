package org.camunda.community.process_test_coverage.examples.spring_test.platform7;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.community.process_test_coverage.spring_test.platform7.ProcessEngineCoverageConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@SpringBootTest
@Import({CoverageTestConfiguration.class, ProcessEngineCoverageConfiguration.class})
public class SecondOrderProcessTest {

    @Autowired
    private RuntimeService runtimeService;

    @Test
    public void shouldExecuteHappyPath() {
        final ProcessInstance instance = this.startProcess();

        assertThat(instance).isWaitingAt("Task_ProcessOrder");

        complete(task(), withVariables("orderOk", true));

        assertThat(instance).isWaitingAt("Task_DeliverOrder");

        complete(task());

        assertThat(instance)
                .hasPassed("Event_OrderProcessed")
                .isEnded();
    }

    private ProcessInstance startProcess() {
        return runtimeService.startProcessInstanceByKey("order-process");
    }


}
