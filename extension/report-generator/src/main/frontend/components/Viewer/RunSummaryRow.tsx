import { h } from 'preact';
import { Code2, CheckSquare } from 'lucide-preact';
import type { ParsedModel, ParsedRun } from '../../api/api';

interface Props {
    selected: boolean;
    type: 'model' | 'run';
    model: ParsedModel | ParsedRun;
    onClick: () => void;
}

const RunSummaryRow = ({ selected, type, model, onClick }: Props) => {
    const coverage = model.coverage;
    const covered = model.coveredNodeCount + model.coveredSequenceFlowCount;
    const total = model.totalElementCount;
    const name = type === 'model' ? (model as ParsedModel).key : (model as ParsedRun).name;

    const Icon = type === 'model' ? Code2 : CheckSquare;

    let coverageColor = '';
    if (coverage >= 0.9) coverageColor = 'bg-green-200';
    else if (coverage >= 0.5) coverageColor = 'bg-yellow-200';
    else coverageColor = 'bg-red-200';

    return (
        <tr
            onClick={onClick}
            class={`cursor-pointer transition-colors ${
                selected ? 'bg-gray-300 font-semibold' : 'hover:bg-gray-100'
            }`}
        >
            <td class={`text-left py-2 pl-${type === 'run' ? '11' : '4'}`}>
                <div class="flex items-center">
                    <Icon class="w-4 h-4 mr-2 text-gray-600" />
                    {name}
                </div>
            </td>
            <td class="text-right py-2">{covered}</td>
            <td class="text-right py-2">{total}</td>
            <td class="w-12" />
            <td class={`text-right py-2 px-2 ${coverageColor}`}>
                {(coverage * 100).toFixed(2)}%
            </td>
        </tr>
    );
};

export default RunSummaryRow;
