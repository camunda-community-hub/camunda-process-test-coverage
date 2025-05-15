import { Tooltip as MaterialTooltip } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import React from "react";

declare type Placement =
    | "bottom-end"
    | "bottom-start"
    | "bottom"
    | "left-end"
    | "left-start"
    | "left"
    | "right-end"
    | "right-start"
    | "right"
    | "top-end"
    | "top-start"
    | "top";

interface Props {
    title: string;
    placement?: Placement;
}

const useStyles = makeStyles(() => ({
    tooltip: {
        fontSize: "0.85rem",
        backgroundColor: "rgba(0, 0, 0, 0.87)"
    },
    tooltipArrow: {
        color: "rgba(0, 0, 0, 0.87)"
    },
}));

const Tooltip: React.FC<Props> = props => {
    const classes = useStyles();

    return (
        <MaterialTooltip
            title={props.title}
            arrow
            placement={props.placement || "top"}
            classes={{
                arrow: classes.tooltipArrow,
                tooltip: classes.tooltip
            }}>
            <span>{props.children}</span>
        </MaterialTooltip>
    );
};

export default Tooltip;
