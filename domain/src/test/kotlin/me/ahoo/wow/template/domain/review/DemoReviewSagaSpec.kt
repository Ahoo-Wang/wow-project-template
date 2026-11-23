package me.ahoo.wow.template.domain.review

import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.PublishDemo
import me.ahoo.wow.template.api.demo.RejectDemo
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.RejectReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewCreated
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.domain.demo.DemoModel
import me.ahoo.wow.template.domain.demo.demoModel
import me.ahoo.wow.test.sagaSpec
import org.junit.jupiter.api.Test

/**
 * 审核 Saga：把聚合的提交交给读取它的处理函数（与订阅一致），校验派生出的命令及其目标聚合。
 */
class DemoReviewSagaSpec {
    private val handlers = demoReviewSagaHandlers()

    @Test
    fun `a created demo should start its review, under the demo's id`() = sagaSpec(handlers) {
        val demo = givenAggregate(demoModel(), DEMO)
        whenEvent(DemoCreated(data = "data"), by = CreateDemo::class) {
            expectHandledBy("onDemoCreated")
            expectCommand(CreateReview(demoId = DEMO), to = REVIEW_TYPE.ref(demo.ref.tenantId, DEMO))
            // 同一提交再次处理派生出同一命令（确定性 ID），只被决策一次。
            expectDeterministic()
        }
    }

    @Test
    fun `an approved review should publish its demo`() = sagaSpec(handlers) {
        val review = givenAggregate(reviewModel(), DEMO, ReviewCreated(demoId = DEMO))
        whenEvent(ReviewApproved(demoId = DEMO, comment = "LGTM"), by = ApproveReview::class) {
            expectHandledBy("onReviewApproved")
            expectCommand(PublishDemo(reviewId = DEMO), to = DEMO_TYPE.ref(review.ref.tenantId, DEMO))
        }
    }

    @Test
    fun `a rejected review should reject its demo`() = sagaSpec(handlers) {
        val review = givenAggregate(reviewModel(), DEMO, ReviewCreated(demoId = DEMO))
        whenEvent(ReviewRejected(demoId = DEMO, reason = "reason"), by = RejectReview::class) {
            expectHandledBy("onReviewRejected")
            expectCommand(RejectDemo(reviewId = DEMO, reason = "reason"), to = DEMO_TYPE.ref(review.ref.tenantId, DEMO))
        }
    }

    private companion object {
        const val DEMO = "demo-1"
        val DEMO_TYPE = DemoModel.TYPE
        val REVIEW_TYPE = ReviewModel.TYPE
    }
}
