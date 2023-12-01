package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.api.annotation.OnEvent
import me.ahoo.wow.api.modeling.AggregateId
import me.ahoo.wow.spring.stereotype.StatelessSaga
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.UpdateDemo

@StatelessSaga
class DemoSaga {
    companion object {
        private val log = org.slf4j.LoggerFactory.getLogger(DemoSaga::class.java)
    }

    @OnEvent
    fun onCreated(event: DemoCreated, aggregateId: AggregateId): UpdateDemo {
        if (log.isDebugEnabled) {
            log.debug("onCreated: $event")
        }
        return UpdateDemo(
            id = aggregateId.id,
            data = "updated"
        )
    }
}