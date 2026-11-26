package me.ahoo.wow.template.server

import kotlinx.coroutines.runBlocking
import me.ahoo.cosec.testkit.IdentityHeader
import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.annotation.ExperimentalWowApi
import me.ahoo.wow.api.caller.Caller
import me.ahoo.wow.api.client.wait
import me.ahoo.wow.api.read.snapshots
import me.ahoo.wow.api.wire.DevelopmentHeaders
import me.ahoo.wow.client.UserCredential
import me.ahoo.wow.cosec.CoSecCallerResolver
import me.ahoo.wow.cosec.CoSecIdentity
import me.ahoo.wow.http.caller.CallerResolver
import me.ahoo.wow.template.api.article.ArticleStateView
import me.ahoo.wow.template.api.article.CreateArticle
import me.ahoo.wow.test.server.wowServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * cosec profile：服务在 CoSec 网关之后，调用方取自网关验证并转发的 CoSec-Identity。
 * 测试扮演网关：为每个调用方写出网关会转发的身份头（[CoSecIdentity.headers]）。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(ExperimentalWowApi::class) // wow-cosec 在 10.0 中是实验性的
class BlogCoSecTest {
    private val server = wowServer<BlogServer>(
        "cosec",
        // 网关后面的节点是部署，不是开发：只有开发 profile 才以内存运行，这里显式声明内存后端。
        properties = mapOf(IdentityHeader.ONLY_GATEWAY_CAN_REACH_KEY to "true", "wow.backend" to "memory"),
    )

    /** 网关转发的凭据：网关验证过的主体及其角色。 */
    private val gateway = UserCredential { caller ->
        CoSecIdentity(caller.subject!!, caller.roles).headers()
    }

    @AfterAll
    fun close() {
        server.close()
    }

    @Test
    fun `the caller should be CoSec's`() {
        server.bean<CallerResolver>().assert().isInstanceOf(CoSecCallerResolver::class.java)
    }

    @Test
    fun `an author should own the article it creates, and an editor read it`() {
        val kate = Caller.of(subject = "kate")
        runBlocking { server.commands(kate, gateway).wait(CreateArticle("Hi", "Body.", "cosec-hi"), ARTICLE) }

        read(kate)!!.scope.ownerId.assert().isEqualTo("kate")
        read(Caller.of(subject = "leo")).assert().isNull()
        read(Callers.EDITOR).assert().isNotNull()
    }

    @Test
    fun `a request without the gateway's identity should be answered 401`() {
        val answer = server.http.post(
            "/blog/article/cosec-mallory/create-article",
            """{"title":"Hi","body":"Body.","slug":"cosec-mallory"}""",
            mapOf(DevelopmentHeaders.USER to "mallory"),
        )
        answer.problem!!.status.assert().isEqualTo(UNAUTHORIZED)
    }

    private fun read(caller: Caller) =
        runBlocking { server.queries(caller, gateway).snapshots<ArticleStateView>().state(ARTICLE) }

    private companion object {
        const val ARTICLE = "article-cosec"
        const val UNAUTHORIZED = 401
    }
}
