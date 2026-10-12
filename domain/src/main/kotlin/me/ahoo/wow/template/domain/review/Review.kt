package me.ahoo.wow.template.domain.review

import me.ahoo.wow.api.annotation.AggregateRoot
import me.ahoo.wow.api.annotation.OnCommand
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.RejectReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewCreated
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.api.review.ReviewStatus

/**
 * 审核聚合根：一次审核只能被裁决一次。
 */
@Suppress("unused")
@AggregateRoot
class Review(private val state: ReviewState) {

    @OnCommand
    fun onCreate(command: CreateReview): ReviewCreated {
        return ReviewCreated(demoId = command.demoId)
    }

    @OnCommand
    fun onApprove(command: ApproveReview): ReviewApproved {
        checkPending()
        return ReviewApproved(demoId = state.demoId, comment = command.comment)
    }

    @OnCommand
    fun onReject(command: RejectReview): ReviewRejected {
        checkPending()
        return ReviewRejected(demoId = state.demoId, reason = command.reason)
    }

    private fun checkPending() {
        check(state.status == ReviewStatus.PENDING) { "Review[${state.id}] is already ${state.status}." }
    }
}
