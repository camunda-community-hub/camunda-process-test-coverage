package org.camunda.community.process_test_coverage.junit5.platform7

import org.camunda.bpm.engine.TaskService
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.variable.Variables
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@DisplayName("Testing display name")
class DisplayNameTest {

    companion object {
        @JvmField
        @RegisterExtension
        var extension: ProcessEngineCoverageExtension = ProcessEngineCoverageExtension.builder().assertClassCoverageAtLeast(1.0).build()
    }


    fun completeTask() {
        val taskService: TaskService = extension.processEngine.taskService
        val task = taskService.createTaskQuery().singleResult()
        taskService.complete(task.id)
    }

    @Test
    @Deployment(resources = ["coverage-ratio-test.bpmn"])
    @DisplayName("Coverage ratio for OK path")
    fun testCoverageRatio_Ok() {
        extension.processEngine.runtimeService.startProcessInstanceByKey("testCoverageRatio", Variables.createVariables().putValue("ok", true))
        completeTask()
    }

    @Test
    @Deployment(resources = ["coverage-ratio-test.bpmn"])
    fun testCoverageRatio_NotOk() {
        extension.processEngine.runtimeService.startProcessInstanceByKey("testCoverageRatio", Variables.createVariables().putValue("ok", false))
        completeTask()
    }

}