package me.ahoo.wow.template.server.demo

import kotlinx.coroutines.runBlocking
import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.caller.Caller
import me.ahoo.wow.api.client.CommandClient
import me.ahoo.wow.api.client.wait
import me.ahoo.wow.api.read.snapshots
import me.ahoo.wow.api.result.FrameworkCodes
import me.ahoo.wow.api.result.WaitPlan
import me.ahoo.wow.template.api.DemoContract
import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.demo.DemoStateView
import me.ahoo.wow.template.api.demo.DemoStatus
import me.ahoo.wow.template.api.demo.UpdateDemo
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.template.api.review.RejectReview
import me.ahoo.wow.template.server.Server
import me.ahoo.wow.test.server.assertRejected
import me.ahoo.wow.test.server.wowServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

/**
 * 端到端验证 命令 → 聚合 → Saga → 跨聚合命令 的完整链路（内存后端）：应用以随机端口启动，
 * 通过类型化客户端发送命令，等待计划（`Wow-Wait`）沿 Saga 派生的命令一直等到目标聚合的快照。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DemoReviewFlowTest {
    private val server = wowServer<Server>("dev")

    @AfterAll
    fun close() {
        server.close()
    }

    @Test
    fun `an approved review should publish its demo`() {
        val demoId = createDemo("hello")
        demo(demoId).status.assert().isEqualTo(DemoStatus.REVIEWING)

        runBlocking { commands().wait(ApproveReview(comment = "LGTM"), demoId) { wait(TO_PUBLISHED) } }
        demo(demoId).status.assert().isEqualTo(DemoStatus.PUBLISHED)

        assertRejected { commands().wait(UpdateDemo(data = "changed"), demoId) }
            .code.assert().isEqualTo(DemoContract.Demo.Errors.WRONG_STATUS)
    }

    @Test
    fun `a rejected review should reject its demo`() {
        val demoId = createDemo("hello")

        runBlocking { commands().wait(RejectReview(reason = "invalid"), demoId) { wait(TO_REJECTED) } }
        demo(demoId).status.assert().isEqualTo(DemoStatus.REJECTED)
    }

    @Test
    fun `blank data should be invalid before the demo decides`() {
        val invalid = server.http.post("/demo/demo/create-demo", """{"data":""}""").problem!!
        invalid.code.assert().isEqualTo(FrameworkCodes.COMMAND_INVALID)
        invalid.violations.single().path.assert().isEqualTo("data")
    }

    @Test
    fun `an internal command should have no public route`() {
        val demoId = createDemo("hello")
        server.http.post("/demo/demo/$demoId/publish-demo", """{"reviewId":"$demoId"}""")
            .status.assert().isEqualTo(NOT_FOUND)
    }

    /** 创建 Demo，并等待审核 Saga 为它创建审核；返回 Demo（也是审核）的 ID。 */
    private fun createDemo(data: String): String {
        val demoId = "demo-" + UUID.randomUUID()
        runBlocking { commands().wait(CreateDemo(data = data), demoId) { wait(TO_REVIEW) } }
        return demoId
    }

    private fun commands(caller: Caller = Caller.ANONYMOUS): CommandClient = server.commands(caller)

    private fun demo(id: String): DemoStateView =
        runBlocking { server.queries(Caller.ANONYMOUS).snapshots<DemoStateView>().state(id) }!!.state

    private companion object {
        const val NOT_FOUND = 404

        /** 创建 Demo 后，等到审核 Saga 派生的 `CreateReview` 写入快照。 */
        val TO_REVIEW: WaitPlan = WaitPlan.of(
            DemoContract.Wait.DEMO_REVIEW_SAGA__ON_DEMO_CREATED,
            DemoContract.Wait.SNAPSHOT,
        )
        val TO_PUBLISHED: WaitPlan = WaitPlan.of(
            DemoContract.Wait.DEMO_REVIEW_SAGA__ON_REVIEW_APPROVED,
            DemoContract.Wait.SNAPSHOT,
        )
        val TO_REJECTED: WaitPlan = WaitPlan.of(
            DemoContract.Wait.DEMO_REVIEW_SAGA__ON_REVIEW_REJECTED,
            DemoContract.Wait.SNAPSHOT,
        )
    }
}
