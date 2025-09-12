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
import { h, render } from 'preact';
import CoverageReport from '../components/CoverageReport';
import {Model, Suite} from "../api/api";
import styles from "./index.css?inline";

export { CoverageReport };

// Custom Element Wrapper
export class CoverageReportElement extends HTMLElement {
    private _data: { suites: Suite[], models: Model[] } = { suites: [], models: [] };
    private _colors: { green: number, yellow: number } = { green: 0.9, yellow: 0.6 };

    constructor() {
        super();
        this.attachShadow({ mode: "open" });
    }

    connectedCallback() {
        if (this._data) this.render();
    }

    set data(value: any) {
        this._data = value;
        this.render();
    }

    get data() {
        return this._data;
    }

    set colors(value: any) {
        this._colors = value;
        this.render();
    }

    get colors() {
        return this._colors;
    }

    private render() {
        render(<><style>{styles}</style><CoverageReport coverageData={this._data} colors={this._colors} /></>, this.shadowRoot!);
    }

}

if (!customElements.get('coverage-report')) {
    customElements.define('coverage-report', CoverageReportElement);
}
