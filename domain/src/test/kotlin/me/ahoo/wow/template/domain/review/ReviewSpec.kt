package me.ahoo.wow.template.domain.review

import me.ahoo.test.asserts.assert
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
import me.ahoo.wow.test.aggregateSpec
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days

/**
 * 审核：只能裁决一次，到期未裁决则过期。领域服务 [ReviewPolicy] 由生成的模型工厂注入，spec 的时钟驱动定时命令。
 */
class ReviewSpec {
    private val model = reviewModel(reviewPolicy = ReviewPolicy(TIMEOUT))

    @Test
    fun `opening a review should schedule its expiry`() = aggregateSpec(model, id = REVIEW) {
        whenCommand(CreateReview(ARTICLE)) {
            expectEvents(ReviewCreated(ARTICLE))
            expectScheduled(Review.EXPIRY, ExpireReview, ScheduleRule.after(TIMEOUT))
            expectState { status.assert().isEqualTo(ReviewStatus.PENDING) }
        }
    }

    @Test
    fun `an undecided review should expire once its time is up`() = aggregateSpec(model, id = REVIEW) {
        givenCommands(CreateReview(ARTICLE))
        advanceTime(TIMEOUT) {
            expectEvents(ReviewExpired(ARTICLE))
            expectState { status.assert().isEqualTo(ReviewStatus.EXPIRED) }
        }
        whenCommand(ApproveReview()) { expectRejected(Review.Rejections.ALREADY_DECIDED) }
    }

    @Test
    fun `a review should be decided once, and its expiry cancelled`() = aggregateSpec(model, id = REVIEW) {
        givenCommands(CreateReview(ARTICLE))
        fork("approved") {
            whenCommand(ApproveReview("LGTM")) {
                expectEvents(ReviewApproved(ARTICLE, "LGTM"))
                expectScheduleCancelled(Review.EXPIRY)
                expectState {
                    status.assert().isEqualTo(ReviewStatus.APPROVED)
                    remark.assert().isEqualTo("LGTM")
                }
            }
            whenCommand(RejectReview("Changed my mind.")) { expectRejected(Review.Rejections.ALREADY_DECIDED) }
            expectNothingDue(within = TIMEOUT)
        }
        fork("rejected") {
            whenCommand(RejectReview("Needs an example.")) {
                expectEvents(ReviewRejected(ARTICLE, "Needs an example."))
                expectScheduleCancelled(Review.EXPIRY)
                expectState { status.assert().isEqualTo(ReviewStatus.REJECTED) }
            }
            whenCommand(ApproveReview()) { expectRejected(Review.Rejections.ALREADY_DECIDED) }
        }
    }

    @Test
    fun `an expiry due after the decision should change nothing`() = aggregateSpec(model, id = REVIEW) {
        given(ReviewCreated(ARTICLE), ReviewApproved(ARTICLE, "LGTM"))
        whenCommand(ExpireReview) {
            expectUnchanged()
            expectState { status.assert().isEqualTo(ReviewStatus.APPROVED) }
        }
    }

    @Test
    fun `a review policy should refuse a timeout that is not positive`() {
        val failure = runCatching { ReviewPolicy(0.days) }.exceptionOrNull()
        failure.assert().isInstanceOf(IllegalArgumentException::class.java)
    }

    private companion object {
        const val ARTICLE = "article-1"
        const val REVIEW = "review-1"
        val TIMEOUT = 7.days
    }
}
