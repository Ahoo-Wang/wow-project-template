package me.ahoo.wow.template.api.demo

import me.ahoo.wow.api.annotation.CommandRoute
import me.ahoo.wow.api.annotation.CreateAggregate
import me.ahoo.wow.api.annotation.Event

@CreateAggregate
@CommandRoute(
    method = CommandRoute.Method.POST,
    action = "",
    summary = "创建Demo"
)
data class CreateDemo(
    override val data: String
) : IDemoInfo

@Event
data class DemoCreated(
    override val data: String
) : IDemoInfo
