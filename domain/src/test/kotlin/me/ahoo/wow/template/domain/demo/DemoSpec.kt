package me.ahoo.wow.template.domain.demo

import me.ahoo.test.asserts.assert
import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.UpdateDemo
import me.ahoo.wow.test.AggregateSpec

class DemoSpec : AggregateSpec<Demo, DemoState>({
    on {
        val create = CreateDemo(
            data = "data"
        )
        whenCommand(create) {
            expectNoError()
            expectEventType(DemoCreated::class)
            expectState {
                data.assert().isEqualTo(create.data)
            }
            fork {
                val update = UpdateDemo(
                    data = "newData"
                )
                whenCommand(update) {
                    expectNoError()
                    expectEventType(DemoUpdated::class)
                    expectState {
                        data.assert().isEqualTo(update.data)
                    }
                }
            }
        }
    }
})
