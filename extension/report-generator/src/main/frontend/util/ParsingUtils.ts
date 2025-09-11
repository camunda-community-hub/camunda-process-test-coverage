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

export const computeCoverage = (suites: Suite[], models: Model[]): ParsedModel[] => (
    models.map(model => {

        const parsedSuites: ParsedSuite[] = suites
            .filter(suite => suite.runs.some(run => run.events.some(event => event.modelKey === model.key)))
            .map(suite => {

                const runs: ParsedRun[] = suite.runs.map(run => {
                    const events = run.events.filter(event => event.modelKey === model.key);
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
                    id: suite.id,
                    name: suite.name,
                    runs: runs,
                    coveredSequenceFlows: coveredFlows,
                    coveredSequenceFlowCount: coveredFlows.length,
                    coveredNodes: coveredNodes,
                    coveredNodeCount: coveredNodes.length,
                    totalElementCount: model.totalElementCount,
                    coverage: (coveredNodes.length + coveredFlows.length) / model.totalElementCount
                };
            });

        const coveredFlows = parsedSuites
            .flatMap(run => run.coveredSequenceFlows)
            .filter(distinct());
        const coveredNodes = parsedSuites
            .flatMap(run => run.coveredNodes)
            .filter(distinct((item: CoveredNode) => item.id));
        const totalElementCount = model.totalElementCount;

        return {
            id: model.id,
            key: model.key,
            xml: model.xml,
            suites: parsedSuites,
            coveredSequenceFlows: coveredFlows,
            coveredSequenceFlowCount: coveredFlows.length,
            coveredNodes: coveredNodes,
            coveredNodeCount: coveredNodes.length,
            totalElementCount: totalElementCount,
            coverage: (coveredNodes.length + coveredFlows.length) / totalElementCount
        };
    })
);
