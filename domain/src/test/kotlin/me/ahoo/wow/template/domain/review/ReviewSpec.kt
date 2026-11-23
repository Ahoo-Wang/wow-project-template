package me.ahoo.wow.template.domain.review

import me.ahoo.test.asserts.assert
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.RejectReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewCreated
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.api.review.ReviewStatus
import me.ahoo.wow.test.aggregateSpec
import org.junit.jupiter.api.Test

class ReviewSpec {
    private val model = reviewModel()

    @Test
    fun `creating a review should be pending`() = aggregateSpec(model, id = DEMO) {
        whenCommand(CreateReview(demoId = DEMO)) {
            expectEvents(ReviewCreated(demoId = DEMO))
            expectState {
                demoId.assert().isEqualTo(DEMO)
                status.assert().isEqualTo(ReviewStatus.PENDING)
            }
        }
    }

    @Test
    fun `a review should be decided once`() = aggregateSpec(model, id = DEMO) {
        given(ReviewCreated(demoId = DEMO))
        fork("approve") {
            whenCommand(ApproveReview(comment = "LGTM")) {
                expectEvents(ReviewApproved(demoId = DEMO, comment = "LGTM"))
                expectState { status.assert().isEqualTo(ReviewStatus.APPROVED) }
            }
            whenCommand(RejectReview(reason = "reason")) { expectRejected(Review.Rejections.ALREADY_DECIDED) }
        }
        fork("reject") {
            whenCommand(RejectReview(reason = "reason")) {
                expectEvents(ReviewRejected(demoId = DEMO, reason = "reason"))
                expectState { status.assert().isEqualTo(ReviewStatus.REJECTED) }
            }
            whenCommand(ApproveReview()) { expectRejected(Review.Rejections.ALREADY_DECIDED) }
        }
    }

    private companion object {
        const val DEMO = "demo-1"
    }
}
