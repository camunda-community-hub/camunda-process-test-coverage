package org.camunda.community.process_test_coverage.examples.jgiven.platform7;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.function.Supplier;

public class OrderProcessBean implements Supplier<ProcessInstance> {

    private final ProcessEngine processEngine;
    private ProcessInstance processInstance;

    public OrderProcessBean(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Override
    public ProcessInstance get() {
        return processInstance;
    }

    public void start() {
        processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("order-process");
    }

}
