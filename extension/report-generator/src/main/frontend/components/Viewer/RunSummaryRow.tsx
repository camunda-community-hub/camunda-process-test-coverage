import { h } from 'preact';
import { CheckSquare, ChevronDown, ChevronRight } from 'lucide-preact';
import type {ParsedRun, ParsedSuite} from '../../api/api';

interface Props {
    selected: boolean;
    type: 'suite' | 'run';
    model: ParsedSuite | ParsedRun;
    onClick: () => void;
}

const RunSummaryRow = ({ selected, type, model, onClick }: Props) => {
    const coverage = model.coverage;
    const covered = model.coveredNodeCount + model.coveredSequenceFlowCount;
    const total = model.totalElementCount;
    const name = model.name;

    const Icon = type === 'suite' ? (selected ? ChevronDown : ChevronRight) : CheckSquare;

    let coverageColor: string;
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
