package me.ahoo.wow.template.api.demo

import jakarta.validation.constraints.NotBlank
import me.ahoo.wow.api.annotation.Summary

/**
 * 更新 Demo：`POST /demo/demo/{id}/update-demo`。
 */
@Summary("更新Demo")
data class UpdateDemo(
    @field:NotBlank
    override val data: String
) : IDemoInfo

@Summary("Demo已更新")
data class DemoUpdated(
    val data: String
)
