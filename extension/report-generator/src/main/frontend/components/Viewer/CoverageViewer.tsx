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
import { useCallback, useEffect, useState } from 'preact/hooks';
import {
    ZoomIn, ZoomOut, Focus, Download,
} from 'lucide-preact';
import BpmnViewer, { BpmnViewerData, BpmnViewerListener } from './BpmnViewer';
import type {ParsedModel, ParsedRun, ParsedSuite} from '../../api/api';
import { downloadFile } from '../../util/FileUtils';

interface Props {
    selectedModel?: ParsedModel;
    selectedSuite?: ParsedSuite;
    selectedRun?: ParsedRun;
}

const CoverageViewer = ({ selectedModel, selectedSuite, selectedRun }: Props) => {
    const [data, setData] = useState<BpmnViewerData | undefined>(undefined);
    const [bpmnListener, setBpmnListener] = useState<BpmnViewerListener | undefined>(undefined);
    const [showCoverage, setShowCoverage] = useState(true);
    const [showTransactionBoundaries, setShowTransactionBoundaries] = useState(false);
    const [showExpressions, setShowExpressions] = useState(false);

    const download = useCallback(() => {
        if (selectedModel) {
            downloadFile(`${selectedModel.key}.bpmn`, selectedModel.xml);
        }
    }, [selectedModel]);

    useEffect(() => {
        if (selectedRun && selectedModel) {
            setData({
                xml: selectedModel.xml,
                highlightFlowNodes: selectedRun.coveredNodes.map(node => node.id),
                highlightSequenceFlows: selectedRun.coveredSequenceFlows,
            });
        } else if (selectedSuite && selectedModel) {
            setData({
                xml: selectedModel.xml,
                highlightFlowNodes: selectedSuite.runs.flatMap(run => run.coveredNodes).map(node => node.id),
                highlightSequenceFlows: selectedSuite.runs.flatMap(run => run.coveredSequenceFlows),
            });
        } else if (selectedModel) {
            setData({
                xml: selectedModel.xml,
                highlightFlowNodes: selectedModel.coveredNodes.map(node => node.id),
                highlightSequenceFlows: selectedModel.coveredSequenceFlows,
            });
        } else {
            setData(undefined);
        }
    }, [selectedModel, selectedSuite, selectedRun]);

    if (!selectedModel) return null;

    return (
        <fieldset class="border border-gray-300 rounded-md p-4">
            <legend class="px-2 text-sm font-medium text-gray-700">
                BPMN Coverage Viewer
            </legend>
            <div class="flex items-center px-4 py-2 space-x-3 text-sm">
                <label class="flex items-center space-x-1 text-gray-600">
                    <input
                        type="checkbox"
                        class="accent-blue-500"
                        checked={showCoverage}
                        onChange={e => setShowCoverage(e.currentTarget.checked)}
                    />
                    <span>Show Coverage</span>
                </label>

                <label class="flex items-center space-x-1 text-gray-600">
                    <input
                        type="checkbox"
                        class="accent-blue-500"
                        checked={showTransactionBoundaries}
                        onChange={e => setShowTransactionBoundaries(e.currentTarget.checked)}
                    />
                    <span>Show Transaction Boundaries</span>
                </label>

                <label class="flex items-center space-x-1 text-gray-600">
                    <input
                        type="checkbox"
                        class="accent-blue-500"
                        checked={showExpressions}
                        onChange={e => setShowExpressions(e.currentTarget.checked)}
                    />
                    <span>Show Expressions</span>
                </label>

                <div class="grow" />

                <button
                    title="Zoom In"
                    onClick={() => bpmnListener?.send('ZOOM_IN')}
                    class="p-1 hover:bg-gray-200 rounded-sm"
                >
                    <ZoomIn size={18} />
                </button>

                <button
                    title="Zoom Out"
                    onClick={() => bpmnListener?.send('ZOOM_OUT')}
                    class="p-1 hover:bg-gray-200 rounded-sm"
                >
                    <ZoomOut size={18} />
                </button>

                <button
                    title="Reset Zoom"
                    onClick={() => bpmnListener?.send('RESET_ZOOM')}
                    class="p-1 hover:bg-gray-200 rounded-sm"
                >
                    <Focus size={18} />
                </button>

                <button
                    title="Download BPMN"
                    onClick={download}
                    class="p-1 hover:bg-gray-200 rounded-sm disabled:opacity-50"
                    disabled={!selectedModel}
                >
                    <Download size={18} />
                </button>
            </div>

            <div class="relative h-[640px]">
                <BpmnViewer
                    className="absolute inset-0"
                    showCoverage={showCoverage}
                    showExpressions={showExpressions}
                    showTransactionBoundaries={showTransactionBoundaries}
                    setListener={setBpmnListener}
                    data={data}
                />
            </div>
        </fieldset>
    );
};

export default CoverageViewer;
