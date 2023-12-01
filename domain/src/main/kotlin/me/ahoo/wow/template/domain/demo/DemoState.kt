package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.api.Identifier
import me.ahoo.wow.api.annotation.OnSourcing
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoUpdated

class DemoState(override val id: String) : Identifier {
    var data: String? = null
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