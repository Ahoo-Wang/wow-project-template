package me.ahoo.wow.template.domain.review

import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.result.FrameworkCodes
import me.ahoo.wow.template.api.article.ArticleCreated
import me.ahoo.wow.template.api.article.ArticleSubmitted
import me.ahoo.wow.template.api.article.PublishArticle
import me.ahoo.wow.template.api.article.ReturnArticle
import me.ahoo.wow.template.api.article.SubmitArticle
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.template.api.review.CreateReview
import me.ahoo.wow.template.api.review.ExpireReview
import me.ahoo.wow.template.api.review.RejectReview
import me.ahoo.wow.template.api.review.ReviewApproved
import me.ahoo.wow.template.api.review.ReviewCreated
import me.ahoo.wow.template.api.review.ReviewExpired
import me.ahoo.wow.template.api.review.ReviewRejected
import me.ahoo.wow.template.domain.article.Article
import me.ahoo.wow.template.domain.article.ArticleModel
import me.ahoo.wow.template.domain.article.articleModel
import me.ahoo.wow.test.sagaSpec
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days

/**
 * 审核 Saga：把聚合的提交交给读取它的处理函数（与订阅一致），校验派生的命令及其目标；
 * `expectDeterministic()` 再处理一次同一提交，期望派生出同样的命令，即 Saga 的幂等性。
 */
class ArticleReviewSagaSpec {
    private val handlers = articleReviewSagaHandlers()
    private val articles = articleModel()
    private val reviews = reviewModel(reviewPolicy = ReviewPolicy(7.days))

    @Test
    fun `a submitted article should open a review of it`() = sagaSpec(handlers) {
        givenAggregate(articles, ARTICLE, ArticleCreated("Hello, Wow", "Body.", "hello-wow"))
        whenEvent(ArticleSubmitted, by = SubmitArticle::class) {
            expectHandledBy("onArticleSubmitted")
            expectCommand<CreateReview> { articleId.assert().isEqualTo(ARTICLE) }
            expectDeterministic()
        }
    }

    @Test
    fun `an approved review should publish its article`() = sagaSpec(handlers) {
        val review = givenAggregate(reviews, REVIEW, ReviewCreated(ARTICLE))
        whenEvent(ReviewApproved(ARTICLE, "LGTM"), by = ApproveReview::class) {
            expectHandledBy("onReviewApproved")
            expectCommand(PublishArticle(REVIEW), to = ArticleModel.TYPE.ref(review.ref.tenantId, ARTICLE))
            expectDeterministic()
        }
    }

    @Test
    fun `a rejected review should return its article with the reason`() = sagaSpec(handlers) {
        val review = givenAggregate(reviews, REVIEW, ReviewCreated(ARTICLE))
        whenEvent(ReviewRejected(ARTICLE, "Needs an example."), by = RejectReview::class) {
            expectHandledBy("onReviewRejected")
            expectCommand(
                ReturnArticle(REVIEW, "Needs an example."),
                to = ArticleModel.TYPE.ref(review.ref.tenantId, ARTICLE)
            )
        }
    }

    @Test
    fun `an expired review should return its article`() = sagaSpec(handlers) {
        val review = givenAggregate(reviews, REVIEW, ReviewCreated(ARTICLE))
        whenEvent(ReviewExpired(ARTICLE), by = ExpireReview::class) {
            expectHandledBy("onReviewExpired")
            expectCommand(
                ReturnArticle(REVIEW, ArticleReviewSaga.EXPIRED_REASON),
                to = ArticleModel.TYPE.ref(review.ref.tenantId, ARTICLE)
            )
        }
    }

    @Test
    fun `a publication refused because the article was deleted should need no compensation`() =
        sagaSpec(handlers) {
            givenAggregate(reviews, REVIEW, ReviewCreated(ARTICLE))
            whenEvent(ReviewApproved(ARTICLE, "LGTM"), by = ApproveReview::class) {
                whenRejected(commands.single(), FrameworkCodes.AGGREGATE_DELETED) {
                    expectHandledBy("onPublishRejected")
                    expectNoCommand()
                }
            }
        }

    @Test
    fun `a return refused because the article was deleted should need no compensation`() = sagaSpec(handlers) {
        givenAggregate(reviews, REVIEW, ReviewCreated(ARTICLE))
        whenEvent(ReviewRejected(ARTICLE, "No."), by = RejectReview::class) {
            whenRejected(commands.single(), FrameworkCodes.AGGREGATE_DELETED) {
                expectHandledBy("onReturnRejected")
                expectNoCommand()
            }
        }
    }

    /** 预期之外的拒绝不被吞掉：处理失败，订阅随后重试并最终停放，交给运维排查。 */
    @Test
    fun `an unexpected rejection should fail the handling, not be accepted`() = sagaSpec(handlers) {
        givenAggregate(reviews, REVIEW, ReviewCreated(ARTICLE))
        whenEvent(ReviewApproved(ARTICLE, "LGTM"), by = ApproveReview::class) {
            whenRejected(commands.single(), Article.Rejections.WRONG_STATUS) {
                expectError<IllegalStateException> {
                    message.assert().contains("accepts wow.aggregate.deleted", "blog.article.wrong-status")
                }
            }
        }
    }

    private companion object {
        const val ARTICLE = "article-1"
        const val REVIEW = "review-1"
    }
}
