package me.ahoo.wow.template.domain.review

import me.ahoo.test.asserts.assert
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.RejectReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewCreated
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.api.review.ReviewStatus
import me.ahoo.wow.test.AggregateSpec

class ReviewSpec : AggregateSpec<Review, ReviewState>({
    on {
        val create = CreateReview(demoId = "demoId")
        whenCommand(create) {
            expectNoError()
            expectEventType(ReviewCreated::class)
            expectState {
                demoId.assert().isEqualTo(create.demoId)
                status.assert().isEqualTo(ReviewStatus.PENDING)
            }
            fork("approve") {
                whenCommand(ApproveReview(comment = "LGTM")) {
                    expectNoError()
                    expectEventType(ReviewApproved::class)
                    expectEventBody<ReviewApproved> {
                        demoId.assert().isEqualTo(create.demoId)
                    }
                    expectState {
                        status.assert().isEqualTo(ReviewStatus.APPROVED)
                    }
                    fork("reject after approved") {
                        whenCommand(RejectReview(reason = "reason")) {
                            expectError<IllegalStateException> {
                                message.assert().contains("already APPROVED")
                            }
                        }
                    }
                }
            }
            fork("reject") {
                whenCommand(RejectReview(reason = "reason")) {
                    expectNoError()
                    expectEventType(ReviewRejected::class)
                    expectState {
                        status.assert().isEqualTo(ReviewStatus.REJECTED)
                    }
                }
            }
        }
    }
})
