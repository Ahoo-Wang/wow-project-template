package me.ahoo.wow.template.domain.review

import me.ahoo.test.asserts.assert
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.PublishDemo
import me.ahoo.wow.template.api.demo.RejectDemo
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.test.SagaSpec

class DemoReviewSagaSpec : SagaSpec<DemoReviewSaga>({
    on {
        whenEvent(DemoCreated(data = "data")) {
            expectNoError()
            expectCommandType(CreateReview::class)
            expectCommand<CreateReview> {
                aggregateId.id.assert().isEqualTo(body.demoId)
            }
        }
    }
    on {
        whenEvent(ReviewApproved(demoId = "demoId", comment = "LGTM")) {
            expectNoError()
            expectCommandType(PublishDemo::class)
            expectCommand<PublishDemo> {
                aggregateId.id.assert().isEqualTo("demoId")
            }
        }
    }
    on {
        whenEvent(ReviewRejected(demoId = "demoId", reason = "reason")) {
            expectNoError()
            expectCommandType(RejectDemo::class)
            expectCommand<RejectDemo> {
                aggregateId.id.assert().isEqualTo("demoId")
                body.reason.assert().isEqualTo("reason")
            }
        }
    }
})
