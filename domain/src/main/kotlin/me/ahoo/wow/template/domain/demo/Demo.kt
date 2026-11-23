package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.api.annotation.AggregateRoot
import me.ahoo.wow.api.annotation.ErrorCode
import me.ahoo.wow.api.decide.reject
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
 * 不满足不变量时以带错误码的拒绝（[Rejections]）回应，而不是抛出异常。
 * 删除/恢复使用 Wow 内置的生命周期命令（`wow.lifecycle.DeleteAggregate` / `RestoreAggregate`），无需在此声明。
 */
@AggregateRoot
class Demo(private val state: DemoState) {

    fun onCommand(command: CreateDemo): DemoCreated = DemoCreated(data = command.data)

    fun onCommand(command: UpdateDemo): DemoUpdated {
        if (state.status == DemoStatus.PUBLISHED) {
            reject(Rejections.WRONG_STATUS, "The demo is published and cannot be updated.")
        }
        if (command.data == state.data) {
            reject(Rejections.DATA_UNCHANGED, "The demo's data is unchanged.")
        }
        return DemoUpdated(data = command.data)
    }

    fun onCommand(command: PublishDemo): DemoPublished {
        requireReviewing()
        return DemoPublished(reviewId = command.reviewId)
    }

    fun onCommand(command: RejectDemo): DemoRejected {
        requireReviewing()
        return DemoRejected(reviewId = command.reviewId, reason = command.reason)
    }

    private fun requireReviewing() {
        if (state.status != DemoStatus.REVIEWING) {
            reject(Rejections.WRONG_STATUS, "The demo is ${state.status}, expected REVIEWING.")
        }
    }

    /**
     * 聚合的拒绝码（本地名）：编译器与内核将其命名为 `demo.demo.<code>`，调用方从 `DemoContract.Demo.Errors` 读取。
     */
    object Rejections {
        @ErrorCode(description = "The demo's status does not allow the command.", status = 409)
        const val WRONG_STATUS = "wrong-status"

        @ErrorCode(description = "An update changes the demo's data.")
        const val DATA_UNCHANGED = "data-unchanged"
    }
}
