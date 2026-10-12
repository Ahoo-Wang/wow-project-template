package me.ahoo.wow.template.api.demo

import me.ahoo.test.asserts.assert
import org.junit.jupiter.api.Test

class DemoViewTest {

    @Test
    fun `view exposes demo state contract`() {
        val state: IDemoState = DemoView(id = "id", data = "data", status = DemoStatus.PUBLISHED)
        state.id.assert().isEqualTo("id")
        state.data.assert().isEqualTo("data")
        state.status.assert().isEqualTo(DemoStatus.PUBLISHED)
    }
}
