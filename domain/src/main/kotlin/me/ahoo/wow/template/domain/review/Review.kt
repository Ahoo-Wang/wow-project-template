package me.ahoo.wow.template.domain.review

import me.ahoo.wow.api.annotation.AggregateRoot
import me.ahoo.wow.api.annotation.ErrorCode
import me.ahoo.wow.api.decide.reject
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
@AggregateRoot
class Review(private val state: ReviewState) {

    fun onCommand(command: CreateReview): ReviewCreated = ReviewCreated(demoId = command.demoId)

    fun onCommand(command: ApproveReview): ReviewApproved {
        requirePending()
        return ReviewApproved(demoId = state.demoId, comment = command.comment)
    }

    fun onCommand(command: RejectReview): ReviewRejected {
        requirePending()
        return ReviewRejected(demoId = state.demoId, reason = command.reason)
    }

    private fun requirePending() {
        if (state.status != ReviewStatus.PENDING) {
            reject(Rejections.ALREADY_DECIDED, "The review is already ${state.status}.")
        }
    }

    object Rejections {
        @ErrorCode(description = "The review was already approved or rejected.", status = 409)
        const val ALREADY_DECIDED = "already-decided"
    }
}
