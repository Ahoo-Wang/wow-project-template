package me.ahoo.wow.template.server

import kotlinx.coroutines.runBlocking
import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.caller.Caller
import me.ahoo.wow.api.client.wait
import me.ahoo.wow.api.read.Snapshot
import me.ahoo.wow.api.read.snapshots
import me.ahoo.wow.api.result.FrameworkCodes
import me.ahoo.wow.api.result.WaitPlan
import me.ahoo.wow.template.api.BlogContract
import me.ahoo.wow.template.api.article.ArticleStateView
import me.ahoo.wow.template.api.article.ArticleStatus
import me.ahoo.wow.template.api.article.CreateArticle
import me.ahoo.wow.template.api.article.DeleteArticle
import me.ahoo.wow.template.api.article.EditArticle
import me.ahoo.wow.template.api.article.RestoreArticle
import me.ahoo.wow.template.api.article.SubmitArticle
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.template.api.review.RejectReview
import me.ahoo.wow.template.api.review.ReviewStateView
import me.ahoo.wow.template.api.review.ReviewStatus
import me.ahoo.wow.template.server.Callers.EDITOR
import me.ahoo.wow.template.server.Callers.author
import me.ahoo.wow.test.server.assertRejected
import me.ahoo.wow.test.server.wowServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

