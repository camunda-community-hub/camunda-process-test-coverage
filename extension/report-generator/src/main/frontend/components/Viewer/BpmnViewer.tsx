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
import { h } from "preact";
import { useEffect, useMemo, useRef } from "preact/hooks";
import Viewer from "bpmn-js/lib/NavigatedViewer";
import camundaModdleDescriptor from "camunda-bpmn-moddle/resources/camunda.json";

export interface BpmnViewerData {
    highlightSequenceFlows: string[];
    highlightFlowNodes: string[];
    xml: string;
}

export type BpmnViewerEvent = "RESET_ZOOM" | "ZOOM_IN" | "ZOOM_OUT";
export type BpmnViewerListener = { send: (event: BpmnViewerEvent) => void };

interface Props {
    showTransactionBoundaries: boolean;
    showCoverage: boolean;
    showExpressions: boolean;
    data?: BpmnViewerData;
    className?: string;
    setListener?: (listener: BpmnViewerListener) => void;
}

const getTransactionBoundaries = (element: any) => {
    const { businessObject, loopCharacteristics } = element;
    const eventDefinitions = businessObject.eventDefinitions || [];
    const eventDefinitionType = eventDefinitions.length && eventDefinitions[0].$type;

    const isWaitStateTask =
        element.type === "bpmn:ReceiveTask" ||
        element.type === "bpmn:UserTask" ||
        (element.type === "bpmn:ServiceTask" && businessObject.type === "external");

    const isWaitStateGateway = false;

    const isWaitStateEvent =
        element.type === "bpmn:IntermediateCatchEvent" &&
        (eventDefinitionType === "bpmn:MessageEventDefinition" ||
            eventDefinitionType === "bpmn:TimerEventDefinition" ||
            eventDefinitionType === "bpmn:SignalEventDefinition" ||
            eventDefinitionType === "bpmn:ConditionalEventDefinition");

    const isAsyncAfter = businessObject.asyncAfter || (loopCharacteristics && loopCharacteristics.asyncAfter);
    const isAsyncBefore = businessObject.asyncBefore || (loopCharacteristics && loopCharacteristics.asyncBefore);

    const boundariesBefore = isWaitStateTask || isWaitStateEvent || isWaitStateGateway || isAsyncBefore;

    return { before: !!boundariesBefore, after: !!isAsyncAfter };
};

let viewer: Viewer | undefined;

const BpmnViewer = (props: Props) => {
    const containerRef = useRef<HTMLDivElement>(null);

    const listener: BpmnViewerListener = useMemo(() => ({
        send: (event: BpmnViewerEvent) => {
            switch (event) {
                case "RESET_ZOOM":
                    viewer?.get("canvas").zoom("fit-viewport", true);
                    break;
                case "ZOOM_IN":
                    viewer?.get("zoomScroll").zoom(1);
                    break;
                case "ZOOM_OUT":
                    viewer?.get("zoomScroll").zoom(-1);
                    break;
            }
        }
    }), []);

    useEffect(() => {
        if (props.setListener) props.setListener(listener);
    }, [listener, props.setListener]);

    useEffect(() => {
        if (!containerRef.current) return;

        viewer = new Viewer({
            container: containerRef.current,
            moddleExtensions: {
                camunda: camundaModdleDescriptor,
            }
        });

        viewer.get("zoomScroll").toggle(false);

        return () => {
            viewer = undefined;
        };
    }, []);

    useEffect(() => {
        (async () => {
            if (viewer && props.data) {
                const canvas = viewer.get("canvas");
                const overlays = viewer.get("overlays");
                const elementRegistry = viewer.get("elementRegistry");

                await viewer.importXML(props.data.xml);

                // Fit viewport
                canvas.zoom("fit-viewport", true);

                if (props.showCoverage) {
                    props.data.highlightFlowNodes.forEach(nodeId => {
                        canvas.addMarker(nodeId, "highlight");
                    });

                    props.data.highlightSequenceFlows.forEach(flowId => {
                        const element = document.querySelector(`g[data-element-id='${flowId}']`);
                        const path = element?.querySelector('path');

                        if (path) {
                            path.classList.add("highlight-sequence-flow");
                        }
                    });
                }

                // Add transaction boundaries and expressions overlays
                const elements = elementRegistry.getAll();
                for (const element of elements) {
                    if (element.type !== "label") {
                        if (props.showTransactionBoundaries) {
                            const boundaries = getTransactionBoundaries(element);
                            if (boundaries.before) {
                                overlays.add(element.id, "note", {
                                    position: {
                                        bottom: element.type === "bpmn:IntermediateCatchEvent" ? 34 : 64,
                                        left: element.type === "bpmn:IntermediateCatchEvent" ? -3 : -5,
                                    },
                                    html: `<div class="bg-red-700 rounded-xs min-h-[50px] w-[4px] ${
                                        element.type === "bpmn:IntermediateCatchEvent" ? "min-h-[32px]" : ""
                                    }"></div>`
                                });
                            }
                            if (boundaries.after) {
                                overlays.add(element.id, "note", {
                                    position: { bottom: 64, right: -1 },
                                    html: `<div class="bg-red-700 rounded-xs min-h-[50px] w-[4px]"></div>`
                                });
                            }
                        }

                        if (props.showExpressions) {
                            const extElements = element.businessObject.extensionElements?.values || [];
                            extElements.forEach((ext: any) => {
                                if (
                                    ext.$type?.toLowerCase() === "camunda:executionlistener" &&
                                    (ext.event === "end" || ext.event === "start") &&
                                    ext.fields
                                ) {
                                    ext.fields.forEach((field: any) => {
                                        const position = {
                                            bottom: 0,
                                            [ext.event === "end" ? "right" : "left"]: 0
                                        };
                                        const html = `<div class="bg-blue-700 text-white rounded-sm px-1 text-xs font-sans whitespace-nowrap">${field.expression}</div>`;
                                        overlays.add(element.id, "note", { position, html });
                                    });
                                }
                            });

                            if (element.type === "bpmn:SequenceFlow" && element.businessObject.conditionExpression) {
                                const waypoints = element.waypoints;
                                const position: any = { left: 0 };
                                if (waypoints[0].y > waypoints[waypoints.length - 1].y) {
                                    position.top = -29;
                                } else {
                                    position.bottom = -3;
                                }
                                overlays.add(element.id, "note", {
                                    position,
                                    html: `<div class="bg-blue-700 text-white rounded-sm px-1 text-xs font-sans whitespace-nowrap">${element.businessObject.conditionExpression.body}</div>`
                                });
                            }

                            if (element.businessObject.$attrs?.["camunda:delegateExpression"]) {
                                overlays.add(element.id, "note", {
                                    position: { bottom: -3, left: 0 },
                                    html: `<div class="bg-blue-700 text-white rounded-sm px-1 text-xs font-sans whitespace-nowrap">${element.businessObject.$attrs["camunda:delegateExpression"]}</div>`
                                });
                            }
                        }
                    }
                }
            }
        })();
    }, [
        props.data,
        props.showCoverage,
        props.showExpressions,
        props.showTransactionBoundaries
    ]);

    return (
        <div className={`h-[640px] overflow-hidden ${props.className || ""}`}>
            <div ref={containerRef} className="h-[640px]" />
        </div>
    );
};

export default BpmnViewer;
