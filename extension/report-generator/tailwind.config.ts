/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./src/main/frontend/index.html",
        "./src/main/frontend/**/*.{js,ts,jsx,tsx}"
    ],
    theme: {
        extend: {
            colors: {
                primary: {
                    light: "#32d35c",
                    DEFAULT: "#26b44b",
                    dark: "#3d9a4c",
                    contrast: "#ffffff"
                },
                secondary: {
                    light: "#455a64",
                    DEFAULT: "#37474f",
                    dark: "#263238",
                    contrast: "#ffffff"
                },
                text: {
                    primary: "rgba(0,0,0,0.87)",
                    secondary: "rgba(0,0,0,0.6)",
                    hint: "rgba(0,0,0,0.38)",
                    disabled: "rgba(0,0,0,0.38)"
                },
                divider: "rgba(34,36,38,0.1)",
                background: {
                    paper: "rgba(34,36,38,0.1)",
                    DEFAULT: "#ffffff"
                }
            },
            fontFamily: {
                sans: ["Helvetica", "Arial", "sans-serif"]
            }
        }
    },
    plugins: []
};