/**
 * 服务端到端：应用以随机端口启动（内存后端），测试以不同调用方用类型化客户端发送命令。
 * 等待计划（`Wow-Wait`）沿 Saga 派生的命令一直等到目标聚合的快照，所以不需要轮询。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArticleReviewFlowTest {
    private val server = wowServer<BlogServer>("dev")

    @AfterAll
    fun close() {
        server.close()
    }

    @Test
    fun `an article an editor approves should be published`() {
        val alice = author("alice")
        val article = create(alice)
        val review = submit(article, alice)
        review(review).articleId.assert().isEqualTo(article)

        runBlocking { server.commands(EDITOR).wait(ApproveReview("LGTM"), review) { wait(TO_PUBLISHED) } }

        val published = read(article, alice)!!
        published.state.status.assert().isEqualTo(ArticleStatus.PUBLISHED)
        published.state.reviewId.assert().isEqualTo(review)
        published.scope.ownerId.assert().isEqualTo("alice")
        review(review).status.assert().isEqualTo(ReviewStatus.APPROVED)
    }

    @Test
    fun `a rejected article should go back to its author, who edits and submits it again`() {
        val bob = author("bob")
        val article = create(bob)
        val first = submit(article, bob)

        runBlocking { server.commands(EDITOR).wait(RejectReview("Needs an example."), first) { wait(TO_RETURNED) } }
        read(article, bob)!!.state.status.assert().isEqualTo(ArticleStatus.DRAFT)

        runBlocking { server.commands(bob).wait(EditArticle("Hello, Wow", "Now with an example."), article) }
        val second = submit(article, bob)
        second.assert().isNotEqualTo(first)
        review(second).status.assert().isEqualTo(ReviewStatus.PENDING)
    }

    @Test
    fun `a slug taken in the tenant should be refused`() {
        val slug = slug()
        create(author("carol"), slug)
        assertRejected { server.commands(author("dave")).wait(CreateArticle("Mine", "Body.", slug)) }
            .code.assert().isEqualTo(BlogContract.Article.Errors.SLUG_TAKEN)
    }

    @Test
    fun `another author should neither read nor change the article`() {
        val article = create(author("erika"))
        val frank = author("frank")
        read(article, frank).assert().isNull()
        assertRejected { server.commands(frank).wait(EditArticle("Mine now", "Body."), article) }
            .code.assert().isEqualTo(FrameworkCodes.AGGREGATE_NOT_FOUND)
    }

    @Test
    fun `only an editor should decide a review`() {
        val gina = author("gina")
        val review = submit(create(gina), gina)
        val refused = assertRejected { server.commands(gina).wait(ApproveReview("Looks great to me"), review) }
        refused.code.assert().isEqualTo(FrameworkCodes.AUTH_FORBIDDEN)
        review(review).status.assert().isEqualTo(ReviewStatus.PENDING)
    }

    @Test
    fun `a deleted article should free its slug, and be restored once the slug is free again`() {
        val slug = slug()
        val lena = author("lena")
        val mike = author("mike")
        val article = create(lena, slug)
        runBlocking { server.commands(lena).wait(DeleteArticle, article) }

        // 删除释放了 slug：别人可以用它，此时恢复被拒绝。
        val taken = create(mike, slug)
        assertRejected { server.commands(lena).wait(RestoreArticle, article) }
            .code.assert().isEqualTo(BlogContract.Article.Errors.SLUG_TAKEN)

        // 对方删除后 slug 再次空出，恢复成功并重新预留 slug。
        runBlocking { server.commands(mike).wait(DeleteArticle, taken) }
        runBlocking { server.commands(lena).wait(RestoreArticle, article) }
        read(article, lena)!!.deleted.assert().isFalse()
        assertRejected { server.commands(author("nora")).wait(CreateArticle("Mine", "Body.", slug)) }
            .code.assert().isEqualTo(BlogContract.Article.Errors.SLUG_TAKEN)

        // 内置的恢复不重新预留 slug，所以文章不开放它。
        server.http.post("/blog/article/$article/wow.lifecycle.restore-aggregate", "{}", caller = lena)
            .status.assert().isEqualTo(NOT_FOUND)
    }

    @Test
    fun `restoring an article that is not deleted should change nothing, and keep its slug`() {
        val slug = slug()
        val olga = author("olga")
        val article = create(olga, slug)
        val result = runBlocking { server.commands(olga).wait(RestoreArticle, article) }
        result.version.assert().isEqualTo(1)
        assertRejected { server.commands(author("paul")).wait(CreateArticle("Mine", "Body.", slug)) }
            .code.assert().isEqualTo(BlogContract.Article.Errors.SLUG_TAKEN)
    }

    /** 审核不开放生命周期命令：谁删了审核，文章就会一直停在审核中。 */
    @Test
    fun `nobody should delete a review`() {
        val nina = author("nina")
        val review = submit(create(nina), nina)
        for (caller in listOf(nina, EDITOR)) {
            server.http.request("DELETE", "/blog/review/$review", caller = caller)
                .status.assert().isEqualTo(NOT_FOUND)
        }
        review(review).status.assert().isEqualTo(ReviewStatus.PENDING)
    }

    /** Wow 的运维命令（这里是快照重建，由 Manifest 列出路由）同样只有编辑能发送。 */
    @Test
    fun `only an editor should rebuild snapshots`() {
        val rebuild = "/blog/article/wow.snapshot.rebuild-snapshot"
        val refused = server.http.post(rebuild, """{"ids":["article-1"]}""", caller = author("kim")).problem!!
        refused.code.assert().isEqualTo(FrameworkCodes.AUTH_FORBIDDEN)
        server.http.post(rebuild, """{"ids":["article-1"]}""", caller = EDITOR).problem.assert().isNull()
    }

    /** 命令的形状在准入时校验，早于聚合决策：400 `wow.command.invalid`，逐条列出违反的约束。 */
    @Test
    fun `an invalid command should be refused at admission, naming each violation`() {
        val answer = server.http.post(
            "/blog/article/create-article",
            """{"title":"","body":"Body.","slug":"Not A Slug"}""",
            caller = author("hank"),
        )
        val problem = answer.problem!!
        problem.status.assert().isEqualTo(BAD_REQUEST)
        problem.code.assert().isEqualTo(FrameworkCodes.COMMAND_INVALID)
        problem.violations.map { it.path to it.code }
            .assert().containsExactlyInAnyOrder("slug" to "pattern", "title" to "not-blank")
    }

    @Test
    fun `an internal command should have no public route`() {
        val article = create(author("ivy"))
        server.http.post("/blog/article/$article/publish-article", """{"reviewId":"r"}""", caller = EDITOR)
            .status.assert().isEqualTo(NOT_FOUND)
    }

    /** 以 [author] 创建一篇文章，返回它的 ID。 */
    private fun create(author: Caller, slug: String = slug()): String {
        val id = "article-" + UUID.randomUUID()
        runBlocking { server.commands(author).wait(CreateArticle("Hello, Wow", "Event sourcing.", slug), id) }
        return id
    }

    /** 提交文章，并等待审核 Saga 开启的审核写入快照；返回审核的 ID。 */
    private fun submit(article: String, author: Caller): String =
        runBlocking { server.commands(author).wait(SubmitArticle, article) { wait(TO_REVIEW) } }.ref.id

    private fun read(article: String, caller: Caller): Snapshot<ArticleStateView>? =
        runBlocking { server.queries(caller).snapshots<ArticleStateView>().state(article) }

    private fun review(review: String): ReviewStateView =
        runBlocking { server.queries(EDITOR).snapshots<ReviewStateView>().state(review) }!!.state

    private fun slug() = "hello-" + UUID.randomUUID().toString().take(8)

    private companion object {
        const val BAD_REQUEST = 400
        const val NOT_FOUND = 404

        val TO_REVIEW: WaitPlan = WaitPlan.of(
            BlogContract.Wait.ARTICLE_REVIEW_SAGA__ON_ARTICLE_SUBMITTED,
            BlogContract.Wait.SNAPSHOT,
        )
        val TO_PUBLISHED: WaitPlan = WaitPlan.of(
            BlogContract.Wait.ARTICLE_REVIEW_SAGA__ON_REVIEW_APPROVED,
            BlogContract.Wait.SNAPSHOT,
        )
        val TO_RETURNED: WaitPlan = WaitPlan.of(
            BlogContract.Wait.ARTICLE_REVIEW_SAGA__ON_REVIEW_REJECTED,
            BlogContract.Wait.SNAPSHOT,
        )
    }
}
