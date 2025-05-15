import { Theme } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import React from "react";
import { ParsedModel, ParsedRun, ParsedSuite } from "../../api/api";
import Section from "../Util/Section";
import Tooltip from "../Util/Tooltip";
import RunSummaryRow from "./RunSummaryRow";

interface Props {
    selectedSuite: ParsedSuite;
    selectedModel: ParsedModel | undefined;
    selectedRun: ParsedRun | undefined;
    onModelSelected: (model: ParsedModel) => void;
    onRunSelected: (run: ParsedRun) => void;
}

const useStyles = makeStyles((theme: Theme) => ({
    summaryTable: {
        borderSpacing: "0px !important"
    },
    summaryHead: {
        backgroundColor: "rgba(34, 36, 38, 0.05)",
        "&>th": {
            paddingTop: "1rem",
            paddingBottom: "1rem"
        },
        "&>th:first-child": {
            paddingLeft: "1rem"
        },
        "&>th:last-child": {
            paddingRight: "1rem"
        }
    },
    summaryRow: {
        cursor: "pointer",
        transition: theme.transitions.create("background-color"),
        "&:hover": {
            backgroundColor: "rgba(34, 36, 38, 0.1)"
        },
        "&>td": {
            paddingTop: "0.5rem",
            paddingBottom: "0.5rem"
        },
        "&>td:first-child": {
            paddingLeft: "1rem"
        },
        "&>td:last-child": {
            paddingRight: "1rem"
        }
    },
    summaryRowActive: {
        backgroundColor: "rgba(34, 36, 38, 0.15)",
        "&>td": {
            fontWeight: 500
        },
        "&:hover": {
            backgroundColor: "rgba(34, 36, 38, 0.15)"
        }
    },
    summaryFoot: {
        backgroundColor: "rgba(0, 0, 0, 0.05)",
        fontWeight: 500,
        "&>td": {
            paddingTop: "1rem",
            paddingBottom: "1rem"
        },
        "&>td:first-child": {
            paddingLeft: "1rem"
        },
        "&>td:last-child": {
            paddingRight: "1rem"
        }
    },
    summaryColumnCount: {
        textAlign: "right"
    },
    summaryColumnCoverage: {
        textAlign: "right",
        width: "1%",
        paddingLeft: "1rem"
    },
    separator: {
        margin: "0 0.25rem"
    },
    rowIcon: {
        marginRight: "0.5rem"
    },
    spacingRow: {
        "&>td": {
            padding: "0.25rem"
        }
    },
    summaryColumnNamePadding1: {
        paddingLeft: "2.75rem !important"
    },
    summaryColumnNamePadding2: {
        paddingLeft: "4.5rem !important"
    },
    coverageGreen: {
        backgroundColor: "rgba(0, 255, 0, 0.38)"
    },
    coverageYellow: {
        backgroundColor: "rgba(255, 255, 0, 0.38)"
    },
    coverageRed: {
        backgroundColor: "rgba(255, 0, 0, 0.38)"
    },
    spacingColumn: {
        width: "3rem"
    },
    empty: {
        padding: "1rem",
        textAlign: "center",
        fontWeight: "bold"
    }
}));

const RunSummary: React.FC<Props> = props => {
    const classes = useStyles();

    return (
        <Section title="Build Summary">
            <table className={classes.summaryTable}>

                <thead>
                    <tr className={classes.summaryHead}>
                        <th aria-hidden />
                        <th className={classes.summaryColumnCount}>
                            <Tooltip title="Covered Flow Nodes &amp; Sequence Flows">
                                <span>Covered</span>
                            </Tooltip>
                        </th>
                        <th className={classes.summaryColumnCount}>
                            <Tooltip title="Total Flow Nodes &amp; Sequence Flows">
                                <span>Total</span>
                            </Tooltip>
                        </th>
                        <th className={classes.spacingColumn} aria-hidden />
                        <th className={classes.summaryColumnCoverage}>Coverage</th>
                    </tr>
                </thead>

                <tbody>

                    <tr className={classes.spacingRow} key="spacing1">
                        <td />
                    </tr>

                    {props.selectedSuite.models.length === 0 && (
                        <tr>
                            <td
                                className={classes.empty}
                                colSpan={99}>
                                This suite contains no models.
                            </td>
                        </tr>
                    )}

                    {props.selectedSuite.models.map(model => (
                        <React.Fragment key={model.key}>
                            <RunSummaryRow
                                type="model"
                                selected={props.selectedModel === model}
                                key={model.key}
                                model={model}
                                onClick={() => props.onModelSelected(model)} />

                            {props.selectedModel === model && model.runs.map(run => (
                                <RunSummaryRow
                                    type="run"
                                    selected={props.selectedRun === run}
                                    key={run.id}
                                    model={run}
                                    onClick={() => props.onRunSelected(run)} />
                            ))}
                        </React.Fragment>
                    ))}

                    <tr className={classes.spacingRow} key="spacing2">
                        <td />
                    </tr>
                </tbody>
            </table>
        </Section>
    );
};

export default RunSummary;
