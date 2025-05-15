import { makeStyles } from "@material-ui/core/styles";
import Viewer from "bpmn-js/lib/NavigatedViewer";
import clsx from "clsx";
import $ from "jquery";
import React, { useEffect, useMemo } from "react";
import camundaModdleDescriptor from "camunda-bpmn-moddle/resources/camunda.json";
import { ModdleElement } from "bpmn-js/lib/model/Types";

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

const useStyles = makeStyles(() => ({
    root: {
        height: "640px",
        overflow: "hidden"
    },
    modeler: {
        height: "640px"
    },
    highlight: {
        "&:not(.djs-connection) .djs-visual > :nth-child(1)": {
            fill: "rgba(50, 205, 50, 0.50) !important"
        }
    },
    highlightSequenceFlow: {
        stroke: "rgba(20, 125, 20, 1) !important",
        strokeWidth: "2px !important"
    },
    transactionBoundary: {
        backgroundColor: "rgba(180, 21, 21, 0.7)",
        borderRadius: "1px",
        minHeight: "50px",
        width: "4px"
    },
    transactionBoundarySmall: {
        backgroundColor: "rgba(180, 21, 21, 0.7)",
        borderRadius: "1px",
        minHeight: "32px",
        width: "4px"
    },
    executionListener: {
        backgroundColor: "rgba(21, 66, 180, 0.7)",
        color: "white",
        borderRadius: "4px",
        fontFamily: "Arial",
        fontSize: "12px",
        padding: "5px",
        minHeight: "16px",
        minWidth: "16px",
        textAlign: "center",
        whiteSpace: "nowrap"
    }
}));

// @see
// https://github.com/bpmn-io/camunda-transaction-boundaries/blob/master/lib/TransactionBoundaries.js#L63
const getTransactionBoundaries = (element: any): {
    before: boolean;
    after: boolean;
} => {
    const { businessObject, loopCharacteristics } = element;
    const eventDefinitions = businessObject.eventDefinitions || [];
    const eventDefinitionType = eventDefinitions.length && eventDefinitions[0].$type;

    const isWaitStateTask = element.type === "bpmn:ReceiveTask"
        || element.type === "bpmn:UserTask"
        || (element.type === "bpmn:ServiceTask" && businessObject.type === "external");

    // TODO: Parallel/Inclusive Gateway with multiple incoming sequence flows
    const isWaitStateGateway = false;

    const isWaitStateEvent = element.type === "bpmn:IntermediateCatchEvent" && (
        eventDefinitionType === "bpmn:MessageEventDefinition"
        || eventDefinitionType === "bpmn:TimerEventDefinition"
        || eventDefinitionType === "bpmn:SignalEventDefinition"
        || eventDefinitionType === "bpmn:ConditionalEventDefinition"
    );

    const isAsyncAfter = businessObject.asyncAfter
        || (loopCharacteristics && loopCharacteristics.asyncAfter);

    const isAsyncBefore = businessObject.asyncBefore
        || (loopCharacteristics && loopCharacteristics.asyncBefore);

    const boundariesBefore = isWaitStateTask
        || isWaitStateEvent
        || isWaitStateGateway
        || isAsyncBefore;

    return { before: !!boundariesBefore, after: !!isAsyncAfter };
};

let viewer: Viewer | undefined;

