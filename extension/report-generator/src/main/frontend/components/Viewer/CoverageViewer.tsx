import { h } from 'preact';
import { useCallback, useEffect, useState } from 'preact/hooks';
import {
    ZoomIn, ZoomOut, Focus, Download,
} from 'lucide-preact';
import BpmnViewer, { BpmnViewerData, BpmnViewerListener } from './BpmnViewer';
import type { ParsedModel, ParsedRun } from '../../api/api';
import { downloadFile } from '../../util/FileUtils';

interface Props {
    selectedModel?: ParsedModel;
    selectedRun?: ParsedRun;
}

const CoverageViewer = ({ selectedModel, selectedRun }: Props) => {
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
        } else if (selectedModel) {
            setData({
                xml: selectedModel.xml,
                highlightFlowNodes: selectedModel.coveredNodes.map(node => node.id),
                highlightSequenceFlows: selectedModel.coveredSequenceFlows,
            });
        } else {
            setData(undefined);
        }
    }, [selectedModel, selectedRun]);

    if (!selectedModel) return null;

    return (
        <div class="max-w-screen-md w-full mt-8 mx-auto border-2 border-gray-200 bg-white/70 rounded">
            <div class="h-9 px-2 py-1 bg-gray-100 font-medium">
                Model Viewer
            </div>

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

                <div class="flex-grow" />

                <button
                    title="Zoom In"
                    onClick={() => bpmnListener?.send('ZOOM_IN')}
                    class="p-1 hover:bg-gray-200 rounded"
                >
                    <ZoomIn size={18} />
                </button>

                <button
                    title="Zoom Out"
                    onClick={() => bpmnListener?.send('ZOOM_OUT')}
                    class="p-1 hover:bg-gray-200 rounded"
                >
                    <ZoomOut size={18} />
                </button>

                <button
                    title="Reset Zoom"
                    onClick={() => bpmnListener?.send('RESET_ZOOM')}
                    class="p-1 hover:bg-gray-200 rounded"
                >
                    <Focus size={18} />
                </button>

                <button
                    title="Download BPMN"
                    onClick={download}
                    class="p-1 hover:bg-gray-200 rounded disabled:opacity-50"
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
        </div>
    );
};

export default CoverageViewer;
