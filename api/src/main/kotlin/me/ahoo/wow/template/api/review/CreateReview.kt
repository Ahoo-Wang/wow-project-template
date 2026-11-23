package me.ahoo.wow.template.api.review

import me.ahoo.wow.api.annotation.CreateAggregate
import me.ahoo.wow.api.annotation.InternalCommand
import me.ahoo.wow.api.annotation.Summary

/**
 * 内部命令：Demo 创建后由 Saga 发起审核，审核 ID 即 Demo ID。
 */
@Summary("创建审核")
@CreateAggregate
@InternalCommand
data class CreateReview(
    val demoId: String
)

@Summary("审核已创建")
data class ReviewCreated(
    val demoId: String
)
