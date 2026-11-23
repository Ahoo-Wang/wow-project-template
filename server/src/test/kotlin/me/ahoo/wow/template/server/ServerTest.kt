package me.ahoo.wow.template.server

import me.ahoo.test.asserts.assert
import me.ahoo.wow.manifest.Manifest
import me.ahoo.wow.runtime.node.ServiceNode
import me.ahoo.wow.test.server.wowServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTest {
    private val server = wowServer<Server>("dev")

    @AfterAll
    fun close() {
        server.close()
    }

    @Test
    fun `the server should run one node of the demo context`() {
        val node = server.bean<ServiceNode>()
        node.service.assert().isEqualTo("demo-service")
        node.isReady.assert().isTrue()
        node.hostedContexts.assert().containsExactly("demo")
        server.bean<Manifest>().contexts.single().aggregates.map { it.name }
            .assert().containsExactlyInAnyOrder("demo", "review")
    }
}
