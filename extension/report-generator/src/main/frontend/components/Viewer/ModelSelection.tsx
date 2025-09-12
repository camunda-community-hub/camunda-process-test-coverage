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
import {h} from "preact";
import type {Model, ParsedModel} from "../../api/api";
import {generateThumbnails} from "../../util/ThumbnailGenerator";

interface Props {
    coverage: ParsedModel[];
    onModelSelected: (model: Model) => void;
    allModels: Model[];
    colors: { green: number, yellow: number };
}

const ModelSelection = ({ coverage, onModelSelected, allModels, colors }: Props) => {

    const width = allModels.length > 10 ? 130 : 260;
    const height = allModels.length > 10 ? 90 : 180;
    const thumbs = generateThumbnails(allModels, width, height);
    const coverageColor = (percent) => {
        let coverageColor: string;
        if (percent >= colors.green) coverageColor = 'bg-green-200';
        else if (percent >= colors.yellow) coverageColor = 'bg-yellow-200';
        else coverageColor = 'bg-red-200';
        return coverageColor;
    }

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
                    {thumbs[c.key] && (
                        <img
                            src={thumbs[c.key]}
                            alt={c.key}
                            class="w-full h-32 object-contain bg-white rounded-xl mb-2"
                        />
                    )}
                    <div class="text-xs opacity-70 mb-2">{c.coveredNodeCount + c.coveredSequenceFlowCount} / {c.totalElementCount} elements</div>
                    <div class="w-full bg-slate-200 rounded-full h-2 overflow-hidden">
                        <div
                            class={ coverageColor(c.coverage) + ' h-2' }
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