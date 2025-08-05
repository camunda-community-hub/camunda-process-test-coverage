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
        <div class="flex flex-col w-[960px] max-w-[960px] mx-auto mt-8 mb-4">
            <h1 class="text-2xl font-bold mb-0">Test Coverage Report</h1>
            <span class="mt-1 pb-2 mb-6 border-b-2 border-gray-600 text-gray-800">
        {`${window.COVERAGE_DATA.suites.length} Suites, ${window.COVERAGE_DATA.models.length} Models processed.`}
      </span>

            {selectedSuite && (
                <div class="flex flex-wrap mt-2 mb-8 gap-2">
                    <select
                        class="w-[32%] border border-gray-400 px-3 py-2 rounded"
                        value={selectedSuiteId}
                        onChange={e => setSelectedSuiteId((e.target as HTMLSelectElement).value)}
                    >
                        <option disabled selected>Select Test Suite</option>
                        {parsedSuites.map(suite => (
                            <option key={suite.id} value={suite.id}>
                                {suite.name.split('.').pop()}
                            </option>
                        ))}
                    </select>

                    {selectedModel && (
                        <>
                            <select
                                class="w-[32%] border border-gray-400 px-3 py-2 rounded"
                                value={selectedModelKey}
                                onChange={e => setSelectedModelKey((e.target as HTMLSelectElement).value)}
                            >
                                <option disabled selected>Select Model</option>
                                {selectedSuite.models.map(model => (
                                    <option key={model.key} value={model.key}>
                                        {model.key}
                                    </option>
                                ))}
                            </select>

                            <select
                                class="w-[32%] border border-gray-400 px-3 py-2 rounded"
                                value={selectedRunId ?? ''}
                                onChange={e => {
                                    const val = (e.target as HTMLSelectElement).value;
                                    setSelectedRunId(val === '' ? undefined : val);
                                }}
                            >
                                <option value="">None</option>
                                {selectedModel.runs.map(run => (
                                    <option key={run.id} value={run.id}>
                                        {run.name}
                                    </option>
                                ))}
                            </select>
                        </>
                    )}
                </div>
            )}

            {!selectedSuite && (
                <>
                    <div class="flex flex-wrap gap-3 mb-4">
                        {parsedSuites.map(suite => (
                            <div
                                key={suite.id}
                                onClick={() => setSelectedSuiteId(suite.id)}
                                class={`flex flex-col px-4 py-2 border-2 border-green-800 rounded cursor-pointer w-[23%] ${
                                    selectedSuiteId === suite.id ? 'bg-green-800 text-white' : 'text-black'
                                }`}
                            >
                                <span class="font-bold truncate">{suite.name.split('.').pop()}</span>
                                <span class="text-sm">{(suite.coverage * 100).toFixed(2)}% Coverage</span>
                            </div>
                        ))}
                    </div>
                    <div class="text-center text-xl font-bold mt-4 mb-4">
                        Please select a test suite to see details.
                    </div>
                </>
            )}

            {selectedSuite && !selectedModel && (
                <>
                    <div class="flex flex-wrap gap-3 mb-4">
                        {selectedSuite.models.map(model => (
                            <div
                                key={model.key}
                                onClick={() => setSelectedModelKey(model.key)}
                                class={`flex flex-col px-4 py-2 border-2 border-green-800 rounded cursor-pointer w-[23%] ${
                                    selectedModelKey === model.key ? 'bg-green-800 text-white' : 'text-black'
                                }`}
                            >
                                <span class="font-bold truncate">{model.key}</span>
                                <span class="text-sm">{`Coverage: ${(model.coverage * 100).toFixed(2)}%`}</span>
                            </div>
                        ))}
                    </div>
                    <div class="text-center text-xl font-bold mt-4 mb-4">
                        Please select a model to see details.
                    </div>
                </>
            )}

            {selectedSuite && selectedModel && !selectedRun && (
                <>
                    <div class="font-bold text-base mb-2">Select a run to see details for that run.</div>
                    <div class="flex flex-wrap gap-3 mb-4">
                        {selectedModel.runs.map(run => (
                            <div
                                key={run.id}
                                onClick={() => setSelectedRunId(run.id)}
                                class={`flex flex-col px-4 py-2 border-2 border-green-800 rounded cursor-pointer w-[23%] ${
                                    selectedRunId === run.id ? 'bg-green-800 text-white' : 'text-black'
                                }`}
                            >
                                <span class="font-bold truncate">{run.name}</span>
                                <span class="text-sm">{`Coverage: ${(run.coverage * 100).toFixed(2)}%`}</span>
                            </div>
                        ))}
                    </div>
                </>
            )}

            {selectedSuite && selectedModel && (
                <CoverageViewer selectedModel={selectedModel} selectedRun={selectedRun} />
            )}

            {selectedSuite && (
                <RunSummary
                    selectedSuite={selectedSuite}
                    selectedModel={selectedModel}
                    selectedRun={selectedRun}
                    onModelSelected={model => setSelectedModelKey(model.key)}
                    onRunSelected={run => setSelectedRunId(run.id)}
                />
            )}
        </div>
    );
};

export default ViewerContainer;
