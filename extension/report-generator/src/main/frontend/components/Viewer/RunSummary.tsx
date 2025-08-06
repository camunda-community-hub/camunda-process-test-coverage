import { h } from 'preact';
import { ParsedModel, ParsedRun, ParsedSuite } from '../../api/api';
import Section from '../Util/Section';
import Tooltip from '../Util/Tooltip';
import RunSummaryRow from './RunSummaryRow';

interface Props {
    selectedSuite: ParsedSuite;
    selectedModel: ParsedModel | undefined;
    selectedRun: ParsedRun | undefined;
    onModelSelected: (model: ParsedModel) => void;
    onRunSelected: (run: ParsedRun) => void;
}

const RunSummary = (props: Props) => {
    return (
        <Section title="Build Summary">
            <table className="table-auto w-full border border-divider rounded-md overflow-hidden">
                <thead>
                <tr className="bg-divider">
                    <th aria-hidden />
                    <th className="text-right py-4 pl-4">
                        <Tooltip title="Covered Flow Nodes & Sequence Flows">
                            <span>Covered</span>
                        </Tooltip>
                    </th>
                    <th className="text-right py-4 pr-4">
                        <Tooltip title="Total Flow Nodes & Sequence Flows">
                            <span>Total</span>
                        </Tooltip>
                    </th>
                    <th className="w-12" aria-hidden />
                    <th className="text-right w-1/12 pl-4">Coverage</th>
                </tr>
                </thead>

                <tbody>
                <tr>
                    <td className="h-2" colSpan={99} />
                </tr>

                {props.selectedSuite.models.length === 0 && (
                    <tr>
                        <td colSpan={99} className="text-center font-bold p-4">
                            This suite contains no models.
                        </td>
                    </tr>
                )}

                {props.selectedSuite.models.map(model => (
                    <tbody key={model.key}>
                    <RunSummaryRow
                        type="model"
                        selected={props.selectedModel === model}
                        model={model}
                        onClick={() => props.onModelSelected(model)}
                    />
                    {props.selectedModel === model &&
                        model.runs.map(run => (
                            <RunSummaryRow
                                type="run"
                                selected={props.selectedRun === run}
                                model={run}
                                onClick={() => props.onRunSelected(run)}
                            />
                        ))}
                    </tbody>
                ))}

                <tr>
                    <td className="h-2" colSpan={99} />
                </tr>
                </tbody>
            </table>
        </Section>
    );
};

export default RunSummary;
