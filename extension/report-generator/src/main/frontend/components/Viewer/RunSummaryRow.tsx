import { Theme } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import { Code, PlaylistAddCheck } from "@material-ui/icons";
import clsx from "clsx";
import React from "react";
import { ParsedModel, ParsedRun } from "../../api/api";

interface Props {
    selected: boolean;
    type: "model" | "run";
    model: ParsedModel | ParsedRun;
    onClick: () => void;
}

const useStyles = makeStyles((theme: Theme) => ({
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
    summaryColumnName: {
        textAlign: "left",
        display: "flex"
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
    summaryColumnNamePadding: {
        paddingLeft: "2.75rem !important"
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
    },
    playIcon: {
        color: theme.palette.primary.main
    }
}));

const RunSummaryRow: React.FC<Props> = props => {
    const classes = useStyles();

    const { type, model, onClick, selected } = props;

    const data = {
        coverage: model.coverage,
        covered: model.coveredNodeCount + model.coveredSequenceFlowCount,
        icon: type === "model" ? Code : PlaylistAddCheck,
        name: type === "model" ? (model as ParsedModel).key : (model as ParsedRun).name,
        total: model.totalElementCount
    };

    return (
        <tr
            onClick={onClick}
            className={clsx(
                classes.summaryRow,
                selected && classes.summaryRowActive
            )}>
            <td className={clsx(
                classes.summaryColumnName,
                type === "run" && classes.summaryColumnNamePadding,
            )}>
                {React.createElement(data.icon, {
                    className: classes.rowIcon,
                    fontSize: "small"
                })}
                {data.name}
            </td>
            <td className={classes.summaryColumnCount}>{data.covered}</td>
            <td className={classes.summaryColumnCount}>{data.total}</td>
            <td className={classes.spacingColumn} />
            <td className={clsx(classes.summaryColumnCoverage, {
                [classes.coverageGreen]: data.coverage >= 0.9,
                [classes.coverageYellow]: data.coverage < 0.9 && data.coverage >= 0.5,
                [classes.coverageRed]: data.coverage < 0.5
            })}>
                {`${(data.coverage * 100).toFixed(2)}%`}
            </td>
        </tr>
    );
};

export default RunSummaryRow;
