import { Checkbox, FormControlLabel, IconButton, Theme } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import { CenterFocusStrong, CloudDownload, ZoomIn, ZoomOut } from "@material-ui/icons";
import React, { useCallback, useEffect, useState } from "react";
import { ParsedModel, ParsedRun } from "../../api/api";
import { downloadFile } from "../../util/FileUtils";
import BpmnViewer, { BpmnViewerData, BpmnViewerListener } from "./BpmnViewer";

interface Props {
    selectedModel: ParsedModel | undefined;
    selectedRun: ParsedRun | undefined;
}

const useStyles = makeStyles((theme: Theme) => ({
    settings: {
        zoom: 0.85,
        padding: "0.5rem 0.5rem 0rem 1rem",
        display: "flex"
    },
    settingsLabel: {
        transition: theme.transitions.create("opacity"),
        opacity: 0.38,
        "&:hover": {
            opacity: 0.87
        }
    },
    sectionContainer: {
        border: "2px solid rgba(34, 36, 38, 0.1)",
        backgroundColor: "rgba(255, 255, 255, 0.54)",
        borderRadius: "4px",
        display: "flex",
        flexDirection: "column",
        maxWidth: "960px",
        width: "100%",
        margin: "2rem auto 0rem auto"
    },
    sectionTitle: {
        height: "36px",
        padding: "0.5rem",
        backgroundColor: "rgba(34, 36, 38, 0.1)",
        display: "block",
        width: "100%",
        fontWeight: 500
    },
    sectionTitleFlex: {
        display: "flex"
    },
    sectionTitleRight: {
        flexGrow: 1,
        textAlign: "right",
        color: theme.palette.text.hint
    },
    viewer: {
        height: "640px",
        width: "100%"
    },
    viewerContainer: {
        position: "relative",
        height: "640px",
        "&>div": {
            position: "absolute"
        }
    },
    settingsSpacer: {
        flexGrow: 1
    }
}));

const CoverageViewer: React.FC<Props> = props => {
    const classes = useStyles();

    const { selectedModel, selectedRun } = props;

    const [data, setData] = useState<BpmnViewerData | undefined>(undefined);
    const [bpmnListener, setBpmnListener] = useState<BpmnViewerListener | undefined>(undefined);

    const [showCoverage, setShowCoverage] = useState(true);
    const [showTransactionBoundaries, setShowTransactionBoundaries] = useState(false);
    const [showExpressions, setShowExpressions] = useState(false);

    const download = useCallback(() => {
        if (selectedModel) {
            downloadFile(`${selectedModel.key}.bpmn`, selectedModel.xml);
        }
    }, [selectedModel]);

    useEffect(() => {
        if (selectedRun && selectedModel) {
            setData({
                xml: selectedModel.xml,
                highlightFlowNodes: selectedRun.coveredNodes.map(node => node.id),
                highlightSequenceFlows: selectedRun.coveredSequenceFlows
            });
        } else if (selectedModel) {
            setData({
                xml: selectedModel.xml,
                highlightFlowNodes: selectedModel.coveredNodes.map(node => node.id),
                highlightSequenceFlows: selectedModel.coveredSequenceFlows
            });
        } else {
            setData(undefined);
        }
    }, [selectedModel, selectedRun]);

    if (!selectedModel) {
        return null;
    }

    return (
        <>
            <div className={classes.sectionContainer}>
                <div className={classes.sectionTitle}>
                    <span>Model Viewer</span>
                </div>
                <div className={classes.settings}>

                    <FormControlLabel
                        label="Show Coverage"
                        className={classes.settingsLabel}
                        control={(
                            <Checkbox
                                size="small"
                                color="primary"
                                checked={showCoverage}
                                onChange={(e, checked) => setShowCoverage(checked)}
                                name="ShowCoverage" />
                        )} />

                    <FormControlLabel
                        label="Show Transaction Boundaries"
                        className={classes.settingsLabel}
                        control={(
                            <Checkbox
                                size="small"
                                color="primary"
                                checked={showTransactionBoundaries}
                                onChange={(e, checked) => setShowTransactionBoundaries(checked)}
                                name="ShowTransactionBoundaries" />
                        )} />

                    <FormControlLabel
                        label="Show Expressions"
                        className={classes.settingsLabel}
                        control={(
                            <Checkbox
                                size="small"
                                color="primary"
                                checked={showExpressions}
                                onChange={(e, checked) => setShowExpressions(checked)}
                                name="ShowExpressions" />
                        )} />

                    <div className={classes.settingsSpacer} />

                    <IconButton
                        title="Zoom In"
                        onClick={() => bpmnListener?.send("ZOOM_IN")}>
                        <ZoomIn />
                    </IconButton>

                    <IconButton
                        title="Zoom Out"
                        onClick={() => bpmnListener?.send("ZOOM_OUT")}>
                        <ZoomOut />
                    </IconButton>

                    <IconButton
                        title="Reset Zoom"
                        onClick={() => bpmnListener?.send("RESET_ZOOM")}>
                        <CenterFocusStrong />
                    </IconButton>

                    <IconButton
                        disabled={!selectedModel}
                        title="Download BPMN"
                        onClick={download}>
                        <CloudDownload />
                    </IconButton>

                </div>

                <div className={classes.viewerContainer}>
                    <BpmnViewer
                        className={classes.viewer}
                        showCoverage={showCoverage}
                        showExpressions={showExpressions}
                        showTransactionBoundaries={showTransactionBoundaries}
                        setListener={setBpmnListener}
                        data={data} />
                </div>

            </div>
        </>
    );
};

export default CoverageViewer;
