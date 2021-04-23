package org.camunda.bpm.extension.process_test_coverage.rules;

public class CoverageTestProcessConstants {

    public static final String PROCESS_DEFINITION_KEY = "process-test-coverage";
    
    /** where to find bpmn in classpath */
    public static final String BPMN_PATH = "process.bpmn";
    
    public static final String[] ALL_ELEMENTS = new String[]{
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
            
    };
    
    public static final String[] PATH_A_ELEMENTS = new String[]{
        "StartEvent_1",
        "SequenceFlow_Start1ToExclusive3",
        "ExclusiveGateway_3",
        "SequenceFlow_Exclusive3ToManualA",
        "ManualTask_3",
        "SequenceFlow_ManualAToEnd2",
        "EndEvent_2"
    };
    
    public static final String[] PATH_B_ELEMENTS = new String[]{
        "StartEvent_1",
        "SequenceFlow_Start1ToExclusive3",
        "ExclusiveGateway_3",
        "SequenceFlow_Exclusive3ToManualB",
        "ManualTask_4",
        "SequenceFlow_ManualBToEnd3",
        "EndEvent_3"
    };

    
}
