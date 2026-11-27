package me.ahoo.wow.template.domain.review

import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewCreated
import me.ahoo.wow.template.api.review.ReviewExpired
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.api.review.ReviewStatus

class ReviewState {
    var articleId: String = ""
        private set
    var status: ReviewStatus = ReviewStatus.PENDING
        private set

    /** 编辑的意见：批准时的评语，或驳回的理由。 */
    var remark: String = ""
        private set

    fun onSourcing(event: ReviewCreated) {
        articleId = event.articleId
    }

    fun onSourcing(event: ReviewApproved) {
        status = ReviewStatus.APPROVED
        remark = event.comment
    }

    fun onSourcing(event: ReviewRejected) {
        status = ReviewStatus.REJECTED
        remark = event.reason
    }

    @Suppress("UnusedParameter")
    fun onSourcing(event: ReviewExpired) {
        status = ReviewStatus.EXPIRED
    }
}
