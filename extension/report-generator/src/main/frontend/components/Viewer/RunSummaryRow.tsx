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
import { CheckSquare, ChevronDown, ChevronRight } from 'lucide-preact';
import type {ParsedRun, ParsedSuite} from '../../api/api';

interface Props {
    selected: boolean;
    type: 'suite' | 'run';
    model: ParsedSuite | ParsedRun;
    onClick: () => void;
    colors: { green: number, yellow: number };
}

const RunSummaryRow = ({ selected, type, model, onClick, colors }: Props) => {
    const coverage = model.coverage;
    const covered = model.coveredNodeCount + model.coveredSequenceFlowCount;
    const total = model.totalElementCount;
    const name = model.name;

    const Icon = type === 'suite' ? (selected ? ChevronDown : ChevronRight) : CheckSquare;

    let coverageColor: string;
    if (coverage >= colors.green) coverageColor = 'bg-green-200';
    else if (coverage >= colors.yellow) coverageColor = 'bg-yellow-200';
    else coverageColor = 'bg-red-200';

    return (
        <tr
            onClick={onClick}
            class={`cursor-pointer transition-colors ${
                selected ? 'bg-gray-300 font-semibold' : 'hover:bg-gray-100'
            }`}
        >
            <td
                class={`text-left truncate py-2 ${
                    type === 'run' ? 'pl-8' : 'pl-0'
                }`}
            >
                <div class="flex items-center overflow-hidden">
                    <Icon class="w-4 h-4 mr-2 text-gray-600 shrink-0" />
                    <span className="truncate">{name}</span>
                </div>
            </td>

            <td class="text-right">{covered}</td>
            <td class="text-right">{total}</td>
            <td />
            <td class={`text-right ${coverageColor} whitespace-nowrap`}>
                {(coverage * 100).toFixed(2)}%
            </td>
        </tr>
    );
};

export default RunSummaryRow;
