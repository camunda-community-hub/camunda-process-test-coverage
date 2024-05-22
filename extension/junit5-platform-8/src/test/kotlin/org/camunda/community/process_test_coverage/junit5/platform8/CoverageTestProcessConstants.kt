/*-
 * #%L
 * Camunda Process Test Coverage JUnit5 Platform 8
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
package org.camunda.community.process_test_coverage.junit5.platform8

import io.camunda.zeebe.client.ZeebeClient

object CoverageTestProcessConstants {

    const val PROCESS_DEFINITION_KEY = "process-test-coverage"

    /** where to find bpmn in classpath  */
    const val BPMN_PATH = "process.bpmn"

    val ALL_ELEMENTS = arrayOf(
            "StartEvent_1",
            "SequenceFlow_Start1ToExclusive3",
            "ExclusiveGateway_3",
            "SequenceFlow_Exclusive3ToManualA",
            "ManualTask_3",
            "SequenceFlow_ManualAToEnd2",
            "EndEvent_2",
            "SequenceFlow_Exclusive3ToManualB",
            "ManualTask_4",
            "SequenceFlow_ManualBToEnd3",
            "EndEvent_3"
    )

    val PATH_B_ELEMENTS = arrayOf(
            "StartEvent_1",
            "SequenceFlow_Start1ToExclusive3",
            "ExclusiveGateway_3",
            "SequenceFlow_Exclusive3ToManualB",
            "ManualTask_4",
            "SequenceFlow_ManualBToEnd3",
            "EndEvent_3"
    )

    fun deploy(client: ZeebeClient, resourcePath: String = BPMN_PATH) {
        client.newDeployResourceCommand()
            .addResourceFromClasspath(resourcePath)
            .send()
            .join()
    }

}
