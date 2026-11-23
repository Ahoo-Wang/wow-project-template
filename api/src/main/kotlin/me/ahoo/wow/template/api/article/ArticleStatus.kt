package me.ahoo.wow.template.api.article

/**
 * ```text
 * DRAFT -> IN_REVIEW -> PUBLISHED
 *   ^          |
 *   +----------+  退回：审核被驳回或超时
 * ```
 */
enum class ArticleStatus {
    DRAFT,
    IN_REVIEW,
    PUBLISHED,
}
