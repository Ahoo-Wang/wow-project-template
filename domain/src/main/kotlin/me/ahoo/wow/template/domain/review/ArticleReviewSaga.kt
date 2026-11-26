package me.ahoo.wow.template.domain.review

import me.ahoo.wow.api.annotation.OnEvent
import me.ahoo.wow.api.annotation.OnRejected
import me.ahoo.wow.api.annotation.Saga
import me.ahoo.wow.api.client.CommandRequest
import me.ahoo.wow.api.client.toCommandRequest
import me.ahoo.wow.api.result.FrameworkCodes
import me.ahoo.wow.api.subscribe.CommandReceipt
import me.ahoo.wow.api.subscribe.StateTypedEvent
import me.ahoo.wow.template.api.article.ArticleSubmitted
import me.ahoo.wow.template.api.article.PublishArticle
import me.ahoo.wow.template.api.article.ReturnArticle
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewExpired
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.domain.article.ArticleState

/**
 * 文章审核流程，跨两个聚合协作：
 *
 * ```text
 * ArticleSubmitted ──▶ CreateReview ──▶ ReviewApproved ──▶ PublishArticle
 *                                     ├▶ ReviewRejected ──▶ ReturnArticle
 *                                     └▶ ReviewExpired  ──▶ ReturnArticle
 * ```
 *
 * - 每个 `@OnEvent` 函数是一个独立订阅（`ArticleReviewSaga.<function>`），从日志按自己的检查点读取，至少投递一次。
 * - 运行时为派生命令生成确定性的 ID 与请求 ID：同一提交再次处理派生出同一请求，目标只决策一次。
 * - 每个请求只给出目标 ID：聚合类型由契约按命令类确定，租户与作用域沿用所处理的提交。
 * - 开启审核的请求不带 ID：由目标生成，并在每次重试中保持第一次的 ID。每次提交都开启一次新的审核。
 */
@Saga
class ArticleReviewSaga {

    @OnEvent
    fun onArticleSubmitted(event: StateTypedEvent<ArticleState, ArticleSubmitted>): CommandRequest<CreateReview> =
        CreateReview(articleId = event.ref.id).toCommandRequest()

    @OnEvent
    fun onReviewApproved(event: StateTypedEvent<ReviewState, ReviewApproved>): CommandRequest<PublishArticle> =
        PublishArticle(reviewId = event.ref.id).toCommandRequest(event.event.articleId)

    @OnEvent
    fun onReviewRejected(event: StateTypedEvent<ReviewState, ReviewRejected>): CommandRequest<ReturnArticle> =
        ReturnArticle(reviewId = event.ref.id, reason = event.event.reason).toCommandRequest(event.event.articleId)

    @OnEvent
    fun onReviewExpired(event: StateTypedEvent<ReviewState, ReviewExpired>): CommandRequest<ReturnArticle> =
        ReturnArticle(reviewId = event.ref.id, reason = EXPIRED_REASON).toCommandRequest(event.event.articleId)

    /**
     * 审核期间作者删除了文章（[me.ahoo.wow.template.api.article.DeleteArticle]，相当于撤稿），发布被拒绝
     * （`wow.aggregate.deleted`）：无需补偿，接受即可。
     * 没有 `@OnRejected` 处理函数，派生命令被拒绝会停放（park）审核聚合，等待人工处理。
     *
     * `accept` 只接受列出的错误码，每次接受都记在 `wow.saga.rejection` 日志里。其他拒绝不该发生（文章只会被本 Saga
     * 移出审核，审核中删除的文章不能恢复，重复投递由确定性 ID 去重），一旦发生就照常停放审核聚合，交给运维排查
     * （`/wow/ops/parked`），而不是被悄悄吞掉。
     */
    @OnRejected
    fun onPublishRejected(rejected: CommandReceipt.Rejected<PublishArticle>) =
        rejected.accept(FrameworkCodes.AGGREGATE_DELETED)

    /** 同上：文章已删除，无需退回。 */
    @OnRejected
    fun onReturnRejected(rejected: CommandReceipt.Rejected<ReturnArticle>) =
        rejected.accept(FrameworkCodes.AGGREGATE_DELETED)

    companion object {
        const val EXPIRED_REASON = "The review expired before an editor decided it."
    }
}
