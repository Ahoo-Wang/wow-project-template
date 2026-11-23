package me.ahoo.wow.template.domain.review

import me.ahoo.wow.api.annotation.OnEvent
import me.ahoo.wow.api.annotation.Saga
import me.ahoo.wow.api.client.CommandRequest
import me.ahoo.wow.api.client.toCommandRequest
import me.ahoo.wow.api.subscribe.StateTypedEvent
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.PublishDemo
import me.ahoo.wow.template.api.demo.RejectDemo
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.domain.demo.DemoState

/**
 * Demo 审核流程（跨聚合协作）：
 *
 * ```
 * DemoCreated ──▶ CreateReview ──▶ ReviewApproved ──▶ PublishDemo
 *                                 └▶ ReviewRejected ──▶ RejectDemo
 * ```
 *
 * 每个处理函数是一个独立订阅（`DemoReviewSaga.<function>`），从日志按检查点读取，至少投递一次。
 * 运行时为派生命令生成确定性 ID：同一提交被重复处理时派生出同一命令，只被决策一次。
 * 审核 ID 复用 Demo ID；每个命令只给出目标 ID，聚合类型由契约按命令类决定，租户与作用域沿用所处理的提交。
 */
@Saga
class DemoReviewSaga {

    @OnEvent
    fun onDemoCreated(event: StateTypedEvent<DemoState, DemoCreated>): CommandRequest<CreateReview> =
        CreateReview(demoId = event.ref.id).toCommandRequest(event.ref.id)

    @OnEvent
    fun onReviewApproved(event: StateTypedEvent<ReviewState, ReviewApproved>): CommandRequest<PublishDemo> =
        PublishDemo(reviewId = event.ref.id).toCommandRequest(event.event.demoId)

    @OnEvent
    fun onReviewRejected(event: StateTypedEvent<ReviewState, ReviewRejected>): CommandRequest<RejectDemo> =
        RejectDemo(reviewId = event.ref.id, reason = event.event.reason).toCommandRequest(event.event.demoId)
}
