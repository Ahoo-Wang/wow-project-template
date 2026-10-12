package me.ahoo.wow.template.domain

import me.ahoo.wow.api.annotation.BoundedContext
import me.ahoo.wow.template.DemoService

/**
 * domain 模块的限界上下文声明：
 * - 供本模块 KSP（wow-compiler）将聚合根归属到 [DemoService.SERVICE_NAME]，缺失会导致编译期元数据解析失败；
 * - 作为 server 组件扫描（Saga 等）的包锚点。
 */
@Suppress("unused")
@BoundedContext(name = DemoService.SERVICE_NAME)
object DemoBoundedContext
