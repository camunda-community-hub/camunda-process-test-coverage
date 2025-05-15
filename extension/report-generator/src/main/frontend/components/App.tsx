import { CssBaseline } from "@material-ui/core";
import { makeStyles, ThemeProvider } from "@material-ui/core/styles";
import React from "react";
import Theme from "../theme";
import ViewerContainer from "./Viewer/ViewerContainer";

const useStyles = makeStyles(() => ({
    root: {
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column"
    }
}));

const App: React.FC = () => {
    const classes = useStyles();

    return (
        <ThemeProvider theme={Theme}>

            <div className={classes.root}>

                <CssBaseline />

                <ViewerContainer />

            </div>

        </ThemeProvider>
    );
};

export default App;
