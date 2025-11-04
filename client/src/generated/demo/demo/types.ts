/**
 * 创建Demo
 * - key: demo.demo.CreateDemo
 */
export interface CreateDemo {
    /**
     * - String Constraints
     *   - minLength: 1
     */
    data: string;
}

/** - key: demo.demo.DemoAggregatedFields */
export enum DemoAggregatedFields {
    AGGREGATE_ID = 'aggregateId',
    TENANT_ID = 'tenantId',
    OWNER_ID = 'ownerId',
    VERSION = 'version',
    EVENT_ID = 'eventId',
    FIRST_OPERATOR = 'firstOperator',
    OPERATOR = 'operator',
    FIRST_EVENT_TIME = 'firstEventTime',
    EVENT_TIME = 'eventTime',
    DELETED = 'deleted',
    STATE = 'state',
    STATE_DATA = 'state.data',
    STATE_ID = 'state.id'
}

/**
 * demo_created
 * - key: demo.demo.DemoCreated
 */
export interface DemoCreated {
    /**
     * - String Constraints
     *   - minLength: 1
     */
    data: string;
}

/** - key: demo.demo.DemoState */
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
 */
export interface UpdateDemo {
    /**
     * - String Constraints
     *   - minLength: 1
     */
    data: string;
}
