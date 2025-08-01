/*-
 * #%L
 * Camunda Process Test Coverage Sonar Plugin
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.camunda.community.process_test_coverage.sonar

import org.camunda.bpm.model.bpmn.Bpmn
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.bpmn.instance.FlowNode
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent
import org.camunda.bpm.model.bpmn.instance.LinkEventDefinition
import org.camunda.bpm.model.bpmn.instance.Process
import org.camunda.bpm.model.bpmn.instance.SequenceFlow
import org.camunda.bpm.model.xml.instance.ModelElementInstance
import org.camunda.community.process_test_coverage.core.export.CoverageStateJsonExporter.createCoverageStateResult
import org.camunda.community.process_test_coverage.core.export.CoverageStateResult
import org.camunda.community.process_test_coverage.core.model.Model
import org.slf4j.LoggerFactory
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import java.util.stream.Collectors


class ReportImporter(private val ctx: SensorContext) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProcessTestCoverageSensor::class.java)
    }

    fun importCoverage(result: CoverageStateResult) {
        val resultsMap = result.models.filter { it.totalElementCount > 0 }.associateBy { it.key }
        val fs = ctx.fileSystem()
        fs.inputFiles(fs.predicates().and(
                    fs.predicates().hasLanguage(BpmnLanguage.KEY),
                    fs.predicates().hasType(InputFile.Type.MAIN))
                )
                .associateBy {
                    Bpmn.readModelFromStream(it.inputStream()).processDefinitionKey()
                }
                .forEach {
                    LOG.debug("Calculating coverage for process {} in file {}", it.key, it.value.filename())
                    val coverage = resultsMap[it.key]?.let { model -> result.calculateCoverage(model) } ?: 0.0
                    LOG.debug("Coverage for process {} is {}", it.key, coverage)
                    ctx.newMeasure<Double>()
                            .on(it.value)
                            .forMetric(ProcessTestCoverageMetrics.PROCESS_TEST_COVERAGE)
                            .withValue(coverage.asPercent())
                            .save()
                }
    }

    fun importProjectCoverage(result: CoverageStateResult) {
        val models = ctx.fileSystem().inputFiles(ctx.fileSystem().predicates().hasLanguage(BpmnLanguage.KEY))
                .map { readModel(it) }
        val totalElementCount = models.sumOf { it.totalElementCount }
        val coveredElementCount = models.sumOf { result.getEventsDistinct(modelKey = it.key).size }
        if (totalElementCount == 0) {
            LOG.warn("No BPMN models found, not able to calculate coverage")
        } else {
            val percentage = (coveredElementCount.toDouble() / totalElementCount.toDouble()).asPercent()
            LOG.debug("Importing project coverage for $coveredElementCount/$totalElementCount with percentage $percentage")
            ctx.newMeasure<Double>()
                .on(ctx.project())
                .forMetric(ProcessTestCoverageMetrics.PROCESS_TEST_COVERAGE)
                .withValue(percentage)
                .save()
            ctx.newMeasure<String>()
                .on(ctx.project())
                .forMetric(ProcessTestCoverageMetrics.PROCESS_TEST_COVERAGE_REPORT)
                .withValue(createCoverageStateResult(result.suites, result.models))
                .save()
        }
    }

    private fun readModel(file: InputFile): Model {
        val modelInstance = Bpmn.readModelFromStream(file.inputStream())
        val key = modelInstance.processDefinitionKey()
        val definitionFlowNodes = getExecutableFlowNodes(modelInstance.getModelElementsByType(FlowNode::class.java), key)
        val definitionSequenceFlows = getExecutableSequenceNodes(modelInstance.getModelElementsByType(SequenceFlow::class.java), definitionFlowNodes)

        return Model(
                key,
                definitionFlowNodes.size + definitionSequenceFlows.size,
                "unknown",
                Bpmn.convertToString(modelInstance)
        )
    }

    private fun getExecutableFlowNodes(flowNodes: Collection<FlowNode>, processId: String): Set<FlowNode> {
        return flowNodes.stream()
                .filter { node: FlowNode? -> isExecutable(node, processId) }
                .collect(Collectors.toSet())
    }

    private fun getExecutableSequenceNodes(sequenceFlows: Collection<SequenceFlow>, definitionFlowNodes: Set<FlowNode>): Set<SequenceFlow> {
        return sequenceFlows.stream()
                .filter { s: SequenceFlow -> definitionFlowNodes.contains(s.source) }
                .collect(Collectors.toSet())
    }

    private fun isExecutable(node: ModelElementInstance?, processId: String): Boolean {
        if (node == null) {
            return false
        }
        return when (node) {
            is Process -> node.isExecutable && node.id == processId
            is IntermediateThrowEvent -> node.eventDefinitions.none { it is LinkEventDefinition }
            else -> isExecutable(node.parentElement, processId)
        }
    }

    private fun BpmnModelInstance.processDefinitionKey() =
            getModelElementsByType(Process::class.java).firstOrNull { process -> process.isExecutable }?.id
                    ?: throw IllegalArgumentException("No executable process found")

    private fun Double.asPercent() = this * 100

}
