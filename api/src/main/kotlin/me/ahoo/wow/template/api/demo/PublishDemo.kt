package me.ahoo.wow.template.api.demo

import me.ahoo.wow.api.annotation.InternalCommand
import me.ahoo.wow.api.annotation.Summary

/**
 * 内部命令：由审核通过驱动（[me.ahoo.wow.template.api.review.ReviewApproved]）。公开端口不提供其路由，避免绕过审核直接发布。
 */
@Summary("发布Demo")
@InternalCommand
data class PublishDemo(
    val reviewId: String
)

@Summary("Demo已发布")
data class DemoPublished(
    val reviewId: String
)
