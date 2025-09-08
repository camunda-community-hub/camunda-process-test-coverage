import {h} from 'preact';
import {useMemo} from "preact/hooks";
import CoverageReport from "./CoverageReport";

const App = () => {

    const modelParam = useMemo(() => new URLSearchParams(globalThis.location?.search ?? '').get('model'), []);

    return (
        <CoverageReport coverageData={window.COVERAGE_DATA} colors={window.COLORS ?? { green: 0.9, yellow: 0.5}} modelParam={modelParam} />
    );
};

export default App;