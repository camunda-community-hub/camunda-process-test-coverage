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
import BpmnJS from 'bpmn-js/lib/Viewer';
import {Model} from "../api/api";
import {useEffect, useState} from "preact/hooks";

export function generateThumbnails(models: Model[], width = 300, height = 200) {
    const [thumbs, setThumbs] = useState<Record<string, string>>({});

    useEffect(() => {
        let cancelled = false;

        async function run() {
            if (typeof document === 'undefined' || !models?.length) return;

            // create offscreen container
            const container = document.createElement('div');
            Object.assign(container.style, {
                position: 'fixed',
                left: '-10000px',
                top: '0',
                width: width + 'px',
                height: height + 'px',
                overflow: 'hidden',
                visibility: 'hidden',
                pointerEvents: 'none',
                background: 'white'
            } as CSSStyleDeclaration);
            document.body.appendChild(container);

            const viewer = new BpmnJS({ container, width, height });
            const map: Record<string, string> = {};

            try {
                for (const m of models) {
                    try {
                        await viewer.importXML(m.xml);
                        const canvas = (viewer as any).get('canvas');
                        // adjust to box
                        canvas.zoom('fit-viewport', 'auto');
                        // wait for rendering
                        await new Promise((r) => requestAnimationFrame(() => r(null)));

                        const { svg } = await (viewer as any).saveSVG({ format: true });
                        // data url without need for base64 encoding
                        map[m.key] = 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svg);
                        if (cancelled) break;
                    } catch (e) {
                        // fallback: no thumbnail
                        map[m.key] = '';
                        console.warn('Not able to create thumbnail for ', m.key, e);
                    }
                }
            } finally {
                try { (viewer as any).destroy(); } catch {}
                container.remove();
            }

            if (!cancelled) setThumbs(map);
        }

        run();
        return () => { cancelled = true; };
    }, [models, width, height]);

    return thumbs;
}