import { FormControl, InputLabel, MenuItem, Select } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import React, { useEffect, useMemo, useState } from "react";
import { parseSuites } from "../../util/ParsingUtils";
import CoverageViewer from "./CoverageViewer";
import RunSummary from "./RunSummary";

const useStyles = makeStyles(() => ({
    title: {
        fontSize: "2rem",
        marginBottom: 0
    },
    subtitle: {
        marginTop: "0.25rem",
        paddingBottom: "0.5rem",
        marginBottom: "1.5rem",
        borderBottom: "2px solid #666"
    },
    selectorTitle: {
        fontWeight: "bold",
        fontSize: "1rem",
        marginTop: "0.5rem"
    },
    page: {
        display: "flex",
        flexDirection: "column",
        width: "960px",
        maxWidth: "960px",
        margin: "2rem auto 1rem auto"
    },
    selector: {
        display: "flex",
        flexWrap: "wrap",
        padding: "0.5rem 0",
        flexDirection: "row"
    },
    item: {
        flex: "0 0 calc((100% - 2.25rem)/4)",
        marginBottom: "0.75rem",
        marginRight: "0.75rem",
        borderRadius: "0.25rem",
        color: "black",
        border: "2px solid darkgreen",
        display: "flex",
        padding: "0.5rem",
        flexDirection: "column",
        cursor: "pointer",
        "&>span": {
            whiteSpace: "nowrap"
        },
        "&:nth-child(4n)": {
            marginRight: 0
        }
    },
    itemSelected: {
        backgroundColor: "darkgreen",
        color: "white"
    },
    itemTitle: {
        fontWeight: "bold",
        marginBottom: "0.25rem"
    },
    itemSubtitle: {
        fontSize: "0.75rem"
    },
    selectWrapper: {
        display: "flex",
        marginTop: "0.5rem",
        marginBottom: "2rem"
    },
    select: {
        width: "calc((100% - 1rem) / 3)",
        marginRight: "0.5rem",
        "&:last-child": {
            marginRight: 0
        }
    },
    hint: {
        fontSize: "1.5rem",
        textAlign: "center",
        marginTop: "1rem",
        marginBottom: "1rem",
        fontWeight: "bold"
    },
    paper: {
        backgroundColor: "white"
    }
}));

