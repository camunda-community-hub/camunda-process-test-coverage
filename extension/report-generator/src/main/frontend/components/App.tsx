import { h } from 'preact';
import {useEffect, useMemo, useState} from "preact/hooks";
import {computeCoverage} from "../util/ParsingUtils";
import CoverageViewer from "./Viewer/CoverageViewer";
import {Model} from "../api/api";
import ModelSelection from "./Viewer/ModelSelection";
import RunSummary from "./Viewer/RunSummary";

const App = () => {

    const modelParam = useMemo(() => new URLSearchParams(globalThis.location?.search ?? '').get('model'), []);
    const coverage = useMemo(
        () => computeCoverage(window.COVERAGE_DATA.suites, window.COVERAGE_DATA.models),
        [window.COVERAGE_DATA]
    );
    const [allModels] = useState<Model[]>(window.COVERAGE_DATA.models);

    const [selectedModelKey, setSelectedModelKey] = useState<string | null>(() => {
        if (modelParam && allModels.some((m) => m.key === modelParam)) return modelParam;
        if (allModels.length === 1) return allModels[0].key;
        return null;
    });
    const selectedModel = useMemo(() => coverage.find((c) => c.key === selectedModelKey) || null, [coverage, selectedModelKey]);

    const [selectedSuiteId, setSelectedSuiteId] = useState<string | undefined>();
    const selectedSuite = useMemo(
        () => selectedModel?.suites.find(suite => suite.id === selectedSuiteId),
        [selectedModel, selectedSuiteId]
    );

    const [selectedRunId, setSelectedRunId] = useState<string | undefined>();
    const selectedRun = useMemo(
        () => selectedSuite?.runs.find(run => run.id === selectedRunId),
        [selectedSuite, selectedRunId]
    );

    useEffect(() => {
        if (modelParam && allModels.some((m) => m.key === modelParam)) {
            setSelectedModelKey(modelParam);
        }
    }, [modelParam, allModels]);
    useEffect(() => {
        setSelectedRunId(undefined);
    }, [selectedSuiteId]);
    useEffect(() => {
        setSelectedSuiteId(undefined);
        setSelectedRunId(undefined);
    }, [selectedModelKey]);

    return (
        <div class="min-h-screen w-full bg-slate-50 text-slate-900 p-6">
            <div class="max-w-5xl mx-auto space-y-6">

                {!selectedModel ?
                    <ModelSelection onModelSelected={model => setSelectedModelKey(model.key)} allModels={allModels} coverage={coverage} />
                    : null
                }

                {selectedModel ? (
                    <>
                        <header class="flex items-center justify-between">
                            {allModels.length > 1 && !modelParam ? (
                                <a
                                    class="text-xs underline opacity-70 hover:opacity-100"
                                    href={typeof location !== 'undefined' ? `${location.pathname}` : '#'}
                                >
                                    Reset Selection
                                </a>
                            ) : null}
                        </header>
                        <CoverageViewer selectedModel={selectedModel} selectedSuite={selectedSuite} selectedRun={selectedRun} />
                        <RunSummary selectedModel={selectedModel} selectedSuiteId={selectedSuiteId} selectedRunId={selectedRunId}
                                        onSuiteSelected={suiteId => {
                                            if (suiteId === selectedSuiteId) {
                                                setSelectedRunId(undefined);
                                            } else {
                                                setSelectedSuiteId(suiteId);
                                            }
                                        }} onRunSelected={runId => setSelectedRunId(runId)} />
                    </>
                    ) : null
                }
            </div>
        </div>
    );
};

export default App;