export interface Model {
    id: string;
    key: string;
    totalElementCount: number;
    version?: string;
    xml: string;
}

export interface Suite {
    id: string;
    name: string;
    runs: Run[];
}

export interface Run {
    id: string;
    name: string;
    events: Event[];
}

export interface Event {
    source: EventSource;
    type: EventType;
    definitionKey: string;
    elementType: string;
    modelKey: string;
    timestamp: number;
}

export type EventType = "START" | "END" | "TAKE";
export type EventSource = "FLOW_NODE" | "SEQUENCE_FLOW" | "DMN_RULE";

export interface CoveredNode {
    id: string;
    ended: boolean;
}

export interface CoverageContainer {
    totalElementCount: number;
    coveredNodes: CoveredNode[];
    coveredNodeCount: number;
    coveredSequenceFlows: string[];
    coveredSequenceFlowCount: number;
    coverage: number;
}

export interface ParsedSuite extends CoverageContainer {
    id: string;
    name: string;
    models: ParsedModel[];
}

export interface ParsedModel extends CoverageContainer {
    id: string;
    key: string;
    runs: ParsedRun[];
    xml: string;
}

export interface ParsedRun extends CoverageContainer {
    id: string;
    name: string;
}
