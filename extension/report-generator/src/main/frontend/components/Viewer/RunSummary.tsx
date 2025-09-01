import { h } from 'preact';
import { ParsedModel, ParsedRun, ParsedSuite } from '../../api/api';
import RunSummaryRow from './RunSummaryRow';

interface Props {
    selectedModel: ParsedModel;
    selectedSuite: ParsedSuite | undefined;
    selectedRun: ParsedRun | undefined;
    onSuiteSelected: (suite: ParsedSuite) => void;
    onRunSelected: (run: ParsedRun) => void;
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
                        selected={props.selectedSuite === suite}
                        model={suite}
                        onClick={() => props.onSuiteSelected(suite)}
                    />
                    {props.selectedSuite === suite && suite.runs.map(run => (
                        <RunSummaryRow
                            type="run"
                            selected={props.selectedRun === run}
                            model={run}
                            onClick={() => props.onRunSelected(run)}
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
