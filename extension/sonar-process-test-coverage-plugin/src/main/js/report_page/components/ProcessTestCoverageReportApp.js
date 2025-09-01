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

import React from "react";
import { getJSON } from "sonar-request";
import html from '../../../../../target/classes/index.html';

export function isBranch(branchLike) {
    return branchLike !== undefined && branchLike.isMain !== undefined;
}

export function isPullRequest(branchLike) {
    return branchLike !== undefined && branchLike.key !== undefined;
}

export function loadProcessTestCoverageReport(options) {
    var request = {
        component : options.component.key,
        metricKeys : "process_test_coverage_report"
    };

    // branch and pullRequest are internal parameters for /api/measures/component
    if (isBranch(options.branchLike)) {
        request.branch = options.branchLike.name;
    } else if (isPullRequest(options.branchLike)) {
        request.pullRequest = options.branchLike.key;
    }

    return getJSON("/api/measures/component", request).then(function(response) {
        var report = response.component.measures.find((measure) => {
            return measure.metric === "process_test_coverage_report";
        });
        if (typeof report  === "undefined") {
            return "<div style='text-align: center'><h2>No JSON-Report found for process test coverage</h2></div>";
        } else {
            return report.value;
        }
    });
}

export default class ProcessTestCoverageReportApp extends React.PureComponent {
    constructor() {
        super();
        this.state = {
            loading: true,
            data: "",
            height: 0,
        };
    }


    componentDidMount() {
        // eslint-disable-next-line react/prop-types
        loadProcessTestCoverageReport(this.props.options).then((data) => {
            this.setState({
                loading: false,
                data
            });
        });
        /**
         * Add event listener
         */
        this.updateDimensions();
        window.addEventListener("resize", this.updateDimensions.bind(this));
    }

    /**
     * Remove event listener
     */
    componentWillUnmount() {
        window.removeEventListener("resize", this.updateDimensions.bind(this));
    }

    updateDimensions() {
        // 72px SonarQube common pane
        // 72px SonarQube project pane
        // 145,5 SonarQube footer
        let updateHeight = window.innerHeight - (72 + 48 + 145.5);
        this.setState({ height: updateHeight });
    }

    render() {
        if (this.state.loading) {
            return (
                <div className="page page-limited">
                    Loading...
                </div>
            );
        }

        return (
            <div className="page process-test-coverage-report-container" >
                <iframe classsandbox="allow-scripts allow-same-origin" height={this.state.height} srcDoc={html.replace('{{__REPORT_JSON_PLACEHOLDER__}}', this.state.data)} style={{border: "none"}}/>
            </div>
        );
    }
}
