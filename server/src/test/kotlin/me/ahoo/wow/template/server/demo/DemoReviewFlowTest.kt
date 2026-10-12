package me.ahoo.wow.template.server.demo

import me.ahoo.test.asserts.assert
import me.ahoo.wow.template.api.demo.DemoStatus
import me.ahoo.wow.template.api.demo.DemoView
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.Duration

/**
 * 端到端验证 HTTP 命令 → 聚合 → Saga → 跨聚合命令 的完整链路（内存基础设施）。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoReviewFlowTest {
    companion object {
        private const val TENANT_ID = "test"
        private const val WAIT_STAGE = "Command-Wait-Stage"
        private val AWAIT_TIMEOUT: Duration = Duration.ofSeconds(5)
    }

    @LocalServerPort
    private var port: Int = 0

    private lateinit var client: WebTestClient

    @BeforeEach
    fun setup() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    @Test
    fun `approved review publishes demo`() {
        val demoId = createDemo("hello")
        awaitDemoStatus(demoId, DemoStatus.REVIEWING)

        sendCommand("/tenant/$TENANT_ID/review/$demoId/approve", """{"comment":"LGTM"}""")
            .expectStatus().isOk

        awaitDemoStatus(demoId, DemoStatus.PUBLISHED)
        sendCommand("/tenant/$TENANT_ID/demo/$demoId", """{"data":"changed"}""")
            .expectStatus().isBadRequest
    }

    @Test
    fun `rejected review rejects demo`() {
        val demoId = createDemo("hello")
        awaitDemoStatus(demoId, DemoStatus.REVIEWING)

        sendCommand("/tenant/$TENANT_ID/review/$demoId/reject", """{"reason":"invalid"}""")
            .expectStatus().isOk

        awaitDemoStatus(demoId, DemoStatus.REJECTED)
    }

    @Test
    fun `blank data is rejected by validation`() {
        client.post().uri("/tenant/$TENANT_ID/demo")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"data":""}""")
            .exchange()
            .expectStatus().isBadRequest
    }

    private fun createDemo(data: String): String {
        val result = client.post().uri("/tenant/$TENANT_ID/demo")
            .contentType(MediaType.APPLICATION_JSON)
            .header(WAIT_STAGE, "PROCESSED")
            .bodyValue("""{"data":"$data"}""")
            .exchange()
            .expectStatus().isOk
            .expectBody<Map<String, Any>>()
            .returnResult()
            .responseBody!!
        return result["aggregateId"] as String
    }

    private fun sendCommand(uri: String, body: String): WebTestClient.ResponseSpec {
        return client.put().uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .header(WAIT_STAGE, "PROCESSED")
            .bodyValue(body)
            .exchange()
    }

    /**
     * Saga 驱动的后续命令是异步的，轮询聚合状态直到达到期望值。
     */
    private fun awaitDemoStatus(demoId: String, expected: DemoStatus) {
        val deadline = System.nanoTime() + AWAIT_TIMEOUT.toNanos()
        var actual: DemoStatus?
        do {
            actual = client.get().uri("/tenant/$TENANT_ID/demo/$demoId/state")
                .exchange()
                .returnResult(DemoView::class.java)
                .responseBody
                .blockFirst()
                ?.status
            if (actual == expected) return
            Thread.sleep(50)
        } while (System.nanoTime() < deadline)
        actual.assert().isEqualTo(expected)
    }
}
