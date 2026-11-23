package me.ahoo.wow.template.api.demo

import jakarta.validation.constraints.NotBlank
import me.ahoo.wow.api.annotation.CreateAggregate
import me.ahoo.wow.api.annotation.Summary

/**
 * 创建 Demo：`POST /demo/demo/create-demo`（服务端生成 ID），或 `POST /demo/demo/{id}/create-demo`（调用方指定 ID）。
 */
@Summary("创建Demo")
@CreateAggregate
data class CreateDemo(
    @field:NotBlank
    override val data: String
) : IDemoInfo

@Summary("Demo已创建")
data class DemoCreated(
    val data: String
)
