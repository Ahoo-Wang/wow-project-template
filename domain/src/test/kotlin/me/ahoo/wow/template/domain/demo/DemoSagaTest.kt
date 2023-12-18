package me.ahoo.wow.template.domain.demo

import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.UpdateDemo
import me.ahoo.wow.test.SagaVerifier.sagaVerifier
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class DemoSagaTest {

    @Test
    fun onCreated() {
        val event = DemoCreated("data")
        sagaVerifier<DemoSaga>()
            .`when`(
                event
            )
            .expectCommandBody<UpdateDemo> {
                assertThat(it.data, equalTo("updated"))
            }
            .verify()
    }
}
