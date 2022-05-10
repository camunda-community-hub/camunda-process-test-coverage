package org.camunda.community.process_test_coverage.junit5.platform7

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


}