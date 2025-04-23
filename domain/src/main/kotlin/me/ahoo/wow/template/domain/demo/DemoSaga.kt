package me.ahoo.wow.template.domain.demo

import io.github.oshai.kotlinlogging.KotlinLogging
import me.ahoo.wow.api.annotation.OnEvent
import me.ahoo.wow.api.modeling.AggregateId
import me.ahoo.wow.command.factory.CommandBuilder
import me.ahoo.wow.command.factory.CommandBuilder.Companion.commandBuilder
import me.ahoo.wow.spring.stereotype.StatelessSagaComponent
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.UpdateDemo

@StatelessSagaComponent
class DemoSaga {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @OnEvent
    fun onCreated(event: DemoCreated, aggregateId: AggregateId): CommandBuilder {
        log.debug { "onCreated: $event" }
        return UpdateDemo(
            data = "updated"
        ).commandBuilder().aggregateId(aggregateId.id)
    }
}
