package org.camunda.community.process_test_coverage.examples.junit5.platform7;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@Deployment(resources = "collapsed-subprocess.bpmn")
public class CollapsedSubProcessTest {

    @RegisterExtension
    public static ProcessEngineCoverageExtension extension = ProcessEngineExtensionProvider.extension;

    @Test
    public void shouldExecuteHappyPath() {
        final ProcessInstance instance = this.startProcess();

        assertThat(instance).isWaitingAt("SubTask");

        complete(task());

        assertThat(instance).isWaitingAt("TheTask");

        complete(task());

        assertThat(instance).isEnded();
    }


    private ProcessInstance startProcess() {
        return runtimeService().startProcessInstanceByKey("CollapsedSubProcessTest");
    }


}
