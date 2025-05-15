import { makeStyles } from "@material-ui/core/styles";
import React from "react";

interface Props {
    title: string;
}

const useStyles = makeStyles(() => ({
    sectionContainer: {
        border: "2px solid rgba(34, 36, 38, 0.1)",
        backgroundColor: "rgba(255, 255, 255, 0.54)",
        borderRadius: "4px",
        display: "flex",
        flexDirection: "column",
        maxWidth: "960px",
        width: "100%",
        margin: "1rem auto 0rem auto"
    },
    sectionTitle: {
        height: "36px",
        padding: "0.5rem",
        backgroundColor: "rgba(34, 36, 38, 0.1)",
        display: "block",
        width: "100%",
        fontWeight: 500
    },
    sectionContent: {
        display: "flex",
        flexDirection: "column",
        padding: "1rem"
    }
}));

const Section: React.FC<Props> = props => {
    const classes = useStyles();

    return (
        <div className={classes.sectionContainer}>

            <span className={classes.sectionTitle}>
                {props.title}
            </span>

            <div className={classes.sectionContent}>
                {props.children}
            </div>

        </div>
    );
};

export default Section;
