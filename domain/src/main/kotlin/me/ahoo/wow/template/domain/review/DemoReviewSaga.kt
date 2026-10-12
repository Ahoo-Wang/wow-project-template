package me.ahoo.wow.template.domain.review

import me.ahoo.wow.api.annotation.OnEvent
import me.ahoo.wow.api.annotation.StatelessSaga
import me.ahoo.wow.api.modeling.AggregateId
import me.ahoo.wow.command.factory.CommandBuilder
import me.ahoo.wow.command.factory.CommandBuilder.Companion.commandBuilder
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.PublishDemo
import me.ahoo.wow.template.api.demo.RejectDemo
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewRejected

/**
 * Demo 审核流程（跨聚合协作）：
 *
 * ```
 * DemoCreated ──▶ CreateReview ──▶ ReviewApproved ──▶ PublishDemo
 *                                 └▶ ReviewRejected ──▶ RejectDemo
 * ```
 *
 * 审核 ID 复用 Demo ID：事件至少投递一次（at-least-once），重复的 `CreateReview`
 * 会因聚合已存在而被拒绝，从而天然幂等。
 */
@StatelessSaga
class DemoReviewSaga {

    @OnEvent
    fun onDemoCreated(@Suppress("UnusedParameter") event: DemoCreated, aggregateId: AggregateId): CommandBuilder {
        return CreateReview(demoId = aggregateId.id)
            .commandBuilder()
            .aggregateId(aggregateId.id)
    }

    @OnEvent
    fun onReviewApproved(event: ReviewApproved, aggregateId: AggregateId): CommandBuilder {
        return PublishDemo(reviewId = aggregateId.id)
            .commandBuilder()
            .aggregateId(event.demoId)
    }

    @OnEvent
    fun onReviewRejected(event: ReviewRejected, aggregateId: AggregateId): CommandBuilder {
        return RejectDemo(reviewId = aggregateId.id, reason = event.reason)
            .commandBuilder()
            .aggregateId(event.demoId)
    }
}
