package me.ahoo.wow.template.api.demo

import me.ahoo.wow.api.annotation.InternalCommand
import me.ahoo.wow.api.annotation.Summary

/**
 * 内部命令：由审核驳回驱动（[me.ahoo.wow.template.api.review.ReviewRejected]）。公开端口不提供其路由。
 */
@Summary("驳回Demo")
@InternalCommand
data class RejectDemo(
    val reviewId: String,
    val reason: String
)

@Summary("Demo已驳回")
data class DemoRejected(
    val reviewId: String,
    val reason: String
)
