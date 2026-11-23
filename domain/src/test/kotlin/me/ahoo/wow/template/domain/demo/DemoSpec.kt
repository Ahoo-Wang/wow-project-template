package me.ahoo.wow.template.domain.demo

import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.lifecycle.DeleteAggregate
import me.ahoo.wow.api.lifecycle.RestoreAggregate
import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoPublished
import me.ahoo.wow.template.api.demo.DemoRejected
import me.ahoo.wow.template.api.demo.DemoStatus
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.PublishDemo
import me.ahoo.wow.template.api.demo.RejectDemo
import me.ahoo.wow.template.api.demo.UpdateDemo
import me.ahoo.wow.test.aggregateSpec
import org.junit.jupiter.api.Test

/**
 * Demo 的状态机：编译器生成的模型（[demoModel]）运行在内核上。一个 spec 是一条命令序列，
 * 连续的 `whenCommand` 依次作用于上一条留下的聚合；需要从同一起点分叉时用 `fork`。
 */
class DemoSpec {
    private val model = demoModel()

    @Test
    fun `creating a demo should await its review`() = aggregateSpec(model, id = DEMO) {
        whenCommand(CreateDemo(data = "data")) {
            expectEvents(DemoCreated(data = "data"))
            expectState {
                data.assert().isEqualTo("data")
                status.assert().isEqualTo(DemoStatus.REVIEWING)
            }
        }
    }

    @Test
    fun `a reviewing demo should be updated, published or rejected`() = aggregateSpec(model, id = DEMO) {
        given(DemoCreated(data = "data"))
        fork("update") {
            whenCommand(UpdateDemo(data = "newData")) {
                expectEvents(DemoUpdated(data = "newData"))
                expectState { data.assert().isEqualTo("newData") }
            }
        }
        fork("update with unchanged data") {
            whenCommand(UpdateDemo(data = "data")) {
                expectRejected(Demo.Rejections.DATA_UNCHANGED)
            }
        }
        fork("publish") {
            whenCommand(PublishDemo(reviewId = DEMO)) {
                expectEvents(DemoPublished(reviewId = DEMO))
                expectState { status.assert().isEqualTo(DemoStatus.PUBLISHED) }
                fork("update after published") {
                    whenCommand(UpdateDemo(data = "newData")) { expectRejected(Demo.Rejections.WRONG_STATUS) }
                }
                fork("publish twice") {
                    whenCommand(PublishDemo(reviewId = DEMO)) { expectRejected(Demo.Rejections.WRONG_STATUS) }
                }
            }
        }
        fork("reject") {
            whenCommand(RejectDemo(reviewId = DEMO, reason = "reason")) {
                expectEvents(DemoRejected(reviewId = DEMO, reason = "reason"))
                expectState { status.assert().isEqualTo(DemoStatus.REJECTED) }
                fork("reject twice") {
                    whenCommand(RejectDemo(reviewId = DEMO, reason = "reason")) {
                        expectRejected(Demo.Rejections.WRONG_STATUS)
                    }
                }
            }
        }
    }

    @Test
    fun `a deleted demo should be restored`() = aggregateSpec(model, id = DEMO) {
        given(DemoCreated(data = "data"))
        whenCommand(DeleteAggregate()) { expectDeleted() }
        whenCommand(RestoreAggregate()) {
            expectState { data.assert().isEqualTo("data") }
        }
    }

    private companion object {
        const val DEMO = "demo-1"
    }
}
