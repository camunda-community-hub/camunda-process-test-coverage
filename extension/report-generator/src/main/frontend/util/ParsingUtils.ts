import {
    CoveredNode,
    Event,
    EventType,
    Model,
    ParsedModel,
    ParsedRun,
    ParsedSuite,
    Suite
} from "../api/api";
import distinct from "./ArrayUtils";

export const getDistinctIds = (events: Event[], type: EventType): string[] => (
    events
        .filter(event => event.type === type)
        .map(event => event.definitionKey)
        .filter(distinct())
);

export const parseSuites = (suites: Suite[], models: Model[]): ParsedSuite[] => (
    suites.map(suite => {
        const modelKeys = suite.runs
            .flatMap(run => run.events)
            .map(event => event.modelKey)
            .filter(distinct());

        const parsedModels: ParsedModel[] = modelKeys
            .map(modelKey => {
                const model = models.find(m => m.key === modelKey);

                if (!model) {
                    throw new Error(`Could not find required model with key ${modelKey}`);
                }

                const runs: ParsedRun[] = suite.runs.map(run => {
                    const events = run.events.filter(event => event.modelKey === modelKey);
                    const coveredFlows = getDistinctIds(events, "TAKE");
                    const startedNodes = getDistinctIds(events, "START");
                    const endedNodes = getDistinctIds(events, "END");
                    const coveredNodes: CoveredNode[] = startedNodes.map(id => ({
                        id: id,
                        ended: endedNodes.indexOf(id) !== -1
                    }));

                    return {
                        id: run.id,
                        name: run.name,
                        totalElementCount: model.totalElementCount,
                        coveredNodes: coveredNodes,
                        coveredNodeCount: coveredNodes.length,
                        coveredSequenceFlows: coveredFlows,
                        coveredSequenceFlowCount: coveredFlows.length,
                        coverage: (coveredNodes.length + coveredFlows.length)
                            / model.totalElementCount
                    };
                });

                const coveredFlows = runs
                    .flatMap(run => run.coveredSequenceFlows)
                    .filter(distinct());
                const coveredNodes = runs
                    .flatMap(run => run.coveredNodes)
                    .filter(distinct((item: CoveredNode) => item.id));

                return {
                    id: model.id,
                    key: model.key,
                    xml: model.xml,
                    runs: runs,
                    coveredSequenceFlows: coveredFlows,
                    coveredSequenceFlowCount: coveredFlows.length,
                    coveredNodes: coveredNodes,
                    coveredNodeCount: coveredNodes.length,
                    totalElementCount: model.totalElementCount,
                    coverage: (coveredNodes.length + coveredFlows.length) / model.totalElementCount
                };
            });

        const coveredFlows = parsedModels
            .flatMap(run => run.coveredSequenceFlows)
            .filter(distinct());
        const coveredNodes = parsedModels
            .flatMap(run => run.coveredNodes)
            .filter(distinct((item: CoveredNode) => item.id));
        const totalElementCount = parsedModels.reduce(
            (prev, cur) => prev + cur.totalElementCount, 0
        );

        return {
            id: suite.id,
            name: suite.name,
            models: parsedModels,
            coveredSequenceFlows: coveredFlows,
            coveredSequenceFlowCount: coveredFlows.length,
            coveredNodes: coveredNodes,
            coveredNodeCount: coveredNodes.length,
            totalElementCount: totalElementCount,
            coverage: (coveredNodes.length + coveredFlows.length) / totalElementCount
        };
    })
);
