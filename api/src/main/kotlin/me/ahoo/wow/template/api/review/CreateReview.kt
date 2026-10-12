package me.ahoo.wow.template.api.review

import me.ahoo.wow.api.annotation.CommandRoute
import me.ahoo.wow.api.annotation.CreateAggregate
import me.ahoo.wow.api.annotation.Event
import me.ahoo.wow.api.annotation.Summary

/**
 * 内部命令：Demo 创建后由 Saga 发起审核。
 */
@Summary("创建审核")
@CreateAggregate
@CommandRoute(enabled = false)
data class CreateReview(
    val demoId: String
)

@Event
data class ReviewCreated(
    val demoId: String
)
