/** - key: demo.ApiVersion */
export enum ApiVersion {
    V2 = 'V2',
    V3 = 'V3'
}

/** - key: demo.Link */
export interface Link {
    href: string;
    templated: boolean;
}

/** - key: demo.SecurityContext */
export type SecurityContext = Record<string, any>;
/** - key: demo.StringLinkMap */
export type StringLinkMap = Record<string, Link>;
/** - key: demo.StringObjectMap */
export type StringObjectMap = Record<string, any>;

/** - key: demo.WebServerNamespace */
export interface WebServerNamespace {
    value: string;
}
