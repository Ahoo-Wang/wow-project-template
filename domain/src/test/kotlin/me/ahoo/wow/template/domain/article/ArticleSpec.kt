package me.ahoo.wow.template.domain.article

import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.caller.Caller
import me.ahoo.wow.api.result.FrameworkCodes
import me.ahoo.wow.template.api.article.ArticleCreated
import me.ahoo.wow.template.api.article.ArticleEdited
import me.ahoo.wow.template.api.article.ArticlePublished
import me.ahoo.wow.template.api.article.ArticleReturned
import me.ahoo.wow.template.api.article.ArticleStatus
import me.ahoo.wow.template.api.article.ArticleSubmitted
import me.ahoo.wow.template.api.article.CreateArticle
import me.ahoo.wow.template.api.article.DeleteArticle
import me.ahoo.wow.template.api.article.EditArticle
import me.ahoo.wow.template.api.article.PublishArticle
import me.ahoo.wow.template.api.article.ReturnArticle
import me.ahoo.wow.template.api.article.SubmitArticle
import me.ahoo.wow.test.AggregateSpec
import me.ahoo.wow.test.aggregateSpec
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * 文章的状态机：编译器生成的模型（[articleModel]）运行在内核上，不需要 Spring 与存储。
 *
 * 一个 spec 是一条命令序列：连续的 `whenCommand` 依次作用于上一条留下的聚合；从同一起点分叉用 `fork`。
 */
class ArticleSpec {
    private val model = articleModel()

    private fun articleSpec(block: AggregateSpec<ArticleState>.() -> Unit) =
        aggregateSpec(model, id = ARTICLE, start = START, block = block)

    @Test
    fun `creating an article should reserve its slug and start as a draft`() = articleSpec {
        whenCommand(CreateArticle(TITLE, BODY, SLUG)) {
            expectEvents(created)
            expectState {
                slug.assert().isEqualTo(SLUG)
                status.assert().isEqualTo(ArticleStatus.DRAFT)
            }
        }
        holds(Article.SLUGS, SLUG).assert().isTrue()
    }

    @Test
    fun `a slug another article holds should be refused`() = articleSpec {
        givenReservation(Article.SLUGS, SLUG, ArticleModel.TYPE.ref(Caller.DEFAULT_TENANT, "another-article"))
        whenCommand(CreateArticle(TITLE, BODY, SLUG)) { expectRejected(Article.Rejections.SLUG_TAKEN) }
    }

    @Test
    fun `a draft should be edited, and the same text should change nothing`() = articleSpec {
        given(created)
        whenCommand(EditArticle(TITLE, BODY)) { expectUnchanged() }
        whenCommand(EditArticle("Hello, Wow 10", BODY)) {
            expectEvents(ArticleEdited("Hello, Wow 10", BODY))
            expectState { title.assert().isEqualTo("Hello, Wow 10") }
        }
    }

    @Test
    fun `a submitted article should be published or returned, and nothing else`() = articleSpec {
        given(created)
        whenCommand(SubmitArticle) {
            expectEvents(ArticleSubmitted)
            expectState { status.assert().isEqualTo(ArticleStatus.IN_REVIEW) }
        }
        fork("edited in review") {
            whenCommand(EditArticle("Changed", BODY)) { expectRejected(Article.Rejections.WRONG_STATUS) }
        }
        fork("submitted twice") {
            whenCommand(SubmitArticle) { expectRejected(Article.Rejections.WRONG_STATUS) }
        }
        fork("published") {
            whenCommand(PublishArticle(REVIEW)) {
                expectEvents(ArticlePublished(REVIEW))
                expectState {
                    status.assert().isEqualTo(ArticleStatus.PUBLISHED)
                    reviewId.assert().isEqualTo(REVIEW)
                    // 发布时间取自提交时间：spec 的时钟从 START 开始。
                    publishedAt.assert().isEqualTo(START.toEpochMilli())
                }
            }
            whenCommand(ReturnArticle(REVIEW, "too late")) { expectRejected(Article.Rejections.WRONG_STATUS) }
        }
        fork("returned, then edited and submitted again") {
            whenCommand(ReturnArticle(REVIEW, "Needs an example.")) {
                expectEvents(ArticleReturned(REVIEW, "Needs an example."))
                expectState { status.assert().isEqualTo(ArticleStatus.DRAFT) }
            }
            whenCommand(EditArticle(TITLE, "$BODY With an example.")) { expectEventTypes(ArticleEdited::class) }
            whenCommand(SubmitArticle) { expectEvents(ArticleSubmitted) }
        }
    }

    @Test
    fun `a draft should not be published`() = articleSpec {
        given(created)
        whenCommand(PublishArticle(REVIEW)) { expectRejected(Article.Rejections.WRONG_STATUS) }
    }

    @Test
    fun `deleting an article should release its slug, and the article should take no more commands`() = articleSpec {
        givenCommands(CreateArticle(TITLE, BODY, SLUG))
        whenCommand(DeleteArticle) { expectDeleted() }
        holds(Article.SLUGS, SLUG).assert().isFalse()
        whenCommand(EditArticle("After", BODY)) { expectRejected(FrameworkCodes.AGGREGATE_DELETED) }
    }

    @Test
    fun `an article under review should be deleted, as its author withdraws it`() = articleSpec {
        givenCommands(CreateArticle(TITLE, BODY, SLUG), SubmitArticle)
        whenCommand(DeleteArticle) { expectDeleted() }
    }

    private companion object {
        const val ARTICLE = "article-1"
        const val REVIEW = "review-1"
        const val TITLE = "Hello, Wow"
        const val BODY = "Event sourcing, step by step."
        const val SLUG = "hello-wow"
        val START: Instant = Instant.parse("2026-01-01T00:00:00Z")
        val created = ArticleCreated(TITLE, BODY, SLUG)
    }
}
