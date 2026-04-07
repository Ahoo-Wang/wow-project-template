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
 * - key: demo.demo.DemoAggregatedFields
 * - schema: 
 * ```json
 * {
 *   "type": "string",
 *   "enum": [
 *     "",
 *     "aggregateId",
 *     "tenantId",
 *     "ownerId",
 *     "spaceId",
 *     "version",
 *     "eventId",
 *     "firstOperator",
 *     "operator",
 *     "firstEventTime",
 *     "eventTime",
 *     "tags",
 *     "deleted",
 *     "state",
 *     "state.data",
 *     "state.id"
 *   ]
 * }
 * ```
 */
export enum DemoAggregatedFields {
    AGGREGATE_ID = `aggregateId`,
    TENANT_ID = `tenantId`,
    OWNER_ID = `ownerId`,
    SPACE_ID = `spaceId`,
    VERSION = `version`,
    EVENT_ID = `eventId`,
    FIRST_OPERATOR = `firstOperator`,
    OPERATOR = `operator`,
    FIRST_EVENT_TIME = `firstEventTime`,
    EVENT_TIME = `eventTime`,
    TAGS = `tags`,
    DELETED = `deleted`,
    STATE = `state`,
    STATE_DATA = `state.data`,
    STATE_ID = `state.id`
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
 *       "type": "string",
 *       "minLength": 1
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
    /**
     * - String Constraints
     *   - minLength: 1
     */
    data: string;
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
 *       "type": "string",
 *       "minLength": 1
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
    /**
     * - String Constraints
     *   - minLength: 1
     */
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
