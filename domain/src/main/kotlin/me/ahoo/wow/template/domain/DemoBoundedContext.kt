package me.ahoo.wow.template.domain

import me.ahoo.wow.api.annotation.BoundedContext

/**
 * 限界上下文声明（每个 domain 模块恰好一个）：上下文名是每个命令、事件、路由（`/demo/...`）、主题与表名的一部分，
 * api 模块生成的 `DemoContract.NAME` 即来自此处。上下文名只能是小写字母、数字与连字符。
 *
 * 新增聚合无需在此登记：聚合由 `@AggregateRoot` 自行声明。
 */
@Suppress("unused")
@BoundedContext(name = DemoBoundedContext.NAME, description = "Demo 及其审核流程")
object DemoBoundedContext {
    const val NAME = "demo"
}
