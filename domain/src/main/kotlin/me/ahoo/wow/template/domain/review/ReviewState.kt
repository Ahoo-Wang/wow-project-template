package me.ahoo.wow.template.domain.review

import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewCreated
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.api.review.ReviewStatus

class ReviewState {
    var demoId: String = ""
        private set
    var status: ReviewStatus = ReviewStatus.PENDING
        private set

    fun onSourcing(event: ReviewCreated) {
        demoId = event.demoId
        status = ReviewStatus.PENDING
    }

    @Suppress("UnusedParameter")
    fun onSourcing(event: ReviewApproved) {
        status = ReviewStatus.APPROVED
    }

    @Suppress("UnusedParameter")
    fun onSourcing(event: ReviewRejected) {
        status = ReviewStatus.REJECTED
    }
}
