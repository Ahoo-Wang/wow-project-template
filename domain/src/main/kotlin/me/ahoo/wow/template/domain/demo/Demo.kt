package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.api.annotation.AggregateRoot
import me.ahoo.wow.api.annotation.OnCommand
import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.UpdateDemo

@AggregateRoot
class Demo(private val state: DemoState) {

    @OnCommand
    fun onCreate(command: CreateDemo): DemoCreated {
        return DemoCreated(
            data = command.data,
        )
    }

    @OnCommand
    fun onUpdate(command: UpdateDemo): DemoUpdated {
        return DemoUpdated(
            data = command.data
        )
    }
}