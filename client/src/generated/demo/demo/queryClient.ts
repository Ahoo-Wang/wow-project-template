import { QueryClientFactory, QueryClientOptions, ResourceAttributionPathSpec } from "@ahoo-wang/fetcher-wow";
import { DemoAggregatedFields, DemoCreated, DemoState, DemoUpdated } from "./types";
import { DEMO_BOUNDED_CONTEXT_ALIAS } from "../boundedContext";

const DEFAULT_QUERY_CLIENT_OPTIONS: QueryClientOptions = {
    contextAlias: DEMO_BOUNDED_CONTEXT_ALIAS,
    aggregateName: 'demo',
    resourceAttribution: ResourceAttributionPathSpec.TENANT,
};

export enum DemoDomainEventTypeMapTitle {
    demo_created = 'demo_created',
    demo_updated = 'demo_updated'
}

export type DemoDomainEventType = DemoCreated | DemoUpdated;

export const demoQueryClientFactory = new QueryClientFactory<DemoState, DemoAggregatedFields | string, DemoDomainEventType>(DEFAULT_QUERY_CLIENT_OPTIONS);
