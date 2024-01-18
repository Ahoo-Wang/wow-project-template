package me.ahoo.wow.template.api.demo.query

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class DemoDtoTest {

    @Test
    fun `should create demo dto`() {
        val demoDto = DemoDto("demo-id")
        assertThat(demoDto.id, equalTo("demo-id"))
    }
}