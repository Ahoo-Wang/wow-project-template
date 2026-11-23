package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoPublished
import me.ahoo.wow.template.api.demo.DemoRejected
import me.ahoo.wow.template.api.demo.DemoStatus
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.IDemoInfo

/**
 * Demo 的状态：只由事件溯源函数（`onSourcing`）修改。聚合 ID 不是状态的一部分（快照的 `aggregateId`、命令的 `ref.id`）。
 */
class DemoState : IDemoInfo {
    override var data: String = ""
        private set
    var status: DemoStatus = DemoStatus.REVIEWING
        private set

    fun onSourcing(event: DemoCreated) {
        data = event.data
        status = DemoStatus.REVIEWING
    }

    fun onSourcing(event: DemoUpdated) {
        data = event.data
    }

    @Suppress("UnusedParameter")
    fun onSourcing(event: DemoPublished) {
        status = DemoStatus.PUBLISHED
    }

    @Suppress("UnusedParameter")
    fun onSourcing(event: DemoRejected) {
        status = DemoStatus.REJECTED
    }
}
