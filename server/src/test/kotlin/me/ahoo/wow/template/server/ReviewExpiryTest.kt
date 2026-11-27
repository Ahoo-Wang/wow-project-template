package me.ahoo.wow.template.server

import kotlinx.coroutines.runBlocking
import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.caller.Caller
import me.ahoo.wow.api.client.wait
import me.ahoo.wow.api.read.snapshots
import me.ahoo.wow.api.result.WaitPlan
import me.ahoo.wow.template.api.BlogContract
import me.ahoo.wow.template.api.article.ArticleStateView
import me.ahoo.wow.template.api.article.ArticleStatus
import me.ahoo.wow.template.api.article.CreateArticle
import me.ahoo.wow.template.api.article.SubmitArticle
import me.ahoo.wow.template.api.review.ReviewStateView
import me.ahoo.wow.template.api.review.ReviewStatus
import me.ahoo.wow.template.server.Callers.EDITOR
import me.ahoo.wow.template.server.Callers.author
import me.ahoo.wow.test.server.eventually
import me.ahoo.wow.test.server.wowServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * 审核超时：定时命令由节点的定时器在到期后发送，没有哪个命令的等待计划能看到它，所以用 `eventually` 轮询结果。
 * 这里把时限缩短到 2 秒、定时器轮询间隔缩短到 200 毫秒。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewExpiryTest {
    private val server = wowServer<BlogServer>(
        "dev",
        properties = mapOf(
            "blog.review.timeout" to "2s",
            "wow.timers.poll-interval" to "200ms",
        ),
    )

    @AfterAll
    fun close() {
        server.close()
    }

    @Test
    fun `a review no editor decided in time should expire and return its article`() {
        val june = author("june")
        runBlocking { server.commands(june).wait(CreateArticle("Late", "Body.", "late-review"), ARTICLE) }
        val review = runBlocking {
            server.commands(june).wait(SubmitArticle, ARTICLE) { wait(TO_REVIEW) }
        }.ref.id

        eventually("the article returned to its author") {
            article(june).status == ArticleStatus.DRAFT
        }
        article(june).reviewId.assert().isEqualTo(review)
        runBlocking { server.queries(EDITOR).snapshots<ReviewStateView>().state(review) }!!.state.status
            .assert().isEqualTo(ReviewStatus.EXPIRED)
    }

    private fun article(caller: Caller): ArticleStateView =
        runBlocking { server.queries(caller).snapshots<ArticleStateView>().state(ARTICLE) }!!.state

    private companion object {
        const val ARTICLE = "article-expiring"

        /** 提交文章后，等到审核 Saga 开启的审核写入快照。 */
        val TO_REVIEW: WaitPlan = WaitPlan.of(
            BlogContract.Wait.ARTICLE_REVIEW_SAGA__ON_ARTICLE_SUBMITTED,
            BlogContract.Wait.SNAPSHOT,
        )
    }
}
