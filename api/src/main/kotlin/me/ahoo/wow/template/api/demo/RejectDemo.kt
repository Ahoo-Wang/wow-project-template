package me.ahoo.wow.template.api.demo

import me.ahoo.wow.api.annotation.CommandRoute
import me.ahoo.wow.api.annotation.Event
import me.ahoo.wow.api.annotation.Summary

/**
 * 内部命令：由审核驳回驱动，不对外暴露 HTTP 路由。
 */
@Summary("驳回Demo")
@CommandRoute(enabled = false)
data class RejectDemo(
    val reviewId: String,
    val reason: String
)

@Event
data class DemoRejected(
    val reviewId: String,
    val reason: String
)
