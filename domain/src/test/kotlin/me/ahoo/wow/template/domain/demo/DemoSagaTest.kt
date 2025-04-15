package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.UpdateDemo
import me.ahoo.wow.test.SagaVerifier.sagaVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DemoSagaTest {

    @Test
    fun onCreated() {
        val event = DemoCreated("data")
        sagaVerifier<DemoSaga>()
            .whenEvent(event)
            .expectCommandBody<UpdateDemo> {
                assertThat(it.data).isEqualTo("updated")
            }
            .verify()
    }
}
