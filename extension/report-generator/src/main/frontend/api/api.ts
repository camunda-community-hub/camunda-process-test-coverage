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
    runs: ParsedRun[];
}

export interface ParsedModel extends CoverageContainer {
    id: string;
    key: string;
    suites: ParsedSuite[];
    xml: string;
}

export interface ParsedRun extends CoverageContainer {
    id: string;
    name: string;
}
