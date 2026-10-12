package me.ahoo.wow.template.api.demo

/**
 * Demo 的查询视图模型，供调用方反序列化快照查询结果。
 */
data class DemoView(
    override val id: String,
    override val data: String,
    override val status: DemoStatus,
) : IDemoState
