import {h} from "preact";
import type {Model, ParsedModel} from "../../api/api";
import {generateThumbnails} from "../../util/ThumbnailGenerator";

interface Props {
    coverage: ParsedModel[];
    onModelSelected: (model: Model) => void;
    allModels: Model[];
}

const ModelSelection = ({ coverage, onModelSelected, allModels }: Props) => {

    const thumbs = generateThumbnails(allModels, 260, 180);

    return (<fieldset class="border border-gray-300 rounded-md p-4">
        <legend class="px-2 text-sm font-medium text-gray-700">
            Model Selection
        </legend>
        <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-3">
            {coverage.map((c) => (
                <button
                    key={c.key}
                    onClick={() => onModelSelected(c)}
                    class={
                        'text-left rounded-2xl border p-4 shadow-sm hover:shadow transition border-slate-200'
                    }
                >
                    <div class="text-sm font-semibold mb-1">{c.key}</div>
                    <img
                        src={thumbs[c.key]}
                        alt={c.key}
                        class="w-full h-32 object-contain bg-white rounded-xl mb-2"
                    />
                    <div class="text-xs opacity-70 mb-2">{c.coveredNodeCount + c.coveredSequenceFlowCount} / {c.totalElementCount} elements</div>
                    <div class="w-full bg-slate-200 rounded-full h-2 overflow-hidden">
                        <div
                            class="bg-blue-500 h-2"
                            style={{ width: `${Math.min(100, Math.max(0, c.coverage * 100)).toFixed(2)}%` }}
                        />
                    </div>
                    <div class="text-xs mt-1">{(c.coverage * 100).toFixed(2)}%</div>
                </button>
            ))}
        </div>
    </fieldset>);

}

export default ModelSelection;