import { Model, Suite } from "../api/api";

declare global {
    interface Window {
        COVERAGE_DATA: {
            suites: Suite[];
            models: Model[];
        };
    }
}
