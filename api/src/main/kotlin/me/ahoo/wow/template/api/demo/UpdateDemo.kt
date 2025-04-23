package me.ahoo.wow.template.api.demo

import me.ahoo.wow.api.annotation.CommandRoute

@CommandRoute(
    appendIdPath = CommandRoute.AppendPath.ALWAYS,
    action = "",
    summary = "更新Demo"
)
data class UpdateDemo(
    override val data: String
) : IDemoInfo

data class DemoUpdated(override val data: String) : IDemoInfo
