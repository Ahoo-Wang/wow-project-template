/**
 * 审核通过
 * - key: demo.review.ApproveReview
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "comment": {
 *       "type": "string"
 *     }
 *   },
 *   "title": "审核通过",
 *   "description": ""
 * }
 * ```
 */
export interface ApproveReview {
    comment: string;
}

/**
 * 审核驳回
 * - key: demo.review.RejectReview
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "reason": {
 *       "type": "string",
 *       "minLength": 1
 *     }
 *   },
 *   "required": [
 *     "reason"
 *   ],
 *   "title": "审核驳回",
 *   "description": ""
 * }
 * ```
 */
export interface RejectReview {
    /**
     * - String Constraints
     *   - minLength: 1
     */
    reason: string;
}

/**
 * review_approved
 * - key: demo.review.ReviewApproved
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "comment": {
 *       "type": "string"
 *     },
 *     "demoId": {
 *       "type": "string"
 *     }
 *   },
 *   "required": [
 *     "comment",
 *     "demoId"
 *   ],
 *   "title": "review_approved"
 * }
 * ```
 */
export interface ReviewApproved {
    comment: string;
    demoId: string;
}

/**
 * review_created
 * - key: demo.review.ReviewCreated
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "demoId": {
 *       "type": "string"
 *     }
 *   },
 *   "required": [
 *     "demoId"
 *   ],
 *   "title": "review_created"
 * }
 * ```
 */
export interface ReviewCreated {
    demoId: string;
}

/**
 * review_rejected
 * - key: demo.review.ReviewRejected
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "demoId": {
 *       "type": "string"
 *     },
 *     "reason": {
 *       "type": "string"
 *     }
 *   },
 *   "required": [
 *     "demoId",
 *     "reason"
 *   ],
 *   "title": "review_rejected"
 * }
 * ```
 */
export interface ReviewRejected {
    demoId: string;
    reason: string;
}

/**
 * - key: demo.review.ReviewState
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "demoId": {
 *       "type": "string"
 *     },
 *     "id": {
 *       "type": "string"
 *     },
 *     "status": {
 *       "$ref": "#/components/schemas/demo.review.ReviewStatus"
 *     }
 *   },
 *   "required": [
 *     "id"
 *   ]
 * }
 * ```
 */
export interface ReviewState {
    demoId: string;
    id: string;
    status: ReviewStatus;
}

/**
 * - key: demo.review.ReviewStateCursorPage
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "list": {
 *       "type": "array",
 *       "items": {
 *         "$ref": "#/components/schemas/demo.review.ReviewState"
 *       }
 *     },
 *     "nextCursor": {
 *       "anyOf": [
 *         {
 *           "type": "null"
 *         },
 *         {
 *           "type": "string"
 *         }
 *       ]
 *     }
 *   },
 *   "required": [
 *     "list",
 *     "nextCursor"
 *   ]
 * }
 * ```
 */
export interface ReviewStateCursorPage {
    list: ReviewState[];
    nextCursor: (null | string);
}

/**
 * - key: demo.review.ReviewStatus
 * - schema: 
 * ```json
 * {
 *   "type": "string",
 *   "enum": [
 *     "PENDING",
 *     "APPROVED",
 *     "REJECTED"
 *   ]
 * }
 * ```
 */
export enum ReviewStatus {
    PENDING = 'PENDING',
    APPROVED = 'APPROVED',
    REJECTED = 'REJECTED'
}

/**
 * - key: demo.review.ReviewAggregatedFields
 * - schema: 
 * ```json
 * {
 *   "type": "string",
 *   "enum": [
 *     "aggregateId",
 *     "aggregateName",
 *     "contextName",
 *     "deleted",
 *     "eventId",
 *     "eventTime",
 *     "firstEventTime",
 *     "firstOperator",
 *     "operator",
 *     "ownerId",
 *     "snapshotTime",
 *     "spaceId",
 *     "state",
 *     "state.demoId",
 *     "state.id",
 *     "state.status",
 *     "tags",
 *     "tenantId",
 *     "version"
 *   ]
 * }
 * ```
 */
export enum ReviewAggregatedFields {
    AGGREGATE_ID = 'aggregateId',
    AGGREGATE_NAME = 'aggregateName',
    CONTEXT_NAME = 'contextName',
    DELETED = 'deleted',
    EVENT_ID = 'eventId',
    EVENT_TIME = 'eventTime',
    FIRST_EVENT_TIME = 'firstEventTime',
    FIRST_OPERATOR = 'firstOperator',
    OPERATOR = 'operator',
    OWNER_ID = 'ownerId',
    SNAPSHOT_TIME = 'snapshotTime',
    SPACE_ID = 'spaceId',
    STATE = 'state',
    STATE_DEMO_ID = 'state.demoId',
    STATE_ID = 'state.id',
    STATE_STATUS = 'state.status',
    TAGS = 'tags',
    TENANT_ID = 'tenantId',
    VERSION = 'version'
}
