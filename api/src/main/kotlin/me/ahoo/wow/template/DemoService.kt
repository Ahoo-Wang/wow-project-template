package me.ahoo.wow.template

import me.ahoo.wow.api.annotation.BoundedContext
import me.ahoo.wow.template.DemoService.DEMO_AGGREGATE_NAME
import me.ahoo.wow.template.DemoService.REVIEW_AGGREGATE_NAME
import me.ahoo.wow.template.api.demo.CreateDemo
import me.ahoo.wow.template.api.review.CreateReview

/**
 * 限界上下文的发布语言入口：服务名、别名与聚合清单。
 *
 * `packageScopes` 决定命令/事件归属于哪个聚合，新增聚合时在此登记。
 */
@BoundedContext(
    name = DemoService.SERVICE_NAME,
    alias = DemoService.SERVICE_ALIAS,
    aggregates = [
        BoundedContext.Aggregate(DEMO_AGGREGATE_NAME, packageScopes = [CreateDemo::class]),
        BoundedContext.Aggregate(REVIEW_AGGREGATE_NAME, packageScopes = [CreateReview::class]),
    ],
)
object DemoService {
    /**
     * 需与 `spring.application.name` 保持一致。
     */
    const val SERVICE_NAME = "demo-service"
    const val SERVICE_ALIAS = "demo"
    const val DEMO_AGGREGATE_NAME = "demo"
    const val REVIEW_AGGREGATE_NAME = "review"
}
