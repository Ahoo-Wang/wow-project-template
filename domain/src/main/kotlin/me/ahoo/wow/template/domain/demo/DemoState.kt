package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.api.annotation.OnSourcing
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.IDemoState

class DemoState(override val id: String) : IDemoState {
    override var data: String = ""
        private set

    @OnSourcing
    fun onCreated(event: DemoCreated) {
        data = event.data
    }

    @OnSourcing
    fun onUpdated(event: DemoUpdated) {
        data = event.data
    }
}
