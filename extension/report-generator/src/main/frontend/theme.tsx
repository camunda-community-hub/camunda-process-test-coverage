import { createTheme } from "@material-ui/core/styles";

const theme = createTheme({
    palette: {
        primary: {
            light: "#32d35c",
            main: "#26b44b",
            dark: "#3d9a4c",
            contrastText: "#fff"
        },
        secondary: {
            light: "#455a64",
            main: "#37474f",
            dark: "#263238",
            contrastText: "#fff"
        },
        text: {
            primary: "rgba(0, 0, 0, 0.87)",
            secondary: "rgba(0, 0, 0, 0.6)",
            hint: "rgba(0, 0, 0, 0.38)",
            disabled: "rgba(0, 0, 0, 0.38)"
        },
        divider: "rgba(34, 36, 38, 0.1)",
        background: {
            paper: "rgba(34, 36, 38, 0.1)",
            default: "#FFFFFF"
        }
    },
    typography: {
        fontFamily: "Helvetica, Arial, sans-serif"
    }
});

export default theme;
