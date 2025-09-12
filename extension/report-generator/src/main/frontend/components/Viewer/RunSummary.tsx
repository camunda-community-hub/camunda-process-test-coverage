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
import { ParsedModel } from '../../api/api';
import RunSummaryRow from './RunSummaryRow';

interface Props {
    selectedModel: ParsedModel;
    selectedSuiteId: string | undefined;
    selectedRunId: string | undefined;
    onSuiteSelected: (suiteId: string) => void;
    onRunSelected: (runId: string) => void;
    colors: { green: number, yellow: number };
}

const RunSummary = (props: Props) => {
    return (
        <fieldset class="border border-gray-300 rounded-md p-4">
            <legend class="px-2 text-sm font-medium text-gray-700">
                Run Selection
            </legend>
            <table className="table-fixed w-full border border-divider rounded-md overflow-hidden">
                <colgroup>
                    <col className="w-full" />
                    <col className="w-16" />
                    <col className="w-16" />
                    <col className="w-4" />
                    <col className="w-20" />
                </colgroup>

                <thead>
                    <tr className="bg-divider">
                        <th aria-hidden className="p-4" />
                        <th className="text-right">
                            <span>Covered</span>
                        </th>
                        <th className="text-right">
                            <span>Total</span>
                        </th>
                        <th aria-hidden />
                        <th className="text-right">Coverage</th>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td className="h-2" colSpan={99} />
                    </tr>

                {props.selectedModel.suites.length === 0 && (
                    <tr>
                        <td colSpan={99} className="text-center font-bold p-4">
                            This model contains no test runs.
                        </td>
                    </tr>
                )}

                {props.selectedModel.suites.map(suite => (
                    <>
                    <RunSummaryRow
                        type="suite"
                        selected={props.selectedSuiteId === suite.id && !props.selectedRunId}
                        model={suite}
                        onClick={() => props.onSuiteSelected(suite.id)}
                        colors={props.colors}
                    />
                    {props.selectedSuiteId === suite.id && suite.runs.map(run => (
                        <RunSummaryRow
                            type="run"
                            selected={props.selectedRunId === run.id}
                            model={run}
                            onClick={() => props.onRunSelected(run.id)}
                            colors={props.colors}
                        />
                    ))}
                    </>
                ))}

                <tr>
                    <td className="h-2" colSpan={99} />
                </tr>
                </tbody>
            </table>
        </fieldset>
    );
};

export default RunSummary;
