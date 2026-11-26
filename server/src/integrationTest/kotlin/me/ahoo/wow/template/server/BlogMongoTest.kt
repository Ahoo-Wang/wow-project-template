package me.ahoo.wow.template.server

import kotlinx.coroutines.runBlocking
import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.caller.Caller
import me.ahoo.wow.api.client.wait
import me.ahoo.wow.api.read.snapshots
import me.ahoo.wow.api.result.WaitPlan
import me.ahoo.wow.spring.boot.store.HostBackends
import me.ahoo.wow.template.api.BlogContract
import me.ahoo.wow.template.api.article.ArticleStateView
import me.ahoo.wow.template.api.article.ArticleStatus
import me.ahoo.wow.template.api.article.CreateArticle
import me.ahoo.wow.template.api.article.SubmitArticle
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.test.server.assertRejected
import me.ahoo.wow.test.server.wowServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.mongodb.MongoDBContainer

/**
 * 在真实存储上运行服务：容器中的 MongoDB 副本集（Wow 的 MongoDB 后端需要副本集）。
 * 覆盖内存后端测不到的部分：日志、快照、slug 预留与查询索引都落在 MongoDB 上。
 *
 * 不属于 `check`：`./gradlew integrationTest`，需要 Docker。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogMongoTest {
    private val mongo = MongoDBContainer("mongo:7.0").withReplicaSet().also { it.start() }
    private val server = wowServer<BlogServer>(
        "mongo",
        "dev",
        properties = mapOf("spring.mongodb.uri" to mongo.replicaSetUrl),
    )

    @AfterAll
    fun close() {
        try {
            server.close()
        } finally {
            mongo.stop()
        }
    }

    @Test
    fun `the node should run on MongoDB`() {
        server.bean<HostBackends>().type.assert().isEqualTo("mongo")
    }

    @Test
    fun `an article should be reviewed and published on MongoDB, its slug unique there`() {
        val alice = Caller.owner("alice")
        runBlocking { server.commands(alice).wait(CreateArticle("Hello", "Body.", "hello-mongo"), ARTICLE) }
        val review = runBlocking {
            server.commands(alice).wait(SubmitArticle, ARTICLE) { wait(TO_REVIEW) }
        }.ref.id
        runBlocking {
            server.commands(EDITOR).wait(ApproveReview("LGTM"), review) { wait(TO_PUBLISHED) }
        }
        runBlocking { server.queries(alice).snapshots<ArticleStateView>().state(ARTICLE) }!!.state.status
            .assert().isEqualTo(ArticleStatus.PUBLISHED)

        assertRejected { server.commands(Caller.owner("bob")).wait(CreateArticle("Mine", "Body.", "hello-mongo")) }
            .code.assert().isEqualTo(BlogContract.Article.Errors.SLUG_TAKEN)
    }

    private companion object {
        const val ARTICLE = "article-mongo"
        val TO_REVIEW: WaitPlan = WaitPlan.of(
            BlogContract.Wait.ARTICLE_REVIEW_SAGA__ON_ARTICLE_SUBMITTED,
            BlogContract.Wait.SNAPSHOT,
        )
        val TO_PUBLISHED: WaitPlan = WaitPlan.of(
            BlogContract.Wait.ARTICLE_REVIEW_SAGA__ON_REVIEW_APPROVED,
            BlogContract.Wait.SNAPSHOT,
        )
        val EDITOR: Caller = Caller.of(subject = "erin", roles = listOf(BlogAuthorization.EDITOR))
    }
}
