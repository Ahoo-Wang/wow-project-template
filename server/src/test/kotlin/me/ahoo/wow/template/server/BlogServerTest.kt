package me.ahoo.wow.template.server

import me.ahoo.test.asserts.assert
import me.ahoo.wow.manifest.Manifest
import me.ahoo.wow.runtime.node.ServiceNode
import me.ahoo.wow.template.domain.review.ReviewPolicy
import me.ahoo.wow.test.server.wowServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.time.Duration.Companion.days

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogServerTest {
    private val server = wowServer<BlogServer>("dev")

    @AfterAll
    fun close() {
        server.close()
    }

    @Test
    fun `the server should run one node of the blog context`() {
        val node = server.bean<ServiceNode>()
        node.service.assert().isEqualTo("blog-service")
        node.isReady.assert().isTrue()
        node.hostedContexts.assert().containsExactly("blog")
        server.bean<Manifest>().contexts.single().aggregates.map { it.name }
            .assert().containsExactlyInAnyOrder("article", "review")
    }

    @Test
    fun `the review policy should take the configured timeout`() {
        server.bean<ReviewPolicy>().timeout.assert().isEqualTo(7.days)
    }
}
