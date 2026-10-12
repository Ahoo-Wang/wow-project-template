package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.api.annotation.AggregateRoot
import me.ahoo.wow.api.annotation.OnCommand
import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoPublished
import me.ahoo.wow.template.api.demo.DemoRejected
import me.ahoo.wow.template.api.demo.DemoStatus
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.PublishDemo
import me.ahoo.wow.template.api.demo.RejectDemo
import me.ahoo.wow.template.api.demo.UpdateDemo

/**
 * Demo 聚合根：只负责基于当前状态校验不变量并产生事件，状态变更由 [DemoState] 通过事件溯源完成。
 *
 * 删除/恢复使用 Wow 内置的 `DefaultDeleteAggregate` / `DefaultRecoverAggregate`，无需在此声明。
 */
@Suppress("unused")
@AggregateRoot
class Demo(private val state: DemoState) {

    @OnCommand
    fun onCreate(command: CreateDemo): DemoCreated {
        return DemoCreated(data = command.data)
    }

    @OnCommand
    fun onUpdate(command: UpdateDemo): DemoUpdated {
        check(state.status != DemoStatus.PUBLISHED) { "Demo[${state.id}] is published and cannot be updated." }
        require(command.data != state.data) { "Demo[${state.id}] data is unchanged." }
        return DemoUpdated(data = command.data)
    }

    @OnCommand
    fun onPublish(command: PublishDemo): DemoPublished {
        checkReviewing()
        return DemoPublished(reviewId = command.reviewId)
    }

    @OnCommand
    fun onReject(command: RejectDemo): DemoRejected {
        checkReviewing()
        return DemoRejected(reviewId = command.reviewId, reason = command.reason)
    }

    private fun checkReviewing() {
        check(state.status == DemoStatus.REVIEWING) { "Demo[${state.id}] is ${state.status}, expected REVIEWING." }
    }
}
