package me.ahoo.wow.template.api.demo

import me.ahoo.wow.api.annotation.CommandRoute
import me.ahoo.wow.api.annotation.Event
import me.ahoo.wow.api.annotation.Summary

/**
 * 内部命令：由审核通过驱动，不对外暴露 HTTP 路由，避免绕过审核直接发布。
 */
@Summary("发布Demo")
@CommandRoute(enabled = false)
data class PublishDemo(
    val reviewId: String
)

@Event
data class DemoPublished(
    val reviewId: String
)
