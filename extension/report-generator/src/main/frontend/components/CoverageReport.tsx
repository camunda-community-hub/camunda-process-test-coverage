import { h } from 'preact';
import {useEffect, useMemo, useState} from "preact/hooks";
import {computeCoverage} from "../util/ParsingUtils";
import CoverageViewer from "./Viewer/CoverageViewer";
import {Model, Suite} from "../api/api";
import ModelSelection from "./Viewer/ModelSelection";
import RunSummary from "./Viewer/RunSummary";

interface Props {
    coverageData: { suites: Suite[], models: Model[] };
    colors: { green: number, yellow: number };
    modelParam?: string | null;
}

const CoverageReport = (props: Props) => {

    const modelParam = props.modelParam;
    const coverage = useMemo(
        () => computeCoverage(props.coverageData.suites, props.coverageData.models),
        [props.coverageData]
    );
    const [allModels] = useState<Model[]>(props.coverageData.models);

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
                    <ModelSelection onModelSelected={model => setSelectedModelKey(model.key)} allModels={allModels} coverage={coverage} colors={props.colors} />
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
                                    Back To Model Selection
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
                                        }} onRunSelected={runId => setSelectedRunId(runId)} colors={props.colors} />
                    </>
                    ) : null
                }
            </div>
        </div>
    );
};

export default CoverageReport;
