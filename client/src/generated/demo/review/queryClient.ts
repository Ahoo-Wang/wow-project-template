import type { QueryClientOptions} from "@ahoo-wang/fetcher-wow";
import { QueryClientFactory, ResourceAttributionPathSpec } from "@ahoo-wang/fetcher-wow";
import type { ReviewAggregatedFields, ReviewApproved, ReviewCreated, ReviewRejected, ReviewState } from "./types";
import { DEMO_BOUNDED_CONTEXT_ALIAS } from "../boundedContext";

const DEFAULT_QUERY_CLIENT_OPTIONS: QueryClientOptions = {
    contextAlias: DEMO_BOUNDED_CONTEXT_ALIAS,
    aggregateName: 'review',
    resourceAttribution: ResourceAttributionPathSpec.TENANT,
};

export enum ReviewDomainEventTypeMapTitle {
    review_approved = 'review_approved',
    review_created = 'review_created',
    review_rejected = 'review_rejected'
}

export type ReviewDomainEventType = ReviewApproved | ReviewCreated | ReviewRejected;

export const reviewQueryClientFactory = new QueryClientFactory<ReviewState, ReviewAggregatedFields | string, ReviewDomainEventType>(DEFAULT_QUERY_CLIENT_OPTIONS);
