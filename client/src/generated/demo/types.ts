/**
 * - key: demo.JsonNode
 * - schema: 
 * ```json
 * {}
 * ```
 */
export type JsonNode = any;
/**
 * - key: demo.StringObjectMap
 * - schema: 
 * ```json
 * {
 *   "type": "object"
 * }
 * ```
 */
export type StringObjectMap = globalThis.Record<string, any>;
/**
 * - key: demo.StringStringListMap
 * - schema: 
 * ```json
 * {
 *   "type": "object",
 *   "additionalProperties": {
 *     "type": "array",
 *     "items": {
 *       "type": "string"
 *     }
 *   }
 * }
 * ```
 */
export type StringStringListMap = globalThis.Record<string, string[]>;

/**
 * - key: demo.TimeUnit
 * - schema: 
 * ```json
 * {
 *   "type": "string",
 *   "enum": [
 *     "NANOSECONDS",
 *     "MICROSECONDS",
 *     "MILLISECONDS",
 *     "SECONDS",
 *     "MINUTES",
 *     "HOURS",
 *     "DAYS"
 *   ]
 * }
 * ```
 */
export enum TimeUnit {
    NANOSECONDS = 'NANOSECONDS',
    MICROSECONDS = 'MICROSECONDS',
    MILLISECONDS = 'MILLISECONDS',
    SECONDS = 'SECONDS',
    MINUTES = 'MINUTES',
    HOURS = 'HOURS',
    DAYS = 'DAYS'
}
