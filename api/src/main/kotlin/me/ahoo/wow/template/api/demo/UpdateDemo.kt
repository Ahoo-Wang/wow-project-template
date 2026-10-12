package me.ahoo.wow.template.api.demo

import jakarta.validation.constraints.NotBlank
import me.ahoo.wow.api.annotation.CommandRoute
import me.ahoo.wow.api.annotation.Event
import me.ahoo.wow.api.annotation.Summary

@Summary("更新Demo")
@CommandRoute(
    appendIdPath = CommandRoute.AppendPath.ALWAYS,
    action = "",
)
data class UpdateDemo(
    @field:NotBlank
    override val data: String
) : IDemoInfo

@Event
data class DemoUpdated(
    val data: String
)
