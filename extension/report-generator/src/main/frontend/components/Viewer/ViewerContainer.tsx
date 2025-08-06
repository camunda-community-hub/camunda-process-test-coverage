import { h } from 'preact';
import { useEffect, useMemo, useState } from 'preact/hooks';
import { parseSuites } from '../../util/ParsingUtils';
import CoverageViewer from './CoverageViewer';
import RunSummary from './RunSummary';

const ViewerContainer = () => {
    const [selectedModelKey, setSelectedModelKey] = useState<string | undefined>();
    const [selectedSuiteId, setSelectedSuiteId] = useState<string | undefined>();
    const [selectedRunId, setSelectedRunId] = useState<string | undefined>();

    const parsedSuites = useMemo(
        () => parseSuites(window.COVERAGE_DATA.suites, window.COVERAGE_DATA.models),
        [window.COVERAGE_DATA]
    );

    const selectedSuite = useMemo(
        () => parsedSuites.find(s => s.id === selectedSuiteId),
        [parsedSuites, selectedSuiteId]
    );

    const selectedModel = useMemo(
        () => selectedSuite?.models.find(m => m.key === selectedModelKey),
        [selectedSuite, selectedModelKey]
    );

    const selectedRun = useMemo(
        () => selectedModel?.runs.find(r => r.id === selectedRunId),
        [selectedModel, selectedRunId]
    );

    useEffect(() => {
        setSelectedModelKey(undefined);
        setSelectedRunId(undefined);
    }, [selectedSuiteId]);

    useEffect(() => {
        setSelectedRunId(undefined);
    }, [selectedModelKey]);

    return (
        <div class="flex flex-col max-w-6xl w-full mx-auto px-4 mt-8 mb-6">
            <h1 class="text-2xl font-bold text-text-primary">Test Coverage Report</h1>
            <p class="text-text-secondary mb-4 text-sm">
                {`${window.COVERAGE_DATA.suites.length} Suites, ${window.COVERAGE_DATA.models.length} Models processed.`}
            </p>

            {/* Suite selection */}
            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 mb-6">
                <select
                    class="border border-divider rounded px-3 py-2 bg-white text-sm"
                    value={selectedSuiteId ?? ''}
                    onChange={e => setSelectedSuiteId((e.target as HTMLSelectElement).value)}
                >
                    <option disabled value="">Select Test Suite</option>
                    {parsedSuites.map(suite => (
                        <option key={suite.id} value={suite.id}>
                            {suite.name.split('.').pop()}
                        </option>
                    ))}
                </select>

                {selectedSuite && (
                    <>
                        <select
                            class="border border-divider rounded px-3 py-2 bg-white text-sm"
                            value={selectedModelKey ?? ''}
                            onChange={e => setSelectedModelKey((e.target as HTMLSelectElement).value)}
                        >
                            <option disabled value="">Select Model</option>
                            {selectedSuite.models.map(model => (
                                <option key={model.key} value={model.key}>
                                    {model.key}
                                </option>
                            ))}
                        </select>

                        <select
                            class="border border-divider rounded px-3 py-2 bg-white text-sm"
                            value={selectedRunId ?? ''}
                            onChange={e => {
                                const val = (e.target as HTMLSelectElement).value;
                                setSelectedRunId(val === '' ? undefined : val);
                            }}
                        >
                            <option value="">None</option>
                            {selectedModel?.runs.map(run => (
                                <option key={run.id} value={run.id}>
                                    {run.name}
                                </option>
                            ))}
                        </select>
                    </>
                )}
            </div>

            {/* Suite Cards */}
            {!selectedSuite && (
                <>
                    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                        {parsedSuites.map(suite => (
                            <div
                                key={suite.id}
                                onClick={() => setSelectedSuiteId(suite.id)}
                                class={`cursor-pointer border-2 rounded px-4 py-2 transition ${
                                    selectedSuiteId === suite.id ? 'bg-primary text-white border-primary' : 'border-primary text-black hover:bg-primary-light hover:text-white'
                                }`}
                            >
                                <div class="font-bold truncate">{suite.name.split('.').pop()}</div>
                                <div class="text-sm">{(suite.coverage * 100).toFixed(2)}% Coverage</div>
                            </div>
                        ))}
                    </div>
                    <p class="text-center text-text-secondary text-lg">Please select a test suite to see details.</p>
                </>
            )}

            {/* Model Cards */}
            {selectedSuite && !selectedModel && (
                <>
                    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                        {selectedSuite.models.map(model => (
                            <div
                                key={model.key}
                                onClick={() => setSelectedModelKey(model.key)}
                                class={`cursor-pointer border-2 rounded px-4 py-2 transition ${
                                    selectedModelKey === model.key ? 'bg-primary text-white border-primary' : 'border-primary text-black hover:bg-primary-light hover:text-white'
                                }`}
                            >
                                <div class="font-bold truncate">{model.key}</div>
                                <div class="text-sm">Coverage: {(model.coverage * 100).toFixed(2)}%</div>
                            </div>
                        ))}
                    </div>
                    <p class="text-center text-text-secondary text-lg">Please select a model to see details.</p>
                </>
            )}

            {/* Run Cards */}
            {selectedSuite && selectedModel && !selectedRun && (
                <>
                    <p class="text-base font-semibold mb-2 text-text-primary">Select a run to see details for that run.</p>
                    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                        {selectedModel.runs.map(run => (
                            <div
                                key={run.id}
                                onClick={() => setSelectedRunId(run.id)}
                                class={`cursor-pointer border-2 rounded px-4 py-2 transition ${
                                    selectedRunId === run.id ? 'bg-primary text-white border-primary' : 'border-primary text-black hover:bg-primary-light hover:text-white'
                                }`}
                            >
                                <div class="font-bold truncate">{run.name}</div>
                                <div class="text-sm">Coverage: {(run.coverage * 100).toFixed(2)}%</div>
                            </div>
                        ))}
                    </div>
                </>
            )}

            {/* Viewer */}
            {selectedSuite && selectedModel && (
                <div class="bg-background-paper rounded border border-divider shadow-sm p-4 mb-6 overflow-x-auto">
                    <CoverageViewer selectedModel={selectedModel} selectedRun={selectedRun} />
                </div>
            )}

            {/* Summary */}
            {selectedSuite && (
                <div class="bg-background-paper rounded border border-divider shadow-sm p-4">
                    <RunSummary
                        selectedSuite={selectedSuite}
                        selectedModel={selectedModel}
                        selectedRun={selectedRun}
                        onModelSelected={model => setSelectedModelKey(model.key)}
                        onRunSelected={run => setSelectedRunId(run.id)}
                    />
                </div>
            )}
        </div>
    );
};

export default ViewerContainer;
