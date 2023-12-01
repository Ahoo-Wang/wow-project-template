package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.id.GlobalIdGenerator
import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.UpdateDemo
import me.ahoo.wow.test.aggregate.`when`
import me.ahoo.wow.test.aggregateVerifier
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class DemoTest {

    @Test
    fun onCreate() {
        val command = CreateDemo(
            data = "data"
        )

        aggregateVerifier<Demo, DemoState>()
            .`when`(command)
            .expectNoError()
            .expectEventType(DemoCreated::class.java)
            .expectState {
                assertThat(it.data, equalTo(command.data))
            }
            .verify()
    }

    @Test
    fun onUpdate() {
        val command = UpdateDemo(
            id = GlobalIdGenerator.generateAsString(),
            data = "data"
        )

        aggregateVerifier<Demo, DemoState>()
            .given(DemoCreated("old"))
            .`when`(command)
            .expectNoError()
            .expectEventType(DemoUpdated::class.java)
            .expectState {
                assertThat(it.data, equalTo(command.data))
            }
            .verify()
    }
}