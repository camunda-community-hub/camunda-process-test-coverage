/* eslint-disable */

declare module "bpmn-js/lib/NavigatedViewer" {
    export default class NavigatedViewer {
        constructor(options: any);
        get(param: any): any;
        importXML(xml: string): Promise<any>;
        saveSVG(): Promise<{svg: string}>;
    }
}
