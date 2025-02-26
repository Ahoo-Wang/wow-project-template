package me.ahoo.wow.template

import me.ahoo.wow.api.annotation.BoundedContext
import me.ahoo.wow.template.DemoService.DEMO_AGGREGATE_NAME
import me.ahoo.wow.template.api.demo.CreateDemo

@BoundedContext(
    name = DemoService.SERVICE_NAME,
    alias = DemoService.SERVICE_ALIAS,
    aggregates = [
        BoundedContext.Aggregate(DEMO_AGGREGATE_NAME, packageScopes = [CreateDemo::class])
    ],
)
object DemoService {
    const val SERVICE_NAME = "demo-service"
    const val SERVICE_ALIAS = "demo"
    const val DEMO_AGGREGATE_NAME = "demo"
}
