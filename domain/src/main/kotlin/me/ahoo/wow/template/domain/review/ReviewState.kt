package me.ahoo.wow.template.domain.review

import me.ahoo.wow.api.Identifier
import me.ahoo.wow.api.annotation.OnSourcing
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewCreated
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.api.review.ReviewStatus

class ReviewState(override val id: String) : Identifier {
    var demoId: String = ""
        private set
    var status: ReviewStatus = ReviewStatus.PENDING
        private set

    @OnSourcing
    fun onCreated(event: ReviewCreated) {
        demoId = event.demoId
        status = ReviewStatus.PENDING
    }

    @Suppress("UnusedParameter")
    @OnSourcing
    fun onApproved(event: ReviewApproved) {
        status = ReviewStatus.APPROVED
    }

    @Suppress("UnusedParameter")
    @OnSourcing
    fun onRejected(event: ReviewRejected) {
        status = ReviewStatus.REJECTED
    }
}
