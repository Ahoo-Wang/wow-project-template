package me.ahoo.wow.template.domain.review

import me.ahoo.wow.api.aggregate.Ownership
import me.ahoo.wow.api.annotation.AggregateRoot
import me.ahoo.wow.api.annotation.Description
import me.ahoo.wow.api.annotation.ErrorCode
import me.ahoo.wow.api.annotation.OnCommand
import me.ahoo.wow.api.decide.DecideScope
import me.ahoo.wow.api.decide.reject
import me.ahoo.wow.api.schedule.ScheduleRule
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.ExpireReview
import me.ahoo.wow.template.api.review.RejectReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewCreated
import me.ahoo.wow.template.api.review.ReviewExpired
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.api.review.ReviewStatus

/**
 * 一次提交的审核：只能裁决一次，到期未裁决则过期。
 *
 * 审核属于租户（`Ownership.TENANT`），不属于提交文章的作者：租户内的调用方都能读到它，
 * 谁能裁决由宿主的 `CommandAuthorizer` 决定（只有编辑）。
 *
 * 过期是一条持久化的定时命令：开启审核时安排 [ExpireReview]，随事件一起提交；裁决时取消。
 * 定时命令至少投递一次、绝不提前，到期时审核已裁决则什么也不做。
 */
@AggregateRoot(ownership = Ownership.TENANT)
@Description("编辑对一次提交的审核")
class Review(private val state: ReviewState) {

    @OnCommand(schedules = [ExpireReview::class])
    fun onCommand(command: CreateReview, reviewPolicy: ReviewPolicy, scope: DecideScope): ReviewCreated {
        scope.schedule(EXPIRY, ScheduleRule.after(reviewPolicy.timeout), ExpireReview)
        return ReviewCreated(articleId = command.articleId)
    }

    fun onCommand(command: ApproveReview, scope: DecideScope): ReviewApproved {
        requirePending()
        scope.cancelSchedule(EXPIRY)
        return ReviewApproved(articleId = state.articleId, comment = command.comment)
    }

    fun onCommand(command: RejectReview, scope: DecideScope): ReviewRejected {
        requirePending()
        scope.cancelSchedule(EXPIRY)
        return ReviewRejected(articleId = state.articleId, reason = command.reason)
    }

    /** 已裁决的审核不再过期：返回 `null`，什么也不提交。 */
    @Suppress("UnusedParameter") // 命令没有字段：函数处理的是它的类型。
    fun onCommand(command: ExpireReview): ReviewExpired? =
        if (state.status == ReviewStatus.PENDING) ReviewExpired(articleId = state.articleId) else null

    private fun requirePending() {
        if (state.status != ReviewStatus.PENDING) {
            reject(Rejections.ALREADY_DECIDED, "The review is already ${state.status}.")
        }
    }

    companion object {
        /** 过期定时命令的键：同一审核只有一个。 */
        const val EXPIRY = "expiry"
    }

    object Rejections {
        @ErrorCode(description = "The review was already approved, rejected or expired.", status = 409)
        const val ALREADY_DECIDED = "already-decided"
    }
}
