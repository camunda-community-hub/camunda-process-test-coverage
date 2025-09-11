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
