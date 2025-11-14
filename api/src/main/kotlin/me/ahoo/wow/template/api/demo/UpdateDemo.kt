package me.ahoo.wow.template.api.demo

import me.ahoo.wow.api.annotation.CommandRoute
import me.ahoo.wow.api.annotation.Summary

@Summary("更新Demo")
@CommandRoute(
    appendIdPath = CommandRoute.AppendPath.ALWAYS,
    action = "",
)
data class UpdateDemo(
    override val data: String
) : IDemoInfo

data class DemoUpdated(override val data: String) : IDemoInfo
