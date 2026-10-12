package me.ahoo.wow.template.domain.demo

import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.command.DefaultDeleteAggregate
import me.ahoo.wow.api.command.DefaultRecoverAggregate
import me.ahoo.wow.api.event.DefaultAggregateDeleted
import me.ahoo.wow.api.event.DefaultAggregateRecovered
import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.demo.DemoCreated
import me.ahoo.wow.template.api.demo.DemoPublished
import me.ahoo.wow.template.api.demo.DemoRejected
import me.ahoo.wow.template.api.demo.DemoStatus
import me.ahoo.wow.template.api.demo.DemoUpdated
import me.ahoo.wow.template.api.demo.PublishDemo
import me.ahoo.wow.template.api.demo.RejectDemo
import me.ahoo.wow.template.api.demo.UpdateDemo
import me.ahoo.wow.test.AggregateSpec

class DemoSpec : AggregateSpec<Demo, DemoState>({
    on {
        val create = CreateDemo(data = "data")
        whenCommand(create) {
            expectNoError()
            expectEventType(DemoCreated::class)
            expectState {
                data.assert().isEqualTo(create.data)
                status.assert().isEqualTo(DemoStatus.REVIEWING)
            }
            fork("update") {
                val update = UpdateDemo(data = "newData")
                whenCommand(update) {
                    expectNoError()
                    expectEventType(DemoUpdated::class)
                    expectState {
                        data.assert().isEqualTo(update.data)
                    }
                }
            }
            fork("update with unchanged data") {
                whenCommand(UpdateDemo(data = create.data)) {
                    expectError<IllegalArgumentException> {
                        message.assert().contains("unchanged")
                    }
                }
            }
            fork("publish") {
                whenCommand(PublishDemo(reviewId = "review")) {
                    expectNoError()
                    expectEventType(DemoPublished::class)
                    expectState {
                        status.assert().isEqualTo(DemoStatus.PUBLISHED)
                    }
                    fork("update after published") {
                        whenCommand(UpdateDemo(data = "newData")) {
                            expectError<IllegalStateException> {
                                message.assert().contains("published")
                            }
                        }
                    }
                    fork("publish twice") {
                        whenCommand(PublishDemo(reviewId = "review")) {
                            expectError<IllegalStateException> {
                                message.assert().contains("expected REVIEWING")
                            }
                        }
                    }
                }
            }
            fork("reject") {
                whenCommand(RejectDemo(reviewId = "review", reason = "reason")) {
                    expectNoError()
                    expectEventType(DemoRejected::class)
                    expectState {
                        status.assert().isEqualTo(DemoStatus.REJECTED)
                    }
                }
            }
            fork("delete and recover") {
                whenCommand(DefaultDeleteAggregate) {
                    expectNoError()
                    expectEventType(DefaultAggregateDeleted::class)
                    fork {
                        whenCommand(DefaultRecoverAggregate) {
                            expectNoError()
                            expectEventType(DefaultAggregateRecovered::class)
                        }
                    }
                }
            }
        }
    }
})