const ViewerContainer: React.FC = () => {
    const classes = useStyles();

    const [selectedModelKey, setSelectedModelKey] = useState<string | undefined>(undefined);
    const [selectedSuiteId, setSelectedSuiteId] = useState<string | undefined>(undefined);
    const [selectedRunId, setSelectedRunId] = useState<string | undefined>(undefined);

    const parsedSuites = useMemo(
        () => parseSuites(window.COVERAGE_DATA.suites, window.COVERAGE_DATA.models),
        [window.COVERAGE_DATA]
    );

    const selectedSuite = useMemo(
        () => parsedSuites.find(s => s.id === selectedSuiteId),
        [parsedSuites, selectedSuiteId]
    );

    const selectedModel = useMemo(
        () => selectedSuite?.models.find(m => m.key === selectedModelKey),
        [selectedSuite, selectedModelKey]
    );

    const selectedRun = useMemo(
        () => selectedModel?.runs.find(r => r.id === selectedRunId),
        [selectedModel, selectedRunId]
    );

    // Reset model and run id on suite changed
    useEffect(() => {
        setSelectedModelKey(undefined);
        setSelectedRunId(undefined);
    }, [selectedSuiteId]);

    // Reset run id on model changed
    useEffect(() => {
        setSelectedRunId(undefined);
    }, [selectedModelKey]);

    return (
        <div className={classes.page}>

            <h1 className={classes.title}>Test Coverage Report</h1>

            <span className={classes.subtitle}>
                {`${window.COVERAGE_DATA.suites.length} Suites, ${window.COVERAGE_DATA.models.length} Models processed.`}
            </span>

            {selectedSuite && (
                <div className={classes.selectWrapper}>
                    <FormControl
                        className={classes.select}
                        size="small"
                        color="primary"
                        variant="outlined">

                        <InputLabel id="test-suite">
                            Test Suite
                        </InputLabel>

                        <Select
                            label="Test Suite"
                            value={selectedSuiteId}
                            labelId="test-suite"
                            MenuProps={{ classes: { paper: classes.paper } }}
                            onChange={e => setSelectedSuiteId(e.target.value as string)}>

                            {parsedSuites.map(suite => (
                                <MenuItem
                                    key={suite.id}
                                    value={suite.id}>
                                    {suite.name.substr(suite.name.lastIndexOf(".") + 1)}
                                </MenuItem>
                            ))}

                        </Select>
                    </FormControl>

                    {selectedModel && (
                        <>

                            <FormControl
                                className={classes.select}
                                size="small"
                                color="primary"
                                variant="outlined">

                                <InputLabel id="model">
                                    Model
                                </InputLabel>

                                <Select
                                    label="Model"
                                    value={selectedModelKey}
                                    labelId="model"
                                    MenuProps={{ classes: { paper: classes.paper } }}
                                    onChange={e => setSelectedModelKey(e.target.value as string)}>

                                    {selectedSuite.models.map(model => (
                                        <MenuItem
                                            key={model.key}
                                            value={model.key}>
                                            {model.key}
                                        </MenuItem>
                                    ))}

                                </Select>
                            </FormControl>

                            <FormControl
                                className={classes.select}
                                size="small"
                                color="primary"
                                variant="outlined">

                                <InputLabel id="run">
                                    Run
                                </InputLabel>

                                <Select
                                    label="Run"
                                    value={selectedRunId === undefined ? "" : selectedRunId}
                                    labelId="run"
                                    MenuProps={{ classes: { paper: classes.paper } }}
                                    onChange={e => setSelectedRunId(e.target.value === "" ? undefined : e.target.value as string)}>

                                    <MenuItem value=""><em>None</em></MenuItem>
                                    {selectedModel.runs.map(run => (
                                        <MenuItem
                                            key={run.id}
                                            value={run.id}>
                                            {run.name}
                                        </MenuItem>
                                    ))}

                                </Select>
                            </FormControl>
                        </>
                    )}
                </div>
            )}

            {!selectedSuite && (
                <>
                    <div className={classes.selector}>
                        {parsedSuites.map(suite => (
                            <div
                                onClick={() => setSelectedSuiteId(suite.id)}
                                key={suite.id}
                                className={clsx(
                                    classes.item,
                                    selectedSuiteId === suite.id && classes.itemSelected
                                )}>
                                <span className={classes.itemTitle}>
                                    {suite.name.substr(suite.name.lastIndexOf(".") + 1)}
                                </span>
                                <span className={classes.itemSubtitle}>
                                    {`${(suite.coverage * 100).toFixed(2)}% Coverage`}
                                </span>
                            </div>
                        ))}
                    </div>
                    <div className={classes.hint}>
                        Please select a test suite to see details.
                    </div>
                </>
            )}

            {selectedSuite && !selectedModel && (
                <>
                    <div className={classes.selector}>
                        {selectedSuite.models.map(model => (
                            <div
                                onClick={() => setSelectedModelKey(model.key)}
                                key={model.key}
                                className={clsx(
                                    classes.item,
                                    selectedModelKey === model.key && classes.itemSelected
                                )}>
                                <span className={classes.itemTitle}>
                                    {model.key}
                                </span>
                                <span className={classes.itemSubtitle}>
                                    {`Coverage: ${(model.coverage * 100).toFixed(2)}%`}
                                </span>
                            </div>
                        ))}
                    </div>
                    <div className={classes.hint}>
                        Please select a model to see details.
                    </div>
                </>
            )}

            {selectedSuite && selectedModel && !selectedRun && (
                <>
                    <div className={classes.selectorTitle}>
                        Select a run to see details for that run.
                    </div>
                    <div className={classes.selector}>
                        {selectedModel.runs.map(run => (
                            <div
                                onClick={() => setSelectedRunId(run.id)}
                                key={run.id}
                                className={clsx(
                                    classes.item,
                                    selectedRunId === run.id && classes.itemSelected
                                )}>
                                <span className={classes.itemTitle}>
                                    {run.name}
                                </span>
                                <span className={classes.itemSubtitle}>
                                    {`Coverage: ${(run.coverage * 100).toFixed(2)}%`}
                                </span>
                            </div>
                        ))}
                    </div>
                </>
            )}

            {selectedSuite && selectedModel && (
                <CoverageViewer
                    selectedModel={selectedModel}
                    selectedRun={selectedRun} />
            )}

            {selectedSuite && (
                <RunSummary
                    selectedSuite={selectedSuite}
                    selectedModel={selectedModel}
                    selectedRun={selectedRun}
                    onModelSelected={model => setSelectedModelKey(model.key)}
                    onRunSelected={run => setSelectedRunId(run.id)} />
            )}
        </div>
    );
};

export default ViewerContainer;
