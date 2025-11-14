package me.ahoo.wow.template.api.demo

import me.ahoo.wow.api.annotation.CommandRoute
import me.ahoo.wow.api.annotation.CreateAggregate
import me.ahoo.wow.api.annotation.Event
import me.ahoo.wow.api.annotation.Summary

@Summary("创建Demo")
@CreateAggregate
@CommandRoute(
    method = CommandRoute.Method.POST,
    action = "",
)
data class CreateDemo(
    override val data: String
) : IDemoInfo

@Event
data class DemoCreated(
    override val data: String
) : IDemoInfo
