package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.api.annotation.OnSourcing
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoPublished
import me.ahoo.wow.template.api.demo.DemoRejected
import me.ahoo.wow.template.api.demo.DemoStatus
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.IDemoState

class DemoState(override val id: String) : IDemoState {
    override var data: String = ""
        private set
    override var status: DemoStatus = DemoStatus.REVIEWING
        private set

    @OnSourcing
    fun onCreated(event: DemoCreated) {
        data = event.data
        status = DemoStatus.REVIEWING
    }

    @OnSourcing
    fun onUpdated(event: DemoUpdated) {
        data = event.data
    }

    @Suppress("UnusedParameter")
    @OnSourcing
    fun onPublished(event: DemoPublished) {
        status = DemoStatus.PUBLISHED
    }

    @Suppress("UnusedParameter")
    @OnSourcing
    fun onRejected(event: DemoRejected) {
        status = DemoStatus.REJECTED
    }
}
