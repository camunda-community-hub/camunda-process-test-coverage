package org.camunda.bpm.extension.process_test_coverage.examples;

import io.holunda.camunda.bpm.extension.jgiven.JGivenProcessStage;
import io.holunda.camunda.bpm.extension.jgiven.ProcessStage;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

@JGivenProcessStage
public class OrderProcessStage extends ProcessStage<OrderProcessStage, OrderProcessBean> {

    public OrderProcessStage process_is_started() {
        processInstanceSupplier = new OrderProcessBean(camunda.getProcessEngine());
        processInstanceSupplier.start();
        assertThat(processInstanceSupplier.get()).isNotNull();
        assertThat(processInstanceSupplier.get()).isStarted();
        return this;
    }

}
