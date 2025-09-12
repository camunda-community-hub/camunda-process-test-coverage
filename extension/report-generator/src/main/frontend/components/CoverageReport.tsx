/*-
 * #%L
 * Camunda Process Test Coverage Sonar Plugin
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
    const allModels = props.coverageData.models;

    const [selectedModelKey, setSelectedModelKey] = useState<string | null>(null);
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
        if (allModels.length === 1) {
            setSelectedModelKey(allModels[0].key)
        }
    }, [allModels, modelParam]);
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
