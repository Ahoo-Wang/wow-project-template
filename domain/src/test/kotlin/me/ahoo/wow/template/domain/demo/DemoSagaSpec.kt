package me.ahoo.wow.template.domain.demo

import me.ahoo.test.asserts.assert
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.UpdateDemo
import me.ahoo.wow.test.SagaSpec

class DemoSagaSpec : SagaSpec<DemoSaga>({
    on {
        val demoCreated = DemoCreated("data")
        whenEvent(demoCreated) {
            expectNoError()
            expectCommandType(UpdateDemo::class)
            expectCommandBody<UpdateDemo> {
                data.assert().isEqualTo("updated")
            }
        }
    }
})
