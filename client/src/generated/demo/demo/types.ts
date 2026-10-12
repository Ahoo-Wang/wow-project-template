/**
 * 创建Demo
 * - key: demo.demo.CreateDemo
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "data": {
 *       "type": "string",
 *       "minLength": 1
 *     }
 *   },
 *   "required": [
 *     "data"
 *   ],
 *   "title": "创建Demo",
 *   "description": ""
 * }
 * ```
 */
export interface CreateDemo {
    /**
     * - String Constraints
     *   - minLength: 1
     */
    data: string;
}

/**
 * demo_created
 * - key: demo.demo.DemoCreated
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "data": {
 *       "type": "string"
 *     }
 *   },
 *   "required": [
 *     "data"
 *   ],
 *   "title": "demo_created"
 * }
 * ```
 */
export interface DemoCreated {
    data: string;
}

/**
 * demo_published
 * - key: demo.demo.DemoPublished
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "reviewId": {
 *       "type": "string"
 *     }
 *   },
 *   "required": [
 *     "reviewId"
 *   ],
 *   "title": "demo_published"
 * }
 * ```
 */
export interface DemoPublished {
    reviewId: string;
}

/**
 * demo_rejected
 * - key: demo.demo.DemoRejected
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "reason": {
 *       "type": "string"
 *     },
 *     "reviewId": {
 *       "type": "string"
 *     }
 *   },
 *   "required": [
 *     "reason",
 *     "reviewId"
 *   ],
 *   "title": "demo_rejected"
 * }
 * ```
 */
export interface DemoRejected {
    reason: string;
    reviewId: string;
}

/**
 * - key: demo.demo.DemoState
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "data": {
 *       "type": "string",
 *       "minLength": 1
 *     },
 *     "id": {
 *       "type": "string"
 *     },
 *     "status": {
 *       "$ref": "#/components/schemas/demo.demo.DemoStatus"
 *     }
 *   },
 *   "required": [
 *     "id"
 *   ]
 * }
 * ```
 */
export interface DemoState {
    /**
     * - String Constraints
     *   - minLength: 1
     */
    data: string;
    id: string;
    status: DemoStatus;
}

/**
 * - key: demo.demo.DemoStateCursorPage
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "list": {
 *       "type": "array",
 *       "items": {
 *         "$ref": "#/components/schemas/demo.demo.DemoState"
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
export interface DemoStateCursorPage {
    list: DemoState[];
    nextCursor: (null | string);
}

/**
 * - key: demo.demo.DemoStatus
 * - schema: 
 * ```json
 * {
 *   "type": "string",
 *   "enum": [
 *     "REVIEWING",
 *     "PUBLISHED",
 *     "REJECTED"
 *   ]
 * }
 * ```
 */
export enum DemoStatus {
    REVIEWING = 'REVIEWING',
    PUBLISHED = 'PUBLISHED',
    REJECTED = 'REJECTED'
}

/**
 * demo_updated
 * - key: demo.demo.DemoUpdated
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "data": {
 *       "type": "string"
 *     }
 *   },
 *   "required": [
 *     "data"
 *   ],
 *   "title": "demo_updated"
 * }
 * ```
 */
export interface DemoUpdated {
    data: string;
}

/**
 * 更新Demo
 * - key: demo.demo.UpdateDemo
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "properties": {
 *     "data": {
 *       "type": "string",
 *       "minLength": 1
 *     }
 *   },
 *   "required": [
 *     "data"
 *   ],
 *   "title": "更新Demo",
 *   "description": ""
 * }
 * ```
 */
export interface UpdateDemo {
    /**
     * - String Constraints
     *   - minLength: 1
     */
    data: string;
}

/**
 * - key: demo.demo.DemoAggregatedFields
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
 *     "state.data",
 *     "state.id",
 *     "state.status",
 *     "tags",
 *     "tenantId",
 *     "version"
 *   ]
 * }
 * ```
 */
export enum DemoAggregatedFields {
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
    STATE_DATA = 'state.data',
    STATE_ID = 'state.id',
    STATE_STATUS = 'state.status',
    TAGS = 'tags',
    TENANT_ID = 'tenantId',
    VERSION = 'version'
}
