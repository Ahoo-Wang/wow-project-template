package me.ahoo.wow.template.domain

import me.ahoo.wow.api.annotation.BoundedContext

/**
 * 限界上下文（每个 domain 模块恰好一个）。上下文名是每个命令、事件、路由（`/blog/...`）、主题与表名的前缀，
 * api 模块生成的 `BlogContract.NAME` 即来自此处，确定后不宜再改。只能是小写字母、数字与连字符。
 *
 * 聚合由 `@AggregateRoot` 自行声明，无需在此登记。
 */
@BoundedContext(name = "blog", description = "作者撰写文章，编辑审核后发布")
object BlogBoundedContext
