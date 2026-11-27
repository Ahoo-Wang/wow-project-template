package me.ahoo.wow.template.domain.article

import me.ahoo.wow.api.query.annotation.QueryIndex
import me.ahoo.wow.api.read.EventStream
import me.ahoo.wow.template.api.article.ArticleCreated
import me.ahoo.wow.template.api.article.ArticleEdited
import me.ahoo.wow.template.api.article.ArticlePublished
import me.ahoo.wow.template.api.article.ArticleReturned
import me.ahoo.wow.template.api.article.ArticleStatus
import me.ahoo.wow.template.api.article.ArticleSubmitted

/**
 * 文章的状态：由事件演化而来，只在 `onSourcing` 中修改，不做 I/O、不读时钟。
 * 聚合 ID 不是状态的一部分（快照的 `aggregateId`、命令的 `ref.id`）。
 *
 * `@QueryIndex` 声明快照查询的索引，后端在启动时按 `wow.ddl` 建立：按 slug 查一篇文章，按状态列出文章并按发布时间排序。
 */
class ArticleState {
    var title: String = ""
        private set
    var body: String = ""
        private set

    @field:QueryIndex
    var slug: String = ""
        private set

    @field:QueryIndex(with = ["state.publishedAt"])
    var status: ArticleStatus = ArticleStatus.DRAFT
        private set

    /** 最近一次审核的 ID：发布或退回时记录。 */
    var reviewId: String? = null
        private set

    /** 发布时间（epoch 毫秒），取自发布事件所在提交的时间，而不是系统时钟。 */
    var publishedAt: Long? = null
        private set

    fun onSourcing(event: ArticleCreated) {
        title = event.title
        body = event.body
        slug = event.slug
    }

    fun onSourcing(event: ArticleEdited) {
        title = event.title
        body = event.body
    }

    @Suppress("UnusedParameter")
    fun onSourcing(event: ArticleSubmitted) {
        status = ArticleStatus.IN_REVIEW
    }

    fun onSourcing(event: ArticlePublished, commit: EventStream) {
        status = ArticleStatus.PUBLISHED
        reviewId = event.reviewId
        publishedAt = commit.commitTime
    }

    fun onSourcing(event: ArticleReturned) {
        status = ArticleStatus.DRAFT
        reviewId = event.reviewId
    }
}
