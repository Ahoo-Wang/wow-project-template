package me.ahoo.wow.template.domain.demo

import me.ahoo.test.asserts.assert
import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.UpdateDemo
import me.ahoo.wow.test.aggregate.whenCommand
import me.ahoo.wow.test.aggregateVerifier
import org.junit.jupiter.api.Test

class DemoTest {

    @Test
    fun onCreate() {
        val command = CreateDemo(
            data = "data"
        )

        aggregateVerifier<Demo, DemoState>()
            .whenCommand(command)
            .expectNoError()
            .expectEventType(DemoCreated::class.java)
            .expectState {
                it.data.assert().isEqualTo(command.data)
            }
            .verify()
    }

    @Test
    fun onUpdate() {
        val command = UpdateDemo(
            data = "data"
        )

        aggregateVerifier<Demo, DemoState>()
            .given(DemoCreated("old"))
            .whenCommand(command)
            .expectNoError()
            .expectEventType(DemoUpdated::class.java)
            .expectState {
                it.data.assert().isEqualTo(command.data)
            }
            .verify()
    }
}