const BpmnViewer: React.FC<Props> = props => {
    const classes = useStyles();

    const { data, setListener } = props;
    const listener: BpmnViewerListener = useMemo(() => ({
        send: async (event: BpmnViewerEvent) => {
            switch (event) {
                case "RESET_ZOOM": {
                    viewer?.get("canvas").zoom("fit-viewport", true);
                    break;
                }
                case "ZOOM_IN": {
                    viewer?.get("zoomScroll").zoom(1);
                    break;
                }
                case "ZOOM_OUT": {
                    viewer?.get("zoomScroll").zoom(-1);
                    break;
                }
                default: {
                    // Do nothing
                }
            }
        }
    }), []);

    useEffect(() => {
        setListener && setListener(listener);
    }, [setListener, listener]);

    useEffect(() => {
        viewer = new Viewer({
            container: "#bpmn-canvas",
            moddleExtensions: {
                camunda: camundaModdleDescriptor
            }
        });
        viewer.get("zoomScroll").toggle(false);
    }, []);

    useEffect(() => {
        (async () => {
            if (viewer && data) {
                const canvas = viewer.get("canvas");
                const overlays = viewer.get("overlays");
                const elementRegistry = viewer.get("elementRegistry");

                await viewer.importXML(data.xml);

                // zoom to fit full viewport
                canvas.zoom("fit-viewport", true);

                if (props.showCoverage) {
                    data.highlightFlowNodes?.forEach(node => {
                        canvas.addMarker(node, classes.highlight);
                    });

                    data.highlightSequenceFlows?.forEach(flow => {
                        $(`g[data-element-id='${flow}']`)
                            .find("path")
                            .addClass(classes.highlightSequenceFlow);
                    });
                }

                // visualizations
                const elements = elementRegistry.getAll();
                for (let i = 0; i < elements.length; i++) {
                    const element = elements[i];
                    if (element.type !== "label") {
                        if (props.showTransactionBoundaries) {
                            const transactionBoundaries = getTransactionBoundaries(element);
                            if (transactionBoundaries.before) {
                                overlays.add(element.id, "note", {
                                    position: {
                                        bottom: (element.type === "bpmn:IntermediateCatchEvent" ? 34 : 64),
                                        left: (element.type === "bpmn:IntermediateCatchEvent" ? -3 : -5)
                                    },
                                    html: `<div class='${element.type === "bpmn:IntermediateCatchEvent" ? classes.transactionBoundarySmall : classes.transactionBoundary}' />`
                                });
                            }
                            if (transactionBoundaries.after) {
                                overlays.add(element.id, "note", {
                                    position: {
                                        bottom: 64,
                                        right: -1
                                    },
                                    html: `<div class='${classes.transactionBoundary}' />`
                                });
                            }
                        }

                        if (props.showExpressions) {
                            if (element.businessObject.extensionElements?.values) {
                                const extensionElements: ModdleElement[] = element
                                    .businessObject.extensionElements.values;
                                extensionElements.forEach(extensionElement => {
                                    const { $type, event, fields } = extensionElement;

                                    const executionListener = "camunda:executionListener";
                                    if ($type.toLowerCase() === executionListener.toLowerCase() && (event === "end" || event === "start") && fields) {
                                        fields.forEach((field: { expression: any; }) => {
                                            const position = {
                                                bottom: 0,
                                                [event === "end" ? "right" : "left"]: 0
                                            };

                                            const html = `<div class='${classes.executionListener}'>${field.expression}</div>`;

                                            overlays.add(element.id, "note", { position, html });
                                        });
                                    }
                                });
                            }

                            if (element.type === "bpmn:SequenceFlow"
                                && element.businessObject.conditionExpression) {
                                const position = {
                                    left: 0
                                } as {
                                    left: number,
                                    top: number | undefined,
                                    bottom: number | undefined
                                };
                                if (element.waypoints[0].y
                                    > element.waypoints[element.waypoints.length - 1].y) {
                                    position.top = -29;
                                } else {
                                    position.bottom = -3;
                                }
                                overlays.add(element.id, "note", {
                                    position: position,
                                    html: `<div class='${classes.executionListener}'>${element.businessObject.conditionExpression.body}</div>`
                                });
                            }

                            if (element.businessObject.$attrs["camunda:delegateExpression"]) {
                                overlays.add(element.id, "note", {
                                    position: {
                                        bottom: -3,
                                        left: 0
                                    },
                                    html: `<div class='${classes.executionListener}'>${element.businessObject.$attrs["camunda:delegateExpression"]}</div>`
                                });
                            }
                        }
                    }
                }
            }
        })();
    }, [data, classes, props.showCoverage, props.showExpressions, props.showTransactionBoundaries]);

    return (
        <div className={clsx(classes.root, props.className)}>
            <div className={classes.modeler} id="bpmn-canvas" />
        </div>
    );
};

export default BpmnViewer;
