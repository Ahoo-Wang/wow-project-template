package me.ahoo.wow.template.server.demo

import io.github.oshai.kotlinlogging.KotlinLogging
import me.ahoo.wow.api.annotation.ProjectionProcessor
import me.ahoo.wow.template.api.demo.DemoCreated

@ProjectionProcessor
class DemoProjector {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    fun onEvent(event: DemoCreated) {
        log.debug { "onEvent: $event" }
    }
}
