package me.ahoo.wow.template.api.demo

import jakarta.validation.constraints.NotBlank
import me.ahoo.wow.api.annotation.CommandRoute

@CommandRoute(
    appendIdPath = CommandRoute.AppendPath.ALWAYS,
    action = "",
    summary = "更新Demo"
)
data class UpdateDemo(
    @CommandRoute.PathVariable val id: String,
    @field:NotBlank
    val data: String
)

data class DemoUpdated(val data: String)
